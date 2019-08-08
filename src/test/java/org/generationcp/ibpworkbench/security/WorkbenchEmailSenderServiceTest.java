
package org.generationcp.ibpworkbench.security;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.reflect.FieldUtils;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Created by cyrus on 4/15/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkbenchEmailSenderServiceTest {

	public static final String GENERATED_URL = "http://a.generated.com";
	private static final String RESET_TOKEN_TEST = "test_reset_token";

	@Mock
	private WorkbenchUserService workbenchUserService;

	@Mock
	private UserService userService;

	@Mock
	private ServletContext servletContext;

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private TemplateEngine templateEngine;

	@Mock
	private MessageSource messageSource;

	@Mock
	private HttpServletRequest request;

	private final String senderEmail = "will@i.am";

	private final Integer noOfHoursBeforeExpire = 24;

	@InjectMocks
	private WorkbenchEmailSenderService service = new WorkbenchEmailSenderService();

	@Before
	public void setup() throws Exception {
		FieldUtils.writeDeclaredField(this.service, "senderEmail", this.senderEmail, true);
		FieldUtils.writeDeclaredField(this.service, "noOfHoursBeforeExpire", this.noOfHoursBeforeExpire, true);

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));

		Mockito.when(this.request.getScheme()).thenReturn("http");
		Mockito.when(this.request.getServerName()).thenReturn("my-host");
		Mockito.when(this.request.getServerPort()).thenReturn(18080);

		this.service = Mockito.spy(this.service);
	}

	@Test
	public void testDoRequestPasswordReset() throws Exception {
		String generatedUrl = "http://a.generated.com";

		UserInfo userInfo = Mockito.mock(UserInfo.class);
		WorkbenchUser testUser = new WorkbenchUser();
		Person testPerson = new Person();
		testPerson.setFirstName("hello");
		testPerson.setLastName("world");
		testPerson.setEmail("hello@world.com");

		testUser.setName("TEST_NAME");
		testUser.setPerson(testPerson);

		Mockito.when(this.userService.getUserInfoByUsername(testUser.getName())).thenReturn(userInfo);
		Mockito.doReturn(WorkbenchEmailSenderServiceTest.GENERATED_URL).when(this.service).generateResetPasswordUrl(userInfo);
		Mockito.doNothing().when(this.service)
				.sendForgotPasswordRequest(Matchers.anyString(), Matchers.anyString(), Matchers.eq(generatedUrl));

		this.service.doRequestPasswordReset(testUser);

		Mockito.verify(this.service, Mockito.times(1)).sendForgotPasswordRequest(Matchers.anyString(), Matchers.anyString(),
				Matchers.eq(generatedUrl));

	}

	@Test
	public void testGenerateResetPasswordUrl() throws Exception {
		UserInfo userInfo = Mockito.mock(UserInfo.class);

		String url = this.service.generateResetPasswordUrl(userInfo);

		Assert.assertNotNull(url);

		Mockito.verify(this.userService, Mockito.times(1)).updateUserInfo(userInfo);

	}

	@Test
	public void testSendForgotPasswordRequest() throws Exception {
		String recipientName = "this is my name";
		MimeMessageHelper message = Mockito.mock(MimeMessageHelper.class);
		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);

		Mockito.when(this.mailSender.createMimeMessage()).thenReturn(mimeMessage);

		Mockito.doReturn("processed html text").when(this.service).processTemplate(Matchers.any(Context.class),Matchers.anyString());
		Mockito.doReturn(message).when(this.service).getMimeMessageHelper(mimeMessage);
		Mockito.doReturn(Mockito.mock(ByteArrayResource.class)).when(this.service).retrieveLogoImage();

		this.service.sendForgotPasswordRequest(recipientName, "recipient@email.com", WorkbenchEmailSenderServiceTest.GENERATED_URL);

		Mockito.verify(this.mailSender).send(mimeMessage);

	}

	@Test
	public void testValidateResetToken() throws Exception {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(1);
		Mockito.when(this.userService.getUserInfoByResetToken(WorkbenchEmailSenderServiceTest.RESET_TOKEN_TEST)).thenReturn(
				userInfo);

		Mockito.doReturn(true).when(this.service).isResetTokenValid(userInfo);

		this.service.validateResetToken(WorkbenchEmailSenderServiceTest.RESET_TOKEN_TEST);

		Mockito.verify(this.workbenchUserService, Mockito.times(1)).getUserByUserid(1);
	}

	@Test(expected = InvalidResetTokenException.class)
	public void testValidateResetTokenWithException() throws Exception {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(1);
		Mockito.when(this.userService.getUserInfoByResetToken(WorkbenchEmailSenderServiceTest.RESET_TOKEN_TEST)).thenReturn(
				userInfo);

		Mockito.doReturn(false).when(this.service).isResetTokenValid(userInfo);

		this.service.validateResetToken(WorkbenchEmailSenderServiceTest.RESET_TOKEN_TEST);
	}

	@Test
	public void testDeleteToken() throws Exception {
		UserAccountModel user = new UserAccountModel();
		user.setUsername("sample");

		Mockito.when(this.userService.getUserInfoByUsername(user.getUsername())).thenReturn(new UserInfo());

		ArgumentCaptor<UserInfo> userInfoArg = ArgumentCaptor.forClass(UserInfo.class);
		Mockito.when(this.userService.updateUserInfo(userInfoArg.capture())).thenReturn(null);

		this.service.deleteToken(user);

		UserInfo resultUserInfo = userInfoArg.getValue();
		Assert.assertNull("null token", resultUserInfo.getResetToken());
		Assert.assertNull("null expiry date", resultUserInfo.getResetExpiryDate());

		Mockito.verify(this.userService, Mockito.times(1)).updateUserInfo(Matchers.any(UserInfo.class));

	}
}

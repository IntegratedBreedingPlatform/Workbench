package org.generationcp.ibpworkbench.security;

import org.apache.commons.lang.reflect.FieldUtils;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by cyrus on 4/15/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ForgotPasswordEmailServiceTest {

	public static final String GENERATED_URL = "http://a.generated.com";
	private static final String RESET_TOKEN_TEST = "test_reset_token";

	@Mock
	private WorkbenchUserService workbenchUserService;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

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

	private String senderEmail = "will@i.am";

	private Integer noOfHoursBeforeExpire = 24;

	@InjectMocks
	private ForgotPasswordEmailService service = new ForgotPasswordEmailService();

	@Before
	public void setup() throws Exception {
		FieldUtils.writeDeclaredField(service, "senderEmail", senderEmail, true);
		FieldUtils.writeDeclaredField(service, "noOfHoursBeforeExpire", noOfHoursBeforeExpire, true);

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(request.getScheme()).thenReturn("http");
		when(request.getServerName()).thenReturn("my-host");
		when(request.getServerPort()).thenReturn(18080);

		service = spy(service);
	}

	@Test
	public void testDoRequestPasswordReset() throws Exception {
		String generatedUrl = "http://a.generated.com";

		UserInfo userInfo = mock(UserInfo.class);
		User testUser = new User();
		Person testPerson = new Person();
		testPerson.setFirstName("hello");
		testPerson.setLastName("world");
		testPerson.setEmail("hello@world.com");

		testUser.setName("TEST_NAME");
		testUser.setPerson(testPerson);

		when(workbenchDataManager.getUserInfoByUsername(testUser.getName())).thenReturn(userInfo);
		doReturn(GENERATED_URL).when(service).generateResetPasswordUrl(userInfo);
		doNothing().when(service).sendForgotPasswordRequest(anyString(), anyString(),
				eq(generatedUrl));

		service.doRequestPasswordReset(testUser);

		verify(service,times(1)).sendForgotPasswordRequest(anyString(), anyString(),
				eq(generatedUrl));

	}

	@Test
	public void testGenerateResetPasswordUrl() throws Exception {
		UserInfo userInfo = mock(UserInfo.class);

		String url = service.generateResetPasswordUrl(userInfo);

		assertNotNull(url);

		verify(workbenchDataManager,times(1)).updateUserInfo(userInfo);

	}

	@Test
	public void testSendForgotPasswordRequest() throws Exception {
		String recipientName = "this is my name";
		MimeMessageHelper message = mock(MimeMessageHelper.class);
		MimeMessage mimeMessage = mock(MimeMessage.class);

		when (mailSender.createMimeMessage()).thenReturn(mimeMessage);

		doReturn("processed html text").when(service).processTemplate(any(Context.class));
		doReturn(message).when(service).getMimeMessageHelper(mimeMessage);
		doReturn(mock(ByteArrayResource.class)).when(service).retrieveLogoImage();

		service.sendForgotPasswordRequest(recipientName, "recipient@email.com", GENERATED_URL);

		verify(mailSender).send(mimeMessage);


	}

	@Test
	public void testValidateResetToken() throws Exception {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(1);
		when(workbenchDataManager.getUserInfoByResetToken(RESET_TOKEN_TEST)).thenReturn(userInfo);

		doReturn(true).when(service).isResetTokenValid(userInfo);

		service.validateResetToken(RESET_TOKEN_TEST);

		verify(workbenchUserService,times(1)).getUserByUserid(1);
	}

	@Test(expected = InvalidResetTokenException.class)
	public void testValidateResetTokenWithException() throws Exception {
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(1);
		when(workbenchDataManager.getUserInfoByResetToken(RESET_TOKEN_TEST)).thenReturn(userInfo);

		doReturn(false).when(service).isResetTokenValid(userInfo);

		service.validateResetToken(RESET_TOKEN_TEST);
	}

	@Test
	public void testDeleteToken() throws Exception {
		UserAccountModel user = new UserAccountModel();
		user.setUsername("sample");

		when(workbenchDataManager.getUserInfoByUsername(user.getUsername())).thenReturn(
				new UserInfo());

		ArgumentCaptor<UserInfo> userInfoArg = ArgumentCaptor.forClass(UserInfo.class);
		when(workbenchDataManager.updateUserInfo(userInfoArg.capture())).thenReturn(null);

		service.deleteToken(user);

		UserInfo resultUserInfo = userInfoArg.getValue();
		assertNull("null token",resultUserInfo.getResetToken());
		assertNull("null expiry date",resultUserInfo.getResetExpiryDate());

		verify(workbenchDataManager,times(1)).updateUserInfo(any(UserInfo.class));

	}
}
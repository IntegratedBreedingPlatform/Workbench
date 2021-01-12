package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.Window;
import org.junit.Assert;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class ChangeCredentialsWindowTest {

	public static final String FIRSTNAME = "firstname";
	public static final String LASTNAME = "lastname";
	public static final String EMAIL_ADDRESS = "dummy@yahoo.com";
	public static final String PASSWORD = "hellopw";

	@Mock
	private UserService userService;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private Window parentWindow;

	@Mock
	private ChangeCredentialsWindow.CredentialsChangedEvent credentialsChangedEvent;

	@InjectMocks
	private final ChangeCredentialsWindow changeCredentialsWindow = new ChangeCredentialsWindow(this.credentialsChangedEvent);

	@Before
	public void init() {

		final WorkbenchUser user = new WorkbenchUser();
		user.setUserid(1);
		user.setName("testUsername");
		user.setPerson(new Person());

		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(user);

		this.changeCredentialsWindow.setParentWindow(this.parentWindow);
		this.changeCredentialsWindow.initializeComponents();

	}

	@Test
	public void testSaveCredentialsValidationSuccess() {

		this.changeCredentialsWindow.getFirstName().setValue(FIRSTNAME);
		this.changeCredentialsWindow.getLastName().setValue(LASTNAME);
		this.changeCredentialsWindow.getEmailAddress().setValue(EMAIL_ADDRESS);

		this.changeCredentialsWindow.saveCredentials();

		// Verify that updateUser is called if there are no validation error.
		Mockito.verify(this.userService).updateUser(Mockito.any(WorkbenchUser.class));
		Mockito.verify(this.parentWindow).showNotification(Mockito.any(Window.Notification.class));
		Mockito.verify(this.parentWindow).removeWindow(Mockito.any(Window.class));
		Mockito.verify(this.credentialsChangedEvent).onChanged(FIRSTNAME, LASTNAME, EMAIL_ADDRESS);

	}


	@Test
	public void testSaveCredentialsValidationError() {

		this.changeCredentialsWindow.saveCredentials();

		// Verify that updateUser is not called if there is validation error.
		Mockito.verify(this.userService, Mockito.times(0)).updateUser(Mockito.any(WorkbenchUser.class));
		Mockito.verify(this.parentWindow).showNotification(Mockito.any(Window.Notification.class));
		Mockito.verify(this.parentWindow, Mockito.times(0)).removeWindow(Mockito.any(Window.class));
		Mockito.verify(this.credentialsChangedEvent, Mockito.times(0)).onChanged(FIRSTNAME, LASTNAME, EMAIL_ADDRESS);

	}

	@Test
	public void testUpdateUserWithPassword() {

		this.changeCredentialsWindow.updateUser(FIRSTNAME, LASTNAME, EMAIL_ADDRESS, PASSWORD);

		final ArgumentCaptor<WorkbenchUser> captor = ArgumentCaptor.forClass(WorkbenchUser.class);

		Mockito.verify(this.userService).updateUser(captor.capture());

		final WorkbenchUser userToBeUpdated = captor.getValue();

		Assert.assertTrue(new BCryptPasswordEncoder().matches(PASSWORD, userToBeUpdated.getPassword()));
		Assert.assertEquals(FIRSTNAME, userToBeUpdated.getPerson().getFirstName());
		Assert.assertEquals(LASTNAME, userToBeUpdated.getPerson().getLastName());
		Assert.assertEquals(EMAIL_ADDRESS, userToBeUpdated.getPerson().getEmail());


	}

	@Test
	public void testUpdateUserNoPassword() {

		this.changeCredentialsWindow.updateUser(FIRSTNAME, LASTNAME, EMAIL_ADDRESS, "");

		final ArgumentCaptor<WorkbenchUser> captor = ArgumentCaptor.forClass(WorkbenchUser.class);

		Mockito.verify(this.userService).updateUser(captor.capture());

		final WorkbenchUser userToBeUpdated = captor.getValue();

		Assert.assertEquals(null, userToBeUpdated.getPassword());
		Assert.assertEquals(FIRSTNAME, userToBeUpdated.getPerson().getFirstName());
		Assert.assertEquals(LASTNAME, userToBeUpdated.getPerson().getLastName());
		Assert.assertEquals(EMAIL_ADDRESS, userToBeUpdated.getPerson().getEmail());


	}

	@Test
	public void testValidatePassword() {

		final String errorMessage = "ERROR_CONFIRM_PASSWORD";
		Mockito.when(this.messageSource.getMessage(Message.ERROR_CONFIRM_PASSWORD)).thenReturn(errorMessage);

		try {
			this.changeCredentialsWindow.validatePassword(PASSWORD, "ssfffasd");
			Assert.fail("Password and confirm password values are not same so the method should throw a validation exception");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessage, e.getMessage());
		}

		try {
			this.changeCredentialsWindow.validatePassword(PASSWORD, PASSWORD);
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("Password and confirm password values are equal so the method should not throw a validation exception");
		}

		try {
			this.changeCredentialsWindow.validatePassword("", PASSWORD);
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("If password is empty, validation is ignored and the method should not throw a validation exception");
		}

	}


	@Test
	public void testValidateEmailAdresss() {

		final String errorMessageEmailIsBlank = "ERROR_PASSWORD_IS_BLANK";
		final String errorMessageEmailAlreadyExists = "ERROR_EMAIL_ALREADY_EXISTS";
		Mockito.when(this.messageSource.getMessage(Message.ERROR_EMAIL_IS_BLANK)).thenReturn(errorMessageEmailIsBlank);
		Mockito.when(this.messageSource.getMessage(Message.ERROR_EMAIL_ALREADY_EXISTS)).thenReturn(errorMessageEmailAlreadyExists);

		try {
			this.changeCredentialsWindow.validateEmailAdresss(EMAIL_ADDRESS);
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("Email Address is not empty, so the method should not throw a validation exception");
		}

		try {
			this.changeCredentialsWindow.validateEmailAdresss("");
			Assert.fail("Email Address is empty, the method should throw a validation exception");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailIsBlank, e.getMessage());
		}

		Mockito.when(this.userService.isPersonWithEmailExists(EMAIL_ADDRESS)).thenReturn(true);

		try {
			this.changeCredentialsWindow.validateEmailAdresss(EMAIL_ADDRESS);
			Assert.fail("Email Address already exists, the method should throw a validation exception");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailAlreadyExists, e.getMessage());
		}

	}

	@Test
	public void testValidateEmailFormatEmailIsValid() {

		try {
			this.changeCredentialsWindow.validateEmailFormat("asdjhasjdh@yahoo.com");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("Email Address format is valid, the method should not throw a validation exception");
		}


	}

	@Test
	public void testValidateEmailFormatEmailIsInvalid() {

		final String errorMessageEmailIsInvalid = "ERROR_EMAIL_ALREADY_EXISTS";
		Mockito.when(this.messageSource.getMessage(Message.ERROR_EMAIL_IS_INVALID_FORMAT)).thenReturn(errorMessageEmailIsInvalid);

		try {
			this.changeCredentialsWindow.validateEmailFormat("asdjhasjdh.com");
			Assert.fail("Email Address format is invalid, the method should throw a validation exception");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailIsInvalid, e.getMessage());
		}

		try {
			this.changeCredentialsWindow.validateEmailFormat("asdjhasjdh@aksdj");
			Assert.fail("Email Address format is invalid, the method should throw a validation exception");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailIsInvalid, e.getMessage());
		}


	}


	@Test
	public void testValidateName() {

		final String errorMessageNameIsBlank = "ERROR_NAME_IS_BLANK";
		Mockito.when(this.messageSource.getMessage(Message.ERROR_NAME_IS_BLANK)).thenReturn(errorMessageNameIsBlank);


		try {
			this.changeCredentialsWindow.validateName(FIRSTNAME, "");
			Assert.fail("Last name is empty, so the method should throw a validation exception");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageNameIsBlank, e.getMessage());
		}

		try {
			this.changeCredentialsWindow.validateName("", LASTNAME);
			Assert.fail("First Name is empty, so the method should throw a validation exception");
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageNameIsBlank, e.getMessage());
		}

		try {
			this.changeCredentialsWindow.validateName(FIRSTNAME, LASTNAME);
		} catch (final ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("First Name and Last Name are not empty, so the method should not throw a validation exception");
		}

	}




}

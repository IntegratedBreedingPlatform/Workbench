package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.Window;
import junit.framework.Assert;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
public class ChangeCredentialsWindowTest {

	public static final String FIRSTNAME = "firstname";
	public static final String LASTNAME = "lastname";
	public static final String EMAIL_ADDRESS = "dummy@yahoo.com";
	public static final String PASSWORD = "hellopw";

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private Window parentWindow;

	@Mock
	private ChangeCredentialsWindow.CredentialsChangedEvent credentialsChangedEvent;

	@InjectMocks
	private ChangeCredentialsWindow changeCredentialsWindow = new ChangeCredentialsWindow(credentialsChangedEvent);

	@Before
	public void init() {

		User user = new User();
		user.setUserid(1);
		user.setName("testUsername");
		user.setPerson(new Person());

		Mockito.when(contextUtil.getCurrentWorkbenchUser()).thenReturn(user);

		changeCredentialsWindow.setParentWindow(parentWindow);
		changeCredentialsWindow.initializeComponents();

	}

	@Test
	public void testSaveCredentialsValidationSuccess() {

		changeCredentialsWindow.getFirstName().setValue(FIRSTNAME);
		changeCredentialsWindow.getLastName().setValue(LASTNAME);
		changeCredentialsWindow.getEmailAddress().setValue(EMAIL_ADDRESS);

		changeCredentialsWindow.saveCredentials();

		// Verify that updateUser is called if there are no validation error.
		Mockito.verify(workbenchDataManager).updateUser(Mockito.any(User.class));
		Mockito.verify(parentWindow).showNotification(Mockito.any(Window.Notification.class));
		Mockito.verify(parentWindow).removeWindow(Mockito.any(Window.class));
		Mockito.verify(credentialsChangedEvent).onChanged(FIRSTNAME, LASTNAME, EMAIL_ADDRESS);

	}


	@Test
	public void testSaveCredentialsValidationError() {

		changeCredentialsWindow.saveCredentials();

		// Verify that updateUser is not called if there is validation error.
		Mockito.verify(workbenchDataManager, Mockito.times(0)).updateUser(Mockito.any(User.class));
		Mockito.verify(parentWindow).showNotification(Mockito.any(Window.Notification.class));
		Mockito.verify(parentWindow, Mockito.times(0)).removeWindow(Mockito.any(Window.class));
		Mockito.verify(credentialsChangedEvent, Mockito.times(0)).onChanged(FIRSTNAME, LASTNAME, EMAIL_ADDRESS);

	}

	@Test
	public void testUpdateUserWithPassword() {

		changeCredentialsWindow.updateUser(FIRSTNAME, LASTNAME, EMAIL_ADDRESS, PASSWORD);

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

		Mockito.verify(workbenchDataManager).updateUser(captor.capture());

		User userToBeUpdated = captor.getValue();

		Assert.assertTrue(new BCryptPasswordEncoder().matches(PASSWORD, userToBeUpdated.getPassword()));
		Assert.assertEquals(FIRSTNAME, userToBeUpdated.getPerson().getFirstName());
		Assert.assertEquals(LASTNAME, userToBeUpdated.getPerson().getLastName());
		Assert.assertEquals(EMAIL_ADDRESS, userToBeUpdated.getPerson().getEmail());


	}

	@Test
	public void testUpdateUserNoPassword() {

		changeCredentialsWindow.updateUser(FIRSTNAME, LASTNAME, EMAIL_ADDRESS, "");

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

		Mockito.verify(workbenchDataManager).updateUser(captor.capture());

		User userToBeUpdated = captor.getValue();

		Assert.assertEquals(null, userToBeUpdated.getPassword());
		Assert.assertEquals(FIRSTNAME, userToBeUpdated.getPerson().getFirstName());
		Assert.assertEquals(LASTNAME, userToBeUpdated.getPerson().getLastName());
		Assert.assertEquals(EMAIL_ADDRESS, userToBeUpdated.getPerson().getEmail());


	}

	@Test
	public void testValidatePassword() {

		String errorMessage = "ERROR_CONFIRM_PASSWORD";
		Mockito.when(messageSource.getMessage(Message.ERROR_CONFIRM_PASSWORD)).thenReturn(errorMessage);

		try {
			changeCredentialsWindow.validatePassword(PASSWORD, "ssfffasd");
			Assert.fail("Password and confirm password values are not same so the method should throw a validation exception");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessage, e.getMessage());
		}

		try {
			changeCredentialsWindow.validatePassword(PASSWORD, PASSWORD);
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("Password and confirm password values are equal so the method should not throw a validation exception");
		}

		try {
			changeCredentialsWindow.validatePassword("", PASSWORD);
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("If password is empty, validation is ignored and the method should not throw a validation exception");
		}

	}


	@Test
	public void testValidateEmailAdresss() {

		String errorMessageEmailIsBlank = "ERROR_PASSWORD_IS_BLANK";
		String errorMessageEmailAlreadyExists = "ERROR_EMAIL_ALREADY_EXISTS";
		Mockito.when(messageSource.getMessage(Message.ERROR_EMAIL_IS_BLANK)).thenReturn(errorMessageEmailIsBlank);
		Mockito.when(messageSource.getMessage(Message.ERROR_EMAIL_ALREADY_EXISTS)).thenReturn(errorMessageEmailAlreadyExists);

		try {
			changeCredentialsWindow.validateEmailAdresss(EMAIL_ADDRESS);
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("Email Address is not empty, so the method should not throw a validation exception");
		}

		try {
			changeCredentialsWindow.validateEmailAdresss("");
			Assert.fail("Email Address is empty, the method should throw a validation exception");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailIsBlank, e.getMessage());
		}

		Mockito.when(workbenchDataManager.isPersonWithEmailExists(EMAIL_ADDRESS)).thenReturn(true);

		try {
			changeCredentialsWindow.validateEmailAdresss(EMAIL_ADDRESS);
			Assert.fail("Email Address already exists, the method should throw a validation exception");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailAlreadyExists, e.getMessage());
		}

	}

	@Test
	public void testValidateEmailFormatEmailIsValid() {

		String errorMessageEmailIsInvalid = "ERROR_EMAIL_ALREADY_EXISTS";
		Mockito.when(messageSource.getMessage(Message.ERROR_EMAIL_IS_INVALID_FORMAT)).thenReturn(errorMessageEmailIsInvalid);

		try {
			changeCredentialsWindow.validateEmailFormat("asdjhasjdh@yahoo.com");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("Email Address format is valid, the method should not throw a validation exception");
		}


	}

	@Test
	public void testValidateEmailFormatEmailIsInvalid() {

		String errorMessageEmailIsInvalid = "ERROR_EMAIL_ALREADY_EXISTS";
		Mockito.when(messageSource.getMessage(Message.ERROR_EMAIL_IS_INVALID_FORMAT)).thenReturn(errorMessageEmailIsInvalid);

		try {
			changeCredentialsWindow.validateEmailFormat("asdjhasjdh.com");
			Assert.fail("Email Address format is invalid, the method should throw a validation exception");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailIsInvalid, e.getMessage());
		}

		try {
			changeCredentialsWindow.validateEmailFormat("asdjhasjdh@aksdj");
			Assert.fail("Email Address format is invalid, the method should throw a validation exception");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageEmailIsInvalid, e.getMessage());
		}


	}


	@Test
	public void testValidateName() {

		String errorMessageNameIsBlank = "ERROR_NAME_IS_BLANK";
		Mockito.when(messageSource.getMessage(Message.ERROR_NAME_IS_BLANK)).thenReturn(errorMessageNameIsBlank);


		try {
			changeCredentialsWindow.validateName(FIRSTNAME, "");
			Assert.fail("Last name is empty, so the method should throw a validation exception");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageNameIsBlank, e.getMessage());
		}

		try {
			changeCredentialsWindow.validateName("", LASTNAME);
			Assert.fail("First Name is empty, so the method should throw a validation exception");
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.assertEquals(errorMessageNameIsBlank, e.getMessage());
		}

		try {
			changeCredentialsWindow.validateName(FIRSTNAME, LASTNAME);
		} catch (ChangeCredentialsWindow.ValidationException e) {
			Assert.fail("First Name and Last Name are not empty, so the method should not throw a validation exception");
		}

	}




}

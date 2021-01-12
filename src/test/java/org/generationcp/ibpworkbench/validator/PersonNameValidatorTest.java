package org.generationcp.ibpworkbench.validator;

import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Field;

import org.junit.Assert;

@RunWith(MockitoJUnitRunner.class)
public class PersonNameValidatorTest {

	@Mock
	private UserService userService;

	@Mock
	private ValidatorCounter validatorCounter;

	@InjectMocks
	private PersonNameValidator personNameValidator;

	@Before
	public void setUp() {
		final Field firstName = Mockito.mock(Field.class);
		Mockito.when(firstName.getValue()).thenReturn("Firstname");
		this.personNameValidator.setFirstName(firstName);

		final Field lastName = Mockito.mock(Field.class);
		Mockito.when(lastName.getValue()).thenReturn("Firstname");
		this.personNameValidator.setLastName(lastName);

		this.personNameValidator.setValidatorCounter(this.validatorCounter);
		this.personNameValidator.setUserService(this.userService);
	}

	@Test
	public void testValidateWhereValueIsValidAndPersonCounterIsGreaterThan2() {
		Mockito.when(this.validatorCounter.getNameValidationCounter()).thenReturn(3);
		try {
			this.personNameValidator.validate(new Object());
		} catch (final InvalidValueException e) {
			Assert.fail("There should be no invalid value exception.");
		}
	}

	@Test
	public void testValidateWhereValueIsValidAndPersonCounterIsLessThan2() {
		Mockito.when(this.validatorCounter.getNameValidationCounter()).thenReturn(0);
		Mockito.when(this.userService.isPersonExists(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(false);
		try {
			this.personNameValidator.validate(new Object());
		} catch (final InvalidValueException e) {
			Assert.fail("There should be no invalid value exception.");
		}
	}

	@Test(expected = InvalidValueException.class)
	public void testValidateWhereValueIsInValid() {
		Mockito.when(this.validatorCounter.getNameValidationCounter()).thenReturn(0);
		Mockito.when(this.userService.isPersonExists(Matchers.anyString(), Matchers.anyString()))
				.thenReturn(true);
		this.personNameValidator.validate(new Object());
	}
}

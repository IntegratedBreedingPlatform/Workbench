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

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class UsernameValidatorTest {
	@Mock
	private UserService userService;

	@Mock
	private ValidatorCounter validatorCounter;

	@InjectMocks
	private UsernameValidator usernameValidator;

	@Before
	public void setUp() {
		this.usernameValidator.setValidatorCounter(this.validatorCounter);
		this.usernameValidator.setUserService(this.userService);
	}

	@Test
	public void testIsValidTrueWhereUsernameCounterIsGreaterThan2() {
		Mockito.when(this.validatorCounter.getUsernameCounter()).thenReturn(3);
		final boolean isValid = this.usernameValidator.isValid("name");
		Assert.assertTrue(isValid);
	}

	@Test
	public void testIsValidTrueWhereUsernameCounterIsLessThan2() {
		Mockito.when(this.validatorCounter.getUsernameCounter()).thenReturn(0);
		Mockito.when(this.userService.isUsernameExists(Matchers.anyString())).thenReturn(false);
		final boolean isValid = this.usernameValidator.isValid("name");
		Assert.assertTrue(isValid);
	}

	@Test
	public void testIsValidFalse() {
		Mockito.when(this.validatorCounter.getUsernameCounter()).thenReturn(0);
		Mockito.when(this.userService.isUsernameExists(Matchers.anyString())).thenReturn(true);
		final boolean isValid = this.usernameValidator.isValid("name");
		Assert.assertFalse(isValid);
	}

}

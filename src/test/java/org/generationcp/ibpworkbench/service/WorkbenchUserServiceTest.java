
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchUserServiceTest {

	public static final String LAST_NAME = "lastName";
	private static final int TEST_USER_ID = 1;
	private static final Integer TEST_PERSON_ID = 2;
	private static final String TEST_USERNAME = "TEST_USERNAME";
	private static final String HASHED_PASSWORD = "$2a$10$ycdBiYL8X9NcDmamrqdcY./n8EiBqejjMMLPYGULev.iNtJrWimwe";
	private static final String PASSWORD = "password";
	public static final String FIRST_NAME = "firstName";
	public static final String EMAIL = "email@email.com";

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private WorkbenchUserService userService;

	@Test
	public void testSaveUserAccount() throws Exception {
		UserAccountModel userAccount = this.createUserAccount();
		ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

		this.userService.saveUserAccount(userAccount);

		Mockito.verify(this.workbenchDataManager).addPerson(personCaptor.capture());
		Mockito.verify(this.workbenchDataManager).addUser(userCaptor.capture());
		Person capturedPerson = personCaptor.getAllValues().get(0);
		User capturedUser = userCaptor.getAllValues().get(0);

		Assert.assertEquals(FIRST_NAME, capturedPerson.getFirstName());
		Assert.assertEquals(LAST_NAME, capturedPerson.getLastName());
		Assert.assertEquals(EMAIL, capturedPerson.getEmail());

		Assert.assertEquals(TEST_USERNAME, capturedUser.getName());
		Assert.assertEquals(60, capturedUser.getPassword().length());
		// Check that non-encrypted password matches the encrypted one
		Assert.assertTrue(passwordEncoder.matches(PASSWORD, capturedUser.getPassword()));
		Assert.assertEquals(DateUtil.getCurrentDateAsIntegerValue(), capturedUser.getAssignDate());
		Assert.assertEquals(DateUtil.getCurrentDateAsIntegerValue(), capturedUser.getCloseDate());
	}

	@Test
	public void testSaveNewUserAccount() throws Exception {
		UserAccountModel userAccount = this.createUserAccount();
		ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

		this.userService.saveNewUserAccount(userAccount);

		Mockito.verify(this.workbenchDataManager).addPerson(personCaptor.capture());
		Mockito.verify(this.workbenchDataManager).addUser(userCaptor.capture());
		Person capturedPerson = personCaptor.getAllValues().get(0);
		User capturedUser = userCaptor.getAllValues().get(0);

		Assert.assertEquals(FIRST_NAME, capturedPerson.getFirstName());
		Assert.assertEquals(LAST_NAME, capturedPerson.getLastName());
		Assert.assertEquals(EMAIL, capturedPerson.getEmail());

		Assert.assertEquals(TEST_USERNAME, capturedUser.getName());
		Assert.assertEquals(60, capturedUser.getPassword().length());
		// Check that non-encrypted password matches the encrypted one
		Assert.assertTrue(passwordEncoder.matches(TEST_USERNAME, capturedUser.getPassword()));
		Assert.assertEquals(new Integer(0), capturedUser.getAssignDate());
		Assert.assertEquals(new Integer(0), capturedUser.getCloseDate());

	}

	protected UserAccountModel createUserAccount() {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName(FIRST_NAME);
		userAccount.setLastName(LAST_NAME);
		userAccount.setEmail(EMAIL);
		userAccount.setRole("ADMIN");
		userAccount.setUsername(TEST_USERNAME);
		userAccount.setPassword(PASSWORD);
		return userAccount;
	}

	@Test
	public void testIsValidUserLogin() throws Exception {
		UserAccountModel userAccount = this.createUserAccount();
		User user = new User();
		user.setPersonid(TEST_PERSON_ID);
		user.setPassword(HASHED_PASSWORD);
		Person person = new Person();
		List<User> userList = new ArrayList<>();
		userList.add(user);

		Mockito.when(this.workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
				userList);
		Mockito.when(this.workbenchDataManager.getPersonById(TEST_PERSON_ID)).thenReturn(person);

		Assert.assertTrue(this.userService.isValidUserLogin(userAccount));
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
		String userName = "testUsername";
		String password = "testPassword";

		this.userService.updateUserPassword(userName, password);

		Mockito.verify(this.workbenchDataManager).changeUserPassword(eq(userName), not(eq(password)));
		Mockito.verify(this.workbenchDataManager).changeUserPassword(eq(userName), passwordCaptor.capture());
		String capturedPassword = passwordCaptor.getAllValues().get(0);
		Assert.assertEquals(60, capturedPassword.length());
		// Check that non-encrypted password matches the encrypted one
		Assert.assertTrue(passwordEncoder.matches(password, capturedPassword));
	}

	@Test
	public void testIsPasswordEqualToUsername() throws Exception {
		User user = new User();
		user.setName(PASSWORD);
		user.setPassword(HASHED_PASSWORD);

		Assert.assertTrue(this.userService.isPasswordEqualToUsername(user));
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		User user = new User();
		user.setPersonid(TEST_PERSON_ID);
		Person person = new Person();
		List<User> userList = new ArrayList<>();
		userList.add(user);

		Mockito.when(this.workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
				userList);
		Mockito.when(this.workbenchDataManager.getPersonById(TEST_PERSON_ID)).thenReturn(person);

		User resultUser = this.userService.getUserByUserName(TEST_USERNAME);

		Assert.assertEquals("Should be the same as the setup user", user, resultUser);

	}

	@Test
	public void testGetUserByUserId() throws Exception {
		User user = new User();
		user.setPersonid(TEST_PERSON_ID);
		Person person = new Person();
		Mockito.when(this.workbenchDataManager.getUserById(TEST_USER_ID)).thenReturn(user);
		Mockito.when(this.workbenchDataManager.getPersonById(TEST_PERSON_ID)).thenReturn(person);

		User resultUser = this.userService.getUserByUserid(TEST_USER_ID);

		Assert.assertEquals("Should be the same as the setup user", user, resultUser);

	}

}

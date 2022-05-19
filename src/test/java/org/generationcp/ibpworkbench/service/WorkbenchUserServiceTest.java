
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;

import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Mock
	private UserService workbenchDataManager;

	@InjectMocks
	private WorkbenchUserService userService;

	@Test
	public void testSaveUserAccount() throws Exception {
		final UserAccountModel userAccount = this.createUserAccount();
		final ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
		final ArgumentCaptor<WorkbenchUser> userCaptor = ArgumentCaptor.forClass(WorkbenchUser.class);

		this.userService.saveUserAccount(userAccount);

		Mockito.verify(this.workbenchDataManager).addPerson(personCaptor.capture());
		Mockito.verify(this.workbenchDataManager).addUser(userCaptor.capture());
		final Person capturedPerson = personCaptor.getAllValues().get(0);
		final WorkbenchUser capturedUser = userCaptor.getAllValues().get(0);

		Assert.assertEquals(FIRST_NAME, capturedPerson.getFirstName());
		Assert.assertEquals(LAST_NAME, capturedPerson.getLastName());
		Assert.assertEquals(EMAIL, capturedPerson.getEmail());

		Assert.assertEquals(TEST_USERNAME, capturedUser.getName());
		Assert.assertEquals(60, capturedUser.getPassword().length());
		// Check that non-encrypted password matches the encrypted one
		Assert.assertTrue(this.passwordEncoder.matches(PASSWORD, capturedUser.getPassword()));
		Assert.assertEquals(DateUtil.getCurrentDateAsIntegerValue(), capturedUser.getAssignDate());
		Assert.assertEquals(DateUtil.getCurrentDateAsIntegerValue(), capturedUser.getCloseDate());
	}

	protected UserAccountModel createUserAccount() {
		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName(FIRST_NAME);
		userAccount.setLastName(LAST_NAME);
		userAccount.setEmail(EMAIL);
		// Role ID 1 = ADMIN
		userAccount.setRole(new Role(1));
		userAccount.setUsername(TEST_USERNAME);
		userAccount.setPassword(PASSWORD);
		return userAccount;
	}

	@Test
	public void testIsUserActive() throws Exception {
		final UserAccountModel userAccount = this.createUserAccount();
		final WorkbenchUser user = new WorkbenchUser();
		user.setStatus(0);
		final Person person = new Person();
		person.setId(TEST_PERSON_ID);
		user.setPerson(person);
		final List<WorkbenchUser> userList = new ArrayList<>();
		userList.add(user);

		Mockito.when(this.workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
			userList);

		Assert.assertTrue(this.userService.isUserActive(userAccount));

		user.setStatus(1);
		Assert.assertFalse(this.userService.isUserActive(userAccount));

		user.setStatus(null);
		Assert.assertFalse(this.userService.isUserActive(userAccount));

		userList.remove(user);
		Assert.assertFalse(this.userService.isUserActive(userAccount));
	}

	@Test
	public void testIsValidUserLogin() throws Exception {
		final UserAccountModel userAccount = this.createUserAccount();
		final WorkbenchUser user = new WorkbenchUser();
		user.setPassword(HASHED_PASSWORD);
		final Person person = new Person();
		person.setId(TEST_PERSON_ID);
		user.setPerson(person);
		final List<WorkbenchUser> userList = new ArrayList<>();
		userList.add(user);

		Mockito.when(this.workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
			userList);

		Assert.assertTrue(this.userService.isValidUserLogin(userAccount));
	}

	@Test
	public void testIsValidUserLoginShouldReturnFalseIfInvalid() {
		final UserAccountModel userAccount = this.createUserAccount();
		Mockito.when(this.workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
			Collections.<WorkbenchUser>emptyList());
		Assert.assertFalse(this.userService.isValidUserLogin(userAccount));
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		final ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
		final String userName = "testUsername";
		final String password = "testPassword";

		this.userService.updateUserPassword(userName, password);

		Mockito.verify(this.workbenchDataManager).changeUserPassword(eq(userName), not(eq(password)));
		Mockito.verify(this.workbenchDataManager).changeUserPassword(eq(userName), passwordCaptor.capture());
		final String capturedPassword = passwordCaptor.getAllValues().get(0);
		Assert.assertEquals(60, capturedPassword.length());
		// Check that non-encrypted password matches the encrypted one
		Assert.assertTrue(this.passwordEncoder.matches(password, capturedPassword));
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		final WorkbenchUser user = new WorkbenchUser();
		final Person person = new Person();
		person.setId(TEST_PERSON_ID);
		user.setPerson(person);

		final List<WorkbenchUser> userList = new ArrayList<>();
		userList.add(user);

		Mockito.when(this.workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
			userList);

		final WorkbenchUser resultUser = this.userService.getUserByUserName(TEST_USERNAME);

		Assert.assertEquals("Should be the same as the setup user", user, resultUser);

	}

	@Test
	public void testGetUserByUserNameWithNoExistingUserAcct() throws Exception {
		final List<WorkbenchUser> userList = new ArrayList<>();

		Mockito.when(this.workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
			userList);

		final WorkbenchUser resultUser = this.userService.getUserByUserName(TEST_USERNAME);

		Assert.assertNull("Should be null since there is no user with the TEST_USERNAME", resultUser);

	}

	@Test
	public void testGetUserByUserId() throws Exception {
		final WorkbenchUser user = new WorkbenchUser();
		final Person person = new Person();
		user.setPerson(person);
		person.setId(TEST_PERSON_ID);
		Mockito.when(this.workbenchDataManager.getUserById(TEST_USER_ID)).thenReturn(user);

		final WorkbenchUser resultUser = this.userService.getUserByUserid(TEST_USER_ID);

		Assert.assertEquals("Should be the same as the setup user", user, resultUser);

	}
}

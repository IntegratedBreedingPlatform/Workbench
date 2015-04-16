package org.generationcp.ibpworkbench.service;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchUserServiceTest {

	private static final int TEST_USER_ID = 1;
	private static final Integer TEST_PERSON_ID = 2;
	private static final String TEST_USERNAME = "TEST_USERNAME";
	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private WorkbenchUserService userService;

	@Test
	public void testSaveUserAccount() throws Exception {
		UserAccountModel userAccount = createUserAccount();

		userService.saveUserAccount(userAccount);

		verify(workbenchDataManager).addPerson(any(Person.class));
		verify(workbenchDataManager).addUser(any(User.class));
		verify(workbenchDataManager).addSecurityQuestion(any(SecurityQuestion.class));
	}

	protected UserAccountModel createUserAccount() {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");
		userAccount.setEmail("email@email.com");
		userAccount.setRole("ADMIN");
		userAccount.setUsername("username");
		userAccount.setPassword("password");
		return userAccount;
	}

	@Test
	public void testIsValidUserLogin() throws Exception {
		UserAccountModel userAccount = createUserAccount();

		userService.isValidUserLogin(userAccount);

		verify(workbenchDataManager).isValidUserLogin(userAccount.getUsername(),
				userAccount.getPassword());
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		UserAccountModel userAccount = createUserAccount();

		userService.updateUserPassword(userAccount);

		verify(workbenchDataManager).changeUserPassword(userAccount.getUsername(),
				userAccount.getPassword());
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		User user = new User();
		user.setPersonid(TEST_PERSON_ID);
		Person person = new Person();
		when(workbenchDataManager.getUserByName(TEST_USERNAME, 0, 1, Operation.EQUAL).get(0)).thenReturn(user);
		when(workbenchDataManager.getPersonById(TEST_PERSON_ID)).thenReturn(person);

		User resultUser = userService.getUserByUserName(TEST_USERNAME);

		assertEquals("Should be the same as the setup user",user,resultUser);

	}

	@Test
	public void testGetUserByUserId() throws Exception {
		User user = new User();
		user.setPersonid(TEST_PERSON_ID);
		Person person = new Person();
		when(workbenchDataManager.getUserById(TEST_USER_ID)).thenReturn(user);
		when(workbenchDataManager.getPersonById(TEST_PERSON_ID)).thenReturn(person);

		User resultUser = userService.getUserByUserid(TEST_USER_ID);

		assertEquals("Should be the same as the setup user",user,resultUser);

	}

}
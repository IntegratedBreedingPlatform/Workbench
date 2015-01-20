package org.generationcp.ibpworkbench.service;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchUserServiceTest {

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

		verify(workbenchDataManager).changeUserPassword(userAccount.getUsername(),userAccount.getPassword());
	}

}
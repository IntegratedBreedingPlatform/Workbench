
package org.generationcp.ibpworkbench.actions;

import junit.framework.Assert;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SaveNewProjectAddUserActionTest {

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private SaveNewProjectAddUserAction action = new SaveNewProjectAddUserAction(null, null);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSaveUserAccount() throws MiddlewareQueryException {

		UserAccountModel userAccount = new UserAccountModel();
		TwinTableSelect<User> membersSelect = new TwinTableSelect<User>(User.class);

		this.action.saveUserAccount(userAccount, membersSelect);

		Mockito.verify(this.workbenchDataManager).addPerson(Matchers.any(Person.class));
		Mockito.verify(this.workbenchDataManager).addUser(Matchers.any(User.class));
		Mockito.verify(this.workbenchDataManager).insertOrUpdateUserInfo(Matchers.any(UserInfo.class));

		Assert.assertEquals("The user must be added to the TwinTableSelect UI", 1, membersSelect.getValue().size());

	}
}

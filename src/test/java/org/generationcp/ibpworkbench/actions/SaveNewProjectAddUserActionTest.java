
package org.generationcp.ibpworkbench.actions;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SaveNewProjectAddUserActionTest {

	@Mock
	private WorkbenchUserService workbenchUserService;

	@InjectMocks
	private SaveNewProjectAddUserAction action = new SaveNewProjectAddUserAction(null, null);

	@Before
	public void setUp() {
	}

	@Test
	public void testSaveUserAccount() throws MiddlewareQueryException {

		UserAccountModel userAccount = new UserAccountModel();
		TwinTableSelect<User> membersSelect = Mockito.mock(TwinTableSelect.class);
		User user = Mockito.mock(User.class);

		Set<User> userSet = new HashSet<>();
		userSet.add(user);

		Mockito.when(this.workbenchUserService.saveNewUserAccount(userAccount)).thenReturn(user);
		Mockito.doNothing().when(membersSelect).addItem(user);
		Mockito.when(membersSelect.getValue()).thenReturn(userSet);

		this.action.saveUserAccount(userAccount, membersSelect);

		Mockito.verify(this.workbenchUserService).saveNewUserAccount(userAccount);
		Assert.assertEquals("The user must be added to the TwinTableSelect UI", 1, membersSelect.getValue().size());

	}
}

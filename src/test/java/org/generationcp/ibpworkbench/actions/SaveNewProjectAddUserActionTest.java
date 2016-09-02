
package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.ListUtils;
import org.generationcp.commons.security.Role;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.UserRole;
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

	@Mock
	private ProgramService programService;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private SessionData sessionData;

	@InjectMocks
	private final SaveNewProjectAddUserAction action = new SaveNewProjectAddUserAction(null, null);

	private final Project currentProject = new Project();

	@Before
	public void setUp() {

		final String crop = "maize";
		final CropType cropType = new CropType(crop);
		currentProject.setCropType(cropType);

		Mockito.when(this.sessionData.getLastOpenedProject()).thenReturn(currentProject);

	}

	@Test
	public void testSaveUserAccount() throws MiddlewareQueryException {

		final UserAccountModel userAccount = new UserAccountModel();
		final TwinTableSelect<User> membersSelect = Mockito.mock(TwinTableSelect.class);
		final User user = Mockito.mock(User.class);

		final Set<User> userSet = new HashSet<>();
		userSet.add(user);

		Mockito.when(this.workbenchUserService.saveNewUserAccount(userAccount)).thenReturn(user);
		Mockito.doNothing().when(membersSelect).addItem(user);
		Mockito.when(this.workbenchDataManager.getCropTypeByName(currentProject.getCropType().getCropName())).thenReturn(currentProject.getCropType());
		Mockito.when(membersSelect.getValue()).thenReturn(userSet);

		this.action.saveUserAccount(userAccount, membersSelect);

		Mockito.verify(this.workbenchUserService).saveNewUserAccount(userAccount);

		Mockito.verify(this.programService).addUserToAllProgramsOfCropTypeIfAdmin(user, currentProject.getCropType());

		Assert.assertEquals("The user must be added to the TwinTableSelect UI", 1, membersSelect.getValue().size());

	}

	@Test
	public void addUserToAllProgramsOfCurrentCropIfAdminUserIsAdmin() {

		final User user = new User();
		user.setUserid(1);
		user.setRoles(Arrays.asList(new UserRole(user, Role.ADMIN.toString())));

		this.action.addUserToAllProgramsOfCurrentCropIfAdmin(user);

		Mockito.verify(this.programService).addUserToAllProgramsOfCropTypeIfAdmin(user, currentProject.getCropType());

		Assert.assertFalse("The user is admin so it should be disabled for selection",user.isEnabled());
	}

	@Test
	public void addUserToAllProgramsOfCurrentCropIfAdminUserIsNotAdmin() {

		final User user = new User();
		user.setUserid(1);

		this.action.addUserToAllProgramsOfCurrentCropIfAdmin(user);

		Mockito.verify(this.programService).addUserToAllProgramsOfCropTypeIfAdmin(user, currentProject.getCropType());

		Assert.assertTrue("The user is not admin so it should be enabled for selection",user.isEnabled());
	}
}

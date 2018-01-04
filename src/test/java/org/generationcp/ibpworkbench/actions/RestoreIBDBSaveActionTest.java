
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;

public class RestoreIBDBSaveActionTest {

	private static final int NO_OF_RESTORED_PROGRAMS = 10;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ProgramService programService;

	@Mock
	private MySQLUtil mySqlUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private File restoreFile;
	
	@Captor
	private ArgumentCaptor<Set<User>> userSetCaptor;

	private RestoreIBDBSaveAction restoreAction;

	private User defaultAdminUser;
	private User loggedInUser;
	private Project currentProject;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.currentProject = this.createProject();
		this.restoreAction = new RestoreIBDBSaveAction(this.currentProject, Mockito.mock(Window.class));
		this.restoreAction.setWorkbenchDataManager(this.workbenchDataManager);
		this.restoreAction.setContextUtil(this.contextUtil);
		this.restoreAction.setProgramService(this.programService);
		this.restoreAction.setMysqlUtil(this.mySqlUtil);
		this.restoreAction.setMessageSource(this.messageSource);
		this.restoreAction.setRestoreFile(this.restoreFile);

		// WorkbenchDataManager mocks
		this.defaultAdminUser = this.createUser(1, ProgramService.ADMIN_USERNAME, 1);
		this.loggedInUser = this.createUser(2, "mrbreeder", 2);
		Mockito.when(this.workbenchDataManager.getUserByUsername(ProgramService.ADMIN_USERNAME)).thenReturn(this.defaultAdminUser);
		Mockito.when(this.workbenchDataManager.getProjectsByCrop(this.currentProject.getCropType()))
				.thenReturn(this.createTestProjectsForCrop());
	}

	@Test
	public void testAddDefaultAdminToProgramsWhenCurrentUserIsDefaultAdmin() {
		// Setup default admin as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.defaultAdminUser);

		// Call method to test
		this.restoreAction.addDefaultAdminAndCurrentUserAsMembersOfRestoredPrograms();

		this.verifyCurrentUserWasAddedToAllPrograms(this.defaultAdminUser);
	}

	@Test
	public void testAddDefaultAdminToProgramsWhenCurrentUserIsNotDefaultAdmin() {
		// Setup another user (not the default admin) as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.loggedInUser);

		// Call method to test
		this.restoreAction.addDefaultAdminAndCurrentUserAsMembersOfRestoredPrograms();

		this.verifyCurrentUserWasAddedToAllPrograms(this.loggedInUser);
	}

	// Verify that current user was added to all programs for crop
	private void verifyCurrentUserWasAddedToAllPrograms(final User currentUser) {
		Mockito.verify(this.programService, Mockito.times(RestoreIBDBSaveActionTest.NO_OF_RESTORED_PROGRAMS))
				.saveProgramMembers(Matchers.any(Project.class), this.userSetCaptor.capture());
		final Set<User> users = this.userSetCaptor.getValue();
		
		// "Expecting only the current user to be added."
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(currentUser, users.iterator().next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRestoreProcessWhenCurrentUserIsNotDefaultAdmin() throws Exception {
		// Setup another user (not the default admin) as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(this.loggedInUser.getUserid());
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.loggedInUser);
		Mockito.when(this.workbenchDataManager.getLocalIbdbUserId(this.loggedInUser.getUserid(), this.currentProject.getProjectId()))
				.thenReturn(this.loggedInUser.getUserid());

		// Call method to test. True means user confirmed to continue restore operation
		this.restoreAction.onClose(new CustomConfirmDialog(true));

		// Verify key restore operations
		Mockito.verify(this.mySqlUtil).restoreDatabase(Matchers.anyString(), Matchers.any(File.class), Matchers.any(Callable.class));
		Mockito.verify(this.mySqlUtil).updateOwnerships(this.currentProject.getDatabaseName(), this.loggedInUser.getUserid());
		Mockito.verify(this.contextUtil).logProgramActivity(Mockito.anyString(), Mockito.anyString());
		this.verifyCurrentUserWasAddedToAllPrograms(this.loggedInUser);

		Assert.assertFalse("Expecting not to have error since restore process was succesful.", this.restoreAction.isHasRestoreError());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRestoreProcessWhenCurrentUserIsDefaultAdmin() throws Exception {
		// Setup another user (not the default admin) as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(this.defaultAdminUser.getUserid());
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.defaultAdminUser);
		Mockito.when(this.workbenchDataManager.getLocalIbdbUserId(this.defaultAdminUser.getUserid(), this.currentProject.getProjectId()))
				.thenReturn(this.defaultAdminUser.getUserid());

		// Call method to test. True means user confirmed to continue restore operation
		this.restoreAction.onClose(new CustomConfirmDialog(true));

		// Verify key restore operations
		Mockito.verify(this.mySqlUtil).restoreDatabase(Matchers.anyString(), Matchers.any(File.class), Matchers.any(Callable.class));
		Mockito.verify(this.mySqlUtil).updateOwnerships(this.currentProject.getDatabaseName(), this.defaultAdminUser.getUserid());
		Mockito.verify(this.contextUtil).logProgramActivity(Mockito.anyString(), Mockito.anyString());

		this.verifyCurrentUserWasAddedToAllPrograms(this.defaultAdminUser);

		Assert.assertFalse("Expecting not to have error since restore process was succesful.", this.restoreAction.isHasRestoreError());
	}

	@Test
	public void testRestoreProcessWhenUserDidNotConfirmToProceed() {
		// Call method to test. False means user did not confirm to continue restore operation
		this.restoreAction.onClose(new CustomConfirmDialog(false));

		// Verify no interactions with mocks
		Mockito.verifyZeroInteractions(this.mySqlUtil);
		Mockito.verifyNoMoreInteractions(this.programService);
		Mockito.verifyNoMoreInteractions(this.workbenchDataManager);

		Assert.assertTrue("Expecting to have error since restore action was not completed.", this.restoreAction.isHasRestoreError());
	}
	
	@Test
	public void testUpdateGermplasmListOwnershipWhenUserIsNotNull() throws IOException, SQLException{
		this.restoreAction.updateGermplasmListOwnership(this.loggedInUser.getUserid());
		
		final ArgumentCaptor<String> databaseNameCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(this.mySqlUtil, Mockito.times(1)).updateOwnerships(databaseNameCaptor.capture(), userIdCaptor.capture());
		Assert.assertEquals(databaseNameCaptor.getValue(), this.currentProject.getDatabaseName());
		Assert.assertEquals(userIdCaptor.getValue(), this.loggedInUser.getUserid());
	}
	
	@Test
	public void testUpdateGermplasmListOwnershipWhenUserIsNull() throws IOException, SQLException{
		this.restoreAction.updateGermplasmListOwnership(null);
		
		Mockito.verify(this.workbenchDataManager, Mockito.never()).addProjectActivity(Matchers.any(ProjectActivity.class));
	}

	private List<Project> createTestProjectsForCrop() {
		final List<Project> projects = new ArrayList<>();
		for (int i = 0; i < RestoreIBDBSaveActionTest.NO_OF_RESTORED_PROGRAMS; i++) {
			projects.add(this.createProject());
		}
		return projects;
	}

	private User createUser(final Integer userId, final String username, final Integer personId) {
		final User loggedInUser = new User();
		loggedInUser.setUserid(userId);
		loggedInUser.setName(username);
		loggedInUser.setPersonid(personId);
		return loggedInUser;
	}

	private Project createProject() {
		final Project project = new Project();
		final Integer id = new Random().nextInt(100);
		project.setProjectId(Long.valueOf(id.toString()));
		project.setProjectName("TestRiceProject " + id);
		final CropType cropType = new CropType(CropType.CropEnum.RICE.toString());
		project.setCropType(cropType);

		return project;
	}

	// We will use this class to simulate that the user has confirmed in the dialog
	private class CustomConfirmDialog extends ConfirmDialog {

		private static final long serialVersionUID = 1L;

		CustomConfirmDialog(final boolean isConfirmed) {
			super();
			this.setConfirmed(isConfirmed);
		}
	}
}

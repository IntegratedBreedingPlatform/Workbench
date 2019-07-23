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
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;

public class RestoreIBDBSaveActionTest {

	private static final String SUPERADMIN_USERNAME = "superadmin";

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

	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;

	@Captor
	private ArgumentCaptor<Set<WorkbenchUser>> userSetCaptor;

	private RestoreIBDBSaveAction restoreAction;

	private WorkbenchUser superAdminUser;
	private WorkbenchUser loggedInUser;
	private Project currentProject;
	private List<Project> restoredProjects;

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
		this.restoreAction.setInstallationDirectoryUtil(this.installationDirectoryUtil);

		// WorkbenchDataManager mocks
		this.superAdminUser = this.createUser(1, SUPERADMIN_USERNAME, 1);
		this.loggedInUser = this.createUser(2, "mrbreeder", 2);
		this.restoredProjects = this.createTestProjectsForCrop();
		Mockito.when(this.workbenchDataManager.getProjectsByCrop(this.currentProject.getCropType()))
				.thenReturn(this.restoredProjects);
	}

	@Test
	public void testAddSuperAdminToProgramsWhenCurrentUserIsSuperAdmin() {
		// Setup super admin as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.superAdminUser);

		// Call method to test
		this.restoreAction.addSuperAdminAndCurrentUserAsMembersOfRestoredPrograms(this.restoredProjects);

		this.verifyCurrentUserWasAddedToAllPrograms(this.superAdminUser);
	}

	@Test
	public void testAddSuperAdminToProgramsWhenCurrentUserIsNotSuperAdmin() {
		// Setup another user (not superadmin user) as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.loggedInUser);

		// Call method to test
		this.restoreAction.addSuperAdminAndCurrentUserAsMembersOfRestoredPrograms(this.restoredProjects);

		this.verifyCurrentUserWasAddedToAllPrograms(this.loggedInUser);
	}

	// Verify that current user was added to all programs for crop
	private void verifyCurrentUserWasAddedToAllPrograms(final WorkbenchUser currentUser) {
		Mockito.verify(this.programService, Mockito.times(RestoreIBDBSaveActionTest.NO_OF_RESTORED_PROGRAMS))
				.saveProgramMembers(ArgumentMatchers.any(Project.class), this.userSetCaptor.capture());
		final Set<WorkbenchUser> users = this.userSetCaptor.getValue();

		// "Expecting only the current user to be added."
		Assert.assertEquals(1, users.size());
		Assert.assertEquals(currentUser, users.iterator().next());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRestoreProcessWhenCurrentUserIsNotSuperAdmin() throws Exception {
		// Setup another user (not the super admin) as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(this.loggedInUser.getUserid());
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.loggedInUser);

		// Call method to test. True means user confirmed to continue restore operation
		this.restoreAction.onClose(new CustomConfirmDialog(true));

		// Verify key restore operations
		Mockito.verify(this.mySqlUtil).restoreDatabase(ArgumentMatchers.anyString(), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Callable.class));
		Mockito.verify(this.mySqlUtil).updateOwnerships(this.currentProject.getDatabaseName(), this.loggedInUser.getUserid());
		this.verifyCurrentUserWasAddedToAllPrograms(this.loggedInUser);
		Mockito.verify(this.installationDirectoryUtil).resetWorkspaceDirectoryForCrop(this.currentProject.getCropType(), this.restoredProjects);
		Mockito.verify(this.contextUtil).logProgramActivity(ArgumentMatchers.<String>isNull(), Mockito.anyString());


		Assert.assertFalse("Expecting not to have error since restore process was succesful.", this.restoreAction.isHasRestoreError());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRestoreProcessWhenCurrentUserIsSuperAdmin() throws Exception {
		// Setup another user (not the default admin) as current user
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(this.superAdminUser.getUserid());
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.superAdminUser);

		// Call method to test. True means user confirmed to continue restore operation
		this.restoreAction.onClose(new CustomConfirmDialog(true));

		// Verify key restore operations
		Mockito.verify(this.mySqlUtil).restoreDatabase(ArgumentMatchers.anyString(), ArgumentMatchers.any(File.class), ArgumentMatchers.any(Callable.class));
		Mockito.verify(this.mySqlUtil).updateOwnerships(this.currentProject.getDatabaseName(), this.superAdminUser.getUserid());
		this.verifyCurrentUserWasAddedToAllPrograms(this.superAdminUser);
		Mockito.verify(this.installationDirectoryUtil).resetWorkspaceDirectoryForCrop(this.currentProject.getCropType(), this.restoredProjects);
		Mockito.verify(this.contextUtil).logProgramActivity(ArgumentMatchers.<String>isNull(), Mockito.anyString());


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
		Mockito.verifyZeroInteractions(this.installationDirectoryUtil);

		Assert.assertTrue("Expecting to have error since restore action was not completed.", this.restoreAction.isHasRestoreError());
	}

	@Test
	public void testUpdateGermplasmListOwnershipWhenUserIsNotNull() throws IOException, SQLException {
		this.restoreAction.updateGermplasmListOwnership(this.loggedInUser.getUserid());

		final ArgumentCaptor<String> databaseNameCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(this.mySqlUtil, Mockito.times(1)).updateOwnerships(databaseNameCaptor.capture(), userIdCaptor.capture());
		Assert.assertEquals(databaseNameCaptor.getValue(), this.currentProject.getDatabaseName());
		Assert.assertEquals(userIdCaptor.getValue(), this.loggedInUser.getUserid());
	}

	@Test
	public void testUpdateGermplasmListOwnershipWhenUserIsNull() throws IOException, SQLException {
		this.restoreAction.updateGermplasmListOwnership(null);

		Mockito.verify(this.workbenchDataManager, Mockito.never()).addProjectActivity(ArgumentMatchers.any(ProjectActivity.class));
	}

	private List<Project> createTestProjectsForCrop() {
		final List<Project> projects = new ArrayList<>();
		for (int i = 0; i < RestoreIBDBSaveActionTest.NO_OF_RESTORED_PROGRAMS; i++) {
			projects.add(this.createProject());
		}
		return projects;
	}

	private WorkbenchUser createUser(final Integer userId, final String username, final Integer personId) {
		final WorkbenchUser loggedInUser = new WorkbenchUser();
		loggedInUser.setUserid(userId);
		loggedInUser.setName(username);
		final Person person = new Person();
		person.setId(personId);
		loggedInUser.setPerson(person);
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


package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Container;

public class ProgramMembersPanelTest {

	private static final int OWNER_USER_ID = 1;
	private static final int OWNER_PERSON_ID = 1;
	private static final String OWNER_NAME = "USER1";

	private static final int MEMBER_USER_ID = 2;
	private static final int MEMBER_PERSON_ID = 2;
	private static final String MEMBER_NAME = "USER2";

	private static final int ADMIN_USER_ID = 3;
	private static final int ADMIN_PERSON_ID = 3;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private SessionData sessionData;

	private Project project;

	@InjectMocks
	private ProgramMembersPanel controller;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.project = this.createProjectTestData(1, ProgramMembersPanelTest.OWNER_USER_ID);
		this.controller = new ProgramMembersPanel(this.project);
		this.controller.setWorkbenchDataManager(this.workbenchDataManager);
		this.controller.setSessionData(this.sessionData);
	}

	private Project createProjectTestData(final long projectId, final int owner) {
		final Project project = new Project();
		project.setProjectId(projectId);
		project.setProjectName("Project " + projectId);
		project.setUniqueID(UUID.randomUUID().toString());
		project.setUserId(owner);
		return project;
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateUsersContainerWhenProgramOwnerIsCurrentUser() {
		this.mockProgramMembers();
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);

		final Container usersContainer = this.controller.createUsersContainer();

		final Collection<User> programMembers = (Collection<User>) usersContainer.getItemIds();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		// Check that program owner should be disabled
		for (final User user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)) {
				Assert.assertFalse("Program Owner and Default Admin users should be disabled so they cannot be removed as member.",
						user.isEnabled());
			} else {
				Assert.assertTrue("Other users should be enabled so they can be removed as members.", user.isEnabled());
			}
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateUsersContainerWhenCurrentUserIsNotProgramOwner() {
		this.mockProgramMembers();
		this.mockCurrentUser(ProgramMembersPanelTest.MEMBER_USER_ID);

		final Container usersContainer = this.controller.createUsersContainer();

		final Collection<User> programMembers = (Collection<User>) usersContainer.getItemIds();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		// Two users should be disabled - current user and program owner
		for (final User user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)
					|| user.getUserid().equals(ProgramMembersPanelTest.MEMBER_PERSON_ID)) {
				Assert.assertFalse("Program owner and current user should be disabled and cannot be removed as program members.",
						user.isEnabled());
			} else {
				Assert.assertTrue("Other users should be enabled so they can be removed as members.", user.isEnabled());
			}
		}
	}

	@Test
	public void testInitializeUsers() {
		// Setup test data and mocks
		this.mockDataAndReturnTheProjectUserRoles();
		this.mockCurrentUser(ProgramMembersPanelTest.MEMBER_USER_ID);

		// Initialization in controller
		this.controller.initializeComponents();
		this.controller.initializeValues();

		// Call method to test
		this.controller.initializeUsers();

		// Check that members are selected in twin table
		final Set<User> programMembers = this.controller.getProgramMembersDisplayed();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		// Check that ADMIN user is disabled from selection
		for (final User user : programMembers) {
			if (ProgramService.ADMIN_USERNAME.equalsIgnoreCase(user.getName())) {
				Assert.assertFalse("Default Admin should be disabled and cannot be removed as program member.", user.isEnabled());
			}
		}
	}

	private List<ProjectUserRole> mockDataAndReturnTheProjectUserRoles() {
		final Role dummyRole = new Role();
		dummyRole.setRoleId(1);
		Mockito.doReturn(Arrays.asList(dummyRole)).when(this.workbenchDataManager).getAllRolesOrderedByLabel();
		
		final List<ProjectUserRole> projectUserRoles = this.createProjectUserRolesTestData(this.project);
		Mockito.doReturn(projectUserRoles).when(this.workbenchDataManager).getProjectUserRolesByProject(this.project);

		return projectUserRoles;
	}

	private List<ProjectUserRole> createProjectUserRolesTestData(final Project project) {
		final List<ProjectUserRole> projectUserRoles = new ArrayList<>();
		final List<User> programMembers = this.createProgramMembersTestData();
		for (final User user : programMembers) {
			final ProjectUserRole projectUserRole = new ProjectUserRole();
			projectUserRole.setProject(project);
			projectUserRole.setUserId(user.getUserid());
			projectUserRoles.add(projectUserRole);

			Mockito.doReturn(user).when(this.workbenchDataManager).getUserById(user.getUserid());
		}
		return projectUserRoles;
	}

	private void mockProgramMembers() {
		final List<User> programMembers = this.createProgramMembersTestData();
		Mockito.doReturn(programMembers).when(this.workbenchDataManager).getAllUsersSorted();
		for (final User user : programMembers) {
			Mockito.doReturn(this.createPersonTestData(user.getPersonid())).when(this.workbenchDataManager)
					.getPersonById(user.getPersonid());
		}
	}

	private void mockCurrentUser(final int userId) {
		Mockito.doReturn(new User(userId)).when(this.sessionData).getUserData();
	}

	private Person createPersonTestData(final Integer personid) {
		final Person person = new Person();
		person.setId(personid);
		return person;
	}

	private List<User> createProgramMembersTestData() {
		final List<User> programMembers = new ArrayList<>();
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.OWNER_USER_ID, ProgramMembersPanelTest.OWNER_NAME,
				ProgramMembersPanelTest.OWNER_PERSON_ID));
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.MEMBER_USER_ID, ProgramMembersPanelTest.MEMBER_NAME,
				ProgramMembersPanelTest.MEMBER_PERSON_ID));
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.ADMIN_USER_ID, ProgramService.ADMIN_USERNAME,
				ProgramMembersPanelTest.ADMIN_PERSON_ID));
		return programMembers;
	}

	private User createUsersTestData(final int userId, final String username, final int personId) {
		final User user = new User(userId);
		user.setName(username);
		user.setPersonid(personId);
		user.setPerson(new Person("Mister", "User", username + userId));
		
		final List<UserRole> userRoleList = new ArrayList<>();
		userRoleList.add(new UserRole(user, "ADMIN"));
		user.setRoles(userRoleList);
		
		return user;
	}

}

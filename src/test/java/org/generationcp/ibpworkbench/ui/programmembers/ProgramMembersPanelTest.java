
package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.generationcp.commons.security.Role;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
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
	private static final int MEMBER_USER_ID = 2;
	private static final int MEMBER_PERSON_ID = 2;
	private static final int ADMIN_USER_ID = 3;
	private static final int ADMIN_PERSON_ID = 3;
	private static final org.generationcp.middleware.pojos.workbench.Role WORKFLOW_ROLE_TEST_DATA = 
			new org.generationcp.middleware.pojos.workbench.Role();
	
	static {
		WORKFLOW_ROLE_TEST_DATA.setRoleId(1);
	}

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
	public void testCreateUsersContainerProgramOwnerAsCurrentUser() {
		this.mockProgramMembers();
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final Container usersContainer = this.controller.createUsersContainer();
		final Collection<User> programMembers = (Collection<User>) usersContainer.getItemIds();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());
		for (final User user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)) {
				Assert.assertFalse("Current user should be disabled so it cannot be removed as member.", user.isEnabled());
			} else if (user.getUserid().equals(ProgramMembersPanelTest.MEMBER_PERSON_ID)) {
				Assert.assertTrue("Other users should be enabled so they can be removed as a member.", user.isEnabled());
			} else if (user.getUserid().equals(ProgramMembersPanelTest.ADMIN_PERSON_ID)) {
				Assert.assertTrue("Admin user should be disabled so it cannot be added as a member.", user.isEnabled());
			}
		}
	}

	@Test
	public void testCreateUsersContainerProgramNotOwnerAsCurrentUser() {
		this.mockProgramMembers();
		this.mockCurrentUser(ProgramMembersPanelTest.MEMBER_USER_ID);
		final Container usersContainer = this.controller.createUsersContainer();
		final Collection<User> programMembers = (Collection<User>) usersContainer.getItemIds();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());
		for (final User user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.MEMBER_PERSON_ID)) {
				Assert.assertFalse("Current user should be disabled so it cannot be removed as member.", user.isEnabled());
			} else if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)) {
				Assert.assertFalse("Program owner should be disabled so it cannot be removed as member.", user.isEnabled());
			} else if (user.getUserid().equals(ProgramMembersPanelTest.ADMIN_PERSON_ID)) {
				Assert.assertTrue("Admin user should be enabled so it can be added as a member.", user.isEnabled());
			}
		}
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
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.OWNER_USER_ID, ProgramMembersPanelTest.OWNER_PERSON_ID, false));
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.MEMBER_USER_ID, ProgramMembersPanelTest.MEMBER_PERSON_ID, false));
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.ADMIN_USER_ID, ProgramMembersPanelTest.ADMIN_PERSON_ID, true));
		return programMembers;
	}

	private User createUsersTestData(final int userId, final int personId, boolean isAdmin) {
		final User user = new User(userId);
		user.setPersonid(personId);
		if(isAdmin) {
			user.setRoles(new ArrayList<UserRole>());
			user.getRoles().add(new UserRole(user, Role.ADMIN.toString()));
		}
		return user;
	}
	
	public void testInitializeUsers() {
		final List<ProjectUserRole> projectUserRoles = this.mockDataAndReturnTheProjectUserRoles();
		this.controller.initializeComponents();
		
		//test
		this.controller.initializeUsers();
		for (final ProjectUserRole projectUserRole : projectUserRoles) {
			final User user = this.workbenchDataManager.getUserById(projectUserRole.getUserId());
			Assert.assertFalse("Admin user should be disabled so it cannot be removed as a member.", user.isEnabled());
		}
	}

	private List<ProjectUserRole> mockDataAndReturnTheProjectUserRoles() {
		final List<ProjectUserRole> projectUserRoles = this.createProjectUserRolesTestData(this.project);
		Mockito.doReturn(projectUserRoles).when(this.workbenchDataManager).getProjectUserRolesByProject(this.project);
		for (final ProjectUserRole projectUserRole : projectUserRoles) {
			final User user = new User(projectUserRole.getUserId());
			Mockito.doReturn(user).
				when(this.workbenchDataManager).getUserById(projectUserRole.getUserId());
			final List<org.generationcp.middleware.pojos.workbench.Role> roles = new ArrayList<>();
			roles.add(WORKFLOW_ROLE_TEST_DATA);
			Mockito.doReturn(roles).
				when(this.workbenchDataManager).getRolesByProjectAndUser(this.project, user);
		}
		return projectUserRoles;
	}

	private List<ProjectUserRole> createProjectUserRolesTestData(final Project project) {
		final List<ProjectUserRole> projectUserRoles = new ArrayList<>();
		final List<User> programMembers = this.createProgramMembersTestData();
		for (final User user : programMembers) {
			final ProjectUserRole projectUserRole = new ProjectUserRole();
			projectUserRole.setProject(project);
			projectUserRole.setUserId(user.getUserid());
			projectUserRole.setRole(WORKFLOW_ROLE_TEST_DATA);
			projectUserRoles.add(projectUserRole);
		}
		return projectUserRoles;
	}
}

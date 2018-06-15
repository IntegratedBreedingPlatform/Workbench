
package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Container;
import com.vaadin.ui.Label;

public class ProgramMembersPanelTest {

	private static final int OWNER_USER_ID = 1;
	private static final int OWNER_PERSON_ID = 1;
	private static final String OWNER_NAME = "USER1";

	private static final int MEMBER_USER_ID = 2;
	private static final int MEMBER_PERSON_ID = 2;
	private static final String MEMBER_NAME = "USER2";

	private static final int ADMIN_USER_ID = 3;
	private static final int ADMIN_PERSON_ID = 3;
	private static final String ADMIN_NAME = "USER3";

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ContextUtil contextUtil;

	private Project project;

	@InjectMocks
	private ProgramMembersPanel programMembersPanel;
	
	private WorkbenchUser superAdminUser;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.project = this.createProjectTestData(1, ProgramMembersPanelTest.OWNER_USER_ID);
		this.programMembersPanel = new ProgramMembersPanel(this.project);
		this.programMembersPanel.setWorkbenchDataManager(this.workbenchDataManager);
		this.programMembersPanel.setContextUtil(this.contextUtil);
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

		final Container usersContainer = this.programMembersPanel.createUsersContainer();

		final Collection<WorkbenchUser> programMembers = (Collection<WorkbenchUser>) usersContainer.getItemIds();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		// Check that program owner should be disabled
		for (final WorkbenchUser user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)) {
				Assert.assertFalse(
						"Program Owner and Default Admin users should be disabled so they cannot be removed as member.",
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

		final Container usersContainer = this.programMembersPanel.createUsersContainer();

		final Collection<WorkbenchUser> programMembers = (Collection<WorkbenchUser>) usersContainer.getItemIds();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		// Two users should be disabled - current user and program owner
		for (final WorkbenchUser user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)
					|| user.getUserid().equals(ProgramMembersPanelTest.MEMBER_PERSON_ID)) {
				Assert.assertFalse(
						"Program owner and current user should be disabled and cannot be removed as program members.",
						user.isEnabled());
			} else {
				Assert.assertTrue("Other users should be enabled so they can be removed as members.", user.isEnabled());
			}
		}
	}

	@Test
	public void testGenerateRoleCellForOwner() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithRole(ProgramMembersPanelTest.OWNER_USER_ID);
		final Label roleLabel = this.programMembersPanel.generateRoleCell(itemId);
		Assert.assertEquals(((WorkbenchUser) itemId).getRoles().get(0).getCapitalizedRole(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertEquals("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testGenerateRoleCellForMember() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithRole(ProgramMembersPanelTest.MEMBER_PERSON_ID);
		final Label roleLabel = this.programMembersPanel.generateRoleCell(itemId);
		Assert.assertEquals(((WorkbenchUser) itemId).getRoles().get(0).getCapitalizedRole(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertNotSame("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testgenerateUserNameCellForOwner() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.OWNER_PERSON_ID,
				"UserName", 1, "Firstname", "Middlename");
		final Label roleLabel = this.programMembersPanel.generateUserNameCell(itemId);
		Assert.assertEquals(((WorkbenchUser) itemId).getPerson().getDisplayName(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertEquals("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testgenerateUserNameCellForMember() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.MEMBER_PERSON_ID,
				"UserName", 1, "Firstname", "Middlename");
		final Label roleLabel = this.programMembersPanel.generateUserNameCell(itemId);
		Assert.assertEquals(((WorkbenchUser) itemId).getPerson().getDisplayName(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertNotSame("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testInitializeUsers() {
		// Setup test data and mocks
		Mockito.when(this.workbenchDataManager.getActiveUserIDsByProjectId(Matchers.anyLong()))
				.thenReturn(Arrays.asList(ProgramMembersPanelTest.OWNER_USER_ID, ProgramMembersPanelTest.ADMIN_USER_ID,
						ProgramMembersPanelTest.MEMBER_USER_ID));
		this.mockCurrentUser(ProgramMembersPanelTest.MEMBER_USER_ID);
		final List<WorkbenchUser> testProgramMembers = this.createProgramMembersTestData();
		for (final WorkbenchUser user : testProgramMembers) {
			Mockito.when(this.workbenchDataManager.getUserById(user.getUserid())).thenReturn(user);
		}

		// Initialization in controller
		this.programMembersPanel.initializeComponents();
		this.programMembersPanel.initializeValues();

		// Call method to test
		this.programMembersPanel.initializeUsers();

		// Check that members are selected in twin table
		final Set<WorkbenchUser> programMembers = this.programMembersPanel.getProgramMembersDisplayed();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		// Check that ADMIN user is disabled from selection
		for (final WorkbenchUser user : programMembers) {
			if (this.superAdminUser.equals(user)) {
				Assert.assertFalse("SuperAdmin user should be disabled and cannot be removed as program member.",
						user.isEnabled());
			} else {
				Assert.assertTrue("Non-SuperAdmin user should be enabled and can be removed as program member.",
						user.isEnabled());
			}
		}
	}

	private void mockProgramMembers() {
		final List<WorkbenchUser> programMembers = this.createProgramMembersTestData();
		Mockito.doReturn(programMembers).when(this.workbenchDataManager).getAllActiveUsersSorted();
		for (final WorkbenchUser user : programMembers) {
			Mockito.doReturn(PersonTestDataInitializer.createPerson(user.getPersonid())).when(this.workbenchDataManager)
					.getPersonById(user.getPersonid());
		}
	}

	private void mockCurrentUser(final int userId) {
		Mockito.doReturn(userId).when(this.contextUtil).getCurrentWorkbenchUserId();
	}

	private List<WorkbenchUser> createProgramMembersTestData() {
		final List<WorkbenchUser> programMembers = new ArrayList<>();
		final WorkbenchUser user1 = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.OWNER_USER_ID,
				ProgramMembersPanelTest.OWNER_NAME, ProgramMembersPanelTest.OWNER_PERSON_ID,
				ProgramMembersPanelTest.OWNER_NAME, ProgramMembersPanelTest.OWNER_NAME);
		user1.setRoles(Collections.singletonList(new UserRole(user1, new Role(2, "Breeder"))));
		programMembers.add(user1);

		final WorkbenchUser user2 = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.MEMBER_USER_ID,
				ProgramMembersPanelTest.MEMBER_NAME, ProgramMembersPanelTest.MEMBER_PERSON_ID,
				ProgramMembersPanelTest.MEMBER_NAME, ProgramMembersPanelTest.MEMBER_NAME);
		user2.setRoles(Collections.singletonList(new UserRole(user2, new Role(3, "Technician"))));
		programMembers.add(user2);
		
		this.superAdminUser = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.ADMIN_USER_ID,
				ProgramMembersPanelTest.ADMIN_NAME, ProgramMembersPanelTest.ADMIN_PERSON_ID,
				ProgramMembersPanelTest.ADMIN_NAME, ProgramMembersPanelTest.ADMIN_NAME);
		this.superAdminUser.setRoles(Collections.singletonList(new UserRole(this.superAdminUser, new Role(5, "SuperAdmin"))));
		Mockito.when(this.workbenchDataManager.isSuperAdminUser(this.superAdminUser.getUserid())).thenReturn(true);
		programMembers.add(this.superAdminUser);
		
		return programMembers;
	}

}


package org.generationcp.ibpworkbench.ui.programmembers;

import com.vaadin.ui.Label;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.RoleType;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

	public static final int ROLE_ADMIN_ID = 1;
	public static final String ROLE_ADMIN_NAME = "Program Admin";
	public static final int ROLE_BREEDER_ID = 2;
	public static final String ROLE_BREEDER_NAME = "Breeder";

	@Mock
	private UserService userService;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private Project project;

	@InjectMocks
	private ProgramMembersPanel programMembersPanel;

	private WorkbenchUser superAdminUser;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.project = this.createProjectTestData(1, ProgramMembersPanelTest.MEMBER_PERSON_ID);
		this.programMembersPanel = new ProgramMembersPanel(this.project);
		this.programMembersPanel.setUserService(this.userService);
		this.programMembersPanel.setContextUtil(this.contextUtil);
		this.programMembersPanel.setMessageSource(messageSource);
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();
	}

	private Project createProjectTestData(final long projectId, final int owner) {
		final Project project = new Project();
		project.setProjectId(projectId);
		project.setProjectName("Project " + projectId);
		project.setUniqueID(UUID.randomUUID().toString());
		project.setUserId(owner);
		final CropType cropType = new CropType();
		cropType.setCropName("maize");
		project.setCropType(cropType);
		return project;
	}

	@Test
	public void testGenerateRoleCellForOwner() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final WorkbenchUser itemId = UserTestDataInitializer.createUserWithProjectRole(ProgramMembersPanelTest.OWNER_USER_ID, this.project);
		final Label roleLabel = this.programMembersPanel.generateRoleCell(itemId);
		Assert.assertEquals( itemId.getRoles().get(0).getCapitalizedRole(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertEquals("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testGenerateRoleCellForMember() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final WorkbenchUser itemId = UserTestDataInitializer.createUserWithProjectRole(ProgramMembersPanelTest.MEMBER_PERSON_ID, this.project);
		final Label roleLabel = this.programMembersPanel.generateRoleCell(itemId);
		Assert.assertEquals( itemId.getRoles().get(0).getCapitalizedRole(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertNotSame("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testGenerateRoleCellForMemberWithMultipleProjectRole() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final WorkbenchUser itemId = UserTestDataInitializer.createWorkbenchUser();
		UserTestDataInitializer.addUserRole(itemId, ProgramMembersPanelTest.ROLE_ADMIN_ID, ProgramMembersPanelTest.ROLE_ADMIN_NAME, new Project());
		UserTestDataInitializer.addUserRole(itemId, ProgramMembersPanelTest.ROLE_BREEDER_ID, ProgramMembersPanelTest.ROLE_BREEDER_NAME, this.project);

		final Label roleLabel = this.programMembersPanel.generateRoleCell(itemId);
		Assert.assertEquals("Only user's role for the program will be shown.", itemId.getRoles().get(1).getCapitalizedRole(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertNotSame("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testgenerateUserNameCellForOwner() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final WorkbenchUser itemId = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.OWNER_PERSON_ID,
				"UserName", 1, "Firstname", "Middlename");
		final Label roleLabel = this.programMembersPanel.generateUserNameCell(itemId);
		Assert.assertEquals( itemId.getPerson().getDisplayName(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertEquals("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testgenerateUserNameCellForMember() {
		this.mockCurrentUser(ProgramMembersPanelTest.OWNER_USER_ID);
		final WorkbenchUser itemId = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.MEMBER_PERSON_ID,
				"UserName", 1, "Firstname", "Middlename");
		final Label roleLabel = this.programMembersPanel.generateUserNameCell(itemId);
		Assert.assertEquals( itemId.getPerson().getDisplayName(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertNotSame("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testInitializeUsers() {
		// Setup test data and mocks
		Mockito.when(this.userService.getActiveUserIDsWithAccessToTheProgram(Mockito.anyLong()))
				.thenReturn(Arrays.asList(ProgramMembersPanelTest.OWNER_USER_ID, ProgramMembersPanelTest.ADMIN_USER_ID,
						ProgramMembersPanelTest.MEMBER_USER_ID));
		final List<WorkbenchUser> testProgramMembers = this.createProgramMembersTestData();

		Mockito.when(this.userService.getUsersByCrop(Mockito.anyString()))
			.thenReturn(testProgramMembers);


		this.mockCurrentUser(ProgramMembersPanelTest.MEMBER_USER_ID);
		for (final WorkbenchUser user : testProgramMembers) {
			Mockito.when(this.userService.getUserById(user.getUserid())).thenReturn(user);
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

		// Check that Instance user and Current User are disabled from selection
		for (final WorkbenchUser user : programMembers) {
			if (this.superAdminUser.equals(user) || user.getUserid().equals(MEMBER_USER_ID)) {
				Assert.assertFalse("SuperAdmin user should be disabled and cannot be removed as program member.",
						user.isEnabled());
			} else {
				Assert.assertTrue("Non-SuperAdmin user should be enabled and can be removed as program member.",
						user.isEnabled());
			}
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
		final Role breeder = new Role(2, "Breeder");
		final RoleType cropRoleType = new RoleType(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.name());
		cropRoleType.setId(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.getId());
		breeder.setRoleType(cropRoleType);

		final UserRole userRole1 = new UserRole(user1, breeder);
		userRole1.setCropType(project.getCropType());
		userRole1.setWorkbenchProject(project);
		user1.setRoles(Collections.singletonList(userRole1));
		programMembers.add(user1);

		final WorkbenchUser user2 = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.MEMBER_USER_ID,
				ProgramMembersPanelTest.MEMBER_NAME, ProgramMembersPanelTest.MEMBER_PERSON_ID,
				ProgramMembersPanelTest.MEMBER_NAME, ProgramMembersPanelTest.MEMBER_NAME);
		final Role technician = new Role(3, "Technician");
		final RoleType programRoleType = new RoleType(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.name());
		programRoleType.setId(org.generationcp.middleware.domain.workbench.RoleType.PROGRAM.getId());
		technician.setRoleType(programRoleType);
		final UserRole userRole2 = new UserRole(user2, technician);
		userRole2.setCropType(project.getCropType());
		userRole2.setWorkbenchProject(project);
		user2.setRoles(Collections.singletonList(userRole2));
		programMembers.add(user2);

		this.superAdminUser = UserTestDataInitializer.createUserWithPerson(ProgramMembersPanelTest.ADMIN_USER_ID,
				ProgramMembersPanelTest.ADMIN_NAME, ProgramMembersPanelTest.ADMIN_PERSON_ID,
				ProgramMembersPanelTest.ADMIN_NAME, ProgramMembersPanelTest.ADMIN_NAME);
		final Role superAdminRole = new Role(5, "SuperAdmin");
		final RoleType instanceRoleType = new RoleType(org.generationcp.middleware.domain.workbench.RoleType.INSTANCE.name());
		instanceRoleType.setId(org.generationcp.middleware.domain.workbench.RoleType.INSTANCE.getId());
		superAdminRole.setRoleType(instanceRoleType);

		this.superAdminUser.setRoles(Collections.singletonList(new UserRole(this.superAdminUser, superAdminRole)));
		programMembers.add(this.superAdminUser);

		return programMembers;
	}

}

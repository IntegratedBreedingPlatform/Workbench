
package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
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
	private ContextUtil contextUtil;
	
	private Project project;

	@InjectMocks
	private ProgramMembersPanel programMembersPanel;
	
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

		final Container usersContainer = this.programMembersPanel.createUsersContainer();

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
		Mockito.when(this.workbenchDataManager.getActiveUserIDsByProjectId(Matchers.anyLong())).thenReturn(Arrays.asList(ProgramMembersPanelTest.OWNER_USER_ID, ProgramMembersPanelTest.ADMIN_USER_ID, ProgramMembersPanelTest.MEMBER_USER_ID));
		this.mockCurrentUser(ProgramMembersPanelTest.MEMBER_USER_ID);
		List<User> testProgramMembers = this.createProgramMembersTestData();
		for(User user: testProgramMembers){
			Mockito.when(this.workbenchDataManager.getUserById(user.getUserid())).thenReturn(user);
		}
		
		// Initialization in controller
		this.programMembersPanel.initializeComponents();
		this.programMembersPanel.initializeValues();

		// Call method to test
		this.programMembersPanel.initializeUsers();

		// Check that members are selected in twin table
		final Set<User> programMembers = this.programMembersPanel.getProgramMembersDisplayed();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		// Check that ADMIN user is disabled from selection
		for (final User user : programMembers) {
			if (ProgramService.ADMIN_USERNAME.equalsIgnoreCase(user.getName())) {
				Assert.assertFalse("Default Admin should be disabled and cannot be removed as program member.", user.isEnabled());
			}
		}
	}

	private void mockProgramMembers() {
		final List<User> programMembers = this.createProgramMembersTestData();
		Mockito.doReturn(programMembers).when(this.workbenchDataManager).getAllActiveUsersSorted();
		for (final User user : programMembers) {
			Mockito.doReturn(this.createPersonTestData(user.getPersonid())).when(this.workbenchDataManager)
					.getPersonById(user.getPersonid());
		}
	}

	private void mockCurrentUser(final int userId) {
		Mockito.doReturn(userId).when(this.contextUtil).getCurrentWorkbenchUserId();
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
		final User user = UserTestDataInitializer.createUserWithPerson(userId, username, personId, "Mister", "User");
		
		final List<UserRole> userRoleList = new ArrayList<>();
		userRoleList.add(new UserRole(user, "ADMIN"));
		user.setRoles(userRoleList);
		
		return user;
	}

}

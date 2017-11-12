package org.generationcp.ibpworkbench.ui.project.create;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.data.initializer.PersonTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.data.Container;
import com.vaadin.ui.Label;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMembersComponentTest {

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

	@InjectMocks
	private ProjectMembersComponent projectMembersComponent;

	@Before
	public void setUp() {
		this.projectMembersComponent.setWorkbenchDataManager(this.workbenchDataManager);
		this.projectMembersComponent.setContextUtil(this.contextUtil);

		final List<User> programMembers = this.createProgramMembersTestData();
		Mockito.doReturn(programMembers).when(this.workbenchDataManager).getAllActiveUsersSorted();
		for (final User user : programMembers) {
			Mockito.doReturn(PersonTestDataInitializer.createPerson(user.getPersonid())).when(this.workbenchDataManager)
					.getPersonById(user.getPersonid());
		}
	}

	@Test
	public void testGenerateRoleCellForOwner() {
		this.mockCurrentUser(ProjectMembersComponentTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithRole(ProjectMembersComponentTest.OWNER_USER_ID);
		final Label roleLabel = this.projectMembersComponent.generateRoleCell(itemId);
		Assert.assertEquals(((User) itemId).getRoles().get(0).getCapitalizedRole(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertEquals("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testGenerateRoleCellForMember() {
		this.mockCurrentUser(ProjectMembersComponentTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithRole(ProjectMembersComponentTest.MEMBER_PERSON_ID);
		final Label roleLabel = this.projectMembersComponent.generateRoleCell(itemId);
		Assert.assertEquals(((User) itemId).getRoles().get(0).getCapitalizedRole(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertNotSame("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testGenerateUserNameCellForOwner() {
		this.mockCurrentUser(ProjectMembersComponentTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithPerson(ProjectMembersComponentTest.OWNER_PERSON_ID,
				"UserName", 1, "Firstname", "Middlename");
		final Label roleLabel = this.projectMembersComponent.generateUserNameCell(itemId);
		Assert.assertEquals(((User) itemId).getPerson().getDisplayName(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertEquals("label-bold", roleLabel.getStyleName());
	}

	@Test
	public void testGenerateUserNameCellForMember() {
		this.mockCurrentUser(ProjectMembersComponentTest.OWNER_USER_ID);
		final Object itemId = UserTestDataInitializer.createUserWithPerson(ProjectMembersComponentTest.MEMBER_PERSON_ID,
				"UserName", 1, "Firstname", "Middlename");
		final Label roleLabel = this.projectMembersComponent.generateUserNameCell(itemId);
		Assert.assertEquals(((User) itemId).getPerson().getDisplayName(), roleLabel.getValue());
		Assert.assertEquals("label", roleLabel.getDebugId());
		Assert.assertNotSame("label-bold", roleLabel.getStyleName());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateUsersContainer() {
		this.mockCurrentUser(ProjectMembersComponentTest.OWNER_USER_ID);

		final Container usersContainer = this.projectMembersComponent.createUsersContainer();

		final Collection<User> programMembers = (Collection<User>) usersContainer.getItemIds();
		Assert.assertNotNull(programMembers);
		Assert.assertEquals("There should be 3 program members.", 3, programMembers.size());

		for (final User user : programMembers) {
			if (user.getUserid().equals(ProjectMembersComponentTest.OWNER_PERSON_ID)) {
				Assert.assertFalse("Program Owner should be disabled so they cannot be removed as member.",
						user.isEnabled());
			} else {
				Assert.assertTrue("Other users should be enabled so they can be removed as members.", user.isEnabled());
			}
		}
	}

	private void mockCurrentUser(final int userId) {
		Mockito.doReturn(userId).when(this.contextUtil).getCurrentWorkbenchUserId();
	}

	private List<User> createProgramMembersTestData() {
		final List<User> programMembers = new ArrayList<>();
		programMembers.add(UserTestDataInitializer.createUserWithPerson(ProjectMembersComponentTest.OWNER_USER_ID,
				ProjectMembersComponentTest.OWNER_NAME, ProjectMembersComponentTest.OWNER_PERSON_ID,
				ProjectMembersComponentTest.OWNER_NAME, ProjectMembersComponentTest.OWNER_NAME));
		programMembers.add(UserTestDataInitializer.createUserWithPerson(ProjectMembersComponentTest.MEMBER_USER_ID,
				ProjectMembersComponentTest.MEMBER_NAME, ProjectMembersComponentTest.MEMBER_PERSON_ID,
				ProjectMembersComponentTest.MEMBER_NAME, ProjectMembersComponentTest.MEMBER_NAME));
		programMembers.add(UserTestDataInitializer.createUserWithPerson(ProjectMembersComponentTest.ADMIN_USER_ID,
				ProgramService.ADMIN_USERNAME, ProjectMembersComponentTest.ADMIN_PERSON_ID,
				ProjectMembersComponentTest.ADMIN_NAME, ProjectMembersComponentTest.ADMIN_NAME));
		return programMembers;
	}
}

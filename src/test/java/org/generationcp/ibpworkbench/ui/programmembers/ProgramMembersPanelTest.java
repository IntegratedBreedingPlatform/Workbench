
package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
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
		Assert.assertEquals("There should be 2 program members.", 2, programMembers.size());
		for (final User user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)) {
				Assert.assertFalse("Current user should be disabled so it cannot be removed as member.", user.isEnabled());
			} else if (user.getUserid().equals(ProgramMembersPanelTest.MEMBER_PERSON_ID)) {
				Assert.assertTrue("Other users should be enabled so they can be removed as a member.", user.isEnabled());
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
		Assert.assertEquals("There should be 2 program members.", 2, programMembers.size());
		for (final User user : programMembers) {
			if (user.getUserid().equals(ProgramMembersPanelTest.MEMBER_PERSON_ID)) {
				Assert.assertFalse("Current user should be disabled so it cannot be removed as member.", user.isEnabled());
			} else if (user.getUserid().equals(ProgramMembersPanelTest.OWNER_PERSON_ID)) {
				Assert.assertFalse("Program owner should be disabled so it cannot be removed as member.", user.isEnabled());
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
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.OWNER_USER_ID, ProgramMembersPanelTest.OWNER_PERSON_ID));
		programMembers.add(this.createUsersTestData(ProgramMembersPanelTest.MEMBER_USER_ID, ProgramMembersPanelTest.MEMBER_PERSON_ID));
		return programMembers;
	}

	private User createUsersTestData(final int userId, final int personId) {
		final User user = new User(userId);
		user.setPersonid(personId);
		return user;
	}
}

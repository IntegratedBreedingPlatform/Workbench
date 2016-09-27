
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(MockitoJUnitRunner.class)
public class ProgramServiceTest {

	@Mock
	private RequestAttributes attrs;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ToolUtil toolUtil;

	@Mock
	private UserDataManager userDataManager;

	@InjectMocks
	private final ProgramService programService = new ProgramService();

	private Person workbenchPerson;
	private Person cropDBPerson;
	private User workbenchUser;
	private User cropDBUser;

	@Before
	public void setup() throws Exception {

		final MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		this.initializeTestPersonsAndUsers();
	}

	private void initializeTestPersonsAndUsers() {
		this.workbenchPerson = this.createPerson(1, "John", "Doe");
		this.cropDBPerson = this.createPerson(2, "JOHN", "DOE");

		this.workbenchUser = new User();
		this.workbenchUser.setName("John");
		this.workbenchUser.setPersonid(1);

		this.cropDBUser = new User();
		this.cropDBUser.setName("John");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateNewProgram() throws Exception {

		final Project project = this.createProject();

		// Set up test users and persons data and mocks
		final User loggedInUser = this.createUser(1, "mrbreeder", 1);
		final User memberUser = this.createUser(2, "mrbreederfriend", 2);
		final User defaultAdminUser = this.createUser(3, ProgramService.ADMIN_USERNAME, 3);
		Mockito.when(this.workbenchDataManager.getUserByUsername(ProgramService.ADMIN_USERNAME)).thenReturn(defaultAdminUser);

		final Set<User> selectedUsers = new HashSet<User>();
		selectedUsers.add(loggedInUser);
		selectedUsers.add(memberUser);
		this.programService.setSelectedUsers(selectedUsers);
		this.programService.setCurrentUser(loggedInUser);

		final Person loggedInPerson = this.createPerson(1, "Jan", "Erik");
		final Person memberPerson = this.createPerson(2, "John", "Doe");
		final Person defaultAdminPerson = this.createPerson(3, "Default", "Admin");
		Mockito.when(this.workbenchDataManager.getPersonById(loggedInPerson.getId())).thenReturn(loggedInPerson);
		Mockito.when(this.workbenchDataManager.getPersonById(memberPerson.getId())).thenReturn(memberPerson);
		Mockito.when(this.workbenchDataManager.getPersonById(defaultAdminPerson.getId())).thenReturn(defaultAdminPerson);

		// Other WorkbenchDataManager mocks
		Mockito.when(this.workbenchDataManager.getCropTypeByName(Matchers.anyString())).thenReturn(project.getCropType());
		final ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(this.workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);

		final List<Role> allRolesList = new ArrayList<Role>();
		allRolesList.add(new Role(1, "CB Breeder", null));
		allRolesList.add(new Role(2, "MAS Breeder", null));
		Mockito.when(this.workbenchDataManager.getAllRoles()).thenReturn(allRolesList);

		Mockito.when(this.userDataManager.addUser(Matchers.any(User.class))).thenReturn(2);

		// Call the method to test
		this.programService.createNewProgram(project);

		// Verify that the key database operations for program creation are invoked.
		Mockito.verify(this.workbenchDataManager).addProject(project);

		// Add Person and Add User called three times - 1)for current person and user and
		// 2) for member person and user selected and 3) for default ADMIN user
		Mockito.verify(this.userDataManager, Mockito.times(3)).addPerson(Matchers.any(Person.class));
		Mockito.verify(this.userDataManager, Mockito.times(3)).addUser(Matchers.any(User.class));

		// Ibdb_user_map is added for both current, member and default ADMIN users
		Mockito.verify(this.workbenchDataManager, Mockito.times(3)).addIbdbUserMap(Matchers.any(IbdbUserMap.class));

		Mockito.verify(this.workbenchDataManager).addProjectUserRole(Matchers.anyList());

		// Verify that utility to create workspace directory was called
		Mockito.verify(this.toolUtil).createWorkspaceDirectoriesForProject(project);

	}

	@Test
	public void testCreateCropPersonIfNecessaryWhenPersonIsExisting() {

		Mockito.when(this.userDataManager.getPersonByEmail(this.workbenchPerson.getEmail())).thenReturn(this.cropDBPerson);

		final Person result = this.programService.createCropPersonIfNecessary(this.workbenchPerson);

		Mockito.verify(this.userDataManager, Mockito.times(0)).addPerson(Matchers.any(Person.class));
		Assert.assertSame(result, this.cropDBPerson);

	}

	@Test
	public void testCreateCropPersonIfNecessaryWhenPersonIsNotExisting() {

		Mockito.when(this.userDataManager.getPersonByEmail(this.workbenchPerson.getEmail())).thenReturn(null);

		final Person result = this.programService.createCropPersonIfNecessary(this.workbenchPerson);

		Mockito.verify(this.userDataManager, Mockito.times(1)).addPerson(Matchers.any(Person.class));
		Assert.assertNotSame(result, this.workbenchPerson);
		Assert.assertEquals(result.getFirstName(), this.workbenchPerson.getFirstName());
		Assert.assertEquals(result.getLastName(), this.workbenchPerson.getLastName());

	}

	@Test
	public void testCreateCropUserIfNecessaryWhenUserIsExisting() {

		Mockito.when(this.userDataManager.getUserByUserName(this.workbenchUser.getName())).thenReturn(this.cropDBUser);

		final User result = this.programService.createCropUserIfNecessary(this.workbenchUser, this.cropDBPerson);

		Mockito.verify(this.userDataManager, Mockito.times(0)).addUser(Matchers.any(User.class));

		Assert.assertSame(result, this.cropDBUser);

	}

	@Test
	public void testCreateCropUserIfNecessaryWhenUserIsNotExisting() {

		Mockito.when(this.userDataManager.getUserByUserName(this.workbenchUser.getName())).thenReturn(null);

		final User result = this.programService.createCropUserIfNecessary(this.workbenchUser, this.cropDBPerson);

		Mockito.verify(this.userDataManager, Mockito.times(1)).addUser(Matchers.any(User.class));

		Assert.assertEquals(this.cropDBPerson.getId(), result.getPersonid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_ACCESS_NUMBER), result.getAccess());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_TYPE), result.getType());
		Assert.assertEquals(Integer.valueOf(0), result.getInstalid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_STATUS), result.getStatus());
		Assert.assertNotNull(result.getAssignDate());

	}

	@Test
	public void testSaveWorkbenchUserToCropUserMapping() {

		final Project project = this.createProject();

		final Set<User> users = new HashSet<>();
		users.add(this.workbenchUser);

		Mockito.when(this.workbenchDataManager.getPersonById(this.workbenchUser.getPersonid())).thenReturn(this.workbenchPerson);
		Mockito.when(this.userDataManager.getPersonByEmail(this.workbenchPerson.getEmail())).thenReturn(this.cropDBPerson);
		Mockito.when(this.userDataManager.getUserByUserName(this.workbenchUser.getName())).thenReturn(this.cropDBUser);

		this.programService.saveWorkbenchUserToCropUserMapping(project, users);

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).addIbdbUserMap(Matchers.any(IbdbUserMap.class));

	}

	private Project createProject() {
		final Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName("TestRiceProject");
		final CropType cropType = new CropType(CropType.CropEnum.RICE.toString());
		cropType.setDbName("ibdbv2_rice_merged");
		project.setCropType(cropType);

		return project;
	}

	private Person createPerson(final Integer personId, final String firstName, final String lastName) {
		final Person person = new Person();
		person.setId(personId);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		return person;
	}

	private User createUser(final Integer userId, final String username, final Integer personId) {
		final User loggedInUser = new User();
		loggedInUser.setUserid(userId);
		loggedInUser.setName(username);
		loggedInUser.setPersonid(personId);
		return loggedInUser;
	}

}

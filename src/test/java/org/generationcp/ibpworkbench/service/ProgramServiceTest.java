
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.ibpworkbench.data.initializer.ProjectTestDataInitializer;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

	@Before
	public void setup() throws Exception {

		final MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

	}


	Person workbenchPerson;
	Person cropDBPerson;
	User workbenchUser;
	User cropDBUser;

	@Before
	public void init() {

		initializeTestPersonsAndUsers();

	}

	private void initializeTestPersonsAndUsers() {

		workbenchPerson = createPerson(1, "John", "Doe");
		cropDBPerson = createPerson(2, "JOHN", "DOE");

		workbenchUser = new User();
		workbenchUser.setName("John");
		workbenchUser.setPersonid(1);

		cropDBUser = new User();
		cropDBUser.setName("John");

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddNewProgram() throws Exception {

		final Project project = this.createProject();

		final User loggedInUser = new User();
		loggedInUser.setUserid(1);
		loggedInUser.setName("mrbreeder");
		loggedInUser.setPersonid(1);

		final List<UserRole> roles = new ArrayList<>();
		roles.add(new UserRole(loggedInUser, ProgramService.ADMIN_ROLE));
		loggedInUser.setRoles(roles);

		final Person loggedInPerson = new Person();
		loggedInPerson.setId(1);
		loggedInPerson.setFirstName("Jan");
		loggedInPerson.setLastName("Erik");

		final User memberUser = new User();
		memberUser.setUserid(2);
		memberUser.setName("mrbreederfriend");
		memberUser.setPersonid(2);

		final Person memberPerson = new Person();
		memberPerson.setId(2);
		memberPerson.setFirstName("John");
		memberPerson.setLastName("Doe");

		final Set<User> programMembers = new HashSet<User>();
		programMembers.add(memberUser);

		// WorkbenchDataManager mocks
		Mockito.when(this.workbenchDataManager.getCropTypeByName(Matchers.anyString())).thenReturn(project.getCropType());
		final ArrayList<WorkflowTemplate> workflowTemplates = new ArrayList<WorkflowTemplate>();
		workflowTemplates.add(new WorkflowTemplate());
		Mockito.when(this.workbenchDataManager.getWorkflowTemplates()).thenReturn(workflowTemplates);

		Mockito.when(this.workbenchDataManager.getUserById(loggedInUser.getUserid())).thenReturn(loggedInUser);
		Mockito.when(this.workbenchDataManager.getPersonById(loggedInPerson.getId())).thenReturn(loggedInPerson);

		Mockito.when(this.workbenchDataManager.getUserById(memberUser.getUserid())).thenReturn(memberUser);
		Mockito.when(this.workbenchDataManager.getPersonById(memberPerson.getId())).thenReturn(memberPerson);

		final ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(this.workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);

		final ArrayList<Role> allRolesList = new ArrayList<Role>();
		allRolesList.add(new Role(1, "CB Breeder", null));
		allRolesList.add(new Role(2, "MAS Breeder", null));
		allRolesList.add(new Role(3, "MABC Breeder", null));
		allRolesList.add(new Role(4, "MARS Breeder", null));
		allRolesList.add(new Role(5, "Manager", null));
		Mockito.when(this.workbenchDataManager.getAllRoles()).thenReturn(allRolesList);

		Mockito.when(this.userDataManager.addUser(Matchers.any(User.class))).thenReturn(2);
		Mockito.when(this.userDataManager.getUserById(Matchers.anyInt())).thenReturn(memberUser);

		final List<Project> projects = new ArrayList<>();
		final Project program = new Project();
		program.setProjectId(new Long(1));
		projects.add(program);
		Mockito.when(this.workbenchDataManager.getProjectsByCropType(project.getCropType())).thenReturn(projects);

		Mockito.when(puiDao.getByProjectIdAndUserId(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);

		this.programService.setCurrentUser(loggedInUser);

		final Set<User> selectedUsers = new HashSet<User>();
		selectedUsers.add(loggedInUser);
		selectedUsers.add(memberUser);
		this.programService.setSelectedUsers(selectedUsers);

		this.programService.createNewProgram(project);

		// Verify that the key database operations for program creation are invoked.
		Mockito.verify(this.workbenchDataManager).addProject(project);

		Mockito.verify(this.toolUtil).createWorkspaceDirectoriesForProject(project);

		// Once to add current person and user and once for member person and user.
		Mockito.verify(this.userDataManager, Mockito.times(2)).addPerson(Matchers.any(Person.class));
		Mockito.verify(this.userDataManager, Mockito.times(2)).addUser(Matchers.any(User.class));

		// Map is added for both current and membeer user.
		Mockito.verify(this.workbenchDataManager, Mockito.times(2)).addIbdbUserMap(Matchers.any(IbdbUserMap.class));

		Mockito.verify(this.workbenchDataManager).addProjectUserRole(Matchers.anyList());

		Mockito.verify(this.workbenchDataManager, Mockito.atLeast(1 + projects.size())).saveOrUpdateProjectUserInfo(
				Matchers.any(ProjectUserInfo.class));

	}

	@Test
	public void testAddProjectUserToAllPrograms() {
		// set data
		final User user = new User();
		user.setUserid(1);
		final CropType cropType = new CropType(CropType.CropEnum.MAIZE.toString());
		final List<Project> projects = ProjectTestDataInitializer.createProjectsWithCropType(cropType);
		Mockito.doReturn(projects).when(this.workbenchDataManager).getProjectsByCropType(cropType);

		// mock methods - set first project as an existing program of the user while the second as not
		final Project firstProject = projects.get(0);
		final Project secondProject = projects.get(1);
		final ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(this.workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);
		Mockito.doReturn(new ProjectUserInfo()).when(puiDao)
				.getByProjectIdAndUserId(firstProject.getProjectId().intValue(), user.getUserid());
		Mockito.doReturn(null).when(puiDao).getByProjectIdAndUserId(secondProject.getProjectId().intValue(), user.getUserid());
		final List<Role> roles = this.getAllRolesTestData();
		Mockito.doReturn(roles).when(this.workbenchDataManager).getAllRoles();
		Mockito.doReturn(new ArrayList<Role>()).when(this.workbenchDataManager).getRolesByProjectAndUser(firstProject, user);
		Mockito.doReturn(new ArrayList<Role>()).when(this.workbenchDataManager).getRolesByProjectAndUser(secondProject, user);

		// test
		this.programService.addProjectUserToAllPrograms(workbenchUser);

		final ArgumentCaptor<ProjectUserInfo> captor = ArgumentCaptor.forClass(ProjectUserInfo.class);

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).saveOrUpdateProjectUserInfo(captor.capture());

		final ProjectUserInfo captured = captor.getValue();
		Assert.assertEquals("The user id of the newly-added program member must be " + user.getUserid(), user.getUserid().intValue(),
				captured.getUserId().intValue());
		Assert.assertEquals("The project id of the newly-added program member must be " + secondProject.getProjectId().intValue(),
				secondProject.getProjectId().intValue(), captured.getProjectId().intValue());

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).getAllRoles();
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).getRolesByProjectAndUser(firstProject, user);
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).getRolesByProjectAndUser(secondProject, user);
		Mockito.verify(this.workbenchDataManager, Mockito.times(roles.size() * projects.size())).addProjectUserRole(
				Matchers.any(Project.class), Matchers.any(User.class), Matchers.any(Role.class));

	}

	@Test
	public void testAddUsersToProgram() {
		final String crop = CropType.CropEnum.MAIZE.toString();
		final int projectId = 1;
		final Project program = new Project();
		program.setProjectId(new Long(projectId));
		final int memberAdminUserId = 1;
		final int nonMemberAdminUserId = 2;
		final List<User> adminUsers = this.createAdminUsersTestData(memberAdminUserId, nonMemberAdminUserId);

		// mock data
		Mockito.doReturn(adminUsers).when(this.workbenchDataManager).getAllUsersByRole(org.generationcp.commons.security.Role.ADMIN.toString());
		final ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(this.workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);
		Mockito.doReturn(new ProjectUserInfo()).when(puiDao).getByProjectIdAndUserId(projectId, memberAdminUserId);
		Mockito.doReturn(null).when(puiDao).getByProjectIdAndUserId(projectId, nonMemberAdminUserId);
		final List<Role> roles = this.getAllRolesTestData();
		Mockito.doReturn(roles).when(this.workbenchDataManager).getAllRoles();

		final User memberAdminUser = adminUsers.get(0);
		final User nonMemberAdminUser = adminUsers.get(1);
		Mockito.doReturn(new ArrayList<Role>()).when(this.workbenchDataManager).getRolesByProjectAndUser(program, memberAdminUser);
		Mockito.doReturn(new ArrayList<Role>()).when(this.workbenchDataManager).getRolesByProjectAndUser(program, nonMemberAdminUser);

		Mockito.when(this.workbenchDataManager.getPersonById(memberAdminUser.getPersonid())).thenReturn(this.createPerson(memberAdminUser.getPersonid(),
				"John", "Doe"));
		Mockito.when(this.workbenchDataManager.getPersonById(nonMemberAdminUser.getPersonid())).thenReturn(this.createPerson(nonMemberAdminUser.getPersonid(),
				"Juan", "Dela Cruz"));

		// test
		this.programService.addUsersToProgram(adminUsers, program);

		final ArgumentCaptor<ProjectUserInfo> captor = ArgumentCaptor.forClass(ProjectUserInfo.class);
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).saveOrUpdateProjectUserInfo(captor.capture());

		final ProjectUserInfo captured = captor.getValue();
		Assert.assertEquals("The user id of the newly-added program member must be " + nonMemberAdminUserId, nonMemberAdminUserId, captured
				.getUserId().intValue());
		Assert.assertEquals("The project id of the newly-added program member must be " + projectId, projectId, captured.getProjectId()
				.intValue());

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).getAllRoles();
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).getRolesByProjectAndUser(program, memberAdminUser);
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).getRolesByProjectAndUser(program, nonMemberAdminUser);
		Mockito.verify(this.workbenchDataManager, Mockito.times(roles.size() * adminUsers.size())).addProjectUserRole(
				Matchers.any(Project.class), Matchers.any(User.class), Matchers.any(Role.class));
		Mockito.verify(this.workbenchDataManager, Mockito.times(adminUsers.size())).addIbdbUserMap(Matchers.any(IbdbUserMap.class));
	}

	@Test
	public void testCreateCropDBPersonIfNecessaryExistingCropDBPerson() {


		Mockito.when(userDataManager.getPersonByFirstAndLastName(workbenchPerson.getFirstName(), workbenchPerson.getLastName())).thenReturn(cropDBPerson);

		Person result = this.programService.createCropDBPersonIfNecessary(workbenchPerson);

		Mockito.verify(userDataManager, Mockito.times(0)).addPerson(Mockito.any(Person.class));
		Assert.assertSame(result, cropDBPerson);


	}

	@Test
	public void testCreateCropDBPersonIfNecessaryCropDBPersonDoesNotExist() {


		Mockito.when(userDataManager.getPersonByFirstAndLastName(workbenchPerson.getFirstName(), workbenchPerson.getLastName())).thenReturn(null);

		Person result = this.programService.createCropDBPersonIfNecessary(workbenchPerson);

		Mockito.verify(userDataManager, Mockito.times(1)).addPerson(Mockito.any(Person.class));
		Assert.assertNotSame(result, workbenchPerson);
		Assert.assertEquals(result.getFirstName(), workbenchPerson.getFirstName());
		Assert.assertEquals(result.getLastName(), workbenchPerson.getLastName());

	}

	@Test
	public void testCreateIBDBUserIfNecessaryCropDBUSerIsExisting() {


		Mockito.when(userDataManager.getUserByUserName(workbenchUser.getName())).thenReturn(cropDBUser);

		User result = this.programService.createIBDBUserIfNecessary(workbenchUser, cropDBPerson);

		Mockito.verify(userDataManager, Mockito.times(0)).addUser(Mockito.any(User.class));

		Assert.assertSame(result, cropDBUser);

	}

	@Test
	public void testCreateIBDBUserIfNecessaryCropDBUSerIsNotExisting() {

		Mockito.when(userDataManager.getUserByUserName(workbenchUser.getName())).thenReturn(null);

		User result = this.programService.createIBDBUserIfNecessary(workbenchUser, cropDBPerson);

		Mockito.verify(userDataManager, Mockito.times(1)).addUser(Mockito.any(User.class));

		Assert.assertEquals(cropDBPerson.getId(), result.getPersonid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_ACCESS_NUMBER), result.getAccess());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_TYPE), result.getType());
		Assert.assertEquals(Integer.valueOf(0), result.getInstalid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_STATUS), result.getStatus());
		Assert.assertNotNull(result.getAssignDate());

	}

	@Test
	public void testCreateIBDBUserMapping() {

		Project project = this.createProject();

		final Set<User> users = new HashSet<>();
		users.add(workbenchUser);

		Mockito.when(this.workbenchDataManager.getPersonById(workbenchUser.getPersonid())).thenReturn(workbenchPerson);
		Mockito.when(this.userDataManager.getPersonByFirstAndLastName(workbenchPerson.getFirstName(), workbenchPerson.getLastName())).thenReturn(cropDBPerson);
		Mockito.when(this.userDataManager.getUserByUserName(workbenchUser.getName())).thenReturn(cropDBUser);

		this.programService.createIBDBUserMapping(project, users);

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).addIbdbUserMap(Mockito.any(IbdbUserMap.class));

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

	private Person createPerson(final Integer personid, final String firstName, final String lastName) {
		Person person = new Person();
		person.setId(personid);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		return person;
	}

	private List<Role> getAllRolesTestData() {
		final List<Role> roles = new ArrayList<>();
		roles.add(this.createRoleTestData(1));
		roles.add(this.createRoleTestData(2));
		roles.add(this.createRoleTestData(3));
		roles.add(this.createRoleTestData(4));
		roles.add(this.createRoleTestData(5));
		return roles;
	}

	private Role createRoleTestData(final Integer roleId) {
		final Role role = new Role();
		role.setRoleId(roleId);
		return role;
	}

	private List<User> createAdminUsersTestData(final int memberAdminUserId, final int nonMemberAdminUserId) {
		final List<User> adminUserIds = new ArrayList<>();

		User userMemberAdmin = new User(memberAdminUserId);
		userMemberAdmin.setPersonid(1);
		adminUserIds.add(userMemberAdmin);

		User userNonMemberAdmin = new User(nonMemberAdminUserId);
		userNonMemberAdmin.setPersonid(2);
		adminUserIds.add(userNonMemberAdmin);
		return adminUserIds;
	}

}

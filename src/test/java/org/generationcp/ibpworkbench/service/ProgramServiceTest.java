
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
	private ProgramService programService = new ProgramService();
	
	private Person workbenchPerson;
	private Person cropDBPerson;
	private User workbenchUser;
	private User cropDBUser;

	@Before
	public void setup() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		initializeTestPersonsAndUsers();
	}
	
	
	private void initializeTestPersonsAndUsers() {
		this.workbenchPerson = createPerson(1, "John", "Doe");
		this.cropDBPerson = createPerson(2, "JOHN", "DOE");

		this.workbenchUser = new User();
		this.workbenchUser.setName("John");
		this.workbenchUser.setPersonid(1);

		this.cropDBUser = new User();
		this.cropDBUser.setName("John");
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateNewProgram() throws Exception {

		Project project = this.createProject();
		
		// Set up test users and persons data and mocks
		User loggedInUser = this.createUser(1, "mrbreeder", 1);
		User memberUser = this.createUser(2, "mrbreederfriend", 2);
		User defaultAdminUser = this.createUser(3, ProgramService.ADMIN_USERNAME, 3);
		Mockito.when(workbenchDataManager.getUserByUsername(ProgramService.ADMIN_USERNAME)).thenReturn(defaultAdminUser);

		Set<User> selectedUsers = new HashSet<User>();
		selectedUsers.add(loggedInUser);
		selectedUsers.add(memberUser);
		programService.setSelectedUsers(selectedUsers);
		programService.setCurrentUser(loggedInUser);

		Person loggedInPerson = this.createPerson(1, "Jan", "Erik");
		Person memberPerson =this.createPerson(2, "John", "Doe");
		Person defaultAdminPerson =this.createPerson(3, "Default", "Admin");
		Mockito.when(workbenchDataManager.getPersonById(loggedInPerson.getId())).thenReturn(loggedInPerson);
		Mockito.when(workbenchDataManager.getPersonById(memberPerson.getId())).thenReturn(memberPerson);
		Mockito.when(workbenchDataManager.getPersonById(defaultAdminPerson.getId())).thenReturn(defaultAdminPerson);

		
		// Other WorkbenchDataManager mocks
		Mockito.when(workbenchDataManager.getCropTypeByName(Matchers.anyString())).thenReturn(project.getCropType());
		ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);

		List<Role> allRolesList = new ArrayList<Role>();
		allRolesList.add(new Role(1, "CB Breeder", null));
		allRolesList.add(new Role(2, "MAS Breeder", null));
		Mockito.when(workbenchDataManager.getAllRoles()).thenReturn(allRolesList);

		Mockito.when(userDataManager.addUser(Matchers.any(User.class))).thenReturn(2);
		
		
		// Call the method to test
		programService.createNewProgram(project);

		
		// Verify that the key database operations for program creation are invoked.
		Mockito.verify(workbenchDataManager).addProject(project);

		// Add Person and Add User called three times - 1)for current person and user and 
		// 2) for member person and user selected and 3) for default ADMIN user
		Mockito.verify(userDataManager, Mockito.times(3)).addPerson(Matchers.any(Person.class));
		Mockito.verify(userDataManager, Mockito.times(3)).addUser(Matchers.any(User.class));

		// Ibdb_user_map is added for both current, member and default ADMIN users
		Mockito.verify(workbenchDataManager, Mockito.times(3)).addIbdbUserMap(Matchers.any(IbdbUserMap.class));
		
		Mockito.verify(workbenchDataManager).addProjectUserRole(Matchers.anyList());

		
		// Verify that utility to create workspace directory was called
		Mockito.verify(toolUtil).createWorkspaceDirectoriesForProject(project);
		
	}


	@Test
	public void testCreateCropPersonIfNecessaryWhenPersonIsExisting() {

		Mockito.when(userDataManager.getPersonByFirstAndLastName(workbenchPerson.getFirstName(), workbenchPerson.getLastName())).thenReturn(cropDBPerson);

		Person result = this.programService.createCropPersonIfNecessary(workbenchPerson);

		Mockito.verify(userDataManager, Mockito.times(0)).addPerson(Mockito.any(Person.class));
		Assert.assertSame(result, cropDBPerson);

	}

	
	@Test
	public void testCreateCropPersonIfNecessaryWhenPersonIsNotExisting() {

		Mockito.when(userDataManager.getPersonByFirstAndLastName(workbenchPerson.getFirstName(), workbenchPerson.getLastName())).thenReturn(null);

		Person result = this.programService.createCropPersonIfNecessary(workbenchPerson);

		Mockito.verify(userDataManager, Mockito.times(1)).addPerson(Mockito.any(Person.class));
		Assert.assertNotSame(result, workbenchPerson);
		Assert.assertEquals(result.getFirstName(), workbenchPerson.getFirstName());
		Assert.assertEquals(result.getLastName(), workbenchPerson.getLastName());

	}

	@Test
	public void testCreateCropUserIfNecessaryWhenUserIsExisting() {


		Mockito.when(userDataManager.getUserByUserName(workbenchUser.getName())).thenReturn(cropDBUser);

		User result = this.programService.createCropUserIfNecessary(workbenchUser, cropDBPerson);

		Mockito.verify(userDataManager, Mockito.times(0)).addUser(Mockito.any(User.class));

		Assert.assertSame(result, cropDBUser);

	}

	@Test
	public void testCreateCropUserIfNecessaryWhenUserIsNotExisting() {

		Mockito.when(userDataManager.getUserByUserName(workbenchUser.getName())).thenReturn(null);

		User result = this.programService.createCropUserIfNecessary(workbenchUser, cropDBPerson);

		Mockito.verify(userDataManager, Mockito.times(1)).addUser(Mockito.any(User.class));

		Assert.assertEquals(cropDBPerson.getId(), result.getPersonid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_ACCESS_NUMBER), result.getAccess());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_TYPE), result.getType());
		Assert.assertEquals(Integer.valueOf(0), result.getInstalid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_STATUS), result.getStatus());
		Assert.assertNotNull(result.getAssignDate());

	}

	@Test
	public void testSaveWorkbenchUserToCropUserMapping() {

		Project project = this.createProject();

		final Set<User> users = new HashSet<>();
		users.add(workbenchUser);

		Mockito.when(this.workbenchDataManager.getPersonById(workbenchUser.getPersonid())).thenReturn(workbenchPerson);
		Mockito.when(this.userDataManager.getPersonByFirstAndLastName(workbenchPerson.getFirstName(), workbenchPerson.getLastName())).thenReturn(cropDBPerson);
		Mockito.when(this.userDataManager.getUserByUserName(workbenchUser.getName())).thenReturn(cropDBUser);

		this.programService.saveWorkbenchUserToCropUserMapping(project, users);

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
	
	private Person createPerson(final Integer personId, final String firstName, final String lastName) {
		Person person = new Person();
		person.setId(personId);
		person.setFirstName(firstName);
		person.setLastName(lastName);
		return person;
	}
	
	private User createUser(final Integer userId, final String username, final Integer personId) {
		User loggedInUser = new User();
		loggedInUser.setUserid(userId);
		loggedInUser.setName(username);
		loggedInUser.setPersonid(personId);
		return loggedInUser;
	}

}

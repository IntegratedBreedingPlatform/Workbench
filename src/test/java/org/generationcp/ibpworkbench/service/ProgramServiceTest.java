
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
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

	@Before
	public void setup() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddNewProgram() throws Exception {

		Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName("TestRiceProject");
		CropType cropType = new CropType(CropType.CropEnum.RICE.toString());
		cropType.setDbName("ibdbv2_rice_merged");
		project.setCropType(cropType);

		User loggedInUser = new User();
		loggedInUser.setUserid(1);
		loggedInUser.setName("mrbreeder");
		loggedInUser.setPersonid(1);

		Person loggedInPerson = new Person();
		loggedInPerson.setId(1);
		loggedInPerson.setFirstName("Jan");
		loggedInPerson.setLastName("Erik");

		User memberUser = new User();
		memberUser.setUserid(2);
		memberUser.setName("mrbreederfriend");
		memberUser.setPersonid(2);

		Person memberPerson = new Person();
		memberPerson.setId(2);
		memberPerson.setFirstName("John");
		memberPerson.setLastName("Doe");

		Set<User> programMembers = new HashSet<User>();
		programMembers.add(memberUser);

		// WorkbenchDataManager mocks
		Mockito.when(workbenchDataManager.getCropTypeByName(Matchers.anyString())).thenReturn(cropType);
		ArrayList<WorkflowTemplate> workflowTemplates = new ArrayList<WorkflowTemplate>();
		workflowTemplates.add(new WorkflowTemplate());
		Mockito.when(workbenchDataManager.getWorkflowTemplates()).thenReturn(workflowTemplates);

		Mockito.when(workbenchDataManager.getUserById(loggedInUser.getUserid())).thenReturn(loggedInUser);
		Mockito.when(workbenchDataManager.getPersonById(loggedInPerson.getId())).thenReturn(loggedInPerson);

		Mockito.when(workbenchDataManager.getUserById(memberUser.getUserid())).thenReturn(memberUser);
		Mockito.when(workbenchDataManager.getPersonById(memberPerson.getId())).thenReturn(memberPerson);

		ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);

		ArrayList<Role> allRolesList = new ArrayList<Role>();
		allRolesList.add(new Role(1, "CB Breeder", null));
		allRolesList.add(new Role(2, "MAS Breeder", null));
		allRolesList.add(new Role(3, "MABC Breeder", null));
		allRolesList.add(new Role(4, "MARS Breeder", null));
		allRolesList.add(new Role(5, "Manager", null));
		Mockito.when(workbenchDataManager.getAllRoles()).thenReturn(allRolesList);

		Mockito.when(userDataManager.addUser(Matchers.any(User.class))).thenReturn(2);
		Mockito.when(userDataManager.getUserById(Matchers.anyInt())).thenReturn(memberUser);

		programService.setCurrentUser(loggedInUser);

		Set<User> selectedUsers = new HashSet<User>();
		selectedUsers.add(loggedInUser);
		selectedUsers.add(memberUser);
		programService.setSelectedUsers(selectedUsers);

		programService.createNewProgram(project);

		// Verify that the key database operations for program creation are invoked.
		Mockito.verify(workbenchDataManager).addProject(project);

		Mockito.verify(toolUtil).createWorkspaceDirectoriesForProject(project);

		// Once to add current person and user and once for member person and user.
		Mockito.verify(userDataManager, Mockito.times(2)).addPerson(Matchers.any(Person.class));
		Mockito.verify(userDataManager, Mockito.times(2)).addUser(Matchers.any(User.class));

		// Map is added for both current and membeer user.
		Mockito.verify(workbenchDataManager, Mockito.times(2)).addIbdbUserMap(Matchers.any(IbdbUserMap.class));

		Mockito.verify(workbenchDataManager).addProjectUserRole(Matchers.anyList());
	}

}


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

	@SuppressWarnings("unchecked")
	@Test
	public void testAddNewProgram() throws Exception {

		final Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName("TestRiceProject");
		final CropType cropType = new CropType(CropType.CropEnum.RICE.toString());
		cropType.setDbName("ibdbv2_rice_merged");
		project.setCropType(cropType);

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
		Mockito.when(this.workbenchDataManager.getCropTypeByName(Matchers.anyString())).thenReturn(cropType);
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
	public void testAddProjectUserToAllProgramsOfCropType() {
		// set data
		final int userId = 1;
		final CropType cropType = new CropType(CropType.CropEnum.MAIZE.toString());
		final List<Project> projects = ProjectTestDataInitializer.createProjectsWithCropType(cropType);
		Mockito.doReturn(projects).when(this.workbenchDataManager).getProjectsByCropType(cropType);

		// mock methods - set first project as an existing program of the user while the second as not
		final Project firstProject = projects.get(0);
		final Project secondProject = projects.get(1);
		final ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(this.workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);
		Mockito.doReturn(new ProjectUserInfo()).when(puiDao).getByProjectIdAndUserId(firstProject.getProjectId().intValue(), userId);
		Mockito.doReturn(null).when(puiDao).getByProjectIdAndUserId(secondProject.getProjectId().intValue(), userId);

		// test
		this.programService.addProjectUserToAllProgramsOfCropType(userId, cropType);

		final ArgumentCaptor<ProjectUserInfo> captor = ArgumentCaptor.forClass(ProjectUserInfo.class);

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).saveOrUpdateProjectUserInfo(captor.capture());

		final ProjectUserInfo captured = captor.getValue();
		Assert.assertEquals("The user id of the newly-added program member must be " + userId, userId, captured.getUserId().intValue());
		Assert.assertEquals("The project id of the newly-added program member must be " + userId, secondProject.getProjectId().intValue(),
				captured.getProjectId().intValue());

	}

	@Test
	public void testAddAllAdminUsersOfCropToProgram() {
		final String crop = CropType.CropEnum.MAIZE.toString();
		final int projectId = 1;
		final int memberAdminUserId = 1;
		final int nonMemberAdminUserId = 2;
		final List<Integer> adminUserIds = this.createAdminUserIdsTestData(memberAdminUserId, nonMemberAdminUserId);

		// mock data
		Mockito.doReturn(adminUserIds).when(this.workbenchDataManager).getAdminUserIdsOfCrop(crop);
		final ProjectUserInfoDAO puiDao = Mockito.mock(ProjectUserInfoDAO.class);
		Mockito.when(this.workbenchDataManager.getProjectUserInfoDao()).thenReturn(puiDao);
		Mockito.doReturn(new ProjectUserInfo()).when(puiDao).getByProjectIdAndUserId(projectId, memberAdminUserId);
		Mockito.doReturn(null).when(puiDao).getByProjectIdAndUserId(projectId, nonMemberAdminUserId);

		// test
		this.programService.addAllAdminUsersOfCropToProgram(crop, projectId);

		final ArgumentCaptor<ProjectUserInfo> captor = ArgumentCaptor.forClass(ProjectUserInfo.class);
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).saveOrUpdateProjectUserInfo(captor.capture());

		final ProjectUserInfo captured = captor.getValue();
		Assert.assertEquals("The user id of the newly-added program member must be " + nonMemberAdminUserId, nonMemberAdminUserId, captured
				.getUserId().intValue());
		Assert.assertEquals("The project id of the newly-added program member must be " + projectId, projectId, captured.getProjectId()
				.intValue());
	}

	private List<Integer> createAdminUserIdsTestData(final int memberAdminUserId, final int nonMemberAdminUserId) {
		final List<Integer> adminUserIds = new ArrayList<>();
		adminUserIds.add(memberAdminUserId);
		adminUserIds.add(nonMemberAdminUserId);
		return adminUserIds;
	}

}

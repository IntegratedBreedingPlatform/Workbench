package org.generationcp.ibpworkbench.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.data.initializer.WorkbenchUserTestDataInitializer;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
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
import org.springframework.web.context.request.RequestAttributes;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.manager.api.LocationDataManager;

@RunWith(MockitoJUnitRunner.class)
public class ProgramServiceTest {

	private static final String SUPERADMIN_USERNAME = "superadmin";
	private static final int USER_ID = 123;
	private static final String SAMPLE_AUTH_TOKEN_VALUE = "RANDOM_TOKEN";

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession httpSession;

	@Mock
	private RequestAttributes attrs;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private UserDataManager userDataManager;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ProjectUserInfoDAO projectUserInfoDAO;

	@Mock
	private Cookie cookie;

	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock 
	private LocationDataManager locationDataManager;

	@InjectMocks
	private final ProgramService programService = new ProgramService();

	private Person loggedInPerson;
	private Person memberPerson;
	private Person superAdminPerson;
	private WorkbenchUser loggedInUser;
	private WorkbenchUser memberUser;
	private WorkbenchUser superAdminUser;
	private User cropUser;

	@Before
	public void setup() throws Exception {

		Mockito.when(this.request.getSession()).thenReturn(this.httpSession);
		Mockito.when(this.cookie.getName()).thenReturn(ContextConstants.PARAM_AUTH_TOKEN);
		;
		Mockito.when(this.cookie.getValue()).thenReturn(ProgramServiceTest.SAMPLE_AUTH_TOKEN_VALUE);
		Mockito.when(this.request.getCookies()).thenReturn(new Cookie[] { this.cookie });

		this.initializeTestPersonsAndUsers();

		Mockito.when(this.workbenchDataManager.getProjectUserInfoDao()).thenReturn(this.projectUserInfoDAO);
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(ProgramServiceTest.USER_ID);
	}

	private void initializeTestPersonsAndUsers() {
		// Setup test users and persons
		this.loggedInPerson = this.createPerson(1, "Jan", "Erik");
		this.memberPerson = this.createPerson(2, "John", "Doe");
		this.superAdminPerson = this.createPerson(3, "Default", "SuperAdmin");

		this.loggedInUser = this.createUser(1, "mrbreeder", 1);
		this.memberUser = this.createUser(2, "mrbreederfriend", 2);
		this.superAdminUser = this.createUser(3, ProgramServiceTest.SUPERADMIN_USERNAME, 3);
		this.cropUser = this.loggedInUser.copyToUser();
		this.cropUser.setUserid(1);

		// Setup mocks
		Mockito.when(this.contextUtil.getCurrentWorkbenchUser()).thenReturn(this.loggedInUser);
		Mockito.when(this.userDataManager.getUserByUserName(this.loggedInUser.getName())).thenReturn(this.cropUser);
		Mockito.when(this.userDataManager.getPersonByEmail(this.loggedInPerson.getEmail()))
				.thenReturn(this.loggedInPerson);

		Mockito.when(this.workbenchDataManager.getPersonById(this.loggedInPerson.getId()))
				.thenReturn(this.loggedInPerson);
		Mockito.when(this.workbenchDataManager.getPersonById(this.memberPerson.getId())).thenReturn(this.memberPerson);
		Mockito.when(this.workbenchDataManager.getPersonById(this.superAdminPerson.getId()))
				.thenReturn(this.superAdminPerson);
		Mockito.when(this.workbenchDataManager.getSuperAdminUsers())
				.thenReturn(Collections.singletonList(this.superAdminUser));
	}

	@Test
	public void testCreateNewProgram() throws Exception {
		// Create test data and set up mocks
		final Project project = this.createProject();
		final Set<WorkbenchUser> selectedUsers = new HashSet<WorkbenchUser>();
		selectedUsers.add(this.loggedInUser);
		selectedUsers.add(this.memberUser);

		final Integer unspecifiedLocationID = 9999;
		final String entityType = "LOCATION";

		// Other WorkbenchDataManager mocks
		Mockito.when(this.workbenchDataManager.getCropTypeByName(Matchers.anyString()))
				.thenReturn(project.getCropType());
		Mockito.when(this.userDataManager.addUser(Matchers.any(User.class))).thenReturn(2);
		Mockito.when(this.locationDataManager.retrieveLocIdOfUnspecifiedLocation()).thenReturn(String.valueOf(unspecifiedLocationID));

		// Call the method to test
		this.programService.createNewProgram(project, selectedUsers);

		// Verify that the key database operations for program creation are
		// invoked.
		Mockito.verify(this.workbenchDataManager).addProject(project);
		Assert.assertEquals(ProgramServiceTest.USER_ID, project.getUserId());
		Assert.assertNull(project.getLastOpenDate());

		this.verifyMockInteractionsForSavingProgramMembers();

		// Capture the argument of the saveProgramFavorite function
		final ArgumentCaptor<ProgramFavorite> captor = ArgumentCaptor.forClass(ProgramFavorite.class);
		Mockito.verify(this.germplasmDataManager, Mockito.times(1)).saveProgramFavorite(captor.capture());
		// Assert the arguments
		final ProgramFavorite programFavorite = captor.getValue();
		Assert.assertEquals(unspecifiedLocationID, programFavorite.getEntityId());
		Assert.assertEquals(entityType, programFavorite.getEntityType());

		// Verify that utility to create workspace directory was called
		Mockito.verify(this.installationDirectoryUtil).createWorkspaceDirectoriesForProject(project);

		// Verify session attribute was set
		final ArgumentCaptor<Object> contextInfoCaptor = ArgumentCaptor.forClass(Object.class);
		Mockito.verify(this.httpSession).setAttribute(Matchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO),
				contextInfoCaptor.capture());
		final ContextInfo contextInfo = (ContextInfo) contextInfoCaptor.getValue();
		Assert.assertEquals(ProgramServiceTest.USER_ID, contextInfo.getLoggedInUserId().intValue());
		Assert.assertEquals(project.getProjectId(), contextInfo.getSelectedProjectId());
		Assert.assertEquals(ProgramServiceTest.SAMPLE_AUTH_TOKEN_VALUE, contextInfo.getAuthToken());
	}

	@Test
	public void testCreateCropPersonIfNecessaryWhenPersonIsExisting() {
		// Call method to test
		final Person result = this.programService.createCropPersonIfNecessary(this.loggedInPerson);

		Mockito.verify(this.userDataManager, Mockito.times(0)).addPerson(Matchers.any(Person.class));
		Assert.assertSame(result, this.loggedInPerson);

	}

	@Test
	public void testCreateCropPersonIfNecessaryWhenPersonIsNotExisting() {
		// Returning null means person does not exist yet
		Mockito.when(this.userDataManager.getPersonByEmail(this.loggedInPerson.getEmail())).thenReturn(null);

		// Call method to test
		final Person result = this.programService.createCropPersonIfNecessary(this.loggedInPerson);

		Mockito.verify(this.userDataManager, Mockito.times(1)).addPerson(Matchers.any(Person.class));
		Assert.assertNotSame(result, this.loggedInPerson);
		Assert.assertEquals(result.getFirstName(), this.loggedInPerson.getFirstName());
		Assert.assertEquals(result.getLastName(), this.loggedInPerson.getLastName());

	}

	@Test
	public void testCreateCropUserIfNecessaryWhenUserIsExisting() {
		// Call method to test
		final User user = this.programService.createCropUserIfNecessary(this.loggedInUser, this.loggedInPerson);

		Mockito.verify(this.userDataManager, Mockito.times(0)).addUser(Matchers.any(User.class));
		Assert.assertSame(this.cropUser, user);
	}

	@Test
	public void testCreateCropUserIfNecessaryWhenUserIsNotExisting() {
		// Returning null means user does not exist yet
		Mockito.when(this.userDataManager.getUserByUserName(this.loggedInUser.getName())).thenReturn(null);

		// Call method to test
		final User user = this.programService.createCropUserIfNecessary(this.loggedInUser, this.memberPerson);

		Mockito.verify(this.userDataManager, Mockito.times(1)).addUser(Matchers.any(User.class));

		Assert.assertEquals(this.memberPerson.getId(), user.getPersonid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_ACCESS_NUMBER), user.getAccess());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_TYPE), user.getType());
		Assert.assertEquals(Integer.valueOf(0), user.getInstalid());
		Assert.assertEquals(Integer.valueOf(ProgramService.PROJECT_USER_STATUS), user.getStatus());
		Assert.assertNotNull(user.getAssignDate());

	}

	@Test
	public void testUpdateMembersUserInfo() {
		Mockito.when(this.workbenchDataManager.getActiveUserIDsByProjectId(Matchers.anyLong()))
				.thenReturn(Arrays.asList(1, 2, 3));
		final Project project = ProjectTestDataInitializer.createProject();
		final Set<WorkbenchUser> userList = new HashSet<>();
		userList.add(WorkbenchUserTestDataInitializer.createWorkbenchUser());
		this.programService.updateMembersUserInfo(userList, project);
		final int numberOfUsers = userList.size();
		Mockito.verify(this.workbenchDataManager, Mockito.times(numberOfUsers))
				.getProjectUserInfoByProjectIdAndUserId(Matchers.anyLong(), Matchers.anyInt());
		// Expecting to save only the 2nd user as the 1st user is already saved
		// as a member
		Mockito.verify(this.workbenchDataManager, Mockito.times(numberOfUsers))
				.saveOrUpdateProjectUserInfo(Matchers.any(ProjectUserInfo.class));
		Mockito.verify(this.workbenchDataManager).getActiveUserIDsByProjectId(Matchers.anyLong());
		Mockito.verify(this.workbenchDataManager).getProjectUserInfoByProjectIdAndUserIds(Matchers.anyLong(),
				Matchers.anyList());
		Mockito.verify(this.workbenchDataManager).deleteProjectUserInfos(Matchers.anyList());
	}

	@Test
	public void testGetRemovedUserIds() {
		final List<Integer> activeUserIds = Arrays.asList(1, 2);
		final Collection<WorkbenchUser> userList = Arrays.asList(new WorkbenchUser(1));
		final List<Integer> removedUserIds = this.programService.getRemovedUserIds(activeUserIds, userList);
		Assert.assertEquals(1, removedUserIds.size());
		Assert.assertEquals("2", removedUserIds.get(0).toString());
	}

	@Test
	public void testSaveWorkbenchUserToCropUserMapping() {

		final Project project = this.createProject();
		final Set<WorkbenchUser> users = new HashSet<>();
		users.add(this.loggedInUser);

		// Call method to test
		this.programService.saveWorkbenchUserToCropUserMapping(project, users);

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).addIbdbUserMap(Matchers.any(IbdbUserMap.class));

	}

	@Test
	public void testSaveProgramMembersWhenSuperAdminPartOfSelectedUsers() {
		// Setup test project users
		final Project project = this.createProject();
		final Set<WorkbenchUser> selectedUsers = new HashSet<WorkbenchUser>();
		selectedUsers.add(this.loggedInUser);
		selectedUsers.add(this.memberUser);
		selectedUsers.add(this.superAdminUser);

		// call method to test
		this.programService.saveProgramMembers(project, selectedUsers);

		this.verifyMockInteractionsForSavingProgramMembers();
	}

	@Test
	public void testSaveProgramMembersWhenSuperAdminNotPartOfSelectedUsers() {
		// Setup test project users
		final Project project = this.createProject();
		final Set<WorkbenchUser> selectedUsers = new HashSet<WorkbenchUser>();
		selectedUsers.add(this.loggedInUser);
		selectedUsers.add(this.memberUser);

		// call method to test
		this.programService.saveProgramMembers(project, selectedUsers);

		// Verify that in saveProgramMembers, superadmin user was added to set
		// of users
		Assert.assertEquals(3, selectedUsers.size());
		Assert.assertTrue(selectedUsers.contains(this.superAdminUser));

		this.verifyMockInteractionsForSavingProgramMembers();
	}

	// Verify Middleware methods to save as program members were called
	private void verifyMockInteractionsForSavingProgramMembers() {
		// Verify Ibdb_user_map is added for both current, member and SUPERADMIN
		// user
		Mockito.verify(this.workbenchDataManager, Mockito.times(3)).addIbdbUserMap(Matchers.any(IbdbUserMap.class));

		// Verify Workbench_project_user_info records are created
		Mockito.verify(this.workbenchDataManager, Mockito.times(3))
				.saveOrUpdateProjectUserInfo(Matchers.any(ProjectUserInfo.class));
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

	private WorkbenchUser createUser(final Integer userId, final String username, final Integer personId) {
		final WorkbenchUser loggedInUser = new WorkbenchUser();
		loggedInUser.setUserid(userId);
		loggedInUser.setName(username);
		loggedInUser.setPersonid(personId);
		return loggedInUser;
	}

}

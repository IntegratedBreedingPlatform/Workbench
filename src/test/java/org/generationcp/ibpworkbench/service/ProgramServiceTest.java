package org.generationcp.ibpworkbench.service;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.api.program.ProgramFavoriteService;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class ProgramServiceTest {

	private static final int USER_ID = 123;
	private static final String SAMPLE_AUTH_TOKEN_VALUE = "RANDOM_TOKEN";

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpSession httpSession;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private UserService userService;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private Cookie cookie;

	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;

	@Mock
	private LocationDataManager locationDataManager;

	@Mock
	private ProgramFavoriteService programFavoriteService;

	@Mock
	private org.generationcp.middleware.api.program.ProgramService programServiceMw;

	@InjectMocks
	private final ProgramService programService = new ProgramService();

	private WorkbenchUser loggedInUser;
	private WorkbenchUser memberUser;
	private WorkbenchUser cropUser;

	@Before
	public void setup() {

		Mockito.when(this.request.getSession()).thenReturn(this.httpSession);
		Mockito.when(this.cookie.getName()).thenReturn(ContextConstants.PARAM_AUTH_TOKEN);
		Mockito.when(this.cookie.getValue()).thenReturn(ProgramServiceTest.SAMPLE_AUTH_TOKEN_VALUE);
		Mockito.when(this.request.getCookies()).thenReturn(new Cookie[] {this.cookie});

		this.initializeTestPersonsAndUsers();
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(ProgramServiceTest.USER_ID);
	}

	private void initializeTestPersonsAndUsers() {
		// Setup test users and persons

		this.loggedInUser = this.createUser(1, "mrbreeder", 1);
		this.memberUser = this.createUser(2, "mrbreederfriend", 2);
		this.cropUser = this.loggedInUser;
		this.cropUser.setUserid(1);

	}

	@Test
	public void testCreateNewProgram() throws Exception {
		// Create test data and set up mocks
		final Project project = this.createProject();

		// TODO unused list after modifying programService.createNewProgram
		//  Rewrite or make new tests for program-role users
		//noinspection MismatchedQueryAndUpdateOfCollection
		final Set<WorkbenchUser> selectedUsers = new HashSet<>();
		selectedUsers.add(this.loggedInUser);
		selectedUsers.add(this.memberUser);

		final Integer unspecifiedLocationID = 9999;

		// Other WorkbenchDataManager mocks
		Mockito.when(this.workbenchDataManager.getCropTypeByName(ArgumentMatchers.anyString()))
				.thenReturn(project.getCropType());
		Mockito.when(this.locationDataManager.retrieveLocIdOfUnspecifiedLocation()).thenReturn(String.valueOf(unspecifiedLocationID));

		// Call the method to test
		this.programService.createNewProgram(project);

		// Verify that the key database operations for program creation are
		// invoked.
		Mockito.verify(this.programServiceMw).addProject(project);
		Assert.assertEquals(ProgramServiceTest.USER_ID, project.getUserId());
		Assert.assertNull(project.getLastOpenDate());

		Mockito.verify(this.programFavoriteService, Mockito.times(1)).addProgramFavorite(Mockito.eq(project.getUniqueID()), Mockito.eq(
			ProgramFavorite.FavoriteType.LOCATION), Mockito.eq(unspecifiedLocationID));

		// Verify that utility to create workspace directory was called
		Mockito.verify(this.installationDirectoryUtil)
			.createWorkspaceDirectoriesForProject(project.getCropType().getCropName(), project.getProjectName());

		// Verify session attribute was set
		final ArgumentCaptor<Object> contextInfoCaptor = ArgumentCaptor.forClass(Object.class);
		Mockito.verify(this.httpSession).setAttribute(ArgumentMatchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO),
				contextInfoCaptor.capture());
		final ContextInfo contextInfo = (ContextInfo) contextInfoCaptor.getValue();
		Assert.assertEquals(ProgramServiceTest.USER_ID, contextInfo.getLoggedInUserId().intValue());
		Assert.assertEquals(project.getProjectId(), contextInfo.getSelectedProjectId());
		Assert.assertEquals(ProgramServiceTest.SAMPLE_AUTH_TOKEN_VALUE, contextInfo.getAuthToken());
	}

	@Test
	public void testGetRemovedUserIds() {
		final List<Integer> activeUserIds = new ArrayList<>();
		activeUserIds.addAll(Arrays.asList(1, 2));
		final Collection<WorkbenchUser> userList = Arrays.asList(new WorkbenchUser(1));
		Mockito.when(this.userService.getActiveUserIDsWithAccessToTheProgram(ArgumentMatchers.anyLong()))
			.thenReturn(activeUserIds);
		final List<Integer> removedUserIds = this.programService.getUsersNotAssociatedToSpecificProgram(1, userList);
		Assert.assertEquals(1, removedUserIds.size());
		Assert.assertEquals("2", removedUserIds.get(0).toString());
	}

	@Test
	public void testAddUnspecifiedLocationToFavorite() {
		Mockito.when(this.locationDataManager.retrieveLocIdOfUnspecifiedLocation()).thenReturn("1");
		this.programService.addUnspecifiedLocationToFavorite(this.createProject());
		Mockito.verify(this.locationDataManager).retrieveLocIdOfUnspecifiedLocation();
		Mockito.verify(this.programFavoriteService).addProgramFavorite(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@Test
	public void testNonExistingUnspecifiedLocationId() {
		Mockito.when(this.locationDataManager.retrieveLocIdOfUnspecifiedLocation()).thenReturn("");
		this.programService.addUnspecifiedLocationToFavorite(this.createProject());
		Mockito.verify(this.programFavoriteService, Mockito.never()).addProgramFavorite(Mockito.any(), Mockito.any(), Mockito.any());
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

	private WorkbenchUser createUser(final Integer userId, final String username, final Integer personId) {
		final WorkbenchUser loggedInUser = new WorkbenchUser();
		loggedInUser.setUserid(userId);
		loggedInUser.setName(username);
		final Person person = new Person();
		person.setId(personId);
		loggedInUser.setPerson(person);
		return loggedInUser;
	}

}

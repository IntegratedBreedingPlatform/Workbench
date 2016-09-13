package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

@Service
@Transactional
public class ProgramService {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramService.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private UserDataManager userDataManager;

	private Set<User> selectedUsers;

	private User currentUser;

	// http://cropwiki.irri.org/icis/index.php/TDM_Users_and_Access
	public static final int PROJECT_USER_ACCESS_NUMBER = 100;
	public static final int PROJECT_USER_TYPE = 422;
	public static final int PROJECT_USER_STATUS = 1;

	public static final String ADMIN_ROLE = "ADMIN";

	public void createNewProgram(final Project program) {

		this.saveBasicDetails(program);

		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		final Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		final Cookie authToken = WebUtils.getCookie(request, ContextConstants.PARAM_AUTH_TOKEN);
		ContextUtil.setContextInfo(request, userIdCookie != null ? Integer.valueOf(userIdCookie.getValue()) : null, program.getProjectId(),
				authToken != null ? authToken.getValue() : null);
		ContextHolder.setCurrentCrop(program.getCropType().getCropName());
		this.toolUtil.createWorkspaceDirectoriesForProject(program);

		// Get all the ADMIN users then add the current selected users.
		final List<User> users = this.workbenchDataManager.getAllUsersByRole(org.generationcp.commons.security.Role.ADMIN.toString());
		users.addAll(this.selectedUsers);

		this.addUsersToProgram(users, program);

		ProgramService.LOG
				.info("Program created. ID:" + program.getProjectId() + " Name:" + program.getProjectName() + " Start date:" + program
						.getStartDate());
	}

	public void addUserToAllProgramsIfAdmin(final User user) {
		if (this.isAdmin(user)) {
			this.addProjectUserToAllPrograms(user);
		}
	}

	public boolean isAdmin(final User user) {
		final List<UserRole> roles = user.getRoles();
		if (roles != null) {
			for (final UserRole userRole : roles) {
				if (ProgramService.ADMIN_ROLE.equalsIgnoreCase(userRole.getRole())) {
					return true;
				}
			}
		}
		return false;
	}

	private void saveBasicDetails(final Project program) {
		program.setUserId(this.currentUser.getUserid());
		final CropType cropType = this.workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());
		if (cropType == null) {
			this.workbenchDataManager.addCropType(program.getCropType());
		}
		program.setLastOpenDate(null);
		this.workbenchDataManager.addProject(program);
	}

	/**
	 * Creates IBDBUserMap entries for the specified users.
	 */
	public void createIBDBUserMapping(final Project project, final Set<User> users, final Map<Integer, Person> workbenchPersonsMap,
			final Map<String, User> cropDBUsersMap) {

		for (final User user : users) {

			// ccreate a copy of the Person
			final Person workbenchPerson = workbenchPersonsMap.get(user.getPersonid());
			final User cropDBUser = createIBDBUserIfNecessary(user, workbenchPerson, cropDBUsersMap);

			this.createAndSaveIBDBUserMap(project.getProjectId(), user.getUserid(), cropDBUser.getUserid());

		}
	}

	public Map<String, User> retrieveExistingCropDBUsersMap(List<User> users) {

		Map<String, User> map = new HashMap<>();

		// generate list of usernames
		List<String> usernames = new ArrayList<>();
		for (User user : users) {
			usernames.add(user.getName());
		}

		for (User user : this.userDataManager.getUsersByUserNames(usernames)) {
			map.put(user.getName(), user);
		}
		return map;
	}

	public Map<Integer, Person> retrieveExistingWorkbenchPersonsMap(final List<User> users) {

		Map<Integer, Person> map = new HashMap<>();

		List<Integer> personids = new ArrayList<>();
		for (User user : users) {
			personids.add(user.getPersonid());
		}

		for (Person person : this.workbenchDataManager.getPersonsByIds(personids)) {
			map.put(person.getId(), person);
		}
		return map;
	}

	User createIBDBUserIfNecessary(User user, Person workbenchPerson, final Map<String, User> cropDBUsersMap) {

		// if a user is assigned to a program, then add them to the crop database where they can then access the program
		if (!cropDBUsersMap.containsKey(user.getName())) {

			Person newCropDBPerson = workbenchPerson.copy();
			Integer newCropDBPersonId = this.userDataManager.addPerson(newCropDBPerson);

			User newCropDBUser = user.copy();
			newCropDBUser.setPersonid(newCropDBPersonId);
			newCropDBUser.setAccess(ProgramService.PROJECT_USER_ACCESS_NUMBER);
			newCropDBUser.setType(ProgramService.PROJECT_USER_TYPE);
			newCropDBUser.setInstalid(Integer.valueOf(0));
			newCropDBUser.setStatus(Integer.valueOf(ProgramService.PROJECT_USER_STATUS));
			newCropDBUser.setAssignDate(this.getCurrentDate());

			this.userDataManager.addUser(newCropDBUser);

			return newCropDBUser;

		} else {
			return cropDBUsersMap.get(user.getName());
		}

	}

	String getNameKey(Person person) {
		return person.getFirstName().toLowerCase() + person.getLastName().toLowerCase();
	}

	private void createAndSaveIBDBUserMap(final Long projectId, final Integer workbenchUserId, final Integer ibdbUserId) {
		// Add the mapping between Workbench user and the ibdb user.
		final IbdbUserMap ibdbUserMap = new IbdbUserMap();
		ibdbUserMap.setWorkbenchUserId(workbenchUserId);
		ibdbUserMap.setProjectId(projectId);
		ibdbUserMap.setIbdbUserId(ibdbUserId);
		this.workbenchDataManager.addIbdbUserMap(ibdbUserMap);
	}

	private Integer getCurrentDate() {
		return DateUtil.getCurrentDateAsIntegerValue();
	}

	public void setSelectedUsers(final Set<User> users) {
		this.selectedUsers = users;
	}

	public void setCurrentUser(final User currentUser) {
		this.currentUser = currentUser;
	}

	void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	void setToolUtil(final ToolUtil toolUtil) {
		this.toolUtil = toolUtil;
	}

	public void addProjectUserToAllPrograms(final User user) {

		final List<Project> projects = this.workbenchDataManager.getProjects();
		final List<Role> allRoles = this.workbenchDataManager.getAllRoles();

		final Map<Integer, Person> workbenchPersonsMap = retrieveExistingWorkbenchPersonsMap(Arrays.asList(user));
		final Map<String, User> cropDBUsersMap = retrieveExistingCropDBUsersMap(Arrays.asList(user));

		for (final Project project : projects) {
			if (this.workbenchDataManager.getProjectUserInfoDao()
					.getByProjectIdAndUserId(project.getProjectId().intValue(), user.getUserid()) == null) {
				final ProjectUserInfo pUserInfo = new ProjectUserInfo(project.getProjectId().intValue(), user.getUserid());
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
			this.assignAllTheRolesOfTheProgramToUser(allRoles, project, user);
			this.createIBDBUserMapping(project, new HashSet<User>(Arrays.asList(user)), workbenchPersonsMap, cropDBUsersMap);
		}

	}

	public void addUsersToProgram(final List<User> users, final Project program) {

		final List<Role> allRoles = this.workbenchDataManager.getAllRoles();

		final Map<Integer, Person> workbenchPersonsMap = retrieveExistingWorkbenchPersonsMap(users);
		final Map<String, User> cropDBUsersMap = retrieveExistingCropDBUsersMap(users);

		for (final User user : users) {
			if (this.workbenchDataManager.getProjectUserInfoDao()
					.getByProjectIdAndUserId(program.getProjectId().intValue(), user.getUserid()) == null) {
				final ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(), user.getUserid());
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
			this.assignAllTheRolesOfTheProgramToUser(allRoles, program, user);
		}
		this.createIBDBUserMapping(program, new HashSet<>(users), workbenchPersonsMap, cropDBUsersMap);

	}

	@SuppressWarnings("rawtypes")
	private void assignAllTheRolesOfTheProgramToUser(final List<Role> allRoles, final Project project, final User user) {
		final List<Role> roles = new ArrayList(allRoles);
		final List<Role> existingRoles = this.workbenchDataManager.getRolesByProjectAndUser(project, user);
		roles.removeAll(existingRoles);
		List<ProjectUserRole> userRoles = new ArrayList<>();
		for (final Role role : roles) {
			final ProjectUserRole projectUserRole = new ProjectUserRole();
			projectUserRole.setProject(project);
			projectUserRole.setUserId(user.getUserid());
			projectUserRole.setRole(role);
			userRoles.add(projectUserRole);
		}
		this.workbenchDataManager.addProjectUserRole(userRoles);
	}

}


package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
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

	private final Map<Integer, String> idAndNameOfProgramMembers = new HashMap<Integer, String>();

	// http://cropwiki.irri.org/icis/index.php/TDM_Users_and_Access
	private static final int PROJECT_USER_ACCESS_NUMBER = 100;
	private static final int PROJECT_USER_TYPE = 422;
	private static final int PROJECT_USER_STATUS = 1;

	public static final String ADMIN_ROLE = "ADMIN";

	public void createNewProgram(final Project program) {

		this.idAndNameOfProgramMembers.clear();

		this.saveBasicDetails(program);

		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		final Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		final Cookie authToken = WebUtils.getCookie(request, ContextConstants.PARAM_AUTH_TOKEN);
		ContextUtil.setContextInfo(request, userIdCookie != null ? Integer.valueOf(userIdCookie.getValue()) : null, program.getProjectId(),
				authToken != null ? authToken.getValue() : null);
		ContextHolder.setCurrentCrop(program.getCropType().getCropName());
		this.toolUtil.createWorkspaceDirectoriesForProject(program);

		this.addProjectUserRoles(program);
		this.copyProjectUsers(program);
		this.saveProjectUserInfo(program);

		this.addAllAdminUsersOfCropToProgram(program.getCropType().getCropName(), program);

		ProgramService.LOG.info("Program created. ID:" + program.getProjectId() + " Name:" + program.getProjectName() + " Start date:"
				+ program.getStartDate());
	}

	public void addUserToAllProgramsOfCropTypeIfAdmin(final User user, final CropType cropType) {
		if (this.isAdmin(user)) {
			this.addProjectUserToAllProgramsOfCropType(user, cropType);
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

	private void addProjectUserRoles(final Project project) {
		final List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
		final Set<User> allProjectMembers = new HashSet<User>();
		allProjectMembers.add(this.currentUser);
		allProjectMembers.addAll(this.selectedUsers);

		final List<Role> allRolesList = this.workbenchDataManager.getAllRoles();

		for (final User user : allProjectMembers) {
			for (final Role role : allRolesList) {
				final ProjectUserRole projectUserRole = new ProjectUserRole();
				projectUserRole.setUserId(user.getUserid());
				projectUserRole.setRole(role);
				projectUserRole.setProject(project);
				projectUserRoles.add(projectUserRole);
			}
		}
		this.workbenchDataManager.addProjectUserRole(projectUserRoles);
	}

	private void saveProjectUserInfo(final Project program) {
		// Create records for workbench_project_user_info table
		for (final Map.Entry<Integer, String> e : this.idAndNameOfProgramMembers.entrySet()) {
			try {
				if (this.workbenchDataManager.getProjectUserInfoDao()
						.getByProjectIdAndUserId(program.getProjectId().intValue(), e.getKey()) == null) {
					final ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(), e.getKey());
					this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
				}
			} catch (final MiddlewareQueryException e1) {
				ProgramService.LOG.error(e1.getMessage(), e1);
			}
		}
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
	 * Create necessary database entries for selected program members.
	 */
	public void copyProjectUsers(final Project project) {

		for (final User user : this.selectedUsers) {

			// fetch user record from workbench DB
			final User workbenchUser = this.workbenchDataManager.getUserById(user.getUserid());

			// ccreate a copy of the Person
			final Person workbenchPerson = this.workbenchDataManager.getPersonById(workbenchUser.getPersonid());
			Person cropDBPerson = workbenchPerson.copy();

			// add the Person record if they do not exist
			if (!this.userDataManager.isPersonExists(cropDBPerson.getFirstName().toUpperCase(), cropDBPerson.getLastName().toUpperCase())) {
				this.userDataManager.addPerson(cropDBPerson);
			} else {
				final List<Person> persons = this.userDataManager.getAllPersons();
				for (final Person person : persons) {
					if (person.getLastName().equalsIgnoreCase(cropDBPerson.getLastName().toUpperCase())
							&& person.getFirstName().equalsIgnoreCase(cropDBPerson.getFirstName().toUpperCase())) {
						cropDBPerson = person;
						break;
					}
				}
			}

			// create a copy of the user record
			final User cropDBUser = workbenchUser.copy();

			// if a user is assigned to a program, then add them to the crop database where they can then access the program
			if (!this.userDataManager.isUsernameExists(cropDBUser.getName())) {
				cropDBUser.setPersonid(cropDBPerson.getId());
				cropDBUser.setAccess(ProgramService.PROJECT_USER_ACCESS_NUMBER);
				cropDBUser.setType(ProgramService.PROJECT_USER_TYPE);
				cropDBUser.setInstalid(Integer.valueOf(0));
				cropDBUser.setStatus(Integer.valueOf(ProgramService.PROJECT_USER_STATUS));
				cropDBUser.setAssignDate(this.getCurrentDate());
				final Integer userId = this.userDataManager.addUser(cropDBUser);

				final User ibdbUser = this.userDataManager.getUserById(userId);
				this.createIBDBUserMap(project.getProjectId(), workbenchUser.getUserid(), ibdbUser.getUserid());
			} else {
				final User ibdbUser = this.userDataManager.getUserByUserName(cropDBUser.getName());
				this.createIBDBUserMap(project.getProjectId(), workbenchUser.getUserid(), ibdbUser.getUserid());
			}
			this.idAndNameOfProgramMembers.put(workbenchUser.getUserid(), cropDBUser.getName());
		}
	}

	private void createIBDBUserMap(final Long projectId, final Integer workbenchUserId, final Integer ibdbUserId) {
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

	public void addProjectUserToAllProgramsOfCropType(final User user, final CropType cropType) {
		final List<Project> projects = this.workbenchDataManager.getProjectsByCropType(cropType);
		final List<Role> allRoles = this.workbenchDataManager.getAllRoles();
		for (final Project project : projects) {
			if (this.workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(project.getProjectId().intValue(),
					user.getUserid()) == null) {
				final ProjectUserInfo pUserInfo = new ProjectUserInfo(project.getProjectId().intValue(), user.getUserid());
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
			this.assignAllTheRolesOfTheProgramToUser(allRoles, project, user);
		}
	}

	public void addAllAdminUsersOfCropToProgram(final String crop, final Project program) {
		final List<User> adminUsers = this.workbenchDataManager.getAdminUsersOfCrop(crop);
		final List<Role> allRoles = this.workbenchDataManager.getAllRoles();
		for (final User user : adminUsers) {
			if (this.workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(program.getProjectId().intValue(),
					user.getUserid()) == null) {
				final ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(), user.getUserid());
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
			this.assignAllTheRolesOfTheProgramToUser(allRoles, program, user);
		}
	}

	@SuppressWarnings("rawtypes")
	private void assignAllTheRolesOfTheProgramToUser(final List<Role> allRoles, final Project program, final User user) {
		final List<Role> roles = new ArrayList(allRoles);
		final List<Role> existingRoles = this.workbenchDataManager.getRolesByProjectAndUser(program, user);
		roles.removeAll(existingRoles);
		for (final Role role : roles) {
			this.workbenchDataManager.addProjectUserRole(program, user, role);
		}
	}

}

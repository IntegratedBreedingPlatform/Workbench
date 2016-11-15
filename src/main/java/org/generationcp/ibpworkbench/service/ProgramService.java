
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.SessionData;
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

	public static final String ADMIN_USERNAME = "ADMIN";

	private static final Logger LOG = LoggerFactory.getLogger(ProgramService.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private UserDataManager userDataManager;

	@Autowired
	private SessionData sessionData;

	// http://cropwiki.irri.org/icis/index.php/TDM_Users_and_Access
	public static final int PROJECT_USER_ACCESS_NUMBER = 100;
	public static final int PROJECT_USER_TYPE = 422;
	public static final int PROJECT_USER_STATUS = 1;

	/**
	 * Create new project in workbench and add specified users as project members. Also creates copy of workbench person and user to currect
	 * crop DB, if not yet existing. Finally, create a new folder under <install directory>/workspace/<program name>
	 *
	 * @param program : program to save
	 * @param programUsers : users to add as members of new program
	 */
	public void createNewProgram(final Project program, final Set<User> programUsers) {
		// Need to save first to workbench_project so project id can be saved in session
		this.saveWorkbenchProject(program);
		this.setContextInfoAndCurrentCrop(program);

		this.saveProgramMembers(program, programUsers);

		// After saving, we create folder for program under <install directory>/workspace
		this.toolUtil.createWorkspaceDirectoriesForProject(program);

		ProgramService.LOG.info("Program created. ID:" + program.getProjectId() + " Name:" + program.getProjectName() + " Start date:"
				+ program.getStartDate());
	}

	/**
	 * Save default "ADMIN" user and other set selected users as members of given program by saving in the ff tables:
	 * workbench_project_user_role, workbench_project_user_info, workbench_ibdb_user_map and in crop.persons (if applicable)
	 *
	 * @param program : program to add members to
	 * @param users : users to add as members of given program
	 */
	public void saveProgramMembers(final Project program, final Set<User> users) {
		// Add default "ADMIN" user to selected users of program to give access to new program
		final User defaultAdminUser = this.workbenchDataManager.getUserByUsername(ProgramService.ADMIN_USERNAME);
		if (defaultAdminUser != null) {
			users.add(defaultAdminUser);
		}

		// Save workbench project metadata and to crop users, persons (if necessary)
		if (!users.isEmpty()) {
			this.saveProjectUserRoles(program, users);
			final List<Integer> userIDs = this.saveWorkbenchUserToCropUserMapping(program, users);
			this.saveProjectUserInfo(program, userIDs);
		}
	}

	private void setContextInfoAndCurrentCrop(final Project program) {
		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		final Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		final Cookie authToken = WebUtils.getCookie(request, ContextConstants.PARAM_AUTH_TOKEN);
		ContextUtil.setContextInfo(request, userIdCookie != null ? Integer.valueOf(userIdCookie.getValue()) : null, program.getProjectId(),
				authToken != null ? authToken.getValue() : null);

		ContextHolder.setCurrentCrop(program.getCropType().getCropName());
		ContextHolder.setCurrentProgram(program.getUniqueID());
	}

	/*
	 * Add records to workbench_project_user_role table. It's a redundant table but currently, a record here is needed in order to get
	 * projects of user
	 *
	 * @param project
	 */
	private void saveProjectUserRoles(final Project project, final Set<User> users) {
		final List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
		final List<Role> allRolesList = this.workbenchDataManager.getAllRoles();

		if (allRolesList != null && !allRolesList.isEmpty()){
			// we only need 1 record in workbench_project_user_role table for user to have access to program
			final Role role = allRolesList.get(0);
			for (final User user : users) {
				final ProjectUserRole projectUserRole = new ProjectUserRole();
				projectUserRole.setUserId(user.getUserid());
				projectUserRole.setRole(role);
				projectUserRole.setProject(project);
				projectUserRoles.add(projectUserRole);
			}
			this.workbenchDataManager.addProjectUserRole(projectUserRoles);
		}

	}

	/*
	 * Create records for workbench_project_user_info table if combination of project id, user id is not yet existing in workbench DB
	 */
	private void saveProjectUserInfo(final Project program, final List<Integer> userIDs) {
		for (final Integer userID : userIDs) {
			final int projectID = program.getProjectId().intValue();

			if (this.workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(projectID, userID) == null) {
				final ProjectUserInfo pUserInfo = new ProjectUserInfo(projectID, userID);
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
		}
	}

	/*
	 * Create new record in workbench_project table in workbench DB for current crop.
	 *
	 * @param program
	 */
	private void saveWorkbenchProject(final Project program) {
		// sets current user as program owner
		program.setUserId(this.sessionData.getUserData().getUserid());

		final CropType cropType = this.workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());
		if (cropType == null) {
			this.workbenchDataManager.addCropType(program.getCropType());
		}
		program.setLastOpenDate(null);

		this.workbenchDataManager.addProject(program);
	}

	/**
	 * Create person and user records in crop DB (from counterpart workbench records), if not yet existing in crop. Save mapping of
	 * workbench user to crop user
	 *
	 * @param project
	 * @param users
	 * @return
	 */
	public List<Integer> saveWorkbenchUserToCropUserMapping(final Project project, final Set<User> users) {
		final List<Integer> userIDs = new ArrayList<>();

		for (final User user : users) {
			// Create person and user records in crop DB if not yet existing.
			final Person workbenchPerson = this.workbenchDataManager.getPersonById(user.getPersonid());
			final Person cropPerson = this.createCropPersonIfNecessary(workbenchPerson);
			final User cropUser = this.createCropUserIfNecessary(user, cropPerson);

			// Save mapping of workbench user to crop user
			this.createAndSaveIBDBUserMap(project.getProjectId(), user.getUserid(), cropUser.getUserid());
			userIDs.add(user.getUserid());
		}

		return userIDs;
	}

	/**
	 * Search for a person record in crop DB with specified email. Email is assumed to be unique among users. If not yet existing, add new
	 * person record.
	 *
	 * @param workbenchPerson
	 * @return
	 */
	Person createCropPersonIfNecessary(final Person workbenchPerson) {
		Person cropDBPerson = this.userDataManager.getPersonByEmail(workbenchPerson.getEmail());
		if (cropDBPerson == null) {
			cropDBPerson = workbenchPerson.copy();
			this.userDataManager.addPerson(cropDBPerson);
		}

		return cropDBPerson;
	}

	/**
	 * Search for a person record in crop DB with specified username. If not found, create a user record in crop DB
	 *
	 * @param user
	 * @param cropPerson
	 * @return
	 */
	User createCropUserIfNecessary(final User user, final Person cropPerson) {
		User cropUser = this.userDataManager.getUserByUserName(user.getName());

		if (cropUser == null) {
			cropUser = user.copy();
			cropUser.setPersonid(cropPerson.getId());
			cropUser.setAccess(ProgramService.PROJECT_USER_ACCESS_NUMBER);
			cropUser.setType(ProgramService.PROJECT_USER_TYPE);
			cropUser.setInstalid(Integer.valueOf(0));
			cropUser.setStatus(Integer.valueOf(ProgramService.PROJECT_USER_STATUS));
			cropUser.setAssignDate(this.getCurrentDate());

			this.userDataManager.addUser(cropUser);
		}

		return cropUser;
	}

	// Add the mapping between Workbench user and the ibdb user.
	private void createAndSaveIBDBUserMap(final Long projectId, final Integer workbenchUserId, final Integer ibdbUserId) {
		final IbdbUserMap ibdbUserMap = new IbdbUserMap();
		ibdbUserMap.setWorkbenchUserId(workbenchUserId);
		ibdbUserMap.setProjectId(projectId);
		ibdbUserMap.setIbdbUserId(ibdbUserId);
		this.workbenchDataManager.addIbdbUserMap(ibdbUserMap);
	}

	private Integer getCurrentDate() {
		return DateUtil.getCurrentDateAsIntegerValue();
	}

	void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	void setToolUtil(final ToolUtil toolUtil) {
		this.toolUtil = toolUtil;
	}

}

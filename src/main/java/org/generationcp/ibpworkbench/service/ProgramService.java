
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

	private Set<User> selectedUsers;

	private User currentUser;

	// http://cropwiki.irri.org/icis/index.php/TDM_Users_and_Access
	public static final int PROJECT_USER_ACCESS_NUMBER = 100;
	public static final int PROJECT_USER_TYPE = 422;
	public static final int PROJECT_USER_STATUS = 1;
	
	
	/**
	 * Create new project in workbench and add selected users as project members. Also creates copy of workbench person and user 
	 * to currect crop DB, if not yet existing. Finally, create a new folder under <install directory>/workspace/<program name>
	 * 
	 * @param program
	 */
	public void createNewProgram(Project program) {
		// Need to save first to workbench_project so project id can be saved in session
		this.saveWorkbenchProject(program);
		this.setContextInfoAndCurrentCrop(program);
		
		// Add default "ADMIN" user to selected users of program to give access to new program
		final User defaultAdminUser = this.workbenchDataManager.getUserByUsername(ADMIN_USERNAME);
		this.selectedUsers.add(defaultAdminUser);
		
		// Save workbench project metadata and to crop users, persons (if necessary)
		this.saveProjectUserRoles(program);
		final List<Integer> userIDs = this.saveWorkbenchUserToCropUserMapping(program, this.selectedUsers);
		this.saveProjectUserInfo(program, userIDs);
		
		// After saving, we create folder for program under <install directory>/workspace
		this.toolUtil.createWorkspaceDirectoriesForProject(program);

		
		ProgramService.LOG.info("Program created. ID:" + program.getProjectId() + " Name:" + program.getProjectName() + " Start date:"
				+ program.getStartDate());
	}

	
	private void setContextInfoAndCurrentCrop(Project program) {
		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		final Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		final Cookie authToken = WebUtils.getCookie(request, ContextConstants.PARAM_AUTH_TOKEN);
		ContextUtil.setContextInfo(request, userIdCookie != null ? Integer.valueOf(userIdCookie.getValue()) :  null, 
				program.getProjectId(), authToken !=null ? authToken.getValue() : null);
		
		ContextHolder.setCurrentCrop(program.getCropType().getCropName());
	}

	
	/*
	 * Add records to workbench_project_user_role table. It's a redundant table but currently,
	 * a record here is needed in order to get projects of user
	 * 
	 * @param project
	 */
	private void saveProjectUserRoles(Project project) {
		List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
		Set<User> allProjectMembers = new HashSet<User>();
		allProjectMembers.add(this.currentUser);
		allProjectMembers.addAll(this.selectedUsers);

		List<Role> allRolesList = this.workbenchDataManager.getAllRoles();

		for (User user : allProjectMembers) {
			for (Role role : allRolesList) {
				ProjectUserRole projectUserRole = new ProjectUserRole();
				projectUserRole.setUserId(user.getUserid());
				projectUserRole.setRole(role);
				projectUserRole.setProject(project);
				projectUserRoles.add(projectUserRole);
			}
		}
		this.workbenchDataManager.addProjectUserRole(projectUserRoles);
	}

	
	/*
	 *  Create records for workbench_project_user_info table if combination of 
	 *  project id, user id is not yet existing in workbench DB
	 */
	private void saveProjectUserInfo(final Project program, final List<Integer> userIDs) {
		for (final Integer userID : userIDs) {
			final int projectID = program.getProjectId().intValue();
			
			if (this.workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(projectID, userID) == null) {
				ProjectUserInfo pUserInfo = new ProjectUserInfo(projectID, userID);
				this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
			}
		}
	}

	
	/*
	 * Create new record in workbench_project table in workbench DB for current crop.
	 * 
	 * @param program
	 */
	private void saveWorkbenchProject(Project program) {
		program.setUserId(this.currentUser.getUserid());
		CropType cropType = this.workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());
		if (cropType == null) {
			this.workbenchDataManager.addCropType(program.getCropType());
		}
		program.setLastOpenDate(null);
		
		this.workbenchDataManager.addProject(program);
	}

	
	/**
	 * Create person and user records in crop DB (from counterpart workbench records), if not yet existing in crop.
	 * Save mapping of workbench user to crop user
	 * 
	 * @param project
	 * @param users
	 * @return
	 */
	public List<Integer> saveWorkbenchUserToCropUserMapping(Project project, Set<User> users) {
		final List<Integer> userIDs = new ArrayList<>();
		
		for (User user : users) {
			// Create person and user records in crop DB if not yet existing.
			final Person workbenchPerson = this.workbenchDataManager.getPersonById(user.getPersonid());
			final Person cropPerson = createCropPersonIfNecessary(workbenchPerson);
			final User cropUser = createCropUserIfNecessary(user, cropPerson);

			// Save mapping of workbench user to crop user
			this.createAndSaveIBDBUserMap(project.getProjectId(), user.getUserid(), cropUser.getUserid());
			userIDs.add(user.getUserid());
		}
		
		return userIDs;
	}
	
	
	/**
	 * Search for a person record in crop DB with specified email. Email is assumed to be unique among users.
	 * If not yet existing, add new person record.
	 * 
	 * @param workbenchPerson
	 * @return
	 */
	Person createCropPersonIfNecessary(Person workbenchPerson) {
		Person cropDBPerson = userDataManager.getPersonByEmail(workbenchPerson.getEmail());
		if (cropDBPerson == null) {
			cropDBPerson = workbenchPerson.copy();
			userDataManager.addPerson(cropDBPerson);
		}

		return cropDBPerson;
	}

	
	/**
	 * Search for a person record in crop DB with specified username.
	 * If not found, create a user record in crop DB
	 * 
	 * @param user
	 * @param cropPerson
	 * @return
	 */
	User createCropUserIfNecessary(User user, Person cropPerson) {
		User cropUser = userDataManager.getUserByUserName(user.getName());
		
		if (cropUser == null) {
			cropUser = user.copy();
			cropUser.setPersonid(cropPerson.getId());
			cropUser.setAccess(ProgramService.PROJECT_USER_ACCESS_NUMBER);
			cropUser.setType(ProgramService.PROJECT_USER_TYPE);
			cropUser.setInstalid(Integer.valueOf(0));
			cropUser.setStatus(Integer.valueOf(ProgramService.PROJECT_USER_STATUS));
			cropUser.setAssignDate(this.getCurrentDate());
			
			userDataManager.addUser(cropUser);
		} 
		
		return cropUser;
	}

	
	// Add the mapping between Workbench user and the ibdb user.
	private void createAndSaveIBDBUserMap(Long projectId, Integer workbenchUserId, Integer ibdbUserId) {
		IbdbUserMap ibdbUserMap = new IbdbUserMap();
		ibdbUserMap.setWorkbenchUserId(workbenchUserId);
		ibdbUserMap.setProjectId(projectId);
		ibdbUserMap.setIbdbUserId(ibdbUserId);
		this.workbenchDataManager.addIbdbUserMap(ibdbUserMap);
	}
		
	private Integer getCurrentDate() {
		return DateUtil.getCurrentDateAsIntegerValue();
	}

	public void setSelectedUsers(Set<User> users) {
		this.selectedUsers = users;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	void setToolUtil(ToolUtil toolUtil) {
		this.toolUtil = toolUtil;
	}
	
}

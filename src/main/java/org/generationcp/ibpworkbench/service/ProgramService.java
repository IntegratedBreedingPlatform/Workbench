
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
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.util.ToolUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
	private ManagerFactoryProvider managerFactoryProvider;
	
	@Autowired
	private UserDataManager userDataManager;

	private MysqlAccountGenerator mySQLAccountGenerator;

	private Set<User> selectedUsers;

	private User currentUser;

	private final Map<Integer, String> idAndNameOfProgramMembers = new HashMap<Integer, String>();

	// http://cropwiki.irri.org/icis/index.php/TDM_Users_and_Access
	private static final int PROJECT_USER_ACCESS_NUMBER = 100;
	private static final int PROJECT_USER_TYPE = 422;
	private static final int PROJECT_USER_STATUS = 1;

	public void createNewProgram(Project program) throws MiddlewareQueryException {

		this.idAndNameOfProgramMembers.clear();

		this.saveBasicDetails(program);
		
		final HttpServletRequest request = ((ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes())
				.getRequest();
		final Cookie userIdCookie = WebUtils.getCookie(request, ContextConstants.PARAM_LOGGED_IN_USER_ID);
		final Cookie authToken = WebUtils.getCookie(request, ContextConstants.PARAM_AUTH_TOKEN);
		ContextUtil.setContextInfo(request, userIdCookie != null ? Integer.valueOf(userIdCookie.getValue()) :  null, 
				program.getProjectId(), authToken !=null ? authToken.getValue() : null);
		
		this.toolUtil.createWorkspaceDirectoriesForProject(program);

		this.addProjectUserRoles(program);
		this.copyProjectUsers(userDataManager, program);
		this.saveProjectUserInfo(program);

		ProgramService.LOG.info("Program created. ID:" + program.getProjectId() + " Name:" + program.getProjectName() + " Start date:"
				+ program.getStartDate());
	}

	private void addProjectUserRoles(Project project) throws MiddlewareQueryException {
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

	private void saveProjectUserInfo(Project program) {
		// Create records for workbench_project_user_info table
		for (Map.Entry<Integer, String> e : this.idAndNameOfProgramMembers.entrySet()) {
			try {
				if (this.workbenchDataManager.getProjectUserInfoDao()
						.getByProjectIdAndUserId(program.getProjectId().intValue(), e.getKey()) == null) {
					ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(), e.getKey());
					this.workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
				}
			} catch (MiddlewareQueryException e1) {
				ProgramService.LOG.error(e1.getMessage(), e1);
			}
		}
	}

	private void saveBasicDetails(Project program) throws MiddlewareQueryException {
		program.setUserId(this.currentUser.getUserid());
		CropType cropType = this.workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());
		if (cropType == null) {
			this.workbenchDataManager.addCropType(program.getCropType());
		}
		program.setLastOpenDate(null);
		this.workbenchDataManager.addProject(program);
	}

	/**
	 * Create necessary database entries for selected program members.
	 */
	private void copyProjectUsers(UserDataManager userDataManager, Project project) throws MiddlewareQueryException {

		for (User user : this.selectedUsers) {

			User workbenchUser = this.workbenchDataManager.getUserById(user.getUserid());
			User cropDBUser = workbenchUser.copy();

			Person workbenchPerson = this.workbenchDataManager.getPersonById(workbenchUser.getPersonid());
			Person cropDBPerson = workbenchPerson.copy();

			if (!userDataManager.isPersonExists(cropDBPerson.getFirstName().toUpperCase(), cropDBPerson.getLastName().toUpperCase())) {
				userDataManager.addPerson(cropDBPerson);
			} else {
				List<Person> persons = userDataManager.getAllPersons();
				for (Person person : persons) {
					if (person.getLastName().equalsIgnoreCase(cropDBPerson.getLastName().toUpperCase())
							&& person.getFirstName().equalsIgnoreCase(cropDBPerson.getFirstName().toUpperCase())) {
						cropDBPerson = person;
						break;
					}
				}
			}

			if (!userDataManager.isUsernameExists(cropDBUser.getName())) {
				cropDBUser.setPersonid(cropDBPerson.getId());
				// TODO we are setting following fields because they are non nullable. Review and make nullable.
				// See http://cropwiki.irri.org/icis/index.php/TDM_ICIS_Application_and_Database_Installation for background.
				cropDBUser.setAccess(ProgramService.PROJECT_USER_ACCESS_NUMBER);
				cropDBUser.setType(ProgramService.PROJECT_USER_TYPE);
				cropDBUser.setInstalid(Integer.valueOf(0));
				cropDBUser.setStatus(Integer.valueOf(ProgramService.PROJECT_USER_STATUS));
				cropDBUser.setAdate(this.getCurrentDate());
				Integer userId = userDataManager.addUser(cropDBUser);

				User ibdbUser = userDataManager.getUserById(userId);
				this.createIBDBUserMap(project.getProjectId(), workbenchUser.getUserid(), ibdbUser.getUserid());
			} else {
				User ibdbUser = userDataManager.getUserByUserName(cropDBUser.getName());
				this.createIBDBUserMap(project.getProjectId(), workbenchUser.getUserid(), ibdbUser.getUserid());
			}
			this.idAndNameOfProgramMembers.put(workbenchUser.getUserid(), cropDBUser.getName());
		}
	}

	private void createIBDBUserMap(Long projectId, Integer workbenchUserId, Integer ibdbUserId) throws MiddlewareQueryException {
		// Add the mapping between Workbench user and the ibdb user.
		IbdbUserMap ibdbUserMap = new IbdbUserMap();
		ibdbUserMap.setWorkbenchUserId(workbenchUserId);
		ibdbUserMap.setProjectId(projectId);
		ibdbUserMap.setIbdbUserId(ibdbUserId);
		this.workbenchDataManager.addIbdbUserMap(ibdbUserMap);
	}

	private void createMySQLAccounts(Project program) {
		// Create mysql user accounts for members of the project
		this.mySQLAccountGenerator.setCropType(program.getCropType());
		this.mySQLAccountGenerator.setProjectId(program.getProjectId());
		this.mySQLAccountGenerator.setIdAndNameOfProjectMembers(this.idAndNameOfProgramMembers);
		this.mySQLAccountGenerator.setDataManager(this.workbenchDataManager);
		this.mySQLAccountGenerator.generateMysqlAccounts();
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

	public void setMySQLAccountGenerator(MysqlAccountGenerator mySQLAccountGenerator) {
		this.mySQLAccountGenerator = mySQLAccountGenerator;
	}

	void setManagerFactoryProvider(ManagerFactoryProvider managerFactoryProvider) {
		this.managerFactoryProvider = managerFactoryProvider;
	}
}

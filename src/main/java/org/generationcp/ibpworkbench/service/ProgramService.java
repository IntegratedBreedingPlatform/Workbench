
package org.generationcp.ibpworkbench.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.database.IBDBGeneratorCentralDb;
import org.generationcp.ibpworkbench.database.IBDBGeneratorLocalDb;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
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

@Service
public class ProgramService {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramService.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;
	
	@Autowired
    private ManagerFactoryProvider managerFactoryProvider;
	
	private IBDBGeneratorCentralDb centralDbGenerator;
	private IBDBGeneratorLocalDb localDbGenerator;
	
    private Set<User> selectedUsers;
    
    private User currentUser;

    private List<Role> allRolesList;
    private final Map<Integer, String> idAndNameOfProgramMembers = new HashMap<Integer, String>();
    
    private static final int PROJECT_USER_ACCESS_NUMBER = 100;
    private static final int PROJECT_USER_TYPE = 422;
    private static final int PROJECT_USER_STATUS = 1;
    private static final int PROJECT_USER_ACCESS_NUMBER_CENTRAL = 150;
    private static final int PROJECT_USER_TYPE_CENTRAL = 420;

	public void createNewProgram(Project program) throws Exception {

		saveBasicDetails(program);		
		// create the project's workspace directories
		toolUtil.createWorkspaceDirectoriesForProject(program);
		boolean isDBGenerationSuccess = generateDatabases(program);
		
		if (isDBGenerationSuccess) {
			ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(program);
			
			User currentUserCopy = this.currentUser.copy();
			addUserRecords(managerFactory, currentUserCopy);

			this.allRolesList = workbenchDataManager.getAllRoles();
			// save current user roles to the program
			List<ProjectUserRole> projectUserRoles = getCurrentUserRoles();
			if ((projectUserRoles != null) && (!projectUserRoles.isEmpty())) {
				saveProgramUserRoles(projectUserRoles, program);
			}
			// save user roles for the rest of program members
			List<ProjectUserRole> projectMembers = getProgamMemberUserRoles();
			if ((projectMembers != null) && (!projectMembers.isEmpty())) {
				saveProjectMembers(managerFactory.getUserDataManager(), projectMembers, program);
			}
			managerFactory.close();
			
			createMySQLAccounts(program);
			saveProjectUserInfo(program);
			createIBDBUserMap(program, currentUserCopy);
		}
		LOG.info("Program id:" + program.getProjectId() + " Name:" + program.getProjectName() + " " + program.getStartDate());
		LOG.info("Database generation successful? " + isDBGenerationSuccess);
	}

	private boolean generateDatabases(Project program) {
		boolean isDBGenerationSuccess;
		
		this.centralDbGenerator.setCropType(program.getCropType());
		isDBGenerationSuccess = this.centralDbGenerator.generateDatabase();

		this.localDbGenerator.setCropType(program.getCropType());
		this.localDbGenerator.setProjectId(program.getProjectId());
		isDBGenerationSuccess = this.localDbGenerator.generateDatabase();
		
		return isDBGenerationSuccess;
	}

	private void addUserRecords(ManagerFactory managerFactory, User currentUserCopy) throws MiddlewareQueryException {
		// create the project's local person and user data
		Person currentPerson = workbenchDataManager.getPersonById(currentUser.getUserid());
		Person currentPersonCopy = currentPerson.copy();
		// add the person to the project's local database
		managerFactory.getUserDataManager().addPerson(currentPersonCopy);

		// add the user, person to the central database if creating a new custom crop
		if (!centralDbGenerator.isAlreadyExists()) {
			currentPerson.setInstituteId(1);

			Person centralPerson = currentPerson.copy();
			centralPerson.setId(currentPerson.getId());
			managerFactory.getUserDataManager().addPersonToCentral(centralPerson);

			currentUserCopy.setAccess(PROJECT_USER_ACCESS_NUMBER_CENTRAL);
			currentUserCopy.setType(PROJECT_USER_TYPE_CENTRAL);
			currentUserCopy.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
			currentUserCopy.setAdate(getCurrentDate());
			currentUserCopy.setInstalid(1);
			managerFactory.getUserDataManager().addUserToCentral(currentUserCopy);
		}

		// add a user to project's local database
		String newUserName = currentPersonCopy.getInitialsWithTimestamp();
		// password should be 11 chars long only
		String newPassword = newUserName.substring(0, 11);
		currentUserCopy.setName(newUserName);
		currentUserCopy.setPassword(newPassword);
		currentUserCopy.setPersonid(currentPersonCopy.getId());
		currentUserCopy.setAccess(PROJECT_USER_ACCESS_NUMBER);
		currentUserCopy.setType(PROJECT_USER_TYPE);
		currentUserCopy.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
		currentUserCopy.setAdate(getCurrentDate());
		currentUserCopy.setInstalid(Integer.valueOf(-1));
		managerFactory.getUserDataManager().addUser(currentUserCopy);
		
		// add to map of project members
		this.idAndNameOfProgramMembers.put(currentUser.getUserid(), newUserName);
	}

	private void saveProjectUserInfo(Project program) {
		// Create records for workbench_project_user_info table
		for (Map.Entry<Integer, String> e : idAndNameOfProgramMembers.entrySet()) {
			try {
				if (workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(program.getProjectId().intValue(), e.getKey()) == null) {
					ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(), e.getKey());
					workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
				}
			} catch (MiddlewareQueryException e1) {
				// do nothing
			}
		}
	}

	private void saveBasicDetails(Project program) throws MiddlewareQueryException {
		program.setUserId(this.currentUser.getUserid());
		// TODO: REMOVE Once template is no longer required in Project
		CropType cropType = workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());
		if (cropType == null) {
			workbenchDataManager.addCropType(program.getCropType());
		}
		program.setTemplate(workbenchDataManager.getWorkflowTemplates().get(0));
		program.setLastOpenDate(null);
		workbenchDataManager.addProject(program);

		// set the project's local database name - only assignable after project is persisted because id is part of local db name.
		String localDatabaseName = program.getCropType().getLocalDatabaseNameWithProject(program);
		String centralDatabaseName = program.getCropType().getCentralDbName();
		program.setLocalDbName(localDatabaseName);
		program.setCentralDbName(centralDatabaseName);
		workbenchDataManager.saveOrUpdateProject(program);
	}
	
    /**
     * Create necessary database entries for each program member.
     *
     * @param userDataManager
     * @param projectUserRoles
     * @param project
     * @throws MiddlewareQueryException
     */
    private void saveProjectMembers(UserDataManager userDataManager, List<ProjectUserRole> projectUserRoles, Project project) throws MiddlewareQueryException {
        Map<Integer,String> usersAccountedFor = new HashMap<Integer, String>();
        for (ProjectUserRole projectUserRole : projectUserRoles){
            // Save role
            projectUserRole.setProject(project);

            //do not insert manager role, for some reason.. nageerror ng unique constraints
            workbenchDataManager.addProjectUserRole(projectUserRole);

            // Save User to local db
            //check if this user has already been accounted for, because each user may have many roles so this check is needed
            if(!usersAccountedFor.containsKey(projectUserRole.getUserId())){
                User workbenchUser = workbenchDataManager.getUserById(projectUserRole.getUserId());
                User localUser =  workbenchUser.copy();

                Person currentPerson = workbenchDataManager.getPersonById(workbenchUser.getPersonid());
                Person localPerson = currentPerson.copy();

                // Check if the Person record already exists
                if (!userDataManager.isPersonExists(localPerson.getFirstName().toUpperCase(), localPerson.getLastName().toUpperCase())){
                    userDataManager.addPerson(localPerson);
                } else {
                    // set localPerson to the existing person
                    List<Person> persons = userDataManager.getAllPersons();
                    for (Person person : persons){
                        if (person.getLastName().toUpperCase().equals(localPerson.getLastName().toUpperCase()) &&
                                person.getFirstName().toUpperCase().equals(localPerson.getFirstName().toUpperCase())){
                            localPerson = person;
                            break;
                        }
                    }
                }

                //append a timestamp to the username and password
                //and change the start of the username of be the initials of the user
                String newUserName = localPerson.getInitialsWithTimestamp();
                //password must be 11 chars long
                String newPassword = newUserName.substring(0, 11);

                localUser.setName(newUserName);
                localUser.setPassword(newPassword);

                // If the selected member does not exist yet in the local database, then add
                if (!userDataManager.isUsernameExists(localUser.getName())){
                    localUser.setPersonid(localPerson.getId());
                    localUser.setAccess(PROJECT_USER_ACCESS_NUMBER);
                    localUser.setType(PROJECT_USER_TYPE);
                    localUser.setInstalid(Integer.valueOf(-1));
                    localUser.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
                    localUser.setAdate(getCurrentDate());
                    Integer userId = userDataManager.addUser(localUser);
                    this.idAndNameOfProgramMembers.put(workbenchUser.getUserid(), newUserName);

                    // add a workbench user to ibdb user mapping
                    User ibdbUser = userDataManager.getUserById(userId);
                    IbdbUserMap ibdbUserMap = new IbdbUserMap();
                    ibdbUserMap.setWorkbenchUserId(workbenchUser.getUserid());
                    ibdbUserMap.setProjectId(project.getProjectId());
                    ibdbUserMap.setIbdbUserId(ibdbUser.getUserid());
                    workbenchDataManager.addIbdbUserMap(ibdbUserMap);

                }
                usersAccountedFor.put(projectUserRole.getUserId(), newUserName);
            }
        }
    }
    
    private void saveProgramUserRoles(List<ProjectUserRole> projectUserRoles, Project projectSaved) throws MiddlewareQueryException {
        for (ProjectUserRole projectUserRole : projectUserRoles){
            projectUserRole.setProject(projectSaved);
            projectUserRole.setUserId(this.currentUser.getUserid());
            workbenchDataManager.addProjectUserRole(projectUserRole);
        }
    }
    
	private void createIBDBUserMap(Project program, User currentUserCopy) throws MiddlewareQueryException {
		// add a workbench user to ibdb user mapping
		IbdbUserMap ibdbUserMap = new IbdbUserMap();
		ibdbUserMap.setWorkbenchUserId(currentUser.getUserid());
		ibdbUserMap.setProjectId(program.getProjectId());
		ibdbUserMap.setIbdbUserId(currentUserCopy.getUserid());
		workbenchDataManager.addIbdbUserMap(ibdbUserMap);
	}
	
	private void createMySQLAccounts(Project program) {
		// create mysql user accounts for members of the project
		MysqlAccountGenerator mysqlAccountGenerator =
				new MysqlAccountGenerator(program.getCropType(), program.getProjectId(), this.idAndNameOfProgramMembers, this.workbenchDataManager);
		mysqlAccountGenerator.generateMysqlAccounts();
	}
    
    private List<ProjectUserRole> getCurrentUserRoles() throws MiddlewareQueryException {
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        for (Role role : allRolesList) {
            ProjectUserRole projectUserRole = new ProjectUserRole();
            projectUserRole.setRole(role);
            projectUserRoles.add(projectUserRole);
        }
        return projectUserRoles;
    }

    private List<ProjectUserRole> getProgamMemberUserRoles() throws MiddlewareQueryException  {
    	List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        for (User user : selectedUsers) {
            // only retrieve selected members that's not the current user.
            if (user.getUserid().equals(currentUser.getUserid())) {
                continue;
            }
            for (Role role : allRolesList) {
                ProjectUserRole projectUserRole = new ProjectUserRole();
                projectUserRole.setUserId(user.getUserid());
                projectUserRole.setRole(role);

                projectUserRoles.add(projectUserRole);
            }
        }
        return projectUserRoles;
    }

    private Integer getCurrentDate(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = formatter.format(now.getTime());
        Integer dateNowInt = Integer.valueOf(dateNowStr);
        return dateNowInt;
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

	public void setCentralDbGenerator(IBDBGeneratorCentralDb centralDbGenerator) {
		this.centralDbGenerator = centralDbGenerator;
	}

	
	public void setLocalDbGenerator(IBDBGeneratorLocalDb localDbGenerator) {
		this.localDbGenerator = localDbGenerator;
	}
	
	void setManagerFactoryProvider(ManagerFactoryProvider managerFactoryProvider) {
		this.managerFactoryProvider = managerFactoryProvider;
	}
}

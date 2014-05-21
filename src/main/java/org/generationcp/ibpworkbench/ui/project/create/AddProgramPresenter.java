package org.generationcp.ibpworkbench.ui.project.create;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.database.IBDBGeneratorCentralDb;
import org.generationcp.ibpworkbench.database.IBDBGeneratorLocalDb;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cyrus on 5/19/14.
 */
@Configurable
public class AddProgramPresenter {
    private static final Logger LOG = LoggerFactory.getLogger(AddProgramPresenter.class);

    private static final int PROJECT_USER_ACCESS_NUMBER = 100;
    private static final int PROJECT_USER_TYPE = 422;
    private static final int PROJECT_USER_STATUS = 1;
    private static final int PROJECT_USER_ACCESS_NUMBER_CENTRAL = 150;
    private static final int PROJECT_USER_TYPE_CENTRAL = 420;

    private AddProgramView view;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SessionData sessionData;

    // data to save
    private Set<User> users;
    protected Project program;
    private Collection<Location> favoriteLocations;
    private Collection<Method> favoriteMethods;

    private List<Role> allRolesList;
    private final Map<Integer, String> idAndNameOfProgramMembers = new HashMap<Integer, String>();
    private int programUserInstalId = -1; // instalid of installation inserted, default value is -1

    public AddProgramPresenter(AddProgramView view) {
        this.view = view;

    }

    public boolean validateAndSave() {
        return validateAndSaveBasicDetails() && validateAndSaveProgramMembers();
    }

    public boolean validateAndSaveBasicDetails() {
        LOG.debug("Do validate basic details");
        try {
            //this.users = view.programMembersPanel.validateAndSave();
            this.program = view.createProjectPanel.projectBasicDetailsComponent.validateAndSave();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateAndSaveProgramMembers() {
        this.users = this.view.programMembersPanel.validateAndSave();

        return true;    // always allow.
    }

    public void enableProgramMethodsAndLocationsTab() {
        boolean isGenericCrop = true;


        try {
            for (CropType installedCrop : workbenchDataManager.getInstalledCentralCrops()) {
                if (program.getCropType().getCropName().equalsIgnoreCase(installedCrop.getCropName())) {
                    isGenericCrop = false;

                    break;
                }
            }
        } catch (MiddlewareQueryException e) {
            LOG.error("Error in enableProgramMethodsAndLocationsTab()",e);
            return;
        } finally {
            if (isGenericCrop)
                view.enableFinishBtn();
            else
                view.enableOptionalTabsAndFinish(program.getCropType());
        }
    }

    public void retrievceLocationsAndMethods() {
        this.favoriteLocations = view.getFavoriteLocations();
        this.favoriteMethods = view.getFavoriteMethods();
    }

    public void disableProgramMethodsAndLocationsTab() { view.disableOptionalTabsAndFinish(); }

    public void doAddNewProgram() throws Exception {

        // Validate and Save (to memory) both basic details and program members

        if (!validateAndSave())
            throw new Exception("basic_details_invalid");

        // retrieve results from locations / methods
        retrievceLocationsAndMethods();

        boolean isGenerationSuccess = false;
        boolean isMysqlAccountGenerationSuccess = false;

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

        program.setUserId(app.getSessionData().getUserData().getUserid());


        //TODO: REMOVE Once template is no longer required in Project
        CropType cropType = workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());
        if(cropType == null) {
            workbenchDataManager.addCropType(program.getCropType());
        }
        program.setTemplate(workbenchDataManager.getWorkflowTemplates().get(0));

        program.setLastOpenDate(null);
        workbenchDataManager.addProject(program);

        // set the project's local database name
        String localDatabaseName = program.getCropType().getLocalDatabaseNameWithProject(program);
        String centralDatabaseName = program.getCropType().getCentralDbName();
        program.setLocalDbName(localDatabaseName);
        program.setCentralDbName(centralDatabaseName);

        workbenchDataManager.saveOrUpdateProject(program);
        // create the project's workspace directories
        toolUtil.createWorkspaceDirectoriesForProject(program);

        //create central database
        IBDBGeneratorCentralDb centralDbGenerator;
        centralDbGenerator = new IBDBGeneratorCentralDb(program.getCropType());
        isGenerationSuccess = centralDbGenerator.generateDatabase();

        IBDBGeneratorLocalDb generator;
        generator = new IBDBGeneratorLocalDb(program.getCropType(), program.getProjectId());
        isGenerationSuccess = generator.generateDatabase();


        if (isGenerationSuccess) {
            //generator.addCachedLocations(app.getSessionData().getProjectLocationData());
            //generator.addCachedBreedingMethods(app.getSessionData().getProjectBreedingMethodData());

            User currentUser = app.getSessionData().getUserData();
            User user = currentUser.copy();

                ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(program);

                // create the project's local person and user data
                Person currentPerson = workbenchDataManager.getPersonById(currentUser.getUserid());
                Person person = currentPerson.copy();

                // add the person to the project's local database
                managerFactory.getUserDataManager().addPerson(person);

                // add the user ,person and instln to the central database if creating a new custom crop
                if (!centralDbGenerator.isAlreadyExists()){
                    currentPerson.setInstituteId(1);

                    Person centralPerson = currentPerson.copy();
                    centralPerson.setId(currentPerson.getId());
                    managerFactory.getUserDataManager().addPersonToCentral(centralPerson);

                    currentUser.setAccess(PROJECT_USER_ACCESS_NUMBER_CENTRAL);
                    currentUser.setType(PROJECT_USER_TYPE_CENTRAL);
                    currentUser.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
                    currentUser.setAdate(getCurrentDate());
                    currentUser.setInstalid(1);
                    managerFactory.getUserDataManager().addUserToCentral(currentUser);
                    centralDbGenerator.addCentralInstallationRecord(program.getProjectName(), currentUser.getUserid());
                }

                // add a user to project's local database
                String newUserName = person.getInitialsWithTimestamp();
                //password should be 11 chars long only
                String newPassword = newUserName.substring(0, 11);

                user.setName(newUserName);
                user.setPassword(newPassword);
                user.setPersonid(person.getId());
                user.setAccess(PROJECT_USER_ACCESS_NUMBER);
                user.setType(PROJECT_USER_TYPE);
                user.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
                user.setAdate(getCurrentDate());
                int localUserId = managerFactory.getUserDataManager().addUser(user);
                //add to map of project members
                this.idAndNameOfProgramMembers.put(currentUser.getUserid(), newUserName);

                // Add the installation record in the local db with the given project name and the newly added local user
                programUserInstalId = generator.addLocalInstallationRecord(program.getProjectName(), localUserId);

                // Set the instalId of the local user
                user.setInstalid(Integer.valueOf(programUserInstalId));
                managerFactory.getUserDataManager().updateUser(user);


                // save current user roles to the program
                List<ProjectUserRole> projectUserRoles = this.getCurrentUserRoles();

                if ((projectUserRoles != null) && (!projectUserRoles.isEmpty())) {
                    saveProgramUserRoles(projectUserRoles, program);
                }

                // save user roles for the rest of program members
                List<ProjectUserRole> projectMembers = this.getProgamMemberUserRoles();

                if ((projectMembers != null) && (!projectMembers.isEmpty())) {
                    saveProjectMembers(managerFactory.getUserDataManager(), projectMembers, program);
                }

                // save locations and methods
                saveProjectLocation(managerFactory,favoriteLocations,program);
                saveProjectMethods(managerFactory,favoriteMethods,program);

                managerFactory.close();

            // create mysql user accounts for members of the project
            if(isGenerationSuccess){
                Set<User> projectMembersSet = new HashSet<User>();
                projectMembersSet.add(currentUser);

                for(ProjectUserRole projectUserRole : projectUserRoles){
                    try{
                        User member = this.workbenchDataManager.getUserById(projectUserRole.getUserId());
                        projectMembersSet.add(member);
                    } catch(MiddlewareQueryException ex) {
                        //do nothing because getting the User will not fail
                    }
                }

                MysqlAccountGenerator mysqlAccountGenerator = new MysqlAccountGenerator(program.getCropType(), program.getProjectId(),
                        this.idAndNameOfProgramMembers, this.workbenchDataManager);

                isMysqlAccountGenerationSuccess = mysqlAccountGenerator.generateMysqlAccounts();

            }


            //Create records for workbench_project_user_info table
            for (Map.Entry<Integer, String> e : idAndNameOfProgramMembers.entrySet()){


                try {
                    if (workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(program.getProjectId().intValue(),  e.getKey())==null){
                        ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(),  e.getKey());
                        workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
                    }
                } catch (MiddlewareQueryException e1) {
                    // do nothing
                }

            }

            // add a workbench user to ibdb user mapping
            IbdbUserMap ibdbUserMap = new IbdbUserMap();
            ibdbUserMap.setWorkbenchUserId(currentUser.getUserid());
            ibdbUserMap.setProjectId(program.getProjectId());
            ibdbUserMap.setIbdbUserId(user.getUserid());
            workbenchDataManager.addIbdbUserMap(ibdbUserMap);

        }


        LOG.info(program.getProjectId() + "  " + program.getProjectName() + " " + program.getStartDate() + " "
                + program.getTemplate().getTemplateId());
        LOG.info("IBDB Local Generation Successful?: " + isGenerationSuccess);
        LOG.info("Mysql Accounts Generation Successful?: " + isMysqlAccountGenerationSuccess);
    }

    private List<ProjectUserRole> getCurrentUserRoles() throws MiddlewareQueryException {
        if (allRolesList == null)
            allRolesList = workbenchDataManager.getAllRoles();

        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();

        for (Role role : allRolesList) {
            ProjectUserRole projectUserRole = new ProjectUserRole();
            projectUserRole.setRole(role);

            projectUserRoles.add(projectUserRole);
        }

        return projectUserRoles;
    }

    private List<ProjectUserRole> getProgamMemberUserRoles() throws MiddlewareQueryException  {
        if (allRolesList == null)
            allRolesList = workbenchDataManager.getAllRoles();

        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();

        for (User user : users) {
            // only retrieve selected members that's not the current user.
            if (user.getUserid().equals(sessionData.getUserData().getUserid()))
                continue;

            for (Role role : allRolesList) {
                ProjectUserRole projectUserRole = new ProjectUserRole();
                projectUserRole.setUserId(user.getUserid());
                projectUserRole.setRole(role);

                projectUserRoles.add(projectUserRole);
            }
        }
        return projectUserRoles;
    }


    public void resetBasicDetails() {
        view.resetBasicDetails();
    }

    public void resetProgramMembers() {
        view.resetProgramMembers();
    }

    private void saveProgramUserRoles(List<ProjectUserRole> projectUserRoles, Project projectSaved) throws MiddlewareQueryException {

        if (allRolesList == null)
            allRolesList = workbenchDataManager.getAllRoles();

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        Integer userId = app.getSessionData().getUserData().getUserid();

        for (ProjectUserRole projectUserRole : projectUserRoles){
            projectUserRole.setProject(projectSaved);
            projectUserRole.setUserId(userId);

            workbenchDataManager.addProjectUserRole(projectUserRole);
        }

    }

    private void saveProjectMethods(ManagerFactory managerFactory, Collection<Method> methods, Project projectSaved) throws MiddlewareQueryException {

        List<ProjectMethod> projectMethodList = new ArrayList<ProjectMethod>();
        int mID = 0;
        for (Method m : methods) {
            ProjectMethod projectMethod = new ProjectMethod();
            if(m.getMid() < 1){
                //save the added  method to the local database created
                mID = managerFactory.getGermplasmDataManager().addMethod(new Method(m.getMid(), m.getMtype(), m.getMgrp(),
                        m.getMcode(), m.getMname(), m.getMdesc(),0, 0, 0,0, 0,0, 0, 0));
            }else{
                mID=m.getMid();
            }
            projectMethod.setMethodId(mID);
            projectMethod.setProject(projectSaved);
            projectMethodList.add(projectMethod);
        }
        workbenchDataManager.addProjectMethod(projectMethodList);

    }

    private void saveProjectLocation(ManagerFactory managerFactory, Collection<Location> locations, Project projectSaved) throws MiddlewareQueryException {

        List<ProjectLocationMap> projectLocationMapList = new ArrayList<ProjectLocationMap>();
        long locID=0;
        for (Location l : locations) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
            if(l.getLocid() < 1){
                //save the added new location to the local database created
                Location location = new Location();
                location.setLocid(l.getLocid());
                location.setCntryid(0);
                location.setLabbr(l.getLabbr());
                location.setLname(l.getLname());
                location.setLrplce(0);
                location.setLtype(0);
                location.setNllp(0);
                location.setSnl1id(0);
                location.setSnl2id(0);
                location.setSnl3id(0);

                locID= managerFactory.getLocationDataManager().addLocation(location);
            }else{
                locID=l.getLocid();
            }
            projectLocationMap.setLocationId(locID);
            projectLocationMap.setProject(projectSaved);
            projectLocationMapList.add(projectLocationMap);
        }

        workbenchDataManager.addProjectLocationMap(projectLocationMapList);
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
            //  if(!projectUserRole.getRole().getName().equalsIgnoreCase(Role.MANAGER_ROLE_NAME))
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
                    localUser.setInstalid(Integer.valueOf(programUserInstalId));
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

    private Integer getCurrentDate(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = formatter.format(now.getTime());
        Integer dateNowInt = Integer.valueOf(dateNowStr);
        return dateNowInt;

    }
}

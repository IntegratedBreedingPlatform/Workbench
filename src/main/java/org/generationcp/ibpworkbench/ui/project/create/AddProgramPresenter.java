package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.data.Validator;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.database.IBDBGeneratorCentralDb;
import org.generationcp.ibpworkbench.database.IBDBGeneratorLocalDb;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
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
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    // data to save
    private Set<User> users;
    private Project program;
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
        } catch (Validator.InvalidValueException e) {
            return false;
        }
    }

    public boolean validateAndSaveProgramMembers() {
        this.users = this.view.programMembersPanel.validateAndSave();

        return true;    // always allow.
    }

    public void enableProgramMethodsAndLocationsTab() {
        boolean isGenericCrop = true;
        for (CropType.CropEnum cropEnum : CropType.CropEnum.values()) {
            if (program.getCropType().getCropName().equalsIgnoreCase(cropEnum.toString())) {
                isGenericCrop = false;

                break;
            }
        }

        if (isGenericCrop)
            view.enableFinishBtn();
        else
            view.enableOptionalTabsAndFinish(program.getCropType());
    }

    public void retrievceLocationsAndMethods() {
        this.favoriteLocations = view.getFavoriteLocations();
        this.favoriteMethods = view.getFavoriteMethods();
    }

    public void disableProgramMethodsAndLocationsTab() { view.disableOptionalTabsAndFinish(); }

    public void doAddNewProgram() throws Exception {

        // Validate and Save (to memory) both basic details and program members

        if (!validateAndSave())
            return;

        // retrieve results from locations / methods
        retrievceLocationsAndMethods();


        // program and member objects should be filled by now

        program.setUserId(sessionData.getUserData().getUserid());


        //TODO: REMOVE Once template is no longer required in Program
        CropType cropType = workbenchDataManager.getCropTypeByName(program.getCropType().getCropName());

        // must be a generic crop
        if (cropType == null) {
            workbenchDataManager.addCropType(program.getCropType());    // add cropType to DB
        }
        program.setTemplate(workbenchDataManager.getWorkflowTemplates().get(0));

        // Add program item to Database
        workbenchDataManager.addProject(program);

        // initialize local and central db name
        program.setLocalDbName(program.getCropType().getLocalDatabaseNameWithProject(program));
        program.setCentralDbName(program.getCropType().getCentralDbName());

        // update the program
        workbenchDataManager.saveOrUpdateProject(program);

        // create program directories
        toolUtil.createWorkspaceDirectoriesForProject(program);


        // create central (if generic crop) + local databases database
        IBDBGeneratorCentralDb centralDBGenerator = new IBDBGeneratorCentralDb(program.getCropType());
        IBDBGeneratorLocalDb localDBGenerator = new IBDBGeneratorLocalDb(program.getCropType(), program.getProjectId());

        if (!centralDBGenerator.generateDatabase())
             throw new Exception("Failed to generate central database");
        if (!localDBGenerator.generateDatabase())
            throw new Exception("Failed to generate local database");

        // successful generation! now create users + person data
        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(program);
        UserDataManager udm = managerFactory.getUserDataManager();

        User currentUser = sessionData.getUserData().copy();
        currentUser.setUserid(sessionData.getUserData().getUserid());
        Person currentPerson = workbenchDataManager.getPersonById(sessionData.getUserData().getUserid());
        // add the person to program's local database
        udm.addPerson(currentPerson);

        // add user, person and instln to central on generic crop
        if (!centralDBGenerator.isAlreadyExists()) {
            currentPerson.setInstituteId(1);

            Person centralPerson = currentPerson.copy();
            centralPerson.setId(currentPerson.getId());
            udm.addPersonToCentral(centralPerson);

            currentUser.setAccess(PROJECT_USER_ACCESS_NUMBER_CENTRAL);
            currentUser.setType(PROJECT_USER_TYPE_CENTRAL);
            currentUser.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
            currentUser.setAdate(getCurrentDate());
            currentUser.setInstalid(1);

            udm.addUserToCentral(currentUser);
            centralDBGenerator.addCentralInstallationRecord(program.getProjectName(),currentUser.getUserid());
        }

        // add the user to program's local db
        Person currentPersonCopy = currentPerson.copy();
        currentPersonCopy.setId(currentPerson.getId());

        User user = sessionData.getUserData().copy();
        user.setUserid(sessionData.getUserData().getUserid());

        String newUsername = currentPersonCopy.getInitialsWithTimestamp();
        String newPassword = newUsername.substring(0,11);
        user.setName(newUsername);
        user.setPassword(newPassword);
        user.setPersonid(currentPersonCopy.getId());
        user.setAccess(PROJECT_USER_ACCESS_NUMBER);
        user.setType(PROJECT_USER_TYPE);
        user.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
        user.setAdate(getCurrentDate());

        int localUserId = udm.addUser(user);

        // add to map of program members
        this.idAndNameOfProgramMembers.put(user.getUserid(), newUsername);

        // Add the installation record in the local db with the given program name and the newly added local user
        programUserInstalId = localDBGenerator.addLocalInstallationRecord(program.getProjectName(), localUserId);

        // Set the instalId of the local user
        user.setInstalid(Integer.valueOf(programUserInstalId));
        udm.updateUser(user);

        // save logic for program members

        // get all program roles
        List<ProjectUserRole> programUserRoles = new ArrayList<ProjectUserRole>();
        // BY DEFAULT, current user has all the roles

        if (allRolesList == null)
            allRolesList = workbenchDataManager.getAllRoles();

        for (Role role : allRolesList) {
            ProjectUserRole programUserRole = new ProjectUserRole();
            programUserRole.setRole(role);
            programUserRoles.add(programUserRole);
        }

        List<ProjectUserRole> projectUserRoles = this.getProgamMemberUserRoles();
        if (projectUserRoles != null && !projectUserRoles.isEmpty()) {
            saveProjectMembers(udm,projectUserRoles,program);
        }

        // create mysql user accounts for members
        Set<User> programMembers = new HashSet<User>();
        programMembers.add(sessionData.getUserData());  // add current user
        for (ProjectUserRole projectUserRole : programUserRoles) {
            programMembers.add(this.workbenchDataManager.getUserById(projectUserRole.getUserId()));
        }

        MysqlAccountGenerator mysqlAccountGenerator = new MysqlAccountGenerator(program.getCropType(), program.getProjectId(),
                this.idAndNameOfProgramMembers, this.workbenchDataManager);

        // generate mysql
        if (!mysqlAccountGenerator.generateMysqlAccounts())
            throw new Exception("failed to generate Mysql Accounts");

        // Update workbencg project summary info
        for (Map.Entry<Integer, String> e : idAndNameOfProgramMembers.entrySet()){
            if (workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(program.getProjectId().intValue(),  e.getKey())==null){
                ProjectUserInfo pUserInfo = new ProjectUserInfo(program.getProjectId().intValue(),  e.getKey());
                workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo);
            }
        }

        // save logic for program location
        //saveProjectLocation(managerFactory,favoriteLocations,program);

        // save logic for program methods
        //saveProjectMethods(managerFactory,favoriteMethods,program);

        managerFactory.close();
    }

    private List<ProjectUserRole> getProgamMemberUserRoles() throws MiddlewareQueryException  {
        if (allRolesList == null)
            allRolesList = workbenchDataManager.getAllRoles();

        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();

        User currentUser = null;



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

    @Deprecated
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

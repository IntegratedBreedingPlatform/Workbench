/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class SaveUsersInProjectAction implements ClickListener{

    private static final Logger LOG = LoggerFactory.getLogger(SaveUsersInProjectAction.class);
    private static final long serialVersionUID = 1L;
    private static final int PROJECT_USER_ACCESS_NUMBER = 100;
    private static final int PROJECT_USER_TYPE = 422;
    private static final int PROJECT_USER_STATUS = 1;
    
    private int projectUserInstalId = -1; // instalid of installation inserted, default value is -1 
    
    private Project project;
    
    private Table tblMembers;
    
    private Map<Integer, String> idAndNameOfProjectMembers;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private List<ProjectUserRole> projectUserRoles;
    private ManagerFactory managerFactory;
    private  List<ProjectUserRole> projectMembers;
    
    public SaveUsersInProjectAction(Project project, List<ProjectUserRole> projectUserRoles,  List<ProjectUserRole> projectMembers ) {
        this.project = project;
        this.projectUserRoles = projectUserRoles;
        this.projectMembers = projectMembers;
    }
    
    public SaveUsersInProjectAction(Project project,  Table tblMembers ) {
        this.project = project;
        this.tblMembers = tblMembers;
    }

    public boolean validate(){
        return true;
    }

    
    @Override
    public void buttonClick(ClickEvent event) {

    	boolean isMysqlAccountGenerationSuccess = false;
    	System.out.println("2project is : "+ this.project.getProjectName());
    	if (this.project != null) {
                //generator.addCachedLocations(app.getSessionData().getProjectLocationData());
                //generator.addCachedBreedingMethods(app.getSessionData().getProjectBreedingMethodData());
    			System.out.println("1project is : "+ this.project.getProjectName());
                /*
    			try {
                     	//creates the access for the local db        	   
                        managerFactory = managerFactoryProvider.getManagerFactoryForProject(this.project);
                    	System.out.println("3project is : "+ this.project.getProjectName());
                    //    List<ProjectUserRole> projectUserRoles = createProjectPanel.getProjectUserRoles();
                        if ((projectUserRoles != null) && (!projectUserRoles.isEmpty())) {
                            saveProjectUserRoles(projectUserRoles, this.project);
                        }
                       
                      //  List<ProjectUserRole> projectMembers = createProjectPanel.getProjectMembers();
                        if ((projectMembers != null) && (!projectMembers.isEmpty())) {
                            saveProjectMembers(projectMembers, this.project);
                        }

                    } catch (MiddlewareQueryException e) {
                        LOG.error(e.getMessage(), e);
                        MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                                + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                        //TODO cleanup of records already saved for the project needed
                        //return;
                    }

				*/
                // create mysql user accounts for members of the project
                if(this.project != null){
                    Set<User> projectMembers = new HashSet<User>();
                    
                    //delete all existing first
                    try{
                    	 this.workbenchDataManager.updateProjectsRolesForProject(this.project, getProjectMembers());
                    	 event.getComponent().getWindow().showNotification("Added Members");
                    } catch(MiddlewareQueryException ex) {
                        //do nothing because getting the User will not fail
                    	 event.getComponent().getWindow().showNotification("Failed to add members");
                    }
                    
                    }
                    
                   
                }
                // add a workbench user to ibdb user mapping
               
        }
        

    

    

    private void saveProjectUserRoles(List<ProjectUserRole> projectUserRoles, Project projectSaved) throws MiddlewareQueryException {

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        Integer userId = app.getSessionData().getUserData().getUserid();

        for (ProjectUserRole projectUserRole : projectUserRoles){
            projectUserRole.setProject(projectSaved);
            projectUserRole.setUserId(userId);
            workbenchDataManager.addProjectUserRole(projectUserRole);
        }

    }

    private void saveProjectMembers(List<ProjectUserRole> projectUserRoles, Project projectSaved) throws MiddlewareQueryException {
        
        UserDataManager userDataManager = managerFactory.getUserDataManager();
        Map<Integer,String> usersAccountedFor = new HashMap<Integer, String>();
        
        for (ProjectUserRole projectUserRole : projectUserRoles){
            
            // Save role
            projectUserRole.setProject(projectSaved);
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
                    localUser.setInstalid(Integer.valueOf(projectUserInstalId));
                    localUser.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
                    localUser.setAdate(getCurrentDate());
                    Integer userId = userDataManager.addUser(localUser);      
                    this.idAndNameOfProjectMembers.put(workbenchUser.getUserid(), newUserName);

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
    
    public List<ProjectUserRole> getProjectMembers() {
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        
        System.out.println("getProjectMembers");
        
        Container container = tblMembers.getContainerDataSource();
        Collection<User> userList = (Collection<User>) container.getItemIds();
        
        List<Role> roleList = null;
        try {
            roleList = workbenchDataManager.getAllRoles();
        }
        catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting workbench roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                                                   Message.CONTACT_ADMIN_ERROR_DESC);
        }
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        User currentUser = app.getSessionData().getUserData();
        
        
        try {
        	List<Role> roles = null;
            roles = workbenchDataManager.getAllRolesOrderedByLabel();
            
            for (Role role : roles) {
                CheckBox cb = new CheckBox(role.getName());
                cb.setData(role.getRoleId());
                if (role.getName().equals(Role.MANAGER_ROLE_NAME)) {
                    //set default checked value
                	ProjectUserRole currentProjectUserRole = new ProjectUserRole();
                    currentProjectUserRole.setUserId(currentUser.getUserid());
                    currentProjectUserRole.setRole(role);
                    System.out.println("getProjectMembers name "+ currentUser.getName());
                  //  projectUserRoles.add(currentProjectUserRole);
                }
                
            }
        } catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

       
           
        
        
        
        
        for (User user : userList) {
            Item item = container.getItem(user);
            
            for (Role role : roleList) {
                String propertyId = "role_" + role.getRoleId();
                Property property = item.getItemProperty(propertyId);
                Boolean value = (Boolean) property.getValue();
                
                if (value != null && value.booleanValue()) {
                    ProjectUserRole projectUserRole = new ProjectUserRole();
                    projectUserRole.setUserId(user.getUserid());
                    projectUserRole.setRole(role);
                    System.out.println("getProjectMembers name "+ user.getName());
                    
                    projectUserRoles.add(projectUserRole);
                }
            }
        }
        
        for(ProjectUserRole pur: projectUserRoles)
        {
        	System.out.println("pur " + pur);
        }
        
        return projectUserRoles;
    }
    
    public List<ProjectUserRole> getProjectUserRoles() {
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        System.out.println("getProjectUserRoles");
        for (CheckBox cb : createUserRolesCheckBoxList()) {
            if ((Boolean) cb.getValue() == true) {
                Role role;
                try {
                    role = workbenchDataManager.getRoleById((Integer) cb.getData());
                    ProjectUserRole projectUserRole = new ProjectUserRole();
                    projectUserRole.setRole(role);
                    
                    projectUserRoles.add(projectUserRole);
                } catch (MiddlewareQueryException e) {
                  LOG.error("Error encountered while getting project user roles", e);
                  throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }
        }
        return projectUserRoles;

    }
    
    private List<CheckBox> createUserRolesCheckBoxList() {
        List<Role> roles = null;
        List<CheckBox> rolesCheckBoxList = new ArrayList<CheckBox>();
        
        System.out.println("createUserRolesCheckBoxList");
        
        try {
            roles = workbenchDataManager.getAllRolesOrderedByLabel();
        } catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        for (Role role : roles) {
            CheckBox cb = new CheckBox(role.getName());
            cb.setData(role.getRoleId());
            if (role.getName().equals(Role.MANAGER_ROLE_NAME)) {
                //set default checked value
                cb.setValue(true);
            }
            cb.setCaption(role.getLabel());
            rolesCheckBoxList.add(cb);

        }

        return rolesCheckBoxList;

    }
}

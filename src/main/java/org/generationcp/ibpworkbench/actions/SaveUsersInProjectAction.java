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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;

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
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public SaveUsersInProjectAction(Project project,  Table tblMembers ) {
        this.project = project;
        this.tblMembers = tblMembers;
    }

    public boolean validate(){
        return true;
    }

    
    @Override
    public void buttonClick(ClickEvent event) {
        if (project == null) {
            return;
        }
        
        Container container = tblMembers.getContainerDataSource();
        
        @SuppressWarnings("unchecked")
        Collection<User> userList = (Collection<User>) container.getItemIds();
        
        try {
        	
        	//get the members/user_roles who are not yet added in the database
        	for (User u : userList){
        		List<ProjectUserRole> list = workbenchDataManager.getProjectUserRolesByProject(project);
        		Boolean urole_exists = false;
        		for (ProjectUserRole urole : list){
        			if (urole.getUserId().equals(u.getUserid())) urole_exists = true;	
        		}
        		 
        		if (!urole_exists
        				&& ((Boolean) container.getItem(u).getItemProperty("role_1").getValue()
        				|| (Boolean) container.getItem(u).getItemProperty("role_2").getValue()
        				|| (Boolean) container.getItem(u).getItemProperty("role_3").getValue()
        				|| (Boolean) container.getItem(u).getItemProperty("role_4").getValue()
        				|| (Boolean) container.getItem(u).getItemProperty("role_5").getValue())
        				
        				){
	        		try {
	        			 	IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
	        			 	User user = app.getSessionData().getUserData();
	        			 	ProjectActivity projAct = new ProjectActivity(new Integer(project.getProjectId().intValue()), project, "member", "Added a workbench member (" + u.getName() + ") to the project", user, new Date());
	                        workbenchDataManager.addProjectActivity(projAct);
	 
	                 }
	                 catch (MiddlewareQueryException e) {
	                     LOG.error("Cannot log project activity", e);
	                 }
        		}
        	}
        			
        	
            List<ProjectUserRole> projectUserRoles = getProjectMembers();
            
            // update the project user roles
            this.workbenchDataManager.updateProjectsRolesForProject(this.project, projectUserRoles);
            
            // create the MySQL users for each project member
            // TODO: why do we need to create a MySQL for each project member?
            // why not create a MySQL user for the Workbench user when the account is created?
            createMySQLUsers(projectUserRoles);
            
            // create local database users for each workbench user
            ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
            createLocalDatabaseUsers(managerFactory, projectUserRoles, project);
            
            event.getComponent().getWindow().showNotification("Successfully updated this project's members list.");
        } catch(MiddlewareQueryException ex) {
            //do nothing because getting the User will not fail
            event.getComponent().getWindow().showNotification("A database problem occured while updating this project's members list. Please contact your administrator.");
        }
        
        try{
     	  
            for (User u : userList){
         	  if (workbenchDataManager.getProjectUserInfoDao().getByProjectIdAndUserId(project.getProjectId().intValue(), u.getUserid()) == null) {
         		  ProjectUserInfo pUserInfo = new ProjectUserInfo(project.getProjectId().intValue(),u.getUserid());
         		  workbenchDataManager.saveOrUpdateProjectUserInfo(pUserInfo); 
                 
         	  }
            }
     
	     }catch(Exception e){
	     	System.out.println(e.getMessage());
	     	
	     }
    }
    
    /**
     * Create a local database user for each workbench user.
     * 
     * @param managerFactory
     * @param projectUserRoles
     * @param projectSaved
     * @throws MiddlewareQueryException
     */
    private void createLocalDatabaseUsers(ManagerFactory managerFactory, List<ProjectUserRole> projectUserRoles, Project projectSaved) throws MiddlewareQueryException {
        UserDataManager userDataManager = managerFactory.getUserDataManager();
        Map<Integer,String> usersAccountedFor = new HashMap<Integer, String>();
        
        for (ProjectUserRole projectUserRole : projectUserRoles){
            Integer workbenchUserId = projectUserRole.getUserId();
            
            // a user may have multiple roles
            // and we only need to create 1 local user per workbench user
            if(usersAccountedFor.containsKey(workbenchUserId)){
                continue;
            }
            
            User workbenchUser = workbenchDataManager.getUserById(workbenchUserId);
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
            
            ProjectUserMysqlAccount userMysqlAccount = workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(project.getProjectId().intValue(), workbenchUser.getUserid());
            if (userMysqlAccount == null) {
                // this probably won't happen because we create MySQL accounts for each user
                continue;
            }

            // use the MySQL username and password as username/password for the new local database user
            localUser.setName(userMysqlAccount.getMysqlUsername());
            localUser.setPassword(userMysqlAccount.getMysqlPassword());

            // If the selected member does not exist yet in the local database, then add
            User localDatabaseUser = userDataManager.getUserByUserName(localUser.getName());
            Integer userId = localDatabaseUser == null ? null : localDatabaseUser.getUserid();
            
            if (userId != null) {
                continue;
            }
            
            localUser.setPersonid(localPerson.getId());
            localUser.setAccess(PROJECT_USER_ACCESS_NUMBER);
            localUser.setType(PROJECT_USER_TYPE);
            localUser.setInstalid(Integer.valueOf(projectUserInstalId));
            localUser.setStatus(Integer.valueOf(PROJECT_USER_STATUS));
            localUser.setAdate(getCurrentDate());
            userId = userDataManager.addUser(localUser);      

            // add a workbench user to ibdb user mapping
            User ibdbUser = userDataManager.getUserById(userId);
            IbdbUserMap ibdbUserMap = new IbdbUserMap();
            ibdbUserMap.setWorkbenchUserId(workbenchUser.getUserid());
            ibdbUserMap.setProjectId(project.getProjectId());
            ibdbUserMap.setIbdbUserId(ibdbUser.getUserid());
            workbenchDataManager.addIbdbUserMap(ibdbUserMap);
            
            usersAccountedFor.put(projectUserRole.getUserId(), localUser.getName());
        }
    }
    
    protected void createMySQLUsers(List<ProjectUserRole> projectUserRoles) {
        if (project.getProjectId() == null) {
            return;
        }
        
        Map<Integer, String> idAndNameOfProjectMembers = new HashMap<Integer, String>();
        
        for(ProjectUserRole projectUserRole : projectUserRoles){
            try{
                User member = this.workbenchDataManager.getUserById(projectUserRole.getUserId());
                if (member == null) {
                    continue;
                }
                
                Person person = workbenchDataManager.getPersonById(member.getPersonid());
                if (person == null) {
                    continue;
                }
                
                ProjectUserMysqlAccount userMysqlAccount = workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(project.getProjectId().intValue(), member.getUserid());
                if (userMysqlAccount == null) {
                    // we need to create a MySQL account for this user
                    idAndNameOfProjectMembers.put(member.getUserid(), person.getInitialsWithTimestamp());
                }
            } catch(MiddlewareQueryException ex) {
                //do nothing because getting the User will not fail
            }
        }
        
        MysqlAccountGenerator mysqlAccountGenerator = new MysqlAccountGenerator(this.project.getCropType(), this.project.getProjectId(), 
                idAndNameOfProjectMembers, this.workbenchDataManager);
        
        mysqlAccountGenerator.generateMysqlAccounts();
    }

    private Integer getCurrentDate(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateNowStr = formatter.format(now.getTime());
        Integer dateNowInt = Integer.valueOf(dateNowStr);
        return dateNowInt;
    }
    
    protected List<ProjectUserRole> getProjectMembers() {
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        
        Container container = tblMembers.getContainerDataSource();
        
        @SuppressWarnings("unchecked")
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
                    
                    projectUserRoles.add(projectUserRole);
                }
            }
        }
        
        return projectUserRoles;
    }
}

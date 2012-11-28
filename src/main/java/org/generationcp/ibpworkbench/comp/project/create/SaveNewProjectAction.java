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

package org.generationcp.ibpworkbench.comp.project.create;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.database.IBDBGenerator;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.IbdbUserMap;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.ProjectMethod;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class SaveNewProjectAction implements ClickListener{

    private static final Logger LOG = LoggerFactory.getLogger(SaveNewProjectAction.class);
    private static final long serialVersionUID = 1L;
    private static final int PROJECT_USER_ACCESS_NUMBER = 100;
    private static final int PROJECT_USER_TYPE = 422;
    

    private CreateProjectPanel createProjectPanel;
    private Project project;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    private Project projectSaved;
    private ManagerFactory managerFactory;
    
    public SaveNewProjectAction(CreateProjectPanel createProjectPanel) {
        this.createProjectPanel = createProjectPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {

        boolean validProjectValues = createProjectPanel.validate();

        if (validProjectValues) {

            project = createProjectPanel.getProject();

            boolean isGenerationSuccess = false;

            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

            project.setUserId(app.getSessionData().getUserData().getUserid());

            try {
                //TODO: REMOVE Once template is no longer required in Project
                project.setTemplate(workbenchDataManager.getWorkflowTemplates().get(0));

                project.setLastOpenDate(null);
                projectSaved = workbenchDataManager.saveOrUpdateProject(project);

                // create the project's workspace directories
                toolUtil.createWorkspaceDirectoriesForProject(projectSaved);
            } catch (MiddlewareQueryException e) {
                LOG.error(e.getMessage());
            }

            IBDBGenerator generator;

            try {
                generator = new IBDBGenerator(project.getCropType(), project.getProjectId());
                isGenerationSuccess = generator.generateDatabase();
            } catch (InternationalizableException e) {
                LOG.error(e.toString(), e);
                MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
                return;
            }

            if (isGenerationSuccess) {
                //generator.addCachedLocations(app.getSessionData().getProjectLocationData());
                //generator.addCachedBreedingMethods(app.getSessionData().getProjectBreedingMethodData());

                User currentUser = app.getSessionData().getUserData();
                User user = currentUser.copy();

                try {

                    managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
                    
                    // create the project's local person and user data
                    Person currentPerson = workbenchDataManager.getPersonById(currentUser.getUserid());
                    Person person = currentPerson.copy();

                    // add the person to the project's local database
                    managerFactory.getUserDataManager().addPerson(person);

                    // add a user to project's local database
                    user.setPersonid(person.getId());
                    user.setAccess(PROJECT_USER_ACCESS_NUMBER);
                    user.setType(PROJECT_USER_TYPE);
                    user.setAdate(getCurrentDate());
                    managerFactory.getUserDataManager().addUser(user);

                    List<ProjectUserRole> projectUserRoles = createProjectPanel.getProjectUserRoles();
                    if ((projectUserRoles != null) && (!projectUserRoles.isEmpty())) {
                        saveProjectUserRoles(projectUserRoles, projectSaved);
                    }

                    List<ProjectUserRole> projectMembers = createProjectPanel.getProjectMembers();
                    if ((projectMembers != null) && (!projectMembers.isEmpty())) {
                        saveProjectMembers(projectMembers, projectSaved);
                    }

                } catch (MiddlewareQueryException e) {
                    LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                            + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                    return;
                }

                // add a workbench user to ibdb user mapping
                IbdbUserMap ibdbUserMap = new IbdbUserMap();
                ibdbUserMap.setWorkbenchUserId(currentUser.getUserid());
                ibdbUserMap.setProjectId(project.getProjectId());
                ibdbUserMap.setIbdbUserId(user.getUserid());
                try {
                    workbenchDataManager.addIbdbUserMap(ibdbUserMap);

                    // FIXME: What happens when the user deletes all associated methods and locations?
                    // Ideally, the methods and locations will be saved automatically when we save a project.
                    // However, we need to fix the Project POJOs mapping in order to do that

                    Set<Method> methods = project.getMethods();
                    if ((methods != null) && (!methods.isEmpty())) {
                        saveProjectMethods(methods, projectSaved);
                    }

                    //add a project location to workbench
                    Set<Location> locations = project.getLocations();
                    if ((locations != null) && (!locations.isEmpty())) {
                        saveProjectLocation(locations, projectSaved);
                    }

                } catch (MiddlewareQueryException e) {
                    LOG.error(e.getMessage(), e);
                    MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.DATABASE_ERROR), "<br />"
                            + messageSource.getMessage(Message.SAVE_PROJECT_ERROR_DESC));
                    return;
                }
            }

//            app.getSessionData().getProjectLocationData().clear();
//            app.getSessionData().getUniqueLocations().clear();

            LOG.info(project.getProjectId() + "  " + project.getProjectName() + " " + project.getStartDate() + " "
                    + project.getTemplate().getTemplateId());
            LOG.info("IBDB Local Generation Successful?: " + isGenerationSuccess);

            // go back to dashboard
            HomeAction home = new HomeAction();
            home.buttonClick(event);
        }

    }

    private void saveProjectMethods(Set<Method> methods, Project projectSaved) throws MiddlewareQueryException {

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

    private void saveProjectLocation(Set<Location> locations, Project projectSaved) throws MiddlewareQueryException {

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
                
                locID= managerFactory.getGermplasmDataManager().addLocation(location);
            }else{
                locID=l.getLocid();
            }
            projectLocationMap.setLocationId(locID);
            projectLocationMap.setProject(projectSaved);
            projectLocationMapList.add(projectLocationMap);
        }

        workbenchDataManager.addProjectLocationMap(projectLocationMapList);

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
        for (ProjectUserRole projectUserRole : projectUserRoles){
            
            // Save role
            projectUserRole.setProject(projectSaved);
            workbenchDataManager.addProjectUserRole(projectUserRole);

            // Save User to local db
            User workbenchUser = workbenchDataManager.getUserById(projectUserRole.getUserId());
            
            Person currentPerson = workbenchDataManager.getPersonById(workbenchUser.getPersonid());
            Person localPerson = currentPerson.copy();
            managerFactory.getUserDataManager().addPerson(localPerson);
           
            User localUser =  workbenchUser.copy();
            localUser.setPersonid(localPerson.getId());
            localUser.setAccess(PROJECT_USER_ACCESS_NUMBER);
            localUser.setType(PROJECT_USER_TYPE);
            localUser.setAdate(getCurrentDate());
            
            managerFactory.getUserDataManager().addUser(localUser);

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

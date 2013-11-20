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

import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.gxe.ProjectTableCellStyleGenerator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class ShowProjectDetailAction implements ItemClickListener {
    private static final long serialVersionUID = 1L;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Label lblActivity;
    
    private Table tblProject;
    private WorkbenchMainView workbenchDashboardwindow;
    private Label lblDashboardTitle;
    private Table tblActivity;

    private Table tblRoles;
    
    private OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction;
    
    private OpenWorkflowForRoleAction openWorkflowForRoleAction;
    
    private Button selectDatasetForBreedingViewButton;
    private Project currentProj;
    
    private GermplasmListPreview germplasmListPreview;
    private NurseryListPreview nurseryListPreview;
    private TabSheet previewTab;
    
    private List<Project> projects;
    
    public ShowProjectDetailAction(Label lblActivity, Table tblProject, Table tblActivity, Table tblRoles, 
            Button selectDatasetForBreedingViewButton, OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction,
            Project currentProject, GermplasmListPreview germplasmListPreview, NurseryListPreview nurseryListPreview, TabSheet previewTab, List<Project> projects) {
        this.lblActivity = lblActivity;
        this.tblProject = tblProject;
        this.tblActivity = tblActivity;
        this.tblRoles = tblRoles;
        this.selectDatasetForBreedingViewButton = selectDatasetForBreedingViewButton;
        this.openSelectDatasetForBreedingViewAction = openSelectDatasetForBreedingViewAction;
        this.currentProj = currentProject;
        this.germplasmListPreview = germplasmListPreview;
        this.nurseryListPreview = nurseryListPreview;
        this.previewTab = previewTab;
        this.projects = projects;
    }
    
   
    
    @Override
    public void itemClick(ItemClickEvent event) {
        @SuppressWarnings("unchecked")
        //BeanItem<Project> item = (BeanItem<Project>) event.getItem();              
        
        Project project = null; //item.getBean();
        Long projectId = (Long)event.getItemId();
        for(Project tempProject : projects){
            if(tempProject.getProjectId().longValue()  == projectId.longValue()){
                project = tempProject; 
                break;
            }
        }
        
        
        if (project == null) {
            return;
        }else
        {
        	currentProj = project;
        	IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        	app.getSessionData().setSelectedProject(currentProj);
        }
        
        // update the project activity table's listener
        if (openSelectDatasetForBreedingViewAction != null) {
            selectDatasetForBreedingViewButton.removeListener(openSelectDatasetForBreedingViewAction);
        }
        openSelectDatasetForBreedingViewAction = new OpenSelectProjectForStudyAndDatasetViewAction(project);
        selectDatasetForBreedingViewButton.addListener(openSelectDatasetForBreedingViewAction);
        
        // update the project activity table's listener
        
        if (openWorkflowForRoleAction != null) {
            tblRoles.removeListener(openWorkflowForRoleAction);
            tblRoles.setStyleName("gcp-tblroles");
        }
        
        openWorkflowForRoleAction = new OpenWorkflowForRoleAction(project);
        tblRoles.addListener(openWorkflowForRoleAction);
        tblRoles.setStyleName("gcp-tblroles gcp-selected");
        SessionData sessionData = IBPWorkbenchApplication.get().getSessionData();
        
        try {
            long projectActivitiesCount = workbenchDataManager.countProjectActivitiesByProjectId(project.getProjectId());
            List<ProjectActivity> activityList = workbenchDataManager.getProjectActivitiesByProjectId(project.getProjectId(), 0, (int) projectActivitiesCount);
            
            List<Role> roleList = workbenchDataManager.getRolesByProjectAndUser(project, sessionData.getUserData());
            
            String label = messageSource.getMessage(Message.PROJECT_DETAIL) + ": " + project.getProjectName();
            //projectDetailLabel.setValue(label);
           
            workbenchDashboardwindow = (WorkbenchMainView) event.getComponent().getWindow();
            workbenchDashboardwindow.addTitle(project.getProjectName());
            
            
            tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(tblProject, project));
            tblProject.refreshRowCache();
            
            updateActivityTable(activityList);
            updateRoleTable(roleList);
            germplasmListPreview.setProject(project);
            nurseryListPreview.setProject(project);
            previewTab.setSelectedTab(germplasmListPreview);
            
        }
        catch (MiddlewareQueryException e) {
            showDatabaseError(event.getComponent().getWindow());
        }
    }
    
    private void updateActivityTable(List<ProjectActivity> activityList) {
        Object[] oldColumns = tblActivity.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
        container.setBeanIdProperty("projectActivityId");
        tblActivity.setContainerDataSource(container);
        
        for (ProjectActivity activity : activityList) {
            container.addBean(activity);
        }
        
        lblActivity.setValue(messageSource.getMessage(Message.ACTIVITIES) + " [" + activityList.size() + "]");
        
        tblActivity.setContainerDataSource(container);
        
        tblActivity.setVisibleColumns(columns);
    }
    
    private void updateRoleTable(List<Role> roleList) {
        Object[] oldColumns = tblRoles.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, Role> container = new BeanContainer<Integer, Role>(Role.class);
        container.setBeanIdProperty("roleId");
        tblRoles.setContainerDataSource(container);
        
        for (Role role : roleList) {
            container.addBean(role);
        }
        
        tblRoles.setContainerDataSource(container);
        
        tblRoles.setVisibleColumns(columns);
    }
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
}

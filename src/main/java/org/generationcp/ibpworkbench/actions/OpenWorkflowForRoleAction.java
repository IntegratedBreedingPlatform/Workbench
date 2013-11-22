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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.workflow.ConventionalBreedingWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.workflow.MabcWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.workflow.ManagerWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.workflow.MarsProjectDashboard;
import org.generationcp.ibpworkbench.ui.workflow.MasWorkflowDiagram;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class OpenWorkflowForRoleAction implements ItemClickListener, ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenWorkflowForRoleAction.class);
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private ToolUtil toolUtil;
    
    protected Project project;
    
    public OpenWorkflowForRoleAction() {
    }
    
    public OpenWorkflowForRoleAction(Project project) {
        this.project = project;
    }
    
    @Override
    public void itemClick(ItemClickEvent event) {
        @SuppressWarnings("unchecked")
        BeanItem<Role> item = (BeanItem<Role>) event.getItem();
        
        Role role = item.getBean();
        if (role == null) {
            return;
        }
        
        LOG.trace("Opening workflow for role {} on project {}", new Object[] { role, project });
        
        WorkflowTemplate workflowTemplate = role.getWorkflowTemplate();
        if (workflowTemplate == null) {
            LOG.warn("No workflow template assigned to role: {}", role);
            return;
        }
        
        Component component = event.getComponent();
        Window window = component.getWindow();
        IContentWindow contentWindow = (IContentWindow) window;
        
        showWorkflowDashboard(project, role, contentWindow);
        
        String url = String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", project.getProjectId(), role.getRoleId());
        String workflowName = role.getWorkflowTemplate().getName();
       // NavManager.navigateApp(window, url, true, project.getProjectName());
        
        NavManager.navigateApp(window, url, true, workflowName);
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        Window window = event.getComponent().getWindow();
        doAction(window, "/OpenWorkflowForRole", true);
    }
    
    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }
    
    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        IContentWindow contentWindow = (IContentWindow) window;
        Map<String, List<String>> params = UriUtils.getUriParameters(uriFragment);
        
        Project project = null;
        Role role = null;
        try {
            Long projectId = Long.parseLong(params.get("projectId").get(0));
            Integer roleId = Integer.parseInt(params.get("roleId").get(0));
            
            project = workbenchDataManager.getProjectById(projectId);
            role = workbenchDataManager.getRoleById(roleId);
            
            showWorkflowDashboard(project, role, contentWindow);
        }
        catch (MiddlewareQueryException e) {
            LOG.error("QueryException", e);
            MessageNotifier.showError(window, 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
        }
        
        //NavManager.navigateApp(window, uriFragment, isLinkAccessed, project.getProjectName());
        
        String workflowName = role.getWorkflowTemplate().getName();
        NavManager.navigateApp(window, uriFragment, isLinkAccessed, workflowName);
    }
    
    protected void showWorkflowDashboard(Project project, Role role, IContentWindow contentWindow) {
        // we used to update the tool configurations here
        // but we don't need it anymore
        
        updateProjectLastOpenedDate((Window) contentWindow, project);
        
        SessionData sessionData = IBPWorkbenchApplication.get().getSessionData();
        sessionData.setLastOpenedProject(project);
        
        String workflowName = role.getWorkflowTemplate().getName();
        if (workflowName != null){
            if (workflowName.equals("MARS")) {
                MarsProjectDashboard projectDashboard = new MarsProjectDashboard(false, project,role);
                contentWindow.showContent(projectDashboard);
            }
            else if (workflowName.equals("MAS")) {
                MasWorkflowDiagram masWorkflowDiagram = new MasWorkflowDiagram(false, project,role);
                contentWindow.showContent(masWorkflowDiagram);
            }
            else if (workflowName.equals("Manager")) {
                ManagerWorkflowDiagram projectDashboard = new ManagerWorkflowDiagram(false, project, role);
                contentWindow.showContent(projectDashboard);
            }
            else if (workflowName.equals("MABC")) {
                MabcWorkflowDiagram mabcWorkflowDiagram = new MabcWorkflowDiagram(false, project,role);
                contentWindow.showContent(mabcWorkflowDiagram);
            }
            else if (workflowName.equals("CB")) {
                ConventionalBreedingWorkflowDiagram cbDiagram = new ConventionalBreedingWorkflowDiagram(false, project,role);
                contentWindow.showContent(cbDiagram);
            }
        }
    }
    
    
    
    private void updateProjectLastOpenedDate(Window window, Project project) {
        try {
        	
        	// set the last opened project in the session
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            
            
        	ProjectUserInfoDAO projectUserInfoDao = workbenchDataManager.getProjectUserInfoDao();
        	ProjectUserInfo	projectUserInfo = projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId().intValue(), app.getSessionData().getUserData().getUserid());
        	if (projectUserInfo != null) {
            	projectUserInfo.setLastOpenDate(new Date());
            	workbenchDataManager.saveOrUpdateProjectUserInfo(projectUserInfo);
        	}
    
        	project.setLastOpenDate(new Date());
            workbenchDataManager.mergeProject(project);
            
            app.getSessionData().setLastOpenedProject(project);
            
        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString(), e);
            showDatabaseError(window);
        }
    }
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
}

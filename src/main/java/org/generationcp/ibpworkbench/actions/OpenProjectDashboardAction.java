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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.ProjectDashboard;
import org.generationcp.ibpworkbench.comp.MarsProjectThumbnailPanel;
import org.generationcp.ibpworkbench.comp.MasProjectThumbnailPanel;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.comp.window.ProgressWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class OpenProjectDashboardAction implements ItemClickListener, MouseEvents.ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenProjectDashboardAction.class);

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private ToolUtil toolUtil;
    
    @Override
    public void itemClick(ItemClickEvent event) {
        Component component = event.getComponent();
        
        @SuppressWarnings("unchecked")
        BeanItem<Project> item = (BeanItem<Project>) event.getItem();
        
        Project project = item.getBean();
        if (project == null) {
            return;
        }
        
        String viewId = "/home/openProject?projectId=" + project.getProjectId(); 
        NavManager.navigateApp(component.getWindow(), viewId, true, project.getProjectName());
        
        openProject(component.getWindow(), project);
    }
    
    @Override
    public void click(ClickEvent event) {
        Component component = event.getComponent();
        
        Project project = null;
        if (component instanceof MarsProjectThumbnailPanel){
            project = (Project) ((MarsProjectThumbnailPanel) component).getData();
        } else if (component instanceof MasProjectThumbnailPanel){
            project = (Project) ((MasProjectThumbnailPanel) component).getData();
        }
        
        if (project == null) {
            return;
        }
        
        String viewId = "/home/openProject?projectId=" + project.getProjectId(); 
        NavManager.navigateApp(component.getWindow(), viewId, true, project.getProjectName());
        
        openProject(component.getWindow(), project);
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }
    
    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        
        //get parameters from uri fragment
        
        //retrieve project record using id from uri fragment
        //instantiate ProjectDashboard
        
        //call window.showContent
        Map<String, List<String>> params = UriUtils.getUriParameters(uriFragment);
                
        Project p = null;
        try {
            p = workbenchDataManager
                    .getProjectById(Long.parseLong(params.get("projectId").get(0)));
        } catch (NumberFormatException nfe) {
            LOG.error("NumberFormatException", nfe);
            MessageNotifier.showError(window, 
                    messageSource.getMessage(Message.INVALID_URL_PARAM_ERROR), 
                    "<br />" + messageSource.getMessage(Message.INVALID_URL_PARAM_ERROR_DESC));
            return;
        } catch (QueryException qe) {
            LOG.error("QueryException", qe);
            showDatabaseError(window);
            return;
        }
        
        openProject(window, p);
        
        NavManager.navigateApp(window, uriFragment, isLinkAccessed, p.getProjectName());
    }
    
    private void openProject(Window window, Project project) {
        updateTools(window, project);
        
        // open the project's dashboard
        ProjectDashboard projectDashboard = null;
        try {
            projectDashboard = new ProjectDashboard(project);
            projectDashboard.addProjectThumbnailPanelListener(new OpenProjectWorkflowAction());
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(window, i.getCaption(), i.getDescription());
            }
            return;
        }
        
        updateProjectLastOpenedDate(window, project);
        
        ((IContentWindow) window).showContent(projectDashboard);
    }
    
    private void updateProjectLastOpenedDate(Window window, Project p) {
        try {
            p.setLastOpenDate(new Date());
            workbenchDataManager.saveOrUpdateProject(p);
            
            // set the last opened project in the session
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            app.getSessionData().setLastOpenedProject(p);
        } catch (QueryException e) {
            LOG.error(e.toString(), e);
            showDatabaseError(window);
        }
    }
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
    
    protected void updateTools(Window window, Project project) {
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        // don't do anything if the project is the last project opened
        if (app.getSessionData().isLastOpenedProject(project)) {
            return;
        }
        
        // show a progress window
        ProgressWindow progressWindow = new ProgressWindow(messageSource.getMessage(Message.UPDATING_TOOLS_CONFIGURATION), 10 * 1000);
        progressWindow.setCaption(messageSource.getMessage(Message.UPDATING));
        progressWindow.setModal(true);
        progressWindow.setClosable(false);
        progressWindow.setResizable(false);
        progressWindow.center();
        
        window.addWindow(progressWindow);
        progressWindow.startProgress();
        
        // get all native tools
        List<Tool> nativeTools = null;
        try {
            nativeTools = workbenchDataManager.getToolsWithType(ToolType.NATIVE);
        }
        catch (QueryException e1) {
            LOG.error("QueryException", e1);
            MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        
        for (Tool tool : nativeTools) {
            // close the native tools
            try {
                toolUtil.closeNativeTool(tool);
            }
            catch (IOException e) {
                LOG.error("Exception", e);
            }
            
            // rewrite the configuration file
            try {
                toolUtil.updateToolConfigurationForProject(tool, project);
            }
            catch (IOException e) {
                LOG.error("Exception", e);
            }
        }
        
        // get web tools
        List<Tool> webTools = null;
        try {
            webTools = workbenchDataManager.getToolsWithType(ToolType.WEB);
        }
        catch (QueryException e2) {
            LOG.error("QueryException", e2);
            MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        
        for (Tool tool : webTools) {
            // rewrite the configuration file
            try {
                toolUtil.updateToolConfigurationForProject(tool, project);
            }
            catch (IOException e) {
                LOG.error("Exception", e);
            }
        }
    }
}
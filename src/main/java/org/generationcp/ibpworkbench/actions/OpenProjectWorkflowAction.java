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

import java.util.List;
import java.util.Map;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.MarsProjectDashboard;
import org.generationcp.ibpworkbench.comp.MasProjectDashboard;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.model.provider.IProjectProvider;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class OpenProjectWorkflowAction implements LayoutClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenProjectWorkflowAction.class);

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Override
    public void layoutClick(LayoutClickEvent event) {
        Component component = event.getComponent();
        
        IProjectProvider projectProvider = (IProjectProvider) component;
        Project project = projectProvider.getProject();
        
        if (project == null) {
            return;
        }
        
        IContentWindow window = (IContentWindow) component.getWindow();
        
        NavManager.navigateApp(component.getWindow(), 
                "/OpenProjectWorkflow?projectId="+project.getProjectId(),
                true, project.getProjectName());

        // Create a project dashboard based on the project's workflow type
        String projectTemplate = project.getTemplate().getName();                
        if (projectTemplate != null){
            if (projectTemplate.equals("MARS")) { 
                MarsProjectDashboard projectDashboard = new MarsProjectDashboard(project);
                window.showContent(projectDashboard);
            } else if (projectTemplate.equals("MAS")){
                MasProjectDashboard projectDashboard = new MasProjectDashboard(project);
                window.showContent(projectDashboard);
            }
        }

    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        IContentWindow w = (IContentWindow) window;
        Map<String, List<String>> params = UriUtils.getUriParameters(uriFragment);
                
        Project project = null;
        try {
            Long projectId = Long.parseLong(params.get("projectId").get(0));
            project = workbenchDataManager.getProjectById(projectId);
        } catch (MiddlewareQueryException e) {
            LOG.error("QueryException", e);
            MessageNotifier.showError(window, 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
        }
        
        // Create a project dashboard based on the project's workflow type
        String projectTemplate = project.getTemplate().getName();                
        if (projectTemplate != null){
            if (projectTemplate.equals("MARS")) { 
                MarsProjectDashboard projectDashboard = new MarsProjectDashboard(project);
                w.showContent(projectDashboard);
            } else if (projectTemplate.equals("MAS")){
                MasProjectDashboard projectDashboard = new MasProjectDashboard(project);
                w.showContent(projectDashboard);
            }
        }
        
        NavManager.navigateApp(window, uriFragment, isLinkAccessed, project.getProjectName());
    }
}

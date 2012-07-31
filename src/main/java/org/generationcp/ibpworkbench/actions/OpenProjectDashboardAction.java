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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.ProjectDashboard;
import org.generationcp.ibpworkbench.comp.ProjectThumbnailPanel;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
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
    
    @Override
    public void itemClick(ItemClickEvent event) {
        Component component = event.getComponent();
        IContentWindow window = (IContentWindow) component.getWindow();
        
        @SuppressWarnings("unchecked")
        BeanItem<Project> item = (BeanItem<Project>) event.getItem();
        
        Project project = item.getBean();
        if (project == null) {
            return;
        }
        
        ProjectDashboard projectDashboard = null;
        try {
            projectDashboard = new ProjectDashboard(project);
            projectDashboard.addProjectThumbnailPanelListener(new OpenProjectWorkflowAction());
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
            }
            return;
        }
        
        String viewId = "/home/openProject?projectId=" + project.getProjectId(); 
        NavManager.navigateApp(component.getWindow(), viewId, true, project.getProjectName());
        
        window.showContent(projectDashboard);
    }
    
    @Override
    public void click(ClickEvent event) {
        Component component = event.getComponent();
        IContentWindow window = (IContentWindow) component.getWindow();
        
        Project project = (Project) ((ProjectThumbnailPanel) component).getData();
        if (project == null) {
            return;
        }

        ProjectDashboard projectDashboard = null;
        try {
            projectDashboard = new ProjectDashboard(project);
            projectDashboard.addProjectThumbnailPanelListener(new OpenProjectWorkflowAction());
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
            }
            return;
        }

        window.showContent(projectDashboard);

        String viewId = "/home/openProject?projectId=" + project.getProjectId();
        NavManager.navigateApp((Window) window, viewId, true, project.getProjectName());
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
        IContentWindow w = (IContentWindow) window;
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
            MessageNotifier.showError(window, 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        
        ProjectDashboard projectDashboard = null;
        try {
            projectDashboard = new ProjectDashboard(p);
            projectDashboard.addProjectThumbnailPanelListener(new OpenProjectWorkflowAction());
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(window, i.getCaption(), i.getDescription());
            }
            return;
        }
        
        w.showContent(projectDashboard);
        
        NavManager.navigateApp(window, uriFragment, isLinkAccessed, p.getProjectName());
    }
}
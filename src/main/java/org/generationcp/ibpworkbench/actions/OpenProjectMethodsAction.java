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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.ProjectLocationPanel;
import org.generationcp.ibpworkbench.comp.ProjectBreedingMethodsPanel;
import org.generationcp.ibpworkbench.comp.WorkflowConstants;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 *  @author Joyce Avestro
 */
@Configurable
public class OpenProjectMethodsAction implements WorkflowConstants,  ClickListener, ActionListener {
	
	
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenProjectMethodsAction.class);
   
    private Project project;
    private Role role;
    
    public OpenProjectMethodsAction() {
      
    }
    public OpenProjectMethodsAction(Project project, Role role) {
        this.project = project;
        this.role = role;
    }
    
    @Autowired
	private WorkbenchDataManager workbenchDataManager;
	 

    @Override
    public void buttonClick(ClickEvent event) {
        doAction(event.getComponent().getWindow(), null, true);
    }
    
    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }
    
    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        IContentWindow w = (IContentWindow) window;
        
        try {
        	ProjectBreedingMethodsPanel projectMethodsPanel = new ProjectBreedingMethodsPanel(project, role);
        	projectMethodsPanel.setWidth("780px"); 
            
            w.showContent(projectMethodsPanel);
            
            try {
                IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
                User user = app.getSessionData().getUserData();
                Project currentProject = app.getSessionData().getLastOpenedProject();

                ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Project Methods", "Launched Project Methods", user, new Date());

                workbenchDataManager.addProjectActivity(projAct);

            } catch (MiddlewareQueryException e1) {
                MessageNotifier.showError(window, "Database Error",
                                          "<br />" + "Contact your administrator");
                return;
            }
            
            NavManager.navigateApp(window, "/ProjectMethods", isLinkAccessed);
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(window, i.getCaption(), i.getDescription());
            }
            return;
        }
    }
}

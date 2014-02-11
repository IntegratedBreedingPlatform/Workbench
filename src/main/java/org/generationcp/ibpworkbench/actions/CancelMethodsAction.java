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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.ProjectBreedingMethodsPanel;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * 
 * @author Jeffrey Morales, Joyce Avestro
 * 
 * 
 */
@Configurable
public class CancelMethodsAction implements ClickListener, ActionListener  {
    
    private static final Logger LOG = LoggerFactory.getLogger(CancelMethodsAction.class);
    private static final long serialVersionUID = 1L;
    
    private AddLocationsWindow window;
	private ProjectBreedingMethodsPanel projectBreedingMethodsPanel;

    public CancelMethodsAction(AddLocationsWindow window) {
        this.window = window;
    }

    public CancelMethodsAction(ProjectBreedingMethodsPanel projectBreedingMethodsPanel) {
        this.projectBreedingMethodsPanel = projectBreedingMethodsPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {
            
        if (window != null){
            window.getParent().removeWindow(window);
        } else if (projectBreedingMethodsPanel != null){
            doAction(event.getComponent().getWindow(), null, true);
        }
    
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);        
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        try {
            Project project = projectBreedingMethodsPanel.getProject();
            Role role = projectBreedingMethodsPanel.getRole();

            String url = String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", project.getProjectId(), role.getRoleId());
            (new OpenWorkflowForRoleAction(projectBreedingMethodsPanel.getProject())).doAction(window, url, isLinkAccessed);
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

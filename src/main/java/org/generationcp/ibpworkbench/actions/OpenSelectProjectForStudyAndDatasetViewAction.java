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

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

/**
 * 
 * @author Jeffrey Morales
 *
 */
public class OpenSelectProjectForStudyAndDatasetViewAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    
    private Project currentProject;
    private Project lastOpenedProject;
    
    public OpenSelectProjectForStudyAndDatasetViewAction(Project currentProject) {
        
        this.currentProject = currentProject;
        
    }

    private static final Logger LOG = LoggerFactory.getLogger(OpenSelectProjectForStudyAndDatasetViewAction.class);
    
    @Override
    public void buttonClick(ClickEvent event) {
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        lastOpenedProject = app.getSessionData().getLastOpenedProject();
        
        if (currentProject != null) {
            
            event.getComponent().getWindow().addWindow(new SelectDatasetForBreedingViewWindow(currentProject, Database.CENTRAL));
            
        } else if (lastOpenedProject != null) {
            
            event.getComponent().getWindow().addWindow(new SelectDatasetForBreedingViewWindow(lastOpenedProject, Database.CENTRAL));
            
        } else {
            
            event.getComponent().getWindow().showNotification("Please select a Project first.", Notification.TYPE_ERROR_MESSAGE);
            
        }

    }
    
}

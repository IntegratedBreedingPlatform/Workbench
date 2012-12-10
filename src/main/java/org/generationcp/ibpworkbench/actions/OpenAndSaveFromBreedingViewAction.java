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
import org.generationcp.ibpworkbench.comp.window.SelectDetailsForBreedingViewWindow;
import org.generationcp.ibpworkbench.model.BreedingViewEntryModel;
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
public class OpenAndSaveFromBreedingViewAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    
    private Project currentProject;
    private Project lastOpenedProject;
    private SelectDetailsForBreedingViewWindow selectDetailsForBreedingViewWindow;
    
    BreedingViewEntryModel breedingViewEntryModel;
    
    public OpenAndSaveFromBreedingViewAction(Project currentProject, SelectDetailsForBreedingViewWindow selectDetailsForBreedingViewWindow) {
        
        this.currentProject = currentProject;
        this.selectDetailsForBreedingViewWindow = selectDetailsForBreedingViewWindow;
        
    }

    private static final Logger LOG = LoggerFactory.getLogger(OpenAndSaveFromBreedingViewAction.class);
    
    @Override
    public void buttonClick(ClickEvent event) {
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        lastOpenedProject = app.getSessionData().getLastOpenedProject();
        
        if (currentProject != null) {
            
            breedingViewEntryModel = createBreedingViewEntryModel(selectDetailsForBreedingViewWindow.getTxtName().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getTxtVersion().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getTxtEnvironment().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getSelProjectType().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getSelDesignType().getValue().toString());
            
            app.getMainWindow().removeWindow(event.getComponent().getWindow());
            app.getMainWindow().addWindow(new SelectDatasetForBreedingViewWindow(currentProject, Database.CENTRAL, breedingViewEntryModel));
            
        } else if (lastOpenedProject != null) {
            
            breedingViewEntryModel = createBreedingViewEntryModel(selectDetailsForBreedingViewWindow.getTxtName().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getTxtVersion().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getTxtEnvironment().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getSelProjectType().getValue().toString(),
                                        selectDetailsForBreedingViewWindow.getSelDesignType().getValue().toString());
            
            app.getMainWindow().removeWindow(event.getComponent().getWindow());
            app.getMainWindow().addWindow(new SelectDatasetForBreedingViewWindow(lastOpenedProject, Database.CENTRAL, breedingViewEntryModel));
            
        } else {
            
            event.getComponent().getWindow().showNotification("Please select a Project first.", Notification.TYPE_ERROR_MESSAGE);
            
        }

    }
    
    private BreedingViewEntryModel createBreedingViewEntryModel(String name, String version, String environment, String projectType, String designType) {
        
        BreedingViewEntryModel breedingViewEntryModel = new BreedingViewEntryModel();
        
        breedingViewEntryModel.setName(name);
        breedingViewEntryModel.setVersion(version);
        breedingViewEntryModel.setEnvironment(environment);
        breedingViewEntryModel.setProjectType(projectType);
        breedingViewEntryModel.setDesignType(designType);
        
        return breedingViewEntryModel;
        
    }
    
}

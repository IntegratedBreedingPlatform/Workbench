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

import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @author Jeffrey Morales
 *
 */
public class OpenSelectDatasetForBreedingViewAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    
    private Project currentProject;
    private Project lastOpenedProject;
    
    public OpenSelectDatasetForBreedingViewAction(Project currentProject) {
        
        this.currentProject = currentProject;
        
    }

    private static final Logger LOG = LoggerFactory.getLogger(OpenSelectDatasetForBreedingViewAction.class);
    
    @Override
    public void buttonClick(ClickEvent event) {
        
        System.out.println(currentProject);
        
        event.getComponent().getWindow().addWindow(new SelectDatasetForBreedingViewWindow(currentProject, Database.CENTRAL));

    }
    
}

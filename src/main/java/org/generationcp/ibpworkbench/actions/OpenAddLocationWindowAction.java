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

import org.generationcp.ibpworkbench.comp.ProjectLocationPanel;
import org.generationcp.ibpworkbench.comp.project.create.ProjectLocationsComponent;
import org.generationcp.ibpworkbench.comp.window.AddLocationsWindow;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @author Jeffrey Morales, Joyce Avestro
 * 
 */

public class OpenAddLocationWindowAction implements ClickListener{
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenAddLocationWindowAction.class);

    private static final long serialVersionUID = 1L;

    private ProjectLocationsComponent projectLocationComponent;  // from Create Project Panel
    private ProjectLocationPanel projectLocationPanel;           // from Manager Workflow configuration - project locations panel

    private  List<UserDefinedField> udfList; private List<Country> countryList;
    
    public OpenAddLocationWindowAction(ProjectLocationsComponent projectLocationComponent) {
        this.projectLocationComponent = projectLocationComponent;
    }

    public OpenAddLocationWindowAction(ProjectLocationPanel projectLocationPanel) {
        this.projectLocationPanel = projectLocationPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (projectLocationComponent != null) {
            event.getComponent().getWindow().addWindow(new AddLocationsWindow(projectLocationComponent,projectLocationComponent.getGermplasmDataManager()));
        } else if (projectLocationPanel != null) {
            event.getComponent().getWindow().addWindow(new AddLocationsWindow(projectLocationPanel,projectLocationPanel.getGermplasmDataManager()));
        }
    }

    
}

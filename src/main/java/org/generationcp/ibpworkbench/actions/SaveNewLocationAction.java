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

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.ProjectLocationPanel;
import org.generationcp.ibpworkbench.comp.form.AddLocationForm;
import org.generationcp.ibpworkbench.comp.project.create.ProjectLocationsComponent;
import org.generationcp.ibpworkbench.comp.window.AddLocationsWindow;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * 
 * @author Jeffrey Morales, Joyce Avestro
 * 
 */

@Configurable
public class SaveNewLocationAction implements ClickListener{

    private static final Logger LOG = LoggerFactory.getLogger(SaveNewLocationAction.class);
    private static final long serialVersionUID = 1L;

    private AddLocationForm newLocationForm;

    private AddLocationsWindow window;

    private ProjectLocationsComponent projectLocationsComponent;
    private ProjectLocationPanel projectLocationPanel;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public SaveNewLocationAction(AddLocationForm newLocationForm, AddLocationsWindow window,
            ProjectLocationsComponent projectLocationsComponent) {
        this.newLocationForm = newLocationForm;
        this.window = window;
        this.projectLocationsComponent = projectLocationsComponent;
    }

    public SaveNewLocationAction(AddLocationForm newLocationForm, AddLocationsWindow window, ProjectLocationPanel projectLocationPanel) {
        this.newLocationForm = newLocationForm;
        this.window = window;
        this.projectLocationPanel = projectLocationPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        newLocationForm.commit();

        @SuppressWarnings("unchecked")
        BeanItem<LocationModel> locationBean = (BeanItem<LocationModel>) newLocationForm.getItemDataSource();
        LocationModel location = locationBean.getBean();

        newLocationForm.commit();

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

        // TODO: (BUG) there's a problem getting the nextKey of the locations when there's already existing locations
        // in the local database. Always starts at -1 for new sessions
        if (!app.getSessionData().getUniqueLocations().contains(location.getLocationName())) {

            app.getSessionData().getUniqueLocations().add(location.getLocationName());

            Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size() + 1;

            nextKey = nextKey * -1;

            LocationModel newLocation = new LocationModel();

            newLocation.setLocationName(location.getLocationName());
            newLocation.setLocationAbbreviation(location.getLocationAbbreviation());
            newLocation.setLocationId(nextKey);
            
            newLocation.setLtype(location.getLtype() != null ? location.getLtype() : 0);
            newLocation.setCntryid(location.getCntryid() != null ? location.getCntryid() : 0);
            
            app.getSessionData().getProjectLocationData().put(nextKey, newLocation);

            LOG.info(app.getSessionData().getProjectLocationData().toString());

            newLocationForm.commit();

            Location newLoc = new Location();
            newLoc.setLocid(newLocation.getLocationId());
            newLoc.setLname(newLocation.getLocationName());
            newLoc.setLabbr(newLocation.getLocationAbbreviation());
            newLoc.setLtype(newLocation.getLtype() != null ? newLocation.getLtype() : 0);
            newLoc.setCntryid(newLocation.getCntryid() != null ? newLocation.getCntryid() : 0);
            
            if (projectLocationsComponent != null) {
                projectLocationsComponent.getSelect().addItem(newLoc);
                projectLocationsComponent.getSelect().setItemCaption(newLoc, newLoc.getLname());

                projectLocationsComponent.getSelect().select(newLoc);
                projectLocationsComponent.getSelect().setValue(newLoc);
            } else if (projectLocationPanel != null) {
                projectLocationPanel.getSelect().addItem(newLoc);
                projectLocationPanel.getSelect().setItemCaption(newLoc, newLoc.getLname());
                projectLocationPanel.addNewLocations(newLoc);
                
                projectLocationPanel.getSelect().select(newLoc);
                projectLocationPanel.getSelect().setValue(newLoc);
            }
            
            User user = app.getSessionData().getUserData();
            Project currentProject = app.getSessionData().getLastOpenedProject();
            ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Project Locations", "Added new Location ("+ newLocation.getLocationName() + ")", user, new Date());
            try {
				workbenchDataManager.addProjectActivity(projAct);
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            newLocation = null;
            window.getParent().removeWindow(window);

        }

    }
}

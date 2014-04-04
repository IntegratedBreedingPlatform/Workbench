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
import java.util.List;

import com.vaadin.data.Validator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.window.ConfirmLocationsWindow;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
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
import com.vaadin.ui.Button;
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

    private final ProgramLocationsView programLocationsView;
    private final ProgramLocationsPresenter programLocationsPresenter;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    public SaveNewLocationAction(AddLocationForm newLocationForm, AddLocationsWindow window,
            ProgramLocationsView programLocationsView, ProgramLocationsPresenter programLocationsPresenter) {
        this.newLocationForm = newLocationForm;
        this.window = window;
        this.programLocationsView = programLocationsView;
        this.programLocationsPresenter = programLocationsPresenter;
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
    	

    	try {
            newLocationForm.commit();
            @SuppressWarnings("unchecked")
            BeanItem<LocationModel> locationBean = (BeanItem<LocationModel>) newLocationForm.getItemDataSource();
            LocationModel location = locationBean.getBean();

            List<Location> existingLocations = programLocationsPresenter.getExistingLocations(location.getLocationName());

            // there exists a location with the same name?
    		if (existingLocations.size() > 0){
    			new ConfirmLocationsWindow(window, existingLocations , programLocationsPresenter, new Button.ClickListener() {
				
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						saveLocation();
					}
				} ).show();
    			
    		}else{
    			saveLocation();
    		}
    		
    	} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		} catch (Validator.EmptyValueException e) {
            MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), e.getLocalizedMessage());
            return;
        } catch (Validator.InvalidValueException e) {
            MessageNotifier.showError(event.getComponent().getWindow(), messageSource.getMessage(Message.INVALID_OPERATION), e.getLocalizedMessage());
            return;
        }
    }
    
    private void saveLocation() {
        @SuppressWarnings("unchecked")
        BeanItem<LocationModel> locationBean = (BeanItem<LocationModel>) newLocationForm.getItemDataSource();
        LocationModel location = locationBean.getBean();

        // increment key from the session's list of locations (correct id from local db)
        sessionData.getUniqueLocations().add(location.getLocationName());
        Integer nextKey = sessionData.getProjectLocationData().keySet().size() + 1;
        nextKey = nextKey * -1;

        location.setLocationId(nextKey);

        // add new location to session list
        sessionData.getProjectLocationData().put(nextKey, location);

        LOG.info(sessionData.getProjectLocationData().toString());

        // save to middleware
        try {
            Location loc = location.toLocation();

            programLocationsPresenter.addLocation(loc);
 		} catch (MiddlewareQueryException e1) {
 			e1.printStackTrace();
 		    return;
        }

        // Log operation to project activities
        User user = sessionData.getUserData();
        if (user != null) {
            Project currentProject = sessionData.getLastOpenedProject();
            ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject,messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK), "Added new Location ("+ location.getLocationName() + ")", user, new Date());
            try {
                workbenchDataManager.addProjectActivity(projAct);
            } catch (MiddlewareQueryException e) {
                e.printStackTrace();
            }
        }

        window.getParent().removeWindow(window);

    }
}

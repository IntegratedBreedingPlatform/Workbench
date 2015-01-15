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

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.form.AddLocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.AddLocationsWindow;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.ibpworkbench.ui.window.ConfirmLocationsWindow;
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

import java.util.Date;
import java.util.List;

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

    private final ProgramLocationsPresenter programLocationsPresenter;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    public SaveNewLocationAction(AddLocationForm newLocationForm, AddLocationsWindow window,ProgramLocationsPresenter programLocationsPresenter) {
        this.newLocationForm = newLocationForm;
        this.window = window;
        this.programLocationsPresenter = programLocationsPresenter;
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
    	

    	try {
            newLocationForm.commit();
            @SuppressWarnings("unchecked")
            BeanItem<LocationViewModel> locationBean = (BeanItem<LocationViewModel>) newLocationForm.getItemDataSource();
            LocationViewModel location = locationBean.getBean();

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
            MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
            return;
        } catch (Validator.InvalidValueException e) {
            MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
            return;
        }
    }
    
    private void saveLocation() {
        @SuppressWarnings("unchecked")
        BeanItem<LocationViewModel> locationBean = (BeanItem<LocationViewModel>) newLocationForm.getItemDataSource();
        LocationViewModel locModel = locationBean.getBean();

        // increment key from the session's list of locations (correct id from local db)
        Integer nextKey = sessionData.getProjectLocationData().keySet().size() + 1;

        locModel.setLocationId(nextKey);

        // add new location to session list
        sessionData.getProjectLocationData().put(nextKey, locModel);

        LOG.info(sessionData.getProjectLocationData().toString());

        // save to middleware
        try {
            Location loc = programLocationsPresenter.convertLocationViewToLocation(locModel);

            programLocationsPresenter.addLocation(loc);
 		} catch (MiddlewareQueryException e) {
 			LOG.error(e.getMessage(),e);
 		    return;
        }

        // Log operation to project activities
        User user = sessionData.getUserData();
        if (user != null) {
            Project currentProject = sessionData.getLastOpenedProject();
            ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject,messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK), "Added new Location ("+ locModel.getLocationName() + ")", user, new Date());
            try {
                workbenchDataManager.addProjectActivity(projAct);
            } catch (MiddlewareQueryException e) {
                e.printStackTrace();
            }
        }

        window.getParent().removeWindow(window);

    }
}

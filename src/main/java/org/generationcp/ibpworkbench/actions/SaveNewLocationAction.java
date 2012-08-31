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
import org.generationcp.ibpworkbench.comp.form.AddLocationForm;
import org.generationcp.ibpworkbench.comp.window.AddLocationsWindow;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.pojos.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class SaveNewLocationAction implements ClickListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(SaveNewLocationAction.class);
    private static final long serialVersionUID = 1L;
   
    private AddLocationForm newLocationForm;
    
    private AddLocationsWindow window;

    public SaveNewLocationAction(AddLocationForm newLocationForm, AddLocationsWindow window) {
        this.newLocationForm = newLocationForm;
        this.window = window;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
    	newLocationForm.commit();

        @SuppressWarnings("unchecked")
        BeanItem<LocationModel> locationBean = (BeanItem<LocationModel>) newLocationForm.getItemDataSource();
        LocationModel location = locationBean.getBean();
        
        newLocationForm.commit();
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        if (!app.getSessionData().getUniqueLocations().contains(location.getLocationName())){
        
        	app.getSessionData().getUniqueLocations().add(location.getLocationName());
        	
        	Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size();
        	
        	LocationModel newLocation = new LocationModel();
        	
        	newLocation.setLocationName(location.getLocationName());
        	newLocation.setLocationAbbreviation(location.getLocationAbbreviation());
        	
        	newLocation.setLocationId(nextKey);
        
            app.getSessionData().getProjectLocationData().put(nextKey, newLocation);
            
            newLocation = null;
            
            LOG.info(app.getSessionData().getProjectLocationData().toString());
        // go back to dashboard
        //HomeAction home = new HomeAction();
        //home.buttonClick(event);
            
            newLocationForm.commit();
            
            window.getParent().removeWindow(window);
        
        }
        
    }
}

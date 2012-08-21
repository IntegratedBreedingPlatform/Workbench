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

    public SaveNewLocationAction(AddLocationForm newLocationForm) {
        this.newLocationForm = newLocationForm;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
    	newLocationForm.commit();

        @SuppressWarnings("unchecked")
        BeanItem<Location> locationBean = (BeanItem<Location>) newLocationForm.getItemDataSource();
        Location location = locationBean.getBean();
        
        newLocationForm.commit();
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        if (!app.getSessionData().getUniqueLocations().contains(location.getLname())){
        
        	app.getSessionData().getUniqueLocations().add(location.getLname());
        	
        	Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size();
        	
        	Location newLocation = new Location();
        	
        	newLocation.setLname(location.getLname());
        	newLocation.setLabbr(location.getLabbr());
        	
        	newLocation.setLocid(nextKey);
        	newLocation.setCntryid(0);
        	newLocation.setLrplce(0);
        	newLocation.setLtype(0);
        	newLocation.setNllp(0);
        	newLocation.setNllp(0);
            newLocation.setSnl1id(0);
            newLocation.setSnl2id(0);
            newLocation.setSnl3id(0);
        
            app.getSessionData().getProjectLocationData().put(nextKey, newLocation);
            
            newLocation = null;
            
            LOG.info(app.getSessionData().getProjectLocationData().toString());
        // go back to dashboard
        //HomeAction home = new HomeAction();
        //home.buttonClick(event);
        
        }
        
    }
}

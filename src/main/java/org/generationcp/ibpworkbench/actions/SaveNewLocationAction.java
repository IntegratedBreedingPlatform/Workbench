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
        
        location.setCntryid(0);
        location.setLrplce(0);
        location.setLtype(0);
        location.setNllp(0);
        location.setSnl1id(0);
        location.setSnl2id(0);
        location.setSnl3id(0);
        
        Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size();
        
        app.getSessionData().getProjectLocationData().put(nextKey+1, location);
            
        // go back to dashboard
        //HomeAction home = new HomeAction();
        //home.buttonClick(event);
    }
}

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
package org.generationcp.ibpworkbench.comp.form;

import java.util.Arrays;

import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.middleware.pojos.Location;
import org.springframework.context.annotation.DependsOn;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;


/**
 * <b>Description</b>: Custom form for adding Locations.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Jeffrey Morales
 * <br>
 * <b>File Created</b>: August 20, 2012
 * @deprecated unused class
 */
public class AddLocationForm extends Form{
    
	private static final long serialVersionUID = 865075321914843448L;

	private BeanItem<Location> locationBean;
    
    private Location location;
    
    private GridLayout grid;
    
    public AddLocationForm(Location location) {
        this.location = location;
        
        assemble();
    }

    protected void assemble() {
        
        initializeComponents();
        initializeLayout();
    }

    protected void initializeLayout() {
        
    }

    protected void initializeComponents() { 
        
        grid = new GridLayout(2, 1);
        grid.setSpacing(true);
        grid.setMargin(true);
        
        setLayout(grid);
        
        locationBean = new BeanItem<Location>(location);
        
        setItemDataSource(locationBean);
        setComponentError(null);
        setFormFieldFactory(new LocationFormFieldFactory());
        setVisibleItemProperties(Arrays.asList(
                new String[] { "lname", "labbr" }));
        
        setWriteThrough(false);
        setInvalidCommitted(false);
    }
    
    @Override
    protected void attachField(Object propertyId, Field field) {
        
        if("lname".equals(propertyId)) {
            grid.addComponent(field, 0, 0);
        } else if ("labbr".equals(propertyId)) {
            grid.addComponent(field, 1, 0);
        }
    }
    
}

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
package org.generationcp.ibpworkbench.ui.form;

import java.util.Arrays;

import com.vaadin.ui.FormLayout;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.manager.api.GermplasmDataManager;

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
 */
public class AddLocationForm extends Form {
    
	private static final long serialVersionUID = 865075321914843448L;

    private GridLayout grid;
    
	private ProgramLocationsPresenter presenter;
    
    public AddLocationForm(ProgramLocationsPresenter presenter) {
        this.presenter = presenter;
        
        assemble();
    }

    protected void assemble() {
        
        initializeComponents();
        initializeLayout();
    }

    protected void initializeLayout() {
        
    }

    protected void initializeComponents() { 
        /*
        grid = new GridLayout(1, 5);
        grid.setSpacing(true);
        grid.setMargin(true);
        
        setLayout(grid);
        */

        final FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(true);

        setItemDataSource(new BeanItem<LocationViewModel>(new LocationViewModel()));

        setComponentError(null);
        setFormFieldFactory(new LocationFormFieldFactory(presenter));

        setVisibleItemProperties(Arrays.asList(
                new String[] { "locationName", "locationAbbreviation","ltype","cntryid", "provinceId" }));
        
        setWriteThrough(false);
        setInvalidCommitted(false);
        setValidationVisibleOnCommit(false);

        this.setLayout(formLayout);

    }

    /*
    @Override
    protected void attachField(Object propertyId, Field field) {
        
        if("locationName".equals(propertyId)) {
            grid.addComponent(field, 0, 0);
        } else if ("locationAbbreviation".equals(propertyId)) {
            grid.addComponent(field, 0, 1);
        } else if ("ltype".equals(propertyId)) {
        	grid.addComponent(field,0,2);
        } else if ("cntryid".equals(propertyId)) {
        	grid.addComponent(field,0,3);
        } else if ("provinceId".equals(propertyId)) {
            grid.addComponent(field, 0,4);
        }
    }
    */
    
}

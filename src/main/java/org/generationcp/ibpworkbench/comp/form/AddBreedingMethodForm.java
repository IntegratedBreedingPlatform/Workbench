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

import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.generationcp.ibpworkbench.model.formfieldfactory.BreedingMethodFormFieldFactory;

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
public class AddBreedingMethodForm extends Form{

	/**
     * 
     */
    private static final long serialVersionUID = -3649453194910730855L;

    private BeanItem<BreedingMethodModel> breedingMethodBean;
    
    private BreedingMethodModel method;
    
    private GridLayout grid;
    
    public AddBreedingMethodForm(BreedingMethodModel method) {
        this.method = method;
        
        assemble();
    }

    protected void assemble() {
        
        initializeComponents();
        initializeLayout();
    }

    protected void initializeLayout() {
        
    }
    
    protected void initializeComponents() { 
        
        grid = new GridLayout(4, 1);
        grid.setSpacing(true);
        grid.setMargin(true);
        
        setLayout(grid);
        
        breedingMethodBean = new BeanItem<BreedingMethodModel>(method);
        
        setItemDataSource(breedingMethodBean);
        setComponentError(null);
        setFormFieldFactory(new BreedingMethodFormFieldFactory());
        setVisibleItemProperties(Arrays.asList(
                new String[] { "methodName", "methodDescription", "methodType", "methodCode" }));
        
        setWriteThrough(false);
        setInvalidCommitted(false);
    }
    
    @Override
    protected void attachField(Object propertyId, Field field) {
        
        if("methodName".equals(propertyId)) {
            grid.addComponent(field, 0, 0);
        } else if ("methodDescription".equals(propertyId)) {
            grid.addComponent(field, 1, 0);
        } else if ("methodType".equals(propertyId)) {
            grid.addComponent(field, 2, 0);
        } else if ("methodCode".equals(propertyId)) {
            grid.addComponent(field, 3, 0);
        }
    }
    
}

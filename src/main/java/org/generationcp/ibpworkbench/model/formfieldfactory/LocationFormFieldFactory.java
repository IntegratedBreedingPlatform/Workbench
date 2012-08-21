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
package org.generationcp.ibpworkbench.model.formfieldfactory;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;


/**
 * <b>Description</b>: Field factory for generating Location fields for Location class.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Jeffrey Morales 
 * <br>
 * <b>File Created</b>: Jul 16, 2012
 */
@Configurable
public class LocationFormFieldFactory extends DefaultFieldFactory{

    private static final long serialVersionUID = 3560059243526106791L;
    
    private Field locationName;
    private Field locationAbbreviation;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public LocationFormFieldFactory() {
        initFields();
    }
    
    private void initFields() {
    	
        locationName = new TextField();
        locationName.setRequired(true);
        locationName.setRequiredError("Please enter a Location Name.");
        locationName.addValidator(new StringLengthValidator("Location Name must be 1-60 characters.", 1, 60, false));
        
        locationAbbreviation = new TextField();
        locationAbbreviation.setRequired(true);
        locationAbbreviation.setRequiredError("Please enter a Location Abbreviation.");
        locationAbbreviation.addValidator(new StringLengthValidator("Location Abbreviation must be 1-8 characters.", 1, 8, false));


    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
         
        Field field = super.createField(item, propertyId, uiContext);
        
        if ("lname".equals(propertyId)) {
            messageSource.setCaption(locationName, Message.LOC_NAME);
            return locationName;
            
        } else if ("labbr".equals(propertyId)) {
            messageSource.setCaption(locationAbbreviation, Message.LOC_ABBR);
            return locationAbbreviation;
        }
        
        return field;
    }
}

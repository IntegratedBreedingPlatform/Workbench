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
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;


/**
 * <b>Description</b>: Field factory for generating Breeding Method fields for Breeding Method class.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Jeffrey Morales 
 * <br>
 * <b>File Created</b>: August 30, 2012
 */
@Configurable
public class BreedingMethodFormFieldFactory extends DefaultFieldFactory{

    private static final long serialVersionUID = 3560059243526106791L;
    
    private Field methodName;
    private Field methodDescription;
    private Select methodSelectType;
    private Select methodSelectGroup;
    private Field methodCode;
    
    // For new item handling and listener of crop type combo box
    //private MethodTypeComboAction methodTypeComboAction;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public BreedingMethodFormFieldFactory() {
        initFields();
    }
    
    private void initFields() {
    	
        methodName = new TextField();
        methodName.setRequired(true);
        methodName.setRequiredError("Please enter a Breeding Method Name.");
        methodName.addValidator(new StringLengthValidator("Breeding Method Name must be 1-50 characters.", 1, 50, false));
        methodName.setWidth("180px");
        
        
        methodDescription = new TextField();
        methodDescription.setRequired(true);
        methodDescription.setRequiredError("Please enter a Breeding Method Description.");
        methodDescription.addValidator(new StringLengthValidator("Breeding Method Description must be 1-255 characters.", 1, 255, false));
        methodDescription.setWidth("180px");
        
        methodCode = new TextField();
        methodCode.setRequired(true);
        methodCode.setRequiredError("Please enter a Breeding Method Code.");
        methodCode.addValidator(new StringLengthValidator("Breeding Method Code must be 1-8 characters.", 1, 8, false));
        methodCode.setWidth("60px");
        

    }
    

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
         
        Field field = super.createField(item, propertyId, uiContext);
        
        if ("methodName".equals(propertyId)) {
            messageSource.setCaption(methodName, Message.BREED_METH_NAME);
            return methodName;
            
        } else if ("methodDescription".equals(propertyId)) {
            messageSource.setCaption(methodDescription, Message.BREED_METH_DESC);
            return methodDescription;
        } else if ("methodCode".equals(propertyId)) {
            messageSource.setCaption(methodCode, Message.BREED_METH_CODE);
            return methodCode;
        } else if ("methodType".equals(propertyId)) {
            
            methodSelectType = new Select();
            
            messageSource.setCaption(methodSelectType, Message.BREED_METH_TYPE);
            methodSelectType.addItem("GEN");
            methodSelectType.setItemCaption("GEN", "Generative");
            methodSelectType.addItem("DER");
            methodSelectType.setItemCaption("DER", "Derivative");
            methodSelectType.addItem("MAN");
            methodSelectType.setItemCaption("MAN", "Maintenance");
            methodSelectType.select("GEN");
            methodSelectType.setNullSelectionAllowed(false);
                
            return methodSelectType;
        } else if ("methodGroup".equals(propertyId)) {
            
            methodSelectGroup = new Select();
            
            messageSource.setCaption(methodSelectGroup, Message.BREED_METH_GRP);
            methodSelectGroup.addItem("S");
            methodSelectGroup.setItemCaption("S", "Self Fertilizing");
            methodSelectGroup.addItem("O");
            methodSelectGroup.setItemCaption("O", "Cross Pollinating");
            methodSelectGroup.addItem("C");
            methodSelectGroup.setItemCaption("C", "Clonally Propagating");
            methodSelectGroup.addItem("G");
            methodSelectGroup.setItemCaption("G", "All System");
            methodSelectGroup.select("");
            methodSelectGroup.setNullSelectionAllowed(false);
                
            return methodSelectGroup;
        }
        
        return field;
    }
}

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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
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
    private ComboBox comboBox;
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
        
        methodDescription = new TextField();
        methodDescription.setRequired(true);
        methodDescription.setRequiredError("Please enter a Breeding Method Description.");
        methodDescription.addValidator(new StringLengthValidator("Breeding Method Description must be 1-255 characters.", 1, 255, false));
        
        methodCode = new TextField();
        methodCode.setRequired(true);
        methodCode.setRequiredError("Please enter a Breeding Method Code.");
        methodCode.addValidator(new StringLengthValidator("Breeding Method Code must be 1-8 characters.", 1, 8, false));
        


        
        //methodTypeComboAction = new MethodTypeComboAction();


    }
    
/*    public MethodTypeComboAction getMethodTypeComboAction() {
        return methodTypeComboAction;
    }*/

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
        }else if ("methodType".equals(propertyId)) {
            
            comboBox = new ComboBox();
            
            messageSource.setCaption(comboBox, Message.BREED_METH_TYPE);
            
            List<String> methodTypes = new ArrayList<String>();
         
            methodTypes.add("DER");
            methodTypes.add("MAN");
            methodTypes.add("GEN");
            
            BeanItemContainer<String> beanItemContainer = new BeanItemContainer<String>(String.class);
            for (String methodType : methodTypes) {
                beanItemContainer.addBean(methodType);
            }

            comboBox.setContainerDataSource(beanItemContainer);
            comboBox.setNewItemsAllowed(false);
            
            //comboBox.setItemCaptionPropertyId("methodType");
            comboBox.setRequired(true);
            comboBox.setRequiredError("Please select a Breeding Method Type.");
            comboBox.setImmediate(true);
                
            return comboBox;
        }
        
        return field;
    }
}

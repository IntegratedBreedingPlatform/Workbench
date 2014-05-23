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
package org.generationcp.ibpworkbench.validator;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.Field;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


/**
 * <b>Description</b>: Tests if a Persons record with the same First Name and Last Name already exists.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 22, 2012
 */
@Configurable
public class PersonNameValidator extends AbstractValidator{

    private static final long serialVersionUID = 4065915808146235650L;
    
    private static final Logger LOG = LoggerFactory.getLogger(PersonNameValidator.class);
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
   
    private Field firstName;
    private Field lastName;
    
    public PersonNameValidator(Field firstName, Field lastName) {
        super("Person with First Name \"{0}\" and Last Name \"{1}\" already exists.");
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    @Override
    public void validate(Object value) throws InvalidValueException {
        if (!isValid(value)) {
            String message = getErrorMessage()
                .replace("{0}", firstName.getValue().toString())
                .replace("{1}", lastName.getValue().toString());
            throw new InvalidValueException(message);
        }
    }

    @Override
    public boolean isValid(Object value) {
    	int person_counter;
    	IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
    	person_counter = app.getSessionData().getNamevalidation_counter();
    	person_counter++;
    	app.getSessionData().setNamevalidation_counter(person_counter);
    	
    	
    	if(person_counter > 2) 
    	{
    		app.getSessionData().setNamevalidation_counter(0);
    		person_counter = 0;
    		return true;
    	}
    	
    	
        try {
            return !workbenchDataManager.isPersonExists(
                    firstName.getValue().toString(), lastName.getValue().toString());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
//            return false;
        }
    }
    
}

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

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.WorkbenchManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.Field;


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
    private WorkbenchManagerFactory workbenchManagerFactory;
    
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
        try {
            return !workbenchManagerFactory.getWorkBenchDataManager().isPersonExists(
                    firstName.getValue().toString(), lastName.getValue().toString());
        } catch (QueryException e) {
            LOG.error(e.getMessage());
            return false;
        }
    }
    
}

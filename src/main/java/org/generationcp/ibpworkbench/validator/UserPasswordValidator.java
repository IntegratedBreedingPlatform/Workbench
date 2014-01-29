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

import org.generationcp.commons.util.StringUtil;

import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.ui.Field;


/**
 * <b>Description</b>: Tests if the password and password confirmation entered are equal.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 22, 2012
 */
public class UserPasswordValidator extends AbstractValidator{

    private static final long serialVersionUID = 7701189305820452658L;
    
    private Field password;
    private Field passwordConfirmation;
    
    public UserPasswordValidator(Field password, Field passwordConfirmation) {
        super("The passwords entered do not match. Please try again.");
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    @Override
    public boolean isValid(Object value) {
        if(!StringUtil.isEmpty(password.getValue().toString()) && 
           !StringUtil.isEmpty(passwordConfirmation.getValue().toString())) {
            return password.getValue().equals(passwordConfirmation.getValue());
        }
        
        //Validation will fail on StringLenghtValidator and field.setRequired(true) password fields
        //are empty
        return true;
    }

}

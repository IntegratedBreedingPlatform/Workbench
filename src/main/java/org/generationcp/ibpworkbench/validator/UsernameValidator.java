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
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.validator.AbstractValidator;


/**
 * <b>Description</b>: Tests if a Users record with the same Username already exists.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 22, 2012
 */
@Configurable
public class UsernameValidator extends AbstractValidator{
    
    private static final long serialVersionUID = -1537885028422014862L;

    private static final Logger LOG = LoggerFactory.getLogger(UsernameValidator.class);
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public UsernameValidator() {
        super("User with Username \"{0}\" already exists.");
    }

    @Override
    public boolean isValid(Object value) {
        try {
            return !workbenchDataManager.isUsernameExists(value.toString());
        } catch (QueryException e) {
            if(LOG.isErrorEnabled()) {
                LOG.error(e.getMessage());
            }
            return false;
        }
    }

}

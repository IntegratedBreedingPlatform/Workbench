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

import com.vaadin.ui.Layout;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.model.formfieldfactory.UserAccountFormFieldFactory;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;


/**
 * <b>Description</b>: Custom form for registering a user account.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 11, 2012
 */
public class UserAccountForm extends Form {

    private static final long serialVersionUID = -7726164779128415602L;
    
    private BeanItem<UserAccountModel> userBean;
    
    private UserAccountModel userAccount;
    
    private GridLayout grid;
    
    public UserAccountForm(UserAccountModel userAccount) {
        this.userAccount = userAccount;
        
        assemble();
    }

    protected void assemble() {
        
        initializeComponents();
        initializeLayout();
    }

    protected void initializeComponents() { 
        
    	setImmediate(false);
        grid = new GridLayout(4, 7);
        grid.setSpacing(true);
        grid.setMargin(new Layout.MarginInfo(true,true,true,true));
        
        setLayout(grid);
        
        userBean = new BeanItem<UserAccountModel>(userAccount);
        
        setItemDataSource(userBean);
        setComponentError(null);
        setFormFieldFactory(new UserAccountFormFieldFactory());
        setVisibleItemProperties(Arrays.asList(
                new String[] { "firstName", "middleName", "lastName", "email", 
                               "username", "password", "passwordConfirmation",
                               "securityQuestion", "securityAnswer"}));
        
        setWriteThrough(false);
        setInvalidCommitted(false);
        
    }
    
    protected void initializeLayout() {
        
    }
    
    @Override
    protected void attachField(Object propertyId, Field field) {
        
        /*if("positionTitle".equals(propertyId)) {
            grid.addComponent(field, 0, 0);
        } else */
        if ("firstName".equals(propertyId)) {
            grid.addComponent(field, 0, 0);
        } else if ("middleName".equals(propertyId)) {
            grid.addComponent(field, 1, 0);
        } else if ("lastName".equals(propertyId)) {
            grid.addComponent(field, 2, 0);
        } else if ("email".equals(propertyId)) {
            grid.addComponent(field, 0, 1);
        } else if ("username".equals(propertyId)) {
            grid.addComponent(field, 0, 2);
        } else if ("password".equals(propertyId)) {
            grid.addComponent(field, 0, 3);
        } else if ("passwordConfirmation".equals(propertyId)) {
            grid.addComponent(field, 0, 4);
        } else if ("securityQuestion".equals(propertyId)) {
            grid.addComponent(field, 0, 5, 2, 5);
        } else if ("securityAnswer".equals(propertyId)) {
            grid.addComponent(field, 0, 6, 2, 6);
        } 
    }
    
}

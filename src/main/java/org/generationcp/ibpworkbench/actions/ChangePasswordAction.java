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

package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenWindowAction.WindowEnum;
import org.generationcp.ibpworkbench.comp.form.LoginForm;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.generationcp.ibpworkbench.comp.window.WorkbenchDashboardWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

@Configurable
public class ChangePasswordAction implements ClickListener{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ChangePasswordAction.class);

    private String username;
    private PasswordField confirm;
    private PasswordField password;
    
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public ChangePasswordAction(String username, PasswordField password, PasswordField confirm) 
    {
    	this.username = username;
    	this.password = password;
    	this.confirm = confirm;
    	
    	System.out.println("ChangePasswordAction " + password + " : "+ confirm);
	}
 
    @Override
    public void buttonClick(ClickEvent event) {
    	
    	System.out.println("click " + this.password.getValue().toString() + " : "+ this.confirm.getValue().toString());
    	
        try{
        	if(!this.password.getValue().toString().equals(this.confirm.getValue().toString()))
        	{
        		MessageNotifier.showError(event.getComponent().getWindow(), 
                        "Invalid Password", 
                        "<br />" + "Password must be the same as confirm password");
                return;
        	}
        	
        	if(this.password.getValue().toString().equalsIgnoreCase("") || this.password.getValue().toString().equalsIgnoreCase(" "))
        	{
        		MessageNotifier.showError(event.getComponent().getWindow(), 
                        "" +
                        "Password Cannot Be Blank", 
                        "<br />" + "Password cannot be blank");
                return;
        	}
        	if(workbenchDataManager.changeUserPassword(username, password.getValue().toString()))
        	{
        		MessageNotifier.showMessage(event.getComponent().getWindow(), "Success", "Successfully changed password");
        	}
        	else
        	{
        		MessageNotifier.showError(event.getComponent().getWindow(), 
                        "Invalid Password", 
                        "<br />" + "Password must be the same as confirm password");
        	}
        	
        } catch (Exception e) {
            LOG.error("Error encountered while trying to login", e);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.LOGIN_ERROR), 
                    "<br />" + messageSource.getMessage(Message.LOGIN_DB_ERROR_DESC));
            return;
        }
    }
}

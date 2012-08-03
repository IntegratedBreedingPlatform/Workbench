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
import org.generationcp.ibpworkbench.ApplicationMetaData;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.form.LoginForm;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.generationcp.ibpworkbench.comp.window.WorkbenchDashboardWindow;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

@Configurable
public class LoginAction implements ClickListener{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(LoginAction.class);

    private LoginWindow loginWindow;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public LoginAction(LoginWindow loginWindow) {
        this.loginWindow = loginWindow;

        loginWindow.getLoginForm().getBtnLogin().addListener(this);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        LoginForm loginForm = loginWindow.getLoginForm();

        TextField txtEmailAddress = loginForm.getTxtEmailAddress();
        PasswordField pfPassword = loginForm.getPfPassword();
        
        String username = (String) txtEmailAddress.getValue();
        String password = (String) pfPassword.getValue();
        
        LOG.trace("Login with " + txtEmailAddress.getValue() + "/" + pfPassword.getValue());
        
        boolean valid = false;
        try {
            valid = workbenchDataManager.isValidUserLogin(username, password);
        } catch (QueryException e) {
            LOG.error("Error encountered while trying to login", e);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.LOGIN_ERROR), 
                    "<br />" + messageSource.getMessage(Message.LOGIN_DB_ERROR_DESC));
            return;
        }
        
        if (!valid) {
            // loginForm.getMessageLabel().setVisible(true);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.LOGIN_ERROR), 
                    "<br />" + messageSource.getMessage(Message.error_login_invalid));
            return;
        }
        
        IBPWorkbenchApplication application = (IBPWorkbenchApplication) event.getComponent().getApplication();
        
        
        // Create the application data instance
           ApplicationMetaData sessionData = new ApplicationMetaData(application);
           
        //TODO: Verify the try-catch flow
        try {
            ApplicationMetaData.setUserData(workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL).get(0));
        } catch (QueryException e) {
            LOG.error("Error encountered while trying to login", e);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
        }

        // Register it as a listener in the application context
        application.getContext().addTransactionListener(sessionData);
       
        // Also set the user data model
        //ApplicationMetaData.setUserData(userDataManager.get);
        
        WorkbenchDashboardWindow window = null;
        try {
            window = new WorkbenchDashboardWindow();
            application.removeWindow(application.getMainWindow());
            application.setMainWindow(window);
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(application.getMainWindow(),
                        i.getCaption(), i.getDescription());
            }
            return;
        }
    }
}

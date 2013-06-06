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
import org.generationcp.ibpworkbench.comp.form.LoginForm;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.generationcp.ibpworkbench.comp.window.WorkbenchDashboardWindow;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
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
    
    @Autowired
    private ToolUtil toolUtil;

    public LoginAction(LoginWindow loginWindow) {
        this.loginWindow = loginWindow;

        loginWindow.getLoginForm().getBtnLogin().addListener(this);
    }
 
    @Override
    public void buttonClick(ClickEvent event) {
        LoginForm loginForm = loginWindow.getLoginForm();

        TextField txtUsername = loginForm.getTxtUsername();
        PasswordField pfPassword = loginForm.getPfPassword();
        
        String username = ((String) txtUsername.getValue()).trim();
        String password = ((String) pfPassword.getValue()).trim();
        
        LOG.trace("Login with " + txtUsername.getValue() + "/" + pfPassword.getValue());
        
        boolean valid = false;
        try {
            valid = workbenchDataManager.isValidUserLogin(username, password);
        } catch (MiddlewareQueryException e) {
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
                    "<br />" + messageSource.getMessage(Message.ERROR_LOGIN_INVALID));
            return;
        }
        
        IBPWorkbenchApplication application = (IBPWorkbenchApplication) event.getComponent().getApplication();
        
        User user = null;
        try {
            // set the session's current user
            user = workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL).get(0);
            application.getSessionData().setUserData(user);

            // save the currently logged user
            WorkbenchRuntimeData data = workbenchDataManager.getWorkbenchRuntimeData();
            if (data == null) {
                data = new WorkbenchRuntimeData();
            }
            data.setUserId(user.getUserid());
            
            workbenchDataManager.updateWorkbenchRuntimeData(data);
        }
        catch (MiddlewareQueryException e) {
            LOG.error("Database error encountered", e);
            MessageNotifier.showError(event.getComponent().getWindow(), 
                    messageSource.getMessage(Message.DATABASE_ERROR), 
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        
        WorkbenchDashboardWindow newWindow = null;
        try {
            newWindow = new WorkbenchDashboardWindow();
            //application.removeWindow(application.getMainWindow());
            
            
            application.getMainWindow().open(new ExternalResource(application.getURL()));
            
            application.setMainWindow(newWindow);
            
            
            Project project = workbenchDataManager.getLastOpenedProject(user.getUserid());
            if (project != null) {
                toolUtil.updateTools(newWindow, messageSource, project, false);
            }
            
            
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

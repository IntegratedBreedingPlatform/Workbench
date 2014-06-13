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

import javax.servlet.http.Cookie;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.window.LoginWindow;
import org.generationcp.ibpworkbench.util.CookieUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.WorkbenchRuntimeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;

@Configurable
public class LoginPresenter {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(LoginPresenter.class);
    private static LoginPresenter self;

    private LoginWindow loginWindow;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;
    
    public LoginPresenter(LoginWindow loginWindow) {
        this.loginWindow = loginWindow;
        this.self = this;
    }

    public static LoginPresenter getLoginActionInstance() {
        return self;
    }

    public void doLogin(String username,String password,ClickEvent event) {
        LOG.debug("Login with " + username + "/" + password);

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

        
        User user = null;
        try {
            // set the session's current user
            user = workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL).get(0);
            user.setPerson(workbenchDataManager.getPersonById(user.getPersonid()));
            
            sessionData.setUserData(user);
            
            // set the cookie if remember me option is enabled

            if (loginWindow.isRememberEnabled()) {
            	LOG.debug("COOKIE: Remember Option is enabled");
            	
            	CookieUtils.setupCookies(
                        new Cookie(CookieUtils.LoginCookieProperties.REMEMBER_OPT, "true"),
                        new Cookie(CookieUtils.LoginCookieProperties.USERNAME, user.getName()),
                        new Cookie(CookieUtils.LoginCookieProperties.PASSWORD, user.getPassword())
                );
            } else {
                CookieUtils.setupCookies(
                        new Cookie(CookieUtils.LoginCookieProperties.REMEMBER_OPT,"false"),
                        new Cookie(CookieUtils.LoginCookieProperties.USERNAME,""),
                        new Cookie(CookieUtils.LoginCookieProperties.PASSWORD,"")
                );
            	//CookieUtils.removeCookies(LoginCookieProperties.REMEMBER_OPT,LoginCookieProperties.USERNAME,LoginCookieProperties.PASSWORD);
            }

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


        /* DIRECT TO THE MAIN DASHBOARD PAGE */
        WorkbenchMainView newWindow = null;
        try {
            newWindow = new WorkbenchMainView();
            //application.removeWindow(application.getMainWindow());

            event.getComponent().getApplication().getMainWindow().open(new ExternalResource(event.getComponent().getApplication().getURL()));

            event.getComponent().getApplication().setMainWindow(newWindow);
            
            // we used to update the tool configurations here
            // but we don't need it anymore
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(event.getComponent().getApplication().getMainWindow(),
                        i.getCaption(), i.getDescription());
            }
        }

    }
}

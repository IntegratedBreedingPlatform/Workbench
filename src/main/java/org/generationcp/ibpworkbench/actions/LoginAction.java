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

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.form.LoginForm;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.generationcp.ibpworkbench.comp.window.WorkbenchDashboardWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class LoginAction implements ClickListener{

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(LoginAction.class);

    private LoginWindow loginWindow;

    public LoginAction(LoginWindow loginWindow) {
        this.loginWindow = loginWindow;

        loginWindow.getLoginForm().getBtnLogin().addListener(this);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        LoginForm loginForm = loginWindow.getLoginForm();

        TextField txtEmailAddress = loginForm.getTxtEmailAddress();
        PasswordField pfPassword = loginForm.getPfPassword();

        LOG.trace("Login with " + txtEmailAddress.getValue() + "/" + pfPassword.getValue());

        IBPWorkbenchApplication application = (IBPWorkbenchApplication) event.getComponent().getApplication();
        application.removeWindow(application.getMainWindow());
        WorkbenchDashboardWindow window = new WorkbenchDashboardWindow();
        application.setMainWindow(window);

        NavManager.navigateApp(window, "/home", true);
    }
}

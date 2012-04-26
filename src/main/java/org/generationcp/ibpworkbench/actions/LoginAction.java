package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.form.LoginForm;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class LoginAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    private LoginForm loginForm;
    
    public LoginAction(LoginForm loginForm) {
        this.loginForm = loginForm;
        
        loginForm.getBtnLogin().addListener(this);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        TextField txtEmailAddress = loginForm.getTxtEmailAddress();
        PasswordField pfPassword = loginForm.getPfPassword();
        
        loginForm.getWindow().showNotification("Login with " + txtEmailAddress.getValue() + "/" + pfPassword.getValue());
        
        IBPWorkbenchApplication application = (IBPWorkbenchApplication) loginForm.getApplication();
        application.removeWindow(application.getLoginWindow());
        application.setMainWindow(application.getDashboardWindow());
    }

}

package org.generationcp.ibpworkbench.comp.window;

import org.generationcp.ibpworkbench.comp.form.LoginForm;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class LoginWindow extends Window {
    private static final long serialVersionUID = 1L;
    
    private LoginForm loginForm;

    public LoginWindow() {
        loginForm = new LoginForm();
        loginForm.setSizeUndefined();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        
        layout.addComponent(loginForm);
        layout.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);
        
        addComponent(layout);
    }
    
    public LoginForm getLoginForm() {
        return loginForm;
    }
}

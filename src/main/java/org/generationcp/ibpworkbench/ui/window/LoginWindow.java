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

package org.generationcp.ibpworkbench.ui.window;

import org.generationcp.ibpworkbench.ui.form.LoginForm;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class LoginWindow extends Window{

    private static final long serialVersionUID = 1L;
    
   
    private LoginForm loginForm;

    public LoginWindow() {
    	super("Integrated Breeding Platform | Workbench");
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

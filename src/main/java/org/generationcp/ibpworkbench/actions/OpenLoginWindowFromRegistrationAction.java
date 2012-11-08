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

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.Application;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;


/**
 * <b>Description</b>: Displays the workbench login window after successfully saving a new User Account.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Mark Agarrado
 * <br>
 * <b>File Created</b>: Nov 8, 2012
 */
@Configurable
public class OpenLoginWindowFromRegistrationAction implements ClickListener{
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private static final long serialVersionUID = 5784289264247702925L;

    @Override
    public void buttonClick(ClickEvent event) {
        LoginWindow window = new LoginWindow();
        new LoginAction(window);
        Application app = event.getComponent().getApplication();
        
        app.removeWindow(app.getMainWindow());
        app.setMainWindow(window);
        
        MessageNotifier.showWarning(window, 
                messageSource.getMessage(Message.REGISTER_SUCCESS),
                messageSource.getMessage(Message.REGISTER_SUCCESS_DESCRIPTION));
    }

}

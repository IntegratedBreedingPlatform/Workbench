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

import java.util.Iterator;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.form.LoginForm;
import org.generationcp.ibpworkbench.comp.window.LoginWindow;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;


/**
 * <b>Description</b>: Displays the workbench login window.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jul 22, 2012
 */
public class OpenLoginWindowAction implements ClickListener{

    private static final long serialVersionUID = 5784289264247702925L;
    
    @Override
    public void buttonClick(ClickEvent event) {
    	event.getComponent().getApplication().close(); // closes the app then reloads if logout url is not set
    
    	//LoginWindow window = new LoginWindow();
        //new LoginAction(window);
        //Application app = event.getComponent().getApplication();
        
        //window.setWidth("100%");
        //window.center();
        //window.setPositionY(0);
        //window.setClosable(false);
        //window.setDraggable(false);
        //window.setHeight("100%");
        //window.setBorder(0);       
        
        //app.getMainWindow().removeAllComponents();
        //app.getMainWindow().addWindow(window);
        
        //app.removeWindow(app.getMainWindow());
        //app.setMainWindow(window);
    }

}

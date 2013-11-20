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

import com.vaadin.ui.themes.Reindeer;
import org.generationcp.ibpworkbench.ui.ForgotPasswordUsernamePanel;

import com.vaadin.ui.Window;

public class SecurityQuestionWindow extends Window {
    
    private static final long serialVersionUID = 3983198771242295731L;

    public SecurityQuestionWindow() {
        super("Integrated Breeding Platform | Workbench");
        // set as modal window, other components are disabled while window is open
        setModal(true);

        // define window size, set as not resizable
        setWidth("450px");
        setHeight("160px");
        setResizable(false);
        
        // center window within the browser
        center();
        
        assemble();
        this.addStyleName(Reindeer.WINDOW_LIGHT);
        setCaption("Retrieve Forgotten Password");
    }
    
    protected void initializeComponents() {
        ForgotPasswordUsernamePanel panel = new ForgotPasswordUsernamePanel();
        setContent(panel);
    }

    protected void initializeLayout() {
        
    }
    
    protected void initializeActions() {
        
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
}


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

import org.generationcp.ibpworkbench.comp.CreateNewProjectPanel;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

public class OpenNewProjectAction implements ClickListener, ActionListener{
    private static final long serialVersionUID = 1L;

    @Override
    public void buttonClick(ClickEvent event) {
        doAction(event.getComponent().getWindow(), null);
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment) {
        IContentWindow w = (IContentWindow) window;
        
        CreateNewProjectPanel newProjectPanel = new CreateNewProjectPanel();
        newProjectPanel.setWidth("480px");
        
        w.showContent(newProjectPanel);
        
        newProjectPanel.getSaveButton().addListener(new SaveNewProjectAction(newProjectPanel.getForm()));
        
        NavManager.navigateApp(window, "/createProject");
    }

}

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

import org.generationcp.ibpworkbench.comp.ContactBookPanel;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * <b>Description</b>: Listener class for generating Create Contact view.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jun 11, 2012.
 */
public class CreateContactAction implements ClickListener, ActionListener {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2150231810445628892L;

    /**
     * Button click.
     *
     * @param event the event
     */
    @Override
    public void buttonClick(ClickEvent event) {
        doAction(event.getComponent().getWindow(), null);
    }

    /**
     * Do action.
     *
     * @param event the event
     */
    @Override
    public void doAction(Event event) {
       NavManager.breadCrumbClick(this, event);
    }

    /**
     * Do action.
     *
     * @param window the window
     * @param uriFragment the uri fragment
     */
    @Override
    public void doAction(Window window, String uriFragment) {
        ContactBookPanel contactBookPanel = new ContactBookPanel();
        IContentWindow w = (IContentWindow) window;
        
        w.showContent(contactBookPanel);
        
        NavManager.navigateApp(window, "/home/createContacts");
    }

}

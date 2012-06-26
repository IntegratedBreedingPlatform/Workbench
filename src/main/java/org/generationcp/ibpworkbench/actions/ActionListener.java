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

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

/**
 * <b>Description</b>: Interface for invoking an action for the different
 * Vaadin listener types.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jun 14, 2012
 */
public interface ActionListener {
    
    //
    /**
     * Used by bread crumbs. Invoked when a bread crumb is clicked.
     *
     * @param event the event
     */
    public void doAction(Component.Event event);
    
    //
    /**
     * Used by NavUriFragmentChangedListener
     *
     * @param window the window
     * @param uriFragment the uri fragment
     */
    public void doAction(Window window, String uriFragment);
}

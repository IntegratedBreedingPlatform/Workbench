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
package org.generationcp.ibpworkbench.navigation;

import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.comp.window.WorkbenchDashboardWindow;

import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * <b>Description</b>: This class contains convenience methods for 
 * navigation management.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jun 11, 2012.
 */
public final class NavManager {
    
    private NavManager() {}

    /**
     * Convenience method for updating the crumbtrail and for setting the URI fragment.
     *
     * @param window the window
     * @param viewId the view id
     */
    public static void navigateApp(Window window, String viewId) {
        navigateApp(window, viewId, null);
    }
    
    /**
     * Convenience method for updating the crumbtrail and for setting the URI fragment.
     *
     * @param window the window
     * @param viewId the view id
     * @param breadCrumbLabel the bread crumb label
     */
    public static void navigateApp(Window window, String viewId, String breadCrumbLabel) {
        WorkbenchDashboardWindow workbenchWindow = (WorkbenchDashboardWindow) window;
        CrumbTrail crumbTrail = workbenchWindow.getCrumbTrail();
        
        if(!crumbTrail.getLastBreadCrumbUri().equals(viewId)) {
            crumbTrail.setLinkAccessed(true);
            
            workbenchWindow.setUriFragment(viewId);
            crumbTrail.updateCrumbTrail(viewId, breadCrumbLabel);
        } else {
            //page refresh is handled in Listener implementation
        }
    }
    
    /**
     * Convenience method for calling ActionListener.doAction(window, uriFragment)
     *
     * @param listener the listener
     * @param event the event
     */
    public static void breadCrumbClick(ActionListener listener, Event event) {
        BreadCrumb b = (BreadCrumb) event.getComponent().getParent();
        
        listener.doAction(b.getWindow(), b.getUriFragment());
//        navigateApp(event, b.getUriFragment());
    }

}

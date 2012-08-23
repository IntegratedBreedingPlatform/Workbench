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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.WorkbenchDashboard;
import org.generationcp.ibpworkbench.comp.window.WorkbenchDashboardWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * <b>Description</b>: Listener class for generating the home page view.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jun 11, 2012.
 */
public class HomeAction implements ClickListener, ActionListener{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5592156945270416052L;
    
    private static final Logger LOG = LoggerFactory.getLogger(HomeAction.class);
    
    /**
     * Button click.
     *
     * @param event the event
     */
    @Override
    public void buttonClick(ClickEvent event) {
        Window window = event.getComponent().getWindow();
        doAction(window, "/Home", true);
        
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
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        // we create a new WorkbenchDashboard object here
        // so that the UI is reset to its initial state
        // we can remove this if we want to present the last UI state.
        WorkbenchDashboardWindow w = (WorkbenchDashboardWindow) window;
        WorkbenchDashboard workbenchDashboard = null;
        try {
            workbenchDashboard = new WorkbenchDashboard();
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(window, i.getCaption(), i.getDescription());
            }
            return;
        }
        
        workbenchDashboard.setProjectThumbnailClickHandler(new OpenProjectDashboardAction());
        workbenchDashboard.addProjectTableListener(new OpenProjectDashboardAction());
        
        w.setWorkbenchDashboard(workbenchDashboard);
        
        w.showContent(w.getWorkbenchDashboard());
        
        NavManager.navigateApp(window, "/Home", isLinkAccessed);
    }

}

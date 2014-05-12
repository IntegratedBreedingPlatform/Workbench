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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
@Configurable
public final class NavManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(NavManager.class);
    
    @Autowired
    private static SimpleResourceBundleMessageSource messageSource;
    
    private NavManager() {}

    /**
     * Convenience method for updating the crumbtrail and for setting the URI fragment.
     *
     * @param window the window
     * @param viewId the view id
     */
    public static void navigateApp(Window window, String viewId, boolean isLinkAccessed) {
        navigateApp(window, viewId, isLinkAccessed, null);
    }
    
    /**
     * Convenience method for updating the crumbtrail and for setting the URI fragment.
     *
     * @param window the window
     * @param viewId the view id
     * @param breadCrumbLabel the bread crumb label
     */
    public static void navigateApp(Window window, String viewId, boolean isLinkAccessed, String breadCrumbLabel) {
        if (!(window instanceof WorkbenchMainView))
            return;

        WorkbenchMainView workbenchWindow = (WorkbenchMainView) window;
        CrumbTrail crumbTrail = null;
        
        if(crumbTrail.getCrumbTrailList().isEmpty() || !crumbTrail.getLastBreadCrumbUri().equals(viewId)) {
            if(isLinkAccessed) {
                crumbTrail.setLinkAccessed(true);
            }
            
            workbenchWindow.setUriFragment(viewId);
            
            try {
                crumbTrail.updateCrumbTrail(viewId, breadCrumbLabel);
            } catch (NumberFormatException e) {
                LOG.error("NumberFormatException", e);
                showConfigError(window);
            } catch (InternationalizableException e){
                LOG.error("InternationalizableException", e);
                MessageNotifier.showError(window, e.getCaption(), e.getDescription());
            } catch (Exception e) {
                LOG.error("Exception", e);
                if(e.getCause() instanceof InternationalizableException) {
                    InternationalizableException i = (InternationalizableException) e.getCause(); 
                    MessageNotifier.showError(window, i.getCaption(), i.getDescription());
                } else {
                    showConfigError(window);
                }
            }
        } else {
            //page refresh is handled in Listener implementation
        }
    }
    
    private static void showConfigError(Window window) {
        MessageNotifier.showError(window, messageSource.getMessage(Message.CONFIG_ERROR),
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
    
    /**
     * Convenience method for calling ActionListener.doAction(window, uriFragment)
     *
     * @param listener the listener
     * @param event the event
     */
    public static void breadCrumbClick(ActionListener listener, Event event) {
        BreadCrumb b = (BreadCrumb) event.getComponent().getParent();
        
        listener.doAction(b.getWindow(), b.getUriFragment(), true);
//        navigateApp(event, b.getUriFragment());
    }

}

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

import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedListener;
import sun.rmi.runtime.Log;

/**
 * <b>Description</b>: Handles URI fragment changes for the application and calls mapped 
 * Listeners for the view. Browser back button, bookmark access and manual user input on 
 * the address bar events are handled by this listener.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Michael Blancaflor
 * <br>
 * <b>File Created</b>: Jun 13, 2012.
 *
 */
@Configurable
public class NavUriFragmentChangedListener implements FragmentChangedListener {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -504911617538850779L;
    private static final Logger LOG = LoggerFactory.getLogger(NavUriFragmentChangedListener.class);
    
    /** The nav xml parser. */
    private NavXmlParser navXmlParser;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private WorkbenchDataManager manager;


    /**
     * Instantiates a new nav uri fragment changed listener.
     */
    public NavUriFragmentChangedListener() throws InternationalizableException {
        navXmlParser = new NavXmlParser();
    }

    /**
     * Handles URI change event.
     *
     * @param source the source
     */
    @Override
    public void fragmentChanged(FragmentChangedEvent source) {
        UriFragmentUtility u = source.getUriFragmentUtility();
        LOG.debug("Workbench Fragment updated. Fragment[" + u.getFragment() + "]");
    }
    
}

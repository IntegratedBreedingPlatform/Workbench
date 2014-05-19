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
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.project.create.AddProgramPanel;
import org.generationcp.ibpworkbench.ui.project.create.CreateProjectPanel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class OpenNewProjectAction implements ClickListener, ActionListener{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(OpenNewProjectAction.class);

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Override
    public void buttonClick(ClickEvent event) {
    	doAction(event.getComponent().getWindow(), null, true);
    }

    @Override
    public void doAction(Event event) {
        //NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        final IContentWindow w = (IContentWindow) window;

        try {
        	AddProgramPanel newProjectPanel = new AddProgramPanel() {
				private static final long serialVersionUID = 1640109693569711793L;

				@Override
                public void detach() {
                    super.detach();

                }
            };

            w.showContent(newProjectPanel);

            if (w instanceof WorkbenchMainView) {
                ((WorkbenchMainView)w).addTitle("");
            }
            
            //NavManager.navigateApp(window, "/CreateProject", isLinkAccessed);
        } catch (Exception e) {
            LOG.error("Exception", e);
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(window, i.getCaption(), i.getDescription());
            }
            return;
        }
        
    }

}

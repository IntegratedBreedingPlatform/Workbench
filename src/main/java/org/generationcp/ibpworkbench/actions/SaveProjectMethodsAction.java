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
import org.generationcp.ibpworkbench.comp.ProjectBreedingMethodsPanel;
import org.generationcp.ibpworkbench.comp.ProjectLocationPanel;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * @author Joyce Avestro
 * 
 */
@Configurable
public class SaveProjectMethodsAction implements ClickListener, ActionListener{

    private static final Logger LOG = LoggerFactory.getLogger(SaveProjectMethodsAction.class);
    private static final long serialVersionUID = 1L;

	private ProjectBreedingMethodsPanel projectBreedingMethodsPanel;

    public SaveProjectMethodsAction(ProjectBreedingMethodsPanel projectBreedingMethodsPanel) {
        this.projectBreedingMethodsPanel = projectBreedingMethodsPanel;
    }

    @Override
    public void buttonClick(ClickEvent event) {
    	projectBreedingMethodsPanel.validateAndSave();
        doAction(event.getComponent().getWindow(), null, true);
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
        try {
            Project project = projectBreedingMethodsPanel.getProject();
            Role role = projectBreedingMethodsPanel.getRole();

            String url = String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", project.getProjectId(), role.getRoleId());
            (new OpenWorkflowForRoleAction(projectBreedingMethodsPanel.getProject())).doAction(window, url, isLinkAccessed);
        } catch (Exception e) {
            LOG.error("Exception", e);
            if (e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(window, i.getCaption(), i.getDescription());
            }
            return;
        }

    }

}

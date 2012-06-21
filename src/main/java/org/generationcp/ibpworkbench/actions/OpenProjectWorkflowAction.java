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

import org.generationcp.ibpworkbench.comp.MarsProjectDashboard;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.model.provider.IProjectProvider;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;

public class OpenProjectWorkflowAction implements LayoutClickListener{

    private static final long serialVersionUID = 1L;

    @Override
    public void layoutClick(LayoutClickEvent event) {
        
        Component component = event.getComponent();

        IProjectProvider projectProvider = (IProjectProvider) component;
        Project project = projectProvider.getProject();

        if (project == null)
            return;

        IContentWindow window = (IContentWindow) component.getWindow();

        // TODO: create a project dashboard based on the project's workflow type
        MarsProjectDashboard projectDashboard = new MarsProjectDashboard(project);
        window.showContent(projectDashboard);
    }
}

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

import org.generationcp.ibpworkbench.comp.ProjectDashboard;
import org.generationcp.ibpworkbench.comp.ProjectThumbnailPanel;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.ui.Component;

public class OpenProjectDashboardAction implements ItemClickListener, MouseEvents.ClickListener{

    private static final long serialVersionUID = 1L;

    @Override
    public void itemClick(ItemClickEvent event) {
        
        Component component = event.getComponent();

        IContentWindow window = (IContentWindow) component.getWindow();

        @SuppressWarnings("unchecked")
        BeanItem<Project> item = (BeanItem<Project>) event.getItem();

        Project project = item.getBean();
        if (project == null) {
            return;
        }

        ProjectDashboard projectDashboard = new ProjectDashboard(project);
        projectDashboard.addProjectThumbnailPanelListener(new OpenProjectWorkflowAction());

        window.showContent(projectDashboard);
    }

    @Override
    public void click(ClickEvent event) {
        
        Component component = event.getComponent();
        IContentWindow window = (IContentWindow) component.getWindow();

        Project project = (Project) ((ProjectThumbnailPanel) component).getData();
        if (project == null) {
            return;
        }

        ProjectDashboard projectDashboard = new ProjectDashboard(project);
        projectDashboard.addProjectThumbnailPanelListener(new OpenProjectWorkflowAction());

        window.showContent(projectDashboard);
    }
}

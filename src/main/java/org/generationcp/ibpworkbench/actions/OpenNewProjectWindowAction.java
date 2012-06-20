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

import org.generationcp.ibpworkbench.comp.form.ProjectFormPanel;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ListenerMethod.MethodException;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class OpenNewProjectWindowAction implements Command, ClickListener{

    private static final long serialVersionUID = 1L;

    private Window window;

    public OpenNewProjectWindowAction(Window window) {
        this.window = window;
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
        doAction();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        doAction();
    }

    protected void doAction() {
        final Window window = new Window("New Project");
        window.setModal(true);
        window.setWidth("400px");
        window.setHeight("260px");

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);

        final ProjectFormPanel formPanel = new ProjectFormPanel(new BeanItem<Project>(new Project()));
        formPanel.setSizeFull();
        layout.addComponent(formPanel);

        formPanel.getSaveButton().addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    formPanel.getProjectForm().commit();

                    window.getParent().removeWindow(window);
                } catch (MethodException e) {
                    e.printStackTrace();
                }
            }
        });
        formPanel.getCancelButton().addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                window.getParent().removeWindow(window);
            }
        });

        window.setContent(layout);

        this.window.addWindow(window);
    }
}

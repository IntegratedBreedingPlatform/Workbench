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

package org.generationcp.ibpworkbench.comp.form;

import java.util.Arrays;

import org.generationcp.ibpworkbench.model.formfieldfactory.ProjectFormFieldFactory;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class ProjectFormPanel extends VerticalLayout{

    private static final long serialVersionUID = 1L;

    private BeanItem<Project> project;

    private DefaultProjectForm projectForm;

    private Button cancelButton;

    private Button saveButton;

    public ProjectFormPanel(BeanItem<Project> project) {
        this.project = project;

        assemble();
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Form getProjectForm() {
        return projectForm;
    }

    protected void initializeComponents() {
        // add the form
        projectForm = new DefaultProjectForm();
        projectForm.setItemDataSource(project);
        addComponent(projectForm);

        // add the save/cancel buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        saveButton = new Button("Save");
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveButton);

        addComponent(buttonLayout);
        setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
    }

    protected void initializeActions() {

    }

    protected void assemble() {
        initializeComponents();
        initializeActions();
    }

    private class DefaultProjectForm extends Form{

        private static final long serialVersionUID = 1L;

        private GridLayout layout;

        public DefaultProjectForm() {
            layout = new GridLayout(2, 2);

            // use margins around layout
            layout.setSpacing(true);

            setLayout(layout);

            // setup buffering
            setWriteThrough(false);
            setInvalidCommitted(false);

            // set field factory
            setFormFieldFactory(new ProjectFormFieldFactory());

            // set the properties shown
            setVisibleItemProperties(Arrays.asList(new String[] { "projectName", "targetDueDate" }));
        }

        @Override
        protected void attachField(Object propertyId, Field field) {
            if (propertyId.equals("projectName")) {
                layout.addComponent(field, 0, 0);
            } else if (propertyId.equals("targetDueDate")) {
                layout.addComponent(field, 0, 1);
            }
        }
    }
}

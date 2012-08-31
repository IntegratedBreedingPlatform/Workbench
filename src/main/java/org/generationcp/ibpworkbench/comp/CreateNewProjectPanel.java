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

package org.generationcp.ibpworkbench.comp;

import org.generationcp.ibpworkbench.model.formfieldfactory.ProjectFormFieldFactory;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class CreateNewProjectPanel extends VerticalLayout implements InitializingBean{

    private static final long serialVersionUID = 1L;

    private Label newProjectTitle;
    
    private Form projectForm;
    
    private ProjectFormFieldFactory projectFormFieldFactory;
    
    private Button cancelButton;

    private Button saveProjectButton;
    
    private Button showLocationWindowButton;

    private Component buttonArea;
    
    private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] { "projectName", "targetDueDate", "cropType", "template", "members", "methods", "locations" };

    public CreateNewProjectPanel() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        assemble();

        projectForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
        
    }

    public Button getShowLocationWindowButton() {
        return showLocationWindowButton;
    }
    
    public Button getSaveProjectButton() {
        return saveProjectButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public Form getProjectForm() {
        return projectForm;
    }
    
    protected void initializeComponents() {
        newProjectTitle = new Label("Create New Project");
        newProjectTitle.setStyleName("gcp-content-title");

        addComponent(newProjectTitle);
        
        BeanItem<Project> projectBean = new BeanItem<Project>(new Project());
        
        projectFormFieldFactory = new ProjectFormFieldFactory();

        projectForm = new Form();
        projectForm.setItemDataSource(projectBean);
        projectForm.setFormFieldFactory(projectFormFieldFactory);
        addComponent(projectForm);
        
        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");
        
        showLocationWindowButton = new Button("Add Location");
        
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);

        // set the save/cancel buttons
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }
    
    protected void initializeActions() {
        Field field = projectForm.getField("cropType");
        field.addListener(new ValueChangeListener() {
            private static final long serialVersionUID = 1L;
            private String lastValue = null;

            @Override
            public void valueChange(ValueChangeEvent event) {
                String value = (String) event.getProperty().getValue();
                boolean sameAsLastValue = lastValue == null ? value == null : lastValue.equals(value);
                if (sameAsLastValue) {
                    return;
                }
                else {
                    lastValue = value;
                }
                
                // set the visible properties again,
                // so that all fields gets renewed
                projectForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
            }
        });
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        showLocationWindowButton = new Button("Add Location");
        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");
        
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveProjectButton);
        buttonLayout.addComponent(showLocationWindowButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}

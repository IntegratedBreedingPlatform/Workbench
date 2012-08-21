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

import org.generationcp.ibpworkbench.comp.form.AddLocationForm;
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
    
    private Label addLocationTitle;

    private Form projectForm;
    
    private ProjectFormFieldFactory projectFormFieldFactory;
    
    private AddLocationForm locationForm;

    private Button cancelButton;

    private Button saveProjectButton;
    
    private Button addLocationButton;

    Component buttonArea;
    
    private final static String[] VISIBLE_ITEM_PROPERTIES = new String[] { "projectName", "targetDueDate", "cropType", "template", "members" };

    public CreateNewProjectPanel() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        assemble();

        projectForm.setVisibleItemProperties(VISIBLE_ITEM_PROPERTIES);
        
    }

    public Button getAddLocationButton() {
        return addLocationButton;
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
    
    public AddLocationForm getLocationForm() {
        return locationForm;
    }

    protected void initializeComponents() {
        newProjectTitle = new Label("Create New Project");
        newProjectTitle.setStyleName("gcp-content-title");

        addComponent(newProjectTitle);
        
        BeanItem<Project> projectBean = new BeanItem<Project>(new Project());
//        projectBean.addItemProperty("tblLocation", new Table());
//        projectBean.addItemProperty("tblMethods", new Table());
        
        projectFormFieldFactory = new ProjectFormFieldFactory();

        projectForm = new Form();
        projectForm.setItemDataSource(projectBean);
        projectForm.setFormFieldFactory(projectFormFieldFactory);
        addComponent(projectForm);
        
//        addLocationTitle = new Label("Add Location");
//        addLocationTitle.setStyleName("gcp-content-title");
//        addComponent(addLocationTitle);
        
//        locationForm = new AddLocationForm(new Location());
//        addComponent(locationForm);
//        
//        addLocationButton = new Button("Add Location");
//        addComponent(addLocationButton);
        
        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");
        
        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);

//        newProjectTitle.setSizeUndefined();

        //projectForm.setSizeFull();
        
//        addLocationTitle.setSizeUndefined();
        
        //locationForm.setSizeFull();

        // set the save/cancel buttons
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }
    
    protected void initializeActions() {
//        addLocationButton.addListener(new SaveNewLocationAction(locationForm));

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

        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");
        
        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveProjectButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}

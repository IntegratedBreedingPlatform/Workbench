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

package org.generationcp.ibpworkbench.comp.project.create;

import java.util.Date;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CropTypeComboAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;

/**
 * The first tab (Basic Details) in Create Project Accordion Component.
 * 
 * @author Joyce Avestro
 */
@Configurable
public class ProjectBasicDetailsComponent extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(ProjectBasicDetailsComponent.class);
    private static final long serialVersionUID = 1L;

    private CreateProjectPanel createProjectPanel; // the containing panel

    private GridLayout gridLayout;
    private TextField projectNameField;
    private DateField startDateField;
    private ComboBox cropTypeCombo;
    private CropTypeComboAction cropTypeComboAction;
    
    private Button nextButton;
    private Component buttonArea;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public ProjectBasicDetailsComponent(CreateProjectPanel createProjectPanel) {
        super();
        this.createProjectPanel = createProjectPanel;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }

    protected void initializeComponents() {
        setSpacing(true);
        setMargin(true);
        
        gridLayout = new GridLayout();
        gridLayout.setRows(4);
        gridLayout.setColumns(3);
        gridLayout.setSpacing(true);

        projectNameField = new TextField();
        projectNameField.setRequired(true);
        projectNameField.setRequiredError("Please enter a Project Name.");
        projectNameField.addValidator(new StringLengthValidator("Project Name must be 3-255 characters", 3, 255, false));

        startDateField = new DateField();
        startDateField.setRequired(true);
        startDateField.setRequiredError("Please enter a Start Date.");

        cropTypeCombo = createCropTypeComboBox();
        
        gridLayout.addComponent(new Label("Project Name"), 1, 1);
        gridLayout.addComponent(projectNameField, 2, 1);
        gridLayout.addComponent(new Label("Start Date"), 1, 2);
        gridLayout.addComponent(startDateField, 2, 2);
        gridLayout.addComponent(new Label("Crop"), 1, 3);
        gridLayout.addComponent(cropTypeCombo, 2, 3);

        addComponent(gridLayout);
        
        buttonArea = layoutButtonArea();
        this.addComponent(buttonArea);
    }

    protected void initializeValues() {
        //set default value of Start Date to the current date
        startDateField.setValue(new Date());
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }

    protected void initializeActions() {
       // cropTypeCombo.addListener(cropTypeComboAction);
        nextButton.addListener(new NextButtonClickListener());

    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        nextButton = new Button("Next");
        buttonLayout.addComponent(nextButton);
        return buttonLayout;
    }

    public void setCropType(CropType cropType) {
        createProjectPanel.setSelectedCropType(cropType);
    }
    
    public void refreshVisibleItems(){
        requestRepaintAll();
    }
    
    private ComboBox createCropTypeComboBox(){
        List<CropType> cropTypes = null;
        try {
            cropTypes = workbenchDataManager.getInstalledCentralCrops();
        }
        catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting installed central crops", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                    Message.CONTACT_ADMIN_ERROR_DESC);
        }
        
        BeanItemContainer<CropType> beanItemContainer = new BeanItemContainer<CropType>(CropType.class);
        for (CropType cropType : cropTypes) {
            beanItemContainer.addBean(cropType);
        }

        ComboBox comboBox = new ComboBox();
        comboBox.setContainerDataSource(beanItemContainer);
        comboBox.setNewItemsAllowed(false);
        cropTypeComboAction = new CropTypeComboAction();
        cropTypeComboAction.setSourceComponent(this);
        cropTypeComboAction.setCropTypeComboBox(comboBox);
       
        //comboBox.setNewItemHandler(cropTypeComboAction);
        
        comboBox.setItemCaptionPropertyId("cropName");
        comboBox.setRequired(true);
        comboBox.setRequiredError("Please select a Crop.");
        comboBox.setImmediate(true);
        
        if (cropTypes.size() == 1){     //If there is only one crop, set this by default
            comboBox.setValue(cropTypes.get(0));
        }

        return comboBox;
    }
    
    public boolean validate(){
        boolean success = true;
        String projectName = (String) projectNameField.getValue();
        Date startDate = (Date) startDateField.getValue();
        CropType cropType = (CropType) cropTypeCombo.getValue();
        
        StringBuffer errorDescription = new StringBuffer();
        
        if ((projectName == null) || (projectName.equals(""))){
            errorDescription.append("No project name supplied. ");
            success = false;
        } else {
        	// Check if the project name already exists
            try {
                Project project = workbenchDataManager.getProjectByName(projectName);
                if (project != null && project.getProjectName() != null && project.getProjectName().equals(projectName)){
                    errorDescription.append("There is already a project with the given name. ");
                    success = false;
                }
            }
            catch (MiddlewareQueryException e) {
                LOG.error("Error encountered while getting project by name", e);
                throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                        Message.CONTACT_ADMIN_ERROR_DESC);
            }
            // Run assigned validators
            try {
            	projectNameField.validate();
            } catch (InvalidValueException e) {
            	errorDescription.append(e.getMessage() + ". ");
            	success = false;
            }

        }
        if (startDate == null){
            errorDescription.append("No start date given. ");
            success = false;
        }
        if (cropType == null){
            errorDescription.append("No crop type selected. ");
            success = false;
        }
        
        if (!success){
            MessageNotifier.showError(getWindow(), "Error", errorDescription.toString());
        }
            
        return success;
    }
    
    public boolean validateAndSave(){
        boolean valid = validate();
        if (valid){
            Project project = createProjectPanel.getProject();
            project.setProjectName((String) projectNameField.getValue());
            project.setStartDate((Date) startDateField.getValue());
            project.setCropType((CropType) cropTypeCombo.getValue());
        }
        return valid;
    } 
    
    private class NextButtonClickListener implements ClickListener{
        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            if (validate()){
                setCropType((CropType) cropTypeCombo.getValue());
                createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.SECOND_TAB_USER_ROLES);
            }
        }
    }

}

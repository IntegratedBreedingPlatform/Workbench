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

package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.validator.RegexValidator;
import org.generationcp.commons.vaadin.validator.ValidationUtil;
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
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

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
        
        Pattern projectNameInvalidCharPattern = Pattern.compile("^[^\\\\/:\\*\\?\"<>\\|]+$", Pattern.DOTALL);
        Pattern cropNameInvalidCharPattern = Pattern.compile("^[^'\":;,\\./\\|\\-=\\(\\)\\\\]+$", Pattern.DOTALL);

        projectNameField = new TextField();
        projectNameField.setImmediate(true);
        projectNameField.setRequired(true);
        projectNameField.setRequiredError("Please enter a Program Name.");
        projectNameField.addValidator(new StringLengthValidator("Program Name must be 3-255 characters.", 3, 255, false));
        projectNameField.addValidator(new RegexValidator("Program Name must not contain any of the following: \\ / : * ? \" < > |", projectNameInvalidCharPattern, true));
        projectNameField.addShortcutListener(new Button.ClickShortcut(nextButton,KeyCode.ENTER));
        
        startDateField = new DateField();
        startDateField.setRequired(true);
        startDateField.setDateFormat("yyyy-MM-dd");
        startDateField.setRequiredError("Please enter a Start Date.");
		startDateField.setStyleName("project-data-time");

        cropTypeCombo = createCropTypeComboBox();
        cropTypeCombo.addValidator(new RegexValidator("Crop name must not contain any of the following: ' \" : ; , . / \\ | - = \\( \\)", cropNameInvalidCharPattern, true));
        
        gridLayout.addComponent(new Label("Crop"), 1, 1);
        gridLayout.addComponent(cropTypeCombo, 2, 1);
        gridLayout.addComponent(new Label("Program Name"), 1, 2);
        gridLayout.addComponent(projectNameField, 2, 2);
        gridLayout.addComponent(new Label("Start Date"), 1, 3);
        gridLayout.addComponent(startDateField, 2, 3);
        

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
        setComponentAlignment(buttonArea, Alignment.TOP_CENTER);
        setComponentAlignment(gridLayout,Alignment.TOP_CENTER);
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
        //nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
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
        comboBox.setNewItemsAllowed(true);
        //comboBox.addListener(new ComboBoxNewItemsListener());
        cropTypeComboAction = new CropTypeComboAction();
        cropTypeComboAction.setSourceComponent(this);
        cropTypeComboAction.setCropTypeComboBox(comboBox);
       
        comboBox.setNewItemHandler(cropTypeComboAction);
        
        comboBox.setItemCaptionPropertyId("cropName");
        comboBox.setRequired(true);
        comboBox.setRequiredError("Please select a Crop.");
        comboBox.setImmediate(true);

        if (cropTypes.size() == 1){     //If there is only one crop, set this by default
            comboBox.setValue(cropTypes.get(0));
        }

        return comboBox;
    }

    public void updateProjectDetailsFormField(Project project) {
       this.projectNameField.setValue(project.getProjectName());
       this.startDateField.setValue(project.getStartDate());
       this.cropTypeCombo.setValue(project.getCropType());
    }
    
    public boolean validate(){
        boolean success = true;
        String projectName = (String) projectNameField.getValue();
        Date startDate = (Date) startDateField.getValue();
        CropType cropType = (CropType) cropTypeCombo.getValue();
        
        StringBuffer errorDescription = new StringBuffer();
        
        if ((projectName == null) || (projectName.equals(""))){
            errorDescription.append("No program name supplied. ");
            success = false;
        } else {
        	// Check if the project name already exists
            try {
                Project project = workbenchDataManager.getProjectByName(projectName);
                if (project != null && project.getProjectName() != null && project.getProjectName().equalsIgnoreCase(projectName)){

                    if (createProjectPanel.getProject() == null || createProjectPanel.getProject().getProjectId() == null){
                        errorDescription.append("There is already a program with the given name. ");
                        success = false;
                    } else if (createProjectPanel.getProject().getProjectId().intValue() != project.getProjectId().intValue()){
                    	errorDescription.append("There is already a program with the given name. ");
                        success = false;
                    }

                }
            }
            catch (MiddlewareQueryException e) {
                LOG.error("Error encountered while getting program by name", e);
                throw new InternationalizableException(e, Message.DATABASE_ERROR, 
                        Message.CONTACT_ADMIN_ERROR_DESC);
            }
            // Run assigned validators
            try {
                projectNameField.validate();
            }
            catch (InvalidValueException e) {
                errorDescription.append(ValidationUtil.getMessageFor(e));
                success = false;
            }
            
            try {
                cropTypeCombo.validate();
            }
            catch (InvalidValueException e) {
                errorDescription.append(ValidationUtil.getMessageFor(e));
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
            
            String projectName = (String) projectNameField.getValue();
            if (projectName != null) {
                projectName = projectName.trim();
            }
            
            project.setProjectName(projectName);
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

    public void disableCropTypeCombo() {
        this.cropTypeCombo.setEnabled(false);
    }
}

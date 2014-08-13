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
import java.util.Properties;
import java.util.regex.Pattern;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.validator.RegexValidator;
import org.generationcp.commons.vaadin.validator.ValidationUtil;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.util.SchemaVersionUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
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
    
    public static String GENERIC_CROP_DESCRIPTION = "Use Generic Database for Other Crop";
 
    private CreateProjectPanel createProjectPanel; // the containing panel

    private GridLayout gridLayout;
    private TextField projectNameField;
    private TextField otherCropNameField;
    private DateField startDateField;
    private ComboBox cropTypeCombo;
    
    private Label lblCrop;
    private Label lblOtherCrop;
    private Label lblProjectName;
    private Label lblStartDate;
   
    private Boolean isUpdate = false;

    private CropType oldCropType;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    protected SimpleResourceBundleMessageSource messageSource;

    @Autowired
    protected SessionData sessionData;
    
    @Autowired
    @Qualifier("workbenchProperties")
    private Properties workbenchProperties;

    public ProjectBasicDetailsComponent(CreateProjectPanel createProjectPanel) {
        super();
        this.createProjectPanel = createProjectPanel;
    }
    
    public ProjectBasicDetailsComponent(CreateProjectPanel createProjectPanel, Boolean isUpdate) {
        super();
        this.createProjectPanel = createProjectPanel;
        this.setIsUpdate(isUpdate);
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
        
        if (isUpdate){
        	initializeLayoutForUpdate();
        }
    }

    protected void initializeComponents() {
        gridLayout = new GridLayout();
        gridLayout.setRows(4);
        gridLayout.setColumns(5);
        gridLayout.setSpacing(true);
        
        Pattern projectNameInvalidCharPattern = Pattern.compile("^[^\\\\/:\\*\\?\"<>\\|]+$", Pattern.DOTALL);
        Pattern cropNameInvalidCharPattern = Pattern.compile("^[^<>'\":;,\\./\\|\\-=\\(\\)\\\\]+$", Pattern.DOTALL);

        projectNameField = new TextField();
        projectNameField.setImmediate(true);
        projectNameField.setRequired(true);
        projectNameField.setRequiredError("Please enter a Program Name.");
        projectNameField.addValidator(new StringLengthValidator("Program Name must be 3-65 characters.", 3, 65, false));
        projectNameField.addValidator(new RegexValidator("Program Name must not contain any of the following: \\ / : * ? \" < > |", projectNameInvalidCharPattern, true));
        projectNameField.setStyleName("hide-caption");
        projectNameField.setWidth("250px");
        
        otherCropNameField = new TextField();
        otherCropNameField.setImmediate(true);
        otherCropNameField.setRequired(false);
        otherCropNameField.setRequiredError("Please enter Crop Name.");
        otherCropNameField.addValidator(new StringLengthValidator("Crop Name must be 3-70 characters.", 3, 70, false));
        otherCropNameField.addValidator(new RegexValidator("Crop name must not contain any of the following: '< > \" : ; , . / \\ | - = \\( \\)", cropNameInvalidCharPattern, true));
        otherCropNameField.addValidator(new ValueRangeValidator("This crop name is reserved because there is a database available for it. Please install the crop name database before creating this program if you wish to take advantage of traits and other information for this crop. If you wish to proceed with using the generic database, please choose a different name for your crop."));
        otherCropNameField.setStyleName("hide-caption");
        otherCropNameField.setVisible(false);
        
        startDateField = new DateField();
        startDateField.setRequired(true);
        startDateField.setDateFormat("yyyy-MM-dd");
        startDateField.setRequiredError("Please enter a Start Date.");
		startDateField.setStyleName("project-data-time");
		startDateField.setStyleName("hide-caption");
        startDateField.setResolution(DateField.RESOLUTION_DAY);
        startDateField.setWidth("250px");

        cropTypeCombo = createCropTypeComboBox();
        cropTypeCombo.setWidth("250px");
        //cropTypeCombo.addValidator(new RegexValidator("Crop name must not contain any of the following: ' \" : ; , . / \\ | - = \\( \\)", cropNameInvalidCharPattern, true));
        cropTypeCombo.setStyleName("hide-caption");
        
        lblCrop = new Label();
        lblCrop.setValue(messageSource.getMessage(Message.BASIC_DETAILS_CROP));
        lblCrop.setStyleName("label-bold");
        lblCrop.setContentMode(Label.CONTENT_XHTML);
        
        lblOtherCrop = new Label();
        lblOtherCrop.setValue(messageSource.getMessage(Message.BASIC_DETAILS_OTHER_CROP_NAME));
        lblOtherCrop.setContentMode(Label.CONTENT_XHTML);
        lblOtherCrop.setVisible(false);
        
        lblProjectName = new Label();
        lblProjectName.setValue(messageSource.getMessage(Message.BASIC_DETAILS_PROGRAM_NAME));
        lblProjectName.setStyleName("label-bold");
        lblProjectName.setContentMode(Label.CONTENT_XHTML);
        lblStartDate = new Label();
        lblStartDate.setValue(messageSource.getMessage(Message.BASIC_DETAILS_PROGRAM_STARTDATE));
        lblStartDate.setStyleName("label-bold");
        lblStartDate.setContentMode(Label.CONTENT_XHTML);
        
        gridLayout.addComponent(lblCrop, 1, 1);
        gridLayout.addComponent(cropTypeCombo, 2, 1);
        gridLayout.addComponent(lblOtherCrop, 3, 1);
        gridLayout.addComponent(otherCropNameField, 4, 1);
        gridLayout.addComponent(lblProjectName, 1, 2);
        gridLayout.addComponent(projectNameField, 2, 2);
        gridLayout.addComponent(lblStartDate, 1, 3);
        gridLayout.addComponent(startDateField, 2, 3);
       
        addComponent(gridLayout);
        
    }

    protected void initializeValues() {
        startDateField.setValue(new Date());
    }

    protected void initializeLayout() {
        this.setMargin(false);
        this.setSpacing(false);

        this.setComponentAlignment(gridLayout, Alignment.TOP_CENTER);
    }
    
    protected void initializeLayoutForUpdate() {
    	
        //this.setComponentAlignment(gridLayout,Alignment.TOP_LEFT);
        
        CropType selectedCropType = sessionData.getSelectedProject().getCropType();
        
        Boolean isCustomCrop = true;
        for (CropType.CropEnum crop : CropType.CropEnum.values()){
        	if (crop.toString().equalsIgnoreCase(selectedCropType.getCropName())){
        		isCustomCrop = false;
        		break;
        	}
        }
       
        if (isCustomCrop){
        	
    	     CropType genericCropType = new CropType();
    	     genericCropType.setCentralDbName("generic");
    	     genericCropType.setCropName(GENERIC_CROP_DESCRIPTION);
    	     cropTypeCombo.addItem(genericCropType);
    	     cropTypeCombo.setValue(genericCropType);
    	     
    	     lblOtherCrop.setVisible(true);
    	     otherCropNameField.setVisible(true);
    	     otherCropNameField.setEnabled(false);
    	     otherCropNameField.setRequired(false);
    	     otherCropNameField.removeAllValidators();
    	     otherCropNameField.setValue(selectedCropType.getCropName());
        }
        
        
        disableCropTypeCombo();
        		
    }
    

    protected void initializeActions() {

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
        
        CropType genericCropType = new CropType();
        genericCropType.setCentralDbName("generic");
        genericCropType.setCropName(GENERIC_CROP_DESCRIPTION);
        beanItemContainer.addBean(genericCropType);

        ComboBox comboBox = new ComboBox();
        comboBox.setContainerDataSource(beanItemContainer);
        comboBox.setNewItemsAllowed(false);
        comboBox.setItemCaptionPropertyId("cropName");
        comboBox.setRequired(true);
        comboBox.setRequiredError("Please choose a Crop.");
        comboBox.setInputPrompt("Please choose");
        comboBox.setInvalidAllowed(false);
        comboBox.setNullSelectionAllowed(false);
        comboBox.setImmediate(true);  
       
        if (cropTypes.size() == 1){     //If there is only one crop, set this by default
            comboBox.setValue(cropTypes.get(0));
        }
        
        comboBox.addListener(new Property.ValueChangeListener() {
			
        	private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				if (event.getProperty() == null) return;
				if (event.getProperty().getValue() == null) return;
				
				if (((CropType)event.getProperty().getValue()).getCropName().equals(GENERIC_CROP_DESCRIPTION)){
					otherCropNameField.setVisible(true);
					otherCropNameField.setRequired(true);
					lblOtherCrop.setVisible(true);
				}else{
					otherCropNameField.setVisible(false);
					otherCropNameField.setRequired(false);
					otherCropNameField.setValue("");
					lblOtherCrop.setVisible(false);
				}

                if (!isUpdate) {
                    if (oldCropType == null) {
                        oldCropType = getCropTypeBasedOnInput();
                    } else {
                        CropType newCropType = getCropTypeBasedOnInput();

                        if (!oldCropType.getCropName().equalsIgnoreCase(newCropType.getCropName())) {
                            createProjectPanel.cropTypeChanged(newCropType);
                            LOG.debug("changed");
                        }

                        oldCropType = newCropType;
                    }
                    
                }
                if(getWindow()!=null) {
	                String minimumCropVersion = SchemaVersionUtil.getMinimumCropVersion();
	                String currentCropVersion = getCropTypeBasedOnInput().getVersion();
	                if(!SchemaVersionUtil.checkIfVersionIsSupported(currentCropVersion,minimumCropVersion)) {
	                   	MessageNotifier.showWarning(getWindow(),"",
	                			messageSource.getMessage(Message.MINIMUM_CROP_VERSION_WARNING,
	                					minimumCropVersion,
	                					currentCropVersion!=null?currentCropVersion:
	                						messageSource.getMessage(Message.BELOW_MINIMUM)));
	                }
                }

				
			}
		});
       
        return comboBox;
    }

    public void updateProjectDetailsFormField(Project project) {
       this.projectNameField.setValue(project.getProjectName());
       this.startDateField.setValue(project.getStartDate());
       this.cropTypeCombo.setValue(project.getCropType());
       
       if (isUpdate)
       initializeLayoutForUpdate();
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

                    if (!isUpdate){
                        errorDescription.append("There is already a program with the given name. ");
                        success = false;
                    } else if (sessionData.getSelectedProject().getProjectId().intValue() != project.getProjectId().intValue()){
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
                otherCropNameField.validate();
            }
            catch (InvalidValueException e) {
                errorDescription.append(ValidationUtil.getMessageFor(e));
                success = false;
            }
            
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
            errorDescription.append("Please enter a valid start date. ");
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
    
    public Project validateAndSave() throws InvalidValueException {
        if (!validate()) {
            throw new InvalidValueException("Failed Validation on BasicDetailsForm");
        }

        Project project = new Project();

        String projectName = (String) projectNameField.getValue();
        if (projectName != null) {
            projectName = projectName.trim();
        }

        project.setProjectName(projectName);
        project.setStartDate((Date) startDateField.getValue());

        project.setCropType(getCropTypeBasedOnInput());
        
        
        
        return project;
    }
    
    
    public CropType getCropTypeBasedOnInput(){
    	
    	if (((CropType)cropTypeCombo.getValue()).getCropName().equalsIgnoreCase(GENERIC_CROP_DESCRIPTION)){
    	    String bmsVersion = workbenchProperties.getProperty("workbench.version", null);
    	    
    		String newItemCaption = (String) otherCropNameField.getValue();
    		CropType cropType = new CropType(newItemCaption);  	
            cropType.setCentralDbName("ibdbv2_" + newItemCaption.toLowerCase().replaceAll("\\s+", "_") + "_central");
            cropType.setVersion(bmsVersion);
            return cropType;
    	}else{
    		return (CropType) cropTypeCombo.getValue();
    	}

    }

    public void disableCropTypeCombo() {
        this.cropTypeCombo.setEnabled(false);
    }
    
    public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public void setAlignment(Alignment alignment){
    	
    	this.setComponentAlignment(gridLayout, alignment);
    }
	
	private class ValueRangeValidator extends AbstractValidator{

		public ValueRangeValidator(String errorMessage) {
			super(errorMessage);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean isValid(Object value) {
			
			try {
				CropType cropType = workbenchDataManager.getCropTypeByName(value.toString().trim());
				if (cropType != null){
					this.setErrorMessage(String.format("The {0} crop already exists. Please change the Crop Name.", value.toString().trim()));
					return false;
				}
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			for (CropType.CropEnum crop : CropType.CropEnum.values()){
				if (crop.toString().equalsIgnoreCase((value.toString().trim()))){
					this.setErrorMessage(String.format("This crop name ({0}) is reserved because there is a database available for it. Please install the {0} database before creating this program if you wish to take advantage of traits and other information for this crop. If you wish to proceed with using the generic database, please choose a different name for your crop.", value.toString().trim()));
					return false;
				}
			}
			
			
			return true;
		}
		
	}
}

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.fields.BmsDateField;
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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import static java.lang.String.*;

/**
 * The first tab (Basic Details) in Create Project Accordion Component.
 *
 * @author Joyce Avestro
 */

//TODO The messages in this class should be localised
@Configurable
public class ProjectBasicDetailsComponent extends VerticalLayout implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectBasicDetailsComponent.class);
	private static final long serialVersionUID = 1L;

	//TODO
	private static final String GENERIC_CROP_DESCRIPTION = "Use Generic Database for Other Crop";

	// the containing panel
	private final CreateProjectPanel createProjectPanel;

	private GridLayout gridLayout;
	private TextField projectNameField;
	private TextField otherCropNameField;
	private BmsDateField startDateField;
	private ComboBox cropTypeCombo;

	private Label lblOtherCrop;

	private Boolean isUpdate = false;

	private StringBuilder errorDescription;

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

	public ProjectBasicDetailsComponent(final CreateProjectPanel createProjectPanel) {
		super();
		this.createProjectPanel = createProjectPanel;
	}

	public ProjectBasicDetailsComponent(final CreateProjectPanel createProjectPanel, final Boolean isUpdate) {
		super();
		this.createProjectPanel = createProjectPanel;
		this.setIsUpdate(isUpdate);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeValues();
		this.initializeLayout();

		if (this.isUpdate) {
			this.disableCropTypeCombo();
		}
	}

	protected void initializeComponents() {
		this.gridLayout = new GridLayout();
		this.gridLayout.setRows(4);
		this.gridLayout.setColumns(5);
		this.gridLayout.setSpacing(true);
		this.gridLayout.addStyleName("basic-details");

		final Pattern projectNameInvalidCharPattern = Pattern.compile("^[^\\\\/:\\*\\?\"<>\\|]+$", Pattern.DOTALL);
		final Pattern cropNameInvalidCharPattern = Pattern.compile("^[^<>'\":;,\\./\\|\\-=\\(\\)\\\\]+$", Pattern.DOTALL);

		this.projectNameField = new TextField();
		this.projectNameField.setImmediate(true);
		this.projectNameField.setRequired(true);
		//TODO
		this.projectNameField.setRequiredError("Please enter a Program Name.");
		//TODO
		this.projectNameField.addValidator(new StringLengthValidator("Program Name must be 3-65 characters.", 3, 65, false));
		//TODO
		this.projectNameField.addValidator(new RegexValidator("Program Name must not contain any of the following: \\ / : * ? \" < > |",
				projectNameInvalidCharPattern, true));
		this.projectNameField.setStyleName("hide-caption");
		this.projectNameField.setWidth("250px");
		this.projectNameField.setDebugId("vaadin_projectname_txt");

		this.otherCropNameField = new TextField();
		this.otherCropNameField.setImmediate(true);
		this.otherCropNameField.setRequired(false);
		//TODO
		this.otherCropNameField.setRequiredError("Please enter Crop Name.");
		//TODO
		this.otherCropNameField.addValidator(new StringLengthValidator("Crop Name must be 3-70 characters.", 3, 70, false));
		//TODO
		this.otherCropNameField.addValidator(new RegexValidator(
				"Crop name must not contain any of the following: '< > \" : ; , . / \\ | - = \\( \\)", cropNameInvalidCharPattern, true));
		//TODO
		this.otherCropNameField
		.addValidator(new ValueRangeValidator(
				"This crop name is reserved because there is a database available for it. Please install the crop name database before creating this program if you wish to take advantage of traits and other information for this crop. If you wish to proceed with using the generic database, please choose a different name for your crop."));
		this.otherCropNameField.setStyleName("hide-caption");
		this.otherCropNameField.setVisible(false);

		this.startDateField = new BmsDateField();
		this.startDateField.setRequired(true);
		//TODO
		this.startDateField.setRequiredError("Please enter a Start Date.");
		this.startDateField.setStyleName("project-data-time");
		this.startDateField.setStyleName("hide-caption");
		this.startDateField.setWidth("250px");

		this.cropTypeCombo = this.createCropTypeComboBox();
		this.cropTypeCombo.setWidth("250px");
		this.cropTypeCombo.setStyleName("hide-caption");
		this.cropTypeCombo.setDebugId("vaadin_croptype_combo");

		final Label lblCrop = new Label();
		lblCrop.setValue(this.messageSource.getMessage(Message.BASIC_DETAILS_CROP));
		lblCrop.setStyleName("label-bold");
		lblCrop.setContentMode(Label.CONTENT_XHTML);

		this.lblOtherCrop = new Label();
		this.lblOtherCrop.setValue(this.messageSource.getMessage(Message.BASIC_DETAILS_OTHER_CROP_NAME));
		this.lblOtherCrop.setContentMode(Label.CONTENT_XHTML);
		this.lblOtherCrop.setVisible(false);

		final Label lblProjectName = new Label();
		lblProjectName.setValue(this.messageSource.getMessage(Message.BASIC_DETAILS_PROGRAM_NAME));
		lblProjectName.setStyleName("label-bold");
		lblProjectName.setContentMode(Label.CONTENT_XHTML);

		final Label lblStartDate = new Label();
		lblStartDate.setValue(this.messageSource.getMessage(Message.BASIC_DETAILS_PROGRAM_STARTDATE));
		lblStartDate.setStyleName("label-bold");
		lblStartDate.setContentMode(Label.CONTENT_XHTML);

		this.gridLayout.addComponent(lblCrop, 1, 1);
		this.gridLayout.addComponent(this.cropTypeCombo, 2, 1);
		this.gridLayout.addComponent(this.lblOtherCrop, 3, 1);
		this.gridLayout.addComponent(this.otherCropNameField, 4, 1);
		this.gridLayout.addComponent(lblProjectName, 1, 2);
		this.gridLayout.addComponent(this.projectNameField, 2, 2);
		this.gridLayout.addComponent(lblStartDate, 1, 3);
		this.gridLayout.addComponent(this.startDateField, 2, 3);
		this.addComponent(this.gridLayout);

	}

	protected void initializeValues() {
		this.startDateField.setValue(new Date());
	}

	protected void initializeLayout() {
		this.setMargin(false);
		this.setSpacing(false);

		this.setComponentAlignment(this.gridLayout, Alignment.TOP_LEFT);
	}

	private ComboBox createCropTypeComboBox() {
		final List<CropType> cropTypes;
		try {
			cropTypes = this.workbenchDataManager.getInstalledCropDatabses();
		} catch (final MiddlewareQueryException e) {
			//TODO
			ProjectBasicDetailsComponent.LOG.error("Error encountered while getting installed central crops", e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

		final BeanItemContainer<CropType> beanItemContainer = new BeanItemContainer<>(CropType.class);
		for (final CropType cropType : cropTypes) {
			beanItemContainer.addBean(cropType);
		}

		final ComboBox comboBox = new ComboBox();
		comboBox.setContainerDataSource(beanItemContainer);
		comboBox.setNewItemsAllowed(false);
		comboBox.setItemCaptionPropertyId("cropName");
		comboBox.setRequired(true);
		//TODO
		comboBox.setRequiredError("Please choose a Crop. ");
		//TODO
		comboBox.setInputPrompt("Please choose");
		comboBox.setInvalidAllowed(false);
		comboBox.setNullSelectionAllowed(false);
		comboBox.setImmediate(true);

		// If there is only one crop, set this by default
		if (cropTypes.size() == 1) {
			comboBox.setValue(cropTypes.get(0));
		}

		comboBox.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(final ValueChangeEvent event) {

				if (event.getProperty() == null) {
					return;
				}
				if (event.getProperty().getValue() == null) {
					return;
				}

				if (((CropType) event.getProperty().getValue()).getCropName().equals(ProjectBasicDetailsComponent.GENERIC_CROP_DESCRIPTION)) {
					ProjectBasicDetailsComponent.this.otherCropNameField.setVisible(true);
					ProjectBasicDetailsComponent.this.otherCropNameField.setRequired(true);
					ProjectBasicDetailsComponent.this.lblOtherCrop.setVisible(true);
				} else {
					ProjectBasicDetailsComponent.this.otherCropNameField.setVisible(false);
					ProjectBasicDetailsComponent.this.otherCropNameField.setRequired(false);
					ProjectBasicDetailsComponent.this.otherCropNameField.setValue("");
					ProjectBasicDetailsComponent.this.lblOtherCrop.setVisible(false);
				}

				if (!ProjectBasicDetailsComponent.this.isUpdate) {
					if (ProjectBasicDetailsComponent.this.oldCropType == null) {
						ProjectBasicDetailsComponent.this.oldCropType = ProjectBasicDetailsComponent.this.getCropTypeBasedOnInput();
					} else {
						final CropType newCropType = ProjectBasicDetailsComponent.this.getCropTypeBasedOnInput();

						if (!ProjectBasicDetailsComponent.this.oldCropType.getCropName().equalsIgnoreCase(newCropType.getCropName())) {
							ProjectBasicDetailsComponent.this.createProjectPanel.cropTypeChanged(newCropType);
							ProjectBasicDetailsComponent.LOG.debug("changed");
						}

						ProjectBasicDetailsComponent.this.oldCropType = newCropType;
					}

				}
				if (ProjectBasicDetailsComponent.this.getWindow() != null) {
					final String minimumCropVersion = SchemaVersionUtil.getMinimumCropVersion();
					final String currentCropVersion = ProjectBasicDetailsComponent.this.getCropTypeBasedOnInput().getVersion();
					if (!SchemaVersionUtil.checkIfVersionIsSupported(currentCropVersion, minimumCropVersion)) {
						MessageNotifier.showWarning(ProjectBasicDetailsComponent.this.getWindow(), "",
								ProjectBasicDetailsComponent.this.messageSource.getMessage(
										Message.MINIMUM_CROP_VERSION_WARNING,
										currentCropVersion != null ? currentCropVersion : ProjectBasicDetailsComponent.this.messageSource
												.getMessage(Message.NOT_AVAILABLE)));
					}
				}

			}
		});

		return comboBox;
	}

	void updateProjectDetailsFormField(final Project project) {
		this.projectNameField.setValue(project.getProjectName());
		this.startDateField.setValue(project.getStartDate());
		this.cropTypeCombo.setValue(project.getCropType());

		if (this.isUpdate) {
			this.disableCropTypeCombo();
		}
	}

	public boolean validate() {
		boolean success = true;
		final String projectName = (String) this.projectNameField.getValue();
		final CropType cropType = (CropType) this.cropTypeCombo.getValue();

		this.errorDescription = new StringBuilder();

		if (projectName == null || projectName.equals("")) {
			//TODO
			this.errorDescription.append("No program name supplied. ");
			success = false;
		} else {
			// Check if the project name already exists for given crop
			try {
				if (this.workbenchDataManager.getProjectByNameAndCrop(projectName, cropType) != null) {
					this.errorDescription.append(this.messageSource.getMessage(Message.DUPLICATE_PROGRAM_NAME_ERROR)).append(" ");
					success = false;
				}
			} catch (final MiddlewareQueryException e) {
				//TODO
				ProjectBasicDetailsComponent.LOG.error("Error encountered while getting program by name", e);
				throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
			}
			// Run assigned validators

			try {
				this.otherCropNameField.validate();
			} catch (final InvalidValueException e) {
				this.errorDescription.append(ValidationUtil.getMessageFor(e));
				success = false;
			}

			try {
				this.projectNameField.validate();
			} catch (final InvalidValueException e) {
				this.errorDescription.append(ValidationUtil.getMessageFor(e));
				success = false;
			}

			try {
				this.cropTypeCombo.validate();
			} catch (final InvalidValueException e) {
				this.errorDescription.append(ValidationUtil.getMessageFor(e));
				success = false;
			}

		}

		final String dateValidationMsg = this.validateDate();
		if (dateValidationMsg.length() > 0) {
			this.errorDescription.append(dateValidationMsg);
			success = false;
		}

		if (cropType == null) {
			//TODO
			this.errorDescription.append("No crop type selected. ");
			success = false;
		}

		if (!success) {
			MessageNotifier.showRequiredFieldError(this.getWindow(), this.errorDescription.toString());
		}

		return success;
	}

	private String validateDate() {
		String errorMessage = "";
		try {
			this.startDateField.validate();
		} catch (final InvalidValueException e) {
			errorMessage = e.getMessage();
			ProjectBasicDetailsComponent.LOG.debug(e.getMessage());
		}

		return errorMessage;
	}

	Project getProjectDetails() throws InvalidValueException {
		if (!this.validate()) {
			//TODO
			throw new InvalidValueException("Failed Validation on BasicDetailsForm");
		}

		final Project project = new Project();
		String projectName = (String) this.projectNameField.getValue();
		if (projectName != null) {
			projectName = projectName.trim();
		}
		project.setProjectName(projectName);
		project.setStartDate((Date) this.startDateField.getValue());
		project.setCropType(this.getCropTypeBasedOnInput());
		return project;
	}

	private CropType getCropTypeBasedOnInput() {

		if (((CropType) this.cropTypeCombo.getValue()).getCropName()
				.equalsIgnoreCase(ProjectBasicDetailsComponent.GENERIC_CROP_DESCRIPTION)) {
			final String bmsVersion = this.workbenchProperties.getProperty("workbench.version", null);

			final String newItemCaption = (String) this.otherCropNameField.getValue();
			final CropType cropType = new CropType(newItemCaption);
			cropType.setDbName("ibdbv2_" + newItemCaption.toLowerCase().replaceAll("\\s+", "_") + "_merged");
			cropType.setVersion(bmsVersion);
			return cropType;
		} else {
			return (CropType) this.cropTypeCombo.getValue();
		}

	}

	private void disableCropTypeCombo() {
		this.cropTypeCombo.setEnabled(false);
	}

	private void setIsUpdate(final Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public void setAlignment(final Alignment alignment) {
		this.setComponentAlignment(this.gridLayout, alignment);
	}

	void enableForm() {
		this.cropTypeCombo.setEnabled(false);
		this.startDateField.setEnabled(true);
		this.projectNameField.setEnabled(true);
	}

	void disableForm() {
		this.cropTypeCombo.setEnabled(false);
		this.startDateField.setEnabled(false);
		this.projectNameField.setEnabled(false);
	}

	private class ValueRangeValidator extends AbstractValidator {

		private static final long serialVersionUID = 1L;

		ValueRangeValidator(final String errorMessage) {
			super(errorMessage);
		}

		@Override
		public boolean isValid(final Object value) {
			final CropType cropType = ProjectBasicDetailsComponent.this.workbenchDataManager.getCropTypeByName(value.toString().trim());
			if (cropType != null) {
				//TODO
				this.setErrorMessage(format("The {0} crop already exists. Please change the Crop Name.", value.toString().trim()));
				return false;
			}

			for (final CropType.CropEnum crop : CropType.CropEnum.values()) {
				if (crop.toString().equalsIgnoreCase(value.toString().trim())) {
					//TODO
					this.setErrorMessage(format("This crop name ({0}) is reserved because there is a database available for it. Please install the {0} database before creating this program if you wish to take advantage of traits and other information for this crop. If you wish to proceed with using the generic database, please choose a different name for your crop.",
									value.toString().trim()));
					return false;
				}
			}

			return true;
		}

	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	StringBuilder getErrorDescription() {
		return this.errorDescription;
	}
}

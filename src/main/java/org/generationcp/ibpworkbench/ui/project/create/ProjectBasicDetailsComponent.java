/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.fields.BmsDateField;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.validator.RegexValidator;
import org.generationcp.commons.vaadin.validator.ValidationUtil;
import org.generationcp.ibpworkbench.Message;
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

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * The first tab (Basic Details) in Create Project Accordion Component.
 *
 * @author Joyce Avestro
 */

@Configurable
public class ProjectBasicDetailsComponent extends VerticalLayout implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectBasicDetailsComponent.class);
	private static final long serialVersionUID = 1L;

	// the containing panel
	private final CreateProjectPanel createProjectPanel;

	private GridLayout gridLayout;
	private TextField projectNameField;
	private BmsDateField startDateField;
	private AbstractField cropTypeField;
	
	private Boolean isUpdate = false;

	private StringBuilder errorDescription;

	private CropType oldCropType;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	protected SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

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
	}

	protected void initializeComponents() {
		this.gridLayout = new GridLayout();
		this.gridLayout.setDebugId("gridLayout");
		this.gridLayout.setRows(4);
		this.gridLayout.setColumns(5);
		this.gridLayout.setSpacing(true);
		this.gridLayout.addStyleName("basic-details");

		final Pattern projectNameInvalidCharPattern = Pattern.compile("^[^\\\\/:\\*\\?\"<>\\|]+$", Pattern.DOTALL);

		this.projectNameField = new TextField();
		this.projectNameField.setDebugId("projectNameField");
		this.projectNameField.setImmediate(true);
		this.projectNameField.setRequired(true);
		this.projectNameField.setRequiredError(this.messageSource.getMessage("PROGRAM_NAME_REQUIRED_ERROR"));
		this.projectNameField.addValidator(
				new StringLengthValidator(this.messageSource.getMessage("PROGRAM_NAME_LENGTH_ERROR"), 3, 65, false));
		this.projectNameField.addValidator(
				new RegexValidator(this.messageSource.getMessage("PROGRAM_NAME_INVALID_ERROR"), projectNameInvalidCharPattern, true));
		this.projectNameField.setStyleName("hide-caption");
		this.projectNameField.setWidth("250px");
		this.projectNameField.setDebugId("vaadin_projectname_txt");

		this.startDateField = new BmsDateField();
		this.startDateField.setDebugId("startDateField");
		this.startDateField.setRequired(true);
		this.startDateField.setRequiredError(this.messageSource.getMessage("START_DATE_REQUIRED_ERROR"));
		this.startDateField.setStyleName("project-data-time");
		this.startDateField.setStyleName("hide-caption");
		this.startDateField.setWidth("250px");

		if (this.isUpdate) {
			this.cropTypeField = new TextField();
			this.cropTypeField.setDebugId("vaadin_croptype_txt");
			this.cropTypeField.setEnabled(false);
		} else {
			this.cropTypeField = this.createCropTypeComboBox();
			this.cropTypeField.setDebugId("vaadin_croptype_combo");
		}
		this.cropTypeField.setWidth("250px");
		this.cropTypeField.setStyleName("hide-caption");

		final Label lblCrop = new Label();
		lblCrop.setDebugId("lblCrop");
		lblCrop.setValue(this.messageSource.getMessage(Message.BASIC_DETAILS_CROP));
		lblCrop.setStyleName("label-bold");
		lblCrop.setContentMode(Label.CONTENT_XHTML);

		final Label lblProjectName = new Label();
		lblProjectName.setDebugId("lblProjectName");
		lblProjectName.setValue(this.messageSource.getMessage(Message.BASIC_DETAILS_PROGRAM_NAME));
		lblProjectName.setStyleName("label-bold");
		lblProjectName.setContentMode(Label.CONTENT_XHTML);

		final Label lblStartDate = new Label();
		lblStartDate.setDebugId("lblStartDate");
		lblStartDate.setValue(this.messageSource.getMessage(Message.BASIC_DETAILS_PROGRAM_STARTDATE));
		lblStartDate.setStyleName("label-bold");
		lblStartDate.setContentMode(Label.CONTENT_XHTML);

		this.gridLayout.addComponent(lblCrop, 1, 1);
		this.gridLayout.addComponent(this.cropTypeField, 2, 1);
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

	ComboBox createCropTypeComboBox() {
		final List<CropType> cropTypes;
		try {
			final int userId = this.contextUtil.getCurrentWorkbenchUserId();
			cropTypes = this.workbenchDataManager.getCropsWithAddProgramPermission(userId);
		} catch (final MiddlewareQueryException e) {
			ProjectBasicDetailsComponent.LOG.error(this.messageSource.getMessage("INSTALL_CROPS_ERROR"), e);
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

		final BeanItemContainer<CropType> beanItemContainer = new BeanItemContainer<>(CropType.class);
		for (final CropType cropType : cropTypes) {
			beanItemContainer.addBean(cropType);
		}

		final ComboBox comboBox = new ComboBox();
		comboBox.setDebugId("comboBox");
		comboBox.setContainerDataSource(beanItemContainer);
		comboBox.setNewItemsAllowed(false);
		comboBox.setItemCaptionPropertyId("cropName");
		comboBox.setRequired(true);
		comboBox.setRequiredError(this.messageSource.getMessage("CROP_REQUIRED_ERROR") + " ");
		comboBox.setInputPrompt(this.messageSource.getMessage("CHOOSE_VALUE_PROMPT"));
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

				if (!ProjectBasicDetailsComponent.this.isUpdate) {
					if (ProjectBasicDetailsComponent.this.oldCropType == null) {
						ProjectBasicDetailsComponent.this.oldCropType = ProjectBasicDetailsComponent.this.getCropTypeBasedOnInput();
					} else {
						final CropType newCropType = ProjectBasicDetailsComponent.this.getCropTypeBasedOnInput();

						if (!ProjectBasicDetailsComponent.this.oldCropType.getCropName().equalsIgnoreCase(newCropType.getCropName())) {
							ProjectBasicDetailsComponent.this.createProjectPanel.cropTypeChanged(newCropType);
						}

						ProjectBasicDetailsComponent.this.oldCropType = newCropType;
					}

				}
			}
		});

		return comboBox;
	}

	void updateProjectDetailsFormField(final Project project) {
		this.projectNameField.setValue(project.getProjectName());
		this.startDateField.setValue(project.getStartDate());
		this.cropTypeField.setValue(this.isUpdate ? project.getCropType().getCropName() : project.getCropType());
		this.cropTypeField.setData(project.getCropType());
	}

	public boolean validate() {
		boolean success = true;
		final String projectName = (String) this.projectNameField.getValue();
		final CropType cropType = (CropType) this.cropTypeField.getData();

		this.errorDescription = new StringBuilder();

		if (projectName == null || projectName.isEmpty()) {
			this.errorDescription.append(this.messageSource.getMessage("NO_PROGRAM_NAME_ERROR")).append(" ");
			success = false;
		} else {
			// Check if the project name already exists for given crop
			try {
				if (this.workbenchDataManager.getProjectByNameAndCrop(projectName, cropType) != null && !this.isUpdate) {
					this.errorDescription.append(this.messageSource.getMessage(Message.DUPLICATE_PROGRAM_NAME_ERROR)).append(" ");
					success = false;
				}
			} catch (final MiddlewareQueryException e) {
				ProjectBasicDetailsComponent.LOG.error(this.messageSource.getMessage("CANNOT_GET_PROGRAM_BY_NAME_ERROR"), e);
				throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
			}
			// Run assigned validators

			try {
				this.projectNameField.validate();
			} catch (final InvalidValueException e) {
				this.errorDescription.append(ValidationUtil.getMessageFor(e));
				success = false;
			}

			try {
				if (!this.isUpdate) {
					this.cropTypeField.validate();
				}
			} catch (final InvalidValueException e) {
				this.errorDescription.append(ValidationUtil.getMessageFor(e));
				success = false;
			}

			try {
				this.startDateField.validate();
			} catch (final InvalidValueException e) {
				ProjectBasicDetailsComponent.LOG.debug(e.getMessage(), e);
				this.errorDescription.append(ValidationUtil.getMessageFor(e));
				success = false;
			}
		}

		if (cropType == null) {
			this.errorDescription.append(this.messageSource.getMessage("CROP_TYPE_REQUIRED_ERROR")).append(" ");
			success = false;
		}

		if (!success) {
			MessageNotifier.showRequiredFieldError(this.getWindow(), this.errorDescription.toString());
		}

		return success;
	}

	Project getProjectDetails() {
		if (!this.validate()) {
			throw new InvalidValueException(this.messageSource.getMessage("INVALID_BASIC_DETAILS_FORM_EXCEPTION"));
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
		return (CropType) this.cropTypeField.getValue();
	}

	protected void setIsUpdate(final Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public void setAlignment(final Alignment alignment) {
		this.setComponentAlignment(this.gridLayout, alignment);
	}

	void enableForm() {
		this.cropTypeField.setEnabled(false);
		this.startDateField.setEnabled(true);
		this.projectNameField.setEnabled(true);
	}

	void disableForm() {
		this.cropTypeField.setEnabled(false);
		this.startDateField.setEnabled(false);
		this.projectNameField.setEnabled(false);
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	StringBuilder getErrorDescription() {
		return this.errorDescription;
	}

	
	public TextField getProjectNameField() {
		return projectNameField;
	}

	
	public BmsDateField getStartDateField() {
		return startDateField;
	}

	
	public AbstractField getCropTypeField() {
		return cropTypeField;
	}

	
	public CropType getOldCropType() {
		return oldCropType;
	}

	
	public void setOldCropType(CropType oldCropType) {
		this.oldCropType = oldCropType;
	}

	public void setStartDateField(BmsDateField startDateField) {
		this.startDateField = startDateField;
	}
}

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.model.formfieldfactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextArea;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.ui.fields.SanitizedTextField;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.MethodClass;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <b>Description</b>: Field factory for generating Breeding Method fields for Breeding Method class.
 * <p/>
 * <br>
 * <br>
 * <p/>
 * <b>Author</b>: Jeffrey Morales <br>
 * <b>File Created</b>: August 30, 2012
 */
@Configurable
public class BreedingMethodFormFieldFactory extends DefaultFieldFactory {

	private static final String FIELD_WIDTH = "250px";
	private static final long serialVersionUID = 3560059243526106791L;
	private static final Logger LOG = LoggerFactory.getLogger(BreedingMethodFormFieldFactory.class);

	private Field methodName;
	private Field methodDescription;
	private Select methodSelectType;
	private Select methodSelectGroup;
	private Field methodCode;
	private Select methodSelectClass;

	private Boolean isEditMode;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Resource
	private ContextUtil contextUtil;
	private Integer methodId;

	public BreedingMethodFormFieldFactory(final Map<Integer, String> classMap) {
		this.initFields(classMap);
	}

	public void setMethodId(final Integer mid) {
		this.methodId = mid;
	}

	private void initFields(final Map<Integer, String> classMap) {

		this.methodName = new SanitizedTextField();
		this.methodName.setDebugId("methodName");
		this.methodName.setRequired(true);
		this.methodName.setRequiredError("Please enter a Breeding Method Name.");
		this.methodName.addValidator(new StringLengthValidator("Breeding Method Name must be 1-50 characters.", 1, 50, false));
		this.methodName.addValidator(new MethodNameValidator());
		this.methodName.setWidth(BreedingMethodFormFieldFactory.FIELD_WIDTH);

		this.methodDescription = new TextArea();
		this.methodDescription.setDebugId("methodDescription");
		this.methodDescription.setRequired(true);
		this.methodDescription.setRequiredError("Please enter a Breeding Method Description.");
		this.methodDescription
				.addValidator(new StringLengthValidator("Breeding Method Description must be 1-255 characters.", 1, 255, false));
		this.methodDescription.setWidth("375px");
		this.methodDescription.setHeight("100px");

		this.methodCode = new SanitizedTextField();
		this.methodCode.setDebugId("methodCode");
		this.methodCode.setRequired(true);
		this.methodCode.setRequiredError("Please enter a Breeding Method Code.");
		this.methodCode.addValidator(new StringLengthValidator("Breeding Method Code must be 1-8 characters.", 1, 8, false));
		this.methodCode.addValidator(new MethodCodeValidator());
		this.methodCode.setWidth("70px");

		this.methodSelectType = new Select();
		this.methodSelectType.setDebugId("methodSelectType");
		this.methodSelectType.setImmediate(true);
		this.methodSelectType.setWidth(BreedingMethodFormFieldFactory.FIELD_WIDTH);
		this.methodSelectType.addItem(MethodType.GENERATIVE.getCode());
		this.methodSelectType.setItemCaption(MethodType.GENERATIVE.getCode(), MethodType.GENERATIVE.getName());
		this.methodSelectType.addItem(MethodType.DERIVATIVE.getCode());
		this.methodSelectType.setItemCaption(MethodType.DERIVATIVE.getCode(), MethodType.DERIVATIVE.getName());
		this.methodSelectType.addItem(MethodType.MAINTENANCE.getCode());
		this.methodSelectType.setItemCaption(MethodType.MAINTENANCE.getCode(), MethodType.MAINTENANCE.getName());
		this.methodSelectType.setNullSelectionAllowed(false);
		this.methodSelectType.setRequired(true);
		this.methodSelectType.setRequiredError("Please select a Generation Advancement Type");

		this.methodSelectType.addListener(new Property.ValueChangeListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 3918955977372077902L;

			@Override
			public void valueChange(final ValueChangeEvent event) {
				BreedingMethodFormFieldFactory.this.methodSelectClass.removeAllItems();

				final List<MethodClass> methodClasses =
					MethodClass.getByMethodType().get(MethodType.getMethodType(event.getProperty().getValue().toString()));

				if (methodClasses == null) {
					return;
				}

				for (final MethodClass methodClass : methodClasses) {
					final Integer key = methodClass.getId();
					final String value = classMap.get(key);
					BreedingMethodFormFieldFactory.this.methodSelectClass.addItem(key);
					BreedingMethodFormFieldFactory.this.methodSelectClass.setItemCaption(key, value);
				}
			}

		});

		this.methodSelectGroup = new Select();
		this.methodSelectGroup.setDebugId("methodSelectGroup");
		this.methodSelectGroup.setWidth(BreedingMethodFormFieldFactory.FIELD_WIDTH);
		this.methodSelectGroup.addItem("S");
		this.methodSelectGroup.setItemCaption("S", "Self Fertilizing");
		this.methodSelectGroup.addItem("O");
		this.methodSelectGroup.setItemCaption("O", "Cross Pollinating");
		this.methodSelectGroup.addItem("C");
		this.methodSelectGroup.setItemCaption("C", "Clonally Propagating");
		this.methodSelectGroup.addItem("G");
		this.methodSelectGroup.setItemCaption("G", "All System");
		this.methodSelectGroup.select("");
		this.methodSelectGroup.setNullSelectionAllowed(false);

		this.methodSelectClass = new Select();
		this.methodSelectClass.setDebugId("methodSelectClass");
		this.methodSelectClass.setWidth(BreedingMethodFormFieldFactory.FIELD_WIDTH);
		this.methodSelectClass.setNullSelectionAllowed(false);
		this.methodSelectClass.setRequired(true);
		this.methodSelectClass.setRequiredError("Please select a Class");
		this.methodSelectClass.setImmediate(true);
	}

	@Override
	public Field createField(final Item item, final Object propertyId, final Component uiContext) {

		final Field field = super.createField(item, propertyId, uiContext);

		if ("mname".equals(propertyId)) {
			return this.methodName;

		} else if ("mdesc".equals(propertyId)) {
			return this.methodDescription;
		} else if ("mcode".equals(propertyId)) {
			return this.methodCode;
		} else if ("mtype".equals(propertyId)) {
			return this.methodSelectType;
		} else if ("mgrp".equals(propertyId)) {
			return this.methodSelectGroup;
		} else if ("geneq".equals(propertyId)) {
			return this.methodSelectClass;
		}
		return field;
	}

	class MethodNameValidator implements Validator {

		public static final String BREEDING_METHOD_ALREADY_EXISTS = "Breeding Method \"%s\" already exists.";
		/**
		 *
		 */
		private static final long serialVersionUID = 1243756382212441154L;

		@Override
		public void validate(final Object value) {

			if (value == null) {
				return;
			}

			if (!this.isValid(value)) {
				throw new InvalidValueException(String.format(BREEDING_METHOD_ALREADY_EXISTS, value.toString()));
			}
		}

		@Override
		public boolean isValid(final Object value) {

			if (value == null) {
				return true;
			}

			Method method = null;
			try {
				final Project currentProject = BreedingMethodFormFieldFactory.this.contextUtil.getProjectInContext();
				method = BreedingMethodFormFieldFactory.this.germplasmDataManager.getMethodByName(value.toString());
			} catch (final MiddlewareQueryException e) {
				BreedingMethodFormFieldFactory.LOG.error(e.getMessage(), e);
			}

			// If Method ID is not null, then Method already exists
			if (method != null && method.getMid() != null) {

				if (BreedingMethodFormFieldFactory.this.isEditMode && BreedingMethodFormFieldFactory.this.methodName.isModified()
					&& BreedingMethodFormFieldFactory.this.methodId != null
					&& !BreedingMethodFormFieldFactory.this.methodId.equals(method.getMid())) {
					return false;
				} else if (!BreedingMethodFormFieldFactory.this.isEditMode) {
					return false;
				}

			}
			return true;
		}

	}

	class MethodCodeValidator implements Validator {

		/**
		 *
		 */
		private static final long serialVersionUID = -830998093233311135L;
		public static final String BREEDING_METHOD_WITH_CODE_ALREADY_EXISTS = "Breeding Method with Code \"%s\" already exists.";

		@Override
		public void validate(final Object value) {

			if (value == null) {
				return;
			}

			if (!this.isValid(value)) {
				throw new InvalidValueException(String.format(BREEDING_METHOD_WITH_CODE_ALREADY_EXISTS, value.toString()));
			}
		}

		@Override
		public boolean isValid(final Object value) {

			if (value == null) {
				return true;
			}

			Method method = null;
			try {
				final Project currentProject = BreedingMethodFormFieldFactory.this.contextUtil.getProjectInContext();
				method = BreedingMethodFormFieldFactory.this.germplasmDataManager.getMethodByCode(value.toString());
			} catch (final MiddlewareQueryException e) {
				BreedingMethodFormFieldFactory.LOG.error(e.getMessage(), e);
			}

			// If Method ID is not null, then Method already exists
			if (method != null && method.getMid() != null) {

				if (BreedingMethodFormFieldFactory.this.isEditMode && BreedingMethodFormFieldFactory.this.methodCode.isModified()) {
					return false;
				} else if (!BreedingMethodFormFieldFactory.this.isEditMode) {
					return false;
				}

			}
			return true;
		}

	}

	public void setGermplasmDataManager(final GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setEditMode(final Boolean editMode) {
		this.isEditMode = editMode;
	}

	public void setMethodName(final Field methodName) {
		this.methodName = methodName;
	}

	public void setMethodCode(final Field methodCode) {
		this.methodCode = methodCode;
	}

}

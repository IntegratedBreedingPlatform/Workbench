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

package org.generationcp.ibpworkbench.model.formfieldfactory;

import java.util.Map;
import javax.annotation.Resource;

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
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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

	public BreedingMethodFormFieldFactory(Map<Integer, String> classMap) {
		this.initFields(classMap);
	}

	public BreedingMethodFormFieldFactory(Map<Integer, String> classMap, Boolean isEditMode) {
		this.isEditMode = isEditMode;
		this.initFields(classMap);
	}

	private void initFields(final Map<Integer, String> classMap) {

		this.methodName = new SanitizedTextField();
		this.methodName.setDebugId("methodName");
		this.methodName.setRequired(true);
		this.methodName.setRequiredError("Please enter a Breeding Method Name.");
		this.methodName.addValidator(new StringLengthValidator("Breeding Method Name must be 1-50 characters.", 1, 50, false));
		this.methodName.addValidator(new Validator() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1243756382212441154L;

			@Override
			public void validate(Object value) {

				if (value == null) {
					return;
				}

				if (!this.isValid(value)) {
					throw new InvalidValueException(String.format("Breeding Method \"%s\" already exists.", value.toString()));
				}
			}

			@Override
			public boolean isValid(Object value) {

				if (value == null) {
					return true;
				}

				Method method = null;
				try {
					Project currentProject = contextUtil.getProjectInContext();
					method = germplasmDataManager.getMethodByName(value.toString(), currentProject.getUniqueID());
				} catch (MiddlewareQueryException e) {
					BreedingMethodFormFieldFactory.LOG.error(e.getMessage(), e);
				}

				// If Method ID is not null, then Method already exists
				if (method != null && method.getMid() != null) {

					if (BreedingMethodFormFieldFactory.this.isEditMode && BreedingMethodFormFieldFactory.this.methodName.isModified()) {
						return false;
					} else if (!BreedingMethodFormFieldFactory.this.isEditMode) {
						return false;
					}

				}
				return true;
			}

		});
		this.methodName.setWidth(BreedingMethodFormFieldFactory.FIELD_WIDTH);

		this.methodDescription = new TextArea();
		this.methodDescription.setDebugId("methodDescription");
		this.methodDescription.setRequired(true);
		this.methodDescription.setRequiredError("Please enter a Breeding Method Description.");
		this.methodDescription.addValidator(new StringLengthValidator("Breeding Method Description must be 1-255 characters.", 1, 255,
				false));
		this.methodDescription.setWidth("375px");
		this.methodDescription.setHeight("100px");

		this.methodCode = new SanitizedTextField();
		this.methodCode.setDebugId("methodCode");
		this.methodCode.setRequired(true);
		this.methodCode.setRequiredError("Please enter a Breeding Method Code.");
		this.methodCode.addValidator(new StringLengthValidator("Breeding Method Code must be 1-8 characters.", 1, 8, false));
		this.methodCode.addValidator(new Validator() {

			/**
			 *
			 */
			private static final long serialVersionUID = -830998093233311135L;

			@Override
			public void validate(Object value) {

				if (value == null) {
					return;
				}

				if (!this.isValid(value)) {
					throw new InvalidValueException(String.format("Breeding Method with Code \"%s\" already exists.", value.toString()));
				}
			}

			@Override
			public boolean isValid(Object value) {

				if (value == null) {
					return true;
				}

				Method method = null;
				try {
					Project currentProject = contextUtil.getProjectInContext();
					method = germplasmDataManager.getMethodByCode(value.toString(), currentProject.getUniqueID());
				} catch (MiddlewareQueryException e) {
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

		});
		this.methodCode.setWidth("70px");

		this.methodSelectType = new Select();
		this.methodSelectType.setDebugId("methodSelectType");
		this.methodSelectType.setImmediate(true);
		this.methodSelectType.setWidth(BreedingMethodFormFieldFactory.FIELD_WIDTH);
		this.methodSelectType.addItem("GEN");
		this.methodSelectType.setItemCaption("GEN", "Generative");
		this.methodSelectType.addItem("DER");
		this.methodSelectType.setItemCaption("DER", "Derivative");
		this.methodSelectType.addItem("MAN");
		this.methodSelectType.setItemCaption("MAN", "Maintenance");
		this.methodSelectType.setNullSelectionAllowed(false);
		this.methodSelectType.setRequired(true);
		this.methodSelectType.setRequiredError("Please select a Generation Advancement Type");

		this.methodSelectType.addListener(new Property.ValueChangeListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = 3918955977372077902L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				BreedingMethodFormFieldFactory.this.methodSelectClass.removeAllItems();
				if ("GEN".equals(event.getProperty().getValue().toString())) {
					for (Integer key : classMap.keySet()) {
						String value = classMap.get(key);

						if (key.equals(TermId.CROSSING_METHODS_CLASS.getId()) || key.equals(TermId.MUTATION_METHODS_CLASS.getId())
								|| key.equals(TermId.GENETIC_MODIFICATION_CLASS.getId())
								|| key.equals(TermId.CYTOGENETIC_MANIPULATION.getId())) {
							BreedingMethodFormFieldFactory.this.methodSelectClass.addItem(key);
							BreedingMethodFormFieldFactory.this.methodSelectClass.setItemCaption(key, value);
						}
					}
				} else if ("DER".equals(event.getProperty().getValue().toString())) {
					for (Integer key : classMap.keySet()) {
						String value = classMap.get(key);
						if (key.equals(TermId.BULKING_BREEDING_METHOD_CLASS.getId())
								|| key.equals(TermId.NON_BULKING_BREEDING_METHOD_CLASS.getId())) {
							BreedingMethodFormFieldFactory.this.methodSelectClass.addItem(key);
							BreedingMethodFormFieldFactory.this.methodSelectClass.setItemCaption(key, value);
						}
					}
				} else if ("MAN".equals(event.getProperty().getValue().toString())) {
					for (Integer key : classMap.keySet()) {
						String value = classMap.get(key);
						if (key.equals(TermId.SEED_INCREASE_METHOD_CLASS.getId())
								|| key.equals(TermId.SEED_ACQUISITION_METHOD_CLASS.getId())
								|| key.equals(TermId.CULTIVAR_FORMATION_METHOD_CLASS.getId())) {
							BreedingMethodFormFieldFactory.this.methodSelectClass.addItem(key);
							BreedingMethodFormFieldFactory.this.methodSelectClass.setItemCaption(key, value);
						}
					}
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
	public Field createField(Item item, Object propertyId, Component uiContext) {

		Field field = super.createField(item, propertyId, uiContext);

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
}

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

package org.generationcp.ibpworkbench.ui.programmethods;

import java.util.Arrays;
import java.util.Map;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.formfieldfactory.BreedingMethodFormFieldFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

/**
 * <b>Description</b>: Custom form for adding Locations.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Jeffrey Morales <br>
 * <b>File Created</b>: August 20, 2012
 */
@Configurable
public class BreedingMethodForm extends Form {

	/**
	 *
	 */
	private static final long serialVersionUID = -3649453194910730855L;
	private final MethodView modelBean;
	private GridLayout grid;
	private final Map<Integer, String> classMap;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public BreedingMethodForm(final Map<Integer, String> classMap) {
		this.classMap = classMap;
		this.modelBean = new MethodView();
		this.initializeComponents();

	}

	public BreedingMethodForm(final Map<Integer, String> classMap, final MethodView methodView) {
		this.classMap = classMap;
		this.modelBean = methodView;
		this.initializeComponents();
	}


	protected void initializeComponents() {
		this.grid = new GridLayout(2, 6);
		this.grid.setDebugId("grid");
		this.grid.setSpacing(true);
		this.grid.setMargin(new Layout.MarginInfo(true, false, false, false));
		this.setLayout(this.grid);

		this.setItemDataSource(new BeanItem<MethodView>(this.modelBean));

		this.setComponentError(null);
		if (this.modelBean.getMid() != null) {
			this.setFormFieldFactory(new BreedingMethodFormFieldFactory(this.classMap, true, this.modelBean.getMid()));
		} else {
			this.setFormFieldFactory(new BreedingMethodFormFieldFactory(this.classMap, false));
		}

		this.setVisibleItemProperties(Arrays.asList(new String[] {"mname", "mcode", "mdesc", "mtype", "mgrp", "geneq"}));

		this.setWriteThrough(false);
		this.setInvalidCommitted(false);
		this.setValidationVisibleOnCommit(false);

	}

	@Override
	protected void attachField(final Object propertyId, final Field field) {
		field.setStyleName("hide-caption");
		field.setCaption(null);
		if ("mname".equals(propertyId)) {
			this.grid.addComponent(field, 1, 0);
		} else if ("mcode".equals(propertyId)) {
			this.grid.addComponent(field, 1, 1);
		} else if ("mdesc".equals(propertyId)) {
			this.grid.addComponent(field, 1, 2);
		} else if ("mtype".equals(propertyId)) {
			this.grid.addComponent(field, 1, 3);
		} else if ("mgrp".equals(propertyId)) {
			this.grid.addComponent(field, 1, 4);
		} else if ("geneq".equals(propertyId)) {
			this.grid.addComponent(field, 1, 5);
		}
	}

	@Override
	public void attach() {

		if (this.grid.getComponent(0, 0) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.BREED_METH_NAME), true), 0, 0);
		}

		if (this.grid.getComponent(0, 1) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.BREED_METH_CODE), true), 0, 1);
		}

		if (this.grid.getComponent(0, 2) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.BREED_METH_DESC), true), 0, 2);
		}

		if (this.grid.getComponent(0, 3) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.BREED_METH_TYPE), true), 0, 3);
		}

		if (this.grid.getComponent(0, 4) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.BREED_METH_GRP)), 0, 4);
		}

		if (this.grid.getComponent(0, 5) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.BREED_METH_CLASS), true), 0, 5);
		}

		super.attach();

	}

	private Label createLabel(final String caption) {
		return this.createLabel(caption, false);
	}

	private Label createLabel(final String caption, final boolean required) {

		final Label label = new Label();
		label.setDebugId("label");
		label.setContentMode(Label.CONTENT_XHTML);
		label.setWidth("220px");

		if (!required) {
			label.setValue(String.format("<b>%s</b>", caption));
		} else {
			label.setValue(String.format("<b>%s</b> <span style='color: red'>*</span>", caption));
		}

		return label;
	}
}

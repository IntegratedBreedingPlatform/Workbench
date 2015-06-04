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

package org.generationcp.ibpworkbench.ui.form;

import java.util.Arrays;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
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
public class AddLocationForm extends Form {

	private static final long serialVersionUID = 865075321914843448L;

	private GridLayout grid;

	private final ProgramLocationsPresenter presenter;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public AddLocationForm(ProgramLocationsPresenter presenter) {
		this.presenter = presenter;
		this.assemble();
	}

	protected void assemble() {

		this.initializeComponents();
		this.initializeLayout();
	}

	protected void initializeLayout() {

	}

	protected void initializeComponents() {

		this.grid = new GridLayout(4, 7);
		this.grid.setSpacing(true);
		this.grid.setMargin(new Layout.MarginInfo(true, false, false, false));
		this.setLayout(this.grid);

		this.setItemDataSource(new BeanItem<LocationViewModel>(new LocationViewModel()));

		this.setComponentError(null);
		this.setFormFieldFactory(new LocationFormFieldFactory(this.presenter));

		this.setVisibleItemProperties(Arrays.asList(new String[] {"locationName", "locationAbbreviation", "ltype", "cntryid", "provinceId",
				"latitude", "longitude", "altitude"}));

		this.setWriteThrough(false);
		this.setInvalidCommitted(false);
		this.setValidationVisibleOnCommit(false);

	}

	@Override
	protected void attachField(Object propertyId, Field field) {
		field.setStyleName("hide-caption");
		field.setCaption(null);
		if ("locationName".equals(propertyId)) {
			this.grid.addComponent(field, 1, 0, 3, 0);
		} else if ("locationAbbreviation".equals(propertyId)) {
			this.grid.addComponent(field, 1, 1, 2, 1);
		} else if ("ltype".equals(propertyId)) {
			this.grid.addComponent(field, 1, 2, 3, 2);
		} else if ("cntryid".equals(propertyId)) {
			this.grid.addComponent(field, 1, 3, 3, 3);
		} else if ("provinceId".equals(propertyId)) {
			this.grid.addComponent(field, 1, 4, 3, 4);
		} else if ("latitude".equals(propertyId)) {
			this.grid.addComponent(field, 1, 5);
		} else if ("longitude".equals(propertyId)) {
			this.grid.addComponent(field, 2, 5);
		} else if ("altitude".equals(propertyId)) {
			this.grid.addComponent(field, 3, 5);
		}
	}

	@Override
	public void attach() {

		if (this.grid.getComponent(0, 0) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_NAME), true), 0, 0);
		}
		if (this.grid.getComponent(0, 1) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_ABBR), true), 0, 1);
		}
		if (this.grid.getComponent(0, 2) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_TYPE), true), 0, 2);
		}
		if (this.grid.getComponent(0, 3) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_COUNTRY), false), 0, 3);
		}
		if (this.grid.getComponent(0, 4) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_PROVINCE), false), 0, 4);
		}
		if (this.grid.getComponent(0, 5) == null) {
			this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_GEOGRAPHICAL_DETAILS), false), 0, 5);
		}
		if (this.grid.getComponent(1, 6) == null) {
			Label lblLatitude = this.createCaption(this.messageSource.getMessage(Message.LOC_LATITUDE), false);
			this.grid.addComponent(lblLatitude, 1, 6);
			this.grid.setComponentAlignment(lblLatitude, Alignment.TOP_LEFT);
		}
		if (this.grid.getComponent(2, 6) == null) {
			Label lblLongitude = this.createCaption(this.messageSource.getMessage(Message.LOC_LONGITUDE), false);
			this.grid.addComponent(lblLongitude, 2, 6);
			this.grid.setComponentAlignment(lblLongitude, Alignment.TOP_LEFT);
		}
		if (this.grid.getComponent(3, 6) == null) {
			Label lblLongitude = this.createCaption(this.messageSource.getMessage(Message.LOC_ALTITUDE), false);
			this.grid.addComponent(lblLongitude, 3, 6);
			this.grid.setComponentAlignment(lblLongitude, Alignment.TOP_LEFT);
		}

		super.attach();

	}

	private Label createLabel(String caption, boolean required) {

		Label label = new Label();
		label.setContentMode(Label.CONTENT_XHTML);
		label.setWidth("220px");

		if (!required) {
			label.setValue(String.format("<b>%s</b>", caption));
		} else {
			label.setValue(String.format("<b>%s</b> <span style='color: red'>*</span>", caption));
		}

		return label;

	}

	private Label createCaption(String caption, boolean required) {

		Label label = new Label();
		label.setContentMode(Label.CONTENT_XHTML);
		label.setWidth("80px");

		if (!required) {
			label.setValue(String.format("<i>%s</i>", caption));
		} else {
			label.setValue(String.format("<i>%s</i> <span style='color: red'>*</span>", caption));
		}

		return label;

	}
}

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.form;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;

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
	public static final String LOCATION_NAME = "locationName";
	public static final String LOCATION_ABBREVIATION = "locationAbbreviation";
	public static final String LTYPE = "ltype";
	public static final String CNTRYID = "cntryid";
	public static final String PROVINCE_ID = "provinceId";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String ALTITUDE = "altitude";
	public static final String CROP_ACCESSIBLE = "cropAccessible";

	public static final String LABEL_FORMAT = "<b>%s</b>";
	public static final String REQUIRED_LABEL_FORMAT = "<b>%s</b> <span style='color: red'>*</span>";
	public static final String CAPTION_FORMAT = "<i>%s</i>";
	public static final String REQUIRED_CAPTION_FORMAT = "<i>%s</i> <span style='color: red'>*</span>";

	private GridLayout grid;

	private final ProgramLocationsPresenter presenter;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public AddLocationForm(final ProgramLocationsPresenter presenter) {
		this.presenter = presenter;
		this.initializeComponents();

	}

	protected void initializeComponents() {

		this.grid = new GridLayout(4, 8);
		this.grid.setDebugId("grid");
		this.grid.setSpacing(true);
		this.grid.setMargin(new Layout.MarginInfo(true, false, false, false));
		this.setLayout(this.grid);

		this.setItemDataSource(new BeanItem<LocationViewModel>(new LocationViewModel()));

		this.setComponentError(null);
		this.setFormFieldFactory(new LocationFormFieldFactory(this.presenter));

		this.setVisibleItemProperties(Arrays.asList(
				new String[] {LOCATION_NAME, LOCATION_ABBREVIATION, LTYPE, CNTRYID, PROVINCE_ID, LATITUDE, LONGITUDE, ALTITUDE,
						CROP_ACCESSIBLE}));

		this.setWriteThrough(false);
		this.setInvalidCommitted(false);
		this.setValidationVisibleOnCommit(false);

	}

	@Override
	protected void attachField(final Object propertyId, final Field field) {
		field.setStyleName("hide-caption");
		field.setCaption(null);
		if (LOCATION_NAME.equals(propertyId)) {
			this.grid.addComponent(field, 1, 0, 3, 0);
		} else if (LOCATION_ABBREVIATION.equals(propertyId)) {
			this.grid.addComponent(field, 1, 1, 2, 1);
		} else if (LTYPE.equals(propertyId)) {
			this.grid.addComponent(field, 1, 2, 3, 2);
		} else if (CNTRYID.equals(propertyId)) {
			this.grid.addComponent(field, 1, 3, 3, 3);
		} else if (PROVINCE_ID.equals(propertyId)) {
			this.grid.addComponent(field, 1, 4, 3, 4);
		} else if (LATITUDE.equals(propertyId)) {
			this.grid.addComponent(field, 1, 5);
		} else if (LONGITUDE.equals(propertyId)) {
			this.grid.addComponent(field, 2, 5);
		} else if (ALTITUDE.equals(propertyId)) {
			this.grid.addComponent(field, 3, 5);
		} else if (CROP_ACCESSIBLE.equals(propertyId)) {
			this.grid.addComponent(field, 1, 7, 3, 7);
		}

	}

	@Override
	public void attach() {

		this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_NAME), true), 0, 0);
		this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_ABBR), true), 0, 1);
		this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_TYPE), true), 0, 2);
		this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_COUNTRY), false), 0, 3);
		this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_PROVINCE), false), 0, 4);
		this.grid.addComponent(this.createLabel(this.messageSource.getMessage(Message.LOC_GEOGRAPHICAL_DETAILS), false), 0, 5);

		final Label lblLatitude = this.createCaption(this.messageSource.getMessage(Message.LOC_LATITUDE), false);
		this.grid.addComponent(lblLatitude, 1, 6);
		this.grid.setComponentAlignment(lblLatitude, Alignment.TOP_LEFT);

		final Label lblLongitude = this.createCaption(this.messageSource.getMessage(Message.LOC_LONGITUDE), false);
		this.grid.addComponent(lblLongitude, 2, 6);
		this.grid.setComponentAlignment(lblLongitude, Alignment.TOP_LEFT);

		final Label lblAltitude = this.createCaption(this.messageSource.getMessage(Message.LOC_ALTITUDE), false);
		this.grid.addComponent(lblAltitude, 3, 6);
		this.grid.setComponentAlignment(lblLongitude, Alignment.TOP_LEFT);

		this.grid.getComponent(1, 7).setCaption(this.messageSource.getMessage(Message.LOC_CROP_ACCESSIBLE));

		super.attach();

	}

	protected Label createLabel(final String caption, final boolean required) {

		final Label label = new Label();
		label.setDebugId("label");
		label.setContentMode(Label.CONTENT_XHTML);
		label.setWidth("220px");

		if (!required) {
			label.setValue(String.format(LABEL_FORMAT, caption));
		} else {
			label.setValue(String.format(REQUIRED_LABEL_FORMAT, caption));
		}

		return label;

	}

	protected Label createCaption(final String caption, final boolean required) {

		final Label label = new Label();
		label.setDebugId("label");
		label.setContentMode(Label.CONTENT_XHTML);
		label.setWidth("80px");

		if (!required) {
			label.setValue(String.format(CAPTION_FORMAT, caption));
		} else {
			label.setValue(String.format(REQUIRED_CAPTION_FORMAT, caption));
		}

		return label;

	}

	// For unit test purpose only
	protected GridLayout getGrid() {
		return grid;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

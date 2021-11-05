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
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;

/**
 * <b>Description</b>: Custom form for adding Locations.
 * <p>
 * <br>
 * <br>
 * <p>
 * <b>Author</b>: Jeffrey Morales <br>
 * <b>File Created</b>: August 20, 2012
 */
@Configurable
public class LocationForm extends Form {

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

	private LocationFormFieldFactory locationFormFieldFactory;

	private boolean locationUsedInAnyProgram = false;

	private LocationViewModel locationViewModel = new LocationViewModel();

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private LocationDataManager locationDataManager;

	@Autowired
	private StudyDataManager studyDataManager;

	public LocationForm(final LocationViewModel locationViewModel, final LocationFormFieldFactory locationFormFieldFactory) {
		this.locationFormFieldFactory = locationFormFieldFactory;
		if (locationViewModel != null) {
			this.locationViewModel = locationViewModel;
		}
		this.initializeComponents();

	}

	protected void initializeComponents() {

		this.grid = new GridLayout(4, 8);
		this.grid.setDebugId("grid");
		this.grid.setSpacing(true);
		this.grid.setMargin(new Layout.MarginInfo(true, false, false, false));
		this.setLayout(this.grid);

		this.setItemDataSource(new BeanItem<>(this.locationViewModel));

		this.setComponentError(null);
		this.setFormFieldFactory(locationFormFieldFactory);

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

		// Set the selected value of Province combobox after all fields in the form are initialized. The option items of Province
		// will only be available after the Country combobox is initialized.
		final Location provinceValue = this.locationDataManager.getLocationByID(this.locationViewModel.getProvinceId());
		this.locationFormFieldFactory.getProvince().setValue(provinceValue);

		super.attach();

	}

	@Override
	public void commit() {

		super.commit();

		this.updateLocationModelView();

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

	public void updateLocationModelView() {

		// The LocationViewModel's country name, province name and location type name and programUUID properties are not bound to the Form,
		// so when they are changed in the UI, they are not automatically updated. So we have to manually update them.

		final Country country = this.locationFormFieldFactory.retrieveCountryValue();
		if (country != null) {
			this.locationViewModel.setCntryName(country.getIsoabbr());
			this.locationViewModel.setCntryFullName(country.getIsofull());
		} else {
			this.locationViewModel.setCntryName(null);
			this.locationViewModel.setCntryFullName(null);
		}

		final Location province = this.locationFormFieldFactory.retrieveProvinceValue();
		if (province != null) {
			this.locationViewModel.setProvinceName(province.getLname());
		} else {
			this.locationViewModel.setProvinceName(null);
		}

		final UserDefinedField locationType = this.locationFormFieldFactory.retrieveLocationType();
		if (locationType != null) {
			this.locationViewModel.setLtypeStr(locationType.getFname());
		}

	}

	public boolean isLocationUsedInAnyProgram() {
		return locationUsedInAnyProgram;
	}

	// For unit test purpose only
	protected GridLayout getGrid() {
		return grid;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public boolean isLocationNameModified() {
		return this.locationFormFieldFactory.getLocationName().isModified();
	}

	public boolean isLocationAbbreviationModified() {
		return this.locationFormFieldFactory.getLocationAbbreviation().isModified();
	}

	public String getLocationNameValue() {
		return (String) this.locationFormFieldFactory.getLocationName().getValue();
	}

	public String getLocationAbbreviationValue() {
		return (String) this.locationFormFieldFactory.getLocationAbbreviation().getValue();
	}

	public void setLocationFormFieldFactory(final LocationFormFieldFactory locationFormFieldFactory) {
		this.locationFormFieldFactory = locationFormFieldFactory;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setLocationDataManager(final LocationDataManager locationDataManager) {
		this.locationDataManager = locationDataManager;
	}
}

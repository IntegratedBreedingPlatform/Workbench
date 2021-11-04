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
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.fields.SanitizedTextField;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.form.LocationForm;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <b>Description</b>: Field factory for generating Location fields for Location class.
 * <p/>
 * <br>
 * <br>
 * <p/>
 * <b>Author</b>: Jeffrey Morales <br>
 * <b>File Created</b>: Jul 16, 2012
 */
@Configurable
public class LocationFormFieldFactory extends DefaultFieldFactory implements InitializingBean {

	private static final long serialVersionUID = 3560059243526106791L;

	private Field locationName;
	private Field locationAbbreviation;
	private ComboBox lType;
	private ComboBox country;
	private ComboBox province;
	private TextField latitude;
	private TextField longitude;
	private TextField altitude;

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(LocationFormFieldFactory.class);

	private final ProgramLocationsPresenter presenter;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public LocationFormFieldFactory(final ProgramLocationsPresenter presenter) {
		this.presenter = presenter;
	}

	@SuppressWarnings("serial")
	private void initFields(final List<UserDefinedField> udfList, final List<Country> countryList) {
		Collections.sort(countryList, new Comparator<Country>() {

			@Override
			public int compare(final Country o1, final Country o2) {
				return o1.getIsoabbr().compareTo(o2.getIsoabbr());
			}
		});

		this.locationName = new SanitizedTextField();
		this.locationName.setDebugId("locationName");
		this.locationName.setWidth("250px");
		this.locationName.setRequired(true);
		this.locationName.setRequiredError(messageSource.getMessage(Message.ADD_LOCATION_REQUIRED_LOCATION_NAME_ERROR));
		this.locationName
				.addValidator(new StringLengthValidator(messageSource.getMessage(Message.ADD_LOCATION_NAME_LENGTH_ERROR), 1, 60, false));

		this.locationAbbreviation = new SanitizedTextField();
		this.locationAbbreviation.setDebugId("locationAbbreviation");

		this.locationAbbreviation.setWidth("70px");
		this.locationAbbreviation.setRequired(true);
		this.locationAbbreviation.setRequiredError(messageSource.getMessage(Message.ADD_LOCATION_REQUIRED_LOCATION_ABBR_ERROR));
		this.locationAbbreviation
				.addValidator(new StringLengthValidator(messageSource.getMessage(Message.ADD_LOCATION_ABBR_LENGTH_ERROR), 1, 8, false));

		final BeanContainer<String, UserDefinedField> udfBeanContainer =
				new BeanContainer<String, UserDefinedField>(UserDefinedField.class);
		final BeanContainer<String, Country> countryBeanContainer = new BeanContainer<String, Country>(Country.class);
		final BeanContainer<String, Location> provinceBeanContainer = new BeanContainer<String, Location>(Location.class);

		udfBeanContainer.setBeanIdProperty("fldno");
		udfBeanContainer.addAll(udfList);

		countryBeanContainer.setBeanIdProperty("cntryid");
		countryBeanContainer.addAll(countryList);

		provinceBeanContainer.setBeanIdProperty("locid");
		provinceBeanContainer.addAll(new ArrayList<Location>());

		this.lType = new ComboBox();
		this.lType.setDebugId("lType");
		this.lType.setWidth("250px");
		this.lType.setContainerDataSource(udfBeanContainer);
		this.lType.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		this.lType.setItemCaptionPropertyId("fname");
		this.lType.setNullSelectionAllowed(false);
		this.lType.setRequired(true);
		this.lType.setRequiredError(messageSource.getMessage(Message.ADD_LOCATION_REQUIRED_LOCATION_TYPE_ERROR));

		this.country = new ComboBox();
		this.country.setDebugId("country");
		this.country.setWidth("250px");
		this.country.setContainerDataSource(countryBeanContainer);
		this.country.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		this.country.setItemCaptionPropertyId("isoabbr");
		this.country.setImmediate(true);

		this.country.addListener(new Property.ValueChangeListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -41619287191762967L;

			@Override
			public void valueChange(final Property.ValueChangeEvent valueChangeEvent) {
				LocationFormFieldFactory.this.province.setValue(null);
				final Object countryIdValue = LocationFormFieldFactory.this.country.getValue();
				if (countryIdValue != null) {
					@SuppressWarnings("unchecked") final BeanContainer<String, Location> container =
							(BeanContainer<String, Location>) LocationFormFieldFactory.this.province.getContainerDataSource();
					container.removeAllItems();
					container.addAll(LocationFormFieldFactory.this.presenter.getAllProvincesByCountry((Integer) countryIdValue));
				}
			}
		});

		this.province = new ComboBox();
		this.province.setDebugId("province");
		this.province.setWidth("250px");
		this.province.setContainerDataSource(provinceBeanContainer);
		this.province.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		this.province.setItemCaptionPropertyId("lname");
		this.province.setImmediate(true);

		this.latitude = new TextField() {

			/**
			 *
			 */
			private static final long serialVersionUID = 4415306632530012904L;

			@Override
			public Object getValue() {
				return super.getValue() != null ? super.getValue().toString().trim() : null;
			}
		};
		this.latitude.setWidth("70px");
		this.latitude.setRequired(false);
		this.latitude.addValidator(new DoubleValidator(messageSource.getMessage(Message.ADD_LOCATION_INVALID_NUMBER_ERROR)));
		this.latitude.setNullSettingAllowed(true);
		this.latitude.setNullRepresentation("");

		this.longitude = new TextField() {

			/**
			 *
			 */
			private static final long serialVersionUID = -2597945842698982054L;

			@Override
			public Object getValue() {
				return super.getValue() != null ? super.getValue().toString().trim() : null;
			}
		};

		this.longitude.setWidth("70px");
		this.longitude.setRequired(false);
		this.longitude.addValidator(new DoubleValidator(messageSource.getMessage(Message.ADD_LOCATION_INVALID_NUMBER_ERROR)));
		this.longitude.setNullSettingAllowed(true);
		this.longitude.setNullRepresentation("");

		this.altitude = new TextField() {

			/**
			 *
			 */
			private static final long serialVersionUID = 2435847934279942220L;

			@Override
			public Object getValue() {
				return super.getValue() != null ? super.getValue().toString().trim() : null;
			}
		};
		this.altitude.setWidth("70px");
		this.altitude.setRequired(false);
		this.altitude.addValidator(new DoubleValidator(messageSource.getMessage(Message.ADD_LOCATION_INVALID_NUMBER_ERROR)));
		this.altitude.setNullSettingAllowed(true);
		this.altitude.setNullRepresentation("");
	}

	@Override
	public Field createField(final Item item, final Object propertyId, final Component uiContext) {
		if (LocationForm.LOCATION_NAME.equals(propertyId)) {
			return this.locationName;
		} else if (LocationForm.LOCATION_ABBREVIATION.equals(propertyId)) {
			return this.locationAbbreviation;
		} else if (LocationForm.LTYPE.equals(propertyId)) {
			return this.lType;
		} else if (LocationForm.CNTRYID.equals(propertyId)) {
			return this.country;
		} else if (LocationForm.PROVINCE_ID.equals(propertyId)) {
			return this.province;
		} else if (LocationForm.LATITUDE.equals(propertyId)) {
			return this.latitude;
		} else if (LocationForm.LONGITUDE.equals(propertyId)) {
			return this.longitude;
		} else if (LocationForm.ALTITUDE.equals(propertyId)) {
			return this.altitude;
		}
		return super.createField(item, propertyId, uiContext);
	}

	public Field getLocationName() {
		return locationName;
	}

	public Field getLocationAbbreviation() {
		return locationAbbreviation;
	}

	public ComboBox getlType() {
		return lType;
	}

	public ComboBox getCountry() {
		return country;
	}

	public ComboBox getProvince() {
		return province;
	}

	public TextField getLatitude() {
		return latitude;
	}

	public TextField getLongitude() {
		return longitude;
	}

	public TextField getAltitude() {
		return altitude;
	}

	public Country retrieveCountryValue() {
		final BeanContainer<String, Country> beanContainer = (BeanContainer<String, Country>) this.country.getContainerDataSource();
		final BeanItem<Country> beanItem = beanContainer.getItem(this.country.getValue());
		return beanItem == null ? null : beanItem.getBean();
	}

	public Location retrieveProvinceValue() {
		final BeanContainer<String, Location> beanContainer = (BeanContainer<String, Location>) this.province.getContainerDataSource();
		final BeanItem<Location> beanItem = beanContainer.getItem(this.province.getValue());
		return beanItem == null ? null : beanItem.getBean();
	}

	public UserDefinedField retrieveLocationType() {
		final BeanContainer<String, UserDefinedField> beanContainer =
				(BeanContainer<String, UserDefinedField>) this.lType.getContainerDataSource();
		final BeanItem<UserDefinedField> beanItem = beanContainer.getItem(this.lType.getValue());
		return beanItem == null ? null : beanItem.getBean();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initFields(presenter.getUDFByLocationAndLType(), presenter.getCountryList());
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}

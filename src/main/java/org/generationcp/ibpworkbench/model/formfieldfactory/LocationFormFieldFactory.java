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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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
public class LocationFormFieldFactory extends DefaultFieldFactory {

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

	private ProgramLocationsPresenter presenter;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public LocationFormFieldFactory(ProgramLocationsPresenter presenter) {
		this.initFields(presenter.getUDFByLocationAndLType(), presenter.getCountryList());
		this.presenter = presenter;
	}

	@SuppressWarnings("serial")
	private void initFields(List<UserDefinedField> udfList, List<Country> countryList) {
		Collections.sort(countryList, new Comparator<Country>() {

			@Override
			public int compare(Country o1, Country o2) {
				return o1.getIsoabbr().compareTo(o2.getIsoabbr());
			}
		});

		this.locationName = new TextField() {

			/**
			 *
			 */
			 private static final long serialVersionUID = 3402159687769548386L;

			 @Override
			 public Object getValue() {
				 return super.getValue() != null ? super.getValue().toString().trim() : null;
			 }
		};

		this.locationName.setWidth("250px");
		this.locationName.setRequired(true);
		this.locationName.setRequiredError("Please enter a Location Name.");
		this.locationName.addValidator(new StringLengthValidator("Location Name must be 1-60 characters.", 1, 60, false));

		this.locationAbbreviation = new TextField() {

			/**
			 *
			 */
			private static final long serialVersionUID = -8447225372577850948L;

			@Override
			public Object getValue() {
				return super.getValue() != null ? super.getValue().toString().trim() : null;
			}
		};

		this.locationAbbreviation.setWidth("70px");
		this.locationAbbreviation.setRequired(true);
		this.locationAbbreviation.setRequiredError("Please enter a Location Abbreviation.");
		this.locationAbbreviation.addValidator(new StringLengthValidator("Location Abbreviation must be 1-8 characters.", 1, 8, false));

		BeanContainer<String, UserDefinedField> udfBeanContainer = new BeanContainer<String, UserDefinedField>(UserDefinedField.class);
		BeanContainer<String, Country> countryBeanContainer = new BeanContainer<String, Country>(Country.class);
		BeanContainer<String, Location> provinceBeanContainer = new BeanContainer<String, Location>(Location.class);

		udfBeanContainer.setBeanIdProperty("fldno");
		udfBeanContainer.addAll(udfList);

		countryBeanContainer.setBeanIdProperty("cntryid");
		countryBeanContainer.addAll(countryList);

		provinceBeanContainer.setBeanIdProperty("locid");
		provinceBeanContainer.addAll(new ArrayList<Location>());

		this.lType = new ComboBox();
		this.lType.setWidth("250px");
		this.lType.setContainerDataSource(udfBeanContainer);
		this.lType.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		this.lType.setItemCaptionPropertyId("fname");
		this.lType.setNullSelectionAllowed(false);
		this.lType.setRequired(true);
		this.lType.setRequiredError("Please a select Location Type.");

		this.country = new ComboBox();
		this.country.setWidth("250px");
		this.country.setContainerDataSource(countryBeanContainer);
		this.country.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		this.country.setItemCaptionPropertyId("isoabbr");

		this.country.addListener(new Property.ValueChangeListener() {

			/**
			 *
			 */
			private static final long serialVersionUID = -41619287191762967L;

			@Override
			public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
				LocationFormFieldFactory.this.province.setValue(null);
				Object countryIdValue = LocationFormFieldFactory.this.country.getValue();
				if (countryIdValue != null) {
					@SuppressWarnings("unchecked")
					BeanContainer<String, Location> container =
							(BeanContainer<String, Location>) LocationFormFieldFactory.this.province.getContainerDataSource();
					container.removeAllItems();
					container.addAll(LocationFormFieldFactory.this.presenter.getAllProvincesByCountry((Integer) countryIdValue));
				}
			}
		});

		this.province = new ComboBox();
		this.province.setWidth("250px");
		this.province.setContainerDataSource(provinceBeanContainer);
		this.province.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
		this.province.setItemCaptionPropertyId("lname");

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
		this.latitude.addValidator(new DoubleValidator("Please enter a valid number"));
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
		this.longitude.addValidator(new DoubleValidator("Please enter a valid number"));
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
		this.altitude.addValidator(new DoubleValidator("Please enter a valid number"));
		this.altitude.setNullSettingAllowed(true);
		this.altitude.setNullRepresentation("");

	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {
		if ("locationName".equals(propertyId)) {
			this.messageSource.setCaption(this.locationName, Message.LOC_NAME);
			return this.locationName;
		} else if ("locationAbbreviation".equals(propertyId)) {
			this.messageSource.setCaption(this.locationAbbreviation, Message.LOC_ABBR);
			return this.locationAbbreviation;
		} else if ("ltype".equals(propertyId)) {
			this.messageSource.setCaption(this.lType, Message.LOC_TYPE);
			return this.lType;
		} else if ("cntryid".equals(propertyId)) {
			this.messageSource.setCaption(this.country, Message.LOC_COUNTRY);
			return this.country;
		} else if ("provinceId".equals(propertyId)) {
			this.messageSource.setCaption(this.province, Message.LOC_PROVINCE);
			return this.province;
		}
		if ("latitude".equals(propertyId)) {
			this.messageSource.setCaption(this.latitude, Message.LOC_LATITUDE);
			return this.latitude;
		} else if ("longitude".equals(propertyId)) {
			this.messageSource.setCaption(this.longitude, Message.LOC_LONGITUDE);
			return this.longitude;
		} else if ("altitude".equals(propertyId)) {
			this.messageSource.setCaption(this.altitude, Message.LOC_ALTITUDE);
			return this.altitude;
		}

		return super.createField(item, propertyId, uiContext);
	}
}

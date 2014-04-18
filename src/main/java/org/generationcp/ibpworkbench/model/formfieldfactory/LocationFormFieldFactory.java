/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.ibpworkbench.model.formfieldfactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.validator.StringLengthValidator;


/**
 * <b>Description</b>: Field factory for generating Location fields for Location class.
 * <p/>
 * <br>
 * <br>
 * <p/>
 * <b>Author</b>: Jeffrey Morales
 * <br>
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

    private static final Logger LOG = LoggerFactory.getLogger(LocationFormFieldFactory.class);

    private ProgramLocationsPresenter presenter;


    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public LocationFormFieldFactory(ProgramLocationsPresenter presenter) {

        try {
            initFields(presenter.getUDFByLocationAndLType(), presenter.getCountryList());
            this.presenter = presenter;
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }
    }

    private void initFields(List<UserDefinedField> udfList, List<Country> countryList) {
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country o1, Country o2) {


                return o1.getIsoabbr().compareTo(o2.getIsoabbr());


                //return o1.getIsofull().compareToIgnoreCase(o2.getIsofull());
            }
        });

        locationName = new TextField();
        locationName.setWidth("250px");
        locationName.setRequired(true);
        locationName.setRequiredError("Please enter a Location Name.");
        locationName.addValidator(new StringLengthValidator("Location Name must be 1-60 characters.", 1, 60, false));

        locationAbbreviation = new TextField();
        locationAbbreviation.setWidth("70px");
        locationAbbreviation.setRequired(true);
        locationAbbreviation.setRequiredError("Please enter a Location Abbreviation.");
        locationAbbreviation.addValidator(new StringLengthValidator("Location Abbreviation must be 1-8 characters.", 1, 8, false));

        BeanContainer<String, UserDefinedField> udfBeanContainer = new BeanContainer<String, UserDefinedField>(UserDefinedField.class);
        BeanContainer<String, Country> countryBeanContainer = new BeanContainer<String, Country>(Country.class);
        BeanContainer<String, Location> provinceBeanContainer = new BeanContainer<String, Location>(Location.class);

        udfBeanContainer.setBeanIdProperty("fldno");
        udfBeanContainer.addAll(udfList);

        countryBeanContainer.setBeanIdProperty("cntryid");
        countryBeanContainer.addAll(countryList);

        provinceBeanContainer.setBeanIdProperty("locid");
        provinceBeanContainer.addAll(new ArrayList<Location>());


        lType = new ComboBox();
        lType.setWidth("250px");
        lType.setContainerDataSource(udfBeanContainer);
        lType.setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_PROPERTY);
        lType.setItemCaptionPropertyId("fname");
        lType.setNullSelectionAllowed(false);
        lType.select(udfBeanContainer.getItemIds().toArray()[0]);
        lType.setRequired(true);
        lType.setRequiredError("Please a select Location Type.");

        country = new ComboBox();
        country.setWidth("250px");
        country.setContainerDataSource(countryBeanContainer);
        country.setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_PROPERTY);
        country.setItemCaptionPropertyId("isoabbr");

        country.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                province.setValue(null);
                Object countryIdValue = country.getValue();
                if (countryIdValue != null) {
                    try {
                        BeanContainer<String, Location> container = (BeanContainer<String, Location>) province.getContainerDataSource();
                        container.removeAllItems();
                        container.addAll(presenter.getAllProvincesByCountry((Integer) countryIdValue));
                    } catch (MiddlewareQueryException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        province = new ComboBox();
        province.setWidth("250px");
        province.setContainerDataSource(provinceBeanContainer);
        province.setItemCaptionMode(NativeSelect.ITEM_CAPTION_MODE_PROPERTY);
        province.setItemCaptionPropertyId("lname");

    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        if ("locationName".equals(propertyId)) {
            messageSource.setCaption(locationName, Message.LOC_NAME);
            return locationName;

        } else if ("locationAbbreviation".equals(propertyId)) {
            messageSource.setCaption(locationAbbreviation, Message.LOC_ABBR);
            return locationAbbreviation;
        } else if ("ltype".equals(propertyId)) {
            messageSource.setCaption(lType, Message.LOC_TYPE);
            return lType;
        } else if ("cntryid".equals(propertyId)) {
            messageSource.setCaption(country, Message.LOC_COUNTRY);
            return country;
        } else if ("provinceId".equals(propertyId)) {
            messageSource.setCaption(province, Message.LOC_PROVINCE);
            return province;
        }

        return super.createField(item, propertyId, uiContext);
    }
}

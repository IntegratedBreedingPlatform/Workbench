package org.generationcp.ibpworkbench.ui.form;

import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import junit.framework.Assert;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class LocationFormTest {

	public static final String LOC_NAME = "LOC_NAME";
	public static final String LOC_ABBR = "LOC_ABBR";
	public static final String LOC_TYPE = "LOC_TYPE";
	public static final String LOC_COUNTRY = "LOC_COUNTRY";
	public static final String LOC_PROVINCE = "LOC_PROVINCE";
	public static final String LOC_GEOGRAPHICAL_DETAILS = "LOC_GEOGRAPHICAL_DETAILS";
	public static final String LOC_LATITUDE = "LOC_LATITUDE";
	public static final String LOC_LONGITUDE = "LOC_LONGITUDE";
	public static final String LOC_ALTITUDE = "LOC_ALTITUDE";
	public static final String LOC_CROP_ACCESSIBLE = "LOC_CROP_ACCESSIBLE";

	@Mock
	private ProgramLocationsPresenter programLocationsPresenter;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private LocationForm locationForm;

	private LocationFormFieldFactory locationFormFieldFactory;

	private LocationViewModel locationViewModel = new LocationViewModel();

	@Before
	public void setUp() throws Exception {

		locationFormFieldFactory =  new LocationFormFieldFactory(programLocationsPresenter);
		locationFormFieldFactory.setMessageSource(messageSource);
		locationFormFieldFactory.afterPropertiesSet();

		locationForm = new LocationForm(locationViewModel, programLocationsPresenter, locationFormFieldFactory);
		locationForm.setMessageSource(messageSource);

		Mockito.when(programLocationsPresenter.getUDFByLocationAndLType()).thenReturn(new ArrayList<UserDefinedField>());
		Mockito.when(programLocationsPresenter.getCountryList()).thenReturn(new ArrayList<Country>());

		Mockito.when(messageSource.getMessage(Message.LOC_NAME)).thenReturn(LOC_NAME);
		Mockito.when(messageSource.getMessage(Message.LOC_ABBR)).thenReturn(LOC_ABBR);
		Mockito.when(messageSource.getMessage(Message.LOC_TYPE)).thenReturn(LOC_TYPE);
		Mockito.when(messageSource.getMessage(Message.LOC_COUNTRY)).thenReturn(LOC_COUNTRY);
		Mockito.when(messageSource.getMessage(Message.LOC_PROVINCE)).thenReturn(LOC_PROVINCE);
		Mockito.when(messageSource.getMessage(Message.LOC_GEOGRAPHICAL_DETAILS)).thenReturn(LOC_GEOGRAPHICAL_DETAILS);
		Mockito.when(messageSource.getMessage(Message.LOC_LATITUDE)).thenReturn(LOC_LATITUDE);
		Mockito.when(messageSource.getMessage(Message.LOC_LONGITUDE)).thenReturn(LOC_LONGITUDE);
		Mockito.when(messageSource.getMessage(Message.LOC_ALTITUDE)).thenReturn(LOC_ALTITUDE);
		Mockito.when(messageSource.getMessage(Message.LOC_CROP_ACCESSIBLE)).thenReturn(LOC_CROP_ACCESSIBLE);

	}

	@Test
	public void testAttachField() throws Exception {

		Assert.assertEquals(locationFormFieldFactory.getLocationName(), locationForm.getGrid().getComponent(1, 0));
		Assert.assertEquals(locationFormFieldFactory.getLocationAbbreviation(), locationForm.getGrid().getComponent(1, 1));
		Assert.assertEquals(locationFormFieldFactory.getlType(), locationForm.getGrid().getComponent(1, 2));
		Assert.assertEquals(locationFormFieldFactory.getCountry(), locationForm.getGrid().getComponent(1, 3));
		Assert.assertEquals(locationFormFieldFactory.getProvince(), locationForm.getGrid().getComponent(1, 4));
		Assert.assertEquals(locationFormFieldFactory.getLatitude(), locationForm.getGrid().getComponent(1, 5));
		Assert.assertEquals(locationFormFieldFactory.getLongitude(), locationForm.getGrid().getComponent(2, 5));
		Assert.assertEquals(locationFormFieldFactory.getAltitude(), locationForm.getGrid().getComponent(3, 5));
		Assert.assertEquals(locationFormFieldFactory.getCropAccessible(), locationForm.getGrid().getComponent(1, 7));

	}

	@Test
	public void testAttach() {

		locationForm.attach();

		final GridLayout grid = locationForm.getGrid();

		Assert.assertEquals(String.format(LocationForm.REQUIRED_LABEL_FORMAT, LOC_NAME), ((Label) grid.getComponent(0, 0)).getValue());
		Assert.assertEquals(String.format(LocationForm.REQUIRED_LABEL_FORMAT, LOC_ABBR), ((Label) grid.getComponent(0, 1)).getValue());
		Assert.assertEquals(String.format(LocationForm.REQUIRED_LABEL_FORMAT, LOC_TYPE), ((Label) grid.getComponent(0, 2)).getValue());
		Assert.assertEquals(String.format(LocationForm.LABEL_FORMAT, LOC_COUNTRY), ((Label) grid.getComponent(0, 3)).getValue());
		Assert.assertEquals(String.format(LocationForm.LABEL_FORMAT, LOC_PROVINCE), ((Label) grid.getComponent(0, 4)).getValue());
		Assert.assertEquals(String.format(LocationForm.LABEL_FORMAT, LOC_GEOGRAPHICAL_DETAILS),
				((Label) grid.getComponent(0, 5)).getValue());

		Assert.assertEquals(String.format(LocationForm.CAPTION_FORMAT, LOC_LATITUDE), ((Label) grid.getComponent(1, 6)).getValue());
		Assert.assertEquals(String.format(LocationForm.CAPTION_FORMAT, LOC_LONGITUDE), ((Label) grid.getComponent(2, 6)).getValue());
		Assert.assertEquals(String.format(LocationForm.CAPTION_FORMAT, LOC_ALTITUDE), ((Label) grid.getComponent(3, 6)).getValue());

		Assert.assertEquals(LOC_CROP_ACCESSIBLE, grid.getComponent(1, 7).getCaption());

	}

}

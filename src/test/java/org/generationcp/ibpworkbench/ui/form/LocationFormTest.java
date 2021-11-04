package org.generationcp.ibpworkbench.ui.form;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.formfieldfactory.LocationFormFieldFactory;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	public static final String PROGRAM_UUID = "239847832-28473284-asjhdqasd";

	@Mock
	private ProgramLocationsPresenter programLocationsPresenter;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private LocationFormFieldFactory locationFormFieldFactoryMock;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private LocationDataManager locationDataManager;

	private LocationForm locationForm;

	private LocationViewModel locationViewModel;

	@Before
	public void setUp() throws Exception {

		locationViewModel = this.createLocationViewModelTestData();

		locationForm = new LocationForm(locationViewModel, locationFormFieldFactoryMock);
		locationForm.setMessageSource(this.messageSource);
		locationForm.setStudyDataManager(this.studyDataManager);
		locationForm.setContextUtil(this.contextUtil);
		locationForm.setLocationDataManager(this.locationDataManager);

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

	}

	@Test
	public void testAttachField() throws Exception {

		final LocationFormFieldFactory locationFormFieldFactory = new LocationFormFieldFactory(programLocationsPresenter);
		locationFormFieldFactory.setMessageSource(messageSource);
		locationFormFieldFactory.afterPropertiesSet();

		this.locationForm.setLocationFormFieldFactory(locationFormFieldFactory);

		Assert.assertEquals(locationFormFieldFactoryMock.getLocationName(), locationForm.getGrid().getComponent(1, 0));
		Assert.assertEquals(locationFormFieldFactoryMock.getLocationAbbreviation(), locationForm.getGrid().getComponent(1, 1));
		Assert.assertEquals(locationFormFieldFactoryMock.getlType(), locationForm.getGrid().getComponent(1, 2));
		Assert.assertEquals(locationFormFieldFactoryMock.getCountry(), locationForm.getGrid().getComponent(1, 3));
		Assert.assertEquals(locationFormFieldFactoryMock.getProvince(), locationForm.getGrid().getComponent(1, 4));
		Assert.assertEquals(locationFormFieldFactoryMock.getLatitude(), locationForm.getGrid().getComponent(1, 5));
		Assert.assertEquals(locationFormFieldFactoryMock.getLongitude(), locationForm.getGrid().getComponent(2, 5));
		Assert.assertEquals(locationFormFieldFactoryMock.getAltitude(), locationForm.getGrid().getComponent(3, 5));
	}

	@Test
	public void testAttach() throws Exception {

		final LocationFormFieldFactory locationFormFieldFactory = new LocationFormFieldFactory(programLocationsPresenter);
		locationFormFieldFactory.setMessageSource(messageSource);
		locationFormFieldFactory.afterPropertiesSet();

		final LocationForm testLocationForm = new LocationForm(locationViewModel, locationFormFieldFactory);
		testLocationForm.setMessageSource(this.messageSource);
		testLocationForm.setStudyDataManager(this.studyDataManager);
		testLocationForm.setContextUtil(this.contextUtil);
		testLocationForm.setLocationDataManager(locationDataManager);

		final Location province = new Location();
		province.setLocid(this.locationViewModel.getProvinceId());
		when(locationDataManager.getLocationByID(this.locationViewModel.getProvinceId())).thenReturn(province);

		testLocationForm.attach();

		final GridLayout grid = testLocationForm.getGrid();

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

		Assert.assertEquals(this.locationViewModel.getProvinceId(), ((ComboBox) grid.getComponent(1, 4)).getValue());

	}

	@Test
	public void testtestUpdateLocationModelViewFormFieldsHaveValue() {

		final String isoabbr = "Philippines";
		final String isofull = "Republic of the Philippines";
		final String lname = "Metro Manila";
		final String fname = "Breeding Location";

		final Country country = new Country();
		country.setIsoabbr(isoabbr);
		country.setIsofull(isofull);

		final Location province = new Location();
		province.setLname(lname);

		final UserDefinedField userDefinedField = new UserDefinedField();
		userDefinedField.setFname(fname);

		when(this.locationFormFieldFactoryMock.retrieveCountryValue()).thenReturn(country);
		when(this.locationFormFieldFactoryMock.retrieveProvinceValue()).thenReturn(province);
		when(this.locationFormFieldFactoryMock.retrieveLocationType()).thenReturn(userDefinedField);

		this.locationForm.updateLocationModelView();

		assertEquals(isoabbr, this.locationViewModel.getCntryName());
		assertEquals(isofull, this.locationViewModel.getCntryFullName());
		assertEquals(lname, this.locationViewModel.getProvinceName());
		assertEquals(fname, this.locationViewModel.getLtypeStr());

	}

	@Test
	public void testIsLocationNameModified() {

		final Field field = mock(Field.class);
		when(locationFormFieldFactoryMock.getLocationName()).thenReturn(field);
		when(field.isModified()).thenReturn(true);

		assertTrue(this.locationForm.isLocationNameModified());

		verify(this.locationFormFieldFactoryMock).getLocationName();
		verify(field).isModified();

	}

	@Test
	public void testGetLocationNameValue() {

		final Field field = mock(Field.class);
		final String fieldValue = "myValue";
		when(locationFormFieldFactoryMock.getLocationName()).thenReturn(field);
		when(field.getValue()).thenReturn(fieldValue);

		assertEquals(fieldValue, this.locationForm.getLocationNameValue());

		verify(this.locationFormFieldFactoryMock).getLocationName();
		verify(field).getValue();

	}

	private LocationViewModel createLocationViewModelTestData() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationId(new Double(Math.random() * 10).intValue());
		locationViewModel.setLocationName("LocationName");
		locationViewModel.setLocationAbbreviation("locationAbbreviation");
		locationViewModel.setLtype(1);
		locationViewModel.setLtypeStr("locationType");
		locationViewModel.setCntryid(2);
		locationViewModel.setCntryName("countryName");
		locationViewModel.setCntryFullName("countryFullName");
		locationViewModel.setProvinceId(3);
		locationViewModel.setProvinceName("provinceName");
		locationViewModel.setAltitude(5d);
		locationViewModel.setLatitude(6d);
		locationViewModel.setLongitude(7d);
		return locationViewModel;
	}

}

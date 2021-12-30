package org.generationcp.ibpworkbench.ui.programlocations;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

public class ProgramLocationsPresenterTest {

	private static final int NO_OF_FAVORITES = 2;
	private static final int NO_OF_LOCATIONS = 5;
	private static final int NO_OF_LOCATION_WITH_PROGRAM_UUID = 3;
	private static final String COUNTRY_FULL_NAME = "Kenya";
	private static final String LOCATION_TYPE = "Breeding Location";
	private static final Integer LOCID = 123;
	private static final String LOCATION_NAME = "My Location";
	private static final String LOCATION_ABBREVIATION = "ABC";
	private static final Integer CNTRYID = 567;
	private static final Integer LTYPE = 789;
	private static final double LATITUDE = 1111.0;
	private static final double LONGITUDE = 2222.0;
	private static final double ALTITUDE = 3333.0;
	private static final Integer PROVINCE_ID = 1223;
	private static final String PROVINCE_NAME = "Nairobi";
	private static final String DUMMY_PROGRAM_UUID = "1234567890";

	private ProgramLocationsPresenter controller;

	@Mock
	private LocationDataManager locationDataManager;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private ProgramLocationsView programLocationsView;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		final Project project = this.getProject();
		this.controller = new ProgramLocationsPresenter(project, this.germplasmDataManager, this.locationDataManager);
		this.controller.setView(this.programLocationsView);
	}

	private Project getProject() {
		final Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName("Project Name");
		project.setUniqueID(ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID);
		return project;
	}

	@Test
	public void testGetFilteredResults() throws Exception {
		// getFilteredResults with default parameters (only the location type is set)
		final Integer locationType = 1;
		this.setupGetFilteredResults(null, locationType, null, ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID);

		final String notNullTest = "Get filtered list by location type should return a result";
		final String locationTypeTest = "Location type must be equal to " + locationType;

		Collection<LocationViewModel> result = null;
		try {
			result = this.controller.getFilteredResults(null, locationType, null);
			Assert.assertNotNull(notNullTest, result);
		} catch (final MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}

		for (final LocationViewModel locationViewModel : result) {
			Assert.assertEquals(locationTypeTest, locationType, locationViewModel.getLtype());
		}

		final Integer expectedNoOfResults = ProgramLocationsPresenterTest.NO_OF_LOCATIONS - 1;
		Assert.assertTrue("Expecting the results returned " + expectedNoOfResults + " but returned " + result.size(),
				expectedNoOfResults.equals(result.size()));
	}

	@Test
	public void testGetFilteredResultsByCountryIdAndLocationType() throws Exception {
		final Integer countryId = 1;
		final Integer locationType = 1;
		this.setupGetFilteredResults(countryId, locationType, null, ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID);

		final String notNullTest = "Get filtered list by country and location type should return a result";
		final String locationTypeTest = "Location type must be equal to " + locationType;
		final String countryIdTest = "Country id must be equal to " + countryId;

		Collection<LocationViewModel> result = null;
		try {
			result = this.controller.getFilteredResults(countryId, locationType, null);
			Assert.assertNotNull(notNullTest, result);
		} catch (final MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}

		for (final LocationViewModel locationViewModel : result) {
			Assert.assertEquals(countryIdTest, countryId, locationViewModel.getCntryid());
			Assert.assertEquals(locationTypeTest, locationType, locationViewModel.getLtype());
		}

		final Integer expectedNoOfResults = ProgramLocationsPresenterTest.NO_OF_LOCATIONS - 1;
		Assert.assertTrue("Expecting the results returned " + expectedNoOfResults + " but returned " + result.size(),
				expectedNoOfResults.equals(result.size()));
	}

	@Test
	public void testGetFilteredResultsByLocationName() throws Exception {
		final String locationName = "TEST LOCATION";
		this.setupGetFilteredResults(null, null, locationName, ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID);

		final String notNullTest = "Get filtered list by location name should return a result";
		final String locationNameTest = "Location name must be equal to " + locationName;

		Collection<LocationViewModel> result = null;
		try {
			result = this.controller.getFilteredResults(null, null, locationName);
			Assert.assertNotNull(notNullTest, result);
		} catch (final MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}

		for (final LocationViewModel locationViewModel : result) {
			Assert.assertEquals(locationNameTest, locationName, locationViewModel.getLocationName());
		}

		final Integer expectedNoOfResults = ProgramLocationsPresenterTest.NO_OF_LOCATIONS - 1;
		Assert.assertTrue("Expecting the results returned " + expectedNoOfResults + " but returned " + result.size(),
				expectedNoOfResults.equals(result.size()));

	}

	private void setupGetFilteredResults(final Integer countryId, final Integer locationType, String locationName,
			final String programUUID) {
		try {
			Country country = null;
			if (countryId != null) {
				country = Mockito.mock(Country.class);
				country.setCntryid(countryId);
			}

			locationName = locationName != null ? locationName : "";

			final List<Location> locationList = new ArrayList<Location>();
			final List<LocationDetails> locationDetailsList = new ArrayList<LocationDetails>();
			for (int i = 0; i < ProgramLocationsPresenterTest.NO_OF_LOCATIONS; i++) {
				final Integer locId = i + 1;

				final Location location = this.createTestLocation(countryId, locationType, locationName, locId);

				locationList.add(location);

				final LocationDetails locationDetail = this.createTestLocationDetails(countryId, locationType, locationName, locId);

				locationDetailsList.add(locationDetail);

				Mockito.when(this.locationDataManager.getLocationDetailsByLocId(location.getLocid(), 0, 1)).thenReturn(locationDetailsList);
			}

			final LocationDetails LocationDetails = locationDetailsList.get(ProgramLocationsPresenterTest.NO_OF_LOCATION_WITH_PROGRAM_UUID);
			locationDetailsList.remove(LocationDetails);
			Mockito.when(this.locationDataManager.getFilteredLocationsDetails(countryId, locationType, null))
					.thenReturn(locationDetailsList);
			Mockito.when(this.locationDataManager.getFilteredLocationsDetails(null, null, locationName))
					.thenReturn(locationDetailsList);

		} catch (final MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	private LocationDetails createTestLocationDetails(final Integer countryId, final Integer locationType, final String locationName,
			final Integer locId) {
		final LocationDetails locationDetail = new LocationDetails();
		locationDetail.setLocid(locId);
		locationDetail.setLocationName(locationName);
		locationDetail.setLocationAbbreviation(LOCATION_ABBREVIATION);
		locationDetail.setLtype(locationType);
		locationDetail.setCntryid(countryId);
		locationDetail.setLocationType(LOCATION_TYPE);
		locationDetail.setProvinceName(PROVINCE_NAME);
		locationDetail.setCountryFullName(COUNTRY_FULL_NAME);
		locationDetail.setAltitude(ALTITUDE);
		locationDetail.setLatitude(LATITUDE);
		locationDetail.setLongitude(LONGITUDE);
		locationDetail.setProvinceId(PROVINCE_ID);
		return locationDetail;
	}

	private Location createTestLocation(final Integer countryId, final Integer locationType, final String locationName,
		final Integer locId) {
		final Location location = Mockito.mock(Location.class);
		Mockito.when(location.getLocid()).thenReturn(locId);
		Mockito.when(location.getLname()).thenReturn(locationName);
		Mockito.when(location.getLabbr()).thenReturn(LOCATION_ABBREVIATION);
		Mockito.when(location.getLtype()).thenReturn(locationType);

		final Location province = Mockito.mock(Location.class);
		Mockito.when(province.getLocid()).thenReturn(PROVINCE_ID);
		Mockito.when(location.getProvince()).thenReturn(province);

		Mockito.when(location.getAltitude()).thenReturn(ALTITUDE);
		Mockito.when(location.getLatitude()).thenReturn(LATITUDE);
		Mockito.when(location.getLongitude()).thenReturn(LONGITUDE);

		if (countryId != null) {
			final Country country = this.createCountry(countryId);
			Mockito.when(location.getCountry()).thenReturn(country);
		}
		return location;
	}

	private Country createCountry(final Integer countryId) {
		final Country country = Mockito.mock(Country.class);
		Mockito.when(country.getCntryid()).thenReturn(countryId);
		return country;
	}

	@Test
	public void testGetSavedProgramLocations() {
		final ProgramFavorite.FavoriteType entityType = FavoriteType.LOCATION;
		List<LocationViewModel> results = new ArrayList<LocationViewModel>();
		final Integer locationType = 1;

		try {
			this.setupGetFilteredResults(null, locationType, null, ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID);
			this.setUpFavoriteLocations(entityType);
			results = this.controller.getSavedProgramLocations();
		} catch (final MiddlewareQueryException e) {
			Assert.fail();
		}

		Assert.assertTrue("Expecting to return " + ProgramLocationsPresenterTest.NO_OF_FAVORITES + " but returned " + results.size(),
				ProgramLocationsPresenterTest.NO_OF_FAVORITES == results.size());
	}

	private void setUpFavoriteLocations(final ProgramFavorite.FavoriteType entityType) throws MiddlewareQueryException {
		final List<ProgramFavorite> favorites = new ArrayList<ProgramFavorite>();

		for (int i = 0; i < ProgramLocationsPresenterTest.NO_OF_FAVORITES; i++) {
			final Integer locId = i + 1;
			final ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(locId);
			favorite.setEntityType(entityType);
			favorite.setUniqueID(ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID);
			favorites.add(favorite);
		}

		Mockito.when(this.germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID))
				.thenReturn(favorites);

	}

	@Test
	public void testCreateLocationViewModelList() {

		final List<LocationDetails> locationDetailsList = new ArrayList<>();
		final LocationDetails locationDetails = new LocationDetails();

		locationDetails.setLocid(LOCID);
		locationDetails.setLocationName(LOCATION_NAME);
		locationDetails.setLocationAbbreviation(LOCATION_ABBREVIATION);
		locationDetails.setCountryFullName(COUNTRY_FULL_NAME);
		locationDetails.setLocationType(LOCATION_TYPE);
		locationDetails.setCntryid(CNTRYID);
		locationDetails.setLtype(LTYPE);
		locationDetails.setLatitude(LATITUDE);
		locationDetails.setLongitude(LONGITUDE);
		locationDetails.setAltitude(ALTITUDE);
		locationDetailsList.add(locationDetails);

		final Collection<LocationViewModel> result = controller.createLocationViewModelList(locationDetailsList);

		final LocationViewModel locationViewModel = result.iterator().next();

		Assert.assertEquals(LOCID, locationViewModel.getLocationId());
		Assert.assertEquals(LOCATION_NAME, locationViewModel.getLocationName());
		Assert.assertEquals(LOCATION_ABBREVIATION, locationViewModel.getLocationAbbreviation());
		Assert.assertEquals(COUNTRY_FULL_NAME, locationViewModel.getCntryFullName());
		Assert.assertEquals(LOCATION_TYPE, locationViewModel.getLtypeStr());
		Assert.assertEquals(CNTRYID, locationViewModel.getCntryid());
		Assert.assertEquals(LTYPE, locationViewModel.getLtype());
		Assert.assertThat(LATITUDE, is(locationViewModel.getLatitude()));
		Assert.assertThat(LONGITUDE, is(locationViewModel.getLongitude()));
		Assert.assertThat(ALTITUDE, is(locationViewModel.getAltitude()));
	}

	@Test
	public void testConvertFromLocationToLocationViewModel() {
		final Location location = this.createTestLocation(CNTRYID, LTYPE, LOCATION_NAME, LOCID);
		final LocationViewModel result = this.controller.convertFromLocationToLocationViewModel(location);

		Assert.assertEquals(LOCID, result.getLocationId());
		Assert.assertEquals(LOCATION_NAME, result.getLocationName());
		Assert.assertEquals(LOCATION_ABBREVIATION, result.getLocationAbbreviation());
		Assert.assertEquals(LTYPE, result.getLtype());
		Assert.assertEquals(CNTRYID, result.getCntryid());
		Assert.assertThat(LONGITUDE, is(result.getLongitude()));
		Assert.assertThat(LATITUDE, is(result.getLatitude()));
		Assert.assertThat(ALTITUDE, is(result.getAltitude()));
		Assert.assertEquals(PROVINCE_ID, result.getProvinceId());
	}

	@Test
	public void testConvertFromLocationDetailsToLocationViewModel() {
		final LocationDetails location = this.createTestLocationDetails(CNTRYID, LTYPE, LOCATION_NAME, LOCID);
		final LocationViewModel result = this.controller.convertFromLocationDetailsToLocationViewModel(location);

		Assert.assertEquals(LOCID, result.getLocationId());
		Assert.assertEquals(LOCATION_NAME, result.getLocationName());
		Assert.assertEquals(LOCATION_ABBREVIATION, result.getLocationAbbreviation());
		Assert.assertEquals(LTYPE, result.getLtype());
		Assert.assertEquals(CNTRYID, result.getCntryid());
		Assert.assertEquals(COUNTRY_FULL_NAME, result.getCntryFullName());
		Assert.assertThat(LONGITUDE, is(result.getLongitude()));
		Assert.assertThat(LATITUDE, is(result.getLatitude()));
		Assert.assertThat(ALTITUDE, is(result.getAltitude()));
		Assert.assertEquals(PROVINCE_ID, result.getProvinceId());
		Assert.assertEquals(PROVINCE_NAME, result.getProvinceName());
	}

	@Test
	public void testUpdateLocation() {

		final boolean isEditedFromAvailableTable = true;
		final LocationViewModel locationViewModel = createLocationViewModel();

		final Country country = Mockito.mock(Country.class);
		Mockito.when(this.locationDataManager.getCountryById(CNTRYID)).thenReturn(country);

		this.controller.updateLocation(locationViewModel, isEditedFromAvailableTable);

		Mockito.verify(this.locationDataManager).addLocation(ArgumentMatchers.any(Location.class));
		Mockito.verify(this.programLocationsView).refreshLocationViewItemInTable(isEditedFromAvailableTable, locationViewModel);
		Mockito.verify(this.locationDataManager).getCountryById(CNTRYID);
	}

	private LocationViewModel createLocationViewModel() {

		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationId(LOCID);
		locationViewModel.setLocationName(LOCATION_NAME);
		locationViewModel.setLocationAbbreviation(LOCATION_ABBREVIATION);
		locationViewModel.setLtype(LTYPE);
		locationViewModel.setCntryid(CNTRYID);
		locationViewModel.setLongitude(LONGITUDE);
		locationViewModel.setLatitude(LATITUDE);
		locationViewModel.setAltitude(ALTITUDE);
		locationViewModel.setProvinceId(PROVINCE_ID);

		return locationViewModel;

	}

}

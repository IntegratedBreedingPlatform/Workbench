package org.generationcp.ibpworkbench.ui.programlocations;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

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

				final Location location = this.createTestLocation(countryId, locationType, locationName, programUUID, locId);

				locationList.add(location);

				final LocationDetails locationDetail = this.createTestLocationDetails(countryId, locationType, locationName, locId);

				locationDetailsList.add(locationDetail);

				Mockito.when(this.locationDataManager.getLocationDetailsByLocId(location.getLocid(), 0, 1)).thenReturn(locationDetailsList);
			}

			final Location location = locationList.get(ProgramLocationsPresenterTest.NO_OF_LOCATION_WITH_PROGRAM_UUID);
			location.setUniqueID("9876543210");
			final LocationDetails LocationDetails = locationDetailsList.get(ProgramLocationsPresenterTest.NO_OF_LOCATION_WITH_PROGRAM_UUID);
			locationDetailsList.remove(LocationDetails);
			Mockito.when(this.locationDataManager.getFilteredLocationsDetails(countryId, locationType, null, programUUID))
					.thenReturn(locationDetailsList);
			Mockito.when(this.locationDataManager.getFilteredLocationsDetails(null, null, locationName, programUUID))
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
		locationDetail.setProgramUUID(DUMMY_PROGRAM_UUID);
		locationDetail.setProvinceId(PROVINCE_ID);
		return locationDetail;
	}

	private Location createTestLocation(final Integer countryId, final Integer locationType, final String locationName, final String programUUID,
			final Integer locId) {
		final Location location = new Location();
		location.setLocid(locId);
		location.setLname(locationName);
		location.setLabbr(LOCATION_ABBREVIATION);
		location.setLtype(locationType);
		location.setCntryid(countryId);
		location.setUniqueID(programUUID);
		location.setSnl1id(PROVINCE_ID);
		location.setAltitude(ALTITUDE);
		location.setLatitude(LATITUDE);
		location.setLongitude(LONGITUDE);
		return location;
	}

	@Test
	public void testGetSavedProgramLocations() {
		final String entityType = "C";
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

	private void setUpFavoriteLocations(final String entityType) throws MiddlewareQueryException {
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
		locationDetails.setProgramUUID(DUMMY_PROGRAM_UUID);
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
		Assert.assertEquals(DUMMY_PROGRAM_UUID, locationViewModel.getProgramUUID());

	}

	@Test
	public void testConvertLocationViewToLocationProgramAccessible() {

		final LocationViewModel locationViewModel = createLocationViewModel();
		locationViewModel.setCropAccessible(false);

		final Location result = controller.convertLocationViewToLocation(locationViewModel);

		Assert.assertEquals((Integer) 0, result.getLrplce());
		Assert.assertEquals(LOCID, result.getLocid());
		Assert.assertEquals(LOCATION_NAME, result.getLname());
		Assert.assertEquals(LOCATION_ABBREVIATION, result.getLabbr());
		Assert.assertEquals(LTYPE, result.getLtype());
		Assert.assertEquals(CNTRYID, result.getCntryid());
		Assert.assertThat(LONGITUDE, is(result.getLongitude()));
		Assert.assertThat(LATITUDE, is(result.getLatitude()));
		Assert.assertThat(ALTITUDE, is(result.getAltitude()));
		Assert.assertEquals((Integer) 0, result.getNllp());
		Assert.assertEquals((Integer) 0, result.getSnl3id());
		Assert.assertEquals((Integer) 0, result.getSnl2id());
		Assert.assertEquals(PROVINCE_ID, result.getSnl1id());
		Assert.assertEquals(DUMMY_PROGRAM_UUID, result.getUniqueID());

	}

	@Test
	public void testConvertLocationViewToLocationCropAccessible() {

		final LocationViewModel locationViewModel = createLocationViewModel();
		locationViewModel.setCropAccessible(true);

		final Location result = controller.convertLocationViewToLocation(locationViewModel);

		Assert.assertEquals((Integer) 0, result.getLrplce());
		Assert.assertEquals(LOCID, result.getLocid());
		Assert.assertEquals(LOCATION_NAME, result.getLname());
		Assert.assertEquals(LOCATION_ABBREVIATION, result.getLabbr());
		Assert.assertEquals(LTYPE, result.getLtype());
		Assert.assertEquals(CNTRYID, result.getCntryid());
		Assert.assertThat(LONGITUDE, is(result.getLongitude()));
		Assert.assertThat(LATITUDE, is(result.getLatitude()));
		Assert.assertThat(ALTITUDE, is(result.getAltitude()));
		Assert.assertEquals((Integer) 0, result.getNllp());
		Assert.assertEquals((Integer) 0, result.getSnl3id());
		Assert.assertEquals((Integer) 0, result.getSnl2id());
		Assert.assertEquals(PROVINCE_ID, result.getSnl1id());
		Assert.assertEquals(null, result.getUniqueID());

	}

	@Test
	public void testConvertFromLocationToLocationViewModel() {
		final Location location = this.createTestLocation(CNTRYID, LTYPE, LOCATION_NAME, DUMMY_PROGRAM_UUID, LOCID);
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
		Assert.assertEquals(DUMMY_PROGRAM_UUID, result.getProgramUUID());
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
		Assert.assertEquals(DUMMY_PROGRAM_UUID, result.getProgramUUID());
	}

	@Test
	public void testUpdateLocation() {

		final boolean isEditedFromAvailableTable = true;
		final LocationViewModel locationViewModel = createLocationViewModel();

		this.controller.updateLocation(locationViewModel, isEditedFromAvailableTable);

		Mockito.verify(this.locationDataManager).addLocation(this.controller.convertLocationViewToLocation(locationViewModel));
		Mockito.verify(this.programLocationsView).refreshLocationViewItemInTable(isEditedFromAvailableTable, locationViewModel);

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


package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
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

import junit.framework.Assert;

public class ProgramLocationsPresenterTest {

	private static final int NO_OF_FAVORITES = 2;
	private static final int NO_OF_LOCATIONS = 5;
	private static final int NO_OF_LOCATION_WITH_PROGRAM_UUID = 3;
	private ProgramLocationsPresenter controller;
	private static final String DUMMY_PROGRAM_UUID = "1234567890";

	@Mock
	private static GermplasmDataManager gdm;
	@Mock
	private LocationDataManager locationDataManager;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		final Project project = this.getProject(ProgramLocationsPresenterTest.DUMMY_PROGRAM_UUID);
		this.controller =
				new ProgramLocationsPresenter(project, this.germplasmDataManager, this.workbenchDataManager, this.locationDataManager);
	}

	private Project getProject(final String dummyProgramUuid) {
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

				final Location location = new Location();
				location.setLocid(locId);
				location.setLname(locationName);
				location.setLtype(locationType);
				location.setCntryid(countryId);
				location.setUniqueID(programUUID);

				locationList.add(location);

				final LocationDetails locationDetail = new LocationDetails();
				locationDetail.setLocid(locId);
				locationDetail.setLocationName(locationName);
				locationDetail.setLtype(locationType);
				locationDetail.setCntryid(countryId);

				locationDetailsList.add(locationDetail);

				Mockito.when(this.locationDataManager.getLocationDetailsByLocId(location.getLocid(), 0, 1)).thenReturn(locationDetailsList);
			}

			final Location location = locationList.get(ProgramLocationsPresenterTest.NO_OF_LOCATION_WITH_PROGRAM_UUID);
			location.setUniqueID("9876543210");
			final LocationDetails LocationDetails = locationDetailsList.get(ProgramLocationsPresenterTest.NO_OF_LOCATION_WITH_PROGRAM_UUID);
			locationDetailsList.remove(LocationDetails);
			Mockito.when(this.locationDataManager.getFilteredLocations(countryId, locationType, null, programUUID))
					.thenReturn(locationDetailsList);
			Mockito.when(this.locationDataManager.getFilteredLocations(null, null, locationName, programUUID))
					.thenReturn(locationDetailsList);

		} catch (final MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
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
}

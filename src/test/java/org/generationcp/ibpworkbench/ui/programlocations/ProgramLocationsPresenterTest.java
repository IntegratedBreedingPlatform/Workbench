package org.generationcp.ibpworkbench.ui.programlocations;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProgramLocationsPresenterTest {

    private ProgramLocationsPresenter controller;
    private LocationDataManager locationDataManager;

    @Before
    public void setUp() {
        locationDataManager = mock(LocationDataManager.class);
        ManagerFactoryProvider managerFactoryProvider = mock(ManagerFactoryProvider.class);
        Project project = mock(Project.class);
        WorkbenchDataManager workbenchDataManager = mock(WorkbenchDataManager.class);
        controller = new ProgramLocationsPresenter(project, workbenchDataManager, managerFactoryProvider,locationDataManager);
    }

    @Test
    public void testGetFilteredResults() throws Exception {
        //getFilteredResults with default parameters (only the location type is set)
        Integer locationType = 1;
        setupGetFilteredResults(null, locationType, null);

        String notNullTest = "Get filtered list by location type should return a result";
        String locationTypeTest = "Location type must be equal to " + locationType;

        Collection<LocationViewModel> result = null;
        try {
            result = controller.getFilteredResults(null, locationType, null);
            assertNotNull(notNullTest, result);
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
        }

        for (LocationViewModel locationViewModel : result) {
            assertEquals(locationTypeTest, locationType, locationViewModel.getLtype());
        }
    }

    @Test
    public void testGetFilteredResultsByCountryIdAndLocationType() throws Exception {
        Integer countryId = 1;
        Integer locationType = 1;
        setupGetFilteredResults(countryId, locationType, null);

        String notNullTest = "Get filtered list by country and location type should return a result";
        String locationTypeTest = "Location type must be equal to " + locationType;
        String countryIdTest = "Country id must be equal to "+countryId;

        Collection<LocationViewModel> result = null;
        try {
            result = controller.getFilteredResults(countryId, locationType, null);
            assertNotNull(notNullTest,result);
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
        }

        for (LocationViewModel locationViewModel : result) {
            assertEquals(countryIdTest, countryId, locationViewModel.getCntryid());
            assertEquals(locationTypeTest, locationType, locationViewModel.getLtype());
        }
    }

    @Test
    public void testGetFilteredResultsByLocationName() throws Exception {
        String locationName = "TEST LOCATION";
        setupGetFilteredResults(null, null, locationName);

        String notNullTest = "Get filtered list by location name should return a result";
        String locationNameTest = "Location name must be equal to " + locationName;

        Collection<LocationViewModel> result = null;
        try {
            result = controller.getFilteredResults(null, null, locationName);
            assertNotNull(notNullTest,result);
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
        }

        for (LocationViewModel locationViewModel : result) {
            assertEquals(locationNameTest, locationName, locationViewModel.getLocationName());
        }

    }

    private void setupGetFilteredResults(Integer countryId, Integer locationType, String locationName) {
        try {
            Country country = null;
            if(countryId!=null) {
                country = mock(Country.class);
                country.setCntryid(countryId);
            }

            locationName = (locationName != null) ? locationName : "";

            Integer locId = 1;
            Location location = new Location();
            location.setLocid(locId);
            location.setLname(locationName);
            location.setLtype(locationType);
            location.setCntryid(countryId);

            List<Location> locationList = new ArrayList<Location>();
            locationList.add(location);

            LocationDetails locationDetail = new LocationDetails();
            locationDetail.setLocid(locId);
            locationDetail.setLocation_name(locationName);
            locationDetail.setLtype(locationType);
            locationDetail.setCntryid(countryId);

            List<LocationDetails> locationDetailsList = new ArrayList<LocationDetails>();
            locationDetailsList.add(locationDetail);

            when(locationDataManager.getLocationsByNameCountryAndType(locationName,country,locationType))
                    .thenReturn(locationList);
            when(locationDataManager.getLocationDetailsByLocId(location.getLocid(),0,1))
                    .thenReturn(locationDetailsList);
            when(locationDataManager.getLocationByID(location.getLocid()))
                    .thenReturn(location);
            when(locationDataManager.getCountryById(location.getCntryid()))
                    .thenReturn(country);
            when(locationDataManager.getUserDefinedFieldByID(location.getLtype()))
                    .thenReturn(null);

        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
        }
    }
}
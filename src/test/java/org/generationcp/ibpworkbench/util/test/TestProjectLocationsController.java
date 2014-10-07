package org.generationcp.ibpworkbench.util.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsPresenter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestProjectLocationsController {

	private ProgramLocationsPresenter controller;
	private LocationDataManager locationDataManager;
	
    @Before
    public void setUp() {
    	locationDataManager = Mockito.mock(LocationDataManager.class);
    	ManagerFactoryProvider managerFactoryProvider = Mockito.mock(ManagerFactoryProvider.class);
    	Project project = Mockito.mock(Project.class);
    	WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
    	controller = new ProgramLocationsPresenter(project, workbenchDataManager, managerFactoryProvider,locationDataManager);
    }
    
    @Test
    public void testGetFilteredResults() {
    	//getFilteredResults with default parameters (only the location type is set)
    	Integer locationType = 1;
    	setupGetFilteredResults(null, locationType, null);
    	
    	String notNullTest = "Get filtered list by location type should return a result";
		String locationTypeTest = "Location type must be equal to " + locationType;
			
		Collection<LocationViewModel> result = null;
		try {
			result = controller.getFilteredResults(null, locationType, null);
			Assert.assertNotNull(notNullTest,result);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
		
		for (LocationViewModel locationViewModel : result) {
			Assert.assertEquals(locationTypeTest, locationType, locationViewModel.getLtype());
		}
	}
	
    public void testGetFilteredResultsByCountryIdAndLocationType() {
		Integer countryId = 1;
		Integer locationType = 1;
    	setupGetFilteredResults(countryId, locationType, null);
    	
    	String notNullTest = "Get filtered list by country and location type should return a result";
		String locationTypeTest = "Location type must be equal to " + locationType;
		String countryIdTest = "Country id must be equal to "+countryId;
		
		Collection<LocationViewModel> result = null;
		try {
			result = controller.getFilteredResults(countryId, locationType, null);
			Assert.assertNotNull(notNullTest,result);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
		
		for (LocationViewModel locationViewModel : result) {
			Assert.assertEquals(countryIdTest, countryId, locationViewModel.getCntryid());
			Assert.assertEquals(locationTypeTest, locationType, locationViewModel.getLtype());
		}
	}	
	
	@Test
	public void testGetFilteredResultsByLocationName() {
		String locationName = "TEST LOCATION";
		setupGetFilteredResults(null, null, locationName);
		
		String notNullTest = "Get filtered list by location name should return a result";
		String locationNameTest = "Location name must be equal to " + locationName;
		
		Collection<LocationViewModel> result = null;
		try {
			result = controller.getFilteredResults(null, null, locationName);
			Assert.assertNotNull(notNullTest,result);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
		
		for (LocationViewModel locationViewModel : result) {
			Assert.assertEquals(locationNameTest, locationName, locationViewModel.getLocationName());
		}
		
	}
	
	private void setupGetFilteredResults(Integer countryId, Integer locationType, String locationName) {
		try {
			Country country = null;
			if(countryId!=null) {
				country = Mockito.mock(Country.class);
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
			
			Mockito.when(locationDataManager.getLocationsByNameCountryAndType(locationName,country,locationType))
			   	   .thenReturn(locationList);
			Mockito.when(locationDataManager.getLocationDetailsByLocId(location.getLocid(),0,1))
			   	   .thenReturn(locationDetailsList);
			Mockito.when(locationDataManager.getLocationByID(location.getLocid()))
			       .thenReturn(location);
			Mockito.when(locationDataManager.getCountryById(location.getCntryid()))
				   .thenReturn(country);
			Mockito.when(locationDataManager.getUserDefinedFieldByID(location.getLtype()))
	        	   .thenReturn(null);
			
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}		
	}
}

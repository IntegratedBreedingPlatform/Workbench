package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.IWorkbenchSession;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ProgramLocationsPresenter implements InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProgramLocationsPresenter.class);
	
	@Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    //@Autowired
    //private SessionProvider sessionProvider;
    private GermplasmDataManager gdm;
	private Project project;

    public ProgramLocationsPresenter(Project project) {
		this.project = project;
	}
	
	/* THIS IS ONLY USED FOR JUNIT TESTS */
	public ProgramLocationsPresenter(Project project, WorkbenchDataManager workbenchDataManager, ManagerFactoryProvider managerFactoryProvider) {
		this.project = project;

		this.workbenchDataManager = workbenchDataManager;
		this.managerFactoryProvider = managerFactoryProvider;
		
		this.gdm = managerFactoryProvider.getManagerFactoryForProject(project).getGermplasmDataManager();
	}
	
	/**
	 * generates random results
	 * @return
	 */
	public static List<LocationTableViewModel> getRandomResults(Integer countryID,Integer locationType,String locationName) {
		// for now lets random generate the results
		
		return LocationTableViewModel.generateRandomData(2);
	}
	
	public List<LocationTableViewModel> getFilteredResults(Integer countryId,Integer locationType,String locationName) throws MiddlewareQueryException {
		List<Location> locationList = null;
        List<LocationTableViewModel> results = new ArrayList<LocationTableViewModel>();
		locationName = (locationName != null) ? locationName : "";
		
		Country country = gdm.getCountryById(countryId);
        locationList =gdm.getLocationsByNameCountryAndType(locationName,country,locationType);
        
        Collections.sort(locationList, Location.LocationNameComparator);

        for (Location location : locationList) {
        	results.add(this.getLocationDetailsByLocId(location.getLocid()));
        }
        
		return results;
	}

    // The ff is a BAD BAD CODE, necessary but BAD!!! >_<
	public void onAttachInitialize(IWorkbenchSession appSession) {
        try {
            //Get all Local locations
            List<Location> allLocalLocations = null;

            allLocalLocations = gdm.getAllLocalLocations(0,Integer.MAX_VALUE);

            // Initialize IBPWorkbench.app session
            for (Location loc : allLocalLocations) {
                if (loc.getLocid()<0 && !appSession.getSessionData().getUniqueLocations().contains(loc.getLname())) {
                    LocationModel locModel = new LocationModel();
                    locModel.setCntryid(loc.getCntryid());
                    locModel.setLocationAbbreviation(loc.getLabbr());
                    locModel.setLocationId(loc.getLocid());
                    locModel.setLocationName(loc.getLname());
                    locModel.setLtype(loc.getLtype());

                    appSession.getSessionData().getUniqueLocations().add(locModel.getLocationName());

                    //Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size() + 1;
                    //nextKey = nextKey * -1;
                    //app.getSessionData().getProjectLocationData().put(nextKey, locModel);
                    appSession.getSessionData().getProjectLocationData().put(locModel.getLocationId(), locModel);
                }
            }

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }
    }
	
	public List<LocationTableViewModel> getSavedProgramLocations() throws MiddlewareQueryException  {
		List<LocationTableViewModel> result = new ArrayList<LocationTableViewModel>();
		List<Long> locationIds = workbenchDataManager.getLocationIdsByProjectId(project.getProjectId(), 0, Integer.MAX_VALUE);
		
		for (Long locationId : locationIds) {
			result.add(this.getLocationDetailsByLocId(locationId.intValue()));
		}

		return result;
	}
	
	public LocationTableViewModel getLocationDetailsByLocId(int locationId) throws MiddlewareQueryException {
		try {
			
			List<LocationDetails> locList = gdm.getLocationDetailsByLocId(locationId,0,1);
			
			
			if (locationId < 0) {
				Location location = gdm.getLocationByID(locationId);
				
				return convertFrom(location);
			}
			
			
			return convertFrom(locList.get(0));			
		} catch (IndexOutOfBoundsException e) {
			LOG.error("LocationID" + locationId);
			
			return null;
		}
		

	}
	
	private Location initiliazeLocation(Location l) {
        Location location = new Location();
        location.setLocid(l.getLocid());
        location.setLabbr(l.getLabbr());
        location.setLname(l.getLname());
        location.setLrplce(0);

        Integer ltype = (l.getLtype() != null) ? l.getLtype() : 0;
        Integer cntryid = (l.getCntryid() != null) ? l.getCntryid() : 0;

        location.setLtype(ltype);
        location.setCntryid(cntryid);

        location.setNllp(0);
        location.setSnl1id(0);
        location.setSnl2id(0);
        location.setSnl3id(0);
        return location;
    }
		
public boolean saveProgramLocation(List<Integer> selectedLocationIds) throws MiddlewareQueryException {
    	
        // Delete existing project locations in the database
        List<ProjectLocationMap> projectLocationMapList = workbenchDataManager.getProjectLocationMapByProjectId(
                project.getProjectId(), 0,Integer.MAX_VALUE);
        
        for (ProjectLocationMap projectLocationMap : projectLocationMapList){
            workbenchDataManager.deleteProjectLocationMap(projectLocationMap);
        }
        projectLocationMapList.removeAll(projectLocationMapList);
        
        /*
         * add selected location to local db location table if it does not yet exist
         * add location in workbench_project_loc_map in workbench db
         */
        for (Integer l : selectedLocationIds) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
            projectLocationMap.setLocationId(l.longValue());
            projectLocationMap.setProject(getProject());
            projectLocationMapList.add(projectLocationMap);
        }


        // Add the new set of project locations
        workbenchDataManager.addProjectLocationMap(projectLocationMapList);
              
        return true;
    }
	
	private LocationTableViewModel convertFrom(LocationDetails location) {
		LocationTableViewModel viewModel = new LocationTableViewModel();
		viewModel.setLocationId(location.getLocid());
		viewModel.setLocationName(location.getLocation_name());
		viewModel.setLocationAbbreviation(location.getLocation_abbreviation());
		viewModel.setCntryFullName(location.getCountry_full_name());
		viewModel.setLtype(location.getLocation_type());
		
		return viewModel;
	}
	
	private LocationTableViewModel convertFrom(Location location) throws MiddlewareQueryException {
		LocationTableViewModel viewModel = new LocationTableViewModel();
		viewModel.setLocationId(location.getLocid());
		viewModel.setLocationName(location.getLname());
		viewModel.setLocationAbbreviation(location.getLabbr());
		
		
		
		Country country = gdm.getCountryById(location.getCntryid());
		UserDefinedField udf = gdm.getUserDefinedFieldByID(location.getLtype());
	
		if (country != null)
			viewModel.setCntryFullName(country.getIsofull());
		if (udf!=null) 	
			viewModel.setLtype(udf.getFname());
		
		
		return viewModel;
	}
	
	
    public List<Country> getCountryList() throws MiddlewareQueryException {
        List<Country> countryList = gdm.getAllCountry();
        Collections.sort(countryList,new Comparator<Country>() {
			@Override
			public int compare(Country o1, Country o2) {
				
					return o1.getIsoabbr().compareTo(o2.getIsoabbr());
				
			}
		});
        /*for (Country c : countryList) {
            selectLocationCountry.addItem(String.valueOf(c.getCntryid()));
            selectLocationCountry.setItemCaption(String.valueOf(c.getCntryid()), c.getIsoabbr());
        }*/

        return countryList;
    }
    
    public List<UserDefinedField> getLocationTypeList() throws MiddlewareQueryException {

       return this.gdm.getUserDefinedFieldByFieldTableNameAndType(
                "LOCATION", "LTYPE");
        
        /*for (UserDefinedField u : userDefineField) {
            selectLocationType.addItem(String.valueOf(u.getFldno()));
            selectLocationType.setItemCaption(String.valueOf(u.getFldno()), u.getFname());
        }*/
    }

	public Project getProject() {
		// TODO Auto-generated method stub
		return project;
	}

    public GermplasmDataManager getGermplasmDataManager(){
    	return this.gdm;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.gdm = managerFactoryProvider.getManagerFactoryForProject(project).getGermplasmDataManager();
    }
}
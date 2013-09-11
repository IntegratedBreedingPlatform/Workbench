package org.generationcp.ibpworkbench.projectlocations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectLocationMap;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ProjectLocationsController implements InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProjectLocationsController.class);
	
	@Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    
	private GermplasmDataManager gdm;
	private Project project;

	private List<Location> newLocations;

	private CropType cropType;

	private Role role;

	public ProjectLocationsController(Project project, Role role) {
		this.project = project;
		this.role = role;
		
		newLocations = new ArrayList<Location>();
	}
	
	/**
	 * generates random results
	 * @param countryID
	 * @param locationID
	 * @param locationName
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

	@Override
	public void afterPropertiesSet() throws Exception {
		this.gdm = managerFactoryProvider.getManagerFactoryForProject(project).getGermplasmDataManager();	

    	//Get all Local locations
    	List<Location> allLocalLocations = gdm.getAllLocalLocations(0,Integer.MAX_VALUE);
    	
    	// Initialize IBPWorkbench.app session
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        for (Location loc : allLocalLocations) {
        	if (loc.getLocid()<0 && !app.getSessionData().getUniqueLocations().contains(loc.getLname())) {
        		LocationModel locModel = new LocationModel();
            	locModel.setCntryid(loc.getCntryid());
            	locModel.setLocationAbbreviation(loc.getLabbr());
            	locModel.setLocationId(loc.getLocid());
            	locModel.setLocationName(loc.getLname());
            	locModel.setLtype(loc.getLtype());
            	
                app.getSessionData().getUniqueLocations().add(locModel.getLocationName());

                //Integer nextKey = app.getSessionData().getProjectLocationData().keySet().size() + 1;
                //nextKey = nextKey * -1;
                //app.getSessionData().getProjectLocationData().put(nextKey, locModel);
                app.getSessionData().getProjectLocationData().put(locModel.getLocationId(), locModel);
            }
        }
	}
	
	public List<LocationTableViewModel> getSavedProjectLocations() throws MiddlewareQueryException  {
		List<LocationTableViewModel> result = new ArrayList<LocationTableViewModel>();
		List<Long> locationIds = workbenchDataManager.getLocationIdsByProjectId(project.getProjectId(), 0, Integer.MAX_VALUE);
		
		for (Long locationId : locationIds) {
			result.add(this.getLocationDetailsByLocId(locationId.intValue()));
		}
		
		return result;
	}
	
	private LocationTableViewModel getLocationDetailsByLocId(int locationId) throws MiddlewareQueryException {
		try {
			List<LocationDetails> locList = gdm.getLocationDetailsByLocId(locationId,0,1);
			
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
	
	public boolean saveProjectLocation(Collection<Location> availableLocations, Set<Location> selectedLocations, Project projectSaved) throws MiddlewareQueryException {
    	//DEBUG LOGS
    	// check newLocations property
    	//LOG.debug("newLocations count: " + newLocations.size());
    	
        // Delete existing project locations in the database
        List<ProjectLocationMap> projectLocationMapList = workbenchDataManager.getProjectLocationMapByProjectId(
                project.getProjectId(), 0,Integer.MAX_VALUE);
        
        for (ProjectLocationMap projectLocationMap : projectLocationMapList){
            workbenchDataManager.deleteProjectLocationMap(projectLocationMap);
        }
        
        
        //add available location to local db location table if it does not yet exist
        for (Location l : this.newLocations) {
            Location location = initiliazeLocation(l);
            gdm.addLocation(location);
        }
        
        projectLocationMapList = new ArrayList<ProjectLocationMap>();
        
        /*
         * add selected location to local db location table if it does not yet exist
         * add location in workbench_project_loc_map in workbench db
         */
        for (Location l : selectedLocations) {
            ProjectLocationMap projectLocationMap = new ProjectLocationMap();
        
            projectLocationMap.setLocationId(l.getLocid().longValue());
            projectLocationMap.setProject(projectSaved);
            projectLocationMapList.add(projectLocationMap);
        }


        // Add the new set of project locations
        workbenchDataManager.addProjectLocationMap(projectLocationMapList);
        
        // MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.LOCATION_SUCCESSFULLY_CONFIGURED));
        //            //"Success", "Project location(s) successfully configured");
        
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
	
	
	public void addNewLocations(Location location) {
		this.newLocations.add(location);
		
	}
	
    public List<Country> getCountryList() throws MiddlewareQueryException {
        cropType = project.getCropType();
        
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

	public Role getRole() {
		// TODO Auto-generated method stub
		return role;
	}
    
    
    public GermplasmDataManager getGermplasmDataManager(){
    	return this.gdm;
    }
    
}
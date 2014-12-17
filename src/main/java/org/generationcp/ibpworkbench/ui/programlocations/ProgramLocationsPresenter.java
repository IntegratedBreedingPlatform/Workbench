package org.generationcp.ibpworkbench.ui.programlocations;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.LocationDetails;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.*;

@Configurable
public class ProgramLocationsPresenter implements InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(ProgramLocationsPresenter.class);
    private boolean isCropOnly;
    private CropType cropType;
    private ProgramLocationsView view;
    
    private static GermplasmDataManager gdm;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;

    private LocationDataManager locationDataManager;

	private Project project;

    public ProgramLocationsPresenter(ProgramLocationsView view,Project project) {
		this.view = view;
        this.project = project;
	}

    public ProgramLocationsPresenter(ProgramLocationsView view,CropType cropType) {
        this.view = view;
        this.cropType = cropType;
        this.isCropOnly = true;
    }


	/* THIS IS ONLY USED FOR JUNIT TESTS */
	public ProgramLocationsPresenter(Project project, WorkbenchDataManager workbenchDataManager, ManagerFactoryProvider managerFactoryProvider, LocationDataManager locationDataManager) {
		this.project = project;
		this.workbenchDataManager = workbenchDataManager;
		this.locationDataManager = locationDataManager;
	}
	
	/**
	 * generates random results
	 * @return
	 */
	public static List<LocationViewModel> getRandomResults(Integer countryID,Integer locationType,String locationName) {
		// for now lets random generate the results
		
		return LocationViewModel.generateRandomData(2);
	}
	
	public Collection<LocationViewModel> getFilteredResults(Integer countryId,Integer locationType,String locationName) throws MiddlewareQueryException {
		List<Location> locationList = null;

        Map<Integer,LocationViewModel> resultsMap = new LinkedHashMap<Integer, LocationViewModel>();
        Country country = locationDataManager.getCountryById(countryId);
        locationList = locationDataManager.getLocationsByNameCountryAndType(
        		(locationName != null) ? locationName : "",country,locationType);
        
        Collections.sort(locationList, Location.LocationNameComparator);

        for (Location location : locationList) {
            final LocationViewModel locationVModel = this.getLocationDetailsByLocId(location.getLocid());

            if (locationVModel != null) {
                resultsMap.put(location.getLocid(), locationVModel);
            }
        }

        return resultsMap.values();
	}

    public Collection<LocationViewModel> getFilteredResults(Integer countryId,Integer locationType,String locationName,Collection<LocationViewModel> existingItems) throws MiddlewareQueryException {
        List<Location> locationList = null;

        Map<Integer,LocationViewModel> resultsMap = new LinkedHashMap<Integer, LocationViewModel>();
        
        Country country = locationDataManager.getCountryById(countryId);
        locationList = locationDataManager.getLocationsByNameCountryAndType(
        		(locationName != null) ? locationName : "",country,locationType);

        Collections.sort(locationList, Location.LocationNameComparator);

        for (Location location : locationList) {
            final LocationViewModel locationVModel = this.getLocationDetailsByLocId(location.getLocid());
            if (locationVModel != null) {
                resultsMap.put(location.getLocid(), locationVModel);
            }
        }

 
        return resultsMap.values();
    }

    // The ff is a BAD BAD CODE, necessary but BAD!!! >_<
	private void onAttachInitialize() {
        try {
            LOG.debug(">BAD Routine start!");

            for (Location loc : locationDataManager.getAllLocalLocations(0,Integer.MAX_VALUE)) {
                if (loc.getLocid()<0 && !sessionData.getProjectLocationData().containsKey(loc.getLocid())) {
                    LocationViewModel locModel = new LocationViewModel();
                    locModel.setCntryid(loc.getCntryid());
                    locModel.setLocationAbbreviation(loc.getLabbr());
                    locModel.setLocationId(loc.getLocid());
                    locModel.setLocationName(loc.getLname());
                    locModel.setLtype(loc.getLtype());
                    locModel.setLatitude(loc.getLatitude());
                    locModel.setLongitude(loc.getLongitude());
                    locModel.setAltitude(loc.getAltitude());
                    sessionData.getProjectLocationData().put(locModel.getLocationId(), locModel);
                }
            }

        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
        }
    }
	
	public List<LocationViewModel> getSavedProgramLocations() throws MiddlewareQueryException  {
        if (cropType != null) {
            return new ArrayList<LocationViewModel>();
        }

		List<LocationViewModel> result = new ArrayList<LocationViewModel>();
		List<ProgramFavorite> favorites = gdm.getProgramFavorites(FavoriteType.LOCATION);
		
		for (ProgramFavorite favorite : favorites) {
            LocationViewModel locationVModel = this.getLocationDetailsByLocId(favorite.getEntityId());

            if (locationVModel != null) {
			    result.add(locationVModel);
            }
		}

		return result;
	}
	
	//TODO Review this method logic...for merged DB scheme..
	public LocationViewModel getLocationDetailsByLocId(int locationId) throws MiddlewareQueryException {
		try {
			
			List<LocationDetails> locList = locationDataManager.getLocationDetailsByLocId(locationId,0,1);
			if (locationId < 0) {
				Location location = locationDataManager.getLocationByID(locationId);
				return convertFrom(location);
			}
			return convertFrom(locList.get(0));			
		} catch (IndexOutOfBoundsException e) {
			LOG.error("Cannot retrieve location info. [locationId=" + locationId +"]", e);
		} catch (NullPointerException e) {
            LOG.error("Location [locationId="+ locationId +"]  not found", e);
        }
        return null;
	}
	
    public boolean saveProgramLocation(Collection<LocationViewModel> selectedLocations) throws MiddlewareQueryException {
        return saveProgramLocation(selectedLocations,this.project,this.workbenchDataManager);
    }

    public static boolean saveProgramLocation(Collection<LocationViewModel> selectedLocations,Project project,WorkbenchDataManager workbenchDataManager) throws MiddlewareQueryException {

        // Delete existing project locations in the database
    	List<ProgramFavorite> favorites = gdm.getProgramFavorites(FavoriteType.LOCATION);
    	gdm.deleteProgramFavorites(favorites);

         /*
        * add selected location to local db location table if it does not yet exist
        * add location in workbench_project_loc_map in workbench db
        */
    	List<ProgramFavorite> list = new ArrayList<ProgramFavorite>();
        for (LocationViewModel l : selectedLocations) {
        	ProgramFavorite favorite = new ProgramFavorite();
        	favorite.setEntityId(l.getLocationId());
        	favorite.setEntityType(FavoriteType.LOCATION.getName());
        	list.add(favorite);
        }


        // Add the new set of project locations
        gdm.saveProgramFavorites(list);

        return true;
    }

    private LocationViewModel convertFrom(LocationDetails location) {
		LocationViewModel viewModel = new LocationViewModel();
		viewModel.setLocationId(location.getLocid());
		viewModel.setLocationName(location.getLocation_name());
		viewModel.setLocationAbbreviation(location.getLocation_abbreviation());
		viewModel.setCntryFullName(location.getCountry_full_name());
		viewModel.setLtypeStr(location.getLocation_type());
		viewModel.setCntryid(location.getCntryid());
        viewModel.setLtype(location.getLtype());
        viewModel.setLatitude(location.getLatitude());
        viewModel.setLongitude(location.getLongitude());
        viewModel.setAltitude(location.getAltitude());
		return viewModel;
	}
	
	private LocationViewModel convertFrom(Location location) throws MiddlewareQueryException {
		LocationViewModel viewModel = new LocationViewModel();
		viewModel.setLocationId(location.getLocid());
		viewModel.setLocationName(location.getLname());
		viewModel.setLocationAbbreviation(location.getLabbr());
		viewModel.setLtype(location.getLtype());
        viewModel.setCntryid(location.getCntryid());
        viewModel.setProvinceId(location.getSnl3id());
        viewModel.setLatitude(location.getLatitude());
        viewModel.setLongitude(location.getLongitude());
        viewModel.setAltitude(location.getAltitude());

		Country country = locationDataManager.getCountryById(location.getCntryid());
		UserDefinedField udf = locationDataManager.getUserDefinedFieldByID(location.getLtype());
	
		if (country != null) {
			viewModel.setCntryFullName(country.getIsofull());
		}
		if (udf!=null) {
            viewModel.setLtypeStr(udf.getFname());
            viewModel.setLtype(udf.getLfldno());
        }

		
		
		return viewModel;
	}
	
	
    public List<Country> getCountryList() throws MiddlewareQueryException {
        List<Country> countryList = locationDataManager.getAllCountry();
        Collections.sort(countryList,new Comparator<Country>() {
			@Override
			public int compare(Country o1, Country o2) {
				
					return o1.getIsoabbr().compareTo(o2.getIsoabbr());
				
			}
		});

        return countryList;
    }

    public List<Location> getAllProvinces() throws MiddlewareQueryException {
        return locationDataManager.getAllProvinces();
    }

    public List<Location> getAllProvincesByCountry(Integer countryId) throws MiddlewareQueryException {
        return locationDataManager.getAllProvincesByCountry(countryId);
    }
    
    public List<UserDefinedField> getLocationTypeList() throws MiddlewareQueryException {

       return this.locationDataManager.getUserDefinedFieldByFieldTableNameAndType(
                "LOCATION", "LTYPE");
    }

	public Project getProject() {
		return project;
	}

    @Override
    public void afterPropertiesSet() throws Exception {
    	
    	gdm = managerFactoryProvider.getManagerFactoryForProject(project).getGermplasmDataManager();
    	
        if (this.cropType != null) {
            this.locationDataManager = managerFactoryProvider.getManagerFactoryForCropType(cropType).getLocationDataManager();
        } else {
            this.locationDataManager = managerFactoryProvider.getManagerFactoryForProject(project).getLocationDataManager();
            this.onAttachInitialize();
        }


    }

    public List<UserDefinedField> getUDFByLocationAndLType() throws MiddlewareQueryException {
        return locationDataManager.getUserDefinedFieldByFieldTableNameAndType("LOCATION","LTYPE");
    }

    public void addLocation(Location loc) throws MiddlewareQueryException {
        // if crop only AKA locationView instantiated from Add new program page, just add the row to the view table.

        if(!isCropOnly) {
            locationDataManager.addLocation(loc);

            final LocationViewModel locationVModel = this.getLocationDetailsByLocId(loc.getLocid());
            view.addRow(locationVModel,true,0);

        } else {
            view.addRow(convertFrom(loc), true, 0);
        }
    }

    public List<Location> getExistingLocations(String locationName) throws MiddlewareQueryException {
        return locationDataManager.getLocationsByName(locationName, Operation.EQUAL);
    }

    public Location convertLocationViewToLocation(LocationViewModel lvm) {
        Location location = new Location();
        location.setLrplce(0);

        location.setLocid(lvm.getLocationId());
        location.setLname(lvm.getLocationName());
        location.setLabbr(lvm.getLocationAbbreviation());
        location.setLtype(lvm.getLtype());
        location.setCntryid(lvm.getCntryid());

        if (location.getCntryid() == null) {
            location.setCntryid(0);
        }
        
        location.setLatitude(lvm.getLatitude());
        location.setLongitude(lvm.getLongitude());
        location.setAltitude(lvm.getAltitude());

        // defaults
        location.setNllp(0);
        location.setSnl1id(0);
        location.setSnl2id(0);
        location.setSnl3id(lvm.getProvinceId());
        if (location.getSnl3id() == null) {
            location.setSnl3id(0);
        }

        return location;
    }

    public List<Location> convertTo(Collection<LocationViewModel> locationViewModels) {
        List<Location> result = new ArrayList<Location>();

        for (LocationViewModel locationViewModel : locationViewModels) {
            result.add(convertLocationViewToLocation(locationViewModel));
        }

        return result;
    }
}
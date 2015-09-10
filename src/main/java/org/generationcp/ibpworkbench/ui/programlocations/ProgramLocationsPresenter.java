
package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

@Configurable
public class ProgramLocationsPresenter implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ProgramLocationsPresenter.class);
	private boolean isCropOnly;
	private CropType cropType;

	private ProgramLocationsView view;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private LocationDataManager locationDataManager;

	private Project project;

	public ProgramLocationsPresenter(ProgramLocationsView view, Project project) {
		this.view = view;
		this.project = project;
	}

	public ProgramLocationsPresenter(ProgramLocationsView view, CropType cropType) {
		this.view = view;
		this.cropType = cropType;
		this.isCropOnly = true;
	}

	/* THIS IS ONLY USED FOR JUNIT TESTS */
	public ProgramLocationsPresenter(Project project, GermplasmDataManager germplasmDataManager, WorkbenchDataManager workbenchDataManager, LocationDataManager locationDataManager) {
		this.project = project;
		this.workbenchDataManager = workbenchDataManager;
		this.germplasmDataManager = germplasmDataManager;
		this.locationDataManager = locationDataManager;
	}

	public Collection<LocationViewModel> getFilteredResults(Integer countryId, Integer locationType, String locationName)
			{
		List<Location> locationList = null;

		Map<Integer, LocationViewModel> resultsMap = new LinkedHashMap<Integer, LocationViewModel>();
		Country country = this.locationDataManager.getCountryById(countryId);
		locationList =
				this.locationDataManager.getLocationsByNameCountryAndType(locationName != null ? locationName : "", country, locationType);

		Collections.sort(locationList, Location.LocationNameComparator);

		for (Location location : locationList) {
			final LocationViewModel locationVModel = this.getLocationDetailsByLocId(location.getLocid());

			if (locationVModel != null && (location.getUniqueID() == null || location.getUniqueID().equals(this.project.getUniqueID()))) {
				resultsMap.put(location.getLocid(), locationVModel);
			}
		}

		return resultsMap.values();
	}

	public Collection<LocationViewModel> getFilteredResults(Integer countryId, Integer locationType, String locationName,
			Collection<LocationViewModel> existingItems) {
		List<Location> locationList = null;

		Map<Integer, LocationViewModel> resultsMap = new LinkedHashMap<Integer, LocationViewModel>();

		Country country = this.locationDataManager.getCountryById(countryId);
		locationList =
				this.locationDataManager.getLocationsByNameCountryAndType(locationName != null ? locationName : "", country, locationType);

		Collections.sort(locationList, Location.LocationNameComparator);

		for (Location location : locationList) {
			final LocationViewModel locationVModel = this.getLocationDetailsByLocId(location.getLocid());
			if (locationVModel != null && (location.getUniqueID() == null || location.getUniqueID().equals(this.project.getUniqueID()))) {
				resultsMap.put(location.getLocid(), locationVModel);
			}
		}

		return resultsMap.values();
	}

	public List<LocationViewModel> getSavedProgramLocations() {
		if (this.cropType != null) {
			return new ArrayList<LocationViewModel>();
		}

		List<LocationViewModel> result = new ArrayList<LocationViewModel>();
		List<ProgramFavorite> favorites = this.germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, this.project.getUniqueID());

		for (ProgramFavorite favorite : favorites) {
			LocationViewModel locationVModel = this.getLocationDetailsByLocId(favorite.getEntityId());

			if (locationVModel != null) {
				result.add(locationVModel);
			}
		}

		return result;
	}

	public LocationViewModel getLocationDetailsByLocId(int locationId) {
		try {
			List<LocationDetails> locList = this.locationDataManager.getLocationDetailsByLocId(locationId, 0, 1);
			return this.convertFrom(locList.get(0));
		} catch (IndexOutOfBoundsException e) {
			ProgramLocationsPresenter.LOG.error("Cannot retrieve location info. [locationId=" + locationId + "]", e);
		} catch (NullPointerException e) {
			ProgramLocationsPresenter.LOG.error("Location [locationId=" + locationId + "]  not found", e);
		}
		return null;
	}

	public boolean saveFavouriteLocations(Collection<LocationViewModel> selectedLocations) {
		return this.saveFavouriteLocations(selectedLocations, this.project, this.workbenchDataManager);
	}

	public boolean saveFavouriteLocations(Collection<LocationViewModel> selectedLocations, Project project,
			WorkbenchDataManager workbenchDataManager) {

		// Delete existing project locations in the database
		List<ProgramFavorite> favorites = this.germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, project.getUniqueID());
		this.germplasmDataManager.deleteProgramFavorites(favorites);

		/*
		 * add selected location to local db location table if it does not yet exist add location in workbench_project_loc_map in workbench
		 * db
		 */
		List<ProgramFavorite> list = new ArrayList<ProgramFavorite>();
		for (LocationViewModel l : selectedLocations) {
			ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(l.getLocationId());
			favorite.setEntityType(FavoriteType.LOCATION.getName());
			favorite.setUniqueID(project.getUniqueID());
			list.add(favorite);
		}

		// Add the new set of project locations
		this.germplasmDataManager.saveProgramFavorites(list);

		return true;
	}

	private LocationViewModel convertFrom(LocationDetails location) {
		LocationViewModel viewModel = new LocationViewModel();
		viewModel.setLocationId(location.getLocid());
		viewModel.setLocationName(location.getLocationName());
		viewModel.setLocationAbbreviation(location.getLocationAbbreviation());
		viewModel.setCntryFullName(location.getCountryFullName());
		viewModel.setLtypeStr(location.getLocationType());
		viewModel.setCntryid(location.getCntryid());
		viewModel.setLtype(location.getLtype());
		viewModel.setLatitude(location.getLatitude());
		viewModel.setLongitude(location.getLongitude());
		viewModel.setAltitude(location.getAltitude());
		return viewModel;
	}

	private LocationViewModel convertFrom(Location location) {
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

		Country country = this.locationDataManager.getCountryById(location.getCntryid());
		UserDefinedField udf = this.locationDataManager.getUserDefinedFieldByID(location.getLtype());

		if (country != null) {
			viewModel.setCntryFullName(country.getIsofull());
		}
		if (udf != null) {
			viewModel.setLtypeStr(udf.getFname());
			viewModel.setLtype(udf.getLfldno());
		}

		return viewModel;
	}

	public List<Country> getCountryList() {
		List<Country> countryList = this.locationDataManager.getAllCountry();
		Collections.sort(countryList, new Comparator<Country>() {

			@Override
			public int compare(Country o1, Country o2) {

				return o1.getIsoabbr().compareTo(o2.getIsoabbr());

			}
		});

		return countryList;
	}

	public List<Location> getAllProvinces() {
		return this.locationDataManager.getAllProvinces();
	}

	public List<Location> getAllProvincesByCountry(Integer countryId) {
		return this.locationDataManager.getAllProvincesByCountry(countryId);
	}

	public List<UserDefinedField> getLocationTypeList() {

		return this.locationDataManager.getUserDefinedFieldByFieldTableNameAndType("LOCATION", "LTYPE");
	}

	public Project getProject() {
		return this.project;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// method inherited from interface, does nothing
	}

	public List<UserDefinedField> getUDFByLocationAndLType() {
		return this.locationDataManager.getUserDefinedFieldByFieldTableNameAndType("LOCATION", "LTYPE");
	}

	public void addLocation(Location loc) {
		// if crop only AKA locationView instantiated from Add new program page, just add the row to the view table.

		if (!this.isCropOnly) {
			loc.setUniqueID(this.project.getUniqueID());
			this.locationDataManager.addLocation(loc);

			final LocationViewModel locationVModel = this.getLocationDetailsByLocId(loc.getLocid());
			this.view.addRow(locationVModel, true, 0);

		} else {
			this.view.addRow(this.convertFrom(loc), true, 0);
		}
	}

	public List<Location> getExistingLocations(String locationName) {
		return this.locationDataManager.getLocationsByName(locationName, Operation.EQUAL, this.project.getUniqueID());
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
			result.add(this.convertLocationViewToLocation(locationViewModel));
		}

		return result;
	}

	public void setCropType(CropType cropType) {
		this.cropType = cropType;
	}
}

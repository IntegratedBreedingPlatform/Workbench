
package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
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
	public ProgramLocationsPresenter(Project project, GermplasmDataManager germplasmDataManager, LocationDataManager locationDataManager) {
		this.project = project;
		this.germplasmDataManager = germplasmDataManager;
		this.locationDataManager = locationDataManager;
	}

	public Collection<LocationViewModel> getFilteredResults(final Integer countryId, final Integer locationType, final String locationName) {

		final List<LocationDetails> locationDetails =
				this.locationDataManager.getFilteredLocations(countryId, locationType, locationName, this.project.getUniqueID());

		return createLocationViewModelList(locationDetails);
	}

	protected Collection<LocationViewModel> createLocationViewModelList(final List<LocationDetails> locationDetails) {
		final Collection<LocationViewModel> result = new ArrayList<LocationViewModel>();

		for (final Iterator<LocationDetails> iterator = locationDetails.iterator(); iterator.hasNext();) {
			result.add(convertFrom(iterator.next()));
		}
		return result;
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
		return this.saveFavouriteLocations(selectedLocations, this.project);
	}

	public boolean saveFavouriteLocations(Collection<LocationViewModel> selectedLocations, Project project) {

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

	protected LocationViewModel convertFrom(LocationDetails locationDetails) {
		LocationViewModel viewModel = new LocationViewModel();
		viewModel.setLocationId(locationDetails.getLocid());
		viewModel.setLocationName(locationDetails.getLocationName());
		viewModel.setLocationAbbreviation(locationDetails.getLocationAbbreviation());
		viewModel.setCntryFullName(locationDetails.getCountryFullName());
		viewModel.setLtypeStr(locationDetails.getLocationType());
		viewModel.setCntryid(locationDetails.getCntryid());
		viewModel.setLtype(locationDetails.getLtype());
		viewModel.setLatitude(locationDetails.getLatitude());
		viewModel.setLongitude(locationDetails.getLongitude());
		viewModel.setAltitude(locationDetails.getAltitude());
		viewModel.setProgramUUID(locationDetails.getProgramUUID());
		return viewModel;
	}

	protected LocationViewModel convertFrom(Location location) {
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
		viewModel.setProgramUUID(location.getUniqueID());

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


	/**
	 * afterPropertiesSet() is called after Aspect4J weaves spring objects when this class is instantiated since this class is
	 * a @configurable that implements InitializingBean. Since we do not have any need for additional initialization after the weaving, this
	 * method remains unimplemented.
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// No values are required to be initialized for this layout
	}

	public List<UserDefinedField> getUDFByLocationAndLType() {
		return this.locationDataManager.getUserDefinedFieldByFieldTableNameAndType("LOCATION", "LTYPE");
	}

	public void addLocation(Location loc) {
		// if crop only AKA locationView instantiated from Add new program page, just add the row to the view table.

		if (!this.isCropOnly) {

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

	public Location convertLocationViewToLocation(LocationViewModel locationViewModel) {
		Location location = new Location();

		location.setLrplce(0);

		location.setLocid(locationViewModel.getLocationId());
		location.setLname(locationViewModel.getLocationName());
		location.setLabbr(locationViewModel.getLocationAbbreviation());
		location.setLtype(locationViewModel.getLtype());
		location.setCntryid(locationViewModel.getCntryid());

		if (location.getCntryid() == null) {
			location.setCntryid(0);
		}

		location.setLatitude(locationViewModel.getLatitude());
		location.setLongitude(locationViewModel.getLongitude());
		location.setAltitude(locationViewModel.getAltitude());

		// defaults
		location.setNllp(0);
		location.setSnl1id(0);
		location.setSnl2id(0);
		location.setSnl3id(locationViewModel.getProvinceId());

		if (location.getSnl3id() == null) {
			location.setSnl3id(0);
		}

		if (!locationViewModel.getCropAccessible()) {
			location.setUniqueID(this.project.getUniqueID());
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

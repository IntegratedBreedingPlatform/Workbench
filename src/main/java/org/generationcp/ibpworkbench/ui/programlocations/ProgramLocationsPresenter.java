package org.generationcp.ibpworkbench.ui.programlocations;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

	public ProgramLocationsPresenter(final ProgramLocationsView view, final Project project) {
		this.view = view;
		this.project = project;
	}

	public ProgramLocationsPresenter(final ProgramLocationsView view, final CropType cropType) {
		this.view = view;
		this.cropType = cropType;
		this.isCropOnly = true;
	}

	/* THIS IS ONLY USED FOR JUNIT TESTS */
	public ProgramLocationsPresenter(final Project project, final GermplasmDataManager germplasmDataManager,
			final LocationDataManager locationDataManager) {
		this.project = project;
		this.germplasmDataManager = germplasmDataManager;
		this.locationDataManager = locationDataManager;
	}

	public Collection<LocationViewModel> getFilteredResults(final Integer countryId, final Integer locationType,
			final String locationName) {

		final List<LocationDetails> locationDetails =
				this.locationDataManager.getFilteredLocationsDetails(countryId, locationType, locationName);

		return createLocationViewModelList(locationDetails);
	}

	protected Collection<LocationViewModel> createLocationViewModelList(final List<LocationDetails> locationDetails) {
		final Collection<LocationViewModel> result = new ArrayList<>();

		for (final LocationDetails locationDetail : locationDetails) {
			result.add(convertFromLocationDetailsToLocationViewModel(locationDetail));
		}
		return result;
	}

	public List<LocationViewModel> getSavedProgramLocations() {
		if (this.cropType != null) {
			return new ArrayList<>();
		}

		final List<LocationViewModel> result = new ArrayList<>();
		final List<ProgramFavorite> favorites =
				this.germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, this.project.getUniqueID());

		for (final ProgramFavorite favorite : favorites) {
			final LocationViewModel locationVModel = this.getLocationDetailsByLocId(favorite.getEntityId());

			if (locationVModel != null) {
				result.add(locationVModel);
			}
		}

		return result;
	}

	public LocationViewModel getLocationDetailsByLocId(final int locationId) {
		try {
			final List<LocationDetails> locList = this.locationDataManager.getLocationDetailsByLocId(locationId, 0, 1);
			return this.convertFromLocationDetailsToLocationViewModel(locList.get(0));
		} catch (final IndexOutOfBoundsException e) {
			ProgramLocationsPresenter.LOG.error("Cannot retrieve location info. [locationId=" + locationId + "]", e);
		} catch (final NullPointerException e) {
			ProgramLocationsPresenter.LOG.error("Location [locationId=" + locationId + "]  not found", e);
		}
		return null;
	}

	public boolean saveFavouriteLocations(final Collection<LocationViewModel> selectedLocations) {
		return this.saveFavouriteLocations(selectedLocations, this.project);
	}

	public boolean saveFavouriteLocations(final Collection<LocationViewModel> selectedLocations, final Project project) {

		// Delete existing project locations in the database
		final List<ProgramFavorite> favorites = this.germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, project.getUniqueID());
		this.germplasmDataManager.deleteProgramFavorites(favorites);

		/*
		 * add selected location to local db location table if it does not yet exist add location in workbench_project_loc_map in workbench
		 * db
		 */
		final List<ProgramFavorite> list = new ArrayList<>();
		for (final LocationViewModel locationViewModel : selectedLocations) {
			final ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(locationViewModel.getLocationId());
			favorite.setEntityType(FavoriteType.LOCATION);
			favorite.setUniqueID(project.getUniqueID());
			list.add(favorite);
		}

		// Add the new set of project locations
		this.germplasmDataManager.saveProgramFavorites(list);

		return true;
	}

	protected LocationViewModel convertFromLocationDetailsToLocationViewModel(final LocationDetails locationDetails) {
		final LocationViewModel viewModel = new LocationViewModel();
		viewModel.setLocationId(locationDetails.getLocid());
		viewModel.setLocationName(locationDetails.getLocationName());
		viewModel.setLocationAbbreviation(locationDetails.getLocationAbbreviation());
		viewModel.setCntryFullName(locationDetails.getCountryFullName());
		viewModel.setLtypeStr(locationDetails.getLocationType());
		viewModel.setCntryid(locationDetails.getCntryid());
		viewModel.setCntryName(locationDetails.getCountryName());
		viewModel.setLtype(locationDetails.getLtype());
		viewModel.setLatitude(locationDetails.getLatitude());
		viewModel.setLongitude(locationDetails.getLongitude());
		viewModel.setAltitude(locationDetails.getAltitude());
		viewModel.setProvinceId(locationDetails.getProvinceId());
		viewModel.setProvinceName(locationDetails.getProvinceName());
		viewModel.setlDefault(locationDetails.getlDefault());
		return viewModel;
	}

	protected LocationViewModel convertFromLocationToLocationViewModel(final Location location) {
		final LocationViewModel viewModel = new LocationViewModel();
		viewModel.setLocationId(location.getLocid());
		viewModel.setLocationName(location.getLname());
		viewModel.setLocationAbbreviation(location.getLabbr());
		viewModel.setLtype(location.getLtype());

		final Location province = location.getProvince();
		if (province != null) {
			viewModel.setProvinceId(province.getLocid());
		}

		viewModel.setLatitude(location.getLatitude());
		viewModel.setLongitude(location.getLongitude());
		viewModel.setAltitude(location.getAltitude());
		viewModel.setlDefault(location.getLdefault());

		if (location.getCountry() != null) {
			viewModel.setCntryid(location.getCountry().getCntryid());

			final Country country = this.locationDataManager.getCountryById(location.getCountry().getCntryid());
			if (country != null) {
				viewModel.setCntryFullName(country.getIsofull());
			}
		}

		final UserDefinedField udf = this.locationDataManager.getUserDefinedFieldByID(location.getLtype());
		if (udf != null) {
			viewModel.setLtypeStr(udf.getFname());
			viewModel.setLtype(udf.getLfldno());
		}

		return viewModel;
	}

	public List<Country> getCountryList() {
		final List<Country> countryList = this.locationDataManager.getAllCountry();
		Collections.sort(countryList, new Comparator<Country>() {

			@Override
			public int compare(final Country o1, final Country o2) {

				return o1.getIsoabbr().compareTo(o2.getIsoabbr());

			}
		});

		return countryList;
	}

	public List<Location> getAllProvinces() {
		return this.locationDataManager.getAllProvinces();
	}

	public List<Location> getAllProvincesByCountry(final Integer countryId) {
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
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// No values are required to be initialized for this layout
	}

	public List<UserDefinedField> getUDFByLocationAndLType() {
		return this.locationDataManager.getUserDefinedFieldByFieldTableNameAndType("LOCATION", "LTYPE");
	}

	public void addLocation(final Location loc) {
		// if crop only AKA locationView instantiated from Add new program page, just add the row to the view table.

		if (!this.isCropOnly) {

			this.locationDataManager.addLocation(loc);
			final LocationViewModel locationVModel = this.getLocationDetailsByLocId(loc.getLocid());
			this.view.addRow(locationVModel, true, 0);

		} else {
			this.view.addRow(this.convertFromLocationToLocationViewModel(loc), true, 0);
		}
	}

	public void updateLocation(final LocationViewModel locationViewModel, final boolean isEditedFromAvailableTable) {
		this.locationDataManager.addLocation(convertLocationViewToLocation(locationViewModel));
		this.view.refreshLocationViewItemInTable(isEditedFromAvailableTable, locationViewModel);
	}

	public List<Location> getExistingLocations(final String locationName) {
		return this.locationDataManager.getLocationsByName(locationName, Operation.EQUAL);
	}

	public Location convertLocationViewToLocation(final LocationViewModel locationViewModel) {
		final Location location = new Location();

		location.setLrplce(0);

		location.setLocid(locationViewModel.getLocationId());
		location.setLname(locationViewModel.getLocationName());
		location.setLabbr(locationViewModel.getLocationAbbreviation());
		location.setLtype(locationViewModel.getLtype());

		if (locationViewModel.getCntryid() != null) {
			final Country country = this.locationDataManager.getCountryById(locationViewModel.getCntryid());
			location.setCountry(country);
		}

		location.setLatitude(locationViewModel.getLatitude());
		location.setLongitude(locationViewModel.getLongitude());
		location.setAltitude(locationViewModel.getAltitude());

		// defaults
		location.setNllp(0);
		location.setSnl3id(0);
		location.setSnl2id(0);

		if (locationViewModel.getProvinceId() != null) {
			final Location province = this.locationDataManager.getLocationByID(locationViewModel.getProvinceId());
			location.setProvince(province);
		}

		location.setLdefault(locationViewModel.getlDefault());
		if (location.getLdefault() == null) {
			location.setLdefault(Boolean.FALSE);
		}

		return location;
	}

	public List<Location> convertTo(final Collection<LocationViewModel> locationViewModels) {
		final List<Location> result = new ArrayList<>();

		for (final LocationViewModel locationViewModel : locationViewModels) {
			result.add(this.convertLocationViewToLocation(locationViewModel));
		}

		return result;
	}

	public void setCropType(final CropType cropType) {
		this.cropType = cropType;
	}

	public void setView(final ProgramLocationsView view) {
		this.view = view;
	}
}

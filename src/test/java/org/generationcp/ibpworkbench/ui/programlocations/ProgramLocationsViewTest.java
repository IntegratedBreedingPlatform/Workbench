
package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class ProgramLocationsViewTest {

	private ProgramLocationsView view;
	private SimpleResourceBundleMessageSource messageSource;
	private Label favTotalEntriesLabel;
	private Label favSelectedEntriesLabel;
	private Label availTotalEntriesLabel;
	private static final String TABLE_ROW = "TABLE_ROW_";
	private static final int NO_OF_ROWS = 101;
	private static final String LOCATION_NAME = "Phil";
	private static final Integer PHILIPPINES_CNTRYID = 171;
	private static final Integer COUNTRY_LTYPEID = 405;

	@Before
	public void setUp() {
		this.favTotalEntriesLabel = new Label();
		this.favSelectedEntriesLabel = new Label();
		this.availTotalEntriesLabel = new Label();
		this.view = new ProgramLocationsView(new Project());
		this.messageSource = Mockito.mock(SimpleResourceBundleMessageSource.class);
		this.view.setMessageSource(this.messageSource);
		this.view.setFavTotalEntriesLabel(this.favTotalEntriesLabel);
		this.view.setFavSelectedEntriesLabel(this.favSelectedEntriesLabel);
		this.view.setAvailTotalEntriesLabel(this.availTotalEntriesLabel);
		this.setupFilters();
	}

	@Test
	public void testUpdateNoOfEntries() throws Exception {

		Table customTable = this.createTableTestData();
		this.view.updateNoOfEntries(this.favTotalEntriesLabel, customTable);

		int actualNoOfEntries = this.getNoOfEntries(customTable);
		int expectedNoOfEntries = ProgramLocationsViewTest.NO_OF_ROWS;

		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries, String.valueOf(this.favTotalEntriesLabel.getValue())
				.contains(String.valueOf(expectedNoOfEntries)));

		customTable.removeItem(ProgramLocationsViewTest.TABLE_ROW + 1);
		customTable.removeItem(ProgramLocationsViewTest.TABLE_ROW + 2);
		this.view.updateNoOfEntries(this.favTotalEntriesLabel, customTable);

		actualNoOfEntries = this.getNoOfEntries(customTable);
		expectedNoOfEntries -= 2;

		actualNoOfEntries = this.getNoOfEntries(customTable);
		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries, String.valueOf(this.favTotalEntriesLabel.getValue())
				.contains(String.valueOf(expectedNoOfEntries)));

		customTable.addItem(ProgramLocationsViewTest.TABLE_ROW + 1);
		this.view.updateNoOfEntries(this.favTotalEntriesLabel, customTable);

		actualNoOfEntries = this.getNoOfEntries(customTable);
		expectedNoOfEntries += 1;
		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries, String.valueOf(this.favTotalEntriesLabel.getValue())
				.contains(String.valueOf(expectedNoOfEntries)));
	}

	private int getNoOfEntries(Table customTable) {
		return customTable.getItemIds().size();
	}

	private Table createTableTestData() {
		Table table = new Table();
		BeanItemContainer<String> containerDataSource = new BeanItemContainer<String>(String.class);
		table.setContainerDataSource(containerDataSource);
		for (int i = 0; i < ProgramLocationsViewTest.NO_OF_ROWS; i++) {
			String item = ProgramLocationsViewTest.TABLE_ROW + i;
			table.addItem(item);
			containerDataSource.addBean(item);
		}
		return table;
	}

	@Test
	public void testMoveSelectedItems() {
		Table availableTable = this.createTableLocationViewModelTestData(ProgramLocationsView.AVAILABLE);
		Table favoritesTable = this.createEmptyTableLocationViewModelTestData(ProgramLocationsView.FAVORITES);

		this.selectAllItems(availableTable);
		this.view.moveSelectedItems(availableTable, favoritesTable);

		int expectedNoOfEntries = ProgramLocationsViewTest.NO_OF_ROWS;
		int actualNoOfEntries = this.getNoOfEntries(favoritesTable);
		int expectedSelectedNoOfEntries = 0;
		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries, String.valueOf(this.favTotalEntriesLabel.getValue())
				.contains(String.valueOf(expectedNoOfEntries)));
		Assert.assertTrue("The number of selected entries must be " + expectedSelectedNoOfEntries,
				String.valueOf(this.favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));

		this.selectItems(favoritesTable, 2);
		this.view.moveSelectedItems(favoritesTable, availableTable);

		expectedNoOfEntries -= 2;
		actualNoOfEntries = this.getNoOfEntries(favoritesTable);
		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries, String.valueOf(this.favTotalEntriesLabel.getValue())
				.contains(String.valueOf(expectedNoOfEntries)));
		Assert.assertTrue("The number of selected entries must be " + expectedSelectedNoOfEntries,
				String.valueOf(this.favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));

		this.selectAllItems(favoritesTable);
		this.view.moveSelectedItems(favoritesTable, availableTable);

		expectedNoOfEntries = 0;
		actualNoOfEntries = this.getNoOfEntries(favoritesTable);
		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries, String.valueOf(this.favTotalEntriesLabel.getValue())
				.contains(String.valueOf(expectedNoOfEntries)));
		Assert.assertTrue("The number of selected entries must be " + expectedSelectedNoOfEntries,
				String.valueOf(this.favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));

	}

	private void selectAllItems(Table table) {
		table.setValue(table.getItemIds());
	}

	private void selectItems(Table table, int noOfItems) {
		List<Object> items = new ArrayList<Object>();
		@SuppressWarnings("unchecked")
		Iterator<Integer> itemIterator = (Iterator<Integer>) table.getItemIds().iterator();
		for (int i = 0; i < noOfItems; i++) {
			items.add(itemIterator.next());
		}
		table.setValue(items);
	}

	private Table createTableLocationViewModelTestData(String data) {
		Table table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setData(data);
		BeanItemContainer<LocationViewModel> containerDataSource = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		table.setContainerDataSource(containerDataSource);
		for (int itemId = 0; itemId < ProgramLocationsViewTest.NO_OF_ROWS; itemId++) {
			table.addItem(itemId);
			LocationViewModel model = new LocationViewModel();
			model.setLocationId(itemId);
			containerDataSource.addBean(model);
		}
		return table;
	}

	private Table createEmptyTableLocationViewModelTestData(String data) {
		Table table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setData(data);
		BeanItemContainer<LocationViewModel> containerDataSource = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		table.setContainerDataSource(containerDataSource);
		return table;
	}

	@Test
	public void testAddRow() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		LocationViewModel model = this.createLocationViewModelTestData();

		this.view.addRow(model, true, 0);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;

		int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		Assert.assertEquals("The number of rows for favorites locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for favorites locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

	}

	private void setupFilters() {
		Select countryFilter = new Select();
		countryFilter.setValue(0);
		this.view.setCountryFilter(countryFilter);

		Select locationTypeFilter = new Select();
		locationTypeFilter.setValue(0);
		this.view.setLocationTypeFilter(locationTypeFilter);

		TextField searchField = new TextField();
		searchField.setValue("");
		this.view.setSearchField(searchField);

	}

	@Test
	public void testAddRowNullIndex() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		LocationViewModel model = this.createLocationViewModelTestData();
		this.view.addRow(model, true, null);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;

		int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		Assert.assertEquals("The number of rows for favorites locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for favorites locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

	}

	private LocationViewModel createLocationViewModelTestData() {
		LocationViewModel model = new LocationViewModel();
		model.setLocationId(new Double(Math.random() * 10).intValue());
		return model;
	}

	@SuppressWarnings("unchecked")
	private void setUpTables() {
		Table availableTable = this.createEmptyTableLocationViewModelTestData(ProgramLocationsView.AVAILABLE);
		this.view.setAvailableTable(availableTable);
		this.view.setAvailableTableContainer((BeanItemContainer<LocationViewModel>) availableTable.getContainerDataSource());
		Table favoritesTable = this.createEmptyTableLocationViewModelTestData(ProgramLocationsView.FAVORITES);
		this.view.setFavoritesTable(favoritesTable);
		this.view.setFavoritesTableContainer((BeanItemContainer<LocationViewModel>) favoritesTable.getContainerDataSource());
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_SameCountryAndTypeAndWithNameKeyword() {
		LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		String locationName = ProgramLocationsViewTest.LOCATION_NAME;
		Country selectedCountry = new Country();
		selectedCountry.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		UserDefinedField selectedLocationType = new UserDefinedField();
		selectedLocationType.setFldno(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertTrue("The location should be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_AllCountryAndAllTypeAndNoNameKeyword() {
		LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		String locationName = "";
		Country selectedCountry = null;
		UserDefinedField selectedLocationType = null;

		boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertTrue("The location should be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_AllCountryAndZeroTypeAndNoNameKeyword() {
		LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		String locationName = "";
		Country selectedCountry = null;
		UserDefinedField selectedLocationType = new UserDefinedField();
		selectedLocationType.setFldno(0);

		boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertTrue("The location should be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_DiffCountry() {
		LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		String locationName = "";
		Country selectedCountry = new Country();
		selectedCountry.setCntryid(1);
		UserDefinedField selectedLocationType = null;

		boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertFalse("The location should not be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_DiffType() {
		LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		String locationName = "";
		Country selectedCountry = null;
		UserDefinedField selectedLocationType = new UserDefinedField();
		selectedLocationType.setFldno(1);

		boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertFalse("The location should not be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_DiffName() {
		LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		String locationName = "Test";
		Country selectedCountry = null;
		UserDefinedField selectedLocationType = null;

		boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertFalse("The location should not be displayed", isToBeDisplayed);
	}
}

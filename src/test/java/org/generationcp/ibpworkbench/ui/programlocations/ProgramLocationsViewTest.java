package org.generationcp.ibpworkbench.ui.programlocations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProgramLocationsViewTest {

	public static final String PROGRAM_UUID = "812472357-72384632-387247384";
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

	@Mock
	private Table availableTable;

	@Mock
	private Table favoritesTable;

	@Mock
	private Component component;

	@Mock
	private Window window;

	@Before
	public void setUp() {
		this.favTotalEntriesLabel = new Label();
		this.favSelectedEntriesLabel = new Label();
		this.availTotalEntriesLabel = new Label();
		this.view = new ProgramLocationsView(new Project());
		this.messageSource = mock(SimpleResourceBundleMessageSource.class);
		this.view.setMessageSource(this.messageSource);
		this.view.setFavTotalEntriesLabel(this.favTotalEntriesLabel);
		this.view.setFavSelectedEntriesLabel(this.favSelectedEntriesLabel);
		this.view.setAvailTotalEntriesLabel(this.availTotalEntriesLabel);
		this.view.setAvailableTable(this.availableTable);
		this.view.setFavoritesTable(this.favoritesTable);
		this.setupFilters();
	}

	@Test
	public void testUpdateNoOfEntries() throws Exception {

		final Table customTable = this.createTableTestData();
		this.view.updateNoOfEntries(this.favTotalEntriesLabel, customTable);

		int actualNoOfEntries = this.getNoOfEntries(customTable);
		int expectedNoOfEntries = ProgramLocationsViewTest.NO_OF_ROWS;

		assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(this.favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));

		customTable.removeItem(ProgramLocationsViewTest.TABLE_ROW + 1);
		customTable.removeItem(ProgramLocationsViewTest.TABLE_ROW + 2);
		this.view.updateNoOfEntries(this.favTotalEntriesLabel, customTable);

		actualNoOfEntries = this.getNoOfEntries(customTable);
		expectedNoOfEntries -= 2;

		actualNoOfEntries = this.getNoOfEntries(customTable);
		assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(this.favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));

		customTable.addItem(ProgramLocationsViewTest.TABLE_ROW + 1);
		this.view.updateNoOfEntries(this.favTotalEntriesLabel, customTable);

		actualNoOfEntries = this.getNoOfEntries(customTable);
		expectedNoOfEntries += 1;
		assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(this.favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
	}

	private int getNoOfEntries(final Table customTable) {
		return customTable.getItemIds().size();
	}

	private Table createTableTestData() {
		final Table table = new Table();
		final BeanItemContainer<String> containerDataSource = new BeanItemContainer<String>(String.class);
		table.setContainerDataSource(containerDataSource);
		for (int i = 0; i < ProgramLocationsViewTest.NO_OF_ROWS; i++) {
			final String item = ProgramLocationsViewTest.TABLE_ROW + i;
			table.addItem(item);
			containerDataSource.addBean(item);
		}
		return table;
	}

	@Test
	public void testMoveSelectedItems() {
		final Table availableTable = this.createTableLocationViewModelTestData(ProgramLocationsView.AVAILABLE);
		final Table favoritesTable = this.createEmptyTableLocationViewModelTestData(ProgramLocationsView.FAVORITES);

		this.selectAllItems(availableTable);
		this.view.moveSelectedItems(availableTable, favoritesTable);

		int expectedNoOfEntries = ProgramLocationsViewTest.NO_OF_ROWS;
		int actualNoOfEntries = this.getNoOfEntries(favoritesTable);
		final int expectedSelectedNoOfEntries = 0;
		assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(this.favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		Assert.assertTrue("The number of selected entries must be " + expectedSelectedNoOfEntries,
				String.valueOf(this.favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));

		this.selectItems(favoritesTable, 2);
		this.view.moveSelectedItems(favoritesTable, availableTable);

		expectedNoOfEntries -= 2;
		actualNoOfEntries = this.getNoOfEntries(favoritesTable);
		assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(this.favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		Assert.assertTrue("The number of selected entries must be " + expectedSelectedNoOfEntries,
				String.valueOf(this.favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));

		this.selectAllItems(favoritesTable);
		this.view.moveSelectedItems(favoritesTable, availableTable);

		expectedNoOfEntries = 0;
		actualNoOfEntries = this.getNoOfEntries(favoritesTable);
		assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(this.favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		Assert.assertTrue("The number of selected entries must be " + expectedSelectedNoOfEntries,
				String.valueOf(this.favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));

	}

	private void selectAllItems(final Table table) {
		table.setValue(table.getItemIds());
	}

	private void selectItems(final Table table, final int noOfItems) {
		final List<Object> items = new ArrayList<Object>();
		@SuppressWarnings("unchecked") final Iterator<Integer> itemIterator = (Iterator<Integer>) table.getItemIds().iterator();
		for (int i = 0; i < noOfItems; i++) {
			items.add(itemIterator.next());
		}
		table.setValue(items);
	}

	private Table createTableLocationViewModelTestData(final String data) {
		final Table table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setData(data);
		final BeanItemContainer<LocationViewModel> containerDataSource = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		table.setContainerDataSource(containerDataSource);
		for (int itemId = 0; itemId < ProgramLocationsViewTest.NO_OF_ROWS; itemId++) {
			table.addItem(itemId);
			final LocationViewModel model = new LocationViewModel();
			model.setLocationId(itemId);
			containerDataSource.addBean(model);
		}
		return table;
	}

	private Table createEmptyTableLocationViewModelTestData(final String data) {
		final Table table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setData(data);
		final BeanItemContainer<LocationViewModel> containerDataSource = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		table.setContainerDataSource(containerDataSource);
		return table;
	}

	@Test
	public void testAddRow() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		final LocationViewModel model = this.createLocationViewModelTestData();

		this.view.addRow(model, true, 0);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;

		final int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		final int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		assertEquals("The number of rows for favorites locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for favorites locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

	}

	private void setupFilters() {
		final Select countryFilter = new Select();
		countryFilter.setValue(0);
		this.view.setCountryFilter(countryFilter);

		final Select locationTypeFilter = new Select();
		locationTypeFilter.setValue(0);
		this.view.setLocationTypeFilter(locationTypeFilter);

		final TextField searchField = new TextField();
		searchField.setValue("");
		this.view.setSearchField(searchField);

	}

	@Test
	public void testAddRowNullIndex() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		final LocationViewModel model = this.createLocationViewModelTestData();
		this.view.addRow(model, true, null);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;

		final int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		final int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		assertEquals("The number of rows for favorites locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for favorites locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

	}

	private LocationViewModel createLocationViewModelTestData() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationId(new Double(Math.random() * 10).intValue());
		locationViewModel.setLocationName("LocationName");
		locationViewModel.setLocationAbbreviation("locationAbbreviation");
		locationViewModel.setLtype(1);
		locationViewModel.setLtypeStr("locationType");
		locationViewModel.setCntryid(2);
		locationViewModel.setCntryName("countryName");
		locationViewModel.setCntryFullName("countryFullName");
		locationViewModel.setProvinceId(3);
		locationViewModel.setProvinceName("provinceName");
		locationViewModel.setAltitude(5d);
		locationViewModel.setLatitude(6d);
		locationViewModel.setLongitude(7d);
		return locationViewModel;
	}

	@SuppressWarnings("unchecked")
	private void setUpTables() {
		final Table availableTable = this.createEmptyTableLocationViewModelTestData(ProgramLocationsView.AVAILABLE);
		this.view.setAvailableTable(availableTable);
		this.view.setAvailableTableContainer((BeanItemContainer<LocationViewModel>) availableTable.getContainerDataSource());
		final Table favoritesTable = this.createEmptyTableLocationViewModelTestData(ProgramLocationsView.FAVORITES);
		this.view.setFavoritesTable(favoritesTable);
		this.view.setFavoritesTableContainer((BeanItemContainer<LocationViewModel>) favoritesTable.getContainerDataSource());
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_SameCountryAndTypeAndWithNameKeyword() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		final String locationName = ProgramLocationsViewTest.LOCATION_NAME;
		final Country selectedCountry = new Country();
		selectedCountry.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		final UserDefinedField selectedLocationType = new UserDefinedField();
		selectedLocationType.setFldno(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		final boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertTrue("The location should be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_AllCountryAndAllTypeAndNoNameKeyword() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		final String locationName = "";
		final Country selectedCountry = null;
		final UserDefinedField selectedLocationType = null;

		final boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertTrue("The location should be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_AllCountryAndZeroTypeAndNoNameKeyword() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		final String locationName = "";
		final Country selectedCountry = null;
		final UserDefinedField selectedLocationType = new UserDefinedField();
		selectedLocationType.setFldno(0);

		final boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertTrue("The location should be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_DiffCountry() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		final String locationName = "";
		final Country selectedCountry = new Country();
		selectedCountry.setCntryid(1);
		final UserDefinedField selectedLocationType = null;

		final boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertFalse("The location should not be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_DiffType() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		final String locationName = "";
		final Country selectedCountry = null;
		final UserDefinedField selectedLocationType = new UserDefinedField();
		selectedLocationType.setFldno(1);

		final boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertFalse("The location should not be displayed", isToBeDisplayed);
	}

	@Test
	public void testIsToBeDisplayedInAvailableLocations_DiffName() {
		final LocationViewModel locationViewModel = new LocationViewModel();
		locationViewModel.setLocationName("Pre_" + ProgramLocationsViewTest.LOCATION_NAME + "_Post");
		locationViewModel.setCntryid(ProgramLocationsViewTest.PHILIPPINES_CNTRYID);
		locationViewModel.setLtype(ProgramLocationsViewTest.COUNTRY_LTYPEID);

		final String locationName = "Test";
		final Country selectedCountry = null;
		final UserDefinedField selectedLocationType = null;

		final boolean isToBeDisplayed =
				this.view.isToBeDisplayedInAvailableLocations(locationViewModel, locationName, selectedCountry, selectedLocationType);
		Assert.assertFalse("The location should not be displayed", isToBeDisplayed);
	}

	@Test
	public void testCopyLocationViewModelToTableItem() {

		final LocationViewModel locationViewModel = createLocationViewModelTestData();

		final BeanItemContainer<LocationViewModel> beanItemContainer = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		final LocationViewModel bean = new LocationViewModel();
		bean.setLocationId(locationViewModel.getLocationId());
		beanItemContainer.addBean(bean);

		this.view.copyLocationViewModelToTableItem(beanItemContainer, locationViewModel);

		assertEqualLocationViewModel(bean, locationViewModel);

	}

	@Test
	public void testRefreshTable() {

		this.view.refreshTable();

		Mockito.verify(this.availableTable).requestRepaint();
		Mockito.verify(this.availableTable).refreshRowCache();
		Mockito.verify(this.favoritesTable).requestRepaint();
		Mockito.verify(this.favoritesTable).refreshRowCache();

	}

	@Test
	public void testRefreshLocationViewItemInTableEditedFromAvailableTable() {

		final LocationViewModel locationViewModel = createLocationViewModelTestData();

		final BeanItemContainer<LocationViewModel> beanItemContainer = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		final LocationViewModel bean = new LocationViewModel();
		bean.setLocationId(locationViewModel.getLocationId());
		beanItemContainer.addBean(bean);

		this.view.setFavoritesTableContainer(beanItemContainer);

		this.view.refreshLocationViewItemInTable(true, locationViewModel);

		assertEqualLocationViewModel(bean, locationViewModel);
	}

	@Test
	public void testRefreshLocationViewItemInTableEditedFromFavoritesTable() {

		final LocationViewModel locationViewModel = createLocationViewModelTestData();

		final BeanItemContainer<LocationViewModel> beanItemContainer = new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		final LocationViewModel bean = new LocationViewModel();
		bean.setLocationId(locationViewModel.getLocationId());
		beanItemContainer.addBean(bean);

		this.view.setAvailableTableContainer(beanItemContainer);

		this.view.refreshLocationViewItemInTable(false, locationViewModel);

		assertEqualLocationViewModel(bean, locationViewModel);

	}

	@Test
	public void testLocationNameColumnGenerator() {

		final Table table = mock(Table.class);
		final LocationViewModel locationViewModel = createLocationViewModelTestData();

		final ProgramLocationsView.LocationNameColumnGenerator generator = this.view.new LocationNameColumnGenerator(table);

		final Button locationNameLinkButton = (Button) generator.generateCell(null, locationViewModel, null);

		assertNotNull(locationNameLinkButton);
		assertEquals(locationViewModel.getLocationName(), locationNameLinkButton.getCaption());
		assertNotNull(locationNameLinkButton.getListeners(ProgramLocationsView.LocationNameEditClickListener.class));

	}

	@Test
	public void testLocationNameEditClickListener() {

		final Button.ClickEvent clickEvent = mock(Button.ClickEvent.class);
		when(clickEvent.getComponent()).thenReturn(this.component);
		when(this.component.getWindow()).thenReturn(this.window);

		final Table table = mock(Table.class);
		final LocationViewModel locationViewModel = createLocationViewModelTestData();

		final ProgramLocationsView.LocationNameEditClickListener listener =
				this.view.new LocationNameEditClickListener(locationViewModel, table);

		listener.buttonClick(clickEvent);

		Mockito.verify(this.window).addWindow(any(EditLocationsWindow.class));

	}

	void assertEqualLocationViewModel(final LocationViewModel object1, final LocationViewModel object2) {

		assertEquals(object1.getLocationName(), object2.getLocationName());
		assertEquals(object1.getLocationAbbreviation(), object2.getLocationAbbreviation());
		assertEquals(object1.getLtype(), object2.getLtype());
		assertEquals(object1.getLtypeStr(), object2.getLtypeStr());
		assertEquals(object1.getCntryid(), object2.getCntryid());
		assertEquals(object1.getCntryName(), object2.getCntryName());
		assertEquals(object1.getCntryFullName(), object2.getCntryFullName());
		assertEquals(object1.getProvinceId(), object2.getProvinceId());
		assertEquals(object1.getProvinceName(), object2.getProvinceName());
		assertEquals(object1.getAltitude(), object2.getAltitude());
		assertEquals(object1.getLatitude(), object2.getLatitude());
		assertEquals(object1.getLongitude(), object2.getLongitude());
	}

}

package org.generationcp.ibpworkbench.ui.programlocations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class ProgramLocationsViewTest {

    private ProgramLocationsView view;
    private SimpleResourceBundleMessageSource messageSource;
    private Label favTotalEntriesLabel;
    private Label favSelectedEntriesLabel;
    private Label availTotalEntriesLabel;
    private static final String TABLE_ROW = "TABLE_ROW_";
    private static final int NO_OF_ROWS = 101;

    @Before
    public void setUp() {
    	favTotalEntriesLabel = new Label();
    	favSelectedEntriesLabel = new Label();
    	availTotalEntriesLabel = new Label();
    	view = new ProgramLocationsView(new Project());
    	messageSource = mock(SimpleResourceBundleMessageSource.class);
    	view.setMessageSource(messageSource);
    	view.setFavTotalEntriesLabel(favTotalEntriesLabel);
    	view.setFavSelectedEntriesLabel(favSelectedEntriesLabel);
    	view.setAvailTotalEntriesLabel(availTotalEntriesLabel);
    }

	@Test
    public void testUpdateNoOfEntries() throws Exception {
		
		Table customTable = createTableTestData();
		view.updateNoOfEntries(favTotalEntriesLabel, customTable);
		
		int actualNoOfEntries = getNoOfEntries(customTable);
		int expectedNoOfEntries = NO_OF_ROWS;
		
		assertEquals("The number of rows must be equal to "+expectedNoOfEntries,
				expectedNoOfEntries, actualNoOfEntries);
		assertTrue("The number of entries must be "+expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		
		customTable.removeItem(TABLE_ROW+1);
		customTable.removeItem(TABLE_ROW+2);
		view.updateNoOfEntries(favTotalEntriesLabel, customTable);
		
		actualNoOfEntries = getNoOfEntries(customTable);
		expectedNoOfEntries -= 2;
		
		actualNoOfEntries = getNoOfEntries(customTable);
		assertEquals("The number of rows must be equal to "+expectedNoOfEntries, expectedNoOfEntries, 
				actualNoOfEntries);
		assertTrue("The number of entries must be "+expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		
		customTable.addItem(TABLE_ROW+1);
		view.updateNoOfEntries(favTotalEntriesLabel, customTable);
		
		actualNoOfEntries = getNoOfEntries(customTable);
		expectedNoOfEntries += 1;
		assertEquals("The number of rows must be equal to "+expectedNoOfEntries, expectedNoOfEntries, 
				actualNoOfEntries);
		assertTrue("The number of entries must be "+expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
    }

	private int getNoOfEntries(Table customTable) {
		return customTable.getItemIds().size();
	}

	private Table createTableTestData() {
		Table table = new Table();
		BeanItemContainer<String> containerDataSource = new BeanItemContainer<String>(String.class);
		table.setContainerDataSource(containerDataSource);
		for(int i = 0; i < NO_OF_ROWS; i++) {
			String item = TABLE_ROW+i;
			table.addItem(item);
			containerDataSource.addBean(item);
		}
		return table;
	}
	
	@Test
    public void testMoveSelectedItems() {
		Table availableTable = createTableLocationViewModelTestData(ProgramLocationsView.AVAILABLE);
		Table favoritesTable = createEmptyTableLocationViewModelTestData(ProgramLocationsView.FAVORITES);
		
		selectAllItems(availableTable);
		view.moveSelectedItems(availableTable, favoritesTable);
		
		int expectedNoOfEntries = NO_OF_ROWS;
		int actualNoOfEntries = getNoOfEntries(favoritesTable);
		int expectedSelectedNoOfEntries = 0;
		assertEquals("The number of rows must be equal to "+expectedNoOfEntries, expectedNoOfEntries, 
				actualNoOfEntries);
		assertTrue("The number of entries must be "+expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		assertTrue("The number of selected entries must be "+expectedSelectedNoOfEntries,
				String.valueOf(favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));
		
		selectItems(favoritesTable,2);
		view.moveSelectedItems(favoritesTable, availableTable);
		
		expectedNoOfEntries -= 2;
		actualNoOfEntries = getNoOfEntries(favoritesTable);
		assertEquals("The number of rows must be equal to "+expectedNoOfEntries, expectedNoOfEntries, 
				actualNoOfEntries);
		assertTrue("The number of entries must be "+expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		assertTrue("The number of selected entries must be "+expectedSelectedNoOfEntries,
				String.valueOf(favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));
		
		selectAllItems(favoritesTable);
		view.moveSelectedItems(favoritesTable, availableTable);
		
		expectedNoOfEntries = 0;
		actualNoOfEntries = getNoOfEntries(favoritesTable);
		assertEquals("The number of rows must be equal to "+expectedNoOfEntries, expectedNoOfEntries, 
				actualNoOfEntries);
		assertTrue("The number of entries must be "+expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
		assertTrue("The number of selected entries must be "+expectedSelectedNoOfEntries,
				String.valueOf(favSelectedEntriesLabel.getValue()).contains(String.valueOf(expectedSelectedNoOfEntries)));
		
			
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
		BeanItemContainer<LocationViewModel> containerDataSource = 
				new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		table.setContainerDataSource(containerDataSource);
		for(int itemId = 0; itemId < NO_OF_ROWS; itemId++) {
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
		BeanItemContainer<LocationViewModel> containerDataSource = 
				new BeanItemContainer<LocationViewModel>(LocationViewModel.class);
		table.setContainerDataSource(containerDataSource);
		return table;
	}
	
	@Test
    public void testAddRow() throws Exception {
		setUpTables();
		int expectedNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		int expectedNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		
		LocationViewModel model = createLocationViewModelTestData();
		view.addRow(model, true, 0);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;
		
		int actualNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfAvailableEntries,
				String.valueOf(view.getAvailTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfAvailableEntries)));
		
		int actualNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		assertEquals("The number of rows for favorites locations must be equal to "+expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		assertTrue("The number of entries for favorites locations must be "+expectedNoOfFavoritesEntries,
				String.valueOf(view.getFavTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfFavoritesEntries)));
		
		
    }
	
	@Test
    public void testAddRowNullIndex() throws Exception {
		setUpTables();
		int expectedNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		int expectedNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		
		LocationViewModel model = createLocationViewModelTestData();
		view.addRow(model, true, null);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;
		
		int actualNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfAvailableEntries,
				String.valueOf(view.getAvailTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfAvailableEntries)));
		
		int actualNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		assertEquals("The number of rows for favorites locations must be equal to "+expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		assertTrue("The number of entries for favorites locations must be "+expectedNoOfFavoritesEntries,
				String.valueOf(view.getFavTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfFavoritesEntries)));
		
		
    }
	
	private LocationViewModel createLocationViewModelTestData() {
		LocationViewModel model = new LocationViewModel();
		model.setLocationId(new Double(Math.random()*10).intValue());
		return model;
	}

	@SuppressWarnings("unchecked")
	private void setUpTables() {
		Table availableTable = createEmptyTableLocationViewModelTestData(ProgramLocationsView.AVAILABLE);
		view.setAvailableTable(availableTable);
		view.setAvailableTableContainer(
				(BeanItemContainer<LocationViewModel>)availableTable.getContainerDataSource());
		Table favoritesTable = createEmptyTableLocationViewModelTestData(ProgramLocationsView.FAVORITES);
		view.setFavoritesTable(favoritesTable);
		view.setFavoritesTableContainer(
				(BeanItemContainer<LocationViewModel>)favoritesTable.getContainerDataSource());
	}
}
package org.generationcp.ibpworkbench.ui.programmethods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class ProgramMethodsViewTest {

    private ProgramMethodsView view;
    private SimpleResourceBundleMessageSource messageSource;
    private static final String TABLE_ROW = "TABLE_ROW_";
    private static final int NO_OF_ROWS = 5;

    @Before
    public void setUp() {
    	Label favTotalEntriesLabel = new Label();
    	Label availTotalEntriesLabel = new Label();
    	view = new ProgramMethodsView(new Project());
    	messageSource = mock(SimpleResourceBundleMessageSource.class);
    	view.setMessageSource(messageSource);
    	view.setFavTotalEntriesLabel(favTotalEntriesLabel);
		view.setAvailTotalEntriesLabel(availTotalEntriesLabel);
    }

	@Test
    public void testUpdateNoOfEntries() throws Exception {
		Label favTotalEntriesLabel = new Label();
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
		for(int i = 0; i < NO_OF_ROWS; i++) {
			table.addItem(TABLE_ROW+i);
		}
		return table;
	}
	
	@Test
    public void testAddRow() throws Exception {
		setUpTables();
		int expectedNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		int expectedNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		
		MethodView model = createMethodViewModelTestData();
		view.addRow(model, false, 0);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;
		
		int actualNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfAvailableEntries,
				String.valueOf(view.getAvailTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfAvailableEntries)));
		
		int actualNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfFavoritesEntries,
				String.valueOf(view.getFavTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfFavoritesEntries)));
		
		model = createMethodViewModelTestData();
    }
	
	private MethodView createMethodViewModelTestData() {
		MethodView model = new MethodView();
		model.setMid(new Double(Math.random()*10).intValue());
		return model;
	}
	
	@Test
    public void testAddRowNullIndex() throws Exception {
		setUpTables();
		int expectedNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		int expectedNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		
		MethodView model = createMethodViewModelTestData();
		view.addRow(model, false, null);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;
		
		int actualNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfAvailableEntries,
				String.valueOf(view.getAvailTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfAvailableEntries)));
		
		int actualNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfFavoritesEntries,
				String.valueOf(view.getFavTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfFavoritesEntries)));
		
		model = createMethodViewModelTestData();
    }

	@Test
    public void testAddRowAtAvailableTable() throws Exception {
		setUpTables();
		int expectedNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		int expectedNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		
		MethodView model = createMethodViewModelTestData();
		view.addRow(model, true, 0);
		expectedNoOfAvailableEntries++;
		
		int actualNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfAvailableEntries,
				String.valueOf(view.getAvailTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfAvailableEntries)));
		
		int actualNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfFavoritesEntries,
				String.valueOf(view.getFavTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfFavoritesEntries)));
		
		model = createMethodViewModelTestData();
    }
	
	@Test
    public void testAddRowNullIndexAtAvailableTable() throws Exception {
		setUpTables();
		int expectedNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		int expectedNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		
		MethodView model = createMethodViewModelTestData();
		view.addRow(model, true, null);
		expectedNoOfAvailableEntries++;
		
		int actualNoOfAvailableEntries = getNoOfEntries(view.getAvailableTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfAvailableEntries,
				String.valueOf(view.getAvailTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfAvailableEntries)));
		
		int actualNoOfFavoritesEntries = getNoOfEntries(view.getFavoritesTable());
		assertEquals("The number of rows for available locations must be equal to "+expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		assertTrue("The number of entries for available locations must be "+expectedNoOfFavoritesEntries,
				String.valueOf(view.getFavTotalEntriesLabel().getValue()).
					contains(String.valueOf(expectedNoOfFavoritesEntries)));
		
		model = createMethodViewModelTestData();
    }

	
	@SuppressWarnings("unchecked")
	private void setUpTables() {
		Table availableTable = createEmptyTableMethodViewModelTestData(ProgramMethodsView.AVAILABLE);
		view.setAvailableTable(availableTable);
		view.setAvailableTableContainer(
				(BeanItemContainer<MethodView>)availableTable.getContainerDataSource());
		Table favoritesTable = createEmptyTableMethodViewModelTestData(ProgramMethodsView.FAVORITES);
		view.setFavoritesTable(favoritesTable);
		view.setFavoritesTableContainer(
				(BeanItemContainer<MethodView>)favoritesTable.getContainerDataSource());
	}
	private Table createEmptyTableMethodViewModelTestData(String data) {
		Table table = new Table();
		table.setSelectable(true);
        table.setMultiSelect(true);
        table.setData(data);
		BeanItemContainer<MethodView> containerDataSource = 
				new BeanItemContainer<MethodView>(MethodView.class);
		table.setContainerDataSource(containerDataSource);
		return table;
	}
}
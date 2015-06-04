
package org.generationcp.ibpworkbench.ui.programmethods;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
		this.view = new ProgramMethodsView(new Project());
		this.messageSource = Mockito.mock(SimpleResourceBundleMessageSource.class);
		this.view.setMessageSource(this.messageSource);
		this.view.setFavTotalEntriesLabel(favTotalEntriesLabel);
		this.view.setAvailTotalEntriesLabel(availTotalEntriesLabel);
	}

	@Test
	public void testUpdateNoOfEntries() throws Exception {
		Label favTotalEntriesLabel = new Label();
		Table customTable = this.createTableTestData();
		this.view.updateNoOfEntries(favTotalEntriesLabel, customTable);

		int actualNoOfEntries = this.getNoOfEntries(customTable);
		int expectedNoOfEntries = ProgramMethodsViewTest.NO_OF_ROWS;

		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));

		customTable.removeItem(ProgramMethodsViewTest.TABLE_ROW + 1);
		customTable.removeItem(ProgramMethodsViewTest.TABLE_ROW + 2);
		this.view.updateNoOfEntries(favTotalEntriesLabel, customTable);

		actualNoOfEntries = this.getNoOfEntries(customTable);
		expectedNoOfEntries -= 2;

		actualNoOfEntries = this.getNoOfEntries(customTable);
		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));

		customTable.addItem(ProgramMethodsViewTest.TABLE_ROW + 1);
		this.view.updateNoOfEntries(favTotalEntriesLabel, customTable);

		actualNoOfEntries = this.getNoOfEntries(customTable);
		expectedNoOfEntries += 1;
		Assert.assertEquals("The number of rows must be equal to " + expectedNoOfEntries, expectedNoOfEntries, actualNoOfEntries);
		Assert.assertTrue("The number of entries must be " + expectedNoOfEntries,
				String.valueOf(favTotalEntriesLabel.getValue()).contains(String.valueOf(expectedNoOfEntries)));
	}

	private int getNoOfEntries(Table customTable) {
		return customTable.getItemIds().size();
	}

	private Table createTableTestData() {
		Table table = new Table();
		for (int i = 0; i < ProgramMethodsViewTest.NO_OF_ROWS; i++) {
			table.addItem(ProgramMethodsViewTest.TABLE_ROW + i);
		}
		return table;
	}

	@Test
	public void testAddRow() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		MethodView model = this.createMethodViewModelTestData();
		this.view.addRow(model, false, 0);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;

		int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

		model = this.createMethodViewModelTestData();
	}

	private MethodView createMethodViewModelTestData() {
		MethodView model = new MethodView();
		model.setMid(new Double(Math.random() * 10).intValue());
		return model;
	}

	@Test
	public void testAddRowNullIndex() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		MethodView model = this.createMethodViewModelTestData();
		this.view.addRow(model, false, null);
		expectedNoOfAvailableEntries++;
		expectedNoOfFavoritesEntries++;

		int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

		model = this.createMethodViewModelTestData();
	}

	@Test
	public void testAddRowAtAvailableTable() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		MethodView model = this.createMethodViewModelTestData();
		this.view.addRow(model, true, 0);
		expectedNoOfAvailableEntries++;

		int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

		model = this.createMethodViewModelTestData();
	}

	@Test
	public void testAddRowNullIndexAtAvailableTable() throws Exception {
		this.setUpTables();
		int expectedNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		int expectedNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());

		MethodView model = this.createMethodViewModelTestData();
		this.view.addRow(model, true, null);
		expectedNoOfAvailableEntries++;

		int actualNoOfAvailableEntries = this.getNoOfEntries(this.view.getAvailableTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfAvailableEntries,
				expectedNoOfAvailableEntries, actualNoOfAvailableEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfAvailableEntries,
				String.valueOf(this.view.getAvailTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfAvailableEntries)));

		int actualNoOfFavoritesEntries = this.getNoOfEntries(this.view.getFavoritesTable());
		Assert.assertEquals("The number of rows for available locations must be equal to " + expectedNoOfFavoritesEntries,
				expectedNoOfFavoritesEntries, actualNoOfFavoritesEntries);
		Assert.assertTrue("The number of entries for available locations must be " + expectedNoOfFavoritesEntries,
				String.valueOf(this.view.getFavTotalEntriesLabel().getValue()).contains(String.valueOf(expectedNoOfFavoritesEntries)));

		model = this.createMethodViewModelTestData();
	}

	@SuppressWarnings("unchecked")
	private void setUpTables() {
		Table availableTable = this.createEmptyTableMethodViewModelTestData(ProgramMethodsView.AVAILABLE);
		this.view.setAvailableTable(availableTable);
		this.view.setAvailableTableContainer((BeanItemContainer<MethodView>) availableTable.getContainerDataSource());
		Table favoritesTable = this.createEmptyTableMethodViewModelTestData(ProgramMethodsView.FAVORITES);
		this.view.setFavoritesTable(favoritesTable);
		this.view.setFavoritesTableContainer((BeanItemContainer<MethodView>) favoritesTable.getContainerDataSource());
	}

	private Table createEmptyTableMethodViewModelTestData(String data) {
		Table table = new Table();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setData(data);
		BeanItemContainer<MethodView> containerDataSource = new BeanItemContainer<MethodView>(MethodView.class);
		table.setContainerDataSource(containerDataSource);
		return table;
	}
}

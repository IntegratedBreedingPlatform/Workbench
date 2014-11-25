package org.generationcp.ibpworkbench.ui.programmethods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class ProgramMethodsViewTest {

    private ProgramMethodsView view;
    private SimpleResourceBundleMessageSource messageSource;
    private static final String TABLE_ROW = "TABLE_ROW_";
    private static final int NO_OF_ROWS = 5;

    @Before
    public void setUp() {
    	view = new ProgramMethodsView(new Project());
    	messageSource = mock(SimpleResourceBundleMessageSource.class);
    	view.setMessageSource(messageSource);
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
}
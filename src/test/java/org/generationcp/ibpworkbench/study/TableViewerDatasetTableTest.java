
package org.generationcp.ibpworkbench.study;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Button;
import junit.framework.Assert;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TableViewerDatasetTableTest {
	
	@Mock
	private StudyDataManager studyDataManager;

	@InjectMocks
	private TableViewerDatasetTable tableViewerTable;

	@Before
	public void setUp() {
		this.tableViewerTable = new TableViewerDatasetTable(this.studyDataManager, 1, 1);
	}

	@Test
	public void testRetrievalForDatasetWithLessThan100Experiments() throws MiddlewareException {
		int size = 89;
		Mockito.doReturn(new Long(size)).when(this.studyDataManager).countExperiments(Matchers.anyInt());
		Mockito.doReturn(this.createDummyExperiments(size)).when(this.studyDataManager).getExperiments(1, 0, size);

		List<Experiment> experimentsByBatch = this.tableViewerTable.getExperimentsByBatch();
		Mockito.verify(this.studyDataManager).getExperiments(1, 0, size);

		Assert.assertNotNull(experimentsByBatch);
		Assert.assertEquals("Expecting count of experiments retrieved to be equal to dataset size", size, experimentsByBatch.size());
	}

	@Test
	public void testBatchRetrievalForDatasetGreaterThan100ExperimentsWithBatchRemainder() throws MiddlewareException {
		int size = 180;
		Mockito.doReturn(new Long(size)).when(this.studyDataManager).countExperiments(Matchers.anyInt());

		this.validateBatchRetrieval(size);
	}

	@Test
	public void testBatchRetrievalForDatasetGreaterThan100ExperimentsNoBatchRemainder() throws MiddlewareException {
		int size = 300;
		Mockito.doReturn(new Long(size)).when(this.studyDataManager).countExperiments(Matchers.anyInt());

		this.validateBatchRetrieval(size);
	}
	
	@Test
	public void testPopulateDatasetTable() {
		Mockito.doReturn(new Long(1)).when(this.studyDataManager).countExperiments(Matchers.anyInt());
		final Experiment experiment = Mockito.mock(Experiment.class);
		Mockito.when(experiment.getFactors()).thenReturn(Mockito.mock(VariableList.class));
		Mockito.when(experiment.getVariates()).thenReturn(Mockito.mock(VariableList.class));
		Mockito.doReturn(Arrays.asList(experiment)).when(this.studyDataManager).getExperiments(1, 0, 1);
		this.tableViewerTable.populateDatasetTable();
		Mockito.verify(this.studyDataManager).createInstanceLocationIdToNameMapFromStudy(Matchers.anyInt());
		Mockito.verify(experiment).getFactors();
		Mockito.verify(experiment).getVariates();
	}
	
	@Test
	public void testSetItemValuesForLocation() {
		Map<String, String> locationNameMap = new HashMap<>();
		locationNameMap.put("9015", "INT WATER MANAGEMENT INSTITUTE");
		final List<Variable> variables = Arrays.asList(DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LOCATION_ID,
				"9015"));
		Item item = new PropertysetItem();
		final String columnId = "8190-LOCATION_ID";
		item.addItemProperty(columnId, new ObjectProperty<String>(""));
		this.tableViewerTable.setItemValues(locationNameMap, variables, item);
		Assert.assertEquals("INT WATER MANAGEMENT INSTITUTE", item.getItemProperty(columnId).getValue());
	}

	@Test
	public void testSetItemValuesForGID() {
		final List<Variable> variables = Arrays.asList(DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.GID, "1"));
		Item item = new PropertysetItem();
		final String columnId = "8240-GID";
		item.addItemProperty(columnId, new ObjectProperty<Button>(new Button()));
		this.tableViewerTable.setItemValues(new HashMap<String, String>(), variables, item);
		Assert.assertEquals("1", ((Button)item.getItemProperty(columnId).getValue()).getCaption());
	}
	
	@Test
	public void testSetItemValues() {
		final List<Variable> variables = Arrays.asList(DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.DESIG,
				"designation"));
		Item item = new PropertysetItem();
		final String columnId = "8250-DESIG";
		item.addItemProperty(columnId, new ObjectProperty<String>(""));
		this.tableViewerTable.setItemValues(new HashMap<String, String>(), variables, item);
		Assert.assertEquals("designation", item.getItemProperty(columnId).getValue());
	}
	
	@Test
	public void testSetItemValuesForNumeric() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LATITUDE,
				"10");
		variable.getVariableType().getStandardVariable().setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), TableViewerDatasetTable.NUMERIC_VARIABLE,
				"variable with numeric values"));
		final List<Variable> variables = Arrays.asList(variable);
		Item item = new PropertysetItem();
		final String columnId = "8191-LATITUDE";
		item.addItemProperty(columnId, new ObjectProperty<String>(""));
		this.tableViewerTable.setItemValues(new HashMap<String, String>(), variables, item);
		Assert.assertEquals("10", item.getItemProperty(columnId).getValue());
	}
	
	@Test
	public void testSetItemValuesForDate() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.DATE_VARIABLE,
				"07/27/2018");
		variable.getVariableType().getStandardVariable().setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), TableViewerDatasetTable.NUMERIC_VARIABLE,
				"variable with numeric values"));
		final List<Variable> variables = Arrays.asList(variable);
		Item item = new PropertysetItem();
		final String columnId = "1117-DATE_VARIABLE";
		item.addItemProperty(columnId, new ObjectProperty<String>(""));
		this.tableViewerTable.setItemValues(new HashMap<String, String>(), variables, item);
		Assert.assertEquals("20180727", item.getItemProperty(columnId).getValue());
	}
	
	private void validateBatchRetrieval(int size) throws MiddlewareException {
		int batchSize = TableViewerDatasetTable.BATCH_SIZE;
		int batchCount = size / batchSize;
		int remaining = size % batchSize;

		// return dummy experiments from Middleware
		List<Experiment> dummyBatchExperiments = this.createDummyExperiments(batchSize);
		for (int i = 0; i < batchCount; i++) {
			Mockito.doReturn(dummyBatchExperiments).when(this.studyDataManager).getExperiments(1, i * batchSize, batchSize);
		}
		if (remaining > 0) {
			Mockito.doReturn(this.createDummyExperiments(remaining)).when(this.studyDataManager)
					.getExperiments(1, batchSize * batchCount, remaining);
		}

		// actual method call, verify if proper Middleware calls were made
		List<Experiment> experimentsByBatch = this.tableViewerTable.getExperimentsByBatch();
		for (int i = 0; i < batchCount; i++) {
			Mockito.verify(this.studyDataManager).getExperiments(1, i * batchSize, batchSize);
		}
		if (remaining > 0) {
			Mockito.verify(this.studyDataManager).getExperiments(1, batchSize * batchCount, remaining);
		}

		Assert.assertNotNull(experimentsByBatch);
		Assert.assertEquals("Expecting count of experiments retrieved to be equal to dataset size", size, experimentsByBatch.size());
	}

	private List<Experiment> createDummyExperiments(int size) {
		List<Experiment> experiments = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			experiments.add(new Experiment());
		}
		return experiments;
	}

}


package org.generationcp.ibpworkbench.study.containers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.ExperimentTestDataInitializer;
import org.generationcp.middleware.data.initializer.StandardVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.VariableListTestDataInitializer;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;

@RunWith(MockitoJUnitRunner.class)
public class RepresentationDatasetQueryTest {

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private DatasetService datasetService;

	private RepresentationDataSetQuery query;

	@Before
	public void setUp() {
		this.query = new RepresentationDataSetQuery(this.datasetService, this.studyDataManager, new Integer(1), new ArrayList<String>(),
				false, 1);
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsNotCategorical() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.DATE_VARIABLE);
		Assert.assertFalse("Should return false since its a non categorical variable",
				this.query.isCategoricalAcceptedValue("1", standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNull() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		Assert.assertFalse("Should return false since its a value is null",
				this.query.isCategoricalAcceptedValue(null, standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndMatchingResults() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		final List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		Assert.assertFalse("Should return false since its a value is null",
				this.query.isCategoricalAcceptedValue("Desc", standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoMatchingResults() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		final List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		Assert.assertTrue("Should return true since its a value is not matching any valid values",
				this.query.isCategoricalAcceptedValue("Desc1", standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoEnumerations() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		standardVariable.setEnumerations(null);
		Assert.assertTrue("Should return true since its a value is not matching any valid values",
				this.query.isCategoricalAcceptedValue("Desc1", standardVariable));
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsNotCategorical() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.NUMERIC_VARIABLE);
		final Item item = Mockito.mock(Item.class);
		final boolean isAccepted = this.query.setAcceptedItemProperty("1", standardVariable, item, "1");
		Assert.assertFalse("Should return false since its a non categorical variable", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNull() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		final Item item = Mockito.mock(Item.class);
		final boolean isAccepted = this.query.setAcceptedItemProperty(null, standardVariable, item, "1");
		Assert.assertFalse("Should return false since its a value is null", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndMatchingResults() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		final List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		final Item item = Mockito.mock(Item.class);
		final boolean isAccepted = this.query.setAcceptedItemProperty("Desc", standardVariable, item, "1");
		Assert.assertFalse("Should return false since its a value is null", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoMatchingResults() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		final List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		final Item item = Mockito.mock(Item.class);
		final boolean isAccepted = this.query.setAcceptedItemProperty("Desc1", standardVariable, item, "1");
		Assert.assertTrue("Should return true since its a value is not matching any valid values", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoEnumerations() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		standardVariable.setEnumerations(null);
		final Item item = Mockito.mock(Item.class);
		final boolean isAccepted = this.query.setAcceptedItemProperty("Desc1", standardVariable, item, "1");
		Assert.assertTrue("Should return true since its a value is not matching any valid values", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNumericAndWithinMinMax() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.NUMERIC_VARIABLE);
		final VariableConstraints constraints = new VariableConstraints(new Double(1), new Double(10));
		standardVariable.setConstraints(constraints);
		final boolean isAccepted = this.query.isNumericalAcceptedValue("2", standardVariable);
		Assert.assertFalse("Should return false since its within the limit", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNonNumericAndNotWithinMinMax() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		final VariableConstraints constraints = new VariableConstraints(new Double(1), new Double(10));
		standardVariable.setConstraints(constraints);
		final boolean isAccepted = this.query.isNumericalAcceptedValue("20", standardVariable);
		Assert.assertFalse("Should return false since variable is non numberic", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNumericAndNotWithinMinMax() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.NUMERIC_VARIABLE);
		final VariableConstraints constraints = new VariableConstraints(new Double(1), new Double(10));
		standardVariable.setConstraints(constraints);
		final boolean isAccepted = this.query.isNumericalAcceptedValue("20", standardVariable);
		Assert.assertTrue("Should return true since its within the limit", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNumericAndNoWithinMinMax() {
		final StandardVariable standardVariable = StandardVariableTestDataInitializer
				.createStandardVariable(TermId.NUMERIC_VARIABLE);
		final boolean isAccepted = this.query.isNumericalAcceptedValue("20", standardVariable);
		Assert.assertFalse("Should return false since it has no limit", isAccepted);
	}

	@Test
	public void testLoadItems() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment(
				VariableListTestDataInitializer.createVariableList(TermId.CROSS), Mockito.mock(VariableList.class));
		Mockito.when(this.studyDataManager.getExperiments(1, 0, 1)).thenReturn(Arrays.asList(experiment));
		final RepresentationDataSetQuery query = new RepresentationDataSetQuery(this.datasetService, this.studyDataManager, new Integer(1),
				new ArrayList<String>(), false, 1);
		final List<Item> items = query.loadItems(0, 1);
		Mockito.verify(this.studyDataManager).getExperiments(1, 0, 1);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).getItemPropertyIds().contains("8377-CROSS"));
	}

	@Test
	public void testPopulateItemMapWithGID() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		final Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.GID, "1");
		final List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap, experiment, variables, new HashMap<String, String>());
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8240-GID"));
		Assert.assertEquals(Button.class, itemMap.get(1).getItemProperty("8240-GID").getValue().getClass());
	}

	@Test
	public void testPopulateItemMapWithStringValue() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		final Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.DESIG,
				"designation");
		final List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap, experiment, variables, new HashMap<String, String>());
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8250-DESIG"));
		Assert.assertEquals("designation", itemMap.get(1).getItemProperty("8250-DESIG").getValue());
	}

	@Test
	public void testPopulateItemMapWithNumericValueWithoutDecimals() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		final Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LATITUDE,
				"1");
		final List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap, experiment, variables, new HashMap<String, String>());
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8191-LATITUDE"));
		Assert.assertEquals("1", itemMap.get(1).getItemProperty("8191-LATITUDE").getValue());
	}

	@Test
	public void testPopulateItemMapWithNumericValueWithDecimals() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		final Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LATITUDE,
				"1.001");
		final List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap, experiment, variables, new HashMap<String, String>());
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8191-LATITUDE"));
		Assert.assertEquals("1.001", itemMap.get(1).getItemProperty("8191-LATITUDE").getValue());
	}
	
	@Test
	public void testPopulateItemMapWithLocation() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		final Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LOCATION_ID,
				"9015");
		Map<String, String> locationNameMap = new HashMap<>();
		locationNameMap.put("9015", "INT WATER MANAGEMENT INSTITUTE");
		final List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap, experiment, variables, locationNameMap);
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8190-LOCATION_ID"));
		Assert.assertEquals("INT WATER MANAGEMENT INSTITUTE", itemMap.get(1).getItemProperty("8190-LOCATION_ID").getValue());
	}
}

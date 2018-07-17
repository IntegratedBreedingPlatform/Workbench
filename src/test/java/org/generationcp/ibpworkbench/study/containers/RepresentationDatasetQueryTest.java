
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
import org.generationcp.middleware.domain.dms.StandardVariableTest;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;

@RunWith(MockitoJUnitRunner.class)
public class RepresentationDatasetQueryTest {
	
	@Mock
	private StudyDataManager studyDataManager;
	
	private RepresentationDataSetQuery query; 
	
	@Before
	public void setUp() {
		this.query = new RepresentationDataSetQuery(this.studyDataManager, new Integer(1), new ArrayList<String>(), false); 
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsNotCategorical() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.DATE_VARIABLE);
		Assert.assertFalse("Should return false since its a non categorical variable",
				this.query.isCategoricalAcceptedValue("1", standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNull() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		Assert.assertFalse("Should return false since its a value is null", this.query.isCategoricalAcceptedValue(null, standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndMatchingResults() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		Assert.assertFalse("Should return false since its a value is null", this.query.isCategoricalAcceptedValue("Desc", standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoMatchingResults() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		Assert.assertTrue("Should return true since its a value is not matching any valid values",
				query.isCategoricalAcceptedValue("Desc1", standardVariable));
	}

	@Test
	public void testIsCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoEnumerations() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		standardVariable.setEnumerations(null);
		Assert.assertTrue("Should return true since its a value is not matching any valid values",
				this.query.isCategoricalAcceptedValue("Desc1", standardVariable));
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsNotCategorical() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.NUMERIC_VARIABLE);
		Item item = Mockito.mock(Item.class);
		boolean isAccepted = this.query.setAcceptedItemProperty("1", standardVariable, item, "1");
		Assert.assertFalse("Should return false since its a non categorical variable", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNull() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		Item item = Mockito.mock(Item.class);
		boolean isAccepted = query.setAcceptedItemProperty(null, standardVariable, item, "1");
		Assert.assertFalse("Should return false since its a value is null", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndMatchingResults() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		Item item = Mockito.mock(Item.class);
		boolean isAccepted = query.setAcceptedItemProperty("Desc", standardVariable, item, "1");
		Assert.assertFalse("Should return false since its a value is null", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoMatchingResults() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		List<Enumeration> enumerations = new ArrayList<Enumeration>();
		enumerations.add(new Enumeration(1, "name", "desc", 1));
		standardVariable.setEnumerations(enumerations);
		Item item = Mockito.mock(Item.class);
		boolean isAccepted = query.setAcceptedItemProperty("Desc1", standardVariable, item, "1");
		Assert.assertTrue("Should return true since its a value is not matching any valid values", isAccepted);
	}

	@Test
	public void testSetAcceptedItemPropertyIfCategoricalAcceptedValueIfVariableIsCategoricalAndDisplayValueIsNotNullAndNoEnumerations() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		standardVariable.setEnumerations(null);
		Item item = Mockito.mock(Item.class);
		boolean isAccepted = query.setAcceptedItemProperty("Desc1", standardVariable, item, "1");
		Assert.assertTrue("Should return true since its a value is not matching any valid values", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNumericAndWithinMinMax() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.NUMERIC_VARIABLE);
		VariableConstraints constraints = new VariableConstraints(new Double(1), new Double(10));
		standardVariable.setConstraints(constraints);
		boolean isAccepted = query.isNumericalAcceptedValue("2", standardVariable);
		Assert.assertFalse("Should return false since its within the limit", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNonNumericAndNotWithinMinMax() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.CATEGORICAL_VARIABLE);
		VariableConstraints constraints = new VariableConstraints(new Double(1), new Double(10));
		standardVariable.setConstraints(constraints);
		boolean isAccepted = query.isNumericalAcceptedValue("20", standardVariable);
		Assert.assertFalse("Should return false since variable is non numberic", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNumericAndNotWithinMinMax() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.NUMERIC_VARIABLE);
		VariableConstraints constraints = new VariableConstraints(new Double(1), new Double(10));
		standardVariable.setConstraints(constraints);
		boolean isAccepted = query.isNumericalAcceptedValue("20", standardVariable);
		Assert.assertTrue("Should return true since its within the limit", isAccepted);
	}

	@Test
	public void testIsNumericalAcceptedValueIfVariableIsNumericAndNoWithinMinMax() {
		StandardVariable standardVariable = StandardVariableTestDataInitializer.createStandardVariable(TermId.NUMERIC_VARIABLE);
		boolean isAccepted = query.isNumericalAcceptedValue("20", standardVariable);
		Assert.assertFalse("Should return false since it has no limit", isAccepted);
	}
	
	@Test
	public void testLoadItems() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment(VariableListTestDataInitializer.createVariableList(TermId.CROSS), Mockito.mock(VariableList.class));
		Mockito.when(this.studyDataManager.getExperiments(1, 0, 1)).thenReturn(Arrays.asList(experiment));
		RepresentationDataSetQuery query =
				new RepresentationDataSetQuery(this.studyDataManager, new Integer(1), new ArrayList<String>(), false);
		List<Item> items = query.loadItems(0, 1);
		Mockito.verify(this.studyDataManager).getExperiments(1, 0, 1);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).getItemPropertyIds().contains("8377-CROSS"));
	}
	
	@Test
	public void testPopulateItemMapWithGID() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.GID, "1");
		List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap , experiment, variables);
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8240-GID"));
		Assert.assertEquals(Button.class, itemMap.get(1).getItemProperty("8240-GID").getValue().getClass());
	}
	
	@Test
	public void testPopulateItemMapWithStringValue() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.DESIG, "designation");
		List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap , experiment, variables);
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8250-DESIG"));
		Assert.assertEquals("designation", itemMap.get(1).getItemProperty("8250-DESIG").getValue());
	}
	
	@Test
	public void testPopulateItemMapWithNumericValueWithoutDecimals() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LATITUDE, "1");
		List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap , experiment, variables);
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8191-LATITUDE"));
		Assert.assertEquals("1", itemMap.get(1).getItemProperty("8191-LATITUDE").getValue());
	}
	
	@Test
	public void testPopulateItemMapWithNumericValueWithDecimals() {
		final Experiment experiment = ExperimentTestDataInitializer.createExperiment();
		Map<Integer, Item> itemMap = new HashMap<>();
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LATITUDE, "1.001");
		List<Variable> variables = Arrays.asList(variable);
		this.query.populateItemMap(itemMap , experiment, variables);
		Assert.assertEquals(1, itemMap.size());
		Assert.assertTrue(itemMap.get(1).getItemPropertyIds().contains("8191-LATITUDE"));
		Assert.assertEquals("1.001", itemMap.get(1).getItemProperty("8191-LATITUDE").getValue());
	}
}

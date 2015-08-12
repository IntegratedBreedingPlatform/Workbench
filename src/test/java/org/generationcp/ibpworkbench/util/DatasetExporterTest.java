
package org.generationcp.ibpworkbench.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.generationcp.middleware.service.api.OntologyService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DatasetExporterTest {

	@Mock
	private static ManagerFactory factory;

	@Mock
	private static StudyDataManager manager;

	@Mock
	private static WorkbenchDataManager workbenchDataManager;

	@Mock
	private BreedingViewInput bvInput;

	@Mock
	private DataSet dataSet;

	@Mock
	private Experiment experiment;

	@Mock
	private OntologyService ontologyService;

	private static final String FILENAME = "datasetExporterTest.csv";
	private static final String DEFAULT_TRIAL_INSTANCE_NAME = "SITE_NO";
	private static final String ALT_TRIAL_INSTANCE_NAME = "TRIAL";
	private static final String ENV_NAME = "SITE_NAME";
	private static final String ENV_VALUE = "CIMMYT Harrare";
	private static final String REP_NAME = "REP";
	private static final String VARIATE_NAME_1 = "EPP";
	private static final String VARIATE_NAME_2 = "PH";
	private static final String VARIATE_NAME_3 = "EARH";
	private static final String CATEGORICAL_VARIATE_NAME = "BB";

	private static final String DEFAULT_TRIAL_INSTANCE_VALUE_1 = "1";
	private static final String DEFAULT_TRIAL_INSTANCE_VALUE_2 = "2";
	private static final String VARIATE_VALUE_1 = "76.223";
	private static final String VARIATE_VALUE_2 = "7.5";
	private static final String VARIATE_VALUE_3 = "1111.0";
	private static final String CATEGORICAL_VARIATE_VALUE = "missing";
	private static final int CATEGORICAL_VARIATE_ENUM_ID = 1;
	private static final String CATEGORICAL_VARIATE_ENUM_NAME = "5";
	private static final String CATEGORICAL_VARIATE_ENUM_DESCRIPTION = "Very Severe";

	private static final Term NUMERIC_VARIABLE = new Term(TermId.NUMERIC_VARIABLE.getId(), "", "");
	private static final Term CATEGORICAL_VARIABLE = new Term(TermId.CATEGORICAL_VARIABLE.getId(), "", "");

	private static final Term TRIAL_INSTANCE_ROLE = new Term(TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
	private static final Term OBSERVATION_VARIATE_ROLE = new Term(TermId.OBSERVATION_VARIATE.getId(), "", "");
	private static final Term CATEGORICAL_VARIATE_ROLE = new Term(TermId.CATEGORICAL_VARIATE.getId(), "", "");

	private static final String VAR_POST_FIX = "%#! @";
	private static final String CLEANED_VAR_POST_FIX = "%_";

	public static final String BV_MISSING_VALUE = "";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExportToCSVForBreedingView_Default() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		this.createFactorsAndVariatesTestData(factors, factorVariables, variates, variateVariables, false);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		this.experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(this.experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(
					experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);
		DatasetExporter dut = Mockito.spy(exporter);

		dut.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			dut.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = dut.getTableItems();
		Map<String, String> headerAliasMap = dut.getHeaderNameAliasMap();

		// header
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				tableItems.get(0)[1]);
		Assert.assertEquals("Expected 3rd column header is " + DatasetExporterTest.VARIATE_NAME_2, DatasetExporterTest.VARIATE_NAME_2,
				tableItems.get(0)[2]);
		Assert.assertFalse(DatasetExporterTest.VARIATE_NAME_3 + " should not be included",
				ArrayUtils.contains(tableItems.get(0), DatasetExporterTest.VARIATE_NAME_3));

		// data
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		Assert.assertEquals("Expected 2nd column value is " + DatasetExporterTest.VARIATE_VALUE_1, DatasetExporterTest.VARIATE_VALUE_1,
				tableItems.get(1)[1]);
		Assert.assertEquals("Expected 3rd column value is " + DatasetExporterTest.VARIATE_VALUE_2, DatasetExporterTest.VARIATE_VALUE_2,
				tableItems.get(1)[2]);

		Assert.assertEquals("Header name should be " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_1));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_2, DatasetExporterTest.VARIATE_NAME_2,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_2));
		Assert.assertNull(DatasetExporterTest.VARIATE_NAME_3 + " should not be included",
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_3));

	}

	private DataSet createDatasetTestData(List<VariableType> factors, List<VariableType> variates) {
		DataSet dataSet = new DataSet();
		VariableTypeList variableTypes = new VariableTypeList();
		for (VariableType variableType : factors) {
			variableTypes.add(variableType);
		}
		for (VariableType variableType : variates) {
			variableTypes.add(variableType);
		}
		dataSet.setVariableTypes(variableTypes);
		return dataSet;
	}

	private Experiment createExperimentTestData(List<Variable> factorVariables, List<Variable> variateVariables) {
		Experiment experiment = new Experiment();
		VariableList factorVariableList = this.createVariableListTestData(factorVariables);
		VariableList variateVariableList = this.createVariableListTestData(variateVariables);
		experiment.setFactors(factorVariableList);
		experiment.setVariates(variateVariableList);
		experiment.setLocationId(1);
		return experiment;
	}

	private VariableList createVariableListTestData(List<Variable> variables) {
		VariableList variableList = new VariableList();
		variableList.setVariables(variables);
		return variableList;
	}

	private void setupSelectedVariables(HashMap<String, Boolean> variableState, List<VariableType> variates) {
		for (VariableType variableType : variates) {
			String localName = variableType.getLocalName();
			if (localName.startsWith(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME)
					|| localName.startsWith(DatasetExporterTest.VARIATE_NAME_1) || localName.startsWith(DatasetExporterTest.VARIATE_NAME_2)
					|| localName.startsWith(DatasetExporterTest.CATEGORICAL_VARIATE_NAME)) {
				variableState.put(localName, true);
			} else {
				variableState.put(localName, false);
			}
		}
	}

	private void createFactorsAndVariatesTestData(List<VariableType> factors, List<Variable> factorVariables, List<VariableType> variates,
			List<Variable> variateVariables, boolean addSpecialCharAndWhitespaceToVarName) {

		String varPostfix = "";
		if (addSpecialCharAndWhitespaceToVarName) {
			varPostfix = DatasetExporterTest.VAR_POST_FIX;
		}

		int rank = 1;

		this.addVariableToList(factors, factorVariables, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + varPostfix, rank++,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, PhenotypicType.TRIAL_ENVIRONMENT, DatasetExporterTest.NUMERIC_VARIABLE,
				DatasetExporterTest.TRIAL_INSTANCE_ROLE);

		this.addVariableToList(variates, variateVariables, DatasetExporterTest.VARIATE_NAME_1 + varPostfix, rank++,
				DatasetExporterTest.VARIATE_VALUE_1, PhenotypicType.VARIATE, DatasetExporterTest.NUMERIC_VARIABLE,
				DatasetExporterTest.OBSERVATION_VARIATE_ROLE);

		this.addVariableToList(variates, variateVariables, DatasetExporterTest.VARIATE_NAME_2 + varPostfix, rank++,
				DatasetExporterTest.VARIATE_VALUE_2, PhenotypicType.VARIATE, DatasetExporterTest.NUMERIC_VARIABLE,
				DatasetExporterTest.OBSERVATION_VARIATE_ROLE);

		this.addVariableToList(variates, variateVariables, DatasetExporterTest.VARIATE_NAME_3 + varPostfix, rank++,
				DatasetExporterTest.VARIATE_VALUE_3, PhenotypicType.VARIATE, DatasetExporterTest.NUMERIC_VARIABLE,
				DatasetExporterTest.OBSERVATION_VARIATE_ROLE);
	}

	private void addVariableToList(List<VariableType> variableTypes, List<Variable> variables, String variableName, int rank, String value,
			PhenotypicType phenotypicType, Term dataType, Term role) {
		StandardVariable standardVariable = this.createStardardVariableTestData(phenotypicType, variableName, dataType, role);
		VariableType variableType = this.createVariableTypeTestData(variableName, rank, standardVariable);
		variableTypes.add(variableType);
		variables.add(this.createVariableTestData(variableType, value));
	}

	private Variable createVariableTestData(VariableType trial, String value) {
		Variable variable = new Variable();
		variable.setValue(value);
		variable.setVariableType(trial);
		return variable;
	}

	private VariableType createVariableTypeTestData(String localName, int rank, StandardVariable standardVariable) {
		VariableType variableType = new VariableType();
		variableType.setLocalName(localName);
		variableType.setRank(rank);
		variableType.setStandardVariable(standardVariable);
		return variableType;
	}

	@Test
	public void testExportToCSVForBreedingView_NoExistingRecordsForSelectedEnvironment() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_2);

		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		this.createFactorsAndVariatesTestData(factors, factorVariables, variates, variateVariables, false);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		this.experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(this.experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(
					experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);
		DatasetExporter dut = Mockito.spy(exporter);

		dut.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			dut.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = dut.getTableItems();
		Map<String, String> headerAliasMap = dut.getHeaderNameAliasMap();

		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				tableItems.get(0)[1]);
		Assert.assertEquals("The CSV file should only have 1 row because it does not have a record", 1, tableItems.size());

		Assert.assertEquals("Header name should be " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_1));

	}

	@Test
	public void testExportToCSVForBreedingView_TraitNamesWithSpecialCharactersAndWhiteSpace() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		this.createFactorsAndVariatesTestData(factors, factorVariables, variates, variateVariables, true);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		this.experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(this.experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(
					experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(
					DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);
		DatasetExporter dut = Mockito.spy(exporter);

		dut.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			dut.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME
					+ DatasetExporterTest.VAR_POST_FIX, selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = dut.getTableItems();
		Map<String, String> headerAliasMap = dut.getHeaderNameAliasMap();

		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME
				+ DatasetExporterTest.CLEANED_VAR_POST_FIX, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME
				+ DatasetExporterTest.CLEANED_VAR_POST_FIX, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.VARIATE_NAME_1
				+ DatasetExporterTest.CLEANED_VAR_POST_FIX, DatasetExporterTest.VARIATE_NAME_1 + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				tableItems.get(0)[1]);
		Assert.assertEquals("Expected 1st column value  is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		Assert.assertEquals("Expected 2nd column value  is " + DatasetExporterTest.VARIATE_VALUE_1, DatasetExporterTest.VARIATE_VALUE_1,
				tableItems.get(1)[1]);

		Assert.assertEquals("Header name should be " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX,
				headerAliasMap.get(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.CLEANED_VAR_POST_FIX));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_1 + DatasetExporterTest.VAR_POST_FIX,
				DatasetExporterTest.VARIATE_NAME_1 + DatasetExporterTest.VAR_POST_FIX,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_1 + DatasetExporterTest.CLEANED_VAR_POST_FIX));

	}

	@Test
	public void testExportToCSVForBreedingView_TheSelectedFactorIsNotTrialInstance() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		List<SeaEnvironmentModel> selectedEnvironmentsList = new ArrayList<SeaEnvironmentModel>();
		SeaEnvironmentModel envModel = new SeaEnvironmentModel();
		envModel.setLocationId(1);
		envModel.setEnvironmentName(DatasetExporterTest.ENV_VALUE);
		selectedEnvironmentsList.add(envModel);

		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		this.createFactorsAndVariatesTestData(factors, factorVariables, variates, variateVariables, false);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		this.experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(this.experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		// change the trial instance for testing
		factors.get(0).setLocalName(DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(
					experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);
			Mockito.when(this.bvInput.getSelectedEnvironments()).thenReturn(selectedEnvironmentsList);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);
		DatasetExporter dut = Mockito.spy(exporter);

		dut.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			dut.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.ENV_NAME, selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = dut.getTableItems();
		Map<String, String> headerAliasMap = dut.getHeaderNameAliasMap();

		// header
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				tableItems.get(0)[1]);
		Assert.assertEquals("Expected 3rd column header is " + DatasetExporterTest.VARIATE_NAME_2, DatasetExporterTest.VARIATE_NAME_2,
				tableItems.get(0)[2]);
		Assert.assertEquals("Expected 4th column header is " + DatasetExporterTest.ENV_NAME, DatasetExporterTest.ENV_NAME,
				tableItems.get(0)[3]);
		Assert.assertFalse(DatasetExporterTest.VARIATE_NAME_3 + " should not be included",
				ArrayUtils.contains(tableItems.get(0), DatasetExporterTest.VARIATE_NAME_3));

		// data
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		Assert.assertEquals("Expected 2nd column value is " + DatasetExporterTest.VARIATE_VALUE_1, DatasetExporterTest.VARIATE_VALUE_1,
				tableItems.get(1)[1]);
		Assert.assertEquals("Expected 3rd column value is " + DatasetExporterTest.VARIATE_VALUE_2, DatasetExporterTest.VARIATE_VALUE_2,
				tableItems.get(1)[2]);
		Assert.assertEquals("Expected 4th column value is " + DatasetExporterTest.ENV_VALUE, DatasetExporterTest.ENV_VALUE,
				tableItems.get(1)[3]);

		Assert.assertEquals("Header name should be " + DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_1));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_2, DatasetExporterTest.VARIATE_NAME_2,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_2));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.ENV_NAME, DatasetExporterTest.ENV_NAME,
				headerAliasMap.get(DatasetExporterTest.ENV_NAME));
		Assert.assertNull(DatasetExporterTest.VARIATE_NAME_3 + " should not be included",
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_3));

	}

	private StandardVariable createStardardVariableTestData(PhenotypicType type, String name, Term dataType, Term storedIn) {

		StandardVariable stdVar = new StandardVariable();
		stdVar.setPhenotypicType(type);
		stdVar.setName(name);
		stdVar.setDataType(dataType);
		stdVar.setStoredIn(storedIn);

		if (storedIn == DatasetExporterTest.CATEGORICAL_VARIATE_ROLE) {
			List<Enumeration> validValues = new ArrayList<Enumeration>();
			validValues.add(new Enumeration(DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_ID,
					DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_NAME, DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_DESCRIPTION, 1));
			stdVar.setEnumerations(validValues);
		}
		return stdVar;
	}

	@Test
	public void testExportToCSVForBreedingView_WithNumericCategoricalVariate() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		this.createFactorsAndVariatesTestData(factors, factorVariables, variates, variateVariables, false);

		this.addVariableToList(variates, variateVariables, DatasetExporterTest.CATEGORICAL_VARIATE_NAME, 4,
				DatasetExporterTest.CATEGORICAL_VARIATE_VALUE, PhenotypicType.VARIATE, DatasetExporterTest.CATEGORICAL_VARIABLE,
				DatasetExporterTest.CATEGORICAL_VARIATE_ROLE);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		this.experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(this.experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt())).thenReturn(
					experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);
		DatasetExporter dut = Mockito.spy(exporter);

		dut.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			dut.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = dut.getTableItems();
		Map<String, String> headerAliasMap = dut.getHeaderNameAliasMap();

		// header
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				tableItems.get(0)[1]);
		Assert.assertEquals("Expected 3rd column header is " + DatasetExporterTest.VARIATE_NAME_2, DatasetExporterTest.VARIATE_NAME_2,
				tableItems.get(0)[2]);
		Assert.assertFalse(DatasetExporterTest.VARIATE_NAME_3 + " should not be included",
				ArrayUtils.contains(tableItems.get(0), DatasetExporterTest.VARIATE_NAME_3));
		Assert.assertEquals("Expected 4th column header is " + DatasetExporterTest.CATEGORICAL_VARIATE_NAME,
				DatasetExporterTest.CATEGORICAL_VARIATE_NAME, tableItems.get(0)[3]);

		// data
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		Assert.assertEquals("Expected 2nd column value is " + DatasetExporterTest.VARIATE_VALUE_1, DatasetExporterTest.VARIATE_VALUE_1,
				tableItems.get(1)[1]);
		Assert.assertEquals("Expected 3rd column value is " + DatasetExporterTest.VARIATE_VALUE_2, DatasetExporterTest.VARIATE_VALUE_2,
				tableItems.get(1)[2]);
		Assert.assertEquals("Expected 4th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				tableItems.get(1)[3]);

		Assert.assertEquals("Header name should be " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_1, DatasetExporterTest.VARIATE_NAME_1,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_1));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.VARIATE_NAME_2, DatasetExporterTest.VARIATE_NAME_2,
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_2));
		Assert.assertNull(DatasetExporterTest.VARIATE_NAME_3 + " should not be included",
				headerAliasMap.get(DatasetExporterTest.VARIATE_NAME_3));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.CATEGORICAL_VARIATE_NAME,
				DatasetExporterTest.CATEGORICAL_VARIATE_NAME, headerAliasMap.get(DatasetExporterTest.CATEGORICAL_VARIATE_NAME));

	}

}

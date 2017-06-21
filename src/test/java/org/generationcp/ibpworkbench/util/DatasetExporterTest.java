
package org.generationcp.ibpworkbench.util;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.*;
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
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
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
	private static final String EPP_VARIATE = "EPP";
	private static final String PH_VARIATE = "PH";
	private static final String CM_VARIATE = "CM";
	private static final String ALEUCOL_1_5_VARIATE = "Aleucol_1_5";

	private static final String DEFAULT_TRIAL_INSTANCE_VALUE_1 = "1";
	private static final String DEFAULT_TRIAL_INSTANCE_VALUE_2 = "2";
	private static final String EPP_VARIATE_VALUE = "76.223";
	private static final String PH_VARIATE_VALUE = "7.5";
	private static final String CM_VARIATE_VALUE = "1111.0";
	private static final String ALEUCOL_1_5_VARIATE_VALUE = "missing";
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

	private List<DMSVariableType> factors;
	private List<DMSVariableType> variates;
	private List<Variable> factorVariables;
	private List<Variable> variateVariables;

	@Before
	public void setUp() throws Exception {

		this.factors = this.createFactors(1);
		this.variates = this.createVariates(2);
		this.factorVariables = this.createFactorVariables(factors);
		this.variateVariables = this.createVariatesVariables(variates);

		Mockito.when(this.bvInput.getDesignType()).thenReturn(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
	}

	@Test
	public void testExportToCSVForBreedingViewWithNumericAndCategoricalVariates() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

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

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		// header
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				tableItems.get(0)[1]);
		Assert.assertEquals("Expected 3rd column header is " + DatasetExporterTest.PH_VARIATE, DatasetExporterTest.PH_VARIATE,
				tableItems.get(0)[2]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + " should not be included",
				ArrayUtils.contains(tableItems.get(0), DatasetExporterTest.CM_VARIATE));
		Assert.assertEquals("Expected 4th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE, tableItems.get(0)[3]);

		// data
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		Assert.assertEquals("Expected 2nd column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				tableItems.get(1)[1]);
		Assert.assertEquals("Expected 3rd column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				tableItems.get(1)[2]);
		Assert.assertEquals("Expected 4th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				tableItems.get(1)[3]);

	}

	@Test
	public void testExportToCSVForBreedingViewNoExistingRecordsForSelectedEnvironment() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_2);

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

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				tableItems.get(0)[1]);
		Assert.assertEquals("The CSV file should only have 1 row because it does not have a record", 1, tableItems.size());

		Assert.assertEquals("Header name should be " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				headerAliasMap.get(DatasetExporterTest.EPP_VARIATE));

	}

	@Test
	public void testExportToCSVForBreedingViewTraitNamesWithSpecialCharactersAndWhiteSpace() {


		this.appendSpecialCharactersToVariableName(this.factors);
		this.appendSpecialCharactersToVariableName(this.variates);

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

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

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME,
					DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX, selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME
				+ DatasetExporterTest.CLEANED_VAR_POST_FIX, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME
				+ DatasetExporterTest.CLEANED_VAR_POST_FIX, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.EPP_VARIATE
				+ DatasetExporterTest.CLEANED_VAR_POST_FIX, DatasetExporterTest.EPP_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				tableItems.get(0)[1]);
		Assert.assertEquals("Expected 1st column value  is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		Assert.assertEquals("Expected 2nd column value  is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				tableItems.get(1)[1]);

		Assert.assertEquals("Header name should be " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX,
				headerAliasMap.get(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.CLEANED_VAR_POST_FIX));
		Assert.assertEquals("Header name should be " + DatasetExporterTest.EPP_VARIATE + DatasetExporterTest.VAR_POST_FIX,
				DatasetExporterTest.EPP_VARIATE + DatasetExporterTest.VAR_POST_FIX,
				headerAliasMap.get(DatasetExporterTest.EPP_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX));

	}

	@Test
	public void testExportToCSVForBreedingViewTheSelectedFactorIsNotTrialInstance() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		List<SeaEnvironmentModel> selectedEnvironmentsList = new ArrayList<SeaEnvironmentModel>();
		SeaEnvironmentModel envModel = new SeaEnvironmentModel();
		envModel.setLocationId(1);
		envModel.setEnvironmentName(DatasetExporterTest.ENV_VALUE);
		selectedEnvironmentsList.add(envModel);

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

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.ENV_NAME, selectedEnvironments, this.bvInput);
		} catch (DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		// header
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		Assert.assertEquals("Expected 2nd column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				tableItems.get(0)[1]);
		Assert.assertEquals("Expected 3rd column header is " + DatasetExporterTest.PH_VARIATE, DatasetExporterTest.PH_VARIATE,
				tableItems.get(0)[2]);
		Assert.assertEquals("Expected 4th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE, DatasetExporterTest.ALEUCOL_1_5_VARIATE,
				tableItems.get(0)[3]);
		Assert.assertEquals("Expected 5th column header is " + DatasetExporterTest.ENV_NAME, DatasetExporterTest.ENV_NAME,
				tableItems.get(0)[4]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + " should not be included",
				ArrayUtils.contains(tableItems.get(0), DatasetExporterTest.CM_VARIATE));

		// data
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		Assert.assertEquals("Expected 2nd column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				tableItems.get(1)[1]);
		Assert.assertEquals("Expected 3rd column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				tableItems.get(1)[2]);
		Assert.assertEquals("Expected 4th column value is " + BV_MISSING_VALUE, BV_MISSING_VALUE,
				tableItems.get(1)[3]);
		Assert.assertEquals("Expected 5th column value is " + DatasetExporterTest.ENV_VALUE, DatasetExporterTest.ENV_VALUE,
				tableItems.get(1)[4]);

	}

	private DataSet createDatasetTestData(List<DMSVariableType> factors, List<DMSVariableType> variates) {
		DataSet dataSet = new DataSet();
		VariableTypeList variableTypes = new VariableTypeList();
		for (DMSVariableType variableType : factors) {
			variableTypes.add(variableType);
		}
		for (DMSVariableType variableType : variates) {
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

	private void setupSelectedVariables(HashMap<String, Boolean> variableState, List<DMSVariableType> variates) {
		for (DMSVariableType variableType : variates) {
			String localName = variableType.getLocalName();
			if (localName.startsWith(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME)
					|| localName.startsWith(DatasetExporterTest.EPP_VARIATE) || localName.startsWith(DatasetExporterTest.PH_VARIATE)
					|| localName.startsWith(DatasetExporterTest.ALEUCOL_1_5_VARIATE)) {
				variableState.put(localName, true);
			} else {
				variableState.put(localName, false);
			}
		}
	}

	private List<DMSVariableType> createFactors(int rank) {

		final List<DMSVariableType> factors = new ArrayList<>();

		final StandardVariable trialEnvironmentStandardVariable = this.createStardardVariableTestData(PhenotypicType.TRIAL_ENVIRONMENT, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.TRIAL_INSTANCE_ROLE);
		final DMSVariableType trialEnvironmentVariableType = this.createVariableTypeTestData(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, rank, trialEnvironmentStandardVariable);

		factors.add(trialEnvironmentVariableType);

		return factors;

	}

	private List<Variable> createFactorVariables(final List<DMSVariableType> factors) {

		List<Variable> variables = new ArrayList<>();

		variables.add(this.createVariableTestData(factors.get(0), DEFAULT_TRIAL_INSTANCE_VALUE_1));

		return variables;
	}


	private List<DMSVariableType> createVariates(int rank) {

		final List<DMSVariableType> variates = new ArrayList<>();

		final StandardVariable eppStandardVariable = this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.OBSERVATION_VARIATE_ROLE);
		final DMSVariableType eppVariableType = this.createVariableTypeTestData(DatasetExporterTest.EPP_VARIATE, rank++, eppStandardVariable);

		final StandardVariable phStandardVariable = this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.PH_VARIATE, DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.OBSERVATION_VARIATE_ROLE);
		final DMSVariableType phVariableType = this.createVariableTypeTestData(DatasetExporterTest.PH_VARIATE, rank++, phStandardVariable);

		final StandardVariable cmStandardVariable = this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.CM_VARIATE, DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.OBSERVATION_VARIATE_ROLE);
		final DMSVariableType cmVariableType = this.createVariableTypeTestData(DatasetExporterTest.CM_VARIATE, rank++, cmStandardVariable);

		final StandardVariable aleucol1to5Variable = this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.ALEUCOL_1_5_VARIATE, DatasetExporterTest.CATEGORICAL_VARIABLE, DatasetExporterTest.CATEGORICAL_VARIATE_ROLE);
		final DMSVariableType aleucol1to5VariableType = this.createVariableTypeTestData(DatasetExporterTest.ALEUCOL_1_5_VARIATE, rank++, aleucol1to5Variable);

		variates.add(eppVariableType);
		variates.add(phVariableType);
		variates.add(cmVariableType);
		variates.add(aleucol1to5VariableType);

		return variates;
	}

	private List<Variable> createVariatesVariables(final List<DMSVariableType> variates) {

		List<Variable> variables = new ArrayList<>();

		variables.add(this.createVariableTestData(variates.get(0), EPP_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(1), PH_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(2), CM_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(3), ALEUCOL_1_5_VARIATE_VALUE));

		return variables;

	}

	private void appendSpecialCharactersToVariableName(final List<DMSVariableType> variables) {

		for (DMSVariableType variable : variables) {
			variable.setLocalName(variable.getLocalName() + DatasetExporterTest.VAR_POST_FIX);
			variable.getStandardVariable().setName(variable.getStandardVariable().getName() + DatasetExporterTest.VAR_POST_FIX);
		}

	}

	private Variable createVariableTestData(DMSVariableType trial, String value) {
		Variable variable = new Variable();
		variable.setValue(value);
		variable.setVariableType(trial);
		return variable;
	}

	private DMSVariableType createVariableTypeTestData(String localName, int rank, StandardVariable standardVariable) {
		DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(localName);
		variableType.setRank(rank);
		variableType.setStandardVariable(standardVariable);
		variableType.setRole(standardVariable.getPhenotypicType());
		return variableType;
	}

	private StandardVariable createStardardVariableTestData(PhenotypicType type, String name, Term dataType, Term storedIn) {

		StandardVariable stdVar = new StandardVariable();
		stdVar.setPhenotypicType(type);
		stdVar.setName(name);
		stdVar.setDataType(dataType);
		
		if (dataType.getId() == DatasetExporterTest.CATEGORICAL_VARIABLE.getId()) {
			List<Enumeration> validValues = new ArrayList<Enumeration>();
			validValues.add(new Enumeration(DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_ID,
					DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_NAME, DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_DESCRIPTION, 1));
			stdVar.setEnumerations(validValues);
		}
		return stdVar;
	}

}

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
	private OntologyService ontologyService;

	private static final String FILENAME = "datasetExporterTest.csv";
	private static final String DEFAULT_TRIAL_INSTANCE_NAME = "SITE_NO";
	private static final String FIELDMAP_COLUMN = "FIELDMAP_COLUMN";
	private static final String FIELDMAP_RANGE = "FIELDMAP_RANGE";
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
	public static final String FIELDMAP_COLUMN_VALUE = "99";
	public static final String FIELDMAP_RANGE_VALUE = "1234";

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
		this.factorVariables = this.createFactorVariablesForExperiment(factors);
		this.variateVariables = this.createVariatesVariablesForExperiment(variates);

		Mockito.when(this.bvInput.getDesignType()).thenReturn(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
	}

	@Test
	public void testExportToCSVForBreedingViewWithNumericAndCategoricalVariates() {

		final List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		final HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		final List<Experiment> experiments = new ArrayList<Experiment>();
		final List<Variable> factorVariablesWithoutFieldmapVariables = this.createFactorVariablesTrialInstanceOnly(factors);

		final Experiment firstRowObservation = this.createExperimentTestData(factorVariables, variateVariables);
		final Experiment secondRowObservation = this.createExperimentTestData(factorVariablesWithoutFieldmapVariables, variateVariables);

		experiments.add(firstRowObservation);
		experiments.add(secondRowObservation);

		this.dataSet = this.createDatasetTestData(factors, variates);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
					.thenReturn(experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}

		final DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, this.bvInput);
		} catch (final DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		final List<String[]> tableItems = exporter.getTableItems();
		final Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		// Verify the header names
		final String[] headerRow = tableItems.get(0);
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, headerRow[0]);
		Assert.assertEquals("Expected 2st column header is " + DatasetExporterTest.FIELDMAP_COLUMN, DatasetExporterTest.FIELDMAP_COLUMN,
				headerRow[1]);
		Assert.assertEquals("Expected 3st column header is " + DatasetExporterTest.FIELDMAP_RANGE, DatasetExporterTest.FIELDMAP_RANGE,
				headerRow[2]);
		Assert.assertEquals("Expected 4nd column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				headerRow[3]);
		Assert.assertEquals("Expected 5rd column header is " + DatasetExporterTest.PH_VARIATE, DatasetExporterTest.PH_VARIATE,
				headerRow[4]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE));
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE, headerRow[5]);

		// Verify the values of the first observation row
		final String[] firstRow = tableItems.get(1);
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, firstRow[0]);
		Assert.assertEquals("Expected 2st column value is " + DatasetExporterTest.FIELDMAP_COLUMN_VALUE,
				DatasetExporterTest.FIELDMAP_COLUMN_VALUE, firstRow[1]);
		Assert.assertEquals("Expected 3st column value is " + DatasetExporterTest.FIELDMAP_RANGE_VALUE,
				DatasetExporterTest.FIELDMAP_RANGE_VALUE, firstRow[2]);
		Assert.assertEquals("Expected 4nd column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				firstRow[3]);
		Assert.assertEquals("Expected 5rd column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				firstRow[4]);
		Assert.assertEquals("Expected 6th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				firstRow[5]);

		// Verify the values of the second observation row
		final String[] secondRow = tableItems.get(2);
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, secondRow[0]);
		Assert.assertEquals("Expected 2st column value is blank", "", secondRow[1]);
		Assert.assertEquals("Expected 3st column value is blank", "", secondRow[2]);
		Assert.assertEquals("Expected 4nd column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				secondRow[3]);
		Assert.assertEquals("Expected 5rd column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				secondRow[4]);
		Assert.assertEquals("Expected 6th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				secondRow[5]);

	}

	@Test
	public void testExportToCSVForBreedingViewNoExistingRecordsForSelectedEnvironment() {

		final List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_2);

		final HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		final List<Experiment> experiments = new ArrayList<Experiment>();
		final Experiment experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
					.thenReturn(experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}

		final DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, this.bvInput);
		} catch (final DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		final List<String[]> tableItems = exporter.getTableItems();
		final Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		Assert.assertEquals("The CSV file should only have 1 row because it does not have a record", 1, tableItems.size());

		// Verify the header names
		final String[] headerRow = tableItems.get(0);
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, headerRow[0]);
		Assert.assertEquals("Expected 2st column header is " + DatasetExporterTest.FIELDMAP_COLUMN, DatasetExporterTest.FIELDMAP_COLUMN,
				headerRow[1]);
		Assert.assertEquals("Expected 3st column header is " + DatasetExporterTest.FIELDMAP_RANGE, DatasetExporterTest.FIELDMAP_RANGE,
				headerRow[2]);
		Assert.assertEquals("Expected 4nd column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				headerRow[3]);
		Assert.assertEquals("Expected 5rd column header is " + DatasetExporterTest.PH_VARIATE, DatasetExporterTest.PH_VARIATE,
				headerRow[4]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE));
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE, headerRow[5]);

	}

	@Test
	public void testExportToCSVForBreedingViewTraitNamesWithSpecialCharactersAndWhiteSpace() {

		this.appendSpecialCharactersToVariableName(this.factors);
		this.appendSpecialCharactersToVariableName(this.variates);

		final List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		final HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		final List<Experiment> experiments = new ArrayList<Experiment>();
		final Experiment experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
					.thenReturn(experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName())
					.thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}

		final DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME,
					DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX, selectedEnvironments, this.bvInput);
		} catch (final DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		final List<String[]> tableItems = exporter.getTableItems();
		final Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		// Verify the header names
		final String[] headerRow = tableItems.get(0);
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + CLEANED_VAR_POST_FIX, headerRow[0]);
		Assert.assertEquals("Expected 2st column header is " + DatasetExporterTest.FIELDMAP_COLUMN + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.FIELDMAP_COLUMN + CLEANED_VAR_POST_FIX, headerRow[1]);
		Assert.assertEquals("Expected 3st column header is " + DatasetExporterTest.FIELDMAP_RANGE + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.FIELDMAP_RANGE + CLEANED_VAR_POST_FIX, headerRow[2]);
		Assert.assertEquals("Expected 4nd column header is " + DatasetExporterTest.EPP_VARIATE + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.EPP_VARIATE + CLEANED_VAR_POST_FIX, headerRow[3]);
		Assert.assertEquals("Expected 5rd column header is " + DatasetExporterTest.PH_VARIATE + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.PH_VARIATE + CLEANED_VAR_POST_FIX, headerRow[4]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + CLEANED_VAR_POST_FIX + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE + CLEANED_VAR_POST_FIX));
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE + CLEANED_VAR_POST_FIX, headerRow[5]);

	}

	@Test
	public void testExportToCSVForBreedingViewTheSelectedFactorIsNotTrialInstance() {

		final List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1);

		final List<SeaEnvironmentModel> selectedEnvironmentsList = new ArrayList<SeaEnvironmentModel>();
		final SeaEnvironmentModel envModel = new SeaEnvironmentModel();
		envModel.setLocationId(1);
		envModel.setEnvironmentName(DatasetExporterTest.ENV_VALUE);
		selectedEnvironmentsList.add(envModel);

		final HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariables(variatesActiveState, variates);

		final List<Experiment> experiments = new ArrayList<Experiment>();
		final Experiment experiment = this.createExperimentTestData(factorVariables, variateVariables);
		experiments.add(experiment);

		this.dataSet = this.createDatasetTestData(factors, variates);

		// change the trial instance for testing
		factors.get(0).setLocalName(DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);

		try {
			Mockito.when(DatasetExporterTest.manager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
			Mockito.when(DatasetExporterTest.manager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
					.thenReturn(experiments);

			Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
			Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
			Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);
			Mockito.when(this.bvInput.getSelectedEnvironments()).thenReturn(selectedEnvironmentsList);

			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting()).thenReturn(Mockito.mock(WorkbenchSetting.class));
			Mockito.when(DatasetExporterTest.workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}

		final DatasetExporter exporter = new DatasetExporter(DatasetExporterTest.manager, this.ontologyService, 1, 1);

		exporter.setWorkbenchDataManager(DatasetExporterTest.workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.ENV_NAME, selectedEnvironments,
					this.bvInput);
		} catch (final DatasetExporterException e) {

			Assert.fail(e.getMessage());
		}

		final List<String[]> tableItems = exporter.getTableItems();
		final Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		// Verify the header names
		final String[] headerRow = tableItems.get(0);
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME,
				DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME, headerRow[0]);
		Assert.assertEquals("Expected 2st column header is " + DatasetExporterTest.FIELDMAP_COLUMN, DatasetExporterTest.FIELDMAP_COLUMN,
				headerRow[1]);
		Assert.assertEquals("Expected 3st column header is " + DatasetExporterTest.FIELDMAP_RANGE, DatasetExporterTest.FIELDMAP_RANGE,
				headerRow[2]);
		Assert.assertEquals("Expected 4nd column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				headerRow[3]);
		Assert.assertEquals("Expected 5rd column header is " + DatasetExporterTest.PH_VARIATE, DatasetExporterTest.PH_VARIATE,
				headerRow[4]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE));
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE, headerRow[5]);
		Assert.assertEquals("Expected 7th column header is " + DatasetExporterTest.ENV_NAME, DatasetExporterTest.ENV_NAME, headerRow[6]);

		// Verify the values of the first observation row
		final String[] firstRow = tableItems.get(1);
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_VALUE_1, firstRow[0]);
		Assert.assertEquals("Expected 2st column value is " + DatasetExporterTest.FIELDMAP_COLUMN_VALUE,
				DatasetExporterTest.FIELDMAP_COLUMN_VALUE, firstRow[1]);
		Assert.assertEquals("Expected 3st column value is " + DatasetExporterTest.FIELDMAP_RANGE_VALUE,
				DatasetExporterTest.FIELDMAP_RANGE_VALUE, firstRow[2]);
		Assert.assertEquals("Expected 4nd column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				firstRow[3]);
		Assert.assertEquals("Expected 5rd column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				firstRow[4]);
		Assert.assertEquals("Expected 6th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				firstRow[5]);
		Assert.assertEquals("Expected 7th column value is " + DatasetExporterTest.ENV_VALUE, DatasetExporterTest.ENV_VALUE, firstRow[6]);

	}

	private DataSet createDatasetTestData(final List<DMSVariableType> factors, final List<DMSVariableType> variates) {
		final DataSet dataSet = new DataSet();
		final VariableTypeList variableTypes = new VariableTypeList();
		for (final DMSVariableType variableType : factors) {
			variableTypes.add(variableType);
		}
		for (final DMSVariableType variableType : variates) {
			variableTypes.add(variableType);
		}
		dataSet.setVariableTypes(variableTypes);
		return dataSet;
	}

	private Experiment createExperimentTestData(final List<Variable> factorVariables, final List<Variable> variateVariables) {
		final Experiment experiment = new Experiment();
		final VariableList factorVariableList = this.createVariableListTestData(factorVariables);
		final VariableList variateVariableList = this.createVariableListTestData(variateVariables);
		experiment.setFactors(factorVariableList);
		experiment.setVariates(variateVariableList);
		experiment.setLocationId(1);
		return experiment;
	}

	private VariableList createVariableListTestData(final List<Variable> variables) {
		final VariableList variableList = new VariableList();
		variableList.setVariables(variables);
		return variableList;
	}

	private void setupSelectedVariables(final HashMap<String, Boolean> variableState, final List<DMSVariableType> variates) {
		for (final DMSVariableType variableType : variates) {
			final String localName = variableType.getLocalName();
			if (localName.startsWith(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME) || localName
					.startsWith(DatasetExporterTest.EPP_VARIATE) || localName.startsWith(DatasetExporterTest.PH_VARIATE) || localName
					.startsWith(DatasetExporterTest.ALEUCOL_1_5_VARIATE)) {
				variableState.put(localName, true);
			} else {
				variableState.put(localName, false);
			}
		}
	}

	private List<DMSVariableType> createFactors(int rank) {

		final List<DMSVariableType> factors = new ArrayList<>();

		final StandardVariable trialEnvironmentStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.TRIAL_ENVIRONMENT, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
						DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.TRIAL_INSTANCE_ROLE);
		final DMSVariableType trialEnvironmentVariableType =
				this.createVariableTypeTestData(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, rank++, trialEnvironmentStandardVariable);

		final StandardVariable fieldmapColumnStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.TRIAL_ENVIRONMENT, DatasetExporterTest.FIELDMAP_COLUMN,
						DatasetExporterTest.NUMERIC_VARIABLE, null);
		final DMSVariableType fieldmapColumnVariableType =
				this.createVariableTypeTestData(DatasetExporterTest.FIELDMAP_COLUMN, rank++, fieldmapColumnStandardVariable);

		final StandardVariable fieldmapRangeStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.TRIAL_ENVIRONMENT, DatasetExporterTest.FIELDMAP_RANGE,
						DatasetExporterTest.NUMERIC_VARIABLE, null);
		final DMSVariableType fieldmapRangeVariableType =
				this.createVariableTypeTestData(DatasetExporterTest.FIELDMAP_RANGE, rank++, fieldmapRangeStandardVariable);

		factors.add(trialEnvironmentVariableType);
		factors.add(fieldmapColumnVariableType);
		factors.add(fieldmapRangeVariableType);

		return factors;

	}

	private List<Variable> createFactorVariablesForExperiment(final List<DMSVariableType> factors) {

		final List<Variable> variables = new ArrayList<>();

		variables.add(this.createVariableTestData(factors.get(0), DEFAULT_TRIAL_INSTANCE_VALUE_1));
		variables.add(this.createVariableTestData(factors.get(1), FIELDMAP_COLUMN_VALUE));
		variables.add(this.createVariableTestData(factors.get(2), FIELDMAP_RANGE_VALUE));

		return variables;
	}

	private List<Variable> createFactorVariablesTrialInstanceOnly(final List<DMSVariableType> factors) {

		final List<Variable> variables = new ArrayList<>();

		variables.add(this.createVariableTestData(factors.get(0), DEFAULT_TRIAL_INSTANCE_VALUE_1));

		return variables;
	}

	private List<DMSVariableType> createVariates(int rank) {

		final List<DMSVariableType> variates = new ArrayList<>();

		final StandardVariable eppStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.EPP_VARIATE,
						DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.OBSERVATION_VARIATE_ROLE);
		final DMSVariableType eppVariableType =
				this.createVariableTypeTestData(DatasetExporterTest.EPP_VARIATE, rank++, eppStandardVariable);

		final StandardVariable phStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.PH_VARIATE,
						DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.OBSERVATION_VARIATE_ROLE);
		final DMSVariableType phVariableType = this.createVariableTypeTestData(DatasetExporterTest.PH_VARIATE, rank++, phStandardVariable);

		final StandardVariable cmStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.CM_VARIATE,
						DatasetExporterTest.NUMERIC_VARIABLE, DatasetExporterTest.OBSERVATION_VARIATE_ROLE);
		final DMSVariableType cmVariableType = this.createVariableTypeTestData(DatasetExporterTest.CM_VARIATE, rank++, cmStandardVariable);

		final StandardVariable aleucol1to5Variable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.ALEUCOL_1_5_VARIATE,
						DatasetExporterTest.CATEGORICAL_VARIABLE, DatasetExporterTest.CATEGORICAL_VARIATE_ROLE);
		final DMSVariableType aleucol1to5VariableType =
				this.createVariableTypeTestData(DatasetExporterTest.ALEUCOL_1_5_VARIATE, rank++, aleucol1to5Variable);

		variates.add(eppVariableType);
		variates.add(phVariableType);
		variates.add(cmVariableType);
		variates.add(aleucol1to5VariableType);

		return variates;
	}

	private List<Variable> createVariatesVariablesForExperiment(final List<DMSVariableType> variates) {

		final List<Variable> variables = new ArrayList<>();

		variables.add(this.createVariableTestData(variates.get(0), EPP_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(1), PH_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(2), CM_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(3), ALEUCOL_1_5_VARIATE_VALUE));

		return variables;

	}

	private void appendSpecialCharactersToVariableName(final List<DMSVariableType> variables) {

		for (final DMSVariableType variable : variables) {
			variable.setLocalName(variable.getLocalName() + DatasetExporterTest.VAR_POST_FIX);
			variable.getStandardVariable().setName(variable.getStandardVariable().getName() + DatasetExporterTest.VAR_POST_FIX);
		}

	}

	private Variable createVariableTestData(final DMSVariableType trial, final String value) {
		final Variable variable = new Variable();
		variable.setValue(value);
		variable.setVariableType(trial);
		return variable;
	}

	private DMSVariableType createVariableTypeTestData(final String localName, final int rank, final StandardVariable standardVariable) {
		final DMSVariableType variableType = new DMSVariableType();
		variableType.setLocalName(localName);
		variableType.setRank(rank);
		variableType.setStandardVariable(standardVariable);
		variableType.setRole(standardVariable.getPhenotypicType());
		return variableType;
	}

	private StandardVariable createStardardVariableTestData(final PhenotypicType type, final String name, final Term dataType,
			final Term storedIn) {

		final StandardVariable stdVar = new StandardVariable();
		stdVar.setPhenotypicType(type);
		stdVar.setName(name);
		stdVar.setDataType(dataType);

		if (dataType.getId() == DatasetExporterTest.CATEGORICAL_VARIABLE.getId()) {
			final List<Enumeration> validValues = new ArrayList<Enumeration>();
			validValues
					.add(new Enumeration(DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_ID, DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_NAME,
							DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_DESCRIPTION, 1));
			stdVar.setEnumerations(validValues);
		}
		return stdVar;
	}

}

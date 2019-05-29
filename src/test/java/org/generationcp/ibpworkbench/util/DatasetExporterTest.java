package org.generationcp.ibpworkbench.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.OntologyService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DatasetExporterTest {

	public static final int DATASET_ID = 1;

	@Mock
	private static StudyDataManager studyDataManager;

	@Mock
	private BreedingViewInput bvInput;

	@Mock
	private DataSet dataSet;

	@Mock
	private OntologyService ontologyService;

	@InjectMocks
	private final DatasetExporter exporter = new DatasetExporter();

	private static final String FILENAME = "datasetExporterTest.csv";
	private static final String DEFAULT_TRIAL_INSTANCE_NAME = "SITE_NO";
	private static final String ALT_TRIAL_INSTANCE_NAME = "TRIAL";
	private static final String ENV_NAME = "SITE_NAME";
	private static final String ENV_VALUE = "CIMMYT Harrare";
	private static final String EPP_VARIATE = "EPP";
	private static final String PH_VARIATE = "PH";
	private static final String CM_VARIATE = "CM";
	private static final String ALEUCOL_1_5_VARIATE = "Aleucol_1_5";

	private static final String TRIAL_INSTANCE_1 = "1";
	private static final String TRIAL_INSTANCE_2 = "2";
	private static final String EPP_VARIATE_VALUE = "76.223";
	private static final String PH_VARIATE_VALUE = "7.5";
	private static final String CM_VARIATE_VALUE = "1111.0";
	private static final String ALEUCOL_1_5_VARIATE_VALUE = "missing";
	private static final int CATEGORICAL_VARIATE_ENUM_ID = 1;
	private static final String CATEGORICAL_VARIATE_ENUM_NAME = "5";
	private static final String CATEGORICAL_VARIATE_ENUM_DESCRIPTION = "Very Severe";
	private static final String FIELDMAP_COLUMN_VALUE = "99";
	private static final String FIELDMAP_RANGE_VALUE = "1234";
	private static final String ENTRY_TYPE = "1";

	private static final Term NUMERIC_VARIABLE = new Term(TermId.NUMERIC_VARIABLE.getId(), "", "");
	private static final Term CATEGORICAL_VARIABLE = new Term(TermId.CATEGORICAL_VARIABLE.getId(), "", "");
	private static final Term CHARACTER_VARIABLE = new Term(TermId.CHARACTER_VARIABLE.getId(), "", "");

	private static final String VAR_POST_FIX = "%#! @";
	private static final String CLEANED_VAR_POST_FIX = "%_";

	private static final String BV_MISSING_VALUE = "";
	private static final int INDEX_OF_TRIAL_INSTANCE_FACTOR = 2;

	private List<DMSVariableType> factors;
	private List<DMSVariableType> variates;
	private List<Variable> factorVariables;
	private List<Variable> variateVariables;

	@Before
	public void setUp() throws Exception {
		this.exporter.setDatasetId(DatasetExporterTest.DATASET_ID);
		this.factors = this.createFactors(1);
		this.variates = this.createVariates(2);
		this.factorVariables = this.createFactorVariablesForExperiment(this.factors);
		this.variateVariables = this.createVariatesVariablesForExperiment(this.variates);

		this.dataSet = this.createDatasetTestData(this.factors, this.variates);
		this.setupMocks();
	}

	@Test
	public void testGenerateVariateColumnsList() {

		// Method to test
		final List<String> variateColumnsToWrite = this.exporter.generateVariateColumnsList(this.dataSet, this.bvInput);

		// Only selected variates will be included in variate columns
		Assert.assertEquals(3, variateColumnsToWrite.size());
		Assert.assertTrue(variateColumnsToWrite.contains(PH_VARIATE));
		Assert.assertTrue(variateColumnsToWrite.contains(EPP_VARIATE));
		Assert.assertTrue(variateColumnsToWrite.contains(ALEUCOL_1_5_VARIATE));

	}

	@Test
	public void testGenerateFactorColumnsList() {
		final int numberOfFactorsInDataset = this.factors.size();
		Assert.assertEquals("Expecting dataset has " + numberOfFactorsInDataset + " factors.", numberOfFactorsInDataset,
				this.dataSet.getVariableTypes().getFactors().size());

		final List<String> factorColumnsToWrite = this.exporter.generateFactorColumnsList(this.dataSet);

		// Check that dataset and study variable types are not included in factor columns to write
		Assert.assertEquals("Expecting only selected variates will be included in factor columns.", numberOfFactorsInDataset - 2,
				factorColumnsToWrite.size());
		Assert.assertFalse("Not expecting " + TermId.DATASET_NAME.name() + " to be included in factor columns but was included.",
				factorColumnsToWrite.contains(TermId.DATASET_NAME.name()));
		Assert.assertFalse("Not expecting " + "STUDY_NAME" + " to be included in factor columns but was included.",
				factorColumnsToWrite.contains("STUDY_NAME"));
	}

	@Test
	public void testExperimentIsInSelectedEnvironments() {
		boolean isInSelectedEnvironments = this.exporter
				.isExperimentInSelectedEnvironments(this.bvInput, Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_1),
						this.createExperimentTestData(this.factorVariables, this.variateVariables));
		Assert.assertTrue("Expecting experiment to be detected as part of selected Trial Instance 1", isInSelectedEnvironments);

		isInSelectedEnvironments = this.exporter
				.isExperimentInSelectedEnvironments(this.bvInput, Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_2),
						this.createExperimentTestData(this.factorVariables, this.variateVariables));
		Assert.assertFalse("Expecting experiment not to be detected as part of selected Trial Instance 2", isInSelectedEnvironments);
	}

	@Test
	public void testIsDummyRepVariableUsedWhenDummyRepVariablePresent() {
		Mockito.when(this.bvInput.getReplicatesFactorName()).thenReturn(DatasetExporter.DUMMY_REPLICATES);

		boolean isDummyRepUsed = this.exporter.isDummyRepVariableUsed(this.bvInput);
		Assert.assertTrue(
				"Expecting to return true since dummy replicates factor was used and design type is " + this.bvInput.getDesignType(),
				isDummyRepUsed);

		Mockito.when(this.bvInput.getDesignType()).thenReturn(ExperimentDesignType.P_REP.getBvDesignName());
		isDummyRepUsed = this.exporter.isDummyRepVariableUsed(this.bvInput);
		Assert.assertFalse(
				"Expecting to return false because even though dummy replicates factor was used, design type is " + ExperimentDesignType.P_REP
						.getBvDesignName(), isDummyRepUsed);

		Mockito.when(this.bvInput.getDesignType()).thenReturn(ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvDesignName());
		isDummyRepUsed = this.exporter.isDummyRepVariableUsed(this.bvInput);
		Assert.assertFalse("Expecting to return false because even though dummy replicates factor was used, design type is "
				+ ExperimentDesignType.AUGMENTED_RANDOMIZED_BLOCK.getBvDesignName(), isDummyRepUsed);
	}

	@Test
	public void testExportToCSVForBreedingViewWithNumericAndCategoricalVariates() {
		final Experiment firstRowObservation = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		final Experiment secondRowObservation = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		Mockito.when(DatasetExporterTest.studyDataManager.getExperiments(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
				.thenReturn(Lists.newArrayList(firstRowObservation, secondRowObservation));

		// Method to test
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_1), this.bvInput);
		final List<String[]> rowsToWrite = this.exporter.getRowsToWrite();
		Assert.assertEquals("Expecting 3 rows for CSV file", 3, rowsToWrite.size());

		// Verify the header names
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);

		// Verify the values of the observation rows
		this.verifyRowValues(rowsToWrite.get(1));
		this.verifyRowValues(rowsToWrite.get(2));
	}

	@Test
	public void testExportToCSVForBreedingViewExperimentsDontHaveSomeDatasetFactors() {
		// 2nd row experiment doesn't have FIELDMAP_COLUMN and FIELMAP_RANGE variables which are present in dataset/column headers
		final List<Variable> factorVariablesWithoutFieldmapVariables = this.createFactorVariablesWithoutFieldmapVariables(this.factors);
		final Experiment firstRowObservation = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		final Experiment secondRowObservation =
				this.createExperimentTestData(factorVariablesWithoutFieldmapVariables, this.variateVariables);
		Mockito.when(DatasetExporterTest.studyDataManager.getExperiments(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
				.thenReturn(Lists.newArrayList(firstRowObservation, secondRowObservation));

		// Method to test
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_1), this.bvInput);
		final List<String[]> rowsToWrite = this.exporter.getRowsToWrite();
		Assert.assertEquals("Expecting 3 rows for CSV file", 3, rowsToWrite.size());

		// Verify the header names
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);

		// Verify the values of the first observation row
		final String[] firstRow = rowsToWrite.get(1);
		this.verifyRowValues(firstRow);

		// Verify the values of the second observation row which don't have FIELDMAP columns
		final String[] secondRow = rowsToWrite.get(2);
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.TRIAL_INSTANCE_1, DatasetExporterTest.TRIAL_INSTANCE_1,
				secondRow[0]);
		// Expecting blank values for FIELMAP columns
		Assert.assertEquals("Expected 2nd column value is blank", "", secondRow[1]);
		Assert.assertEquals("Expected 3rd column value is blank", "", secondRow[2]);
		Assert.assertEquals("Expected 4th column value is " + DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_NAME, DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_NAME,
				secondRow[3]);
		Assert.assertEquals("Expected 5th column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				secondRow[4]);
		Assert.assertEquals("Expected 6th column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				secondRow[5]);
		Assert.assertEquals("Expected 7th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				secondRow[6]);

	}

	@Test
	public void testExportToCSVForBreedingViewNoExperimentsForSelectedEnvironment() {
		// Sselected TRIAL_INSTANCE is 2 but there are no experiments found for it
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_2), this.bvInput);
		final List<String[]> rowsToWrite = this.exporter.getRowsToWrite();

		Assert.assertEquals("The CSV file should only have 1 row because it does not have any experiment for selected environment.", 1,
				rowsToWrite.size());

		// Verify the header names on 1st column
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);
	}

	@Test
	public void testExportToCSVForBreedingViewTraitNamesWithSpecialCharactersAndWhiteSpace() {
		this.appendSpecialCharactersToVariableNames(this.factors);
		this.appendSpecialCharactersToVariableNames(this.variates);

		final HashMap<String, Boolean> traitsSelectionMap = new HashMap<String, Boolean>();
		traitsSelectionMap.put(EPP_VARIATE + DatasetExporterTest.VAR_POST_FIX, true);
		traitsSelectionMap.put(PH_VARIATE + DatasetExporterTest.VAR_POST_FIX, true);
		traitsSelectionMap.put(ALEUCOL_1_5_VARIATE + DatasetExporterTest.VAR_POST_FIX, false);
		traitsSelectionMap.put(CM_VARIATE + DatasetExporterTest.VAR_POST_FIX, false);

		final HashMap<String, Boolean> covariatesSelectionMap = new HashMap<String, Boolean>();
		covariatesSelectionMap.put(EPP_VARIATE + DatasetExporterTest.VAR_POST_FIX, false);
		covariatesSelectionMap.put(PH_VARIATE + DatasetExporterTest.VAR_POST_FIX, false);
		covariatesSelectionMap.put(ALEUCOL_1_5_VARIATE + DatasetExporterTest.VAR_POST_FIX, true);
		covariatesSelectionMap.put(CM_VARIATE + DatasetExporterTest.VAR_POST_FIX, false);

		Mockito.when(this.bvInput.getVariatesSelectionMap()).thenReturn(traitsSelectionMap);
		Mockito.when(this.bvInput.getCovariatesSelectionMap()).thenReturn(covariatesSelectionMap);

		// Method to test
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_1), this.bvInput);
		final List<String[]> rowsToWrite = this.exporter.getRowsToWrite();

		// Verify the header names were "cleaned up"
		final String[] headerRow = rowsToWrite.get(0);
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME
						+ DatasetExporterTest.CLEANED_VAR_POST_FIX,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.CLEANED_VAR_POST_FIX, headerRow[0]);
		Assert.assertEquals("Expected 2nd column header is " + TermId.FIELDMAP_COLUMN.name() + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				TermId.FIELDMAP_COLUMN.name() + DatasetExporterTest.CLEANED_VAR_POST_FIX, headerRow[1]);
		Assert.assertEquals("Expected 3rd column header is " + TermId.FIELDMAP_RANGE.name() + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				TermId.FIELDMAP_RANGE.name() + DatasetExporterTest.CLEANED_VAR_POST_FIX, headerRow[2]);
		Assert.assertEquals("Expected 4th column header is " + TermId.ENTRY_TYPE.name() + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				TermId.ENTRY_TYPE.name()  + DatasetExporterTest.CLEANED_VAR_POST_FIX, headerRow[3]);
		Assert.assertEquals("Expected 5th column header is " + DatasetExporterTest.EPP_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				DatasetExporterTest.EPP_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX, headerRow[4]);
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.PH_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				DatasetExporterTest.PH_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX, headerRow[5]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX));
		Assert.assertEquals(
				"Expected 7th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE + DatasetExporterTest.CLEANED_VAR_POST_FIX, headerRow[6]);

	}

	@Test
	public void testSanitizeColumnNames() {
		final List<String> columnNames = Arrays.asList("TRIAL_INSTANCE**", "ENTRY_TYPE**", "GID", "DESIGNATION", "ENTRY_NO", "OBS_UNIT_ID",	"REP_NO", "PLOT_NO", "GW_DW_g1000grn");
		final String[] sanitizedColumnNames = this.exporter.sanitizeColumnNames(columnNames);
		for(int i=0; i<sanitizedColumnNames.length; i++) {
			Assert.assertEquals(BreedingViewUtil.trimAndSanitizeName(columnNames.get(i)), sanitizedColumnNames[i]);
		}
	}

	@Test
	public void testExportToCSVForBreedingViewTheSelectedFactorIsNotTrialInstance() {
		// Setup selected environments
		final List<SeaEnvironmentModel> selectedEnvironmentsList = new ArrayList<SeaEnvironmentModel>();
		final SeaEnvironmentModel envModel = new SeaEnvironmentModel();
		envModel.setLocationId(1);
		envModel.setEnvironmentName(DatasetExporterTest.ENV_VALUE);
		selectedEnvironmentsList.add(envModel);
		Mockito.when(this.bvInput.getSelectedEnvironments()).thenReturn(selectedEnvironmentsList);

		// Change the local name for TRIAL_INSTANCE variable for testing
		this.factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR).setLocalName(DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);
		Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);

		// Method to test
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.ENV_NAME,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_1), this.bvInput);
		final List<String[]> rowsToWrite = this.exporter.getRowsToWrite();
		Assert.assertEquals("Expecting 2 rows for CSV file", 2, rowsToWrite.size());

		// Verify the header names
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);
		Assert.assertEquals("Expected 8th column header is " + DatasetExporterTest.ENV_NAME, DatasetExporterTest.ENV_NAME, headerRow[7]);

		// Verify the values of the first observation row
		final String[] firstRow = rowsToWrite.get(1);
		this.verifyRowValues(firstRow);
		Assert.assertEquals("Expected 8th column value is " + DatasetExporterTest.ENV_VALUE, DatasetExporterTest.ENV_VALUE, firstRow[7]);
	}

	@Test
	public void testExportToCSVForBreedingViewDummyReplicateFactorUsed() {
		Mockito.when(this.bvInput.getReplicatesFactorName()).thenReturn(DatasetExporter.DUMMY_REPLICATES);

		// Method to test
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_1), this.bvInput);
		final List<String[]> rowsToWrite = this.exporter.getRowsToWrite();
		Assert.assertEquals("Expecting 2 rows for CSV file", 2, rowsToWrite.size());

		// Verify the header names
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);
		Assert.assertEquals("Expected 8th column header is " + DatasetExporter.DUMMY_REPLICATES, DatasetExporter.DUMMY_REPLICATES,
				headerRow[7]);

		// Verify the values of the first observation row
		final String[] firstRow = rowsToWrite.get(1);
		this.verifyRowValues(firstRow);
		Assert.assertEquals("Expected 8th column value is 1 for column " + DatasetExporter.DUMMY_REPLICATES, "1", firstRow[7]);
	}

	private void verifyRowValues(final String[] firstRow) {
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.TRIAL_INSTANCE_1, DatasetExporterTest.TRIAL_INSTANCE_1,
				firstRow[0]);
		Assert.assertEquals("Expected 2nd column value is " + DatasetExporterTest.FIELDMAP_COLUMN_VALUE,
				DatasetExporterTest.FIELDMAP_COLUMN_VALUE, firstRow[1]);
		Assert.assertEquals("Expected 3rd column value is " + DatasetExporterTest.FIELDMAP_RANGE_VALUE,
				DatasetExporterTest.FIELDMAP_RANGE_VALUE, firstRow[2]);
		Assert.assertEquals("Expected 4th column value is " + DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_NAME, DatasetExporterTest.CATEGORICAL_VARIATE_ENUM_NAME,
				firstRow[3]);
		Assert.assertEquals("Expected 5th column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				firstRow[4]);
		Assert.assertEquals("Expected 6th column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				firstRow[5]);
		Assert.assertEquals("Expected 7th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				firstRow[6]);
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

	private List<DMSVariableType> createFactors(int rank) {
		final StandardVariable datasetNameStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.DATASET, TermId.DATASET_NAME.name(),
						DatasetExporterTest.CHARACTER_VARIABLE);
		final DMSVariableType datasetNameVariableType =
				this.createVariableTypeTestData(TermId.DATASET_NAME.name(), rank++, datasetNameStandardVariable);

		final StandardVariable studyNameStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.STUDY, "STUDY_NAME", DatasetExporterTest.CHARACTER_VARIABLE);
		final DMSVariableType studyNameVariableType = this.createVariableTypeTestData("STUDY_NAME", rank++, studyNameStandardVariable);

		final StandardVariable trialEnvironmentStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.TRIAL_ENVIRONMENT, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
						DatasetExporterTest.NUMERIC_VARIABLE);
		final DMSVariableType trialEnvironmentVariableType =
				this.createVariableTypeTestData(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME, rank++, trialEnvironmentStandardVariable);

		final StandardVariable fieldmapColumnStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.TRIAL_ENVIRONMENT, TermId.FIELDMAP_COLUMN.name(),
						DatasetExporterTest.NUMERIC_VARIABLE);
		final DMSVariableType fieldmapColumnVariableType =
				this.createVariableTypeTestData(TermId.FIELDMAP_COLUMN.name(), rank++, fieldmapColumnStandardVariable);

		final StandardVariable fieldmapRangeStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.TRIAL_ENVIRONMENT, TermId.FIELDMAP_RANGE.name(),
						DatasetExporterTest.NUMERIC_VARIABLE);
		final DMSVariableType fieldmapRangeVariableType =
				this.createVariableTypeTestData(TermId.FIELDMAP_RANGE.name(), rank++, fieldmapRangeStandardVariable);

		final StandardVariable entryTypeStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.GERMPLASM, TermId.ENTRY_TYPE.name(),
						DatasetExporterTest.CATEGORICAL_VARIABLE);
		final DMSVariableType entryTypeVariableType =
				this.createVariableTypeTestData(TermId.ENTRY_TYPE.name(), rank++, entryTypeStandardVariable);

		return Lists.newArrayList(datasetNameVariableType, studyNameVariableType, trialEnvironmentVariableType, fieldmapColumnVariableType,
				fieldmapRangeVariableType, entryTypeVariableType);
	}

	private List<Variable> createFactorVariablesForExperiment(final List<DMSVariableType> factors) {
		final List<Variable> variables = this.createFactorVariablesWithoutFieldmapVariables(factors);
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR + 1),
				DatasetExporterTest.FIELDMAP_COLUMN_VALUE));
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR + 2),
				DatasetExporterTest.FIELDMAP_RANGE_VALUE));
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR + 3),
				DatasetExporterTest.ENTRY_TYPE));
		return variables;
	}

	private List<Variable> createFactorVariablesWithoutFieldmapVariables(final List<DMSVariableType> factors) {
		final List<Variable> variables = new ArrayList<>();
		for (int i = 0; i < DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR; i++) {
			variables.add(this.createVariableTestData(factors.get(i), ""));
		}
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR),
				DatasetExporterTest.TRIAL_INSTANCE_1));
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR + 3),
				DatasetExporterTest.ENTRY_TYPE));

		return variables;
	}

	private List<DMSVariableType> createVariates(int rank) {
		final StandardVariable eppStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.EPP_VARIATE,
						DatasetExporterTest.NUMERIC_VARIABLE);
		final DMSVariableType eppVariableType =
				this.createVariableTypeTestData(DatasetExporterTest.EPP_VARIATE, rank++, eppStandardVariable);

		final StandardVariable phStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.PH_VARIATE,
						DatasetExporterTest.NUMERIC_VARIABLE);
		final DMSVariableType phVariableType = this.createVariableTypeTestData(DatasetExporterTest.PH_VARIATE, rank++, phStandardVariable);

		final StandardVariable cmStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.CM_VARIATE,
						DatasetExporterTest.NUMERIC_VARIABLE);
		final DMSVariableType cmVariableType = this.createVariableTypeTestData(DatasetExporterTest.CM_VARIATE, rank++, cmStandardVariable);

		final StandardVariable aleucol1to5Variable =
				this.createStardardVariableTestData(PhenotypicType.VARIATE, DatasetExporterTest.ALEUCOL_1_5_VARIATE,
						DatasetExporterTest.CATEGORICAL_VARIABLE);
		final DMSVariableType aleucol1to5VariableType =
				this.createVariableTypeTestData(DatasetExporterTest.ALEUCOL_1_5_VARIATE, rank++, aleucol1to5Variable);

		return Lists.newArrayList(eppVariableType, phVariableType, cmVariableType, aleucol1to5VariableType);
	}

	private List<Variable> createVariatesVariablesForExperiment(final List<DMSVariableType> variates) {

		final List<Variable> variables = new ArrayList<>();

		variables.add(this.createVariableTestData(variates.get(0), DatasetExporterTest.EPP_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(1), DatasetExporterTest.PH_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(2), DatasetExporterTest.CM_VARIATE_VALUE));
		variables.add(this.createVariableTestData(variates.get(3), DatasetExporterTest.ALEUCOL_1_5_VARIATE_VALUE));

		return variables;

	}

	private void appendSpecialCharactersToVariableNames(final List<DMSVariableType> variables) {

		for (final DMSVariableType variable : variables) {
			variable.setLocalName(variable.getLocalName() + DatasetExporterTest.VAR_POST_FIX);
			variable.getStandardVariable().setName(variable.getStandardVariable().getName() + DatasetExporterTest.VAR_POST_FIX);
		}
	}

	private Variable createVariableTestData(final DMSVariableType study, final String value) {
		final Variable variable = new Variable();
		variable.setValue(value);
		variable.setVariableType(study);
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

	private StandardVariable createStardardVariableTestData(final PhenotypicType type, final String name, final Term dataType) {

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

	private void verifyColumnHeaders(final String[] headerRow, final String trialInstanceHeader) {
		Assert.assertEquals("Expected 1st column header is " + trialInstanceHeader, trialInstanceHeader, headerRow[0]);
		Assert.assertEquals("Expected 2nd column header is " + TermId.FIELDMAP_COLUMN.name(), TermId.FIELDMAP_COLUMN.name(), headerRow[1]);
		Assert.assertEquals("Expected 3rd column header is " + TermId.FIELDMAP_RANGE.name(), TermId.FIELDMAP_RANGE.name(), headerRow[2]);
		Assert.assertEquals("Expected 4th column header is " + TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name(), headerRow[3]);
		Assert.assertEquals("Expected 5th column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				headerRow[4]);
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.PH_VARIATE, DatasetExporterTest.PH_VARIATE,
				headerRow[5]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE));
		Assert.assertEquals("Expected 7th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE, headerRow[6]);
	}

	private void setupMocks() {
		// Setup Middleware mocks
		Mockito.when(DatasetExporterTest.studyDataManager.getDataSet(ArgumentMatchers.anyInt())).thenReturn(this.dataSet);

		// Setup BreedingViewInput mocks
		Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);
		Mockito.when(this.bvInput.getDesignType()).thenReturn(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvDesignName());

		final HashMap<String, Boolean> traitsSelectionMap = new HashMap<String, Boolean>();
		traitsSelectionMap.put(EPP_VARIATE, true);
		traitsSelectionMap.put(PH_VARIATE, true);
		traitsSelectionMap.put(ALEUCOL_1_5_VARIATE, false);
		traitsSelectionMap.put(CM_VARIATE, false);

		final HashMap<String, Boolean> covariatesSelectionMap = new HashMap<String, Boolean>();
		covariatesSelectionMap.put(EPP_VARIATE, false);
		covariatesSelectionMap.put(PH_VARIATE, false);
		covariatesSelectionMap.put(ALEUCOL_1_5_VARIATE, true);
		covariatesSelectionMap.put(CM_VARIATE, false);

		Mockito.when(this.bvInput.getVariatesSelectionMap()).thenReturn(traitsSelectionMap);
		Mockito.when(this.bvInput.getCovariatesSelectionMap()).thenReturn(covariatesSelectionMap);

		// Setup test experiments of dataset
		final Experiment experiment = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		Mockito.when(DatasetExporterTest.studyDataManager.getExperiments(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
				.thenReturn(Lists.newArrayList(experiment));
	}

}

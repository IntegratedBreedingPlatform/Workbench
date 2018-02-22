package org.generationcp.ibpworkbench.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.service.api.OntologyService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class DatasetExporterTest {

	public static final int DATASET_ID = 1;

	@Mock
	private static ManagerFactory factory;

	@Mock
	private static StudyDataManager studyDataManager;

	@Mock
	private static WorkbenchDataManager workbenchDataManager;

	@Mock
	private BreedingViewInput bvInput;

	@Mock
	private DataSet dataSet;

	@Mock
	private OntologyService ontologyService;

	@InjectMocks
	private DatasetExporter exporter = new DatasetExporter(DATASET_ID);

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

		this.factors = this.createFactors(1);
		this.variates = this.createVariates(2);
		this.factorVariables = this.createFactorVariablesForExperiment(this.factors);
		this.variateVariables = this.createVariatesVariablesForExperiment(this.variates);

		this.dataSet = this.createDatasetTestData(this.factors, this.variates);
		this.setupMocks();
	}
	
	@Test
	public void testGenerateVariateColumnsList() {
		final int numberOfVariatesInDataset = this.variates.size();
		Assert.assertEquals("Expecting dataset has " + numberOfVariatesInDataset + " variates.", numberOfVariatesInDataset, this.dataSet.getVariableTypes().getVariates().size());
		
		// Method to test
		final List<String> variateColumnsToWrite = this.exporter.generateVariateColumnsList(this.dataSet, this.bvInput);
		
		// Only selected variates will be included in variate columns
		Assert.assertEquals("Expecting only selected variates will be included in variate columns.", (numberOfVariatesInDataset - 1), variateColumnsToWrite.size());
		Assert.assertFalse("Not expecting " + DatasetExporterTest.CM_VARIATE + " to be included in variate columns but was included.",
				variateColumnsToWrite.contains(DatasetExporterTest.CM_VARIATE));
	}
	
	@Test
	public void testGenerateFactorColumnsList() {
		final int numberOfFactorsInDataset = this.factors.size();
		Assert.assertEquals("Expecting dataset has " + numberOfFactorsInDataset + " factors.", numberOfFactorsInDataset, this.dataSet.getVariableTypes().getFactors().size());
		
		final List<String> factorColumnsToWrite = this.exporter.generateFactorColumnsList(this.dataSet);
		
		// Check that dataset and study variable types are not included in factor columns to write
		Assert.assertEquals("Expecting only selected variates will be included in factor columns.", (numberOfFactorsInDataset - 2), factorColumnsToWrite.size());
		Assert.assertFalse("Not expecting " + TermId.DATASET_NAME.name() + " to be included in factor columns but was included.",
				factorColumnsToWrite.contains(TermId.DATASET_NAME.name()));
		Assert.assertFalse("Not expecting " + TermId.STUDY_NAME.name() + " to be included in factor columns but was included.",
				factorColumnsToWrite.contains(TermId.STUDY_NAME.name()));
	}
	
	@Test
	public void testExperimentIsInSelectedEnvironments() {
		boolean isInSelectedEnvironments = this.exporter.isExperimentInSelectedEnvironments(this.bvInput, Lists.newArrayList(TRIAL_INSTANCE_1),
				this.createExperimentTestData(this.factorVariables, this.variateVariables));
		Assert.assertTrue("Expecting experiment to be detected as part of selected Trial Instance 1", isInSelectedEnvironments);
		
		isInSelectedEnvironments = this.exporter.isExperimentInSelectedEnvironments(this.bvInput, Lists.newArrayList(TRIAL_INSTANCE_2),
				this.createExperimentTestData(this.factorVariables, this.variateVariables));
		Assert.assertFalse("Expecting experiment not to be detected as part of selected Trial Instance 2", isInSelectedEnvironments);
	}
	
	@Test
	public void testIsDummyRepVariableUsedWhenDummyRepVariablePresent() {
		Mockito.when(this.bvInput.getReplicatesFactorName()).thenReturn(DatasetExporter.DUMMY_REPLICATES);
		
		boolean isDummyRepUsed = this.exporter.isDummyRepVariableUsed(this.bvInput);
		Assert.assertTrue("Expecting to return true since dummy replicates factor was used and design type is " + this.bvInput.getDesignType(), isDummyRepUsed);
		
		Mockito.when(this.bvInput.getDesignType()).thenReturn(DesignType.P_REP_DESIGN.getName());
		isDummyRepUsed = this.exporter.isDummyRepVariableUsed(this.bvInput);
		Assert.assertFalse("Expecting to return false because even though dummy replicates factor was used, design type is " + DesignType.P_REP_DESIGN.getName(), isDummyRepUsed);
		
		Mockito.when(this.bvInput.getDesignType()).thenReturn(DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName());
		isDummyRepUsed = this.exporter.isDummyRepVariableUsed(this.bvInput);
		Assert.assertFalse("Expecting to return false because even though dummy replicates factor was used, design type is " + DesignType.AUGMENTED_RANDOMIZED_BLOCK.getName(), isDummyRepUsed);
	}
	
	@Test
	public void testExportToCSVForBreedingViewWithNumericAndCategoricalVariates() throws DatasetExporterException {
		final Experiment firstRowObservation = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		final Experiment secondRowObservation = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		Mockito.when(DatasetExporterTest.studyDataManager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
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
	public void testExportToCSVForBreedingViewExperimentsDontHaveSomeDatasetFactors() throws DatasetExporterException {
		// 2nd row experiment doesn't have FIELDMAP_COLUMN and FIELMAP_RANGE variables which are present in dataset/column headers
		final List<Variable> factorVariablesWithoutFieldmapVariables = this.createFactorVariablesWithoutFieldmapVariables(this.factors);
		final Experiment firstRowObservation = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		final Experiment secondRowObservation = this.createExperimentTestData(factorVariablesWithoutFieldmapVariables, this.variateVariables);
		Mockito.when(DatasetExporterTest.studyDataManager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
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
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.TRIAL_INSTANCE_1,
				DatasetExporterTest.TRIAL_INSTANCE_1, secondRow[0]);
		// Expecting blank values for FIELMAP columns
		Assert.assertEquals("Expected 2nd column value is blank", "", secondRow[1]);
		Assert.assertEquals("Expected 3rd column value is blank", "", secondRow[2]);
		Assert.assertEquals("Expected 4th column value is " + DatasetExporterTest.EPP_VARIATE_VALUE, DatasetExporterTest.EPP_VARIATE_VALUE,
				secondRow[3]);
		Assert.assertEquals("Expected 5th column value is " + DatasetExporterTest.PH_VARIATE_VALUE, DatasetExporterTest.PH_VARIATE_VALUE,
				secondRow[4]);
		Assert.assertEquals("Expected 6th column value is " + DatasetExporterTest.BV_MISSING_VALUE, DatasetExporterTest.BV_MISSING_VALUE,
				secondRow[5]);

	}

	@Test
	public void testExportToCSVForBreedingViewNoExperimentsForSelectedEnvironment() throws DatasetExporterException {
		// Sselected TRIAL_INSTANCE is 2 but there are no experiments found for it
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_2), this.bvInput);
		final List<String[]> rowsToWrite = exporter.getRowsToWrite();

		Assert.assertEquals("The CSV file should only have 1 row because it does not have any experiment for selected environment.", 1,
				rowsToWrite.size());

		// Verify the header names on 1st column
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);
	}

	@Test
	public void testExportToCSVForBreedingViewTraitNamesWithSpecialCharactersAndWhiteSpace() throws DatasetExporterException {
		this.appendSpecialCharactersToVariableName(this.factors);
		this.appendSpecialCharactersToVariableName(this.variates);
		// Setup selected variates
		final HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariablesAsActive(variatesActiveState, this.variates);
		Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);

		// Method to test
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + DatasetExporterTest.VAR_POST_FIX, Lists.newArrayList(TRIAL_INSTANCE_1),
				this.bvInput);
		final List<String[]> rowsToWrite = exporter.getRowsToWrite();

		// Verify the header names were "cleaned up"
		final String[] headerRow = rowsToWrite.get(0);
		Assert.assertEquals("Expected 1st column header is " + DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME + CLEANED_VAR_POST_FIX, headerRow[0]);
		Assert.assertEquals("Expected 2nd column header is " + TermId.FIELDMAP_COLUMN.name() + CLEANED_VAR_POST_FIX,
				TermId.FIELDMAP_COLUMN.name() + CLEANED_VAR_POST_FIX, headerRow[1]);
		Assert.assertEquals("Expected 3rd column header is " + TermId.FIELDMAP_RANGE.name() + CLEANED_VAR_POST_FIX,
				TermId.FIELDMAP_RANGE.name() + CLEANED_VAR_POST_FIX, headerRow[2]);
		Assert.assertEquals("Expected 4th column header is " + DatasetExporterTest.EPP_VARIATE + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.EPP_VARIATE + CLEANED_VAR_POST_FIX, headerRow[3]);
		Assert.assertEquals("Expected 5th column header is " + DatasetExporterTest.PH_VARIATE + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.PH_VARIATE + CLEANED_VAR_POST_FIX, headerRow[4]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + CLEANED_VAR_POST_FIX + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE + CLEANED_VAR_POST_FIX));
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE + CLEANED_VAR_POST_FIX,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE + CLEANED_VAR_POST_FIX, headerRow[5]);

	}

	@Test
	public void testExportToCSVForBreedingViewTheSelectedFactorIsNotTrialInstance() throws DatasetExporterException {
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
		final List<String[]> rowsToWrite = exporter.getRowsToWrite();
		Assert.assertEquals("Expecting 2 rows for CSV file", 2, rowsToWrite.size());

		// Verify the header names
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.ALT_TRIAL_INSTANCE_NAME);
		Assert.assertEquals("Expected 7th column header is " + DatasetExporterTest.ENV_NAME, DatasetExporterTest.ENV_NAME, headerRow[6]);

		// Verify the values of the first observation row
		final String[] firstRow = rowsToWrite.get(1);
		this.verifyRowValues(firstRow);
		Assert.assertEquals("Expected 7th column value is " + DatasetExporterTest.ENV_VALUE, DatasetExporterTest.ENV_VALUE, firstRow[6]);
	}
	
	@Test
	public void testExportToCSVForBreedingViewDummyReplicateFactorUsed() throws DatasetExporterException {
		Mockito.when(this.bvInput.getReplicatesFactorName()).thenReturn(DatasetExporter.DUMMY_REPLICATES);

		// Method to test
		this.exporter.exportToCSVForBreedingView(DatasetExporterTest.FILENAME, DEFAULT_TRIAL_INSTANCE_NAME,
				Lists.newArrayList(DatasetExporterTest.TRIAL_INSTANCE_1), this.bvInput);
		final List<String[]> rowsToWrite = exporter.getRowsToWrite();
		Assert.assertEquals("Expecting 2 rows for CSV file", 2, rowsToWrite.size());

		// Verify the header names
		final String[] headerRow = rowsToWrite.get(0);
		this.verifyColumnHeaders(headerRow, DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);
		Assert.assertEquals("Expected 7th column header is " + DatasetExporter.DUMMY_REPLICATES, DatasetExporter.DUMMY_REPLICATES, headerRow[6]);

		// Verify the values of the first observation row
		final String[] firstRow = rowsToWrite.get(1);
		this.verifyRowValues(firstRow);
		Assert.assertEquals("Expected 7th column value is 1 for column " + DatasetExporter.DUMMY_REPLICATES, "1", firstRow[6]);
	}
	
	private void verifyRowValues(final String[] firstRow) {
		Assert.assertEquals("Expected 1st column value is " + DatasetExporterTest.TRIAL_INSTANCE_1,
				DatasetExporterTest.TRIAL_INSTANCE_1, firstRow[0]);
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

	private void setupSelectedVariablesAsActive(final HashMap<String, Boolean> variableState, final List<DMSVariableType> variates) {
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
		final StandardVariable datasetNameStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.DATASET, TermId.DATASET_NAME.name(),
						DatasetExporterTest.CHARACTER_VARIABLE);
		final DMSVariableType datasetNameVariableType =
				this.createVariableTypeTestData(TermId.DATASET_NAME.name(), rank++, datasetNameStandardVariable);
		
		final StandardVariable studyNameStandardVariable =
				this.createStardardVariableTestData(PhenotypicType.STUDY, TermId.STUDY_NAME.name(),
						DatasetExporterTest.CHARACTER_VARIABLE);
		final DMSVariableType studyNameVariableType =
				this.createVariableTypeTestData(TermId.STUDY_NAME.name(), rank++, studyNameStandardVariable);
	
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

		return Lists.newArrayList(datasetNameVariableType, studyNameVariableType, trialEnvironmentVariableType, fieldmapColumnVariableType, fieldmapRangeVariableType);
	}

	private List<Variable> createFactorVariablesForExperiment(final List<DMSVariableType> factors) {
		final List<Variable> variables = this.createFactorVariablesWithoutFieldmapVariables(factors);
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR+1), FIELDMAP_COLUMN_VALUE));
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR+2), FIELDMAP_RANGE_VALUE));
		return variables;
	}

	private List<Variable> createFactorVariablesWithoutFieldmapVariables(final List<DMSVariableType> factors) {
		final List<Variable> variables = new ArrayList<>();
		for (int i = 0; i < DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR; i++) {
			variables.add(this.createVariableTestData(factors.get(i), ""));
		}
		variables.add(this.createVariableTestData(factors.get(DatasetExporterTest.INDEX_OF_TRIAL_INSTANCE_FACTOR), TRIAL_INSTANCE_1));

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
		Assert.assertEquals("Expected 1st column header is " + trialInstanceHeader,
				trialInstanceHeader, headerRow[0]);
		Assert.assertEquals("Expected 2nd column header is " + TermId.FIELDMAP_COLUMN.name(), TermId.FIELDMAP_COLUMN.name(),
				headerRow[1]);
		Assert.assertEquals("Expected 3rd column header is " + TermId.FIELDMAP_RANGE.name(), TermId.FIELDMAP_RANGE.name(),
				headerRow[2]);
		Assert.assertEquals("Expected 4th column header is " + DatasetExporterTest.EPP_VARIATE, DatasetExporterTest.EPP_VARIATE,
				headerRow[3]);
		Assert.assertEquals("Expected 5th column header is " + DatasetExporterTest.PH_VARIATE, DatasetExporterTest.PH_VARIATE,
				headerRow[4]);
		Assert.assertFalse(DatasetExporterTest.CM_VARIATE + " should not be included",
				ArrayUtils.contains(headerRow, DatasetExporterTest.CM_VARIATE));
		Assert.assertEquals("Expected 6th column header is " + DatasetExporterTest.ALEUCOL_1_5_VARIATE,
				DatasetExporterTest.ALEUCOL_1_5_VARIATE, headerRow[5]);
	}

	private void setupMocks() {
		// Setup Middleware mocks
		Mockito.when(DatasetExporterTest.studyDataManager.getDataSet(Matchers.anyInt())).thenReturn(this.dataSet);
		
		// Setup BreedingViewInput mocks
		Mockito.when(this.bvInput.getReplicates()).thenReturn(Mockito.mock(Replicates.class));
		Mockito.when(this.bvInput.getReplicates().getName()).thenReturn(DatasetExporterTest.REP_NAME);
		Mockito.when(this.bvInput.getTrialInstanceName()).thenReturn(DatasetExporterTest.DEFAULT_TRIAL_INSTANCE_NAME);
		Mockito.when(this.bvInput.getDesignType()).thenReturn(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		
		// Setup selected variates
		final HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		this.setupSelectedVariablesAsActive(variatesActiveState, this.variates);
		Mockito.when(this.bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
		
		// Setup test experiments of dataset
		final Experiment experiment = this.createExperimentTestData(this.factorVariables, this.variateVariables);
		Mockito.when(DatasetExporterTest.studyDataManager.getExperiments(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
				.thenReturn(Lists.newArrayList(experiment));
	}


}

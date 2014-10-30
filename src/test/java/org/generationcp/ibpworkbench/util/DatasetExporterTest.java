package org.generationcp.ibpworkbench.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.ibpworkbench.util.DatasetExporterException;
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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
	private static final String CATEGORICAL_VARIATE_VALUE = "1";
	private static final int CATEGORICAL_VARIATE_ENUM_ID = 1;
	private static final String CATEGORICAL_VARIATE_ENUM_NAME = "5";
	private static final String CATEGORICAL_VARIATE_ENUM_DESCRIPTION = "Very Severe";
	
	private static final Term NUMERIC_VARIABLE = new Term(TermId.NUMERIC_VARIABLE.getId(),"","");
	private static final Term CATEGORICAL_VARIABLE = new Term(TermId.CATEGORICAL_VARIABLE.getId(),"","");
	
	private static final Term TRIAL_INSTANCE_ROLE = new Term(TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
	private static final Term OBSERVATION_VARIATE_ROLE = new Term(TermId.OBSERVATION_VARIATE.getId(), "", "");
	private static final Term CATEGORICAL_VARIATE_ROLE = new Term(TermId.CATEGORICAL_VARIATE.getId(), "", "");

	private static final String VAR_POST_FIX = "%#! @";
	private static final String CLEANED_VAR_POST_FIX = "%_";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExportToCSVForBreedingView_Default() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DEFAULT_TRIAL_INSTANCE_VALUE_1);

		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		createFactorsAndVariatesTestData(factors,factorVariables,variates,variateVariables,false);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		setupSelectedVariables(variatesActiveState,variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiment = createExperimentTestData(factorVariables,variateVariables);
		experiments.add(experiment);
		
		dataSet = createDatasetTestData(factors,variates);

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn(REP_NAME);
			when(bvInput.getTrialInstanceName()).thenReturn(DEFAULT_TRIAL_INSTANCE_NAME);
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(FILENAME, DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		//header
		assertEquals("Expected 1st column header is "+DEFAULT_TRIAL_INSTANCE_NAME,DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		assertEquals("Expected 2nd column header is "+VARIATE_NAME_1,VARIATE_NAME_1, tableItems.get(0)[1]);
		assertEquals("Expected 3rd column header is "+VARIATE_NAME_2,VARIATE_NAME_2, tableItems.get(0)[2]);
		assertFalse(VARIATE_NAME_3+" should not be included", ArrayUtils.contains(tableItems.get(0), VARIATE_NAME_3));
		
		//data
		assertEquals("Expected 1st column value is "+DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		assertEquals("Expected 2nd column value is "+VARIATE_VALUE_1,
				VARIATE_VALUE_1, tableItems.get(1)[1]);
		assertEquals("Expected 3rd column value is "+VARIATE_VALUE_2,
				VARIATE_VALUE_2, tableItems.get(1)[2]);
		
		assertEquals("Header name should be "+DEFAULT_TRIAL_INSTANCE_NAME,
				DEFAULT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DEFAULT_TRIAL_INSTANCE_NAME));
		assertEquals("Header name should be "+VARIATE_NAME_1,
				VARIATE_NAME_1, headerAliasMap.get(VARIATE_NAME_1));
		assertEquals("Header name should be "+VARIATE_NAME_2,
				VARIATE_NAME_2, headerAliasMap.get(VARIATE_NAME_2));
		assertNull(VARIATE_NAME_3+" should not be included",headerAliasMap.get(VARIATE_NAME_3));

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

	private Experiment createExperimentTestData(
			List<Variable> factorVariables, 
			List<Variable> variateVariables) {
		Experiment experiment = new Experiment();
		VariableList factorVariableList = createVariableListTestData(factorVariables);
		VariableList variateVariableList = createVariableListTestData(variateVariables);
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

	private void setupSelectedVariables(
			HashMap<String, Boolean> variableState,
			List<VariableType> variates) {
		for (VariableType variableType : variates) {
			String localName = variableType.getLocalName();
			if(localName.startsWith(DEFAULT_TRIAL_INSTANCE_NAME) ||
				localName.startsWith(VARIATE_NAME_1) ||
				localName.startsWith(VARIATE_NAME_2) || 
				localName.startsWith(CATEGORICAL_VARIATE_NAME)) {
				variableState.put(localName, true);
			} else {
				variableState.put(localName, false);
			}
		}
	}

	private void createFactorsAndVariatesTestData(
			List<VariableType> factors,
			List<Variable> factorVariables,
			List<VariableType> variates,
			List<Variable> variateVariables, boolean addSpecialCharAndWhitespaceToVarName) {
		
		String varPostfix = "";
		if(addSpecialCharAndWhitespaceToVarName) {
			varPostfix = VAR_POST_FIX;
		}
		
		int rank = 1;
		
		addVariableToList(factors,factorVariables,
				DEFAULT_TRIAL_INSTANCE_NAME+varPostfix,rank++,
				DEFAULT_TRIAL_INSTANCE_VALUE_1,PhenotypicType.TRIAL_ENVIRONMENT,
				NUMERIC_VARIABLE, TRIAL_INSTANCE_ROLE);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_1+varPostfix,rank++,
				VARIATE_VALUE_1,PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_2+varPostfix,rank++,
				VARIATE_VALUE_2,PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_3+varPostfix,rank++,
				VARIATE_VALUE_3,PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE);
	}

	private void addVariableToList(List<VariableType> variableTypes,
			List<Variable> variables, String variableName, int rank, String value,
			PhenotypicType phenotypicType, Term dataType, Term role) {
		StandardVariable standardVariable = createStardardVariableTestData(phenotypicType,variableName, dataType, role);
		VariableType variableType = createVariableTypeTestData(variableName,rank,standardVariable);
		variableTypes.add(variableType);
		variables.add(createVariableTestData(variableType,value));
	}

	private Variable createVariableTestData(VariableType trial, String value) {
		Variable variable = new Variable();
		variable.setValue(value);
		variable.setVariableType(trial);
		return variable;
	}

	private VariableType createVariableTypeTestData(String localName, int rank,
			StandardVariable standardVariable) {
		VariableType variableType = new VariableType();
		variableType.setLocalName(localName);
		variableType.setRank(rank);
		variableType.setStandardVariable(standardVariable);
		return variableType;
	}

	@Test
	public void testExportToCSVForBreedingView_NoExistingRecordsForSelectedEnvironment() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DEFAULT_TRIAL_INSTANCE_VALUE_2);
		
		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		createFactorsAndVariatesTestData(factors,factorVariables,variates,variateVariables,false);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		setupSelectedVariables(variatesActiveState,variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiment = createExperimentTestData(factorVariables,variateVariables);
		experiments.add(experiment);
		
		dataSet = createDatasetTestData(factors,variates);

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn(REP_NAME);
			when(bvInput.getTrialInstanceName()).thenReturn(DEFAULT_TRIAL_INSTANCE_NAME);
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(FILENAME, DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		assertEquals("Expected 1st column header is "+DEFAULT_TRIAL_INSTANCE_NAME,
				DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		assertEquals("Expected 2nd column header is "+VARIATE_NAME_1,
				VARIATE_NAME_1, tableItems.get(0)[1]);
		assertEquals("The CSV file should only have 1 row because it does not have a record", 1, tableItems.size());

		assertEquals("Header name should be "+DEFAULT_TRIAL_INSTANCE_NAME,
				DEFAULT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DEFAULT_TRIAL_INSTANCE_NAME));
		assertEquals("Header name should be "+VARIATE_NAME_1,
				VARIATE_NAME_1, headerAliasMap.get(VARIATE_NAME_1));

	}

	@Test
	public void testExportToCSVForBreedingView_TraitNamesWithSpecialCharactersAndWhiteSpace() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DEFAULT_TRIAL_INSTANCE_VALUE_1);
		
		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		createFactorsAndVariatesTestData(factors,factorVariables,variates,variateVariables,true);

		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		setupSelectedVariables(variatesActiveState,variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiment = createExperimentTestData(factorVariables,variateVariables);
		experiments.add(experiment);
		
		dataSet = createDatasetTestData(factors,variates);

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn(REP_NAME);
			when(bvInput.getTrialInstanceName()).thenReturn(DEFAULT_TRIAL_INSTANCE_NAME+VAR_POST_FIX);
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(FILENAME, DEFAULT_TRIAL_INSTANCE_NAME+VAR_POST_FIX,
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		assertEquals("Expected 1st column header is "+DEFAULT_TRIAL_INSTANCE_NAME+CLEANED_VAR_POST_FIX,
				DEFAULT_TRIAL_INSTANCE_NAME+CLEANED_VAR_POST_FIX, tableItems.get(0)[0]);
		assertEquals("Expected 2nd column header is "+VARIATE_NAME_1+CLEANED_VAR_POST_FIX,
				VARIATE_NAME_1+CLEANED_VAR_POST_FIX, tableItems.get(0)[1]);
		assertEquals("Expected 1st column value  is "+DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		assertEquals("Expected 2nd column value  is "+VARIATE_VALUE_1,
				VARIATE_VALUE_1, tableItems.get(1)[1]);

		assertEquals("Header name should be "+DEFAULT_TRIAL_INSTANCE_NAME+VAR_POST_FIX,
				DEFAULT_TRIAL_INSTANCE_NAME+VAR_POST_FIX, 
				headerAliasMap.get(DEFAULT_TRIAL_INSTANCE_NAME+CLEANED_VAR_POST_FIX));
		assertEquals("Header name should be "+VARIATE_NAME_1+VAR_POST_FIX,
				VARIATE_NAME_1+VAR_POST_FIX, headerAliasMap.get(VARIATE_NAME_1+CLEANED_VAR_POST_FIX));

	}
	
	@Test
	public void testExportToCSVForBreedingView_TheSelectedFactorIsNotTrialInstance() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DEFAULT_TRIAL_INSTANCE_VALUE_1);
		
		List<SeaEnvironmentModel> selectedEnvironmentsList = new ArrayList<SeaEnvironmentModel>();
		SeaEnvironmentModel envModel = new SeaEnvironmentModel();
		envModel.setLocationId(1);
		envModel.setEnvironmentName(ENV_VALUE);
		selectedEnvironmentsList.add(envModel);
		
		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		createFactorsAndVariatesTestData(factors,factorVariables,variates,variateVariables,false);
		
		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		setupSelectedVariables(variatesActiveState,variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiment = createExperimentTestData(factorVariables,variateVariables);
		experiments.add(experiment);
		
		dataSet = createDatasetTestData(factors,variates);
		
		//change the trial instance for testing
		factors.get(0).setLocalName(ALT_TRIAL_INSTANCE_NAME);

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn(REP_NAME);
			when(bvInput.getTrialInstanceName()).thenReturn(ALT_TRIAL_INSTANCE_NAME);
			when(bvInput.getSelectedEnvironments()).thenReturn(selectedEnvironmentsList);
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(FILENAME, ENV_NAME,
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		//header
		assertEquals("Expected 1st column header is "+ALT_TRIAL_INSTANCE_NAME,
				ALT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		assertEquals("Expected 2nd column header is "+VARIATE_NAME_1,
				VARIATE_NAME_1, tableItems.get(0)[1]);
		assertEquals("Expected 3rd column header is "+VARIATE_NAME_2,
				VARIATE_NAME_2, tableItems.get(0)[2]);
		assertEquals("Expected 4th column header is "+ENV_NAME,
				ENV_NAME, tableItems.get(0)[3]);
		assertFalse(VARIATE_NAME_3+" should not be included",
				ArrayUtils.contains(tableItems.get(0), VARIATE_NAME_3));
		
		//data
		assertEquals("Expected 1st column value is "+DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		assertEquals("Expected 2nd column value is "+VARIATE_VALUE_1,
				VARIATE_VALUE_1, tableItems.get(1)[1]);
		assertEquals("Expected 3rd column value is "+VARIATE_VALUE_2,
				VARIATE_VALUE_2, tableItems.get(1)[2]);
		assertEquals("Expected 4th column value is "+ENV_VALUE,
				ENV_VALUE, tableItems.get(1)[3]);
		
		assertEquals("Header name should be "+ALT_TRIAL_INSTANCE_NAME,
				ALT_TRIAL_INSTANCE_NAME, headerAliasMap.get(ALT_TRIAL_INSTANCE_NAME));
		assertEquals("Header name should be "+VARIATE_NAME_1,
				VARIATE_NAME_1, headerAliasMap.get(VARIATE_NAME_1));
		assertEquals("Header name should be "+VARIATE_NAME_2,
				VARIATE_NAME_2, headerAliasMap.get(VARIATE_NAME_2));
		assertEquals("Header name should be "+ENV_NAME,
				ENV_NAME, headerAliasMap.get(ENV_NAME));
		assertNull(VARIATE_NAME_3+" should not be included",
				headerAliasMap.get(VARIATE_NAME_3));

	}


	private StandardVariable createStardardVariableTestData(PhenotypicType type, String name, Term dataType, Term storedIn) {

		StandardVariable stdVar = new StandardVariable();
		stdVar.setPhenotypicType(type);
		stdVar.setName(name);
		stdVar.setDataType(dataType);
		stdVar.setStoredIn(storedIn);
		
		if(storedIn == CATEGORICAL_VARIATE_ROLE) {
			List<Enumeration> validValues = new ArrayList<Enumeration>();
			validValues.add(new Enumeration(
					CATEGORICAL_VARIATE_ENUM_ID,CATEGORICAL_VARIATE_ENUM_NAME, 
					CATEGORICAL_VARIATE_ENUM_DESCRIPTION, 1));
			stdVar.setEnumerations(validValues);
		}
		return stdVar;
	}
	
	@Test
	public void testExportToCSVForBreedingView_WithNumericCategoricalVariate() {

		List<String> selectedEnvironments = new ArrayList<String>();
		selectedEnvironments.add(DEFAULT_TRIAL_INSTANCE_VALUE_1);

		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		createFactorsAndVariatesTestData(factors,factorVariables,variates,variateVariables,false);
		
		addVariableToList(variates,variateVariables,
				CATEGORICAL_VARIATE_NAME,4,
				CATEGORICAL_VARIATE_VALUE,PhenotypicType.VARIATE,
				CATEGORICAL_VARIABLE,CATEGORICAL_VARIATE_ROLE);
		
		HashMap<String, Boolean> variatesActiveState = new HashMap<String, Boolean>();
		setupSelectedVariables(variatesActiveState,variates);

		List<Experiment> experiments = new ArrayList<Experiment>();
		experiment = createExperimentTestData(factorVariables,variateVariables);
		experiments.add(experiment);
		
		dataSet = createDatasetTestData(factors,variates);
		

		try {
			when(manager.getDataSet(anyInt())).thenReturn(dataSet);
			when(manager.getExperiments(anyInt(), anyInt(), anyInt())).thenReturn(experiments);

			when(bvInput.getVariatesActiveState()).thenReturn(variatesActiveState);
			when(bvInput.getReplicates()).thenReturn(mock(Replicates.class));
			when(bvInput.getReplicates().getName()).thenReturn(REP_NAME);
			when(bvInput.getTrialInstanceName()).thenReturn(DEFAULT_TRIAL_INSTANCE_NAME);
			
			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(mock(WorkbenchSetting.class));
			when(workbenchDataManager.getWorkbenchSetting().getInstallationDirectory()).thenReturn("");

		} catch (Exception e) {
			fail(e.getMessage());
		}

		DatasetExporter exporter = new DatasetExporter(manager, 1, 1);
		exporter.setWorkbenchDataManager(workbenchDataManager);
		try {
			exporter.exportToCSVForBreedingView(FILENAME, DEFAULT_TRIAL_INSTANCE_NAME,
					selectedEnvironments, bvInput);
		} catch (DatasetExporterException e) {

			fail(e.getMessage());
		}

		List<String[]> tableItems = exporter.getTableItems();
		Map<String, String> headerAliasMap = exporter.getHeaderNameAliasMap();

		//header
		assertEquals("Expected 1st column header is "+DEFAULT_TRIAL_INSTANCE_NAME,DEFAULT_TRIAL_INSTANCE_NAME, tableItems.get(0)[0]);
		assertEquals("Expected 2nd column header is "+VARIATE_NAME_1,VARIATE_NAME_1, tableItems.get(0)[1]);
		assertEquals("Expected 3rd column header is "+VARIATE_NAME_2,VARIATE_NAME_2, tableItems.get(0)[2]);
		assertFalse(VARIATE_NAME_3+" should not be included", ArrayUtils.contains(tableItems.get(0), VARIATE_NAME_3));
		assertEquals("Expected 4th column header is "+CATEGORICAL_VARIATE_NAME,
				CATEGORICAL_VARIATE_NAME, tableItems.get(0)[3]);
		
		//data
		assertEquals("Expected 1st column value is "+DEFAULT_TRIAL_INSTANCE_VALUE_1,
				DEFAULT_TRIAL_INSTANCE_VALUE_1, tableItems.get(1)[0]);
		assertEquals("Expected 2nd column value is "+VARIATE_VALUE_1,
				VARIATE_VALUE_1, tableItems.get(1)[1]);
		assertEquals("Expected 3rd column value is "+VARIATE_VALUE_2,
				VARIATE_VALUE_2, tableItems.get(1)[2]);
		assertEquals("Expected 4th column value is "+CATEGORICAL_VARIATE_ENUM_NAME,
				CATEGORICAL_VARIATE_ENUM_NAME, tableItems.get(1)[3]);
		
		assertEquals("Header name should be "+DEFAULT_TRIAL_INSTANCE_NAME,
				DEFAULT_TRIAL_INSTANCE_NAME, headerAliasMap.get(DEFAULT_TRIAL_INSTANCE_NAME));
		assertEquals("Header name should be "+VARIATE_NAME_1,
				VARIATE_NAME_1, headerAliasMap.get(VARIATE_NAME_1));
		assertEquals("Header name should be "+VARIATE_NAME_2,
				VARIATE_NAME_2, headerAliasMap.get(VARIATE_NAME_2));
		assertNull(VARIATE_NAME_3+" should not be included",headerAliasMap.get(VARIATE_NAME_3));
		assertEquals("Header name should be "+CATEGORICAL_VARIATE_NAME,
				CATEGORICAL_VARIATE_NAME, headerAliasMap.get(CATEGORICAL_VARIATE_NAME));
		
	}

}

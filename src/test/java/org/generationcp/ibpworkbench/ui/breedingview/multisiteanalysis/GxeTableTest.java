package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Label;

public class GxeTableTest {
	
	private static final int STUDY_ID = 1;
	private static final int MEANS_DATASET_ID = 2;
	private static final int TRIAL_DATASET_ID = 1;
	
	private static final String TRIAL_FACTOR = "TRIAL";
	private static final int TRIAL_FACTOR_ID = 9999;
	private static final String TRIAL_FACTOR_VALUE = "1";
	private static final String SITE_FACTOR = "SITE";
	private static final int SITE_FACTOR_ID = 8888;
	private static final String SITE_FACTOR_VALUE = "CIMMYT, Harrare";
	private static final String GROUP_FACTOR = "MegaEnv";
	private static final int GROUP_FACTOR_ID = 7777;
	private static final String GROUP_FACTOR_VALUE = "Wet Season";

	private static final String VARIATE_NAME_1 = "EPP";
	private static final String VARIATE_NAME_1_MEANS= "EPP_Means";
	private static final String VARIATE_NAME_1_UNITERRORS= "EPP_UnitErrors";
	private static final String VARIATE_NAME_1_HERITABILITY= "EPP_Heritability";
	private static final String VARIATE_NAME_2 = "PH";
	private static final String VARIATE_NAME_2_MEANS= "PH_Means";
	private static final String VARIATE_NAME_2_UNITERRORS= "PH_UnitErrors";
	private static final String VARIATE_NAME_2_HERITABILITY= "PH_Heritability";
	private static final String VARIATE_NAME_3 = "EARH";
	private static final String VARIATE_NAME_3_MEANS= "EARH_Means";
	private static final String VARIATE_NAME_3_UNITERRORS= "EARH_UnitErrors";
	private static final String VARIATE_NAME_3_HERITABILITY= "EARH_Heritability";
	
	private static final int VARIATE_NAME_1_ID = 1000;
	private static final int VARIATE_NAME_1_MEANS_ID= 1001;
	private static final int VARIATE_NAME_1_UNITERRORS_ID= 1002;
	private static final int VARIATE_NAME_1_HERITABILITY_ID= 1003;
	private static final int VARIATE_NAME_2_ID = 1004;
	private static final int VARIATE_NAME_2_MEANS_ID= 1005;
	private static final int VARIATE_NAME_2_UNITERRORS_ID= 1006;
	private static final int VARIATE_NAME_2_HERITABILITY_ID= 1007;
	private static final int VARIATE_NAME_3_ID = 1008;
	private static final int VARIATE_NAME_3_MEANS_ID= 1009;
	private static final int VARIATE_NAME_3_UNITERRORS_ID= 1010;
	private static final int VARIATE_NAME_3_HERITABILITY_ID= 1011;
	
	private static final String VARIATE_NAME_1_HERITABILITY_VALUE = "101";
	private static final String VARIATE_NAME_2_HERITABILITY_VALUE = "102";
	private static final String VARIATE_NAME_3_HERITABILITY_VALUE = "103";
	
	private static final int CATEGORICAL_VARIATE_ENUM_ID = 1;
	private static final String CATEGORICAL_VARIATE_ENUM_NAME = "5";
	private static final String CATEGORICAL_VARIATE_ENUM_DESCRIPTION = "Very Severe";
	
	private static final Term NUMERIC_VARIABLE = new Term(TermId.NUMERIC_VARIABLE.getId(),"","");
	private static final Term CATEGORICAL_VARIABLE = new Term(TermId.CATEGORICAL_VARIABLE.getId(),"","");
	private static final Term CHARACTER_VARIABLE = new Term(TermId.CHARACTER_VARIABLE.getId(),"","");
	private static final Term LS_MEAN_METHOD = new Term(9999,"LS MEAN","LS MEAN");
	private static final Term UNIT_ERROR_METHOD = new Term(9999,"ERROR ESTIMATE","ERROR ESTIMATE");
	
	private static final Term TRIAL_INSTANCE_ROLE = new Term(TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
	private static final Term TRIAL_ENVIRONMENT_ROLE = new Term(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", "");
	private static final Term OBSERVATION_VARIATE_ROLE = new Term(TermId.OBSERVATION_VARIATE.getId(), "", "");
	private static final Term CATEGORICAL_VARIATE_ROLE = new Term(TermId.CATEGORICAL_VARIATE.getId(), "", "");
	
	@Mock
	private Property.ValueChangeListener listener;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	private Map<String, Boolean> variatesCheckBoxState;
	
	@Before
	public void setUp() throws MiddlewareQueryException{
		MockitoAnnotations.initMocks(this);
		
		//means dataset
		List<VariableType> factors = new ArrayList<VariableType>();
		List<VariableType> variates = new ArrayList<VariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();
		
		//trial dataset
		List<VariableType> trialFactors = new ArrayList<VariableType>();
		List<VariableType> trialVariates = new ArrayList<VariableType>();
		List<Variable> trialFactorVariables = new ArrayList<Variable>();
		List<Variable> trialVariateVariables = new ArrayList<Variable>();
		
		List<DatasetReference> datasetRefs = createDataSetRef();
		
		createFactorsAndVariatesTestDataForMeans(factors, factorVariables, variates, variateVariables);
		createFactorsAndVariatesTestDataForTrial(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables);
		DataSet trialDataSet = createTrialDataSet(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables);
		DataSet meansDataSet = createMeansDataSet(factors, factorVariables, variates, variateVariables);
		List<DataSet> meansDataSets = createMeansDataSets(factors, factorVariables, variates, variateVariables);
		List<DataSet> trialDataSets = createTrialDataSets(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables);
		
		variatesCheckBoxState = createVariatesCheckBoxState(variates);
		
		doReturn(datasetRefs).when(studyDataManager).getDatasetReferences(STUDY_ID);
		doReturn(trialDataSet).when(studyDataManager).getDataSet(TRIAL_DATASET_ID);
		doReturn(meansDataSet).when(studyDataManager).getDataSet(MEANS_DATASET_ID);
		doReturn(meansDataSets).when(studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.MEANS_DATA);
		doReturn(trialDataSets).when(studyDataManager).getDataSetsByType(STUDY_ID, DataSetType.SUMMARY_DATA);
		doReturn(createTrialEnvironments(factorVariables, variateVariables)).when(studyDataManager).getTrialEnvironmentsInDataset(MEANS_DATASET_ID);
		doReturn(createExperimentsWithTrialEnvironment(factors, factorVariables, variates, variateVariables)).when(studyDataManager).getExperimentsWithTrialEnvironment(TRIAL_DATASET_ID, MEANS_DATASET_ID, 0, Integer.MAX_VALUE);
		doReturn(createExperimentsWithTrialEnvironment(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables)).when(studyDataManager).getExperiments(TRIAL_DATASET_ID, 0, Integer.MAX_VALUE);
		
		
		
	}
	

	private List<DatasetReference> createDataSetRef() {
		List<DatasetReference> datasetRefs = new ArrayList<DatasetReference>();
		datasetRefs.add(new DatasetReference(TRIAL_DATASET_ID, ""));
		datasetRefs.add(new DatasetReference(MEANS_DATASET_ID, ""));
		return datasetRefs;
	}

	@Test
	public void testFillTableWithDatasetWithTrialInstanceAsSelectedFactor(){
		
		GxeTable gxeTable = spy(new GxeTable(studyDataManager, STUDY_ID, TRIAL_FACTOR, "", variatesCheckBoxState, listener));
	
		assertTrue("The Trial Instance Factor should always be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), TRIAL_FACTOR));
		assertFalse("The Site Factor is not selected so it should not be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), SITE_FACTOR));
		assertFalse("The Group Factor is not selected so it should not be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), GROUP_FACTOR));
		assertTrue(VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_1_MEANS));
		assertTrue(VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_2_MEANS));
		assertTrue(VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_3_MEANS));

		Object itemId = gxeTable.getItemIds().iterator().next();
		assertEquals("", TRIAL_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(TRIAL_FACTOR).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_1_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_1_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_2_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_2_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_3_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_3_MEANS).getValue()).getValue());
	}
	
	@Test
	public void testFillTableWithDatasetWithTrialEnvironmentAsSelectedFactor(){
		
		GxeTable gxeTable = spy(new GxeTable(studyDataManager, STUDY_ID, SITE_FACTOR, "", variatesCheckBoxState, listener));
		
		assertTrue("The Trial Instance Factor should always be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), TRIAL_FACTOR));
		assertTrue("The Site Factor is  selected so it should be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), SITE_FACTOR));
		assertFalse("The Group Factor is not selected so it should not be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), GROUP_FACTOR));
		assertTrue(VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_1_MEANS));
		assertTrue(VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_2_MEANS));
		assertTrue(VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_3_MEANS));
		
		Object itemId = gxeTable.getItemIds().iterator().next();
		assertEquals("", TRIAL_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(TRIAL_FACTOR).getValue()).getValue());
		assertEquals("", SITE_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(SITE_FACTOR).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_1_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_1_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_2_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_2_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_3_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_3_MEANS).getValue()).getValue());
	}
	
	@Test
	public void testFillTableWithDatasetAndTrialInstanceAsSelectedFactorAndWithSelectedGroupFactorName(){
		
		GxeTable gxeTable = spy(new GxeTable(studyDataManager, STUDY_ID, TRIAL_FACTOR, GROUP_FACTOR, variatesCheckBoxState, listener));
		
		assertTrue("The Trial Instance Factor should always be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), TRIAL_FACTOR));
		assertFalse("The Site Factor is not selected so it should not be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), SITE_FACTOR));
		assertTrue("The Group Factor is  selected so it should be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), GROUP_FACTOR));
		assertTrue(VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_1_MEANS));
		assertTrue(VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_2_MEANS));
		assertTrue(VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_3_MEANS));
		
		Object itemId = gxeTable.getItemIds().iterator().next();
		assertEquals("", TRIAL_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(TRIAL_FACTOR).getValue()).getValue());
		assertEquals("", GROUP_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(GROUP_FACTOR).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_1_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_1_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_2_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_2_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_3_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_3_MEANS).getValue()).getValue());
		
	}
	
	@Test
	public void testFillTableWithDatasetAndTrialEnvironmentAsSelectedFactorAndWithSelectedGroupFactorName(){
		
		GxeTable gxeTable = spy(new GxeTable(studyDataManager, STUDY_ID, SITE_FACTOR, GROUP_FACTOR, variatesCheckBoxState, listener));
		
		assertTrue("The Trial Instance Factor should always be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), TRIAL_FACTOR));
		assertTrue("The Site Factor is selected so it should be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), SITE_FACTOR));
		assertTrue("The Group Factor is selected so it should be visible", ArrayUtils.contains(gxeTable.getVisibleColumns(), GROUP_FACTOR));
		assertTrue(VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_1_MEANS));
		assertTrue(VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_2_MEANS));
		assertTrue(VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table", ArrayUtils.contains(gxeTable.getVisibleColumns(), VARIATE_NAME_3_MEANS));
		
		Object itemId = gxeTable.getItemIds().iterator().next();
		assertEquals("", TRIAL_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(TRIAL_FACTOR).getValue()).getValue());
		assertEquals("", SITE_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(SITE_FACTOR).getValue()).getValue());
		assertEquals("", GROUP_FACTOR_VALUE ,((Label)gxeTable.getItem(itemId).getItemProperty(GROUP_FACTOR).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_1_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_1_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_2_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_2_MEANS).getValue()).getValue());
		assertEquals("", "0 (" + VARIATE_NAME_3_HERITABILITY_VALUE + ")" ,((Label)gxeTable.getItem(itemId).getItemProperty(VARIATE_NAME_3_MEANS).getValue()).getValue());
		
	}
	
	private List<DataSet> createMeansDataSets(
			List<VariableType> factors,
			List<Variable> factorVariables,
			List<VariableType> variates,
			List<Variable> variateVariables) {
		
		List<DataSet> dataSets = new ArrayList<DataSet>();
		dataSets.add(createMeansDataSet(factors, factorVariables, variates, variateVariables));
		
		return dataSets;
	}
	private List<DataSet> createTrialDataSets(
			List<VariableType> factors,
			List<Variable> factorVariables,
			List<VariableType> variates,
			List<Variable> variateVariables) {
		
		List<DataSet> dataSets = new ArrayList<DataSet>();
		dataSets.add(createTrialDataSet(factors, factorVariables, variates, variateVariables));
		
		return dataSets;
	}
	
	private DataSet createMeansDataSet(
			List<VariableType> factors,
			List<Variable> factorVariables,
			List<VariableType> variates,
			List<Variable> variateVariables) {
		
		DataSet meansDataSet = new DataSet();
		meansDataSet.setDataSetType(DataSetType.MEANS_DATA);
		meansDataSet.setName("MEANS-DATA");
		meansDataSet.setId(MEANS_DATASET_ID);
		
		VariableTypeList variableTypeList = new VariableTypeList();
		for (VariableType f:factors){
			variableTypeList.add(f);
		}
		for (VariableType v:variates){
			variableTypeList.add(v);
		}
		meansDataSet.setVariableTypes(variableTypeList);
		return meansDataSet;
	}
	
	private DataSet createTrialDataSet(
			List<VariableType> factors,
			List<Variable> factorVariables,
			List<VariableType> variates,
			List<Variable> variateVariables) {
		
		DataSet trialDataSet = new DataSet();
		trialDataSet.setDataSetType(DataSetType.SUMMARY_DATA);
		trialDataSet.setName("SUMMARY-DATA");
		trialDataSet.setId(TRIAL_DATASET_ID);
		
		VariableTypeList variableTypeList = new VariableTypeList();
		for (VariableType f:factors){
			variableTypeList.add(f);
		}
		for (VariableType v:variates){
			variableTypeList.add(v);
		}
		trialDataSet.setVariableTypes(variableTypeList);
	
		return trialDataSet;
	}
	
	private void createFactorsAndVariatesTestDataForMeans(
			List<VariableType> factors,
			List<Variable> factorVariables,
			List<VariableType> variates,
			List<Variable> variateVariables) {
		

		int rank = 1;
		
		addVariableToList(factors,factorVariables,
				TRIAL_FACTOR_ID,
				TRIAL_FACTOR,rank++,
				TRIAL_FACTOR_VALUE,
				PhenotypicType.TRIAL_ENVIRONMENT,
				NUMERIC_VARIABLE, TRIAL_INSTANCE_ROLE, null, null, null);
		
		addVariableToList(factors,factorVariables,
				SITE_FACTOR_ID,
				SITE_FACTOR,rank++,
				SITE_FACTOR_VALUE,
				PhenotypicType.TRIAL_ENVIRONMENT,
				CHARACTER_VARIABLE, TRIAL_ENVIRONMENT_ROLE, null, null, null);
		
		addVariableToList(factors,factorVariables,
				GROUP_FACTOR_ID,
				GROUP_FACTOR,rank++,
				GROUP_FACTOR_VALUE,
				PhenotypicType.TRIAL_ENVIRONMENT,
				CHARACTER_VARIABLE, TRIAL_ENVIRONMENT_ROLE, null, null, null);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_1_MEANS_ID,
				VARIATE_NAME_1_MEANS,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, LS_MEAN_METHOD);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_1_UNITERRORS_ID, 
				VARIATE_NAME_1_UNITERRORS,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, UNIT_ERROR_METHOD);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_2_MEANS_ID,
				VARIATE_NAME_2_MEANS,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, LS_MEAN_METHOD);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_2_UNITERRORS_ID,
				VARIATE_NAME_2_UNITERRORS,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, UNIT_ERROR_METHOD);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_3_MEANS_ID,
				VARIATE_NAME_3_MEANS,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, LS_MEAN_METHOD);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_3_UNITERRORS_ID,
				VARIATE_NAME_3_UNITERRORS,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, UNIT_ERROR_METHOD);
		
				
	}
	
	private void createFactorsAndVariatesTestDataForTrial(
			List<VariableType> factors,
			List<Variable> factorVariables,
			List<VariableType> variates,
			List<Variable> variateVariables) {
		
		int rank = 1;
		
		addVariableToList(factors,factorVariables,
				TRIAL_FACTOR_ID,
				TRIAL_FACTOR,rank++,
				TRIAL_FACTOR_VALUE,
				PhenotypicType.TRIAL_ENVIRONMENT,
				NUMERIC_VARIABLE, TRIAL_INSTANCE_ROLE, null, null, null);
		
		addVariableToList(factors,factorVariables,
				SITE_FACTOR_ID,
				SITE_FACTOR,rank++,
				SITE_FACTOR_VALUE,
				PhenotypicType.TRIAL_ENVIRONMENT,
				CHARACTER_VARIABLE, TRIAL_ENVIRONMENT_ROLE, null, null, null);
		
		addVariableToList(factors,factorVariables,
				GROUP_FACTOR_ID,
				GROUP_FACTOR,rank++,
				GROUP_FACTOR_VALUE,
				PhenotypicType.TRIAL_ENVIRONMENT,
				CHARACTER_VARIABLE, TRIAL_ENVIRONMENT_ROLE, null, null, null);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_1_ID,
				VARIATE_NAME_1,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, null);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_2_ID,
				VARIATE_NAME_2,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, null);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_3_ID,
				VARIATE_NAME_3,rank++,
				"1",PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, null);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_1_HERITABILITY_ID,
				VARIATE_NAME_1_HERITABILITY,rank++,
				VARIATE_NAME_1_HERITABILITY_VALUE,PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, null);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_2_HERITABILITY_ID,
				VARIATE_NAME_2_HERITABILITY,rank++,
				VARIATE_NAME_2_HERITABILITY_VALUE,PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, null);
		
		addVariableToList(variates,variateVariables,
				VARIATE_NAME_3_HERITABILITY_ID,
				VARIATE_NAME_3_HERITABILITY,rank++,
				VARIATE_NAME_3_HERITABILITY_VALUE,PhenotypicType.VARIATE,
				NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, null);
		
				
	}
	
	private void addVariableToList(List<VariableType> variableTypes,
			List<Variable> variables,int id,String variableName, int rank, String value,
			PhenotypicType phenotypicType, Term dataType, Term role, Term property, Term scale, Term method) {
		StandardVariable standardVariable = createStardardVariableTestData(id,phenotypicType,variableName, dataType, role, property, scale, method);
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
	
	private StandardVariable createStardardVariableTestData(int id,PhenotypicType type, String name, Term dataType, Term storedIn, Term property, Term scale,Term method) {

		StandardVariable stdVar = new StandardVariable();
		stdVar.setId(id);
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
		
		
		stdVar.setMethod(method);
		return stdVar;
	}
	
	
	private TrialEnvironments createTrialEnvironments(List<Variable> factorVariables, List<Variable> variateVariables){
		
		TrialEnvironments envs = new TrialEnvironments();
		envs.add(createTrialEnvironment(factorVariables, variateVariables));
		
		return envs;
	}
	
	private TrialEnvironment createTrialEnvironment(List<Variable> factorVariables, List<Variable> variateVariables){
		
		TrialEnvironment trialEnvironment = new TrialEnvironment(1, new VariableList());
		
		for (Variable f:factorVariables){
			trialEnvironment.getVariables().add(f);
		}
		for (Variable v:variateVariables){
			trialEnvironment.getVariables().add(v);
		}
		
		return trialEnvironment;
	}
	
	private Map<String, Boolean> createVariatesCheckBoxState(List<VariableType> variates) {
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		for (VariableType v:variates){
			map.put(v.getLocalName(), true );
		}
		return map;
	}
	
	private VariableList convertToVariableList(List<Variable> variables){
		VariableList variableList = new VariableList();
		for (Variable v: variables){
			variableList.add(v);
		}
		return variableList;
	}
	
	private List<Experiment> createExperimentsWithTrialEnvironment(List<VariableType> factors, List<Variable> factorVariables,
			List<VariableType> variates, List<Variable> variateVariables) {
		List<Experiment> experiments = new ArrayList<Experiment>();
		Experiment exp = new Experiment();
		exp.setId(1);
		exp.setLocationId(1);
		exp.setFactors(convertToVariableList(factorVariables));
		exp.setVariates(convertToVariableList(variateVariables));
		experiments.add(exp);
		return experiments;
	}
}

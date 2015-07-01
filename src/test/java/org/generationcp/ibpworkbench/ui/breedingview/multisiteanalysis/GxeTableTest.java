
package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.ui.Label;
import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private static final String VARIATE_NAME_1_MEANS = "EPP_Means";
	private static final String VARIATE_NAME_1_UNITERRORS = "EPP_UnitErrors";
	private static final String VARIATE_NAME_1_HERITABILITY = "EPP_Heritability";
	private static final String VARIATE_NAME_2 = "PH";
	private static final String VARIATE_NAME_2_MEANS = "PH_Means";
	private static final String VARIATE_NAME_2_UNITERRORS = "PH_UnitErrors";
	private static final String VARIATE_NAME_2_HERITABILITY = "PH_Heritability";
	private static final String VARIATE_NAME_3 = "EARH";
	private static final String VARIATE_NAME_3_MEANS = "EARH_Means";
	private static final String VARIATE_NAME_3_UNITERRORS = "EARH_UnitErrors";
	private static final String VARIATE_NAME_3_HERITABILITY = "EARH_Heritability";

	private static final int VARIATE_NAME_1_ID = 1000;
	private static final int VARIATE_NAME_1_MEANS_ID = 1001;
	private static final int VARIATE_NAME_1_UNITERRORS_ID = 1002;
	private static final int VARIATE_NAME_1_HERITABILITY_ID = 1003;
	private static final int VARIATE_NAME_2_ID = 1004;
	private static final int VARIATE_NAME_2_MEANS_ID = 1005;
	private static final int VARIATE_NAME_2_UNITERRORS_ID = 1006;
	private static final int VARIATE_NAME_2_HERITABILITY_ID = 1007;
	private static final int VARIATE_NAME_3_ID = 1008;
	private static final int VARIATE_NAME_3_MEANS_ID = 1009;
	private static final int VARIATE_NAME_3_UNITERRORS_ID = 1010;
	private static final int VARIATE_NAME_3_HERITABILITY_ID = 1011;

	private static final String VARIATE_NAME_1_HERITABILITY_VALUE = "101";
	private static final String VARIATE_NAME_2_HERITABILITY_VALUE = "102";
	private static final String VARIATE_NAME_3_HERITABILITY_VALUE = "103";

	private static final int CATEGORICAL_VARIATE_ENUM_ID = 1;
	private static final String CATEGORICAL_VARIATE_ENUM_NAME = "5";
	private static final String CATEGORICAL_VARIATE_ENUM_DESCRIPTION = "Very Severe";

	private static final Term NUMERIC_VARIABLE = new Term(TermId.NUMERIC_VARIABLE.getId(), "", "");
	private static final Term CATEGORICAL_VARIABLE = new Term(TermId.CATEGORICAL_VARIABLE.getId(), "", "");
	private static final Term CHARACTER_VARIABLE = new Term(TermId.CHARACTER_VARIABLE.getId(), "", "");
	private static final Term LS_MEAN_METHOD = new Term(9999, "LS MEAN", "LS MEAN");
	private static final Term UNIT_ERROR_METHOD = new Term(9999, "ERROR ESTIMATE", "ERROR ESTIMATE");

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
	public void setUp() throws MiddlewareException {
		MockitoAnnotations.initMocks(this);

		// means dataset
		List<DMSVariableType> factors = new ArrayList<DMSVariableType>();
		List<DMSVariableType> variates = new ArrayList<DMSVariableType>();
		List<Variable> factorVariables = new ArrayList<Variable>();
		List<Variable> variateVariables = new ArrayList<Variable>();

		// trial dataset
		List<DMSVariableType> trialFactors = new ArrayList<DMSVariableType>();
		List<DMSVariableType> trialVariates = new ArrayList<DMSVariableType>();
		List<Variable> trialFactorVariables = new ArrayList<Variable>();
		List<Variable> trialVariateVariables = new ArrayList<Variable>();

		List<DatasetReference> datasetRefs = this.createDataSetRef();

		this.createFactorsAndVariatesTestDataForMeans(factors, factorVariables, variates, variateVariables);
		this.createFactorsAndVariatesTestDataForTrial(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables);
		DataSet trialDataSet = this.createTrialDataSet(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables);
		DataSet meansDataSet = this.createMeansDataSet(factors, factorVariables, variates, variateVariables);
		List<DataSet> meansDataSets = this.createMeansDataSets(factors, factorVariables, variates, variateVariables);
		List<DataSet> trialDataSets = this.createTrialDataSets(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables);

		this.variatesCheckBoxState = this.createVariatesCheckBoxState(variates);

		Mockito.doReturn(datasetRefs).when(this.studyDataManager).getDatasetReferences(GxeTableTest.STUDY_ID);
		Mockito.doReturn(trialDataSet).when(this.studyDataManager).getDataSet(GxeTableTest.TRIAL_DATASET_ID);
		Mockito.doReturn(meansDataSet).when(this.studyDataManager).getDataSet(GxeTableTest.MEANS_DATASET_ID);
		Mockito.doReturn(meansDataSets).when(this.studyDataManager).getDataSetsByType(GxeTableTest.STUDY_ID, DataSetType.MEANS_DATA);
		Mockito.doReturn(trialDataSets).when(this.studyDataManager).getDataSetsByType(GxeTableTest.STUDY_ID, DataSetType.SUMMARY_DATA);
		Mockito.doReturn(this.createTrialEnvironments(factorVariables, variateVariables)).when(this.studyDataManager)
				.getTrialEnvironmentsInDataset(GxeTableTest.MEANS_DATASET_ID);
		Mockito.doReturn(this.createExperimentsWithTrialEnvironment(factors, factorVariables, variates, variateVariables))
				.when(this.studyDataManager)
				.getExperimentsWithTrialEnvironment(GxeTableTest.TRIAL_DATASET_ID, GxeTableTest.MEANS_DATASET_ID, 0, Integer.MAX_VALUE);
		Mockito.doReturn(
				this.createExperimentsWithTrialEnvironment(trialFactors, trialFactorVariables, trialVariates, trialVariateVariables))
				.when(this.studyDataManager).getExperiments(GxeTableTest.TRIAL_DATASET_ID, 0, Integer.MAX_VALUE);

	}

	private List<DatasetReference> createDataSetRef() {
		List<DatasetReference> datasetRefs = new ArrayList<>();
		datasetRefs.add(new DatasetReference(GxeTableTest.TRIAL_DATASET_ID, ""));
		datasetRefs.add(new DatasetReference(GxeTableTest.MEANS_DATASET_ID, ""));
		return datasetRefs;
	}

	@Test
	public void testFillTableWithDatasetWithTrialInstanceAsSelectedFactor() {

		GxeTable gxeTable =
				Mockito.spy(new GxeTable(this.studyDataManager, GxeTableTest.STUDY_ID, GxeTableTest.TRIAL_FACTOR, "",
						this.variatesCheckBoxState, this.listener));

		Assert.assertTrue("The Trial Instance Factor should always be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.TRIAL_FACTOR));
		Assert.assertFalse("The Site Factor is not selected so it should not be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertFalse("The Group Factor is not selected so it should not be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.TRIAL_FACTOR_VALUE,
				((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.TRIAL_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());
	}

	@Test
	public void testFillTableWithDatasetWithTrialEnvironmentAsSelectedFactor() {

		GxeTable gxeTable =
				Mockito.spy(new GxeTable(this.studyDataManager, GxeTableTest.STUDY_ID, GxeTableTest.SITE_FACTOR, "",
						this.variatesCheckBoxState, this.listener));

		Assert.assertTrue("The Trial Instance Factor should always be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.TRIAL_FACTOR));
		Assert.assertTrue("The Site Factor is  selected so it should be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertFalse("The Group Factor is not selected so it should not be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.TRIAL_FACTOR_VALUE,
				((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.TRIAL_FACTOR).getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.SITE_FACTOR_VALUE, ((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.SITE_FACTOR)
				.getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());
	}

	@Test
	public void testFillTableWithDatasetAndTrialInstanceAsSelectedFactorAndWithSelectedGroupFactorName() {

		GxeTable gxeTable =
				Mockito.spy(new GxeTable(this.studyDataManager, GxeTableTest.STUDY_ID, GxeTableTest.TRIAL_FACTOR,
						GxeTableTest.GROUP_FACTOR, this.variatesCheckBoxState, this.listener));

		Assert.assertTrue("The Trial Instance Factor should always be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.TRIAL_FACTOR));
		Assert.assertFalse("The Site Factor is not selected so it should not be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertTrue("The Group Factor is  selected so it should be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.TRIAL_FACTOR_VALUE,
				((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.TRIAL_FACTOR).getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.GROUP_FACTOR_VALUE,
				((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.GROUP_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());

	}

	@Test
	public void testFillTableWithDatasetAndTrialEnvironmentAsSelectedFactorAndWithSelectedGroupFactorName() {

		GxeTable gxeTable =
				Mockito.spy(new GxeTable(this.studyDataManager, GxeTableTest.STUDY_ID, GxeTableTest.SITE_FACTOR, GxeTableTest.GROUP_FACTOR,
						this.variatesCheckBoxState, this.listener));

		Assert.assertTrue("The Trial Instance Factor should always be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.TRIAL_FACTOR));
		Assert.assertTrue("The Site Factor is selected so it should be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertTrue("The Group Factor is selected so it should be visible",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
				ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.TRIAL_FACTOR_VALUE,
				((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.TRIAL_FACTOR).getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.SITE_FACTOR_VALUE, ((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.SITE_FACTOR)
				.getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.GROUP_FACTOR_VALUE,
				((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.GROUP_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")", ((Label) gxeTable.getItem(itemId)
				.getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());

	}

	private List<DataSet> createMeansDataSets(List<DMSVariableType> factors, List<Variable> factorVariables, List<DMSVariableType> variates,
			List<Variable> variateVariables) {

		List<DataSet> dataSets = new ArrayList<>();
		dataSets.add(this.createMeansDataSet(factors, factorVariables, variates, variateVariables));

		return dataSets;
	}

	private List<DataSet> createTrialDataSets(List<DMSVariableType> factors, List<Variable> factorVariables, List<DMSVariableType> variates,
			List<Variable> variateVariables) {

		List<DataSet> dataSets = new ArrayList<>();
		dataSets.add(this.createTrialDataSet(factors, factorVariables, variates, variateVariables));

		return dataSets;
	}

	private DataSet createMeansDataSet(List<DMSVariableType> factors, List<Variable> factorVariables, List<DMSVariableType> variates,
			List<Variable> variateVariables) {

		DataSet meansDataSet = new DataSet();
		meansDataSet.setDataSetType(DataSetType.MEANS_DATA);
		meansDataSet.setName("MEANS-DATA");
		meansDataSet.setId(GxeTableTest.MEANS_DATASET_ID);

		VariableTypeList variableTypeList = new VariableTypeList();
		for (DMSVariableType f : factors) {
			variableTypeList.add(f);
		}
		for (DMSVariableType v : variates) {
			variableTypeList.add(v);
		}
		meansDataSet.setVariableTypes(variableTypeList);
		return meansDataSet;
	}

	private DataSet createTrialDataSet(List<DMSVariableType> factors, List<Variable> factorVariables, List<DMSVariableType> variates,
			List<Variable> variateVariables) {

		DataSet trialDataSet = new DataSet();
		trialDataSet.setDataSetType(DataSetType.SUMMARY_DATA);
		trialDataSet.setName("SUMMARY-DATA");
		trialDataSet.setId(GxeTableTest.TRIAL_DATASET_ID);

		VariableTypeList variableTypeList = new VariableTypeList();
		for (DMSVariableType f : factors) {
			variableTypeList.add(f);
		}
		for (DMSVariableType v : variates) {
			variableTypeList.add(v);
		}
		trialDataSet.setVariableTypes(variableTypeList);

		return trialDataSet;
	}

	private void createFactorsAndVariatesTestDataForMeans(List<DMSVariableType> factors, List<Variable> factorVariables,
			List<DMSVariableType> variates, List<Variable> variateVariables) {

		int rank = 1;

		this.addVariableToList(factors, factorVariables, GxeTableTest.TRIAL_FACTOR_ID, GxeTableTest.TRIAL_FACTOR, rank++,
				GxeTableTest.TRIAL_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
				GxeTableTest.TRIAL_INSTANCE_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.SITE_FACTOR_ID, GxeTableTest.SITE_FACTOR, rank++,
				GxeTableTest.SITE_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.CHARACTER_VARIABLE,
				GxeTableTest.TRIAL_ENVIRONMENT_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.GROUP_FACTOR_ID, GxeTableTest.GROUP_FACTOR, rank++,
				GxeTableTest.GROUP_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.CHARACTER_VARIABLE,
				GxeTableTest.TRIAL_ENVIRONMENT_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_1_MEANS_ID, GxeTableTest.VARIATE_NAME_1_MEANS, rank++,
				"1", PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null,
				GxeTableTest.LS_MEAN_METHOD);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_1_UNITERRORS_ID,
				GxeTableTest.VARIATE_NAME_1_UNITERRORS, rank++, "1", PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE,
				GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, GxeTableTest.UNIT_ERROR_METHOD);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_2_MEANS_ID, GxeTableTest.VARIATE_NAME_2_MEANS, rank++,
				"1", PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null,
				GxeTableTest.LS_MEAN_METHOD);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_2_UNITERRORS_ID,
				GxeTableTest.VARIATE_NAME_2_UNITERRORS, rank++, "1", PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE,
				GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, GxeTableTest.UNIT_ERROR_METHOD);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_3_MEANS_ID, GxeTableTest.VARIATE_NAME_3_MEANS, rank++,
				"1", PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null,
				GxeTableTest.LS_MEAN_METHOD);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_3_UNITERRORS_ID,
				GxeTableTest.VARIATE_NAME_3_UNITERRORS, rank++, "1", PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE,
				GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, GxeTableTest.UNIT_ERROR_METHOD);

	}

	private void createFactorsAndVariatesTestDataForTrial(List<DMSVariableType> factors, List<Variable> factorVariables,
			List<DMSVariableType> variates, List<Variable> variateVariables) {

		int rank = 1;

		this.addVariableToList(factors, factorVariables, GxeTableTest.TRIAL_FACTOR_ID, GxeTableTest.TRIAL_FACTOR, rank++,
				GxeTableTest.TRIAL_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
				GxeTableTest.TRIAL_INSTANCE_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.SITE_FACTOR_ID, GxeTableTest.SITE_FACTOR, rank++,
				GxeTableTest.SITE_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.CHARACTER_VARIABLE,
				GxeTableTest.TRIAL_ENVIRONMENT_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.GROUP_FACTOR_ID, GxeTableTest.GROUP_FACTOR, rank++,
				GxeTableTest.GROUP_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.CHARACTER_VARIABLE,
				GxeTableTest.TRIAL_ENVIRONMENT_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_1_ID, GxeTableTest.VARIATE_NAME_1, rank++, "1",
				PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_2_ID, GxeTableTest.VARIATE_NAME_2, rank++, "1",
				PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_3_ID, GxeTableTest.VARIATE_NAME_3, rank++, "1",
				PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_1_HERITABILITY_ID,
				GxeTableTest.VARIATE_NAME_1_HERITABILITY, rank++, GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE, PhenotypicType.VARIATE,
				GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_2_HERITABILITY_ID,
				GxeTableTest.VARIATE_NAME_2_HERITABILITY, rank++, GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE, PhenotypicType.VARIATE,
				GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_3_HERITABILITY_ID,
				GxeTableTest.VARIATE_NAME_3_HERITABILITY, rank++, GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE, PhenotypicType.VARIATE,
				GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

	}

	private void addVariableToList(List<DMSVariableType> variableTypes, List<Variable> variables, int id, String variableName, int rank,
			String value, PhenotypicType phenotypicType, Term dataType, Term role, Term property, Term scale, Term method) {
		StandardVariable standardVariable =
				this.createStardardVariableTestData(id, phenotypicType, variableName, dataType, role, property, scale, method);
		DMSVariableType variableType = this.createVariableTypeTestData(variableName, rank, standardVariable);
		variableTypes.add(variableType);
		variables.add(this.createVariableTestData(variableType, value));
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
		return variableType;
	}

	private StandardVariable createStardardVariableTestData(int id, PhenotypicType type, String name, Term dataType, Term storedIn,
			Term property, Term scale, Term method) {

		StandardVariable stdVar = new StandardVariable();
		stdVar.setId(id);
		stdVar.setPhenotypicType(type);
		stdVar.setName(name);
		stdVar.setDataType(dataType);
		
		if (dataType.getId() == GxeTableTest.CATEGORICAL_VARIABLE.getId()) {
			List<Enumeration> validValues = new ArrayList<Enumeration>();
			validValues.add(new Enumeration(GxeTableTest.CATEGORICAL_VARIATE_ENUM_ID, GxeTableTest.CATEGORICAL_VARIATE_ENUM_NAME,
					GxeTableTest.CATEGORICAL_VARIATE_ENUM_DESCRIPTION, 1));
			stdVar.setEnumerations(validValues);
		}

		stdVar.setMethod(method);
		return stdVar;
	}

	private TrialEnvironments createTrialEnvironments(List<Variable> factorVariables, List<Variable> variateVariables) {

		TrialEnvironments envs = new TrialEnvironments();
		envs.add(this.createTrialEnvironment(factorVariables, variateVariables));

		return envs;
	}

	private TrialEnvironment createTrialEnvironment(List<Variable> factorVariables, List<Variable> variateVariables) {

		TrialEnvironment trialEnvironment = new TrialEnvironment(1, new VariableList());

		for (Variable f : factorVariables) {
			trialEnvironment.getVariables().add(f);
		}
		for (Variable v : variateVariables) {
			trialEnvironment.getVariables().add(v);
		}

		return trialEnvironment;
	}

	private Map<String, Boolean> createVariatesCheckBoxState(List<DMSVariableType> variates) {
		Map<String, Boolean> map = new HashMap<>();
		for (DMSVariableType v : variates) {
			map.put(v.getLocalName(), true);
		}
		return map;
	}

	private VariableList convertToVariableList(List<Variable> variables) {
		VariableList variableList = new VariableList();
		for (Variable v : variables) {
			variableList.add(v);
		}
		return variableList;
	}

	private List<Experiment> createExperimentsWithTrialEnvironment(List<DMSVariableType> factors, List<Variable> factorVariables,
			List<DMSVariableType> variates, List<Variable> variateVariables) {
		List<Experiment> experiments = new ArrayList<>();
		Experiment exp = new Experiment();
		exp.setId(1);
		exp.setLocationId(1);
		exp.setFactors(this.convertToVariableList(factorVariables));
		exp.setVariates(this.convertToVariableList(variateVariables));
		experiments.add(exp);
		return experiments;
	}
}

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.Property;
import com.vaadin.ui.Label;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GxeTableTest {

	private static final int STUDY_ID = 1;
	private static final int MEANS_DATASET_ID = 2;
	private static final int STUDY_DATASET_ID = 1;
	private static final int PLOT_DATASET_ID = 3;
	private static final int SUMMARY_STATISTICS_DATASET_ID = 4;

	private static final String STUDY_FACTOR = "TRIAL";
	private static final int STUDY_FACTOR_ID = TermId.TRIAL_INSTANCE_FACTOR.getId();
	private static final String STUDY_FACTOR_VALUE = "1";
	private static final String SITE_FACTOR = "SITE";
	private static final int SITE_FACTOR_ID = 8888;
	private static final String SITE_FACTOR_VALUE = "CIMMYT, Harrare";
	private static final String GROUP_FACTOR = "MegaEnv";
	private static final int GROUP_FACTOR_ID = 7777;
	private static final String GROUP_FACTOR_VALUE = "Wet Season";
	private static final String LOCATION_ID_FACTOR = "LOCATION_ID";
	private static final int LOCATION_ID_FACTOR_ID = 9999;
	private static final String LOCATION_ID_FACTOR_VALUE = "100";

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

	private static final Integer HERITABILITY_METHOD_ID = 1;
	private static final String HERITABILITY_METHOD_NAME = "Heritability";

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

	@Mock
	private Property.ValueChangeListener listener;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private OntologyVariableService ontologyVariableService;

	private Map<String, Boolean> variatesCheckBoxState;

	@Before
	public void setUp() throws MiddlewareException {
		MockitoAnnotations.initMocks(this);

		// means dataset
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();
		final List<DMSVariableType> variates = new ArrayList<DMSVariableType>();
		final List<Variable> factorVariables = new ArrayList<Variable>();
		final List<Variable> variateVariables = new ArrayList<Variable>();

		// study dataset
		final List<DMSVariableType> studyFactors = new ArrayList<DMSVariableType>();
		final List<DMSVariableType> studyVariates = new ArrayList<DMSVariableType>();
		final List<Variable> trialFactorVariables = new ArrayList<Variable>();
		final List<Variable> studyVariateVariables = new ArrayList<Variable>();

		// summary dataset
		final List<DMSVariableType> summaryStatisticsFactors = new ArrayList<DMSVariableType>();
		final List<DMSVariableType> summaryStatisticsVariates = new ArrayList<DMSVariableType>();
		final List<Variable> summaryStatisticsFactorVariables = new ArrayList<Variable>();
		final List<Variable> summaryStatisticsVariateVariables = new ArrayList<Variable>();

		// plot dataset
		final List<DMSVariableType> plotFactors = new ArrayList<DMSVariableType>();
		final List<DMSVariableType> plotVariates = new ArrayList<DMSVariableType>();
		final List<Variable> plotFactorVariables = new ArrayList<Variable>();
		final List<Variable> plotVariateVariables = new ArrayList<Variable>();

		final List<DatasetReference> datasetRefs = this.createDataSetRef();

		this.createFactorsAndVariatesTestDataForMeans(factors, factorVariables, variates, variateVariables);
		this.createFactorsAndVariatesTestDataForSummary(studyFactors, trialFactorVariables, studyVariates, studyVariateVariables);
		this.createFactorsAndVariatesTestDataForSummaryStatistics(summaryStatisticsFactors, summaryStatisticsFactorVariables,
			summaryStatisticsVariates, summaryStatisticsVariateVariables);
		this.createFactorsAndVariatesTestDataForPlot(plotFactors, plotFactorVariables,
			plotVariates, plotVariateVariables);

		final DataSet dataSet =
			this.createDataSet(GxeTableTest.STUDY_DATASET_ID, DatasetTypeEnum.SUMMARY_DATA, "SUMMARY-DATA", studyFactors,
				trialFactorVariables, studyVariates, studyVariateVariables);
		final DataSet plotDataSet =
			this.createDataSet(GxeTableTest.PLOT_DATASET_ID, DatasetTypeEnum.PLOT_DATA, "PLOT-DATA", plotFactors, plotFactorVariables,
				plotVariates,
				plotVariateVariables);
		final DataSet meansDataSet =
			this.createDataSet(GxeTableTest.MEANS_DATASET_ID, DatasetTypeEnum.MEANS_DATA, "MEANS-DATA", factors, factorVariables, variates,
				variateVariables);
		final DataSet summaryDataSet =
			this.createDataSet(GxeTableTest.STUDY_DATASET_ID, DatasetTypeEnum.SUMMARY_DATA, "SUMMARY-DATA", studyFactors,
				trialFactorVariables, studyVariates, studyVariateVariables);
		final DataSet summaryStatisticsDataSet =
			this.createDataSet(GxeTableTest.SUMMARY_STATISTICS_DATASET_ID, DatasetTypeEnum.SUMMARY_STATISTICS_DATA,
				"SUMMARY-STATISTICS-DATA", studyFactors,
				trialFactorVariables, studyVariates, studyVariateVariables);

		this.variatesCheckBoxState = this.createVariatesCheckBoxState(variates);

		Mockito.doReturn(datasetRefs).when(this.studyDataManager).getDatasetReferences(GxeTableTest.STUDY_ID);
		Mockito.doReturn(dataSet).when(this.studyDataManager).getDataSet(GxeTableTest.STUDY_DATASET_ID);
		Mockito.doReturn(meansDataSet).when(this.studyDataManager).getDataSet(GxeTableTest.MEANS_DATASET_ID);
		Mockito.doReturn(Arrays.asList(meansDataSet)).when(this.studyDataManager)
			.getDataSetsByType(GxeTableTest.STUDY_ID, DatasetTypeEnum.MEANS_DATA.getId());
		Mockito.doReturn(summaryDataSet).when(this.studyDataManager)
			.findOneDataSetByType(GxeTableTest.STUDY_ID, DatasetTypeEnum.SUMMARY_DATA.getId());
		Mockito.doReturn(this.createStudyEnvironments(factorVariables, variateVariables)).when(this.studyDataManager)
			.getTrialEnvironmentsInDataset(GxeTableTest.MEANS_DATASET_ID);
		Mockito.doReturn(plotDataSet).when(this.studyDataManager)
			.findOneDataSetByType(GxeTableTest.STUDY_ID, DatasetTypeEnum.PLOT_DATA.getId());
		Mockito.doReturn(summaryStatisticsDataSet).when(this.studyDataManager)
			.findOneDataSetByType(GxeTableTest.STUDY_ID, DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId());

		Mockito.doReturn(this.createExperimentsWithStudyEnvironment(factors, factorVariables, variates, variateVariables))
			.when(this.studyDataManager)
			.getExperimentsWithTrialEnvironment(GxeTableTest.STUDY_DATASET_ID, GxeTableTest.MEANS_DATASET_ID, 0, Integer.MAX_VALUE);
		Mockito.doReturn(
				this.createExperimentsWithStudyEnvironment(studyFactors, trialFactorVariables, studyVariates, studyVariateVariables))
			.when(this.studyDataManager).getExperiments(GxeTableTest.STUDY_DATASET_ID, 0, Integer.MAX_VALUE);
		Mockito.doReturn(
				this.createExperimentsWithStudyEnvironment(summaryStatisticsFactors, summaryStatisticsFactorVariables,
					summaryStatisticsVariates, summaryStatisticsVariateVariables))
			.when(this.studyDataManager).getExperiments(GxeTableTest.SUMMARY_STATISTICS_DATASET_ID, 0, Integer.MAX_VALUE);
		final MultiKeyMap multiKeyMap = MultiKeyMap.decorate(new LinkedMap());

		multiKeyMap.put(VARIATE_NAME_1_ID, HERITABILITY_METHOD_ID, VARIATE_NAME_1_HERITABILITY_ID);
		multiKeyMap.put(VARIATE_NAME_2_ID, HERITABILITY_METHOD_ID, VARIATE_NAME_2_HERITABILITY_ID);
		multiKeyMap.put(VARIATE_NAME_3_ID, HERITABILITY_METHOD_ID, VARIATE_NAME_3_HERITABILITY_ID);
		Mockito.doReturn(multiKeyMap).when(this.ontologyVariableService)
			.getAnalysisMethodsOfTraits(ArgumentMatchers.anyList(), ArgumentMatchers.anyList());

	}

	private List<DatasetReference> createDataSetRef() {
		final List<DatasetReference> datasetRefs = new ArrayList<>();
		datasetRefs.add(new DatasetReference(GxeTableTest.STUDY_DATASET_ID, ""));
		datasetRefs.add(new DatasetReference(GxeTableTest.MEANS_DATASET_ID, ""));
		return datasetRefs;
	}

	@Test
	public void testFillTableWithDatasetWithTrialInstanceAsSelectedFactor() throws Exception {

		Mockito.when(this.studyDataManager.isLocationIdVariable(GxeTableTest.STUDY_ID, STUDY_FACTOR)).thenReturn(false);

		final GxeTable gxeTable = new GxeTable(GxeTableTest.STUDY_ID, GxeTableTest.STUDY_FACTOR,

			"", this.variatesCheckBoxState, this.listener);
		gxeTable.setStudyDataManager(this.studyDataManager);
		gxeTable.setOntologyVariableService(this.ontologyVariableService);
		gxeTable.afterPropertiesSet();

		Assert.assertTrue(
			"The Trial Instance Factor should always be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.STUDY_FACTOR));
		Assert.assertFalse(
			"The Site Factor is not selected so it should not be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertFalse(
			"The Group Factor is not selected so it should not be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		final Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.STUDY_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.STUDY_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());
	}

	@Test
	public void testFillTableWithDatasetWithTrialEnvironmentAsSelectedFactor() throws Exception {

		Mockito.when(this.studyDataManager.isLocationIdVariable(GxeTableTest.STUDY_ID, SITE_FACTOR)).thenReturn(false);

		final GxeTable gxeTable =
			new GxeTable(GxeTableTest.STUDY_ID, GxeTableTest.SITE_FACTOR, "", this.variatesCheckBoxState, this.listener);
		gxeTable.setStudyDataManager(this.studyDataManager);
		gxeTable.setOntologyVariableService(this.ontologyVariableService);
		gxeTable.afterPropertiesSet();

		Assert.assertTrue(
			"The Trial Instance Factor should always be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.STUDY_FACTOR));
		Assert.assertTrue(
			"The Site Factor is  selected so it should be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertFalse(
			"The Group Factor is not selected so it should not be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		final Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.STUDY_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.STUDY_FACTOR).getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.SITE_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.SITE_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());
	}

	@Test
	public void testFillTableWithDatasetWithTrialLocationIDAsSelectedFactor() throws Exception {

		final BiMap<String, String> locationIdToNameMap = HashBiMap.create();
		locationIdToNameMap.put(LOCATION_ID_FACTOR_VALUE, "Some Location");

		Mockito.when(this.studyDataManager.isLocationIdVariable(GxeTableTest.STUDY_ID, LOCATION_ID_FACTOR)).thenReturn(true);
		Mockito.when(this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(GxeTableTest.STUDY_ID))
			.thenReturn(locationIdToNameMap);

		final GxeTable gxeTable =
			new GxeTable(GxeTableTest.STUDY_ID, GxeTableTest.LOCATION_ID_FACTOR, "", this.variatesCheckBoxState, this.listener);
		gxeTable.setStudyDataManager(this.studyDataManager);
		gxeTable.setOntologyVariableService(this.ontologyVariableService);
		gxeTable.afterPropertiesSet();

		Assert.assertTrue(
			"The Trial Instance Factor should always be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.STUDY_FACTOR));
		Assert.assertTrue(
			"The Location ID Factor is  selected so it should be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.LOCATION_ID_FACTOR));
		Assert.assertFalse(
			"The Group Factor is not selected so it should not be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		final Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.STUDY_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.STUDY_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "Some Location",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.LOCATION_ID_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());
	}

	@Test
	public void testFillTableWithDatasetAndTrialInstanceAsSelectedFactorAndWithSelectedGroupFactorName() throws Exception {

		final GxeTable gxeTable = new GxeTable(GxeTableTest.STUDY_ID, GxeTableTest.STUDY_FACTOR,

			GxeTableTest.GROUP_FACTOR, this.variatesCheckBoxState, this.listener);
		gxeTable.setStudyDataManager(this.studyDataManager);
		gxeTable.setOntologyVariableService(this.ontologyVariableService);
		gxeTable.afterPropertiesSet();

		Assert.assertTrue(
			"The Trial Instance Factor should always be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.STUDY_FACTOR));
		Assert.assertFalse(
			"The Site Factor is not selected so it should not be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertTrue(
			"The Group Factor is  selected so it should be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		final Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.STUDY_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.STUDY_FACTOR).getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.GROUP_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.GROUP_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());

	}

	@Test
	public void testGetSelectedEnvironments() throws Exception {
		final GxeTable gxeTable =
			new GxeTable(GxeTableTest.STUDY_ID, GxeTableTest.SITE_FACTOR, GxeTableTest.GROUP_FACTOR, this.variatesCheckBoxState,
				this.listener);
		gxeTable.setStudyDataManager(this.studyDataManager);
		gxeTable.setOntologyVariableService(this.ontologyVariableService);
		gxeTable.afterPropertiesSet();
		final List<Environment> environments = gxeTable.getSelectedEnvironments();
		Assert.assertNotNull(environments);
		Assert.assertEquals(1, environments.size());
		final Environment environment = environments.get(0);
		Assert.assertEquals(GxeTableTest.SITE_FACTOR_VALUE, environment.getName());
		Assert.assertTrue(environment.getActive());
		Assert.assertEquals(GxeTableTest.STUDY_FACTOR_VALUE, environment.getTrial());
		Assert.assertEquals(GxeTableTest.STUDY_FACTOR_VALUE, environment.getTrialno());
	}

	@Test
	public void testGetGxeENvironment() throws Exception {
		final GxeTable gxeTable =
			new GxeTable(GxeTableTest.STUDY_ID, GxeTableTest.SITE_FACTOR, GxeTableTest.GROUP_FACTOR, this.variatesCheckBoxState,
				this.listener);
		gxeTable.setStudyDataManager(this.studyDataManager);
		gxeTable.setOntologyVariableService(this.ontologyVariableService);
		gxeTable.afterPropertiesSet();
		final GxeEnvironment environment = gxeTable.getGxeEnvironment();
		Assert.assertNotNull(environment);
		Assert.assertEquals(1, environment.getLabels().size());
	}

	@Test

	public void testFillTableWithDatasetAndTrialEnvironmentAsSelectedFactorAndWithSelectedGroupFactorName() throws Exception {

		final GxeTable gxeTable =
			new GxeTable(GxeTableTest.STUDY_ID, GxeTableTest.SITE_FACTOR, GxeTableTest.GROUP_FACTOR, this.variatesCheckBoxState,
				this.listener);
		gxeTable.setStudyDataManager(this.studyDataManager);
		gxeTable.setOntologyVariableService(this.ontologyVariableService);
		gxeTable.afterPropertiesSet();

		Assert.assertTrue(
			"The Trial Instance Factor should always be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.STUDY_FACTOR));
		Assert.assertTrue(
			"The Site Factor is selected so it should be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.SITE_FACTOR));
		Assert.assertTrue(
			"The Group Factor is selected so it should be visible",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.GROUP_FACTOR));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_1_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_1_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_2_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_2_MEANS));
		Assert.assertTrue(
			GxeTableTest.VARIATE_NAME_3_MEANS + " is selected so it should be visible in the table",
			ArrayUtils.contains(gxeTable.getVisibleColumns(), GxeTableTest.VARIATE_NAME_3_MEANS));

		final Object itemId = gxeTable.getItemIds().iterator().next();
		Assert.assertEquals("", GxeTableTest.STUDY_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.STUDY_FACTOR).getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.SITE_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.SITE_FACTOR).getValue()).getValue());
		Assert.assertEquals("", GxeTableTest.GROUP_FACTOR_VALUE,
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.GROUP_FACTOR).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_1_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_2_MEANS).getValue()).getValue());
		Assert.assertEquals("", "0 (" + GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE + ")",
			((Label) gxeTable.getItem(itemId).getItemProperty(GxeTableTest.VARIATE_NAME_3_MEANS).getValue()).getValue());

	}

	private DataSet createDataSet(final int datasetId, final DatasetTypeEnum datasetType, final String datasetName,
		final List<DMSVariableType> factors, final List<Variable> factorVariables,
		final List<DMSVariableType> variates, final List<Variable> variateVariables) {
		final DataSet dataSet = new DataSet();
		dataSet.setDatasetType(new DatasetType(datasetType.getId()));
		dataSet.setName(datasetName);
		dataSet.setId(datasetId);

		final VariableTypeList variableTypeList = new VariableTypeList();
		for (final DMSVariableType f : factors) {
			variableTypeList.add(f);
		}
		for (final DMSVariableType v : variates) {
			variableTypeList.add(v);
		}
		dataSet.setVariableTypes(variableTypeList);

		return dataSet;
	}

	private void createFactorsAndVariatesTestDataForMeans(
		final List<DMSVariableType> factors, final List<Variable> factorVariables,
		final List<DMSVariableType> variates, final List<Variable> variateVariables) {

		int rank = 1;

		this.addVariableToList(factors, factorVariables, GxeTableTest.STUDY_FACTOR_ID, GxeTableTest.STUDY_FACTOR, rank++,
			GxeTableTest.STUDY_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
			GxeTableTest.TRIAL_INSTANCE_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.SITE_FACTOR_ID, GxeTableTest.SITE_FACTOR, rank++,
			GxeTableTest.SITE_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.CHARACTER_VARIABLE,
			GxeTableTest.TRIAL_ENVIRONMENT_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.LOCATION_ID_FACTOR_ID, GxeTableTest.LOCATION_ID_FACTOR, rank++,
			GxeTableTest.LOCATION_ID_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
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

	private void createFactorsAndVariatesTestDataForSummary(
		final List<DMSVariableType> factors, final List<Variable> factorVariables,
		final List<DMSVariableType> variates, final List<Variable> variateVariables) {

		int rank = 1;

		this.addVariableToList(factors, factorVariables, GxeTableTest.STUDY_FACTOR_ID, GxeTableTest.STUDY_FACTOR, rank++,
			GxeTableTest.STUDY_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
			GxeTableTest.TRIAL_INSTANCE_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.SITE_FACTOR_ID, GxeTableTest.SITE_FACTOR, rank++,
			GxeTableTest.SITE_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.CHARACTER_VARIABLE,
			GxeTableTest.TRIAL_ENVIRONMENT_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.LOCATION_ID_FACTOR_ID, GxeTableTest.LOCATION_ID_FACTOR, rank++,
			GxeTableTest.LOCATION_ID_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
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

	}

	private void createFactorsAndVariatesTestDataForPlot(
		final List<DMSVariableType> factors, final List<Variable> factorVariables,
		final List<DMSVariableType> variates, final List<Variable> variateVariables) {

		int rank = 1;

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_1_ID, GxeTableTest.VARIATE_NAME_1, rank++, "1",
			PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_2_ID, GxeTableTest.VARIATE_NAME_2, rank++, "1",
			PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_3_ID, GxeTableTest.VARIATE_NAME_3, rank++, "1",
			PhenotypicType.VARIATE, GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, null);

	}

	private void createFactorsAndVariatesTestDataForSummaryStatistics(
		final List<DMSVariableType> factors, final List<Variable> factorVariables,
		final List<DMSVariableType> variates, final List<Variable> variateVariables) {

		int rank = 1;

		this.addVariableToList(factors, factorVariables, GxeTableTest.STUDY_FACTOR_ID, GxeTableTest.STUDY_FACTOR, rank++,
			GxeTableTest.STUDY_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
			GxeTableTest.TRIAL_INSTANCE_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, GxeTableTest.LOCATION_ID_FACTOR_ID, GxeTableTest.LOCATION_ID_FACTOR, rank++,
			GxeTableTest.LOCATION_ID_FACTOR_VALUE, PhenotypicType.TRIAL_ENVIRONMENT, GxeTableTest.NUMERIC_VARIABLE,
			GxeTableTest.TRIAL_ENVIRONMENT_ROLE, null, null, null);

		final Term heritabilityMethod = new Term(HERITABILITY_METHOD_ID, HERITABILITY_METHOD_NAME, "");

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_1_HERITABILITY_ID,
			GxeTableTest.VARIATE_NAME_1_HERITABILITY, rank++, GxeTableTest.VARIATE_NAME_1_HERITABILITY_VALUE, PhenotypicType.VARIATE,
			GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, heritabilityMethod);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_2_HERITABILITY_ID,
			GxeTableTest.VARIATE_NAME_2_HERITABILITY, rank++, GxeTableTest.VARIATE_NAME_2_HERITABILITY_VALUE, PhenotypicType.VARIATE,
			GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, heritabilityMethod);

		this.addVariableToList(variates, variateVariables, GxeTableTest.VARIATE_NAME_3_HERITABILITY_ID,
			GxeTableTest.VARIATE_NAME_3_HERITABILITY, rank++, GxeTableTest.VARIATE_NAME_3_HERITABILITY_VALUE, PhenotypicType.VARIATE,
			GxeTableTest.NUMERIC_VARIABLE, GxeTableTest.OBSERVATION_VARIATE_ROLE, null, null, heritabilityMethod);

	}

	private void addVariableToList(
		final List<DMSVariableType> variableTypes, final List<Variable> variables, final int id,
		final String variableName, final int rank, final String value, final PhenotypicType phenotypicType, final Term dataType,
		final Term role, final Term property, final Term scale, final Term method) {
		final StandardVariable standardVariable =
			this.createStardardVariableTestData(id, phenotypicType, variableName, dataType, role, property, scale, method);
		final DMSVariableType variableType = this.createVariableTypeTestData(variableName, rank, standardVariable);
		variableTypes.add(variableType);
		variables.add(this.createVariableTestData(variableType, value));
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

	private StandardVariable createStardardVariableTestData(
		final int id, final PhenotypicType type, final String name, final Term dataType,
		final Term storedIn, final Term property, final Term scale, final Term method) {

		final StandardVariable stdVar = new StandardVariable();
		stdVar.setId(id);
		stdVar.setPhenotypicType(type);
		stdVar.setName(name);
		stdVar.setDataType(dataType);

		if (dataType.getId() == GxeTableTest.CATEGORICAL_VARIABLE.getId()) {
			final List<Enumeration> validValues = new ArrayList<Enumeration>();
			validValues.add(new Enumeration(GxeTableTest.CATEGORICAL_VARIATE_ENUM_ID, GxeTableTest.CATEGORICAL_VARIATE_ENUM_NAME,
				GxeTableTest.CATEGORICAL_VARIATE_ENUM_DESCRIPTION, 1));
			stdVar.setEnumerations(validValues);
		}

		stdVar.setMethod(method);
		return stdVar;
	}

	private TrialEnvironments createStudyEnvironments(final List<Variable> factorVariables, final List<Variable> variateVariables) {

		final TrialEnvironments envs = new TrialEnvironments();
		envs.add(this.createStudyEnvironment(factorVariables, variateVariables));

		return envs;
	}

	private TrialEnvironment createStudyEnvironment(final List<Variable> factorVariables, final List<Variable> variateVariables) {

		final TrialEnvironment environment = new TrialEnvironment(1, new VariableList());

		for (final Variable f : factorVariables) {
			environment.getVariables().add(f);
		}
		for (final Variable v : variateVariables) {
			environment.getVariables().add(v);
		}

		return environment;
	}

	private Map<String, Boolean> createVariatesCheckBoxState(final List<DMSVariableType> variates) {
		final Map<String, Boolean> map = new HashMap<>();
		for (final DMSVariableType v : variates) {
			map.put(v.getLocalName(), true);
		}
		return map;
	}

	private VariableList convertToVariableList(final List<Variable> variables) {
		final VariableList variableList = new VariableList();
		for (final Variable v : variables) {
			variableList.add(v);
		}
		return variableList;
	}

	private List<Experiment> createExperimentsWithStudyEnvironment(
		final List<DMSVariableType> factors,
		final List<Variable> factorVariables, final List<DMSVariableType> variates, final List<Variable> variateVariables) {
		final List<Experiment> experiments = new ArrayList<>();
		final Experiment exp = new Experiment();
		exp.setId(1);
		exp.setLocationId(1);
		exp.setFactors(this.convertToVariableList(factorVariables));
		exp.setVariates(this.convertToVariableList(variateVariables));
		experiments.add(exp);
		return experiments;
	}
}

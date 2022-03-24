package org.generationcp.ibpworkbench.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.WorkbenchContentApp;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.api.ontology.OntologyVariableService;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiSiteDataExporterTest {

	public static final Map<Integer, String> TRAITS =
		ImmutableMap.<Integer, String>builder()
			.put(1, "Aflatox_M_ppb")
			.put(2, "EDia_M_cm")
			.put(3, "SilkLng_M_cm").build();

	private static final String BREEDING_LOCATION = "Breeding Location ";
	private static final String BASIC_FILE_NAME = "MaizeProgram_25124_Study8-MEANS";
	private static final String ENV_FACTOR = "TRIAL_INSTANCE";
	private static final String ENV_GROUP_FACTOR = "LOCATION_NAME";
	private static final String LOCATION_ID = "LOCATION_ID";
	private static final String GENOTYPE_FACTOR = "GID";
	private static final String MEANS = "_Means";
	private static final String[] SUMMARY_METHOD_NAMES = {"Mean", "MeanSED", "CV", "Heritability", "Pvalue"};
	private static final String[] ENVIRONMENTS = {"1", "2", "3"};
	private static final String[] GIDS = {"101", "102", "103", "104", "105"};
	private static final String BMS_INPUT_FILES_DIR = "/someDirectory/breeding_view/input";
	public static final int STUDY_ID = 1;
	public static final int PLOT_DATASET_ID = 2;
	public static final int SUMMARY_STATISTICS_DATASET_ID = 3;

	private static final Term NUMERIC_VARIABLE = new Term(TermId.NUMERIC_VARIABLE.getId(), "", "");
	private static final Term CATEGORICAL_VARIABLE = new Term(TermId.CATEGORICAL_VARIABLE.getId(), "", "");
	private static final Term CHARACTER_VARIABLE = new Term(TermId.CHARACTER_VARIABLE.getId(), "", "");
	private static final Term TRIAL_INSTANCE_ROLE = new Term(TermId.TRIAL_INSTANCE_STORAGE.getId(), "", "");
	private static final Term TRIAL_ENVIRONMENT_ROLE = new Term(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", "");
	private static final Term OBSERVATION_VARIATE_ROLE = new Term(TermId.OBSERVATION_VARIATE.getId(), "", "");

	private MultiSiteDataExporter multiSiteDataExporter;

	@Captor
	private ArgumentCaptor<List<String[]>> meansRowsCaptor;

	@Captor
	private ArgumentCaptor<List<String[]>> summaryRowsCaptor;

	@Captor
	private ArgumentCaptor<Notification> notificationCaptor;

	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private OntologyVariableService ontologyVariableService;

	private Project project;

	private MultiSiteParameters multiSiteParameters;

	private List<Experiment> summaryExperiments;
	private List<Trait> meansTraits;
	private List<Trait> summaryTraits;
	private GxeEnvironment gxeEnvironment;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Need to spy so that actual writing of CSV files won't be performed during tests execution
		this.multiSiteDataExporter = Mockito.spy(new MultiSiteDataExporter());
		this.multiSiteDataExporter.setInstallationDirectoryUtil(this.installationDirectoryUtil);
		this.multiSiteDataExporter.setStudyDataManager(this.studyDataManager);
		this.multiSiteDataExporter.setOntologyVariableService(this.ontologyVariableService);

		this.createMultiSiteParameters();
		this.setupFileUtilMocks();
		this.meansTraits = this.createTraits(Arrays.asList(MEANS));
		this.summaryTraits = this.createTraits(Arrays.asList(SUMMARY_METHOD_NAMES));
		this.gxeEnvironment = new GxeEnvironment();
		this.gxeEnvironment.setLabel(this.createGxeEnviornments(ENVIRONMENTS));

		// summary dataset
		final List<DMSVariableType> summaryStatisticsFactors = new ArrayList<>();
		final List<DMSVariableType> summaryStatisticsVariates = new ArrayList<>();
		final List<Variable> summaryStatisticsFactorVariables = new ArrayList<>();
		final List<Variable> summaryStatisticsVariateVariables = new ArrayList<>();

		// plot dataset
		final List<DMSVariableType> plotFactors = new ArrayList<>();
		final List<DMSVariableType> plotVariates = new ArrayList<>();
		final List<Variable> plotFactorVariables = new ArrayList<>();
		final List<Variable> plotVariateVariables = new ArrayList<>();

		this.createFactorsAndVariatesTestDataForPlot(plotFactors, plotFactorVariables, plotVariates, plotVariateVariables);
		final MultiKeyMap multiKeyMap = MultiKeyMap.decorate(new LinkedMap());
		this.createFactorsAndVariatesTestDataForSummaryStatistics(summaryStatisticsFactors, summaryStatisticsFactorVariables,
			summaryStatisticsVariates, summaryStatisticsVariateVariables, multiKeyMap);
		final DataSet plotDataSet =
			this.createDataSet(PLOT_DATASET_ID, DatasetTypeEnum.PLOT_DATA, "PLOT-DATA", plotFactors, plotFactorVariables,
				plotVariates,
				plotVariateVariables);
		final DataSet summaryStatisticsDataSet =
			this.createDataSet(SUMMARY_STATISTICS_DATASET_ID, DatasetTypeEnum.SUMMARY_STATISTICS_DATA,
				"SUMMARY-STATISTICS-DATA", summaryStatisticsFactors,
				summaryStatisticsFactorVariables, summaryStatisticsVariates, summaryStatisticsVariateVariables);

		final List<Experiment> summaryStatisticsExperiments =
			this.createSummaryStatisticsExperiments(summaryStatisticsFactors, summaryStatisticsVariates);

		Mockito.doReturn(plotDataSet).when(this.studyDataManager)
			.findOneDataSetByType(STUDY_ID, DatasetTypeEnum.PLOT_DATA.getId());
		Mockito.doReturn(summaryStatisticsDataSet).when(this.studyDataManager)
			.findOneDataSetByType(STUDY_ID, DatasetTypeEnum.SUMMARY_STATISTICS_DATA.getId());
		Mockito.doReturn(summaryStatisticsExperiments).when(this.studyDataManager)
			.getExperiments(summaryStatisticsDataSet.getId(), 0, Integer.MAX_VALUE);
		Mockito.doReturn(multiKeyMap).when(this.ontologyVariableService)
			.getAnalysisMethodsOfTraits(ArgumentMatchers.anyList(), ArgumentMatchers.anyList());

	}

	private void setupFileUtilMocks() {
		Mockito.doReturn(BASIC_FILE_NAME + ".csv").when(this.multiSiteDataExporter)
			.writeToCsvFile(ArgumentMatchers.anyString(), ArgumentMatchers.any(Project.class), ArgumentMatchers.any(),
				ArgumentMatchers.eq(false));
		Mockito.doReturn(BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv").when(this.multiSiteDataExporter)
			.writeToCsvFile(ArgumentMatchers.anyString(), ArgumentMatchers.any(Project.class), ArgumentMatchers.any(),
				ArgumentMatchers.eq(true));
		Mockito.doReturn(BMS_INPUT_FILES_DIR).when(this.installationDirectoryUtil)
			.getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
	}

	@Test
	public void testExportMeansDatasetToCsv() {

		final List<String> environmentNames = new ArrayList<>();
		environmentNames.add("1");
		environmentNames.add("2");
		environmentNames.add("3");

		final List<Experiment> meansExperiments = this.createMeansExperiments(ENV_FACTOR, environmentNames);

		this.multiSiteDataExporter
			.exportMeansDatasetToCsv(BASIC_FILE_NAME, this.multiSiteParameters, meansExperiments, ENV_FACTOR, this.gxeEnvironment,
				this.meansTraits, Mockito.mock(WorkbenchContentApp.class));

		Mockito.verify(this.multiSiteDataExporter)
			.writeToCsvFile(ArgumentMatchers.eq(BASIC_FILE_NAME), ArgumentMatchers.eq(this.project), this.meansRowsCaptor.capture(),
				ArgumentMatchers.eq(false));
		final List<String[]> csvRows = this.meansRowsCaptor.getValue();
		Assert.assertNotNull(csvRows);
		Assert.assertEquals(1 + ((long) GIDS.length * environmentNames.size()), csvRows.size());
		final Iterator<String[]> rowsIterator = csvRows.iterator();

		// Verify the header row
		final List<String> headers = new ArrayList<>();
		headers.add(ENV_FACTOR);
		headers.add(ENV_GROUP_FACTOR);
		headers.add(GENOTYPE_FACTOR);
		for (final Trait trait : this.meansTraits) {
			headers.add(trait.getName());
		}
		Assert.assertEquals(headers, Arrays.asList(rowsIterator.next()));
		// Verify data rows
		for (final String environmentName : environmentNames) {
			for (final String gid : GIDS) {
				final String[] row = rowsIterator.next();
				Assert.assertEquals(environmentName, row[0]);
				Assert.assertEquals(BREEDING_LOCATION + environmentName, row[1]);
				Assert.assertEquals(gid, row[2]);
				for (int j = 1; j <= this.meansTraits.size(); j++) {
					Assert.assertEquals(gid + "." + j, row[2 + j]);
				}
			}
		}
	}

	@Test
	public void testExportMeansDatasetToCsvEnvironmentFactorIsLocationID() {

		final List<String> environmentNames = new ArrayList<>();
		environmentNames.add("100");
		environmentNames.add("747");
		environmentNames.add("999");

		final List<Experiment> meansExperiments = this.createMeansExperiments(LOCATION_ID, environmentNames);

		final int studyId = 1;

		final BiMap<String, String> locationIdToNameMap = HashBiMap.create();
		locationIdToNameMap.put("100", "Agua Fria");
		locationIdToNameMap.put("747", "Philippines");
		locationIdToNameMap.put("999", "Thailand");

		final GxeEnvironment testGxeEnvironment = new GxeEnvironment();
		testGxeEnvironment.setLabel(this.createGxeEnviornments(new String[] {"Agua Fria", "Philippines", "Thailand"}));

		Mockito.when(this.studyDataManager.isLocationIdVariable(studyId, LOCATION_ID)).thenReturn(true);
		Mockito.when(this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId)).thenReturn(locationIdToNameMap);

		this.multiSiteDataExporter
			.exportMeansDatasetToCsv(BASIC_FILE_NAME, this.multiSiteParameters, meansExperiments, LOCATION_ID, testGxeEnvironment,
				this.meansTraits, Mockito.mock(WorkbenchContentApp.class));

		Mockito.verify(this.multiSiteDataExporter)
			.writeToCsvFile(ArgumentMatchers.eq(BASIC_FILE_NAME), ArgumentMatchers.eq(this.project), this.meansRowsCaptor.capture(),
				ArgumentMatchers.eq(false));
		final List<String[]> csvRows = this.meansRowsCaptor.getValue();
		Assert.assertNotNull(csvRows);
		Assert.assertEquals(1 + ((long) GIDS.length * environmentNames.size()), csvRows.size());
		final Iterator<String[]> rowsIterator = csvRows.iterator();

		// Verify the header row
		final List<String> headers = new ArrayList<>();
		headers.add(LOCATION_ID);
		headers.add(ENV_GROUP_FACTOR);
		headers.add(GENOTYPE_FACTOR);
		for (final Trait trait : this.meansTraits) {
			headers.add(trait.getName());
		}
		Assert.assertEquals(headers, Arrays.asList(rowsIterator.next()));
		// Verify data rows
		for (final String environmentName : environmentNames) {
			for (final String gid : GIDS) {
				final String[] row = rowsIterator.next();
				Assert.assertEquals(locationIdToNameMap.get(environmentName), row[0]);
				Assert.assertEquals(BREEDING_LOCATION + environmentName, row[1]);
				Assert.assertEquals(gid, row[2]);
				for (int j = 1; j <= this.meansTraits.size(); j++) {
					Assert.assertEquals(gid + "." + j, row[2 + j]);
				}
			}
		}
	}

	@Test
	public void exportMeansDatasetToCsv_HasMissingMean_ShowWarning() {
		final WorkbenchContentApp workbenchApplication = Mockito.mock(WorkbenchContentApp.class);
		final Window window = Mockito.mock(Window.class);
		Mockito.when(workbenchApplication.getMainWindow()).thenReturn(window);

		final List<String> environmentNames = new ArrayList<>();
		environmentNames.add("1");
		environmentNames.add("2");
		environmentNames.add("3");

		final List<Experiment> meansExperiments = this.createMeansExperiments(ENV_FACTOR, environmentNames);

		// Remove a mean
		meansExperiments.get(0).getFactors().getVariables().get(0).setValue(null);

		this.multiSiteDataExporter
			.exportMeansDatasetToCsv(BASIC_FILE_NAME, this.multiSiteParameters, meansExperiments, ENV_FACTOR, this.gxeEnvironment,
				this.meansTraits, workbenchApplication);

		Mockito.verify(window).showNotification(this.notificationCaptor.capture());
		final Notification notification = this.notificationCaptor.getValue();

		Assert.assertNotNull(notification);
		Assert.assertEquals("There are missing mean values", "Warning", notification.getCaption());

	}

	@Test
	public void testExportSummaryStatisticsToCsvFile_EnvironmentFactorIsTrialInstance() {

		this.multiSiteDataExporter
			.exportSummaryStatisticsToCsvFile(1, BASIC_FILE_NAME, ENV_FACTOR, this.meansTraits,
				this.project);

		Mockito.verify(this.multiSiteDataExporter)
			.writeToCsvFile(ArgumentMatchers.eq(BASIC_FILE_NAME), ArgumentMatchers.eq(this.project), this.summaryRowsCaptor.capture(),
				ArgumentMatchers.eq(true));
		final List<String[]> csvRows = this.summaryRowsCaptor.getValue();
		Assert.assertNotNull(csvRows);
		Assert.assertEquals(1 + ((long) this.meansTraits.size() * ENVIRONMENTS.length), csvRows.size());
		final Iterator<String[]> rowsIterator = csvRows.iterator();

		// Verify the header row
		final List<String> headers = new ArrayList<>();
		headers.add(ENV_FACTOR);
		headers.add("Trait");
		headers.addAll(Arrays.asList("NumValues", "NumMissing", "Mean", "Variance", "SD", "Min", "Max", "Range", "Median", "LowerQuartile",
			"UpperQuartile", "MeanRep", "MinRep", "MaxRep", "MeanSED", "MinSED", "MaxSED", "MeanLSD", "MinLSD", "MaxLSD", "CV",
			"Heritability", "WaldStatistic", "WaldDF", "Pvalue"));
		Assert.assertEquals(headers, Arrays.asList(rowsIterator.next()));
		// Verify data rows
		for (final String env : ENVIRONMENTS) {
			int k = 1;
			for (int i = 1; i <= this.meansTraits.size(); i++) {
				final String[] row = rowsIterator.next();
				Assert.assertEquals(env, row[0]);
				Assert.assertEquals(this.meansTraits.get(i - 1).getName(), row[1]);
				int j = 2;
				// <Trait>_NumValue, <Trait>_NumMissing will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_Mean value
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				// <Trait>_Variance, <Trait>_SD, <Trait>_Min, <Trait>_Max, <Trait>_Range, <Trait>_Median, <Trait>_LowerQuartile
				// <Trait>_UpperQuartile, <Trait>_MeanRep, <Trait>_MinRep, <Trait>_MaxRep will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_MeanSED value
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				// <Trait>_MinSED, <Trait>_MaxSED, <Trait>_MeanLSD, <Trait>_MinLSD, <Trait>_MaxLSD will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_CV, <Trait>_Heritability values
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				// <Trait>_WaldStatistic, <Trait>_WaldDF will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_PValue value
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
			}
		}
	}

	@Test
	public void testExportSummaryStatisticsToCsvFile_EnvironmentFactorIsLocationID() {

		final int studyId = 1;

		final BiMap<String, String> locationIdToNameMap = HashBiMap.create();
		locationIdToNameMap.put("100", "Agua Fria");

		Mockito.when(this.studyDataManager.isLocationIdVariable(studyId, LOCATION_ID)).thenReturn(true);
		Mockito.when(this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId)).thenReturn(locationIdToNameMap);

		this.multiSiteDataExporter
			.exportSummaryStatisticsToCsvFile(studyId, BASIC_FILE_NAME, LOCATION_ID, this.meansTraits,
				this.project);

		Mockito.verify(this.multiSiteDataExporter)
			.writeToCsvFile(ArgumentMatchers.eq(BASIC_FILE_NAME), ArgumentMatchers.eq(this.project), this.summaryRowsCaptor.capture(),
				ArgumentMatchers.eq(true));

		final List<String[]> csvRows = this.summaryRowsCaptor.getValue();
		Assert.assertNotNull(csvRows);
		Assert.assertEquals(1 + ((long) this.meansTraits.size() * ENVIRONMENTS.length), csvRows.size());
		final Iterator<String[]> rowsIterator = csvRows.iterator();

		// Verify the header row
		final List<String> headers = new ArrayList<>();
		headers.add(LOCATION_ID);
		headers.add("Trait");
		headers.addAll(Arrays.asList("NumValues", "NumMissing", "Mean", "Variance", "SD", "Min", "Max", "Range", "Median", "LowerQuartile",
			"UpperQuartile", "MeanRep", "MinRep", "MaxRep", "MeanSED", "MinSED", "MaxSED", "MeanLSD", "MinLSD", "MaxLSD", "CV",
			"Heritability", "WaldStatistic", "WaldDF", "Pvalue"));
		Assert.assertEquals(headers, Arrays.asList(rowsIterator.next()));
		// Verify data rows
		for (final String env : ENVIRONMENTS) {
			int k = 1;
			for (int i = 1; i <= this.meansTraits.size(); i++) {
				final String[] row = rowsIterator.next();
				Assert.assertEquals("Agua Fria", row[0]);
				Assert.assertEquals(this.meansTraits.get(i - 1).getName(), row[1]);
				int j = 2;
				// <Trait>_NumValue, <Trait>_NumMissing will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_Mean value
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				// <Trait>_Variance, <Trait>_SD, <Trait>_Min, <Trait>_Max, <Trait>_Range, <Trait>_Median, <Trait>_LowerQuartile
				// <Trait>_UpperQuartile, <Trait>_MeanRep, <Trait>_MinRep, <Trait>_MaxRep will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_MeanSED value
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				// <Trait>_MinSED, <Trait>_MaxSED, <Trait>_MeanLSD, <Trait>_MinLSD, <Trait>_MaxLSD will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_CV, <Trait>_Heritability values
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
				// <Trait>_WaldStatistic, <Trait>_WaldDF will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_PValue value
				Assert.assertTrue(StringUtils.isNotBlank(row[j++]));
			}
		}
	}

	@Test
	public void testGetCsvFileInWorkbenchDirectoryForMeans() {
		final File meansFile = this.multiSiteDataExporter.getCsvFileInWorkbenchDirectory(this.project, BASIC_FILE_NAME, false);
		Mockito.verify(this.installationDirectoryUtil)
			.createWorkspaceDirectoriesForProject(this.project.getCropType().getCropName(), this.project.getProjectName());
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);

		final File expectedFile =
			new File(BMS_INPUT_FILES_DIR + File.separator + BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv");
		final String expectedFileName = BASIC_FILE_NAME + "_";

		Assert.assertEquals(meansFile.getParent(), expectedFile.getParent());
		Assert.assertTrue(meansFile.getName().contains(expectedFileName));

		// Check for date time
		final String[] generatedFileName = meansFile.getName().split("_");
		Assert.assertTrue("File contains 3 or more underscore", generatedFileName.length > 3);
		try {
			Assert.assertNotNull("File contains date",
				new SimpleDateFormat("yyyyMMdd").parse(generatedFileName[generatedFileName.length - 2]));
		} catch (final ParseException e) {
			e.printStackTrace();
			Assert.fail("File must contain Date with format yyyyMMdd");
		}

		try {
			Assert.assertNotNull("File contains time",
				new SimpleDateFormat("hhmmss").parse(generatedFileName[generatedFileName.length - 1]));
		} catch (final ParseException e) {
			e.printStackTrace();
			Assert.fail("File must contain Time with format hhmmss");

		}

	}

	@Test
	public void testGetCsvFileInWorkbenchDirectoryForSummaryStats() {
		final File meansFile = this.multiSiteDataExporter.getCsvFileInWorkbenchDirectory(this.project, BASIC_FILE_NAME, true);
		Mockito.verify(this.installationDirectoryUtil)
			.createWorkspaceDirectoriesForProject(this.project.getCropType().getCropName(), this.project.getProjectName());
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);

		final File expectedFile =
			new File(BMS_INPUT_FILES_DIR + File.separator + BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv");
		final String expectedFileName = BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + "_";

		Assert.assertEquals(meansFile.getParent(), expectedFile.getParent());
		Assert.assertTrue(meansFile.getName().contains(expectedFileName));

		// Check for date time
		final String[] generatedFileName = meansFile.getName().split("_");
		Assert.assertTrue("File contains 3 or more underscore", generatedFileName.length > 3);
		try {
			Assert.assertNotNull("File contains date",
				new SimpleDateFormat("yyyyMMdd").parse(generatedFileName[generatedFileName.length - 2]));
		} catch (final ParseException e) {
			e.printStackTrace();
			Assert.fail("File must contain Date with format yyyyMMdd");
		}

		try {
			Assert.assertNotNull("File contains time",
				new SimpleDateFormat("hhmmss").parse(generatedFileName[generatedFileName.length - 1]));
		} catch (final ParseException e) {
			e.printStackTrace();
			Assert.fail("File must contain Time with format hhmmss");

		}
	}

	private List<Experiment> createMeansExperiments(final String environmentFactor, final List<String> environmentNames) {
		final List<Experiment> meansExperiments = new ArrayList<>();

		for (final String environmentName : environmentNames) {
			for (final String gid : GIDS) {
				final VariableList factors = new VariableList();
				final DMSVariableType trialInstanceVariable = new DMSVariableType();
				trialInstanceVariable.setLocalName(environmentFactor);
				factors.add(new Variable(trialInstanceVariable, environmentName));
				final DMSVariableType gidVariable = new DMSVariableType();
				gidVariable.setLocalName(GENOTYPE_FACTOR);
				factors.add(new Variable(gidVariable, gid));
				final DMSVariableType locationVariable = new DMSVariableType();
				locationVariable.setLocalName(ENV_GROUP_FACTOR);
				factors.add(new Variable(locationVariable, BREEDING_LOCATION + environmentName));

				final VariableList variates = new VariableList();
				for (int i = 1; i <= this.meansTraits.size(); i++) {
					final DMSVariableType traitVariable = new DMSVariableType();
					traitVariable.setLocalName(this.meansTraits.get(i - 1).getName());
					final StandardVariable standardVar = new StandardVariable();
					standardVar.setId(Integer.valueOf(gid + i));
					traitVariable.setStandardVariable(standardVar);
					variates.add(new Variable(traitVariable, Double.valueOf(gid + "." + i)));
				}
				final Experiment experiment = new Experiment();
				experiment.setFactors(factors);
				experiment.setVariates(variates);
				meansExperiments.add(experiment);
			}
		}

		return meansExperiments;
	}

	private List<Experiment> createSummaryStatisticsExperiments(final List<DMSVariableType> factors,
		final List<DMSVariableType> variates) {
		this.summaryExperiments = new ArrayList<>();
		for (final String env : ENVIRONMENTS) {
			final Experiment experiment = new Experiment();
			final Map<Integer, DMSVariableType> factorVariablesMap =
				factors.stream().collect(Collectors.toMap(v -> v.getId(), Function.identity()));
			final Variable trialInstance = this.createVariableTestData(factorVariablesMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId()), env);
			final Variable lcoationId = this.createVariableTestData(factorVariablesMap.get(TermId.LOCATION_ID.getId()), "100");

			final List<Variable> variatesList = new ArrayList<>();
			int i = 1;
			for (final DMSVariableType variate : variates) {
				variatesList.add(this.createVariableTestData(variate, Double.valueOf((Integer.valueOf(env) * 10) + "." + i++).toString()));
			}
			experiment.setFactors(this.convertToVariableList(Arrays.asList(trialInstance, lcoationId)));
			experiment.setVariates(this.convertToVariableList(variatesList));
			this.summaryExperiments.add(experiment);
		}
		return this.summaryExperiments;
	}

	private List<Trait> createTraits(final List<String> suffixes) {
		final List<Trait> traits = new ArrayList<>();
		for (final Map.Entry<Integer, String> entry : TRAITS.entrySet()) {
			for (final String suffix : suffixes) {
				final Trait trait = new Trait();
				trait.setName(entry.getValue() + suffix);
				traits.add(trait);
			}
		}
		return traits;
	}

	private List<GxeEnvironmentLabel> createGxeEnviornments(final String[] environments) {
		final List<GxeEnvironmentLabel> gxeEnvironments = new ArrayList<>();
		for (final String name : environments) {
			final GxeEnvironmentLabel gxeEnvironment = new GxeEnvironmentLabel();
			gxeEnvironment.setName(name);
			gxeEnvironments.add(gxeEnvironment);
		}
		return gxeEnvironments;
	}

	private void createMultiSiteParameters() {
		this.multiSiteParameters = new MultiSiteParameters();
		this.project = ProjectTestDataInitializer.createProject();
		final Study study = new Study();
		study.setId(STUDY_ID);
		this.multiSiteParameters.setProject(this.project);
		this.multiSiteParameters.setSelectedEnvGroupFactorName(ENV_GROUP_FACTOR);
		this.multiSiteParameters.setSelectedGenotypeFactorName(GENOTYPE_FACTOR);
		this.multiSiteParameters.setStudy(study);
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

	private void createFactorsAndVariatesTestDataForPlot(
		final List<DMSVariableType> factors, final List<Variable> factorVariables,
		final List<DMSVariableType> variates, final List<Variable> variateVariables) {

		int rank = 1;

		for (final Map.Entry<Integer, String> entry : TRAITS.entrySet()) {
			this.addVariableToList(variates, variateVariables, entry.getKey(), entry.getValue(), rank++, "",
				PhenotypicType.VARIATE, NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, null);
		}

	}

	private void createFactorsAndVariatesTestDataForSummaryStatistics(
		final List<DMSVariableType> factors, final List<Variable> factorVariables,
		final List<DMSVariableType> variates, final List<Variable> variateVariables, final MultiKeyMap multiKeyMap) {

		int rank = 1;

		this.addVariableToList(factors, factorVariables, TermId.TRIAL_INSTANCE_FACTOR.getId(), ENV_FACTOR, rank++,
			"", PhenotypicType.TRIAL_ENVIRONMENT, NUMERIC_VARIABLE,
			TRIAL_INSTANCE_ROLE, null, null, null);

		this.addVariableToList(factors, factorVariables, TermId.LOCATION_ID.getId(), LOCATION_ID, rank++,
			"", PhenotypicType.TRIAL_ENVIRONMENT, NUMERIC_VARIABLE,
			TRIAL_ENVIRONMENT_ROLE, null, null, null);

		final Map<String, Term> methodTerms = new HashMap<>();
		for (final String methodName : SUMMARY_METHOD_NAMES) {
			final int methodId = new Random().nextInt(10);
			methodTerms.put(methodName, new Term(methodId, methodName, ""));
		}
		for (final Map.Entry<Integer, String> entry : TRAITS.entrySet()) {
			for (final String methodName : SUMMARY_METHOD_NAMES) {
				final int variableId = new Random().nextInt();
				this.addVariableToList(variates, variateVariables, variableId,
					entry.getValue() + "_" + methodName, rank++, String.valueOf(new Random().nextDouble()), PhenotypicType.VARIATE,
					NUMERIC_VARIABLE, OBSERVATION_VARIATE_ROLE, null, null, methodTerms.get(methodName));
				multiKeyMap.put(entry.getKey(), methodTerms.get(methodName).getId(), variableId);
			}
		}

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
		stdVar.setMethod(method);
		return stdVar;
	}

	private VariableList convertToVariableList(final List<Variable> variables) {
		final VariableList variableList = new VariableList();
		for (final Variable v : variables) {
			variableList.add(v);
		}
		return variableList;
	}

}

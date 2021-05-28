package org.generationcp.ibpworkbench.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.WorkbenchContentApp;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.manager.api.StudyDataManager;
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
import java.util.Iterator;
import java.util.List;

public class MultiSiteDataExporterTest {

	private static final String BREEDING_LOCATION = "Breeding Location ";
	private static final String BASIC_FILE_NAME = "MaizeProgram_25124_Study8-MEANS";
	private static final String ENV_FACTOR = "TRIAL_INSTANCE";
	private static final String ENV_GROUP_FACTOR = "LOCATION_NAME";
	private static final String LOCATION_ID = "LOCATION_ID";
	private static final String GENOTYPE_FACTOR = "GID";
	private static final String MEANS = "_Means";
	private static final String[] TRAITS = {"Aflatox_M_ppb", "EDia_M_cm", "SilkLng_M_cm"};
	private static final String[] SUMMARY_SUFFIX = {"_Mean", "_MeanSED", "_CV", "_Heritability", "_Pvalue"};
	private static final String[] ENVIRONMENTS = {"1", "2", "3"};
	private static final String[] GIDS = {"101", "102", "103", "104", "105"};
	private static final String BMS_INPUT_FILES_DIR = "/someDirectory/breeding_view/input";
	public static final int STUDY_ID = 1;

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

		this.createMultiSiteParameters();
		this.setupFileUtilMocks();
		this.meansTraits = this.createTraits(Arrays.asList(MEANS));
		this.summaryTraits = this.createTraits(Arrays.asList(SUMMARY_SUFFIX));
		this.gxeEnvironment = new GxeEnvironment();
		this.gxeEnvironment.setLabel(this.createGxeEnviornments(ENVIRONMENTS));

		this.createSummaryExperiments();
	}

	private void setupFileUtilMocks() {
		Mockito.doReturn(BASIC_FILE_NAME + ".csv").when(this.multiSiteDataExporter)
				.writeToCsvFile(ArgumentMatchers.anyString(), ArgumentMatchers.any(Project.class), ArgumentMatchers.<List<String[]>>any(), ArgumentMatchers.eq(false));
		Mockito.doReturn(BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv").when(this.multiSiteDataExporter)
				.writeToCsvFile(ArgumentMatchers.anyString(), ArgumentMatchers.any(Project.class), ArgumentMatchers.<List<String[]>>any(), ArgumentMatchers.eq(true));
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
	public void testExportTrialDatasetToSummaryStatsCsv() {
		this.multiSiteDataExporter
				.exportTrialDatasetToSummaryStatsCsv(1, BASIC_FILE_NAME, this.summaryExperiments, ENV_FACTOR, this.meansTraits,
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
				Assert.assertEquals((Integer.valueOf(env) * 10) + "." + k++, row[j++]);
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
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
				// <Trait>_MinSED, <Trait>_MaxSED, <Trait>_MeanLSD, <Trait>_MinLSD, <Trait>_MaxLSD will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_CV, <Trait>_Heritability values
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
				// <Trait>_WaldStatistic, <Trait>_WaldDF will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_PValue value
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
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
	public void testExportTrialDatasetToSummaryStatsCsvEnvironmentFactorIsLocationID() {

		final int studyId = 1;

		final BiMap<String, String> locationIdToNameMap = HashBiMap.create();
		locationIdToNameMap.put("100", "Agua Fria");

		Mockito.when(this.studyDataManager.isLocationIdVariable(studyId, LOCATION_ID)).thenReturn(true);
		Mockito.when(this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(studyId)).thenReturn(locationIdToNameMap);

		this.multiSiteDataExporter
				.exportTrialDatasetToSummaryStatsCsv(studyId, BASIC_FILE_NAME, this.summaryExperiments, LOCATION_ID, this.meansTraits,
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
				Assert.assertEquals((Integer.valueOf(env) * 10) + "." + k++, row[j++]);
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
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
				// <Trait>_MinSED, <Trait>_MaxSED, <Trait>_MeanLSD, <Trait>_MinLSD, <Trait>_MaxLSD will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_CV, <Trait>_Heritability values
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
				// <Trait>_WaldStatistic, <Trait>_WaldDF will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_PValue value
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10) + "." + k++).toString(), row[j++]);
			}
		}
	}

	@Test
	public void testGetCsvFileInWorkbenchDirectoryForMeans() {
		final File meansFile = this.multiSiteDataExporter.getCsvFileInWorkbenchDirectory(this.project, BASIC_FILE_NAME, false);
		Mockito.verify(this.installationDirectoryUtil).createWorkspaceDirectoriesForProject(this.project);
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);

		final File expectedFile = new File(BMS_INPUT_FILES_DIR + File.separator + BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv");
		final String expectedFileName = BASIC_FILE_NAME + "_";

		Assert.assertEquals(meansFile.getParent(), expectedFile.getParent());
		Assert.assertTrue(meansFile.getName().contains(expectedFileName));

		// Check for date time
		final String[] generatedFileName = meansFile.getName().split("_");
		Assert.assertTrue("File contains 3 or more underscore",generatedFileName.length > 3);
		try {
			Assert.assertNotNull("File contains date", new SimpleDateFormat("yyyyMMdd").parse(generatedFileName[generatedFileName.length - 2]));
		} catch (final ParseException e) {
			e.printStackTrace();
			Assert.fail("File must contain Date with format yyyyMMdd");
		}

		try {
			Assert.assertNotNull("File contains time", new SimpleDateFormat("hhmmss").parse(generatedFileName[generatedFileName.length - 1]));
		} catch (final ParseException e) {
			e.printStackTrace();
			Assert.fail("File must contain Time with format hhmmss");

		}


	}

	@Test
	public void testGetCsvFileInWorkbenchDirectoryForSummaryStats() {
		final File meansFile = this.multiSiteDataExporter.getCsvFileInWorkbenchDirectory(this.project, BASIC_FILE_NAME, true);
		Mockito.verify(this.installationDirectoryUtil).createWorkspaceDirectoriesForProject(this.project);
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);

		final File expectedFile = new File(BMS_INPUT_FILES_DIR + File.separator + BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv");
		final String expectedFileName = BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + "_";

		Assert.assertEquals(meansFile.getParent(), expectedFile.getParent());
		Assert.assertTrue(meansFile.getName().contains(expectedFileName));

		// Check for date time
		final String[] generatedFileName = meansFile.getName().split("_");
		Assert.assertTrue("File contains 3 or more underscore",generatedFileName.length > 3);
		try {
			Assert.assertNotNull("File contains date", new SimpleDateFormat("yyyyMMdd").parse(generatedFileName[generatedFileName.length - 2]));
		} catch (final ParseException e) {
			e.printStackTrace();
			Assert.fail("File must contain Date with format yyyyMMdd");
		}

		try {
			Assert.assertNotNull("File contains time", new SimpleDateFormat("hhmmss").parse(generatedFileName[generatedFileName.length - 1]));
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

	private void createSummaryExperiments() {
		this.summaryExperiments = new ArrayList<>();
		for (final String env : ENVIRONMENTS) {
			final VariableList factors = new VariableList();
			final VariableList variates = new VariableList();
			final DMSVariableType trialInstanceVariable = new DMSVariableType();
			trialInstanceVariable.setLocalName(ENV_FACTOR);

			final DMSVariableType locationIdVariable = new DMSVariableType();
			locationIdVariable.setLocalName(LOCATION_ID);

			factors.add(new Variable(trialInstanceVariable, env));
			factors.add(new Variable(locationIdVariable, "100"));

			for (int j = 1; j <= this.summaryTraits.size(); j++) {
				final DMSVariableType traitVariable = new DMSVariableType();
				traitVariable.setLocalName(this.summaryTraits.get(j - 1).getName());
				final StandardVariable standardVar = new StandardVariable();
				standardVar.setId(j);
				traitVariable.setStandardVariable(standardVar);
				variates.add(new Variable(traitVariable, Double.valueOf((Integer.valueOf(env) * 10) + "." + j)));
			}
			final Experiment experiment = new Experiment();
			experiment.setFactors(factors);
			experiment.setVariates(variates);
			this.summaryExperiments.add(experiment);
		}
	}

	private List<Trait> createTraits(final List<String> suffixes) {
		final List<Trait> traits = new ArrayList<>();
		for (final String var : TRAITS) {
			for (final String suffix : suffixes) {
				final Trait trait = new Trait();
				trait.setName(var + suffix);
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

}

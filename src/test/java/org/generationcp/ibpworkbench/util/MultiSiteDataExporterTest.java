package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

public class MultiSiteDataExporterTest {
	
	private static final String BREEDING_LOCATION = "Breeding Location ";
	private static final String BASIC_FILE_NAME = "MaizeProgram_25124_Trial8-MEANS";
	private static final String ENV_FACTOR = "TRIAL_INSTANCE";
	private static final String ENV_GROUP_FACTOR = "LOCATION_NAME";
	private static final String GENOTYPE_FACTOR = "GID";
	private static final String MEANS = "_Means";
	private static final String[] TRAITS = {"Aflatox_M_ppb", "EDia_M_cm", "SilkLng_M_cm"};
	private static final String[] SUMMARY_SUFFIX = {"_Mean", "_MeanSED", "_CV", "_Heritability", "_Pvalue"};
	private static final String[] ENVIRONMENTS = {"1", "2", "3"};
	private static final String[] GIDS = {"101", "102", "103", "104", "105"};
	private static final String BMS_INPUT_FILES_DIR = "/someDirectory/breeding_view/input";
	
	private MultiSiteDataExporter multiSiteDataExporter;
	
	@Captor
	private ArgumentCaptor<List<String[]>> meansRowsCaptor;
	
	@Captor
	private ArgumentCaptor<List<String[]>> summaryRowsCaptor;
	
	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;
	
	private Project project;
	
	private MultiSiteParameters multiSiteParameters;
	
	private List<Experiment> meansExperiments;
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
		
		this.createMultiSiteParameters();
		this.setupFileUtilMocks();
		this.meansTraits = this.createTraits(Arrays.asList(MEANS));
		this.summaryTraits = this.createTraits(Arrays.asList(SUMMARY_SUFFIX));
		this.gxeEnvironment = new GxeEnvironment();
		this.gxeEnvironment.setLabel(this.createGxeEnviornments(ENVIRONMENTS));
	
		this.createMeansExperiments();
		this.createSummaryExperiments();
	}

	private void setupFileUtilMocks() {
		Mockito.doReturn(BASIC_FILE_NAME + ".csv").when(this.multiSiteDataExporter).writeToCsvFile(Matchers.anyString(), Matchers.any(Project.class), Matchers.anyList(),
				Matchers.eq(false));
		Mockito.doReturn(BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv").when(this.multiSiteDataExporter).writeToCsvFile(Matchers.anyString(), Matchers.any(Project.class), Matchers.anyList(),
				Matchers.eq(true));
		Mockito.doReturn(BMS_INPUT_FILES_DIR).when(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
	}

	private void createMeansExperiments() {
		this.meansExperiments = new ArrayList<>();
		for (final String env : ENVIRONMENTS) {
			for (final String gid : GIDS) {
				final VariableList factors = new VariableList();
				DMSVariableType trialInstanceVariable = new DMSVariableType();
				trialInstanceVariable.setLocalName(ENV_FACTOR);
				factors.add(new Variable(trialInstanceVariable, env));
				DMSVariableType gidVariable = new DMSVariableType();
				gidVariable.setLocalName(GENOTYPE_FACTOR);
				factors.add(new Variable(gidVariable, gid));
				DMSVariableType locationVariable = new DMSVariableType();
				locationVariable.setLocalName(ENV_GROUP_FACTOR);
				factors.add(new Variable(locationVariable, BREEDING_LOCATION + env));
				
				final VariableList variates = new VariableList();
				for (int i = 1; i <= this.meansTraits.size(); i++) {
					final DMSVariableType traitVariable = new DMSVariableType();
					traitVariable.setLocalName(this.meansTraits.get(i-1).getName());
					final StandardVariable standardVar = new StandardVariable();
					standardVar.setId(Integer.valueOf(gid + i));
					traitVariable.setStandardVariable(standardVar);
					variates.add(new Variable(traitVariable, Double.valueOf(gid + "." + i)));
				}
				final Experiment experiment = new Experiment();
				experiment.setFactors(factors);
				experiment.setVariates(variates);
				this.meansExperiments.add(experiment);
			}
		}
	}
	
	private void createSummaryExperiments() {
		this.summaryExperiments = new ArrayList<>();
		for (final String env : ENVIRONMENTS) {
			final VariableList factors = new VariableList();
		final VariableList variates = new VariableList();
			DMSVariableType trialInstanceVariable = new DMSVariableType();
			trialInstanceVariable.setLocalName(ENV_FACTOR);
			factors.add(new Variable(trialInstanceVariable, env));
			
			for (int j = 1; j <= this.summaryTraits.size(); j++) {
				final DMSVariableType traitVariable = new DMSVariableType();
				traitVariable.setLocalName(this.summaryTraits.get(j-1).getName());
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
	
	@Test
	public void testExportMeansDatasetToCsv() {
		this.multiSiteDataExporter.exportMeansDatasetToCsv(BASIC_FILE_NAME, this.multiSiteParameters, this.meansExperiments, ENV_FACTOR,
				this.gxeEnvironment, this.meansTraits);
		
		Mockito.verify(this.multiSiteDataExporter).writeToCsvFile(Matchers.eq(BASIC_FILE_NAME), Matchers.eq(this.project), this.meansRowsCaptor.capture(),
				Matchers.eq(false));
		final List<String[]> csvRows = this.meansRowsCaptor.getValue();
		Assert.assertNotNull(csvRows);
		Assert.assertEquals(1 + (GIDS.length * ENVIRONMENTS.length), csvRows.size());
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
		for (final String env : ENVIRONMENTS) {
			for (final String gid : GIDS) {
				final String[] row = rowsIterator.next();
				Assert.assertEquals(env, row[0]);
				Assert.assertEquals(BREEDING_LOCATION + env, row[1]);
				Assert.assertEquals(gid, row[2]);
				for (int j = 1; j <= this.meansTraits.size(); j++) {
					Assert.assertEquals(gid + "." + j, row[2 + j]);
				}
			}
		}
	}
	
	@Test
	public void testExportTrialDatasetToSummaryStatsCsv() {
		this.multiSiteDataExporter.exportTrialDatasetToSummaryStatsCsv(BASIC_FILE_NAME, this.summaryExperiments, ENV_FACTOR,
				this.meansTraits, this.project);
		
		Mockito.verify(this.multiSiteDataExporter).writeToCsvFile(Matchers.eq(BASIC_FILE_NAME), Matchers.eq(this.project), this.summaryRowsCaptor.capture(),
				Matchers.eq(true));
		final List<String[]> csvRows = this.summaryRowsCaptor.getValue();
		Assert.assertNotNull(csvRows);
		Assert.assertEquals(1 + (this.meansTraits.size() * ENVIRONMENTS.length), csvRows.size());
		final Iterator<String[]> rowsIterator = csvRows.iterator();
		
		// Verify the header row
		final List<String> headers = new ArrayList<>();
		headers.add(ENV_FACTOR);
		headers.add("Trait");
		headers.addAll(Arrays.asList("NumValues", "NumMissing", "Mean", "Variance", "SD", "Min", "Max", "Range",
						"Median", "LowerQuartile", "UpperQuartile", "MeanRep", "MinRep", "MaxRep", "MeanSED", "MinSED", "MaxSED", "MeanLSD",
						"MinLSD", "MaxLSD", "CV", "Heritability", "WaldStatistic", "WaldDF", "Pvalue"));
		Assert.assertEquals(headers, Arrays.asList(rowsIterator.next()));
		// Verify data rows
		for (final String env : ENVIRONMENTS) {
			int k = 1;
			for (int i = 1; i <= this.meansTraits.size(); i++) {
				final String[] row = rowsIterator.next();
				Assert.assertEquals(env, row[0]);
				Assert.assertEquals(this.meansTraits.get(i-1).getName(), row[1]);
				int j = 2;
				// <Trait>_NumValue, <Trait>_NumMissing will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_Mean value
				Assert.assertEquals((Integer.valueOf(env) * 10)+ "." + k++, row[j++]);
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
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10)+ "." + k++).toString(), row[j++]);
				// <Trait>_MinSED, <Trait>_MaxSED, <Trait>_MeanLSD, <Trait>_MinLSD, <Trait>_MaxLSD will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_CV, <Trait>_Heritability values
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10)+ "." + k++).toString(), row[j++]);
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10)+ "." + k++).toString(), row[j++]);
				// <Trait>_WaldStatistic, <Trait>_WaldDF will always have blank values
				Assert.assertTrue(row[j++].isEmpty());
				Assert.assertTrue(row[j++].isEmpty());
				// <Trait>_PValue value
				Assert.assertEquals(Double.valueOf((Integer.valueOf(env) * 10)+ "." + k++).toString(), row[j++]);
			}
		}
	}
	
	@Test
	public void testGetCsvFileInWorkbenchDirectoryForMeans() {
		final File meansFile = this.multiSiteDataExporter.getCsvFileInWorkbenchDirectory(project, BASIC_FILE_NAME, false);
		Mockito.verify(this.installationDirectoryUtil).createWorkspaceDirectoriesForProject(this.project);
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
		Assert.assertEquals(new File(BMS_INPUT_FILES_DIR + File.separator + BASIC_FILE_NAME + ".csv").getAbsolutePath(),
				meansFile.getAbsolutePath());
	}
	
	@Test
	public void testGetCsvFileInWorkbenchDirectoryForSummaryStats() {
		final File meansFile = this.multiSiteDataExporter.getCsvFileInWorkbenchDirectory(project, BASIC_FILE_NAME, true);
		Mockito.verify(this.installationDirectoryUtil).createWorkspaceDirectoriesForProject(this.project);
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
		Assert.assertEquals(new File(BMS_INPUT_FILES_DIR + File.separator + BASIC_FILE_NAME + MultiSiteDataExporter.SUMMARY_STATS + ".csv")
				.getAbsolutePath(), meansFile.getAbsolutePath());
	}
	
	private void createMultiSiteParameters() {
		this.multiSiteParameters = new MultiSiteParameters();
		this.project = ProjectTestDataInitializer.createProject();
		multiSiteParameters.setProject(this.project);
		multiSiteParameters.setSelectedEnvGroupFactorName(ENV_GROUP_FACTOR);
		multiSiteParameters.setSelectedGenotypeFactorName(GENOTYPE_FACTOR);
		
	}

}

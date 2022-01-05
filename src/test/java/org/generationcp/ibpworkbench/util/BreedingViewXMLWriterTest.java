package org.generationcp.ibpworkbench.util;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.ColPos;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Covariate;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.RowPos;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.sea.xml.DataConfiguration;
import org.generationcp.commons.sea.xml.Design;
import org.generationcp.commons.sea.xml.Environments;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.data.initializer.SeaEnvironmentModelTestDataInitializer;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class BreedingViewXMLWriterTest {

	public static final String COLUMN_FACTOR = "Column_Factor";
	public static final String BLOCKS_FACTOR = "Blocks_Factor";
	public static final String COL_POS_FACTOR = "ColPos_Factor";
	public static final String ROWS_FACTOR = "Rows_Factor";
	public static final String ROW_POS_FACTOR = "RowPos_Factor";
	public static final String REPLICATES_FACTOR = "Replicates_Factor";
	public static final String PLOT_FACTOR = "Plot_Factor";
	public static final String USER_NAME = "UserName";
	private BreedingViewXMLWriter breedingViewXMLWriter;
	private BreedingViewInput breedingViewInput;

	private static final String DATASTORE_FILE = "Datastore.qsv";
	private static final String BV_FOLDER = System.getProperty("user.dir") + "/breeding_view/";
	private static final String INPUT_DIRECTORY = BreedingViewXMLWriterTest.BV_FOLDER + "input";
	private static final String OUTPUT_DIRECTORY = BreedingViewXMLWriterTest.BV_FOLDER + "output";
	private static final String PROJECT_NAME = "TEST_PROJECT";
	private static final String ANALYSIS_NAME = "SSA analysis";
	private static final int STUDY_ID = 2;
	private static final int PLOT_DATASET_ID = 2;
	private static final int MEANS_DATASET_ID = 0;
	private static final String ENVIRONMENT_NAME = "TRIAL_INSTANCE";
	private static final String SOURCE_FILE_PATH = BreedingViewXMLWriterTest.INPUT_DIRECTORY + "/test.csv";
	private static final String DEST_FILE_PATH = BreedingViewXMLWriterTest.INPUT_DIRECTORY + "/test.xml";
	private static final String WEB_API_URL =
			"http://localhost:18080/" + "bmsapi/breeding_view/{cropName}/ssa/save_result_summary&loggedInUserId=1&selectedProjectId=1";

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;

	private Project project;

	@Before
	public void setUp() throws Exception {
		this.project = ProjectTestDataInitializer.createProjectWithCropType();
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(this.project);

		this.breedingViewInput = this.createBreedingViewInput();
		this.breedingViewXMLWriter = new BreedingViewXMLWriter();
		this.breedingViewXMLWriter.setBreedingViewInput(this.breedingViewInput);
		this.breedingViewXMLWriter.setWebApiUrl(WEB_API_URL);
		this.breedingViewXMLWriter.setContextUtil(this.contextUtil);
		this.breedingViewXMLWriter.setInstallationDirectoryUtil(this.installationDirectoryUtil);
		this.createBreedingViewDirectories();

		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(securityContext);

	}

	@Test
	public void testCreateDesign() {

		final Design result = this.breedingViewXMLWriter.createDesign();

		// Make sure these properties have correct values.
		Assert.assertEquals(BreedingViewXMLWriterTest.COLUMN_FACTOR, result.getColumns().getName());
		Assert.assertEquals(BreedingViewXMLWriterTest.BLOCKS_FACTOR, result.getBlocks().getName());
		Assert.assertEquals(BreedingViewXMLWriterTest.COL_POS_FACTOR, result.getColPos().getName());
		Assert.assertEquals(BreedingViewXMLWriterTest.ROWS_FACTOR, result.getRows().getName());
		Assert.assertEquals(BreedingViewXMLWriterTest.ROW_POS_FACTOR, result.getRowPos().getName());
		Assert.assertEquals(BreedingViewXMLWriterTest.REPLICATES_FACTOR, result.getReplicates().getName());
		Assert.assertEquals(BreedingViewXMLWriterTest.PLOT_FACTOR, result.getPlot().getName());
		Assert.assertEquals(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName(), result.getType());

	}

	@Test
	public void testCreateDataConfiguration() {

		final Environments environments = new Environments();
		final Design design = new Design();
		final DataConfiguration dataConfiguration = this.breedingViewXMLWriter.createDataConfiguration(environments, design);

		Assert.assertSame(environments, dataConfiguration.getEnvironments());
		Assert.assertSame(design, dataConfiguration.getDesign());
		Assert.assertSame(this.breedingViewInput.getGenotypes(), dataConfiguration.getGenotypes());
		Assert.assertNotNull(dataConfiguration.getTraits());
		Assert.assertNotNull(dataConfiguration.getCovariates());

	}

	@Test
	public void testCreateTraits() {

		final List<Trait> traits = this.breedingViewXMLWriter.createTraits();
		Assert.assertEquals(1, traits.size());
		Assert.assertEquals("Variable1", traits.get(0).getName());
		Assert.assertTrue(traits.get(0).isActive());

	}

	@Test
	public void testCreateCovariates() {

		final List<Covariate> covariates = this.breedingViewXMLWriter.createCovariates();
		Assert.assertEquals(1, covariates.size());
		Assert.assertEquals("Variable1", covariates.get(0).getName());
		Assert.assertTrue(covariates.get(0).isActive());

	}

	@Test
	public void testRemovePreviousDatastore() throws Exception {

		this.breedingViewXMLWriter.removePreviousDatastore(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		Assert.assertFalse(BreedingViewXMLWriterTest.DATASTORE_FILE + " should not exist",
				new File(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY + "/" + BreedingViewXMLWriterTest.DATASTORE_FILE).exists());
		this.createDummyDatastoreFile(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		this.breedingViewXMLWriter.removePreviousDatastore(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		Assert.assertFalse(BreedingViewXMLWriterTest.DATASTORE_FILE + " should not exist",
				new File(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY + "/" + BreedingViewXMLWriterTest.DATASTORE_FILE).exists());
	}

	@Test
	public void testWriteProjectXML() throws Exception {
		final String filePath = this.breedingViewInput.getDestXMLFilePath();
		this.breedingViewInput.setTrialInstanceName(BreedingViewXMLWriterTest.ENVIRONMENT_NAME);
		this.breedingViewXMLWriter.writeProjectXML();

		Assert.assertTrue(filePath + " should exist", new File(filePath).exists());
	}

	@Test
	public void testGetWebApiUrl() {
		final Project project = ProjectTestDataInitializer.createProjectWithCropType();
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(project);
		final String url = this.breedingViewXMLWriter.getWebApiUrl();
		Mockito.verify(this.contextUtil).getProjectInContext();
		Assert.assertTrue(url.contains(project.getCropType().getCropName()));
	}

	@Test
	public void testCreateEnvironmentsWhereTrialFactorIsTrialInstance() {
		final SeaEnvironmentModel seaEnvironmentModel = SeaEnvironmentModelTestDataInitializer.createSeaEnvironmentModel();
		this.breedingViewInput.setSelectedEnvironments(Arrays.asList(seaEnvironmentModel));
		this.breedingViewInput.setTrialInstanceName(BreedingViewXMLWriterTest.ENVIRONMENT_NAME);
		final Environments environments = this.breedingViewXMLWriter.createEnvironments();
		Assert.assertEquals(this.breedingViewInput.getEnvironment().getName(), environments.getName());
		Assert.assertNull(environments.getTrialName());
		Assert.assertEquals(1, environments.getEnvironments().size());
		final org.generationcp.commons.sea.xml.Environment environment = environments.getEnvironments().get(0);
		Assert.assertEquals(seaEnvironmentModel.getEnvironmentName(), environment.getName());
		Assert.assertTrue(environment.getActive());
		Assert.assertNull(environment.getTrial());
	}

	@Test
	public void testCreateEnvironmentsWhereTrialFactorIsNotTrialInstance() {
		final SeaEnvironmentModel seaEnvironmentModel = SeaEnvironmentModelTestDataInitializer.createSeaEnvironmentModel();
		this.breedingViewInput.setSelectedEnvironments(Arrays.asList(seaEnvironmentModel));
		this.breedingViewInput.getEnvironment().setName("LOCATION_NAME");
		this.breedingViewInput.setTrialInstanceName(BreedingViewXMLWriterTest.ENVIRONMENT_NAME);

		final Environments environments = this.breedingViewXMLWriter.createEnvironments();
		Assert.assertEquals(this.breedingViewInput.getEnvironment().getName(), environments.getName());
		Assert.assertEquals(this.breedingViewInput.getTrialInstanceName(), environments.getTrialName());
		Assert.assertEquals(1, environments.getEnvironments().size());
		final org.generationcp.commons.sea.xml.Environment environment = environments.getEnvironments().get(0);
		Assert.assertEquals(seaEnvironmentModel.getEnvironmentName(), environment.getName());
		Assert.assertTrue(environment.getActive());
		Assert.assertEquals(seaEnvironmentModel.getTrialno(), environment.getTrial());
	}

	@After
	public void cleanUp() throws Exception {
		this.deleteBreedingViewDirectories();
	}

	private void createDummyDatastoreFile(final String outputDirectory) throws IOException {
		final File dataStoreFile = new File(outputDirectory + "/" + BreedingViewXMLWriterTest.DATASTORE_FILE);
		final BufferedWriter output = new BufferedWriter(new FileWriter(dataStoreFile));
		output.write("Datastore content");
		output.close();
	}

	private void deleteBreedingViewDirectories() {
		final File bvDir = new File(BreedingViewXMLWriterTest.BV_FOLDER);
		this.deleteFile(bvDir);
	}

	private void deleteFile(final File fileToBeDeleted) {
		final File[] files = fileToBeDeleted.listFiles();
		if (files != null && files.length > 0) {
			for (final File file : files) {
				this.deleteFile(file);
			}
		}
		fileToBeDeleted.delete();
	}

	private BreedingViewInput createBreedingViewInput() {
		final BreedingViewInput breedingViewInput = new BreedingViewInput();
		breedingViewInput.setBreedingViewProjectName(BreedingViewXMLWriterTest.PROJECT_NAME);
		breedingViewInput.setBreedingViewAnalysisName(BreedingViewXMLWriterTest.ANALYSIS_NAME);
		breedingViewInput.setStudyId(BreedingViewXMLWriterTest.STUDY_ID);
		breedingViewInput.setDatasetId(BreedingViewXMLWriterTest.PLOT_DATASET_ID);
		breedingViewInput.setOutputDatasetId(BreedingViewXMLWriterTest.MEANS_DATASET_ID);
		breedingViewInput.setSourceXLSFilePath(BreedingViewXMLWriterTest.SOURCE_FILE_PATH);
		breedingViewInput.setDestXMLFilePath(BreedingViewXMLWriterTest.DEST_FILE_PATH);

		final Environment environment = new Environment();
		environment.setName(BreedingViewXMLWriterTest.ENVIRONMENT_NAME);

		breedingViewInput.setEnvironment(environment);
		breedingViewInput.setSelectedEnvironments(new ArrayList<SeaEnvironmentModel>());

		final Map<String, Boolean> variatesSelectionMap = new HashMap<>();
		variatesSelectionMap.put("Variable1", true);
		variatesSelectionMap.put("Variable2", false);
		breedingViewInput.setVariatesSelectionMap(variatesSelectionMap);

		final Map<String, Boolean> covariatesSelectionMap = new HashMap<>();
		covariatesSelectionMap.put("Variable3", false);
		covariatesSelectionMap.put("Variable4", true);
		breedingViewInput.setCovariatesSelectionMap(variatesSelectionMap);

		final Columns columns = new Columns();
		columns.setName(BreedingViewXMLWriterTest.COLUMN_FACTOR);
		breedingViewInput.setColumns(columns);

		final Blocks blocks = new Blocks();
		blocks.setName(BreedingViewXMLWriterTest.BLOCKS_FACTOR);
		breedingViewInput.setBlocks(blocks);

		final ColPos colPos = new ColPos();
		colPos.setName(BreedingViewXMLWriterTest.COL_POS_FACTOR);
		breedingViewInput.setColPos(colPos);

		final Rows rows = new Rows();
		rows.setName(BreedingViewXMLWriterTest.ROWS_FACTOR);
		breedingViewInput.setRows(rows);

		final RowPos rowPos = new RowPos();
		rowPos.setName(BreedingViewXMLWriterTest.ROW_POS_FACTOR);
		breedingViewInput.setRowPos(rowPos);

		final Replicates replicates = new Replicates();
		replicates.setName(BreedingViewXMLWriterTest.REPLICATES_FACTOR);
		breedingViewInput.setReplicates(replicates);

		final Plot plot = new Plot();
		plot.setName(BreedingViewXMLWriterTest.PLOT_FACTOR);
		breedingViewInput.setPlot(plot);

		breedingViewInput.setDesignType(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName());

		return breedingViewInput;
	}

	private void createBreedingViewDirectories() {
		final File inputDir = new File(BreedingViewXMLWriterTest.INPUT_DIRECTORY);
		if (!inputDir.exists()) {
			inputDir.mkdirs();
		}
		final File outputDir = new File(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
	}
}

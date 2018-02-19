package org.generationcp.ibpworkbench.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.ColPos;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.RowPos;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.sea.xml.Design;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
	
	@Before
	public void setUp() throws Exception {
		final WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		Mockito.when(contextUtil.getProjectInContext()).thenReturn(ProjectTestDataInitializer.createProjectWithCropType());

		this.breedingViewInput = this.createBreedingViewInput();
		this.breedingViewXMLWriter = new BreedingViewXMLWriter(this.breedingViewInput);
		this.breedingViewXMLWriter.setWebApiUrl(WEB_API_URL);
		this.breedingViewXMLWriter.setContextUtil(contextUtil);
		this.breedingViewXMLWriter.setWorkbenchDataManager(workbenchDataManager);
		this.breedingViewXMLWriter.setInstallationDirectoryUtil(this.installationDirectoryUtil);
		this.createBreedingViewDirectories();

		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		final Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getName()).thenReturn(USER_NAME);

		SecurityContextHolder.setContext(securityContext);

	}

	@Test
	public void testCreateDesign() {

		final Design result = breedingViewXMLWriter.createDesign();

		// Make sure these properties have correct values.
		Assert.assertEquals(COLUMN_FACTOR, result.getColumns().getName());
		Assert.assertEquals(BLOCKS_FACTOR, result.getBlocks().getName());
		Assert.assertEquals(COL_POS_FACTOR, result.getColPos().getName());
		Assert.assertEquals(ROWS_FACTOR, result.getRows().getName());
		Assert.assertEquals(ROW_POS_FACTOR, result.getRowPos().getName());
		Assert.assertEquals(REPLICATES_FACTOR, result.getReplicates().getName());
		Assert.assertEquals(PLOT_FACTOR, result.getPlot().getName());
		Assert.assertEquals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), result.getType());

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
		Mockito.doReturn(OUTPUT_DIRECTORY).when(this.installationDirectoryUtil)
				.getOutputDirectoryForProjectAndTool(Mockito.any(Project.class), Mockito.any(Tool.class));
		final String filePath = this.breedingViewInput.getDestXMLFilePath();
		this.breedingViewXMLWriter.writeProjectXML();
		
		Mockito.verify(this.installationDirectoryUtil).getOutputDirectoryForProjectAndTool(Mockito.any(Project.class),
				Mockito.any(Tool.class));
		Assert.assertTrue(filePath + " should exist", new File(filePath).exists());
	}
	
	@Test
	public void testGetWebApiUrl() {
		final Project project = ProjectTestDataInitializer.createProjectWithCropType();
		Mockito.when(contextUtil.getProjectInContext()).thenReturn(project);
		final String url = this.breedingViewXMLWriter.getWebApiUrl();
		Mockito.verify(this.contextUtil).getProjectInContext();
		Assert.assertTrue(url.contains(project.getCropType().getCropName()));
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
		breedingViewInput.setVariatesActiveState(new HashMap<String, Boolean>());

		final Columns columns = new Columns();
		columns.setName(COLUMN_FACTOR);
		breedingViewInput.setColumns(columns);

		final Blocks blocks = new Blocks();
		blocks.setName(BLOCKS_FACTOR);
		breedingViewInput.setBlocks(blocks);

		final ColPos colPos = new ColPos();
		colPos.setName(COL_POS_FACTOR);
		breedingViewInput.setColPos(colPos);

		final Rows rows = new Rows();
		rows.setName(ROWS_FACTOR);
		breedingViewInput.setRows(rows);

		final RowPos rowPos = new RowPos();
		rowPos.setName(ROW_POS_FACTOR);
		breedingViewInput.setRowPos(rowPos);

		final Replicates replicates = new Replicates();
		replicates.setName(REPLICATES_FACTOR);
		breedingViewInput.setReplicates(replicates);

		final Plot plot = new Plot();
		plot.setName(PLOT_FACTOR);
		breedingViewInput.setPlot(plot);

		breedingViewInput.setDesignType(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

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


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
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BreedingViewXMLWriterTest {

	public static final String COLUMN_FACTOR = "Column_Factor";
	public static final String BLOCKS_FACTOR = "Blocks_Factor";
	public static final String COL_POS_FACTOR = "ColPos_Factor";
	public static final String ROWS_FACTOR = "Rows_Factor";
	public static final String ROW_POS_FACTOR = "RowPos_Factor";
	public static final String REPLICATES_FACTOR = "Replicates_Factor";
	public static final String PLOT_FACTOR = "Plot_Factor";
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
	private static final String WEB_API_URL = "http://localhost:18080/"
			+ "bmsapi/breeding_view/{cropName}/ssa/save_result_summary&loggedInUserId=1&selectedProjectId=1";
	private static final String INSTALLATION_DIRECTORY = "C://Breeding Management System/";

	@Before
	public void setUp() throws Exception {

		ContextUtil contextUtil = Mockito.mock(ContextUtil.class);
		WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		Mockito.when(contextUtil.getProjectInContext()).thenReturn(this.createProject());
		Mockito.when(workbenchDataManager.getWorkbenchSetting()).thenReturn(this.createWorkbenchSetting());

		this.breedingViewInput = this.createBreedingViewInput();
		this.breedingViewXMLWriter = new BreedingViewXMLWriter(this.breedingViewInput);
		this.breedingViewXMLWriter.setWebApiUrl(WEB_API_URL);
		breedingViewXMLWriter.setContextUtil(contextUtil);
		breedingViewXMLWriter.setWorkbenchDataManager(workbenchDataManager);
		this.createBreedingViewDirectories();

	}

	@Test
	public void testCreateDesign() {

		Design result = breedingViewXMLWriter.createDesign();

		// Make sure these properties have correct values.
		Assert.assertEquals(COLUMN_FACTOR,result.getColumns().getName());
		Assert.assertEquals(BLOCKS_FACTOR,result.getBlocks().getName());
		Assert.assertEquals(COL_POS_FACTOR,result.getColPos().getName());
		Assert.assertEquals(ROWS_FACTOR, result.getRows().getName());
		Assert.assertEquals(ROW_POS_FACTOR,result.getRowPos().getName());
		Assert.assertEquals(REPLICATES_FACTOR, result.getReplicates().getName());
		Assert.assertEquals(PLOT_FACTOR,result.getPlot().getName());
		Assert.assertEquals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(),result.getType());

	}

	@Test
	public void testRemovePreviousDatastore() throws Exception {

		this.breedingViewXMLWriter.removePreviousDatastore(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		Assert.assertFalse(BreedingViewXMLWriterTest.DATASTORE_FILE + " should not exist", new File(
				BreedingViewXMLWriterTest.OUTPUT_DIRECTORY + "/" + BreedingViewXMLWriterTest.DATASTORE_FILE).exists());
		this.createDummyDatastoreFile(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		this.breedingViewXMLWriter.removePreviousDatastore(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		Assert.assertFalse(BreedingViewXMLWriterTest.DATASTORE_FILE + " should not exist", new File(
				BreedingViewXMLWriterTest.OUTPUT_DIRECTORY + "/" + BreedingViewXMLWriterTest.DATASTORE_FILE).exists());
	}

	@Test
	public void testWriteProjectXML() throws Exception {
		String filePath = this.breedingViewInput.getDestXMLFilePath();
		this.breedingViewXMLWriter.writeProjectXML();
		Assert.assertTrue(filePath + " should exist", new File(filePath).exists());
	}

	@After
	public void cleanUp() throws Exception {
		this.deleteBreedingViewDirectories();
	}

	private void createDummyDatastoreFile(String outputDirectory) throws IOException {
		File dataStoreFile = new File(outputDirectory + "/" + BreedingViewXMLWriterTest.DATASTORE_FILE);
		BufferedWriter output = new BufferedWriter(new FileWriter(dataStoreFile));
		output.write("Datastore content");
		output.close();
	}

	private void deleteBreedingViewDirectories() {
		File bvDir = new File(BreedingViewXMLWriterTest.BV_FOLDER);
		this.deleteFile(bvDir);
	}

	private void deleteFile(File fileToBeDeleted) {
		File[] files = fileToBeDeleted.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				this.deleteFile(file);
			}
		}
		fileToBeDeleted.delete();
	}


	private BreedingViewInput createBreedingViewInput() {
		BreedingViewInput breedingViewInput = new BreedingViewInput();
		breedingViewInput.setBreedingViewProjectName(BreedingViewXMLWriterTest.PROJECT_NAME);
		breedingViewInput.setBreedingViewAnalysisName(BreedingViewXMLWriterTest.ANALYSIS_NAME);
		breedingViewInput.setStudyId(BreedingViewXMLWriterTest.STUDY_ID);
		breedingViewInput.setDatasetId(BreedingViewXMLWriterTest.PLOT_DATASET_ID);
		breedingViewInput.setOutputDatasetId(BreedingViewXMLWriterTest.MEANS_DATASET_ID);
		breedingViewInput.setSourceXLSFilePath(BreedingViewXMLWriterTest.SOURCE_FILE_PATH);
		breedingViewInput.setDestXMLFilePath(BreedingViewXMLWriterTest.DEST_FILE_PATH);

		Environment environment = new Environment();
		environment.setName(BreedingViewXMLWriterTest.ENVIRONMENT_NAME);


		breedingViewInput.setEnvironment(environment);
		breedingViewInput.setSelectedEnvironments(new ArrayList<SeaEnvironmentModel>());
		breedingViewInput.setVariatesActiveState(new HashMap<String, Boolean>());

		Columns columns = new Columns();
		columns.setName(COLUMN_FACTOR);
		breedingViewInput.setColumns(columns);

		Blocks blocks = new Blocks();
		blocks.setName(BLOCKS_FACTOR);
		breedingViewInput.setBlocks(blocks);

		ColPos colPos = new ColPos();
		colPos.setName(COL_POS_FACTOR);
		breedingViewInput.setColPos(colPos);

		Rows rows = new Rows();
		rows.setName(ROWS_FACTOR);
		breedingViewInput.setRows(rows);

		RowPos rowPos = new RowPos();
		rowPos.setName(ROW_POS_FACTOR);
		breedingViewInput.setRowPos(rowPos);

		Replicates replicates = new Replicates();
		replicates.setName(REPLICATES_FACTOR);
		breedingViewInput.setReplicates(replicates);

		Plot plot = new Plot();
		plot.setName(PLOT_FACTOR);
		breedingViewInput.setPlot(plot);

		breedingViewInput.setDesignType(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		return breedingViewInput;
	}

	private WorkbenchSetting createWorkbenchSetting() {

		WorkbenchSetting workbenchSetting = new WorkbenchSetting();
		workbenchSetting.setInstallationDirectory(INSTALLATION_DIRECTORY);

		return workbenchSetting;
	}

	private Project createProject() {
		Project workbenchProject = new Project();
		workbenchProject.setCropType(new CropType(CropType.CropEnum.MAIZE.name()));
		workbenchProject.setProjectId(1L);
		return workbenchProject;
	}

	private void createBreedingViewDirectories() {
		File inputDir = new File(BreedingViewXMLWriterTest.INPUT_DIRECTORY);
		if (!inputDir.exists()) {
			inputDir.mkdirs();
		}
		File outputDir = new File(BreedingViewXMLWriterTest.OUTPUT_DIRECTORY);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
	}
}

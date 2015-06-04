
package org.generationcp.ibpworkbench.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BreedingViewXMLWriterTest {

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
			+ "IBPWebService/rest/breeding_view/ssa/save_result_summary&loggedInUserId=1&selectedProjectId=1";
	private static final String INSTALLATION_DIRECTORY = "C://Breeding Management System/";
	private static final Project workbenchProject = new Project();

	@Before
	public void setUp() throws Exception {
		this.breedingViewInput = this.createBreedingViewInput();
		this.breedingViewXMLWriter = Mockito.spy(new BreedingViewXMLWriter(this.breedingViewInput));
		this.createBreedingViewDirectories();
		Mockito.doReturn(BreedingViewXMLWriterTest.WEB_API_URL).when(this.breedingViewXMLWriter).getWebApiUrl();
		Mockito.doReturn(BreedingViewXMLWriterTest.INSTALLATION_DIRECTORY).when(this.breedingViewXMLWriter).getInstallationDirectory();
		Mockito.doReturn(BreedingViewXMLWriterTest.workbenchProject).when(this.breedingViewXMLWriter).getLastOpenedProject();
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
		return breedingViewInput;
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

	private void createDummyDatastoreFile(String outputDirectory) throws IOException {
		File dataStoreFile = new File(outputDirectory + "/" + BreedingViewXMLWriterTest.DATASTORE_FILE);
		BufferedWriter output = new BufferedWriter(new FileWriter(dataStoreFile));
		output.write("Datastore content");
		output.close();
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
}

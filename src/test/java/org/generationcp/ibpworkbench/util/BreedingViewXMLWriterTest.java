package org.generationcp.ibpworkbench.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
import org.junit.Before;
import org.junit.Test;

public class BreedingViewXMLWriterTest {
	
	private BreedingViewXMLWriter breedingViewXMLWriter;
	private BreedingViewInput breedingViewInput;
	
	private static final String DATASTORE_FILE = "Datastore.qsv";
	private static final String BV_FOLDER = System.getProperty("user.dir") + "/breeding_view/";
	private static final String INPUT_DIRECTORY = BV_FOLDER + "input";
	private static final String OUTPUT_DIRECTORY = BV_FOLDER + "output";
	private static final String PROJECT_NAME = "TEST_PROJECT";
	private static final String ANALYSIS_NAME = "SSA analysis";
	private static final int STUDY_ID = 2;
	private static final int PLOT_DATASET_ID = 2;
	private static final int MEANS_DATASET_ID = 0;
	private static final String ENVIRONMENT_NAME = "TRIAL_INSTANCE";
	private static final String SOURCE_FILE_PATH = INPUT_DIRECTORY+"/test.csv";
	private static final String DEST_FILE_PATH = INPUT_DIRECTORY+"/test.xml";
	private static final String WEB_API_URL = "http://localhost:18080/" +
			"IBPWebService/rest/breeding_view/ssa/save_result_summary&loggedInUserId=1&selectedProjectId=1";
	private static final String INSTALLATION_DIRECTORY = "C://Breeding Management System/";
	private static final Project workbenchProject = new Project();
	
	@Before
	public void setUp() throws Exception {
		breedingViewInput = createBreedingViewInput();
		breedingViewXMLWriter = spy(new BreedingViewXMLWriter(breedingViewInput));
		createBreedingViewDirectories();
		doReturn(WEB_API_URL).when(breedingViewXMLWriter).getWebApiUrl();
		doReturn(INSTALLATION_DIRECTORY).when(breedingViewXMLWriter).getInstallationDirectory();
		doReturn(workbenchProject).when(breedingViewXMLWriter).getLastOpenedProject();
	}

	private void createBreedingViewDirectories() {
		File inputDir = new File(INPUT_DIRECTORY);
		if(!inputDir.exists()) {
			inputDir.mkdirs();
		}
		File outputDir = new File(OUTPUT_DIRECTORY);
		if(!outputDir.exists()) {
			outputDir.mkdirs();
		}
	}

	private BreedingViewInput createBreedingViewInput() {
		BreedingViewInput breedingViewInput = new BreedingViewInput();
		breedingViewInput.setBreedingViewProjectName(PROJECT_NAME);
		breedingViewInput.setBreedingViewAnalysisName(ANALYSIS_NAME);
		breedingViewInput.setStudyId(STUDY_ID);
		breedingViewInput.setDatasetId(PLOT_DATASET_ID);
		breedingViewInput.setOutputDatasetId(MEANS_DATASET_ID);
		breedingViewInput.setSourceXLSFilePath(SOURCE_FILE_PATH);
		breedingViewInput.setDestXMLFilePath(DEST_FILE_PATH);
		Environment environment = new Environment();
		environment.setName(ENVIRONMENT_NAME);
		breedingViewInput.setEnvironment(environment);
		breedingViewInput.setSelectedEnvironments(new ArrayList<SeaEnvironmentModel>());
		breedingViewInput.setVariatesActiveState(new HashMap<String, Boolean>());
		return breedingViewInput;
	}

	@Test
	public void testRemovePreviousDatastore() throws Exception {
		
		breedingViewXMLWriter.removePreviousDatastore(OUTPUT_DIRECTORY);
		assertFalse(DATASTORE_FILE+" should not exist",
				new File(OUTPUT_DIRECTORY+"/"+DATASTORE_FILE).exists());
		createDummyDatastoreFile(OUTPUT_DIRECTORY);
		breedingViewXMLWriter.removePreviousDatastore(OUTPUT_DIRECTORY);
		assertFalse(DATASTORE_FILE+" should not exist",
				new File(OUTPUT_DIRECTORY+"/"+DATASTORE_FILE).exists());
	}

	private void createDummyDatastoreFile(String outputDirectory) throws IOException {
		File dataStoreFile = new File(outputDirectory+"/"+DATASTORE_FILE);
        BufferedWriter output = new BufferedWriter(new FileWriter(dataStoreFile));
        output.write("Datastore content");
        output.close();
	}
	
	@Test
	public void testWriteProjectXML() throws Exception {
		String filePath = breedingViewInput.getDestXMLFilePath();
		breedingViewXMLWriter.writeProjectXML();
		assertTrue(filePath+" should exist",
				new File(filePath).exists());
	}
	
	@After
	public void cleanUp() throws Exception {
		deleteBreedingViewDirectories();
	}

	private void deleteBreedingViewDirectories() {
		File bvDir = new File(BV_FOLDER);
		deleteFile(bvDir);
	}
	
	private void deleteFile(File fileToBeDeleted) {
		File[] files = fileToBeDeleted.listFiles();
		if(files!=null && files.length>0) {
			for (File file : files) {
				deleteFile(file);
			}
		}
		fileToBeDeleted.delete();
	}
}

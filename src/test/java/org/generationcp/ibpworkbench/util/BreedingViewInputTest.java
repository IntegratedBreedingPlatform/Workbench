
package org.generationcp.ibpworkbench.util;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class BreedingViewInputTest {

	private static final String TEST_BV_INPUT_DIRECTORY = "c:\\BMS4\\workspace\\Maize\\breeding_view\\input";
	private static final String TEST_PROJECT_NAME = "ABC'sTrial";
    private static final String NORMALIZED_PROJECT_NAME ="ABC_sTrial";
    private static final String NORMALIZED_DATASET_NAME = "ABC_sTrial_PLOTDATA";

	private BreedingViewInput unitUnderTest;

	@Before
	public void setUp() throws Exception {
		unitUnderTest = new BreedingViewInput();
		unitUnderTest.setDatasetName(TEST_PROJECT_NAME + "-PLOTDATA");
		unitUnderTest.setSourceXLSFilePath(TEST_BV_INPUT_DIRECTORY + "\\" + TEST_PROJECT_NAME + "-PLOTDATA.csv");
		unitUnderTest.setDestXMLFilePath(TEST_BV_INPUT_DIRECTORY + "\\" + TEST_PROJECT_NAME + "-PLOTDATA.xml");
		unitUnderTest.setBreedingViewAnalysisName("SSA analysis of " + TEST_PROJECT_NAME);
		unitUnderTest.setBreedingViewProjectName(TEST_PROJECT_NAME);
	}

	@Test
	public void testNormalizeBreedingViewInput() {
		unitUnderTest.normalizeBreedingViewInput();

		Assert.assertEquals("Source CSV file path not properly normalized", TEST_BV_INPUT_DIRECTORY + "\\" + NORMALIZED_DATASET_NAME + ".csv",
				unitUnderTest.getSourceXLSFilePath());
        Assert.assertEquals("Destination XML file path not properly normalized", TEST_BV_INPUT_DIRECTORY + "\\" + NORMALIZED_DATASET_NAME + ".xml",
                unitUnderTest.getDestXMLFilePath());
        Assert.assertEquals("Breeding view project name not properly normalized", NORMALIZED_PROJECT_NAME,
                unitUnderTest.getBreedingViewProjectName());
    }

}

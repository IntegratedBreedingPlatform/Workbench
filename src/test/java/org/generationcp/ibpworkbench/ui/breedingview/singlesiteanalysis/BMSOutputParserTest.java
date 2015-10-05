
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputParser.ZipFileInvalidContentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BMSOutputParserTest {

	private static File bmsOutputZipFile;
	private static File bmsOutputZipFileNoContent;

	@BeforeClass
	public static void runOnce() throws URISyntaxException {
		bmsOutputZipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		bmsOutputZipFileNoContent = new File(ClassLoader.getSystemClassLoader().getResource("zipToExtractNoContent.zip").toURI());
	}

	@Test
	public void testParseZipFileValidZipFile() throws URISyntaxException, ZipFileInvalidContentException {

		BMSOutputParser bmsOutputParser = new BMSOutputParser();

		bmsOutputParser.setUploadDirectory(ClassLoader.getSystemResource("").getPath());

		Map<String, String> bmsInformation = bmsOutputParser.parseZipFile(bmsOutputZipFile);

		Assert.assertEquals("3", bmsInformation.get(BMSOutputParser.INPUT_DATASET_ID_INFO));
		Assert.assertEquals("4", bmsInformation.get(BMSOutputParser.OUTPUT_DATASET_ID_INFO));
		Assert.assertEquals("2", bmsInformation.get(BMSOutputParser.STUDY_ID_INFO));
		Assert.assertEquals("1", bmsInformation.get(BMSOutputParser.WORKBENCH_PROJECT_ID_INFO));

		Assert.assertNotNull(bmsOutputParser.getMeansFile());
		Assert.assertNotNull(bmsOutputParser.getOutlierFile());
		Assert.assertNotNull(bmsOutputParser.getSummaryStatsFile());

		bmsOutputParser.deleteTemporaryFiles();
	}

	@Test(expected = ZipFileInvalidContentException.class)
	public void testParseZipFileInvalidZipFile() throws URISyntaxException, ZipFileInvalidContentException {

		BMSOutputParser bmsOutputParser = new BMSOutputParser();
		bmsOutputParser.setUploadDirectory(ClassLoader.getSystemResource("").getPath());

		bmsOutputParser.parseZipFile(bmsOutputZipFileNoContent);

	}

	@Test
	public void testExtractEnvironmentInfoFromFile() throws URISyntaxException, IOException, ZipFileInvalidContentException {

		BMSOutputParser bmsOutputParser = new BMSOutputParser();

		bmsOutputParser.setUploadDirectory(ClassLoader.getSystemResource("").getPath());

		bmsOutputParser.parseZipFile(bmsOutputZipFile);
		Map<String, Object> environmentInfo = bmsOutputParser.extractEnvironmentInfoFromFile();

		Assert.assertEquals("TRIAL_INSTANCE", environmentInfo.get(BMSOutputParser.ENVIRONMENT_FACTOR));

		Set<String> ennvironmentNames = (HashSet) environmentInfo.get(BMSOutputParser.ENVIRONMENT_NAMES);

		Assert.assertTrue(!ennvironmentNames.isEmpty());
		Assert.assertTrue(ennvironmentNames.contains("1"));

		bmsOutputParser.deleteTemporaryFiles();

	}
}

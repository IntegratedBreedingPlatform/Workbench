
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

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

		BMSOutputInformation bmsInformation = bmsOutputParser.parseZipFile(bmsOutputZipFile);

		Assert.assertEquals(3, bmsInformation.getInputDataSetId());
		Assert.assertEquals(4, bmsInformation.getOutputDataSetId());
		Assert.assertEquals(2, bmsInformation.getStudyId());
		Assert.assertEquals(1, bmsInformation.getWorkbenchProjectId());

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

		BMSOutputInformation bmsInformation = new BMSOutputInformation();
		bmsOutputParser.extractEnvironmentInfoFromFile(bmsOutputParser.getMeansFile(), bmsInformation);

		Assert.assertEquals("TRIAL_INSTANCE", bmsInformation.getEnvironmentFactorName());

		Assert.assertTrue(!bmsInformation.getEnvironmentNames().isEmpty());
		Assert.assertTrue(bmsInformation.getEnvironmentNames().contains("1"));

		bmsOutputParser.deleteTemporaryFiles();

	}
}


package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputParser.ZipFileInvalidContentException;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class BMSOutputParserTest {
	
	@Mock
	private ContextUtil contextUtil;
	
	@InjectMocks
	private BMSOutputParser bmsOutputParser;
	
	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	
	private Project project = ProjectTestDataInitializer.createProject();

	private static File bmsOutputZipFile;
	private static File bmsOutputZipFileNoContent;

	@BeforeClass
	public static void runOnce() throws URISyntaxException {
		BMSOutputParserTest.bmsOutputZipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		BMSOutputParserTest.bmsOutputZipFileNoContent =
				new File(ClassLoader.getSystemClassLoader().getResource("zipToExtractNoContent.zip").toURI());
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();
		
		// Create input directory where files will be extracted to
		final String inputDirectory = this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(this.project, ToolName.BV_SSA);
		final File inputDirectoryFile = new File(inputDirectory);
		if (!inputDirectoryFile.exists()){
			inputDirectoryFile.mkdirs();
		}
	}

	@Test
	public void testParseZipFileValidZipFile() throws URISyntaxException, ZipFileInvalidContentException {
		final BMSOutputInformation bmsInformation = bmsOutputParser.parseZipFile(BMSOutputParserTest.bmsOutputZipFile);

		Assert.assertEquals(3, bmsInformation.getInputDataSetId());
		Assert.assertEquals(4, bmsInformation.getOutputDataSetId());
		Assert.assertEquals(2, bmsInformation.getStudyId());
		Assert.assertEquals(1, bmsInformation.getWorkbenchProjectId());

		Assert.assertNotNull(bmsOutputParser.getMeansFile());
		Assert.assertNotNull(bmsOutputParser.getOutlierFile());
		Assert.assertNotNull(bmsOutputParser.getSummaryStatsFile());

		bmsOutputParser.deleteTemporaryFiles();
	}
	
	private void deleteTestInstallationDirectory() {
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR);
		this.installationDirectoryUtil.recursiveFileDelete(testInstallationDirectory);
	}

	@Test(expected = ZipFileInvalidContentException.class)
	public void testParseZipFileInvalidZipFile() throws URISyntaxException, ZipFileInvalidContentException {
		bmsOutputParser.parseZipFile(BMSOutputParserTest.bmsOutputZipFileNoContent);

	}

	@Test
	public void testExtractEnvironmentInfoFromFile() throws URISyntaxException, IOException, ZipFileInvalidContentException {

		bmsOutputParser.parseZipFile(BMSOutputParserTest.bmsOutputZipFile);

		final BMSOutputInformation bmsInformation = new BMSOutputInformation();
		bmsOutputParser.extractEnvironmentInfoFromFile(bmsOutputParser.getMeansFile(), bmsInformation);

		Assert.assertEquals("TRIAL_INSTANCE", bmsInformation.getEnvironmentFactorName());

		Assert.assertTrue(!bmsInformation.getEnvironmentNames().isEmpty());
		Assert.assertTrue(bmsInformation.getEnvironmentNames().contains("1"));

		bmsOutputParser.deleteTemporaryFiles();
	}
	
	@After
	public void cleanup() {
		this.deleteTestInstallationDirectory();
	}
}

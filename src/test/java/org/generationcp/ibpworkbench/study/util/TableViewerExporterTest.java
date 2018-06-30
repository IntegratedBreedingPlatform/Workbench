package org.generationcp.ibpworkbench.study.util;

import java.io.File;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TableViewerExporterTest {
	
	private static final String STUDY_NAME = "Test Study";

	@Mock
	private ContextUtil contextUtil;
	
	@InjectMocks
	private TableViewerExporter tableViewerExporter;
	
	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.tableViewerExporter.setContextUtil(this.contextUtil);
		Mockito.doReturn(ProjectTestDataInitializer.createProject()).when(this.contextUtil).getProjectInContext();
	}
	
	@Test
	public void testWriteExcelFile() throws DatasetExporterException {
		final String filePath = this.tableViewerExporter.writeExcelFile(STUDY_NAME, new HSSFWorkbook());
		
		final File excelFile = new File(filePath);
		Assert.assertTrue(excelFile.exists());
		Assert.assertTrue(excelFile.getName().startsWith(STUDY_NAME));
		Assert.assertTrue(excelFile.getName().endsWith(".xlsx"));
		final String outputDirectory = this.installationDirectoryUtil
				.getOutputDirectoryForProjectAndTool(this.contextUtil.getProjectInContext(), ToolName.STUDY_BROWSER);
		final File outputDirectoryFile = new File(outputDirectory);
		Assert.assertTrue(outputDirectoryFile.exists());
		Assert.assertEquals(outputDirectoryFile, excelFile.getParentFile());
		
		this.deleteTestInstallationDirectory();
	}
	
	private void deleteTestInstallationDirectory() {
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR);
		this.installationDirectoryUtil.recursiveFileDelete(testInstallationDirectory);
	}

}

package org.generationcp.ibpworkbench.cross.study.h2h.main.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.cross.study.h2h.main.ResultsComponent;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.ResultsData;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TraitForComparison;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class HeadToHeadDataListExportTest {
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private List<ResultsData> resultsData;
	
	@Mock
	private Set<TraitForComparison> traitsForComparison;
	
	@Mock
	private Map<String, String> columnMessageMap;
	
	private HSSFWorkbook workbook = new HSSFWorkbook();
	private String[] columnIdArray = {};
	private HeadToHeadDataListExport h2hDataListExport;
	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	
	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);
		
		// Need to spy so that writing of XLS Workbook file contents can be skipped (not enough time to setup data)
		this.h2hDataListExport = Mockito.spy(new HeadToHeadDataListExport());
		this.h2hDataListExport.setContextUtil(this.contextUtil);
		
		Mockito.doReturn(ProjectTestDataInitializer.createProject()).when(this.contextUtil).getProjectInContext();
		Mockito.doReturn(this.workbook).when(this.h2hDataListExport).createExcelWorkbookContents(this.resultsData, this.traitsForComparison,
				this.columnIdArray, this.columnMessageMap);
	}
	
	@Test
	public void testExportHeadToHeadDataListExcel() throws HeadToHeadDataListExportException {
		final String excelFilePath = this.h2hDataListExport.exportHeadToHeadDataListExcel(ResultsComponent.HEAD_TO_HEAD_DATA_LIST, this.resultsData, this.traitsForComparison,
				this.columnIdArray, this.columnMessageMap);
		
		final File excelFile = new File(excelFilePath);
		Assert.assertTrue(excelFile.exists());
		final String outputDirectory = this.installationDirectoryUtil.getOutputDirectoryForProjectAndTool(this.contextUtil.getProjectInContext(), ToolName.MAIN_HEAD_TO_HEAD_BROWSER);
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

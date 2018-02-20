package org.generationcp.ibpworkbench.ui.breedingview.metaanalysis;

import java.io.File;

import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class MetaAnalysisSelectTraitsPanelTest {
	
	@Mock
	private WorkbenchDataManager workbenchDataManager;
	
	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;
	
	@Mock
	private Tool bvTool;
	
	@Mock
	private Project project;
	
	@InjectMocks
	private MetaAnalysisSelectTraitsPanel metaAnalysisTraitsPanel;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.metaAnalysisTraitsPanel = new MetaAnalysisSelectTraitsPanel(this.project, null, null);
		this.metaAnalysisTraitsPanel.setWorkbenchDataManager(this.workbenchDataManager);
		this.metaAnalysisTraitsPanel.setInstallationDirectoryUtil(this.installationDirectoryUtil);
		Mockito.doReturn(this.bvTool).when(this.workbenchDataManager).getToolWithName(Matchers.anyString());
		Mockito.doReturn("").when(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(Matchers.any(Project.class),
				Matchers.any(Tool.class));
	}
	
	@Test
	public void testGetMergedDatasetsExcelFile() {
		final File file = this.metaAnalysisTraitsPanel.getMergedDatasetsExcelFile();
		
		Mockito.verify(this.workbenchDataManager).getToolWithName(ToolName.breeding_view.toString());
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.project, this.bvTool);
		Assert.assertTrue(file.getAbsolutePath().contains("mergedDataSets.xls"));
	}

}

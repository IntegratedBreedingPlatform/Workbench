package org.generationcp.ibpworkbench.ui.window;

import java.io.File;
import java.util.Random;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.junit.Assert;

import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomFileFactory;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;

public class FileUploadBreedingViewOutputWindowTest {
	
	@Mock
	private ContextUtil contextUtil;
	
	@InjectMocks
	private FileUploadBreedingViewOutputWindow fileUploadWindow = new FileUploadBreedingViewOutputWindow(null, 1, null, null);

	private Project project = ProjectTestDataInitializer.createProject();
	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();
	}
	
	@Test
	public void testCustomFileFactory(){
		final CustomFileFactory fileFactory = this.fileUploadWindow.new CustomFileFactory();
		final String fileName = "test" + new Random().nextInt() + ".zip";
		final File createdFile = fileFactory.createFile(fileName, null);
		
		// Verify that file is in expected sub-directory in workspace directory
		final String inputDirectoryPath = this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(this.project, ToolName.BV_SSA);
		final File inputDirectoryFile = new File(inputDirectoryPath);
		Assert.assertTrue(inputDirectoryFile.exists());
		Assert.assertEquals(inputDirectoryPath, createdFile.getParent());
		Assert.assertEquals(fileName, createdFile.getName());
		
		this.deleteTestInstallationDirectory();
	}
	
	private void deleteTestInstallationDirectory() {
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR);
		this.installationDirectoryUtil.recursiveFileDelete(testInstallationDirectory);
	}
}

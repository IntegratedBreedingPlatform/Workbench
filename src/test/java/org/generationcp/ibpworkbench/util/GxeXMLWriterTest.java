package org.generationcp.ibpworkbench.util;

import org.generationcp.commons.breedingview.xml.SSAParameters;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

public class GxeXMLWriterTest {
	
	private static final String BMS_OUTPUT_FILES_DIR = "/someDirectory/output";
	private static final String XML_FILEPATH = BMS_OUTPUT_FILES_DIR + File.separator + "test.csv";
	
	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;
	
	@Mock
	private GxeInput gxeInput;
	
	private GxeXMLWriter gxeXmlWriter;
	
	private Project project;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		this.gxeXmlWriter = new GxeXMLWriter(this.gxeInput);
		this.gxeXmlWriter.setInstallationDirectoryUtil(this.installationDirectoryUtil);
		this.project = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.project).when(this.gxeInput).getProject();
		Mockito.doReturn(XML_FILEPATH).when(this.installationDirectoryUtil).getOutputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);
	}
	
	@Test
	public void testSetOutputDirectoryServerAppIsTrue() {
		 final SSAParameters ssaParameters = new SSAParameters();
		 Assert.assertNull(ssaParameters.getOutputDirectory());
		 Mockito.verifyZeroInteractions(this.installationDirectoryUtil);
	}

}

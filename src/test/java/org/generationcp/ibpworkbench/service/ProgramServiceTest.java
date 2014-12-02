package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;

import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


public class ProgramServiceTest {
	
	@Test
	@Ignore
	public void testAddNewProgram() throws Exception {
		
		ProgramService programService = new ProgramService();
		
		WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		ToolUtil toolUtil = Mockito.mock(ToolUtil.class);
		programService.setWorkbenchDataManager(workbenchDataManager);
		programService.setToolUtil(toolUtil);
		
		Project project = new Project();
		project.setProjectName("TestRiceProject");
		CropType cropType = new CropType(CropType.RICE);
		cropType.setCentralDbName("ibdbv2_rice_central");
		project.setCropType(cropType);
		
		Mockito.when(workbenchDataManager.getCropTypeByName(Mockito.anyString())).thenReturn(cropType);
		ArrayList<WorkflowTemplate> workflowTemplates = new ArrayList<WorkflowTemplate>();
		workflowTemplates.add(new WorkflowTemplate());		
		Mockito.when(workbenchDataManager.getWorkflowTemplates()).thenReturn(workflowTemplates);
		
		User user = new User();
		user.setUserid(1);
		user.setName("mrbreeder");
		
		programService.setCurrentUser(user);
		
		programService.createNewProgram(project);
	}

}

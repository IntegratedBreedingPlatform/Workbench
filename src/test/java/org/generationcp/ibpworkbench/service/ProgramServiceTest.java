package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.ibpworkbench.database.IBDBGeneratorCentralDb;
import org.generationcp.ibpworkbench.database.IBDBGeneratorLocalDb;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.junit.Test;
import org.mockito.Mockito;


public class ProgramServiceTest {
	
	@Test
	public void testAddNewProgram() throws Exception {
		
		ProgramService programService = new ProgramService();
		
		Project project = new Project();
		project.setProjectName("TestRiceProject");
		CropType cropType = new CropType(CropType.RICE);
		cropType.setCentralDbName("ibdbv2_rice_central");
		project.setCropType(cropType);
		
		User loggedInUser = new User();
		loggedInUser.setUserid(1);
		loggedInUser.setName("mrbreeder");
		
		User memberUser = new User();
		memberUser.setUserid(1);
		memberUser.setName("mrbreederfriend");
		
		Set<User> programMembers = new HashSet<User>();
		programMembers.add(memberUser);
		
		//WorkbenchDataManager mocks
		WorkbenchDataManager workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
		Mockito.when(workbenchDataManager.getCropTypeByName(Mockito.anyString())).thenReturn(cropType);
		ArrayList<WorkflowTemplate> workflowTemplates = new ArrayList<WorkflowTemplate>();
		workflowTemplates.add(new WorkflowTemplate());		
		Mockito.when(workbenchDataManager.getWorkflowTemplates()).thenReturn(workflowTemplates);
		
		ToolUtil toolUtil = Mockito.mock(ToolUtil.class);
		IBDBGeneratorCentralDb centralDBGenerator = Mockito.mock(IBDBGeneratorCentralDb.class);
		IBDBGeneratorLocalDb localDBGenerator = Mockito.mock(IBDBGeneratorLocalDb.class);
		
		programService.setWorkbenchDataManager(workbenchDataManager);
		programService.setToolUtil(toolUtil);
		programService.setCentralDbGenerator(centralDBGenerator);
		programService.setLocalDbGenerator(localDBGenerator);
		programService.setCurrentUser(loggedInUser);
		
		programService.setSelectedUsers(programMembers);
		
		programService.createNewProgram(project);
		
		Mockito.verify(workbenchDataManager).addProject(project);
		Mockito.verify(workbenchDataManager).saveOrUpdateProject(project);
		Mockito.verify(workbenchDataManager).getWorkflowTemplates();
		
		Mockito.verify(toolUtil).createWorkspaceDirectoriesForProject(project);
		Mockito.verify(centralDBGenerator).generateDatabase();
		Mockito.verify(localDBGenerator).generateDatabase();
	}

}

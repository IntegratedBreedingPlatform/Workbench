package org.generationcp.ibpworkbench.ui.project.create;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;

import com.vaadin.ui.Button;

public class CreateProjectPanelTest {
	
	@Mock
	private AddProgramPresenter presenter;
	
	@Mock
	private PlatformTransactionManager transactionManager;
	
	@Mock
	private ContextUtil contextUtil;
	
	private Button saveButton = new Button();
	private Button cancelButton = new Button();
	
	@InjectMocks
	private CreateProjectPanel createProjectPanel;
	
	private Project project;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.project = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();
		
		this.createProjectPanel.setTransactionManager(this.transactionManager);
		this.createProjectPanel.setCancelButton(this.cancelButton);
		this.createProjectPanel.setSaveProjectButton(this.saveButton);
		this.createProjectPanel.initializeActions();
	}
	
	@Test
	public void testSaveButtonClick() {
		try {
			this.saveButton.click();
		} catch (final Exception e) {
			// Expecting NPE because click event has no component with window. 
			// Window is needed for notifying successful program saving
		}
		Mockito.verify(this.presenter).doAddNewProgram();
		Mockito.verify(this.presenter).enableProgramMethodsAndLocationsTab();
	}
	
	@Test
	public void testCancelButtonClick() {
		this.cancelButton .click();
		Mockito.verify(this.presenter).resetBasicDetails();
		Mockito.verify(this.presenter).disableProgramMethodsAndLocationsTab();
	}

}

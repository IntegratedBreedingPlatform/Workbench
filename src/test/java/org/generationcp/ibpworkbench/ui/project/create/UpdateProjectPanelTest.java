package org.generationcp.ibpworkbench.ui.project.create;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.actions.DeleteProjectAction;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;

import org.junit.Assert;

public class UpdateProjectPanelTest {
	
	private static final String PROGRAM_NAME = "ABCDE FGHIJK LMNOP";

	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private ProjectBasicDetailsComponent projectBasicDetailsComponent;
	
	@Mock
	private Button saveButton;
	
	@Mock
	private Button cancelButton;
	
	@Mock
	private Button deleteProgramButton;
	
	@InjectMocks
	private UpdateProjectPanel updateProjectPanel;
	
	private Project project;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.project = ProjectTestDataInitializer.createProject();
		this.project.setProjectName(PROGRAM_NAME);
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();
		
		this.updateProjectPanel.setCancelButton(this.cancelButton);
		this.updateProjectPanel.setDeleteProgramButton(this.deleteProgramButton);
		this.updateProjectPanel.setSaveProjectButton(this.saveButton);
	}
	
	@Test
	public void testInitializeBasicDetailsComponent() {
		this.updateProjectPanel.setProjectBasicDetailsComponent(this.projectBasicDetailsComponent);
		this.updateProjectPanel.initializeBasicDetailsComponent();
		
		Mockito.verify(this.projectBasicDetailsComponent).updateProjectDetailsFormField(Matchers.eq(this.project));
		Mockito.verify(this.projectBasicDetailsComponent).disableForm();
	}
	
	@Test
	public void testGetOldProjectName() {
		Assert.assertEquals(PROGRAM_NAME, this.updateProjectPanel.getOldProjectName());
	}
	
	@Test
	public void testSaveAndDeleteProjectActionUpdate() {
		this.updateProjectPanel.saveAndDeleteProjectActionUpdate();
		
		final ArgumentCaptor<ClickListener> saveButtonCaptor = ArgumentCaptor.forClass(ClickListener.class);
		Mockito.verify(this.saveButton).addListener(saveButtonCaptor.capture());
		Mockito.verify(this.saveButton).setVisible(true);
		Assert.assertTrue(saveButtonCaptor.getValue() instanceof UpdateProjectAction);
		final ArgumentCaptor<ClickListener> deleteButtonCaptor = ArgumentCaptor.forClass(ClickListener.class);
		Mockito.verify(this.deleteProgramButton).addListener(deleteButtonCaptor.capture());
		Assert.assertTrue(deleteButtonCaptor.getValue() instanceof DeleteProjectAction);
		Mockito.verify(this.cancelButton).addListener(Mockito.any(ClickListener.class));
		Mockito.verify(this.cancelButton).setVisible(true);
	}
	
	@Test
	public void testCancelButtonClick() {
		final Button cancelButton = new Button();
		this.updateProjectPanel.setCancelButton(cancelButton);
		this.updateProjectPanel.saveAndDeleteProjectActionUpdate();
		// Method to test
		cancelButton.click();
		Mockito.verify(this.projectBasicDetailsComponent).updateProjectDetailsFormField(Matchers.eq(this.project));
	}

}

package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import org.junit.Assert;

public class AddProgramViewTest {
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private Window window;

	private AddProgramView addProgramView = new AddProgramView();
	
	private Project selectedProgram;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		this.addProgramView.setContextUtil(this.contextUtil);
		
		this.addProgramView.initializeComponents();
		this.addProgramView.setIsSingleUserOnly("false");
		this.selectedProgram = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.selectedProgram).when(this.contextUtil).getProjectInContext();
	}

	@Test
	public void testAddProgramMembersTabSingleUser() {

		final TabSheet tabSheet = new TabSheet();
		final VerticalLayout programMembersContainer = new VerticalLayout();
		programMembersContainer.setVisible(false);

		this.addProgramView.setIsSingleUserOnly("true");

		this.addProgramView.addProgramMembersTab(tabSheet, programMembersContainer);

		// Verify that programMembersContainer is not added in tabSheet
		Assert.assertEquals(0, tabSheet.getComponentCount());
		Assert.assertFalse(programMembersContainer.isVisible());

	}

	@Test
	public void testAddProgramMembersTabNotSingleUser() {

		final TabSheet tabSheet = new TabSheet();
		final VerticalLayout programMembersContainer = new VerticalLayout();
		programMembersContainer.setVisible(false);

		this.addProgramView.addProgramMembersTab(tabSheet, programMembersContainer);

		// Verify that programMembersContainer is added in tabSheet
		Assert.assertEquals(1, tabSheet.getComponentCount());
		Assert.assertFalse(tabSheet.getTab(programMembersContainer).isClosable());
		Assert.assertEquals("Members", tabSheet.getTab(programMembersContainer).getCaption());
		Assert.assertTrue(programMembersContainer.isVisible());

	}

	@Test
	public void testInitializeLayoutNotSingleUser() {

		this.addProgramView.initializeLayout();

		// Verify that the following tabs are added
		final TabSheet.Tab basicDetails = this.addProgramView.getTabSheet().getTab(this.addProgramView.getBasicDetailsContainer());
		Assert.assertNotNull(basicDetails);
		Assert.assertFalse(basicDetails.isClosable());
		Assert.assertTrue(this.addProgramView.getBasicDetailsContainer().getComponent(0) instanceof CreateProjectPanel);

		final TabSheet.Tab programMembers = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMembersContainer());
		Assert.assertNotNull(programMembers);
		Assert.assertFalse(programMembers.isClosable());
		Assert.assertTrue(this.addProgramView.getProgramMembersContainer().getComponent(0) instanceof ProjectMembersComponent);
		

		final TabSheet.Tab programLocations = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramLocationsContainer());
		Assert.assertNotNull(programLocations);
		Assert.assertFalse(programLocations.isClosable());
		Assert.assertFalse(programLocations.isEnabled());

		final TabSheet.Tab programMethods = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMethodsContainer());
		Assert.assertNotNull(programMethods);
		Assert.assertFalse(programMethods.isClosable());
		Assert.assertFalse(programMethods.isEnabled());

	}

	@Test
	public void testInitializeLayoutSingleUser() {

		this.addProgramView.setIsSingleUserOnly("true");

		this.addProgramView.initializeLayout();

		// Verify that the following tabs are added
		final TabSheet.Tab basicDetails = this.addProgramView.getTabSheet().getTab(this.addProgramView.getBasicDetailsContainer());
		Assert.assertNotNull(basicDetails);
		Assert.assertFalse(basicDetails.isClosable());

		final TabSheet.Tab programLocations = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramLocationsContainer());
		Assert.assertNotNull(programLocations);
		Assert.assertFalse(programLocations.isClosable());
		Assert.assertFalse(programLocations.isEnabled());

		final TabSheet.Tab programMethods = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMethodsContainer());
		Assert.assertNotNull(programMethods);
		Assert.assertFalse(programMethods.isClosable());
		Assert.assertFalse(programMethods.isEnabled());

		// Verify that the Program Members tab is not added
		Assert.assertNull(this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMembersContainer()));

	}
	
	@Test
	public void testInitializeActions() {
		this.addProgramView.initializeActions();
		
		// Verify Cancel button click listener
		final Button cancelButton = this.addProgramView.getCancelButton();
		Assert.assertNotNull(cancelButton.getListeners(ClickListener.class));

		// Verify Finish button click listener
		final Button finishButton = this.addProgramView.getFinishButton();
		Assert.assertNotNull(finishButton.getListeners(Button.ClickListener.class));
	}
	
	@Test
	public void testDisableOptionalTabsAndFinish() {
		this.addProgramView.initializeLayout();
		this.addProgramView.disableOptionalTabsAndFinish();
		
		final TabSheet.Tab programLocations = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramLocationsContainer());
		Assert.assertNotNull(programLocations);
		Assert.assertFalse(programLocations.isEnabled());
		
		final TabSheet.Tab programMethods = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMethodsContainer());
		Assert.assertNotNull(programMethods);
		Assert.assertFalse(programMethods.isEnabled());
		Assert.assertFalse(this.addProgramView.getFinishButton().isEnabled());
	}
	
	@Test
	public void testEnableFinishBtn() {
		this.addProgramView.enableFinishBtn();
		Assert.assertTrue(this.addProgramView.getFinishButton().isEnabled());
	}
	
	@Test
	public void testUpdateUIOnProgramSave() {
		final VerticalLayout basicDetailsContainer = Mockito.mock(VerticalLayout.class);
		this.addProgramView.setBasicDetailsContainer(basicDetailsContainer);
		final VerticalLayout membersComponentContainer = Mockito.mock(VerticalLayout.class);
		this.addProgramView.setProgramMembersContainer(membersComponentContainer);
		
		this.addProgramView.initializeLayout();
		this.addProgramView.updateUIOnProgramSave(this.selectedProgram, this.window);
		
		final TabSheet.Tab programLocations = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramLocationsContainer());
		Assert.assertNotNull(programLocations);
		Assert.assertTrue(programLocations.isEnabled());
		final TabSheet.Tab programMethods = this.addProgramView.getTabSheet().getTab(this.addProgramView.getProgramMethodsContainer());
		Assert.assertNotNull(programMethods);
		Assert.assertTrue(programMethods.isEnabled());

		Mockito.verify(basicDetailsContainer).removeAllComponents();
		final ArgumentCaptor<Component> componentCaptor = ArgumentCaptor.forClass(Component.class);
		Mockito.verify(basicDetailsContainer, Mockito.times(2)).addComponent(componentCaptor.capture());
		Assert.assertTrue(componentCaptor.getAllValues().get(1) instanceof UpdateProjectPanel);
		
		Mockito.verify(membersComponentContainer).removeAllComponents();
		final ArgumentCaptor<Component> membersComponentCaptor = ArgumentCaptor.forClass(Component.class);
		Mockito.verify(membersComponentContainer, Mockito.times(2)).addComponent(membersComponentCaptor.capture());
		Assert.assertTrue(membersComponentCaptor.getAllValues().get(1) instanceof ProgramMembersPanel);
		Assert.assertEquals(this.selectedProgram, ((ProgramMembersPanel)membersComponentCaptor.getValue()).getProject() );
		
		Assert.assertTrue(this.addProgramView.getFinishButton().isEnabled());
		Assert.assertFalse(this.addProgramView.getCancelButton().isEnabled());
	}
	

}

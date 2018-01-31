package org.generationcp.ibpworkbench.ui.project.create;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;

public class AddProgramPresenterTest {
	
	@Mock
	private AddProgramView view;

	@Mock
	private ProgramService programService;
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private Window window;
	
	@InjectMocks
	private AddProgramPresenter presenter;
	
	private Project selectedProgram;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.selectedProgram = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.selectedProgram).when(this.contextUtil).getProjectInContext();
		
		this.presenter = new AddProgramPresenter(this.view);
		this.presenter.setContextUtil(this.contextUtil);
		this.presenter.setProgramService(this.programService);
	}
	
	@Test
	public void testEnableProgramMethodsAndLocationsTab() {
		this.presenter.enableProgramMethodsAndLocationsTab(this.window);
		Mockito.verify(this.view).updateUIOnProgramSave(this.selectedProgram, this.window);
	}
	
	@Test
	public void testDisableProgramMethodsAndLocationsTab() {
		this.presenter.disableProgramMethodsAndLocationsTab();
		Mockito.verify(this.view).disableOptionalTabsAndFinish();
	}
	
	@Test
	public void testResetBasicDetails() {
		this.presenter.resetBasicDetails();
		Mockito.verify(this.view).resetBasicDetails();
	}
	
	@Test
	public void testResetProgramMember() {
		this.presenter.resetProgramMembers();
		Mockito.verify(this.view).resetProgramMembers();
	}
}

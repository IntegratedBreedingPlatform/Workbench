package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import junit.framework.Assert;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;

import com.vaadin.ui.Button;

public class CreateProjectPanelTest {

	public static final String SUCCESS = "success";

	@Mock
	private Window window;

	@Mock
	private Component component;
	
	@Mock
	private AddProgramPresenter presenter;
	
	@Mock
	private PlatformTransactionManager transactionManager;
	
	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
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
		this.createProjectPanel.setMessageSource(this.messageSource);
		this.createProjectPanel.initializeActions();

		Mockito.when(messageSource.getMessage(Message.SUCCESS)).thenReturn(SUCCESS);
	}
	
	@Test
	public void testSaveProjectButtonListener() {

		final CreateProjectPanel.SaveProjectButtonListener listener = this.createProjectPanel.new SaveProjectButtonListener();
		final Button.ClickEvent event = Mockito.mock(Button.ClickEvent.class);
		Mockito.when(event.getComponent()).thenReturn(this.component);
		Mockito.when(this.component.getWindow()).thenReturn(this.window);
		Mockito.when(this.presenter.doAddNewProgram()).thenReturn(this.project);

		listener.buttonClick(event);

		Mockito.verify(this.presenter).doAddNewProgram();
		Mockito.verify(this.presenter).enableProgramMethodsAndLocationsTab(this.window);

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		Mockito.verify(window).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();

		Assert.assertEquals(SUCCESS, notification.getCaption());
		Assert.assertEquals("</br>Project program has been successfully created.", notification.getDescription());

	}
	
	@Test
	public void testCancelButtonClick() {
		this.cancelButton .click();
		Mockito.verify(this.presenter).resetBasicDetails();
		Mockito.verify(this.presenter).disableProgramMethodsAndLocationsTab();
	}

}

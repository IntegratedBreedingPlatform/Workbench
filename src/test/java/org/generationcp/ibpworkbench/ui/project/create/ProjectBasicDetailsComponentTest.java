package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Window;
import org.generationcp.MessageResourceUtil;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;

public class ProjectBasicDetailsComponentTest {

	private CreateProjectPanel panel;
	private WorkbenchDataManager workbenchDataManager;
	private static final SimpleResourceBundleMessageSource messageSource = MessageResourceUtil.getMessageResource();

	public static final String PROJECT_NAME = "TestProjectName";
	public static final String CROP_NAME = "TestCropName";

	@Before
	public void setUp() {
		this.panel = Mockito.mock(CreateProjectPanel.class);
		this.workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
	}

	@Test
	public void testValidateWithDuplicateNames() throws Exception {
		ProjectBasicDetailsComponent component = Mockito.spy(new ProjectBasicDetailsComponent(this.panel));
		Window window = Mockito.spy(new Window());

		Project project = new Project();
		project.setProjectName(PROJECT_NAME);
		project.setCropType(new CropType(CROP_NAME));
		project.setStartDate(new Date());
		Mockito.when(workbenchDataManager.getProjectByName(PROJECT_NAME)).thenReturn(project);
		Mockito.when(component.getWindow()).thenReturn(window);
		component.setWorkbenchDataManager(workbenchDataManager);
		component.setMessageSource(messageSource);
		component.afterPropertiesSet();

		component.updateProjectDetailsFormField(project);
		Assert.assertFalse(component.validate());
		Assert.assertTrue(component.getErrorDescription().toString().contains(messageSource.getMessage(Message.DUPLICATE_PROGRAM_NAME_ERROR)));
	}

	@Test
	public void testValidateNoProjectWithThatName() throws Exception {
		ProjectBasicDetailsComponent component = Mockito.spy(new ProjectBasicDetailsComponent(this.panel));
		Window window = Mockito.spy(new Window());

		Project project = new Project();
		project.setProjectName(PROJECT_NAME);
		project.setCropType(new CropType(CROP_NAME));
		project.setStartDate(new Date());
		Mockito.when(workbenchDataManager.getProjectByName(PROJECT_NAME)).thenReturn(null);
		Mockito.when(component.getWindow()).thenReturn(window);
		component.setWorkbenchDataManager(workbenchDataManager);
		component.setMessageSource(messageSource);
		component.afterPropertiesSet();

		component.updateProjectDetailsFormField(project);
		component.validate();
		Assert.assertFalse(
				component.getErrorDescription().toString().contains(messageSource.getMessage(Message.DUPLICATE_PROGRAM_NAME_ERROR)));
	}

	@Test(expected=InternationalizableException.class)
	public void testValidateDatabaseException() throws Exception {
		ProjectBasicDetailsComponent component = Mockito.spy(new ProjectBasicDetailsComponent(this.panel));
		Window window = Mockito.spy(new Window());

		Project project = new Project();
		project.setProjectName("ExceptionGettingProjectName");
		project.setCropType(new CropType(CROP_NAME));
		project.setStartDate(new Date());
		Mockito.when(workbenchDataManager.getProjectByName("ExceptionGettingProjectName")).thenThrow(
				new MiddlewareQueryException("DB error"));
		Mockito.when(component.getWindow()).thenReturn(window);
		component.setWorkbenchDataManager(workbenchDataManager);
		component.setMessageSource(messageSource);
		component.afterPropertiesSet();

		component.updateProjectDetailsFormField(project);
		component.validate();
	}

}

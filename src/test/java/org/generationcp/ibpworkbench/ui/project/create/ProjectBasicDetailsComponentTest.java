package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Window;
import org.generationcp.MessageResourceUtil;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;

public class ProjectBasicDetailsComponentTest {

	private static final String EXCEPTION_GETTING_PROJECT_NAME = "ExceptionGettingProjectName";
	private CreateProjectPanel panel;
	private WorkbenchDataManager workbenchDataManager;
	private static final SimpleResourceBundleMessageSource messageSource = MessageResourceUtil.getMessageResource();

	private static final String PROJECT_NAME = "TestProjectName";
	private static final String CROP_NAME = "TestCropName";

	@Before
	public void setUp() {
		this.panel = Mockito.mock(CreateProjectPanel.class);
		this.workbenchDataManager = Mockito.mock(WorkbenchDataManager.class);
	}

	@Test
	public void testValidateWithDuplicateNames() throws Exception {
		final ProjectBasicDetailsComponent component = Mockito.spy(new ProjectBasicDetailsComponent(this.panel));
		final Window window = Mockito.spy(new Window());

		final Project project = new Project();
		project.setProjectName(PROJECT_NAME);
		final CropType cropType = new CropType(CROP_NAME);
		project.setCropType(cropType);
		project.setStartDate(new Date());
		Mockito.when(this.workbenchDataManager.getProjectByNameAndCrop(PROJECT_NAME, cropType)).thenReturn(project);
		final ArrayList<CropType> cropTypes = new ArrayList<>();
		cropTypes.add(cropType);
		Mockito.when(this.workbenchDataManager.getInstalledCropDatabses()).thenReturn(cropTypes);
		Mockito.when(component.getWindow()).thenReturn(window);
		component.setWorkbenchDataManager(this.workbenchDataManager);
		component.setMessageSource(messageSource);
		component.initializeComponents();
		component.afterPropertiesSet();

		component.updateProjectDetailsFormField(project);
		Assert.assertFalse(component.validate());
		Assert.assertTrue(component.getErrorDescription().toString().contains(messageSource.getMessage(Message.DUPLICATE_PROGRAM_NAME_ERROR)));
	}

	@Test
	public void testValidateNoProjectWithThatName() throws Exception {
		final ProjectBasicDetailsComponent component = Mockito.spy(new ProjectBasicDetailsComponent(this.panel));
		final Window window = Mockito.spy(new Window());

		final Project project = new Project();
		project.setProjectName(PROJECT_NAME);
		final CropType cropType = new CropType(CROP_NAME);
		project.setCropType(cropType);
		project.setStartDate(new Date());
		Mockito.when(this.workbenchDataManager.getProjectByNameAndCrop(PROJECT_NAME, cropType)).thenReturn(null);
		Mockito.when(component.getWindow()).thenReturn(window);
		component.setWorkbenchDataManager(this.workbenchDataManager);
		component.setMessageSource(messageSource);
		component.afterPropertiesSet();

		component.updateProjectDetailsFormField(project);
		component.validate();
		Assert.assertFalse(
				component.getErrorDescription().toString().contains(messageSource.getMessage(Message.DUPLICATE_PROGRAM_NAME_ERROR)));
	}

	@Test(expected=InternationalizableException.class)
	public void testValidateDatabaseException() throws Exception {
		final ProjectBasicDetailsComponent component = Mockito.spy(new ProjectBasicDetailsComponent(this.panel));
		final Window window = Mockito.spy(new Window());

		final Project project = new Project();
		project.setProjectName(EXCEPTION_GETTING_PROJECT_NAME);
		final CropType cropType = new CropType(CROP_NAME);
		project.setCropType(cropType);
		project.setStartDate(new Date());
		Mockito.when(this.workbenchDataManager.getProjectByNameAndCrop(EXCEPTION_GETTING_PROJECT_NAME, cropType)).thenThrow(
				new MiddlewareQueryException("DB error"));
		Mockito.when(component.getWindow()).thenReturn(window);
		final ArrayList<CropType> cropTypes = new ArrayList<>();
		cropTypes.add(cropType);
		Mockito.when(this.workbenchDataManager.getInstalledCropDatabses()).thenReturn(cropTypes);
		component.setWorkbenchDataManager(this.workbenchDataManager);
		component.setMessageSource(messageSource);
		component.initializeComponents();
		component.afterPropertiesSet();

		component.updateProjectDetailsFormField(project);
		component.validate();
	}

}

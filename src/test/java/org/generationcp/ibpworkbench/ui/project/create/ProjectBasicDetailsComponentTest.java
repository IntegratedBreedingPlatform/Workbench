package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.fields.BmsDateField;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

public class ProjectBasicDetailsComponentTest {

	private static final String DUPLICATE_NAME_ERROR = "Program with given name already exists";
	private static final String NO_PROGRAM_NAME_ERROR = "Program name cannot be empty.";
	private static final String INVALID_PROGRAM_NAME = "Program name contains invalid characters.";
	
	@Mock
	private CreateProjectPanel panel;
	
	@Mock
	private WorkbenchDataManager workbenchDataManager;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource; 
	
	@Mock
	private Window window;
	
	@Mock
	private CreateProjectPanel createProjectPanel;

	@InjectMocks
	private ProjectBasicDetailsComponent basicDetailsComponent;
	
	private List<CropType> cropTypes;
	private Project testProject;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.testProject = ProjectTestDataInitializer.createProject();
		this.cropTypes = Arrays.asList(new CropType("maize"), new CropType("rice"), new CropType("wheat"));
		Mockito.when(this.workbenchDataManager.getInstalledCropDatabses()).thenReturn(this.cropTypes);
		Mockito.when(this.messageSource.getMessage(Message.DUPLICATE_PROGRAM_NAME_ERROR)).thenReturn(DUPLICATE_NAME_ERROR);
		Mockito.when(this.messageSource.getMessage("NO_PROGRAM_NAME_ERROR")).thenReturn(NO_PROGRAM_NAME_ERROR);
		Mockito.when(this.messageSource.getMessage("PROGRAM_NAME_INVALID_ERROR")).thenReturn(INVALID_PROGRAM_NAME);
		this.basicDetailsComponent = new ProjectBasicDetailsComponent(this.createProjectPanel);
		this.basicDetailsComponent.setIsUpdate(false);
		this.basicDetailsComponent.setMessageSource(this.messageSource);
		this.basicDetailsComponent.setWorkbenchDataManager(this.workbenchDataManager);
		this.basicDetailsComponent.initializeComponents();
	}
	
	@Test
	public void testInstantiatedComponents() {
		final TextField projectNameField = this.basicDetailsComponent.getProjectNameField();
		Assert.assertTrue(projectNameField.isImmediate());
		Assert.assertTrue(projectNameField.isRequired());
		final Collection<Validator> validators = projectNameField.getValidators();
		Assert.assertEquals(2, validators.size());
		final StringLengthValidator projectNameLengthValidator = (StringLengthValidator) validators.iterator().next();
		Assert.assertEquals(3, projectNameLengthValidator.getMinLength());
		Assert.assertEquals(65, projectNameLengthValidator.getMaxLength());
		
		final BmsDateField dateField = this.basicDetailsComponent.getStartDateField();
		Assert.assertTrue(dateField.isRequired());
		
		final ComboBox cropTypeCombobox = this.basicDetailsComponent.getCropTypeCombo();
		Assert.assertEquals(this.cropTypes.size(), cropTypeCombobox.size());
		Assert.assertFalse(cropTypeCombobox.isNewItemsAllowed());
		Assert.assertTrue(cropTypeCombobox.isRequired());
		Assert.assertFalse(cropTypeCombobox.isInvalidAllowed());
		Assert.assertFalse(cropTypeCombobox.isNullSelectionAllowed());
		Assert.assertTrue(cropTypeCombobox.isImmediate());
	}
	

	@Test
	public void testValidateWithDuplicateNames() throws Exception {
		Mockito.when(this.workbenchDataManager.getProjectByNameAndCrop(this.testProject.getProjectName(), this.testProject.getCropType()))
				.thenReturn(this.testProject);

		// Need to spy for mocking the component's window
		final ProjectBasicDetailsComponent mockComponent = Mockito.spy(this.basicDetailsComponent);
		Mockito.doReturn(this.window).when(mockComponent).getWindow();
		mockComponent.updateProjectDetailsFormField(this.testProject);
		Assert.assertFalse(mockComponent.validate());
		Assert.assertTrue(mockComponent.getErrorDescription().toString().contains(DUPLICATE_NAME_ERROR));
		Mockito.verify(this.window).showNotification(Matchers.any(Notification.class));
	}

	@Test
	public void testValidateNoProjectWithThatName() throws Exception {
		this.basicDetailsComponent.updateProjectDetailsFormField(this.testProject);
		Assert.assertTrue(this.basicDetailsComponent.validate());
		Assert.assertTrue(this.basicDetailsComponent.getErrorDescription().toString().isEmpty());
	}
	
	@Test
	public void testValidateEmptyProjectName() {
		this.testProject.setProjectName("");
		
		// Need to spy for mocking the component's window
		final ProjectBasicDetailsComponent mockComponent = Mockito.spy(this.basicDetailsComponent);
		Mockito.doReturn(this.window).when(mockComponent).getWindow();
		mockComponent.updateProjectDetailsFormField(this.testProject);
		Assert.assertFalse(mockComponent.validate());
		Assert.assertTrue(mockComponent.getErrorDescription().toString().contains(NO_PROGRAM_NAME_ERROR));
		Mockito.verify(this.window).showNotification(Matchers.any(Notification.class));
	}
	
	@Test
	public void testValidateProjectNameWithInvalidCharacter() throws Exception {
		this.testProject.setProjectName("Maize ?Program");
		
		// Need to spy for mocking the component's window
		final ProjectBasicDetailsComponent mockComponent1 = Mockito.spy(this.basicDetailsComponent);
		Mockito.doReturn(this.window).when(mockComponent1).getWindow();
		mockComponent1.updateProjectDetailsFormField(this.testProject);
		Assert.assertFalse(mockComponent1.validate());
		Assert.assertTrue(mockComponent1.getErrorDescription().toString().contains(INVALID_PROGRAM_NAME));
		
		this.testProject.setProjectName("Maize :Program*");
		final ProjectBasicDetailsComponent mockComponent2 = Mockito.spy(this.basicDetailsComponent);
		Mockito.doReturn(this.window).when(mockComponent2).getWindow();
		mockComponent2.updateProjectDetailsFormField(this.testProject);
		Assert.assertFalse(mockComponent2.validate());
		Assert.assertTrue(mockComponent2.getErrorDescription().toString().contains(INVALID_PROGRAM_NAME));
		
		this.testProject.setProjectName("Maize <Program>");
		final ProjectBasicDetailsComponent mockComponent3 = Mockito.spy(this.basicDetailsComponent);
		Mockito.doReturn(this.window).when(mockComponent3).getWindow();
		mockComponent3.updateProjectDetailsFormField(this.testProject);
		Assert.assertFalse(mockComponent3.validate());
		Assert.assertTrue(mockComponent3.getErrorDescription().toString().contains(INVALID_PROGRAM_NAME));
		
		this.testProject.setProjectName("Maize \"Program|");
		final ProjectBasicDetailsComponent mockComponent4 = Mockito.spy(this.basicDetailsComponent);
		Mockito.doReturn(this.window).when(mockComponent4).getWindow();
		mockComponent4.updateProjectDetailsFormField(this.testProject);
		Assert.assertFalse(mockComponent4.validate());
		Assert.assertTrue(mockComponent4.getErrorDescription().toString().contains(INVALID_PROGRAM_NAME));
	}
	

	@Test(expected=InternationalizableException.class)
	public void testValidateDatabaseExceptionInRetrievingProjectByName() throws Exception {
		Mockito.when(this.workbenchDataManager.getProjectByNameAndCrop(this.testProject.getProjectName(), this.testProject.getCropType()))
				.thenThrow(new MiddlewareQueryException("DB error"));

		this.basicDetailsComponent.updateProjectDetailsFormField(this.testProject);
		this.basicDetailsComponent.validate();
	}
	
	@Test
	public void testCropTypeComboboxValueSet() {
		this.basicDetailsComponent.updateProjectDetailsFormField(this.testProject);
		Assert.assertEquals(this.testProject.getCropType(), this.basicDetailsComponent.getOldCropType());
	}
	
	@Test
	public void testCropTypeChanged() {
		// Say that another crop was chosen before
		this.basicDetailsComponent.setOldCropType(this.cropTypes.get(0));
		
		// Change selected crop type
		final CropType newCropType = this.cropTypes.get(1);
		this.testProject.setCropType(newCropType);
		this.basicDetailsComponent.updateProjectDetailsFormField(this.testProject);
		Mockito.verify(this.createProjectPanel).cropTypeChanged(newCropType);
	}

}

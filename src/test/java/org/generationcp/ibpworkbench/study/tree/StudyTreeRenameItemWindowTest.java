package org.generationcp.ibpworkbench.study.tree;

import java.util.Collection;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.study.StudyTabSheet;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import junit.framework.Assert;

public class StudyTreeRenameItemWindowTest {
	
	private static final String PROGRAM_UUID = "qwerty-12345";

	private static final String CURRENT_FOLDER_NAME = "Current Folder Name";

	private static final Integer SELECTED_ID = 1001;
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock 
	private StudyTree studyTree;
	
	@Mock
	private StudyTabSheet studyTabSheet;
	
	@Mock
	private Window window;
	
	@Mock
	private StudyFolderNameValidator validator;
	
	private StudyTreeRenameItemWindow renameWindow;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.renameWindow = new StudyTreeRenameItemWindow(SELECTED_ID, CURRENT_FOLDER_NAME, this.studyTree, this.studyTabSheet);
		this.renameWindow.setStudyDataManager(studyDataManager);
		this.renameWindow.setContextUtil(contextUtil);
		this.renameWindow.setMessageSource(messageSource);
		this.renameWindow.setValidator(validator);
		
		Project project = ProjectTestDataInitializer.createProject();
		project.setUniqueID(PROGRAM_UUID);
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
	}
	
	@Test
	public void testAfterPropertiesSet() throws Exception {
		this.renameWindow.afterPropertiesSet();
		
		final TextField nameTextField = this.renameWindow.getNameTextField();
		Assert.assertEquals(50, nameTextField.getMaxLength());
		Assert.assertEquals(CURRENT_FOLDER_NAME, nameTextField.getValue());
		
		final Button okButton = this.renameWindow.getOkButton();
		Assert.assertNotNull(okButton);
		Collection<?> clickListeners = okButton.getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
		
		final Button cancelButton = this.renameWindow.getCancelButton();
		Assert.assertNotNull(cancelButton);
		clickListeners = cancelButton.getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
	}
	
	@Test
	public void testClickCancelButton() throws Exception {
		final StudyTreeRenameItemWindow spyComponent = Mockito.spy(this.renameWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		spyComponent.getCancelButton().click();
		Mockito.verify(spyComponent).closePopup();
	}
	
	@Test
	public void testClickOKButtonWhenCurrentNameEqualsNewName() throws Exception {
		final StudyTreeRenameItemWindow spyComponent = Mockito.spy(this.renameWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		
		spyComponent.getOkButton().click();
		Mockito.verifyZeroInteractions(this.studyTree);
		Mockito.verifyZeroInteractions(this.studyTabSheet);
		Mockito.verify(this.studyDataManager, Mockito.never()).renameSubFolder(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString());
		Mockito.verify(spyComponent).closePopup();
	}
	
	@Test
	public void testClickOKButtonWhenItemIsFolder() throws Exception {
		final StudyTreeRenameItemWindow spyComponent = Mockito.spy(this.renameWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		Mockito.doReturn(true).when(this.studyTree).isFolder(SELECTED_ID);
		Mockito.doReturn(true).when(this.validator).isValidNameInput(Matchers.anyString(), Matchers.any(Window.class));
		
		final String newName = "New Folder Name ZZZ";
		spyComponent.getNameTextField().setValue(newName);
		spyComponent.getOkButton().click();
		Mockito.verify(this.studyTree).setItemCaption(SELECTED_ID, newName);
		Mockito.verify(this.studyTree).select(SELECTED_ID);
		Mockito.verify(this.studyDataManager).renameSubFolder(newName, SELECTED_ID, PROGRAM_UUID);
		Mockito.verifyZeroInteractions(this.studyTabSheet);
		Mockito.verify(spyComponent).closePopup();
	}
	
	@Test
	public void testClickOKButtonWhenItemIsStudy() throws Exception {
		final StudyTreeRenameItemWindow spyComponent = Mockito.spy(this.renameWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		Mockito.doReturn(false).when(this.studyTree).isFolder(SELECTED_ID);
		Mockito.doReturn(true).when(this.validator).isValidNameInput(Matchers.anyString(), Matchers.any(Window.class));
		
		final String newName = "New Folder Name ZZZ";
		spyComponent.getNameTextField().setValue(newName);
		spyComponent.getOkButton().click();
		Mockito.verify(this.studyTree).setItemCaption(SELECTED_ID, newName);
		Mockito.verify(this.studyTree).select(SELECTED_ID);
		Mockito.verify(this.studyDataManager).renameSubFolder(newName, SELECTED_ID, PROGRAM_UUID);
		Mockito.verify(this.studyTabSheet).renameStudyTab(CURRENT_FOLDER_NAME, newName);
		Mockito.verify(spyComponent).closePopup();
	}
	
	@Test
	public void testClickOkButtonInvalidNameInput() throws Exception {
		final StudyTreeRenameItemWindow spyComponent = Mockito.spy(this.renameWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		Mockito.doReturn(false).when(this.validator).isValidNameInput(Matchers.anyString(), Matchers.any(Window.class));
		
		final String newName = "New Folder Name ZZZ";
		spyComponent.getNameTextField().setValue(newName);
		spyComponent.getOkButton().click();
		Mockito.verifyZeroInteractions(this.studyTree);
		Mockito.verifyZeroInteractions(this.studyTabSheet);
		Mockito.verify(this.studyDataManager, Mockito.never()).renameSubFolder(Matchers.anyString(), Matchers.anyInt(), Matchers.anyString());
		Mockito.verify(spyComponent, Mockito.never()).closePopup();
	}

}

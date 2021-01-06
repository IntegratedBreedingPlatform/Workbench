package org.generationcp.ibpworkbench.study.tree;

import java.util.Collection;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import org.junit.Assert;

public class StudyTreeAddFolderWindowTest {
	
	private static final String NEW_FOLDER_NAME = "2018 July Trials";

	private static final int NEW_FOLDER_ID = 123;

	private static final String PROGRAM_UUID = "qwerty-12345";

	private static final Integer SELECTED_ID = 1001;
	private static final Integer PARENT_ID = 10;
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock 
	private StudyTree studyTree;
	
	@Mock
	private StudyFolderNameValidator validator;
	
	private DmsProject parent;
	
	private StudyTreeAddFolderWindow addFolderWindow;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.addFolderWindow = new StudyTreeAddFolderWindow(SELECTED_ID, studyTree);
		this.addFolderWindow.setContextUtil(contextUtil);
		this.addFolderWindow.setMessageSource(messageSource);
		this.addFolderWindow.setValidator(validator);
		this.addFolderWindow.setStudyDataManager(studyDataManager);
		
		Project project = ProjectTestDataInitializer.createProject();
		project.setUniqueID(PROGRAM_UUID);
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
		parent = new DmsProject();
		parent.setProjectId(PARENT_ID);
		Mockito.doReturn(parent).when(this.studyDataManager).getParentFolder(Matchers.anyInt());
		Mockito.doReturn(NEW_FOLDER_ID).when(this.studyDataManager).addSubFolder(Matchers.anyInt(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString());
	}
	
	@Test
	public void testAfterPropertiesSet() throws Exception {
		this.addFolderWindow.afterPropertiesSet();
		
		final TextField nameTextField = this.addFolderWindow.getFolderTextField();
		Assert.assertEquals(50, nameTextField.getMaxLength());
		
		final Button okButton = this.addFolderWindow.getOkButton();
		Assert.assertNotNull(okButton);
		Collection<?> clickListeners = okButton.getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
		
		final Button cancelButton = this.addFolderWindow.getCancelButton();
		Assert.assertNotNull(cancelButton);
		clickListeners = cancelButton.getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
	}
	
	@Test
	public void testClickCancelButton() throws Exception {
		final StudyTreeAddFolderWindow spyComponent = Mockito.spy(this.addFolderWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		spyComponent.getCancelButton().click();
		Mockito.verify(spyComponent).closePopup();
	}
	
	@Test
	public void testClickOKButtonWhenInvalidFolderNameInput() throws Exception {
		final StudyTreeAddFolderWindow spyComponent = Mockito.spy(this.addFolderWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		Mockito.doReturn(false).when(this.validator).isValidNameInput(Matchers.anyString(), Matchers.any(Window.class));
		
		spyComponent.getOkButton().click();
		Mockito.verifyZeroInteractions(this.studyTree);
		Mockito.verify(this.studyDataManager, Mockito.never()).addSubFolder(Matchers.anyInt(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString());
		Mockito.verify(spyComponent, Mockito.never()).closePopup();
	}
	
	@Test
	public void testClickOKButtonWhenItemSelectedIsFolder() throws Exception {
		final StudyTreeAddFolderWindow spyComponent = Mockito.spy(this.addFolderWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		spyComponent.getFolderTextField().setValue(NEW_FOLDER_NAME);
		Mockito.doReturn(true).when(this.validator).isValidNameInput(Matchers.anyString(), Matchers.any(Window.class));
		Mockito.doReturn(true).when(this.studyTree).isFolder(Matchers.anyInt());
		
		spyComponent.getOkButton().click();
		Mockito.verify(this.studyDataManager).addSubFolder(SELECTED_ID, NEW_FOLDER_NAME, NEW_FOLDER_NAME,
				PROGRAM_UUID, NEW_FOLDER_NAME);
		Mockito.verify(this.studyTree).addItem(NEW_FOLDER_ID);
		Mockito.verify(this.studyTree).setItemCaption(NEW_FOLDER_ID, NEW_FOLDER_NAME);
		Mockito.verify(this.studyTree).setItemIcon(NEW_FOLDER_ID, StudyTree.FOLDER_ICON);
		Mockito.verify(this.studyTree).setChildrenAllowed(NEW_FOLDER_ID, true);
		Mockito.verify(this.studyTree).setParent(NEW_FOLDER_ID, SELECTED_ID);
		Mockito.verify(this.studyTree).selectItem(NEW_FOLDER_ID);
		Mockito.verify(spyComponent).closePopup();
	}
	
	@Test
	public void testClickOKButtonWhenItemSelectedIsNotAFolder() throws Exception {
		final StudyTreeAddFolderWindow spyComponent = Mockito.spy(this.addFolderWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		spyComponent.getFolderTextField().setValue(NEW_FOLDER_NAME);
		Mockito.doReturn(true).when(this.validator).isValidNameInput(Matchers.anyString(), Matchers.any(Window.class));
		Mockito.doReturn(false).when(this.studyTree).isFolder(Matchers.anyInt());
		
		spyComponent.getOkButton().click();
		Mockito.verify(this.studyDataManager).getParentFolder(SELECTED_ID);
		Mockito.verify(this.studyDataManager).addSubFolder(PARENT_ID, NEW_FOLDER_NAME, NEW_FOLDER_NAME,
				PROGRAM_UUID, NEW_FOLDER_NAME);
		Mockito.verify(this.studyTree).addItem(NEW_FOLDER_ID);
		Mockito.verify(this.studyTree).setItemCaption(NEW_FOLDER_ID, NEW_FOLDER_NAME);
		Mockito.verify(this.studyTree).setItemIcon(NEW_FOLDER_ID, StudyTree.FOLDER_ICON);
		Mockito.verify(this.studyTree).setChildrenAllowed(NEW_FOLDER_ID, true);
		Mockito.verify(this.studyTree).setParent(NEW_FOLDER_ID, PARENT_ID);
		Mockito.verify(this.studyTree).selectItem(NEW_FOLDER_ID);
		Mockito.verify(spyComponent).closePopup();
	}
	
	@Test
	public void testClickOKButtonWhenMiddlewareExceptionIsThrown() throws Exception {
		final StudyTreeAddFolderWindow spyComponent = Mockito.spy(this.addFolderWindow);
		Mockito.doNothing().when(spyComponent).closePopup();
		spyComponent.afterPropertiesSet();
		Mockito.doReturn(true).when(this.validator).isValidNameInput(Matchers.anyString(), Matchers.any(Window.class));
		Mockito.doReturn(true).when(this.studyTree).isFolder(Matchers.anyInt());
		Mockito.doThrow(new MiddlewareQueryException("ERROR")).when(this.studyDataManager).addSubFolder(Matchers.anyInt(),
				Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString());
		
		spyComponent.getOkButton().click();
		Mockito.verify(this.studyTree).isFolder(SELECTED_ID);
		Mockito.verifyNoMoreInteractions(this.studyTree);
		Mockito.verify(spyComponent, Mockito.never()).closePopup();
	}

}

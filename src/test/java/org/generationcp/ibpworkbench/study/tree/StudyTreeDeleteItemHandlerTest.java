package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.exception.GermplasmStudyBrowserException;
import org.generationcp.ibpworkbench.study.tree.StudyTreeDeleteItemHandler.DeleteItemConfirmHandler;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;

public class StudyTreeDeleteItemHandlerTest {
	
	private static final String PROGRAM_UUID = "abcdefg-hijk-123";
	private static final Integer SELECTED_ID = 101;
	private static final Integer PARENT_ID = 100;

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock
	private StudyTree studyTree;
	
	@Mock
	private StudyTreeButtonsPanel buttonsPanel;
	
	@Mock
	private ConfirmDialog dialog;
	
	@Mock
	private Window window;
	
	private StudyTreeDeleteItemHandler deleteItemHandler;
	private DmsProject parent;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.deleteItemHandler = new StudyTreeDeleteItemHandler(this.studyTree, this.buttonsPanel, this.window);
		this.deleteItemHandler.setStudyDataManager(studyDataManager);
		this.deleteItemHandler.setContextUtil(contextUtil);
		this.deleteItemHandler.setMessageSource(messageSource);
		
		final Project project = ProjectTestDataInitializer.createProject();
		project.setUniqueID(PROGRAM_UUID);
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
		parent = new DmsProject();
		parent.setProjectId(PARENT_ID);
		Mockito.doReturn(parent).when(this.studyDataManager).getParentFolder(Matchers.anyInt());
		Mockito.doReturn(SELECTED_ID).when(this.studyTree).getValue();
	}
	
	@Test
	public void testValidateItemForDeletionIsNotExistingShouldThrowError() {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(null);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertTrue("Should throw exception since project is not existing", throwsException);
	}

	@Test
	public void testValidateItemForDeletionIsExistingAndIsNotFolderShouldThrowError() {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(Mockito.mock(DmsProject.class));
		Mockito.when(this.studyTree.isFolder(Matchers.anyInt())).thenReturn(false);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertTrue("Should throw exception since project is not folder", throwsException);
	}

	@Test
	public void testValidateItemForDeletionIsExistingAndIsFolderShouldNotThrowError() throws MiddlewareQueryException {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(Mockito.mock(DmsProject.class));
		Mockito.when(this.studyTree.isFolder(Matchers.anyInt())).thenReturn(true);
		Mockito.when(this.studyTree.hasChildStudy(Matchers.anyInt())).thenReturn(false);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertFalse("Should not throw exception since project is a folder", throwsException);
	}

	@Test
	public void testValidateItemForDeletionIsExistingAndIsFolderAndHasStudyShouldThrowError() throws MiddlewareQueryException {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(Mockito.mock(DmsProject.class));
		Mockito.when(this.studyTree.isFolder(Matchers.anyInt())).thenReturn(true);
		Mockito.when(this.studyTree.hasChildStudy(Matchers.anyInt())).thenReturn(true);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertTrue("Should throw exception since project is a folder with child studies", throwsException);
	}
	
	@Test
	public void testDeleteItemConfirmHandlerWhenDialogNotConfirmed() {
		final DeleteItemConfirmHandler deleteItemConfirmHandler = this.deleteItemHandler.new DeleteItemConfirmHandler(SELECTED_ID);
		deleteItemConfirmHandler.onClose(dialog);
		Mockito.verifyZeroInteractions(this.studyTree);
		Mockito.verifyZeroInteractions(this.buttonsPanel);
		Mockito.verify(this.studyDataManager, Mockito.never()).deleteEmptyFolder(Matchers.anyInt(), Matchers.anyString());
	}
	
	@Test
	public void testPerformDeleteAction() {
		this.deleteItemHandler.performDeleteAction(SELECTED_ID);
		Mockito.verify(this.studyDataManager).deleteEmptyFolder(SELECTED_ID, PROGRAM_UUID);
		Mockito.verify(this.studyTree).removeItem(SELECTED_ID);
		Mockito.verify(this.studyTree).select(PARENT_ID);
		Mockito.verify(this.studyTree).expandItem(PARENT_ID);
		Mockito.verify(this.studyTree).setImmediate(true);
		Mockito.verify(this.buttonsPanel).updateButtons(SELECTED_ID);
	}
	
	@Test
	public void testPerformDeleteActionWhenParentIsRootNode() {
		final int rootNodeId = DmsProject.SYSTEM_FOLDER_ID;
		this.parent.setProjectId(rootNodeId);
		this.deleteItemHandler.performDeleteAction(SELECTED_ID);
		Mockito.verify(this.studyDataManager).deleteEmptyFolder(SELECTED_ID, PROGRAM_UUID);
		Mockito.verify(this.studyTree).removeItem(SELECTED_ID);
		Mockito.verify(this.studyTree).select(StudyTree.STUDY_ROOT_NODE);
		Mockito.verify(this.studyTree, Mockito.never()).expandItem(Matchers.any());
		Mockito.verify(this.studyTree).setImmediate(true);
		Mockito.verify(this.buttonsPanel).updateButtons(SELECTED_ID);
	}

}

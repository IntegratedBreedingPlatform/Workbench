package org.generationcp.ibpworkbench.study.tree;

import java.util.Collection;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.study.StudyBrowserMain;
import org.generationcp.ibpworkbench.study.StudyTabSheet;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import org.junit.Assert;

public class StudyTreeButtonsPanelTest {
	
	private static final String PROGRAM_UUID = "abcd-efghij";

	private static final String CURRENT_FOLDER_NAME = "Current Folder Name";

	private static final Integer SELECTED_ID = 1001;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock 
	private StudyTree studyTree;
	
	@Mock
	private StudyTabSheet studyTabSheet;
	
	@Mock
	private BrowseStudyTreeComponent browseTreeComponent;
	
	@Mock
	private StudyTypeFilterComponent studyTypeFilterComponent;
	
	@Mock
	private StudyBrowserMain component;
	
	@Mock
	private Window window;
	
	@Mock
	private StudyPermissionValidator studyPermissionValidator;
	
	private StudyReference studyReference;
	private StudyTreeButtonsPanel buttonsPanel;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.studyTree).when(this.browseTreeComponent).getStudyTree();
		Mockito.doReturn(this.studyTabSheet).when(this.browseTreeComponent).getTabSheetStudy();
		Mockito.doReturn(this.studyTypeFilterComponent).when(this.browseTreeComponent).getStudyTypeFilterComponent();
		
		this.buttonsPanel = new StudyTreeButtonsPanel(this.browseTreeComponent);
		this.buttonsPanel.setMessageSource(this.messageSource);
		this.buttonsPanel.setStudyDataManager(this.studyDataManager);
		this.buttonsPanel.setStudyPermissionValidator(this.studyPermissionValidator);
		
		final Study study = new Study();
		study.setProgramUUID(PROGRAM_UUID);
		Mockito.doReturn(study).when(this.studyDataManager).getStudy(Matchers.anyInt());
		Mockito.doReturn(SELECTED_ID).when(this.studyTree).getValue();
		Mockito.doReturn(this.component).when(this.browseTreeComponent).getParentComponent();
		Mockito.doReturn(this.window).when(this.component).getWindow();
		
		this.studyReference = new StudyReference(1, RandomStringUtils.random(10));
		this.studyReference.setProgramUUID(PROGRAM_UUID);
		Mockito.doReturn(this.studyReference).when(this.studyDataManager).getStudyReference(SELECTED_ID);
	}
	
	@Test
	public void testAfterPropertiesSet() throws Exception {
		this.buttonsPanel.afterPropertiesSet();
		
		final Button addFolderButton = this.buttonsPanel.getAddFolderBtn();
		Assert.assertNotNull(addFolderButton);
		Assert.assertFalse(addFolderButton.isEnabled());
		Collection<?> clickListeners = addFolderButton.getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
		
		final Button renameFolderButton = this.buttonsPanel.getRenameFolderBtn();
		Assert.assertNotNull(renameFolderButton);
		Assert.assertFalse(renameFolderButton.isEnabled());
		clickListeners = renameFolderButton.getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
		
		final Button deleteFolderButton = this.buttonsPanel.getDeleteFolderBtn();
		Assert.assertNotNull(deleteFolderButton);
		Assert.assertFalse(deleteFolderButton.isEnabled());
		clickListeners = deleteFolderButton.getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
		
		Assert.assertEquals(this.studyTypeFilterComponent, this.buttonsPanel.getComponent(0));
		final HorizontalLayout controlButtonsLayout = (HorizontalLayout) this.buttonsPanel.getComponent(1);
		Assert.assertEquals(addFolderButton, controlButtonsLayout.getComponent(0));
		Assert.assertEquals(renameFolderButton, controlButtonsLayout.getComponent(1));
		Assert.assertEquals(deleteFolderButton, controlButtonsLayout.getComponent(2));
	}
	
	@Test
	public void testUpdateButtonsForRootNode() {
		this.buttonsPanel.instantiateComponents();
		this.buttonsPanel.updateButtons(StudyTree.STUDY_ROOT_NODE);
		Assert.assertTrue(this.buttonsPanel.getAddFolderBtn().isEnabled());
		Assert.assertFalse(this.buttonsPanel.getRenameFolderBtn().isEnabled());
		Assert.assertFalse(this.buttonsPanel.getDeleteFolderBtn().isEnabled());
	}
	
	@Test
	public void testUpdateButtonsForFolder() {
		this.buttonsPanel.instantiateComponents();
		Mockito.doReturn(true).when(this.studyTree).isFolder(Matchers.anyInt());
		this.buttonsPanel.updateButtons(new Integer("101"));
		Assert.assertTrue(this.buttonsPanel.getAddFolderBtn().isEnabled());
		Assert.assertTrue(this.buttonsPanel.getRenameFolderBtn().isEnabled());
		Assert.assertTrue(this.buttonsPanel.getDeleteFolderBtn().isEnabled());
	}
	
	@Test
	public void testUpdateButtonsForStudy() {
		this.buttonsPanel.instantiateComponents();
		Mockito.doReturn(false).when(this.studyTree).isFolder(Matchers.anyInt());
		this.buttonsPanel.updateButtons(new Integer("101"));
		Assert.assertTrue(this.buttonsPanel.getAddFolderBtn().isEnabled());
		Assert.assertFalse(this.buttonsPanel.getRenameFolderBtn().isEnabled());
		Assert.assertFalse(this.buttonsPanel.getDeleteFolderBtn().isEnabled());
	}
	
	@Test
	public void testClickAddFolderButton() {
		this.buttonsPanel.instantiateComponents();
		this.buttonsPanel.addListeners();
		this.buttonsPanel.getAddFolderBtn().setEnabled(true);
		this.buttonsPanel.getAddFolderBtn().click();
		
		final ArgumentCaptor<Window> windowCaptor = ArgumentCaptor.forClass(Window.class);
		Mockito.verify(this.window).addWindow(windowCaptor.capture());
		final StudyTreeAddFolderWindow addFolderWindow = (StudyTreeAddFolderWindow) windowCaptor.getValue();
		Assert.assertEquals(this.studyTree, addFolderWindow.getTargetTree());
		Assert.assertEquals(SELECTED_ID, addFolderWindow.getParentItemId());
	}
	
	@Test
	public void testClickRenameFolderButton() {
		this.buttonsPanel.instantiateComponents();
		this.buttonsPanel.addListeners();
		Mockito.doReturn(CURRENT_FOLDER_NAME).when(this.studyTree).getItemCaption(SELECTED_ID);
		this.buttonsPanel.getRenameFolderBtn().setEnabled(true);
		this.buttonsPanel.getRenameFolderBtn().click();
		
		final ArgumentCaptor<Window> windowCaptor = ArgumentCaptor.forClass(Window.class);
		Mockito.verify(this.window).addWindow(windowCaptor.capture());
		final StudyTreeRenameItemWindow renameFolderWindow = (StudyTreeRenameItemWindow) windowCaptor.getValue();
		Assert.assertEquals(this.studyTree, renameFolderWindow.getTargetTree());
		Assert.assertEquals(this.studyTabSheet, renameFolderWindow.getTabSheet());
		Assert.assertEquals(SELECTED_ID, renameFolderWindow.getItemId());
		Assert.assertEquals(CURRENT_FOLDER_NAME, renameFolderWindow.getCurrentName());
	}
	
	@Test
	public void testTreeNodeCanBeRenamed() {
		Mockito.doReturn(false).when(this.studyPermissionValidator).userLacksPermissionForStudy(this.studyReference);
		Assert.assertTrue(this.buttonsPanel.treeNodeCanBeRenamed(SELECTED_ID));
	}
	
	@Test
	public void testTreeNodeCanBeRenamedForStudyTemplate() {
		this.studyReference.setProgramUUID(null);
		Mockito.doReturn(false).when(this.studyPermissionValidator).userLacksPermissionForStudy(this.studyReference);
		Assert.assertFalse(this.buttonsPanel.treeNodeCanBeRenamed(SELECTED_ID));
	}
	
	@Test
	public void testTreeNodeCanBeRenamedForRestrictedStudy() {
		Mockito.doReturn(true).when(this.studyPermissionValidator).userLacksPermissionForStudy(this.studyReference);
		Assert.assertFalse(this.buttonsPanel.treeNodeCanBeRenamed(SELECTED_ID));
	}

}

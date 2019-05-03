package org.generationcp.ibpworkbench.study.tree;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.SaveTreeStateListener;
import org.generationcp.ibpworkbench.study.StudyBrowserMain;
import org.generationcp.ibpworkbench.study.StudyBrowserMainLayout;
import org.generationcp.ibpworkbench.study.StudyTabSheet;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

import junit.framework.Assert;

public class BrowseStudyTreeComponentTest {
	
	private static final String STUDY_NAME = "F1 Nursery";

	private static final int STUDY_ID = 101;

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock
	private StudyBrowserMain studyBrowserMain;
	
	@Mock
	private StudyBrowserMainLayout studyBrowserMainLayout;
	
	@Mock
	private StudyTypeFilterComponent studyTypeFilterComponent;
	
	@Mock
	private ComboBox comboBox;
	
	@Mock
	private StudyTree studyTree;
	
	@Mock
	private VerticalLayout treeLayout;
	
	@Mock
	private StudyTabSheet tabSheetStudy;
	
	@Mock
	private Tab tab;
	
	@Mock
	private Component component;
	
	@Mock
	private StudyTreeButtonsPanel buttonsPanel;
	
	@Mock
	private SaveTreeStateListener treeStateListener;
	
	@InjectMocks
	private BrowseStudyTreeComponent browseTreeComponent;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.studyBrowserMainLayout).when(this.studyBrowserMain).getMainLayout();
		Mockito.doReturn(this.comboBox).when(this.studyTypeFilterComponent).getStudyTypeComboBox();
		this.browseTreeComponent.setStudyDataManager(this.studyDataManager);
		final DmsProject study = new DmsProject();
		study.setProjectId(STUDY_ID);
		study.setName(STUDY_NAME);
		Mockito.doReturn(study).when(this.studyDataManager).getProject(Matchers.anyInt());
	}
	
	@Test
	public void testAfterPropertiesSet() {
		final BrowseStudyTreeComponent spyComponent = Mockito.spy(this.browseTreeComponent);
		Mockito.doReturn(StudyTypeFilterComponent.ALL_OPTION).when(spyComponent).getFilteredStudyType();
		spyComponent.afterPropertiesSet();
		
		Assert.assertEquals(this.studyBrowserMainLayout, spyComponent.getStudyBrowserMainLayout());
		Assert.assertNotNull(spyComponent.getTabSheetStudy());
		Assert.assertNotNull(spyComponent.getStudyTypeFilterComponent());
		Assert.assertNotNull(spyComponent.getStudyTree());
		Assert.assertNotNull(spyComponent.getButtonsPanel());
		Assert.assertNotNull(spyComponent.getRefreshButton());
		
		final Collection<?> clickListeners = spyComponent.getRefreshButton().getListeners(ClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
	}
	
	@Test
	public void testRefreshButtonClick() {
		final BrowseStudyTreeComponent spyComponent = Mockito.spy(this.browseTreeComponent);
		Mockito.doNothing().when(spyComponent).createTree();
		spyComponent.createActionButtons();
		spyComponent.addListeners();
		spyComponent.setStudyTypeFilterComponent(this.studyTypeFilterComponent);
		spyComponent.setStudyTree(this.studyTree);
		
		// Method to test
		spyComponent.getRefreshButton().click();
		Mockito.verify(this.comboBox).select(StudyTypeFilterComponent.ALL_OPTION);
		Mockito.verify(spyComponent).createTree();
		Mockito.verify(this.studyTree).expandSavedTreeState();
	}

	@Test
	public void testOkButtonClick() {
		final BrowseStudyTreeComponent spyComponent = Mockito.spy(this.browseTreeComponent);
		Mockito.doNothing().when(spyComponent).closeWindow();
		spyComponent.createActionButtons();
		spyComponent.addListeners();
		spyComponent.setStudyTypeFilterComponent(this.studyTypeFilterComponent);
		spyComponent.setStudyTree(this.studyTree);

		// Method to test
		spyComponent.getOkButton().click();
		Mockito.verify(spyComponent).closeWindow();
	}
	
	@Test
	public void testCreateTree() {
		this.browseTreeComponent.setTreeContainer(this.treeLayout);
		this.browseTreeComponent.setStudyTree(this.studyTree);
		this.browseTreeComponent.setButtonsPanel(this.buttonsPanel);
		this.browseTreeComponent.setStudyTypeFilterComponent(this.studyTypeFilterComponent);
		this.browseTreeComponent.createTree();
		
		Mockito.verify(this.treeLayout).removeComponent(this.studyTree);
		Mockito.verify(this.studyTree).removeAllItems();
		// Verify that tree was recreated and is not the mock object anymore
		final StudyTree newStudyTree = this.browseTreeComponent.getStudyTree();
		Assert.assertNotSame(this.studyTree, newStudyTree);
		Assert.assertFalse(newStudyTree.isNullSelectionAllowed());
		Mockito.verify(this.treeLayout).addComponent(newStudyTree);
		Mockito.verify(this.buttonsPanel).setStudyTree(newStudyTree);
	}
	
	@Test
	public void testStudyExistsWhenStudyDoesntExist() {
		Mockito.doReturn(null).when(this.studyDataManager).getProject(Matchers.anyInt());
		Assert.assertFalse(this.browseTreeComponent.studyExists(1));
	}
	
	@Test
	public void testStudyExistsWhenStudyHasChildStudy() {
		this.browseTreeComponent.setStudyTree(this.studyTree);
		final int studyId = 1;
		Mockito.doReturn(false).when(this.studyTree).isFolder(studyId);
		Mockito.doReturn(true).when(this.studyTree).hasChildStudy(studyId);
		Assert.assertFalse(this.browseTreeComponent.studyExists(studyId));
	}
	
	@Test
	public void testStudyExistsWhenStudyIsFolder() {
		this.browseTreeComponent.setStudyTree(this.studyTree);
		final int studyId = 1;
		Mockito.doReturn(true).when(this.studyTree).isFolder(studyId);
		Mockito.doReturn(false).when(this.studyTree).hasChildStudy(studyId);
		Assert.assertFalse(this.browseTreeComponent.studyExists(studyId));
	}
	
	@Test
	public void testStudyExistsWhenStudyHasNoChildStudy() {
		this.browseTreeComponent.setStudyTree(this.studyTree);
		final int studyId = 1;
		Mockito.doReturn(false).when(this.studyTree).isFolder(studyId);
		Mockito.doReturn(false).when(this.studyTree).hasChildStudy(studyId);
		Assert.assertTrue(this.browseTreeComponent.studyExists(studyId));
	}
	
	@Test
	public void testCreateStudyInfoTab() {
		this.browseTreeComponent.setTabSheetStudy(this.tabSheetStudy);
		this.browseTreeComponent.setStudyBrowserMainLayout(this.studyBrowserMainLayout);
		// Method to test
		this.browseTreeComponent.createStudyInfoTab(STUDY_ID);
		Mockito.verify(this.tabSheetStudy).createStudyInfoTab(STUDY_ID, STUDY_NAME, this.studyBrowserMainLayout);
		Mockito.verify(this.studyBrowserMainLayout).addStudyInfoTabSheet(this.tabSheetStudy);
		Mockito.verify(this.studyBrowserMainLayout).showDetailsLayout();
	}
	
	@Test
	public void testCreateStudyInfoTabWhenStudyTabExists() {
		this.browseTreeComponent.setTabSheetStudy(this.tabSheetStudy);
		Mockito.doReturn(1).when(this.tabSheetStudy).getComponentCount();
		Mockito.doReturn(this.tab).when(this.tabSheetStudy).getTab(0);
		Mockito.doReturn(STUDY_NAME).when(this.tab).getCaption();
		Mockito.doReturn(this.component).when(this.tab).getComponent();
		// Method to test
		this.browseTreeComponent.createStudyInfoTab(STUDY_ID);
		Mockito.verify(this.tabSheetStudy, Mockito.never()).createStudyInfoTab(STUDY_ID, STUDY_NAME, this.studyBrowserMainLayout);
		Mockito.verify(this.studyBrowserMainLayout, Mockito.never()).addStudyInfoTabSheet(this.tabSheetStudy);
		Mockito.verify(this.studyBrowserMainLayout, Mockito.never()).showDetailsLayout();
		Mockito.verify(this.tabSheetStudy).setSelectedTab(this.component);
	}

	@Test
	public void testUpdateButtons() {
		this.browseTreeComponent.setButtonsPanel(this.buttonsPanel);
		this.browseTreeComponent.updateButtons(STUDY_ID);
		Mockito.verify(this.buttonsPanel).updateButtons(STUDY_ID);
	}
	
	@Test
	public void testRenameStudyTab() {
		this.browseTreeComponent.setTabSheetStudy(this.tabSheetStudy);
		final String newName = "New Name";
		this.browseTreeComponent.renameStudyTab(STUDY_NAME, newName);
		Mockito.verify(this.tabSheetStudy).renameStudyTab(STUDY_NAME, newName);
	}
	
	@Test
	public void testOpenStudy() {
		final BrowseStudyTreeComponent spyComponent = Mockito.spy(this.browseTreeComponent);
		Mockito.doNothing().when(spyComponent).showChild(STUDY_ID);
		spyComponent.setStudyTree(this.studyTree);
		Mockito.doReturn(true).when(spyComponent).studyExists(STUDY_ID);
		spyComponent.openStudy(STUDY_ID);
		Mockito.verify(this.studyTree).studyTreeItemClickAction(STUDY_ID);
		Mockito.verify(spyComponent).showChild(STUDY_ID);
	}
	
	@Test
	public void testStudyTypeChange() {
		final BrowseStudyTreeComponent spyComponent = Mockito.spy(this.browseTreeComponent);
		Mockito.doNothing().when(spyComponent).createTree();
		spyComponent.setStudyTree(this.studyTree);
		Mockito.doReturn(this.treeStateListener).when(this.studyTree).getSaveTreeStateListener();
		final List<String> expandedIds = Arrays.asList("1", "2", "3");
		Mockito.doReturn(expandedIds).when(this.treeStateListener).getExpandedIds();
		
		spyComponent.studyTypeChange(new StudyTypeDto());
		Mockito.verify(spyComponent).createTree();
		Mockito.verify(this.studyTree).expandNodes(expandedIds);
	}
}

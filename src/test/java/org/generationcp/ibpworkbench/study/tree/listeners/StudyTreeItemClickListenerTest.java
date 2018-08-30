package org.generationcp.ibpworkbench.study.tree.listeners;

import org.generationcp.ibpworkbench.study.tree.BrowseStudyTreeComponent;
import org.generationcp.ibpworkbench.study.tree.StudyTree;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.MouseEvents.ClickEvent;

public class StudyTreeItemClickListenerTest {
	
	private static final Integer STUDY_ID = 100;
	private static final Integer FOLDER_ID = 1;
	
	@Mock
	private StudyTree studyTree;
	
	@Mock
	private BrowseStudyTreeComponent browseTreeComponent;
	
	@Mock
	private ItemClickEvent clickEvent;

	@InjectMocks
	private StudyTreeItemClickListener clickListener;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(ClickEvent.BUTTON_LEFT).when(this.clickEvent).getButton();
		Mockito.doReturn(STUDY_ID).when(this.clickEvent).getItemId();
		Mockito.doReturn(false).when(this.studyTree).hasChildStudy(STUDY_ID);
		Mockito.doReturn(true).when(this.studyTree).hasChildStudy(FOLDER_ID);
		Mockito.doReturn(false).when(this.studyTree).isFolder(STUDY_ID);
		Mockito.doReturn(true).when(this.studyTree).isFolder(FOLDER_ID);
	}
	
	@Test
	public void testItemClickWhenRightClick() {
		Mockito.doReturn(ClickEvent.BUTTON_RIGHT).when(this.clickEvent).getButton();
		this.clickListener.itemClick(this.clickEvent);
		Mockito.verifyZeroInteractions(this.studyTree);
		Mockito.verifyZeroInteractions(this.browseTreeComponent);
	}
	
	@Test
	public void testItemClickWhenItemIsStudy(){
		this.clickListener.itemClick(this.clickEvent);
		Mockito.verify(this.studyTree).expandOrCollapseStudyTreeNode(STUDY_ID);
		Mockito.verify(this.browseTreeComponent).createStudyInfoTab(STUDY_ID);
		Mockito.verify(this.studyTree).selectItem(STUDY_ID);
	}
	
	@Test
	public void testItemClickWhenItemIsFolder(){
		Mockito.doReturn(FOLDER_ID).when(this.clickEvent).getItemId();
		this.clickListener.itemClick(this.clickEvent);
		Mockito.verify(this.studyTree).expandOrCollapseStudyTreeNode(FOLDER_ID);
		Mockito.verify(this.studyTree).selectItem(FOLDER_ID);
	}
	
	@Test
	public void testItemClickWhenItemIsRootNode(){
		Mockito.doReturn(StudyTree.STUDY_ROOT_NODE).when(this.clickEvent).getItemId();
		this.clickListener.itemClick(this.clickEvent);
		Mockito.verify(this.studyTree).expandOrCollapseStudyTreeNode(StudyTree.STUDY_ROOT_NODE);
		Mockito.verify(this.studyTree).selectItem(StudyTree.STUDY_ROOT_NODE);
		Mockito.verify(this.studyTree, Mockito.never()).hasChildStudy(Matchers.anyInt());
		Mockito.verify(this.studyTree, Mockito.never()).isFolder(Matchers.anyInt());
	}
	
}

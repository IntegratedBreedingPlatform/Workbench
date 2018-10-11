
package org.generationcp.ibpworkbench.study.tree;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;

public class StudyTreeDragAndDropHandlerTest {

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private StudyTree studyTree;
	
	@Mock
	private Window window;
	
	@Mock
	private StudyPermissionValidator studyPermissionValidator;

	private StudyTreeDragAndDropHandler dropHandler;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		this.dropHandler = Mockito.spy(new StudyTreeDragAndDropHandler(studyTree));
		this.dropHandler.setMessageSource(this.messageSource);
		this.dropHandler.setStudyDataManager(this.studyDataManager);
		this.dropHandler.setStudyPermissionValidator(this.studyPermissionValidator);
		Mockito.doReturn(this.window).when(this.studyTree).getWindow();
		final StudyReference reference = new StudyReference(1, RandomStringUtils.random(10));
		Mockito.doReturn(reference).when(this.studyDataManager).getStudyReference(Matchers.anyInt());
		Mockito.doReturn(false).when(this.studyPermissionValidator).userLacksPermissionForStudy(Matchers.any(StudyReference.class));
	}

	@Test
	public void testTreeNodeCanBeMovedIfSourceIsRootFolder() {
		final boolean response = this.dropHandler.treeNodeCanBeMoved(StudyTree.STUDY_ROOT_NODE, false);
		Assert.assertFalse("Should return false since the folder being move is the main root folder", response);
	}

	@Test
	public void testTreeNodeCanBeMovedIfSourceIsNotRootFolderButHaveChild() {
		Mockito.when(this.studyTree.hasChildStudy(Matchers.anyInt())).thenReturn(true);
		final boolean response = this.dropHandler.treeNodeCanBeMoved(new Integer(2), false);
		Assert.assertFalse("Should return false since the folder being move has child folder", response);
	}

	@Test
	public void testTreeNodeCanBeMovedIfSourceIsNotRootFolderAndValidTargetFolderReturnTrue() {
		Mockito.when(this.studyTree.hasChildStudy(Matchers.anyInt())).thenReturn(false);
		final boolean response = this.dropHandler.treeNodeCanBeMoved(new Integer(2), true);
		Assert.assertTrue("Should return true since the folder being move and target folder are both valid", response);
	}
	
	@Test
	public void testTreeNodeCanBeMovedIfSourceIsRestrictedStudy() {
		Mockito.when(this.studyTree.hasChildStudy(Matchers.anyInt())).thenReturn(false);
		Mockito.doReturn(true).when(this.studyPermissionValidator).userLacksPermissionForStudy(Matchers.any(StudyReference.class));
		final boolean response = this.dropHandler.treeNodeCanBeMoved(new Integer(2), true);
		Assert.assertFalse(response);
	}

}

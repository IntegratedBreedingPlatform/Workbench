
package org.generationcp.ibpworkbench.study.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.SaveTreeStateListener;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.tree.listeners.StudyTreeCollapseListener;
import org.generationcp.ibpworkbench.study.tree.listeners.StudyTreeExpandListener;
import org.generationcp.ibpworkbench.study.tree.listeners.StudyTreeItemClickListener;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.TreeDragMode;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import junit.framework.Assert;

public class StudyTreeTest {

	private static final String STUDIES = "Studies";
	private static final String PROGRAM_UUID = "abcd-efg-12345";
	private static final FolderReference FOLDER1 = new FolderReference(1, "Folder 1", "Folder 1 Description", PROGRAM_UUID);
	private static final FolderReference FOLDER2 = new FolderReference(2, "Folder 2", "Folder 2 Description", PROGRAM_UUID);
	private static final StudyReference TRIAL =
			new StudyReference(100, "F1 Trial", "Trial Description", PROGRAM_UUID, StudyTypeDto.getTrialDto());
	private static final StudyReference NURSERY =
			new StudyReference(101, "F2 Nusery", "Nursery Description", PROGRAM_UUID, StudyTypeDto.getNurseryDto());
	private static final List<Reference> STUDY_REFERENCES = Arrays.asList(FOLDER1, TRIAL, NURSERY, FOLDER2);
	
	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private BrowseStudyTreeComponent browseStudyTreeComponent;

	@Mock
	private Window window;

	@Mock
	private StudyTreeExpandListener expandListener;

	@Mock
	private StudyTreeItemClickListener clickListener;

	@Mock
	private UserProgramStateDataManager programStateManager;

	private StudyTree studyTree;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.studyTree = new StudyTree(this.browseStudyTreeComponent, StudyTypeFilterComponent.ALL_OPTION);
		this.studyTree.setContextUtil(contextUtil);
		this.studyTree.setStudyDataManager(studyDataManager);
		this.studyTree.setMessageSource(messageSource);

		final Project project = ProjectTestDataInitializer.createProject();
		project.setUniqueID(PROGRAM_UUID);
		Mockito.doReturn(PROGRAM_UUID).when(this.contextUtil).getCurrentProgramUUID();
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
		Mockito.doReturn(new ArrayList<Reference>()).when(this.studyDataManager).getRootFolders(Matchers.anyString());
		Mockito.doReturn(STUDIES).when(this.messageSource).getMessage(Message.STUDIES);
	}

	@Test
	public void testInitializeStudyTree() throws Exception {
		this.studyTree.afterPropertiesSet();

		Assert.assertEquals(TreeDragMode.NODE, this.studyTree.getDragMode());
		Assert.assertTrue(this.studyTree.containsId(StudyTree.STUDY_ROOT_NODE));
		Assert.assertEquals(STUDIES, this.studyTree.getItemCaption(StudyTree.STUDY_ROOT_NODE));
		Assert.assertTrue(this.studyTree.isImmediate());

		// Verify tree action listeners
		final SaveTreeStateListener saveTreeStateListener = this.studyTree.getSaveTreeStateListener();
		Assert.assertNotNull(saveTreeStateListener);
		final Collection<?> clickListeners = this.studyTree.getListeners(ItemClickEvent.class);
		Assert.assertEquals(1, clickListeners.size());
		Assert.assertTrue(clickListeners.iterator().next() instanceof StudyTreeItemClickListener);
		final Collection<?> expandListeners = this.studyTree.getListeners(ExpandEvent.class);
		Assert.assertEquals(1, expandListeners.size());
		Assert.assertTrue(expandListeners.iterator().next() instanceof StudyTreeExpandListener);
		final Collection<?> collapseListeners = this.studyTree.getListeners(CollapseEvent.class);
		Assert.assertEquals(1, collapseListeners.size());
		Assert.assertTrue(collapseListeners.iterator().next() instanceof StudyTreeCollapseListener);
		Assert.assertNotNull(this.studyTree.getDropHandler());
	}

	@Test
	public void testPopulateRootNode() {
		Mockito.doReturn(STUDY_REFERENCES).when(this.studyDataManager).getRootFoldersByStudyType(Matchers.anyString(), ArgumentMatchers.<Integer>isNull());
		// manually add root node to become the parent
		this.studyTree.addItem(StudyTree.STUDY_ROOT_NODE);

		this.studyTree.populateRootNode();
		verifyItemAddedToTree(FOLDER1.getId(), FOLDER1.getName(), StudyTree.FOLDER_ICON);
		verifyItemAddedToTree(FOLDER2.getId(), FOLDER2.getName(), StudyTree.FOLDER_ICON);
		verifyItemAddedToTree(TRIAL.getId(), TRIAL.getName(), StudyTree.STUDY_ICON);
		verifyItemAddedToTree(NURSERY.getId(), NURSERY.getName(), StudyTree.STUDY_ICON);
	}

	@Test
	public void testPopulateRootNodeWithMiddlewareException() {
		Mockito.doThrow(new MiddlewareQueryException("ERROR")).when(this.studyDataManager).getRootFolders(Matchers.anyString());
		this.studyTree.populateRootNode();
		// Expecting no children added because of exception thrown
		Assert.assertTrue(this.studyTree.getItemIds().isEmpty());
	}

	@Test
	public void testGetThemeResourceByReference() {
		Assert.assertEquals(StudyTree.FOLDER_ICON, this.studyTree.getThemeResourceByReference(FOLDER1));
		Assert.assertEquals(StudyTree.STUDY_ICON, this.studyTree.getThemeResourceByReference(TRIAL));
	}

	@Test
	public void testHasChildStudy() {
		Mockito.doReturn(new ArrayList<Reference>()).when(this.studyDataManager).getChildrenOfFolder(Matchers.anyInt(),
				Matchers.anyString());
		final Integer id = FOLDER1.getId();
		Assert.assertFalse(this.studyTree.hasChildStudy(id));

		Mockito.doReturn(STUDY_REFERENCES).when(this.studyDataManager).getChildrenOfFolder(Matchers.anyInt(), Matchers.anyString());
		Assert.assertTrue(this.studyTree.hasChildStudy(id));
	}

	@Test
	public void testHasChildStudyWithMiddlewareException() {
		Mockito.doThrow(new MiddlewareQueryException("ERROR")).when(this.studyDataManager).getChildrenOfFolder(Matchers.anyInt(),
				Matchers.anyString());
		// Need to spy in order to mock tree window
		final StudyTree spyTree = Mockito.spy(this.studyTree);
		Mockito.doReturn(this.window).when(spyTree).getWindow();
		Assert.assertFalse(spyTree.hasChildStudy(FOLDER1.getId()));
		Mockito.verify(this.window).showNotification(Matchers.any(Notification.class));
	}

	@Test
	public void testStudyTreeItemClickAction() {
		this.studyTree.setClickListener(this.clickListener);
		final Integer id = TRIAL.getId();
		this.studyTree.studyTreeItemClickAction(id);
		Mockito.verify(this.clickListener).studyTreeItemClickAction(id);
	}

	@Test
	public void testAddChildren() {
		// Need to spy in order to mock tree window
		final StudyTree spyTree = Mockito.spy(this.studyTree);
		Mockito.doReturn(this.window).when(spyTree).getWindow();
		spyTree.setExpandListener(this.expandListener);

		final Integer id = TRIAL.getId();
		spyTree.addChildren(id);
		Mockito.verify(this.expandListener).addChildren(id, this.window);
	}

	@Test
	public void testSelectItem() {
		final Integer oldId = TRIAL.getId();
		final Integer id = NURSERY.getId();
		this.studyTree.addItem(oldId);
		this.studyTree.addItem(id);
		this.studyTree.setValue(oldId);
		Assert.assertEquals(oldId, this.studyTree.getValue());

		this.studyTree.selectItem(id);
		Mockito.verify(this.browseStudyTreeComponent).updateButtons(id);
		Assert.assertEquals(id, this.studyTree.getValue());
	}

	@Test
	public void testExpandOrCollapseStudyTreeNode() {
		final Integer id = TRIAL.getId();
		this.studyTree.addItem(id);
		this.studyTree.expandItem(id);
		Assert.assertTrue(this.studyTree.isExpanded(id));

		this.studyTree.expandOrCollapseStudyTreeNode(id);
		Assert.assertFalse(this.studyTree.isExpanded(id));

		this.studyTree.expandOrCollapseStudyTreeNode(id);
		Assert.assertTrue(this.studyTree.isExpanded(id));
	}

	@Test
	public void testIsFolder() {
		final Integer id = TRIAL.getId();
		Mockito.doReturn(true).when(this.studyDataManager).isStudy(Matchers.anyInt());
		Assert.assertFalse(this.studyTree.isFolder(id));

		Mockito.doReturn(false).when(this.studyDataManager).isStudy(Matchers.anyInt());
		Assert.assertTrue(this.studyTree.isFolder(id));

		Mockito.doThrow(new MiddlewareQueryException("ERROR")).when(this.studyDataManager).isStudy(Matchers.anyInt());
		Assert.assertFalse(this.studyTree.isFolder(id));
	}
	
	@Test
	public void testExpandNodesWhenEmptyList() {
		populateTestTree();
		this.studyTree.expandNodes(new ArrayList<String>());
		Assert.assertFalse(this.studyTree.isExpanded(StudyTree.STUDY_ROOT_NODE));
	}

	@Test
	public void testExpandNodes() {
		populateTestTree();
		this.studyTree.expandNodes(Arrays.asList(FOLDER1.getId().toString()));
		Assert.assertTrue(this.studyTree.isExpanded(StudyTree.STUDY_ROOT_NODE));
		Assert.assertTrue(this.studyTree.isExpanded(FOLDER1.getId()));
		Assert.assertFalse(this.studyTree.isExpanded(FOLDER2.getId()));
		Assert.assertFalse(this.studyTree.isExpanded(TRIAL.getId()));
		Assert.assertFalse(this.studyTree.isExpanded(NURSERY.getId()));
		Assert.assertNull(this.studyTree.getValue());
	}

	@Test
	public void testExpandSavedTreeState() {
		populateTestTree();
		this.studyTree.setProgramStateManager(this.programStateManager);
		final List<String> idList = Arrays.asList(FOLDER1.getId().toString(), FOLDER2.getId().toString());
		Mockito.doReturn(idList).when(this.programStateManager).getUserProgramTreeState(Matchers.anyInt(),
				Matchers.anyString(), Matchers.anyString());
		final int userId = 1001;
		Mockito.doReturn(userId).when(this.contextUtil).getCurrentWorkbenchUserId();

		this.studyTree.expandSavedTreeState();
		Mockito.verify(this.programStateManager).getUserProgramTreeState(userId, PROGRAM_UUID,
				ListTreeState.STUDY_LIST.name());
		Assert.assertTrue(this.studyTree.isExpanded(StudyTree.STUDY_ROOT_NODE));
		Assert.assertTrue(this.studyTree.isExpanded(FOLDER1.getId()));
		Assert.assertTrue(this.studyTree.isExpanded(FOLDER2.getId()));
		Assert.assertFalse(this.studyTree.isExpanded(TRIAL.getId()));
		Assert.assertFalse(this.studyTree.isExpanded(NURSERY.getId()));
		Assert.assertNull(this.studyTree.getValue());
	}

	private void populateTestTree() {
		this.studyTree.addItem(StudyTree.STUDY_ROOT_NODE);
		this.studyTree.select(StudyTree.STUDY_ROOT_NODE);
		for (final Reference reference : STUDY_REFERENCES) {
			this.studyTree.addItem(reference.getId());
		}
	}

	private void verifyItemAddedToTree(final Integer id, final String name, final ThemeResource icon) {
		Assert.assertTrue(this.studyTree.containsId(id));
		Assert.assertEquals(name, this.studyTree.getItemCaption(id));
		Assert.assertEquals(icon, this.studyTree.getItemIcon(id));
		Assert.assertEquals(StudyTree.STUDY_ROOT_NODE, this.studyTree.getParent(id));
	}
}

package org.generationcp.ibpworkbench.ui.breedingview;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Daniel Villafuerte on 6/22/2015.
 */

@RunWith(MockitoJUnitRunner.class)
public class BreedingViewTreeTableTest {

	public static final int TEST_FOLDER_ITEM_ID = 2;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ManagerFactoryProvider provider;

	@InjectMocks
	private BreedingViewTreeTable treeTable;

	@Mock
	private UserProgramStateDataManager programStateDataManager;

	@Before
	public void setUp() throws Exception {
		final Project project = mock(Project.class);
		when(this.contextUtil.getProjectInContext()).thenReturn(project);
	}

	@Test
	public void testAddFolderReference() {
		final FolderReference testReference = this.constructTestReference();
		this.treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		assertTrue(this.treeTable.getNodeMap().containsKey(testReference.getId()));
		assertNotNull(this.treeTable.getItem(testReference));
	}

	@Test
	public void testSetCollapsedTrue() {
		final FolderReference testReference = this.constructTestReference();
		this.treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		this.treeTable.setCollapsedFolder(TEST_FOLDER_ITEM_ID, true);
		assertTrue(this.treeTable.isCollapsed(testReference));
	}

	@Test
	public void testSetCollapsedFalse() {
		final FolderReference testReference = this.constructTestReference();
		this.treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		this.treeTable.setCollapsedFolder(TEST_FOLDER_ITEM_ID, false);
		assertFalse(this.treeTable.isCollapsed(testReference));
	}

	@Test
	public void testReinitializeTreeExists() throws MiddlewareQueryException {
		final FolderReference testReference = this.constructTestReference();
		this.treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		final List<String> forExpansion = new ArrayList<>();
		forExpansion.add("STUDY");
		forExpansion.add(Integer.toString(TEST_FOLDER_ITEM_ID));

		when(this.programStateDataManager.getUserProgramTreeState(anyInt(), ArgumentMatchers.<String>isNull(), anyString()))
				.thenReturn(forExpansion);

		this.treeTable.reinitializeTree();

		assertFalse(this.treeTable.isCollapsed(testReference));
	}

	@Test
	public void testGetExpandedIds() {
		final FolderReference testReference = this.constructTestReference();
		this.treeTable.addFolderReferenceNode(new Object[] {}, testReference);
		final List<Integer> ids = this.getIntegerIdsList();
		this.treeTable.expandNodes(ids);
		final List<Integer> expandedIds = this.treeTable.getExpandedIds();
		assertTrue(expandedIds.contains(TEST_FOLDER_ITEM_ID));
	}

	@Test
	public void testExpandNodes() {
		final FolderReference testReference = this.constructTestReference();
		this.treeTable.addFolderReferenceNode(new Object[] {}, testReference);
		final List<Integer> ids = this.getIntegerIdsList();
		this.treeTable.expandNodes(ids);
		assertFalse(this.treeTable.isCollapsed(testReference));
	}

	protected FolderReference constructTestReference() {
		return new FolderReference(TEST_FOLDER_ITEM_ID, "TEST", "TEST");
	}

	protected List<Integer> getIntegerIdsList() {
		final List<Integer> ids = new ArrayList<>();
		ids.add(TEST_FOLDER_ITEM_ID);
		return ids;
	}

}

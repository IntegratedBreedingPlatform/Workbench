package org.generationcp.ibpworkbench.ui.breedingview;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
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
		final User userData = mock(User.class);
		final ManagerFactory factory = mock(ManagerFactory.class);
		when(contextUtil.getProjectInContext()).thenReturn(project);
		when(project.getProjectId()).thenReturn((long) 1);
		when(contextUtil.getCurrentWorkbenchUser()).thenReturn(userData);

		when(provider.getManagerFactoryForProject(project)).thenReturn(factory);
		when(factory.getUserProgramStateDataManager()).thenReturn(programStateDataManager);

	}

	@Test
	public void testAddFolderReference() {
		final FolderReference testReference = constructTestReference();
		treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		assertTrue(treeTable.getNodeMap().containsKey(testReference.getId()));
		assertNotNull(treeTable.getItem(testReference));
	}

	@Test
	public void testSetCollapsedTrue() {
		final FolderReference testReference = constructTestReference();
		treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		treeTable.setCollapsedFolder(TEST_FOLDER_ITEM_ID, true);
		assertTrue(treeTable.isCollapsed(testReference));
	}

	@Test
	public void testSetCollapsedFalse() {
		final FolderReference testReference = constructTestReference();
		treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		treeTable.setCollapsedFolder(TEST_FOLDER_ITEM_ID, false);
		assertFalse(treeTable.isCollapsed(testReference));
	}

	@Test
	public void testReinitializeTreeExists() throws MiddlewareQueryException {
		final FolderReference testReference = constructTestReference();
		treeTable.addFolderReferenceNode(new Object[] {}, testReference);

		final List<String> forExpansion = new ArrayList<>();
		forExpansion.add("STUDY");
		forExpansion.add(Integer.toString(TEST_FOLDER_ITEM_ID));

		when(programStateDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(anyInt(), anyString(), anyString()))
				.thenReturn(forExpansion);

		treeTable.reinitializeTree();

		assertFalse(treeTable.isCollapsed(testReference));
	}

	protected FolderReference constructTestReference() {
		return new FolderReference(TEST_FOLDER_ITEM_ID, "TEST", "TEST");
	}

}

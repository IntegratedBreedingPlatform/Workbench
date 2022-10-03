package org.generationcp.ibpworkbench.ui.breedingview;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Daniel Villafuerte on 6/22/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class SaveBreedingViewStudyTreeStateTest {

	@Mock
	private ManagerFactoryProvider provider;

	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private final SaveBreedingViewStudyTreeState dut = constructTestObject();

	@Mock
	private UserProgramStateDataManager programStateDataManager;

	private FolderReference testReference1;
	private FolderReference testReference2;
	private BreedingViewTreeTable testTable;

	@Before
	public void setUp() throws Exception {
		final Project project = mock(Project.class);
		when(contextUtil.getProjectInContext()).thenReturn(project);
	}

	@Test
	public void testSaveOnWindowCloseNoneOpened() throws MiddlewareQueryException {
		testTable.setCollapsed(testReference1, true);
		testTable.setCollapsed(testReference2, true);

		dut.windowClose(null);

		final ArgumentCaptor<List> stringListCaptor = ArgumentCaptor.forClass(List.class);

		verify(programStateDataManager).saveOrUpdateUserProgramTreeState(anyInt(), ArgumentMatchers.<String>isNull(), anyString(), stringListCaptor.capture());

		final List savedTreeState = stringListCaptor.getValue();

		assertTrue(savedTreeState.size() == 1);
		assertEquals("STUDY", savedTreeState.get(0));
	}

	@Test
	public void testSaveOnWindowCloseOnlyChildFolderOpened() throws MiddlewareQueryException {
		testTable.setCollapsed(testReference1, true);
		testTable.setCollapsed(testReference2, false);

		dut.windowClose(null);

		final ArgumentCaptor<List> stringListCaptor = ArgumentCaptor.forClass(List.class);

		verify(programStateDataManager).saveOrUpdateUserProgramTreeState(anyInt(), ArgumentMatchers.<String>isNull(), anyString(), stringListCaptor.capture());

		final List savedTreeState = stringListCaptor.getValue();

		assertTrue(savedTreeState.size() == 1);
		assertEquals("STUDY", savedTreeState.get(0));
	}

	@Test
	public void testSaveOnWindowCloseAllOpened() throws MiddlewareQueryException {
		testTable.setCollapsed(testReference1, false);
		testTable.setCollapsed(testReference2, false);

		dut.windowClose(null);

		final ArgumentCaptor<List> stringListCaptor = ArgumentCaptor.forClass(List.class);

		verify(programStateDataManager).saveOrUpdateUserProgramTreeState(anyInt(), ArgumentMatchers.<String>isNull(), anyString(), stringListCaptor.capture());

		final List savedTreeState = stringListCaptor.getValue();

		assertTrue(savedTreeState.size() == 3);
		assertEquals("STUDY", savedTreeState.get(0));
		assertEquals(testReference1.getId().toString(), savedTreeState.get(1));
		assertEquals(testReference2.getId().toString(), savedTreeState.get(2));
	}
	
	@Test
	public void testGetExpandedIdsWithNoExpandedFolder() {
		testTable.setCollapsed(testReference1, true);
		List<String> expandedIds = this.dut.getExpandedIds();
		Assert.assertEquals(1, expandedIds.size());
	}
	
	@Test
	public void testGetExpandedIdsWithExpandedFolders() {
		testTable.setCollapsed(testReference1, false);
		testTable.setCollapsed(testReference2, false);
		List<String> expandedIds = this.dut.getExpandedIds();
		Assert.assertEquals(3, expandedIds.size());
	}
	
	@Test
	public void testGetFirstLevelFolders() {
		List<Reference> firstLevelFolders = this.dut.getFirstLevelFolders();
		Assert.assertEquals(1, firstLevelFolders.size());
		Reference folder = firstLevelFolders.get(0);
		Assert.assertEquals(testReference1.getId(), folder.getId());
	}

	protected SaveBreedingViewStudyTreeState constructTestObject() {
		testTable = new BreedingViewTreeTable();

		testReference1 = new FolderReference(2, "TEST", "TEST");
		testReference1.setParentFolderId(DmsProject.SYSTEM_FOLDER_ID);
		testTable.addFolderReferenceNode(new Object[] {}, testReference1);
		testReference2 = new FolderReference(3, "TEST", "TEST");
		testTable.addFolderReferenceNode(new Object[] {}, testReference2);

		testTable.setParent(testReference2, testReference1);

		return new SaveBreedingViewStudyTreeState(testTable);
	}
}

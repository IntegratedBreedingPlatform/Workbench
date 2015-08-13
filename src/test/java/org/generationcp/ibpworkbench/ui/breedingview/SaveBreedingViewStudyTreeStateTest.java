
package org.generationcp.ibpworkbench.ui.breedingview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by Daniel Villafuerte on 6/22/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class SaveBreedingViewStudyTreeStateTest {

	@Mock
	private ManagerFactoryProvider provider;

	@Mock
	private SessionData sessionData;

	@InjectMocks
	private SaveBreedingViewStudyTreeState dut = constructTestObject();

	@Mock
	private UserProgramStateDataManager programStateDataManager;

	private FolderReference testReference1;
	private FolderReference testReference2;
	private BreedingViewTreeTable testTable;

	@Before
	public void setUp() throws Exception {
		Project project = mock(Project.class);
		User userData = mock(User.class);
		ManagerFactory factory = mock(ManagerFactory.class);
		when(sessionData.getSelectedProject()).thenReturn(project);
		when(project.getProjectId()).thenReturn((long) 1);
		when(sessionData.getUserData()).thenReturn(userData);

		when(provider.getManagerFactoryForProject(project)).thenReturn(factory);
		when(factory.getUserProgramStateDataManager()).thenReturn(programStateDataManager);

	}

	@Test
	public void testSaveOnWindowCloseNoneOpened() throws MiddlewareQueryException {
		testTable.setCollapsed(testReference1, true);
		testTable.setCollapsed(testReference2, true);

		dut.windowClose(null);

		ArgumentCaptor<List> stringListCaptor = ArgumentCaptor.forClass(List.class);

		verify(programStateDataManager).saveOrUpdateUserProgramTreeState(anyInt(), anyString(), anyString(), stringListCaptor.capture());

		List savedTreeState = stringListCaptor.getValue();

		assertTrue(savedTreeState.size() == 1);
		assertEquals("STUDY", savedTreeState.get(0));
	}

	@Test
	public void testSaveOnWindowCloseOnlyChildFolderOpened() throws MiddlewareQueryException {
		testTable.setCollapsed(testReference1, true);
		testTable.setCollapsed(testReference2, false);

		dut.windowClose(null);

		ArgumentCaptor<List> stringListCaptor = ArgumentCaptor.forClass(List.class);

		verify(programStateDataManager).saveOrUpdateUserProgramTreeState(anyInt(), anyString(), anyString(), stringListCaptor.capture());

		List savedTreeState = stringListCaptor.getValue();

		assertTrue(savedTreeState.size() == 1);
		assertEquals("STUDY", savedTreeState.get(0));
	}

	@Test
	public void testSaveOnWindowCloseAllOpened() throws MiddlewareQueryException {
		testTable.setCollapsed(testReference1, false);
		testTable.setCollapsed(testReference2, false);

		dut.windowClose(null);

		ArgumentCaptor<List> stringListCaptor = ArgumentCaptor.forClass(List.class);

		verify(programStateDataManager).saveOrUpdateUserProgramTreeState(anyInt(), anyString(), anyString(), stringListCaptor.capture());

		List savedTreeState = stringListCaptor.getValue();

		assertTrue(savedTreeState.size() == 3);
		assertEquals("STUDY", savedTreeState.get(0));
		assertEquals(testReference1.getId().toString(), savedTreeState.get(1));
		assertEquals(testReference2.getId().toString(), savedTreeState.get(2));
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

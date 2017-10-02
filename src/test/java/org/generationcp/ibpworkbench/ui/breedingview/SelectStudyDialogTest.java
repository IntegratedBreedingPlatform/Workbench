
package org.generationcp.ibpworkbench.ui.breedingview;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import junit.framework.Assert;

import org.generationcp.middleware.data.initializer.StudyReferenceTestDataInitializer;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.AssertThrows;

@RunWith(MockitoJUnitRunner.class)
public class SelectStudyDialogTest {
	private static final String UNIQUE_ID = "12345";

	protected SelectStudyDialog dialog;

	@Mock
	private Project currentProject;

	@Mock
	private StudyDataManager studyDataManager;

	@Before
	public void setUp() throws Exception {
		this.dialog = new SelectStudyDialog(Mockito.mock(Window.class), Mockito.mock(Component.class), currentProject);
		this.dialog.setStudyDataManager(studyDataManager);
	}

	@Test
	public void testCreateStudyTreeTable() throws MiddlewareQueryException {
		Mockito.when(currentProject.getUniqueID()).thenReturn(UNIQUE_ID);
		Mockito.when(this.studyDataManager.getRootFolders(this.currentProject.getUniqueID(), StudyType.trials())).thenReturn(StudyReferenceTestDataInitializer.createStudyReferenceList(5));
		BreedingViewTreeTable table = dialog.createStudyTreeTable();
		Mockito.verify(studyDataManager, Mockito.times(1)).getRootFolders(UNIQUE_ID, StudyType.trials());
		Assert.assertEquals("There should be 33 property ids.", 3, table.getContainerPropertyIds().size());
		Assert.assertTrue("The property ids should contain " + SelectStudyDialog.STUDY_NAME, table.getContainerPropertyIds().contains(SelectStudyDialog.STUDY_NAME));
		Assert.assertTrue("The property ids should contain " + SelectStudyDialog.OBJECTIVE, table.getContainerPropertyIds().contains(SelectStudyDialog.OBJECTIVE));
		Assert.assertTrue("The property ids should contain " + SelectStudyDialog.TITLE, table.getContainerPropertyIds().contains(SelectStudyDialog.TITLE));
		Assert.assertEquals("The table should contain 5 studies", 5, table.getNodeMap().size());
	}
}

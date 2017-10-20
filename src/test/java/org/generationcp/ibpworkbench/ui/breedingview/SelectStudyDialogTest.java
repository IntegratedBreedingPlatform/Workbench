
package org.generationcp.ibpworkbench.ui.breedingview;

import org.generationcp.middleware.data.initializer.FolderReferenceTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyReferenceTestDataInitializer;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import junit.framework.Assert;

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
		this.dialog = new SelectStudyDialog(Mockito.mock(Window.class), Mockito.mock(Component.class),
				this.currentProject);
		this.dialog.setStudyDataManager(this.studyDataManager);
	}

	@Test
	public void testCreateStudyTreeTable() throws MiddlewareQueryException {
		Mockito.when(this.currentProject.getUniqueID()).thenReturn(SelectStudyDialogTest.UNIQUE_ID);
		Mockito.when(this.studyDataManager.getRootFolders(this.currentProject.getUniqueID(), StudyType.trials()))
				.thenReturn(StudyReferenceTestDataInitializer.createStudyReferenceList(5));
		final BreedingViewTreeTable table = this.dialog.createStudyTreeTable();
		Mockito.verify(this.studyDataManager, Mockito.times(1)).getRootFolders(SelectStudyDialogTest.UNIQUE_ID,
				StudyType.trials());
		Assert.assertEquals("There should be 33 property ids.", 3, table.getContainerPropertyIds().size());
		Assert.assertTrue("The property ids should contain " + SelectStudyDialog.STUDY_NAME,
				table.getContainerPropertyIds().contains(SelectStudyDialog.STUDY_NAME));
		Assert.assertTrue("The property ids should contain " + SelectStudyDialog.OBJECTIVE,
				table.getContainerPropertyIds().contains(SelectStudyDialog.OBJECTIVE));
		Assert.assertTrue("The property ids should contain " + SelectStudyDialog.TITLE,
				table.getContainerPropertyIds().contains(SelectStudyDialog.TITLE));
		Assert.assertEquals("The table should contain 5 studies", 5, table.getNodeMap().size());
	}

	@Test
	public void testHasChildStudyFalse() {
		final boolean hasChildStudy = this.dialog.hasChildStudy(1);
		Assert.assertFalse("The study should have no child.", hasChildStudy);
	}

	@Test
	public void testHasChildStudyTrue() {
		Mockito.when(
				this.studyDataManager.getChildrenOfFolder(1, this.currentProject.getUniqueID(), StudyType.trials()))
				.thenReturn(StudyReferenceTestDataInitializer.createStudyReferenceList(1));
		
		final boolean hasChildStudy = this.dialog.hasChildStudy(1);
		
		Assert.assertTrue("The study should have a child.", hasChildStudy);
	}

	@Test
	public void testQueryChildrenStudies() {
		final Reference reference = FolderReferenceTestDataInitializer.createReference(1);
		Mockito.when(
				this.studyDataManager.getChildrenOfFolder(1, this.currentProject.getUniqueID(), StudyType.trials()))
				.thenReturn(StudyReferenceTestDataInitializer.createStudyReferenceList(5));
		
		this.dialog.queryChildrenStudies(reference, Mockito.mock(BreedingViewTreeTable.class));
		
		Mockito.verify(this.studyDataManager, Mockito.times(2)).getChildrenOfFolder(1,
				this.currentProject.getUniqueID(), StudyType.trials());
		Mockito.verify(this.studyDataManager, Mockito.times(5)).getStudy(Matchers.anyInt());
	}

}

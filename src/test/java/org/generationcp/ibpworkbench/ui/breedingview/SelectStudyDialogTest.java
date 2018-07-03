
package org.generationcp.ibpworkbench.ui.breedingview;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import junit.framework.Assert;
import org.generationcp.ibpworkbench.study.tree.StudyTypeFilterComponent;
import org.generationcp.middleware.data.initializer.FolderReferenceTestDataInitializer;
import org.generationcp.middleware.data.initializer.StudyReferenceTestDataInitializer;
import org.generationcp.middleware.domain.dms.Reference;
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

@RunWith(MockitoJUnitRunner.class)
public class SelectStudyDialogTest {
	private static final String UNIQUE_ID = "12345";

	protected SelectStudyDialog dialog;

	@Mock
	private Project currentProject;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private StudyTypeFilterComponent studyTypeFilterComponent;

	@Mock
	private ComboBox comboBox;

	@Before
	public void setUp() throws Exception {
		this.dialog = new SelectStudyDialog(Mockito.mock(Window.class), Mockito.mock(Component.class),
				this.currentProject);
		this.dialog.setStudyDataManager(this.studyDataManager);
		this.dialog.setStudyTypeFilterComponent(studyTypeFilterComponent);
	}

	@Test
	public void testCreateStudyTreeTable() throws MiddlewareQueryException {
		Mockito.when(this.currentProject.getUniqueID()).thenReturn(SelectStudyDialogTest.UNIQUE_ID);
		Mockito.when(this.studyTypeFilterComponent.getStudyTypeComboBox()).thenReturn(comboBox);
		Mockito.when(comboBox.getValue()).thenReturn(StudyTypeFilterComponent.ALL_OPTION);
		Mockito.when(this.studyDataManager.getRootFoldersByStudyType(Matchers.eq(this.currentProject.getUniqueID()), Mockito.anyInt()))
				.thenReturn(StudyReferenceTestDataInitializer.createStudyReferenceList(5));
		final BreedingViewTreeTable table = this.dialog.createStudyTreeTable();
		Mockito.verify(this.studyDataManager, Mockito.times(1))
				.getRootFoldersByStudyType(Matchers.eq(SelectStudyDialogTest.UNIQUE_ID), Mockito.anyInt());
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
		Mockito.when(this.studyTypeFilterComponent.getStudyTypeComboBox()).thenReturn(comboBox);
		Mockito.when(comboBox.getValue()).thenReturn(StudyTypeFilterComponent.ALL_OPTION);
		final boolean hasChildStudy = this.dialog.hasChildStudy(1);
		Assert.assertFalse("The study should have no child.", hasChildStudy);
	}

	@Test
	public void testHasChildStudyTrue() {
		Mockito.when(this.studyTypeFilterComponent.getStudyTypeComboBox()).thenReturn(comboBox);
		Mockito.when(comboBox.getValue()).thenReturn(StudyTypeFilterComponent.ALL_OPTION);
		Mockito.when(
				this.studyDataManager.getChildrenOfFolderByStudyType(1, this.currentProject.getUniqueID(), null))
				.thenReturn(StudyReferenceTestDataInitializer.createStudyReferenceList(1));
		
		final boolean hasChildStudy = this.dialog.hasChildStudy(1);
		
		Assert.assertTrue("The study should have a child.", hasChildStudy);
	}

	@Test
	public void testQueryChildrenStudies() {
		Mockito.when(this.studyTypeFilterComponent.getStudyTypeComboBox()).thenReturn(comboBox);
		Mockito.when(comboBox.getValue()).thenReturn(StudyTypeFilterComponent.ALL_OPTION);
		final Reference reference = FolderReferenceTestDataInitializer.createReference(1);
		Mockito.when(
				this.studyDataManager.getChildrenOfFolderByStudyType(1, this.currentProject.getUniqueID(), null))
				.thenReturn(StudyReferenceTestDataInitializer.createStudyReferenceList(5));
		
		this.dialog.queryChildrenStudies(reference, Mockito.mock(BreedingViewTreeTable.class));
		
		Mockito.verify(this.studyDataManager, Mockito.times(2)).getChildrenOfFolderByStudyType(1,
				this.currentProject.getUniqueID(), null);
		Mockito.verify(this.studyDataManager, Mockito.times(5)).getStudy(Matchers.anyInt());
	}

}

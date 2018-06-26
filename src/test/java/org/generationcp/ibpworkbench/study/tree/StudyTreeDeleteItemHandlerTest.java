package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.ibpworkbench.exception.GermplasmStudyBrowserException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

public class StudyTreeDeleteItemHandlerTest {
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private StudyTree studyTree;
	
	private StudyTreeDeleteItemHandler deleteItemHandler;
	
	@Test
	public void testValidateItemForDeletionIsNotExistingShouldThrowError() {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(null);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertTrue("Should throw exception since project is not existing", throwsException);
	}

	@Test
	public void testValidateItemForDeletionIsExistingAndIsNotFolderShouldThrowError() {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(Mockito.mock(DmsProject.class));
		Mockito.when(this.studyTree.isFolder(Matchers.anyInt())).thenReturn(false);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertTrue("Should throw exception since project is not folder", throwsException);
	}

	@Test
	public void testValidateItemForDeletionIsExistingAndIsFolderShouldNotThrowError() throws MiddlewareQueryException {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(Mockito.mock(DmsProject.class));
		Mockito.when(this.studyTree.isFolder(Matchers.anyInt())).thenReturn(true);
		Mockito.when(this.studyTree.hasChildStudy(Matchers.anyInt())).thenReturn(false);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertFalse("Should not throw exception since project is a folder", throwsException);
	}

	@Test
	public void testValidateItemForDeletionIsExistingAndIsFolderAndHasStudyShouldThrowError() throws MiddlewareQueryException {
		Mockito.when(this.studyDataManager.getProject(Matchers.anyInt())).thenReturn(Mockito.mock(DmsProject.class));
		Mockito.when(this.studyTree.isFolder(Matchers.anyInt())).thenReturn(true);
		Mockito.when(this.studyTree.hasChildStudy(Matchers.anyInt())).thenReturn(true);
		final Integer id = 1;
		boolean throwsException = false;
		try {
			this.deleteItemHandler.validateItemForDeletion(id);
		} catch (final GermplasmStudyBrowserException e) {
			throwsException = true;
		}
		Assert.assertTrue("Should throw exception since project is a folder with child studies", throwsException);
	}

}

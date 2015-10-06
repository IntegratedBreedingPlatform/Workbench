
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;

@RunWith(MockitoJUnitRunner.class)
public class NurseryListPreviewPresenterTest {

	public static final String MORE_THAN_255_CHAR_STRING = "the quick brown fox jumps over the lazy dog. "
			+ "the quick brown fox jumps over the lazy dog. " + "the quick brown fox jumps over the lazy dog. "
			+ "the quick brown fox jumps over the lazy dog. " + "the quick brown fox jumps over the lazy dog. "
			+ "the quick brown fox jumps over the lazy dog. ";

	private static final String NURSERIES_AND_TRIALS = "Nurseries and Trials";

	private Project project;

	@Mock
	private NurseryListPreview view;

	@Mock
	private WorkbenchDataManager manager;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private DmsProject dmsProject;

	@InjectMocks
	private final NurseryListPreviewPresenter presenter = new NurseryListPreviewPresenter();

	private final String newFolderName = "folderName";
	private final int folderId = 1;
	private final int sourceId = 1;
	private final int targetId = 1;
	private final boolean isAStudy = true;
	private final int studyId = 2;
	private final int parentFolderId = 3;
	private final int studyIdWithMultipleChildren = 4;
	private final int studyIdWithNoChildren = 5;

	@Before
	public void setUp() throws Exception {
		NurseryListPreview.NURSERIES_AND_TRIALS = NurseryListPreviewPresenterTest.NURSERIES_AND_TRIALS;
		this.project = this.createTestProjectData();
		this.view.setProject(this.project);

		this.presenter.setProject(this.project);
	}

	public Project createTestProjectData() {
		Project project = new Project();
		project.setUserId(1);
		int uniqueId = new Random().nextInt(10000);
		project.setProjectName("Test Project " + uniqueId);
		project.setStartDate(new Date(System.currentTimeMillis()));
		project.setLastOpenDate(new Date(System.currentTimeMillis()));
		project.setUniqueID(Integer.toString(uniqueId));
		return project;
	}

	@Test
	public void testIsFolder() throws Exception {
		this.presenter.isFolder(this.folderId);
		Mockito.verify(this.studyDataManager).isStudy(this.folderId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRenameNurseryListFolder() throws Exception {
		Mockito.when(this.messageSource.getMessage(Message.INVALID_ITEM_NAME)).thenReturn("Blank name not accepted");
		Mockito.when(this.studyDataManager.renameSubFolder(null, 0, this.project.getUniqueID())).thenThrow(MiddlewareException.class);

		this.presenter.renameNurseryListFolder(this.newFolderName, this.folderId);

		Mockito.verify(this.studyDataManager).renameSubFolder(this.newFolderName, this.folderId, this.project.getUniqueID());

		try {
			this.presenter.renameNurseryListFolder(null, 0);
			Assert.fail("should throw an error when newFolderName = null");
		} catch (NurseryListPreviewException e) {
			Assert.assertTrue(e.getMessage().contains(NurseryListPreviewException.BLANK_NAME));
		}

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMoveNurseryListFolder() throws Exception {

		this.presenter.moveNurseryListFolder(this.sourceId, this.targetId, this.isAStudy);
		Mockito.verify(this.studyDataManager).moveDmsProject(this.sourceId, this.targetId, this.isAStudy);

		// simulate middleware error
		Mockito.when(this.studyDataManager.moveDmsProject(-100, -100, false)).thenThrow(MiddlewareQueryException.class);

		try {
			this.presenter.moveNurseryListFolder(-100, -100, false);
			Assert.fail("should throw an NurseryListPreviewException exception");
		} catch (NurseryListPreviewException e) {
			Assert.assertTrue("should throw an NurseryListPreviewException exception", true);
		}

	}

	@Test
	public void testAddNurseryListFolder() throws Exception {
		// 3 scenarios

		// 1st scenario name is null or empty
		try {
			this.presenter.addNurseryListFolder(null, this.studyId);
			Assert.fail("should throw an exception if name = null");
		} catch (NurseryListPreviewException e) {
			Assert.assertTrue(e.getLocalizedMessage().contains("Folder name cannot be blank"));
		}

		// 2nd scenario name == STUDIES
		try {
			this.presenter.addNurseryListFolder(NurseryListPreview.NURSERIES_AND_TRIALS, this.studyId);
			Assert.fail("should throw an exception if name = NurseryListPreview.NURSERIES_AND_TRIALS");
		} catch (NurseryListPreviewException e) {
			Assert.assertTrue(e.getLocalizedMessage().contains("Please choose a different name"));

		}

		// 3rd scenario presenter.isFalder(id) === false
		// assume that studyID is not a folder
		Mockito.when(this.studyDataManager.isStudy(this.studyId)).thenReturn(true);
		Mockito.when(this.studyDataManager.getParentFolder(this.studyId)).thenReturn(this.dmsProject);
		Mockito.when(this.dmsProject.getProjectId()).thenReturn(this.parentFolderId);
		this.presenter.addNurseryListFolder(this.newFolderName, this.studyId);

		// verify that addSubFolder is called with the correct order of parameters
		Mockito.verify(this.studyDataManager).addSubFolder(this.parentFolderId, this.newFolderName, this.newFolderName,
				this.project.getUniqueID());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValidateForDeleteNurseryList() throws Exception {
		// if id is null, expect exception
		try {
			this.presenter.validateForDeleteNurseryList(null);
			Assert.fail("Should throw an exception if id is null");
		} catch (NurseryListPreviewException e) {
			Assert.assertTrue(e.getLocalizedMessage().contains("Please select a folder item"));
		}

		// assume studyDataManager.getProject() returns a DMSObj (no middleware exception)
		Mockito.when(this.studyDataManager.getProject(this.studyIdWithMultipleChildren)).thenReturn(this.dmsProject);
		Mockito.when(this.studyDataManager.getProject(this.studyIdWithNoChildren)).thenReturn(this.dmsProject);

		List<Reference> hasMultipleChildren = Mockito.mock(ArrayList.class);
		hasMultipleChildren.add(Mockito.mock(Reference.class));
		hasMultipleChildren.add(Mockito.mock(Reference.class));
		hasMultipleChildren.add(Mockito.mock(Reference.class));

		Mockito.when(this.studyDataManager.getChildrenOfFolder(this.studyIdWithMultipleChildren, this.project.getUniqueID(), StudyType.nurseriesAndTrials())).thenReturn(
				hasMultipleChildren);
		Mockito.when(this.studyDataManager.getChildrenOfFolder(this.studyIdWithNoChildren, this.project.getUniqueID(), StudyType.nurseriesAndTrials())).thenReturn(
				new ArrayList<Reference>());

		try {
			this.presenter.validateForDeleteNurseryList(this.studyIdWithMultipleChildren);
			Assert.fail("Should throw an exception if NurseryListPreviewException.HAS_CHILDREN");
		} catch (NurseryListPreviewException e) {
			Assert.assertTrue(e.getLocalizedMessage().contains(NurseryListPreviewException.HAS_CHILDREN));
		}

		Assert.assertEquals("Folder has no children, can be deleted", Integer.valueOf(this.studyIdWithNoChildren),
				this.presenter.validateForDeleteNurseryList(this.studyIdWithNoChildren));
	}

	@Test
	public void testValidateStudyFolderName() throws Exception {
		try {
			this.presenter.validateStudyFolderName(this.newFolderName);
		} catch (NurseryListPreviewException e) {
			Assert.fail("We should not expect an exception since the input is valid");
		}
	}

	@Test(expected = NurseryListPreviewException.class)
	public void testValidateStudyFolderNameNull() throws Exception {
		this.presenter.validateStudyFolderName(null);
		Assert.fail("We are expecting an exception since the input is NOT valid");
	}

	@Test(expected = NurseryListPreviewException.class)
	public void testValidateStudyFolderNameBlank() throws Exception {
		this.presenter.validateStudyFolderName("");
		Assert.fail("We are expecting an exception since the input is NOT valid");

	}

	@Test(expected = NurseryListPreviewException.class)
	public void testValidateStudyFolderNameInvalidProgramStudies() throws Exception {
		this.presenter.validateStudyFolderName(NurseryListPreview.NURSERIES_AND_TRIALS);
		Assert.fail("We are expecting an exception since the input is NOT valid");

	}

	@Test(expected = NurseryListPreviewException.class)
	public void testValidateStudyFolderNameTooLong() throws Exception {
		this.presenter.validateStudyFolderName(NurseryListPreviewPresenterTest.MORE_THAN_255_CHAR_STRING);
		Assert.fail("We are expecting an exception since the input is NOT valid");
	}
}

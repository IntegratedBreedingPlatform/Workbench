package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NurseryListPreviewPresenterTest  {
    public static final String MORE_THAN_255_CHAR_STRING = "the quick brown fox jumps over the lazy dog. " +
            "the quick brown fox jumps over the lazy dog. " +
            "the quick brown fox jumps over the lazy dog. " +
            "the quick brown fox jumps over the lazy dog. " +
            "the quick brown fox jumps over the lazy dog. " +
            "the quick brown fox jumps over the lazy dog. ";
    @Mock
    private Project project;

    @Mock
    private NurseryListPreview view;

    @Mock
    private ManagerFactoryProvider managerFactoryProvider;

    @Mock
    private WorkbenchDataManager manager;

    @Mock
    private ManagerFactory managerFactory;

    @Mock
    private StudyDataManager studyDataManager;

    @Mock
    private SimpleResourceBundleMessageSource messageSource;

    @Mock
    private DmsProject dmsProject;

    @InjectMocks
    private NurseryListPreviewPresenter presenter = new NurseryListPreviewPresenter();

    private String newFolderName = "folderName";
    private int folderId = 1;
    private int sourceId = 1;
    private int targetId = 1;
    private boolean isAStudy = true;
    private int studyId = 2;
    private int parentFolderId = 3;
    private int studyIdWithMultipleChildren = 4;
    private int studyIdWithNoChildren = 5;

    @Before
    public void setUp() throws Exception {
        when(view.getManagerFactoryProvider()).thenReturn(managerFactoryProvider);
        view.SHARED_STUDIES = "Public Studies";
        view.MY_STUDIES = "Program Studies";

        when(managerFactoryProvider.getManagerFactoryForProject(project)).thenReturn(managerFactory);
        when(managerFactory.getStudyDataManager()).thenReturn(studyDataManager);

        presenter.setManagerFactory(managerFactory);
    }

    @Test
    public void testIsFolder() throws Exception {
        presenter.isFolder(folderId);
        verify(studyDataManager).isStudy(folderId);
    }

    @Test
    public void testRenameNurseryListFolder() throws Exception {
        when(messageSource.getMessage(Message.INVALID_ITEM_NAME)).thenReturn("Blank name not accepted");
        when(studyDataManager.renameSubFolder(null, 0)).thenThrow(MiddlewareException.class);

        presenter.renameNurseryListFolder(newFolderName,folderId);

        verify(studyDataManager).renameSubFolder(newFolderName,folderId);

        try {
            presenter.renameNurseryListFolder(null,0);
            fail("should throw an error when newFolderName = null");
        } catch (NurseryListPreviewException e) {
            assertTrue(e.getMessage().contains(NurseryListPreviewException.BLANK_NAME));
        }

    }

    @Test
    public void testMoveNurseryListFolder() throws Exception {

        presenter.moveNurseryListFolder(sourceId,targetId,isAStudy);
        verify(studyDataManager).moveDmsProject(sourceId,targetId,isAStudy);

        // simulate middleware error
        when(studyDataManager.moveDmsProject(-100,-100,false)).thenThrow(MiddlewareQueryException.class);

        try {
            presenter.moveNurseryListFolder(-100, -100, false);
            fail("should throw an NurseryListPreviewException exception");
        } catch (NurseryListPreviewException e) {
            assertTrue("should throw an NurseryListPreviewException exception",true);
        }

    }

    @Test
    public void testAddNurseryListFolder() throws Exception {
        // 3 scenarios

        // 1st scenario name is null or empty
        try {
            presenter.addNurseryListFolder(null,studyId);
            fail("should throw an exception if name = null");
        } catch (NurseryListPreviewException e) {
            assertTrue(e.getLocalizedMessage().contains("Folder name cannot be blank"));
        }

        // 2nd scenario name == MY_STUDIES || SHARED_STUDIES
        try {
            presenter.addNurseryListFolder(view.MY_STUDIES,studyId);
            fail("should throw an exception if name = view.MY_STUDIES");
        } catch (NurseryListPreviewException e) {
            assertTrue(e.getLocalizedMessage().contains("Please choose a different name"));

            try {
                presenter.addNurseryListFolder(view.SHARED_STUDIES,studyId);
                assertTrue("should throw an exception if name = view.SHARED_STUDIES",false);
            } catch (NurseryListPreviewException e2) {
                assertTrue(e.getLocalizedMessage().contains("Please choose a different name"));
            }

        }

        // 3rd scenario presenter.isFalder(id) === false
        // assume that studyID is not a folder
        when(studyDataManager.isStudy(studyId)).thenReturn(true);
        when(studyDataManager.getParentFolder(studyId)).thenReturn(dmsProject);
        when(dmsProject.getProjectId()).thenReturn(parentFolderId);
        presenter.addNurseryListFolder(newFolderName,studyId);


        // verify that addSubFolder is called with the correct order of parameters
        verify(studyDataManager).addSubFolder(parentFolderId,newFolderName,newFolderName);
    }

    @Test
    public void testValidateForDeleteNurseryList() throws Exception {
        // if id is null, expect exception
        try {
            presenter.validateForDeleteNurseryList(null);
            fail("Should throw an exception if id is null");
        } catch (NurseryListPreviewException e) {
            assertTrue(e.getLocalizedMessage().contains("Please select a folder item"));
        }

        // assume studyDataManager.getProject() returns a DMSObj (no middleware exception)
        when(studyDataManager.getProject(studyIdWithMultipleChildren)).thenReturn(dmsProject);
        when(studyDataManager.getProject(studyIdWithNoChildren)).thenReturn(dmsProject);

        List<Reference> hasMultipleChildren = mock(ArrayList.class);
        hasMultipleChildren.add(mock(Reference.class));
        hasMultipleChildren.add(mock(Reference.class));
        hasMultipleChildren.add(mock(Reference.class));

        when(studyDataManager.getChildrenOfFolder(studyIdWithMultipleChildren)).thenReturn(hasMultipleChildren);
        when(studyDataManager.getChildrenOfFolder(studyIdWithNoChildren)).thenReturn(new ArrayList<Reference>());

        try {
            presenter.validateForDeleteNurseryList(studyIdWithMultipleChildren);
            fail("Should throw an exception if NurseryListPreviewException.HAS_CHILDREN");
        } catch(NurseryListPreviewException e) {
            assertTrue(e.getLocalizedMessage().contains(NurseryListPreviewException.HAS_CHILDREN));
        }

        assertEquals("Folder has no children, can be deleted",Integer.valueOf(studyIdWithNoChildren),presenter.validateForDeleteNurseryList(studyIdWithNoChildren));
    }

    @Test
    public void testValidateStudyFolderName() throws Exception {
        try {
            presenter.validateStudyFolderName(newFolderName);
        } catch (NurseryListPreviewException e) {
            fail("We should not expect an exception since the input is valid");
        }
    }

    @Test (expected = NurseryListPreviewException.class)
    public void testValidateStudyFolderNameNull() throws Exception {
        presenter.validateStudyFolderName(null);
        fail("We are expecting an exception since the input is NOT valid");
    }

    @Test (expected = NurseryListPreviewException.class)
    public void testValidateStudyFolderNameBlank() throws Exception {
        presenter.validateStudyFolderName("");
        fail("We are expecting an exception since the input is NOT valid");

    }

    @Test (expected = NurseryListPreviewException.class)
    public void testValidateStudyFolderNameInvalidProgramStudies() throws Exception {
        presenter.validateStudyFolderName(NurseryListPreview.MY_STUDIES);
        fail("We are expecting an exception since the input is NOT valid");

    }

    @Test (expected = NurseryListPreviewException.class)
    public void testValidateStudyFolderNameInvalidPublicStudies() throws Exception {
        presenter.validateStudyFolderName(NurseryListPreview.SHARED_STUDIES);
        fail("We are expecting an exception since the input is NOT valid");
    }


    @Test (expected = NurseryListPreviewException.class)
    public void testValidateStudyFolderNameTooLong() throws Exception {
        presenter.validateStudyFolderName(MORE_THAN_255_CHAR_STRING);
        fail("We are expecting an exception since the input is NOT valid");
    }
}
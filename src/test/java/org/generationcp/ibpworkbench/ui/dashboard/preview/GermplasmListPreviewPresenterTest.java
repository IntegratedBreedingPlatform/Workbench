package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmListPreviewPresenterTest {

    public static final String SOME_RANDOM_FOLDER_NAME = "some random folder name";
    public static final String PROGRAM_LISTS = "Program Lists";
    public static final String PUBLIC_LISTS = "Public Lists";

    @Mock
    private GermplasmListPreview view;

    @Mock
    private Project project;

    @Mock
    private GermplasmListManager germplasmListManager;

    @Mock
    private ManagerFactory managerFactory;

    @Mock
    private ManagerFactoryProvider managerFactoryProvider;

    @Mock
    private WorkbenchDataManager manager;

    @Mock
    private SessionData sessionData;

    @Mock
    private User user;

    @Mock
    private SimpleResourceBundleMessageSource messageSource;

    @Mock
    private GermplasmList germplasmListWithParent;

    @Mock
    private GermplasmList parentGermplasmList;

    @Mock
    private GermplasmList notAFolderGermplasmList;

    @Mock
    private GermplasmList isAFolderGermplasmList;

    private int USER_ID = 1;
    private long PROJECT_ID = 1;

    private int LIST_ID_WITH_NO_PARENT = 2,
        LIST_ID_WITH_PARENT = 3,
        LIST_ID_NOT_A_FOLDER = 4,
        LIST_ID_A_FOLDER = 5;

    @InjectMocks
    private GermplasmListPreviewPresenter presenter = new GermplasmListPreviewPresenter();

    @Before
    public void setUp() throws Exception {
        when(view.getManagerFactoryProvider()).thenReturn(managerFactoryProvider);
        when((managerFactoryProvider.getManagerFactoryForProject(project))).thenReturn(managerFactory);
        when(managerFactory.getGermplasmListManager()).thenReturn(germplasmListManager);

        when(sessionData.getUserData()).thenReturn(user);
        when(sessionData.getSelectedProject()).thenReturn(project);
        when(user.getUserid()).thenReturn(USER_ID);
        when(project.getProjectId()).thenReturn(PROJECT_ID);

        // this two conditions setups a successful checkIfUnique() from presenter
        when(germplasmListManager.getGermplasmListByName(SOME_RANDOM_FOLDER_NAME, 0, 1, null, Database.CENTRAL)).thenReturn(null);
        when(germplasmListManager.getGermplasmListByName(SOME_RANDOM_FOLDER_NAME, 0, 1, null, Database.LOCAL)).thenReturn(null);

        when(germplasmListManager.getGermplasmListById(LIST_ID_WITH_NO_PARENT)).thenReturn(null);
        when(germplasmListManager.getGermplasmListById(LIST_ID_WITH_PARENT)).thenReturn(germplasmListWithParent);
        when(germplasmListManager.getGermplasmListById(LIST_ID_A_FOLDER)).thenReturn(isAFolderGermplasmList);
        when(germplasmListManager.getGermplasmListById(LIST_ID_NOT_A_FOLDER)).thenReturn(notAFolderGermplasmList);


        when(manager.getLocalIbdbUserId(sessionData.getUserData().getUserid(), sessionData.getSelectedProject().getProjectId())).thenReturn(USER_ID);
        GermplasmListPreview.MY_LIST = PROGRAM_LISTS;
        GermplasmListPreview.SHARED_LIST =PUBLIC_LISTS;

    }

    @Test
    public void testGetGermplasmListParent() throws Exception {
        presenter.getGermplasmListParent(LIST_ID_WITH_PARENT);
        verify(germplasmListWithParent).getParent();
    }

    @Test
    public void testGetGermplasmListParentFailScenarios() throws Exception {
        when(germplasmListWithParent.getParent()).thenReturn(parentGermplasmList);

        try {
            presenter.getGermplasmListParent(LIST_ID_WITH_NO_PARENT);
            fail("expects a nullpointer since were retriving a null list");
        } catch (GermplasmListPreviewException e) {
            assertTrue(e.getMessage().contains(GermplasmListPreviewException.NO_PARENT));
        }
    }

    @Test
    public void testRenameGermplasmListFolder() throws Exception {
        when(isAFolderGermplasmList.isFolder()).thenReturn(true);

        presenter.renameGermplasmListFolder(SOME_RANDOM_FOLDER_NAME,LIST_ID_A_FOLDER);

        verify(isAFolderGermplasmList).setName(SOME_RANDOM_FOLDER_NAME);
        verify(germplasmListManager).updateGermplasmList(isAFolderGermplasmList);
    }

    @Test
    public void testRenameGermplasmListFolderFailScenarios() throws Exception {
        when(messageSource.getMessage(Message.INVALID_ITEM_NAME)).thenReturn(Message.INVALID_ITEM_NAME.name());

        try {
            presenter.renameGermplasmListFolder(null,LIST_ID_WITH_PARENT);
            fail("should throw exception since we've provided n null folder name");
        } catch (GermplasmListPreviewException e) {
            verify(messageSource).getMessage(Message.INVALID_ITEM_NAME);
        }

        when(notAFolderGermplasmList.isFolder()).thenReturn(false);

        try {
            presenter.renameGermplasmListFolder(SOME_RANDOM_FOLDER_NAME,LIST_ID_NOT_A_FOLDER);
            fail("expects an exception since selected list is not a folder");
        } catch (GermplasmListPreviewException e) {
            assertTrue(e.getMessage().contains(GermplasmListPreviewException.NOT_FOLDER));
        }
    }

    @Test
    public void testAddGermplasmListFolder() throws Exception {
        GermplasmList newListWithParent = new GermplasmList(null, SOME_RANDOM_FOLDER_NAME, Long.valueOf((new SimpleDateFormat(DateUtil.DATE_AS_NUMBER_FORMAT)).format(Calendar.getInstance().getTime())), GermplasmListPreviewPresenter.FOLDER, USER_ID, SOME_RANDOM_FOLDER_NAME, germplasmListWithParent.getParent(), 0);
        newListWithParent.setDescription("(NEW FOLDER) " + SOME_RANDOM_FOLDER_NAME);

        presenter.addGermplasmListFolder(SOME_RANDOM_FOLDER_NAME,LIST_ID_WITH_PARENT);
        verify(germplasmListManager).addGermplasmList(newListWithParent);
    }

    @Test
    public void testAddGermplasmListFolderFailScenarios() throws Exception {
        try {
            presenter.addGermplasmListFolder(null,LIST_ID_NOT_A_FOLDER);
            fail ("should throw an exception since the folder name is null");
        } catch (GermplasmListPreviewException e) {
            assertTrue(e.getMessage().contains(GermplasmListPreviewException.BLANK_NAME));
        }

        try {
            presenter.addGermplasmListFolder(PROGRAM_LISTS,LIST_ID_NOT_A_FOLDER);
            fail ("should throw an exception since the folder name is null");
        } catch (GermplasmListPreviewException e) {
            try {
                presenter.addGermplasmListFolder(PUBLIC_LISTS, LIST_ID_NOT_A_FOLDER);
                fail("something something");

            } catch (GermplasmListPreviewException e2) {
                assertTrue(e.getMessage().contains(GermplasmListPreviewException.INVALID_NAME));
                assertTrue(e2.getMessage().contains(GermplasmListPreviewException.INVALID_NAME));
            }
        }
    }

    @Test
    public void testDeleteGermplsmListFolder() throws Exception {
        presenter.deleteGermplasmListFolder(isAFolderGermplasmList);
        verify(germplasmListManager).deleteGermplasmList(isAFolderGermplasmList);
    }

    @Test
    public void testDropGermplasmListToParent() throws Exception {
        presenter.dropGermplasmListToParent(LIST_ID_NOT_A_FOLDER,LIST_ID_A_FOLDER);
        verify(germplasmListManager).updateGermplasmList(notAFolderGermplasmList);
    }
}

package org.generationcp.ibpworkbench.ui.dashboard.preview;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmListPreviewPresenterTest {

	public static final String SAMPLE_VALID_FOLDER_NAME = "some random folder name";
	public static final String LISTS = "Lists";
	public static final String TOO_LONG_GERMPLASM_LIST_FOLDER_NAME = "50 character sentence is not accepted as valid input here.";

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

	private final int USER_ID = 1;
	private final long PROJECT_ID = 1;

	private final int LIST_ID_WITH_NO_PARENT = 2, LIST_ID_WITH_PARENT = 3, LIST_ID_NOT_A_FOLDER = 4, LIST_ID_A_FOLDER = 5;

	private static final String PROGRAM_UUID = "1234567";

	@InjectMocks
	private final GermplasmListPreviewPresenter presenter = new GermplasmListPreviewPresenter();

	@Before
	public void setUp() throws Exception {
		Mockito.when(this.managerFactoryProvider.getManagerFactoryForProject(this.project)).thenReturn(this.managerFactory);
		Mockito.when(this.managerFactory.getGermplasmListManager()).thenReturn(this.germplasmListManager);

		Mockito.when(this.sessionData.getUserData()).thenReturn(this.user);
		Mockito.when(this.sessionData.getSelectedProject()).thenReturn(this.project);
		Mockito.when(this.user.getUserid()).thenReturn(this.USER_ID);
		Mockito.when(this.project.getProjectId()).thenReturn(this.PROJECT_ID);
		Mockito.when(this.project.getUniqueID()).thenReturn(this.PROGRAM_UUID);

		// this two conditions setups a successful checkIfUnique() from presenter
		Mockito.when(this.germplasmListManager.getGermplasmListByName(GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME,
				PROGRAM_UUID, 0, 1, null))
		.thenReturn(null);

		Mockito.when(this.germplasmListManager.getGermplasmListById(this.LIST_ID_WITH_NO_PARENT)).thenReturn(null);
		Mockito.when(this.germplasmListManager.getGermplasmListById(this.LIST_ID_WITH_PARENT)).thenReturn(this.germplasmListWithParent);
		Mockito.when(this.germplasmListManager.getGermplasmListById(this.LIST_ID_A_FOLDER)).thenReturn(this.isAFolderGermplasmList);
		Mockito.when(this.germplasmListManager.getGermplasmListById(this.LIST_ID_NOT_A_FOLDER)).thenReturn(this.notAFolderGermplasmList);

		Mockito.when(
				this.manager.getLocalIbdbUserId(this.sessionData.getUserData().getUserid(), this.sessionData.getSelectedProject()
						.getProjectId())).thenReturn(this.USER_ID);
		view.setListLabel(GermplasmListPreviewPresenterTest.LISTS);
	}

	@Test
	public void testGetGermplasmListParent() throws Exception {
		this.presenter.getGermplasmListParent(this.LIST_ID_WITH_PARENT);
		Mockito.verify(this.germplasmListWithParent).getParent();
	}

	@Test
	public void testGetGermplasmListParentFailScenarios() throws Exception {
		Mockito.when(this.germplasmListWithParent.getParent()).thenReturn(this.parentGermplasmList);

		try {
			this.presenter.getGermplasmListParent(this.LIST_ID_WITH_NO_PARENT);
			Assert.fail("expects a nullpointer since were retriving a null list");
		} catch (GermplasmListPreviewException e) {
			Assert.assertTrue(e.getMessage().contains(GermplasmListPreviewException.NO_PARENT));
		}
	}

	@Test
	public void testRenameGermplasmListFolder() throws Exception {
		Mockito.when(this.isAFolderGermplasmList.isFolder()).thenReturn(true);

		this.presenter.renameGermplasmListFolder(GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME, this.LIST_ID_A_FOLDER);

		Mockito.verify(this.isAFolderGermplasmList).setName(GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME);
		Mockito.verify(this.germplasmListManager).updateGermplasmList(this.isAFolderGermplasmList);
	}

	@Test
	public void testRenameGermplasmListFolderFailScenarios() throws Exception {
		Mockito.when(this.messageSource.getMessage(Message.INVALID_ITEM_NAME)).thenReturn(Message.INVALID_ITEM_NAME.name());

		try {
			this.presenter.renameGermplasmListFolder(null, this.LIST_ID_WITH_PARENT);
			Assert.fail("should throw exception since we've provided n null folder name");
		} catch (GermplasmListPreviewException e) {
			Assert.assertTrue(e.getMessage().contains(GermplasmListPreviewException.BLANK_NAME));
		}

		Mockito.when(this.notAFolderGermplasmList.isFolder()).thenReturn(false);

		try {
			this.presenter.renameGermplasmListFolder(GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME, this.LIST_ID_NOT_A_FOLDER);
			Assert.fail("expects an exception since selected list is not a folder");
		} catch (GermplasmListPreviewException e) {
			Assert.assertTrue(e.getMessage().contains(GermplasmListPreviewException.NOT_FOLDER));
		}
	}

	@Test
	public void testAddGermplasmListFolder() throws Exception {
		GermplasmList newListWithParent =
				new GermplasmList(null, GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME, DateUtil.getCurrentDateAsLongValue(),
						GermplasmListPreviewPresenter.FOLDER, this.USER_ID, GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME,
						this.germplasmListWithParent.getParent(), 0);
		newListWithParent.setDescription(GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME);
		newListWithParent.setProgramUUID(PROGRAM_UUID);
		this.presenter.addGermplasmListFolder(GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME, this.LIST_ID_WITH_PARENT);

		ArgumentCaptor<GermplasmList> argumentCaptor = ArgumentCaptor.forClass(GermplasmList.class);
		Mockito.verify(this.germplasmListManager).addGermplasmList(argumentCaptor.capture());
		Assert.assertEquals("Folder must be saved with the name supplied.", GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME,
				argumentCaptor.getValue().getDescription());
	}

	@Test
	public void testAddGermplasmListFolderFailScenarios() throws Exception {
		try {
			this.presenter.addGermplasmListFolder(null, this.LIST_ID_NOT_A_FOLDER);
			Assert.fail("should throw an exception since the folder name is null");
		} catch (GermplasmListPreviewException e) {
			Assert.assertTrue(e.getMessage().contains(GermplasmListPreviewException.BLANK_NAME));
		}
	}

	@Test
	public void testDeleteGermplsmListFolder() throws Exception {
		this.presenter.deleteGermplasmListFolder(this.isAFolderGermplasmList);
		Mockito.verify(this.germplasmListManager).deleteGermplasmList(this.isAFolderGermplasmList);
	}

	@Test
	public void testDropGermplasmListToParent() throws Exception {
		this.presenter.dropGermplasmListToParent(this.LIST_ID_NOT_A_FOLDER, this.LIST_ID_A_FOLDER);
		Mockito.verify(this.germplasmListManager).updateGermplasmList(this.notAFolderGermplasmList);
	}

	@Test
	public void testValidateGermplasmListFolderName() throws Exception {
		try {
			this.presenter.validateGermplasmListFolderName(GermplasmListPreviewPresenterTest.SAMPLE_VALID_FOLDER_NAME);
		} catch (GermplasmListPreviewException e) {
			Assert.fail("should not throw an exception as this should be valid input");
		}
	}

	@Test(expected = GermplasmListPreviewException.class)
	public void testValidateGermplasmListFolderNameBlank() throws Exception {
		this.presenter.validateGermplasmListFolderName("");
		Assert.fail("should throw an exception as this is an invalid input");
	}

	@Test(expected = GermplasmListPreviewException.class)
	public void testValidateGermplasmListFolderNameNull() throws Exception {
		this.presenter.validateGermplasmListFolderName(null);
		Assert.fail("should throw an exception as this is an invalid input");

	}

	@Test(expected = GermplasmListPreviewException.class)
	public void testValidateGermplasmListFolderNameInvalidNameMyList() throws Exception {
		this.presenter.validateGermplasmListFolderName(view.getListLabel());
		Assert.fail("should throw an exception as this is an invalid input");
	}

	@Test(expected = GermplasmListPreviewException.class)
	public void testValidateGermplasmListFolderNameNotUnique() throws Exception {
		this.presenter.validateGermplasmListFolderName("");
		Assert.fail("should throw an exception as this is an invalid input");
	}

	@Test(expected = GermplasmListPreviewException.class)
	public void testValidateGermplasmListFolderNameTooLong() throws Exception {
		this.presenter.validateGermplasmListFolderName(GermplasmListPreviewPresenterTest.TOO_LONG_GERMPLASM_LIST_FOLDER_NAME);
		Assert.fail("should throw an exception as this is an invalid input");
	}
}

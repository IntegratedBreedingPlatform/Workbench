
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.vaadin.ui.Tree;

import org.apache.commons.lang.reflect.FieldUtils;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NurseryListPreviewTest {

	private static final String NURSERIES_AND_TRIALS = "Nurseries and Trials";

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private ManagerFactoryProvider managerFactoryProvider;

	@Mock
	private ManagerFactory managerFactory;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private NurseryListPreview view;

	@Before
	public void setUp() throws Exception {
		NurseryListPreview.NURSERIES_AND_TRIALS = NurseryListPreviewTest.NURSERIES_AND_TRIALS;
		Project project = NurseryListPreviewTest.createTestProjectData();

		try {
			Mockito.when(this.studyDataManager.getRootFolders(project.getUniqueID())).thenReturn(this.createTopLevelFolderReferences(0));
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
		Mockito.when(this.managerFactoryProvider.getManagerFactoryForProject(project)).thenReturn(this.managerFactory);
		Mockito.when(this.managerFactory.getStudyDataManager()).thenReturn(this.studyDataManager);
		Mockito.when(this.messageSource.getMessage(Message.NURSERIES_AND_TRIALS)).thenReturn(NurseryListPreviewTest.NURSERIES_AND_TRIALS);

		this.view = new NurseryListPreview(project);
		this.view.setMessageSource(this.messageSource);
		NurseryListPreviewPresenter presenter = this.view.getPresenter();
		FieldUtils.writeDeclaredField(presenter,"studyDataManager",studyDataManager,true);
		this.view.setProject(project);
	}

	public static Project createTestProjectData() {
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
	public void testGenerateTopListOfTree_NoChildren() {
		try {
			String rootFolder = NurseryListPreview.NURSERIES_AND_TRIALS;

			List<Reference> items = this.createTopLevelFolderReferences(0);
			this.view.generateTopListOfTree(items);
			Tree tree = this.view.getTreeView();
			Assert.assertEquals("Root folder is " + rootFolder, tree.getItemIds().iterator().next(), rootFolder);
			Assert.assertNull("Root folder should not have children", tree.getChildren(rootFolder));

		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testGenerateTopListOfTree_WithChildren() {
		try {
			String rootFolder = NurseryListPreview.NURSERIES_AND_TRIALS;

			List<Reference> items = this.createTopLevelFolderReferences(2);
			this.view.generateTopListOfTree(items);
			Tree tree = this.view.getTreeView();
			Assert.assertEquals("Root folder is " + rootFolder, tree.getItemIds().iterator().next(), rootFolder);
			Assert.assertNotNull("Root folder should have children", tree.getChildren(rootFolder));
			Assert.assertEquals("Root folder should have 2 children", 2, tree.getChildren(rootFolder).size());

		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	private List<Reference> createTopLevelFolderReferences(int numberOfItems) {
		List<Reference> items = new ArrayList<Reference>();
		for (int i = 1; i <= numberOfItems; i++) {
			FolderReference folderReference =
					new FolderReference(NurseryListPreview.ROOT_FOLDER, i, "Test Name " + i, "Test Description " + i);
			Mockito.when(this.studyDataManager.isStudy(i)).thenReturn(false);
			items.add(folderReference);
		}
		return items;
	}

	@Test
	public void testProcessToolbarButtons_Studies() {
		this.view.processToolbarButtons(NurseryListPreview.NURSERIES_AND_TRIALS);
		Assert.assertTrue("Add button should be enabled", this.view.getAddFolderBtn().isEnabled());
		Assert.assertFalse("Rename button should not be enabled", this.view.getRenameFolderBtn().isEnabled());
		Assert.assertFalse("Delete button should not be enabled", this.view.getDeleteFolderBtn().isEnabled());
		Assert.assertFalse("Launch button should not be enabled", this.view.getOpenStudyManagerBtn().isEnabled());
	}

	@Test
	public void testProcessToolbarButtons_Folder() {
		Integer folderId = new Random().nextInt(100);
		try {
			Mockito.when(this.studyDataManager.isStudy(folderId)).thenReturn(false);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
		this.view.processToolbarButtons(folderId);

		Assert.assertTrue("Add button should be enabled", this.view.getAddFolderBtn().isEnabled());
		Assert.assertTrue("Rename button should be enabled", this.view.getRenameFolderBtn().isEnabled());
		Assert.assertTrue("Delete button should be enabled", this.view.getDeleteFolderBtn().isEnabled());
		Assert.assertFalse("Launch button should not be enabled", this.view.getOpenStudyManagerBtn().isEnabled());
	}

	@Test
	public void testProcessToolbarButtons_Study() {
		Integer folderId = new Random().nextInt(100);
		try {
			Mockito.when(this.studyDataManager.isStudy(folderId)).thenReturn(true);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
		this.view.processToolbarButtons(folderId);

		Assert.assertTrue("Add button should be enabled", this.view.getAddFolderBtn().isEnabled());
		Assert.assertTrue("Rename button should be enabled", this.view.getRenameFolderBtn().isEnabled());
		Assert.assertTrue("Delete button should be enabled", this.view.getDeleteFolderBtn().isEnabled());
		Assert.assertTrue("Launch button should be enabled", this.view.getOpenStudyManagerBtn().isEnabled());
	}

}

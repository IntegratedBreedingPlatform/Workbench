
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NurseryTreeDropHandlerTest {

	private static final String NURSERIES_AND_TRIALS = "Nurseries and Trials";

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private ManagerFactoryProvider managerFactoryProvider;

	@Mock
	private ManagerFactory managerFactory;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private NurseryListPreviewPresenter presenter;

	private NurseryListPreview view;

	private NurseryTreeDropHandler dropHandler;

	private List<FolderReference> rootFolderChildren;

	@Before
	public void setUp() throws Exception {
		NurseryListPreview.NURSERIES_AND_TRIALS = NurseryTreeDropHandlerTest.NURSERIES_AND_TRIALS;
		Project project = NurseryTreeDropHandlerTest.createTestProjectData();

		this.presenter = Mockito.mock(NurseryListPreviewPresenter.class);

		Mockito.when(this.managerFactoryProvider.getManagerFactoryForProject(project)).thenReturn(this.managerFactory);
		Mockito.when(this.managerFactory.getStudyDataManager()).thenReturn(this.studyDataManager);

		try {
			this.rootFolderChildren = this.createTopLevelFolderReferences(2);
			Mockito.when(this.studyDataManager.getRootFolders(project.getUniqueID())).thenReturn(this.rootFolderChildren);
			Mockito.when(this.messageSource.getMessage(Message.NURSERIES_AND_TRIALS)).thenReturn(
					NurseryTreeDropHandlerTest.NURSERIES_AND_TRIALS);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}

		this.view = new NurseryListPreview(project);

		// since presenter that initializes some object in the view (NurseryListPreview) is now a mock obj not a full spied obj,
		// we need manually initially initialize treeView to the view
		this.view.generateTopListOfTree(new ArrayList<FolderReference>());

		this.view.setMessageSource(this.messageSource);
		this.view.setPresenter(this.presenter);
		this.view.setProject(project);

		try {
			Mockito.when(this.presenter.moveNurseryListFolder(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyBoolean())).thenReturn(false);
		} catch (NurseryListPreviewException e) {
			Assert.fail(e.getMessage());
		}

		this.dropHandler = Mockito.spy(new NurseryTreeDropHandler(this.view.getTreeView(), this.presenter));
		this.dropHandler.setMessageSource(this.messageSource);
		Mockito.doNothing().when(this.dropHandler).showError(Matchers.anyString(), Matchers.anyString());

		Mockito.when(this.messageSource.getMessage(Message.INVALID_OPERATION)).thenReturn(Message.INVALID_OPERATION.toString());
		Mockito.when(this.messageSource.getMessage(Message.UNABLE_TO_MOVE_ROOT_FOLDERS)).thenReturn(
				Message.UNABLE_TO_MOVE_ROOT_FOLDERS.toString());
		Mockito.when(this.messageSource.getMessage(Message.INVALID_OPERATION)).thenReturn(
				Message.INVALID_CANNOT_MOVE_ITEM_WITH_CHILD.toString());
		Mockito.when(this.messageSource.getMessage(Message.ERROR)).thenReturn(Message.ERROR.toString());
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

	private List<FolderReference> createTopLevelFolderReferences(int numberOfItems) throws MiddlewareQueryException {
		List<FolderReference> items = new ArrayList<FolderReference>();
		for (int i = 1; i <= numberOfItems; i++) {
			FolderReference folderReference =
					new FolderReference(NurseryListPreview.ROOT_FOLDER, i, "Test Name " + i, "Test Description " + i);
			boolean isStudy = false;
			if (i % 2 == 0) {
				isStudy = true;
			}
			Mockito.when(this.studyDataManager.isStudy(i)).thenReturn(isStudy);
			items.add(folderReference);
		}
		return items;
	}

	@Test
	public void testMoveNode_SameSourceAndTarget() {
		Integer source = 1;
		Integer target = 1;
		VerticalDropLocation location = VerticalDropLocation.MIDDLE;
		this.dropHandler.moveNode(source, target, location);
		try {
			Mockito.verify(this.presenter, Mockito.never()).moveNurseryListFolder(source, target, false);
		} catch (NurseryListPreviewException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testMoveNode_RootAsSource() {
		Integer source = 1;
		Integer target = 1;
		VerticalDropLocation location = VerticalDropLocation.MIDDLE;
		this.dropHandler.moveNode(NurseryTreeDropHandlerTest.NURSERIES_AND_TRIALS, target, location);
		try {
			Mockito.verify(this.presenter, Mockito.never()).moveNurseryListFolder(source, target, false);
		} catch (NurseryListPreviewException e) {
			Assert.fail(e.getMessage());
		}
	}

}

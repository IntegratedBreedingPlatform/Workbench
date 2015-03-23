package org.generationcp.ibpworkbench.ui.dashboard.preview;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Tree;

@RunWith(MockitoJUnitRunner.class)
public class NurseryListPreviewTest  {
	
	
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
    public void setUp() {
    	NurseryListPreview.NURSERIES_AND_TRIALS = NURSERIES_AND_TRIALS;
    	Project project = createTestProjectData();
    	
    	try {
        	when(studyDataManager.getRootFolders(project.getUniqueID())).
        		thenReturn(createTopLevelFolderReferences(0));
		} catch (MiddlewareQueryException e) {
			fail(e.getMessage());
		}
    	when(managerFactoryProvider.getManagerFactoryForProject(project)).thenReturn(managerFactory);
        when(managerFactory.getStudyDataManager()).thenReturn(studyDataManager);
        when(messageSource.getMessage(Message.NURSERIES_AND_TRIALS)).thenReturn(NURSERIES_AND_TRIALS);
        
        view = new NurseryListPreview(project);
    	view.setManagerFactoryProvider(managerFactoryProvider);
    	view.setMessageSource(messageSource);
    	NurseryListPreviewPresenter presenter = view.getPresenter();
    	presenter.setManagerFactory(managerFactory);
    	view.setProject(project);
    	
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
        	
			List<FolderReference> items = createTopLevelFolderReferences(0);
			view.generateTopListOfTree(items);
	    	Tree tree = view.getTreeView();
	    	assertEquals("Root folder is "+rootFolder,tree.getItemIds().iterator().next(),
	    			rootFolder);
	    	assertNull("Root folder should not have children",tree.getChildren(rootFolder));
	    	
		} catch (MiddlewareQueryException e) {
			fail(e.getMessage());
		}
    	
    	
    	
    }
    
    @Test
    public void testGenerateTopListOfTree_WithChildren() {
    	try {
    		String rootFolder = NurseryListPreview.NURSERIES_AND_TRIALS;
        	
			List<FolderReference> items = createTopLevelFolderReferences(2);
	    	view.generateTopListOfTree(items);
	    	Tree tree = view.getTreeView();
	    	assertEquals("Root folder is "+rootFolder,tree.getItemIds().iterator().next(),
	    			rootFolder);
	    	assertNotNull("Root folder should have children",tree.getChildren(rootFolder));
	    	assertEquals("Root folder should have 2 children",2,tree.getChildren(rootFolder).size());
	    	
		} catch (MiddlewareQueryException e) {
			fail(e.getMessage());
		}
    }

	private List<FolderReference> createTopLevelFolderReferences(int numberOfItems) 
			throws MiddlewareQueryException {
		List<FolderReference> items = new ArrayList<FolderReference>();
		for(int i=1;i<=numberOfItems;i++) {
			FolderReference folderReference = 
					new FolderReference(NurseryListPreview.ROOT_FOLDER,
							i,"Test Name "+i,"Test Description "+i);
			when(studyDataManager.isStudy(i)).thenReturn(false);
			items.add(folderReference);
		}
		return items;
	}
	
	@Test
	public void testProcessToolbarButtons_Studies() {
		view.processToolbarButtons(NurseryListPreview.NURSERIES_AND_TRIALS);
		assertTrue("Add button should be enabled",view.getAddFolderBtn().isEnabled());
		assertFalse("Rename button should not be enabled",view.getRenameFolderBtn().isEnabled());
		assertFalse("Delete button should not be enabled",view.getDeleteFolderBtn().isEnabled());
		assertFalse("Launch button should not be enabled",view.getOpenStudyManagerBtn().isEnabled());
	}
	
	@Test
	public void testProcessToolbarButtons_Folder() {
		Integer folderId = new Random().nextInt(100);
		try {
			when(studyDataManager.isStudy(folderId)).thenReturn(false);
		} catch (MiddlewareQueryException e) {
			fail(e.getMessage());
		}
		view.processToolbarButtons(folderId);
		
		assertTrue("Add button should be enabled",view.getAddFolderBtn().isEnabled());
		assertTrue("Rename button should be enabled",view.getRenameFolderBtn().isEnabled());
		assertTrue("Delete button should be enabled",view.getDeleteFolderBtn().isEnabled());	
		assertFalse("Launch button should not be enabled",view.getOpenStudyManagerBtn().isEnabled());	
	}
	
	@Test
	public void testProcessToolbarButtons_Study() {
		Integer folderId = new Random().nextInt(100);
		try {
			when(studyDataManager.isStudy(folderId)).thenReturn(true);
		} catch (MiddlewareQueryException e) {
			fail(e.getMessage());
		}
		view.processToolbarButtons(folderId);
		
		assertTrue("Add button should be enabled",view.getAddFolderBtn().isEnabled());
		assertTrue("Rename button should be enabled",view.getRenameFolderBtn().isEnabled());
		assertTrue("Delete button should be enabled",view.getDeleteFolderBtn().isEnabled());	
		assertTrue("Launch button should be enabled",view.getOpenStudyManagerBtn().isEnabled());	
	}

    
}
package org.generationcp.ibpworkbench.ui.dashboard.preview;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;

@RunWith(MockitoJUnitRunner.class)
public class NurseryTreeDropHandlerTest  {
	
	private static final String NURSERIES_AND_TRIALS = "Nurseries and Trials";

	@Mock
    private StudyDataManager studyDataManager;
	
	@Mock
    private ManagerFactoryProvider managerFactoryProvider;
	
	@Mock
    private ManagerFactory managerFactory;
	
	@Mock
    private SimpleResourceBundleMessageSource messageSource;
	
	private NurseryTreeDropHandler dropHandler;
	private NurseryListPreviewPresenter presenter;
	private List<FolderReference> rootFolderChildren;
    
    @Before
    public void setUp() {
    	NurseryListPreview.NURSERIES_AND_TRIALS = NURSERIES_AND_TRIALS;
    	Project project = createTestProjectData();
    	
    	when(managerFactoryProvider.getManagerFactoryForProject(project)).thenReturn(managerFactory);
        when(managerFactory.getStudyDataManager()).thenReturn(studyDataManager);
        when(messageSource.getMessage(Message.NURSERIES_AND_TRIALS)).thenReturn(NURSERIES_AND_TRIALS);
        
        try {
        	rootFolderChildren = createTopLevelFolderReferences(2);
            when(studyDataManager.getRootFolders(project.getUniqueID())).thenReturn(rootFolderChildren);
		} catch (MiddlewareQueryException e) {
			fail(e.getMessage());
		}
        
    	NurseryListPreview view = new NurseryListPreview(project);
    	view.setManagerFactoryProvider(managerFactoryProvider);
    	view.setMessageSource(messageSource);
    	NurseryListPreviewPresenter presenter = view.getPresenter();
    	presenter.setManagerFactory(managerFactory);
    	view.setProject(project);
    	
    	try {
			when(presenter.moveNurseryListFolder(anyInt(),anyInt(),anyBoolean()));
		} catch (NurseryListPreviewException e) {
			fail(e.getMessage());
		}
    	
        dropHandler = new NurseryTreeDropHandler(view.getTreeView(), presenter);
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

	private List<FolderReference> createTopLevelFolderReferences(int numberOfItems) 
			throws MiddlewareQueryException {
		List<FolderReference> items = new ArrayList<FolderReference>();
		for(int i=1;i<=numberOfItems;i++) {
			FolderReference folderReference = 
					new FolderReference(NurseryListPreview.ROOT_FOLDER,
							i,"Test Name "+i,"Test Description "+i);
			boolean isStudy = false;
			if(i%2==0) { 
				isStudy = true;
			}
			when(studyDataManager.isStudy(i)).thenReturn(isStudy);
			items.add(folderReference);
		}
		return items;
	}
	
	@Test
	public void testMoveNode() {
		Object source = 1;
		Object target = 1;
		VerticalDropLocation location = VerticalDropLocation.MIDDLE;
		dropHandler.moveNode(null,null,location);
		try {
			verify(presenter, never()).moveNurseryListFolder((Integer)source,(Integer)target,false);
		} catch (NurseryListPreviewException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
    
}
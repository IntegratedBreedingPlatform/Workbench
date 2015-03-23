package org.generationcp.ibpworkbench.ui.dashboard.preview;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
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
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.workbench.Project;
import org.hamcrest.core.AnyOf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.data.util.HierarchicalContainer;
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
	
	@Mock
	private NurseryListPreviewPresenter presenter;
	
	private NurseryListPreview view;
	
	private NurseryTreeDropHandler dropHandler;
	
	private List<FolderReference> rootFolderChildren;
    
    @Before
    public void setUp() {
    	NurseryListPreview.NURSERIES_AND_TRIALS = NURSERIES_AND_TRIALS;
    	Project project = createTestProjectData();
    	
    	when(managerFactoryProvider.getManagerFactoryForProject(project)).thenReturn(managerFactory);
        when(managerFactory.getStudyDataManager()).thenReturn(studyDataManager);
        
        try {
        	rootFolderChildren = createTopLevelFolderReferences(2);
            when(studyDataManager.getRootFolders(project.getUniqueID())).thenReturn(rootFolderChildren);
            when(messageSource.getMessage(Message.NURSERIES_AND_TRIALS)).thenReturn(NURSERIES_AND_TRIALS);
		} catch (MiddlewareQueryException e) {
			fail(e.getMessage());
		}
        
    	view = new NurseryListPreview(project);
    	view.setManagerFactoryProvider(managerFactoryProvider);
    	view.setMessageSource(messageSource);
    	view.setPresenter(presenter);
    	presenter.setManagerFactory(managerFactory);
    	view.setProject(project);
    	
    	try {
			doReturn(false).when(presenter).moveNurseryListFolder(anyInt(),anyInt(),anyBoolean());
		} catch (NurseryListPreviewException e) {
			fail(e.getMessage());
		}
    	
        dropHandler = spy(new NurseryTreeDropHandler(view.getTreeView(), presenter));
        dropHandler.setMessageSource(messageSource);
        doNothing().when(dropHandler).showError(anyString(), anyString());
        
        when(messageSource.getMessage(Message.INVALID_OPERATION)).
        	thenReturn(Message.INVALID_OPERATION.toString());
        when(messageSource.getMessage(Message.UNABLE_TO_MOVE_ROOT_FOLDERS)).
    		thenReturn(Message.UNABLE_TO_MOVE_ROOT_FOLDERS.toString());
        when(messageSource.getMessage(Message.INVALID_OPERATION)).
    		thenReturn(Message.INVALID_CANNOT_MOVE_ITEM_WITH_CHILD.toString());
        when(messageSource.getMessage(Message.ERROR)).
    		thenReturn(Message.ERROR.toString());
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
	public void testMoveNode_SameSourceAndTarget() {
		Integer source = 1;
		Integer target = 1;
		VerticalDropLocation location = VerticalDropLocation.MIDDLE;
		dropHandler.moveNode(source,target,location);
		try {
			verify(presenter, never()).moveNurseryListFolder((Integer)source,(Integer)target,false);
		} catch (NurseryListPreviewException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMoveNode_RootAsSource() {
		Integer source = 1;
		Integer target = 1;
		VerticalDropLocation location = VerticalDropLocation.MIDDLE;
		dropHandler.moveNode(NURSERIES_AND_TRIALS,target,location);
		try {
			verify(presenter, never()).moveNurseryListFolder(source,target,false);
		} catch (NurseryListPreviewException e) {
			fail(e.getMessage());
		}
	}
    
}
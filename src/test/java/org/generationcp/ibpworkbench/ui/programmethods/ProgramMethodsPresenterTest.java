package org.generationcp.ibpworkbench.ui.programmethods;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProgramMethodsPresenterTest {

    private static final int NO_OF_METHODS = 5;
    private static final int NO_OF_METHODS_WITH_PROGRAM_UUID = 3;
    
	@Mock
    private ManagerFactoryProvider managerFactoryProvider;

    @Mock
    private WorkbenchDataManager workbenchDataManager;

    @Mock
    private SessionData sessionData;

    @Mock
    private GermplasmDataManager gerplasmDataManager;
    
    @Mock
    private ProgramMethodsView programMethodsView;
    
    private static final String DUMMY_PROGRAM_UUID = "1234567890";
	private static final Integer NO_OF_FAVORITES = 2;
    
    
    private ProgramMethodsPresenter controller;
    
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		Project project = getProject(DUMMY_PROGRAM_UUID);
		controller = spy(new ProgramMethodsPresenter(programMethodsView, project));
		controller.setGerplasmDataManager(gerplasmDataManager);
	}

	private Project getProject(String dummyProgramUuid) {
		Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName("Project Name");
		project.setUniqueID(DUMMY_PROGRAM_UUID);
		return project;
	}
	
	@Test
	public void testGetFilteredResults(){
		String mgroup = "C";
		String mtype = "GEN";
		String mname = "Method Name";
		
		Collection<MethodView> result = null;
		try {
			setupGetFilteredResults(mgroup,mtype,mname,DUMMY_PROGRAM_UUID);
			result = controller.getFilteredResults(mgroup, mtype, mname); 
		} catch (MiddlewareQueryException e) {
			Assert.fail();
		}
		
		Integer expectedNoOfResults = NO_OF_METHODS - 1;
        Assert.assertTrue("Expecting the results returned " + expectedNoOfResults + " but returned " + result.size(), expectedNoOfResults.equals(result.size()));

	}
	
	@Test
	public void testGetSavedProgramMethods(){
		String entityType = "C";
    	List<MethodView> results = new ArrayList<MethodView>();
		String mgroup = "C";
		String mtype = "GEN";
		String mname = "Method Name";
        
    	try {
    		setupGetFilteredResults(mgroup, mtype, mname, DUMMY_PROGRAM_UUID);
			setUpFavoriteMethods(entityType);
			results = controller.getSavedProgramMethods();
		} catch (MiddlewareQueryException e) {
			Assert.fail();
		}

    	Assert.assertTrue("Expecting to return " + NO_OF_FAVORITES + " but returned " + results.size(), NO_OF_FAVORITES == results.size() );
	}
	
	private void setUpFavoriteMethods(String entityType) throws MiddlewareQueryException {
		List<ProgramFavorite> favorites = new ArrayList<ProgramFavorite>();
		
		for(int i = 0; i < NO_OF_FAVORITES; i++){
			Integer methodId = i + 1;
			ProgramFavorite favorite = new ProgramFavorite();
			favorite.setEntityId(methodId);
			favorite.setEntityType(entityType);
			favorite.setUniqueID(DUMMY_PROGRAM_UUID);	
			
			favorites.add(favorite);
		}
		
		when(gerplasmDataManager.getProgramFavorites(FavoriteType.METHOD, DUMMY_PROGRAM_UUID)).thenReturn(favorites);
		
	}

	public void setupGetFilteredResults(String mgroup, String mtype, String mname, String programUUID) throws MiddlewareQueryException{
		mgroup = (mgroup != null) ? mgroup : "";
		mtype = (mtype != null) ? mtype : "";
		mname = (mname != null) ? mname : "";
		
		List<Method> methods = new ArrayList<Method>();
		
		for(int i = 0; i < NO_OF_METHODS; i++){
			Integer methodId = i + 1;
			Method method = new Method();
			method.setMid(methodId);
			method.setMgrp(mgroup);
			method.setMtype(mtype);
			method.setMname(mname + " " + methodId);
			
			methods.add(method);
			
			when(gerplasmDataManager.getMethodByID(methodId)).thenReturn(method);
		}
		
		for(int i = 0; i < NO_OF_METHODS_WITH_PROGRAM_UUID; i++){
			Method method = methods.get(i);
			method.setUniqueID(DUMMY_PROGRAM_UUID);
		}
		
		Method method = methods.get(NO_OF_METHODS_WITH_PROGRAM_UUID);
		method.setUniqueID("9876543210");
		
		when(gerplasmDataManager.getMethodsByGroupAndTypeAndName(mgroup, mtype, mname)).thenReturn(methods);
	}
}

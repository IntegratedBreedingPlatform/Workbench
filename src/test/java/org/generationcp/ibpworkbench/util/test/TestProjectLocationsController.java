package org.generationcp.ibpworkbench.util.test;

import org.generationcp.commons.hibernate.DefaultManagerFactoryProvider;
import org.generationcp.ibpworkbench.ui.projectlocations.LocationTableViewModel;
import org.generationcp.ibpworkbench.ui.projectlocations.ProjectLocationsController;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestProjectLocationsController {

	private static ProjectLocationsController controller;
	
    private static WorkbenchDataManager workbenchDataManager;

	private static DefaultManagerFactoryProvider managerFactoryProvider;

	private static HibernateUtil hibernateUtil;
	
    // NOTE: please setup VM argument on run+debug configurations on JUNIT just like when we setup a new server instance
    // for the spring load weaving to work properly
    // -javaagent:"${env_var:HOMEDRIVE}${env_var:HOMEPATH}\.m2\repository\org\springframework\spring-instrument\3.1.1.RELEASE\spring-instrument-3.1.1.RELEASE.jar"
   
	@BeforeClass
    public static void setUp() throws Exception {
		
		//Lest test this on the first project the workbench manager gets
		DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("workbench.properties", "workbench");
        hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
                               workbenchDb.getUsername(), workbenchDb.getPassword());
        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
        workbenchDataManager = new WorkbenchDataManagerImpl(sessionProvider);
			
        Assert.assertNotNull("Manager is null, spring did not load the bean",workbenchDataManager);
        
		User u = null;
		Role r = null;
		for (Project p : workbenchDataManager.getProjects() ) {
			
			try {
				u = workbenchDataManager.getUserById(p.getUserId());

				Assert.assertNotNull(u);
					
			} catch (Exception e) {
				continue;
			}
					
			
			if (workbenchDataManager.getRolesByProjectAndUser(p,u).size() <= 0)
				continue;
			
			r = workbenchDataManager.getRolesByProjectAndUser(p,u).iterator().next();	
		
			Assert.assertNotNull(r);
		
			managerFactoryProvider = new DefaultManagerFactoryProvider();
			
	        controller = new ProjectLocationsController(p,workbenchDataManager,managerFactoryProvider);
	        Assert.assertNotNull(controller);

	        break;
		}
    }
	
	@Test
	public void testGetFilteredResults() {
		Assert.assertNotNull(workbenchDataManager);
		Assert.assertNotNull(controller);
		
		
		try {
			UserDefinedField ltype = controller.getLocationTypeList().iterator().next();
			Country c = null;
			
			// just get the country that starts with an alphabeth, this most likely get Afganistan
			
			
			for (Country i : controller.getCountryList()) {
				if (Character.isLetter(i.getIsoabbr().charAt(0))) {
					c = i;
					break;
				}

			}
			
			Assert.assertNotNull(c);
			
			// TEST 1 GET FILTERED RESULTS WITH NULL COUNTRY and NULL NAME 
			System.out.println("testGetFilteredResults(): TEST 1 GET FILTERED RESULTS WITH NULL COUNTRY and NULL NAME ");
			
			
			for (LocationTableViewModel item : controller.getFilteredResults(null, ltype.getFldno(),null))
				System.out.println("> RESULT= " + item);
			
			// TEST 2 GET FILTERED RESULTS WITH COUNTRY and NULL NAME 
			System.out.println("testGetFilteredResults(): TEST 2 GET FILTERED RESULTS WITH COUNTRY: " +  c.getIsoabbr()+ " and NULL NAME ");
						
			for (LocationTableViewModel item : controller.getFilteredResults(c.getCntryid(), controller.getLocationTypeList().iterator().next().getFldno(),null))
				System.out.println("> RESULT= " + item);
			

			// TEST 3 GET FILTERED LOCATION NAME
			System.out.println("testGetFilteredResults(): TEST 3 with filtered name, TEST DATA: IRRI");
						
			for (LocationTableViewModel item : controller.getFilteredResults(null,null,"IRRI"))
				System.out.println("> RESULT= " + item);

		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testSaveLocation() {
		
		// simualate saving location, you can manually add selected location id's to be saved
		// controller.saveProjectLocation(selectedLocationIds);
	}
	
	
	@AfterClass
	public static void doneTest() {
		System.out.println("DONE TEST");
		managerFactoryProvider.close();
		
		hibernateUtil.shutdown();
	}
}

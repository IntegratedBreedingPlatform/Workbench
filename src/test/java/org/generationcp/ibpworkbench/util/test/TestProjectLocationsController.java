package org.generationcp.ibpworkbench.util.test;

import org.generationcp.ibpworkbench.projectlocations.LocationTableViewModel;
import org.generationcp.ibpworkbench.projectlocations.ProjectLocationsController;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class TestProjectLocationsController {

	private static ProjectLocationsController controller;
	
    private static WorkbenchDataManager manager;
	
    // NOTE: please setup VM argument on run+debug configurations on JUNIT just like when we setup a new server instance
    // for the spring load weaving to work properly
    // -javaagent:"${env_var:HOMEDRIVE}${env_var:HOMEPATH}\.m2\repository\org\springframework\spring-instrument\3.1.1.RELEASE\spring-instrument-3.1.1.RELEASE.jar"
   
    @Before
    public void setUp() throws Exception {
		
		// Lest test this on the first project the workbench manager gets
		//DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("workbench.properties", "workbench");
        //HibernateUtil hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
        //                        workbenchDb.getUsername(), workbenchDb.getPassword());
        //HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
        //manager = new WorkbenchDataManagerImpl(sessionProvider);
	
		Assert.assertNotNull(manager);
		
		User u = null;
		Role r = null;
		for (Project p : manager.getProjects() ) {
			
			try {
				if (manager.getUsersByProjectId(p.getProjectId()).size() <= 0)
					continue;
				
				u = manager.getUsersByProjectId(p.getProjectId()).iterator().next();
				
				Assert.assertNotNull(u);
					
			} catch (Exception e) {
				continue;
			}
					
			
			if (manager.getRolesByProjectAndUser(p,u).size() <= 0)
				continue;
			
			r = manager.getRolesByProjectAndUser(p,u).iterator().next();	
		
			Assert.assertNotNull(r);
		
	        controller = new ProjectLocationsController(p,r);
	        
	        Assert.assertNotNull(controller);

	        break;
		}
    }
	
	@Test
	public void testGetFilteredResults() {
		try {
			UserDefinedField ltype = controller.getLocationTypeList().iterator().next();
			Country c = null;
			
			// just get the country that starts with an alphabeth, this most likely get Afganistan
			while (controller.getCountryList().iterator().hasNext()) {
				c = controller.getCountryList().iterator().next();
				
				if (Character.isLetter(c.getIsofull().charAt(0)))
					break;
			}
			
			// TEST 1 GET FILTERED RESULTS WITH NULL COUNTRY and NULL NAME 
			System.out.println("testGetFilteredResults(): TEST 1 GET FILTERED RESULTS WITH NULL COUNTRY and NULL NAME ");
			
			
			for (LocationTableViewModel item : controller.getFilteredResults(null, ltype.getFldno(),null))
				System.out.println("> RESULT= " + item);
			
			// TEST 2 GET FILTERED RESULTS WITH COUNTRY and NULL NAME 
			System.out.println("testGetFilteredResults(): TEST 2 GET FILTERED RESULTS WITH COUNTRY and NULL NAME ");
						
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
	
	
}

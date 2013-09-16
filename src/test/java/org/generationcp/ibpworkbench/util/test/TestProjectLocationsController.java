package org.generationcp.ibpworkbench.util.test;

import org.generationcp.ibpworkbench.projectlocations.LocationTableViewModel;
import org.generationcp.ibpworkbench.projectlocations.ProjectLocationsController;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
@Configurable
public class TestProjectLocationsController {

	private static ProjectLocationsController controller;
	
    @Autowired
    private static WorkbenchDataManager manager;
	
    @Autowired
    private static GermplasmDataManager gdm;
    
	@BeforeClass
    public static void setUpBeforeClass() throws Exception {
		
		// Lest test this on the first project the workbench manager gets
		
		Assert.assertNotNull(manager);
		Assert.assertNotNull(gdm);
		Project p = manager.getProjects().iterator().next();
		
		// get the user and role of this project
		User u = manager.getUsersByProjectId(p.getProjectId()).iterator().next();
		Role r = manager.getRolesByProjectAndUser(p,u).iterator().next();
		
        controller = new ProjectLocationsController(p,r);
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

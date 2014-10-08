package org.generationcp.ibpworkbench.util.test;


import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestBreedingViewProjectXmlWriter{
    
    private static ManagerFactory factory;
    private static StudyDataManager manager;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getStudyDataManager();
    }

    @Test
    public void testWritingXml() throws Exception {
        
/*        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("test");
        
        BreedingViewInput breedingViewInput = new BreedingViewInput(project, );
        
        BreedingViewXMLWriter breedingViewXMLWriter = new BreedingViewXMLWriter(breedingViewInput);
        
        breedingViewXMLWriter.write(manager
                , "testProject.xml"
                , "test"
                , "test"
                , ProjectType.FIELD_TRIAL
                , DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN
                , "test"
                , new Blocks("block")
                , new Replicates("rep")
                , Integer.valueOf(1245)
                , "test");*/
    }
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        factory.close();
    }

}

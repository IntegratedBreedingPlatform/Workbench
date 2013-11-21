/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.ui.dashboard.test;

import java.util.List;

import org.generationcp.commons.hibernate.DefaultManagerFactoryProvider;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreviewPresenter;
import org.generationcp.ibpworkbench.ui.dashboard.preview.TreeNode;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.WorkbenchDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
	
public class TestNurseryListPreviewPresenter{

    private static WorkbenchDataManager workbenchDataManager;

    private static DefaultManagerFactoryProvider managerFactoryProvider;

    private static HibernateUtil hibernateUtil;
    

    private long startTime;

    @Rule
    public TestName name = new TestName();

	@Before
	public void setUp() throws Exception {
        DatabaseConnectionParameters workbenchDb = new DatabaseConnectionParameters("workbench.properties", "workbench");
        hibernateUtil = new HibernateUtil(workbenchDb.getHost(), workbenchDb.getPort(), workbenchDb.getDbName(), 
                               workbenchDb.getUsername(), workbenchDb.getPassword());
        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(hibernateUtil.getSessionFactory());
        workbenchDataManager = new WorkbenchDataManagerImpl(sessionProvider);
            
        Assert.assertNotNull("Manager is null, spring did not load the bean",workbenchDataManager);
        
        managerFactoryProvider = new DefaultManagerFactoryProvider();
	}
	

    @Before
    public void beforeEachTest() {
        startTime = System.nanoTime();
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + ": Elapsed Time = " + elapsedTime + " ns = "
                + ((double) elapsedTime / 1000000000) + " s");
    }
	
	@Test
	public void testGenerateTreeNode() throws Exception {
	    
	    Project project = workbenchDataManager.getProjectById((long) 2);
	    Assert.assertNotNull(project);
	    Debug.println(0, project.toString());
	    
	    NurseryListPreview preview = new NurseryListPreview(project);
	    preview.setManagerFactoryProvider(managerFactoryProvider);
	    NurseryListPreviewPresenter presenter = new NurseryListPreviewPresenter(preview, project);
	    
	    List<TreeNode> treeNodes = presenter.generateTreeNodes();
	    
	    for(TreeNode treeNode : treeNodes){
	        System.out.println(treeNode);
	    }

	}

    
    @AfterClass
    public static void doneTest() {
        managerFactoryProvider.close();
        hibernateUtil.shutdown();
    }

}

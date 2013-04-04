/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.pojos.test;

import java.util.List;

import org.generationcp.middleware.pojos.dms.CV;
import org.generationcp.middleware.pojos.dms.CVTerm;
import org.generationcp.middleware.pojos.dms.CVTermProperty;
import org.generationcp.middleware.pojos.dms.CVTermRelationship;
import org.generationcp.middleware.pojos.dms.CVTermSynonym;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.Experiment;
import org.generationcp.middleware.pojos.dms.ExperimentPhenotype;
import org.generationcp.middleware.pojos.dms.ExperimentProject;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.ExperimentStock;
import org.generationcp.middleware.pojos.dms.Geolocation;
import org.generationcp.middleware.pojos.dms.GeolocationProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.pojos.dms.Stock;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DMSPojosSimpleTest{

    private static final String CONFIG = "test-hibernate.cfg.xml";
    private static HibernateUtil hibernateUtil;

    @BeforeClass
    public static void setUp() throws Exception {
        hibernateUtil = new HibernateUtil(CONFIG);
    }
    
    @Test
    public void testGeolocation() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM Geolocation");
        query.setMaxResults(5);
        List results = query.list();

        System.out.println("testGeolocation() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof Geolocation);
            Assert.assertTrue(obj != null);
            Geolocation holder = (Geolocation) obj;
            System.out.println("  " + holder);
        }
    }
    
    @Test
    public void testCV() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CV");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCV() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CV);
            Assert.assertTrue(obj != null);
            CV holder = (CV) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testCVTerm() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CVTerm");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCVTerm() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CVTerm);
            Assert.assertTrue(obj != null);
            CVTerm holder = (CVTerm) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testCVTermRelationship() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CVTermRelationship");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCVTermRelationship() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CVTermRelationship);
            Assert.assertTrue(obj != null);
            CVTermRelationship holder = (CVTermRelationship) obj;
            System.out.println("  " + holder);
        }
    }
    
    @Test
    public void testExperimentProperty() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM ExperimentProperty");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testExperimentProperty() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof ExperimentProperty);
            Assert.assertTrue(obj != null);
            ExperimentProperty holder = (ExperimentProperty) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testCVTermSynonym() {
        Session session = hibernateUtil.getCurrentSession();
        Query query = session.createQuery("FROM CVTermSynonym");
        query.setMaxResults(10);
        List results = query.list();

        System.out.println("testCVTermSynonym() RESULTS: ");
        for (Object obj : results) {
            Assert.assertTrue(obj instanceof CVTermSynonym);
            Assert.assertTrue(obj != null);
            CVTermSynonym holder = (CVTermSynonym) obj;
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testDmsProject() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM DmsProject");
    	query.setMaxResults(5);
    	
    	System.out.println("testDmsProject() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof DmsProject);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testProjectProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ProjectProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testProjectProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ProjectProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testExperiment() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM Experiment");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperiment() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof Experiment);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }
    
    @Test
    public void testExperimentProject() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ExperimentProject");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperimentProject() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ExperimentProject);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }
    
    @Test
    public void testExperimentPhenotype() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ExperimentPhenotype");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperimentPhenotype() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ExperimentPhenotype);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testStock() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM Stock");
    	query.setMaxResults(5);
    	
    	System.out.println("testStock() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof Stock);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testStockProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM StockProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testStockProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof StockProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }


    @Test
    public void testPhenotype() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM Phenotype");
    	query.setMaxResults(5);
    	
    	System.out.println("testPhenotype() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof Phenotype);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testExperimentStock() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ExperimentStock");
    	query.setMaxResults(5);
    	
    	System.out.println("testExperimentStock() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ExperimentStock);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }
    
    @Test
    public void testGeolocationProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM GeolocationProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testGeolocationProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof GeolocationProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testProjectRelationship() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM ProjectRelationship");
    	query.setMaxResults(5);
    	
    	System.out.println("testProjectRelationship() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof ProjectRelationship);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @Test
    public void testCVTermProperty() {
    	Session session = hibernateUtil.getCurrentSession();
    	Query query = session.createQuery("FROM CVTermProperty");
    	query.setMaxResults(5);
    	
    	System.out.println("testCVTermProperty() RESULTS: ");
    	for (Object obj : query.list()) {
    		Assert.assertTrue(obj instanceof CVTermProperty);
    		Assert.assertTrue(obj != null);
    		System.out.println(" " + obj);
    	}
    }

    @AfterClass
    public static void tearDown() throws Exception {
        hibernateUtil.shutdown();
    }

}

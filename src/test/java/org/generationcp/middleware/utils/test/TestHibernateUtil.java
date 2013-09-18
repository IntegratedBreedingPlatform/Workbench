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

package org.generationcp.middleware.utils.test;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateUtil;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.pojos.Germplasm;
import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Test;

public class TestHibernateUtil{

    @Test
    public void testHibernateUtil() throws Exception {
        DatabaseConnectionParameters params =  new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        HibernateUtil util = new HibernateUtil(new DatabaseConnectionParameters("testDatabaseConfig.properties", "central"));
        Session session = util.getCurrentSession();
        Query query = session.createQuery("FROM Germplasm");
        query.setFirstResult(0);
        query.setMaxResults(5);
        List<Germplasm> results = query.list();

        System.out.println("testHibernateUtil(host=" + params.getHost() + ", port=" + params.getPort() + ", dbName=" + params.getDbName() + ", userName=" + params.getUsername()
                + ", password=" + params.getPassword() + ") GERMPLASMS: ");

        for (Germplasm g : results) {
            System.out.println("  " + g);
        }
    }
}

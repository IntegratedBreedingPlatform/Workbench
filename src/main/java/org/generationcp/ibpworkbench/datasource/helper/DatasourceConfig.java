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

package org.generationcp.ibpworkbench.datasource.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Properties;

import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.util.ResourceFinder;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasourceConfig implements Serializable{

    
    private static final Logger LOG = LoggerFactory.getLogger(DatasourceConfig.class);
    private static final long serialVersionUID = 1818294200537720321L;

    private ManagerFactory managerFactory;

    public DatasourceConfig() {

        String host;
        String port;
        String dbname;
        String username;
        String password;

        Properties prop = new Properties();

        try {
            InputStream in = null;

            try {
                in = new FileInputStream(new File(ResourceFinder.locateFile("IBPDatasource.properties").toURI()));
            } catch (IllegalArgumentException ex) {
                in = Thread.currentThread().getContextClassLoader().getResourceAsStream("IBPDatasource.properties");
            }
            prop.load(in);

            host = prop.getProperty("ibpmiddleware.host");
            LOG.info("host:" + host);
            dbname = prop.getProperty("ibpmiddleware.dbname");
            LOG.info("dbname:" + dbname);
            port = prop.getProperty("ibpmiddleware.port");
            LOG.info("port:" + port);
            username = prop.getProperty("ibpmiddleware.username");
            LOG.info("username:" + username);
            password = prop.getProperty("ibpmiddleware.password");
            LOG.info("password:" + password);
            in.close();

            DatabaseConnectionParameters param = new DatabaseConnectionParameters(host, port, dbname, username, password);
            // the database connection parameters for central is set to null
            // because the Workbench
            // does not need a connection to a central instance of IBDB
            // the Workbench currently uses the WorkbenchDataManager only which
            // needs just a
            // connection to a local instance
            managerFactory = new ManagerFactory(param, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Cannot create data source", e);
        } catch (HibernateException e) {
            throw new RuntimeException("Cannot create data source", e);
        } catch (ConfigException e) {
            throw new RuntimeException("Cannot create data source", e);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create data source", e);
        }

    }

    public ManagerFactory getManagerFactory() {
        return this.managerFactory;
    }

}

/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the
 * GNU General Public License (http://bit.ly/8Ztv8M) and the
 * provisions of Part F of the Generation Challenge Programme
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/
package org.generationcp.ibpworkbench.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.exceptions.SQLFileException;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class IBDBGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(IBDBGenerator.class);
    //common constants
    public static final String WORKBENCH_PROP = "workbench.properties";
    public static final String WORKBENCH_PROP_HOST = "workbench.host";
    public static final String WORKBENCH_PROP_PORT = "workbench.port";
    public static final String WORKBENCH_PROP_USER = "workbench.username";
    public static final String WORKBENCH_PROP_PASSWORD = "workbench.password";

    public static final String WORKBENCH_PROP_INSTALLER_MODE = "workbench.installer.mode";

    public static final String INSTALLER_MODE_LAN = "lan";
    public static final String INSTALLER_MODE_LOCAL = "local";

    protected static final String SQL_CREATE_DATABASE = "CREATE DATABASE ";
    protected static final String SQL_CREATE_DATABASE_IF_NOT_EXISTS = "CREATE DATABASE IF NOT EXISTS ";
    protected static final String SQL_CHAR_SET = " CHARACTER SET ";
    protected static final String SQL_COLLATE = " COLLATE ";
    protected static final String SQL_GRANT_ALL = "GRANT ALL ON ";
    protected static final String SQL_TO = " TO ";
    protected static final String SQL_IDENTIFIED_BY = " IDENTIFIED BY ";
    protected static final String SQL_FLUSH_PRIVILEGES = "FLUSH PRIVILEGES ";
    protected static final String SQL_SINGLE_QUOTE = "'";
    protected static final String SQL_AT_SIGN = "@";
    protected static final String SQL_PERIOD = ".";
    protected static final String DEFAULT_LOCAL_USER = "local";
    protected static final String DEFAULT_CENTRAL_USER = "central";
    protected static final String DEFAULT_LOCAL_HOST = "localhost";
    protected static final String DEFAULT_LOCAL_PASSWORD = "local";
    protected static final String DEFAULT_CENTRAL_PASSWORD = "central";
    protected static final String DEFAULT_ALL = "*";
    protected static final String DEFAULT_CHAR_SET = "utf8";
    protected static final String DEFAULT_COLLATE = "utf8_general_ci";

    protected Connection connection;
    protected String generatedDatabaseName;

    protected String workbenchHost;
    protected String workbenchPort;
    protected String workbenchUsername;
    protected String workbenchPassword;
    protected String workbenchURL;

    @Resource
    protected Properties workbenchProperties;
    
    @Autowired
    private MySQLUtil mysqlUtil;

    protected void createConnection() {
        if (this.connection == null) {

            workbenchHost = workbenchProperties.getProperty(WORKBENCH_PROP_HOST);
            workbenchPort = workbenchProperties.getProperty(WORKBENCH_PROP_PORT);
            workbenchUsername = workbenchProperties.getProperty(WORKBENCH_PROP_USER);
            workbenchPassword = workbenchProperties.getProperty(WORKBENCH_PROP_PASSWORD);
            workbenchURL = "jdbc:mysql://" + workbenchHost + ":" + workbenchPort;

            try {
                connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
            } catch (SQLException e) {
                handleDatabaseError(e);
            }
        }
    }
    
    
    protected void runScriptsInDirectory(String databaseName, File directory) throws SQLFileException {
		mysqlUtil.runScriptsInDirectory(databaseName, directory);
    }

    protected void closeConnection(){
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                handleDatabaseError(e);
            }
        }
    }

    protected static void handleDatabaseError(Exception e){
        LOG.error(e.toString(), e);
        throw new InternationalizableException(e,
                Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
    }

    protected static void handleConfigurationError(Exception e){
        LOG.error(e.toString(), e);
        throw new InternationalizableException(e,
                Message.CONFIG_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
    }

    protected boolean isLanInstallerMode(Properties properties) {
        String installerMode = properties.getProperty(WORKBENCH_PROP_INSTALLER_MODE, INSTALLER_MODE_LOCAL);
        return INSTALLER_MODE_LAN.equals(installerMode);
    }
}

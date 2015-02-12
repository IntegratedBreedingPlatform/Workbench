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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.ScriptRunner;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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

    protected void executeSQLFile(File sqlFile) {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile)));
            scriptRunner.runScript(br);
        }catch (FileNotFoundException e) {
            handleConfigurationError(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }
    
    protected void runScriptsInDirectory(Connection conn, File directory) {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        
        // get the sql files
        File[] sqlFilesArray = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".sql");
            }
        });
        if (sqlFilesArray == null || sqlFilesArray.length == 0) {
            return;
        }
        
        List<File> sqlFiles = Arrays.asList(sqlFilesArray);
        Collections.sort(sqlFiles);
        
        for (File sqlFile : sqlFiles) {
            BufferedReader br = null;
            
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile)));
                scriptRunner.runScript(br);
            }catch (IOException e) {
                handleDatabaseError(e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            }
        }
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

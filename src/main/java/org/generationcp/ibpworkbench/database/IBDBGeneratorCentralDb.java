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
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Statement;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IBDBGeneratorCentralDb extends IBDBGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(IBDBGeneratorCentralDb.class);

    //local constant
    protected static final String WORKBENCH_GDMS_CENTRAL_SQL = "IBDBv1_GMS-CENTRAL.sql";
    private static final String DB_CENTRAL_NAME_PREFIX = "ibdb";
    private static final String DB_CENTRAL_NAME_SUFFIX = "central";

    private CropType cropType;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public IBDBGeneratorCentralDb(CropType cropType) {
        this.cropType = cropType;
    }

    public boolean generateDatabase() throws InternationalizableException {

        boolean isGenerationSuccess = false;

        try {
            createConnection();
            if (databaseExists()) {
                return true;
            }
            createDatabase();
            createManagementSystems();
            isGenerationSuccess = true;
        } catch (InternationalizableException e) {
            isGenerationSuccess = false;
            throw e;
        } finally {
            closeConnection();
        }
        return isGenerationSuccess;
    }
    
    protected String getDatabaseName() {
        StringBuffer databaseName = new StringBuffer();
        databaseName.append(DB_CENTRAL_NAME_PREFIX).append("_").append(cropType.getCropName().toLowerCase()).append("_").append(DB_CENTRAL_NAME_SUFFIX);
        return databaseName.toString().toLowerCase();
    }
    
    private boolean databaseExists() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("USE " + getDatabaseName());
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private void createDatabase() throws InternationalizableException {
        StringBuffer createDatabaseSyntax = new StringBuffer();
        StringBuffer createGrantSyntax = new StringBuffer();
        StringBuffer createFlushSyntax = new StringBuffer();

        String databaseName = getDatabaseName();

        Statement statement = null;
        try {
            statement = connection.createStatement();
            createDatabaseSyntax.append(SQL_CREATE_DATABASE_IF_NOT_EXISTS).append(databaseName).append(SQL_CHAR_SET).append(DEFAULT_CHAR_SET).append(SQL_COLLATE).append(DEFAULT_COLLATE);
            statement.addBatch(createDatabaseSyntax.toString());

            createGrantSyntax.append(SQL_GRANT_ALL).append(databaseName).append(SQL_PERIOD).append(DEFAULT_ALL).append(SQL_TO)
                    .append(SQL_SINGLE_QUOTE).append(DEFAULT_CENTRAL_USER).append(SQL_SINGLE_QUOTE).append(SQL_AT_SIGN).append(SQL_SINGLE_QUOTE).append(DEFAULT_LOCAL_HOST)
                    .append(SQL_SINGLE_QUOTE).append(SQL_IDENTIFIED_BY).append(SQL_SINGLE_QUOTE).append(DEFAULT_CENTRAL_PASSWORD).append(SQL_SINGLE_QUOTE);

            statement.addBatch(createGrantSyntax.toString());
            createFlushSyntax.append(SQL_FLUSH_PRIVILEGES);
            statement.addBatch(createFlushSyntax.toString());
            statement.executeBatch();

            generatedDatabaseName = databaseName.toString();
            connection.setCatalog(databaseName.toString());
        } catch (SQLException e) {
            handleDatabaseError(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    handleDatabaseError(e);
                }
            }
        }
    }

    private void createManagementSystems() throws InternationalizableException {
        try {

            executeSQLFile(new File(ResourceFinder.locateFile(WORKBENCH_DMS_SQL).toURI()));
            executeSQLFile(new File(ResourceFinder.locateFile(WORKBENCH_TMS_SQL).toURI()));
            executeSQLFile(new File(ResourceFinder.locateFile(WORKBENCH_GDMS_SQL).toURI()));
            executeSQLFile(new File(ResourceFinder.locateFile(WORKBENCH_GDMS_CENTRAL_SQL).toURI()));

            LOG.info("IB Central Database Generation Successful");

        } catch (FileNotFoundException e) {
            handleConfigurationError(e);
        } catch (URISyntaxException e) {
            handleConfigurationError(e);
        }
    }


}

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
import java.sql.SQLException;
import java.sql.Statement;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class IBDBGeneratorCentralDb extends IBDBGenerator {
    private CropType cropType;
    private boolean alreadyExistsFlag = false;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public IBDBGeneratorCentralDb(CropType cropType) {
        this.cropType = cropType;
    }
    
    public boolean isAlreadyExists(){
    	return alreadyExistsFlag;
    }

    public boolean generateDatabase() throws InternationalizableException {

        boolean isGenerationSuccess = false;

        try {
            createConnection();
            alreadyExistsFlag = databaseExists();
            if (alreadyExistsFlag) {
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
    
    private boolean databaseExists() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("USE " + cropType.getCentralDbName());
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

        String databaseName = cropType.getCentralDbName();

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
            WorkbenchSetting setting = workbenchDataManager.getWorkbenchSetting();
            if (setting == null) {
                throw new IllegalStateException("Workbench setting record not found");
            }
            
            File localDatabaseDirectory = new File(setting.getInstallationDirectory(), "database/central");
            
            // run the common scripts
            runScriptsInDirectory(connection, new File(localDatabaseDirectory, "common"));
            
            // run the scripts for custom crops
            runScriptsInDirectory(connection, new File(localDatabaseDirectory, "custom"));
            
            // NOTE: IBDBGeneratorCentralDb is intended to be run for custom crops only,
            // hence, we should not be running scripts for specific crops here
            
            // run crop specific script
            // runScriptsInDirectory(connection, new File(localDatabaseDirectory, cropType.getCropName()));
        }
        catch (MiddlewareQueryException e) {
            handleDatabaseError(e);
        }
    }


}

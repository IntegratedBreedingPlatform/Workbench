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
import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.exceptions.SQLFileException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class IBDBGeneratorCentralDb extends IBDBGenerator {
    public static final String DATABASE_CENTRAL = "database/central";
	private static final Logger LOG = LoggerFactory.getLogger(IBDBGeneratorCentralDb.class);
    
	private CropType cropType;
    private boolean alreadyExistsFlag = false;
    
    @Resource
    private Properties workbenchProperties;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public IBDBGeneratorCentralDb() {
    	
    }
    
    public IBDBGeneratorCentralDb(CropType cropType) {
        this.cropType = cropType;
    }
    
    public boolean isAlreadyExists(){
    	return alreadyExistsFlag;
    }

    public boolean generateDatabase(){

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
    
    protected boolean databaseExists() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute("USE " + cropType.getCentralDbName());
            return true;
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    protected void createDatabase() {
        StringBuilder createDatabaseSyntax = new StringBuilder();

        String databaseName = cropType.getCentralDbName();

        Statement statement = null;
        try {
            statement = connection.createStatement();
            createDatabaseSyntax.append(SQL_CREATE_DATABASE_IF_NOT_EXISTS).append(databaseName).append(
                    SQL_CHAR_SET).append(DEFAULT_CHAR_SET).append(SQL_COLLATE).append(
                    DEFAULT_COLLATE);
            statement.addBatch(createDatabaseSyntax.toString());

            if (isLanInstallerMode(workbenchProperties)) {
                String grantFormat = "GRANT ALL ON %s.* TO %s@'%s' IDENTIFIED BY '%s'";

                // grant the user
                String allGrant = String
                        .format(grantFormat, databaseName, DEFAULT_CENTRAL_USER, "%",
                                DEFAULT_CENTRAL_PASSWORD);
                String localGrant = String
                        .format(grantFormat, databaseName, DEFAULT_CENTRAL_USER, DEFAULT_LOCAL_HOST,
                                DEFAULT_CENTRAL_PASSWORD);

                statement.execute(allGrant);
                statement.execute(localGrant);
                statement.execute("FLUSH PRIVILEGES");
            } else {
                StringBuilder createGrantSyntax = new StringBuilder();
                StringBuilder createFlushSyntax = new StringBuilder();
                createGrantSyntax.append(SQL_GRANT_ALL).append(databaseName).append(SQL_PERIOD)
                        .append(DEFAULT_ALL).append(SQL_TO)
                        .append(SQL_SINGLE_QUOTE).append(DEFAULT_CENTRAL_USER)
                        .append(SQL_SINGLE_QUOTE).append(SQL_AT_SIGN).append(SQL_SINGLE_QUOTE)
                        .append(DEFAULT_LOCAL_HOST)
                        .append(SQL_SINGLE_QUOTE).append(SQL_IDENTIFIED_BY).append(SQL_SINGLE_QUOTE)
                        .append(DEFAULT_CENTRAL_PASSWORD).append(SQL_SINGLE_QUOTE);

            statement.addBatch(createGrantSyntax.toString());
            createFlushSyntax.append(SQL_FLUSH_PRIVILEGES);
            statement.addBatch(createFlushSyntax.toString());
            statement.executeBatch();
            }

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

    protected void createManagementSystems() {
        try {
            WorkbenchSetting setting = workbenchDataManager.getWorkbenchSetting();
            if (setting == null) {
                throw new IllegalStateException("Workbench setting record not found");
            }
            
            File localDatabaseDirectory = new File(setting.getInstallationDirectory(), DATABASE_CENTRAL);
            
            // run the common scripts
            runScriptsInDirectory(generatedDatabaseName, new File(localDatabaseDirectory, "common"));
            
            // run the scripts for custom crops
            runScriptsInDirectory(generatedDatabaseName, new File(localDatabaseDirectory, "custom"));
            
            // run the common-post scripts
            runScriptsInDirectory(generatedDatabaseName, new File(localDatabaseDirectory, "common-update"));
            
            // NOTE: IBDBGeneratorCentralDb is intended to be run for custom crops only,
            // hence, we should not be running scripts for specific crops here            
        
        } catch (SQLFileException | MiddlewareQueryException e){
        	handleDatabaseError(e);
        }
    }

	public void setCropType(CropType cropType) {
		this.cropType = cropType;
	}
	
	 public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
        this.workbenchDataManager = workbenchDataManager;
    }
}

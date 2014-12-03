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
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * 
 * @author Jeffrey Morales
 */
@Configurable
public class IBDBGeneratorLocalDb extends IBDBGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(IBDBGeneratorLocalDb.class);

    private CropType cropType;
    private Long projectId;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    public IBDBGeneratorLocalDb() {
    	
    }

    public IBDBGeneratorLocalDb(CropType cropType, Long projectId) {
        this.cropType = cropType;
        this.projectId = projectId;

    }

    public boolean generateDatabase() throws InternationalizableException {
        
        boolean isGenerationSuccess = false;
        
        try {
            createConnection();
            createLocalDatabase();
            createManagementSystems();
            
            isGenerationSuccess = true;
        } catch (InternationalizableException e) {
            isGenerationSuccess = false;            
            throw e;
        } 
        finally {
            closeConnection();
        }

        return isGenerationSuccess;
    }

    private void createLocalDatabase() throws InternationalizableException {

        String databaseName = cropType.getLocalDatabaseNameWithProjectId(projectId);
        StringBuffer createDatabaseSyntax = new StringBuffer();
        StringBuffer createGrantSyntax = new StringBuffer();
        StringBuffer createFlushSyntax = new StringBuffer();

        Statement statement = null;

        try {

            statement = connection.createStatement();
            
            createDatabaseSyntax.append(SQL_CREATE_DATABASE).append(databaseName).append(SQL_CHAR_SET).append(DEFAULT_CHAR_SET).append(SQL_COLLATE).append(DEFAULT_COLLATE);
            
            statement.executeUpdate(createDatabaseSyntax.toString());
            
            createGrantSyntax.append(SQL_GRANT_ALL).append(databaseName).append(SQL_PERIOD).append(DEFAULT_ALL).append(SQL_TO)
                .append(SQL_SINGLE_QUOTE).append(DEFAULT_LOCAL_USER).append(SQL_SINGLE_QUOTE).append(SQL_AT_SIGN).append(SQL_SINGLE_QUOTE).append(DEFAULT_LOCAL_HOST)
                .append(SQL_SINGLE_QUOTE).append(SQL_IDENTIFIED_BY).append(SQL_SINGLE_QUOTE).append(DEFAULT_LOCAL_PASSWORD).append(SQL_SINGLE_QUOTE);
            
            statement.executeUpdate(createGrantSyntax.toString());
            
            createFlushSyntax.append(SQL_FLUSH_PRIVILEGES);
            
            statement.executeUpdate(createFlushSyntax.toString());
            
            generatedDatabaseName = databaseName;
            
            connection.setCatalog(databaseName);
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
            
            File localDatabaseDirectory = new File(setting.getInstallationDirectory(), "database/local");
            
            // run the common scripts
            runScriptsInDirectory(connection, new File(localDatabaseDirectory, "common"));
            
            // run crop specific script
            runScriptsInDirectory(connection, new File(localDatabaseDirectory, cropType.getCropName()));
            
            // run the common-update scripts
            runScriptsInDirectory(connection, new File(localDatabaseDirectory, "common-update"));
        }
        catch (MiddlewareQueryException e) {
            handleDatabaseError(e);
        }
    }
    
    public static void handleDatabaseError(Exception e) throws InternationalizableException {
        LOG.error(e.toString(), e);
        throw new InternationalizableException(e, 
                Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
    }
    
    public static void handleConfigurationError(Exception e) throws InternationalizableException {
        LOG.error(e.toString(), e);
        throw new InternationalizableException(e, 
                Message.CONFIG_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
    }

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	
	public void setCropType(CropType cropType) {
		this.cropType = cropType;
	}

	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}

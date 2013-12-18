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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.generationcp.ibpworkbench.model.LocationModel;
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

    private static final String DEFAULT_INSERT_LOCATIONS = "INSERT location VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DEFAULT_INSERT_BREEDING_METHODS = "INSERT methods VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DEFAULT_INSERT_INSTALLATION = "INSERT instln VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private CropType cropType;
    private Long projectId;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

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
            
            statement.addBatch(createDatabaseSyntax.toString());
            
            createGrantSyntax.append(SQL_GRANT_ALL).append(databaseName).append(SQL_PERIOD).append(DEFAULT_ALL).append(SQL_TO)
                .append(SQL_SINGLE_QUOTE).append(DEFAULT_LOCAL_USER).append(SQL_SINGLE_QUOTE).append(SQL_AT_SIGN).append(SQL_SINGLE_QUOTE).append(DEFAULT_LOCAL_HOST)
                .append(SQL_SINGLE_QUOTE).append(SQL_IDENTIFIED_BY).append(SQL_SINGLE_QUOTE).append(DEFAULT_LOCAL_PASSWORD).append(SQL_SINGLE_QUOTE);
            
            statement.addBatch(createGrantSyntax.toString());
            
            createFlushSyntax.append(SQL_FLUSH_PRIVILEGES);
            
            statement.addBatch(createFlushSyntax.toString());
            
            statement.executeBatch();
            
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
    
    public boolean addCachedLocations(Map<Integer, LocationModel> cachedLocations) {
    	
    	boolean areLocationsAdded = false;
    	
    	PreparedStatement preparedStatement = null;
    	try {
    		
		    connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
    	
		    connection.setCatalog(generatedDatabaseName);
    	
    	    Set<Integer> keySet = cachedLocations.keySet();
    	
    	    Iterator<Integer> keyIter = keySet.iterator();
    	
    	    LocationModel location;
    	
    	    preparedStatement = connection.prepareStatement(DEFAULT_INSERT_LOCATIONS);
    	
    	    while(keyIter.hasNext()) {
    		
    	    	location = cachedLocations.get(keyIter.next());
    		
    		    preparedStatement.setInt(1, location.getLocationId());
    		    preparedStatement.setInt(2, 0);
    		    preparedStatement.setInt(3, 0);
    		    preparedStatement.setString(4, location.getLocationName());
    		    preparedStatement.setString(5, location.getLocationAbbreviation());
    		    preparedStatement.setInt(6, 0);
    		    preparedStatement.setInt(7, 0);
    		    preparedStatement.setInt(8, 0);
    		    preparedStatement.setInt(9, 0);
    		    preparedStatement.setInt(10, 0);
    		    preparedStatement.setInt(11, 0);
    		    
    		    preparedStatement.executeUpdate();
    	    }
    	    
    	    areLocationsAdded = true;

		} catch (SQLException e) {
		    handleDatabaseError(e);
		}
    	finally {
    	    if (preparedStatement != null) {
    	        try {
                    preparedStatement.close();
                }
                catch (SQLException e) {
                }
    	    }
    	    closeConnection();
    	}
    	
    	return areLocationsAdded;
    	
    }
    
    public boolean addCachedBreedingMethods(Map<Integer, BreedingMethodModel> cachedBreedingMethods) {
        
        boolean areBreedingMethodsAdded = false;
        
        PreparedStatement preparedStatement = null;
        try {
            
            connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
        
            connection.setCatalog(generatedDatabaseName);
        
            Set<Integer> keySet = cachedBreedingMethods.keySet();
        
            Iterator<Integer> keyIter = keySet.iterator();
        
            BreedingMethodModel breedingMethod;
        
            preparedStatement = connection.prepareStatement(DEFAULT_INSERT_BREEDING_METHODS);
        
            while(keyIter.hasNext()) {
            
                breedingMethod = cachedBreedingMethods.get(keyIter.next());
            
   /*             mid int
                mtype combo string
                mgrp string 3  -
                mcode string 8
                mname string 50
                mdesc string 255
                mref int 0
                mprgn int 0
                mfprg int 0
                mattr int 0
                geneq int 0
                muid int 0
                lmid int 0
                mdate int*/
                
                preparedStatement.setInt(1, breedingMethod.getMethodId());
                preparedStatement.setString(2, breedingMethod.getMethodType());
                preparedStatement.setString(3, breedingMethod.getMethodGroup());
                preparedStatement.setString(4, breedingMethod.getMethodCode());
                preparedStatement.setString(5, breedingMethod.getMethodName());
                preparedStatement.setString(6, breedingMethod.getMethodDescription());
                preparedStatement.setInt(7, 0);
                preparedStatement.setInt(8, 0);
                preparedStatement.setInt(9, 0);
                preparedStatement.setInt(10, 0);
                preparedStatement.setInt(11, 0);
                preparedStatement.setInt(12, 0);
                preparedStatement.setInt(13, 0);
                
                preparedStatement.setInt(14, 0);
                
                preparedStatement.executeUpdate();
            
            }
            
            areBreedingMethodsAdded = true;
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
        finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (SQLException e) {
                }
            }
            closeConnection();
        }
        
        return areBreedingMethodsAdded;
        
    }
    
    /**
     * @param projectName
     * @return instalid of the installation created. -1 otherwise (default instalid)
     */
    public int addLocalInstallationRecord(String projectName, int localUserId) {

        int installId = -1;

        PreparedStatement preparedStatement = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            
            connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
        
            connection.setCatalog(generatedDatabaseName);
            
            stmt = connection.createStatement();
            resultSet = stmt.executeQuery("SELECT MIN(instalid) FROM instln");
            
            if (resultSet.next()) {
                installId = resultSet.getInt(1);
                installId--;
            }
            
            preparedStatement = connection.prepareStatement(DEFAULT_INSERT_INSTALLATION);
            
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            dateFormat.format(date);
            
            preparedStatement.setInt(1, installId); // instalid
            preparedStatement.setInt(2, localUserId);        // admin
            preparedStatement.setInt(3, Integer.parseInt(dateFormat.format(date))); // udate
            preparedStatement.setInt(4, 0);         // ugid
            preparedStatement.setInt(5, 0);         // ulocn
            preparedStatement.setInt(6, 0);         // ucid
            preparedStatement.setInt(7, 0);         // unid
            preparedStatement.setInt(8, 0);         // uaid
            preparedStatement.setInt(9, 0);         // uldid
            preparedStatement.setInt(10, 0);        // umethn
            preparedStatement.setInt(11, 0);        // ufldno
            preparedStatement.setInt(12, 0);        // urefno
            preparedStatement.setInt(13, 0);        // upid
            preparedStatement.setString(14, projectName);   // idesc
            preparedStatement.setInt(15, 0);        // ulistid
            preparedStatement.setInt(16, 0);        // dms_status
            preparedStatement.setInt(17, 0);        // ulrecid
            
            preparedStatement.executeUpdate();
            
            preparedStatement = null;

            
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
        finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                }
                catch (SQLException e) {
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                }
                catch (SQLException e) {
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (SQLException e) {
                }
            }
            closeConnection();
        }
        
        return installId;
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
}

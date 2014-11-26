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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configurable
public class IBDBGeneratorCentralDb extends IBDBGenerator {
    private CropType cropType;
    private boolean alreadyExistsFlag = false;
    private static final String DEFAULT_INSERT_INSTALLATION = "INSERT instln VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
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
            
            // run the common-post scripts
            runScriptsInDirectory(connection, new File(localDatabaseDirectory, "common-update"));
            
            // NOTE: IBDBGeneratorCentralDb is intended to be run for custom crops only,
            // hence, we should not be running scripts for specific crops here
            
            // run crop specific script
        }
        catch (MiddlewareQueryException e) {
            handleDatabaseError(e);
        }
    }
    
    /**
     * @param projectName
     * @return instalid of the installation created. 0 otherwise (default instalid)
     */
    //TODO BMS-148 : Review usage in org.generationcp.ibpworkbench.ui.project.create.AddProgramPresenter.doAddNewProgram() and cleanup.
    public int addCentralInstallationRecord(String projectName, int localUserId) {

   
        int installId=0;
        int locid=0;
        int methodid=0;
        int fldno=0;

        PreparedStatement preparedStatement = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            
            connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
        
            connection.setCatalog(generatedDatabaseName);
            
            stmt = connection.createStatement();
    		
    		stmt.execute("SELECT MIN(instalid) FROM instln");
    		resultSet = stmt.getResultSet();
    		if (resultSet.next()){
    			installId = stmt.getResultSet().getInt(1) + 1;
    		}
    		stmt.execute("SELECT CASE WHEN max(locid) IS NULL THEN 0 ELSE max(locid) END as locid FROM location");
    		resultSet = stmt.getResultSet();
    		if (resultSet.next()){
    			locid = stmt.getResultSet().getInt(1);
    		}
    		stmt.execute("SELECT CASE WHEN max(mid) IS NULL THEN 0 ELSE max(mid) END as mid FROM methods");
    		resultSet = stmt.getResultSet();
    		if (resultSet.next()){
    			methodid = stmt.getResultSet().getInt(1);
    		}
    		stmt.execute("SELECT CASE WHEN max(fldno) IS NULL THEN 0 ELSE max(fldno) END as fldno FROM udflds");
    		resultSet = stmt.getResultSet();
    		if (resultSet.next()){
    			fldno = stmt.getResultSet().getInt(1);
    		}
      
            preparedStatement = connection.prepareStatement(DEFAULT_INSERT_INSTALLATION);
            
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            dateFormat.format(date);
            
            preparedStatement.setInt(1, installId); // instalid
            preparedStatement.setInt(2, localUserId);        // admin
            preparedStatement.setInt(3, Integer.parseInt(dateFormat.format(date))); // udate
            preparedStatement.setInt(4, 0);         // ugid
            preparedStatement.setInt(5, locid);         // ulocn
            preparedStatement.setInt(6, 0);         // ucid
            preparedStatement.setInt(7, 0);         // unid
            preparedStatement.setInt(8, 0);         // uaid
            preparedStatement.setInt(9, 0);         // uldid
            preparedStatement.setInt(10, methodid);        // umethn
            preparedStatement.setInt(11, fldno);        // ufldno
            preparedStatement.setInt(12, 0);        // urefno
            preparedStatement.setInt(13, 1);        // upid
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


}

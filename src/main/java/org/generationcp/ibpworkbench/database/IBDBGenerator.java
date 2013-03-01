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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.Connection;
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
import java.util.Properties;
import java.util.Set;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.commons.util.ScriptRunner;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jeffrey Morales
 */
public class IBDBGenerator{

    private static final Logger LOG = LoggerFactory.getLogger(IBDBGenerator.class);
    public static final String WORKBENCH_PROP = "workbench.properties";
    
    private static final String WORKBENCH_DMS_SQL = "IBDBv1_DMS.sql";
    private static final String WORKBENCH_GDMS_SQL = "IBDBv1_GDMS.sql";
    private static final String WORKBENCH_GMS_LOCAL_SQL = "IBDBv1_GMS-LOCAL.sql";
    private static final String WORKBENCH_IMS_SQL = "IBDBv1_IMS.sql";
    private static final String WORKBENCH_TMS_SQL = "IBDBv1_TMS.sql";

    
    private static final String GDMS_INSERT_CASSAVA_SQL = "icass_ibdb_local_with_gdms_insert_only.sql";
    private static final String GDMS_INSERT_CHICKPEA_SQL = "ichis_ibdb_local_with_gdms_insert_only.sql";
    private static final String GDMS_INSERT_COWPEA_SQL = "ibdbv1_ivis_local_with_gdms_datainserts.sql";
    private static final String GDMS_INSERT_GROUNDNUT_SQL = "ignis_ibdb_local_with_gdms_insert_only.sql";
    private static final String GDMS_INSERT_MAIZE_SQL = "ibdbv1_imis_local_with_gdms_datainserts.sql";
    private static final String GDMS_INSERT_PHASEOLUS_SQL = "ibdbv1_iphis_local_with_gdms_datainserts.sql";
    private static final String GDMS_INSERT_RICE_SQL = "iris_ibdb_local_with_gdms_insert_only.sql";
    private static final String GDMS_INSERT_SORGHUM_SQL = "ibdbv1_isgis_local_with_gdms_datainserts.sql";
    private static final String GDMS_INSERT_WHEAT_SQL = "iwis_ibdb_local_with_gdms_insert_only.sql";
    
    private static final String LOCAL_INSERT_LENTIL_SQL = "ilis_ibdb_local_insert_only.sql";
    private static final String LOCAL_INSERT_SOYBEAN_SQL = "isbis_ibdb_local_insert_only.sql";

    public static final String WORKBENCH_PROP_HOST = "workbench.host";
    public static final String WORKBENCH_PROP_PORT = "workbench.port";
    public static final String WORKBENCH_PROP_USER = "workbench.username";
    public static final String WORKBENCH_PROP_PASSWORD = "workbench.password";
    
    public static final String DB_LOCAL_NAME_SUFFIX = "_local";
    
    private static final String SQL_CREATE_DATABASE = "CREATE DATABASE ";
    private static final String SQL_CHAR_SET = " CHARACTER SET ";
    private static final String SQL_COLLATE = " COLLATE ";
    public static final String SQL_GRANT_ALL = "GRANT ALL ON ";
    public static final String SQL_TO = " TO ";
    public static final String SQL_IDENTIFIED_BY = " IDENTIFIED BY ";
    public static final String SQL_FLUSH_PRIVILEGES = "FLUSH PRIVILEGES ";
    public static final String SQL_SINGLE_QUOTE = "'";
    public static final String SQL_AT_SIGN = "@";
    public static final String SQL_PERIOD = ".";
    public static final String SQL_END = ";";
    
    private static final String DEFAULT_LOCAL_USER = "local";
    public static final String DEFAULT_LOCAL_HOST = "localhost";
    private static final String DEFAULT_LOCAL_PASSWORD = "local";
    public static final String DEFAULT_ALL = "*";
    private static final String DEFAULT_CHAR_SET = "utf8";
    private static final String DEFAULT_COLLATE = "utf8_general_ci";
    
    private static final String DEFAULT_INSERT_LOCATIONS = "INSERT location VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DEFAULT_INSERT_BREEDING_METHODS = "INSERT methods VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String DEFAULT_INSERT_INSTALLATION = "INSERT instln VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private String workbenchHost;
    private String workbenchPort;
    private String workbenchUsername;
    private String workbenchPassword;
    private String workbenchURL;
    
    private String generatedDatabaseName;

    private CropType cropType;
    private Long projectId;

    private Connection connection = null;

    public IBDBGenerator(CropType cropType, Long projectId) {
        this.cropType = cropType;
        this.projectId = projectId;

    }

    public boolean generateDatabase() throws InternationalizableException {
        
        boolean isGenerationSuccess = false;
        
        try {
            createLocalConnection();
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

    private void createLocalConnection() throws InternationalizableException {

        if (this.connection == null) {

            Properties prop = new Properties();

            InputStream in = null;
            try {
                try {
                    in = new FileInputStream(new File(ResourceFinder.locateFile(WORKBENCH_PROP).toURI()));
                    prop.load(in);
                } catch (IllegalArgumentException ex) {
                    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(WORKBENCH_PROP);
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                }

                workbenchHost = prop.getProperty(WORKBENCH_PROP_HOST);
                workbenchPort = prop.getProperty(WORKBENCH_PROP_PORT);
                workbenchUsername = prop.getProperty(WORKBENCH_PROP_USER);
                workbenchPassword = prop.getProperty(WORKBENCH_PROP_PASSWORD);
                workbenchURL = "jdbc:mysql://" + workbenchHost + ":" + workbenchPort;
            } catch (URISyntaxException e) {
                handleConfigurationError(e);
            } catch (IOException e) {
                handleConfigurationError(e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                    }
                }
            }

            try {
                connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
            } catch (SQLException e) {
                handleDatabaseError(e);
            }

        }

    }

    private void createLocalDatabase() throws InternationalizableException {

        StringBuffer databaseName = new StringBuffer();
        StringBuffer createDatabaseSyntax = new StringBuffer();
        StringBuffer createGrantSyntax = new StringBuffer();
        StringBuffer createFlushSyntax = new StringBuffer();

        databaseName.append(cropType.getCropName().toLowerCase()).append("_").append(projectId).append(DB_LOCAL_NAME_SUFFIX);

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
            executeSQLFile(new File(ResourceFinder.locateFile(WORKBENCH_GMS_LOCAL_SQL).toURI()));
            executeSQLFile(new File(ResourceFinder.locateFile(WORKBENCH_IMS_SQL).toURI()));
            
            if (cropType.getCropName().equalsIgnoreCase(CropType.CASSAVA)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_CASSAVA_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.COWPEA)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_COWPEA_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.CHICKPEA)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_CHICKPEA_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.GROUNDNUT)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_GROUNDNUT_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.PHASEOLUS)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_PHASEOLUS_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.MAIZE)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_MAIZE_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.RICE)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_RICE_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.SORGHUM)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_SORGHUM_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase(CropType.WHEAT)){
                executeSQLFile(new File(ResourceFinder.locateFile(GDMS_INSERT_WHEAT_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase("lentil")){
            	//TODO add lentil and soybean to CropType
                executeSQLFile(new File(ResourceFinder.locateFile(LOCAL_INSERT_LENTIL_SQL).toURI()));
            } else if (cropType.getCropName().equalsIgnoreCase("soybean")){
                executeSQLFile(new File(ResourceFinder.locateFile(LOCAL_INSERT_SOYBEAN_SQL).toURI()));
            }

            LOG.info("IB Local Database Generation Successful");

        } catch (FileNotFoundException e) {
            handleConfigurationError(e);
        } catch (URISyntaxException e) {
            handleConfigurationError(e);
        }

    }

    private void executeSQLFile(File sqlFile) throws InternationalizableException {
        ScriptRunner scriptRunner = new ScriptRunner(connection);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile)));
            scriptRunner.runScript(br);
        }
        catch (FileNotFoundException e) {
            handleConfigurationError(e);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    // intentionally empty
                }
            }
        }
    }

    private void closeConnection() throws InternationalizableException {

        if (this.connection != null) {

            try {

                connection.close();

                connection = null;

            } catch (SQLException e) {
                handleDatabaseError(e);
            }

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

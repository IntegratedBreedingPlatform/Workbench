package org.generationcp.ibpworkbench.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.ResourceFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IBDBGenerator {
	
	private static final Logger LOG = LoggerFactory.getLogger(IBDBGenerator.class);
	private static final String WORKBENCH_PROP = "workbench.properties";
	private static final String WORKBENCH_DMS_SQL = "IBDBv1_DMS.sql";
	private static final String WORKBENCH_GDMS_SQL = "IBDBv1_GDMS.sql";
	private static final String WORKBENCH_GMS_LOCAL_SQL = "IBDBv1_GMS-LOCAL.sql";
	private static final String WORKBENCH_IMS_SQL = "IBDBv1_IMS.sql";
	
	private static final String WORKBENCH_PROP_HOST = "workbench.host";
	private static final String WORKBENCH_PROP_PORT = "workbench.port";
	private static final String WORKBENCH_PROP_USER = "workbench.username";
	private static final String WORKBENCH_PROP_PASSWORD = "workbench.password";
	
	private static final String DB_LOCAL_NAME_SUFFIX = "_local";
	
	private static final String SQL_CREATE_DATABASE = "CREATE DATABASE ";
	private static final String SQL_CHAR_SET = " CHARACTER SET ";
	private static final String SQL_COLLATE = " COLLATE ";
	
	private static final String DEFAULT_CHAR_SET = "utf8";
	private static final String DEFAULT_COLLATE = "utf8_general_ci";
	
	private static final String SQL_LINE_COMMENT = "--";
	private static final String SQL_BEGIN_COMMENT = "/*";
	private static final String SQL_END_COMMENT = "*/";
	
	private static final String SQL_END = ";";
	
    private String workbenchHost;
    private String workbenchPort;
    private String workbenchUsername;
    private String workbenchPassword;
    private String workbenchURL;
	
    private String crop;
    private Long projectId;
    
    private Connection connection = null;
    
    public IBDBGenerator(String crop, Long projectId)  {
    
    	this.crop = crop;
    	this.projectId = projectId;

    }
    
    public boolean generateDatabase() throws InternationalizableException {
    	
    	boolean isGenerationSuccess = false;
    	
    	try {
    	
    	createLocalConnection();
    	createLocalDatabase();
    	createManagementSystems();
    	closeConnection();
    	
    	isGenerationSuccess = true;
    	
    	} catch (InternationalizableException e) {
    		
    		isGenerationSuccess = false;
    		
    	}
    	
    	return isGenerationSuccess;
    	
    }
    
    private void createLocalConnection()  {

    	if (this.connection == null) {
    	
		    Properties prop = new Properties();
			
		    try {
		        InputStream in = null;
		
		        try {
		            in = new FileInputStream(new File(ResourceFinder.locateFile(WORKBENCH_PROP).toURI()));
		        } catch (IllegalArgumentException ex) {
		            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(WORKBENCH_PROP);
		        }
		        prop.load(in);
		
		        workbenchHost = prop.getProperty(WORKBENCH_PROP_HOST);
		        workbenchPort = prop.getProperty(WORKBENCH_PROP_PORT);
		        workbenchUsername = prop.getProperty(WORKBENCH_PROP_USER);
		        workbenchPassword = prop.getProperty(WORKBENCH_PROP_PASSWORD);
		        workbenchURL = "jdbc:mysql://" + workbenchHost + ":" + workbenchPort;
		
		        in.close();
		        
		    } catch (URISyntaxException e) {
		    	
	            LOG.error(e.toString() + "\n" + e.getStackTrace());
	            
	            throw new InternationalizableException();
	                
		    } catch (IOException e) {
		    	
		        LOG.error(e.toString() + "\n" + e.getStackTrace());
		        
		        throw new InternationalizableException();
		        
		    }
	
	        try {
	        	
	            connection = DriverManager.getConnection(workbenchURL, workbenchUsername, workbenchPassword);
	
	        }
	        catch (SQLException e) {
	        	
	            LOG.error("SQL Exception", e);
	            
	            throw new InternationalizableException();
	            
	        }
        
    	}
 
    }
    
    private void createLocalDatabase()  {
    	
    	StringBuffer databaseName = new StringBuffer();
    	
    	StringBuffer createDatabaseSyntax = new StringBuffer();
    	
    	databaseName.append(crop).append("_").append(projectId).append(DB_LOCAL_NAME_SUFFIX);
    	
        Statement statement = null;
        
        try {
    	
	    	statement = connection.createStatement();
	    	
	    	createDatabaseSyntax.append(SQL_CREATE_DATABASE).append(databaseName).append(SQL_CHAR_SET).append(DEFAULT_CHAR_SET).append(SQL_COLLATE).append(DEFAULT_COLLATE);
	    	
	    	statement.executeUpdate(createDatabaseSyntax.toString());
	    	
	    	connection.setCatalog(databaseName.toString());
	    	
	    } catch (SQLException e) {
	    	
	        e.printStackTrace();
	        
	        throw new InternationalizableException();
	        
	    } finally {
	    	
	        if (statement != null) {
	        	
	        	try {
	        		
		            statement.close();
		            
		        } catch (SQLException e) {
		        	
		        	throw new InternationalizableException();
		        	
		        }
	        	
		    }
	        
        }

    }
    
    private void createManagementSystems()  {
    	
		try {

			createTables(new File(ResourceFinder.locateFile(WORKBENCH_DMS_SQL).toURI()));
			createTables(new File(ResourceFinder.locateFile(WORKBENCH_GDMS_SQL).toURI()));
			createTables(new File(ResourceFinder.locateFile(WORKBENCH_GMS_LOCAL_SQL).toURI()));
			createTables(new File(ResourceFinder.locateFile(WORKBENCH_IMS_SQL).toURI()));
			
			LOG.info("IB Local Database Generation Successful");
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
			throw new InternationalizableException();
			
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
			
			throw new InternationalizableException();
			
		} 
        
    }
    
    private void createTables(File sqlFile)  {
    	
/*    	if (!sqlFile.toString().endsWith(".sql")) {
    		throw new IllegalArgumentException("Wrong file type.");
    	}*/
    	
    	StringBuffer batch = null;
    	Statement statement =  null;
    	
    	boolean isEndCommentFound = true;
    	
    	try {
    		
			statement = this.connection.createStatement();
			
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}
    	
        BufferedReader in = null;
    	
	    try {

	
	        try {
	        	
	            in = new BufferedReader(new FileReader(sqlFile));
	            
	        } catch (IllegalArgumentException ex) {
	           
	        	throw new InternationalizableException();
	        	
	        }
	        

	        String inputLine;
	        
	        while ((inputLine = in.readLine()) != null) {
	        	
	            if (inputLine.startsWith(SQL_LINE_COMMENT)) {
	            	
	            	continue;
	            	
	            } else if (inputLine.startsWith(SQL_BEGIN_COMMENT) && inputLine.endsWith(SQL_END_COMMENT)) {
	            	
	            	isEndCommentFound = true;
	            	
	            	continue;
	            	
	            } else if (inputLine.startsWith(SQL_BEGIN_COMMENT)) {
	            	
	            	isEndCommentFound = false;
	            	
	            	continue;
	            	
	            } else if (inputLine.endsWith(SQL_END_COMMENT)) {
	            	
	            	isEndCommentFound = true;
	            	
	            	continue;
	            	
	            } else if (isEndCommentFound) {
	            	
            		if (inputLine.contains(SQL_LINE_COMMENT)) {
            		
            			inputLine = inputLine.substring(0, inputLine.indexOf(SQL_LINE_COMMENT));
            			
            		}
            		
            		if (inputLine.contains(SQL_BEGIN_COMMENT) && inputLine.contains(SQL_END_COMMENT)) {
            			
            			inputLine = inputLine.substring(0, inputLine.indexOf(SQL_BEGIN_COMMENT));
            			
            			isEndCommentFound = true;
            			
            		}
	            	
	            	if(batch != null) {
	            		
	            		batch.append(inputLine);
	            		
	            		if (batch.toString().contains(SQL_END)) {
	            			
	            			statement.addBatch(batch.toString().replace(SQL_END, ""));
	            			
	            			batch = null;
	            			
	            		}
	            		
	            	} else {
	            		
	            		batch = new StringBuffer(inputLine);
	            		
	            		if (batch.toString().contains(SQL_END)) {
	            			
	            			statement.addBatch(batch.toString().replace(SQL_END, ""));
	            			
	            			batch = null;
	            			
	            		}
	            		
	            	}
	            	
	            }
	            
	        }
	        
	        in.close();

	        statement.executeBatch();
	        
	        statement.close();
	        
	        statement = null;
	        
	        LOG.info("Tables in " + sqlFile.getName() + " Generated.");
	        
	    } catch (IOException e) {
	        LOG.error(e.toString() + "\n" + e.getStackTrace());
	        
	        throw new InternationalizableException();
	        
	    } catch (BatchUpdateException e) {
	    	LOG.error(e.toString() + "\n" + e.getStackTrace());
	    	
	    	throw new InternationalizableException();
	    	
	    } catch (SQLException e) {
	        LOG.error(e.toString() + "\n" + e.getStackTrace());
	        
	        throw new InternationalizableException();
	    }
	 
    }
    
    private void closeConnection() {
    	
	    if (this.connection != null) {
	    	
	    	try {
        	  
	    		connection.close();
	    		
		    	connection = null;
            
	    	} catch (SQLException e) {
        	  
	    	}

        }
    	
    }
    
    @Override
    protected void finalize() throws Throwable {

    	super.finalize();
    	
    	closeConnection();
    	
    }

}

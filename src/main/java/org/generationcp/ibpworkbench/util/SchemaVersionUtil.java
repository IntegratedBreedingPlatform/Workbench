/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.

 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.util;

import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.ibpworkbench.database.IBDBGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

@Configurable
public class SchemaVersionUtil {
	
    public static final String MINIMUM_CROP_VERSION = "gcp.minimum.crop.version";
    private final static String WORKBENCH_PROP_FILEPATH = IBDBGenerator.WORKBENCH_PROP;
    
    private static final Logger LOG = LoggerFactory.getLogger(SchemaVersionUtil.class);

	private SchemaVersionUtil() {
		//Utility class should have a private constructor
	}
	
	public static boolean checkIfVersionIsSupported(String currentCropVersion, String minimumCropVersion) {
		StringTokenizer currentTokens = new StringTokenizer(
    			currentCropVersion==null?"":currentCropVersion,".");
		StringTokenizer minimumTokens = new StringTokenizer(
				minimumCropVersion==null?"":minimumCropVersion,".");
		return parseAndCompareTokens(currentTokens,minimumTokens);
	}

	private static boolean parseAndCompareTokens(StringTokenizer currentTokens,
			StringTokenizer minimumTokens) {
		while(currentTokens.hasMoreTokens() && minimumTokens.hasMoreTokens()) {
			String currentToken = currentTokens.nextToken();
			String minimumToken = minimumTokens.nextToken();
			if(currentToken.toUpperCase().compareTo(minimumToken.toUpperCase())<0) {
				return false;
			} else if(currentToken.toUpperCase().compareTo(minimumToken.toUpperCase())>0) {
				return true;
			}
		}
		if(minimumTokens.hasMoreTokens()) {
			return false;
		}
		return true;
	}

	public static Properties loadPropertiesFile(String filename) {
		Properties properties = null;
		InputStream in = null;
		try {
			try {
	            in = new FileInputStream(new File(ResourceFinder.locateFile(filename).toURI()));
	        } catch (IllegalArgumentException ex) {
	        	LOG.error(ex.getMessage(),ex);
	        	//It is expected that a property file can be retrieved as a resource instead
	            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	        } catch (Exception ex) {
				LOG.error(ex.getMessage(),ex);
				//return null if properties file cannot be found
				return null;
			}
		
			try {
	        	if(in!=null) {
	        		properties = new Properties();
	    			properties.load(in);
	        	}
	        } catch(Exception e) {
	        	LOG.error(e.getMessage(),e);
	        	//return null if properties file cannot be loaded
	        	return null;
	        } 
		} finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // exception not thrown intentionally
                	LOG.error(e.getMessage(),e);
                }
            }
        }
        return properties;
	}
	
	public static String getMinimumCropVersion() {
		return getMinimumCropVersionFromFile(WORKBENCH_PROP_FILEPATH);
	}
	
	public static String getMinimumCropVersionFromFile(String workbenchPropertiesFilepath) {
		String minimumCropVersion = null;
		Properties properties = loadPropertiesFile(workbenchPropertiesFilepath);
		if(properties!=null) {
			String version = properties.getProperty(MINIMUM_CROP_VERSION, "");
			if(version!=null) {
	            minimumCropVersion = version.trim();
	        }
		}
        return minimumCropVersion;        
	}
}

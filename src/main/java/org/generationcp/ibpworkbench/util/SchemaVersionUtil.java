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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.generationcp.commons.util.ResourceFinder;
import org.generationcp.ibpworkbench.database.IBDBGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class SchemaVersionUtil {
	
	private static Logger LOG = LoggerFactory.getLogger(SchemaVersionUtil.class);

	public static boolean checkIfVersionIsSupported(String currentCropVersion, String minimumCropVersion) {
		//check BMS minimum support crop version against the crop version of the project
    	//if crop version is below the minimum supported crop version, display a warning
    	if(currentCropVersion==null || currentCropVersion.trim().equals("")) {
    		return false;
    	}
    	StringTokenizer currentTokens = new StringTokenizer(currentCropVersion,".");
		StringTokenizer minimumTokens = new StringTokenizer(minimumCropVersion,".");
		
		String currentToken = "";
		String minimumToken = "";
		while(currentTokens.hasMoreTokens()) {
			currentToken = currentTokens.nextToken();
			if(minimumTokens.hasMoreTokens()) {
				minimumToken = minimumTokens.nextToken();
				try {
					if(Integer.parseInt(currentToken) < Integer.parseInt(minimumToken)) {
						return false;
					}
				} catch(NumberFormatException e) {
					LOG.error(e.getMessage(),e);
					return false;
				}
			}
		}
		if(minimumTokens.hasMoreTokens()) {
			try {
				if(Integer.parseInt(currentToken) == Integer.parseInt(minimumToken)) {
					return false;
				}
			} catch(NumberFormatException e) {
				LOG.error(e.getMessage(),e);
				return false;
			}
		}
		return true;
	}
	
	public static String getMinimumCropVersion() {
		String minimumCropVersion = null;
		Properties properties = new Properties();
		InputStream in = null;
        try {
        	try {
                in = new FileInputStream(new File(ResourceFinder.locateFile(IBDBGenerator.WORKBENCH_PROP).toURI()));
	        } catch (IllegalArgumentException ex) {
	            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(IBDBGenerator.WORKBENCH_PROP);
	        } 
        	if(in!=null) {
        		properties.load(in);
        	}
        } catch(Exception e) {
        	LOG.error(e.getMessage(),e);
        	return minimumCropVersion;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // intentionally empty
                }
            }
        }
        String version = properties.getProperty("gcp.minimum.crop.version", "");
        LOG.debug("gcp.minimum.crop.version="+version);
        if(version!=null) {
            minimumCropVersion = version.trim();
        }
        return minimumCropVersion;        
	}
}

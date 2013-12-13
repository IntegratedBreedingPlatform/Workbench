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
package org.generationcp.ibpworkbench.model;

import java.io.Serializable;


/**
 * <b>Description</b>: A Refined Location POJO for Workbench use.
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Jeffrey Morales
 * <br>
 * <b>File Created</b>: August 30, 2012
 */
public class SeaEnvironmentModel implements Serializable{

    
    /**
     * 
     */
    private static final long serialVersionUID = 9097569778961073968L;
    
    private String environmentName;
    private String trialno;
    private Boolean active;
    
    
    
    /**
     * Initialize fields so that the "null" String value does not appear.
     */
    public SeaEnvironmentModel() {
        setEnvironmentName("");
        setTrialno("");

    }
   

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}


	public String getEnvironmentName() {
		return environmentName;
	}


	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}


	public String getTrialno() {
		return trialno;
	}


	public void setTrialno(String trialno) {
		this.trialno = trialno;
	}

}

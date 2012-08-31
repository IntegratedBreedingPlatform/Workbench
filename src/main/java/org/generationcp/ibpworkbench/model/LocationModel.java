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
public class LocationModel implements Serializable{

    
    /**
     * 
     */
    private static final long serialVersionUID = 9097569778961073968L;
    
    private Integer locationId;
    private String locationName;
    private String locationAbbreviation;
    
    /**
     * Initialize fields so that the "null" String value does not appear.
     */
    public LocationModel() {
        locationName = "";
        locationAbbreviation = "";

    }
    
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAbbreviation() {
        return locationAbbreviation;
    }

    public void setLocationAbbreviation(String locationAbbreviation) {
        this.locationAbbreviation = locationAbbreviation;
    }

    public void trimAll() {
        locationName = locationName.trim();
        locationAbbreviation = locationAbbreviation.trim();
    }

}

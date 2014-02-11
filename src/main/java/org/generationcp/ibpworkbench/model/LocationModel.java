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

import org.generationcp.middleware.pojos.Location;

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
    private Integer ltype;
    private Integer cntryid;
    
    
    
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

	public Integer getCntryid() {
		return cntryid;
	}

	public void setCntryid(Integer cntryid) {
		this.cntryid = (cntryid != null) ? cntryid : 0;
    }

	public Integer getLtype() {
		return ltype;
	}

	public void setLtype(Integer ltype) {
        this.ltype  = (ltype != null) ? ltype : 0;
    }

    public Location toLocation() {
        Location location = new Location();
        location.setLrplce(0);

        location.setLocid(this.getLocationId());
        location.setLname(this.getLocationName());
        location.setLabbr(this.getLocationAbbreviation());
        location.setLtype(this.getLtype());
        location.setCntryid(this.getCntryid());

        // defaults
        location.setNllp(0);
        location.setSnl1id(0);
        location.setSnl2id(0);
        location.setSnl3id(0);

        return location;
    }
}

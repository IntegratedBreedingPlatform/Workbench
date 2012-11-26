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
public class VariateModel implements Serializable{
    
    
    /**
     * 
     */
    private static final long serialVersionUID = -6817357350462208956L;
   
    
    private Integer id;
    private String name;
    private Integer traitid;
    private String trname;
    private Integer scaleid;
    private String scname;
    private Integer tmethid;
    private String tmname;
    
    
    /**
     * Initialize fields so that the "null" String value does not appear.
     */
    public VariateModel() {
        name = "";
        trname = "";
        scname = "";
        tmname = "";

    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Integer getTraitid() {
        return traitid;
    }


    public void setTraitid(Integer traitid) {
        this.traitid = traitid;
    }


    public String getTrname() {
        return trname;
    }


    public void setTrname(String trname) {
        this.trname = trname;
    }


    public Integer getScaleid() {
        return scaleid;
    }


    public void setScaleid(Integer scaleid) {
        this.scaleid = scaleid;
    }


    public String getScname() {
        return scname;
    }


    public void setScname(String scname) {
        this.scname = scname;
    }


    public Integer getTmethid() {
        return tmethid;
    }


    public void setTmethid(Integer tmethid) {
        this.tmethid = tmethid;
    }


    public String getTmname() {
        return tmname;
    }


    public void setTmname(String tmname) {
        this.tmname = tmname;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((scaleid == null) ? 0 : scaleid.hashCode());
        result = prime * result + ((scname == null) ? 0 : scname.hashCode());
        result = prime * result + ((tmethid == null) ? 0 : tmethid.hashCode());
        result = prime * result + ((tmname == null) ? 0 : tmname.hashCode());
        result = prime * result + ((traitid == null) ? 0 : traitid.hashCode());
        result = prime * result + ((trname == null) ? 0 : trname.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VariateModel other = (VariateModel) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (scaleid == null) {
            if (other.scaleid != null)
                return false;
        } else if (!scaleid.equals(other.scaleid))
            return false;
        if (scname == null) {
            if (other.scname != null)
                return false;
        } else if (!scname.equals(other.scname))
            return false;
        if (tmethid == null) {
            if (other.tmethid != null)
                return false;
        } else if (!tmethid.equals(other.tmethid))
            return false;
        if (tmname == null) {
            if (other.tmname != null)
                return false;
        } else if (!tmname.equals(other.tmname))
            return false;
        if (traitid == null) {
            if (other.traitid != null)
                return false;
        } else if (!traitid.equals(other.traitid))
            return false;
        if (trname == null) {
            if (other.trname != null)
                return false;
        } else if (!trname.equals(other.trname))
            return false;
        return true;
    }

}

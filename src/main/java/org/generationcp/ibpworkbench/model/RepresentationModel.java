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


public class RepresentationModel implements Serializable{

    private static final long serialVersionUID = 3521757463492775303L;

    private Integer id;

    private Integer effectId;

    private String name;
    
    private String userFriendlyName;

    public RepresentationModel() {
    }

    public RepresentationModel(Integer id, Integer effectId, String name, String userFriendlyName) {
        super();
        this.id = id;
        this.effectId = effectId;
        this.name = name;
        this.userFriendlyName = userFriendlyName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEffectId() {
        return effectId;
    }

    public void setEffectId(Integer effectId) {
        this.effectId = effectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }

    public void setUserFriendlyName(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((effectId == null) ? 0 : effectId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime
                * result
                + ((userFriendlyName == null) ? 0 : userFriendlyName.hashCode());
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
        RepresentationModel other = (RepresentationModel) obj;
        if (effectId == null) {
            if (other.effectId != null)
                return false;
        } else if (!effectId.equals(other.effectId))
            return false;
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
        if (userFriendlyName == null) {
            if (other.userFriendlyName != null)
                return false;
        } else if (!userFriendlyName.equals(other.userFriendlyName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RepresentationModel [id=" + id + ", effectId=" + effectId
                + ", name=" + name + ", userFriendlyName=" + userFriendlyName
                + "]";
    }

}

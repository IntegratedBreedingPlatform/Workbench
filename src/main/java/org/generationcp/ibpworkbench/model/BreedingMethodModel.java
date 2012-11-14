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
public class BreedingMethodModel implements Serializable{
    
    
    /**
     * 
     */
    private static final long serialVersionUID = -6817357350462208956L;
   
    
    private Integer methodId;
    private String methodName;
    private String methodDescription;
    private String methodType;
    private String methodGroup;
    private String methodCode;
    
    
    /**
     * Initialize fields so that the "null" String value does not appear.
     */
    public BreedingMethodModel() {
        methodName = "";
        methodDescription = "";
        methodType = "";
        methodGroup = "";
        methodCode = "";

    }

    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }
    
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodGroup() {
        return methodGroup;
    }

    public void setMethodGroup(String methodGroup) {
        this.methodGroup = methodGroup;
    }
    
    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }
    
    public void trimAll() {
        methodName = methodName.trim();
        methodDescription = methodDescription.trim();
        methodType = methodType.trim();
        methodGroup = methodGroup.trim();
        methodCode = methodCode.trim();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((methodCode == null) ? 0 : methodCode.hashCode());
        result = prime
                * result
                + ((methodDescription == null) ? 0 : methodDescription
                        .hashCode());
        result = prime * result
                + ((methodGroup == null) ? 0 : methodGroup.hashCode());
        result = prime * result
                + ((methodId == null) ? 0 : methodId.hashCode());
        result = prime * result
                + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result
                + ((methodType == null) ? 0 : methodType.hashCode());
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
        BreedingMethodModel other = (BreedingMethodModel) obj;
        if (methodCode == null) {
            if (other.methodCode != null)
                return false;
        } else if (!methodCode.equals(other.methodCode))
            return false;
        if (methodDescription == null) {
            if (other.methodDescription != null)
                return false;
        } else if (!methodDescription.equals(other.methodDescription))
            return false;
        if (methodGroup == null) {
            if (other.methodGroup != null)
                return false;
        } else if (!methodGroup.equals(other.methodGroup))
            return false;
        if (methodId == null) {
            if (other.methodId != null)
                return false;
        } else if (!methodId.equals(other.methodId))
            return false;
        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;
        if (methodType == null) {
            if (other.methodType != null)
                return false;
        } else if (!methodType.equals(other.methodType))
            return false;
        return true;
    }


}

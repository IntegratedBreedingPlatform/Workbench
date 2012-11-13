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
    private String methodCode;
    
    
    /**
     * Initialize fields so that the "null" String value does not appear.
     */
    public BreedingMethodModel() {
        methodName = "";
        methodDescription = "";
        methodType = "";
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
        methodCode = methodCode.trim();
    }

}

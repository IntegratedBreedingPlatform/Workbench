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

public class FieldBookObservation implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer plotNo;
    private Integer rep;
    private Integer mainPlot;
    private Integer subPlot;
    private String variety;
    private Integer gid;
    private Integer fertilizer;
    private Double yield;
    private Integer pht;

    public Integer getPlotNo() {
        return plotNo;
    }

    public void setPlotNo(Integer plotNo) {
        this.plotNo = plotNo;
    }

    public Integer getRep() {
        return rep;
    }

    public void setRep(Integer rep) {
        this.rep = rep;
    }

    public Integer getMainPlot() {
        return mainPlot;
    }

    public void setMainPlot(Integer mainPlot) {
        this.mainPlot = mainPlot;
    }

    public Integer getSubPlot() {
        return subPlot;
    }

    public void setSubPlot(Integer subPlot) {
        this.subPlot = subPlot;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public Integer getGid() {
        return gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public Integer getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(Integer fertilizer) {
        this.fertilizer = fertilizer;
    }

    public Double getYield() {
        return yield;
    }

    public void setYield(Double yield) {
        this.yield = yield;
    }

    public Integer getPht() {
        return pht;
    }

    public void setPht(Integer pht) {
        this.pht = pht;
    }
}

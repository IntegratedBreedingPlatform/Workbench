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

import java.io.Serializable;
import java.util.HashMap;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.middleware.pojos.workbench.Project;

/**
 * 
 * <br>
 * <br>
 * 
 * <b>Author</b>: Jeffrey Morales
 * <br>
 * <b>File Created</b>:
 */
public class BreedingViewInput implements Serializable {

    private static final long serialVersionUID = 7669967119863861617L;
    
    private Project project;
    private String BreedingViewProjectName;
    private Integer studyId;
    private Integer inputDatasetId;
    private Integer outputDatasetId;
    private String environmentName;
    private String version;
    private String sourceXLSFilePath;
    private String destXMLFilePath;
    private String projectType;
    private String designType;
    private Blocks blocks;
    private Replicates replicates;
    private Genotypes genotypes;
    private Rows rows;
    private Columns columns;
    private Environment environment;
    private HashMap<Integer, String> variateColumns;
    private String datasetName;
    private String datasetSource;
    private HashMap<String, Boolean> variatesActiveState;
    private HashMap<String, Boolean> environmentsActiveState;
    
    public HashMap<Integer, String> getVariateColumns() {
		return variateColumns;
	}

	public void setVariateColumns(HashMap<Integer, String> variateColumns) {
		this.variateColumns = variateColumns;
	}

	public BreedingViewInput(Project project, String breedingViewProjectName, Integer studyId,
            Integer datasetId, String version,
            String sourceXLSFilePath, String destXMLFilePath,
            String projectType) {
        super();
        this.project = project;
        BreedingViewProjectName = breedingViewProjectName;
        this.studyId = studyId;
        this.inputDatasetId = datasetId;
        this.version = version;
        this.sourceXLSFilePath = sourceXLSFilePath;
        this.destXMLFilePath = destXMLFilePath;
        this.projectType = projectType;
        this.blocks = null;
        this.rows = null;
        this.columns = null;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getBreedingViewProjectName() {
        return BreedingViewProjectName;
    }

    public void setBreedingViewProjectName(String breedingViewProjectName) {
        BreedingViewProjectName = breedingViewProjectName;
    }

    public Integer getStudyId() {
        return studyId;
    }

    public void setStudyId(Integer studyId) {
        this.studyId = studyId;
    }

    public Integer getDatasetId() {
        return inputDatasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.inputDatasetId = datasetId;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSourceXLSFilePath() {
        return sourceXLSFilePath;
    }

    public void setSourceXLSFilePath(String sourceXLSFilePath) {
        this.sourceXLSFilePath = sourceXLSFilePath;
    }

    public String getDestXMLFilePath() {
        return destXMLFilePath;
    }

    public void setDestXMLFilePath(String destXMLFilePath) {
        this.destXMLFilePath = destXMLFilePath;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getDesignType() {
        return designType;
    }

    public void setDesignType(String designType) {
        this.designType = designType;
    }

    public Blocks getBlocks() {
        return blocks;
    }

    public void setBlocks(Blocks blocks) {
        this.blocks = blocks;
    }

    public Replicates getReplicates() {
        return replicates;
    }

    public void setReplicates(Replicates replicates) {
        this.replicates = replicates;
    }

    public Genotypes getGenotypes() {
        return genotypes;
    }
    
    public void setGenotypes(Genotypes genotypes) {
        this.genotypes = genotypes;
    }
    
    public Rows getRows() {
        return rows;
    }
    
    public void setRows(Rows rows) {
        this.rows = rows;
    }
    
    public Columns getColumns() {
        return columns;
    }
    
    public void setColumns(Columns columns) {
        this.columns = columns;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((BreedingViewProjectName == null) ? 0
                        : BreedingViewProjectName.hashCode());
        result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
        result = prime * result
                + ((inputDatasetId == null) ? 0 : inputDatasetId.hashCode());
        result = prime * result
                + ((designType == null) ? 0 : designType.hashCode());
        result = prime * result
                + ((destXMLFilePath == null) ? 0 : destXMLFilePath.hashCode());
        result = prime * result
                + ((environmentName == null) ? 0 : environmentName.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result
                + ((projectType == null) ? 0 : projectType.hashCode());
        result = prime * result
                + ((replicates == null) ? 0 : replicates.hashCode());
        result = prime
                * result
                + ((sourceXLSFilePath == null) ? 0 : sourceXLSFilePath
                        .hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        BreedingViewInput other = (BreedingViewInput) obj;
        if (BreedingViewProjectName == null) {
            if (other.BreedingViewProjectName != null)
                return false;
        } else if (!BreedingViewProjectName
                .equals(other.BreedingViewProjectName))
            return false;
        if (blocks == null) {
            if (other.blocks != null)
                return false;
        } else if (!blocks.equals(other.blocks))
            return false;
        if (inputDatasetId == null) {
            if (other.inputDatasetId != null)
                return false;
        } else if (!inputDatasetId.equals(other.inputDatasetId))
            return false;
        if (designType == null) {
            if (other.designType != null)
                return false;
        } else if (!designType.equals(other.designType))
            return false;
        if (destXMLFilePath == null) {
            if (other.destXMLFilePath != null)
                return false;
        } else if (!destXMLFilePath.equals(other.destXMLFilePath))
            return false;
        if (environmentName == null) {
            if (other.environmentName != null)
                return false;
        } else if (!environmentName.equals(other.environmentName))
            return false;
        if (project == null) {
            if (other.project != null)
                return false;
        } else if (!project.equals(other.project))
            return false;
        if (projectType == null) {
            if (other.projectType != null)
                return false;
        } else if (!projectType.equals(other.projectType))
            return false;
        if (replicates == null) {
            if (other.replicates != null)
                return false;
        } else if (!replicates.equals(other.replicates))
            return false;
        if (sourceXLSFilePath == null) {
            if (other.sourceXLSFilePath != null)
                return false;
        } else if (!sourceXLSFilePath.equals(other.sourceXLSFilePath))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BreedingViewInput [project=" + project
                + ", BreedingViewProjectName=" + BreedingViewProjectName
                + ", datasetId=" + inputDatasetId + ", environmentName="
                + environmentName + ", version=" + version
                + ", sourceXLSFilePath=" + sourceXLSFilePath
                + ", destXMLFilePath=" + destXMLFilePath + ", projectType="
                + projectType + ", designType=" + designType + ", blocks="
                + blocks + ", replicates=" + replicates + "]";
    }

	public Integer getOutputDatasetId() {
		return outputDatasetId;
	}

	public void setOutputDatasetId(Integer outputDatasetId) {
		this.outputDatasetId = outputDatasetId;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public String getDatasetSource() {
		return datasetSource;
	}

	public void setDatasetSource(String datasetSource) {
		this.datasetSource = datasetSource;
	}

	public HashMap<String, Boolean> getVariatesActiveState() {
		return variatesActiveState;
	}

	public void setVariatesActiveState(HashMap<String, Boolean> variateActiveStates) {
		this.variatesActiveState = variateActiveStates;
	}

	public HashMap<String, Boolean> getEnvironmentsActiveState() {
		return environmentsActiveState;
	}

	public void setEnvironmentsActiveState(HashMap<String, Boolean> environmentActiveStates) {
		this.environmentsActiveState = environmentActiveStates;
	}

    

    
}

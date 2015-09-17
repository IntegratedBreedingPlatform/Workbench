/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.util;

import java.io.Serializable;
import java.util.List;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.sea.xml.Heritabilities;
import org.generationcp.middleware.pojos.workbench.Project;

/**
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Jeffrey Morales <br>
 * <b>File Created</b>:
 */
public class GxeInput implements Serializable {

	private static final long serialVersionUID = 7669967119863861617L;

	private Project project;
	private String BreedingViewProjectName;
	private Integer studyId;
	private Integer datasetId;
	private String environmentName;
	private String environmentGroup;
	private String version;
	private String sourceXLSFilePath;
	private String sourceCSVFilePath;
	private String sourceCSVSummaryStatsFilePath;
	private String destXMLFilePath;
	private String projectType;
	private String designType;
	private Blocks blocks;
	private Replicates replicates;
	private Genotypes genotypes;
	private Rows rows;
	private Columns columns;
	private GxeEnvironment environment;
	private Heritabilities heritabilities;
	private List<Trait> traits;
	private List<org.generationcp.commons.sea.xml.Environment> selectedEnvironments;

	public GxeInput(Project project, String breedingViewProjectName, Integer studyId, Integer datasetId, String version,
			String sourceXLSFilePath, String destXMLFilePath, String projectType) {
		super();
		this.project = project;
		this.BreedingViewProjectName = breedingViewProjectName;
		this.studyId = studyId;
		this.datasetId = datasetId;
		this.version = version;
		this.sourceXLSFilePath = sourceXLSFilePath;
		this.destXMLFilePath = destXMLFilePath;
		this.projectType = projectType;
		this.blocks = null;
		this.rows = null;
		this.columns = null;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getBreedingViewProjectName() {
		return this.BreedingViewProjectName;
	}

	public void setBreedingViewProjectName(String breedingViewProjectName) {
		this.BreedingViewProjectName = breedingViewProjectName;
	}

	public Integer getStudyId() {
		return this.studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getDatasetId() {
		return this.datasetId;
	}

	public void setDatasetId(Integer datasetId) {
		this.datasetId = datasetId;
	}

	public String getEnvironmentName() {
		return this.environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSourceXLSFilePath() {
		return this.sourceXLSFilePath;
	}

	public void setSourceXLSFilePath(String sourceXLSFilePath) {
		this.sourceXLSFilePath = sourceXLSFilePath;
	}

	public String getDestXMLFilePath() {
		return this.destXMLFilePath;
	}

	public void setDestXMLFilePath(String destXMLFilePath) {
		this.destXMLFilePath = destXMLFilePath;
	}

	public String getProjectType() {
		return this.projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getDesignType() {
		return this.designType;
	}

	public void setDesignType(String designType) {
		this.designType = designType;
	}

	public Blocks getBlocks() {
		return this.blocks;
	}

	public void setBlocks(Blocks blocks) {
		this.blocks = blocks;
	}

	public Replicates getReplicates() {
		return this.replicates;
	}

	public void setReplicates(Replicates replicates) {
		this.replicates = replicates;
	}

	public Genotypes getGenotypes() {
		return this.genotypes;
	}

	public void setGenotypes(Genotypes genotypes) {
		this.genotypes = genotypes;
	}

	public Rows getRows() {
		return this.rows;
	}

	public void setRows(Rows rows) {
		this.rows = rows;
	}

	public Columns getColumns() {
		return this.columns;
	}

	public void setColumns(Columns columns) {
		this.columns = columns;
	}

	public GxeEnvironment getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(GxeEnvironment environment) {
		this.environment = environment;
	}

	public List<Trait> getTraits() {
		return this.traits;
	}

	public void setTraits(List<Trait> traits) {
		this.traits = traits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.BreedingViewProjectName == null ? 0 : this.BreedingViewProjectName.hashCode());
		result = prime * result + (this.blocks == null ? 0 : this.blocks.hashCode());
		result = prime * result + (this.datasetId == null ? 0 : this.datasetId.hashCode());
		result = prime * result + (this.designType == null ? 0 : this.designType.hashCode());
		result = prime * result + (this.destXMLFilePath == null ? 0 : this.destXMLFilePath.hashCode());
		result = prime * result + (this.environmentName == null ? 0 : this.environmentName.hashCode());
		result = prime * result + (this.project == null ? 0 : this.project.hashCode());
		result = prime * result + (this.projectType == null ? 0 : this.projectType.hashCode());
		result = prime * result + (this.replicates == null ? 0 : this.replicates.hashCode());
		result = prime * result + (this.sourceXLSFilePath == null ? 0 : this.sourceXLSFilePath.hashCode());
		result = prime * result + (this.version == null ? 0 : this.version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		GxeInput other = (GxeInput) obj;
		if (this.BreedingViewProjectName == null) {
			if (other.BreedingViewProjectName != null) {
				return false;
			}
		} else if (!this.BreedingViewProjectName.equals(other.BreedingViewProjectName)) {
			return false;
		}
		if (this.blocks == null) {
			if (other.blocks != null) {
				return false;
			}
		} else if (!this.blocks.equals(other.blocks)) {
			return false;
		}
		if (this.datasetId == null) {
			if (other.datasetId != null) {
				return false;
			}
		} else if (!this.datasetId.equals(other.datasetId)) {
			return false;
		}
		if (this.designType == null) {
			if (other.designType != null) {
				return false;
			}
		} else if (!this.designType.equals(other.designType)) {
			return false;
		}
		if (this.destXMLFilePath == null) {
			if (other.destXMLFilePath != null) {
				return false;
			}
		} else if (!this.destXMLFilePath.equals(other.destXMLFilePath)) {
			return false;
		}
		if (this.environmentName == null) {
			if (other.environmentName != null) {
				return false;
			}
		} else if (!this.environmentName.equals(other.environmentName)) {
			return false;
		}
		if (this.project == null) {
			if (other.project != null) {
				return false;
			}
		} else if (!this.project.equals(other.project)) {
			return false;
		}
		if (this.projectType == null) {
			if (other.projectType != null) {
				return false;
			}
		} else if (!this.projectType.equals(other.projectType)) {
			return false;
		}
		if (this.replicates == null) {
			if (other.replicates != null) {
				return false;
			}
		} else if (!this.replicates.equals(other.replicates)) {
			return false;
		}
		if (this.sourceXLSFilePath == null) {
			if (other.sourceXLSFilePath != null) {
				return false;
			}
		} else if (!this.sourceXLSFilePath.equals(other.sourceXLSFilePath)) {
			return false;
		}
		if (this.version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!this.version.equals(other.version)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "GxeInput [project=" + this.project + ", BreedingViewProjectName=" + this.BreedingViewProjectName + ", datasetId="
				+ this.datasetId + ", environmentName=" + this.environmentName + ", version=" + this.version + ", sourceXLSFilePath="
				+ this.sourceXLSFilePath + ", sourceCSVFilePath=" + this.sourceCSVFilePath + ", destXMLFilePath=" + this.destXMLFilePath
				+ ", projectType=" + this.projectType + ", designType=" + this.designType + ", blocks=" + this.blocks + ", replicates="
				+ this.replicates + "]";
	}

	public String getSourceCSVFilePath() {
		return this.sourceCSVFilePath;
	}

	public void setSourceCSVFilePath(String sourceCSVFilePath) {
		this.sourceCSVFilePath = sourceCSVFilePath;
	}

	public List<org.generationcp.commons.sea.xml.Environment> getSelectedEnvironments() {
		return this.selectedEnvironments;
	}

	public void setSelectedEnvironments(List<org.generationcp.commons.sea.xml.Environment> selectedEnvironments) {
		this.selectedEnvironments = selectedEnvironments;
	}

	public String getEnvironmentGroup() {
		return this.environmentGroup;
	}

	public void setEnvironmentGroup(String environmentGroup) {
		this.environmentGroup = environmentGroup;
	}

	public Heritabilities getHeritabilities() {
		return this.heritabilities;
	}

	public void setHeritabilities(Heritabilities heritabilities) {
		this.heritabilities = heritabilities;
	}

	public String getSourceCSVSummaryStatsFilePath() {
		return this.sourceCSVSummaryStatsFilePath;
	}

	public void setSourceCSVSummaryStatsFilePath(String sourceCSVSummaryStatsFilePath) {
		this.sourceCSVSummaryStatsFilePath = sourceCSVSummaryStatsFilePath;
	}

}

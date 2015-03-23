package org.generationcp.ibpworkbench.util.bean;

import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.pojos.workbench.Project;

public class MultiSiteParameters {

	private Project project;
	
	private Study study;
	
	private String selectedEnvironmentFactorName;
	
	private String selectedEnvGroupFactorName;
	
	private String selectedGenotypeFactorName;

	public String getSelectedEnvGroupFactorName() {
		return selectedEnvGroupFactorName;
	}

	public void setSelectedEnvGroupFactorName(String selectedEnvGroupFactorName) {
		this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
	}

	public String getSelectedGenotypeFactorName() {
		return selectedGenotypeFactorName;
	}

	public void setSelectedGenotypeFactorName(String selectedGenotypeFactorName) {
		this.selectedGenotypeFactorName = selectedGenotypeFactorName;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public String getSelectedEnvironmentFactorName() {
		return selectedEnvironmentFactorName;
	}

	public void setSelectedEnvironmentFactorName(String selectedEnvironmentFactorName) {
		this.selectedEnvironmentFactorName = selectedEnvironmentFactorName;
	}

	
	
}

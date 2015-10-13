
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.Set;

/**
 * BMS Information extracted from Breeding View's output file.
 * 
 * @author Aldrin Batac
 * 
 */
public class BMSOutputInformation {

	private int studyId;

	private int workbenchProjectId;

	private int inputDataSetId;

	private int outputDataSetId;

	private String environmentFactorName;

	private Set<String> environmentNames;

	public Set<String> getEnvironmentNames() {
		return this.environmentNames;
	}

	public void setEnvironmentNames(final Set<String> environmentNames) {
		this.environmentNames = environmentNames;
	}

	public String getEnvironmentFactorName() {
		return this.environmentFactorName;
	}

	public void setEnvironmentFactorName(final String environmentFactorName) {
		this.environmentFactorName = environmentFactorName;
	}

	public int getWorkbenchProjectId() {
		return this.workbenchProjectId;
	}

	public void setWorkbenchProjectId(final int workbenchProjectId) {
		this.workbenchProjectId = workbenchProjectId;
	}

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(final int studyId) {
		this.studyId = studyId;
	}

	public int getInputDataSetId() {
		return this.inputDataSetId;
	}

	public void setInputDataSetId(final int inputDataSetId) {
		this.inputDataSetId = inputDataSetId;
	}

	public int getOutputDataSetId() {
		return this.outputDataSetId;
	}

	public void setOutputDataSetId(final int outputDataSetId) {
		this.outputDataSetId = outputDataSetId;
	}

}

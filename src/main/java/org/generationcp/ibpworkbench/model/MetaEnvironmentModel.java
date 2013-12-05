package org.generationcp.ibpworkbench.model;

public class MetaEnvironmentModel {

	public MetaEnvironmentModel() {
		// TODO Auto-generated constructor stub
	}
	
	public int getStudyId() {
		return studyId;
	}
	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getStudyName() {
		return studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public int getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(int dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getTrial() {
		return trial;
	}

	public void setTrial(String trial) {
		this.trial = trial;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	private int studyId;
	private String studyName;
	private int dataSetId;
	private String dataSetName;
	private String trial;
	private String environment;
	

}

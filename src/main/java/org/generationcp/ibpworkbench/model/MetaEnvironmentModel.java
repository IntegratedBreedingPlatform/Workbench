package org.generationcp.ibpworkbench.model;

import org.generationcp.middleware.pojos.dms.DmsProject;

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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		MetaEnvironmentModel other = (MetaEnvironmentModel) obj;

		if (studyId != other.studyId)
			return false;
		if (dataSetId != other.dataSetId)
			return false;
		if (!trial.equals(other.trial))
			return false;
		
		return true;
	}
	
	 @Override
	    public int hashCode() {
	        return this.studyId;
	    }
	

}

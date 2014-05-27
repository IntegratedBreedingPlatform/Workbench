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
	private String trialFactorName;
	private Boolean active;
	private int dataSetTypeId;
	
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

	public String getTrialFactorName() {
		return trialFactorName;
	}

	public void setTrialFactorName(String trialFactorName) {
		this.trialFactorName = trialFactorName;
	}

	public String getKey() {
		// TODO Auto-generated method stub
		return String.format("%s%s%s", studyId, dataSetId, trial) ;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public int getDataSetTypeId() {
		return dataSetTypeId;
	}

	public void setDataSetTypeId(int dataSetTypeId) {
		this.dataSetTypeId = dataSetTypeId;
	}
	

}

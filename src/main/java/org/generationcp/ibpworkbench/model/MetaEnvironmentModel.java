package org.generationcp.ibpworkbench.model;

public class MetaEnvironmentModel {

	private int studyId;
	private String studyName;
	private int dataSetId;
	private String dataSetName;
	private String trial;
	private String environment;
	private String trialFactorName;
	private Boolean active;
	private int dataSetTypeId;

	/**
	 * Default Constructor, used by MetaAnalysisPanel to create a bean instance
	 */
	public MetaEnvironmentModel() {
		// default constructor
	}

	public int getStudyId() {
		return this.studyId;
	}

	public void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	public String getStudyName() {
		return this.studyName;
	}

	public void setStudyName(String studyName) {
		this.studyName = studyName;
	}

	public int getDataSetId() {
		return this.dataSetId;
	}

	public void setDataSetId(int dataSetId) {
		this.dataSetId = dataSetId;
	}

	public String getDataSetName() {
		return this.dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getTrial() {
		return this.trial;
	}

	public void setTrial(String trial) {
		this.trial = trial;
	}

	public String getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
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

		MetaEnvironmentModel other = (MetaEnvironmentModel) obj;

		if (this.studyId != other.studyId) {
			return false;
		}
		if (this.dataSetId != other.dataSetId) {
			return false;
		}
		return this.trial.equals(other.trial);

	}

	@Override
	public int hashCode() {
		return this.studyId;
	}

	public String getTrialFactorName() {
		return this.trialFactorName;
	}

	public void setTrialFactorName(String trialFactorName) {
		this.trialFactorName = trialFactorName;
	}

	public String getKey() {
		// TODO Auto-generated method stub
		return String.format("%s%s%s", this.studyId, this.dataSetId, this.trial);
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public int getDataSetTypeId() {
		return this.dataSetTypeId;
	}

	public void setDataSetTypeId(int dataSetTypeId) {
		this.dataSetTypeId = dataSetTypeId;
	}

}

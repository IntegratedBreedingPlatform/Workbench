package org.generationcp.ibpworkbench.study.constants;


public enum StudyTypeFilter {
	ALL("All", "Studies"), TRIAL("Trial", "Trials"), NURSERY("Nursery", "Nurseries");
	
	private String label;
	private String description;
	
	private StudyTypeFilter(final String label, final String description) {
		this.label = label;
		this.description = description;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public String getDescription() {
		return this.description;
	}
}

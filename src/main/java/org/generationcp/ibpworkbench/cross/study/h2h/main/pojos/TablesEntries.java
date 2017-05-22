
package org.generationcp.ibpworkbench.cross.study.h2h.main.pojos;

public class TablesEntries {

	private String testEntryName;
	private String standardEntryName;
	private String testStandardEntry;
	private String testEntryGID;
	private String standardEntryGID;
	private String testEntryGroupID;
	private String standardEntryGroupID;
	
	public TablesEntries(String testEntryName, String standardEntryName, String testStandardEntry, String testEntryGID2,
			String standardEntryGID2, String testEntryGroupID, String standardEntryGroupID) {
		this.testEntryName = testEntryName;
		this.standardEntryName = standardEntryName;
		this.testStandardEntry = testStandardEntry;
		this.testEntryGID = testEntryGID2;
		this.standardEntryGID = standardEntryGID2;
		this.testEntryGroupID = testEntryGroupID;
		this.standardEntryGroupID = standardEntryGroupID;
	}
	
	public String getTestEntryName() {
		return this.testEntryName;
	}

	public void setTestEntryName(String testEntryName) {
		this.testEntryName = testEntryName;
	}

	public String getStandardEntryName() {
		return this.standardEntryName;
	}

	public void setStandardEntryName(String standardEntryName) {
		this.standardEntryName = standardEntryName;
	}

	public String getTestStandardEntry() {
		return this.testStandardEntry;
	}

	public void setTestStandardEntry(String testStandardEntry) {
		this.testStandardEntry = testStandardEntry;
	}
	
	public String getTestEntryGID() {
		return testEntryGID;
	}

	public void setTestEntryGID(String testEntryGID) {
		this.testEntryGID = testEntryGID;
	}

	public String getStandardEntryGID() {
		return standardEntryGID;
	}

	public void setStandardEntryGID(String standardEntryGID) {
		this.standardEntryGID = standardEntryGID;
	}

	public String getTestEntryGroupID() {
		return testEntryGroupID;
	}

	public void setTestEntryGroupID(String testEntryGroupID) {
		this.testEntryGroupID = testEntryGroupID;
	}

	public String getStandardEntryGroupID() {
		return standardEntryGroupID;
	}

	public void setStandardEntryGroupID(String standardEntryGroupID) {
		this.standardEntryGroupID = standardEntryGroupID;
	}
}

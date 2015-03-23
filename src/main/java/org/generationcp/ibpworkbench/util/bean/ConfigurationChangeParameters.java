package org.generationcp.ibpworkbench.util.bean;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 2/12/2015
 * Time: 3:16 PM
 *
 * Class to abstract the parameters used when changing the configuration parameters for a given tool within a given project
 */

public class ConfigurationChangeParameters {
	private String propertyFile;
	private String dbName;
	private String userName;
	private String password;
	private boolean includeWorkbenchConfig;
	private boolean includeCurrentProjectId;
	private boolean includeOldFieldbookPath;

	public ConfigurationChangeParameters(String propertyFile, String dbName, 
			String userName, String password, boolean includeWorkbenchConfig,
			boolean includeCurrentProjectId, boolean includeOldFieldbookPath) {
		this.propertyFile = propertyFile;
		this.dbName = dbName;
		this.userName = userName;
		this.password = password;
		this.includeWorkbenchConfig = includeWorkbenchConfig;
		this.includeCurrentProjectId = includeCurrentProjectId;
		this.includeOldFieldbookPath = includeOldFieldbookPath;
	}

	public String getPropertyFile() {
		return propertyFile;
	}

	public void setPropertyFile(String propertyFile) {
		this.propertyFile = propertyFile;
	}

	public String getDbName() {
		return dbName;
	}

	public void setLocalDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isIncludeWorkbenchConfig() {
		return includeWorkbenchConfig;
	}

	public void setIncludeWorkbenchConfig(boolean includeWorkbenchConfig) {
		this.includeWorkbenchConfig = includeWorkbenchConfig;
	}

	public boolean isIncludeCurrentProjectId() {
		return includeCurrentProjectId;
	}

	public void setIncludeCurrentProjectId(boolean includeCurrentProjectId) {
		this.includeCurrentProjectId = includeCurrentProjectId;
	}

	public boolean isIncludeOldFieldbookPath() {
		return includeOldFieldbookPath;
	}

	public void setIncludeOldFieldbookPath(boolean includeOldFieldbookPath) {
		this.includeOldFieldbookPath = includeOldFieldbookPath;
	}
}

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.util.bean.ConfigurationChangeParameters;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ToolUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ToolUtil.class);
	public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
	public static final String JDBC_FORMAT_STRING = "jdbc:mysql://%s:%s/%s";
	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	public static final String GDMS_CONFIG_LOCATION = "infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/DatabaseConfig.properties";
	public static final String MBDT_CONFIG_LOCATION = "tools/mbdt/DatabaseConfig.properties";
	public static final String WORKSPACE_DIR = "workspace";

	private String jdbcHost;
	private Long jdbcPort;
	private String centralUser;
	private String centralPassword;
	private String localUser;
	private String localPassword;
	private String workbenchDbName = "workbench";
	private String workbenchUser = "root";
	private String workbenchPassword = "";

	private String workbenchInstallationDirectory;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ContextUtil contextUtil;

	public String getJdbcHost() {
		return this.jdbcHost;
	}

	public void setJdbcHost(final String jdbcHost) {
		this.jdbcHost = jdbcHost;
	}

	public Long getJdbcPort() {
		return this.jdbcPort;
	}

	public void setJdbcPort(final Long jdbcPort) {
		this.jdbcPort = jdbcPort;
	}

	public String getCentralUser() {
		return this.centralUser;
	}

	public void setCentralUser(final String centralUser) {
		this.centralUser = centralUser;
	}

	public String getCentralPassword() {
		return this.centralPassword;
	}

	public void setCentralPassword(final String centralPassword) {
		this.centralPassword = centralPassword;
	}

	public String getLocalUser() {
		return this.localUser;
	}

	public void setLocalUser(final String localUser) {
		this.localUser = localUser;
	}

	public String getLocalPassword() {
		return this.localPassword;
	}

	public void setLocalPassword(final String localPassword) {
		this.localPassword = localPassword;
	}

	public String getWorkbenchDbName() {
		return this.workbenchDbName;
	}

	public void setWorkbenchDbName(final String workbenchDbName) {
		this.workbenchDbName = workbenchDbName;
	}

	public String getWorkbenchUser() {
		return this.workbenchUser;
	}

	public void setWorkbenchUser(final String workbenchUser) {
		this.workbenchUser = workbenchUser;
	}

	public String getWorkbenchPassword() {
		return this.workbenchPassword;
	}

	public void setWorkbenchPassword(final String workbenchPassword) {
		this.workbenchPassword = workbenchPassword;
	}

	public String getWorkbenchInstallationDirectory() {
		return this.workbenchInstallationDirectory;
	}

	public void setWorkbenchInstallationDirectory(final String installationDirectory) {
		this.workbenchInstallationDirectory = installationDirectory;
	}

	/**
	 * Launch the specified native tool.
	 *
	 * @param tool
	 * @return the {@link Process} object created when the tool was launched
	 * @throws IOException if an I/O error occurs while trying to launch the tool
	 * @throws IllegalArgumentException if the specified Tool's type is not {@link ToolType#NATIVE}
	 */
	public Process launchNativeTool(final Tool tool) throws IOException {
		if (tool.getToolType() != ToolType.NATIVE) {
			throw new IllegalArgumentException("Tool must be a native tool");
		}

		String parameter = "";
		if (!StringUtil.isEmpty(tool.getParameter())) {
			parameter = tool.getParameter();
		}

		final String toolPath = this.getComputedToolPath(tool);
		final File absoluteToolFile = new File(toolPath);

		final ProcessBuilder pb = new ProcessBuilder(toolPath, parameter);
		pb.directory(absoluteToolFile.getParentFile());
		return pb.start();
	}

	public void closeAllNativeTools() throws IOException {
		try {
			final List<Tool> nativeTools = this.workbenchDataManager.getToolsWithType(ToolType.NATIVE);

			for (final Tool tool : nativeTools) {
				this.closeNativeTool(tool);
			}

		} catch (final MiddlewareQueryException e) {
			ToolUtil.LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Close the specified native tool.
	 *
	 * @param tool
	 * @throws IOException if an I/O error occurs while trying to stop the tool
	 * @throws IllegalArgumentException if the specified Tool's type is not {@link ToolType#NATIVE}
	 */
	public void closeNativeTool(final Tool tool) throws IOException {
		if (tool.getToolType() != ToolType.NATIVE) {
			throw new IllegalArgumentException("Tool must be a native tool");
		}

		if (!SystemUtils.IS_OS_WINDOWS) {
			return;
		}

		final String toolPath = this.getComputedToolPath(tool);
		final File absoluteToolFile = new File(toolPath);
		final String[] pathTokens = toolPath.split("\\" + File.separator);

		final String executableName = pathTokens[pathTokens.length - 1];

		// taskkill /T /F /IM <exe name>
		final ProcessBuilder pb = new ProcessBuilder("taskkill", "/T", "/F", "/IM", executableName);
		pb.directory(absoluteToolFile.getParentFile());

		final Process process = pb.start();
		try {
			process.waitFor();
		} catch (final InterruptedException e) {
			ToolUtil.LOG.error("Interrupted while waiting for " + tool.getToolName() + " to stop.");
		}
	}

	protected String getComputedToolPath(final Tool tool) {
		String toolPath = tool.getPath();

		// if the tool path is an absolute path
		// and the workbench installation directory has been set,
		// launch the tool from the specified installation directory
		final int startIndex = toolPath.indexOf("tools");
		if (startIndex > 0 && this.workbenchInstallationDirectory != null) {
			toolPath = this.workbenchInstallationDirectory + File.separator + toolPath.substring(startIndex);
		}
		return toolPath;
	}

	protected boolean updatePropertyFile(final File propertyFile, final Map<String, String> newPropertyValues) {
		boolean changed = false;

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			// load the property files
			final Properties properties = new Properties();
			fis = new FileInputStream(propertyFile);
			properties.load(fis);

			// update the property values
			for (final String key : newPropertyValues.keySet()) {
				final String newValue = newPropertyValues.get(key);
				final String oldValue = properties.getProperty(key);

				final boolean equal = newValue == null ? oldValue == null : newValue.equals(oldValue);
				if (!equal) {
					changed = true;
					properties.setProperty(key, newValue);
				}
			}

			// close the file input stream

			// save the new property values
			if (changed) {
				fos = new FileOutputStream(propertyFile);
				properties.store(fos, null);
				fos.flush();
			}
		} catch (final IOException e1) {
			ToolUtil.LOG.error("Cannot update property file: " + propertyFile.getAbsolutePath(), e1);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					ToolUtil.LOG.error(e.getMessage(), e);
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (final IOException e) {
					ToolUtil.LOG.error(e.getMessage(), e);
				}
			}
		}

		return changed;
	}

	public File getConfigurationFile(final ConfigurationChangeParameters params) {
		return new File(params.getPropertyFile()).getAbsoluteFile();
	}

	public void createWorkspaceDirectoriesForProject(final Project project) {
		final WorkbenchSetting workbenchSetting = this.workbenchDataManager.getWorkbenchSetting();
		if (workbenchSetting == null) {
			return;
		}

		final String installationDirectory = workbenchSetting.getInstallationDirectory();

		// create the directory for the project
		final String projectDirName = project.getProjectName();
		final File projectDir = new File(installationDirectory + File.separator + ToolUtil.WORKSPACE_DIR, projectDirName);
		if (projectDir.exists()) {
			return;
		}
		projectDir.mkdirs();

		// create the directory only for breeding_view tool
		final List<String> toolList = Collections.singletonList(ToolEnum.BREEDING_VIEW.getToolName());
		for (final String toolName : toolList) {
			final File toolDir = new File(projectDir, toolName);
			toolDir.mkdirs();

			// create the input and output directories
			new File(toolDir, ToolUtil.INPUT).mkdirs();
			new File(toolDir, ToolUtil.OUTPUT).mkdirs();
		}
	}

	public void renameOldWorkspaceDirectoryToNewFormat(final long projectId, final String oldProjectName) {
		final WorkbenchSetting workbenchSetting = this.workbenchDataManager.getWorkbenchSetting();
		if (workbenchSetting == null) {
			return;
		}

		final String installationDirectory = workbenchSetting.getInstallationDirectory();

		final File oldDir = new File(installationDirectory + File.separator + ToolUtil.WORKSPACE_DIR,
				String.format("%d-%s", projectId, oldProjectName));

		if (oldDir.exists()) {
			oldDir.renameTo(new File(installationDirectory + File.separator + ToolUtil.WORKSPACE_DIR, String.format("%d", projectId)));
		}
	}

	public String getInputDirectoryForTool(final Project project, final Tool tool) {
		final WorkbenchSetting workbenchSetting = this.workbenchDataManager.getWorkbenchSetting();
		if (workbenchSetting == null) {
			throw new IllegalStateException("Workbench Setting record was not found!");
		}

		final String projectDirName = String.format("%s", project.getProjectName());

		final File projectDir = new File(ToolUtil.WORKSPACE_DIR, projectDirName);
		final File toolDir = new File(projectDir, tool.getGroupName());

		return new File(toolDir, ToolUtil.INPUT).getAbsolutePath();
	}

	public String getOutputDirectoryForTool(final Project project, final Tool tool) {
		final WorkbenchSetting workbenchSetting = this.workbenchDataManager.getWorkbenchSetting();
		if (workbenchSetting == null) {
			throw new IllegalStateException("Workbench Setting record was not found!");
		}

		final String projectDirName = String.format("%d", project.getProjectId());

		final String installationDirectory = workbenchSetting.getInstallationDirectory();
		final File projectDir = new File(installationDirectory + File.separator + ToolUtil.WORKSPACE_DIR, projectDirName);
		final File toolDir = new File(projectDir, tool.getGroupName());

		return new File(toolDir, ToolUtil.INPUT).getAbsolutePath();
	}
}

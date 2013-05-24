/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.

 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.poi.util.IOUtils;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.util.Util;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.window.ProgressWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Window;

@Configurable
public class ToolUtil {
    private Logger LOG = LoggerFactory.getLogger(ToolUtil.class);
    
	private String jdbcHost;
	private Long jdbcPort;
	private String centralUser;
	private String centralPassword;
	private String localUser;
	private String localPassword;

	private String workspaceDirectory = "workspace";

	public static String APPDATA_ENV = System.getenv("APPDATA");
	public static String WORKBENCH_R_DIR = "infrastructure/R";
	
	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	public String getJdbcHost() {
		return jdbcHost;
	}

	public void setJdbcHost(String jdbcHost) {
		this.jdbcHost = jdbcHost;
	}

	public Long getJdbcPort() {
		return jdbcPort;
	}

	public void setJdbcPort(Long jdbcPort) {
		this.jdbcPort = jdbcPort;
	}

	public String getCentralUser() {
		return centralUser;
	}

	public void setCentralUser(String centralUser) {
		this.centralUser = centralUser;
	}

	public String getCentralPassword() {
		return centralPassword;
	}

	public void setCentralPassword(String centralPassword) {
		this.centralPassword = centralPassword;
	}

	public String getLocalUser() {
		return localUser;
	}

	public void setLocalUser(String localUser) {
		this.localUser = localUser;
	}

	public String getLocalPassword() {
		return localPassword;
	}

	public void setLocalPassword(String localPassword) {
		this.localPassword = localPassword;
	}

	public String getWorkspaceDirectory() {
		return workspaceDirectory;
	}

	public void setWorkspaceDirectory(String workspaceDirectory) {
		this.workspaceDirectory = workspaceDirectory;
	}

	/**
	 * Launch the specified native tool.
	 * 
	 * @param tool
	 * @return the {@link Process} object created when the tool was launched
	 * @throws IOException
	 *             if an I/O error occurs while trying to launch the tool
	 * @throws IllegalArgumentException
	 *             if the specified Tool's type is not {@link ToolType#NATIVE}
	 */
    public Process launchNativeTool(Tool tool) throws IOException {
        if (tool.getToolType() != ToolType.NATIVE) {
            throw new IllegalArgumentException("Tool must be a native tool");
        }

        File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();

        Runtime runtime = Runtime.getRuntime();

        String parameter = "";
        if (!StringUtil.isEmpty(tool.getParameter())) {
            parameter = tool.getParameter();
        }
        return runtime.exec(new String[] { absoluteToolFile.getAbsolutePath(), parameter });
    }

	/**
	 * Close the specified native tool.
	 * 
	 * @param tool
	 * @throws IOException
	 *             if an I/O error occurs while trying to stop the tool
	 * @throws IllegalArgumentException
	 *             if the specified Tool's type is not {@link ToolType#NATIVE}
	 */
	public void closeNativeTool(Tool tool) throws IOException {
		if (tool.getToolType() != ToolType.NATIVE) {
			throw new IllegalArgumentException("Tool must be a native tool");
		}

		File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();
		String[] pathTokens = absoluteToolFile.getAbsolutePath().split(
				"\\" + File.separator);

		String executableName = pathTokens[pathTokens.length - 1];

		// taskkill /T /F /IM <exe name>
		ProcessBuilder pb = new ProcessBuilder("taskkill", "/T", "/F", "/IM",
				executableName);
		pb.directory(absoluteToolFile.getParentFile());

		pb.start();
	}
	
	public void updateTools(Window window, SimpleResourceBundleMessageSource messageSource, Project project, boolean ignoreLastOpenedProject) {
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        // don't do anything if the project is the last project opened
        if (app.getSessionData().isLastOpenedProject(project) && ignoreLastOpenedProject) {
            return;
        }
        
        // show a progress window
        ProgressWindow progressWindow = new ProgressWindow(messageSource.getMessage(Message.UPDATING_TOOLS_CONFIGURATION), 10 * 1000);
        progressWindow.setCaption(messageSource.getMessage(Message.UPDATING));
        progressWindow.setModal(true);
        progressWindow.setClosable(false);
        progressWindow.setResizable(false);
        progressWindow.center();
        
        window.addWindow(progressWindow);
        progressWindow.startProgress();
        
        // get all native tools
        List<Tool> nativeTools = null;
        try {
            nativeTools = workbenchDataManager.getToolsWithType(ToolType.NATIVE);
        }
        catch (MiddlewareQueryException e1) {
            LOG.error("QueryException", e1);
            MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        
        for (Tool tool : nativeTools) {
            // close the native tools
            try {
                closeNativeTool(tool);
            }
            catch (IOException e) {
                LOG.error("Exception", e);
            }
            
            // rewrite the configuration file
            try {
                updateToolConfigurationForProject(tool, project);
            }
            catch (IOException e) {
                LOG.error("Exception", e);
            }
        }
        
        // get web tools
        List<Tool> webTools = new ArrayList<Tool>();
        try {
            List<Tool> webTools1 = workbenchDataManager.getToolsWithType(ToolType.WEB);
            List<Tool> webTools2 = workbenchDataManager.getToolsWithType(ToolType.WEB_WITH_LOGIN);
            
            if (webTools1 != null) {
                webTools.addAll(webTools1);
            }
            if (webTools2 != null) {
                webTools.addAll(webTools2);
            }
        }
        catch (MiddlewareQueryException e2) {
            LOG.error("QueryException", e2);
            MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        
        for (Tool tool : webTools) {
            // rewrite the configuration file
            try {
                updateToolConfigurationForProject(tool, project);
            }
            catch (IOException e) {
                LOG.error("Exception", e);
            }
        }
    }

	/**
	 * Update the configuration of the specified {@link Tool} to the
	 * configuration needed by the specified {@link Project}.
	 * 
	 * @param tool
	 * @param project
	 * @throws IOException
	 */
	public void updateToolConfigurationForProject(Tool tool, Project project)
			throws IOException {
		String centralDbName = project.getCropType().getCentralDbName();
		String localDbName = String.format("%s_%d_local", project.getCropType()
				.getCropName().toLowerCase(), project.getProjectId());

		// get mysql user name and password to use
		String username = null;
		String password = null;
		String workbenchLoggedinUserId = "";

		IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

		if (app != null) {
			User currentUser = app.getSessionData().getUserData();

			if (currentUser != null) {
				try {
					ProjectUserMysqlAccount account = this.workbenchDataManager
							.getProjectUserMysqlAccountByProjectIdAndUserId(
									Integer.valueOf(project.getProjectId()
											.intValue()), currentUser
											.getUserid());
					username = account.getMysqlUsername();
					password = account.getMysqlPassword();

					workbenchLoggedinUserId = currentUser.getUserid()
							.toString();

				} catch (MiddlewareQueryException ex) {
					// do nothing, use the default central and local mysql user
					// accounts
				}
			}
		}

		
		if (Util.isOneOf(tool.getToolName(), ToolName.fieldbook.name())) {
			
			// save the location of R_HOME_FOLDER to "infrastracture/R" in the core.properties file
			File configFile = new File( APPDATA_ENV +  "/.ibfb/dev/config/Preferences/ibfb/settings/core.properties").getAbsoluteFile();
			File rDir = new File(WORKBENCH_R_DIR).getAbsoluteFile();
			
			Properties p = new Properties();
			FileInputStream fs = new FileInputStream(configFile);				
			p.load(fs);
			fs.close();
			p.setProperty("R_HOME_FOLDER",rDir.getCanonicalPath());
			this.savePropertyFile(p, configFile.getAbsolutePath());
			
			
			// Update databaseconfig.properties
			File configurationFile = new File("tools/" + tool.getToolName()
					+ "/IBFb/ibfb/modules/ext/databaseconfig.properties")
					.getAbsoluteFile();

			String format = "dmscentral.hibernateDialect=\r\n"
					+ "dmscentral.url=%s\r\n"
					+ "dmscentral.driverclassname=com.mysql.jdbc.Driver\r\n"
					+ "dmscentral.username=%s\r\n"
					+ "dmscentral.password=%s\r\n"
					+ "dmscentral.accessType=central\r\n" + ""
					+ "gmscentral.hibernateDialect=\r\n"
					+ "gmscentral.url=%s\r\n"
					+ "gmscentral.driverclassname=com.mysql.jdbc.Driver\r\n"
					+ "gmscentral.username=%s\r\n"
					+ "gmscentral.password=%s\r\n"
					+ "gmscentral.accessType=central\r\n" + "\r\n"
					+ "dmslocal.hibernateDialect=\r\n" + "dmslocal.url=%s\r\n"
					+ "dmslocal.driverclassname=com.mysql.jdbc.Driver\r\n"
					+ "dmslocal.username=%s\r\n" + "dmslocal.password=%s\r\n"
					+ "dmslocal.accessType=local\r\n" + ""
					+ "gmslocal.hibernateDialect=\r\n" + "gmslocal.url=%s\r\n"
					+ "gmslocal.driverclassname=com.mysql.jdbc.Driver\r\n"
					+ "gmslocal.username=%s\r\n" + "gmslocal.password=%s\r\n"
					+ "gmslocal.accessType=local\r\n"
					+ "\r\n"
					+ "workbench.currentUserId=%s\r\n";

			String jdbcFormat = "jdbc:mysql://%s:%s/%s";

			String centralJdbcString = String.format(jdbcFormat, jdbcHost,
					jdbcPort, centralDbName);
			String localJdbcString = String.format(jdbcFormat, jdbcHost,
					jdbcPort, localDbName);

			String configuration = "";

			if (!StringUtil.isEmptyOrWhitespaceOnly(username)
					&& !StringUtil.isEmptyOrWhitespaceOnly(password)) {
				configuration = String.format(format, centralJdbcString,
						username, password, centralJdbcString, username,
						password, localJdbcString, username, password,
						localJdbcString, username, password,
						workbenchLoggedinUserId);
			} else {
				configuration = String.format(format, centralJdbcString,
						centralUser, centralPassword, centralJdbcString,
						centralUser, centralPassword, localJdbcString,
						localUser, localPassword, localJdbcString, localUser,
						localPassword, workbenchLoggedinUserId);
			}

			FileOutputStream fos = new FileOutputStream(configurationFile);
			try {
				fos.write(configuration.getBytes());
				fos.flush();
			} catch (IOException e) {
				throw new IOException(e);
			} finally {
				fos.close();
			}
		} else if (Util.isOneOf(tool.getToolName(),
				ToolName.germplasm_browser.name())) {
			updateToolMiddlewareDatabaseConfiguration(
					"infrastructure/tomcat/webapps/GermplasmStudyBrowser/WEB-INF/classes/IBPDatasource.properties",
					centralDbName, localDbName, username, password);
		} else if (Util.isOneOf(tool.getToolName(),
				ToolName.list_manager.name())) {
		    // crossing manager uses the same property file
		    // so no need to update 
			updateToolMiddlewareDatabaseConfiguration(
					"infrastructure/tomcat/webapps/BreedingManager/WEB-INF/classes/IBPDatasource.properties",
					centralDbName, localDbName, username, password);
		} else if (Util.isOneOf(tool.getToolName(), ToolName.gdms.name())) {
			updateToolMiddlewareDatabaseConfiguration(
					"infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/DatabaseConfig.properties",
					centralDbName, localDbName, username, password);

			// update hibernate configuration
			String[] configurationFiles = new String[] {
					"infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/hibernate.cfg.xml",
					"infrastructure/tomcat/webapps/GDMS/WEB-INF/struts-config.xml" };
			String[] templateFiles = new String[] {
					"infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/hibernate.cfg.xml.template",
					"infrastructure/tomcat/webapps/GDMS/WEB-INF/struts-config.xml.template" };

			for (int index = 0; index < configurationFiles.length; index++) {
				File configurationFile = new File(configurationFiles[index])
						.getAbsoluteFile();
				File configurationFileTemplate = new File(templateFiles[index])
						.getAbsoluteFile();

				byte[] templateBytes = new byte[0];
				FileInputStream fis = new FileInputStream(
						configurationFileTemplate);
				try {
					templateBytes = IOUtils.toByteArray(fis);
				} finally {
					fis.close();
				}
				String templateStr = new String(templateBytes);

				String configuration = "";
				if (!StringUtil.isEmptyOrWhitespaceOnly(username)
						&& !StringUtil.isEmptyOrWhitespaceOnly(password)) {
					configuration = String.format(templateStr, jdbcHost,
							jdbcPort, localDbName, username, password);
				} else {
					configuration = String.format(templateStr, jdbcHost,
							jdbcPort, localDbName, localUser, localPassword);
				}

				FileOutputStream fos = new FileOutputStream(configurationFile);
				try {
					fos.write(configuration.getBytes());
					fos.flush();
				} catch (IOException e) {
					throw new IOException(e);
				} finally {
					fos.close();
				}
			}
		}
	}

	public void updateToolMiddlewareDatabaseConfiguration(
			String ibpDatasourcePropertyFile, String centralDbName,
			String localDbName, String username, String password)
			throws IOException {
		File configurationFile = new File(ibpDatasourcePropertyFile)
				.getAbsoluteFile();

		String format = "central.driver=com.mysql.jdbc.Driver\r\n"
				+ "central.url=%s\r\n" + "central.dbname=%s\r\n"
				+ "central.host=%s\r\n" + "central.port=%s\r\n"
				+ "central.username=%s\r\n" + "central.password=%s\r\n"
				+ "local.driver=com.mysql.jdbc.Driver\r\n" + "local.url=%s\r\n"
				+ "local.dbname=%s\r\n" + "local.host=%s\r\n"
				+ "local.port=%s\r\n" + "local.username=%s\r\n"
				+ "local.password=%s\r\n";

		String centralUrl = String.format("jdbc:mysql://%s:%s/%s", jdbcHost,
				jdbcPort, centralDbName);
		String localUrl = String.format("jdbc:mysql://%s:%s/%s", jdbcHost,
				jdbcPort, localDbName);

		// if the specified MySQL username and password
		// use the configured central user and password
		String configuration = "";
		if (!StringUtil.isEmptyOrWhitespaceOnly(username)
				&& !StringUtil.isEmptyOrWhitespaceOnly(password)) {
			configuration = String.format(format, centralUrl, centralDbName,
					jdbcHost, jdbcPort, username, password, localUrl,
					localDbName, jdbcHost, jdbcPort, username, password);
		} else {
			configuration = String.format(format, centralUrl, centralDbName,
					jdbcHost, jdbcPort, centralUser, centralPassword, localUrl,
					localDbName, jdbcHost, jdbcPort, localUser, localPassword);
		}

		FileOutputStream fos = new FileOutputStream(configurationFile);
		try {
			fos.write(configuration.getBytes());
			fos.flush();
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			fos.close();
		}
	}

	public void createWorkspaceDirectoriesForProject(Project project)
			throws MiddlewareQueryException {
		WorkbenchSetting workbenchSetting = workbenchDataManager
				.getWorkbenchSetting();
		if (workbenchSetting == null)
			return;

		String installationDirectory = workbenchSetting
				.getInstallationDirectory();

		// create the directory for the project
		String projectDirName = String.format("%d-%s", project.getProjectId(),
				project.getProjectName());
		File projectDir = new File(installationDirectory + File.separator
				+ workspaceDirectory, projectDirName);
		projectDir.mkdirs();

		// create the directory for each tool
		List<Tool> toolList = workbenchDataManager.getAllTools();
		for (Tool tool : toolList) {
			File toolDir = new File(projectDir, tool.getToolName());
			toolDir.mkdirs();

			// create the input and output directories
			new File(toolDir, "input").mkdirs();
			new File(toolDir, "output").mkdirs();
		}
	}

	public String getInputDirectoryForTool(Project project, Tool tool)
			throws MiddlewareQueryException {
		WorkbenchSetting workbenchSetting = workbenchDataManager
				.getWorkbenchSetting();
		if (workbenchSetting == null) {
			throw new IllegalStateException(
					"Workbench Setting record was not found!");
		}

		String projectDirName = String.format("%d-%s", project.getProjectId(),
				project.getProjectName());

		String installationDirectory = workbenchSetting
				.getInstallationDirectory();
		File projectDir = new File(installationDirectory + File.separator
				+ workspaceDirectory, projectDirName);
		File toolDir = new File(projectDir, tool.getToolName());

		return new File(toolDir, "input").getAbsolutePath();
	}

	public String getOutputDirectoryForTool(Project project, Tool tool)
			throws MiddlewareQueryException {
		WorkbenchSetting workbenchSetting = workbenchDataManager
				.getWorkbenchSetting();
		if (workbenchSetting == null) {
			throw new IllegalStateException(
					"Workbench Setting record was not found!");
		}

		String projectDirName = String.format("%d-%s", project.getProjectId(),
				project.getProjectName());

		String installationDirectory = workbenchSetting
				.getInstallationDirectory();
		File projectDir = new File(installationDirectory + File.separator
				+ workspaceDirectory, projectDirName);
		File toolDir = new File(projectDir, tool.getToolName());

		return new File(toolDir, "input").getAbsolutePath();
	}
	
	/**
	 * Use this instead of the {@link Properties Properties.}{@link Properties#store(java.io.OutputStream, String) store(...)} method as this method does not escape the colon <b>:</b> character
	 * 
	 * @param props
	 * @param propertyFilePath
	 * @throws FileNotFoundException
	 */
	public void savePropertyFile(Properties props, String propertyFilePath)
	        throws FileNotFoundException {

	    PrintWriter pw = new PrintWriter(propertyFilePath);
	    
	    for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements();) {
	        String key = (String)e.nextElement();
	        pw.println(key + "=" + props.getProperty(key).replace("\\","\\\\"));
	    }
	    pw.close();
	}
}

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

import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.util.Util;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.exception.ConfigurationChangeException;
import org.generationcp.ibpworkbench.util.bean.ConfigurationChangeParameters;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configurable
public class ToolUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ToolUtil.class);
    public static final String DEFAULT_DRIVER = "com.mysql.jdbc.Driver";
    public static final String JDBC_FORMAT_STRING = "jdbc:mysql://%s:%s/%s";
    public static final String INPUT = "input";
    public static final String GDMS_CONFIG_LOCATION = "infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/DatabaseConfig.properties";
    public static final String MBDT_CONFIG_LOCATION = "tools/mbdt/DatabaseConfig.properties";

    private String jdbcHost;
    private Long jdbcPort;
    private String centralUser;
    private String centralPassword;
    private String localUser;
    private String localPassword;
    private String workbenchDbName = "workbench";
    private String workbenchUser = "root";
    private String workbenchPassword = "";

    private String workspaceDirectory = "workspace";

    private String workbenchInstallationDirectory;

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

    public String getWorkbenchDbName() {
        return workbenchDbName;
    }

    public void setWorkbenchDbName(String workbenchDbName) {
        this.workbenchDbName = workbenchDbName;
    }

    public String getWorkbenchUser() {
        return workbenchUser;
    }

    public void setWorkbenchUser(String workbenchUser) {
        this.workbenchUser = workbenchUser;
    }

    public String getWorkbenchPassword() {
        return workbenchPassword;
    }

    public void setWorkbenchPassword(String workbenchPassword) {
        this.workbenchPassword = workbenchPassword;
    }

    public String getWorkspaceDirectory() {
        return workspaceDirectory;
    }

    public void setWorkspaceDirectory(String workspaceDirectory) {
        this.workspaceDirectory = workspaceDirectory;
    }

    public String getWorkbenchInstallationDirectory() {
        return workbenchInstallationDirectory;
    }

    public void setWorkbenchInstallationDirectory(String installationDirectory) {
        this.workbenchInstallationDirectory = installationDirectory;
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
        

        String parameter = "";
        if (!StringUtil.isEmpty(tool.getParameter())) {
            parameter = tool.getParameter();
        }
        
        String toolPath = getComputedToolPath(tool);
        File absoluteToolFile = new File(toolPath);


        ProcessBuilder pb = new ProcessBuilder(toolPath, parameter);
        pb.directory(absoluteToolFile.getParentFile());
        return pb.start();
    }

    public void closeAllNativeTools() throws IOException {
        try {
            List<Tool> nativeTools = workbenchDataManager.getToolsWithType(ToolType.NATIVE);

            for (Tool tool : nativeTools) {
                this.closeNativeTool(tool);
            }

        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(), e);
        }
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

        String toolPath = getComputedToolPath(tool);
        File absoluteToolFile = new File(toolPath);
        String[] pathTokens = toolPath.split(
                                                                       "\\" + File.separator);

        String executableName = pathTokens[pathTokens.length - 1];

        // taskkill /T /F /IM <exe name>
        ProcessBuilder pb = new ProcessBuilder("taskkill", "/T", "/F", "/IM",
                                               executableName);
        pb.directory(absoluteToolFile.getParentFile());

        Process process = pb.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting for " + tool.getToolName() + " to stop.");
        }
    }

    protected String getComputedToolPath(Tool tool) {
        String toolPath = tool.getPath();

        // if the tool path is an absolute path
        // and the workbench installation directory has been set,
        // launch the tool from the specified installation directory
        int startIndex = toolPath.indexOf("tools");
        if (startIndex > 0 && workbenchInstallationDirectory != null) {
            toolPath = workbenchInstallationDirectory + File.separator + toolPath
                    .substring(startIndex);
        }
        return toolPath;
    }

    /**
     * Update the configuration of the specified {@link Tool} to the
     * configuration needed by the specified {@link Project}.
     * 
     * @param tool
     * @param project
     * @throws IOException
     * 
     * @returns <code>true</code> if the configuration of the specified
     *          {@link Tool} was changed, otherwise, this method will returns
     *          <code>false</code>
     */
    public boolean updateToolConfigurationForProject(Tool tool, Project project) throws
            ConfigurationChangeException{
        String dbName = project.getCropType().getDbName();

        // DMV : added short circuit processing of the method, so that database access logic is not performed if tool is not included in target of later activities
        if (!Util.isOneOf(tool.getToolName(), ToolName.gdms.name(), ToolName.mbdt.name())) {
            return false;
        }

        // get mysql user name and password to use
        String username = null;
        String password = null;

        User currentUser = getCurrentUser();

        if (currentUser != null) {
            try {
                ProjectUserMysqlAccount account = this.workbenchDataManager
                        .getProjectUserMysqlAccountByProjectIdAndUserId(
                                project.getProjectId().intValue()
                                , currentUser.getUserid());
                username = account.getMysqlUsername();
                password = account.getMysqlPassword();
            } catch (MiddlewareQueryException ex) {
                // do nothing, use the default central and local mysql user
                // accounts
                LOG.error(ex.getMessage(), ex);
            }
        }


        try {
            WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();

            String configPath = null;
            if (Util.isOneOf(tool.getToolName(), ToolName.gdms.name())) {
				configPath = workbenchSetting.getInstallationDirectory() + File.separator + GDMS_CONFIG_LOCATION;

			} else if (Util.isOneOf(tool.getToolName(), ToolName.mbdt.name())) {
				configPath = workbenchSetting.getInstallationDirectory() + File.separator + MBDT_CONFIG_LOCATION;
			}

            return updateToolMiddlewareDatabaseConfiguration(
                    new ConfigurationChangeParameters(configPath, dbName,
                            username, password, true, false, false));
        } catch (MiddlewareQueryException e) {
            throw new ConfigurationChangeException(e);
        }
    }

    protected User getCurrentUser() {

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        if (app != null) {
            return app.getSessionData().getUserData();
        } else {
            return null;
        }
    }
    
    protected boolean updatePropertyFile(File propertyFile, Map<String, String> newPropertyValues) {
        boolean changed = false;
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            // load the property files
            Properties properties = new Properties();
            fis = new FileInputStream(propertyFile);
            properties.load(fis);
            
            // update the property values
            for (String key : newPropertyValues.keySet()) {
                String newValue = newPropertyValues.get(key);
                String oldValue = properties.getProperty(key);
                
                boolean equal = newValue == null ? oldValue == null : newValue.equals(oldValue);
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
        } catch (IOException e1) {
            LOG.error("Cannot update property file: " + propertyFile.getAbsolutePath(), e1);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e){
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        
        return changed;
    }

    public boolean updateToolMiddlewareDatabaseConfiguration(String ibpDatasourcePropertyFile, String dbName, String username, String password) throws ConfigurationChangeException {
        ConfigurationChangeParameters params = new ConfigurationChangeParameters(ibpDatasourcePropertyFile, dbName, username, password, false, false, false);
        return updateToolMiddlewareDatabaseConfiguration(params);
    }
    

    
    public boolean updateToolMiddlewareDatabaseConfiguration(ConfigurationChangeParameters params) throws ConfigurationChangeException {
        File configurationFile = getConfigurationFile(params);

        String url = String.format(JDBC_FORMAT_STRING, jdbcHost,
                                          jdbcPort, params.getDbName());

        Map<String, String> newPropertyValues = new HashMap<>();

        newPropertyValues.put("central.driver", DEFAULT_DRIVER);
        newPropertyValues.put("central.url", url);
        newPropertyValues.put("central.dbname", params.getDbName());
        newPropertyValues.put("central.host", jdbcHost);
        newPropertyValues.put("central.port", String.valueOf(jdbcPort));
        newPropertyValues.put("central.username", params.getUserName());
        newPropertyValues.put("central.password", params.getPassword());

        newPropertyValues.put("local.driver", DEFAULT_DRIVER);
        newPropertyValues.put("local.url", url);
        newPropertyValues.put("local.dbname", params.getDbName());
        newPropertyValues.put("local.host", jdbcHost);
        newPropertyValues.put("local.port", String.valueOf(jdbcPort));
        newPropertyValues.put("local.username", params.getUserName());
        newPropertyValues.put("local.password", params.getPassword());

        // if the specified MySQL username and password
        // use the configured central user and password
        if (StringUtil.isEmptyOrWhitespaceOnly(params.getUserName()) || StringUtil.isEmptyOrWhitespaceOnly(params.getPassword())) {
            newPropertyValues.put("central.username", centralUser);
            newPropertyValues.put("central.password", centralPassword);
            newPropertyValues.put("local.username", localUser);
            newPropertyValues.put("local.password", localPassword);
        }

        // if we are instructed to include workbench configuration, add it
        if (params.isIncludeWorkbenchConfig()) {
            String jdbcUrl = String.format(JDBC_FORMAT_STRING, jdbcHost, jdbcPort, workbenchDbName);

            newPropertyValues.put("workbench.driver", DEFAULT_DRIVER);
            newPropertyValues.put("workbench.url", jdbcUrl);
            newPropertyValues.put("workbench.host", jdbcHost);
            newPropertyValues.put("workbench.port", String.valueOf(jdbcPort));
            newPropertyValues.put("workbench.dbname", workbenchDbName);
            newPropertyValues.put("workbench.username", workbenchUser);
            newPropertyValues.put("workbench.password", workbenchPassword);
        }

        // if we are instructed to include the workbench current project id, add it
        if (params.isIncludeCurrentProjectId()) {
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            Project project = app.getSessionData().getLastOpenedProject();

            if (project != null) {
                newPropertyValues.put("workbench.currentProjectId", String.valueOf(project.getProjectId()));
            }
        }

        return updatePropertyFile(configurationFile, newPropertyValues);
    }

    public File getConfigurationFile(ConfigurationChangeParameters params) {
        return new File(params.getPropertyFile()).getAbsoluteFile();
    }
    
    public void createWorkspaceDirectoriesForProject(Project project)
        throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager
            .getWorkbenchSetting();
        if (workbenchSetting == null) {
            return;
        }

        String installationDirectory = workbenchSetting
            .getInstallationDirectory();

        // create the directory for the project
        String projectDirName = project.getProjectName();
        File projectDir = new File(installationDirectory + File.separator
                                   + workspaceDirectory, projectDirName);
        
        if (projectDir.exists()) {
            return;
        }
        
        projectDir.mkdirs();

        // create the directory for each tool
        List<Tool> toolList = workbenchDataManager.getAllTools();
        for (Tool tool : toolList) {
            File toolDir = new File(projectDir, tool.getGroupName());
            toolDir.mkdirs();

            // create the input and output directories
            new File(toolDir, INPUT).mkdirs();
            new File(toolDir, "output").mkdirs();
        }
    }

    public void renameOldWorkspaceDirectoryToNewFormat(long projectId,String oldProjectName) throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();
        if (workbenchSetting == null) {
            return;
        }

        String installationDirectory = workbenchSetting
                .getInstallationDirectory();

        File oldDir = new File(installationDirectory + File.separator + workspaceDirectory,String.format("%d-%s",projectId,oldProjectName));

        if (oldDir.exists()) {
            oldDir.renameTo(new File(installationDirectory + File.separator + workspaceDirectory,String.format("%d",projectId)));
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

        String projectDirName = String.format("%s", project.getProjectName());

        String installationDirectory = workbenchSetting
            .getInstallationDirectory();
        File projectDir = new File(installationDirectory + File.separator
                                   + workspaceDirectory, projectDirName);
        File toolDir = new File(projectDir, tool.getGroupName());

        return new File(toolDir, INPUT).getAbsolutePath();
    }

    public String getOutputDirectoryForTool(Project project, Tool tool)
        throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager
            .getWorkbenchSetting();
        if (workbenchSetting == null) {
            throw new IllegalStateException(
                "Workbench Setting record was not found!");
        }

        String projectDirName = String.format("%d", project.getProjectId());

        String installationDirectory = workbenchSetting
            .getInstallationDirectory();
        File projectDir = new File(installationDirectory + File.separator
                                   + workspaceDirectory, projectDirName);
        File toolDir = new File(projectDir, tool.getGroupName());

        return new File(toolDir, INPUT).getAbsolutePath();
    }
}
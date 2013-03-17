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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.util.IOUtils;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.util.Util;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ToolUtil {
    private String jdbcHost;
    private Long jdbcPort;
    private String centralUser;
    private String centralPassword;
    private String localUser;
    private String localPassword;
    
    private String workspaceDirectory = "workspace";
    
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
        
        // if we can get the workbench installation directory
        // from the workbench setting table, use it.
        WorkbenchSetting workbenchSetting = null;
        try {
            workbenchSetting = workbenchDataManager.getWorkbenchSetting();
            
            if (workbenchSetting != null && !StringUtil.isEmpty(workbenchSetting.getInstallationDirectory())) {
                absoluteToolFile = new File(workbenchSetting.getInstallationDirectory(), tool.getPath()).getAbsoluteFile();
            }
        }
        catch (MiddlewareQueryException e) {
            // intentionally empty
        }
        
        Runtime runtime = Runtime.getRuntime();
        return runtime.exec(absoluteToolFile.getAbsolutePath());
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
        String[] pathTokens = absoluteToolFile.getAbsolutePath().split("\\" + File.separator);
        
        String executableName = pathTokens[pathTokens.length - 1];
        
        // taskkill /T /F /IM <exe name>
        ProcessBuilder pb = new ProcessBuilder("taskkill", "/T", "/F", "/IM", executableName);
        pb.directory(absoluteToolFile.getParentFile());
        
        pb.start();
    }
    
    /**
     * Update the configuration of the specified {@link Tool} to the
     * configuration needed by the specified {@link Project}.
     * 
     * @param tool
     * @param project
     * @throws IOException 
     */
    public void updateToolConfigurationForProject(Tool tool, Project project) throws IOException {
        String centralDbName = String.format("ibdb_%s_central", project.getCropType().getCropName().toLowerCase());
        String localDbName = String.format("%s_%d_local", project.getCropType().getCropName().toLowerCase(), project.getProjectId());
        
        //get mysql user name and password to use
        String username = null;
        String password = null;
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        if(app != null){
            User currentUser = app.getSessionData().getUserData();
            
            if(currentUser != null){
                try{
                    ProjectUserMysqlAccount account = this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(
                            Integer.valueOf(project.getProjectId().intValue()), currentUser.getUserid());
                    username = account.getMysqlUsername();
                    password = account.getMysqlPassword();
                } catch(MiddlewareQueryException ex){
                    //do nothing, use the default central and local mysql user accounts
                }
            }
        }
        
        if (Util.isOneOf(tool.getToolName(), ToolName.fieldbook.name(), ToolName.breeding_manager.name())) {
            File configurationFile = new File("tools/" + tool.getToolName() + "/IBFb/ibfb/modules/ext/databaseconfig.properties").getAbsoluteFile();
            
            String format = "dmscentral.hibernateDialect=\r\n"
                          + "dmscentral.url=%s\r\n"
                          + "dmscentral.driverclassname=com.mysql.jdbc.Driver\r\n"
                          + "dmscentral.username=%s\r\n"
                          + "dmscentral.password=%s\r\n"
                          + "dmscentral.accessType=central\r\n"
                          + ""
                          + "gmscentral.hibernateDialect=\r\n"
                          + "gmscentral.url=%s\r\n"
                          + "gmscentral.driverclassname=com.mysql.jdbc.Driver\r\n"
                          + "gmscentral.username=%s\r\n"
                          + "gmscentral.password=%s\r\n"
                          + "gmscentral.accessType=central\r\n"
                          + "\r\n"
                          + "dmslocal.hibernateDialect=\r\n"
                          + "dmslocal.url=%s\r\n"
                          + "dmslocal.driverclassname=com.mysql.jdbc.Driver\r\n"
                          + "dmslocal.username=%s\r\n"
                          + "dmslocal.password=%s\r\n"
                          + "dmslocal.accessType=local\r\n"
                          + ""
                          + "gmslocal.hibernateDialect=\r\n"
                          + "gmslocal.url=%s\r\n"
                          + "gmslocal.driverclassname=com.mysql.jdbc.Driver\r\n"
                          + "gmslocal.username=%s\r\n"
                          + "gmslocal.password=%s\r\n"
                          + "gmslocal.accessType=local\r\n"
                          ;
            
            String jdbcFormat = "jdbc:mysql://%s:%s/%s";
            
            String centralJdbcString = String.format(jdbcFormat, jdbcHost, jdbcPort, centralDbName);
            String localJdbcString = String.format(jdbcFormat, jdbcHost, jdbcPort, localDbName);
            
            String configuration = "";
            if (!StringUtil.isEmptyOrWhitespaceOnly(username) && !StringUtil.isEmptyOrWhitespaceOnly(password)) {
                configuration = String.format(format, centralJdbcString, username, password
                        , centralJdbcString, username, password
                        , localJdbcString, username, password
                        , localJdbcString, username, password);
            } else {
                configuration = String.format(format, centralJdbcString, centralUser, centralPassword
                                                           , centralJdbcString, centralUser, centralPassword
                                                           , localJdbcString, localUser, localPassword
                                                           , localJdbcString, localUser, localPassword);
            }
                
            FileOutputStream fos = new FileOutputStream(configurationFile);
            try {
                fos.write(configuration.getBytes());
                fos.flush();
            }
            catch (IOException e) {
                throw new IOException(e);
            }
            finally {
                fos.close();
            }
        }
        else if (Util.isOneOf(tool.getToolName(), ToolName.germplasm_browser.name())) {
            updateToolMiddlewareDatabaseConfiguration("infrastructure/tomcat/webapps/GermplasmStudyBrowser/WEB-INF/classes/IBPDatasource.properties", centralDbName, localDbName, username, password);
        }
        else if (Util.isOneOf(tool.getToolName(), ToolName.gdms.name())) {
            updateToolMiddlewareDatabaseConfiguration("infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/DatabaseConfig.properties", centralDbName, localDbName, username, password);
            
            // update hibernate configuration
            String[] configurationFiles = new String[] { "infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/hibernate.cfg.xml"
                                                        ,"infrastructure/tomcat/webapps/GDMS/WEB-INF/struts-config.xml"
                                        };
            String[] templateFiles = new String[] { "infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/hibernate.cfg.xml.template"
                                                   ,"infrastructure/tomcat/webapps/GDMS/WEB-INF/struts-config.xml.template"
                                   };
            
            for (int index = 0; index < configurationFiles.length; index++) {
                File configurationFile = new File(configurationFiles[index]).getAbsoluteFile();
                File configurationFileTemplate = new File(templateFiles[index]).getAbsoluteFile();

                byte[] templateBytes = new byte[0];
                FileInputStream fis = new FileInputStream(configurationFileTemplate);
                try {
                    templateBytes = IOUtils.toByteArray(fis);
                }
                finally {
                    fis.close();
                }
                String templateStr = new String(templateBytes);

                String configuration = "";
                if (!StringUtil.isEmptyOrWhitespaceOnly(username) && !StringUtil.isEmptyOrWhitespaceOnly(password)) {
                    configuration = String.format(templateStr, jdbcHost, jdbcPort, localDbName, username, password);
                } else {
                    configuration = String.format(templateStr, jdbcHost, jdbcPort, localDbName, localUser, localPassword);
                }

                FileOutputStream fos = new FileOutputStream(configurationFile);
                try {
                    fos.write(configuration.getBytes());
                    fos.flush();
                }
                catch (IOException e) {
                    throw new IOException(e);
                }
                finally {
                    fos.close();
                }
            }
        }
    }
    
    public void updateToolMiddlewareDatabaseConfiguration(String ibpDatasourcePropertyFile, String centralDbName, String localDbName, String username, String password) throws IOException {
        File configurationFile = new File(ibpDatasourcePropertyFile).getAbsoluteFile();
        
        String format = "central.driver=com.mysql.jdbc.Driver\r\n"
                      + "central.url=%s\r\n"
                      + "central.dbname=%s\r\n"
                      + "central.host=%s\r\n"
                      + "central.port=%s\r\n"
                      + "central.username=%s\r\n"
                      + "central.password=%s\r\n"
                      + "local.driver=com.mysql.jdbc.Driver\r\n"
                      + "local.url=%s\r\n"
                      + "local.dbname=%s\r\n"
                      + "local.host=%s\r\n"
                      + "local.port=%s\r\n"
                      + "local.username=%s\r\n"
                      + "local.password=%s\r\n"
                      ;
        
        String centralUrl = String.format("jdbc:mysql://%s:%s/%s", jdbcHost, jdbcPort, centralDbName);
        String localUrl   = String.format("jdbc:mysql://%s:%s/%s", jdbcHost, jdbcPort, localDbName);
        
        // if the specified MySQL username and password
        // use the configured central user and password
        String configuration = "";
        if (!StringUtil.isEmptyOrWhitespaceOnly(username) && !StringUtil.isEmptyOrWhitespaceOnly(password)) {
            configuration = String.format(format, centralUrl, centralDbName, jdbcHost, jdbcPort, username, password
                    , localUrl, localDbName, jdbcHost, jdbcPort, username, password);
        } else {
            configuration = String.format(format, centralUrl, centralDbName, jdbcHost, jdbcPort, centralUser, centralPassword
                                                       , localUrl, localDbName, jdbcHost, jdbcPort, localUser, localPassword);
        }
        
        FileOutputStream fos = new FileOutputStream(configurationFile);
        try {
            fos.write(configuration.getBytes());
            fos.flush();
        }
        catch (IOException e) {
            throw new IOException(e);
        }
        finally {
            fos.close();
        }
    }
    
    public void createWorkspaceDirectoriesForProject(Project project) throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();
        if (workbenchSetting == null) return;
        
        String installationDirectory = workbenchSetting.getInstallationDirectory();
        
        // create the directory for the project
        String projectDirName = String.format("%d-%s", project.getProjectId(), project.getProjectName());
        File projectDir = new File(installationDirectory + File.separator + workspaceDirectory, projectDirName);
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
    
    public String getInputDirectoryForTool(Project project, Tool tool) throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();
        if (workbenchSetting == null) {
            throw new IllegalStateException("Workbench Setting record was not found!");
        }
        
        String projectDirName = String.format("%d-%s", project.getProjectId(), project.getProjectName());
        
        String installationDirectory = workbenchSetting.getInstallationDirectory();
        File projectDir = new File(installationDirectory + File.separator + workspaceDirectory, projectDirName);
        File toolDir = new File(projectDir, tool.getToolName());
        
        return new File(toolDir, "input").getAbsolutePath();
    }
    
    public String getOutputDirectoryForTool(Project project, Tool tool) throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();
        if (workbenchSetting == null) {
            throw new IllegalStateException("Workbench Setting record was not found!");
        }
        
        String projectDirName = String.format("%d-%s", project.getProjectId(), project.getProjectName());
        
        String installationDirectory = workbenchSetting.getInstallationDirectory();
        File projectDir = new File(installationDirectory + File.separator + workspaceDirectory, projectDirName);
        File toolDir = new File(projectDir, tool.getToolName());
        
        return new File(toolDir, "input").getAbsolutePath();
    }
}

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
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.util.Util;
import org.generationcp.commons.xml.hibernate.HibernateConfiguration;
import org.generationcp.commons.xml.hibernate.SessionFactory;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ToolUtil {
    private static Logger LOG = LoggerFactory.getLogger(ToolUtil.class);

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
        
        String parameter = "";
        if (!StringUtil.isEmpty(tool.getParameter())) {
            parameter = tool.getParameter();
        }
        
        ProcessBuilder pb = new ProcessBuilder(absoluteToolFile.getAbsolutePath(), parameter);
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
            e.printStackTrace();
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

        File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();
        String[] pathTokens = absoluteToolFile.getAbsolutePath().split(
                                                                       "\\" + File.separator);

        String executableName = pathTokens[pathTokens.length - 1];

        // taskkill /T /F /IM <exe name>
        ProcessBuilder pb = new ProcessBuilder("taskkill", "/T", "/F", "/IM",
                                               executableName);
        pb.directory(absoluteToolFile.getParentFile());

        Process process = pb.start();
        try {
            process.waitFor();
        }
        catch (InterruptedException e) {
            LOG.error("Interrupted while waiting for " + tool.getToolName() + " to stop.");
        }
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
    public boolean updateToolConfigurationForProject(Tool tool, Project project) throws MiddlewareQueryException, IOException {
        String centralDbName = project.getCropType().getCentralDbName();
        String localDbName = project.getCropType().getLocalDatabaseNameWithProject(project);

        // get mysql user name and password to use
        String username = null;
        String password = null;
        String workbenchLoggedinUserId = "";

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

        if (app != null) {
            User currentUser = app.getSessionData().getUserData();

            if (currentUser != null) {
                try {
                    ProjectUserMysqlAccount account = this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(
                                                                                         project.getProjectId().intValue()
                                                                                        ,currentUser.getUserid());
                    username = account.getMysqlUsername();
                    password = account.getMysqlPassword();

                    workbenchLoggedinUserId = currentUser.getUserid().toString();
                } catch (MiddlewareQueryException ex) {
                    // do nothing, use the default central and local mysql user
                    // accounts
                }
            }
        }
        
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();

        boolean configurationChanged = false;
        if (Util.isOneOf(tool.getToolName()
                         ,ToolName.breeding_manager.name()
                         ,ToolName.fieldbook.name()
                         ,ToolName.ibfb_germplasm_import.name())) {
            configurationChanged = updateFieldBookConfiguration(tool, centralDbName, localDbName, username, password, workbenchLoggedinUserId);
        } else if (Util.isOneOf(tool.getToolName()
                                ,ToolName.germplasm_browser.name()
                                ,ToolName.germplasm_list_browser.name()
                                ,ToolName.germplasm_headtohead.name()
                                ,ToolName.germplasm_mainheadtohead.name()
                                ,ToolName.query_for_adapted_germplasm.name()
                                ,ToolName.study_browser.name()
                                ,ToolName.study_browser_with_id.name()
                                )) {
            String configPath = workbenchSetting.getInstallationDirectory() + File.separator + "infrastructure/tomcat/webapps/GermplasmStudyBrowser/WEB-INF/classes/IBPDatasource.properties";
            configurationChanged = updateToolMiddlewareDatabaseConfiguration(configPath, centralDbName,
                                                                             localDbName, username, password);
        } else if (Util.isOneOf(tool.getToolName()
                                ,ToolName.fieldbook_web.name()
                                ,ToolName.nursery_manager_fieldbook_web.name()
                                ,ToolName.trial_manager_fieldbook_web.name()
                                ,ToolName.ontology_browser_fieldbook_web.name()
                                )) {
            // Fieldbook web apps
            String configPath = workbenchSetting.getInstallationDirectory() + File.separator + "infrastructure/tomcat/webapps/Fieldbook/WEB-INF/classes/database.properties";
            configurationChanged = updateToolMiddlewareDatabaseConfiguration(configPath, centralDbName, localDbName, username, password, true, true, true);
        } else if (Util.isOneOf(tool.getToolName()
                                ,ToolName.bm_list_manager.name()
                                ,ToolName.bm_list_manager_main.name()
                                ,ToolName.crossing_manager.name()
                                ,ToolName.germplasm_import.name()
                                ,ToolName.list_manager.name()
                                ,ToolName.nursery_template_wizard.name()
                                )) {
            // crossing manager uses the same property file
            // nursery_template_wizard uses the same property file
            // so no need to update 
            String configPath = workbenchSetting.getInstallationDirectory() + File.separator + "infrastructure/tomcat/webapps/BreedingManager/WEB-INF/classes/IBPDatasource.properties";
            configurationChanged = updateToolMiddlewareDatabaseConfiguration(configPath, centralDbName, localDbName,
                                                                             username, password);
        } else if (Util.isOneOf(tool.getToolName(),
                                ToolName.dataset_importer.name())) {
            // crossing manager uses the same property file
            // nursery_template_wizard uses the same property file
            // so no need to update 
            String configPath = workbenchSetting.getInstallationDirectory() + File.separator + "infrastructure/tomcat/webapps/DatasetImporter/WEB-INF/classes/database.properties";
            configurationChanged = updateToolMiddlewareDatabaseConfiguration(configPath, centralDbName, localDbName, username,
                                                                             password, true);
        } else if (Util.isOneOf(tool.getToolName(), ToolName.gdms.name())) {
            String configPath = workbenchSetting.getInstallationDirectory() + File.separator + "infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/DatabaseConfig.properties";
            configurationChanged = updateToolMiddlewareDatabaseConfiguration(configPath, centralDbName, localDbName, username,
                                                                             password, true);
        } else if (Util.isOneOf(tool.getToolName(), ToolName.ibpwebservice.name())) {
            configurationChanged = updateWebServiceConfigurationForProject(project, workbenchSetting);
        }
        
        return configurationChanged;
    }
    
    protected boolean updateFieldBookConfiguration(Tool tool, String centralDbName, String localDbName, String username, String password, String workbenchLoggedinUserId) throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();
        
        String installationDirectory = workbenchSetting == null? "" : workbenchSetting.getInstallationDirectory() + File.separator;
        
        // Update databaseconfig.properties
        File configurationFile = new File(installationDirectory + File.separator + "tools/fieldbook/IBFb/ibfb/modules/ext/databaseconfig.properties").getAbsoluteFile();
        
        String jdbcFormat = "jdbc:mysql://%s:%s/%s";
        String centralJdbcString = String.format(jdbcFormat, jdbcHost,
                                                 jdbcPort, centralDbName);
        String localJdbcString = String.format(jdbcFormat, jdbcHost,
                                               jdbcPort, localDbName);

        String centralDbUser = centralUser;
        String centralDbPassword = centralPassword;
        String localDbUser = localUser;
        String localDbPassword = localPassword;
        if (!StringUtil.isEmptyOrWhitespaceOnly(username)
            && !StringUtil.isEmptyOrWhitespaceOnly(password)) {
            centralDbUser = username;
            centralDbPassword = password;
            localDbUser = username;
            localDbPassword = password;
        }
        
        Map<String, String> propertyValues = new HashMap<String, String>();

        propertyValues.put("dmscentral.url", centralJdbcString);
        propertyValues.put("dmscentral.driverclassname", "com.mysql.jdbc.Driver");
        propertyValues.put("dmscentral.username", centralDbUser);
        propertyValues.put("dmscentral.password", centralDbPassword);
        propertyValues.put("dmscentral.accessType", "central");

        propertyValues.put("dmscentral2.defaultSchema", centralDbName);

        propertyValues.put("gmscentral.hibernateDialect", "");
        propertyValues.put("gmscentral.url", centralJdbcString);
        propertyValues.put("gmscentral.driverclassname", "com.mysql.jdbc.Driver");
        propertyValues.put("gmscentral.username", centralDbUser);
        propertyValues.put("gmscentral.password", centralDbPassword);
        propertyValues.put("gmscentral.accessType", "central");

        propertyValues.put("dmslocal.hibernateDialect", "");
        propertyValues.put("dmslocal.url", localJdbcString);
        propertyValues.put("dmslocal.driverclassname", "com.mysql.jdbc.Driver");
        propertyValues.put("dmslocal.username", localDbUser);
        propertyValues.put("dmslocal.password", localDbPassword);
        propertyValues.put("dmslocal.accessType", "local");

        propertyValues.put("gmslocal.hibernateDialect", "");
        propertyValues.put("gmslocal.url", localJdbcString);
        propertyValues.put("gmslocal.driverclassname", "com.mysql.jdbc.Driver");
        propertyValues.put("gmslocal.username", localDbUser);
        propertyValues.put("gmslocal.password", localDbPassword);
        propertyValues.put("gmslocal.accessType", "local");

        propertyValues.put("workbench.currentUserId", workbenchLoggedinUserId);
        
        return updatePropertyFile(configurationFile, propertyValues);
    }
    
    protected boolean updateGdmsConfiguration(String connectionUrl, String username, String password) throws JAXBException, MiddlewareQueryException, IOException {
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();
        
        String installationDirectory = workbenchSetting == null? "" : workbenchSetting.getInstallationDirectory() + File.separator;
        
        String gdmsConfigFile = installationDirectory + File.separator + "infrastructure/tomcat/webapps/GDMS/WEB-INF/classes/hibernate.cfg.xml";
        
        JAXBContext context = JAXBContext.newInstance(HibernateConfiguration.class);
        Unmarshaller um = context.createUnmarshaller();
        
        HibernateConfiguration config = (HibernateConfiguration) um.unmarshal(new File(gdmsConfigFile));
        SessionFactory sessionFactory = config.getSessionFactory();
        
        boolean urlChanged = sessionFactory.updateConnectionUrl(connectionUrl);
        boolean usernameChanged = sessionFactory.updateUsername(username);
        boolean passwordChanged = sessionFactory.updatePassword(password);
        boolean changed = urlChanged || usernameChanged || passwordChanged;
        
        if (changed) {
            StringWriter stringWriter = new StringWriter();
            
            // save the hibernate configuration
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(config, stringWriter);
            
            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                            + "<!DOCTYPE hibernate-configuration PUBLIC\r\n"
                            + "\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\r\n"
                            + "\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">\r\n";
            String hibernateXml = stringWriter.toString();
            hibernateXml = hibernateXml.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", header);
            
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(gdmsConfigFile);
                fileWriter.write(hibernateXml);
                fileWriter.flush();
                fileWriter.close();
            }
            finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
        
        return changed;
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
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e) {
                }
            }
            
            // save the new property values
            if (changed) {
                fos = new FileOutputStream(propertyFile);
                properties.store(fos, null);
                fos.flush();
            }
        }
        catch (FileNotFoundException e1) {
            LOG.error("Cannot update property file: " + propertyFile.getAbsolutePath(), e1);
        }
        catch (IOException e1) {
            LOG.error("Cannot update property file: " + propertyFile.getAbsolutePath(), e1);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException e) {
                }
            }
            
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (IOException e){
                }
            }
        }
        
        return changed;
    }

    public boolean updateToolMiddlewareDatabaseConfiguration(String ibpDatasourcePropertyFile, String centralDbName
                                                            ,String localDbName, String username, String password) throws IOException, MiddlewareQueryException {
        return updateToolMiddlewareDatabaseConfiguration(ibpDatasourcePropertyFile, centralDbName, localDbName, username, password, false);
    }
    
    public boolean updateToolMiddlewareDatabaseConfiguration(String ibpDatasourcePropertyFile, String centralDbName
                                                            ,String localDbName, String username, String password
                                                            ,boolean includeWorkbenchConfig) throws IOException, MiddlewareQueryException {
        return updateToolMiddlewareDatabaseConfiguration(ibpDatasourcePropertyFile, centralDbName, localDbName, username, password, false, false, false);
    }
    
    public boolean updateToolMiddlewareDatabaseConfiguration(String ibpDatasourcePropertyFile, String centralDbName
                                                            ,String localDbName, String username, String password
                                                            ,boolean includeWorkbenchConfig
                                                            ,boolean includeCurrentProjectId
                                                            ,boolean includeOldFieldbookPath) throws IOException, MiddlewareQueryException {
        File configurationFile = new File(ibpDatasourcePropertyFile).getAbsoluteFile();

        String centralUrl = String.format("jdbc:mysql://%s:%s/%s", jdbcHost,
                                          jdbcPort, centralDbName);
        String localUrl = String.format("jdbc:mysql://%s:%s/%s", jdbcHost,
                                        jdbcPort, localDbName);
        
        Map<String, String> newPropertyValues = new HashMap<String, String>();
        
        newPropertyValues.put("central.driver", "com.mysql.jdbc.Driver");
        newPropertyValues.put("central.url", centralUrl);
        newPropertyValues.put("central.dbname", centralDbName);
        newPropertyValues.put("central.host", jdbcHost);
        newPropertyValues.put("central.port", String.valueOf(jdbcPort));
        newPropertyValues.put("central.username", username);
        newPropertyValues.put("central.password", password);
        newPropertyValues.put("local.driver", "com.mysql.jdbc.Driver");
        newPropertyValues.put("local.url", localUrl);
        newPropertyValues.put("local.dbname", localDbName);
        newPropertyValues.put("local.host", jdbcHost);
        newPropertyValues.put("local.port", String.valueOf(jdbcPort));
        newPropertyValues.put("local.username", username);
        newPropertyValues.put("local.password", password);
        
        // if the specified MySQL username and password
        // use the configured central user and password
        if (StringUtil.isEmptyOrWhitespaceOnly(username) || StringUtil.isEmptyOrWhitespaceOnly(password)) {
            newPropertyValues.put("central.username", centralUser);
            newPropertyValues.put("central.password", centralPassword);
            newPropertyValues.put("local.username", localUser);
            newPropertyValues.put("local.password", localPassword);
        }
        
        // if we are instructed to include workbench configuration, add it
        if (includeWorkbenchConfig) {
            String url = "jdbc:mysql://" + jdbcHost + ":" + String.valueOf(jdbcPort) + "/" + workbenchDbName;
            newPropertyValues.put("workbench.driver", "com.mysql.jdbc.Driver");
            newPropertyValues.put("workbench.url", url);
            newPropertyValues.put("workbench.host", jdbcHost);
            newPropertyValues.put("workbench.port", String.valueOf(jdbcPort));
            newPropertyValues.put("workbench.dbname", workbenchDbName);
            newPropertyValues.put("workbench.username", workbenchUser);
            newPropertyValues.put("workbench.password", workbenchPassword);
        }
        
        // if we are instructed to include the workbench current project id, add it
        if (includeCurrentProjectId) {
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            Project project = app.getSessionData().getLastOpenedProject();
            
            if (project != null) {
                newPropertyValues.put("workbench.currentProjectId", String.valueOf(project.getProjectId()));
            }
        }
        
        // if we are instructed to include the fieldbook tool path, add it
        if (includeOldFieldbookPath) {
            Tool tool = workbenchDataManager.getToolWithName(ToolName.fieldbook.name());
            if (tool != null) {
                newPropertyValues.put("old.fb.tool.path", tool.getPath());
            }
        }
        
        return updatePropertyFile(configurationFile, newPropertyValues);
    }
    
    public boolean updateWebServiceConfigurationForProject(Project project, WorkbenchSetting workbenchSetting) throws IOException, MiddlewareQueryException {
        String centralDbName = project.getCropType().getCentralDbName();
        String localDbName = project.getCropType().getLocalDatabaseNameWithProject(project);
        
        // get mysql user name and password to use
        String username = null;
        String password = null;

        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        if (app != null) {
            User currentUser = app.getSessionData().getUserData();

            if (currentUser != null) {
                try {
                    ProjectUserMysqlAccount account = workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(Integer.valueOf(project.getProjectId().intValue()), currentUser.getUserid());
                    username = account.getMysqlUsername();
                    password = account.getMysqlPassword();
                }
                catch (MiddlewareQueryException ex) {
                    // do nothing, use the default central and local mysql user
                    // accounts
                }
            }
        }
        
        String configPath = workbenchSetting.getInstallationDirectory() + File.separator + "infrastructure/tomcat/webapps/IBPWebService/WEB-INF/classes/IBPDatasource.properties";
        LOG.debug("Updating ibpwebservice configuration at: " + configPath);
        return updateToolMiddlewareDatabaseConfiguration(configPath, centralDbName, localDbName, username, password, false);
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
        String projectDirName = String.format("%d", project.getProjectId());
        File projectDir = new File(installationDirectory + File.separator
                                   + workspaceDirectory, projectDirName);
        
        if (projectDir.exists()) return;
        
        projectDir.mkdirs();

        // create the directory for each tool
        List<Tool> toolList = workbenchDataManager.getAllTools();
        for (Tool tool : toolList) {
            File toolDir = new File(projectDir, tool.getGroupName());
            toolDir.mkdirs();

            // create the input and output directories
            new File(toolDir, "input").mkdirs();
            new File(toolDir, "output").mkdirs();
        }
    }

    public void renameOldWorkspaceDirectoryToNewFormat(long projectId,String oldProjectName) throws MiddlewareQueryException {
        WorkbenchSetting workbenchSetting = workbenchDataManager.getWorkbenchSetting();
        if (workbenchSetting == null)
            return;

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

        String projectDirName = String.format("%d", project.getProjectId());

        String installationDirectory = workbenchSetting
            .getInstallationDirectory();
        File projectDir = new File(installationDirectory + File.separator
                                   + workspaceDirectory, projectDirName);
        File toolDir = new File(projectDir, tool.getGroupName());

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

        String projectDirName = String.format("%d", project.getProjectId());

        String installationDirectory = workbenchSetting
            .getInstallationDirectory();
        File projectDir = new File(installationDirectory + File.separator
                                   + workspaceDirectory, projectDirName);
        File toolDir = new File(projectDir, tool.getGroupName());

        return new File(toolDir, "input").getAbsolutePath();
    }
}

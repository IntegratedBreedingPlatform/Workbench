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
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.Util;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.tomcat.TomcatUtil;
import org.generationcp.ibpworkbench.util.tomcat.WebAppStatusInfo;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

@Configurable
public class LaunchWorkbenchToolAction implements WorkflowConstants, ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private final static Logger LOG = LoggerFactory.getLogger(LaunchWorkbenchToolAction.class);

    public static final String WEB_SERVICE_URL_PROPERTY = "bv.web.url";
    
    public static enum ToolEnum {
         GERMPLASM_BROWSER("germplasm_browser")
        ,STUDY_BROWSER("study_browser")
        ,STUDY_BROWSER_WITH_ID("study_browser_with_id")
        ,GERMPLASM_LIST_BROWSER("germplasm_list_browser")
        ,GDMS("gdms")
        ,FIELDBOOK("fieldbook")
        ,OPTIMAS("optimas")
        ,BREEDING_MANAGER("breeding_manager")
        ,BREEDING_VIEW("breeding_view")
        ,MBDT("mbdt")
        ,LIST_MANAGER("list_manager")
        ,BM_LIST_MANAGER("bm_list_manager")
        ,BM_LIST_MANAGER_MAIN("bm_list_manager_main")
        ,CROSSING_MANAGER("crossing_manager")
        ,NURSERY_TEMPLATE_WIZARD("nursery_template_wizard")
        ,BREEDING_PLANNER("breeding_planner")
        ,IBFB_GERMPLASM_IMPORT("ibfb_germplasm_import")
        ,GERMPLASM_IMPORT("germplasm_import")
        ,HEAD_TO_HEAD_BROWSER("germplasm_headtohead")
        ,MAIN_HEAD_TO_HEAD_BROWSER("germplasm_mainheadtohead")
        ,DATASET_IMPORTER("dataset_importer")
        ,QUERY_FOR_ADAPTED_GERMPLASM("query_for_adapted_germplasm")
        ,FIELDBOOK_WEB("fieldbook_web")
        ,NURSERY_MANAGER_FIELDBOOK_WEB("nursery_manager_fieldbook_web")
        ,TRIAL_MANAGER_FIELDBOOK_WEB("trial_manager_fieldbook_web")
        ,ONTOLOGY_BROWSER_FIELDBOOK_WEB("ontology_browser_fieldbook_web");
        
        String toolName;
        
        ToolEnum(String toolName) {
            this.toolName = toolName;
        }
        
        public String getToolName() {
            return toolName;
        }
        
        public static boolean isCorrectTool(String toolName) {
        	for (ToolEnum tool : ToolEnum.values()) {
        		if (tool.getToolName().equals(toolName))
        			return true;
        	}
        	
        	return false;
        }
        
        public static ToolEnum equivalentToolEnum(String toolName) {
        	for (ToolEnum tool : ToolEnum.values()) {
        		if (tool.getToolName().equals(toolName))
        			return tool;
        	}
        	
        	return null;
        }
    }
    
    private ToolEnum toolEnum;
    
    private Project project;
    
    private String toolConfiguration;

    /**
     * An id passed to the tool.
     * We assume that the tool recognizes:
     *      <tool url>-idParam
     * format and gets the id parameter from there.
     * This is used for Germplasm Browser and Study Browser tools at the moment.
     * This is a very dirty implementation.
     * I wish we could do better but that is what we have for now.
     */
    private Integer idParam;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private ToolUtil toolUtil;
    
    @Autowired
    private TomcatUtil tomcatUtil;

    @Autowired
    private Properties workbenchProperties;

    @Autowired
    private SessionData sessionData;
    
    public LaunchWorkbenchToolAction() {
    }
    
    public LaunchWorkbenchToolAction(ToolEnum toolEnum) {
        this.toolEnum = toolEnum;
        this.toolConfiguration = WorkflowConstants.DEFAULT;
    }
    
    public LaunchWorkbenchToolAction(ToolEnum toolEnum, int idParam) {
        this.idParam = idParam;
    }
    
    public LaunchWorkbenchToolAction(ToolEnum toolEnum, Project project) {
        this(toolEnum, project, null);
    }
    
    public LaunchWorkbenchToolAction(ToolEnum toolEnum, Project project, int listId) {
        this(toolEnum, project, null);
        
        this.idParam = listId;
    }
    
    public LaunchWorkbenchToolAction(ToolEnum toolEnum, Project project, String toolConfiguration) {
        this.toolEnum = toolEnum;
        this.project = project;
        this.toolConfiguration = toolConfiguration;
    }
    
    @Override
    public void buttonClick(ClickEvent event) {
        Window window = event.getComponent().getWindow();
        
        launchTool(toolEnum.getToolName(), window, true);
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }
    
    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
    	
    	String a = uriFragment.split("/")[1];
    	
    	String toolName = (a).split("\\?")[0];
    	
    	
    	
    	Map<String, List<String>> params = UriUtils.getUriParameters(uriFragment);
    	
    	
    	this.toolEnum = ToolEnum.equivalentToolEnum(toolName);
    	
        if(this.toolEnum != null) {
        	
        	Long projectId = null;
            String dataset = "";
            try {
                projectId = Long.parseLong(params.get("projectId").get(0));
                dataset = params.get("dataset").get(0);
            } catch (NullPointerException e) {
                projectId = sessionData.getLastOpenedProject().getProjectId();
            }


        	try {
        		if (ToolEnum.BREEDING_VIEW == this.toolEnum && (dataset != null && !dataset.isEmpty())) {
        			if (dataset.equals("local")) {
    	        		this.toolConfiguration = WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL;
    	        	} else if (dataset.equals("central")) {
    					this.toolConfiguration = WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL;
    				}	
        		
        			this.project = workbenchDataManager.getProjectById(projectId);
        		}
        		
        		launchTool(toolEnum.getToolName(),window,isLinkAccessed);
        		
        	} catch (MiddlewareQueryException e) {
                LOG.error("QueryException", e);
                MessageNotifier.showError(window, 
                        messageSource.getMessage(Message.DATABASE_ERROR), 
                        "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
			}
        } else {
            LOG.debug("Cannot launch tool due to invalid tool: {}", uriFragment.split("/")[1]);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
                    messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(uriFragment.split("/")[1]).toArray()));
        }
    	
    }
    
    public void launchTool(String toolName, Window window, boolean isLinkAccessed) {
        Tool tool = null;
        
        try {
            tool = workbenchDataManager.getToolWithName(toolName);
        } catch (MiddlewareQueryException qe) {
            LOG.error("QueryException", qe);
            MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return;
        }
        
        if (tool == null) {
            LOG.warn("Cannot find tool " + toolEnum);
            showLaunchError(window, toolEnum.toString());
            return;
        } else {
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            User user = app.getSessionData().getUserData();
            
            Project currentProject = getCurrentProject();
            try {
                ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, tool.getTitle(), "Launched "+tool.getTitle(), user, new Date());

                workbenchDataManager.addProjectActivity(projAct);
            } catch (MiddlewareQueryException e1) {
                MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                          "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                return;
            }
            
            // update the tool configuration if needed
            updateToolConfiguration(window, tool);
            
            if (tool.getToolType() == ToolType.NATIVE) {
                try {
                    // close the native tool
                    toolUtil.closeNativeTool(tool);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                
                if (toolName.equals(ToolEnum.BREEDING_VIEW.getToolName())) {
                    // when launching BreedingView, update the web service tool first
                    Tool webServiceTool = new Tool();
                    webServiceTool.setToolName("ibpwebservice");
                    webServiceTool.setPath(workbenchProperties.getProperty(WEB_SERVICE_URL_PROPERTY));
                    webServiceTool.setToolType(ToolType.WEB);
                    
                    updateToolConfiguration(window, webServiceTool);
                }

                try {
                    toolUtil.launchNativeTool(tool);
                }
                catch (IOException e) {
                    File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();

                    LOG.error("Cannot launch " + absoluteToolFile.getAbsolutePath(), e);
                    showLaunchError(window, absoluteToolFile.getAbsolutePath());
                }
            }
            else if (tool.getToolType() == ToolType.WEB_WITH_LOGIN) {
                String loginUrl = tool.getPath();
                
                User localIbdbUser = null;
                try {
                    if (user != null && currentProject != null) {
                        Integer localIbdbUserId = workbenchDataManager.getLocalIbdbUserId(user.getUserid(), currentProject.getProjectId());
                        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProject);
                        UserDataManager userDataManager = managerFactory.getUserDataManager();

                        if (localIbdbUserId != null) {
                            localIbdbUser = userDataManager.getUserById(localIbdbUserId);
                        }
                    }
                }
                catch (MiddlewareQueryException e) {
                    LOG.error("QueryException", e);
                    MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                            "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                    return;
                }
                finally {
                    try {
                        String url = tool.getPath();
                        if (localIbdbUser != null) {
                            url = getWebLoginForwardUrl(loginUrl, localIbdbUser.getName(), localIbdbUser.getPassword());
                        }
                        else if (user != null) {
                            url = getWebLoginForwardUrl(loginUrl, user.getName(), user.getPassword());
                        }
                        
                        Embedded browser = new Embedded(null,new ExternalResource(url));
                        browser.setStyleName("gcp-embedded");
                        browser.setType(Embedded.TYPE_BROWSER);
                        browser.setSizeFull();
                        //browser.setHeight("800px");
                        //browser.setWidth("100%");
                        
                        NavManager.navigateApp(window, "/" + toolName, isLinkAccessed);
                        
                        IContentWindow contentWindow = (IContentWindow) window;
                        contentWindow.showContent(browser);
                    }
                    catch (UnsupportedEncodingException e) {
                        // intentionally empty
                    }
                    finally {
                    }
                }
            }
            else if (tool.getToolType() == ToolType.WEB) {
                String toolUrl = tool.getPath();
                
                // if user is trying to launch the FieldBook webapp,
                // we need to close and reconfigure the old Fieldbook app
                if (Util.isOneOf(tool.getToolName()
                                 ,ToolName.fieldbook_web.name()
                                 ,ToolName.nursery_manager_fieldbook_web.name()
                                 ,ToolName.trial_manager_fieldbook_web.name()
                                 ,ToolName.ontology_browser_fieldbook_web.name()
                                 )) {
                    try {
                        Tool fieldbookTool = workbenchDataManager.getToolWithName(ToolName.fieldbook.name());
                        toolUtil.closeNativeTool(fieldbookTool);
                        
                        toolUtil.updateToolConfigurationForProject(fieldbookTool, currentProject);
                    }
                    catch (MiddlewareQueryException e) {
                        LOG.error("QueryException", e);
                        MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                        return;
                    }
                    catch (IOException e) {
                        LOG.error("Cannot close fieldbook app", e);
                    }
                }
                
                // if the user is trying to launch the BreedingManager webapp
                // we need to reconfigure and deploy the GermplasmBrowser webapp too
                if (Util.isOneOf(tool.getToolName()
                                 ,ToolName.bm_list_manager.name()
                                 //,ToolName.bm_list_manager_main.name()
                                 ,ToolName.crossing_manager.name()
                                 ,ToolName.germplasm_import.name()
                                 ,ToolName.list_manager.name()
                                 ,ToolName.nursery_template_wizard.name()
                                 )) {
                    try {
                        Tool germplasmBrowserTool = workbenchDataManager.getToolWithName(ToolName.germplasm_browser.name());
                        updateToolConfiguration(window, germplasmBrowserTool);
                    }
                    catch (MiddlewareQueryException e) {
                        LOG.error("QueryException", e);
                        MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                        return;
                    }
                }
                
                // if the user is trying to launch the List Manager or the Study Browser webapp
                // we need to append the list id if it was set
                if (Util.isOneOf(tool.getToolName(), ToolEnum.BM_LIST_MANAGER.getToolName()
                                                   , ToolEnum.STUDY_BROWSER_WITH_ID.getToolName())) {
                    if (idParam != null) {
                        if (toolUrl.endsWith("/")) {
                            toolUrl = toolUrl.substring(0, toolUrl.length() - 1);
                        }
                        
                        toolUrl += "-" + idParam;
                    }
                }
                
                // append ?restartApplication if the url does not have one yet
                if (!toolUrl.contains("?restartApplication")) {
                    toolUrl += "?restartApplication";
                }
                
                Embedded browser = new Embedded(null, new ExternalResource(toolUrl));
                
                browser.setType(Embedded.TYPE_BROWSER);
                browser.setSizeFull();
                //browser.setHeight("800px");
                //browser.setWidth("100%");
                
                NavManager.navigateApp(window, "/" + toolName, isLinkAccessed);
                
                IContentWindow contentWindow = (IContentWindow) window;
                contentWindow.showContent(browser);
            }
        }
    }
    
    private void showLaunchError(Window window, String tool) {
        MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
                "<br />" + messageSource.getMessage(Message.LAUNCH_TOOL_ERROR_DESC, tool));
    }
    
    private String getWebLoginForwardUrl(String url, String username, String password) throws UnsupportedEncodingException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        
        String urlFormat = "%s://%s:%d/%s/web_login_forward?login_url=%s&username=%s&password=%s";
        
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        String contextPath = request.getContextPath();
        int port = request.getServerPort();
        
        return String.format(urlFormat, scheme, serverName, port, contextPath, URLEncoder.encode(url, "UTF-8"), username, password);
    }
    
    private boolean updateToolConfiguration(Window window, Tool tool) {
        Project currentProject = getCurrentProject();
        
        String url = tool.getPath();
        
        // update the configuration of the tool
        boolean changedConfig = false;
        try {
            changedConfig = toolUtil.updateToolConfigurationForProject(tool, currentProject);
        }
        catch (IOException e1) {
            MessageNotifier.showError(window, "Cannot update configuration for tool: " + tool.getToolName(),
                                      "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return false;
        }
        catch (MiddlewareQueryException e) {
            MessageNotifier.showError(window, "Cannot update configuration for tool: " + tool.getToolName(),
                                      "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return false;
        }
        
        boolean webTool = Util.isOneOf(tool.getToolType(), ToolType.WEB_WITH_LOGIN, ToolType.WEB);
        
        WebAppStatusInfo statusInfo = null;
        String contextPath = null;
        String localWarPath = null;
        try {
            statusInfo = tomcatUtil.getWebAppStatus();
            if (webTool) {
                contextPath = TomcatUtil.getContextPathFromUrl(url);
                localWarPath = TomcatUtil.getLocalWarPathFromUrl(url);
            }
        }
        catch (Exception e1) {
            MessageNotifier.showError(window, "Cannot get webapp status.",
                                      "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            return false;
        }
        
        if (webTool) {
            try {
                boolean deployed = statusInfo.isDeployed(contextPath);
                boolean running = statusInfo.isRunning(contextPath);
                
                if (changedConfig || !running) {
                    if (!deployed) {
                        // deploy the webapp
                        tomcatUtil.deployLocalWar(contextPath, localWarPath);
                    }
                    else if (running) {
                        // reload the webapp
                    	// reloading only applies to GDMS
                    	if (tool.getToolName() == ToolName.gdms.name()){
                    		LOG.trace("Reloading the GDMS app...");
                    		tomcatUtil.reloadWebApp(contextPath);
                    	}
                        
                    }
                    else {
                        // start the webapp
                        tomcatUtil.startWebApp(contextPath);
                    }
                }
            }
            catch (Exception e) {
                MessageNotifier.showError(window, "Cannot load tool: " + tool.getToolName(),
                                          "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                return false;
            }
        }
        
        return true;
    }
    
    private Project getCurrentProject() {
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        Project currentProject = app.getSessionData().getSelectedProject();
        if (currentProject == null) {
            currentProject = app.getSessionData().getLastOpenedProject();
        }
        if (this.project != null) {
            currentProject = project;
        }
        
        return currentProject;
    }

    public Properties getWorkbenchProperties() {
        return workbenchProperties;
    }

    public void setWorkbenchProperties(Properties workbenchProperties) {
        this.workbenchProperties = workbenchProperties;
    }
}

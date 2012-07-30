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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

@Configurable
public class LaunchWorkbenchToolAction implements ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private final static Logger LOG = LoggerFactory.getLogger(LaunchWorkbenchToolAction.class);
    
    public static enum ToolEnum {
         GERMPLASM_BROWSER("germplasm_browser")
        ,GERMPLASM_PHENOTYPIC("germplasm_phenotypic")
        ,GDMS("gdms")
        ,FIELDBOOK("fieldbook")
        ,OPTIMAS("optimas")
        ,BREEDING_MANAGER("breeding_manager")
        ,BREEDING_VIEW("breeding_view")
        ;
        
        String toolName;
        
        ToolEnum(String toolName) {
            this.toolName = toolName;
        }
        
        public String getToolName() {
            return toolName;
        }
        
        public static boolean isCorrectTool(String toolName) {
            if(ToolEnum.GERMPLASM_BROWSER.getToolName().equals(toolName) ||
               ToolEnum.GERMPLASM_PHENOTYPIC.getToolName().equals(toolName) ||
               ToolEnum.GDMS.getToolName().equals(toolName) ||
               ToolEnum.FIELDBOOK.getToolName().equals(toolName) ||
               ToolEnum.OPTIMAS.getToolName().equals(toolName) ||
               ToolEnum.BREEDING_MANAGER.getToolName().equals(toolName) ||
               ToolEnum.BREEDING_VIEW.getToolName().equals(toolName)) {
                return true;
            }   return false;
            
        }
    }

    private ToolEnum toolEnum;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public LaunchWorkbenchToolAction() {
    }
    
    public LaunchWorkbenchToolAction(ToolEnum toolEnum) {
        this.toolEnum = toolEnum;
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

        Map<String, List<String>> map = UriUtils.getUriParameters(uriFragment);
        String toolName = map.get("toolName").get(0);
        
        if(ToolEnum.isCorrectTool(toolName)) {
            launchTool(toolName, window, isLinkAccessed);
        } else {
            LOG.debug("Cannot launch tool due to invalid tool: {}", toolName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
                    messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(toolName).toArray()));
        }
    }
    
    private void launchTool(String toolName, Window window, boolean isLinkAccessed) {
        Tool tool = null;
        try {
            tool = workbenchDataManager.getToolWithName(toolName);
        } catch (QueryException qe) {
            LOG.error("QueryException", qe);
            MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                    "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
        }
        
        if (tool == null) {
            LOG.warn("Cannot find tool " + toolEnum);
            showLaunchError(window, toolEnum.toString());
            return;
        } else {
            if (tool.getToolType() == ToolType.NATIVE) {
                
                File absoluteToolFile = new File(tool.getPath()).getAbsoluteFile();
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec(absoluteToolFile.getAbsolutePath());
                }
                catch (IOException e) {
                    LOG.error("Cannot launch " + absoluteToolFile.getAbsolutePath(), e);
                    showLaunchError(window, absoluteToolFile.getAbsolutePath());
                }
                
            } else {
                
                Embedded browser = new Embedded("", new ExternalResource(tool.getPath()));
                browser.setType(Embedded.TYPE_BROWSER);
                browser.setSizeFull();
                browser.setHeight("800px");
                browser.setWidth("100%");
                
                NavManager.navigateApp(window, "/home/openProject/openProjectWorkflow/" + toolName + "?toolName=" + toolName, isLinkAccessed);
                
                IContentWindow contentWindow = (IContentWindow) window;
                contentWindow.showContent(browser);
            }
        }
    }
    
    private void showLaunchError(Window window, String tool) {
        MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
                "<br />" + messageSource.getMessage(Message.LAUNCH_TOOL_ERROR_DESC, tool));
    }
    

}

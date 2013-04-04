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

import java.util.Arrays;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.WorkflowConstants;
import org.generationcp.ibpworkbench.comp.window.ProjectMemberWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class OpenWindowAction implements WorkflowConstants, ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private final static Logger LOG = LoggerFactory.getLogger(OpenWindowAction.class);
    
    Project project;
    
    public static enum WindowEnum {
         GERMPLASM_BROWSER("germplasm_browser")
        ,STUDY_BROWSER("study_browser")
        ,GERMPLASM_LIST_BROWSER("germplasm_list_browser")
        ,GDMS("gdms")
        ,FIELDBOOK("fieldbook")
        ,OPTIMAS("optimas")
        ,BREEDING_MANAGER("breeding_manager")
        ,BREEDING_VIEW("breeding_view")
        ,MBDT("mbdt")
        ,MEMBER("member")
        //,BACKUP_IBDB("backup_ibdb")
        //,RESTORE_IBDB("restore_ibdb")
        ;
        
        String windowName;
        
        WindowEnum(String windowName) {
            this.windowName = windowName;
        }
        
        public String getwindowName() {
            return windowName;
        }
        
        public static boolean isCorrectTool(String windowName) {
        	
        	for (WindowEnum winEnum : WindowEnum.values()) {
        		if (winEnum.equals(windowName)) {
        			return true;
        		}
        	}
        	
        	return false;

        	/*
            if(WindowEnum.GERMPLASM_BROWSER.getwindowName().equals(windowName) 
                    || WindowEnum.STUDY_BROWSER.getwindowName().equals(windowName) 
                    || WindowEnum.GERMPLASM_LIST_BROWSER.getwindowName().equals(windowName) 
                    || WindowEnum.GDMS.getwindowName().equals(windowName) 
                    || WindowEnum.FIELDBOOK.getwindowName().equals(windowName) 
                    || WindowEnum.OPTIMAS.getwindowName().equals(windowName) 
                    || WindowEnum.BREEDING_MANAGER.getwindowName().equals(windowName) 
                    || WindowEnum.BREEDING_VIEW.getwindowName().equals(windowName) 
                    || WindowEnum.MEMBER.getwindowName().equals(windowName)
                    ) {
                return true;
            }   return false;
            */
        }
    }

    private WindowEnum windowEnum;
    
    private String toolConfiguration;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private ToolUtil toolUtil;
    
    public OpenWindowAction() {
    }
    
    public OpenWindowAction(WindowEnum windowEnum) {
        this.windowEnum = windowEnum;
        this.toolConfiguration = WorkflowConstants.DEFAULT;
    }
    
    public OpenWindowAction(WindowEnum windowEnum, Project project, String toolConfiguration) {
        this.windowEnum = windowEnum;
        this.project = project;
        this.toolConfiguration = toolConfiguration;
    }
    
    

	@Override
    public void buttonClick(ClickEvent event) {
        
        Window window = event.getComponent().getWindow();
        
      //  launchTool(windowEnum.getwindowName(), window, true);
        launchWindow(window, windowEnum.getwindowName());
        
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

        String windowName = uriFragment.split("/")[1];
        
        if(WindowEnum.isCorrectTool(windowName)) {
        	launchWindow(window, windowName);
        } else {
            LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
                    messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
        }
    }
    
    public void launchWindow(Window window, String windowName)
    {
    	Window mywindow = null;
    	if(WindowEnum.MEMBER.getwindowName().equals(windowName) )
    	{
    		mywindow = new ProjectMemberWindow(this.project);
    		mywindow.setWidth("700");
    		
    		window.addWindow(mywindow);
    	} else {
            LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
                    messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
        }
    	
    }
   
    
}

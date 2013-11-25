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
import java.util.Date;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.gxe.GxeAnalysisComponentPanel;
import org.generationcp.ibpworkbench.ui.ProjectMembersComponentPanel;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.ibtools.breedingview.select.SelectDatasetForBreedingViewPanel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class ChangeWindowAction implements WorkflowConstants, ClickListener, ActionListener {
    private static final long serialVersionUID = 1L;
    
    private final static Logger LOG = LoggerFactory.getLogger(ChangeWindowAction.class);
    
    Project project;
     
    public static enum WindowEnums {
         GERMPLASM_BROWSER("germplasm_browser")
        ,STUDY_BROWSER("study_browser")
        ,GERMPLASM_LIST_BROWSER("germplasm_list_browser")
        ,GDMS("gdms")
        ,FIELDBOOK("fieldbook")
        ,OPTIMAS("optimas")
        ,BREEDING_MANAGER("breeding_manager")
        ,BREEDING_VIEW("breeding_view_wb")
        ,MBDT("mbdt")
        ,MEMBER("member")
        ,BREEDING_GXE("breeding_gxe")
        ;
        
        String windowName;
        
        WindowEnums(String windowName) {
            this.windowName = windowName;
        }
        
        public String getwindowName() {
            return windowName;
        }

        public static WindowEnums equivalentWindowEnum(String windowName) {
            for (WindowEnums window : WindowEnums.values()) {
                if (window.getwindowName().equals(windowName))
                    return window;
            }
            return null;
        }
        
        public static boolean isCorrectTool(String windowName) {
        	
        	for (WindowEnums winEnum : WindowEnums.values()) {
        		if (winEnum.getwindowName().equals(windowName)) {
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

    private WindowEnums windowEnums;
    
    private String toolConfiguration;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private ToolUtil toolUtil;

	private Role role;
    
    public ChangeWindowAction() {
    }
    
    public ChangeWindowAction(WindowEnums windowEnum) {
        this.windowEnums = windowEnum;
        this.toolConfiguration = WorkflowConstants.DEFAULT;
    }
    
    public ChangeWindowAction(WindowEnums windowEnum, Project project) {
        this.windowEnums = windowEnum;
        this.project = project;
      
    }
     
    public ChangeWindowAction(WindowEnums windowEnum, Project project, String toolConfiguration) {
        this.windowEnums = windowEnum;
        this.project = project;
        this.toolConfiguration = toolConfiguration;
    }
    
    public ChangeWindowAction(WindowEnums windowEnum, Project project,Role role, String toolConfiguration) {
        this.windowEnums = windowEnum;
        this.project = project;
        this.role = role;
        this.toolConfiguration = toolConfiguration;
    }
    
    

	@Override
    public void buttonClick(ClickEvent event) {
        doAction(event.getComponent().getWindow(), null, true);
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
    	String windowName = windowEnums.getwindowName();
        if(WindowEnums.isCorrectTool(windowName)) {
        	launchWindow(window, windowName, isLinkAccessed);
        } else {
            LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
                    messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
        }
    }
    
    public void launchWindow(Window window, String windowName, boolean isLinkAccessed)
    {
    	IContentWindow w = (IContentWindow) window;
    	
    	System.out.println("ChangeWindow");
    	if(WindowEnums.MEMBER.getwindowName().equals(windowName) )
    	{
    		
    		
    		 try {
                 IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
                 User user = app.getSessionData().getUserData();
                 Project currentProject = app.getSessionData().getLastOpenedProject();

                 ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, windowName, "Launched "+windowName, user, new Date());

                 workbenchDataManager.addProjectActivity(projAct);

             } catch (MiddlewareQueryException e1) {
                 MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                           "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                 return;
             }
    		 
    		 ProjectMembersComponentPanel projectLocationPanel = new ProjectMembersComponentPanel(this.project);
     		 w.showContent(projectLocationPanel);
     		 NavManager.navigateApp(window, "/ProjectMembers", isLinkAccessed);
     		
    	} else if(WindowEnums.BREEDING_GXE.getwindowName().equals(windowName) )
        	{
        		
        		
        		 try {
                     IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
                     User user = app.getSessionData().getUserData();
                     Project currentProject = app.getSessionData().getLastOpenedProject();

                     ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, windowName, "Launched "+windowName, user, new Date());

                     workbenchDataManager.addProjectActivity(projAct);

                 } catch (MiddlewareQueryException e1) {
                     MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                               "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                     return;
                 }
        		 
        		 GxeAnalysisComponentPanel gxeAnalysisPanel = new GxeAnalysisComponentPanel(this.project,this.role);
         		 w.showContent(gxeAnalysisPanel);
         		 NavManager.navigateApp(window, "/BreedingGxE", isLinkAccessed);
         		
        	} else if (WindowEnums.BREEDING_VIEW.getwindowName().equals(windowName)){
        		
        		 try {
                     IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
                     User user = app.getSessionData().getUserData();
                     Project currentProject = app.getSessionData().getLastOpenedProject();

                     ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, windowName, "Launched "+windowName, user, new Date());

                     workbenchDataManager.addProjectActivity(projAct);

                 } catch (MiddlewareQueryException e1) {
                     MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                               "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
                     return;
                 }
        		
        		 SelectDatasetForBreedingViewPanel breedingViewPanel = new SelectDatasetForBreedingViewPanel(this.project,Database.LOCAL,this.role);
        		 w.showContent(breedingViewPanel);
        		 NavManager.navigateApp(window, "/breeding_view", isLinkAccessed);
        		
        	}
    	else {
            LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
            messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
        }
    	
    }
   
    
}

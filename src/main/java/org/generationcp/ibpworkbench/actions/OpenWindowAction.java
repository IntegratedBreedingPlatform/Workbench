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

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.window.ChangePasswordWindow;
import org.generationcp.ibpworkbench.ui.window.UserToolsManagerWindow;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
        ,BACKUP_IBDB("backup_ibdb")
        ,RESTORE_IBDB("restore_ibdb")
        ,CHANGE_PASSWORD("change_password")
        ,USER_TOOLS("user_tools")
        ,SOFTWARE_LICENSING_AGREEMENT("software_license")
        ;
        
        String windowName;
        
        WindowEnum(String windowName) {
            this.windowName = windowName;
        }
        
        public String getwindowName() {
            return windowName;
        }

        public static WindowEnum equivalentWindowEnum(String windowName) {
            for (WindowEnum window : WindowEnum.values()) {
                if (window.getwindowName().equals(windowName))
                    return window;
            }
            return null;
        }
        
        public static boolean isCorrectTool(String windowName) {
        	
        	for (WindowEnum winEnum : WindowEnum.values()) {
        		if (winEnum.getwindowName().equals(windowName)) {
        			return true;
        		}
        	}
        	
        	return false;

        	
        }
    }

    private WindowEnum windowEnum;

    private String toolConfiguration;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private ToolUtil toolUtil;

	private Role role;

    public OpenWindowAction() {
    }
    
    public OpenWindowAction(WindowEnum windowEnum) {
        this.windowEnum = windowEnum;
        this.toolConfiguration = WorkflowConstants.DEFAULT;
    }
    
    public OpenWindowAction(WindowEnum windowEnum, Project project) {
        this.windowEnum = windowEnum;
        this.project = project;
      
    }
    
    public OpenWindowAction(WindowEnum windowEnum, Project project, String toolConfiguration) {
        this.windowEnum = windowEnum;
        this.project = project;
        this.toolConfiguration = toolConfiguration;
    }
    public OpenWindowAction(WindowEnum windowEnum, Project project,Role role, String toolConfiguration) {
        this.windowEnum = windowEnum;
        this.project = project;
        this.toolConfiguration = toolConfiguration;
        this.role = role;
    }
    

	@Override
    public void buttonClick(ClickEvent event) {
        
        Window window = event.getComponent().getWindow();
        
        launchWindow(window, windowEnum.getwindowName());
        
    }

    @Override
    public void doAction(Event event) {
        // does nothing
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
    
    public void launchWindow(final Window window, String windowName)
    {
    	Window mywindow = null;
    	Boolean windowLaunched = false;
    	String windowCaption = "";

    	if(WindowEnum.CHANGE_PASSWORD.getwindowName().equals(windowName) ) {
    		mywindow = new ChangePasswordWindow();

            window.addWindow(mywindow);
    		windowLaunched = false;
    		
    		windowCaption = mywindow.getCaption();
    	} else if (WindowEnum.USER_TOOLS.getwindowName().equals(windowName)) {
    		mywindow = new UserToolsManagerWindow();
    		window.addWindow(mywindow);

    		windowCaption = mywindow.getCaption();
    		
    		windowLaunched = true;
    		
    	} else if (WindowEnum.SOFTWARE_LICENSING_AGREEMENT.getwindowName().equals(windowName)) {
    		ConfirmDialog dialog = ConfirmDialog.show(window,messageSource.getMessage(Message.SOFTWARE_LICENSE_AGREEMENT),
                    messageSource.getMessage(Message.SOFTWARE_LICENSE_AGREEMENT_DETAILS,getCutOffDate()),
                    messageSource.getMessage(Message.DONE), null, new ConfirmDialog.Listener() {
                private static final long serialVersionUID = 1L;
                
				@Override
                public void onClose(ConfirmDialog dialog) {

                    if (dialog.isConfirmed()) {
                    	window.removeWindow(dialog);
                    }

                }
            });
    		dialog.setContentMode(ConfirmDialog.CONTENT_HTML);
    		
    		windowCaption = messageSource.getMessage(Message.SOFTWARE_LICENSE_AGREEMENT);
    		
    		windowLaunched = true;
    	} else {
            LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
            MessageNotifier.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR), 
            messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
        }
    	
    	// Add to Project Activity logs the launched windows
    	if (windowLaunched) {
    		try {
                IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
                User user = app.getSessionData().getUserData();
                Project currentProject = app.getSessionData().getLastOpenedProject();

                //TODO: internationalize this
                ProjectActivity projAct = new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, windowName,messageSource.getMessage(Message.LAUNCHED_APP,windowCaption), user, new Date());

                workbenchDataManager.addProjectActivity(projAct);

            } catch (MiddlewareQueryException e1) {
                MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
                                          "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
            }
    	}
    }
   
    protected String getCutOffDate() {
		//Dec 31, 2015
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH,11);
		cal.set(Calendar.DATE,31);
		Date cutOffDate = cal.getTime();
		DateFormat sdf = new SimpleDateFormat("MMMMM dd, yyyy");
		return sdf.format(cutOffDate);
		
	}
    
}

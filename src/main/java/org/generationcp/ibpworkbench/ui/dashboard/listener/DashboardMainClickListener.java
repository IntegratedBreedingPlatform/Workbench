/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.ui.dashboard.listener;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.util.SchemaVersionUtil;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import java.util.Date;

/**
 * @author Efficio.Daniel
 *
 */
@Configurable
public class DashboardMainClickListener implements ClickListener{

    private Long projectId;
    private Component source;

    @Autowired
    private WorkbenchDataManager manager;
    
    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SessionData sessionData;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private static final Logger LOG = LoggerFactory.getLogger(DashboardMainClickListener.class);
    
    public DashboardMainClickListener(Component source, Long projectId){
        this.projectId = projectId;
        this.source = source;
    }
    /* (non-Javadoc)
     * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
     */
    @Override
    public void buttonClick(ClickEvent event) {
        // TODO Auto-generated method stub
        try {
           // lets update last opened project
            Project project = sessionData.getSelectedProject();
            String minimumCropVersion = SchemaVersionUtil.getMinimumCropVersion();
            String currentCropVersion = project.getCropType().getVersion();
            if(!SchemaVersionUtil.checkIfVersionIsSupported(currentCropVersion,minimumCropVersion)) {
            	MessageNotifier.showWarning(event.getComponent().getWindow(), 
            			"",messageSource.getMessage(Message.MINIMUM_CROP_VERSION_WARNING,
            					minimumCropVersion,
            					currentCropVersion));
            }

            try {
                toolUtil.createWorkspaceDirectoriesForProject(project);
            } catch (MiddlewareQueryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            this.updateProjectLastOpenedDate(project);

            //update sidebar selection
            LOG.trace("selecting sidebar");
            WorkbenchMainView mainWindow = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();

            mainWindow.addTitle(project.getProjectName());

            if (null != WorkbenchSidebar.sidebarTreeMap.get("manage_list"))
                mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("manage_list"));


            // page change to list manager, with parameter passed
            (new LaunchWorkbenchToolAction(LaunchWorkbenchToolAction.ToolEnum.BM_LIST_MANAGER_MAIN, project ,null)).buttonClick(event);

            //System.out.println("Open list manager" + this.projectId);
        } catch (InternationalizableException e){
            LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();
            MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
        }
    }
    
	public void updateProjectLastOpenedDate(Project project) {
        try {

            // set the last opened project in the session
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

            ProjectUserInfoDAO projectUserInfoDao = manager.getProjectUserInfoDao();
            ProjectUserInfo projectUserInfo = projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId().intValue(), app.getSessionData().getUserData().getUserid());
            if (projectUserInfo != null) {
                projectUserInfo.setLastOpenDate(new Date());
                manager.saveOrUpdateProjectUserInfo(projectUserInfo);
            }

            project.setLastOpenDate(new Date());
            manager.mergeProject(project);

            app.getSessionData().setLastOpenedProject(project);

        } catch (MiddlewareQueryException e) {
            LOG.error(e.toString(), e);
        }
    }
}

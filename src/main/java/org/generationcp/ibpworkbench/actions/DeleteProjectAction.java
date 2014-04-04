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

import org.generationcp.commons.hibernate.DefaultManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class DeleteProjectAction implements ClickListener, ActionListener{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(DeleteProjectAction.class);
    private Project currentProject;
    private ClickEvent evt;


    @Autowired
    private WorkbenchDataManager manager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private DefaultManagerFactoryProvider managerFactoryProvider;
    
    public DeleteProjectAction()
    {
    }
    
    @Override
    public void buttonClick(final ClickEvent event) {
        this.evt = event;

        doAction(event.getComponent().getWindow(),"delete_program",true);
    }

	@Override
	public void doAction(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doAction(final Window window, String uriFragment,
			boolean isLinkAccessed) {
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        if(app.getMainWindow()!= null)
        {
            User currentUser = app.getSessionData().getUserData();
            this.currentProject = app.getSessionData().getSelectedProject();
            if(this.currentProject == null)
            {
                MessageNotifier.showError(window,messageSource.getMessage(Message.INVALID_OPERATION),messageSource.getMessage(Message.INVALID_NO_PROGRAM_SELECTED));

            }
            ConfirmDialog.show(app.getMainWindow(),messageSource.getMessage(Message.DELETE_PROJECT_LINK),
                    messageSource.getMessage(Message.DELETE_PROGRAM_CONFIRM,currentProject.getProjectName()),
                    messageSource.getMessage(Message.YES),
                    messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog dialog) {

                    // TODO Auto-generated method stub
                    if (dialog.isConfirmed()) {

                        try {

                            manager.deleteProjectDependencies(currentProject);
                            Project newProj = new Project();
                            newProj.setProjectId(currentProject.getProjectId());
                            newProj.setProjectName(currentProject.getProjectName());
                            newProj.setLocalDbName(currentProject.getLocalDbName());
                            newProj.setCentralDbName(currentProject.getCentralDbName());
                            managerFactoryProvider.removeProjectFromLocalSession(newProj.getProjectId());
                            manager.dropLocalDatabase(newProj);
                            manager.deleteProject(newProj);
                            
                        } catch (MiddlewareQueryException e) {
                            //MessageNotifier.showError(window,messageSource.getMessage(Message.ERROR), e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                        // go back to dashboard
                        (new HomeAction()).doAction(window, "/Home", true);

                    }


                }
            });
        }
	}

    

}

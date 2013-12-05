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

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.common.ConfirmDialog;
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

public class DeleteProjectAction implements ClickListener, ActionListener{
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(DeleteProjectAction.class);
    private Project currentProject;
    private ClickEvent evt;
    private WorkbenchDataManager workbenchDataManager;

    public DeleteProjectAction(WorkbenchDataManager workbenchDataManager)
    {
    	this.workbenchDataManager = workbenchDataManager;
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
	public void doAction(Window window, String uriFragment,
			boolean isLinkAccessed) {
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        if(app.getMainWindow()!= null)
        {
            final Window myWindow = window;
            User currentUser = app.getSessionData().getUserData();
            this.currentProject = app.getSessionData().getSelectedProject();
            if(this.currentProject == null)
            {
                MessageNotifier.showError(myWindow, "Error", "Please select a program");

            }
            ConfirmDialog.show(app.getMainWindow(), "Delete Program",  "Are you sure you want to delete "+currentProject.getProjectName()+ " ?", "Yes", "Cancel", new ConfirmDialog.Listener() {
                @Override
                public void onClose(ConfirmDialog dialog) {

                    // TODO Auto-generated method stub
                    if (dialog.isConfirmed()) {

                        try {

                            workbenchDataManager.deleteProjectDependencies(currentProject);
                            Project newProj = new Project();
                            newProj.setProjectId(currentProject.getProjectId());
                            newProj.setProjectName(currentProject.getProjectName());
                            newProj.setLocalDbName(currentProject.getLocalDbName());
                            newProj.setCentralDbName(currentProject.getCentralDbName());
                            workbenchDataManager.dropLocalDatabase(newProj);
                            workbenchDataManager.deleteProject(newProj);

                            // go back to dashboard
                            //new HomeAction().doAction(myWindow, "/Home", true);


                            WorkbenchMainView w = (WorkbenchMainView) myWindow;
                            WorkbenchDashboard workbenchDashboard = null;
                            workbenchDashboard = new WorkbenchDashboard();
                            w.setWorkbenchDashboard(workbenchDashboard);
                            w.addTitle("");
                            w.showContent(w.getWorkbenchDashboard());

                        } catch (MiddlewareQueryException e) {
                            // TODO Auto-generated catch block
                            MessageNotifier.showError(myWindow,"Error", e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }


                }
            });
        }
	}

    

}

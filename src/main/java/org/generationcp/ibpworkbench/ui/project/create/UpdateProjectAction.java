package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Button;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 10/28/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class UpdateProjectAction implements Button.ClickListener  {

    private UpdateProjectPanel projectPanel;
    private static final Logger LOG = LoggerFactory.getLogger(UpdateProjectAction.class);

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private static final long serialVersionUID = 1L;
    private static final int PROJECT_USER_ACCESS_NUMBER = 100;
    private static final int PROJECT_USER_TYPE = 422;
    private static final int PROJECT_USER_STATUS = 1;
    private static final int PROJECT_USER_ACCESS_NUMBER_CENTRAL = 150;
    private static final int PROJECT_USER_TYPE_CENTRAL = 420;

    public UpdateProjectAction(UpdateProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
    }

    @Override
    public void buttonClick(Button.ClickEvent event) {

        if (projectPanel.validate()) {
            try {
                doUpdate();
            } catch (MiddlewareQueryException e) {
                e.printStackTrace();
            }
        }


    }

    private void doUpdate() throws MiddlewareQueryException {

        LOG.debug("doUpdate >");
        LOG.debug(String.format("Project: [%s]", projectPanel.getProject())  );
        LOG.debug(String.format("Project Roles: [%s]", projectPanel.getProjectUserRoles()) );

        if (projectPanel.validate()) {
            // rename old workspace directory if found
            toolUtil.renameOldWorkspaceDirectoryToNewFormat(projectPanel.getProject().getProjectId(),projectPanel.getOldProjectName());

            // update the project
            workbenchDataManager.saveOrUpdateProject(projectPanel.getProject());

            // update project roles

            List<ProjectUserRole> updatedProjectUserRoles = projectPanel.getProjectUserRoles();

            // TODO: the following logic is best moved to workbench data manager
            List<ProjectUserRole> deleteRoles = workbenchDataManager.getProjectUserRolesByProject(projectPanel.getProject());
            // remove all previous roles assigned to current user
            for(ProjectUserRole projectUserRole : deleteRoles){
                if (projectUserRole.getUserId().intValue() == projectPanel.getCurrentUser().getUserid().intValue())
                    workbenchDataManager.deleteProjectUserRole(projectUserRole);
            }

            // add the newly updated roles
            for (ProjectUserRole projectUserRole : updatedProjectUserRoles) {
                workbenchDataManager.addProjectUserRole(projectPanel.getProject(),projectPanel.getCurrentUser(),projectUserRole.getRole());
            }

            MessageNotifier.showMessage(projectPanel.getWindow(),String.format("Successfully updated %s",projectPanel.getProject().getProjectName()),"");

            ProjectActivity projAct = new ProjectActivity(new Integer(projectPanel.getProject().getProjectId().intValue()), projectPanel.getProject(),"Update Program", "Updated Program - " + projectPanel.getProject().getProjectName(), projectPanel.getCurrentUser(), new Date());
            workbenchDataManager.addProjectActivity(projAct);

            if (IBPWorkbenchApplication.get().getMainWindow() instanceof  WorkbenchMainView)
                ((WorkbenchMainView)(IBPWorkbenchApplication.get().getMainWindow())).addTitle(projectPanel.getProject().getProjectName());
        }
    }
}
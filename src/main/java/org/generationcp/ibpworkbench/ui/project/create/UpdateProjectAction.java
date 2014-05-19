package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Button;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.StringUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
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
    private SessionData sessionData;

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
        LOG.debug(String.format("Project: [%s]", sessionData.getSelectedProject())  );
      

        if (projectPanel.validate()) {
            // rename old workspace directory if found
            toolUtil.renameOldWorkspaceDirectoryToNewFormat(sessionData.getSelectedProject().getProjectId(),projectPanel.getOldProjectName());

            // update the project
            Project updatedProject = projectPanel.projectBasicDetailsComponent.validateAndSave();
            sessionData.getSelectedProject().setProjectName(updatedProject.getProjectName());
            sessionData.getSelectedProject().setStartDate(updatedProject.getStartDate());
            workbenchDataManager.saveOrUpdateProject(sessionData.getSelectedProject());

            // update project roles

            List<ProjectUserRole> updatedProjectUserRoles = getProjectUserRoles();

            // TODO: the following logic is best moved to workbench data manager
            List<ProjectUserRole> deleteRoles = workbenchDataManager.getProjectUserRolesByProject(sessionData.getSelectedProject());
            // remove all previous roles assigned to current user
            for(ProjectUserRole projectUserRole : deleteRoles){
                if (projectUserRole.getUserId().intValue() == sessionData.getUserData().getUserid())
                    workbenchDataManager.deleteProjectUserRole(projectUserRole);
            }

            // add the newly updated roles
            for (ProjectUserRole projectUserRole : updatedProjectUserRoles) {
                workbenchDataManager.addProjectUserRole(sessionData.getSelectedProject(),sessionData.getUserData(),projectUserRole.getRole());
            }

            //MessageNotifier.showMessage(projectPanel.getWindow(),String.format("Successfully updated %s",projectPanel.getProject().getProjectName()),"");
            MessageNotifier.showMessage(projectPanel.getWindow(),"Program update is successful",String.format("%s is updated.", StringUtils.abbreviate(sessionData.getSelectedProject().getProjectName(),50)));

            ProjectActivity projAct = new ProjectActivity(new Integer(sessionData.getSelectedProject().getProjectId().intValue()),sessionData.getSelectedProject(),"Update Program", "Updated Program - " + sessionData.getSelectedProject().getProjectName(),sessionData.getUserData(), new Date());
            workbenchDataManager.addProjectActivity(projAct);

            if (IBPWorkbenchApplication.get().getMainWindow() instanceof  WorkbenchMainView)
                ((WorkbenchMainView)(IBPWorkbenchApplication.get().getMainWindow())).addTitle(sessionData.getSelectedProject().getProjectName());
        }
    }

    public List<ProjectUserRole> getProjectUserRoles() {

        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        try {
            //WorkflowTemplate managerTemplate = workbenchDataManager.getWorkflowTemplateByName(WorkflowTemplate.MANAGER_NAME).get(0);

            // BY DEFAULT, current user has all the roles
            for (Role role : workbenchDataManager.getAllRoles()) {
                ProjectUserRole projectUserRole = new ProjectUserRole();
                projectUserRole.setRole(role);
                projectUserRoles.add(projectUserRole);

            }

        } catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        return projectUserRoles;
    }
}
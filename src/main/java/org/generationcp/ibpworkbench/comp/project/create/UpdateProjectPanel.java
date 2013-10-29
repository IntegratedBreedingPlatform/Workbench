package org.generationcp.ibpworkbench.comp.project.create;

import com.vaadin.ui.*;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 10/28/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateProjectPanel extends CreateProjectPanel {
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    private String oldProjectName;

    public UpdateProjectPanel() {
        super();
    }

    @Override
    protected void initializeActions() {
        super.saveProjectButton.addListener(new UpdateProjectAction(this));
        cancelButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {

                    Project project = UpdateProjectPanel.this.getProject();

                    // I'm defaulting the Role to Manager role
                    WorkflowTemplate managerTemplate = workbenchDataManager.getWorkflowTemplateByName(WorkflowTemplate.MANAGER_NAME).get(0);
                    Role role = workbenchDataManager.getRoleByNameAndWorkflowTemplate(Role.MANAGER_ROLE_NAME, managerTemplate);

                    String url = String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", project.getProjectId(), role.getRoleId());
                    (new OpenWorkflowForRoleAction(project)).doAction(event.getComponent().getWindow(), url, true);
                } catch (Exception e) {
                    if(e.getCause() instanceof InternationalizableException) {
                        InternationalizableException i = (InternationalizableException) e.getCause();
                        MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
                    }
                    return;
                }
            }
        });

    }

    @Override
    protected  void initializeComponents() {
        this.newProjectTitleArea = new HorizontalLayout();
        this.newProjectTitleArea.setSpacing(true);

        this.newProjectTitleArea.setHeight("40px");
        //newProjectTitleArea.setStyleName("gcp-content-title");

        this.addComponent(newProjectTitleArea);

        UpdateProjectAccordion projectAccordion = new UpdateProjectAccordion(this);

        this.createProjectAccordion = projectAccordion;
        this.addComponent(createProjectAccordion);

        this.buttonArea = layoutButtonArea();
        this.addComponent(buttonArea);

    }

    @Override
    protected void initializeValues() {
        // initialize component values

    }

    @Override
    public void afterPropertiesSet() {
        try {
            // initialize state
            currentUser = workbenchDataManager.getUserById(IBPWorkbenchApplication.get().getSessionData().getUserData().getUserid());   // get hibernate managed version of user
            project = workbenchDataManager.getLastOpenedProject(currentUser.getUserid());
            oldProjectName = new String(project.getProjectName());


            this.setSelectedCropType(project.getCropType());

            this.initializeComponents();
            this.initializeLayout();
            this.initializeActions();
            this.initializeValues();


        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }



    }


    @Override
    public void setTitle(String label, String description) {
        newProjectTitleArea.removeAllComponents();

        Label title = new Label("Update Project: " + label);

        newProjectTitleArea.setStyleName("gcp-content-title");
        newProjectTitleArea.addComponent(title);

        Label descLbl = new Label(description);
        descLbl.setWidth("300px");

        PopupView popup = new PopupView("?",descLbl);
        popup.setStyleName("gcp-popup-view");

        newProjectTitleArea.addComponent(popup);

        newProjectTitleArea.setComponentAlignment(title, Alignment.MIDDLE_LEFT);
        newProjectTitleArea.setComponentAlignment(popup, Alignment.MIDDLE_LEFT);
    }

    public String getOldProjectName() {
        return oldProjectName;
    }
}

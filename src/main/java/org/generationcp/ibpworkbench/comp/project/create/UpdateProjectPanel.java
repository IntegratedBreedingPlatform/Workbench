package org.generationcp.ibpworkbench.comp.project.create;

import com.vaadin.ui.*;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
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

    public UpdateProjectPanel() {
        super();
    }

    @Override
    protected void initializeActions() {
        super.saveProjectButton.addListener(new UpdateProjectAction(this));
        cancelButton.addListener(new HomeAction());

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
}

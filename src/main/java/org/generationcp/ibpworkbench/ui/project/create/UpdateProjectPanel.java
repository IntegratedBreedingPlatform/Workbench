package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.DeleteProjectAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 10/28/13
 * Time: 10:59 AM
 * To change this template use File | Settings | File Templates.
 */
@Configurable
public class UpdateProjectPanel extends CreateProjectPanel {


    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SessionData sessionData;

    private Label heading;

    private Button deleteProgramButton;

    public UpdateProjectPanel() {

    }

    @Override
    protected void initializeActions() {
        super.saveProjectButton.addListener(new UpdateProjectAction(this));
        super.saveProjectButton.setCaption("Save");
        cancelButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                projectBasicDetailsComponent.updateProjectDetailsFormField(sessionData.getSelectedProject());

            }
        });

        deleteProgramButton.addListener(new DeleteProjectAction());
    }


    @Override
    protected void initializeComponents() {

        newProjectTitleArea = new HorizontalLayout();
        newProjectTitleArea.setSpacing(true);

        heading = new Label("<span class=\"fa fa-file-text-o\" style=\"color: #009DDA; font-size: 23px \" ></span>&nbsp;Basic Details", Label.CONTENT_XHTML);
        heading.setStyleName(Bootstrap.Typography.H4.styleName());

        deleteProgramButton = new Button("DELETE PROGRAM");
        deleteProgramButton.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

        newProjectTitleArea.addComponent(heading);
        newProjectTitleArea.addComponent(deleteProgramButton);
        newProjectTitleArea.setComponentAlignment(deleteProgramButton, Alignment.MIDDLE_RIGHT);
        newProjectTitleArea.setSizeUndefined();
        newProjectTitleArea.setWidth("100%");
        newProjectTitleArea.setMargin(false, false, false, false);	// move this to css



        projectBasicDetailsComponent = new ProjectBasicDetailsComponent(this, true);

        projectBasicDetailsComponent.updateProjectDetailsFormField(sessionData.getSelectedProject());

        buttonArea = layoutButtonArea();
    }

    @Override
    protected void initializeLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setMargin(new Layout.MarginInfo(true, true, true, true));
        root.setSpacing(true);
        root.addComponent(newProjectTitleArea);
        root.addComponent(projectBasicDetailsComponent);
        root.addComponent(buttonArea);
        root.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);

        this.setScrollable(false);
        this.setSizeFull();
        this.setContent(root);
        this.setStyleName(Reindeer.PANEL_LIGHT);
    }

/*
    protected Component createMainPanel() {
        HorizontalLayout panel = new HorizontalLayout();
        panel.setSpacing(false);

        panel.addComponent(projectBasicDetailsComponent);
        panel.addComponent(deleteProgramButton);

        panel.setComponentAlignment(projectBasicDetailsComponent, Alignment.MIDDLE_LEFT);
        panel.setComponentAlignment(deleteProgramButton, Alignment.MIDDLE_CENTER);

        panel.setSizeFull();

        return panel;
    }
*/

    @Override
    protected void initializeValues() {
        // initialize component values

    }

    @Override
    public void afterPropertiesSet() {
        try {
            // initialize state
            currentUser = workbenchDataManager.getUserById(sessionData.getUserData().getUserid());   // get hibernate managed version of user


            this.initializeComponents();
            this.initializeLayout();
            this.initializeActions();
            this.initializeValues();


        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }
    }

    public String getOldProjectName() {
        return sessionData.getSelectedProject().getProjectName();
    }

    public boolean validate() {
        if (projectBasicDetailsComponent.validate()) {
            return true;
        }

        return false;
    }

    public void hideDeleteBtn() {
        this.deleteProgramButton.setVisible(false);
    }

}

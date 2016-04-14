
package org.generationcp.ibpworkbench.ui.project.create;

import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.DeleteProjectAction;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 10/28/13 Time: 10:59 AM To change this template use File | Settings | File Templates.
 */
@Configurable
public class UpdateProjectPanel extends CreateProjectPanel {

	private static final long serialVersionUID = 1L;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SessionData sessionData;

	private Label heading;

	private Button deleteProgramButton;

	/**
	 * Default constructor, instantiated by ProgamAdministratorPanel as its component for
	 * updating workbench program
	 */
	public UpdateProjectPanel() {
		// default constructor
	}

	@Override
	protected void initializeActions() {


		try{
			this.cancelButton.setVisible(false);
			super.saveProjectButton.setVisible(false);
			saveAndDeleteProjectActionUpdate();
		}catch(AccessDeniedException ex){
			// Do nothing the screen needs to be display but the
		}

	}

	/**
	 * Only the Save and Delete actions need to be restricted
	 * If a user with unauthorize access is trying to access this method an ${@link AccessDeniedException} will be thrown.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	private void saveAndDeleteProjectActionUpdate() {
		super.saveProjectButton.addListener(new UpdateProjectAction(this));
		super.saveProjectButton.setCaption("Save");
		super.saveProjectButton.setVisible(true);
		this.deleteProgramButton.addListener(new DeleteProjectAction());
		this.cancelButton.setVisible(true);

		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				UpdateProjectPanel.this.projectBasicDetailsComponent.updateProjectDetailsFormField(UpdateProjectPanel.this.sessionData
						.getSelectedProject());

			}
		});
	}

	@Override
	protected void initializeComponents() {

		this.newProjectTitleArea = new HorizontalLayout();
		this.newProjectTitleArea.setSpacing(true);

		this.heading =
				new Label("<span class=\"bms-fa-text-o\" style=\"color: #009DDA; font-size: 23px \" ></span>&nbsp;Basic Details",
						Label.CONTENT_XHTML);
		this.heading.setStyleName(Bootstrap.Typography.H4.styleName());


		this.newProjectTitleArea.addComponent(this.heading);

		this.newProjectTitleArea.setSizeUndefined();
		this.newProjectTitleArea.setWidth("100%");
		this.newProjectTitleArea.setMargin(false, false, false, false); // move this to css

		this.projectBasicDetailsComponent = new ProjectBasicDetailsComponent(this, true);

		this.projectBasicDetailsComponent.updateProjectDetailsFormField(this.sessionData.getSelectedProject());
		this.projectBasicDetailsComponent.disableForm();
		this.buttonArea = this.layoutButtonArea();

		try {
			initializeRestrictedComponents();
		}catch(AccessDeniedException ex){
			/*
			 * Do nothing: the screen needs to be displayed, only some of the components needs to be hidden.
			 * If a user with unauthorize access is trying to access this method an ${@link AccessDeniedException} will be thrown.
	 		 */
		}
	}
	/**
	 * Only the Delete button need to be restricted
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	private void initializeRestrictedComponents() {

		this.deleteProgramButton = new Button("DELETE PROGRAM");
		this.deleteProgramButton.setStyleName(Bootstrap.Buttons.INFO.styleName() + " loc-add-btn");

		this.newProjectTitleArea.addComponent(this.deleteProgramButton);
		this.newProjectTitleArea.setComponentAlignment(this.deleteProgramButton, Alignment.MIDDLE_RIGHT);
		this.projectBasicDetailsComponent.enableForm();
	}

	@Override
	protected void initializeLayout() {
		VerticalLayout root = new VerticalLayout();
		root.setMargin(new Layout.MarginInfo(true, true, true, true));
		root.setSpacing(true);
		root.addComponent(this.newProjectTitleArea);
		root.addComponent(this.projectBasicDetailsComponent);
		root.addComponent(this.buttonArea);
		root.setComponentAlignment(this.buttonArea, Alignment.TOP_CENTER);

		this.setScrollable(false);
		this.setSizeFull();
		this.setContent(root);
		this.setStyleName(Reindeer.PANEL_LIGHT);
	}

	@Override
	public void afterPropertiesSet() {
		// initialize state
		// get hibernate managed version of user
		this.currentUser = this.workbenchDataManager.getUserById(this.sessionData.getUserData().getUserid());

		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();

	}

	public String getOldProjectName() {
		return this.sessionData.getSelectedProject().getProjectName();
	}

	public boolean validate() {
		return this.projectBasicDetailsComponent.validate();

	}

	public void hideDeleteBtn() {
		if(this.deleteProgramButton!=null){
			this.deleteProgramButton.setVisible(false);
		}
	}

}

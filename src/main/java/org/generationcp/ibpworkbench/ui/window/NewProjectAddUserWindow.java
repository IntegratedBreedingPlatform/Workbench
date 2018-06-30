
package org.generationcp.ibpworkbench.ui.window;

import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.ibpworkbench.ui.programmembers.NewProjectAddUserPanel;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

public class NewProjectAddUserWindow extends BaseSubWindow {

	/**
	 *
	 */
	private static final long serialVersionUID = 3983198771242295731L;

	private NewProjectAddUserPanel newUserPanel;

	private final TwinTableSelect<WorkbenchUser> membersSelect;

	private VerticalLayout layout;

	public NewProjectAddUserWindow(TwinTableSelect<WorkbenchUser> membersSelect) {
		this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.membersSelect = membersSelect;

		// set as modal window, other components are disabled while window is open
		this.setModal(true);

		// define window size, set as not resizable
		this.setWidth("925px");
		this.setHeight("540px");
		this.setResizable(false);

		// center window within the browser
		this.center();

		this.assemble();

		this.setCaption("Add a New Workbench User");
	}

	protected void initializeComponents() {

		this.layout = new VerticalLayout();
		this.layout.setDebugId("layout");
		this.setContent(this.layout);

		// reuse "Register New Account" window from login screen
		this.newUserPanel = new NewProjectAddUserPanel(this.membersSelect);
		this.newUserPanel.setDebugId("newUserPanel");
		this.newUserPanel.setSizeFull();
		this.layout.addComponent(this.newUserPanel);
	}

	protected void initializeLayout() {
		this.layout.setSpacing(true);
		this.layout.setMargin(true);
	}

	protected void initializeActions() {
		// currently does nothing
	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}
}

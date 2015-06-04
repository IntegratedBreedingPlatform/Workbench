
package org.generationcp.ibpworkbench.ui.programadministration;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.ibpworkbench.ui.project.create.UpdateProjectPanel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ProgramAdministrationPanel extends Panel implements InitializingBean {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	private VerticalLayout rootLayout;

	private TabSheet tabSheet;

	// TABS
	private UpdateProjectPanel updateProjectPanel;
	private ProgramMembersPanel programMembersPanel;
	private ProgramLocationsView programLocationsView;
	private ProgramMethodsView programMethodsView;

	public ProgramAdministrationPanel() {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
		this.updateLabels();
	}

	private void updateLabels() {
		// do nothing
	}

	protected void assemble() {

		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();

	}

	protected void initializeComponents() {
		this.rootLayout = new VerticalLayout();
		this.tabSheet = this.generateTabSheet();

		this.updateProjectPanel = new UpdateProjectPanel();
		this.programMembersPanel = new ProgramMembersPanel(this.sessionData.getLastOpenedProject());
		this.programLocationsView = new ProgramLocationsView(this.sessionData.getLastOpenedProject());
		this.programMethodsView = new ProgramMethodsView(this.sessionData.getLastOpenedProject());
	}

	protected void initializeActions() {

	}

	protected void initializeLayout() {

		final Label heading = new Label("Manage Program Settings");
		final Label headingDesc =
				new Label(
						"Modify the details of this program, and manage the members, locations, and methods associated with it using the tabs below.");
		heading.setStyleName(Bootstrap.Typography.H1.styleName());

		this.rootLayout.setMargin(new Layout.MarginInfo(false, true, true, true));
		this.rootLayout.setWidth("100%");
		this.rootLayout.setSpacing(true);

		this.updateProjectPanel.setVisible(true);
		this.programMembersPanel.setVisible(true);
		this.programLocationsView.setVisible(true);
		this.programMethodsView.setVisible(true);

		this.tabSheet.addTab(this.updateProjectPanel);
		this.tabSheet.getTab(this.updateProjectPanel).setClosable(false);
		this.tabSheet.getTab(this.updateProjectPanel).setCaption("Basic Details");

		this.tabSheet.addTab(this.programMembersPanel);
		this.tabSheet.getTab(this.programMembersPanel).setClosable(false);
		this.tabSheet.getTab(this.programMembersPanel).setCaption("Members");

		this.tabSheet.addTab(this.programLocationsView);
		this.tabSheet.getTab(this.programLocationsView).setClosable(false);
		this.tabSheet.getTab(this.programLocationsView).setCaption("Locations");

		this.tabSheet.addTab(this.programMethodsView);
		this.tabSheet.getTab(this.programMethodsView).setClosable(false);
		this.tabSheet.getTab(this.programMethodsView).setCaption("Breeding Methods");

		this.rootLayout.addComponent(heading);
		this.rootLayout.addComponent(headingDesc);
		this.rootLayout.addComponent(this.tabSheet);

		this.setContent(this.rootLayout);
		this.setScrollable(true);
		this.setSizeFull();
	}

	protected TabSheet generateTabSheet() {
		TabSheet tab = new TabSheet();

		tab.setImmediate(true);
		tab.setStyleName(Reindeer.TABSHEET_MINIMAL);
		tab.setStyleName("panel-border");

		return tab;
	}

}

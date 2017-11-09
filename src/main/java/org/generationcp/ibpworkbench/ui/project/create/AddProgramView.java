package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.listener.LaunchProgramAction;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;

@Configurable
public class AddProgramView extends Panel implements InitializingBean {

	private static final long serialVersionUID = 1L;

	private HorizontalLayout titleLayout;

	@Autowired
	private SessionData sessionData;

	@Value("${workbench.is.single.user.only}")
	private String isSingleUserOnly;

	private VerticalLayout rootLayout;

	private TabSheet tabSheet;

	// TABS
	protected CreateProjectPanel createProjectPanel;
	protected ProjectMembersComponent programMembersPanel;
	protected ProgramLocationsView programLocationsView;
	protected ProgramMethodsView programMethodsView;

	// container of the tabs
	private VerticalLayout programMethodsContainer;
	private VerticalLayout programLocationsContainer;
	private VerticalLayout programMembersContainer;
	private VerticalLayout basicDetailsContainer;

	private AddProgramPresenter presenter;
	private Button finishButton;
	private Button cancelBtn;

	private int initialTabView = OpenNewProjectAction.BASIC_DETAILS_TAB;

	public AddProgramView() {
	}

	public AddProgramView(final int initialTabView) {
		this.initialTabView = initialTabView;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.presenter = new AddProgramPresenter(this);

		this.assemble();
		this.updateLabels();
	}

	private void updateLabels() {
		// currently does nothing
	}

	protected void assemble() {

		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();

	}

	protected void initializeComponents() {
		this.rootLayout = new VerticalLayout();
		this.rootLayout.setDebugId("rootLayout");

		this.tabSheet = this.generateTabSheet();

		this.createProjectPanel = new CreateProjectPanel(this.presenter);
		this.createProjectPanel.setDebugId("createProjectPanel");
		this.programMembersPanel = new ProjectMembersComponent(this.presenter);
		this.programMembersPanel.setDebugId("programMembersPanel");

		this.programLocationsContainer = new VerticalLayout();
		this.getProgramLocationsContainer().setDebugId("programLocationsContainer");
		this.programMethodsContainer = new VerticalLayout();
		this.getProgramMethodsContainer().setDebugId("programMethodsContainer");
		this.programMembersContainer = new VerticalLayout();
		this.getProgramMembersContainer().setDebugId("programMembersContainer");
		this.basicDetailsContainer = new VerticalLayout();
		this.getBasicDetailsContainer().setDebugId("basicDetailsContainer");

		this.getProgramLocationsContainer().setMargin(false);
		this.getProgramLocationsContainer().setSpacing(false);

		this.getProgramMethodsContainer().setMargin(false);
		this.getProgramMethodsContainer().setSpacing(false);

		this.getProgramMembersContainer().setMargin(false);
		this.getProgramMembersContainer().setSpacing(false);
		this.getProgramMembersContainer().setVisible(false);

		this.getBasicDetailsContainer().setMargin(false);
		this.getBasicDetailsContainer().setSpacing(false);

		// finish button
		this.finishButton = new Button("Finish");
		this.finishButton.setDebugId("finishButton");
		this.finishButton.setEnabled(false);
		this.finishButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.finishButton.setDebugId("vaadin_finish_btn");

		// finish button
		this.cancelBtn = new Button("Cancel");
		this.cancelBtn.setDebugId("cancelBtn");
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	protected void addProgramMembersTab(final TabSheet tabSheet, final VerticalLayout programMembersContainer) {

		// Do not display the Program Members tab if BMS is in single user mode.
		if (!Boolean.parseBoolean(getIsSingleUserOnly())) {
			programMembersContainer.setVisible(true);
			tabSheet.addTab(programMembersContainer);
			tabSheet.getTab(programMembersContainer).setClosable(false);
			tabSheet.getTab(programMembersContainer).setCaption("Members");
		}

	}

	protected void initializeActions() {
		this.finishButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent clickEvent) {
				final Project newlyCreatedProgram = AddProgramView.this.sessionData.getSelectedProject();
				new LaunchProgramAction(newlyCreatedProgram).buttonClick(clickEvent);
			}
		});

		this.cancelBtn.addListener(new HomeAction());
	}

	protected void initializeLayout() {

		this.setTitleContent();

		final Label headingDesc = new Label(
				"To provide additional Program configuration, " + "click on <em>Members, Locations, and Breeding Method</em> tabs."
						+ " Note that <em>Locations and Methods</em> are optional and will be available once you"
						+ " complete the <em>Basic Details</em> form by clicking <em>Save</em>."
						+ " Click <em>Finish</em> to complete the operation.", Label.CONTENT_XHTML);

		this.rootLayout.setMargin(new Layout.MarginInfo(false, true, true, true));
		this.rootLayout.setWidth("100%");
		this.rootLayout.setSpacing(true);

		this.getBasicDetailsContainer().addComponent(this.createProjectPanel);
		this.getProgramMembersContainer().addComponent(this.programMembersPanel);

		this.getTabSheet().addTab(this.getBasicDetailsContainer());
		this.getTabSheet().getTab(this.getBasicDetailsContainer()).setClosable(false);
		this.getTabSheet().getTab(this.getBasicDetailsContainer()).setCaption("Basic Details");

		try {
			addProgramMembersTab(this.tabSheet, this.programMembersContainer);
		} catch (final AccessDeniedException ex) {
			//Do nothing, if the user does not have the required roles the screen needs to be displayed as well.
		}

		this.getTabSheet().addTab(this.getProgramLocationsContainer());
		this.getTabSheet().getTab(this.getProgramLocationsContainer()).setClosable(false);
		this.getTabSheet().getTab(this.getProgramLocationsContainer()).setEnabled(false);
		this.getTabSheet().getTab(this.getProgramLocationsContainer()).setCaption("Locations");

		this.getTabSheet().addTab(this.getProgramMethodsContainer());
		this.getTabSheet().getTab(this.getProgramMethodsContainer()).setClosable(false);
		this.getTabSheet().getTab(this.getProgramMethodsContainer()).setEnabled(false);

		this.getTabSheet().getTab(this.getProgramMethodsContainer()).setCaption("Breeding Methods");

		this.rootLayout.addComponent(this.titleLayout);
		this.rootLayout.addComponent(headingDesc);
		this.rootLayout.addComponent(this.getTabSheet());

		final HorizontalLayout btnContainer = new HorizontalLayout();
		btnContainer.setDebugId("btnContainer");
		btnContainer.setSpacing(true);
		btnContainer.setSizeUndefined();

		btnContainer.addComponent(this.cancelBtn);
		btnContainer.addComponent(this.finishButton);

		this.rootLayout.addComponent(btnContainer);
		this.rootLayout.setComponentAlignment(btnContainer, Alignment.MIDDLE_CENTER);

		// set initial tab view
		this.getTabSheet().setSelectedTab(this.initialTabView);

		this.setContent(this.rootLayout);
		this.setScrollable(true);
		this.setSizeFull();
	}

	private void setTitleContent() {
		this.titleLayout = new HorizontalLayout();
		this.titleLayout.setDebugId("titleLayout");
		this.titleLayout.setSpacing(true);

		final Label toolTitle = new Label("Add a Program");
		toolTitle.setDebugId("toolTitle");
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		toolTitle.setWidth("216px");

		this.titleLayout.addComponent(toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.PROGRAM_CREATION, "Go to Program Creation Tutorial"));
	}

	protected TabSheet generateTabSheet() {
		final TabSheet tab = new TabSheet();
		tab.setDebugId("tab");

		tab.setImmediate(true);
		tab.setStyleName(Reindeer.TABSHEET_MINIMAL);
		tab.setStyleName("panel-border");

		return tab;
	}

	public void updateUIOnProgramSave(final Project project) {
		if (IBPWorkbenchApplication.get().getMainWindow() instanceof WorkbenchMainView) {
			((WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow())
					.addTitle(this.sessionData.getSelectedProject().getProjectName());
		}

		// initialize program methods and view and set them to the tabs
		this.programMethodsView = new ProgramMethodsView(project);
		this.programMethodsView.setDebugId("programMethodsView");
		this.programLocationsView = new ProgramLocationsView(project);
		this.programLocationsView.setDebugId("programLocationsView");

		this.getTabSheet().getTab(this.getProgramMethodsContainer()).setEnabled(true);
		this.getProgramMethodsContainer().removeAllComponents();
		this.getProgramMethodsContainer().addComponent(this.programMethodsView);

		this.getTabSheet().getTab(this.getProgramLocationsContainer()).setEnabled(true);
		this.getProgramLocationsContainer().removeAllComponents();
		this.getProgramLocationsContainer().addComponent(this.programLocationsView);

		// re-initialize program members and basic details (in update mode)
		this.getBasicDetailsContainer().removeAllComponents();
		final UpdateProjectPanel updateProjectPanel = new UpdateProjectPanel();
		updateProjectPanel.setDebugId("updateProjectPanel");
		updateProjectPanel.hideDeleteBtn();
		this.getBasicDetailsContainer().addComponent(updateProjectPanel);

		this.getProgramMembersContainer().removeAllComponents();
		this.getProgramMembersContainer().addComponent(new ProgramMembersPanel(this.sessionData.getLastOpenedProject()));

		this.finishButton.setEnabled(true);
		this.cancelBtn.setEnabled(false);
	}

	public void disableOptionalTabsAndFinish() {
		this.getTabSheet().getTab(this.getProgramMethodsContainer()).setEnabled(false);
		this.getTabSheet().getTab(this.getProgramLocationsContainer()).setEnabled(false);

		this.finishButton.setEnabled(false);
	}

	public void enableFinishBtn() {
		this.finishButton.setEnabled(true);
	}

	public void resetBasicDetails() {
		this.getBasicDetailsContainer().removeAllComponents();

		this.createProjectPanel = new CreateProjectPanel(this.presenter);
		this.createProjectPanel.setDebugId("createProjectPanel");
		this.getBasicDetailsContainer().addComponent(this.createProjectPanel);
	}

	public void resetProgramMembers() {
		this.getProgramMembersContainer().removeAllComponents();
		this.programMembersPanel = new ProjectMembersComponent(this.presenter);
		this.programMembersPanel.setDebugId("programMembersPanel");
		this.getProgramMembersContainer().addComponent(this.programMembersPanel);
	}

	public Collection<Location> getFavoriteLocations() {

		if (this.programLocationsView != null) {
			return this.programLocationsView.getFavoriteLocations();
		}

		return new ArrayList<Location>();
	}

	public Collection<Method> getFavoriteMethods() {
		if (this.programMethodsView != null) {
			return this.programMethodsView.getFavoriteMethods();
		}

		return new ArrayList<Method>();
	}

	protected String getIsSingleUserOnly() {
		return isSingleUserOnly;
	}

	protected void setIsSingleUserOnly(final String isSingleUserOnly) {
		this.isSingleUserOnly = isSingleUserOnly;
	}

	protected TabSheet getTabSheet() {
		return tabSheet;
	}

	protected VerticalLayout getProgramMethodsContainer() {
		return programMethodsContainer;
	}

	protected VerticalLayout getProgramLocationsContainer() {
		return programLocationsContainer;
	}

	protected VerticalLayout getProgramMembersContainer() {
		return programMembersContainer;
	}

	protected VerticalLayout getBasicDetailsContainer() {
		return basicDetailsContainer;
	}
}

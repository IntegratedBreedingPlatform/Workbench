
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.ArrayList;
import java.util.Collection;

import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainClickListener;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class AddProgramView extends Panel implements InitializingBean {

	private static final long serialVersionUID = 1L;

	private HorizontalLayout titleLayout;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

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

	public AddProgramView(int initialTabView) {
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

		this.tabSheet = this.generateTabSheet();

		this.createProjectPanel = new CreateProjectPanel(this.presenter);
		this.programMembersPanel = new ProjectMembersComponent(this.presenter);

		this.programLocationsContainer = new VerticalLayout();
		this.programMethodsContainer = new VerticalLayout();
		this.programMembersContainer = new VerticalLayout();
		this.basicDetailsContainer = new VerticalLayout();

		this.programLocationsContainer.setMargin(false);
		this.programLocationsContainer.setSpacing(false);

		this.programMethodsContainer.setMargin(false);
		this.programMethodsContainer.setSpacing(false);

		this.programMembersContainer.setMargin(false);
		this.programMembersContainer.setSpacing(false);

		this.basicDetailsContainer.setMargin(false);
		this.basicDetailsContainer.setSpacing(false);

		// finish button
		this.finishButton = new Button("Finish");
		this.finishButton.setEnabled(false);
		this.finishButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.finishButton.setDebugId("vaadin_finish_btn");

		// finish button
		this.cancelBtn = new Button("Cancel");
	}

	protected void initializeActions() {
		this.finishButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				final Project newlyCreatedProgram = AddProgramView.this.sessionData.getSelectedProject();
				new DashboardMainClickListener(IBPWorkbenchApplication.get().getMainWindow(), newlyCreatedProgram
						.getProjectId()).buttonClick(clickEvent);
			}
		});

		this.cancelBtn.addListener(new HomeAction());
	}

	protected void initializeLayout() {

		this.setTitleContent();

		final Label headingDesc =
				new Label("To provide additional Program configuration, "
						+ "click on <em>Members, Locations, and Breeding Method</em> tabs."
						+ " Note that <em>Locations and Methods</em> are optional and will be available once you"
						+ " complete the <em>Basic Details</em> form by clicking <em>Save</em>."
						+ " Click <em>Finish</em> to complete the operation.", Label.CONTENT_XHTML);

		this.rootLayout.setMargin(new Layout.MarginInfo(false, true, true, true));
		this.rootLayout.setWidth("100%");
		this.rootLayout.setSpacing(true);

		this.basicDetailsContainer.addComponent(this.createProjectPanel);
		this.programMembersContainer.addComponent(this.programMembersPanel);

		this.tabSheet.addTab(this.basicDetailsContainer);
		this.tabSheet.getTab(this.basicDetailsContainer).setClosable(false);
		this.tabSheet.getTab(this.basicDetailsContainer).setCaption("Basic Details");

		this.tabSheet.addTab(this.programMembersContainer);
		this.tabSheet.getTab(this.programMembersContainer).setClosable(false);
		this.tabSheet.getTab(this.programMembersContainer).setCaption("Members");

		this.tabSheet.addTab(this.programLocationsContainer);
		this.tabSheet.getTab(this.programLocationsContainer).setClosable(false);
		this.tabSheet.getTab(this.programLocationsContainer).setEnabled(false);
		this.tabSheet.getTab(this.programLocationsContainer).setCaption("Locations");

		this.tabSheet.addTab(this.programMethodsContainer);
		this.tabSheet.getTab(this.programMethodsContainer).setClosable(false);
		this.tabSheet.getTab(this.programMethodsContainer).setEnabled(false);

		this.tabSheet.getTab(this.programMethodsContainer).setCaption("Breeding Methods");

		this.rootLayout.addComponent(this.titleLayout);
		this.rootLayout.addComponent(headingDesc);
		this.rootLayout.addComponent(this.tabSheet);

		final HorizontalLayout btnContainer = new HorizontalLayout();
		btnContainer.setSpacing(true);
		btnContainer.setSizeUndefined();

		btnContainer.addComponent(this.cancelBtn);
		btnContainer.addComponent(this.finishButton);

		this.rootLayout.addComponent(btnContainer);
		this.rootLayout.setComponentAlignment(btnContainer, Alignment.MIDDLE_CENTER);

		// set initial tab view
		this.tabSheet.setSelectedTab(this.initialTabView);

		this.setContent(this.rootLayout);
		this.setScrollable(true);
		this.setSizeFull();
	}

	private void setTitleContent() {
		this.titleLayout = new HorizontalLayout();
		this.titleLayout.setSpacing(true);

		Label toolTitle = new Label("Add a Program");
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		toolTitle.setWidth("216px");

		this.titleLayout.addComponent(toolTitle);
		this.titleLayout.addComponent(new HelpButton(HelpModule.PROGRAM_CREATION, "Go to Program Creation Tutorial"));
	}

	protected TabSheet generateTabSheet() {
		TabSheet tab = new TabSheet();

		tab.setImmediate(true);
		tab.setStyleName(Reindeer.TABSHEET_MINIMAL);
		tab.setStyleName("panel-border");

		return tab;
	}

	public void updateUIOnProgramSave(Project project) {
		if (IBPWorkbenchApplication.get().getMainWindow() instanceof WorkbenchMainView) {
			((WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow()).addTitle(this.sessionData.getSelectedProject()
					.getProjectName());
		}

		// initialize program methods and view and set them to the tabs
		this.programMethodsView = new ProgramMethodsView(project);
		this.programLocationsView = new ProgramLocationsView(project);

		this.tabSheet.getTab(this.programMethodsContainer).setEnabled(true);
		this.programMethodsContainer.removeAllComponents();
		this.programMethodsContainer.addComponent(this.programMethodsView);

		this.tabSheet.getTab(this.programLocationsContainer).setEnabled(true);
		this.programLocationsContainer.removeAllComponents();
		this.programLocationsContainer.addComponent(this.programLocationsView);

		// re-initialize program members and basic details (in update mode)
		this.basicDetailsContainer.removeAllComponents();
		UpdateProjectPanel updateProjectPanel = new UpdateProjectPanel();
		updateProjectPanel.hideDeleteBtn();
		this.basicDetailsContainer.addComponent(updateProjectPanel);

		this.programMembersContainer.removeAllComponents();
		this.programMembersContainer.addComponent(new ProgramMembersPanel(this.sessionData.getLastOpenedProject()));

		this.finishButton.setEnabled(true);
		this.cancelBtn.setEnabled(false);
	}

	public void disableOptionalTabsAndFinish() {
		this.tabSheet.getTab(this.programMethodsContainer).setEnabled(false);
		this.tabSheet.getTab(this.programLocationsContainer).setEnabled(false);

		this.finishButton.setEnabled(false);
	}

	public void enableFinishBtn() {
		this.finishButton.setEnabled(true);
	}

	public void resetBasicDetails() {
		this.basicDetailsContainer.removeAllComponents();

		this.createProjectPanel = new CreateProjectPanel(this.presenter);
		this.basicDetailsContainer.addComponent(this.createProjectPanel);
	}

	public void resetProgramMembers() {
		this.programMembersContainer.removeAllComponents();

		this.programMembersPanel = new ProjectMembersComponent(this.presenter);
		this.programMembersContainer.addComponent(this.programMembersPanel);
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
}

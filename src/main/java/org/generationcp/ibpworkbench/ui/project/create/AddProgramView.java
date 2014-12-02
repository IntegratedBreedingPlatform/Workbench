package org.generationcp.ibpworkbench.ui.project.create;

import java.util.ArrayList;
import java.util.Collection;

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

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
    private SessionData sessionData;
	
	private VerticalLayout rootLayout;
	
	private TabSheet tabSheet;

	//TABS
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

    public AddProgramView() {	}
    public AddProgramView(int initialTabView) { this.initialTabView = initialTabView; }

	@Override
	public void afterPropertiesSet() throws Exception {
		presenter = new AddProgramPresenter(this);

        assemble();
		updateLabels();	
	}
	
	private void updateLabels(){
	    // currently does nothing
    }

	protected void assemble() {

		initializeComponents();
		initializeLayout();
		initializeActions();

	}
	
	protected void initializeComponents() {
		rootLayout = new VerticalLayout();
		
		tabSheet = generateTabSheet();

		createProjectPanel = new CreateProjectPanel(presenter);
		programMembersPanel = new ProjectMembersComponent(presenter);

        programLocationsContainer = new VerticalLayout();
        programMethodsContainer = new VerticalLayout();
        programMembersContainer = new VerticalLayout();
        basicDetailsContainer = new VerticalLayout();

        programLocationsContainer.setMargin(false);
        programLocationsContainer.setSpacing(false);

        programMethodsContainer.setMargin(false);
        programMethodsContainer.setSpacing(false);

        programMembersContainer.setMargin(false);
        programMembersContainer.setSpacing(false);

        basicDetailsContainer.setMargin(false);
        basicDetailsContainer.setSpacing(false);

        // finish button
        finishButton = new Button("Finish");
        finishButton.setEnabled(false);
        finishButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        finishButton.setDebugId("vaadin_finish_btn");

        // finish button
        cancelBtn = new Button("Cancel");
    }
	
	protected void initializeActions() {
	    finishButton.addListener(new Button.ClickListener() {
       
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                new DashboardMainClickListener(IBPWorkbenchApplication.get().getMainWindow(),presenter.program.getProjectId()).buttonClick(clickEvent);
            }
        });

        cancelBtn.addListener(new HomeAction());
	}

	protected void initializeLayout() {
		
		final Label heading = new Label("Add a Program");
		heading.setStyleName(Bootstrap.Typography.H1.styleName());

        final Label headingDesc = new Label("To provide additional Program configuration, " +
                "click on <em>Members, Locations, and Breeding Method</em> tabs." +
                " Note that <em>Locations and Methods</em> are optional and will be available once you" +
                " complete the <em>Basic Details</em> form by clicking <em>Save</em>." +
                " Click <em>Finish</em> to complete the operation.",Label.CONTENT_XHTML);

		rootLayout.setMargin(new Layout.MarginInfo(false,true,true,true));
		rootLayout.setWidth("100%");
		rootLayout.setSpacing(true);

        basicDetailsContainer.addComponent(createProjectPanel);
        programMembersContainer.addComponent(programMembersPanel);

		tabSheet.addTab(basicDetailsContainer);
		tabSheet.getTab(basicDetailsContainer).setClosable(false);
		tabSheet.getTab(basicDetailsContainer).setCaption("Basic Details");
		
		tabSheet.addTab(programMembersContainer);
		tabSheet.getTab(programMembersContainer).setClosable(false);
		tabSheet.getTab(programMembersContainer).setCaption("Members");

		tabSheet.addTab(programLocationsContainer);
		tabSheet.getTab(programLocationsContainer).setClosable(false);
        tabSheet.getTab(programLocationsContainer).setEnabled(false);
        tabSheet.getTab(programLocationsContainer).setCaption("Locations");
		
		tabSheet.addTab(programMethodsContainer);
		tabSheet.getTab(programMethodsContainer).setClosable(false);
        tabSheet.getTab(programMethodsContainer).setEnabled(false);

        tabSheet.getTab(programMethodsContainer).setCaption("Breeding Methods");
		
		rootLayout.addComponent(heading);
        rootLayout.addComponent(headingDesc);
		rootLayout.addComponent(tabSheet);

        final HorizontalLayout btnContainer = new HorizontalLayout();
        btnContainer.setSpacing(true);
        btnContainer.setSizeUndefined();

        btnContainer.addComponent(cancelBtn);
        btnContainer.addComponent(finishButton);


        rootLayout.addComponent(btnContainer);
        rootLayout.setComponentAlignment(btnContainer,Alignment.MIDDLE_CENTER);

        // set initial tab view
        tabSheet.setSelectedTab(initialTabView);

		setContent(rootLayout);
		setScrollable(true);
	    setSizeFull();
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
            ((WorkbenchMainView)IBPWorkbenchApplication.get().getMainWindow()).addTitle(sessionData.getSelectedProject().getProjectName());
        }

        // initialize program methods and view and set them to the tabs
        programMethodsView = new ProgramMethodsView(project);
        programLocationsView = new ProgramLocationsView(project);

        tabSheet.getTab(programMethodsContainer).setEnabled(true);
        programMethodsContainer.removeAllComponents();
        programMethodsContainer.addComponent(programMethodsView);

        tabSheet.getTab(programLocationsContainer).setEnabled(true);
        programLocationsContainer.removeAllComponents();
        programLocationsContainer.addComponent(programLocationsView);

        // re-initialize program members and basic details (in update mode)
        basicDetailsContainer.removeAllComponents();
        UpdateProjectPanel updateProjectPanel = new UpdateProjectPanel();
        updateProjectPanel.hideDeleteBtn();
        basicDetailsContainer.addComponent(updateProjectPanel);

        programMembersContainer.removeAllComponents();
        programMembersContainer.addComponent(new ProgramMembersPanel(sessionData.getLastOpenedProject()));

        finishButton.setEnabled(true);
        cancelBtn.setEnabled(false);
    }

    public void disableOptionalTabsAndFinish() {
        tabSheet.getTab(programMethodsContainer).setEnabled(false);
        tabSheet.getTab(programLocationsContainer).setEnabled(false);

        finishButton.setEnabled(false);
    }

    public void enableFinishBtn() {
        finishButton.setEnabled(true);
    }

    public void resetBasicDetails() {
        basicDetailsContainer.removeAllComponents();

        createProjectPanel = new CreateProjectPanel(presenter);
        basicDetailsContainer.addComponent(createProjectPanel);
    }

    public void resetProgramMembers() {
        programMembersContainer.removeAllComponents();

        programMembersPanel = new ProjectMembersComponent(presenter);
        programMembersContainer.addComponent(programMembersPanel);
    }


    public Collection<Location> getFavoriteLocations() {

        if (programLocationsView != null) {
            return programLocationsView.getFavoriteLocations();
        }

        return new ArrayList<Location>();
    }

    public Collection<Method> getFavoriteMethods() {
        if (programMethodsView != null) {
            return programMethodsView.getFavoriteMethods();
        }

        return new ArrayList<Method>();
    }
}

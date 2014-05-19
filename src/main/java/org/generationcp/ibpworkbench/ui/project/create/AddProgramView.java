package org.generationcp.ibpworkbench.ui.project.create;

import java.util.List;

import com.vaadin.ui.*;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.ibpworkbench.ui.project.create.UpdateProjectPanel;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.themes.Reindeer;

@Configurable
public class AddProgramView extends Panel implements InitializingBean{
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
    private SessionData sessionData;
	
	private VerticalLayout rootLayout;
	
	private TabSheet tabSheet;
	
	protected Project project;                // the project created
	protected List<Location> newLocations;    // locations added in Locations tab (ProjectLocationsComponent)
	protected List<Method> newMethods;        // methods added in Breeding Methods tab (ProjectBreedingMethodsComponent)
	protected List<User> newUsers;            // users added in Project Members tab (ProjectMembersComponent)
	protected User currentUser;               // should be the currently logged in user that will try to add / update a new project
	
	//TABS
	protected CreateProjectPanel createProjectPanel;
	protected ProjectMembersComponent programMembersPanel;
	protected ProgramLocationsView programLocationsView;
	protected ProgramMethodsView programMethodsView;

    private VerticalLayout programMethodsContainer;
    private VerticalLayout programLocationsContainer;
    private AddProgramPresenter presenter;
    private Button finishButton;

    public AddProgramView() {
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		presenter = new AddProgramPresenter(this);

        assemble();
		updateLabels();	
	}
	
	private void updateLabels(){
		 //messageSource.setValue(lblPageTitle, Message.TITLE_GXE);;
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
		createProjectPanel.setStyleName(Reindeer.PANEL_LIGHT);
		programMembersPanel = new ProjectMembersComponent(tabSheet);
		programMembersPanel.setStyleName(Reindeer.PANEL_LIGHT);
		//programLocationsView = new ProgramLocationsView(sessionData.getLastOpenedProject());
		//programMethodsView = new ProgramMethodsView(sessionData.getLastOpenedProject());

        programLocationsContainer = new VerticalLayout();
        programMethodsContainer = new VerticalLayout();

        programLocationsContainer.setMargin(false);
        programLocationsContainer.setSpacing(false);
        //programLocationsContainer.setSizeFull();

        programMethodsContainer.setMargin(false);
        programMethodsContainer.setSpacing(false);
        //programMethodsContainer.setSizeFull();

        // finish button
        finishButton = new Button("Finish");
        finishButton.setEnabled(false);
    }
	
	protected void initializeActions() {
	    finishButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                presenter.doAddNewProgram();
            }
        });
	}

	protected void initializeLayout() {
		
		final Label heading = new Label("Add a Program");
		heading.setStyleName(Bootstrap.Typography.H1.styleName());
		
		rootLayout.setMargin(new Layout.MarginInfo(false,true,true,true));
		rootLayout.setWidth("100%");
		rootLayout.setSpacing(true);
		
		createProjectPanel.setVisible(true);
		programMembersPanel.setVisible(true);
		programMethodsContainer.setVisible(true);
		programLocationsContainer.setVisible(true);

		tabSheet.addTab(createProjectPanel);
		tabSheet.getTab(createProjectPanel).setClosable(false);
		tabSheet.getTab(createProjectPanel).setCaption("Basic Details");
		
		tabSheet.addTab(programMembersPanel);
		tabSheet.getTab(programMembersPanel).setClosable(false);
		tabSheet.getTab(programMembersPanel).setCaption("Members");
		
		tabSheet.addTab(programLocationsContainer);
		tabSheet.getTab(programLocationsContainer).setClosable(false);
        tabSheet.getTab(programLocationsContainer).setEnabled(false);
        tabSheet.getTab(programLocationsContainer).setCaption("Locations");
		
		tabSheet.addTab(programMethodsContainer);
		tabSheet.getTab(programMethodsContainer).setClosable(false);
        tabSheet.getTab(programMethodsContainer).setEnabled(false);

        tabSheet.getTab(programMethodsContainer).setCaption("Breeding Methods");
		
		rootLayout.addComponent(heading);
		rootLayout.addComponent(tabSheet);

        rootLayout.addComponent(finishButton);
        rootLayout.setComponentAlignment(finishButton,Alignment.MIDDLE_CENTER);

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

    public void enableOptionalTabsAndFinish(CropType selectedCropType) {
        programMethodsView = new ProgramMethodsView(selectedCropType);
        programLocationsView = new ProgramLocationsView(selectedCropType);

        tabSheet.getTab(programMethodsContainer).setEnabled(true);
        programMethodsContainer.removeAllComponents();
        programMethodsContainer.addComponent(programMethodsView);

        tabSheet.getTab(programLocationsContainer).setEnabled(true);
        programLocationsContainer.removeAllComponents();
        programLocationsContainer.addComponent(programLocationsView);

        finishButton.setEnabled(true);
    }

    public void disableOptionalTabsAndFinish() {
        // disable program methods + locations view when crop type is changed;
        tabSheet.getTab(programMethodsContainer).setEnabled(false);
        tabSheet.getTab(programLocationsContainer).setEnabled(false);

        finishButton.setEnabled(false);
    }

    public void enableFinishBtn() {
        finishButton.setEnabled(true);
    }
}

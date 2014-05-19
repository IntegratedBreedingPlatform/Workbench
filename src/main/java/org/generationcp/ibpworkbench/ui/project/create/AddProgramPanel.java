package org.generationcp.ibpworkbench.ui.project.create;

import java.util.List;

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
import org.generationcp.middleware.pojos.workbench.Project;
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
public class AddProgramPanel extends Panel implements InitializingBean{
	
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
	private CreateProjectPanel createProjectPanel;
	private ProjectMembersComponent programMembersPanel;
	private ProgramLocationsView programLocationsView;
	private ProgramMethodsView programMethodsView;

	public AddProgramPanel() {
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
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
		
		createProjectPanel = new CreateProjectPanel(tabSheet);
		createProjectPanel.setStyleName(Reindeer.PANEL_LIGHT);
		programMembersPanel = new ProjectMembersComponent(tabSheet);
		programMembersPanel.setStyleName(Reindeer.PANEL_LIGHT);
		programLocationsView = new ProgramLocationsView(sessionData.getLastOpenedProject());
		programMethodsView = new ProgramMethodsView(sessionData.getLastOpenedProject());
	}
	
	protected void initializeActions() {
		
		
	}

	protected void initializeLayout() {
		
		final Label heading = new Label("Add a Program");
		heading.setStyleName(Bootstrap.Typography.H1.styleName());
		
		rootLayout.setMargin(new Layout.MarginInfo(false,true,true,true));
		rootLayout.setWidth("100%");
		rootLayout.setSpacing(true);
		
		createProjectPanel.setVisible(true);
		programMembersPanel.setVisible(true);
		programLocationsView.setVisible(true);
		programMethodsView.setVisible(true);
		
		
		tabSheet.addTab(createProjectPanel);
		tabSheet.getTab(createProjectPanel).setClosable(false);
		tabSheet.getTab(createProjectPanel).setCaption("Basic Details");
		
		tabSheet.addTab(programMembersPanel);
		tabSheet.getTab(programMembersPanel).setClosable(false);
		tabSheet.getTab(programMembersPanel).setCaption("Members");
		
		tabSheet.addTab(programLocationsView);
		tabSheet.getTab(programLocationsView).setClosable(false);
		tabSheet.getTab(programLocationsView).setCaption("Locations");
		
		tabSheet.addTab(programMethodsView);
		tabSheet.getTab(programMethodsView).setClosable(false);
		tabSheet.getTab(programMethodsView).setCaption("Breeding Methods");
		
		rootLayout.addComponent(heading);
		rootLayout.addComponent(tabSheet);
		
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

}

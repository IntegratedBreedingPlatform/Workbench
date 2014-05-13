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
public class ProgramAdministrationPanel extends Panel implements InitializingBean{
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	@Autowired
    private SessionData sessionData;
	
	private VerticalLayout rootLayout;
	
	private TabSheet tabSheet;
	
	//TABS
	private UpdateProjectPanel updateProjectPanel;
	private ProgramMembersPanel programMembersPanel;
	private ProgramLocationsView programLocationsView;
	private ProgramMethodsView programMethodsView;

	public ProgramAdministrationPanel() {
		
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
		
		updateProjectPanel = new UpdateProjectPanel();
		updateProjectPanel.setStyleName(Reindeer.PANEL_LIGHT);
		programMembersPanel = new ProgramMembersPanel(sessionData.getLastOpenedProject());
		programMembersPanel.setStyleName(Reindeer.PANEL_LIGHT);
		programLocationsView = new ProgramLocationsView(sessionData.getLastOpenedProject());
		programMethodsView = new ProgramMethodsView(sessionData.getLastOpenedProject());
	}
	
	protected void initializeActions() {
		
		
	}

	protected void initializeLayout() {
		
		final Label heading = new Label("Manage Program Settings");
        final Label headingDesc = new Label("Modify the details of this program, and manage the members, locations, and methods associated with it using the tabs below.");
        heading.setStyleName(Bootstrap.Typography.H3.styleName());
		
		rootLayout.setMargin(new Layout.MarginInfo(true,true,true,true));
		rootLayout.setWidth("100%");
		rootLayout.setSpacing(true);
		
		updateProjectPanel.setVisible(true);
		programMembersPanel.setVisible(true);
		programLocationsView.setVisible(true);
		programMethodsView.setVisible(true);
		
		
		tabSheet.addTab(updateProjectPanel);
		tabSheet.getTab(updateProjectPanel).setClosable(false);
		tabSheet.getTab(updateProjectPanel).setCaption("Basic Details");
		
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
		rootLayout.addComponent(headingDesc);
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

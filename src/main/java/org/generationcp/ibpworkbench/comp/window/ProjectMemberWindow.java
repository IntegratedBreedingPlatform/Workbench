/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.comp.window;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CreateContactAction;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.actions.OpenToolVersionsAction;
import org.generationcp.ibpworkbench.actions.SignoutAction;
import org.generationcp.ibpworkbench.comp.ProjectMembersComponentPanel;
import org.generationcp.ibpworkbench.comp.project.create.CreateProjectPanel;
import org.generationcp.ibpworkbench.navigation.CrumbTrail;
import org.generationcp.ibpworkbench.navigation.NavUriFragmentChangedListener;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class ProjectMemberWindow extends Window implements IContentWindow, InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    private static final String VERSION = "1.1.3.10";
    
    private Label workbenchTitle;
    private Button saveButton;
   

    private Label actionsTitle;
    private Button createProjectButton;

    private Button createContactButton;
    private Label recentTitle;
    private Label usersGuideTitle;
    private Label hint1;

    private ProjectMembersComponentPanel mainContent;
    private CrumbTrail crumbTrail;
    private Project project;
    private UriFragmentUtility uriFragUtil;
    private NavUriFragmentChangedListener uriChangeListener;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public ProjectMemberWindow(Project project) {
    	this.project = project;
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    protected void initializeComponents() throws Exception {
        // workbench header components
        workbenchTitle = new Label();
        workbenchTitle.setStyleName("gcp-window-title");

        saveButton = new Button("Save");
        saveButton.setStyleName(BaseTheme.BUTTON_LINK);
        saveButton.setSizeUndefined();

       

        recentTitle = new Label();
        recentTitle.setStyleName("gcp-section-title");
        recentTitle.setSizeUndefined();

        usersGuideTitle = new Label();
        usersGuideTitle.setStyleName("gcp-section-title");
        usersGuideTitle.setSizeUndefined();

        hint1 = new Label();
        hint1.setContentMode(Label.CONTENT_PREFORMATTED);
        hint1.setSizeUndefined();

       
        mainContent = new ProjectMembersComponentPanel(this.project);
        crumbTrail = new CrumbTrail();
        crumbTrail.setMargin(true, true, false, true);
        crumbTrail.setSpacing(false);
        crumbTrail.setSizeUndefined();

        uriFragUtil = new UriFragmentUtility();
        uriChangeListener = new NavUriFragmentChangedListener();

        uriFragUtil.addListener(uriChangeListener);
    }

    protected void initializeLayout() {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        // add the vertical split panel
       

        mainContent.setMargin(false);
        mainContent.setSpacing(false);
        
     
        // layout the right area of the content area split panel
        // contentAreaSplitPanel.addComponent(workbenchDashboard);
       
        setContent(mainContent);
    }

    protected void initializeActions() {
    	saveButton.addListener(new HomeAction());
       
    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }

    private Component layoutWorkbenchHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidth("100%");
        headerLayout.setHeight("100%");
        headerLayout.setMargin(false, false, false, false);
        headerLayout.setSpacing(false);
        
        Embedded ibpLogo = new Embedded(null, new ThemeResource("../gcp-default/images/ibp_logo.jpg"));
        ibpLogo.setWidth("50px");
        ibpLogo.setHeight("50px");
        headerLayout.addComponent(ibpLogo);
        headerLayout.setExpandRatio(ibpLogo, 0.0f);

        // workbench title area
        headerLayout.addComponent(workbenchTitle);
        headerLayout.setComponentAlignment(workbenchTitle, Alignment.MIDDLE_LEFT);
        headerLayout.setExpandRatio(workbenchTitle, 1.0f);

        headerLayout.addComponent(uriFragUtil);

        // right side button area
        HorizontalLayout headerRightLayout = new HorizontalLayout();
        headerRightLayout.setSizeUndefined();
        headerRightLayout.setMargin(false);
        headerRightLayout.setSpacing(true);

        headerRightLayout.addComponent(saveButton);
        headerRightLayout.setComponentAlignment(saveButton, Alignment.TOP_LEFT);

        

        headerLayout.addComponent(headerRightLayout);
        headerLayout.setComponentAlignment(headerRightLayout, Alignment.MIDDLE_RIGHT);
        headerLayout.setExpandRatio(headerRightLayout, 0.0f);

        return headerLayout;
    }

    private Component layoutLeftArea() {
        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setWidth("100%");
        leftLayout.setHeight(null);
        leftLayout.setSpacing(true);

        leftLayout.addComponent(actionsTitle);
        leftLayout.setComponentAlignment(actionsTitle, Alignment.TOP_CENTER);

        leftLayout.addComponent(createProjectButton);
        leftLayout.setComponentAlignment(createProjectButton, Alignment.TOP_CENTER);

        // TODO: These are commented out to remove non-working elements for June
        // milestone
        // leftLayout.addComponent(createContactButton);
        // leftLayout.setComponentAlignment(createContactButton,
        // Alignment.TOP_CENTER);
        //
        // leftLayout.addComponent(recentTitle);
        // leftLayout.setComponentAlignment(recentTitle, Alignment.TOP_CENTER);

        leftLayout.addComponent(usersGuideTitle);
        leftLayout.setComponentAlignment(usersGuideTitle, Alignment.TOP_CENTER);
        
        //TODO update the contents of the hint to apply to the new workflow dashboard screen
        //leftLayout.addComponent(hint1);
        
        return leftLayout;
    }

    /**
     * Show the specified {@link Component} on the right side area of the
     * Workbench's split panel.
     * 
     * @param content
     */
    public void showContent(Component content) {

        // contentAreaSplitPanel.removeComponent(contentAreaSplitPanel.getSecondComponent());
        // contentAreaSplitPanel.addComponent(content);

        mainContent.removeComponent(mainContent.getComponent(1));
        mainContent.addComponent(content);
    }

   
    public CrumbTrail getCrumbTrail() {
        return crumbTrail;
    }

    public void setCrumbTrail(CrumbTrail crumbTrail) {
        this.crumbTrail = crumbTrail;
    }

    public void setUriFragment(String fragment) {
        uriFragUtil.setFragment(fragment);
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    
    public void addTitle(String myTitle)
    {
    	 String title = messageSource.getMessage(Message.WORKBENCH_TITLE) + " " + VERSION+ " " + myTitle;
         workbenchTitle.setValue(title);
    }
    @Override
    public void updateLabels() {
        String title = messageSource.getMessage(Message.WORKBENCH_TITLE) + " " + VERSION;
        workbenchTitle.setValue(title);
        
        messageSource.setCaption(actionsTitle, Message.ACTIONS);
        messageSource.setCaption(createProjectButton, Message.PROJECT_CREATE);
        messageSource.setCaption(createContactButton, Message.CONTACT_CREATE);
        
        messageSource.setValue(recentTitle, Message.RECENT);
        messageSource.setValue(usersGuideTitle, Message.USER_GUIDE);
        
        messageSource.setValue(hint1, Message.USER_GUIDE_1);
    }
}

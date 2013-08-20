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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CreateContactAction;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.actions.OpenToolVersionsAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction.WindowEnum;
import org.generationcp.ibpworkbench.actions.SignoutAction;
import org.generationcp.ibpworkbench.comp.WorkbenchDashboard;
import org.generationcp.ibpworkbench.navigation.CrumbTrail;
import org.generationcp.ibpworkbench.navigation.NavUriFragmentChangedListener;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable

public class WorkbenchDashboardWindow extends Window implements IContentWindow, InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;
    
    public static final String VERSION = "1.1.4.20";
    public static final String HELP_LINK = "https://www.integratedbreeding.net/manuals-and-tutorials-ib-tools";
    private Label workbenchTitle;
    private Button homeButton;
    private Button signOutButton;
    private Button accountButton;
    private Button toolVersionsButton;
    private Button helpButton;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    private Label actionsTitle;
    private Button createProjectButton;
    private Button deleteProjectButton;

    private Button createContactButton;
    private Label recentTitle;
    private Label usersGuideTitle;
    private Label hint1;
    private VerticalSplitPanel verticalSplitPanel;

    private HorizontalSplitPanel contentAreaSplitPanel;

    private VerticalLayout mainContent;

    private WorkbenchDashboard workbenchDashboard;

    private CrumbTrail crumbTrail;

    private UriFragmentUtility uriFragUtil;
    private NavUriFragmentChangedListener uriChangeListener;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private WorkbenchDashboardWindow thisInstance;

	//private Button userToolsButton;

    public WorkbenchDashboardWindow() {
    	super("Integrated Breeding Platform | Workbench");
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

        homeButton = new Button();
        homeButton.setStyleName(BaseTheme.BUTTON_LINK);
        homeButton.setSizeUndefined();

        signOutButton = new Button();
        signOutButton.setStyleName(BaseTheme.BUTTON_LINK);
        signOutButton.setSizeUndefined();

        accountButton = new Button();
        accountButton.setStyleName(BaseTheme.BUTTON_LINK);
        accountButton.setSizeUndefined();

        //userToolsButton = new Button();
        //userToolsButton.setStyleName(BaseTheme.BUTTON_LINK);
        //userToolsButton.setSizeUndefined();
        
        toolVersionsButton = new Button();
        toolVersionsButton.setStyleName(BaseTheme.BUTTON_LINK);
        toolVersionsButton.setSizeUndefined();
        
        helpButton = new Button();
        helpButton.setStyleName(BaseTheme.BUTTON_LINK);
        helpButton.setSizeUndefined();

        // left area components
        actionsTitle = new Label();
        actionsTitle.setStyleName("gcp-section-title");
        actionsTitle.setSizeUndefined();
        
        
      

        createProjectButton = new Button("Create Project");
        createProjectButton.setStyleName(Reindeer.BUTTON_LINK + " gcp-createproject-btn");
        createProjectButton.setWidth("100%");
        createContactButton = new Button("Create Contact");
        createContactButton.setWidth("120px");

        recentTitle = new Label();
        recentTitle.setStyleName("gcp-section-title");
        recentTitle.setSizeUndefined();

        usersGuideTitle = new Label();
        usersGuideTitle.setStyleName("gcp-section-title");
        usersGuideTitle.setSizeUndefined();

        hint1 = new Label();
        hint1.setContentMode(Label.CONTENT_PREFORMATTED);
        hint1.setSizeUndefined();

        workbenchDashboard = new WorkbenchDashboard();

        verticalSplitPanel = new VerticalSplitPanel();
        contentAreaSplitPanel = new HorizontalSplitPanel();

        mainContent = new VerticalLayout();
        mainContent.setStyleName("gcp-maincontentarea");
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
        verticalSplitPanel.setSplitPosition(87, Sizeable.UNITS_PIXELS);
        verticalSplitPanel.setLocked(true);
        verticalSplitPanel.setStyleName("gcp-workbench-vsplit-panel");
        verticalSplitPanel.setSizeFull();

        
        
        layout.addComponent(verticalSplitPanel);

        // add the workbench header
        Component workbenchHeader = layoutWorkbenchHeader();
        verticalSplitPanel.addComponent(workbenchHeader);

        // add the content area split panel
        contentAreaSplitPanel.setSplitPosition(87, Sizeable.UNITS_PIXELS);
        contentAreaSplitPanel.setStyleName("gcp-workbench-content-split-panel");
        contentAreaSplitPanel.setLocked(true);

        // layout the left area of the content area split panel
        Component leftArea = layoutLeftArea();
        contentAreaSplitPanel.addComponent(leftArea);

        mainContent.setMargin(false);
        mainContent.setSpacing(false);
        mainContent.addComponent(crumbTrail);
        mainContent.addComponent(workbenchDashboard);

        // layout the right area of the content area split panel
        // contentAreaSplitPanel.addComponent(workbenchDashboard);
        contentAreaSplitPanel.addComponent(mainContent);

        verticalSplitPanel.addComponent(contentAreaSplitPanel);

        setContent(layout);
    }

    protected void initializeActions() {
        final Window thisWindow = this;
        
    	homeButton.addListener(new HomeAction());
        signOutButton.addListener(new SignoutAction());
        
        helpButton.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				WorkbenchDashboardWindow.this.open(new ExternalResource(HELP_LINK),"_blank");
			}
		});
        
        Button.ClickListener userToolsClickListener = new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
			public void buttonClick(ClickEvent event) {
				thisWindow.addWindow(new UserToolsManagerWindow());
			}
		};
        
        //userToolsButton.addListener (userToolsClickListener);
        
        toolVersionsButton.addListener(new OpenToolVersionsAction());
       
        
        
        createProjectButton.addListener(new OpenNewProjectAction());
        createProjectButton.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                thisInstance.addTitle(messageSource.getMessage(Message.PROJECT_CREATE_TITLE));
            }
        });
        
        createContactButton.addListener(new CreateContactAction());
    }

    protected void assemble() throws Exception {
        thisInstance = this;

        initializeComponents();
        initializeLayout();
        initializeActions();
        onLoadOperations();
    }

    protected void onLoadOperations() {
        User user = IBPWorkbenchApplication.get().getSessionData().getUserData();
        String username = user.getName();

        if (username == null) {
            return;
        }

        try {
            UserInfo userInfo = workbenchDataManager.getUserInfo(user.getUserid());
            if (userInfo == null || userInfo.getLoginCount() < 1) {
	            	if (user.getName().equals((user.getPassword())) && userInfo != null){
		            	OpenWindowAction ow = new OpenWindowAction(WindowEnum.CHANGE_PASSWORD);
		                ow.launchWindow(this, "change_password");
	            	}
	            	
	            	if (userInfo == null) {
	                    userInfo = new UserInfo();
	                }
	                userInfo.setUserId(user.getUserid());
	                userInfo.setLoginCount(1);
	                workbenchDataManager.insertOrUpdateUserInfo(userInfo);

            }
            workbenchDataManager.incrementUserLogInCount(userInfo.getUserId());
        }
        catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }
    }
    	
    private Component layoutWorkbenchHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidth("100%");
        headerLayout.setHeight("100%");
        headerLayout.setMargin(false, false, false, false);
        headerLayout.setSpacing(false);
        
        Embedded ibpLogo = new Embedded(null, new ThemeResource("../gcp-default/images/ibp_logo2.jpg"));
        ibpLogo.setWidth("87px");
        ibpLogo.setHeight("87px");
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

        headerRightLayout.addComponent(homeButton);
        headerRightLayout.setComponentAlignment(homeButton, Alignment.TOP_LEFT);

        headerRightLayout.addComponent(new Label("|"));

        headerRightLayout.addComponent(signOutButton);
        headerRightLayout.setComponentAlignment(signOutButton, Alignment.TOP_LEFT);

        headerRightLayout.addComponent(new Label("|"));

        //headerRightLayout.addComponent(accountButton);
        //headerRightLayout.setComponentAlignment(accountButton, Alignment.TOP_LEFT);

        //headerRightLayout.addComponent(new Label("|"));

        //headerRightLayout.addComponent(userToolsButton);
        //headerRightLayout.setComponentAlignment(userToolsButton, Alignment.TOP_LEFT);

        
        //headerRightLayout.addComponent(new Label("|"));

        headerRightLayout.addComponent(toolVersionsButton);
        headerRightLayout.setComponentAlignment(toolVersionsButton, Alignment.TOP_LEFT);
        
        headerRightLayout.addComponent(new Label("|"));

        headerRightLayout.addComponent(helpButton);
        headerRightLayout.setComponentAlignment(helpButton, Alignment.TOP_LEFT);

        headerLayout.addComponent(headerRightLayout);
        headerLayout.setComponentAlignment(headerRightLayout, Alignment.MIDDLE_RIGHT);
        headerLayout.setExpandRatio(headerRightLayout, 0.0f);

        return headerLayout;
    }

    private Component layoutLeftArea() {
        VerticalLayout leftLayout = new VerticalLayout();
        //leftLayout.setSpacing(true);

        //leftLayout.addComponent(actionsTitle);
        //leftLayout.setComponentAlignment(actionsTitle, Alignment.TOP_CENTER);
        //leftLayout.setSize;
        
        leftLayout.addComponent(createProjectButton);
        
        leftLayout.setExpandRatio(createProjectButton,1.0F);
        //leftLayout.setComponentAlignment(createProjectButton, Alignment.TOP_CENTER);

        // TODO: These are commented out to remove non-working elements for June
        // milestone
        // leftLayout.addComponent(createContactButton);
        // leftLayout.setComponentAlignment(createContactButton,
        // Alignment.TOP_CENTER);
        //
        // leftLayout.addComponent(recentTitle);
        // leftLayout.setComponentAlignment(recentTitle, Alignment.TOP_CENTER);

        //leftLayout.addComponent(usersGuideTitle);
        //leftLayout.setComponentAlignment(usersGuideTitle, Alignment.TOP_CENTER);
        
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

    public WorkbenchDashboard getWorkbenchDashboard() {
        return workbenchDashboard;
    }

    public void setWorkbenchDashboard(WorkbenchDashboard workbenchDashboard) {
        this.workbenchDashboard = workbenchDashboard;
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
    	
    	if (myTitle != null && !myTitle.isEmpty()) {
    		myTitle = ": <h1>" + myTitle +"</h1>";
    	} else {
    		myTitle = "";
    	}
    	
    	
    	 String title = "<h1>"+messageSource.getMessage(Message.WORKBENCH_TITLE) + "</h1> <h2>" + VERSION+ "</h2> " + myTitle;
         workbenchTitle.setValue(title);
         workbenchTitle.setContentMode(Label.CONTENT_XHTML);

    }
    @Override
    public void updateLabels() {
        String title =  "<h1>"+messageSource.getMessage(Message.WORKBENCH_TITLE) + "</h1> <h2>" + VERSION + "</h2>";
        workbenchTitle.setValue(title);
        workbenchTitle.setContentMode(Label.CONTENT_XHTML);

        messageSource.setCaption(homeButton, Message.HOME);
        messageSource.setCaption(signOutButton, Message.SIGNOUT);
        messageSource.setCaption(accountButton, Message.ACCOUNT);
        messageSource.setCaption(toolVersionsButton, Message.TOOL_VERSIONS);
       //messageSource.setCaption(userToolsButton,Message.TOOL_USERS);
        messageSource.setCaption(helpButton, Message.HELP);
        
        messageSource.setCaption(actionsTitle, Message.ACTIONS);
        
        messageSource.setCaption(createProjectButton, Message.PROJECT_CREATE);
        messageSource.setCaption(createContactButton, Message.CONTACT_CREATE);
        
        messageSource.setValue(recentTitle, Message.RECENT);
        messageSource.setValue(usersGuideTitle, Message.USER_GUIDE);
        
        messageSource.setValue(hint1, Message.USER_GUIDE_1);
    }
    
    public void toggleCreateButtonStyle() {
    	if (!createProjectButton.getStyleName().contains("gcp-enabled")) {
    		createProjectButton.setStyleName(Reindeer.BUTTON_LINK + " gcp-createproject-btn gcp-enabled");
    	} else {
    		createProjectButton.setStyleName(Reindeer.BUTTON_LINK + " gcp-createproject-btn");
    	}
    }
}

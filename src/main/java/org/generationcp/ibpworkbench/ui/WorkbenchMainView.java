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

package org.generationcp.ibpworkbench.ui;

import java.util.Properties;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.IWorkbenchSession;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenToolVersionsAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction.WindowEnum;
import org.generationcp.ibpworkbench.actions.SignoutAction;
import org.generationcp.ibpworkbench.navigation.CrumbTrail;
import org.generationcp.ibpworkbench.navigation.NavUriFragmentChangedListener;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.project.create.CreateProjectPanel;
import org.generationcp.ibpworkbench.ui.project.create.UpdateProjectPanel;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.ui.window.HelpWindow;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.ui.window.UserToolsManagerWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable

public class WorkbenchMainView extends Window implements IContentWindow, InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;

    public static final String HELP_LINK = "https://www.integratedbreeding.net/manuals-and-tutorials-ib-tools";
    private Label workbenchTitle;
    private Button homeButton;
    private Button signOutButton;
    private Button accountButton;
    private Button toolVersionsButton;
    private Button helpButton;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;
    
    @Autowired
    @Qualifier("workbenchProperties")
    private Properties workbenchProperties;

    private Label actionsTitle;
    //private Button createProjectButton;

    //private Button createContactButton;
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

    private WorkbenchSidebar sidebar;
    private Button collapseButton;
    //private Label loginUserLbl;

    //private Button userToolsButton;

    public WorkbenchMainView() {
        super("Breeding Management System | Workbench");
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //this.sessionProvider.setSessionData(sessionData);

        assemble();
    }

    protected void initializeComponents() throws Exception {
        // workbench header components

        workbenchTitle = new Label();
        workbenchTitle.setStyleName("gcp-window-title");

        addTitle("");

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

        //createProjectButton = new Button("Create Project");
        //createProjectButton.setStyleName(Reindeer.BUTTON_LINK + " gcp-createproject-btn");
        //createProjectButton.setWidth("100%");
        //createContactButton = new Button("Create Contact");
        //createContactButton.setWidth("120px");

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

        collapseButton = new Button("<span class='glyphicon icon-list'></span>");
        collapseButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + " collapse-btn");
        collapseButton.setHtmlContentAllowed(true);
        collapseButton.setDescription(messageSource.getMessage("TOGGLE_SIDEBAR"));

        crumbTrail = new CrumbTrail();
        //crumbTrail.setMargin(true, true, true, true);
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
        contentAreaSplitPanel.setSplitPosition(5, Sizeable.UNITS_PIXELS);
        contentAreaSplitPanel.addStyleName(Reindeer.SPLITPANEL_SMALL);
        contentAreaSplitPanel.addStyleName("gcp-workbench-content-split-panel");


        sidebar = new WorkbenchSidebar();
        contentAreaSplitPanel.addComponent(sidebar);
        // layout the left area of the content area split panel
        //contentAreaSplitPanel.addComponent(new WorkbenchSidebar(null,null));

        mainContent.setSizeFull();
        mainContent.setMargin(false);
        mainContent.setSpacing(false);

        // Breadcrumb Tinapay
        final HorizontalLayout crumbTrailContainer = new HorizontalLayout();
        crumbTrailContainer.setStyleName("gcp-crumbtrail");
        crumbTrailContainer.setWidth("100%");
        crumbTrailContainer.setHeight("28px");

        collapseButton.setHeight("100%");
        crumbTrailContainer.addComponent(collapseButton);
        //crumbTrailContainer.setComponentAlignment(collapseButton,Alignment.MIDDLE_CENTER);
        crumbTrailContainer.addComponent(crumbTrail);
        crumbTrailContainer.setExpandRatio(crumbTrail,1.0F);
        crumbTrailContainer.setComponentAlignment(crumbTrail,Alignment.MIDDLE_LEFT);


        mainContent.addComponent(crumbTrailContainer);

        this.showContent(workbenchDashboard);

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
                //WorkbenchMainView.this.open(new ExternalResource(HELP_LINK),"_blank");
                thisWindow.addWindow(new HelpWindow());
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

        this.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent closeEvent) {
                IBPWorkbenchApplication.get().toggleJira();
            }
        });

        collapseButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent clickEvent) {
                if ( contentAreaSplitPanel.getSplitPosition() > 0)
                    contentAreaSplitPanel.setSplitPosition(0);
                else
                    contentAreaSplitPanel.setSplitPosition(360);
            }
        });

        /*
        createProjectButton.addListener(new OpenNewProjectAction());
        createProjectButton.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                WorkbenchMainView.this.addTitle(messageSource.getMessage(Message.PROJECT_CREATE_TITLE));
            }
        });

        createContactButton.addListener(new CreateContactAction());
        */

    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
        onLoadOperations();
    }

    protected void onLoadOperations() {
        User user = sessionData.getUserData();
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


        if (sessionData.getLastOpenedProject() != null)
            workbenchDashboard.initializeDashboardContents().doAction(sessionData.getLastOpenedProject().getProjectId(),this);

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
        VerticalLayout headerRightContainer = new VerticalLayout();
        headerRightContainer.setSizeUndefined();
        headerLayout.setHeight("100%");
        headerLayout.setMargin(false);
        headerRightContainer.setSpacing(true);
        headerRightContainer.addStyleName("main-header-right-container");

        //loginUserLbl = new Label();

        HorizontalLayout headerRightLinks = new HorizontalLayout();
        headerRightLinks.setSizeUndefined();
        headerRightLinks.setMargin(false);
        headerRightLinks.setSpacing(true);

        headerRightLinks.addComponent(homeButton);
        headerRightLinks.setComponentAlignment(homeButton, Alignment.TOP_LEFT);

        headerRightLinks.addComponent(new Label("|"));

        //headerRightLinks.addComponent(accountButton);
        //headerRightLinks.setComponentAlignment(accountButton, Alignment.TOP_LEFT);

        //headerRightLinks.addComponent(new Label("|"));

        //headerRightLinks.addComponent(userToolsButton);
        //headerRightLinks.setComponentAlignment(userToolsButton, Alignment.TOP_LEFT);


        //headerRightLinks.addComponent(new Label("|"));

        headerRightLinks.addComponent(toolVersionsButton);
        headerRightLinks.setComponentAlignment(toolVersionsButton, Alignment.TOP_LEFT);

        headerRightLinks.addComponent(new Label("|"));

        headerRightLinks.addComponent(helpButton);
        headerRightLinks.setComponentAlignment(helpButton, Alignment.TOP_LEFT);

        headerRightLinks.addComponent(new Label("|"));

        headerRightLinks.addComponent(signOutButton);
        headerRightLinks.setComponentAlignment(signOutButton, Alignment.TOP_LEFT);

        headerRightContainer.addComponent(headerRightLinks);
        //headerRightContainer.addComponent(loginUserLbl);

        headerLayout.addComponent(headerRightContainer);
        headerLayout.setComponentAlignment(headerRightContainer, Alignment.MIDDLE_RIGHT);
        headerLayout.setExpandRatio(headerRightContainer, 0.0f);

        return headerLayout;
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

        if (mainContent.getComponentCount() > 1)
            mainContent.removeComponent(mainContent.getComponent(1));

        if (content instanceof Embedded) {
            //mainContent.setSizeFull();
            mainContent.addComponent(content);
            mainContent.setExpandRatio(content,1.0F);

        } else {

            if (content instanceof Panel) {
                content.setStyleName(Reindeer.PANEL_LIGHT);
                mainContent.addComponent(content);
                mainContent.setExpandRatio(content,1.0F);
            } else {
                final Panel wrap = new Panel();
                wrap.setStyleName(Reindeer.PANEL_LIGHT);
                wrap.setSizeFull();
                wrap.setScrollable(true);

                if (content instanceof  ComponentContainer)
                    wrap.setContent((ComponentContainer) content);
                else {
                    VerticalLayout vl = new VerticalLayout();
                    vl.addComponent(content);
                    vl.setSizeUndefined();
                    wrap.setContent(vl);
                }

                /*

                final VerticalLayout wrap = new VerticalLayout();
                wrap.setSizeFull();
                wrap.addStyleName("gcp-dashboard-main");
                wrap.addComponent(workbenchDashboard);

                */

                mainContent.addComponent(wrap);
                mainContent.setExpandRatio(wrap,1.0F);
            }
        }

        if (content instanceof UpdateProjectPanel || !(content instanceof WorkbenchDashboard || content instanceof  CreateProjectPanel))
            contentAreaSplitPanel.setSplitPosition(360,Sizeable.UNITS_PIXELS);
        else
            contentAreaSplitPanel.setSplitPosition(0,Sizeable.UNITS_PIXELS);
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
            myTitle = "<h3>" + myTitle +"</h3>";
        } else {
            myTitle = "";
        }


        String version = workbenchProperties.getProperty("workbench.version", "");
        String title = "<h1>"+messageSource.getMessage(Message.WORKBENCH_TITLE) + "</h1> <h2>" + version + "</h2> " + myTitle;
        workbenchTitle.setValue(title);
        workbenchTitle.setContentMode(Label.CONTENT_XHTML);

    }

    @Override
    public void updateLabels() {
        IWorkbenchSession appSession = (IWorkbenchSession) this.getApplication();

        //String title =  "<h1>"+messageSource.getMessage(Message.WORKBENCH_TITLE) + "</h1> <h2>" + VERSION + "</h2>";
        //workbenchTitle.setValue(title);
        //workbenchTitle.setContentMode(Label.CONTENT_XHTML);

        messageSource.setCaption(homeButton, Message.HOME);
        //messageSource.setCaption(signOutButton, Message.SIGNOUT);

        String signoutName = appSession.getSessionData().getUserData().getPerson().getFirstName();
        if (signoutName.length() > 10)
            signoutName = signoutName.substring(0,9) + "...";

        signOutButton.setCaption(messageSource.getMessage(Message.SIGNOUT) + " (" + signoutName + ")");
        signOutButton.setDescription(messageSource.getMessage(Message.LOGGED_IN) + " " + appSession.getSessionData().getUserData().getPerson().getFirstName() + " " + appSession.getSessionData().getUserData().getPerson().getLastName());

        messageSource.setCaption(accountButton, Message.ACCOUNT);
        messageSource.setCaption(toolVersionsButton, Message.TOOL_VERSIONS);
        //messageSource.setCaption(userToolsButton,Message.TOOL_USERS);
        messageSource.setCaption(helpButton, Message.HELP);

        messageSource.setCaption(actionsTitle, Message.ACTIONS);

        //messageSource.setCaption(createProjectButton, Message.PROJECT_CREATE);
        //messageSource.setCaption(createContactButton, Message.CONTACT_CREATE);

        messageSource.setValue(recentTitle, Message.RECENT);
        messageSource.setValue(usersGuideTitle, Message.USER_GUIDE);

        messageSource.setValue(hint1, Message.USER_GUIDE_1);

        //loginUserLbl.setValue(messageSource.getMessage(Message.LOGGED_IN) + " " + appSession.getSessionData().getUserData().getPerson().getFirstName() + " " + appSession.getSessionData().getUserData().getPerson().getLastName() );
    }

    public WorkbenchSidebar getSidebar() {
        return sidebar;
    }


}
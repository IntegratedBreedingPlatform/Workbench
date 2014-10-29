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

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.IWorkbenchSession;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction.WindowEnum;
import org.generationcp.ibpworkbench.actions.SignoutAction;
import org.generationcp.ibpworkbench.navigation.NavUriFragmentChangedListener;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.project.create.AddProgramView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.ui.window.HelpWindow;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.hene.popupbutton.PopupButton;

import java.util.Properties;

@Configurable

public class WorkbenchMainView extends Window implements IContentWindow, InitializingBean, InternationalizableComponent {
    private static final long serialVersionUID = 1L;

    public static final String HELP_LINK = "https://www.integratedbreeding.net/manuals-and-tutorials-ib-tools";
    private Label workbenchTitle;
    private Button homeButton;
    private PopupButton memberButton;
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

    private HorizontalSplitPanel root;

    private VerticalLayout mainContent;

    private WorkbenchDashboard workbenchDashboard;

    private UriFragmentUtility uriFragUtil;
    private NavUriFragmentChangedListener uriChangeListener;

    private WorkbenchSidebar sidebar;
    private Button collapseButton;
    private Button signoutButton;
    private Button logoBtn;

    public WorkbenchMainView() {
        super("Breeding Management System | Workbench");
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();

        // initialize other operations on load (
        workbenchDashboard = new WorkbenchDashboard();
        onLoadOperations();

        // show dashboard
        this.showContent(workbenchDashboard);
    }

    protected void initializeComponents() throws Exception {
        // workbench header components
        initializeHeaderComponents();

        // sidebar
        sidebar = new WorkbenchSidebar();


        // left area components
        actionsTitle = new Label();
        actionsTitle.setStyleName("gcp-section-title");
        actionsTitle.setSizeUndefined();



        collapseButton = new Button(
                "<span class='bms-header-btn'><span class='bms-fa-chevron-right ico'/></span>");
        collapseButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + " header-btn");
        collapseButton.setHtmlContentAllowed(true);
        collapseButton.setDescription(messageSource.getMessage("TOGGLE_SIDEBAR"));

        uriFragUtil = new UriFragmentUtility();
        uriChangeListener = new NavUriFragmentChangedListener();

        uriFragUtil.addListener(uriChangeListener);
    }

    private void initializeHeaderComponents() {

        logoBtn = new Button();

        workbenchTitle = new Label();
        workbenchTitle.setStyleName("gcp-window-title");
        workbenchTitle.setContentMode(Label.CONTENT_XHTML);

        homeButton = new Button("<span class='bms-header-btn'><span>My Programs</span></span>");
        homeButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + " header-btn");
        homeButton.setHtmlContentAllowed(true);
        homeButton.setSizeUndefined();

        memberButton = new PopupButton();
        memberButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + " bms-header-popuplink");
        memberButton.setHtmlContentAllowed(true);
        memberButton.setSizeUndefined();

        final VerticalLayout memberPopup = new VerticalLayout();
        memberPopup.setStyleName("bms-memberpopup");
        memberPopup.setSizeUndefined();

        signoutButton = new Button(messageSource.getMessage(Message.SIGNOUT));
        signoutButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        signoutButton.setSizeFull();
        signoutButton.addListener(new SignoutAction());

        Person member = sessionData.getUserData().getPerson();
        memberPopup.addComponent(new Label(String.format("<h2>%s %s</h2><h4>%s</h4>",member.getFirstName(),member.getLastName(),member.getEmail()),Label.CONTENT_XHTML));
        memberPopup.addComponent(signoutButton);

        memberButton.addComponent(memberPopup);


        helpButton = new Button("<span class='bms-header-btn2'><span class='bms-fa-question-circle ico'></span></span>");
        helpButton.setStyleName(Bootstrap.Buttons.LINK.styleName());
        helpButton.setHtmlContentAllowed(true);
        helpButton.setSizeUndefined();

    }

    protected void initializeLayout() {
        mainContent = new VerticalLayout();
        mainContent.setStyleName("gcp-maincontentarea");
        mainContent.setSizeFull();
        mainContent.setMargin(false);
        mainContent.setSpacing(false);

        // sidebar
        final VerticalSplitPanel sidebarWrap = new VerticalSplitPanel();
        sidebarWrap.setStyleName(Reindeer.SPLITPANEL_SMALL);
        sidebarWrap.addStyleName("bms-sidebarcontent");
        sidebarWrap.setSplitPosition(20,Sizeable.UNITS_PIXELS,true);
        sidebarWrap.setLocked(true);

        final Label title = new Label(String.format("<span style='font-size: 8pt; color:#9EA5A7; display: inline-block; margin-left: 3px'>%s&nbsp;%s</span>",messageSource.getMessage(Message.WORKBENCH_TITLE),workbenchProperties.getProperty("workbench.version", "")),Label.CONTENT_XHTML);

        sidebarWrap.setFirstComponent(sidebar);
        sidebarWrap.setSecondComponent(title);

        // content area
        final VerticalSplitPanel contentAreaSplit = new VerticalSplitPanel();
        contentAreaSplit.setSplitPosition(40,Sizeable.UNITS_PIXELS);
        contentAreaSplit.setLocked(true);
        contentAreaSplit.addStyleName(Reindeer.SPLITPANEL_SMALL);

        // contentArea contents
        contentAreaSplit.addComponent(layoutWorkbenchHeader());
        contentAreaSplit.addComponent(mainContent);

        // the root layout
        root = new HorizontalSplitPanel();
        root.setSizeFull();
        root.setSplitPosition(0,Sizeable.UNITS_PIXELS);
        root.addStyleName(Reindeer.SPLITPANEL_SMALL);
        root.addStyleName("gcp-workbench-content-split-panel");

        // root contents
        root.setFirstComponent(sidebarWrap);
        root.setSecondComponent(contentAreaSplit);


        this.setSizeFull();
        this.setContent(root);
    }


    private void toggleSidebarIcon() {
        if ( root.getSplitPosition() == 0) {
            collapseButton.setCaption("<span class='bms-header-btn'><span class='bms-fa-chevron-right ico'/></span>");

        }
        else {
            collapseButton.setCaption("<span class='bms-header-btn'><span class='bms-fa-chevron-left ico'/></span>");
        }
    }

    protected void initializeActions() {
        final Window thisWindow = this;

        final Button.ClickListener homeAction = new HomeAction();
        homeButton.addListener(homeAction);
        logoBtn.addListener(homeAction);

        helpButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                thisWindow.addWindow(new HelpWindow());
            }
        });

        this.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent closeEvent) {
                IBPWorkbenchApplication.get().toggleJira();
            }
        });

        collapseButton.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent clickEvent) {
                if ( root.getSplitPosition() > 0) {
                    root.setSplitPosition(0,Sizeable.UNITS_PIXELS);
                }
                else {
                    root.setSplitPosition(240,Sizeable.UNITS_PIXELS);
                }
                // change icon here
                toggleSidebarIcon();
            }
        });

    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeActions();
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


        if (sessionData.getLastOpenedProject() != null) {
            workbenchDashboard.initializeDashboardContents(null).doAction(sessionData.getLastOpenedProject().getProjectId(), this);
        }

    }

    private Component layoutWorkbenchHeader() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setStyleName("bms-header");
        headerLayout.setWidth("100%");
        headerLayout.setHeight("100%");
        headerLayout.setMargin(new Layout.MarginInfo(false,false,false,false));
        headerLayout.setSpacing(false);


        headerLayout.addComponent(collapseButton);
        headerLayout.setComponentAlignment(collapseButton, Alignment.MIDDLE_LEFT);

        final Embedded ibpLogo = new Embedded(null, new ThemeResource("../gcp-default/images/ibp_logo2.jpg"));

        logoBtn.setIcon(ibpLogo.getSource());
        logoBtn.setStyleName(BaseTheme.BUTTON_LINK + " bms-logo-btn");
        logoBtn.setWidth("34px");
        logoBtn.setHeight("34px");

        headerLayout.addComponent(logoBtn);
        headerLayout.setComponentAlignment(logoBtn, Alignment.MIDDLE_LEFT);

        // workbench title area
        headerLayout.addComponent(workbenchTitle);
        headerLayout.setComponentAlignment(workbenchTitle, Alignment.MIDDLE_LEFT);
        headerLayout.setExpandRatio(workbenchTitle, 1.0f);

        headerLayout.addComponent(uriFragUtil);


        headerLayout.addComponent(homeButton);
        headerLayout.addComponent(helpButton);
        headerLayout.addComponent(memberButton);

        headerLayout.setComponentAlignment(homeButton,Alignment.MIDDLE_RIGHT);
        headerLayout.setComponentAlignment(helpButton,Alignment.MIDDLE_RIGHT);
        headerLayout.setComponentAlignment(memberButton,Alignment.MIDDLE_RIGHT);

        return headerLayout;
    }

    /**
     * Show the specified {@link Component} on the right side area of the
     * Workbench's split panel.
     *
     * @param content
     */
    public void showContent(Component content) {

            mainContent.removeAllComponents();

        if (content instanceof Embedded) {
            mainContent.addComponent(content);
            mainContent.setExpandRatio(content, 1.0F);

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

                if (content instanceof  ComponentContainer) {
                    wrap.setContent((ComponentContainer) content);
                } else {
                    VerticalLayout vl = new VerticalLayout();
                    vl.addComponent(content);
                    vl.setSizeUndefined();
                    wrap.setContent(vl);
                }

                mainContent.addComponent(wrap);
                mainContent.setExpandRatio(wrap,1.0F);
            }
        }

        if (!(content instanceof WorkbenchDashboard || content instanceof AddProgramView)) {
            root.setSplitPosition(240, Sizeable.UNITS_PIXELS);
        } else {
            root.setSplitPosition(0, Sizeable.UNITS_PIXELS);
        }

        toggleSidebarIcon();
    }

    public void setUriFragment(String fragment,boolean isLinkAccessed) {
        uriFragUtil.setFragment(fragment,!isLinkAccessed);
    }

    @Override
    public void attach() {
        super.attach();

        updateLabels();
    }

    public void addTitle(String myTitle)
    {
        if (myTitle.length() > 50) {
            workbenchTitle.setDescription(myTitle);
        } else {
            workbenchTitle.setDescription("");
        }

        if (myTitle != null && !myTitle.isEmpty()) {
            myTitle = StringUtils.abbreviate(myTitle, 50);
        } else {
            myTitle = "";
        }

        workbenchTitle.setValue(String.format("<h1>%s</h1>",myTitle));

    }

    @Override
    public void updateLabels() {
        IWorkbenchSession appSession = (IWorkbenchSession) this.getApplication();

        String signoutName = appSession.getSessionData().getUserData().getName();
        if (signoutName.length() > 10) {
            signoutName = signoutName.substring(0, 9) + "...";
        }

        memberButton.setCaption("<span class='bms-header-btn2'><span>" + signoutName + "</span><span class='bms-fa-caret-down' style='padding: 0 10px 0 0'></span></span>");

    }

    public WorkbenchSidebar getSidebar() {
        return sidebar;
    }


}
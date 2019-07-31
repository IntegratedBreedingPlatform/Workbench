/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui;

import com.vaadin.terminal.ExternalResource;
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
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.AskForSupportAction;
import org.generationcp.ibpworkbench.actions.HelpButtonClickAction;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.actions.SignoutAction;
import org.generationcp.ibpworkbench.navigation.NavUriFragmentChangedListener;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.project.create.AddProgramView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.ui.window.ChangeCredentialsWindow;
import org.generationcp.ibpworkbench.ui.window.ChangePasswordWindow;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.vaadin.hene.popupbutton.PopupButton;

import javax.annotation.Resource;
import java.util.Objects;

@Configurable
public class WorkbenchMainView extends Window implements IContentWindow, InitializingBean, InternationalizableComponent {
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchMainView.class);

	private static final int SIDEBAR_OPEN_POSITION = 240;
	private static final long serialVersionUID = 1L;

	private static final String HEADER_BTN = " header-btn";

	private Label workbenchTitle;
	private Button homeButton;
	private Button adminButton;
	private PopupButton memberButton;
	private Button helpButton;
	private Button aboutButton;

	@Resource
	private TomcatUtil tomcatUtil;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private SimpleResourceBundleMessageSource messageSource;

	@Resource
	private ContextUtil contextUtil;

	@Value("${workbench.version}")
	private String workbenchVersion;

	@Value("${workbench.is.single.user.only}")
	private String isSingleUserOnly;

	@Value("${workbench.is.add.program.enabled}")
	private String isAddProgramEnabled;

	@Value("${ask.for.support.url}")
	private String askForSupportURL;

	@Value("${about.bms.url}")
	private String aboutBmsURL;


	private Label actionsTitle;

	private HorizontalSplitPanel root;

	private VerticalLayout mainContent;

	private HorizontalLayout workbenchHeaderLayout;

	private WorkbenchDashboard workbenchDashboard;

	private UriFragmentUtility uriFragUtil;
	private NavUriFragmentChangedListener uriChangeListener;

	private WorkbenchSidebar sidebar;
	private Button sidebarToggleButton;
	private Button signoutButton;
	private Button logoBtn;
	private Button askSupportBtn;
	private Button addProgramButton;

	// Hide sidebar toggle button when in Dashboard and Add Program screens where no program has been selected yet
	private boolean doHideSidebarToggleButton = true;
	private boolean isWorkbenchDashboardShown = true;
	private boolean isSiteAdminShown = false;

	public WorkbenchMainView() {
		super("Breeding Management System | Workbench");
	}

	/**
	 * Assemble the UI after all dependencies has been set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
		this.onLoadOperations();
		this.showContent(this.workbenchDashboard);
	}

	protected void initializeComponents() {
		// initialize dashboard
		this.workbenchDashboard = new WorkbenchDashboard(getWindow());
		this.workbenchDashboard.setDebugId("workbenchDashboard");

		// workbench header components
		this.initializeHeaderComponents();

		// sidebar
		this.sidebar = new WorkbenchSidebar();
		this.sidebar.setDebugId("sidebar");

		// left area components
		this.actionsTitle = new Label();
		this.actionsTitle.setDebugId("actionsTitle");
		this.actionsTitle.setStyleName("gcp-section-title");
		this.actionsTitle.setSizeUndefined();

		this.uriFragUtil = new UriFragmentUtility();
		this.uriFragUtil.setDebugId("uriFragUtil");
		this.uriChangeListener = new NavUriFragmentChangedListener();

		this.uriFragUtil.addListener(this.uriChangeListener);
	}

	private void initializeHeaderComponents() {
		this.sidebarToggleButton = new Button("<span class='bms-header-btn'><span class='bms-fa-chevron-right ico'/></span>");
		this.sidebarToggleButton.setDebugId("collapseButton");
		this.sidebarToggleButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + HEADER_BTN);
		this.sidebarToggleButton.setHtmlContentAllowed(true);
		this.sidebarToggleButton.setDescription(this.messageSource.getMessage("TOGGLE_SIDEBAR"));

		final Embedded ibpLogo = new Embedded(null, new ThemeResource("../gcp-default/images/ibp_logo2.jpg"));
		ibpLogo.setDebugId("ibpLogo");
		this.logoBtn = new Button();
		this.logoBtn.setDebugId("logoBtn");
		this.logoBtn.setIcon(ibpLogo.getSource());
		this.logoBtn.setStyleName(BaseTheme.BUTTON_LINK + " bms-logo-btn bms-logo-btn-with-margin");
		this.logoBtn.setWidth("34px");
		this.logoBtn.setHeight("34px");

		this.workbenchTitle = new Label();
		this.workbenchTitle.setDebugId("workbenchTitle");
		this.workbenchTitle.setStyleName("gcp-window-title");
		this.workbenchTitle.setContentMode(Label.CONTENT_XHTML);

		this.displayCurrentProjectTitle();

		this.homeButton = new Button(
				String.format("<span class='bms-header-btn'><span>%s</span></span>", this.messageSource.getMessage("MY_PROGRAMS")));
		this.homeButton.setDebugId("homeButton");
		this.homeButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + HEADER_BTN);
		this.homeButton.setHtmlContentAllowed(true);
		this.homeButton.setSizeUndefined();

		this.addProgramButton = new Button(String.format(
				"<span class='bms-header-btn'><span class='glyphicon glyphicon-plus' style='padding-right: 0px'></span>"
						+ "<span>%s</span></span>", this.messageSource.getMessage(Message.ADD_A_PROGRAM)));
		this.addProgramButton.setDebugId("addProgramButton");
		this.addProgramButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + HEADER_BTN);
		this.addProgramButton.setHtmlContentAllowed(true);
		this.addProgramButton.setSizeUndefined();

		this.adminButton = new Button(
				String.format("<span class='bms-header-btn'><span>%s</span></span>", this.messageSource.getMessage("ADMIN_BUTTON")));
		this.getAdminButton().setStyleName(Bootstrap.Buttons.LINK.styleName() + HEADER_BTN);
		this.getAdminButton().setHtmlContentAllowed(true);
		this.getAdminButton().setSizeUndefined();

		this.memberButton = new PopupButton();
		this.getMemberButton().setDebugId("memberButton");
		this.getMemberButton().setStyleName(Bootstrap.Buttons.LINK.styleName() + " bms-header-popuplink");
		this.getMemberButton().setHtmlContentAllowed(true);
		this.getMemberButton().setSizeUndefined();

		this.signoutButton = new Button(this.messageSource.getMessage(Message.SIGNOUT));
		this.signoutButton.setDebugId("signoutButton");
		this.signoutButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.signoutButton.setSizeFull();
		this.signoutButton.addListener(new SignoutAction());

		final Person member = this.contextUtil.getCurrentWorkbenchUser().getPerson();
		this.getMemberButton().addComponent(this.createMemberPopup(member.getFirstName(), member.getLastName(), member.getEmail()));

		this.aboutButton = new Button("<span class='bms-header-btn2'><span class='bms-fa-information-circle ico'></span></span>");
		this.aboutButton.setDebugId("aboutButton");
		this.aboutButton.setStyleName(Bootstrap.Buttons.LINK.styleName());
		this.aboutButton.setHtmlContentAllowed(true);
		this.aboutButton.setSizeUndefined();

		this.helpButton = new Button("<span class='bms-header-btn2'><span class='bms-fa-question-circle ico'></span></span>");
		this.helpButton.setDebugId("helpButton");
		this.helpButton.setStyleName(Bootstrap.Buttons.LINK.styleName());
		this.helpButton.setHtmlContentAllowed(true);
		this.helpButton.setSizeUndefined();
		this.helpButton.setDebugId("help-button-icon");
	}

	private VerticalLayout createMemberPopup(final String firstName, final String lastName, final String emailAddress) {

		final VerticalLayout memberPopup = new VerticalLayout();
		memberPopup.setDebugId("memberPopup");
		memberPopup.setStyleName("bms-memberpopup");
		memberPopup.setSpacing(true);
		memberPopup.setSizeUndefined();

		memberPopup.addComponent(
				new Label(String.format("<h2>%s %s</h2><h4>%s</h4>", firstName, lastName, emailAddress), Label.CONTENT_XHTML));
		memberPopup.addComponent(this.signoutButton);

		return memberPopup;

	}

	void displayCurrentProjectTitle() {

		try {
			final String projectTitle = this.doHideSidebarToggleButton ? "" : this.contextUtil.getProjectInContext().getProjectName();
			this.addTitle(projectTitle);
		} catch (final MiddlewareQueryException e) {
			// MiddlewareQueryException will be thrown if there's no program exists yet in BMS.
			LOG.debug(e.getMessage(), e);
		}

	}

	Button getAskSupportBtn() {
		if (Objects.equals(this.askSupportBtn, null)) {
			this.askSupportBtn = new Button("<span class='bms-header-btn2'><span class='fa fa-comments ico'></span></span>");
			this.askSupportBtn.setDebugId("askSupportBtn");
			this.askSupportBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());
			this.askSupportBtn.setHtmlContentAllowed(true);
			this.askSupportBtn.setSizeUndefined();
			this.askSupportBtn.setDebugId("support-icon");
			this.askSupportBtn.setDescription("Ask support/Feedback");
			this.askSupportBtn.addListener(new AskForSupportAction(this, this.askForSupportURL));
		}

		return this.askSupportBtn;
	}

	protected void initializeLayout() {
		this.mainContent = new VerticalLayout();
		this.mainContent.setDebugId("mainContent");
		this.mainContent.setStyleName("gcp-maincontentarea");
		this.mainContent.setSizeFull();
		this.mainContent.setMargin(false);
		this.mainContent.setSpacing(false);

		// sidebar
		final VerticalSplitPanel sidebarWrap = new VerticalSplitPanel();
		sidebarWrap.setDebugId("sidebarWrap");
		sidebarWrap.setStyleName(Reindeer.SPLITPANEL_SMALL);
		sidebarWrap.addStyleName("bms-sidebarcontent");
		sidebarWrap.setSplitPosition(20, Sizeable.UNITS_PIXELS, true);
		sidebarWrap.setLocked(true);

		final Label title = new Label(
				String.format("<span style='font-size: 8pt; color:#9EA5A7; display: inline-block; margin-left: 3px'>%s&nbsp;%s</span>",
						this.messageSource.getMessage(Message.WORKBENCH_TITLE), this.workbenchVersion), Label.CONTENT_XHTML);

		sidebarWrap.setFirstComponent(this.sidebar);
		sidebarWrap.setSecondComponent(title);

		// content area
		final VerticalSplitPanel contentAreaSplit = new VerticalSplitPanel();
		contentAreaSplit.setDebugId("contentAreaSplit");
		contentAreaSplit.setSplitPosition(40, Sizeable.UNITS_PIXELS);
		contentAreaSplit.setLocked(true);
		contentAreaSplit.addStyleName(Reindeer.SPLITPANEL_SMALL);

		this.workbenchHeaderLayout = new HorizontalLayout();
		this.workbenchHeaderLayout.setDebugId("headerLayout");
		this.workbenchHeaderLayout.setStyleName("bms-header");
		this.workbenchHeaderLayout.setWidth("100%");
		this.workbenchHeaderLayout.setHeight("100%");
		this.workbenchHeaderLayout.setMargin(new Layout.MarginInfo(false, false, false, false));
		this.workbenchHeaderLayout.setSpacing(false);
		this.layoutWorkbenchHeaderComponents();

		// contentArea contents
		contentAreaSplit.addComponent(this.workbenchHeaderLayout);
		contentAreaSplit.addComponent(this.mainContent);

		// the root layout
		this.root = new HorizontalSplitPanel();
		this.root.setDebugId("root");
		this.root.setSizeFull();
		this.root.setSplitPosition(0, Sizeable.UNITS_PIXELS);
		this.root.addStyleName(Reindeer.SPLITPANEL_SMALL);
		this.root.addStyleName("gcp-workbench-content-split-panel");

		// root contents
		this.root.setFirstComponent(sidebarWrap);
		this.root.setSecondComponent(contentAreaSplit);

		this.setSizeFull();
		this.setContent(this.root);
	}

	private void toggleSidebarIcon() {
		if (this.root.getSplitPosition() == 0) {
			this.sidebarToggleButton.setCaption("<span class='bms-header-btn'><span class='bms-fa-chevron-right ico'/></span>");

		} else {
			this.sidebarToggleButton.setCaption("<span class='bms-header-btn'><span class='bms-fa-chevron-left ico'/></span>");
		}
	}

	protected void initializeActions() {

		final Button.ClickListener homeAction = new HomeAction();
		this.homeButton.addListener(homeAction);
		this.logoBtn.addListener(homeAction);

		this.addProgramButton.addListener(new OpenNewProjectAction());

		this.getAdminButton().addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				WorkbenchMainView.this.isSiteAdminShown = true;
				final IContentWindow contentFrame = (IContentWindow) event.getComponent().getWindow();
				contentFrame.showContent("controller/admin");

				// collapse sidebar
				WorkbenchMainView.this.root.setSplitPosition(0, Sizeable.UNITS_PIXELS);
				// change icon here
				WorkbenchMainView.this.toggleSidebarIcon();
				WorkbenchMainView.this.isSiteAdminShown = false;
			}
		});

		this.aboutButton.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				final IContentWindow contentFrame = (IContentWindow) event.getComponent().getWindow();
				contentFrame.showContent("controller/about/");
			}
		});

		this.helpButton.addListener(new HelpButtonClickAction(this, this.aboutBmsURL));

		this.addListener(new CloseListener() {

			@Override
			public void windowClose(final CloseEvent closeEvent) {
				IBPWorkbenchApplication.get().toggleScripts();
			}
		});

		this.sidebarToggleButton.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(final ClickEvent clickEvent) {
				if (WorkbenchMainView.this.root.getSplitPosition() > 0) {
					WorkbenchMainView.this.root.setSplitPosition(0, Sizeable.UNITS_PIXELS);
				} else {
					WorkbenchMainView.this.root.setSplitPosition(240, Sizeable.UNITS_PIXELS);
				}
				// change icon here
				WorkbenchMainView.this.toggleSidebarIcon();
			}
		});

	}

	protected void assemble() {
		this.initializeComponents();
		this.initializeLayout();
		this.initializeActions();
	}

	void onLoadOperations() {

		final WorkbenchUser user = this.contextUtil.getCurrentWorkbenchUser();

		final UserInfo userInfo = this.createUserInfoIfNecessary(user);

		this.showChangeCredentialsWindowOnFirstLogin(this.getWindow(), user, userInfo);

		this.workbenchDataManager.incrementUserLogInCount(userInfo.getUserId());

	}

	UserInfo createUserInfoIfNecessary(final WorkbenchUser user) {

		UserInfo userInfo = this.workbenchDataManager.getUserInfo(user.getUserid());

		if (userInfo == null) {
			userInfo = new UserInfo();
			userInfo.setUserId(user.getUserid());
			userInfo.setLoginCount(0);
			this.workbenchDataManager.insertOrUpdateUserInfo(userInfo);
		}

		return userInfo;
	}

	void showChangeCredentialsWindowOnFirstLogin(final Window window, final WorkbenchUser user, final UserInfo userInfo) {

		// Only display the Change Credentials/Password on first login of user
		if (userInfo.getLoginCount() < 1) {
			if (this.workbenchDataManager.isSuperAdminUser(user.getUserid())) {
				// If the user has SUPERADMIN role, on first login, force the user to change
				// the account firstname, lastname, email address and password (optional)
				window.addWindow(new ChangeCredentialsWindow(new ChangeCredentialsWindow.CredentialsChangedEvent() {

					@Override
					public void onChanged(final String firstname, final String lastName, final String emailAddress) {

						// Refresh the name and email of member detail popup after the user credrentials are changed.
						WorkbenchMainView.this.refreshMemberDetailsPopup(firstname, lastName, emailAddress);

					}

				}));
			} else {
				// If not admin user, just ask to change the password.
				window.addWindow(new ChangePasswordWindow());
			}

		}
	}

	/*
	 * Layout the components in Workbench header. Button to expand/collapse will be hidden when in Dashboard and Create Program views.
	 */
	private void layoutWorkbenchHeaderComponents() {
		this.logoBtn.setStyleName(BaseTheme.BUTTON_LINK + " bms-logo-btn bms-logo-btn-with-margin");
		// Button to collapse or expand sidebar
		if (!this.doHideSidebarToggleButton) {
			this.logoBtn.setStyleName(BaseTheme.BUTTON_LINK + " bms-logo-btn");
			this.workbenchHeaderLayout.addComponent(this.sidebarToggleButton);
			this.workbenchHeaderLayout.setComponentAlignment(this.sidebarToggleButton, Alignment.MIDDLE_LEFT);
		}

		this.workbenchHeaderLayout.addComponent(this.logoBtn);
		this.workbenchHeaderLayout.setComponentAlignment(this.logoBtn, Alignment.MIDDLE_LEFT);

		// workbench title area
		this.workbenchHeaderLayout.addComponent(this.workbenchTitle);
		this.workbenchHeaderLayout.setComponentAlignment(this.workbenchTitle, Alignment.MIDDLE_LEFT);
		this.workbenchHeaderLayout.setExpandRatio(this.workbenchTitle, 1.0f);

		this.workbenchHeaderLayout.addComponent(this.uriFragUtil);

		try {
			this.layoutAdminButton();
		} catch (final AccessDeniedException e) {
			// do nothing if the user is not authorized to access Admin button
			LOG.debug(e.getMessage(), e);
		}

		if (this.isWorkbenchDashboardShown) {
			try {
				this.layoutAddProgramButton(this.workbenchHeaderLayout);
			} catch (final AccessDeniedException e) {
				// do nothing if the user is not authorized to access Admin button
				LOG.debug(e.getMessage(), e);
			}
		} else {
			this.workbenchHeaderLayout.addComponent(this.homeButton);
			this.workbenchHeaderLayout.setComponentAlignment(this.homeButton, Alignment.MIDDLE_RIGHT);
		}

		this.workbenchHeaderLayout.addComponent(this.aboutButton);
		this.workbenchHeaderLayout.addComponent(this.helpButton);
		this.workbenchHeaderLayout.addComponent(this.getAskSupportBtn());
		this.workbenchHeaderLayout.addComponent(this.getMemberButton());

		this.workbenchHeaderLayout.setComponentAlignment(this.askSupportBtn, Alignment.MIDDLE_RIGHT);
		this.workbenchHeaderLayout.setComponentAlignment(this.helpButton, Alignment.MIDDLE_RIGHT);
		this.workbenchHeaderLayout.setComponentAlignment(this.getMemberButton(), Alignment.MIDDLE_RIGHT);
	}

	private void refreshHeaderLayout() {
		this.workbenchHeaderLayout.removeAllComponents();

		this.toggleSidebarIcon();
		this.layoutWorkbenchHeaderComponents();
		this.workbenchHeaderLayout.requestRepaint();
	}

	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ADMINISTRATION') or hasRole('ROLE_SITE_ADMIN')")
	private void layoutAdminButton() {
		this.addAdminButton(this.workbenchHeaderLayout);
	}

	void addAdminButton(final HorizontalLayout layout) {
		// Do not display the admin button if BMS is in single user mode.
		if (!Boolean.parseBoolean(this.isSingleUserOnly)) {
			layout.addComponent(this.getAdminButton());
			layout.setComponentAlignment(this.getAdminButton(), Alignment.MIDDLE_RIGHT);
		}

	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CROP_MANAGEMENT','ROLE_MANAGE_PROGRAMS','ROLE_ADD_PROGRAM')")
	void layoutAddProgramButton(final HorizontalLayout layout) {

		if (Boolean.parseBoolean(this.isAddProgramEnabled)) {
			// Only display the Add A Program Button if user is admin and isAddProgramEnabled is true
			layout.addComponent(this.addProgramButton);
			layout.setComponentAlignment(this.addProgramButton, Alignment.MIDDLE_RIGHT);
		}

	}

	/**
	 * Show the specified {@link Component} on the right side area of the Workbench's split panel.
	 *
	 * @param content
	 */
	@Override
	public void showContent(final Component content) {

		this.mainContent.removeAllComponents();

		if (content instanceof Embedded) {
			this.mainContent.addComponent(content);
			this.mainContent.setExpandRatio(content, 1.0F);

		} else {

			if (content instanceof Panel) {
				content.setStyleName(Reindeer.PANEL_LIGHT);
				this.mainContent.addComponent(content);
				this.mainContent.setExpandRatio(content, 1.0F);
			} else {
				final Panel wrap = new Panel();
				wrap.setDebugId("wrap");
				wrap.setStyleName(Reindeer.PANEL_LIGHT);
				wrap.setSizeFull();
				wrap.setScrollable(true);

				if (content instanceof ComponentContainer) {
					wrap.setContent((ComponentContainer) content);
				} else {
					final VerticalLayout vl = new VerticalLayout();
					vl.setDebugId("vl");
					vl.addComponent(content);
					vl.setSizeUndefined();
					wrap.setContent(vl);
				}

				this.mainContent.addComponent(wrap);
				this.mainContent.setExpandRatio(wrap, 1.0F);
			}
		}

		// Hide sidebar button if in Workbench Dashboard or in Create Program screens
		this.isWorkbenchDashboardShown = content instanceof WorkbenchDashboard;
		this.doHideSidebarToggleButton = this.isWorkbenchDashboardShown || this.isSiteAdminShown
			|| content instanceof AddProgramView || this.workbenchTitle.getDescription() != "";
		if (this.doHideSidebarToggleButton) {
			this.root.setSplitPosition(0, Sizeable.UNITS_PIXELS);
		} else {
			this.root.setSplitPosition(SIDEBAR_OPEN_POSITION, Sizeable.UNITS_PIXELS);
		}

		this.root.setLocked(this.doHideSidebarToggleButton);
		this.displayCurrentProjectTitle();
		// Refresh buttons available on header section
		this.refreshHeaderLayout();
	}

	@Override
	public void showContent(final String toolUrl) {
		if (!toolUrl.isEmpty()) {
			final Embedded browser = new Embedded(null, new ExternalResource(toolUrl));
			browser.setDebugId("browser");

			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();

			this.showContent(browser);
		}
	}

	public void setUriFragment(final String fragment, final boolean isLinkAccessed) {
		this.uriFragUtil.setFragment(fragment, !isLinkAccessed);
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	public void addTitle(final String myTitle) {
		if (myTitle.length() > 50) {
			this.workbenchTitle.setDescription(myTitle);
		} else {
			this.workbenchTitle.setDescription("");
		}
		String pageTitle = "";
		if (!myTitle.isEmpty()) {
			pageTitle = StringUtils.abbreviate(myTitle, 50);
		}

		this.workbenchTitle.setValue(String.format("<h1>%s</h1>", pageTitle));

	}

	@Override
	public void updateLabels() {

		String signoutName = this.contextUtil.getCurrentWorkbenchUser().getName();
		if (signoutName.length() > 10) {
			signoutName = signoutName.substring(0, 9) + "...";
		}

		this.getMemberButton().setCaption("<span class='bms-header-btn2'><span>" + signoutName
				+ "</span><span class='bms-fa-caret-down' style='padding: 0 10px 0 0'></span></span>");

	}

	public WorkbenchSidebar getSidebar() {
		return this.sidebar;
	}

	public void setSidebar(final WorkbenchSidebar sidebar) {
		this.sidebar = sidebar;
	}

	// For test purposes
	HorizontalLayout getWorkbenchHeaderLayout() {
		return this.workbenchHeaderLayout;
	}

	// For test purposes
	Button getSidebarToggleButton() {
		return this.sidebarToggleButton;
	}

	Button getAddProgramButton() {
		return this.addProgramButton;
	}

	Button getHomeButton() {
		return this.homeButton;
	}

	Label getWorkbenchTitle() {
		return this.workbenchTitle;
	}

	// For test purposes
	void setIsSingleUserOnly(final String isSingleUserOnly) {
		this.isSingleUserOnly = isSingleUserOnly;
	}

	// For test purposes
	Button getAdminButton() {
		return this.adminButton;
	}

	// For test purposes
	void setWorkbenchTitle(final Label workbenchTitle) {
		this.workbenchTitle = workbenchTitle;
	}

	// For test purposes
	void setIsAddProgramEnabled(final String isAddProgramEnabled) {
		this.isAddProgramEnabled = isAddProgramEnabled;
	}

	void refreshMemberDetailsPopup(final String firstname, final String lastName, final String emailAddress) {

		this.getMemberButton().removeAllComponents();
		this.getMemberButton().addComponent(this.createMemberPopup(firstname, lastName, emailAddress));

	}

	// For testing purpose only
	PopupButton getMemberButton() {
		return this.memberButton;
	}

	void setMemberButton(final PopupButton memberButton) {
		this.memberButton = memberButton;
	}

	Button getLogoBtn() {
		return this.logoBtn;
	}

	Button getHelpButton() {
		return this.helpButton;
	}


	void setAskForSupportURL(final String askForSupportURL) {
		this.askForSupportURL = askForSupportURL;
	}


	void setAboutBmsURL(final String aboutBmsURL) {
		this.aboutBmsURL = aboutBmsURL;
	}

	void setDoHideSidebarToggleButton(final boolean doHideSidebarToggleButton) {
		this.doHideSidebarToggleButton = doHideSidebarToggleButton;
	}

	void setIsSiteAdminShown(final boolean isSiteAdminShown) {
		this.isSiteAdminShown = isSiteAdminShown;
	}

	void setIsWorkbenchDashboardShown(final boolean isWorkbenchDashboardShown) {
		this.isWorkbenchDashboardShown = isWorkbenchDashboardShown;
	}
}

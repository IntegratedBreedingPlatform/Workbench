/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui;

import java.util.Objects;
import java.util.Properties;
import javax.annotation.Resource;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.help.document.HelpWindow;
import org.generationcp.commons.tomcat.util.TomcatUtil;
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
import org.generationcp.ibpworkbench.service.AppLauncherService;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.project.create.AddProgramView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.ui.window.EmbeddedWindow;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.WorkbenchUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.hene.popupbutton.PopupButton;

@Configurable
public class WorkbenchMainView extends Window implements IContentWindow, InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchMainView.class);

	private static final String HEADER_BTN = " header-btn";

	private Label workbenchTitle;
	private Button homeButton;
	private Button adminButton;
	private PopupButton memberButton;
	private Button helpButton;

	@Resource
	private TomcatUtil tomcatUtil;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private SimpleResourceBundleMessageSource messageSource;

	@Resource
	private SessionData sessionData;

	@Resource
	private AppLauncherService applauncherService;

	@Resource
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
	private Button askSupportBtn;

	public WorkbenchMainView() {
		super("Breeding Management System | Workbench");
	}

	/**
	 * Assemble the UI after all dependencies has been set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
		this.workbenchDashboard = new WorkbenchDashboard();
		this.onLoadOperations();
		this.showContent(this.workbenchDashboard);
	}

	protected void initializeComponents() {
		// workbench header components
		this.initializeHeaderComponents();

		// sidebar
		this.sidebar = new WorkbenchSidebar();

		// left area components
		this.actionsTitle = new Label();
		this.actionsTitle.setStyleName("gcp-section-title");
		this.actionsTitle.setSizeUndefined();

		this.collapseButton = new Button("<span class='bms-header-btn'><span class='bms-fa-chevron-right ico'/></span>");
		this.collapseButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + HEADER_BTN);
		this.collapseButton.setHtmlContentAllowed(true);
		this.collapseButton.setDescription(this.messageSource.getMessage("TOGGLE_SIDEBAR"));

		this.uriFragUtil = new UriFragmentUtility();
		this.uriChangeListener = new NavUriFragmentChangedListener();

		this.uriFragUtil.addListener(this.uriChangeListener);
	}

	private void initializeHeaderComponents() {

		this.logoBtn = new Button();

		this.workbenchTitle = new Label();
		this.workbenchTitle.setStyleName("gcp-window-title");
		this.workbenchTitle.setContentMode(Label.CONTENT_XHTML);

		//TODO localise that text
		this.homeButton = new Button("<span class='bms-header-btn'><span>My Programs</span></span>");
		this.homeButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + HEADER_BTN);
		this.homeButton.setHtmlContentAllowed(true);
		this.homeButton.setSizeUndefined();

		this.adminButton = new Button(
				String.format("<span class='bms-header-btn'><span>%s</span></span>", this.messageSource.getMessage("ADMIN_BUTTON")));
		this.adminButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + HEADER_BTN);
		this.adminButton.setHtmlContentAllowed(true);
		this.adminButton.setSizeUndefined();

		this.memberButton = new PopupButton();
		this.memberButton.setStyleName(Bootstrap.Buttons.LINK.styleName() + " bms-header-popuplink");
		this.memberButton.setHtmlContentAllowed(true);
		this.memberButton.setSizeUndefined();

		final VerticalLayout memberPopup = new VerticalLayout();
		memberPopup.setStyleName("bms-memberpopup");
		memberPopup.setSpacing(true);
		memberPopup.setSizeUndefined();

		this.signoutButton = new Button(this.messageSource.getMessage(Message.SIGNOUT));
		this.signoutButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.signoutButton.setSizeFull();
		this.signoutButton.addListener(new SignoutAction());

		Person member = this.sessionData.getUserData().getPerson();
		memberPopup.addComponent(
				new Label(String.format("<h2>%s %s</h2><h4>%s</h4>", member.getFirstName(), member.getLastName(), member.getEmail()),
						Label.CONTENT_XHTML));
		memberPopup.addComponent(this.signoutButton);

		this.memberButton.addComponent(memberPopup);

		this.helpButton = new Button("<span class='bms-header-btn2'><span class='bms-fa-question-circle ico'></span></span>");
		this.helpButton.setStyleName(Bootstrap.Buttons.LINK.styleName());
		this.helpButton.setHtmlContentAllowed(true);
		this.helpButton.setSizeUndefined();
		this.helpButton.setDebugId("help-button-icon");
	}

	private Button getAskSupportBtn() {
		if (Objects.equals(this.askSupportBtn, null)) {
			this.askSupportBtn = new Button("<span class='bms-header-btn2'><span class='fa fa-comments ico'></span></span>");
			this.askSupportBtn.setStyleName(Bootstrap.Buttons.LINK.styleName());
			this.askSupportBtn.setHtmlContentAllowed(true);
			this.askSupportBtn.setSizeUndefined();
			this.askSupportBtn.setDebugId("support-icon");
			this.askSupportBtn.setDescription("Ask support/Feedback");
			this.askSupportBtn.addListener(new Button.ClickListener() {

				@Override
				public void buttonClick(ClickEvent clickEvent) {
					EmbeddedWindow askSupportWindow = new EmbeddedWindow();
					askSupportWindow.setWidth("60%");
					askSupportWindow.setHeight("80%");
					askSupportWindow.setCaption("Ask Support/Feedback");
					askSupportWindow.showContent("/ibpworkbench/controller/support/");

					WorkbenchMainView.this.addWindow(askSupportWindow);
				}
			});
		}

		return this.askSupportBtn;
	}

	protected void initializeLayout() {
		this.mainContent = new VerticalLayout();
		this.mainContent.setStyleName("gcp-maincontentarea");
		this.mainContent.setSizeFull();
		this.mainContent.setMargin(false);
		this.mainContent.setSpacing(false);

		// sidebar
		final VerticalSplitPanel sidebarWrap = new VerticalSplitPanel();
		sidebarWrap.setStyleName(Reindeer.SPLITPANEL_SMALL);
		sidebarWrap.addStyleName("bms-sidebarcontent");
		sidebarWrap.setSplitPosition(20, Sizeable.UNITS_PIXELS, true);
		sidebarWrap.setLocked(true);

		final Label title = new Label(
				String.format("<span style='font-size: 8pt; color:#9EA5A7; display: inline-block; margin-left: 3px'>%s&nbsp;%s</span>",
						this.messageSource.getMessage(Message.WORKBENCH_TITLE),
						this.workbenchProperties.getProperty("workbench.version", "")), Label.CONTENT_XHTML);

		sidebarWrap.setFirstComponent(this.sidebar);
		sidebarWrap.setSecondComponent(title);

		// content area
		final VerticalSplitPanel contentAreaSplit = new VerticalSplitPanel();
		contentAreaSplit.setSplitPosition(40, Sizeable.UNITS_PIXELS);
		contentAreaSplit.setLocked(true);
		contentAreaSplit.addStyleName(Reindeer.SPLITPANEL_SMALL);

		// contentArea contents
		contentAreaSplit.addComponent(this.layoutWorkbenchHeader());
		contentAreaSplit.addComponent(this.mainContent);

		// the root layout
		this.root = new HorizontalSplitPanel();
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
			this.collapseButton.setCaption("<span class='bms-header-btn'><span class='bms-fa-chevron-right ico'/></span>");

		} else {
			this.collapseButton.setCaption("<span class='bms-header-btn'><span class='bms-fa-chevron-left ico'/></span>");
		}
	}

	protected void initializeActions() {
		final Window thisWindow = this;

		final Button.ClickListener homeAction = new HomeAction();
		this.homeButton.addListener(homeAction);
		this.logoBtn.addListener(homeAction);
		
		this.adminButton.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final IContentWindow contentFrame = (IContentWindow) event.getComponent().getWindow();
				contentFrame.showContent("controller/admin");

				// collapse sidebar
				WorkbenchMainView.this.root.setSplitPosition(0, Sizeable.UNITS_PIXELS);
				// change icon here
				WorkbenchMainView.this.toggleSidebarIcon();
			}
		});

		this.helpButton.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				thisWindow.addWindow(new HelpWindow(WorkbenchMainView.this.workbenchDataManager, WorkbenchMainView.this.tomcatUtil,
						WorkbenchMainView.this.workbenchProperties));
			}
		});

		this.addListener(new CloseListener() {

			@Override
			public void windowClose(CloseEvent closeEvent) {
				IBPWorkbenchApplication.get().toggleScripts();
			}
		});

		this.collapseButton.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent clickEvent) {
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

	protected void onLoadOperations() {
		User user = this.sessionData.getUserData();
		String username = user.getName();

		if (username == null) {
			return;
		}

		try {
			UserInfo userInfo = this.updateUserInfoIfNecessary(user);
			this.workbenchDataManager.incrementUserLogInCount(userInfo.getUserId());
		} catch (MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}

		if (this.sessionData.getLastOpenedProject() != null) {
			this.workbenchDashboard.initializeDashboardContents(null)
					.doAction(this.sessionData.getLastOpenedProject().getProjectId(), this);
		}

	}

	private UserInfo updateUserInfoIfNecessary(User user) throws MiddlewareQueryException {
		UserInfo userInfo = this.workbenchDataManager.getUserInfo(user.getUserid());
		if (userInfo == null || userInfo.getLoginCount() < 1) {
			if (WorkbenchUtil.isPasswordEqualToUsername(user) && userInfo != null) {
				OpenWindowAction ow = new OpenWindowAction(WindowEnum.CHANGE_PASSWORD);
				ow.launchWindow(this, WindowEnum.CHANGE_PASSWORD);
			}

			if (userInfo == null) {
				userInfo = new UserInfo();
			}
			userInfo.setUserId(user.getUserid());
			userInfo.setLoginCount(1);
			this.workbenchDataManager.insertOrUpdateUserInfo(userInfo);

		}
		return userInfo;
	}

	private Component layoutWorkbenchHeader() {
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setStyleName("bms-header");
		headerLayout.setWidth("100%");
		headerLayout.setHeight("100%");
		headerLayout.setMargin(new Layout.MarginInfo(false, false, false, false));
		headerLayout.setSpacing(false);

		headerLayout.addComponent(this.collapseButton);
		headerLayout.setComponentAlignment(this.collapseButton, Alignment.MIDDLE_LEFT);

		final Embedded ibpLogo = new Embedded(null, new ThemeResource("../gcp-default/images/ibp_logo2.jpg"));

		this.logoBtn.setIcon(ibpLogo.getSource());
		this.logoBtn.setStyleName(BaseTheme.BUTTON_LINK + " bms-logo-btn");
		this.logoBtn.setWidth("34px");
		this.logoBtn.setHeight("34px");

		headerLayout.addComponent(this.logoBtn);
		headerLayout.setComponentAlignment(this.logoBtn, Alignment.MIDDLE_LEFT);

		// workbench title area
		headerLayout.addComponent(this.workbenchTitle);
		headerLayout.setComponentAlignment(this.workbenchTitle, Alignment.MIDDLE_LEFT);
		headerLayout.setExpandRatio(this.workbenchTitle, 1.0f);

		headerLayout.addComponent(this.uriFragUtil);

		headerLayout.addComponent(this.adminButton);
		headerLayout.addComponent(this.homeButton);

		headerLayout.addComponent(this.helpButton);
		headerLayout.addComponent(this.getAskSupportBtn());
		headerLayout.addComponent(this.memberButton);

		headerLayout.setComponentAlignment(this.homeButton, Alignment.MIDDLE_RIGHT);
		headerLayout.setComponentAlignment(this.adminButton, Alignment.MIDDLE_RIGHT);
		headerLayout.setComponentAlignment(this.askSupportBtn, Alignment.MIDDLE_RIGHT);
		headerLayout.setComponentAlignment(this.helpButton, Alignment.MIDDLE_RIGHT);
		headerLayout.setComponentAlignment(this.memberButton, Alignment.MIDDLE_RIGHT);

		return headerLayout;
	}

	/**
	 * Show the specified {@link Component} on the right side area of the Workbench's split panel.
	 *
	 * @param content
	 */
	@Override
	public void showContent(Component content) {

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
				wrap.setStyleName(Reindeer.PANEL_LIGHT);
				wrap.setSizeFull();
				wrap.setScrollable(true);

				if (content instanceof ComponentContainer) {
					wrap.setContent((ComponentContainer) content);
				} else {
					VerticalLayout vl = new VerticalLayout();
					vl.addComponent(content);
					vl.setSizeUndefined();
					wrap.setContent(vl);
				}

				this.mainContent.addComponent(wrap);
				this.mainContent.setExpandRatio(wrap, 1.0F);
			}
		}

		if (!(content instanceof WorkbenchDashboard || content instanceof AddProgramView)) {
			this.root.setSplitPosition(240, Sizeable.UNITS_PIXELS);
		} else {
			this.root.setSplitPosition(0, Sizeable.UNITS_PIXELS);
		}

		this.toggleSidebarIcon();
	}

	@Override
	public void showContent(String toolUrl) {
		if (!toolUrl.isEmpty()) {
			Embedded browser = new Embedded(null, new ExternalResource(toolUrl));

			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();

			this.showContent(browser);
		}
	}

	public void setUriFragment(String fragment, boolean isLinkAccessed) {
		this.uriFragUtil.setFragment(fragment, !isLinkAccessed);
	}

	@Override
	public void attach() {
		super.attach();

		this.updateLabels();
	}

	public void addTitle(String myTitle) {
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
		IWorkbenchSession appSession = (IWorkbenchSession) this.getApplication();

		String signoutName = appSession.getSessionData().getUserData().getName();
		if (signoutName.length() > 10) {
			signoutName = signoutName.substring(0, 9) + "...";
		}

		this.memberButton.setCaption("<span class='bms-header-btn2'><span>" + signoutName
				+ "</span><span class='bms-fa-caret-down' style='padding: 0 10px 0 0'></span></span>");

	}

	public WorkbenchSidebar getSidebar() {
		return this.sidebar;
	}

}

package org.generationcp.ibpworkbench.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.ibpworkbench.ui.window.ChangeCredentialsWindow;
import org.generationcp.ibpworkbench.ui.window.ChangePasswordWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import junit.framework.Assert;

public class WorkbenchMainViewTest {

	public static final String PROJECT_NAME = "Maize Program 1";
	public static final String CURRENT_USER_NAME = "John Doe";

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private Properties workbenchProperties;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private WorkbenchMainView workbenchMainView;

	private Project currentProject;

	private int ADMIN_USER_ID = 1;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Setup mocks
		final Person person = new Person("A", "B", "C");
		person.setEmail("a@leafnode.io");
		final User currentUser = new User(ADMIN_USER_ID);
		currentUser.setName(CURRENT_USER_NAME);
		currentUser.setPerson(person);

		Mockito.when(contextUtil.getCurrentWorkbenchUser()).thenReturn(currentUser);

		this.currentProject = new Project();
		this.currentProject.setProjectName(PROJECT_NAME);
		Mockito.when(contextUtil.getProjectInContext()).thenReturn(currentProject);

		this.workbenchMainView.initializeComponents();
		this.workbenchMainView.initializeLayout();
	}

	@Test
	public void testHeaderLayoutWhenDashboardIsShowing() {
		// Verify header already as dashboard is default content of main window
		this.verifyHeaderLayoutWHenShowingDashboard();

		// Verify that name of last opened project is displayed on header
		this.verifyLastOpenedProjectNameIsDisplayed();
	}

	@Test
	public void testHeaderLayoutWhenDashboardIsNotShowing() {
		this.workbenchMainView.showContent(new VerticalLayout());

		this.verifyHeaderLayoutWhenNotShowingDashboard();

		// Verify that name of last opened project is displayed on header
		this.verifyLastOpenedProjectNameIsDisplayed();
	}

	private void verifyLastOpenedProjectNameIsDisplayed() {
		Assert.assertEquals("<h1>" + this.currentProject.getProjectName() + "</h1>", this.workbenchMainView.getWorkbenchTitle().getValue());
	}

	private void verifyHeaderLayoutWHenShowingDashboard() {
		final HorizontalLayout workbenchHeaderLayout = this.workbenchMainView.getWorkbenchHeaderLayout();
		Assert.assertEquals(8, workbenchHeaderLayout.getComponentCount());

		final Iterator<Component> componentIterator = workbenchHeaderLayout.getComponentIterator();
		boolean addProgramButtonShown = false;
		while (componentIterator.hasNext()) {
			final Component component = componentIterator.next();
			// Verify that button to toggle sidebar is not showing
			if (component.equals(this.workbenchMainView.getSidebarToggleButton())) {
				Assert.fail("Toggle button to show sidebar should be hidden but was not.");

				// Verify "My Programs" button is not showing
			} else if (component.equals(this.workbenchMainView.getHomeButton())) {
				Assert.fail("My Programs button should be hidden but was not.");

			} else if (component.equals(this.workbenchMainView.getAddProgramButton())) {
				addProgramButtonShown = true;
			}
		}

		// Verify "Add Program" button is showing
		Assert.assertTrue(addProgramButtonShown);
	}

	private void verifyHeaderLayoutWhenNotShowingDashboard() {
		final HorizontalLayout workbenchHeaderLayout = this.workbenchMainView.getWorkbenchHeaderLayout();
		Assert.assertEquals(9, workbenchHeaderLayout.getComponentCount());

		final Iterator<Component> componentIterator = workbenchHeaderLayout.getComponentIterator();
		boolean toggleSidebarButtonShowing = false;
		boolean myProgramsButtonShowing = false;
		while (componentIterator.hasNext()) {
			final Component component = componentIterator.next();

			if (component.equals(this.workbenchMainView.getSidebarToggleButton())) {
				toggleSidebarButtonShowing = true;
			} else if (component.equals(this.workbenchMainView.getHomeButton())) {
				myProgramsButtonShowing = true;
				// Verify that Add Program button should not be showing - it's only appears on Dashboard page
			} else if (component.equals(this.workbenchMainView.getAddProgramButton())) {
				Assert.fail("Add Program button should be hidden but was not.");
			}
		}

		// Verify "My Programs" button is showing
		Assert.assertTrue(myProgramsButtonShowing);
		// Verify that button to toggle sidebar is showing
		Assert.assertTrue(toggleSidebarButtonShowing);
	}

	@Test
	public void testCreateUserInfoIfNecessary() {

		final User user = new User();
		user.setUserid(101);

		Mockito.when(this.workbenchDataManager.getUserInfo(Matchers.anyInt())).thenReturn(null);

		final UserInfo newUserInfo = this.workbenchMainView.createUserInfoIfNecessary(user);

		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).insertOrUpdateUserInfo(newUserInfo);
		Assert.assertEquals(user.getUserid(), newUserInfo.getUserId());
		Assert.assertEquals(Integer.valueOf(0), newUserInfo.getLoginCount());

	}

	@Test
	public void testShowChangeCredentialsWindowOnFirstLoginUserIsAdminAccount() {

		final Window window = Mockito.mock(Window.class);
		final User user = new User();
		user.setName(ProgramService.ADMIN_USERNAME);

		final UserInfo userInfo = new UserInfo();
		userInfo.setUserId(ADMIN_USER_ID);
		userInfo.setLoginCount(0);

		// Verify that Change Credentials window is displayed
		this.workbenchMainView.showChangeCredentialsWindowOnFirstLogin(window, user, userInfo);

		Mockito.verify(window).addWindow(Mockito.isA(ChangeCredentialsWindow.class));

	}

	@Test
	public void testShowChangeCredentialsWindowOnFirstLoginUserIsAdminAccountSecondLogin() {

		final Window window = Mockito.mock(Window.class);
		final User user = new User();
		user.setName(ProgramService.ADMIN_USERNAME);

		final UserInfo userInfo = new UserInfo();
		userInfo.setUserId(ADMIN_USER_ID);
		userInfo.setLoginCount(1);

		this.workbenchMainView.showChangeCredentialsWindowOnFirstLogin(window, user, userInfo);

		// Verify that Change Credentials window is not displayed
		Mockito.verify(window, Mockito.times(0)).addWindow(Mockito.isA(ChangeCredentialsWindow.class));

	}

	@Test
	public void testShowChangeCredentialsWindowOnFirstLoginUserIsNotAdminAccount() {

		final Window window = Mockito.mock(Window.class);
		final User user = new User();
		user.setName("Username");

		final UserInfo userInfo = new UserInfo();
		userInfo.setUserId(1000);
		userInfo.setLoginCount(0);

		this.workbenchMainView.showChangeCredentialsWindowOnFirstLogin(window, user, userInfo);

		// Verify that Change Password window is displayed
		Mockito.verify(window).addWindow(Mockito.isA(ChangePasswordWindow.class));

	}

	@Test
	public void testShowChangeCredentialsWindowOnFirstLoginUserIsNotAdminAccountSecondLogin() {

		final Window window = Mockito.mock(Window.class);
		final User user = new User();
		user.setName(ProgramService.ADMIN_USERNAME);

		final UserInfo userInfo = new UserInfo();
		userInfo.setUserId(1000);
		userInfo.setLoginCount(1);

		this.workbenchMainView.showChangeCredentialsWindowOnFirstLogin(window, user, userInfo);

		// Verify that Change Password window is not displayed
		Mockito.verify(window, Mockito.times(0)).addWindow(Mockito.isA(ChangePasswordWindow.class));

	}

	@Test
	public void testAddAdminButtonSingleUserOnlyIsFalse() {

		HorizontalLayout layout = new HorizontalLayout();
		this.workbenchMainView.setIsSingleUserOnly("false");
		this.workbenchMainView.addAdminButton(layout);

		// Verify that Admin button is added in layout
		Assert.assertTrue(layout.getComponentIndex(this.workbenchMainView.getAdminButton()) != -1);

	}

	@Test
	public void testAddAdminButtonSingleUserOnlyIsTrue() {

		HorizontalLayout layout = new HorizontalLayout();
		this.workbenchMainView.setIsSingleUserOnly("true");
		this.workbenchMainView.addAdminButton(layout);

		// Verify that Admin button is not added in layout
		Assert.assertTrue(layout.getComponentIndex(this.workbenchMainView.getAdminButton()) == -1);

	}

	@Test
	public void testOnLoadOperations() {

		this.workbenchMainView.onLoadOperations();

		Mockito.verify(this.workbenchDataManager).incrementUserLogInCount(ADMIN_USER_ID);

	}

	@Test
	public void testDisplayCurrentProjectTitle() {

		final Label workbenchTitleLabel = new Label();

		this.workbenchMainView.setWorkbenchTitle(workbenchTitleLabel);

		this.workbenchMainView.displayCurrentProjectTitle();

		Assert.assertEquals(String.format("<h1>%s</h1>", PROJECT_NAME), workbenchTitleLabel.getValue());

	}

	@Test
	public void testDisplayCurrentProjectTitleNoProgramExists() {

		final Label workbenchTitleLabel = new Label();
		this.workbenchMainView.setWorkbenchTitle(workbenchTitleLabel);

		// Throw MiddlewareQueryException to simulate the case where no program exists yet in BMS.
		Mockito.when(this.contextUtil.getProjectInContext()).thenThrow(new MiddlewareQueryException(""));

		this.workbenchMainView.displayCurrentProjectTitle();

		Assert.assertEquals("", workbenchTitleLabel.getValue());

	}

	@Test
	public void testRefreshMemberDetailsPopup() {

		final String firstName = "firstname";
		final String lastName = "lastName";
		final String emailAddress = "emailAddress";

		this.workbenchMainView.refreshMemberDetailsPopup(firstName, lastName, emailAddress);

		final VerticalLayout memberDetailPopup = (VerticalLayout) this.workbenchMainView.getMemberButton().getComponentIterator().next();

		// Make sure that there are 2 components added in the view (Member detail label and signout button)
		Assert.assertEquals(2, memberDetailPopup.getComponentCount());

		final Label memberDetail = (Label) memberDetailPopup.getComponent(0);

		Assert.assertEquals(String.format("<h2>%s %s</h2><h4>%s</h4>", firstName, lastName, emailAddress), memberDetail.getValue());
		Assert.assertEquals(Label.CONTENT_XHTML, memberDetail.getContentMode());

		Assert.assertTrue(memberDetailPopup.getComponent(1) instanceof Button);

	}

	@Test
	public void testUpdateLabels() {

		final PopupButton popupButton = new PopupButton();

		this.workbenchMainView.setMemberButton(popupButton);
		this.workbenchMainView.updateLabels();

		Assert.assertEquals(
				"<span class='bms-header-btn2'><span>John Doe</span><span class='bms-fa-caret-down' style='padding: 0 10px 0 0'></span></span>",
				popupButton.getCaption());
	}
	
	@Test
	public void testInitializeActions() {
		this.workbenchMainView.initializeActions();
		
		final Collection<?> homeButtonListeneners = this.workbenchMainView.getHomeButton().getListeners(Button.ClickEvent.class);
		Assert.assertNotNull(homeButtonListeneners);
		Assert.assertTrue(homeButtonListeneners.size() == 1);
		Assert.assertTrue(homeButtonListeneners.iterator().next() instanceof HomeAction);
		
		final Collection<?> logoButtonListeners = this.workbenchMainView.getLogoBtn().getListeners(Button.ClickEvent.class);
		Assert.assertNotNull(logoButtonListeners);
		Assert.assertTrue(logoButtonListeners.size() == 1);
		Assert.assertTrue(logoButtonListeners.iterator().next() instanceof HomeAction);
		
		final Collection<?> adminButtonListeners = this.workbenchMainView.getAdminButton().getListeners(Button.ClickEvent.class);
		Assert.assertNotNull(adminButtonListeners);
		Assert.assertTrue(adminButtonListeners.size() == 1);
		
		final Collection<?> programAdminButtonListeners = this.workbenchMainView.getAddProgramButton().getListeners(Button.ClickEvent.class);
		Assert.assertNotNull(programAdminButtonListeners);
		Assert.assertTrue(programAdminButtonListeners.size() == 1);
		Assert.assertTrue(programAdminButtonListeners.iterator().next() instanceof OpenNewProjectAction);
		
		final Collection<?> helpButtonListeners = this.workbenchMainView.getHelpButton().getListeners(Button.ClickEvent.class);
		Assert.assertNotNull(helpButtonListeners);
		Assert.assertTrue(helpButtonListeners.size() == 1);
		
		final Collection<?> closeEventListeners = this.workbenchMainView.getListeners(Window.CloseEvent.class);
		Assert.assertNotNull(closeEventListeners);
		Assert.assertTrue(closeEventListeners.size() == 1);
	}

}

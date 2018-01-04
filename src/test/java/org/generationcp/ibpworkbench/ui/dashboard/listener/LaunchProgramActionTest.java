
package org.generationcp.ibpworkbench.ui.dashboard.listener;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class LaunchProgramActionTest {

	private static final Integer USER_ID = 100;

	@Mock
	private HttpSession httpSession;

	@Mock
	private HttpServletRequest request;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ProjectUserInfoDAO projectUserInfoDAO;

	@Mock
	private ToolUtil toolUtil;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private LaunchWorkbenchToolAction launchWorkbenchToolAction;

	@Mock
	private WorkbenchMainView window;

	@InjectMocks
	private LaunchProgramAction launchProgramAction;

	private Project selectedProgram;

	private ProjectUserInfo projectUserInfo;

	@Before
	public void setup() {

		this.setMockDependenciesToTestModule();

		// Setup test data
		this.selectedProgram = ProjectTestDataInitializer.createProject();
		this.projectUserInfo = new ProjectUserInfo();
		this.projectUserInfo.setProject(this.selectedProgram);
		this.projectUserInfo.setUserId(LaunchProgramActionTest.USER_ID);

		// Setup Mock objects to return
		Mockito.doReturn(this.projectUserInfoDAO).when(this.workbenchDataManager).getProjectUserInfoDao();
		Mockito.doReturn(this.projectUserInfo).when(this.projectUserInfoDAO).getByProjectIdAndUserId(Matchers.anyLong(), Matchers.anyInt());
		final User currentUser = new User(LaunchProgramActionTest.USER_ID);
		Mockito.doReturn(currentUser).when(this.contextUtil).getCurrentWorkbenchUser();
		Mockito.doReturn(LaunchProgramActionTest.USER_ID).when(this.contextUtil).getCurrentWorkbenchUserId();
		Mockito.when(this.request.getSession()).thenReturn(this.httpSession);
	}

	private void setMockDependenciesToTestModule() {
		this.launchProgramAction.setTransactionManager(this.transactionManager);
		this.launchProgramAction.setWorkbenchDataManager(this.workbenchDataManager);
		this.launchProgramAction.setLaunchWorkbenchToolAction(this.launchWorkbenchToolAction);
		this.launchProgramAction.setContextUtil(this.contextUtil);
		this.launchProgramAction.setRequest(this.request);
	}

	@Test
	public void testButtonClick() {
		// Selected program will always be present upon button click as it's the program of clicked button
		this.launchProgramAction = new LaunchProgramAction(this.selectedProgram);
		this.setMockDependenciesToTestModule();

		// Setup Mocks
		final ClickEvent clickEvent = Mockito.mock(ClickEvent.class);
		final Button component = Mockito.mock(Button.class);
		Mockito.doReturn(component).when(clickEvent).getComponent();
		Mockito.doReturn(this.window).when(component).getWindow();

		// Call method to test
		this.launchProgramAction.buttonClick(clickEvent);

		this.verifyMockInteractionsWhenOpeningProgram();
	}

	@Test
	public void testItemClick() {
		// Setup Mocks
		final ItemClickEvent itemClickEvent = Mockito.mock(ItemClickEvent.class);
		final Table table = Mockito.mock(Table.class);
		Mockito.doReturn(this.selectedProgram).when(itemClickEvent).getItemId();
		Mockito.doReturn(table).when(itemClickEvent).getComponent();
		Mockito.doReturn(this.window).when(table).getWindow();

		// Call method to test
		this.launchProgramAction.itemClick(itemClickEvent);

		this.verifyMockInteractionsWhenOpeningProgram();
	}

	@Test
	public void testOpenSelectedProgram() {
		// Call method to test
		this.launchProgramAction.openSelectedProgram(this.selectedProgram, this.window);

		this.verifyMockInteractionsWhenOpeningProgram();
	}

	@Test
	public void testUpdateProjectLastOpenedDate() {
		this.launchProgramAction.updateProjectLastOpenedDate(this.selectedProgram);
		this.verifyMockInteractionsForUpdatingProgram();
	}

	/*
	 * Verify from mock interactions that launch program processing was done on: 1. Check that session data was set, 2. Last open date for
	 * project user was updated and 3. Sidebar and workbench header updated 4. List Manager was launched
	 */
	private void verifyMockInteractionsWhenOpeningProgram() {
		Mockito.verify(this.window).addTitle(this.selectedProgram.getProjectName());
		this.verifyMockInteractionsForUpdatingProgram();
		Mockito.verify(this.launchWorkbenchToolAction, Mockito.times(1)).onAppLaunch(this.window);
	}

	private void verifyMockInteractionsForUpdatingProgram() {
		final Date currentDate = new Date();
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).saveOrUpdateProjectUserInfo(this.projectUserInfo);
		final Date userLastOpenDate = this.projectUserInfo.getLastOpenDate();
		Assert.assertEquals(currentDate.getYear(), userLastOpenDate.getYear());
		Assert.assertEquals(currentDate.getMonth(), userLastOpenDate.getMonth());
		Assert.assertEquals(currentDate.getDate(), userLastOpenDate.getDate());
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).mergeProject(this.selectedProgram);
		final Date lastOpenDate = this.selectedProgram.getLastOpenDate();
		Assert.assertEquals(currentDate.getYear(), lastOpenDate.getYear());
		Assert.assertEquals(currentDate.getMonth(), lastOpenDate.getMonth());
		Assert.assertEquals(currentDate.getDate(), lastOpenDate.getDate());
	}
}

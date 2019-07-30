package org.generationcp.ibpworkbench.ui.dashboard.listener;

import com.google.common.collect.Lists;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import junit.framework.Assert;
import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebarPresenter;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.workbench.PermissionDto;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.service.api.permission.PermissionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private ContextUtil contextUtil;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private LaunchWorkbenchToolAction launchWorkbenchToolAction;

	@Mock
	private WorkbenchMainView window;

	@Mock
	private PermissionServiceImpl permissionService;

	@InjectMocks
	private LaunchProgramAction launchProgramAction;

	@Mock
	private WorkbenchSidebar sidebar;

	@Mock
	private WorkbenchSidebarPresenter presenter;

	private Project selectedProgram;

	private ProjectUserInfo projectUserInfo;

	private List<PermissionDto> permissions;

	private List<WorkbenchSidebarCategory> categoriesByLinkIds;

	private List<WorkbenchSidebarCategory> workbenchSidebarCategoryList;
	private Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> workbenchSidebarCategoryListMap;

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
		this.setMocks();
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
		this.setMocks();
		// Call method to test
		this.launchProgramAction.itemClick(itemClickEvent);
		this.verifyMockInteractionsWhenOpeningProgram();
	}

	@Test
	public void testOpenSelectedProgram() {
		this.setMocks();
		// Call method to test
		this.launchProgramAction.openSelectedProgram(this.selectedProgram, this.window);
		this.verifyMockInteractionsWhenOpeningProgram();
	}

	@Test
	public void testUpdateProjectLastOpenedDate() {
		this.setMocks();
		this.launchProgramAction.updateProjectLastOpenedDate(this.selectedProgram);
		this.verifyMockInteractionsForUpdatingProgram();
	}

	/*
	 * Verify from mock interactions that launch program processing was done on:
	 * 1. Check that session data was set,
	 * 2. Last open date for project user was updated,
	 * 3. Sidebar and workbench header updated
	 * 4. List Manager was launched
	 */
	private void verifyMockInteractionsWhenOpeningProgram() {
		Mockito.verify(this.window).addTitle(this.selectedProgram.getProjectName());

		// Verify session attribute was set
		final ArgumentCaptor<Object> contextInfoCaptor = ArgumentCaptor.forClass(Object.class);
		Mockito.verify(this.httpSession).setAttribute(Matchers.eq(ContextConstants.SESSION_ATTR_CONTEXT_INFO), contextInfoCaptor.capture());
		final ContextInfo contextInfo = (ContextInfo) contextInfoCaptor.getValue();
		Assert.assertEquals(USER_ID.intValue(), contextInfo.getLoggedInUserId().intValue());
		Assert.assertEquals(this.selectedProgram.getProjectId(), contextInfo.getSelectedProjectId());
		Assert.assertNull(contextInfo.getAuthToken());

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

	private void setMocks() {
		this.sidebar = new WorkbenchSidebar();
		this.sidebar.setPresenter(this.presenter);
		this.workbenchSidebarCategoryListMap = new HashMap<>();
		this.workbenchSidebarCategoryList = Lists.newArrayList();
		final PermissionDto permissionDto = new PermissionDto();
		permissionDto.setId(1);
		permissionDto.setDescription("Test");
		permissionDto.setName("TestName");
		permissionDto.setParentId(null);
		permissionDto.setWorkbenchCategoryLinkId(1);
		this.permissions = Lists.newArrayList(permissionDto);

		final WorkbenchSidebarCategoryLink link = new WorkbenchSidebarCategoryLink();
		final WorkbenchSidebarCategory workbenchSidebarCategory = new WorkbenchSidebarCategory();
		link.setSidebarCategoryLinkId(1);
		link.setWorkbenchSidebarCategory(workbenchSidebarCategory);
		link.setSidebarLinkTitle("Title");
		link.setSidebarLinkName("Name");

		workbenchSidebarCategory.setSidebarCategoryName("Category");
		workbenchSidebarCategory.setSidebarCategorylabel("Label");
		workbenchSidebarCategory.setSidebarCategoryId(1);
		workbenchSidebarCategory.setWorkbenchSidebarCategoryLinks(Lists.newArrayList(link));
		this.categoriesByLinkIds = Lists.newArrayList(workbenchSidebarCategory);

		this.window.setSidebar(new WorkbenchSidebar());
		this.launchProgramAction.setWindow(this.window);

		this.launchProgramAction.setPermissionService(this.permissionService);
		Mockito.when(this.permissionService.getPermissionLinks(Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt()))
			.thenReturn(this.permissions);
		Mockito.when(this.window.getSidebar()).thenReturn(this.sidebar);
	}
}

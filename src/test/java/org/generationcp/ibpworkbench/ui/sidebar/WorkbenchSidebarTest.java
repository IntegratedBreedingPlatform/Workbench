package org.generationcp.ibpworkbench.ui.sidebar;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchSidebarTest {

	private static final Integer USER_ID = 100;

	@Mock
	private Component parent;

	@Mock
	private Window window;

	@Mock
	private Tree sidebarTree;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private UserService userService;

	private WorkbenchSidebar workbenchSidebar;

	private Project selectedProgram;

	private ProjectUserInfo projectUserInfo;

	@Before
	public void init() {

		MockitoAnnotations.initMocks(this);

		workbenchSidebar = new WorkbenchSidebar();
		workbenchSidebar.setSidebarTree(sidebarTree);
		workbenchSidebar.setContextUtil(contextUtil);
		workbenchSidebar.setTransactionManager(transactionManager);
		workbenchSidebar.setWorkbenchDataManager(workbenchDataManager);
		workbenchSidebar.setUserService(userService);

		// Setup test data
		this.selectedProgram = ProjectTestDataInitializer.createProject();
		this.projectUserInfo = new ProjectUserInfo();
		this.projectUserInfo.setProject(this.selectedProgram);
		this.projectUserInfo.setUser(new WorkbenchUser(WorkbenchSidebarTest.USER_ID));

		// Setup Mock objects to return
		Mockito.doReturn(WorkbenchSidebarTest.USER_ID).when(this.contextUtil).getCurrentWorkbenchUserId();
		Mockito.doReturn(this.selectedProgram).when(this.contextUtil).getProjectInContext();
		Mockito.doReturn(this.projectUserInfo).when(this.userService).getProjectUserInfoByProjectIdAndUserId(Matchers.anyLong(), Matchers.anyInt());

	}

	@Test
	public void testTreeClickListener() {

		final ItemClickEvent event = mock(ItemClickEvent.class);
		final ActionListener actionListener = mock(ActionListener.class);

		final WorkbenchSidebar.TreeItem treeItem = new WorkbenchSidebar().new TreeItem("1", "caption", null);

		when(event.getComponent()).thenReturn(parent);
		when(parent.getWindow()).thenReturn(window);

		when(event.getItemId()).thenReturn(treeItem);

		final WorkbenchSidebar mockWorkbenchSidebar = mock(WorkbenchSidebar.class);
		when(mockWorkbenchSidebar.doCollapse(treeItem)).thenReturn(false);
		when(mockWorkbenchSidebar.getLinkActions(treeItem.getId(), selectedProgram)).thenReturn(actionListener);

		final WorkbenchSidebar.TreeItemClickListener listener = workbenchSidebar.new TreeItemClickListener(mockWorkbenchSidebar);
		listener.itemClick(event);

		verify(mockWorkbenchSidebar).updateProjectLastOpenedDate();
		verify(actionListener).doAction(window, "/" + treeItem.getId(), true);

	}

	@Test
	public void testUpdateProjectLastOpenedDate() {
		this.workbenchSidebar.updateProjectLastOpenedDate();

		final Date currentDate = new Date();
		Mockito.verify(this.userService, Mockito.times(1)).saveOrUpdateProjectUserInfo(this.projectUserInfo);
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

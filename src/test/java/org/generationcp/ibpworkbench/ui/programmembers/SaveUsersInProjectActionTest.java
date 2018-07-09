
package org.generationcp.ibpworkbench.ui.programmembers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.data.initializer.UserTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import junit.framework.Assert;

public class SaveUsersInProjectActionTest {

	private static final Integer USER_ID = 100;

	@Mock
	private ProgramService programService;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ProjectUserInfoDAO projectUserInfoDao;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private TwinTableSelect<WorkbenchUser> twinTable;

	@Mock
	private ClickEvent clickEvent;

	@Mock
	private HorizontalLayout component;

	@Mock
	private Window window;

	@InjectMocks
	private SaveUsersInProjectAction saveUsersInProjectAction;

	private Project project;

	private Set<WorkbenchUser> userList;

	private ProjectUserInfo projectUserInfo;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.project = ProjectTestDataInitializer.createProject();
		this.saveUsersInProjectAction = new SaveUsersInProjectAction(this.project, this.twinTable);
		this.saveUsersInProjectAction.setTransactionManager(this.transactionManager);
		this.saveUsersInProjectAction.setWorkbenchDataManager(this.workbenchDataManager);
		this.saveUsersInProjectAction.setProgramService(this.programService);

		this.projectUserInfo = new ProjectUserInfo();
		this.projectUserInfo.setProject(this.project);
		this.projectUserInfo.setUserId(SaveUsersInProjectActionTest.USER_ID);

		this.userList = new HashSet<>();
		final WorkbenchUser user1 = UserTestDataInitializer.createWorkbenchUser();
		user1.setUserid(1);
		this.userList.add(user1);
		final WorkbenchUser user2 = UserTestDataInitializer.createWorkbenchUser();
		user2.setUserid(2);
		this.userList.add(user2);

		Mockito.doReturn(this.userList).when(this.twinTable).getValue();
		Mockito.doReturn(this.component).when(this.clickEvent).getComponent();
		Mockito.doReturn(this.window).when(this.component).getWindow();
		Mockito.doReturn(this.projectUserInfoDao).when(this.workbenchDataManager).getProjectUserInfoDao();
		// Set User1 as current existing member of program already
		Mockito.doReturn(this.projectUserInfo).when(this.projectUserInfoDao).getByProjectIdAndUserId(Matchers.anyLong(),
				Matchers.eq(user1.getUserid()));
	}

	@Test
	public void testButtonClick() {
		Mockito.when(this.workbenchDataManager.getActiveUserIDsByProjectId(Matchers.anyLong())).thenReturn(Arrays.asList(1,2,3));
		this.saveUsersInProjectAction.buttonClick(this.clickEvent);

		final int numberOfUsers = this.userList.size();
		Mockito.verify(this.workbenchDataManager, Mockito.times(numberOfUsers)).getProjectUserInfoByProjectIdAndUserId(Matchers.anyLong(),
				Matchers.anyInt());
		// Expecting to save only the 2nd user as the 1st user is already saved as a member
		Mockito.verify(this.workbenchDataManager, Mockito.times(numberOfUsers))
				.saveOrUpdateProjectUserInfo(Matchers.any(ProjectUserInfo.class));
		Mockito.verify(this.programService).saveWorkbenchUserToCropUserMapping(Matchers.eq(this.project), Matchers.eq(this.userList));
		Mockito.verify(this.workbenchDataManager).getActiveUserIDsByProjectId(Matchers.anyLong());
		Mockito.verify(this.workbenchDataManager).getProjectUserInfoByProjectIdAndUserIds(Matchers.anyLong(), Matchers.anyList());
		Mockito.verify(this.workbenchDataManager).deleteProjectUserInfos(Matchers.anyList());
	}
	
	@Test
	public void testGetRemovedUserIds() {
		final List<Integer> activeUserIds = Arrays.asList(1, 2);
		final Collection<WorkbenchUser> userList = Arrays.asList(new WorkbenchUser(1));
		final List<Integer> removedUserIds = this.saveUsersInProjectAction.getRemovedUserIds(activeUserIds, userList);
		Assert.assertEquals(1, removedUserIds.size());
		Assert.assertEquals("2", removedUserIds.get(0).toString());
	}

}

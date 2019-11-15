
package org.generationcp.ibpworkbench.ui.programmembers;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashSet;
import java.util.Set;

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
		this.saveUsersInProjectAction.setProgramService(this.programService);

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
	}

	@Test
	public void testButtonClick() {
		this.saveUsersInProjectAction.buttonClick(this.clickEvent);
		Mockito.verify(this.programService).updateMembersProjectUserInfo(userList, project);
	}
}

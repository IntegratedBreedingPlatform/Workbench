
package org.generationcp.ibpworkbench.actions;

import java.util.HashSet;
import java.util.Set;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import org.junit.Assert;

@RunWith(MockitoJUnitRunner.class)
public class SaveNewProjectAddUserActionTest {

	@Mock
	private WorkbenchUserService workbenchUserService;

	@Mock
	private Form userAccountForm;
	
	@Mock
	private TwinTableSelect<WorkbenchUser> membersSelect;
	
	@Mock
	private WorkbenchUser user;
	
	@Mock
	private BeanItem<UserAccountModel> bean;
	
	@Mock
	private Component component;
	
	@Mock
	private Window window;
	
	@Mock
	private Window parentWindow;
	
	@Mock
	private ClickEvent clickEvent;

	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private Button button;

	@InjectMocks
	private SaveNewProjectAddUserAction action;
	
	private UserAccountModel userAccount;
	
	private Project project;
	

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.setupTestUserAccount();
		this.project = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();

		this.action = new SaveNewProjectAddUserAction(this.userAccountForm, this.membersSelect);
		this.action.setWorkbenchUserService(this.workbenchUserService);
		this.action.setContextUtil(this.contextUtil);
		Mockito.doReturn(this.bean).when(this.userAccountForm).getItemDataSource();
		Mockito.doReturn(this.userAccount).when(this.bean).getBean();
		Mockito.doReturn(this.button).when(this.clickEvent).getButton();
		Mockito.doReturn(this.window).when(this.button).getWindow();
		Mockito.doReturn(this.parentWindow).when(this.window).getParent();
	}

	@Test
	public void testSaveUserAccount() {
		this.action.saveUserAccount(this.userAccount, membersSelect);

		Mockito.verify(this.workbenchUserService).saveNewUserAccount(this.userAccount);
		Assert.assertEquals("The user must be added to the TwinTableSelect UI", 1, this.membersSelect.getValue().size());
	}

	private void setupTestUserAccount() {
		this.userAccount = new UserAccountModel();
		Set<WorkbenchUser> userSet = new HashSet<>();
		userSet.add(this.user);

		Mockito.when(this.workbenchUserService.saveNewUserAccount(this.userAccount)).thenReturn(this.user);
		Mockito.doNothing().when(this.membersSelect).addItem(this.user);
		Mockito.when(this.membersSelect.getValue()).thenReturn(userSet);
	}
	
	@Test
	public void testButtonClick() {
		this.action.buttonClick(this.clickEvent);
		
		Mockito.verify(this.userAccountForm).commit();
		Mockito.verify(this.workbenchUserService).saveNewUserAccount(this.userAccount);
		Mockito.verify(this.contextUtil).logProgramActivity("Program Member",
				"Added a new user (" + this.userAccount.getUsername() + ") to " + this.project.getProjectName());
		Mockito.verify(this.parentWindow).removeWindow(this.window);
	}
}

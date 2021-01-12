package org.generationcp.ibpworkbench.ui.window;

import org.junit.Assert;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.actions.ChangePasswordAction;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChangePasswordWindowTest {

	public static final String USER_NAME = "UserName";
	@Mock
	private ContextUtil contextUtil;

	private ChangePasswordWindow changePasswordWindow;

	@Before
	public void init() throws Exception {

		changePasswordWindow = new ChangePasswordWindow();
		changePasswordWindow.setContextUtil(contextUtil);
		changePasswordWindow.initializeComponents();

		final WorkbenchUser user = new WorkbenchUser();
		user.setName(USER_NAME);
		when(contextUtil.getCurrentWorkbenchUser()).thenReturn(user);

	}

	@Test
	public void testInitializeActions() {

		changePasswordWindow.initializeActions();

		Assert.assertNotNull(changePasswordWindow.getSaveButton().getListeners(ChangePasswordAction.class));
		Assert.assertNotNull(changePasswordWindow.getSaveButton().getListeners(ChangePasswordWindow.RemoveWindowListener.class));
	}

}

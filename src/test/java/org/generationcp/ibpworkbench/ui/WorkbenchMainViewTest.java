
package org.generationcp.ibpworkbench.ui;

import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchMainViewTest {

	@InjectMocks
	private WorkbenchMainView workbenchMainView;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Test
	public void testUpdateUserInfoIfNecessary() {
		final User user = Mockito.mock(User.class);
		UserInfo userInfo = Mockito.mock(UserInfo.class);
		Mockito.when(this.workbenchDataManager.getUserInfo(Matchers.anyInt())).thenReturn(userInfo);

		Mockito.when(userInfo.getLoginCount()).thenReturn(0);
		this.workbenchMainView.updateUserInfoIfNecessary(user);
		Mockito.verify(userInfo, Mockito.times(1)).setLoginCount(1);

		Mockito.when(userInfo.getLoginCount()).thenReturn(1);
		this.workbenchMainView.updateUserInfoIfNecessary(user);
		Mockito.verify(userInfo, Mockito.times(1)).setLoginCount(1);

		userInfo = null;
		Mockito.when(this.workbenchDataManager.getUserInfo(Matchers.anyInt())).thenReturn(userInfo);
		this.workbenchMainView.updateUserInfoIfNecessary(user);
		Mockito.verify(this.workbenchDataManager, Mockito.times(2)).insertOrUpdateUserInfo(Matchers.any(UserInfo.class));
	}
}

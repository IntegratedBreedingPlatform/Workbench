
package org.generationcp.ibpworkbench;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RunWith(MockitoJUnitRunner.class)
public class IBPWorkbenchApplicationTest {

	@Mock
	private SecurityContext currentContext;

	@Mock
	private LogoutHandler rememberMeServices;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private Authentication auth;

	@InjectMocks
	private IBPWorkbenchApplication workbenchApp;

	@Test
	public void testLogout() throws Exception {
		IBPWorkbenchApplication fakeWorkbenchApp = Mockito.spy(this.workbenchApp);

		Mockito.doNothing().when(this.rememberMeServices).logout(this.request, this.response, this.auth);

		Mockito.when(this.currentContext.getAuthentication()).thenReturn(this.auth);
		Mockito.doReturn(this.currentContext).when(fakeWorkbenchApp).getCurrentSecurityContext();

		fakeWorkbenchApp.logout();

		Mockito.verify(this.rememberMeServices).logout(this.request, this.response, this.auth);
		Mockito.verify(this.currentContext).setAuthentication(null);
	}

}

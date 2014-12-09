package org.generationcp.ibpworkbench;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

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
		IBPWorkbenchApplication fakeWorkbenchApp = spy(workbenchApp);

		doNothing().when(rememberMeServices).logout(request, response, auth);

		when(currentContext.getAuthentication()).thenReturn(auth);
		doReturn(currentContext).when(fakeWorkbenchApp).getCurrentSecurityContext();

		fakeWorkbenchApp.logout();

		verify(rememberMeServices).logout(request, response, auth);
		verify(currentContext).setAuthentication(null);
	}

}
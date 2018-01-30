package org.generationcp.ibpworkbench;

import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Window;
import junit.framework.Assert;
import org.generationcp.ibpworkbench.common.WebClientInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;

@RunWith(MockitoJUnitRunner.class)
public class IBPWorkbenchApplicationTest {

	public static final String MY_WINDOW = "myWindow";

	@Mock
	private Window mainWindow;

	@Mock
	private WebBrowser webBrowser;

	@Mock
	private Window childWindow;

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

	@Mock
	private WebClientInfo webClientInfo;

	@InjectMocks
	private IBPWorkbenchApplication workbenchApp = Mockito.spy(new IBPWorkbenchApplication());

	@Before
	public void init() throws MalformedURLException {

		SecurityContextHolder.setContext(currentContext);
		Mockito.when(workbenchApp.isRunning()).thenReturn(true);
		Mockito.when(childWindow.getName()).thenReturn(MY_WINDOW);
		this.workbenchApp.setMainWindow(mainWindow);
		this.workbenchApp.addWindow(childWindow);

		Mockito.when(mainWindow.getTerminal()).thenReturn(webBrowser);

	}

	@Test
	public void testLogout() throws Exception {

		Mockito.doNothing().when(this.rememberMeServices).logout(this.request, this.response, this.auth);

		Mockito.when(this.currentContext.getAuthentication()).thenReturn(this.auth);

		this.workbenchApp.logout();

		Mockito.verify(this.rememberMeServices).logout(this.request, this.response, this.auth);
		Mockito.verify(this.currentContext).setAuthentication(null);
	}

	@Test
	public void testGetWindow() {

		this.workbenchApp.getWindow(MY_WINDOW);

		Mockito.verify(this.webClientInfo).setWebBrowser(Mockito.any(WebBrowser.class));
		Mockito.verify(this.childWindow)
				.executeJavaScript(Mockito.eq(String.format(IBPWorkbenchApplication.SCRIPT, IBPWorkbenchApplication.PREFETCH_SCRIPT)));

		Assert.assertTrue(this.workbenchApp.isScriptsRun());

	}

}

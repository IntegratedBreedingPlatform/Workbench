
package org.generationcp.browser.study.listeners;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.constant.DefaultGermplasmStudyBrowserPath;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

@RunWith(MockitoJUnitRunner.class)
public class ViewStudyDetailsButtonClickListenerTest {

	private static final String STUDY_NAME = "Dummy Study";

	private static final int STUDY_ID = 1;

	@Mock
	private WorkbenchDataManager workbenchManager;

	@Mock
	private SessionData sessionData;

	@Mock
	private HttpServletRequest request;

	@InjectMocks
	private final ViewStudyDetailsButtonClickListener viewStudyListener = Mockito.spy(new ViewStudyDetailsButtonClickListener(
			ViewStudyDetailsButtonClickListenerTest.STUDY_ID, ViewStudyDetailsButtonClickListenerTest.STUDY_NAME));

	@Before
	public void setUp() {
		Mockito.when(this.sessionData.getWorkbenchContextParameters()).thenReturn("&loggedinUserId=1&selectedProjectId=1");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));

		Mockito.when(this.request.getScheme()).thenReturn("http");
		Mockito.when(this.request.getServerName()).thenReturn("my-host");
		Mockito.when(this.request.getServerPort()).thenReturn(18080);
	}

	@Test
	public void testLaunchStudyDetailsWindow() throws MiddlewareQueryException {

		Mockito.doReturn(null).when(this.workbenchManager).getToolWithName(Matchers.anyString());

		ClickEvent event = Mockito.mock(ClickEvent.class);
		Button mockButton = Mockito.mock(Button.class);
		Window parentWindow = new Window();
		Mockito.doReturn(parentWindow).when(mockButton).getWindow();
		Mockito.doReturn(mockButton).when(event).getComponent();

		this.viewStudyListener.buttonClick(event);

		// Assert state
		Set<Window> childWindows = parentWindow.getChildWindows();
		Assert.assertNotNull(childWindows);
		Assert.assertTrue("Should have one sub-window", !childWindows.isEmpty());

		Window studyWindow = childWindows.iterator().next();
		Assert.assertNotNull(studyWindow.getContent());
		Assert.assertEquals("Study Details window caption should be", "Study Information: "
				+ ViewStudyDetailsButtonClickListenerTest.STUDY_NAME, studyWindow.getCaption());

		Embedded embeddedResource = (Embedded) studyWindow.getContent().getComponentIterator().next();
		ExternalResource externalResource = (ExternalResource) embeddedResource.getSource();
		String expectedStudyURL =
				"http://my-host:18080/" + DefaultGermplasmStudyBrowserPath.STUDY_BROWSER_LINK
				+ ViewStudyDetailsButtonClickListenerTest.STUDY_ID + "?restartApplication&loggedinUserId=1&selectedProjectId=1";
		Assert.assertEquals("URL to StudyBrowser resource should be", expectedStudyURL, externalResource.getURL());

	}

}

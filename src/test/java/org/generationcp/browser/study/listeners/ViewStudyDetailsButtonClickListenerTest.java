package org.generationcp.browser.study.listeners;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static org.mockito.Mockito.*;

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
	private ViewStudyDetailsButtonClickListener viewStudyListener = spy(
			new ViewStudyDetailsButtonClickListener(STUDY_ID, STUDY_NAME));

	@Before
	public void setUp() {
		when(sessionData.getWorkbenchContextParameters())
				.thenReturn("&loggedinUserId=1&selectedProjectId=1");
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(request.getScheme()).thenReturn("http");
		when(request.getServerName()).thenReturn("my-host");
		when(request.getServerPort()).thenReturn(18080);
	}

	@Test
	public void testLaunchStudyDetailsWindow() throws MiddlewareQueryException {

		doReturn(null).when(workbenchManager).getToolWithName(Matchers.anyString());

		ClickEvent event = Mockito.mock(ClickEvent.class);
		Button mockButton = Mockito.mock(Button.class);
		Window parentWindow = new Window();
		doReturn(parentWindow).when(mockButton).getWindow();
		doReturn(mockButton).when(event).getComponent();

		viewStudyListener.buttonClick(event);

		// Assert state
		Set<Window> childWindows = parentWindow.getChildWindows();
		Assert.assertNotNull(childWindows);
		Assert.assertTrue("Should have one sub-window", !childWindows.isEmpty());

		Window studyWindow = childWindows.iterator().next();
		Assert.assertNotNull(studyWindow.getContent());
		Assert.assertEquals("Study Details window caption should be",
				"Study Information: " + STUDY_NAME, studyWindow.getCaption());

		Embedded embeddedResource = (Embedded)
				studyWindow.getContent().getComponentIterator().next();
		ExternalResource externalResource = (ExternalResource) embeddedResource.getSource();
		String expectedStudyURL =
				"http://my-host:18080/" + DefaultGermplasmStudyBrowserPath.STUDY_BROWSER_LINK
						+ STUDY_ID + "?restartApplication&loggedinUserId=1&selectedProjectId=1";
		Assert.assertEquals("URL to StudyBrowser resource should be", expectedStudyURL,
				externalResource.getURL());

	}

}

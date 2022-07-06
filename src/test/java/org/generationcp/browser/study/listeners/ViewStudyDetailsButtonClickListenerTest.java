package org.generationcp.browser.study.listeners;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;
import org.generationcp.commons.constant.DefaultGermplasmStudyBrowserPath;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.api.tool.ToolService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class ViewStudyDetailsButtonClickListenerTest {

	private static final String STUDY_NAME = "Dummy Study";

	private static final int STUDY_ID = 1;
	public static final int USER_ID = 12;
	public static final long PROJECT_ID = 123l;

	@Mock
	private ToolService toolService;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private HttpServletRequest request;

	@InjectMocks
	private final ViewStudyDetailsButtonClickListener viewStudyListener = Mockito.spy(
		new ViewStudyDetailsButtonClickListener(ViewStudyDetailsButtonClickListenerTest.STUDY_ID,
			ViewStudyDetailsButtonClickListenerTest.STUDY_NAME));

	@Before
	public void setUp() {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));

		Mockito.when(this.request.getScheme()).thenReturn("http");
		Mockito.when(this.request.getServerName()).thenReturn("my-host");
		Mockito.when(this.request.getServerPort()).thenReturn(18080);

		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(USER_ID);
		final Project project = new Project();
		project.setProjectId(PROJECT_ID);
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(project);

		SecurityContextHolder.setContext(this.securityContext);
	}

	@Test
	public void testLaunchStudyDetailsWindow() throws MiddlewareQueryException {

		Mockito.doReturn(null).when(this.toolService).getToolWithName(Matchers.anyString());

		final ClickEvent event = Mockito.mock(ClickEvent.class);
		final Button mockButton = Mockito.mock(Button.class);
		final Window parentWindow = new Window();
		Mockito.doReturn(parentWindow).when(mockButton).getWindow();
		Mockito.doReturn(mockButton).when(event).getComponent();

		this.viewStudyListener.buttonClick(event);

		// Assert state
		final Set<Window> childWindows = parentWindow.getChildWindows();
		Assert.assertNotNull(childWindows);
		Assert.assertFalse("Should have one sub-window", childWindows.isEmpty());

		final Window studyWindow = childWindows.iterator().next();
		Assert.assertNotNull(studyWindow.getContent());
		Assert.assertEquals("Study Details window caption should be",
			"Study Information: " + ViewStudyDetailsButtonClickListenerTest.STUDY_NAME, studyWindow.getCaption());

		final Embedded embeddedResource = (Embedded) studyWindow.getContent().getComponentIterator().next();
		final ExternalResource externalResource = (ExternalResource) embeddedResource.getSource();
		final String expectedStudyURL = "http://my-host:18080/" + DefaultGermplasmStudyBrowserPath.STUDY_BROWSER_LINK
			+ ViewStudyDetailsButtonClickListenerTest.STUDY_ID
			+ "?restartApplication&loggedInUserId=12&selectedProjectId=123";
		Assert.assertEquals("URL to StudyBrowser resource should be", expectedStudyURL, externalResource.getURL());

	}

}

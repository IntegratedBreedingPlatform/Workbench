package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.ibpworkbench.ContentWindow;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.service.AppLauncherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LaunchWorkbenchToolActionTest {

	public static final String SAMPLE_RETURN_URL = "sample_return_url";
	private ToolEnum sampleTool = ToolEnum.BREEDING_VIEW;

	@Mock
	private  AppLauncherService appLauncherService;

	@Mock
	private ContentWindow window;

	private int idParam = 1;

	@InjectMocks
	private LaunchWorkbenchToolAction launchWorkbenchToolAction = spy(new LaunchWorkbenchToolAction(sampleTool,idParam));

	@Test
	public void testDoAction() throws Exception {
		String urlFragment = "/" + sampleTool.getToolName();
		doNothing().when(launchWorkbenchToolAction).onAppLaunch(window);

		launchWorkbenchToolAction.doAction(window,urlFragment,true);

		verify(launchWorkbenchToolAction,times(1)).onAppLaunch(window);
	}

	@Test
	public void testOnAppLaunch() throws Exception, AppLaunchException {
		when(appLauncherService.launchTool(sampleTool.getToolName(),idParam)).thenReturn(
				SAMPLE_RETURN_URL);

		launchWorkbenchToolAction.onAppLaunch(window);

		verify(appLauncherService, times(1)).launchTool(sampleTool.getToolName(), idParam);
		verify(window,times(1)).showContent(SAMPLE_RETURN_URL);

	}
}
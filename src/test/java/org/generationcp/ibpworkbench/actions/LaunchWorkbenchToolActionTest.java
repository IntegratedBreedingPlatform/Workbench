
package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.ibpworkbench.ContentWindow;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.service.AppLauncherService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LaunchWorkbenchToolActionTest {

	public static final String SAMPLE_RETURN_URL = "sample_return_url";
	private final ToolEnum sampleTool = ToolEnum.BREEDING_VIEW;

	@Mock
	private AppLauncherService appLauncherService;

	@Mock
	private ContentWindow window;

	private final int idParam = 1;

	@InjectMocks
	private final LaunchWorkbenchToolAction launchWorkbenchToolAction = Mockito.spy(new LaunchWorkbenchToolAction(this.sampleTool,
			this.idParam));

	@Test
	public void testDoAction() throws Exception {
		String urlFragment = "/" + this.sampleTool.getToolName();
		Mockito.doNothing().when(this.launchWorkbenchToolAction).onAppLaunch(this.window);

		this.launchWorkbenchToolAction.doAction(this.window, urlFragment, true);

		Mockito.verify(this.launchWorkbenchToolAction, Mockito.times(1)).onAppLaunch(this.window);
	}

	@Test
	public void testOnAppLaunch() throws Exception, AppLaunchException {
		Mockito.when(this.appLauncherService.launchTool(this.sampleTool.getToolName(), this.idParam)).thenReturn(
				LaunchWorkbenchToolActionTest.SAMPLE_RETURN_URL);

		this.launchWorkbenchToolAction.onAppLaunch(this.window);

		Mockito.verify(this.appLauncherService, Mockito.times(1)).launchTool(this.sampleTool.getToolName(), this.idParam);
		Mockito.verify(this.window, Mockito.times(1)).showContent(LaunchWorkbenchToolActionTest.SAMPLE_RETURN_URL);

	}
}

package org.generationcp.ibpworkbench.service;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.tomcat.TomcatUtil;
import org.generationcp.ibpworkbench.util.tomcat.WebAppStatusInfo;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppLauncherServiceTest {

	public static final String SCHEME = "http";
	public static final String HOST_NAME = "host-name";
	public static final int PORT = 18080;
	public static final String SAMPLE_BASE_URL = "somewhere/out/there";
	public static final int ID_PARAM = 5;
	public static final String WORKBENCH_CONTEXT_PARAMS = "&loggedinUserId=1&selectedProjectId=1";
	public static final String RESTART_URL_STR = "?restartApplication";
	@Mock
	HttpServletRequest request;
	@Mock
	private WorkbenchDataManager workbenchDataManager;
	@Mock
	private SessionData sessionData;
	@Mock
	private ToolUtil toolUtil;
	@Mock
	private TomcatUtil tomcatUtil;
	@Mock
	private Properties workbenchProperties;
	@InjectMocks
	private AppLauncherService appLauncherService = spy(new AppLauncherService());

	@Before
	public void setUp() throws Exception {
		when(sessionData.getWorkbenchContextParameters()).thenReturn(
				WORKBENCH_CONTEXT_PARAMS);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(request.getScheme()).thenReturn(SCHEME);
		when(request.getServerName()).thenReturn(HOST_NAME);
		when(request.getServerPort()).thenReturn(PORT);
	}

	@Test
	public void testLaunchTool() throws Exception, AppLaunchException {
		// case 1: web tool
		Tool aWebTool = new Tool();
		aWebTool.setToolName(ToolEnum.BM_LIST_MANAGER.getToolName());
		aWebTool.setToolType(ToolType.WEB);

		// case 2: gdms
		Tool gdmsTool = new Tool();
		gdmsTool.setToolName(ToolEnum.GDMS.getToolName());
		gdmsTool.setToolType(ToolType.WEB_WITH_LOGIN);

		// case 3: NATIVE
		Tool nativeTool = new Tool();
		nativeTool.setToolName(ToolEnum.BREEDING_PLANNER.getToolName());
		nativeTool.setToolType(ToolType.NATIVE);

		when(workbenchDataManager.getToolWithName(ToolEnum.BM_LIST_MANAGER.getToolName())).thenReturn(aWebTool);
		when(workbenchDataManager.getToolWithName(ToolEnum.GDMS.getToolName())).thenReturn(gdmsTool);
		when(workbenchDataManager.getToolWithName(ToolEnum.BREEDING_PLANNER.getToolName())).thenReturn(nativeTool);

		doNothing().when(appLauncherService).updateToolConfiguration(any(Tool.class));
		doNothing().when(appLauncherService).updateGermplasmStudyBrowserConfigurationIfNecessary(
				any(Tool.class));
	 	doNothing().when(sessionData).logProgramActivity(anyString(),anyString());

		doNothing().when(appLauncherService).launchNativeapp(any(Tool.class));
		doReturn("/result").when(appLauncherService).launchWebappWithLogin(any(Tool.class));
		doReturn("/result").when(appLauncherService).launchWebapp(any(Tool.class), any(Integer.class));

		// the tests itself
		appLauncherService.launchTool(ToolEnum.BREEDING_PLANNER.getToolName(),null);
		verify(appLauncherService).launchNativeapp(nativeTool);

		appLauncherService.launchTool(ToolEnum.BM_LIST_MANAGER.getToolName(),null);
		verify(appLauncherService).launchWebapp(aWebTool,null);

		appLauncherService.launchTool(ToolEnum.GDMS.getToolName(),null);
		verify(appLauncherService).launchWebappWithLogin(gdmsTool);
	}

	@Test
	public void testUpdateToolConfiguration() throws Exception, AppLaunchException {
		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.BM_LIST_MANAGER.getToolName());
		aWebTool.setPath(SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);

		when(sessionData.getLastOpenedProject()).thenReturn(mock(Project.class));
		when(toolUtil
				.updateToolConfigurationForProject(aWebTool, sessionData.getLastOpenedProject()))
				.thenReturn(false);

		WebAppStatusInfo statusInfo = mock(WebAppStatusInfo.class);
		when(tomcatUtil.getWebAppStatus()).thenReturn(statusInfo);
		when(statusInfo.isDeployed(anyString())).thenReturn(false);
		when(statusInfo.isRunning(anyString())).thenReturn(false);

		// run case 1 , webapp is not yet deployed
		appLauncherService.updateToolConfiguration(aWebTool);

		verify(tomcatUtil,times(1)).deployLocalWar("/somewhere", "somewhere");

		// run case 2, webapp is deployed but not running
		when(statusInfo.isDeployed(anyString())).thenReturn(true);
		appLauncherService.updateToolConfiguration(aWebTool);

		verify(tomcatUtil,times(1)).startWebApp("/somewhere");

		// run case 3, webapp is deployed and running, and tool is a GDMS
		when(toolUtil
				.updateToolConfigurationForProject(aWebTool,sessionData.getLastOpenedProject())).thenReturn(true);

		aWebTool.setToolName(ToolEnum.GDMS.getToolName());
		aWebTool.setToolType(ToolType.WEB_WITH_LOGIN);

		when(statusInfo.isDeployed(anyString())).thenReturn(true);
		when(statusInfo.isRunning(anyString())).thenReturn(true);

		appLauncherService.updateToolConfiguration(aWebTool);

		verify(tomcatUtil,times(1)).reloadWebApp("/somewhere");
	}

	@Test
	public void testUpdateGermplasmStudyBrowserConfigurationIfNecessary()
			throws Exception, AppLaunchException {
		Tool gsbTool = new Tool();
		gsbTool.setToolName(ToolEnum.GERMPLASM_BROWSER.getToolName());
		when(workbenchDataManager.getToolWithName(ToolEnum.GERMPLASM_BROWSER.getToolName())).thenReturn(gsbTool);

		doNothing().when(appLauncherService).updateToolConfiguration(gsbTool);

		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.BM_LIST_MANAGER.getToolName());
		aWebTool.setPath(SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);

		appLauncherService.updateGermplasmStudyBrowserConfigurationIfNecessary(aWebTool);

		verify(appLauncherService).updateToolConfiguration(gsbTool);
	}

	@Test
	public void testLaunchNativeapp() throws Exception, AppLaunchException {
		Tool aNativeTool = new Tool();

		// for vaadin type params with a dash
		aNativeTool.setToolName(ToolEnum.BREEDING_VIEW.getToolName());
		aNativeTool.setPath(SAMPLE_BASE_URL);
		aNativeTool.setToolType(ToolType.NATIVE);

		doNothing().when(toolUtil).closeNativeTool(aNativeTool);

		// since the tool is breeding view, ibpwebservice should be updated too
		ArgumentCaptor<Tool> captor = ArgumentCaptor.forClass(Tool.class);
		doNothing().when(appLauncherService).updateToolConfiguration(any(Tool.class));
		when(toolUtil.launchNativeTool(aNativeTool)).thenReturn(mock(Process.class));

		// launch the native app!
		appLauncherService.launchNativeapp(aNativeTool);

		verify(appLauncherService).updateToolConfiguration(captor.capture());
		assertEquals("ibpwebservice is configured", "ibpwebservice",
				captor.getValue().getToolName());
		verify(appLauncherService, times(1)).launchNativeapp(aNativeTool);

	}

	@Test
	public void testLaunchWebapp() throws Exception {
		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.BM_LIST_MANAGER.getToolName());
		aWebTool.setPath(SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		String urlResult = appLauncherService.launchWebapp(aWebTool, ID_PARAM);

		assertEquals("should return correct url for List manager app",
				String.format("%s://%s:%d/%s-%d%s", SCHEME, HOST_NAME, PORT, SAMPLE_BASE_URL,
						ID_PARAM, RESTART_URL_STR + WORKBENCH_CONTEXT_PARAMS), urlResult);

		// for fieldbook apps with params with param
		aWebTool = new Tool();

		aWebTool.setToolName(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB.getToolName());
		aWebTool.setPath(SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		urlResult = appLauncherService.launchWebapp(aWebTool, ID_PARAM);

		assertEquals("should return correct url for fieldbook nursery app",
				String.format("%s://%s:%d/%s/editNursery/%d%s", SCHEME, HOST_NAME, PORT,
						SAMPLE_BASE_URL, ID_PARAM, RESTART_URL_STR + WORKBENCH_CONTEXT_PARAMS),
				urlResult);

		aWebTool = new Tool();

		aWebTool.setToolName(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB.getToolName());
		aWebTool.setPath(SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		urlResult = appLauncherService.launchWebapp(aWebTool, ID_PARAM);

		assertEquals("should return correct url for fieldbook trial app",
				String.format("%s://%s:%d/%s/openTrial/%d%s", SCHEME, HOST_NAME, PORT,
						SAMPLE_BASE_URL, ID_PARAM, RESTART_URL_STR + WORKBENCH_CONTEXT_PARAMS),
				urlResult);

	}

	@Test
	public void testLaunchWebappWithLogin() throws Exception {
		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.GDMS.getToolName());
		aWebTool.setPath(SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB_WITH_LOGIN);

		User user = new User();
		user.setUserid(1);
		user.setName("a_username");
		user.setPassword("a_password");
		when(sessionData.getUserData()).thenReturn(user);

		String urlResult = appLauncherService.launchWebappWithLogin(aWebTool);

		assertEquals("should return correct url for gdms app",
				String.format("%s://%s:%d/%s/web_login_forward?username=%s&password=%s", SCHEME,
						HOST_NAME, PORT, SAMPLE_BASE_URL, 1, user.getPassword()), urlResult);

	}
}
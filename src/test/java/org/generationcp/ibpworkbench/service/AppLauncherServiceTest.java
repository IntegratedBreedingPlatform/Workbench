
package org.generationcp.ibpworkbench.service;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(MockitoJUnitRunner.class)
public class AppLauncherServiceTest {

	public static final String SCHEME = "http";
	public static final String HOST_NAME = "host-name";
	public static final int PORT = 18080;
	public static final String SAMPLE_BASE_URL = "somewhere/out/there";
	public static final int ID_PARAM = 5;
	public static final String WORKBENCH_CONTEXT_PARAMS = "&loggedinUserId=1&selectedProjectId=1";
	public static final String RESTART_URL_STR = "?restartApplication";
	public static final Long DUMMY_PROJECT_ID = Long.valueOf(1);
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
	private final AppLauncherService appLauncherService = Mockito.spy(new AppLauncherService());

	@Before
	public void setUp() throws Exception {
		Mockito.when(this.sessionData.getWorkbenchContextParameters()).thenReturn(AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS);
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));

		Mockito.when(this.request.getScheme()).thenReturn(AppLauncherServiceTest.SCHEME);
		Mockito.when(this.request.getServerName()).thenReturn(AppLauncherServiceTest.HOST_NAME);
		Mockito.when(this.request.getServerPort()).thenReturn(AppLauncherServiceTest.PORT);
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

		Mockito.when(this.workbenchDataManager.getToolWithName(ToolEnum.BM_LIST_MANAGER.getToolName())).thenReturn(aWebTool);
		Mockito.when(this.workbenchDataManager.getToolWithName(ToolEnum.GDMS.getToolName())).thenReturn(gdmsTool);
		Mockito.when(this.workbenchDataManager.getToolWithName(ToolEnum.BREEDING_PLANNER.getToolName())).thenReturn(nativeTool);

		Mockito.doNothing().when(this.tomcatUtil).deployWebAppIfNecessary(Matchers.any(Tool.class));
		Mockito.doNothing().when(this.appLauncherService).updateGermplasmStudyBrowserConfigurationIfNecessary(Matchers.any(Tool.class));
		Mockito.doNothing().when(this.sessionData).logProgramActivity(Matchers.anyString(), Matchers.anyString());

		Mockito.doNothing().when(this.appLauncherService).launchNativeapp(Matchers.any(Tool.class));
		Mockito.doReturn("/result").when(this.appLauncherService).launchWebappWithLogin(Matchers.any(Tool.class));
		Mockito.doReturn("/result").when(this.appLauncherService).launchWebapp(Matchers.any(Tool.class), Matchers.any(Integer.class));

		// the tests itself
		this.appLauncherService.launchTool(ToolEnum.BREEDING_PLANNER.getToolName(), null);
		Mockito.verify(this.appLauncherService).launchNativeapp(nativeTool);

		this.appLauncherService.launchTool(ToolEnum.BM_LIST_MANAGER.getToolName(), null);
		Mockito.verify(this.appLauncherService).launchWebapp(aWebTool, null);

		this.appLauncherService.launchTool(ToolEnum.GDMS.getToolName(), null);
		Mockito.verify(this.appLauncherService).launchWebappWithLogin(gdmsTool);
	}

	@Test
	public void testUpdateGermplasmStudyBrowserConfigurationIfNecessary() throws Exception, AppLaunchException {
		Tool gsbTool = new Tool();
		gsbTool.setToolName(ToolEnum.GERMPLASM_BROWSER.getToolName());
		Mockito.when(this.workbenchDataManager.getToolWithName(ToolName.GERMPLASM_BROWSER.name())).thenReturn(gsbTool);

		Mockito.doNothing().when(this.tomcatUtil).deployWebAppIfNecessary(gsbTool);

		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.BM_LIST_MANAGER.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);

		this.appLauncherService.updateGermplasmStudyBrowserConfigurationIfNecessary(aWebTool);

		Mockito.verify(this.tomcatUtil).deployWebAppIfNecessary(gsbTool);
	}

	@Test
	public void testLaunchNativeapp() throws Exception, AppLaunchException {
		Tool aNativeTool = new Tool();

		// for vaadin type params with a dash
		aNativeTool.setToolName(ToolEnum.BREEDING_VIEW.getToolName());
		aNativeTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aNativeTool.setToolType(ToolType.NATIVE);

		Mockito.doNothing().when(this.toolUtil).closeNativeTool(aNativeTool);

		// since the tool is breeding view, ibpwebservice should be updated too
		ArgumentCaptor<Tool> captor = ArgumentCaptor.forClass(Tool.class);
		Mockito.doNothing().when(this.tomcatUtil).deployWebAppIfNecessary(Matchers.any(Tool.class));
		Mockito.when(this.toolUtil.launchNativeTool(aNativeTool)).thenReturn(Mockito.mock(Process.class));

		// launch the native app!
		this.appLauncherService.launchNativeapp(aNativeTool);

		Mockito.verify(this.tomcatUtil).deployWebAppIfNecessary(captor.capture());
		Assert.assertEquals("ibpwebservice is configured", "ibpwebservice", captor.getValue().getToolName());
		Mockito.verify(this.appLauncherService, Mockito.times(1)).launchNativeapp(aNativeTool);

	}

	@Test
	public void testLaunchWebapp() throws Exception {
		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.BM_LIST_MANAGER.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		String urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.ID_PARAM);

		Assert.assertEquals("should return correct url for List manager app", String.format("%s://%s:%d/%s-%d%s",
				AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME, AppLauncherServiceTest.PORT,
				AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.ID_PARAM, AppLauncherServiceTest.RESTART_URL_STR
						+ AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

		// for fieldbook apps with params with param
		aWebTool = new Tool();

		aWebTool.setToolName(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.ID_PARAM);

		Assert.assertEquals("should return correct url for fieldbook nursery app", String.format("%s://%s:%d/%s/editNursery/%d%s",
				AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME, AppLauncherServiceTest.PORT,
				AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.ID_PARAM, AppLauncherServiceTest.RESTART_URL_STR
						+ AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

		aWebTool = new Tool();

		aWebTool.setToolName(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.ID_PARAM);

		Assert.assertEquals("should return correct url for fieldbook trial app",
				String.format("%s://%s:%d/%s/openTrial/%d%s", AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME,
						AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.ID_PARAM,
						AppLauncherServiceTest.RESTART_URL_STR + AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

	}

	@Test
	public void testLaunchMigratorWebapp() {
		Tool migratorWebTool = new Tool();

		migratorWebTool.setToolName(ToolEnum.MIGRATOR.getToolName());
		migratorWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		migratorWebTool.setToolType(ToolType.WEB);
		String urlResult = this.appLauncherService.launchMigratorWebapp(migratorWebTool, AppLauncherServiceTest.ID_PARAM);

		Assert.assertEquals("should return correct url for List manager app",
				String.format("%s://%s:%d/%s%d", AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME,
						AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.ID_PARAM), urlResult);
	}

	@Test
	public void testLaunchWebappWithLogin() throws Exception {
		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.GDMS.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB_WITH_LOGIN);

		User user = new User();
		user.setUserid(1);
		user.setName("a_username");
		user.setPassword("a_password");

		Project project = Mockito.mock(Project.class);
		Mockito.when(project.getProjectId()).thenReturn(AppLauncherServiceTest.DUMMY_PROJECT_ID);
		Mockito.when(this.sessionData.getLastOpenedProject()).thenReturn(project);

		Mockito.when(this.sessionData.getUserData()).thenReturn(user);

		String urlResult = this.appLauncherService.launchWebappWithLogin(aWebTool);

		Assert.assertEquals("should return correct url for gdms app", String.format(
				"%s://%s:%d/%s?restartApplication&selectedProjectId=%s&loggedInUserId=%s", AppLauncherServiceTest.SCHEME,
				AppLauncherServiceTest.HOST_NAME, AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL,
				AppLauncherServiceTest.DUMMY_PROJECT_ID, 1), urlResult);

	}
}

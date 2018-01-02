
package org.generationcp.ibpworkbench.service;

import java.util.Properties;
import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.tomcat.util.TomcatUtil;
import org.generationcp.ibpworkbench.exception.AppLaunchException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(MockitoJUnitRunner.class)
public class AppLauncherServiceTest {

	public static final String SCHEME = "http";
	public static final String HOST_NAME = "host-name";
	public static final int PORT = 18080;
	public static final String SAMPLE_BASE_URL = "somewhere/out/there";

	public static final String WORKBENCH_CONTEXT_PARAMS = "&loggedInUserId=5&selectedProjectId=1&authToken=VXNlck5hbWU";
	public static final String RESTART_URL_STR = "?restartApplication";

	public static final int LOGGED_IN_USER_ID = 5;
	public static final Long PROJECT_ID = Long.valueOf(1);
	public static final String USER_NAME = "UserName";

	@Mock
	HttpServletRequest request;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ToolUtil toolUtil;

	@Mock
	private TomcatUtil tomcatUtil;

	@Mock
	private Properties workbenchProperties;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private SecurityContext securityContext;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private final AppLauncherService appLauncherService = Mockito.spy(new AppLauncherService());

	@Before
	public void setUp() throws Exception {

		ContextInfo contextInfo = new ContextInfo(LOGGED_IN_USER_ID, PROJECT_ID);

		Mockito.when(contextUtil.getContextInfoFromSession()).thenReturn(contextInfo);

		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.request));

		Authentication authentication = Mockito.mock(Authentication.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		Mockito.when(authentication.getName()).thenReturn(USER_NAME);
		SecurityContextHolder.setContext(securityContext);


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
	public void testLaunchNativeapp() throws Exception, AppLaunchException {
		Tool aNativeTool = new Tool();

		// for vaadin type params with a dash
		aNativeTool.setToolName(ToolEnum.BREEDING_VIEW.getToolName());
		aNativeTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aNativeTool.setToolType(ToolType.NATIVE);

		Mockito.doNothing().when(this.toolUtil).closeNativeTool(aNativeTool);

		// launch the native app!
		this.appLauncherService.launchNativeapp(aNativeTool);

		Mockito.verify(this.appLauncherService, Mockito.times(1)).launchNativeapp(aNativeTool);

	}

	@Test
	public void testLaunchWebapp() throws Exception {
		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.BM_LIST_MANAGER.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		String urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.LOGGED_IN_USER_ID);

		Assert.assertEquals("should return correct url for List manager app", String.format("%s://%s:%d/%s-%d%s",
				AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME, AppLauncherServiceTest.PORT,
				AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.LOGGED_IN_USER_ID, AppLauncherServiceTest.RESTART_URL_STR
						+ AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

		// for fieldbook apps with params with param
		aWebTool = new Tool();

		aWebTool.setToolName(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.LOGGED_IN_USER_ID);

		Assert.assertEquals("should return correct url for fieldbook nursery app", String.format("%s://%s:%d/%s/editNursery/%d%s",
				AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME, AppLauncherServiceTest.PORT,
				AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.LOGGED_IN_USER_ID, AppLauncherServiceTest.RESTART_URL_STR
						+ AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

		aWebTool = new Tool();

		aWebTool.setToolName(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB);
		urlResult = this.appLauncherService.launchWebapp(aWebTool, AppLauncherServiceTest.LOGGED_IN_USER_ID);

		Assert.assertEquals("should return correct url for fieldbook trial app",
				String.format("%s://%s:%d/%s/openTrial/%d%s", AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME,
						AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.LOGGED_IN_USER_ID,
						AppLauncherServiceTest.RESTART_URL_STR + AppLauncherServiceTest.WORKBENCH_CONTEXT_PARAMS), urlResult);

	}

	@Test
	public void testLaunchMigratorWebapp() {
		Tool migratorWebTool = new Tool();

		migratorWebTool.setToolName(ToolEnum.MIGRATOR.getToolName());
		migratorWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		migratorWebTool.setToolType(ToolType.WEB);
		String urlResult = this.appLauncherService.launchMigratorWebapp(migratorWebTool, AppLauncherServiceTest.LOGGED_IN_USER_ID);

		Assert.assertEquals("should return correct url for List manager app",
				String.format("%s://%s:%d/%s%d", AppLauncherServiceTest.SCHEME, AppLauncherServiceTest.HOST_NAME,
						AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL, AppLauncherServiceTest.LOGGED_IN_USER_ID), urlResult);
	}

	@Test
	public void testLaunchWebappWithLogin() throws Exception {
		Tool aWebTool = new Tool();

		// for vaadin type params with a dash
		aWebTool.setToolName(ToolEnum.GDMS.getToolName());
		aWebTool.setPath(AppLauncherServiceTest.SAMPLE_BASE_URL);
		aWebTool.setToolType(ToolType.WEB_WITH_LOGIN);

		User user = new User();
		user.setUserid(LOGGED_IN_USER_ID);
		user.setName("a_username");
		user.setPassword("a_password");

		Project project = Mockito.mock(Project.class);
		Mockito.when(project.getProjectId()).thenReturn(AppLauncherServiceTest.PROJECT_ID);

		String urlResult = this.appLauncherService.launchWebappWithLogin(aWebTool);

		Assert.assertEquals("should return correct url for gdms app", String.format(
				"%s://%s:%d/%s?restartApplication&loggedInUserId=%s&selectedProjectId=%s", AppLauncherServiceTest.SCHEME,
				AppLauncherServiceTest.HOST_NAME, AppLauncherServiceTest.PORT, AppLauncherServiceTest.SAMPLE_BASE_URL,
				LOGGED_IN_USER_ID, AppLauncherServiceTest.PROJECT_ID), urlResult);

	}
}

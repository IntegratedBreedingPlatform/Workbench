
package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.util.Map;

import org.generationcp.ibpworkbench.exception.ConfigurationChangeException;
import org.generationcp.ibpworkbench.util.bean.ConfigurationChangeParameters;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserMysqlAccount;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
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

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 2/12/2015 Time: 11:10 AM
 */

@RunWith(MockitoJUnitRunner.class)
public class ToolUtilTest {

	public static final String DUMMY_INSTALLATION_PATH = "d:/BMS";
	public static final String DUMMY_TOOL_NAME = "DummyTool";
	public static final String DUMMY_TOOL_TITLE = "DummyTitle";
	public static final String DUMMY_NATIVE_TOOL_PATH = "c:/Breeding Management System/tools/dummyTool/dummyTool.exe";
	public static final String DUMMY_NON_NATIVE_TOOL_PATH = "http://localhost:8081/Fieldbook/someTool";

	public static final String DUMMY_CENTRAL_DB_NAME = "centralDb";
	public static final String DUMMY_LOCAL_DB_NAME = "localDb";
	public static final String DUMMY_SQL_USERNAME = "username";
	public static final String DUMMY_SQL_PASSWORD = "password";

	public static final String DUMMY_WORKBENCH_DB_NAME = "workbench";

	public static final String ANY_STRING = "any";

	public static final long DUMMY_PROJECT_ID = 1;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private ToolUtil dut;

	@Before
	public void setUp() throws Exception {
		this.dut.setWorkbenchInstallationDirectory(ToolUtilTest.DUMMY_INSTALLATION_PATH);
	}

	@Test
	public void testComputeToolPath() {
		Tool tool = this.constructDummyNativeTool();
		String path = this.dut.getComputedToolPath(tool);

		Assert.assertNotNull(path);
		Assert.assertTrue("ToolUtil unable to provide the correct tool path", path.contains(ToolUtilTest.DUMMY_INSTALLATION_PATH));
	}

	@Test
	public void testComputeToolPathNonNative() {
		Tool tool = new Tool(ToolUtilTest.DUMMY_TOOL_NAME, ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NON_NATIVE_TOOL_PATH);

		String path = this.dut.getComputedToolPath(tool);

		Assert.assertNotNull(path);
		Assert.assertFalse("ToolUtil unable to provide the correct tool path", path.contains(ToolUtilTest.DUMMY_INSTALLATION_PATH));
		Assert.assertEquals("ToolUtil should not modify this tool path", tool.getPath(), path);
	}

	@Test
	public void testUpdateToolConfigurationForProjectNonGDMSMBDT() {
		Project project = Mockito.mock(Project.class);
		CropType cropType = Mockito.mock(CropType.class);

		Mockito.when(project.getCropType()).thenReturn(cropType);
		Mockito.when(cropType.getDbName()).thenReturn(ToolUtilTest.DUMMY_CENTRAL_DB_NAME);

		Tool dummyTool = new Tool(ToolUtilTest.DUMMY_TOOL_NAME, ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NON_NATIVE_TOOL_PATH);
		try {
			boolean updatedConfiguration = this.dut.updateToolConfigurationForProject(dummyTool, project);

			Assert.assertFalse("ToolUtil changed configuration even for tool that does not need configuration change", updatedConfiguration);
		} catch (ConfigurationChangeException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationGDMSNullUser() {

		try {
			ToolUtil mole = Mockito.spy(this.dut);
			Mockito.doReturn(null).when(mole).getCurrentUser();
			Mockito.doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(Matchers.any(ConfigurationChangeParameters.class));

			Tool gdmsTool = this.constructDummyNativeTool();

			this.testUpdateToolConfiguration(mole, gdmsTool, ToolUtilTest.DUMMY_INSTALLATION_PATH + File.separator
					+ ToolUtil.GDMS_CONFIG_LOCATION, null, null);

		} catch (ConfigurationChangeException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationGDMSNotNullUserNoSQLAccount() {

		try {
			ToolUtil mole = Mockito.spy(this.dut);
			Mockito.doReturn(null).when(mole).getCurrentUser();
			Mockito.doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(Matchers.any(ConfigurationChangeParameters.class));

			Mockito.when(this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(Matchers.anyInt(), Matchers.anyInt()))
					.thenThrow(MiddlewareQueryException.class);
			Tool gdmsTool = this.constructDummyNativeTool();

			this.testUpdateToolConfiguration(mole, gdmsTool, ToolUtilTest.DUMMY_INSTALLATION_PATH + File.separator
					+ ToolUtil.GDMS_CONFIG_LOCATION, null, null);

		} catch (ConfigurationChangeException | MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationGDMSNotNullUser() {

		try {
			ToolUtil mole = Mockito.spy(this.dut);
			Mockito.doReturn(Mockito.mock(User.class)).when(mole).getCurrentUser();
			Mockito.doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(Matchers.any(ConfigurationChangeParameters.class));

			Tool gdmsTool = this.constructDummyNativeTool();

			ProjectUserMysqlAccount account = Mockito.mock(ProjectUserMysqlAccount.class);
			Mockito.when(account.getMysqlUsername()).thenReturn(ToolUtilTest.DUMMY_SQL_USERNAME);
			Mockito.when(account.getMysqlPassword()).thenReturn(ToolUtilTest.DUMMY_SQL_PASSWORD);

			Mockito.when(this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(Matchers.anyInt(), Matchers.anyInt()))
					.thenReturn(account);

			this.testUpdateToolConfiguration(mole, gdmsTool, ToolUtilTest.DUMMY_INSTALLATION_PATH + File.separator
					+ ToolUtil.GDMS_CONFIG_LOCATION, ToolUtilTest.DUMMY_SQL_USERNAME, ToolUtilTest.DUMMY_SQL_PASSWORD);

		} catch (ConfigurationChangeException | MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationMBDTNullUser() {

		try {
			ToolUtil mole = Mockito.spy(this.dut);
			Mockito.doReturn(null).when(mole).getCurrentUser();
			Mockito.doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(Matchers.any(ConfigurationChangeParameters.class));

			Tool mbdtTool = new Tool(ToolName.mbdt.name(), ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NATIVE_TOOL_PATH);

			this.testUpdateToolConfiguration(mole, mbdtTool, ToolUtilTest.DUMMY_INSTALLATION_PATH + File.separator
					+ ToolUtil.MBDT_CONFIG_LOCATION, null, null);

		} catch (ConfigurationChangeException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationMBDTNotNullUser() {

		try {
			ToolUtil mole = Mockito.spy(this.dut);
			Mockito.doReturn(Mockito.mock(User.class)).when(mole).getCurrentUser();
			Mockito.doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(Matchers.any(ConfigurationChangeParameters.class));

			Tool mbdtTool = new Tool(ToolName.mbdt.name(), ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NATIVE_TOOL_PATH);

			ProjectUserMysqlAccount account = Mockito.mock(ProjectUserMysqlAccount.class);
			Mockito.when(account.getMysqlUsername()).thenReturn(ToolUtilTest.DUMMY_SQL_USERNAME);
			Mockito.when(account.getMysqlPassword()).thenReturn(ToolUtilTest.DUMMY_SQL_PASSWORD);

			Mockito.when(this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(Matchers.anyInt(), Matchers.anyInt()))
					.thenReturn(account);

			this.testUpdateToolConfiguration(mole, mbdtTool, ToolUtilTest.DUMMY_INSTALLATION_PATH + File.separator
					+ ToolUtil.MBDT_CONFIG_LOCATION, ToolUtilTest.DUMMY_SQL_USERNAME, ToolUtilTest.DUMMY_SQL_PASSWORD);

		} catch (ConfigurationChangeException | MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationMBDTNotNullUserNoSQLAccount() {

		try {
			ToolUtil mole = Mockito.spy(this.dut);
			Mockito.doReturn(null).when(mole).getCurrentUser();
			Mockito.doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(Matchers.any(ConfigurationChangeParameters.class));

			Mockito.when(this.workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(Matchers.anyInt(), Matchers.anyInt()))
					.thenThrow(MiddlewareQueryException.class);
			Tool mbdtTool = new Tool(ToolName.mbdt.name(), ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NATIVE_TOOL_PATH);

			this.testUpdateToolConfiguration(mole, mbdtTool, ToolUtilTest.DUMMY_INSTALLATION_PATH + File.separator
					+ ToolUtil.MBDT_CONFIG_LOCATION, null, null);

		} catch (ConfigurationChangeException | MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolMiddlewareDatabaseConfigurationWithWorkbench() {
		ConfigurationChangeParameters params =
				new ConfigurationChangeParameters(ToolUtilTest.ANY_STRING, ToolUtilTest.ANY_STRING, ToolUtilTest.ANY_STRING,
						ToolUtilTest.ANY_STRING, true, false, false);

		this.dut.setWorkbenchDbName(ToolUtilTest.DUMMY_WORKBENCH_DB_NAME);

		ToolUtil mole = Mockito.spy(this.dut);
		Mockito.doReturn(true).when(mole).updatePropertyFile(Matchers.any(File.class), Matchers.anyMap());
		Mockito.doReturn(Mockito.mock(File.class)).when(mole).getConfigurationFile(Matchers.any(ConfigurationChangeParameters.class));

		try {
			boolean result = mole.updateToolMiddlewareDatabaseConfiguration(params);
			Assert.assertTrue(result);
			ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

			Mockito.verify(mole).updatePropertyFile(Matchers.any(File.class), captor.capture());

			Map<String, String> propertyValues = captor.getValue();

			Assert.assertTrue("Updated configuration does not contain workbench details", propertyValues.containsKey("workbench.dbname"));
			Assert.assertEquals("Wrong workbench db name set when updating configuration", ToolUtilTest.DUMMY_WORKBENCH_DB_NAME,
					propertyValues.get("workbench.dbname"));
		} catch (ConfigurationChangeException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolMiddlewareDatabaseConfigurationNoWorkbench() {
		ConfigurationChangeParameters params =
				new ConfigurationChangeParameters(ToolUtilTest.ANY_STRING, ToolUtilTest.ANY_STRING, ToolUtilTest.ANY_STRING,
						ToolUtilTest.ANY_STRING, false, false, false);

		this.dut.setWorkbenchDbName(ToolUtilTest.DUMMY_WORKBENCH_DB_NAME);

		ToolUtil mole = Mockito.spy(this.dut);
		Mockito.doReturn(true).when(mole).updatePropertyFile(Matchers.any(File.class), Matchers.anyMap());
		Mockito.doReturn(Mockito.mock(File.class)).when(mole).getConfigurationFile(Matchers.any(ConfigurationChangeParameters.class));

		try {
			boolean result = mole.updateToolMiddlewareDatabaseConfiguration(params);
			Assert.assertTrue(result);
			ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

			Mockito.verify(mole).updatePropertyFile(Matchers.any(File.class), captor.capture());

			Map<String, String> propertyValues = captor.getValue();

			Assert.assertFalse("Updated configuration does not contain workbench details", propertyValues.containsKey("workbench.dbname"));
		} catch (ConfigurationChangeException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolMiddlewareDatabaseConfigurationEmptyUsernameParam() {
		ConfigurationChangeParameters params =
				new ConfigurationChangeParameters(ToolUtilTest.ANY_STRING, ToolUtilTest.ANY_STRING, null, null, false, false, false);

		this.dut.setCentralPassword(ToolUtilTest.DUMMY_SQL_PASSWORD);
		this.dut.setCentralUser(ToolUtilTest.DUMMY_SQL_USERNAME);
		this.dut.setLocalPassword(ToolUtilTest.DUMMY_SQL_PASSWORD);
		this.dut.setLocalUser(ToolUtilTest.DUMMY_SQL_USERNAME);

		ToolUtil mole = Mockito.spy(this.dut);
		Mockito.doReturn(true).when(mole).updatePropertyFile(Matchers.any(File.class), Matchers.anyMap());
		Mockito.doReturn(Mockito.mock(File.class)).when(mole).getConfigurationFile(Matchers.any(ConfigurationChangeParameters.class));

		try {
			boolean result = mole.updateToolMiddlewareDatabaseConfiguration(params);
			Assert.assertTrue(result);
			ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

			Mockito.verify(mole).updatePropertyFile(Matchers.any(File.class), captor.capture());

			Map<String, String> propertyValues = captor.getValue();

			Assert.assertEquals("Updated configuration does not set params from config file", ToolUtilTest.DUMMY_SQL_USERNAME,
					propertyValues.get("central.username"));
			Assert.assertEquals("Updated configuration does not set params from config file", ToolUtilTest.DUMMY_SQL_PASSWORD,
					propertyValues.get("central.password"));
		} catch (ConfigurationChangeException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void testUpdateToolConfiguration(ToolUtil utilSetup, Tool toolToTest, String expectedConfigPath, String expectedUserName,
			String expectedPassword) {
		try {
			Project project = Mockito.mock(Project.class);
			Mockito.when(project.getProjectId()).thenReturn(ToolUtilTest.DUMMY_PROJECT_ID);
			CropType cropType = Mockito.mock(CropType.class);

			WorkbenchSetting setting = Mockito.mock(WorkbenchSetting.class);

			Mockito.when(this.workbenchDataManager.getWorkbenchSetting()).thenReturn(setting);
			Mockito.when(setting.getInstallationDirectory()).thenReturn(ToolUtilTest.DUMMY_INSTALLATION_PATH);

			Mockito.when(project.getCropType()).thenReturn(cropType);
			Mockito.when(cropType.getDbName()).thenReturn(ToolUtilTest.DUMMY_CENTRAL_DB_NAME);

			boolean changed = utilSetup.updateToolConfigurationForProject(toolToTest, project);

			if (toolToTest.getToolName().equals(ToolName.mbdt.name())) {
				ArgumentCaptor<ConfigurationChangeParameters> params = ArgumentCaptor.forClass(ConfigurationChangeParameters.class);
				Mockito.verify(utilSetup).updateToolMiddlewareDatabaseConfiguration(params.capture());
				Assert.assertTrue("Tool Util unable to update tool configuration properly", changed);
				ConfigurationChangeParameters captured = params.getValue();
				Assert.assertEquals("Unable to properly initialize the configuration location path for application", expectedConfigPath,
						captured.getPropertyFile());
				Assert.assertEquals("Wrong configuration sent for username", expectedUserName, captured.getUserName());
				Assert.assertEquals("Wrong configuration sent for password", expectedPassword, captured.getPassword());
			} else {
				Mockito.verify(utilSetup, Mockito.never()).updateToolMiddlewareDatabaseConfiguration(
						Matchers.any(ConfigurationChangeParameters.class));
			}
		} catch (MiddlewareQueryException | ConfigurationChangeException e) {
			Assert.fail(e.getMessage());
		}
	}

	protected Tool constructDummyNativeTool() {
		return new Tool(ToolName.GDMS.name(), ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NATIVE_TOOL_PATH);
	}
}

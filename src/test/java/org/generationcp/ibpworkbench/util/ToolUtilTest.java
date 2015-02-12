package org.generationcp.ibpworkbench.util;

import org.generationcp.ibpworkbench.exception.ConfigurationChangeException;
import org.generationcp.ibpworkbench.util.bean.ConfigurationChangeParameters;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 2/12/2015
 * Time: 11:10 AM
 */

@RunWith(MockitoJUnitRunner.class)
public class ToolUtilTest {

	public static final String DUMMY_INSTALLATION_PATH = "d:/BMS";
	public static final String DUMMY_TOOL_NAME = "DummyTool";
	public static final String DUMMY_TOOL_TITLE = "DummyTitle";
	public static final String DUMMY_NATIVE_TOOL_PATH = "c:/Breeding Management System/tools/dummyTool/dummyTool.exe";
	public static final String DUMMY_NON_NATIVE_TOOL_PATH = "http://localhost:8081/Fieldbook/someTool";

	public static final String DUMMY_CENTRAL_DB_NAME ="centralDb";
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
		dut.setWorkbenchInstallationDirectory(DUMMY_INSTALLATION_PATH);
	}

	@Test
	public void testComputeToolPath() {
		Tool tool = constructDummyNativeTool();
		String path = dut.getComputedToolPath(tool);


		assertNotNull(path);
		assertTrue("ToolUtil unable to provide the correct tool path",
				path.contains(DUMMY_INSTALLATION_PATH));
	}

	@Test
	public void testComputeToolPathNonNative() {
		Tool tool = new Tool(DUMMY_TOOL_NAME, DUMMY_TOOL_TITLE, DUMMY_NON_NATIVE_TOOL_PATH);

		String path = dut.getComputedToolPath(tool);

		assertNotNull(path);
		assertFalse("ToolUtil unable to provide the correct tool path",
				path.contains(DUMMY_INSTALLATION_PATH));
		assertEquals("ToolUtil should not modify this tool path", tool.getPath(), path);
	}

	@Test
	public void testUpdateToolConfigurationForProjectNonGDMSMBDT() {
		Project project = mock(Project.class);
		CropType cropType = mock(CropType.class);

		when(project.getCropType()).thenReturn(cropType);
		when(cropType.getCentralDbName()).thenReturn(DUMMY_CENTRAL_DB_NAME);
		when(cropType.getLocalDatabaseNameWithProject(any(Project.class))).thenReturn(DUMMY_LOCAL_DB_NAME);

		Tool dummyTool = new Tool(DUMMY_TOOL_NAME, DUMMY_TOOL_TITLE, DUMMY_NON_NATIVE_TOOL_PATH);
		try {
			boolean updatedConfiguration = dut.updateToolConfigurationForProject(dummyTool, project);

			assertFalse("ToolUtil changed configuration even for tool that does not need configuration change", updatedConfiguration);
		} catch (ConfigurationChangeException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationGDMSNullUser() {

		try {
			ToolUtil mole = spy(dut);
								doReturn(null).when(mole).getCurrentUser();
								doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(
										any(ConfigurationChangeParameters.class));

			Tool gdmsTool = constructDummyNativeTool();

			testUpdateToolConfiguration(mole, gdmsTool, DUMMY_INSTALLATION_PATH + File.separator + ToolUtil.GDMS_CONFIG_LOCATION, null, null);

		} catch (ConfigurationChangeException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationGDMSNotNullUserNoSQLAccount() {

		try {
			ToolUtil mole = spy(dut);
			doReturn(null).when(mole).getCurrentUser();
			doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(
					any(ConfigurationChangeParameters.class));

			when(workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(anyInt(), anyInt())).thenThrow(MiddlewareQueryException.class);
			Tool gdmsTool = constructDummyNativeTool();

			testUpdateToolConfiguration(mole, gdmsTool,
					DUMMY_INSTALLATION_PATH + File.separator + ToolUtil.GDMS_CONFIG_LOCATION, null,
					null);

		} catch (ConfigurationChangeException | MiddlewareQueryException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationGDMSNotNullUser() {

		try {
			ToolUtil mole = spy(dut);
			doReturn(mock(User.class)).when(mole).getCurrentUser();
			doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(
					any(ConfigurationChangeParameters.class));

			Tool gdmsTool = constructDummyNativeTool();

			ProjectUserMysqlAccount account = mock(ProjectUserMysqlAccount.class);
			when(account.getMysqlUsername()).thenReturn(DUMMY_SQL_USERNAME);
			when(account.getMysqlPassword()).thenReturn(DUMMY_SQL_PASSWORD);

			when(workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(anyInt(), anyInt())).thenReturn(account);

			testUpdateToolConfiguration(mole, gdmsTool,
					DUMMY_INSTALLATION_PATH + File.separator + ToolUtil.GDMS_CONFIG_LOCATION,
					DUMMY_SQL_USERNAME,
					DUMMY_SQL_PASSWORD);

		} catch (ConfigurationChangeException | MiddlewareQueryException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolConfigurationMBDTNullUser() {

		try {
			ToolUtil mole = spy(dut);
			doReturn(null).when(mole).getCurrentUser();
			doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(
					any(ConfigurationChangeParameters.class));

			Tool mbdtTool = new Tool(ToolName.mbdt.name(), DUMMY_TOOL_TITLE, DUMMY_NATIVE_TOOL_PATH);

			testUpdateToolConfiguration(mole, mbdtTool,
					DUMMY_INSTALLATION_PATH + File.separator + ToolUtil.MBDT_CONFIG_LOCATION, null,
					null);

		} catch (ConfigurationChangeException e) {
			fail(e.getMessage());
		}
	}

	@Test
		public void testUpdateToolConfigurationMBDTNotNullUser() {

			try {
				ToolUtil mole = spy(dut);
				doReturn(mock(User.class)).when(mole).getCurrentUser();
				doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(
						any(ConfigurationChangeParameters.class));

				Tool mbdtTool = new Tool(ToolName.mbdt.name(), DUMMY_TOOL_TITLE,
									DUMMY_NATIVE_TOOL_PATH);

				ProjectUserMysqlAccount account = mock(ProjectUserMysqlAccount.class);
				when(account.getMysqlUsername()).thenReturn(DUMMY_SQL_USERNAME);
				when(account.getMysqlPassword()).thenReturn(DUMMY_SQL_PASSWORD);

				when(workbenchDataManager.getProjectUserMysqlAccountByProjectIdAndUserId(anyInt(), anyInt())).thenReturn(account);

				testUpdateToolConfiguration(mole, mbdtTool,
						DUMMY_INSTALLATION_PATH + File.separator + ToolUtil.MBDT_CONFIG_LOCATION,
						DUMMY_SQL_USERNAME,
						DUMMY_SQL_PASSWORD);

			} catch (ConfigurationChangeException | MiddlewareQueryException e) {
				fail(e.getMessage());
			}
		}

	@Test
	public void testUpdateToolConfigurationMBDTNotNullUserNoSQLAccount() {

		try {
			ToolUtil mole = spy(dut);
			doReturn(null).when(mole).getCurrentUser();
			doReturn(true).when(mole).updateToolMiddlewareDatabaseConfiguration(
					any(ConfigurationChangeParameters.class));

			when(workbenchDataManager
					.getProjectUserMysqlAccountByProjectIdAndUserId(anyInt(), anyInt()))
					.thenThrow(MiddlewareQueryException.class);
			Tool mbdtTool = new Tool(ToolName.mbdt.name(), DUMMY_TOOL_TITLE,
					DUMMY_NATIVE_TOOL_PATH);

			testUpdateToolConfiguration(mole, mbdtTool,
					DUMMY_INSTALLATION_PATH + File.separator + ToolUtil.MBDT_CONFIG_LOCATION, null,
					null);

		} catch (ConfigurationChangeException | MiddlewareQueryException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdateToolMiddlewareDatabaseConfigurationWithWorkbench() {
		ConfigurationChangeParameters params = new ConfigurationChangeParameters(ANY_STRING, ANY_STRING, ANY_STRING, ANY_STRING, ANY_STRING, true, false, false);

		dut.setWorkbenchDbName(DUMMY_WORKBENCH_DB_NAME);

		ToolUtil mole = spy(dut);
		doReturn(true).when(mole).updatePropertyFile(any(File.class), anyMap());
		doReturn(mock(File.class)).when(mole).getConfigurationFile(any(ConfigurationChangeParameters.class));

		try {
			boolean result = mole.updateToolMiddlewareDatabaseConfiguration(params);
			assertTrue(result);
			ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

			verify(mole).updatePropertyFile(any(File.class), captor.capture());

			Map<String, String> propertyValues = captor.getValue();

			assertTrue("Updated configuration does not contain workbench details", propertyValues.containsKey("workbench.dbname"));
			assertEquals("Wrong workbench db name set when updating configuration", DUMMY_WORKBENCH_DB_NAME, propertyValues.get("workbench.dbname"));
		} catch (ConfigurationChangeException e) {
			fail(e.getMessage());
		}
	}

	@Test
		public void testUpdateToolMiddlewareDatabaseConfigurationNoWorkbench() {
			ConfigurationChangeParameters params = new ConfigurationChangeParameters(ANY_STRING, ANY_STRING, ANY_STRING, ANY_STRING, ANY_STRING, false, false, false);

			dut.setWorkbenchDbName(DUMMY_WORKBENCH_DB_NAME);

			ToolUtil mole = spy(dut);
			doReturn(true).when(mole).updatePropertyFile(any(File.class), anyMap());
			doReturn(mock(File.class)).when(mole).getConfigurationFile(any(ConfigurationChangeParameters.class));

			try {
				boolean result = mole.updateToolMiddlewareDatabaseConfiguration(params);
				assertTrue(result);
				ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

				verify(mole).updatePropertyFile(any(File.class), captor.capture());

				Map<String, String> propertyValues = captor.getValue();

				assertFalse("Updated configuration does not contain workbench details",
						propertyValues.containsKey("workbench.dbname"));
			} catch (ConfigurationChangeException e) {
				fail(e.getMessage());
			}
		}

	@Test
	public void testUpdateToolMiddlewareDatabaseConfigurationEmptyUsernameParam() {
		ConfigurationChangeParameters params = new ConfigurationChangeParameters(ANY_STRING,
				ANY_STRING, ANY_STRING, null, null, false, false, false);

		dut.setCentralPassword(DUMMY_SQL_PASSWORD);
		dut.setCentralUser(DUMMY_SQL_USERNAME);
		dut.setLocalPassword(DUMMY_SQL_PASSWORD);
		dut.setLocalUser(DUMMY_SQL_USERNAME);

		ToolUtil mole = spy(dut);
		doReturn(true).when(mole).updatePropertyFile(any(File.class), anyMap());
		doReturn(mock(File.class)).when(mole)
				.getConfigurationFile(any(ConfigurationChangeParameters.class));

		try {
			boolean result = mole.updateToolMiddlewareDatabaseConfiguration(params);
			assertTrue(result);
			ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

			verify(mole).updatePropertyFile(any(File.class), captor.capture());

			Map<String, String> propertyValues = captor.getValue();

			assertEquals("Updated configuration does not set params from config file",
					DUMMY_SQL_USERNAME, propertyValues.get("central.username"));
			assertEquals("Updated configuration does not set params from config file",
					DUMMY_SQL_PASSWORD, propertyValues.get("central.password"));
		} catch (ConfigurationChangeException e) {
			fail(e.getMessage());
		}
	}



	public void testUpdateToolConfiguration(ToolUtil utilSetup, Tool toolToTest, String expectedConfigPath, String expectedUserName, String expectedPassword) {
		try {
			Project project = mock(Project.class);
			when(project.getProjectId()).thenReturn(DUMMY_PROJECT_ID);
			CropType cropType = mock(CropType.class);

			WorkbenchSetting setting = mock(WorkbenchSetting.class);

			when(workbenchDataManager.getWorkbenchSetting()).thenReturn(setting);
			when(setting.getInstallationDirectory()).thenReturn(DUMMY_INSTALLATION_PATH);

			when(project.getCropType()).thenReturn(cropType);
			when(cropType.getCentralDbName()).thenReturn(DUMMY_CENTRAL_DB_NAME);
			when(cropType.getLocalDatabaseNameWithProject(any(Project.class)))
					.thenReturn(DUMMY_LOCAL_DB_NAME);


			boolean changed = utilSetup.updateToolConfigurationForProject(toolToTest, project);

			ArgumentCaptor<ConfigurationChangeParameters> params = ArgumentCaptor
					.forClass(ConfigurationChangeParameters.class);
			verify(utilSetup).updateToolMiddlewareDatabaseConfiguration(params.capture());

			assertTrue("Tool Util unable to update tool configuration properly", changed);
			ConfigurationChangeParameters captured = params.getValue();
			assertEquals(
					"Unable to properly initialize the configuration location path for application",
					expectedConfigPath,
					captured.getPropertyFile());
			assertEquals("Wrong configuration sent for username", expectedUserName,
					captured.getUserName());
			assertEquals("Wrong configuration sent for password", expectedPassword,
					captured.getPassword());

		} catch (MiddlewareQueryException | ConfigurationChangeException e) {
			fail(e.getMessage());
		}
	}

	protected Tool constructDummyNativeTool() {
		return new Tool(ToolName.gdms.name(), DUMMY_TOOL_TITLE, DUMMY_NATIVE_TOOL_PATH);
	}
}

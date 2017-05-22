
package org.generationcp.ibpworkbench.util;

import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

	protected Tool constructDummyNativeTool() {
		return new Tool(ToolName.GDMS.name(), ToolUtilTest.DUMMY_TOOL_TITLE, ToolUtilTest.DUMMY_NATIVE_TOOL_PATH);
	}
}

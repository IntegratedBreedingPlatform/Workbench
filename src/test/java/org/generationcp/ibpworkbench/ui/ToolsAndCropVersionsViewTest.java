package org.generationcp.ibpworkbench.ui;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.MessageResourceUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.VaadinComponentsUtil;
import org.generationcp.commons.vaadin.ui.VaadinComponentsUtil.VaadinComponentFieldType;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ToolsAndCropVersionsViewTest {
	
	private static ToolsAndCropVersionsView toolsAndCropVersionsView;
	private static final SimpleResourceBundleMessageSource messageSource = 
			MessageResourceUtil.getMessageResource();
	private static WorkbenchDataManager workbenchDataManager;
	
	@BeforeClass
	public static void setUp() throws Exception {
		workbenchDataManager = mock(WorkbenchDataManager.class);
		doReturn(getAllCrops()).when(workbenchDataManager).getInstalledCentralCrops();
		doReturn(getAllTools()).when(workbenchDataManager).getAllTools();
		toolsAndCropVersionsView = new ToolsAndCropVersionsView();
		toolsAndCropVersionsView.setMessageSource(messageSource);
		toolsAndCropVersionsView.setWorkbenchDataManager(workbenchDataManager);
		toolsAndCropVersionsView.afterPropertiesSet();
	}
	
	private static List<Tool> getAllTools() {
		List<Tool> tools = new ArrayList<Tool>();
		Tool tool = new Tool(ToolName.gdms.name(), "GDMS", "GDMS/login.do");
		tool.setToolId(9L);
		tools.add(tool);
		return tools;
	}

	private static List<CropType> getAllCrops() {
		List<CropType> cropTypes = new ArrayList<CropType>();
		CropType cropType = new CropType();
		cropType.setCropName("maize");
		cropType.setDbName("ibdbv2_maize_merged");
		cropType.setVersion("4.0.0");
		cropTypes.add(cropType);
		return cropTypes;
	}
	
	@Test
	public void testCheckIfGDMSIsDisplayed() {
		String value = "gdms";
		boolean isFound = VaadinComponentsUtil.findComponent(toolsAndCropVersionsView,
				VaadinComponentFieldType.TABLE_CONTENT, value, "toolName");
		Assert.assertTrue(value+" is not found",isFound);
	}
}

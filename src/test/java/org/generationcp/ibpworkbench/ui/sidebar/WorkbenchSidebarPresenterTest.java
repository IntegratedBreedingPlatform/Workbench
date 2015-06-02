package org.generationcp.ibpworkbench.ui.sidebar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkbenchSidebarPresenterTest {

	
	private static WorkbenchSidebarPresenter workbenchSidebarPresenter;
	private static WorkbenchDataManager manager;
	private static WorkbenchSidebarCategory adminCategory;
	
	@BeforeClass
	public static void setUpClass() throws MiddlewareQueryException {
		manager = mock(WorkbenchDataManager.class);
		adminCategory = new WorkbenchSidebarCategory(
				"admin","Program Administration");
		workbenchSidebarPresenter = new WorkbenchSidebarPresenter(null);
		workbenchSidebarPresenter.setManager(manager);
		doReturn(createDataImportTool()).when(manager).
			getToolWithName(ToolEnum.DATASET_IMPORTER.getToolName());
	}
	
	private static Tool createDataImportTool() {
		return new Tool("dataset_importer","Data Import Tool","DatasetImporter/");
	}

	@Test
	public void testAddAdminCategoryLinks() throws MiddlewareQueryException {
		//default with no backup and restore
		workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("false");
		List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<WorkbenchSidebarCategoryLink>();
		
		workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks, adminCategory);
		for (WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			assertFalse("recovery".equals(workbenchSidebarCategoryLink.getSidebarLinkName()));
		}
	}
	
	@Test
	public void testAddAdminCategoryLinks_WithBackAndRestore() throws MiddlewareQueryException {
		//default with no backup and restore
		workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("true");
		List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<WorkbenchSidebarCategoryLink>();
		WorkbenchSidebarCategory adminCategory = new WorkbenchSidebarCategory(
				"admin","Program Administration");
		workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks, adminCategory);
		boolean hasBackupAndRestore = false;
		for (WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			if("recovery".equals(workbenchSidebarCategoryLink.getSidebarLinkName())) {
				hasBackupAndRestore = true;
			}
		}
		assertTrue(hasBackupAndRestore);
	}
}

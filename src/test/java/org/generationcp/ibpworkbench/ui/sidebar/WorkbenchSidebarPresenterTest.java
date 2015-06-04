
package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class WorkbenchSidebarPresenterTest {

	private static WorkbenchSidebarPresenter workbenchSidebarPresenter;
	private static WorkbenchDataManager manager;
	private static WorkbenchSidebarCategory adminCategory;

	@BeforeClass
	public static void setUpClass() throws MiddlewareQueryException {
		WorkbenchSidebarPresenterTest.manager = Mockito.mock(WorkbenchDataManager.class);
		WorkbenchSidebarPresenterTest.adminCategory = new WorkbenchSidebarCategory("admin", "Program Administration");
		WorkbenchSidebarPresenterTest.workbenchSidebarPresenter = new WorkbenchSidebarPresenter(null);
		WorkbenchSidebarPresenterTest.workbenchSidebarPresenter.setManager(WorkbenchSidebarPresenterTest.manager);
		Mockito.doReturn(WorkbenchSidebarPresenterTest.createDataImportTool()).when(WorkbenchSidebarPresenterTest.manager)
				.getToolWithName(ToolEnum.DATASET_IMPORTER.getToolName());
	}

	private static Tool createDataImportTool() {
		return new Tool("dataset_importer", "Data Import Tool", "DatasetImporter/");
	}

	@Test
	public void testAddAdminCategoryLinks() throws MiddlewareQueryException {
		// default with no backup and restore
		WorkbenchSidebarPresenterTest.workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("false");
		List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<WorkbenchSidebarCategoryLink>();

		WorkbenchSidebarPresenterTest.workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks,
				WorkbenchSidebarPresenterTest.adminCategory);
		for (WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			Assert.assertFalse("recovery".equals(workbenchSidebarCategoryLink.getSidebarLinkName()));
		}
	}

	@Test
	public void testAddAdminCategoryLinks_WithBackAndRestore() throws MiddlewareQueryException {
		// default with no backup and restore
		WorkbenchSidebarPresenterTest.workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("true");
		List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<WorkbenchSidebarCategoryLink>();
		WorkbenchSidebarCategory adminCategory = new WorkbenchSidebarCategory("admin", "Program Administration");
		WorkbenchSidebarPresenterTest.workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks, adminCategory);
		boolean hasBackupAndRestore = false;
		for (WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			if ("recovery".equals(workbenchSidebarCategoryLink.getSidebarLinkName())) {
				hasBackupAndRestore = true;
			}
		}
		Assert.assertTrue(hasBackupAndRestore);
	}
}

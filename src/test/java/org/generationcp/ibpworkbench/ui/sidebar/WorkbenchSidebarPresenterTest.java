package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class WorkbenchSidebarPresenterTest {

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private UsernamePasswordAuthenticationToken loggedInUser;

	@InjectMocks
	private final WorkbenchSidebarPresenter workbenchSidebarPresenter = new WorkbenchSidebarPresenter();

	private static WorkbenchDataManager manager;
	private static WorkbenchSidebarCategory adminCategory;

	@Before
	public void setUp() throws MiddlewareQueryException {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn("Dummy Message").when(this.messageSource).getMessage(Mockito.anyString());
		WorkbenchSidebarPresenterTest.manager = Mockito.mock(WorkbenchDataManager.class);
		WorkbenchSidebarPresenterTest.adminCategory = new WorkbenchSidebarCategory("admin", "Program Administration");
		this.workbenchSidebarPresenter.setManager(WorkbenchSidebarPresenterTest.manager);
		Mockito.doReturn(WorkbenchSidebarPresenterTest.createDataImportTool()).when(WorkbenchSidebarPresenterTest.manager)
				.getToolWithName(ToolEnum.DATASET_IMPORTER.getToolName());

	}

	private static Tool createDataImportTool() {
		return new Tool("dataset_importer", "Data Import Tool", "DatasetImporter/");
	}

	@Test
	public void testAddAdminCategoryLinks() throws MiddlewareQueryException {
		// default with no backup and restore
		this.workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("false");
		final List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<>();

		this.workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks, WorkbenchSidebarPresenterTest.adminCategory);
		for (final WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			Assert.assertFalse("recovery".equals(workbenchSidebarCategoryLink.getSidebarLinkName()));
		}
	}

	@Test
	public void testAddAdminCategoryLinks_WithBackAndRestore() throws MiddlewareQueryException {
		// default with no backup and restore
		this.workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("true");
		final List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<>();
		final WorkbenchSidebarCategory adminCategory = new WorkbenchSidebarCategory("admin", "Program Administration");
		this.workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks, adminCategory);
		boolean hasBackupAndRestore = false;
		for (final WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			if ("recovery".equals(workbenchSidebarCategoryLink.getSidebarLinkName())) {
				hasBackupAndRestore = true;
			}
		}
		Assert.assertTrue(hasBackupAndRestore);
	}

	@Test
	public void testIsCategoryLinkPermissibleForUserRoleWithAdminAndPermissibleRolesAdmin() throws Exception {
		this.workbenchSidebarPresenter.setImportGermplasmPermissibleRoles("Admin");

		final WorkbenchSidebarCategoryLink link = new WorkbenchSidebarCategoryLink();
		link.setSidebarLinkName("germplasm_import");
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX+"ADMIN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("admin", "admin", Lists.newArrayList(roleAuthority));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(this.loggedInUser);
		SecurityContextHolder.setContext(securityContext);

		boolean categoryLinkPermissibleForUserRole = this.workbenchSidebarPresenter.isCategoryLinkPermissibleForUserRole(link);
		Assert.assertTrue("Germplasm Import link should be added in Workbench sidebar and should return true",
				categoryLinkPermissibleForUserRole);
	}

	@Test
	public void testIsCategoryLinkPermissibleForUserRoleWithAdminAndPermissibleRolesTechnician() throws Exception {
		this.workbenchSidebarPresenter.setImportGermplasmPermissibleRoles("Technician");
		SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX+"ADMIN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("technician", "technician", Lists.newArrayList(roleAuthority));
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(this.loggedInUser);
		SecurityContextHolder.setContext(securityContext);

		final WorkbenchSidebarCategoryLink link = new WorkbenchSidebarCategoryLink();
		link.setSidebarLinkName("germplasm_import");

		boolean categoryLinkPermissibleForUserRole = this.workbenchSidebarPresenter.isCategoryLinkPermissibleForUserRole(link);
		Assert.assertFalse("Germplasm Import link should not be added in Workbench sidebar and should return false",
				categoryLinkPermissibleForUserRole);
	}
}

package org.generationcp.ibpworkbench.ui.sidebar;

import com.google.common.collect.Lists;
import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkbenchSidebarPresenterTest {

	private static final Integer USER_ID = 100;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ProjectUserInfoDAO projectUserInfoDAO;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private PlatformTransactionManager transactionManager;

	private Project selectedProgram;

	private ProjectUserInfo projectUserInfo;

	private UsernamePasswordAuthenticationToken loggedInUser;

	@InjectMocks
	private final WorkbenchSidebarPresenter workbenchSidebarPresenter = new WorkbenchSidebarPresenter();

	private WorkbenchSidebarCategory adminCategory;
	private WorkbenchSidebarCategory categoryWithNoLinks;
	private WorkbenchSidebarCategory activitiesCategory;
	private WorkbenchSidebarCategory infoMgtCategory;
	private final List<WorkbenchSidebarCategory> sidebarCategories = new ArrayList<>();
	private final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> sidebarLinksFromDB = new HashMap<>();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		this.workbenchSidebarPresenter.setManager(this.workbenchDataManager);
		this.setupTestSidebarLinks();

		// Setup test data
		this.selectedProgram = ProjectTestDataInitializer.createProject();
		this.projectUserInfo = new ProjectUserInfo();
		this.projectUserInfo.setProject(this.selectedProgram);
		this.projectUserInfo.setUserId(WorkbenchSidebarPresenterTest.USER_ID);

		// Setup Mock objects to return
		Mockito.doReturn(WorkbenchSidebarPresenterTest.USER_ID).when(this.contextUtil).getCurrentWorkbenchUserId();
		Mockito.doReturn(this.selectedProgram).when(this.contextUtil).getProjectInContext();
		Mockito.doReturn(this.projectUserInfoDAO).when(this.workbenchDataManager).getProjectUserInfoDao();
		Mockito.doReturn(this.projectUserInfo).when(this.projectUserInfoDAO).getByProjectIdAndUserId(Matchers.anyLong(), Matchers.anyInt());

	}

	private void setupTestSidebarLinks() {
		Mockito.doReturn("Dummy Message").when(this.messageSource).getMessage(Matchers.anyString());
		this.adminCategory = new WorkbenchSidebarCategory(WorkbenchSidebarPresenter.ADMIN_CATEGORY, "Program Administration");
		this.adminCategory.setSidebarCategoryId(1);
		this.categoryWithNoLinks = new WorkbenchSidebarCategory("tools", "Breeding Tools");
		this.categoryWithNoLinks.setSidebarCategoryId(2);
		this.activitiesCategory = new WorkbenchSidebarCategory("activities", "Breeding Activities");
		this.activitiesCategory.setSidebarCategoryId(3);
		this.infoMgtCategory = new WorkbenchSidebarCategory("info_mgt", "Information Management");
		this.infoMgtCategory.setSidebarCategoryId(4);
		this.sidebarCategories
				.addAll(Arrays.asList(this.adminCategory, this.categoryWithNoLinks, this.activitiesCategory, this.infoMgtCategory));

		this.sidebarLinksFromDB.put(this.activitiesCategory, Arrays.asList(
				new WorkbenchSidebarCategoryLink(new Tool(ToolEnum.BM_LIST_MANAGER.toString(), "manage_germplasm", "/ManageGermplasm"),
						this.activitiesCategory, "manage_list", "Manage Germplasm"),
				new WorkbenchSidebarCategoryLink(new Tool(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB.toString(), "trial_mgr", "/TrialManager"),
						this.activitiesCategory, "trial_manager", "Trial Manager")));
		this.sidebarLinksFromDB.put(this.infoMgtCategory, Arrays.asList(
				new WorkbenchSidebarCategoryLink(new Tool(ToolEnum.GDMS.toString(), "gdms", "/GDMS"), this.infoMgtCategory, "gdms",
						"Genotyping Data Management"),
				new WorkbenchSidebarCategoryLink(new Tool(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB.toString(), "h2h", "/H2HMain"),
						this.infoMgtCategory, "h2h", "Head To Head Query")));
		Mockito.doReturn(this.sidebarCategories).when(this.workbenchDataManager).getAllWorkbenchSidebarCategory();
		Mockito.doReturn(this.sidebarLinksFromDB.get(this.activitiesCategory)).when(this.workbenchDataManager)
				.getAllWorkbenchSidebarLinksByCategoryId(this.activitiesCategory);
		Mockito.doReturn(this.sidebarLinksFromDB.get(this.infoMgtCategory)).when(this.workbenchDataManager)
				.getAllWorkbenchSidebarLinksByCategoryId(this.infoMgtCategory);
	}

	@Test
	public void testAddAdminCategoryLinks() {
		// default with no backup and restore
		this.workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("false");
		final List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<>();

		this.workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks, this.adminCategory);
		for (final WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			Assert.assertFalse(WorkbenchSidebarPresenter.RECOVERY_LINK.equals(workbenchSidebarCategoryLink.getSidebarLinkName()));
		}
	}

	@Test
	public void testAddAdminCategoryLinksWithBackAndRestore() {
		// default with no backup and restore
		this.workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("true");
		final List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<>();
		this.workbenchSidebarPresenter.addAdminCategoryLinks(categoryLinks, this.adminCategory);
		boolean hasBackupAndRestore = false;
		for (final WorkbenchSidebarCategoryLink workbenchSidebarCategoryLink : categoryLinks) {
			if (WorkbenchSidebarPresenter.RECOVERY_LINK.equals(workbenchSidebarCategoryLink.getSidebarLinkName())) {
				hasBackupAndRestore = true;
			}
		}
		Assert.assertTrue(hasBackupAndRestore);
	}

	@Test
	public void testIsCategoryLinkPermissibleForUserRoleWithAdminAndPermissibleRolesAdmin() {
		this.workbenchSidebarPresenter.setImportGermplasmPermissibleRoles("Admin");

		final WorkbenchSidebarCategoryLink link = new WorkbenchSidebarCategoryLink();
		link.setSidebarLinkName("germplasm_import");
		final SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "ADMIN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("admin", "admin", Lists.newArrayList(roleAuthority));
		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(this.loggedInUser);
		SecurityContextHolder.setContext(securityContext);

		final boolean categoryLinkPermissibleForUserRole = this.workbenchSidebarPresenter.isCategoryLinkPermissibleForUserRole(link);
		Assert.assertTrue("Germplasm Import link should be added in Workbench sidebar and should return true",
				categoryLinkPermissibleForUserRole);
	}

	@Test
	public void testIsCategoryLinkPermissibleForUserRoleWithAdminAndPermissibleRolesTechnician() {
		this.workbenchSidebarPresenter.setImportGermplasmPermissibleRoles("Technician");
		final SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "ADMIN");

		this.loggedInUser = new UsernamePasswordAuthenticationToken("technician", "technician", Lists.newArrayList(roleAuthority));
		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(this.loggedInUser);
		SecurityContextHolder.setContext(securityContext);

		final WorkbenchSidebarCategoryLink link = new WorkbenchSidebarCategoryLink();
		link.setSidebarLinkName("germplasm_import");

		final boolean categoryLinkPermissibleForUserRole = this.workbenchSidebarPresenter.isCategoryLinkPermissibleForUserRole(link);
		Assert.assertFalse("Germplasm Import link should not be added in Workbench sidebar and should return false",
				categoryLinkPermissibleForUserRole);
	}

	@Test
	public void testGetCategoryLinkItems() {
		this.workbenchSidebarPresenter.setIsBackupAndRestoreEnabled("true");
		final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> linksMap =
				this.workbenchSidebarPresenter.getCategoryLinkItems();

		// Expecting categories without links to not be included
		Assert.assertEquals(this.sidebarCategories.size() - 1, linksMap.keySet().size());
		final List<WorkbenchSidebarCategoryLink> adminLinks = linksMap.get(this.adminCategory);
		Assert.assertNotNull(adminLinks);
		// "ADMIN" links are hardcoded and not from DB
		Assert.assertEquals(3, adminLinks.size());
		boolean manageProgramLinkFound = false;
		boolean backupRestoreLinkFound = false;
		boolean aboutBMSLinkFound = false;
		for (final WorkbenchSidebarCategoryLink link : adminLinks) {
			if (WorkbenchSidebarPresenter.MANAGE_PROGRAM_LINK.equals(link.getSidebarLinkName())) {
				manageProgramLinkFound = true;
			} else if (WorkbenchSidebarPresenter.RECOVERY_LINK.equals(link.getSidebarLinkName())) {
				backupRestoreLinkFound = true;
			} else if (WorkbenchSidebarPresenter.ABOUT_BMS_LINK.equals(link.getSidebarLinkName())) {
				aboutBMSLinkFound = true;
			}
		}
		Assert.assertTrue(manageProgramLinkFound);
		Assert.assertTrue(backupRestoreLinkFound);
		Assert.assertTrue(aboutBMSLinkFound);
		Assert.assertNotNull(linksMap.get(this.activitiesCategory));
		Assert.assertNotNull(linksMap.get(this.infoMgtCategory));
		Assert.assertEquals(this.sidebarLinksFromDB.get(this.activitiesCategory), linksMap.get(this.activitiesCategory));
		Assert.assertEquals(this.sidebarLinksFromDB.get(this.infoMgtCategory), linksMap.get(this.infoMgtCategory));
	}

	@Test
	public void testUpdateProjectLastOpenedDate() {
		this.workbenchSidebarPresenter.updateProjectLastOpenedDate();

		final Date currentDate = new Date();
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).saveOrUpdateProjectUserInfo(this.projectUserInfo);
		final Date userLastOpenDate = this.projectUserInfo.getLastOpenDate();
		Assert.assertEquals(currentDate.getYear(), userLastOpenDate.getYear());
		Assert.assertEquals(currentDate.getMonth(), userLastOpenDate.getMonth());
		Assert.assertEquals(currentDate.getDate(), userLastOpenDate.getDate());
		Mockito.verify(this.workbenchDataManager, Mockito.times(1)).mergeProject(this.selectedProgram);
		final Date lastOpenDate = this.selectedProgram.getLastOpenDate();
		Assert.assertEquals(currentDate.getYear(), lastOpenDate.getYear());
		Assert.assertEquals(currentDate.getMonth(), lastOpenDate.getMonth());
		Assert.assertEquals(currentDate.getDate(), lastOpenDate.getDate());
	}

}

package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLinkRole;
import org.generationcp.middleware.service.api.user.UserService;
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

import com.google.common.collect.Lists;

public class WorkbenchSidebarPresenterTest {

	private static final Integer USER_ID = 100;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private UserService userService;

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
		Mockito.doReturn(this.projectUserInfo).when(this.userService).getProjectUserInfoByProjectIdAndUserId(Matchers.anyLong(), Matchers.anyInt());

	}

	private void setupTestSidebarLinks() {
		Mockito.doReturn("Dummy Message").when(this.messageSource).getMessage(Matchers.anyString());
		this.adminCategory = new WorkbenchSidebarCategory("Admin", "Program Administration");
		this.adminCategory.setSidebarCategoryId(1);
		this.categoryWithNoLinks = new WorkbenchSidebarCategory("tools", "Breeding Tools");
		this.categoryWithNoLinks.setSidebarCategoryId(2);
		this.activitiesCategory = new WorkbenchSidebarCategory("activities", "Breeding Activities");
		this.activitiesCategory.setSidebarCategoryId(3);
		this.infoMgtCategory = new WorkbenchSidebarCategory("info_mgt", "Information Management");
		this.infoMgtCategory.setSidebarCategoryId(4);
		this.sidebarCategories
				.addAll(Arrays.asList(this.adminCategory, this.categoryWithNoLinks, this.activitiesCategory, this.infoMgtCategory));

		// Breeding Activities Links
		final WorkbenchSidebarCategoryLink manageGermplasmLink = new WorkbenchSidebarCategoryLink(new Tool(ToolName.BM_LIST_MANAGER_MAIN.toString(), "manage_germplasm", "/ManageGermplasm"),
				this.activitiesCategory, "manage_list", "Manage Germplasm");
		manageGermplasmLink.setRoles(Arrays.asList(new WorkbenchSidebarCategoryLinkRole(manageGermplasmLink, new Role(1, "Admin")),
				new WorkbenchSidebarCategoryLinkRole(manageGermplasmLink, new Role(2, "Breeder"))));
		final WorkbenchSidebarCategoryLink trialManagerLink = new WorkbenchSidebarCategoryLink(new Tool(ToolName.TRIAL_MANAGER_FIELDBOOK_WEB.toString(), "trial_mgr", "/TrialManager"),
				this.activitiesCategory, "trial_manager", "Trial Manager");
		trialManagerLink.setRoles(Arrays.asList(new WorkbenchSidebarCategoryLinkRole(trialManagerLink, new Role(1, "Admin"))));
		this.sidebarLinksFromDB.put(this.activitiesCategory, Arrays.asList(manageGermplasmLink, trialManagerLink));

		// Info Management Links
		final WorkbenchSidebarCategoryLink gdmsLink = new WorkbenchSidebarCategoryLink(new Tool(ToolName.GDMS.toString(), "gdms", "/GDMS"), this.infoMgtCategory, "gdms",
				"Genotyping Data Management");
		gdmsLink.setRoles(Arrays.asList(new WorkbenchSidebarCategoryLinkRole(gdmsLink, new Role(1, "Admin"))));
		final WorkbenchSidebarCategoryLink h2hLink = new WorkbenchSidebarCategoryLink(new Tool(ToolName.TRIAL_MANAGER_FIELDBOOK_WEB.toString(), "h2h", "/H2HMain"),
				this.infoMgtCategory, "h2h", "Head To Head Query");
		h2hLink.setRoles(Arrays.asList(new WorkbenchSidebarCategoryLinkRole(h2hLink, new Role(1, "Admin")),
				new WorkbenchSidebarCategoryLinkRole(h2hLink, new Role(2, "Breeder"))));
		this.sidebarLinksFromDB.put(this.infoMgtCategory, Arrays.asList(gdmsLink, h2hLink));

		// Program Admin links
		final WorkbenchSidebarCategoryLink manageProgramLink = new WorkbenchSidebarCategoryLink(new Tool(ToolName.BM_LIST_MANAGER_MAIN.toString(), "manage_germplasm", "/ManageGermplasm"),
				this.adminCategory, "manage_program", "Manage Program");
		manageProgramLink.setRoles(Arrays.asList(new WorkbenchSidebarCategoryLinkRole(manageProgramLink, new Role(1, "Admin"))));
		final WorkbenchSidebarCategoryLink backupRestoreLink = new WorkbenchSidebarCategoryLink(new Tool(ToolName.TRIAL_MANAGER_FIELDBOOK_WEB.toString(), "trial_mgr", "/TrialManager"),
				this.adminCategory, "backup_restore", "Backup and Restore");
		backupRestoreLink.setRoles(new ArrayList<WorkbenchSidebarCategoryLinkRole>());
		this.sidebarLinksFromDB.put(this.adminCategory, Arrays.asList(manageProgramLink, backupRestoreLink));

		Mockito.doReturn(this.sidebarCategories).when(this.workbenchDataManager).getAllWorkbenchSidebarCategory();
		Mockito.doReturn(this.sidebarLinksFromDB.get(this.activitiesCategory)).when(this.workbenchDataManager)
				.getAllWorkbenchSidebarLinksByCategoryId(this.activitiesCategory);
		Mockito.doReturn(this.sidebarLinksFromDB.get(this.infoMgtCategory)).when(this.workbenchDataManager)
				.getAllWorkbenchSidebarLinksByCategoryId(this.infoMgtCategory);
		Mockito.doReturn(this.sidebarLinksFromDB.get(this.adminCategory)).when(this.workbenchDataManager)
		.getAllWorkbenchSidebarLinksByCategoryId(this.adminCategory);
	}

	@Test
	public void testGetCategoryLinkItemsForAdmin() {
		final SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "ADMIN");
		this.loggedInUser = new UsernamePasswordAuthenticationToken("admin", "admin", Lists.newArrayList(roleAuthority));
		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(this.loggedInUser);
		SecurityContextHolder.setContext(securityContext);

		final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> linksMap =
				this.workbenchSidebarPresenter.getCategoryLinkItems();

		// Expecting categories without links to not be included
		Assert.assertEquals(this.sidebarCategories.size() - 1, linksMap.keySet().size());
		Assert.assertNull(linksMap.get(this.categoryWithNoLinks));

		final List<WorkbenchSidebarCategoryLink> adminLinks = linksMap.get(this.adminCategory);
		Assert.assertNotNull(adminLinks);
		// Expecting backup restore not to be included as no role configured to access it
		Assert.assertEquals(1, adminLinks.size());
		Assert.assertNotNull(linksMap.get(this.activitiesCategory));
		Assert.assertNotNull(linksMap.get(this.infoMgtCategory));
		Assert.assertEquals(this.sidebarLinksFromDB.get(this.activitiesCategory), linksMap.get(this.activitiesCategory));
		Assert.assertEquals(this.sidebarLinksFromDB.get(this.infoMgtCategory), linksMap.get(this.infoMgtCategory));
	}

	@Test
	public void testGetCategoryLinkItemsForBreeder() {
		final SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority(SecurityUtil.ROLE_PREFIX + "BREEDER");
		this.loggedInUser = new UsernamePasswordAuthenticationToken("breeder", "breeder", Lists.newArrayList(roleAuthority));
		final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(this.loggedInUser);
		SecurityContextHolder.setContext(securityContext);

		final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> linksMap =
				this.workbenchSidebarPresenter.getCategoryLinkItems();

		// Expecting categories without links and with no links he has access to to not be included
		Assert.assertEquals(this.sidebarCategories.size() - 2, linksMap.keySet().size());
		Assert.assertNull(linksMap.get(this.categoryWithNoLinks));
		Assert.assertNull(linksMap.get(this.adminCategory));

		final List<WorkbenchSidebarCategoryLink> activitiesLinks = linksMap.get(this.activitiesCategory);
		Assert.assertNotNull(activitiesLinks);
		Assert.assertEquals(1, activitiesLinks.size());
		final List<WorkbenchSidebarCategoryLink> infoLinks = linksMap.get(this.infoMgtCategory);
		Assert.assertNotNull(infoLinks);
		Assert.assertEquals(1, infoLinks.size());
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

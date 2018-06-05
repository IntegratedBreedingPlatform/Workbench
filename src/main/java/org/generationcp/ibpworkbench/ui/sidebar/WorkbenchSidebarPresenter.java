package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.security.AuthorizationUtil;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLinkRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Configurable
public class WorkbenchSidebarPresenter implements InitializingBean {

	protected static final String ADMIN_CATEGORY = "admin";

	protected static final String ABOUT_BMS_LINK = "about_bms";

	protected static final String RECOVERY_LINK = "recovery";

	protected static final String MANAGE_PROGRAM_LINK = "manage_program";

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebarPresenter.class);

	@Autowired
	private WorkbenchDataManager manager;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	public WorkbenchSidebarPresenter() {
		// does nothing
	}

	/**
	 * afterPropertiesSet() is called after Aspect4J weaves spring objects when this class is instantiated since this class is
	 * a @configurable that implements InitializingBean. Since we do not have any need for additional initialization after the weaving, this
	 * method remains unimplemented.
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// No values are required to be initialized for this layout
	}

	public WorkbenchDataManager getManager() {
		return this.manager;
	}

	public Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> getCategoryLinkItems() {
		// get all categories first
		final Map<WorkbenchSidebarCategory, List<WorkbenchSidebarCategoryLink>> sidebarLinks = new LinkedHashMap<>();

		final List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<>();

		final List<WorkbenchSidebarCategory> workbenchSidebarCategoryList = this.manager.getAllWorkbenchSidebarCategory();

		for (final WorkbenchSidebarCategory category : workbenchSidebarCategoryList) {
			if (isAdminCategory(category)) {
				this.addAdminCategoryLinks(categoryLinks, category);
			} else {
				categoryLinks.addAll(this.manager.getAllWorkbenchSidebarLinksByCategoryId(category));
			}
		}
		
		for (final WorkbenchSidebarCategoryLink link : categoryLinks) {
			// For now, we don't check user roles for ADMIN category links 
			// since we add its links manually in this class and not retrieved from the DB
			if (this.isAdminCategory(link.getWorkbenchSidebarCategory()) || 
					isCategoryLinkPermissibleForUserRole(link)) {
				if (sidebarLinks.get(link.getWorkbenchSidebarCategory()) == null) {
					sidebarLinks.put(link.getWorkbenchSidebarCategory(), new ArrayList<WorkbenchSidebarCategoryLink>());
				}
				if (link.getTool() == null) {
					link.setTool(new Tool(link.getSidebarLinkName(), link.getSidebarLinkTitle(), ""));
				}
				sidebarLinks.get(link.getWorkbenchSidebarCategory()).add(link);
			}
		}

		return sidebarLinks;
	}

	private boolean isAdminCategory(final WorkbenchSidebarCategory category) {
		return ADMIN_CATEGORY.equals(category.getSidebarCategoryName());
	}

	protected boolean isCategoryLinkPermissibleForUserRole(final WorkbenchSidebarCategoryLink link) {
		final List<Role> permittedRoles = new ArrayList<>();
		final List<WorkbenchSidebarCategoryLinkRole> sidebarRoles = link.getRoles();
		for (final WorkbenchSidebarCategoryLinkRole sidebarRole : sidebarRoles) {
			permittedRoles.add(sidebarRole.getRole());
		}

		try {
			AuthorizationUtil.preAuthorize(permittedRoles);
		} catch (final AccessDeniedException ex) {
			LOG.debug(ex.getMessage(), ex);
			return false;
		}

		return true;
	}

	protected void addAdminCategoryLinks(final List<WorkbenchSidebarCategoryLink> categoryLinks, final WorkbenchSidebarCategory category) {
		categoryLinks.add(new WorkbenchSidebarCategoryLink(null, category, MANAGE_PROGRAM_LINK,
				this.messageSource.getMessage("LINK_MANAGE_SETTINGS")));
		categoryLinks.add(new WorkbenchSidebarCategoryLink(null, category, RECOVERY_LINK,
				this.messageSource.getMessage("LINK_BACKUP_RESTORE")));
		categoryLinks
				.add(new WorkbenchSidebarCategoryLink(null, category, ABOUT_BMS_LINK, this.messageSource.getMessage("LINK_ABOUT_BMS")));
	}

	public void updateProjectLastOpenedDate() {

		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			protected void doInTransactionWithoutResult(final TransactionStatus status) {
				final Project project = contextUtil.getProjectInContext();

				final ProjectUserInfoDAO projectUserInfoDao = WorkbenchSidebarPresenter.this.manager.getProjectUserInfoDao();
				final ProjectUserInfo projectUserInfo =
						projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId(), contextUtil.getCurrentWorkbenchUserId());

				if (projectUserInfo != null) {
					projectUserInfo.setLastOpenDate(new Date());
					WorkbenchSidebarPresenter.this.manager.saveOrUpdateProjectUserInfo(projectUserInfo);
				}

				project.setLastOpenDate(new Date());
				WorkbenchSidebarPresenter.this.manager.mergeProject(project);

			}
		});

	}

	public void setManager(final WorkbenchDataManager manager) {
		this.manager = manager;
	}

}

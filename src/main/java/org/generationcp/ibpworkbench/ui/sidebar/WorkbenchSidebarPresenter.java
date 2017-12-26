package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.security.AuthorizationUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Configurable
public class WorkbenchSidebarPresenter implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebarPresenter.class);

	@Autowired
	private WorkbenchDataManager manager;

	@Autowired
	private SessionData sessionData;

	@Value("${workbench.is.backup.and.restore.enabled}")
	private String isBackupAndRestoreEnabled;

	@Value("${workbench.import.germplasm.permissible.roles}")
	private String importGermplasmPermissibleRoles;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	public WorkbenchSidebarPresenter() {
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

		try {
			final List<WorkbenchSidebarCategoryLink> categoryLinks = new ArrayList<>();

			final List<WorkbenchSidebarCategory> workbenchSidebarCategoryList = this.manager.getAllWorkbenchSidebarCategory();

			for (final WorkbenchSidebarCategory category : workbenchSidebarCategoryList) {
				if ("admin".equals(category.getSidebarCategoryName())) {
					this.addAdminCategoryLinks(categoryLinks, category);
				} else {
					categoryLinks.addAll(this.manager.getAllWorkbenchSidebarLinksByCategoryId(category));
				}
			}

			for (final WorkbenchSidebarCategoryLink link : categoryLinks) {
				if (isCategoryLinkPermissibleForUserRole(link)) {
					if (sidebarLinks.get(link.getWorkbenchSidebarCategory()) == null) {
						sidebarLinks.put(link.getWorkbenchSidebarCategory(), new ArrayList<WorkbenchSidebarCategoryLink>());
					}
					if (link.getTool() == null) {
						link.setTool(new Tool(link.getSidebarLinkName(), link.getSidebarLinkTitle(), ""));
					}
					sidebarLinks.get(link.getWorkbenchSidebarCategory()).add(link);
				}

			}
		} catch (final MiddlewareQueryException e) {
			WorkbenchSidebarPresenter.LOG.error(e.getMessage(), e);
		}
		return sidebarLinks;
	}

	protected boolean isCategoryLinkPermissibleForUserRole(final WorkbenchSidebarCategoryLink link) {
		if (ToolName.GERMPLASM_IMPORT.name().equalsIgnoreCase(link.getSidebarLinkName())) {
			try {
				AuthorizationUtil.preAuthorize(importGermplasmPermissibleRoles);
			} catch (AccessDeniedException ex) {
				return false;
			}

		}
		return true;
	}

	protected void addAdminCategoryLinks(final List<WorkbenchSidebarCategoryLink> categoryLinks, final WorkbenchSidebarCategory category) {
		categoryLinks.add(new WorkbenchSidebarCategoryLink(null, category, "manage_program",
				this.messageSource.getMessage("LINK_MANAGE_SETTINGS")));
		if (this.isBackupAndRestoreEnabled != null && Boolean.valueOf(this.isBackupAndRestoreEnabled)) {
			categoryLinks.add(new WorkbenchSidebarCategoryLink(null, category, "recovery",
					this.messageSource.getMessage("LINK_BACKUP_RESTORE")));
		}
		categoryLinks.add(new WorkbenchSidebarCategoryLink(null, category, "about_bms", this.messageSource.getMessage("LINK_ABOUT_BMS")));
	}

	public void updateProjectLastOpenedDate() {

		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			protected void doInTransactionWithoutResult(final TransactionStatus status) {
				// set the last opened project in the session
				final IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
				final Project project = app.getSessionData().getSelectedProject();

				final ProjectUserInfoDAO projectUserInfoDao = WorkbenchSidebarPresenter.this.manager.getProjectUserInfoDao();
				final ProjectUserInfo projectUserInfo = projectUserInfoDao
						.getByProjectIdAndUserId(project.getProjectId(), app.getSessionData().getUserData().getUserid());
				if (projectUserInfo != null) {
					projectUserInfo.setLastOpenDate(new Date());
					WorkbenchSidebarPresenter.this.manager.saveOrUpdateProjectUserInfo(projectUserInfo);
				}

				project.setLastOpenDate(new Date());
				WorkbenchSidebarPresenter.this.manager.mergeProject(project);

				app.getSessionData().setLastOpenedProject(project);
			}
		});

	}

	public void setIsBackupAndRestoreEnabled(final String isBackupAndRestoreEnabled) {
		this.isBackupAndRestoreEnabled = isBackupAndRestoreEnabled;
	}

	public void setManager(final WorkbenchDataManager manager) {
		this.manager = manager;
	}

	public void setImportGermplasmPermissibleRoles(String importGermplasmPermissibleRoles) {
		this.importGermplasmPermissibleRoles = importGermplasmPermissibleRoles;
	}
}

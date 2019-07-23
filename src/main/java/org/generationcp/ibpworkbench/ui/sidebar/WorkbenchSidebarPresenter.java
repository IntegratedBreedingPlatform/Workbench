package org.generationcp.ibpworkbench.ui.sidebar;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.security.AuthorizationUtil;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategory;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLink;
import org.generationcp.middleware.pojos.workbench.WorkbenchSidebarCategoryLinkRole;
import org.generationcp.middleware.service.api.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;

@Configurable
public class WorkbenchSidebarPresenter implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebarPresenter.class);

	@Autowired
	private WorkbenchDataManager manager;

	@Autowired
	private UserService userService;

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
			categoryLinks.addAll(this.manager.getAllWorkbenchSidebarLinksByCategoryId(category));
		}

		for (final WorkbenchSidebarCategoryLink link : categoryLinks) {
			if (this.isCategoryLinkPermissibleForUserRole(link)) {
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

	public void updateProjectLastOpenedDate() {
		final Project project = contextUtil.getProjectInContext();

		final ProjectUserInfo projectUserInfo =
				this.userService.getProjectUserInfoByProjectIdAndUserId(project.getProjectId(), contextUtil.getCurrentWorkbenchUserId());

		if (projectUserInfo != null) {
			projectUserInfo.setLastOpenDate(new Date());
			WorkbenchSidebarPresenter.this.manager.saveOrUpdateProjectUserInfo(projectUserInfo);
		}

		project.setLastOpenDate(new Date());
		WorkbenchSidebarPresenter.this.manager.mergeProject(project);
	}

	public void setManager(final WorkbenchDataManager manager) {
		this.manager = manager;
	}

}

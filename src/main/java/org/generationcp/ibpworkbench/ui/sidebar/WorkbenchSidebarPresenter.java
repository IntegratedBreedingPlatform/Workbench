package org.generationcp.ibpworkbench.ui.sidebar;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Date;

@Configurable
public class WorkbenchSidebarPresenter implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchSidebarPresenter.class);

	@Autowired
	private WorkbenchDataManager manager;

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

	public void updateProjectLastOpenedDate() {
		final Project project = this.contextUtil.getProjectInContext();

		final ProjectUserInfoDAO projectUserInfoDao = this.manager.getProjectUserInfoDao();
		final ProjectUserInfo projectUserInfo =
				projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId(), this.contextUtil.getCurrentWorkbenchUserId());

		if (projectUserInfo != null) {
			projectUserInfo.setLastOpenDate(new Date());
			this.manager.saveOrUpdateProjectUserInfo(projectUserInfo);
		}

		project.setLastOpenDate(new Date());
		this.manager.mergeProject(project);
	}

	public void setManager(final WorkbenchDataManager manager) {
		this.manager = manager;
	}

}

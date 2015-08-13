/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.dashboard.listener;

import java.util.Date;

import org.generationcp.commons.constant.ToolEnum;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.ibpworkbench.util.SchemaVersionUtil;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.dao.ProjectUserInfoDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;

/**
 * @author Efficio.Daniel
 *
 */
@Configurable
public class DashboardMainClickListener implements ClickListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 5742093045098439073L;
	private final Long projectId;
	private final Component source;

	@Autowired
	private WorkbenchDataManager manager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private SessionData sessionData;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private static final Logger LOG = LoggerFactory.getLogger(DashboardMainClickListener.class);

	public DashboardMainClickListener(Component source, Long projectId) {
		this.projectId = projectId;
		this.source = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.ClickEvent)
	 */
	@Override
	public void buttonClick(final ClickEvent event) {
		try {
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				protected void doInTransactionWithoutResult(TransactionStatus status) {

					// lets update last opened project
					Project project = DashboardMainClickListener.this.sessionData.getSelectedProject();
					String minimumCropVersion = SchemaVersionUtil.getMinimumCropVersion();
					String currentCropVersion = project.getCropType().getVersion();
					if (!SchemaVersionUtil.checkIfVersionIsSupported(currentCropVersion, minimumCropVersion)) {
						MessageNotifier.showWarning(event.getComponent().getWindow(), "", DashboardMainClickListener.this.messageSource
								.getMessage(Message.MINIMUM_CROP_VERSION_WARNING, currentCropVersion != null ? currentCropVersion
										: DashboardMainClickListener.this.messageSource.getMessage(Message.NOT_AVAILABLE)));
					}

					try {
						DashboardMainClickListener.this.toolUtil.createWorkspaceDirectoriesForProject(project);
					} catch (MiddlewareQueryException e) {
						throw new RuntimeException(e.getMessage(),e);
					}

					DashboardMainClickListener.this.updateProjectLastOpenedDate(project);

					// update sidebar selection
					DashboardMainClickListener.LOG.trace("selecting sidebar");
					WorkbenchMainView mainWindow = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();

					mainWindow.addTitle(project.getProjectName());

					if (null != WorkbenchSidebar.sidebarTreeMap.get("manage_list")) {
						mainWindow.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("manage_list"));
					}

					// page change to list manager, with parameter passed
					new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN).buttonClick(event);

				}
			});
		} catch (InternationalizableException e) {
			DashboardMainClickListener.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
		} catch (Exception e) {
			DashboardMainClickListener.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(event.getComponent().getWindow(), "", e.getLocalizedMessage());

		}

	}

	public void updateProjectLastOpenedDate(Project project) {
		try {

			// set the last opened project in the session
			IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

			ProjectUserInfoDAO projectUserInfoDao = this.manager.getProjectUserInfoDao();
			ProjectUserInfo projectUserInfo =
					projectUserInfoDao.getByProjectIdAndUserId(project.getProjectId().intValue(), app.getSessionData().getUserData()
							.getUserid());
			if (projectUserInfo != null) {
				projectUserInfo.setLastOpenDate(new Date());
				this.manager.saveOrUpdateProjectUserInfo(projectUserInfo);
			}

			project.setLastOpenDate(new Date());
			this.manager.mergeProject(project);

			app.getSessionData().setLastOpenedProject(project);

		} catch (MiddlewareQueryException e) {
			DashboardMainClickListener.LOG.error(e.toString(), e);
		}
	}
}

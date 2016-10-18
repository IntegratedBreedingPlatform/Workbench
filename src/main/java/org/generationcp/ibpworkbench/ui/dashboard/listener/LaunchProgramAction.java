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

import javax.swing.ButtonModel;

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

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;

/**
 * @author Efficio.Daniel
 *
 */
@Configurable
public class LaunchProgramAction implements ItemClickListener, ClickListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 5742093045098439073L;

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

	private static final Logger LOG = LoggerFactory.getLogger(LaunchProgramAction.class);
	
	private Project selectedProgram;
	
	public LaunchProgramAction(){
		super();
	}
	
	public LaunchProgramAction(final Project selectedProgram){
		this.selectedProgram = selectedProgram;
	}

	void openSelectedProgram(final Project project, final Window window) {
		try {
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);

			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				protected void doInTransactionWithoutResult(TransactionStatus status) {

					// Sets selected program/project to session
					LaunchProgramAction.this.sessionData.setSelectedProject(project);
					
					// Warn if the selected program's crop is outdated
					String minimumCropVersion = SchemaVersionUtil.getMinimumCropVersion();
					String currentCropVersion = project.getCropType().getVersion();
					if (!SchemaVersionUtil.checkIfVersionIsSupported(currentCropVersion, minimumCropVersion)) {
						MessageNotifier.showWarning(window, "", LaunchProgramAction.this.messageSource
								.getMessage(Message.MINIMUM_CROP_VERSION_WARNING, currentCropVersion != null ? currentCropVersion
										: LaunchProgramAction.this.messageSource.getMessage(Message.NOT_AVAILABLE)));
					}

					LaunchProgramAction.this.updateProjectLastOpenedDate(project);

					// Set project name to header
					WorkbenchMainView workbenchMainView = (WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow();
					workbenchMainView.addTitle(project.getProjectName());

					// FIXME - why are we populating the sidebar everytime we change projects?
					workbenchMainView.getSidebar().populateLinks();
					
					// update sidebar selection
					LaunchProgramAction.LOG.trace("selecting sidebar");
					if (null != WorkbenchSidebar.sidebarTreeMap.get("manage_list")) {
						workbenchMainView.getSidebar().selectItem(WorkbenchSidebar.sidebarTreeMap.get("manage_list"));
					}

					// page change to list manager, with parameter passed
					new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER_MAIN).onAppLaunch(window);

				}
			});
		} catch (InternationalizableException e) {
			LaunchProgramAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(window, e.getCaption(), e.getDescription());
		} catch (Exception e) {
			LaunchProgramAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(window, "", e.getLocalizedMessage());

		}
	}

	void updateProjectLastOpenedDate(Project project) {
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
			LaunchProgramAction.LOG.error(e.toString(), e);
		}
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		final Project project = (Project) event.getItemId();
		openSelectedProgram(project, event.getComponent().getWindow());
	}

	@Override
	public void buttonClick(ClickEvent event) {
		if (this.selectedProgram != null){
			openSelectedProgram(this.selectedProgram, event.getComponent().getWindow());
		}
		
	}
}

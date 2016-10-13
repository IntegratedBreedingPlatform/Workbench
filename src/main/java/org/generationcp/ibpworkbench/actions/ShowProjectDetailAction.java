/*******************************************************************************
 * /******************************************************************************* Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.ProjectTableCellStyleGenerator;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class ShowProjectDetailAction implements Property.ValueChangeListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ShowProjectDetailAction.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	private final Table tblProject;
	private WorkbenchMainView workbenchDashboardwindow;

	private OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction;

	private final Button selectDatasetForBreedingViewButton;
	private Project currentProj;

	private final List<Project> projects;

	@Autowired
	private PlatformTransactionManager transactionManager;

	public ShowProjectDetailAction(Table tblProject, Button selectDatasetForBreedingViewButton,
			OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction, Project currentProject,
			List<Project> projects) {
		this.tblProject = tblProject;
		this.selectDatasetForBreedingViewButton = selectDatasetForBreedingViewButton;
		this.openSelectDatasetForBreedingViewAction = openSelectDatasetForBreedingViewAction;
		this.currentProj = currentProject;
		this.projects = projects;
	}

	private void showDatabaseError(Window window) {
		MessageNotifier.showError(window, this.messageSource.getMessage(Message.DATABASE_ERROR),
				"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
	}

	@Override
	public void valueChange(Property.ValueChangeEvent event) {
		this.doAction((Long) event.getProperty().getValue(), this.tblProject.getWindow());

	}

	public void doAction(final Long projectId, final Window workbenchDashboardWin) {
		try {
			final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					Project project = null;
					if (projectId != null) {
						for (Project tempProject : ShowProjectDetailAction.this.projects) {
							if (tempProject.getProjectId().longValue() == projectId.longValue()) {
								project = tempProject;
								break;
							}
						}
					} else {
						project = ShowProjectDetailAction.this.sessionData.getLastOpenedProject();
					}

					if (project == null) {
						return;
					} else {
						ShowProjectDetailAction.this.currentProj = project;
						IBPWorkbenchApplication.get().getSessionData().setSelectedProject(ShowProjectDetailAction.this.currentProj);
					}

					// update the project activity table's listener
					if (ShowProjectDetailAction.this.openSelectDatasetForBreedingViewAction != null) {
						ShowProjectDetailAction.this.selectDatasetForBreedingViewButton
								.removeListener(ShowProjectDetailAction.this.openSelectDatasetForBreedingViewAction);
					}
					ShowProjectDetailAction.this.openSelectDatasetForBreedingViewAction =
							new OpenSelectProjectForStudyAndDatasetViewAction(project);
					ShowProjectDetailAction.this.selectDatasetForBreedingViewButton
							.addListener(ShowProjectDetailAction.this.openSelectDatasetForBreedingViewAction);

					ShowProjectDetailAction.this.workbenchDashboardwindow = (WorkbenchMainView) workbenchDashboardWin;
					if (ShowProjectDetailAction.this.workbenchDashboardwindow != null) {
						ShowProjectDetailAction.this.workbenchDashboardwindow.addTitle(project.getProjectName());
					}

					// retieve sidebar instance from app
					if (workbenchDashboardWin instanceof WorkbenchMainView) {
						WorkbenchMainView main = (WorkbenchMainView) workbenchDashboardWin;

						if (main.getSidebar() != null) {
							main.getSidebar().populateLinks();
						}
					}

					ShowProjectDetailAction.this.tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(
							ShowProjectDetailAction.this.tblProject, project));
					ShowProjectDetailAction.this.tblProject.refreshRowCache();

					
				}
			});
		} catch (Exception e) {
			ShowProjectDetailAction.LOG.error(e.getMessage(), e);
			this.showDatabaseError(this.tblProject.getWindow());
		}
	}
}

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

import java.util.Arrays;
import java.util.List;

import org.generationcp.browser.study.containers.StudyDetailsQueryFactory;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.ProjectTableCellStyleGenerator;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.summaryview.SummaryView;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
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
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class ShowProjectDetailAction implements Property.ValueChangeListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ShowProjectDetailAction.class);
	private final SummaryView summaryView;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	private final Table tblProject;
	private WorkbenchMainView workbenchDashboardwindow;

	private OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction;

	private final Button selectDatasetForBreedingViewButton;
	private Project currentProj;

	private final GermplasmListPreview germplasmListPreview;
	private final NurseryListPreview nurseryListPreview;
	private final TabSheet previewTab;

	private final List<Project> projects;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private PlatformTransactionManager transactionManager;

	public ShowProjectDetailAction(Table tblProject, SummaryView summaryView, Button selectDatasetForBreedingViewButton,
			OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction, Project currentProject,
			GermplasmListPreview germplasmListPreview, NurseryListPreview nurseryListPreview, TabSheet previewTab, List<Project> projects) {
		this.tblProject = tblProject;
		this.selectDatasetForBreedingViewButton = selectDatasetForBreedingViewButton;
		this.openSelectDatasetForBreedingViewAction = openSelectDatasetForBreedingViewAction;
		this.currentProj = currentProject;
		this.germplasmListPreview = germplasmListPreview;
		this.nurseryListPreview = nurseryListPreview;
		this.previewTab = previewTab;
		this.projects = projects;
		this.summaryView = summaryView;
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
					try {
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

						long projectActivitiesCount;

						projectActivitiesCount =
								ShowProjectDetailAction.this.workbenchDataManager.countProjectActivitiesByProjectId(project.getProjectId());

						List<ProjectActivity> activityList =
								ShowProjectDetailAction.this.workbenchDataManager.getProjectActivitiesByProjectId(project.getProjectId(),
										0, (int) projectActivitiesCount);

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

						ShowProjectDetailAction.this.summaryView.updateActivityTable(activityList);

						StudyDetailsQueryFactory trialFactory =
								new StudyDetailsQueryFactory(studyDataManager, StudyType.T, Arrays
										.asList(ShowProjectDetailAction.this.summaryView.getTblTrialColumns()), project.getUniqueID());

						ShowProjectDetailAction.this.summaryView.updateTrialSummaryTable(trialFactory);

						StudyDetailsQueryFactory nurseryFactory =
								new StudyDetailsQueryFactory(studyDataManager, StudyType.N, Arrays
										.asList(ShowProjectDetailAction.this.summaryView.getTblNurseryColumns()), project.getUniqueID());
						ShowProjectDetailAction.this.summaryView.updateNurserySummaryTable(nurseryFactory);

						StudyDetailsQueryFactory seasonFactory =
								new StudyDetailsQueryFactory(studyDataManager, null, Arrays.asList(ShowProjectDetailAction.this.summaryView
										.getTblSeasonColumns()), project.getUniqueID());
						ShowProjectDetailAction.this.summaryView.updateSeasonSummaryTable(seasonFactory);

						ShowProjectDetailAction.this.germplasmListPreview.setProject(ShowProjectDetailAction.this.currentProj);
						ShowProjectDetailAction.this.nurseryListPreview.setProject(ShowProjectDetailAction.this.currentProj);
						ShowProjectDetailAction.this.previewTab.setSelectedTab(ShowProjectDetailAction.this.germplasmListPreview);
					} catch (MiddlewareQueryException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			});
		} catch (Exception e) {
			ShowProjectDetailAction.LOG.error(e.getMessage(), e);
			this.showDatabaseError(this.tblProject.getWindow());
		}
	}
}

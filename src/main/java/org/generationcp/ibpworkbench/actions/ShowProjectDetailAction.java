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

	public void doAction(Long projectId, Window workbenchDashboardWin) {
		Project project = null;
		if (projectId != null) {
			for (Project tempProject : this.projects) {
				if (tempProject.getProjectId().longValue() == projectId.longValue()) {
					project = tempProject;
					break;
				}
			}
		} else {
			project = this.sessionData.getLastOpenedProject();
		}

		if (project == null) {
			return;
		} else {
			this.currentProj = project;
			IBPWorkbenchApplication.get().getSessionData().setSelectedProject(this.currentProj);
		}

		// update the project activity table's listener
		if (this.openSelectDatasetForBreedingViewAction != null) {
			this.selectDatasetForBreedingViewButton.removeListener(this.openSelectDatasetForBreedingViewAction);
		}
		this.openSelectDatasetForBreedingViewAction = new OpenSelectProjectForStudyAndDatasetViewAction(project);
		this.selectDatasetForBreedingViewButton.addListener(this.openSelectDatasetForBreedingViewAction);

		try {
			long projectActivitiesCount = this.workbenchDataManager.countProjectActivitiesByProjectId(project.getProjectId());
			List<ProjectActivity> activityList =
					this.workbenchDataManager.getProjectActivitiesByProjectId(project.getProjectId(), 0, (int) projectActivitiesCount);

			this.workbenchDashboardwindow = (WorkbenchMainView) workbenchDashboardWin;
			if (this.workbenchDashboardwindow != null) {
				this.workbenchDashboardwindow.addTitle(project.getProjectName());
			}

			// retieve sidebar instance from app
			if (workbenchDashboardWin instanceof WorkbenchMainView) {
				WorkbenchMainView main = (WorkbenchMainView) workbenchDashboardWin;

				if (main.getSidebar() != null) {
					main.getSidebar().populateLinks();
				}
			}

			this.tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(this.tblProject, project));
			this.tblProject.refreshRowCache();

			this.summaryView.updateActivityTable(activityList);

			StudyDetailsQueryFactory trialFactory =
					new StudyDetailsQueryFactory(studyDataManager, StudyType.T, Arrays.asList(this.summaryView.getTblTrialColumns()),
							project.getUniqueID());

			this.summaryView.updateTrialSummaryTable(trialFactory);

			StudyDetailsQueryFactory nurseryFactory =
					new StudyDetailsQueryFactory(studyDataManager, StudyType.N, Arrays.asList(this.summaryView.getTblNurseryColumns()),
							project.getUniqueID());
			this.summaryView.updateNurserySummaryTable(nurseryFactory);

			StudyDetailsQueryFactory seasonFactory =
					new StudyDetailsQueryFactory(studyDataManager, null, Arrays.asList(this.summaryView.getTblSeasonColumns()),
							project.getUniqueID());
			this.summaryView.updateSeasonSummaryTable(seasonFactory);

			this.germplasmListPreview.setProject(this.currentProj);
			this.nurseryListPreview.setProject(this.currentProj);
			this.previewTab.setSelectedTab(this.germplasmListPreview);

		} catch (MiddlewareQueryException e) {
			ShowProjectDetailAction.LOG.error(e.getMessage(), e);
			this.showDatabaseError(this.tblProject.getWindow());
		}
	}
}

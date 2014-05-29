/*******************************************************************************
/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;
import java.util.List;

@Configurable
public class ShowProjectDetailAction implements Property.ValueChangeListener {
    private static final long serialVersionUID = 1L;
    private SummaryView summaryView;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

    private Table tblProject;
    private WorkbenchMainView workbenchDashboardwindow;

    private OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction;

    private Button selectDatasetForBreedingViewButton;
    private Project currentProj;

    private GermplasmListPreview germplasmListPreview;
    private NurseryListPreview nurseryListPreview;
    private TabSheet previewTab;

    private List<Project> projects;

    public ShowProjectDetailAction(Table tblProject, SummaryView summaryView,
                                   Button selectDatasetForBreedingViewButton, OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction,
                                   Project currentProject, GermplasmListPreview germplasmListPreview, NurseryListPreview nurseryListPreview, TabSheet previewTab, List<Project> projects
    ) {
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
        MessageNotifier.showError(window,
                messageSource.getMessage(Message.DATABASE_ERROR),
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        //BeanItem<Project> item = (BeanItem<Project>) event.getItem();

        this.doAction((Long) event.getProperty().getValue(),tblProject.getWindow());

    }

    public void doAction(Long projectId,Window workbenchDashboardWin) {
        Project project = null; //item.getBean();
        if (projectId != null)
            for(Project tempProject : projects){
                if(tempProject.getProjectId().longValue()  == projectId.longValue()){
                    project = tempProject;
                    break;
                }
            }
        else {
            project = sessionData.getLastOpenedProject();
        }

        if (project == null) {
            return;
        }else
        {
            currentProj = project;
            IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
            app.getSessionData().setSelectedProject(currentProj);
        }

        // update the project activity table's listener
        if (openSelectDatasetForBreedingViewAction != null) {
            selectDatasetForBreedingViewButton.removeListener(openSelectDatasetForBreedingViewAction);
        }
        openSelectDatasetForBreedingViewAction = new OpenSelectProjectForStudyAndDatasetViewAction(project);
        selectDatasetForBreedingViewButton.addListener(openSelectDatasetForBreedingViewAction);

        final StudyDataManager studyDataManager = managerFactoryProvider.getManagerFactoryForProject(currentProj).getStudyDataManager();

        try {
            long projectActivitiesCount = workbenchDataManager.countProjectActivitiesByProjectId(project.getProjectId());
            List<ProjectActivity> activityList = workbenchDataManager.getProjectActivitiesByProjectId(project.getProjectId(), 0, (int) projectActivitiesCount);


            workbenchDashboardwindow = (WorkbenchMainView) workbenchDashboardWin;
            if (workbenchDashboardwindow != null)
                workbenchDashboardwindow.addTitle(project.getProjectName());

            //if (WorkbenchSidebar.thisInstance != null)
            //    WorkbenchSidebar.thisInstance.populateLinks();


            // retieve sidebar instance from app
            if (workbenchDashboardWin instanceof WorkbenchMainView) {
                WorkbenchMainView main =  (WorkbenchMainView) workbenchDashboardWin;

                if (main.getSidebar() != null)
                    main.getSidebar().populateLinks();
            }

            tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(tblProject, project));
            tblProject.refreshRowCache();

            summaryView.updateActivityTable(activityList);

            StudyDetailsQueryFactory trialFactory = new StudyDetailsQueryFactory(
                    studyDataManager, StudyType.T, Arrays.asList(summaryView.getTblTrialColumns()));

            summaryView.updateTrialSummaryTable(trialFactory);


            StudyDetailsQueryFactory nurseryFactory = new StudyDetailsQueryFactory(
                    studyDataManager, StudyType.N, Arrays.asList(summaryView.getTblNurseryColumns()));
            summaryView.updateNurserySummaryTable(nurseryFactory);

            StudyDetailsQueryFactory seasonFactory = new StudyDetailsQueryFactory(
                    studyDataManager, null, Arrays.asList(summaryView.getTblSeasonColumns()));
            summaryView.updateSeasonSummaryTable(seasonFactory);

            germplasmListPreview.setProject(currentProj);
            nurseryListPreview.setProject(currentProj);
            previewTab.setSelectedTab(germplasmListPreview);

        }
        catch (MiddlewareQueryException e) {
            showDatabaseError(tblProject.getWindow());
        }
    }
}

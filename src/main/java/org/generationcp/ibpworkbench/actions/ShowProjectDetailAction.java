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

import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.ui.dashboard.SummaryView;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.gxe.ProjectTableCellStyleGenerator;
import org.generationcp.ibpworkbench.ui.sidebar.WorkbenchSidebar;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class ShowProjectDetailAction implements ItemClickListener {
    private static final long serialVersionUID = 1L;
    private SummaryView summaryView;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    private StudyDataManager studyDataManager;
    private ManagerFactory managerFactory;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
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
            Project currentProject, GermplasmListPreview germplasmListPreview, NurseryListPreview nurseryListPreview, TabSheet previewTab, List<Project> projects) {
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
    
   
    
    @Override
    public void itemClick(ItemClickEvent event) {
        @SuppressWarnings("unchecked")
        //BeanItem<Project> item = (BeanItem<Project>) event.getItem();              
        
        Project project = null; //item.getBean();
        Long projectId = (Long)event.getItemId();
        for(Project tempProject : projects){
            if(tempProject.getProjectId().longValue()  == projectId.longValue()){
                project = tempProject; 
                break;
            }
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
        
        SessionData sessionData = IBPWorkbenchApplication.get().getSessionData();
        
        managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProj);
        studyDataManager = managerFactory.getStudyDataManager();
        
        try {
            long projectActivitiesCount = workbenchDataManager.countProjectActivitiesByProjectId(project.getProjectId());
            List<ProjectActivity> activityList = workbenchDataManager.getProjectActivitiesByProjectId(project.getProjectId(), 0, (int) projectActivitiesCount);
            List<StudyDetails> trialSummaryList = studyDataManager.getStudyDetails(StudyType.T,0,Integer.MAX_VALUE);
            List<StudyDetails> nurserySummaryList = studyDataManager.getStudyDetails(StudyType.N,0,Integer.MAX_VALUE);
            List<StudyDetails> seasonSummaryList = studyDataManager.getNurseryAndTrialStudyDetails(0,Integer.MAX_VALUE);
            
            workbenchDashboardwindow = (WorkbenchMainView) event.getComponent().getWindow();
            workbenchDashboardwindow.addTitle(project.getProjectName());

            if (WorkbenchSidebar.thisInstance != null)
                WorkbenchSidebar.thisInstance.populateLinks();

            
            tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(tblProject, project));
            tblProject.refreshRowCache();

            summaryView.updateActivityTable(activityList);
            summaryView.updateTrialSummaryTable(trialSummaryList);
            summaryView.updateNurserySummaryTable(nurserySummaryList);
            summaryView.updateSeasonSummaryTable(seasonSummaryList);
            
            germplasmListPreview.setProject(currentProj);
            nurseryListPreview.setProject(currentProj);
            previewTab.setSelectedTab(germplasmListPreview);
            
        }
        catch (MiddlewareQueryException e) {
            showDatabaseError(event.getComponent().getWindow());
        }
    }
    /*
    private void updateActivityTable(List<ProjectActivity> activityList) {
        Object[] oldColumns = tblActivity.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);

        BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
        container.setBeanIdProperty("projectActivityId");
        tblActivity.setContainerDataSource(container);

        for (ProjectActivity activity : activityList) {
            container.addBean(activity);
        }

        lblActivity.setValue(messageSource.getMessage(Message.ACTIVITIES) + " [" + activityList.size() + "]");

        tblActivity.setContainerDataSource(container);

        tblActivity.setVisibleColumns(columns);
    }*/
    

    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
}

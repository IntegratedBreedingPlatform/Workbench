package org.generationcp.ibpworkbench.comp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.generationcp.ibpworkbench.actions.OpenProjectDashboardAction;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.manager.IWorkFlowActivityManager;
import org.generationcp.ibpworkbench.manager.MockWorkFlowActivityManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkFlowActivity;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ProjectDashboard extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    
    private Project project;

    private Label dashboardTitle;
    private Label datasetsTitle;
    
    private ProjectThumbnailPanel projectThumbnailPanel;
    
    private Table upcomingActivityTable;
    
    private Label recentActivityTitle;
    private Table recentActivityTable;
    
    //Temporary Back Button Navigation
    private Button backButton;
    private HorizontalLayout buttonLayout;

    public ProjectDashboard(Project project) {
        this.project = project;
        
        assemble();
    }
    
    protected void initializeComponents() {
        dashboardTitle = new Label("Project Dashboard: " + project.getProjectName());
        dashboardTitle.setStyleName("gcp-content-title");
        
        datasetsTitle = new Label("Datasets");
        
        projectThumbnailPanel = new ProjectThumbnailPanel(project);
        
        recentActivityTitle = new Label("Recent Activity");
        
        initializeUpcomingActivityTable();
        initializeRecentActivityTable();
        
        backButton = new Button("Back", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                IContentWindow window = (IContentWindow) event.getComponent().getWindow();
                
                WorkbenchDashboard workbenchDashboard = new WorkbenchDashboard();
                workbenchDashboard.addProjectTableListener(new OpenProjectDashboardAction());
                
                window.showContent(workbenchDashboard);
            }
        });
        
        buttonLayout = new HorizontalLayout();
    }
    
    protected void initializeUpcomingActivityTable()  {
        upcomingActivityTable = new Table() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                if (property.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
                }
                
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        upcomingActivityTable.setImmediate(true); // react at once when something is selected
        
        BeanContainer<String, WorkFlowActivity> activityContainer = new BeanContainer<String, WorkFlowActivity>(WorkFlowActivity.class);
        activityContainer.setBeanIdProperty("activityId");
        
        IWorkFlowActivityManager activityManager = MockWorkFlowActivityManager.getInstance();
        List<WorkFlowActivity> activities = activityManager.getUpcomingActivities(project);
        for (WorkFlowActivity activity : activities) {
            activityContainer.addBean(activity);
        }
        upcomingActivityTable.setContainerDataSource(activityContainer);
        
        upcomingActivityTable.setColumnHeader("title", "Next Activity");
        upcomingActivityTable.setColumnHeader("owner", "Owner");
        upcomingActivityTable.setColumnHeader("dueDate", "Due Date");
        
        String[] columns = new String[]{"title", "owner", "dueDate"};
        upcomingActivityTable.setVisibleColumns(columns);
    }
    
    protected void initializeRecentActivityTable() {
        recentActivityTable = new Table() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                if (property.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
                }
                else if (property.getType() == Project.class) {
                    return property.getValue() == null ? "" : ((Project) property.getValue()).getProjectName();
                }
                
                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        recentActivityTable.setImmediate(true); // react at once when something is selected
        
        BeanContainer<String, WorkFlowActivity> activityContainer = new BeanContainer<String, WorkFlowActivity>(WorkFlowActivity.class);
        activityContainer.setBeanIdProperty("activityId");
        
        IWorkFlowActivityManager activityManager = MockWorkFlowActivityManager.getInstance();
        List<WorkFlowActivity> activities = activityManager.getUpcomingActivities(project);
        for (WorkFlowActivity activity : activities) {
            activityContainer.addBean(activity);
        }
        recentActivityTable.setContainerDataSource(activityContainer);
        
        recentActivityTable.setColumnHeader("date", "date");
        recentActivityTable.setColumnHeader("project", "Project");
        recentActivityTable.setColumnHeader("title", "Action");
        recentActivityTable.setColumnHeader("status", "Status");
        recentActivityTable.setColumnHeader("owner", "Owner");
        
        String[] columns = new String[]{"date", "project", "title", "status", "owner"};
        recentActivityTable.setVisibleColumns(columns);
    }
    
    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        
        dashboardTitle.setSizeUndefined();
        addComponent(dashboardTitle);
        
        // data sets area
        Component dataSetArea = layoutDataSetsArea();
        addComponent(dataSetArea);
        
        // project activity area
        Component projectActivityArea = layoutProjectActivityArea();
        addComponent(projectActivityArea);
        
        // recent activity area
        Component recentActivityArea = layoutRecentActivityArea();
        recentActivityArea.setHeight("200px");
        addComponent(recentActivityArea);
        
        addButtonLayout();
    }
    
    private void addButtonLayout() {
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        buttonLayout.setWidth("100%");
        
        buttonLayout.addComponent(backButton);
        buttonLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);
        
        addComponent(buttonLayout);
    }

    public void addProjectThumbnailPanelListener(LayoutClickListener listener) {
        projectThumbnailPanel.addListener(listener);
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
    }
    
    protected Component layoutDataSetsArea() {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("200px");
        layout.setMargin(true);
        layout.setSpacing(true);
        
        datasetsTitle.setSizeUndefined();
        layout.addComponent(datasetsTitle);
        
        HorizontalLayout dataSetsLayout = new HorizontalLayout();
        
        layout.addComponent(dataSetsLayout);
        layout.setExpandRatio(dataSetsLayout, 1.0f);
        
        panel.setContent(layout);
        
        return panel;
    }
    
    protected Component layoutProjectActivityArea() {
        Panel panel = new Panel();
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth("100%");
        layout.setSpacing(true);
        layout.setMargin(true);
        
        // project thumbnail
        projectThumbnailPanel.setWidth("380px");
        projectThumbnailPanel.setMargin(false);
        layout.addComponent(projectThumbnailPanel);
        
        // upcoming activities
        upcomingActivityTable.setSizeFull();
        layout.addComponent(upcomingActivityTable);
        layout.setExpandRatio(upcomingActivityTable, 1.0f);
        
        panel.setContent(layout);
        
        return panel;
    }
    
    protected Component layoutRecentActivityArea() {
        VerticalLayout layout = new VerticalLayout();
        
        layout.addComponent(recentActivityTitle);
        
        recentActivityTable.setWidth("100%");
        layout.addComponent(recentActivityTable);
        
        return layout;
    }
}

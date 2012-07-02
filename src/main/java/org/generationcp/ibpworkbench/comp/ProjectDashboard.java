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

package org.generationcp.ibpworkbench.comp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.manager.IWorkFlowActivityManager;
import org.generationcp.ibpworkbench.manager.MockWorkFlowActivityManager;
import org.generationcp.ibpworkbench.spring.InternationalizableComponent;
import org.generationcp.ibpworkbench.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkFlowActivity;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class ProjectDashboard extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;

    private Project project;

    private Label dashboardTitle;
    private Label datasetsTitle;

    private ProjectThumbnailPanel projectThumbnailPanel;

    private Table upcomingActivityTable;

    private Label recentActivityTitle;
    private Table recentActivityTable;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    public ProjectDashboard(Project project) {
        this.project = project;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }
    
    protected void initializeComponents() {
        dashboardTitle = new Label();
        dashboardTitle.setStyleName("gcp-content-title");

        datasetsTitle = new Label();

        projectThumbnailPanel = new ProjectThumbnailPanel(project);

        recentActivityTitle = new Label();

        initializeUpcomingActivityTable();
        initializeRecentActivityTable();

    }

    protected void initializeUpcomingActivityTable() {
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
        upcomingActivityTable.setImmediate(true); // react at once when
                                                  // something is selected

        BeanContainer<String, WorkFlowActivity> activityContainer = new BeanContainer<String, WorkFlowActivity>(WorkFlowActivity.class);
        activityContainer.setBeanIdProperty("activityId");

        IWorkFlowActivityManager activityManager = MockWorkFlowActivityManager.getInstance();
        List<WorkFlowActivity> activities = activityManager.getUpcomingActivities(project);
        for (WorkFlowActivity activity : activities) {
            activityContainer.addBean(activity);
        }
        upcomingActivityTable.setContainerDataSource(activityContainer);

        String[] columns = new String[] { "title", "owner", "dueDate" };
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
                } else if (property.getType() == Project.class) {
                    return property.getValue() == null ? "" : ((Project) property.getValue()).getProjectName();
                }

                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        recentActivityTable.setImmediate(true); // react at once when something
                                                // is selected

        BeanContainer<String, WorkFlowActivity> activityContainer = new BeanContainer<String, WorkFlowActivity>(WorkFlowActivity.class);
        activityContainer.setBeanIdProperty("activityId");

        IWorkFlowActivityManager activityManager = MockWorkFlowActivityManager.getInstance();
        List<WorkFlowActivity> activities = activityManager.getUpcomingActivities(project);
        for (WorkFlowActivity activity : activities) {
            activityContainer.addBean(activity);
        }
        recentActivityTable.setContainerDataSource(activityContainer);

        String[] columns = new String[] { "date", "project", "title", "status", "owner" };
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
        projectThumbnailPanel.setWidth("420px");
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
    
    public void attach() {
        super.attach();
        
        updateLabels();
    };
    
    @Override
    public void updateLabels() {
        messageSource.setValue(dashboardTitle, Message.project_dashboard_title, project.getProjectName());
        
        messageSource.setValue(datasetsTitle, Message.datasets);
        messageSource.setValue(datasetsTitle, Message.datasets);
        
        messageSource.setColumnHeader(upcomingActivityTable, "title", Message.activity_next);
        messageSource.setColumnHeader(upcomingActivityTable, "owner", Message.owner);
        messageSource.setColumnHeader(upcomingActivityTable, "dueDate", Message.date_due);
        
        messageSource.setColumnHeader(recentActivityTable, "date", Message.date);
        messageSource.setColumnHeader(recentActivityTable, "project", Message.project);
        messageSource.setColumnHeader(recentActivityTable, "title", Message.action);
        messageSource.setColumnHeader(recentActivityTable, "status", Message.status);
        messageSource.setColumnHeader(recentActivityTable, "owner", Message.owner);
    }
}

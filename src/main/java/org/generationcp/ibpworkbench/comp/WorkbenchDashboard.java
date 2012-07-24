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

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class WorkbenchDashboard extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
    private static final long serialVersionUID = 1L;

    private Button btnLeft;
    private Button btnRight;
    private Panel projectThumbnailArea;
    private Table tblProject;

    private Label lblDashboardTitle;

    private HorizontalLayout projectThumbnailLayout;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private com.vaadin.event.MouseEvents.ClickListener projectThumbnailClickHandler;

    public WorkbenchDashboard() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    public void setProjectThumbnailClickHandler(com.vaadin.event.MouseEvents.ClickListener projectThumbnailClickHandler) {
        this.projectThumbnailClickHandler = projectThumbnailClickHandler;
    }

    public void addProjectTableListener(ItemClickListener listener) {
        tblProject.addListener(listener);
    }

    protected void initializeComponents() {
        lblDashboardTitle = new Label();
        lblDashboardTitle.setStyleName("gcp-content-title");

        // project list components
        projectThumbnailArea = new Panel();
        btnLeft = new Button("<<");
        btnRight = new Button(">>");

        // project table components
        tblProject = new Table() {

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
        tblProject.setImmediate(true); // react at once when something is
                                         // selected

        BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
        projectContainer.setBeanIdProperty("projectName");
        tblProject.setContainerDataSource(projectContainer);

        tblProject.setColumnCollapsingAllowed(true);
        tblProject.setCellStyleGenerator(new CellStyleGenerator() {

            private static final long serialVersionUID = 1L;

            @Override
            public String getStyle(Object itemId, Object propertyId) {
                return "project-table";
            }
        });
    }

    protected void initializeLayout() {
        setWidth("100%");
        setMargin(true);
        setSpacing(true);

        lblDashboardTitle.setSizeUndefined();
        addComponent(lblDashboardTitle);

        Component projectThumbnailArea = layoutProjectThumbnailArea();
        addComponent(projectThumbnailArea);

        Component projectTableArea = layoutProjectTableArea();
        addComponent(projectTableArea);
        setExpandRatio(projectTableArea, 1.0f);
    }

    protected void initializeData() {
        //TODO: Verify the try-catch flow
        try {
            // Get the list of Projects
            List<Project> projects = workbenchDataManager.getProjects();

            // set the Project Table data source
            BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
            projectContainer.setBeanIdProperty("projectName");
            for (Project project : projects) {
                projectContainer.addBean(project);
            }
            tblProject.setContainerDataSource(projectContainer);

            // set the visible columns on the Project Table
            String[] columns = new String[] { "targetDueDate", "projectName", "action", "status", "owner" };
            tblProject.setVisibleColumns(columns);

            // update the Project Thumbnail area
            for (Project project : projects) {
                ProjectThumbnailPanel projectPanel = new ProjectThumbnailPanel(project);
                projectPanel.setData(project);

                projectPanel.addListener(new LayoutClickListener() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public void layoutClick(LayoutClickEvent event) {
                        if (projectThumbnailClickHandler == null) {
                            return;
                        }

                        projectThumbnailClickHandler.click(event);
                    }
                });

                projectThumbnailLayout.addComponent(projectPanel);
            }
        } catch (QueryException e) {
            LOG.error("Error encountered while getting workflow templates", e);
        }
    }

    protected void initializeActions() {
        // TODO: if we are going to support left/right buttons here
        // then we need a fixed thumbnail area width.
        // otherwise, we can just let the user scroll using the
        // native scrollbar and take advantage of the user's
        // available screen width.
        // NOTE: if we are going to set a fixed width, we must
        // design the screens against a specific viewport size.
        btnLeft.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                int offset = projectThumbnailArea.getScrollLeft();
                offset -= 100;
                if (offset < 0) {
                    offset = 0;
                }
                projectThumbnailArea.setScrollLeft(offset);
            }
        });
        btnRight.addListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                int offset = projectThumbnailArea.getScrollLeft();
                offset += 100;
                projectThumbnailArea.setScrollLeft(offset);
            }
        });
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeData();
        initializeActions();
    }

    private Component layoutProjectThumbnailArea() {
        AbsoluteLayout outerLayout = new AbsoluteLayout();
        outerLayout.setWidth("100%");
        outerLayout.setHeight("430px");

        projectThumbnailArea.setWidth("100%");
        projectThumbnailArea.setScrollable(true);

        projectThumbnailLayout = new HorizontalLayout();
        projectThumbnailLayout.setSpacing(true);
        projectThumbnailLayout.setMargin(true);

        projectThumbnailArea.setContent(projectThumbnailLayout);

        // NOTE: the project thumbnail layout is intentionally empty at this
        // point.
        // The child components will be added later.

        outerLayout.addComponent(projectThumbnailArea, "top: 0px; left: 20px; right: 20px;");
        outerLayout.addComponent(btnLeft, "top: 50%; left: 10px");
        outerLayout.addComponent(btnRight, "top: 50%; right: 10px");

        return outerLayout;
    }

    private Component layoutProjectTableArea() {
        tblProject.setWidth("100%");
        tblProject.setHeight("100%");
        return tblProject;
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    @Override
    public void updateLabels() {
        messageSource.setValue(lblDashboardTitle, Message.dashboard);
        messageSource.setCaption(tblProject, Message.project_table_caption);
        
        messageSource.setColumnHeader(tblProject, "targetDueDate", Message.date);
        messageSource.setColumnHeader(tblProject, "projectName", Message.project);
        messageSource.setColumnHeader(tblProject, "action", Message.action);
        messageSource.setColumnHeader(tblProject, "status", Message.status);
        messageSource.setColumnHeader(tblProject, "owner", Message.owner);
    }
}

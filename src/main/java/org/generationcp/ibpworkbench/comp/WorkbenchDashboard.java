package org.generationcp.ibpworkbench.comp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.generationcp.ibpworkbench.datasource.helper.DatasourceConfig;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class WorkbenchDashboard extends VerticalLayout implements InitializingBean {
    private static final long serialVersionUID = 1L;

    private Button leftButton;
    private Button rightButton;
    private Panel projectThumbnailArea;
    private Table projectTable;

    private Label dashboardTitle;

    private HorizontalLayout projectThumbnailLayout;
    
    @Autowired(required = true)
    private DatasourceConfig dataSourceConfig;
    
    public WorkbenchDashboard() {
    }
    
    public void setDataSourceConfig(DatasourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }
    
    public void addProjectTableListener(ItemClickListener listener) {
        projectTable.addListener(listener);
    }
    
    protected void initializeComponents() {
        dashboardTitle = new Label("Dashboard");
        dashboardTitle.setStyleName("gcp-content-title");
        
        // project list components
        projectThumbnailArea = new Panel();
        leftButton = new Button("<<");
        rightButton = new Button(">>");
        
        // project table components
        projectTable = new Table() {
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
        projectTable.setImmediate(true); // react at once when something is selected
        
        BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
        projectContainer.setBeanIdProperty("projectName");
        projectTable.setContainerDataSource(projectContainer);
        
        projectTable.setColumnHeader("targetDueDate", "Date");
        projectTable.setColumnHeader("projectName", "Project");
        projectTable.setColumnHeader("action", "Action");
        projectTable.setColumnHeader("status", "Status");
        projectTable.setColumnHeader("owner", "Owner");
    }
    
    protected void initializeLayout() {
        setWidth("100%");
        setMargin(true);
        setSpacing(true);
        
        dashboardTitle.setSizeUndefined();
        addComponent(dashboardTitle);
        
        Component projectThumbnailArea = layoutProjectThumbnailArea();
        addComponent(projectThumbnailArea);
        
        Component projectTableArea = layoutProjectTableArea();
        addComponent(projectTableArea);
        setExpandRatio(projectTableArea, 1.0f);
    }
    
    protected void initializeData() {
        // Get the list of Projects
        WorkbenchDataManager manager = dataSourceConfig.getManagerFactory().getWorkbenchDataManager();
        List<Project> projects = manager.getProjects();
        
        // set the Project Table data source
        BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
        projectContainer.setBeanIdProperty("projectName");
        for (Project project : projects) {
            projectContainer.addBean(project);
        }
        projectTable.setContainerDataSource(projectContainer);
        
        // set the visible columns on the Project Table
        String[] columns = new String[]{"targetDueDate", "projectName", "action", "status", "owner"};
        projectTable.setVisibleColumns(columns);
        
        // update the Project Thumbnail area
        for (Project project : projects) {
            ProjectThumbnailPanel projectPanel = new ProjectThumbnailPanel(project);
            projectThumbnailLayout.addComponent(projectPanel);
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
        leftButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                int offset = projectThumbnailArea.getScrollLeft();
                offset -= 100;
                if (offset < 0) offset = 0;
                projectThumbnailArea.setScrollLeft(offset);
            }
        });
        rightButton.addListener(new ClickListener() {
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
        
        // NOTE: the project thumbnail layout is intentionally empty at this point.
        // The child components will be added later.
        
        outerLayout.addComponent(projectThumbnailArea, "top: 0px; left: 20px; right: 20px;");
        outerLayout.addComponent(leftButton, "top: 50%; left: 10px");
        outerLayout.addComponent(rightButton, "top: 50%; right: 10px");
        
        return outerLayout;
    }
    
    private Component layoutProjectTableArea() {
        projectTable.setWidth("100%");
        projectTable.setHeight("100%");
        return projectTable;
    }
}

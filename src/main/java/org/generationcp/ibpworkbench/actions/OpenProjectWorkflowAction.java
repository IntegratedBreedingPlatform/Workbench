package org.generationcp.ibpworkbench.actions;

import java.util.List;
import java.util.Map;

import org.generationcp.ibpworkbench.comp.MarsProjectDashboard;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.ibpworkbench.datasource.helper.DatasourceConfig;
import org.generationcp.ibpworkbench.model.provider.IProjectProvider;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.navigation.UriUtils;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class OpenProjectWorkflowAction implements LayoutClickListener, ActionListener {
    private static final long serialVersionUID = 1L;

    @Autowired(required = true)
    private DatasourceConfig dataSourceConfig;
    
    @Override
    public void layoutClick(LayoutClickEvent event) {
        Component component = event.getComponent();
        
        IProjectProvider projectProvider = (IProjectProvider) component;
        Project project = projectProvider.getProject();
        
        if (project == null) return;
        
        IContentWindow window = (IContentWindow) component.getWindow();
        
        //TODO: create a project dashboard based on the project's workflow type
        MarsProjectDashboard projectDashboard = new MarsProjectDashboard(project);
        
        NavManager.navigateApp(component.getWindow(), 
                "/home/openProject/openProjectWorkflow?projectId="+project.getProjectId(), 
                project.getProjectName());
        
        window.showContent(projectDashboard);
    }

    @Override
    public void doAction(Event event) {
        NavManager.breadCrumbClick(this, event);
    }

    @Override
    public void doAction(Window window, String uriFragment) {
        IContentWindow w = (IContentWindow) window;
        Map<String, List<String>> params = UriUtils.getUriParameters(uriFragment);
                
        WorkbenchDataManager manager = dataSourceConfig.getManagerFactory().getWorkbenchDataManager();
        List<Project> projects = manager.getProjects();
        
        Project p = null;
        Long projectId = Long.parseLong(params.get("projectId").get(0));

        for(Project proj : projects) {
            if(proj.getProjectId().equals(projectId)) {
                p = proj;
            }
        }
        
        MarsProjectDashboard projectDashboard = new MarsProjectDashboard(p);
        
        w.showContent(projectDashboard);
        
        NavManager.navigateApp(window, uriFragment, p.getProjectName());
    }
}

package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.comp.WorkbenchDashboard;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;

public class SaveNewProjectAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    private Form newProjectForm;

    public SaveNewProjectAction(Form newProjectForm) {
        this.newProjectForm = newProjectForm;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Component component = event.getComponent();
        IContentWindow contentWindow = (IContentWindow) component.getWindow();
        
        newProjectForm.commit();

        @SuppressWarnings("unchecked")
        BeanItem<Project> projectBean = (BeanItem<Project>) newProjectForm.getItemDataSource();
        Project project = projectBean.getBean();
        
        IBPWorkbenchApplication application = (IBPWorkbenchApplication) component.getApplication();
        WorkbenchDataManager manager = application.getWorkbenchDataManager();
        
        manager.saveOrUpdateProject(project);
        System.out.printf("%d %s %s %s", project.getProjectId(), project.getProjectName(), project.getTargetDueDate(), project.getTemplate().getTemplateId());
        
        // go back to dashboard
        WorkbenchDashboard workbenchDashboard = new WorkbenchDashboard();
        workbenchDashboard.addProjectTableListener(new OpenProjectDashboardAction());
        contentWindow.showContent(workbenchDashboard);
    }
}

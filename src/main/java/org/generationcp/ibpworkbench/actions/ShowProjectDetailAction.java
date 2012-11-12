package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.comp.table.ProjectTableCellStyleGenerator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class ShowProjectDetailAction implements ItemClickListener {
    private static final long serialVersionUID = 1L;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Label projectDetailLabel;
    
    private Table tblProject;
    
    private Table tblActivity;

    private Table tblRoles;
    
    private OpenWorkflowForRoleAction openWorkflowForRoleAction;

    public ShowProjectDetailAction(Label projectDetailLabel, Table tblProject, Table tblActivity, Table tblRoles) {
        this.projectDetailLabel = projectDetailLabel;
        this.tblProject = tblProject;
        this.tblActivity = tblActivity;
        this.tblRoles = tblRoles;
    }
    
    @Override
    public void itemClick(ItemClickEvent event) {
        @SuppressWarnings("unchecked")
        BeanItem<Project> item = (BeanItem<Project>) event.getItem();
        
        Project project = item.getBean();
        if (project == null) {
            return;
        }
        
        // update the project activity table's listener
        if (openWorkflowForRoleAction != null) {
            tblRoles.removeListener(openWorkflowForRoleAction);
        }
        openWorkflowForRoleAction = new OpenWorkflowForRoleAction(project);
        tblRoles.addListener(openWorkflowForRoleAction);
        
        SessionData sessionData = IBPWorkbenchApplication.get().getSessionData();
        
        try {
            long projectActivitiesCount = workbenchDataManager.countProjectActivitiesByProjectId(project.getProjectId());
            List<ProjectActivity> activityList = workbenchDataManager.getProjectActivitiesByProjectId(project.getProjectId(), 0, (int) projectActivitiesCount);
            
            List<Role> roleList = workbenchDataManager.getRolesByProjectAndUser(project, sessionData.getUserData());
            
            String label = messageSource.getMessage(Message.PROJECT_DETAIL) + ": " + project.getProjectName();
            projectDetailLabel.setValue(label);
            
            tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(tblProject, project));
            tblProject.refreshRowCache();
            
            updateActivityTable(activityList);
            updateRoleTable(roleList);
        }
        catch (MiddlewareQueryException e) {
            showDatabaseError(event.getComponent().getWindow());
        }
    }
    
    private void updateActivityTable(List<ProjectActivity> activityList) {
        Object[] oldColumns = tblActivity.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, ProjectActivity> container = new BeanContainer<Integer, ProjectActivity>(ProjectActivity.class);
        container.setBeanIdProperty("projectActivityId");
        tblActivity.setContainerDataSource(container);
        
        for (ProjectActivity activity : activityList) {
            container.addBean(activity);
        }
        
        tblActivity.setContainerDataSource(container);
        
        tblActivity.setVisibleColumns(columns);
    }
    
    private void updateRoleTable(List<Role> roleList) {
        Object[] oldColumns = tblRoles.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, Role> container = new BeanContainer<Integer, Role>(Role.class);
        container.setBeanIdProperty("roleId");
        tblRoles.setContainerDataSource(container);
        
        for (Role role : roleList) {
            container.addBean(role);
        }
        
        tblRoles.setContainerDataSource(container);
        
        tblRoles.setVisibleColumns(columns);
    }
    
    private void showDatabaseError(Window window) {
        MessageNotifier.showError(window, 
                messageSource.getMessage(Message.DATABASE_ERROR), 
                "<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
    }
}

package org.generationcp.ibpworkbench.comp.table;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.data.Item;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;

public class ProjectTableCellStyleGenerator implements CellStyleGenerator {
    private static final long serialVersionUID = 1L;

    private Table tblProject;
    private Project selectedProject;

    public ProjectTableCellStyleGenerator(Table tblProject, Project selectedProject) {
        this.tblProject = tblProject;
        this.selectedProject = selectedProject;
    }

    @Override
    public String getStyle(Object itemId, Object propertyId) {
        Item item = tblProject.getItem(itemId);
        Object projectId = item.getItemProperty("projectId").getValue();
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        Project lastOpenedProject = app.getSessionData().getLastOpenedProject();
        
        if (projectId == null) {
            return "project-table";
        }
        else if (selectedProject != null && projectId.equals(selectedProject.getProjectId())) {
            return "gcp-selected-project";
        }
        else if (lastOpenedProject != null) {
            if (projectId.equals(lastOpenedProject.getProjectId())) {
                return "gcp-highlight";
            }
        }
        
        return "project-table";
    }
}

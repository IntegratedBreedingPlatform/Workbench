
package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;

public class ProjectTableCellStyleGenerator implements CellStyleGenerator {

	private static final long serialVersionUID = 1L;

	private final Table tblProject;
	private final Project selectedProject;

	public ProjectTableCellStyleGenerator(Table tblProject, Project selectedProject) {
		this.tblProject = tblProject;
		this.selectedProject = selectedProject;
	}

	@Override
	public String getStyle(Object itemId, Object propertyId) {
		Object projectId = itemId;

		IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
		Project lastOpenedProject = app.getSessionData().getLastOpenedProject();

		if (projectId == null) {
			return "project-table";
		} else if (this.selectedProject != null && projectId.equals(this.selectedProject.getProjectId())) {
			return "gcp-selected-project";
		} else if (lastOpenedProject != null && projectId.equals(lastOpenedProject.getProjectId())) {
			return "gcp-highlight";
		}

		return "project-table";
	}
}

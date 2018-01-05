
package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.ui.Table.CellStyleGenerator;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;

@Configurable
public class ProjectTableCellStyleGenerator implements CellStyleGenerator {

	private static final long serialVersionUID = 1L;

	@Resource
	private ContextUtil contextUtil;

	public ProjectTableCellStyleGenerator() {
		// does nothing
	}

	@Override
	public String getStyle(Object itemId, Object propertyId) {
		Object projectId = itemId;

		Project currentProject = contextUtil.getProjectInContext();

		if (projectId == null) {
			return "project-table";
		} else if (currentProject != null && projectId.equals(currentProject.getProjectId())) {
			return "gcp-selected-project";
		}

		return "project-table";
	}
}

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.ui.Table.CellStyleGenerator;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
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
	public String getStyle(final Object itemId, final Object propertyId) {
		final Object projectId = itemId;

		Project currentProject = null;

		try {
			currentProject = contextUtil.getProjectInContext();
		} catch (final MiddlewareQueryException e) {
			// This error is expected if there's no previously selected program yet
			// for the current user. For this case, just ignore the error.
		}


		if (projectId == null) {
			return "project-table";
		} else if (currentProject != null && projectId.equals(currentProject.getProjectId())) {
			return "gcp-selected-project";
		}

		return "project-table";
	}
}

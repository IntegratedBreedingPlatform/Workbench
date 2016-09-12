
package org.generationcp.ibpworkbench.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;

public class ProjectTestDataInitializer {

	private static final int NUM_OF_PROJECTS = 2;

	public static List<Project> createProjectsWithCropType(final CropType cropType) {
		final List<Project> projects = new ArrayList<>();
		for (int projectId = 1; projectId <= ProjectTestDataInitializer.NUM_OF_PROJECTS; projectId++) {
			final Project project = new Project();
			project.setProjectId((long) projectId);
			project.setCropType(cropType);
			projects.add(project);
		}
		return projects;
	}
}

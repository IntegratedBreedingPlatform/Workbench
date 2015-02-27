package org.generationcp.ibpworkbench;

import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains all session data needed by the workbench application.
 *
 * @author Glenn Marintes
 */
@Deprecated
public class SessionData {
	private Project lastOpenedProject;
	private Project selectedProject;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	private User userData;
	private Integer username_counter = 0;
	private Integer namevalidation_counter = 0;
	private HashMap<Integer, LocationViewModel> locationMaps = new HashMap<Integer, LocationViewModel>();
	private Set<String> uniqueLocations = new HashSet<String>();
	private HashMap<Integer, MethodView> breedingMethodMaps = new HashMap<Integer, MethodView>();
	private Set<String> uniqueBreedingMethods = new HashSet<String>();

	public Project getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
	}

	public Integer getUsername_counter() {
		return username_counter;
	}

	public void setUsername_counter(Integer username_counter) {
		this.username_counter = username_counter;
	}

	public Integer getNamevalidation_counter() {
		return namevalidation_counter;
	}

	public void setNamevalidation_counter(Integer namevalidation_counter) {
		this.namevalidation_counter = namevalidation_counter;
	}

	public Project getLastOpenedProject() {
		return lastOpenedProject;
	}

	public void setLastOpenedProject(Project lastOpenedProject) {
		this.lastOpenedProject = lastOpenedProject;
	}

	/**
	 * Check if the specified project was the last project opened.
	 *
	 * @param project
	 * @return
	 */
	public boolean isLastOpenedProject(Project project) {
		return lastOpenedProject == null ? project == null : lastOpenedProject.equals(project);
	}

	public User getUserData() {
		return this.userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public HashMap<Integer, LocationViewModel> getProjectLocationData() {
		return this.locationMaps;
	}

	public HashMap<Integer, MethodView> getProjectBreedingMethodData() {
		return this.breedingMethodMaps;
	}

	public Set<String> getUniqueBreedingMethods() {
		return this.uniqueBreedingMethods;
	}

	public void logProgramActivity(String activityTitle, String activityDescription)
			throws MiddlewareQueryException {

		ProjectActivity projAct = new ProjectActivity(
				selectedProject.getProjectId().intValue(),
				selectedProject,
				activityTitle,
				activityDescription,
				userData,
				new Date());

		workbenchDataManager.addProjectActivity(projAct);
	}

}

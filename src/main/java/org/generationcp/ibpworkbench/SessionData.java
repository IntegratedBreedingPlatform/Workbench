package org.generationcp.ibpworkbench;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;

import javax.annotation.Resource;

/**
 * This class contains all session data needed by the workbench application.
 * 
 * @author Glenn Marintes
 */
public class SessionData {
    private Project lastOpenedProject;
    private Project selectedProject;

	@Resource
	private WorkbenchDataManager workbenchDataManager;
    
    public Project getSelectedProject() {
		return selectedProject;
	}

	public void setSelectedProject(Project selectedProject) {
		this.selectedProject = selectedProject;
	}

	private User userData;
    private Integer username_counter = 0;
    private Integer namevalidation_counter = 0;
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

	private HashMap<Integer, LocationViewModel> locationMaps = new HashMap<Integer, LocationViewModel>();
    private Set<String> uniqueLocations = new HashSet<String>();
    private HashMap<Integer, MethodView> breedingMethodMaps = new HashMap<Integer, MethodView>();
    private Set<String> uniqueBreedingMethods = new HashSet<String>();

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

    @Deprecated
    public Set<String> getUniqueLocations() {
        return this.uniqueLocations;
    }
    
    public HashMap<Integer, MethodView> getProjectBreedingMethodData() {
        return this.breedingMethodMaps;
    }
    
    public Set<String> getUniqueBreedingMethods() {
        return this.uniqueBreedingMethods;
    }


	public void logProgramActivity(String activityTitle, String activityDescription)
			throws MiddlewareQueryException {
		Project currentProject = this.getLastOpenedProject();
		User currentUser = this.getUserData();

		ProjectActivity projAct = new ProjectActivity(
				currentProject.getProjectId().intValue(),
				currentProject,
				activityTitle,
				activityDescription,
				currentUser,
				new Date());

		workbenchDataManager.addProjectActivity(projAct);
	}

	public String getWorkbenchContextParameters() {

		String contextParameterString = ContextUtil
				.getContextParameterString(this.getUserData().getUserid(),
						this.getSelectedProject().getProjectId());

		String authenticationTokenString = ContextUtil.addQueryParameter(ContextConstants.PARAM_AUTH_TOKEN,
				SecurityUtil.getEncodedToken());
		return contextParameterString + authenticationTokenString;
	}
}

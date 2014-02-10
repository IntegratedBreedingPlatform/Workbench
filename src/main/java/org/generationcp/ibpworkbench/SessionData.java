package org.generationcp.ibpworkbench;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.ibpworkbench.model.BreedingMethodModel;
import org.generationcp.ibpworkbench.model.LocationModel;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * This class contains all session data needed by the workbench application.
 * 
 * @author Glenn Marintes
 */
@Configurable
public class SessionData {
    private Project lastOpenedProject;
    private Project selectedProject;
    
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

	private HashMap<Integer, LocationModel> locationMaps = new HashMap<Integer, LocationModel>();
    private Set<String> uniqueLocations = new HashSet<String>();
    private HashMap<Integer, BreedingMethodModel> breedingMethodMaps = new HashMap<Integer, BreedingMethodModel>();
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
    
    public HashMap<Integer, LocationModel> getProjectLocationData() {
        return this.locationMaps;
    }
    
    public Set<String> getUniqueLocations() {
        return this.uniqueLocations;
    }
    
    public HashMap<Integer, BreedingMethodModel> getProjectBreedingMethodData() {
        return this.breedingMethodMaps;
    }
    
    public Set<String> getUniqueBreedingMethods() {
        return this.uniqueBreedingMethods;
    }
    
}

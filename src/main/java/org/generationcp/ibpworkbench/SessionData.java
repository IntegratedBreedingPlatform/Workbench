package org.generationcp.ibpworkbench;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;

/**
 * This class contains all session data needed by the workbench application.
 * 
 * @author Glenn Marintes
 */
public class SessionData {
    private Project lastOpenedProject;
    private User userData;
    private HashMap<Integer, Location> locationMaps = new HashMap<Integer, Location>();
    private Set<String> uniqueLocations = new HashSet<String>();

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
    
    public HashMap<Integer, Location> getProjectLocationData() {
        return this.locationMaps;
    }
    
    public Set<String> getUniqueLocations() {
        return this.uniqueLocations;
    }
    
}

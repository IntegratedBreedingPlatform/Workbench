package org.generationcp.ibpworkbench;

import org.generationcp.middleware.pojos.workbench.Project;

/**
 * This class contains all session data needed by the workbench application.
 * 
 * @author Glenn Marintes
 */
public class SessionData {
    private Project lastOpenedProject;

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
}

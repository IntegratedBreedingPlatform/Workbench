
package org.generationcp.ibpworkbench;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.generationcp.commons.context.ContextConstants;
import org.generationcp.commons.security.SecurityUtil;
import org.generationcp.commons.util.ContextUtil;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;

import com.vaadin.terminal.gwt.server.WebBrowser;

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
	private Integer usernameCounter = 0;
	private Integer nameValidationCounter = 0;

	private WebBrowser browserInfo;

	private final HashMap<Integer, MethodView> breedingMethodMaps = new HashMap<Integer, MethodView>();
	private final Set<String> uniqueBreedingMethods = new HashSet<String>();

	public Project getSelectedProject() {
		return this.selectedProject;
	}

	public void setSelectedProject(Project selectedProject) {
		if (selectedProject != null) {
			ContextUtil.setContextInfo(IBPWorkbenchApplication.get().getRequest(), this.userData.getUserid(),
					selectedProject.getProjectId(), null);
			ContextHolder.setCurrentCrop(selectedProject.getCropType().getCropName());
			ContextHolder.setCurrentProgram(selectedProject.getUniqueID());
			this.selectedProject = selectedProject;
		}
	}

	public Integer getUsernameCounter() {
		return this.usernameCounter;
	}

	public void setUsernameCounter(Integer username_counter) {
		this.usernameCounter = username_counter;
	}

	public Integer getNameValidationCounter() {
		return this.nameValidationCounter;
	}

	public void setNameValidationCounter(Integer namevalidation_counter) {
		this.nameValidationCounter = namevalidation_counter;
	}

	public Project getLastOpenedProject() {
		return this.lastOpenedProject;
	}

	public void setLastOpenedProject(Project lastOpenedProject) {
		if (lastOpenedProject != null) {
			ContextUtil.setContextInfo(IBPWorkbenchApplication.get().getRequest(), this.userData.getUserid(),
					lastOpenedProject.getProjectId(), null);
			ContextHolder.setCurrentCrop(lastOpenedProject.getCropType().getCropName());
			ContextHolder.setCurrentProgram(lastOpenedProject.getUniqueID());
			this.lastOpenedProject = lastOpenedProject;
		}

	}

	/**
	 * Check if the specified project was the last project opened.
	 *
	 * @param project
	 * @return
	 */
	public boolean isLastOpenedProject(Project project) {
		return this.lastOpenedProject == null ? project == null : this.lastOpenedProject.equals(project);
	}

	public User getUserData() {
		return this.userData;
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public HashMap<Integer, MethodView> getProjectBreedingMethodData() {
		return this.breedingMethodMaps;
	}

	public Set<String> getUniqueBreedingMethods() {
		return this.uniqueBreedingMethods;
	}

	public void logProgramActivity(String activityTitle, String activityDescription) throws MiddlewareQueryException {
		Project currentProject = this.getLastOpenedProject();
		User currentUser = this.getUserData();

		ProjectActivity projAct =
				new ProjectActivity(currentProject.getProjectId().intValue(), currentProject, activityTitle, activityDescription,
						currentUser, new Date());

		this.workbenchDataManager.addProjectActivity(projAct);
	}

	public String getWorkbenchContextParameters() {

		String contextParameterString =
				ContextUtil.getContextParameterString(this.getUserData().getUserid(), this.getSelectedProject().getProjectId());

		String authenticationTokenString = ContextUtil.addQueryParameter(ContextConstants.PARAM_AUTH_TOKEN, SecurityUtil.getEncodedToken());
		return contextParameterString + authenticationTokenString;
	}

	public Map<String, String> getBrowserInfo() {
		Map<String, String> browserInfo = new HashMap<>();
		browserInfo.put("browser", String.format("%s", this.browserInfo.getBrowserApplication()));
		browserInfo.put("screenResolution",
				String.format("%s width x %s height", this.browserInfo.getScreenWidth(), this.browserInfo.getScreenHeight()));

		return browserInfo;
	}

	public void setBrowserInfo(WebBrowser browserInfo) {
		this.browserInfo = browserInfo;
	}
}

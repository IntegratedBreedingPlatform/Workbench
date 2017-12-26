
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 10/28/13 Time: 11:07 AM To change this template use File | Settings | File Templates.
 */
@Configurable
public class UpdateProjectAction implements Button.ClickListener {

	private final UpdateProjectPanel projectPanel;
	private static final Logger LOG = LoggerFactory.getLogger(UpdateProjectAction.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private SessionData sessionData;

	private static final long serialVersionUID = 1L;

	public UpdateProjectAction(UpdateProjectPanel projectPanel) {
		this.projectPanel = projectPanel;
	}

	@Override
	public void buttonClick(Button.ClickEvent event) {

		if (this.projectPanel.validate()) {
			this.doUpdate();
		}

	}

	private void doUpdate() throws MiddlewareQueryException {

		UpdateProjectAction.LOG.debug("doUpdate >");
		UpdateProjectAction.LOG.debug(String.format("Project: [%s]", this.sessionData.getSelectedProject()));

		if (this.projectPanel.validate()) {
			// rename old workspace directory if found
			this.toolUtil.renameOldWorkspaceDirectoryToNewFormat(this.sessionData.getSelectedProject().getProjectId(),
					this.projectPanel.getOldProjectName());

			// update the project
			Project updatedProject = this.projectPanel.projectBasicDetailsComponent.getProjectDetails();
			this.sessionData.getSelectedProject().setProjectName(updatedProject.getProjectName());
			this.sessionData.getSelectedProject().setStartDate(updatedProject.getStartDate());
			this.workbenchDataManager.saveOrUpdateProject(this.sessionData.getSelectedProject());

			MessageNotifier.showMessage(this.projectPanel.getWindow(), "Program update is successful",
					String.format("%s is updated.", StringUtils.abbreviate(this.sessionData.getSelectedProject().getProjectName(), 50)));

			ProjectActivity projAct =
					new ProjectActivity(new Integer(this.sessionData.getSelectedProject().getProjectId().intValue()),
							this.sessionData.getSelectedProject(), "Update Program", "Updated Program - "
									+ this.sessionData.getSelectedProject().getProjectName(), this.sessionData.getUserData(), new Date());
			this.workbenchDataManager.addProjectActivity(projAct);

			if (IBPWorkbenchApplication.get().getMainWindow() instanceof WorkbenchMainView) {
				((WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow()).addTitle(this.sessionData.getSelectedProject()
						.getProjectName());
			}
		}
	}
	
}

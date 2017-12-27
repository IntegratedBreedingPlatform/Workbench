
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.generationcp.ibpworkbench.util.ToolUtil;
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
	private ContextUtil contextUtil;

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

	private void doUpdate() {

		final Project project = this.contextUtil.getProjectInContext();

		UpdateProjectAction.LOG.debug("doUpdate >");
		UpdateProjectAction.LOG.debug(String.format("Project: [%s]", project));

		if (this.projectPanel.validate()) {
			// rename old workspace directory if found
			this.toolUtil.renameOldWorkspaceDirectoryToNewFormat(project.getProjectId(),
					this.projectPanel.getOldProjectName());

			// update the project
			Project updatedProject = this.projectPanel.projectBasicDetailsComponent.getProjectDetails();
			project.setProjectName(updatedProject.getProjectName());
			project.setStartDate(updatedProject.getStartDate());
			this.workbenchDataManager.saveOrUpdateProject(project);

			MessageNotifier.showMessage(this.projectPanel.getWindow(), "Program update is successful",
					String.format("%s is updated.", StringUtils.abbreviate(project.getProjectName(), 50)));

			ProjectActivity projAct =
					new ProjectActivity(new Integer(project.getProjectId().intValue()),
							project, "Update Program", "Updated Program - "
									+ project.getProjectName(), this.contextUtil.getCurrentWorkbenchUser(), new Date());
			this.workbenchDataManager.addProjectActivity(projAct);

			if (IBPWorkbenchApplication.get().getMainWindow() instanceof WorkbenchMainView) {
				((WorkbenchMainView) IBPWorkbenchApplication.get().getMainWindow()).addTitle(project
						.getProjectName());
			}
		}
	}
	
}

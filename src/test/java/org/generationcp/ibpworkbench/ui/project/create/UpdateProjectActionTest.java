
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Date;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

import junit.framework.Assert;

public class UpdateProjectActionTest {

	private static final Date NEW_START_DATE = new Date(2017, 12, 31);

	private static final String OLD_PROGRAM_NAME = "Maize 123";

	private static final String NEW_PROGRAM_NAME = "ABCDEFG HIJK LMNOP";

	@Mock
	private UpdateProjectPanel projectPanel;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private ToolUtil toolUtil;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ProjectBasicDetailsComponent basicDetailsComponent;

	@Mock
	private ClickEvent clickEvent;

	@Mock
	private Window window;

	private Project project;

	private Project updatedProject;

	@InjectMocks
	private UpdateProjectAction updateProjectAction;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.updateProjectAction.setContextUtil(this.contextUtil);
		this.updateProjectAction.setWorkbenchDataManager(this.workbenchDataManager);
		this.updateProjectAction.setToolUtil(this.toolUtil);

		this.project = ProjectTestDataInitializer.createProject();
		this.updatedProject = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();
		Mockito.doReturn(true).when(this.projectPanel).validate();
		Mockito.doReturn(UpdateProjectActionTest.OLD_PROGRAM_NAME).when(this.projectPanel).getOldProjectName();
		Mockito.doReturn(this.basicDetailsComponent).when(this.projectPanel).getProjectBasicDetailsComponent();
		this.updatedProject.setProjectName(UpdateProjectActionTest.NEW_PROGRAM_NAME);
		this.updatedProject.setStartDate(UpdateProjectActionTest.NEW_START_DATE);
		Mockito.doReturn(this.updatedProject).when(this.basicDetailsComponent).getProjectDetails();
		Mockito.doReturn(this.window).when(this.projectPanel).getWindow();
	}

	@Test
	public void testButtonClick() {
		try {
			this.updateProjectAction.buttonClick(this.clickEvent);

			Mockito.verify(this.toolUtil).renameOldWorkspaceDirectoryToNewFormat(this.project.getProjectId(),
					UpdateProjectActionTest.OLD_PROGRAM_NAME);
			final ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
			Mockito.verify(this.workbenchDataManager).saveOrUpdateProject(projectCaptor.capture());
			final Project projectUpdated = projectCaptor.getValue();
			Assert.assertEquals(UpdateProjectActionTest.NEW_PROGRAM_NAME, projectUpdated.getProjectName());
			Assert.assertEquals(UpdateProjectActionTest.NEW_START_DATE, projectUpdated.getStartDate());
			Mockito.verify(this.contextUtil).logProgramActivity(Matchers.eq("Update Program"),
					Matchers.eq("Updated Program - " + this.project.getProjectName()));

		} catch (final IllegalStateException e) {
			// Expecting to throw exception because no active application for test session
		}
	}

	@Test
	public void testButtonClickWithInvalidInputs() {
		Mockito.doReturn(false).when(this.projectPanel).validate();
		this.updateProjectAction.buttonClick(this.clickEvent);

		Mockito.verify(this.toolUtil, Mockito.never()).renameOldWorkspaceDirectoryToNewFormat(Matchers.anyLong(), Matchers.anyString());
		Mockito.verify(this.workbenchDataManager, Mockito.never()).saveOrUpdateProject(Matchers.any(Project.class));
		Mockito.verify(this.contextUtil, Mockito.never()).logProgramActivity(Matchers.anyString(), Matchers.anyString());
	}

}

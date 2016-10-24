
package org.generationcp.ibpworkbench.ui.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.dashboard.listener.LaunchProgramAction;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;

public class WorkbenchDashboardTest {

	private static final int NUMBER_OF_PROGRAMS = 10;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private SessionData sessionData;

	@InjectMocks
	private WorkbenchDashboard workbenchDashboard;

	private List<Project> programs;

	private Project lastOpenedProgram;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.workbenchDashboard.initializeComponents();

		final User currentUser = new User(1);
		Mockito.doReturn(currentUser).when(this.sessionData).getUserData();

		this.programs = this.createProjects(WorkbenchDashboardTest.NUMBER_OF_PROGRAMS, new CropType(CropType.CropEnum.MAIZE.toString()));
		this.lastOpenedProgram = this.programs.get(7);
		Mockito.doReturn(this.programs).when(this.workbenchDataManager).getProjectsByUser(currentUser);
		Mockito.doReturn(this.lastOpenedProgram).when(this.workbenchDataManager).getLastOpenedProject(currentUser.getUserid());
	}

	@Test
	public void testInitializeData() {
		// Call method to test
		this.workbenchDashboard.initializeData();

		// Check size of resulting programs table
		final Table programsTable = this.workbenchDashboard.getProgramsTable();
		Assert.assertNotNull(programsTable);

		// Check contents of resulting programs table and verify that listener to open program was added to Launch button
		Assert.assertEquals(WorkbenchDashboardTest.NUMBER_OF_PROGRAMS, programsTable.size());
		for (final Project project : this.programs) {
			final Item programInTable = programsTable.getItem(project);
			Assert.assertNotNull(programInTable);

			Assert.assertEquals(project.getProjectName(),
					programInTable.getItemProperty(WorkbenchDashboard.PROGRAM_NAME_COLUMN_ID).getValue());
			Assert.assertEquals(project.getCropType().getCropName(),
					programInTable.getItemProperty(WorkbenchDashboard.CROP_NAME_COLUMN_ID).getValue());
			final Button launchButton =
					(Button) programInTable.getItemProperty(WorkbenchDashboard.BUTTON_LIST_MANAGER_COLUMN_ID).getValue();
			Assert.assertNotNull(launchButton);
			Assert.assertNotNull(launchButton.getListeners(ClickListener.class));
			Assert.assertNotNull(launchButton.getListeners(LaunchProgramAction.class));
		}

	}

	@Test
	public void testInitializeActions() {
		// Call method to test
		this.workbenchDashboard.initializeActions();

		// Check that item click listener was added to table rows
		final Table programsTable = this.workbenchDashboard.getProgramsTable();
		Assert.assertNotNull(programsTable.getListeners(ItemClickListener.class));
		Assert.assertNotNull(programsTable.getListeners(LaunchProgramAction.class));
	}

	// TODO extract this to ProjectTestDataIntializer
	private List<Project> createProjects(final Integer numberOfProjects, final CropType cropType) {
		final List<Project> programsList = new ArrayList<>();
		for (int i = 0; i < numberOfProjects; i++) {
			final int index = i + 1;
			final Project project = new Project();
			project.setProjectId(new Long(index));
			project.setProjectName(cropType + " Program " + index);
			project.setCropType(cropType);
			programsList.add(project);
		}
		return programsList;
	}

}

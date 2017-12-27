
package org.generationcp.ibpworkbench.ui.dashboard;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import java.io.InputStream;
import com.vaadin.ui.CssLayout;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.dashboard.listener.LaunchProgramAction;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Table;

public class WorkbenchDashboardTest {

	private static final int NUMBER_OF_PROGRAMS = 10;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ServletContext servletContext;

	@InjectMocks
	private WorkbenchDashboard workbenchDashboard;

	private List<Project> programs;

	private Project lastOpenedProgram;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Setup test data and mocks
		final User currentUser = new User(1);
		Mockito.doReturn(currentUser).when(this.contextUtil).getCurrentWorkbenchUser();

		this.programs = this.createProjects(WorkbenchDashboardTest.NUMBER_OF_PROGRAMS, new CropType(CropType.CropEnum.MAIZE.toString()));
		this.lastOpenedProgram = this.programs.get(7);
		Mockito.doReturn(this.programs).when(this.workbenchDataManager).getProjectsByUser(currentUser);
		Mockito.doReturn(this.lastOpenedProgram).when(this.workbenchDataManager).getLastOpenedProject(currentUser.getUserid());

		// Initialize UI components
		this.workbenchDashboard.initializeComponents();
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

	@Test
	public void testFindInstituteLogo() {
		final String path = "src/main/web/src/images/institute/logo.png";

		// Check the scenario that logo resource is not existing
		Mockito.when(this.servletContext.getResourceAsStream(Matchers.anyString())).thenReturn(null);
		Assert.assertTrue(StringUtils.isBlank(this.workbenchDashboard.findInstituteLogo(path)));

		// Check the scenario that logo resource is existing
		final InputStream inputStream = Mockito.mock(InputStream.class);
		Mockito.when(this.servletContext.getResourceAsStream(Matchers.anyString())).thenReturn(inputStream);
		Assert.assertFalse(StringUtils.isBlank(this.workbenchDashboard.findInstituteLogo(path)));
	}

	@Test
	public void initializeLayoutWithoutClientLogo() {
		// Call method to test. In this context, institute logo resource is not found.
		this.workbenchDashboard.initializeLayout();

		// Verify that preview area is empty (institute logo was not added in layout)
		Assert.assertEquals(2, this.workbenchDashboard.getComponentCount());
		Assert.assertTrue(this.workbenchDashboard.getComponent(1) instanceof HorizontalSplitPanel);
		final HorizontalSplitPanel splitPanel = (HorizontalSplitPanel) this.workbenchDashboard.getComponent(1);
		Assert.assertTrue(splitPanel.getSecondComponent() instanceof CssLayout);
		final CssLayout previewArea = (CssLayout) splitPanel.getSecondComponent();
		Assert.assertEquals(0, previewArea.getComponentCount());
	}

	@Test
	public void initializeLayoutWithClientLogo() {
		final Embedded mockLogo = Mockito.mock(Embedded.class);
		this.workbenchDashboard.setInstituteLogo(mockLogo);

		// Call method to test
		this.workbenchDashboard.initializeLayout();

		// Verify that institute logo was added in layout
		Assert.assertEquals(2, this.workbenchDashboard.getComponentCount());
		Assert.assertTrue(this.workbenchDashboard.getComponent(1) instanceof HorizontalSplitPanel);
		final HorizontalSplitPanel splitPanel = (HorizontalSplitPanel) this.workbenchDashboard.getComponent(1);
		Assert.assertTrue(splitPanel.getSecondComponent() instanceof CssLayout);
		final CssLayout previewArea = (CssLayout) splitPanel.getSecondComponent();
		Assert.assertEquals(1, previewArea.getComponentCount());
		Assert.assertEquals(mockLogo, previewArea.getComponent(0));
	}

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

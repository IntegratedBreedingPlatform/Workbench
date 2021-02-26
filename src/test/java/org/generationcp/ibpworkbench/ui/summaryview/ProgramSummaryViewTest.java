package org.generationcp.ibpworkbench.ui.summaryview;

import com.vaadin.addon.tableexport.ExcelExport;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.junit.Assert;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ProgramSummaryViewTest {

	private static final String PROGRAM_UUID = "abcd-12345";

	private static final Long ACTIVITIES_COUNT = 20L;

	private static final Long NURSERY_COUNT = 5L;

	private static final Long STUDY_COUNT = 10L;

	private static final Long STUDIES_COUNT = 10L;
	public static final String PROJECT_NAME = "ProjectName";

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private StudyDataManager studyDataManager;

	@InjectMocks
	private ProgramSummaryView summaryView;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Setup mocks
		final Project project = new Project();
		project.setProjectId(10L);
		project.setProjectName(PROJECT_NAME);
		project.setUniqueID(ProgramSummaryViewTest.PROGRAM_UUID);
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
		Mockito.doReturn(ProgramSummaryViewTest.ACTIVITIES_COUNT).when(this.workbenchDataManager)
				.countProjectActivitiesByProjectId(project.getProjectId());
		Mockito.doReturn(this.getTestProjectActivities(ProgramSummaryViewTest.ACTIVITIES_COUNT.intValue())).when(this.workbenchDataManager)
				.getProjectActivitiesByProjectId(project.getProjectId(), 0, ProgramSummaryViewTest.ACTIVITIES_COUNT.intValue());
		Mockito.doReturn(ProgramSummaryViewTest.NURSERY_COUNT).when(this.studyDataManager)
				.countAllStudyDetails(StudyTypeDto.getNurseryDto(), ProgramSummaryViewTest.PROGRAM_UUID);
		Mockito.doReturn(ProgramSummaryViewTest.STUDY_COUNT).when(this.studyDataManager)
				.countAllStudyDetails(StudyTypeDto.getTrialDto(), ProgramSummaryViewTest.PROGRAM_UUID);
		Mockito.doReturn(ProgramSummaryViewTest.STUDIES_COUNT).when(this.studyDataManager)
				.countAllNurseryAndTrialStudyDetails(ProgramSummaryViewTest.PROGRAM_UUID);

		this.summaryView.initializeComponents();
		this.summaryView.initializeData();
	}

	@Test
	public void testInitializeLayout() {
		this.summaryView.initializeLayout();

		// Check that the Program Studies (All) table is the one shown by default
		Assert.assertNotNull(this.summaryView.getComponent(ProgramSummaryView.COMPONENT_INDEX_OF_TABLES));
		final Table programStudies = this.summaryView.getProgramStudiesTable();
		Assert.assertEquals(programStudies, this.summaryView.getComponent(ProgramSummaryView.COMPONENT_INDEX_OF_TABLES));

		// Check the sizes of tables
		final Table programTrialsTable = this.summaryView.getProgramTrialsTable();
		final Table programNurseriesTable = this.summaryView.getProgramNurseriesTable();
		final Table programActivities = this.summaryView.getProgramActivitiesTable();
		Assert.assertEquals(ProgramSummaryViewTest.STUDY_COUNT.intValue(), programTrialsTable.size());
		Assert.assertEquals(ProgramSummaryViewTest.NURSERY_COUNT.intValue(), programNurseriesTable.size());
		Assert.assertEquals(ProgramSummaryViewTest.ACTIVITIES_COUNT.intValue(), programActivities.size());
		Assert.assertEquals(ProgramSummaryViewTest.STUDIES_COUNT.intValue(), programStudies.size());

		// Check the displayed table columns
		Assert.assertArrayEquals(ProgramSummaryView.TRIAL_NURSERY_COLUMNS,
			Arrays.copyOf(programTrialsTable.getVisibleColumns(), programTrialsTable.getVisibleColumns().length, String[].class));
		Assert.assertArrayEquals(ProgramSummaryView.TRIAL_NURSERY_COLUMNS,
			Arrays.copyOf(programNurseriesTable.getVisibleColumns(), programNurseriesTable.getVisibleColumns().length,
				String[].class));
		Assert.assertArrayEquals(ProgramSummaryView.ACTIVITIES_COLUMNS,
			Arrays.copyOf(programActivities.getVisibleColumns(), programActivities.getVisibleColumns().length, String[].class));
		Assert.assertArrayEquals(ProgramSummaryView.ALL_STUDIES_COLUMNS,
			Arrays.copyOf(programStudies.getVisibleColumns(), programStudies.getVisibleColumns().length, String[].class));
	}

	@Test
	public void testInitializeData() {

		this.summaryView.initializeData();

		// Make sure the container data source of tables are initialized
		assertNotNull(this.summaryView.getProgramTrialsTable().getContainerDataSource());
		assertNotNull(this.summaryView.getProgramStudiesTable().getContainerDataSource());
		assertNotNull(this.summaryView.getProgramNurseriesTable().getContainerDataSource());
		assertNotNull(this.summaryView.getProgramActivitiesTable().getContainerDataSource());

	}

	@Test
	public void testExportButtonListener() {

		this.summaryView.initializeLayout();

		final String tableName = "tableName";
		final Label header = new Label();
		header.setValue(tableName);
		this.summaryView.setHeader(header);
		this.summaryView.addComponent(new Table());

		final ProgramSummaryView.ExportButtonListener listener = Mockito.spy(this.summaryView.new ExportButtonListener());

		doNothing().when(listener).doExport(Matchers.any(ProgramSummaryExcelExport.class), eq(tableName));

		listener.buttonClick(null);

		// Make sure that doExport is called when the button is clicked
		verify(listener).doExport(Matchers.any(ProgramSummaryExcelExport.class), eq(tableName));

	}

	@Test
	public void testExportButtonListenerDoExport() {

		final ProgramSummaryExcelExport excelExport = mock(ProgramSummaryExcelExport.class);
		final String tableName = "tableName";
		final ProgramSummaryView.ExportButtonListener listener = this.summaryView.new ExportButtonListener();

		listener.doExport(excelExport, tableName);

		verify(excelExport).setReportTitle(PROJECT_NAME + " - " + tableName);
		verify(excelExport).setDisplayTotals(false);
		verify(excelExport).export();

	}

	private List<ProjectActivity> getTestProjectActivities(final int noOfActivities) {
		final List<ProjectActivity> activityList = new ArrayList<>();
		for (int i = 0; i < noOfActivities; i++) {
			final ProjectActivity activity = new ProjectActivity();
			activity.setProjectActivityId(i + 1);
			activityList.add(activity);
		}

		return activityList;
	}

}


package org.generationcp.ibpworkbench.ui.summaryview;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;

public class ProgramSummaryViewTest {

	private static final String PROGRAM_UUID = "abcd-12345";

	private static final Long ACTIVITIES_COUNT = 20L;

	private static final Long NURSERY_COUNT = 5L;

	private static final Long TRIAL_COUNT = 10L;

	private static final Long STUDIES_COUNT = 10L;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private SessionData sessionData;

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
		project.setUniqueID(PROGRAM_UUID);
		Mockito.doReturn(project).when(this.sessionData).getSelectedProject();
		Mockito.doReturn(ACTIVITIES_COUNT).when(this.workbenchDataManager).countProjectActivitiesByProjectId(project.getProjectId());
		Mockito.doReturn(getTestProjectActivities(ACTIVITIES_COUNT.intValue())).when(this.workbenchDataManager)
				.getProjectActivitiesByProjectId(project.getProjectId(), 0, ACTIVITIES_COUNT.intValue());
		Mockito.doReturn(NURSERY_COUNT).when(this.studyDataManager).countStudyDetails(StudyType.N, PROGRAM_UUID);
		Mockito.doReturn(TRIAL_COUNT).when(this.studyDataManager).countStudyDetails(StudyType.T, PROGRAM_UUID);
		Mockito.doReturn(STUDIES_COUNT).when(this.studyDataManager).countAllNurseryAndTrialStudyDetails(PROGRAM_UUID);

		this.summaryView.initializeComponents();
		this.summaryView.initializeData();
	}

	@Test
	public void testInitializeLayout() {
		this.summaryView.initializeLayout();

		// Check that the Program Studies (All) table is the one shown by default
		Assert.assertNotNull(this.summaryView.getComponent(ProgramSummaryView.COMPONENT_INDEX_OF_TABLES));
		Assert.assertEquals(this.summaryView.getProgramStudiesTable(),
				this.summaryView.getComponent(ProgramSummaryView.COMPONENT_INDEX_OF_TABLES));
		// Check the sizes of tables
		Assert.assertEquals(TRIAL_COUNT.intValue(), this.summaryView.getProgramTrialsTable().size());
		Assert.assertEquals(NURSERY_COUNT.intValue(), this.summaryView.getProgramNurseriesTable().size());
		Assert.assertEquals(ACTIVITIES_COUNT.intValue(), this.summaryView.getProgramActivitiesTable().size());
		Assert.assertEquals(STUDIES_COUNT.intValue(), this.summaryView.getProgramStudiesTable().size());
	}

	private List<ProjectActivity> getTestProjectActivities(int noOfActivities) {
		List<ProjectActivity> activityList = new ArrayList<>();
		for (int i = 0; i < noOfActivities; i++) {
			ProjectActivity activity = new ProjectActivity();
			activity.setProjectActivityId(i + 1);
			activityList.add(activity);
		}

		return activityList;
	}

}

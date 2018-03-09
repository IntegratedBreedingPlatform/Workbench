
package org.generationcp.browser.study.containers;

import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StudyDetailsQueryTest {

	private final String programUUID = "sample_program_uuid";
	private final StudyTypeDto studyType = new StudyTypeDto(StudyType.S.getLabel());

	// unit under tests
	protected StudyDetailsQuery studyDetailsQueryWithStudyType;
	protected StudyDetailsQuery studyDetailsQueryWoStudyType;

	@Mock
	private StudyDataManager studyDataManager;
	@Mock
	private List<String> columnIds;

	@Before
	public void setUp() throws Exception {
		this.studyDetailsQueryWithStudyType  = new StudyDetailsQuery(this.studyDataManager,
				this.studyType, this.columnIds, this.programUUID);

		this.studyDetailsQueryWoStudyType = new StudyDetailsQuery(this.studyDataManager, null,
				this.columnIds, this.programUUID);
	}

	@Test
	public void testGetStudyDetailsList() throws Exception {

		final int start = 0;
		final int count = 10;

		Mockito.when(this.studyDataManager.getStudyDetails(this.studyType, this.programUUID, start, count)).thenReturn(
				Collections.<StudyDetails>emptyList());
		Mockito.when(this.studyDataManager.getNurseryAndTrialStudyDetails(this.programUUID, start, count)).thenReturn(
				Collections.<StudyDetails>emptyList());

		this.studyDetailsQueryWithStudyType.getStudyDetailsList(start, count);
		this.studyDetailsQueryWoStudyType.getStudyDetailsList(start, count);

		final ArgumentCaptor<String> programUUIDArg0 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> programUUIDArg1 = ArgumentCaptor.forClass(String.class);

		Mockito.verify(this.studyDataManager, Mockito.times(1)).getStudyDetails(Matchers.eq(this.studyType), programUUIDArg0.capture(),
				Matchers.eq(start), Matchers.eq(count));
		Mockito.verify(this.studyDataManager, Mockito.times(1)).getNurseryAndTrialStudyDetails(programUUIDArg1.capture(),
				Matchers.eq(start), Matchers.eq(count));

		Assert.assertEquals("should be the same as programuuid", this.programUUID, programUUIDArg0.getValue());
		Assert.assertEquals("should be the same as programuuid", this.programUUID, programUUIDArg1.getValue());
	}
}

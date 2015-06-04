
package org.generationcp.browser.study.containers;

import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StudyDetailsQueryTest {

	private final String programUUID = "sample_program_uuid";

	@Mock
	private StudyDataManager studyDataManager;

	private final StudyType studyType = StudyType.S;

	@Mock
	private List<String> columnIds;

	@InjectMocks
	private final StudyDetailsQuery studyDetailsQueryWithStudyType = Mockito.spy(new StudyDetailsQuery(this.studyDataManager,
			this.studyType, this.columnIds, this.programUUID));

	@InjectMocks
	private final StudyDetailsQuery studyDetailsQueryWoStudyType = Mockito.spy(new StudyDetailsQuery(this.studyDataManager, null,
			this.columnIds, this.programUUID));

	@Test
	public void testGetStudyDetailsList() throws Exception {

		int start = 0;
		int count = 10;

		Mockito.when(this.studyDataManager.getStudyDetails(this.studyType, this.programUUID, start, count)).thenReturn(
				Collections.<StudyDetails>emptyList());
		Mockito.when(this.studyDataManager.getNurseryAndTrialStudyDetails(this.programUUID, start, count)).thenReturn(
				Collections.<StudyDetails>emptyList());

		this.studyDetailsQueryWithStudyType.getStudyDetailsList(start, count);
		this.studyDetailsQueryWoStudyType.getStudyDetailsList(start, count);

		ArgumentCaptor<String> programUUIDArg0 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> programUUIDArg1 = ArgumentCaptor.forClass(String.class);

		Mockito.verify(this.studyDataManager, Mockito.times(1)).getStudyDetails(Matchers.eq(this.studyType), programUUIDArg0.capture(),
				Matchers.eq(start), Matchers.eq(count));
		Mockito.verify(this.studyDataManager, Mockito.times(1)).getNurseryAndTrialStudyDetails(programUUIDArg1.capture(),
				Matchers.eq(start), Matchers.eq(count));

		Assert.assertEquals("should be the same as programuuid", this.programUUID, programUUIDArg0.getValue());
		Assert.assertEquals("should be the same as programuuid", this.programUUID, programUUIDArg1.getValue());
	}
}

package org.generationcp.browser.study.containers;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StudyDetailsQueryTest {

	private final String programUUID = "sample_program_uuid";

	@Mock
	private StudyDataManager studyDataManager;

	private StudyType studyType = StudyType.S;

	@Mock
	private List<String> columnIds;

	@InjectMocks
	private StudyDetailsQuery studyDetailsQueryWithStudyType = spy(new StudyDetailsQuery(studyDataManager,studyType,columnIds,programUUID));

	@InjectMocks
	private StudyDetailsQuery studyDetailsQueryWoStudyType = spy(new StudyDetailsQuery(studyDataManager,null,columnIds,programUUID));



	@Test
	public void testGetStudyDetailsList() throws Exception {

		int start = 0;
		int count = 10;

		when(studyDataManager.getStudyDetails(studyType, programUUID, start, count)).thenReturn(
				Collections.<StudyDetails>emptyList());
		when(studyDataManager.getNurseryAndTrialStudyDetails(programUUID, start, count)).thenReturn(Collections.<StudyDetails>emptyList());

		studyDetailsQueryWithStudyType.getStudyDetailsList(start,count);
		studyDetailsQueryWoStudyType.getStudyDetailsList(start,count);

		ArgumentCaptor<String> programUUIDArg0 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> programUUIDArg1 = ArgumentCaptor.forClass(String.class);

		verify(studyDataManager,times(1)).getStudyDetails(eq(studyType), programUUIDArg0.capture(), eq(start), eq(count));
		verify(studyDataManager,times(1)).getNurseryAndTrialStudyDetails(programUUIDArg1.capture(), eq(start), eq(count));

		assertEquals("should be the same as programuuid",programUUID,programUUIDArg0.getValue());
		assertEquals("should be the same as programuuid",programUUID,programUUIDArg1.getValue());
	}
}
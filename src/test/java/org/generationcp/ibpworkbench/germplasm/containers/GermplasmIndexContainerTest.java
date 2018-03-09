package org.generationcp.ibpworkbench.germplasm.containers;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.germplasm.GermplasmDetailModel;
import org.generationcp.ibpworkbench.germplasm.GermplasmQueries;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmIndexContainerTest {

	private static final int TEST_TRIAL_ID_2 = 2;
	private static final String TEST_TRIAL_NAME = "TRIAL 2";

	private static final int TEST_NURSERY_ID_1 = 1;
	private static final String TEST_NURSERY_NAME = "NURSERY 1";

	private static final String PROGRAM_UUID = "1";

	private static final String URL_STUDY_NURSERY = "/Fieldbook/NurseryManager/editNursery/";
	private static final String[] URL_STUDY_TRIAL = {"/Fieldbook/TrialManager/openTrial/", "#/trialSettings"};

	private GermplasmIndexContainer germplasmIndexContainer;

	private GermplasmDetailModel gDetailModel;

	@Mock
	private PlatformTransactionManager transactionManager;

	@InjectMocks
	private final GermplasmQueries germplasmQueries = new GermplasmQueries();

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private ContextUtil contextUtil;

	private ContextInfo contextInfo;

	private String aditionalParameters;

	@Before
	public void setUp() {
		final StudyResultSet studyResultSet = Mockito.mock(StudyResultSet.class);
		when(this.studyDataManager.searchStudies(Matchers.any(StudyQueryFilter.class), Matchers.anyInt())).thenReturn(studyResultSet);
		when(studyResultSet.hasMore()).thenReturn(true).thenReturn(true).thenReturn(false);
		final StudyTypeDto studyTypeDTONursery = new StudyTypeDto("N");
		final StudyTypeDto studyTypeDTOTrial = new StudyTypeDto("T");
		when(studyResultSet.next()).thenReturn(
			new StudyReference(TEST_NURSERY_ID_1, TEST_NURSERY_NAME, TEST_NURSERY_NAME, PROGRAM_UUID, studyTypeDTONursery))
			.thenReturn(
				new StudyReference(TEST_TRIAL_ID_2, TEST_TRIAL_NAME, TEST_TRIAL_NAME, PROGRAM_UUID, studyTypeDTOTrial));

		this.gDetailModel = new GermplasmDetailModel();
		this.gDetailModel.setGid(1);

		germplasmQueries.setTransactionManager(transactionManager);
		this.germplasmIndexContainer = new GermplasmIndexContainer(germplasmQueries);

		contextInfo = new ContextInfo(1, 10L, "a0z9c8d7f5");
		doReturn(contextInfo).when(this.contextUtil).getContextInfoFromSession();
		doReturn(PROGRAM_UUID).when(this.contextUtil).getCurrentProgramUUID();
		aditionalParameters = "?restartApplication&loggedInUserId=" + contextInfo.getLoggedInUserId() + "&selectedProjectId=" + contextInfo
			.getSelectedProjectId() + "&authToken=" + contextInfo.getAuthToken();
	}

	@Test
	public void testSize() {
		assertThat(this.germplasmIndexContainer.getGermplasmStudyInformation(gDetailModel, this.contextUtil).size(), equalTo(2));
	}

	@Test
	public void testgetGermplasmStudyInformation() {
		final IndexedContainer container = this.germplasmIndexContainer.getGermplasmStudyInformation(gDetailModel, contextUtil);

		for (final Object itemId : container.getItemIds()) {
			final Item item = container.getItem(itemId);

			if ((Integer) item.getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue() == TEST_NURSERY_ID_1) {
				validateNursery(item);
			} else {
				validateTrial(item);
			}
		}
	}

	private void validateTrial(final Item item) {
		assertThat(TEST_TRIAL_ID_2, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue()));
		assertThat(URL_STUDY_TRIAL[0] + TEST_TRIAL_ID_2 + aditionalParameters + URL_STUDY_TRIAL[1],
			equalTo(((LinkButton) item.getItemProperty(GermplasmIndexContainer.STUDY_NAME).getValue()).getResource().getURL().toString()));
		assertThat(TEST_TRIAL_NAME, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_DESCRIPTION).toString()));

	}

	private void validateNursery(final Item item) {
		assertThat(TEST_NURSERY_ID_1, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue()));
		assertThat(URL_STUDY_NURSERY + TEST_NURSERY_ID_1 + aditionalParameters,
			equalTo(((LinkButton) item.getItemProperty(GermplasmIndexContainer.STUDY_NAME).getValue()).getResource().getURL().toString()));
		assertThat(TEST_NURSERY_NAME, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_DESCRIPTION).toString()));

	}
}

package org.generationcp.ibpworkbench.germplasm.containers;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import org.generationcp.ibpworkbench.germplasm.GermplasmDetailModel;
import org.generationcp.ibpworkbench.germplasm.GermplasmQueries;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class GermplasmIndexContainerTest {

	private static final int TEST_TRIAL_ID_2 = 2;
	private static final String TEST_TRIAL_NAME = "TRIAL 2";

	private static final int TEST_NURSERY_ID_1 = 1;
	private static final String TEST_NURSERY_NAME = "NURSERY 1";

	private static final String PROGRAM_UUID = "1";

	private static final String URL_STUDY_NURSERY = "/Fieldbook/NurseryManager/editNursery/";
	private static final String URL_STUDY_TRIAL = "/Fieldbook/TrialManager/openTrial/";

	private GermplasmIndexContainer germplasmIndexContainer;

	private GermplasmDetailModel gDetailModel;

	@Mock private PlatformTransactionManager transactionManager;

	@InjectMocks private final GermplasmQueries germplasmQueries = new GermplasmQueries();

	@Mock private StudyDataManager studyDataManager;

	@Before
	public void setUp() {
		StudyResultSet studyResultSet = Mockito.mock(StudyResultSet.class);
		Mockito.when(this.studyDataManager.searchStudies(Mockito.any(StudyQueryFilter.class), Mockito.anyInt())).thenReturn(studyResultSet);
		Mockito.when(studyResultSet.hasMore()).thenReturn(true).thenReturn(true).thenReturn(false);
		Mockito.when(studyResultSet.next()).thenReturn(
			new StudyReference(TEST_NURSERY_ID_1, TEST_NURSERY_NAME, TEST_NURSERY_NAME, PROGRAM_UUID, StudyType.getStudyTypeById(10000)))
			.thenReturn(
				new StudyReference(TEST_TRIAL_ID_2, TEST_TRIAL_NAME, TEST_TRIAL_NAME, PROGRAM_UUID, StudyType.getStudyTypeById(10010)));

		this.gDetailModel = new GermplasmDetailModel();
		this.gDetailModel.setGid(1);

		germplasmQueries.setTransactionManager(transactionManager);
		this.germplasmIndexContainer = new GermplasmIndexContainer(germplasmQueries);
	}

	@Test
	public void testSize() {
		assertThat(this.germplasmIndexContainer.getGermplasmStudyInformation(gDetailModel).size(), equalTo(2));
	}

	@Test
	public void testgetGermplasmStudyInformation() {
		IndexedContainer container = this.germplasmIndexContainer.getGermplasmStudyInformation(gDetailModel);

		for (Object itemId : container.getItemIds()) {
			Item item = container.getItem(itemId);

			if (item.getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue() == TEST_NURSERY_ID_1) {
				validateNursery(item);
			} else {
				validateTrial(item);
			}
		}
	}

	private void validateTrial(Item item) {
		assertThat(TEST_TRIAL_ID_2, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue()));
		assertThat(URL_STUDY_TRIAL + TEST_TRIAL_ID_2 + "#/trialSettings",
			equalTo(((LinkButton) item.getItemProperty(GermplasmIndexContainer.STUDY_NAME).getValue()).getResource().getURL().toString()));
		assertThat(TEST_TRIAL_NAME, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_DESCRIPTION).toString()));

	}

	private void validateNursery(Item item) {
		assertThat(TEST_NURSERY_ID_1, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue()));
		assertThat(URL_STUDY_NURSERY + TEST_NURSERY_ID_1,
			equalTo(((LinkButton) item.getItemProperty(GermplasmIndexContainer.STUDY_NAME).getValue()).getResource().getURL().toString()));
		assertThat(TEST_NURSERY_NAME, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_DESCRIPTION).toString()));

	}
}

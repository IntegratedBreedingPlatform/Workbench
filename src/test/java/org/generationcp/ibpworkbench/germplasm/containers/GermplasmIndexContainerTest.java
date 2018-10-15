package org.generationcp.ibpworkbench.germplasm.containers;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.generationcp.browser.study.containers.StudyButtonRenderer;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.germplasm.GermplasmDetailModel;
import org.generationcp.ibpworkbench.germplasm.GermplasmQueries;
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

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;

@RunWith(MockitoJUnitRunner.class)
public class GermplasmIndexContainerTest {

	private static final int TEST_STUDY_ID_2 = 2;
	private static final String TEST_STUDY_NAME = "Study 2";

	private static final String PROGRAM_UUID = "1";

	private GermplasmIndexContainer germplasmIndexContainer;

	private GermplasmDetailModel gDetailModel;

	@Mock
	private PlatformTransactionManager transactionManager;
	
	@Mock
	private StudyButtonRenderer studyButtonRenderer;
	
	@Mock
	private Button button;

	@InjectMocks
	private final GermplasmQueries germplasmQueries = new GermplasmQueries();

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private ContextUtil contextUtil;

	private ContextInfo contextInfo;

	private StudyReference testStudy;

	@Before
	public void setUp() {
		final StudyResultSet studyResultSet = Mockito.mock(StudyResultSet.class);
		when(this.studyDataManager.searchStudies(Matchers.any(StudyQueryFilter.class), Matchers.anyInt())).thenReturn(studyResultSet);
		when(studyResultSet.hasMore()).thenReturn(true).thenReturn(true).thenReturn(false);
		final StudyTypeDto studyTypeDTOTrial = StudyTypeDto.getTrialDto();
		this.testStudy = new StudyReference(TEST_STUDY_ID_2, TEST_STUDY_NAME, TEST_STUDY_NAME, PROGRAM_UUID, studyTypeDTOTrial);
		when(studyResultSet.next()).thenReturn(testStudy);

		this.gDetailModel = new GermplasmDetailModel();
		this.gDetailModel.setGid(1);

		germplasmQueries.setTransactionManager(transactionManager);
		this.germplasmIndexContainer = new GermplasmIndexContainer(germplasmQueries);

		contextInfo = new ContextInfo(1, 10L, "a0z9c8d7f5");
		doReturn(contextInfo).when(this.contextUtil).getContextInfoFromSession();
		doReturn(PROGRAM_UUID).when(this.contextUtil).getCurrentProgramUUID();
	}

	@Test
	public void testAddGermplasmStudyInformation() {
		Mockito.doReturn(this.button).when(this.studyButtonRenderer).renderStudyButton();
		final IndexedContainer container = new IndexedContainer();

		// Create the container properties
		container.addContainerProperty(GermplasmIndexContainer.STUDY_ID, Integer.class, 0);
		container.addContainerProperty(GermplasmIndexContainer.STUDY_NAME, Button.class, null);
		container.addContainerProperty(GermplasmIndexContainer.STUDY_DESCRIPTION, String.class, "");
		
		this.germplasmIndexContainer.addGermplasmStudyInformation(container, this.testStudy, this.studyButtonRenderer);
		
		assertThat(1, equalTo(container.getItemIds().size()));
		final Item item = container.getItem(container.getItemIds().iterator().next());
		assertThat(TEST_STUDY_ID_2, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue()));
		assertThat(this.button, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_NAME).getValue()));
		assertThat(TEST_STUDY_NAME, equalTo(item.getItemProperty(GermplasmIndexContainer.STUDY_DESCRIPTION).toString()));
	}

}

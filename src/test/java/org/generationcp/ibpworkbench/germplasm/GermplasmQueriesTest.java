
package org.generationcp.ibpworkbench.germplasm;

import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.search.filter.StudyQueryFilter;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.util.MaxPedigreeLevelReachedException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 3/27/2015 Time: 3:59 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class GermplasmQueriesTest {

	private static final int TEST_STUDY_ID_2 = 2;
	private static final String TEST_STUDY_NAME_2 = "STUDY 2";

	private static final int TEST_STUDY_ID_1 = 1;
	private static final String TEST_STUDY_NAME_1 = "STUDY 1";

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private InventoryDataManager inventoryDataManager;

	@Mock
	private PedigreeDataManager pedigreeDataManager;

	@InjectMocks
	private final GermplasmQueries dut = new GermplasmQueries();

	@Test
	public void testGetPedigreeCountLabelMaxPedigreeReached() {
		Mockito.when(this.pedigreeDataManager.countPedigreeLevel(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean()))
				.thenThrow(MaxPedigreeLevelReachedException.class);

		final String label = this.dut.getPedigreeLevelCountLabel(1, true, false);

		Assert.assertEquals(GermplasmQueries.MAX_PEDIGREE_LABEL, label);
	}

	@Test
	public void testGetPedigreeCountLabelMaxNotReached() {
		final int dummyPedigreeCount = 4;
		Mockito.when(this.pedigreeDataManager.countPedigreeLevel(ArgumentMatchers.anyInt(), Matchers.anyBoolean(), Matchers.anyBoolean()))
				.thenReturn(dummyPedigreeCount);

		final String label = this.dut.getPedigreeLevelCountLabel(1, true, false);

		Assert.assertEquals(dummyPedigreeCount + " generations", label);
	}

	@Test
	public void testGetPedigreeCountLabelMaxNotReachedOneGenerationOnly() {
		final int dummyPedigreeCount = 1;
		Mockito.when(this.pedigreeDataManager.countPedigreeLevel(ArgumentMatchers.anyInt(), Matchers.anyBoolean(), Matchers.anyBoolean()))
				.thenReturn(dummyPedigreeCount);

		final String label = this.dut.getPedigreeLevelCountLabel(1, true, false);

		Assert.assertEquals(dummyPedigreeCount + " generation", label);
	}

	@Test
	public void testGetGermplasmStudyInfo() {

		final int testGid = 1;
		Mockito.when(this.studyDataManager.searchStudies(Mockito.any(StudyQueryFilter.class))).thenReturn(
			Arrays.asList(new StudyReference(TEST_STUDY_ID_1, TEST_STUDY_NAME_1), new StudyReference(TEST_STUDY_ID_2, TEST_STUDY_NAME_2)));

		final List<StudyReference> result = this.dut.getGermplasmStudyInfo(testGid);

		Assert.assertEquals(2, result.size());
		Assert.assertEquals(TEST_STUDY_ID_1, result.get(0).getId().intValue());
		Assert.assertEquals(TEST_STUDY_NAME_1, result.get(0).getName());
		Assert.assertEquals(TEST_STUDY_ID_2, result.get(1).getId().intValue());
		Assert.assertEquals(TEST_STUDY_NAME_2, result.get(1).getName());
	}

}

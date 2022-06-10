
package org.generationcp.ibpworkbench.cross.study.h2h.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TablesEntries;
import org.generationcp.middleware.api.germplasm.GermplasmNameService;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.vaadin.ui.Table;

import org.junit.Assert;

@RunWith(value = MockitoJUnitRunner.class)
public class SpecifyGermplasmsComponentTest {

	private static final String GERMPLASM2_GID = "2";
	private static final String GERMPLASM2_NAME = "Germplasm 2";
	private static final String GERMPLASM2_GROUPID = "2";
	private static final Integer GID = 1;
	private static final String PREFERRED_NAME = "Preferred Name";

	@Mock
	private HeadToHeadCrossStudyMain mainScreen;

	@Mock
	private TraitsAvailableComponent traitsComponent;

	@Mock
	private GermplasmNameService germplasmNameService;

	@InjectMocks
	private SpecifyGermplasmsComponent specifyGermplasmsComponent;

	Map<String, String> germplasmIdMGIDMap = new HashMap<>();

	@Before
	public void setup() {
		this.germplasmIdMGIDMap.put(SpecifyGermplasmsComponentTest.GERMPLASM2_GID, SpecifyGermplasmsComponentTest.GERMPLASM2_GID);
		this.specifyGermplasmsComponent = new SpecifyGermplasmsComponent(this.mainScreen, this.traitsComponent);
		this.specifyGermplasmsComponent.setGermplasmIdMGIDMap(this.germplasmIdMGIDMap);
		this.specifyGermplasmsComponent.setSingleEntriesSet(new HashSet<>());
		this.specifyGermplasmsComponent.setGermplasmIdNameMap(new HashMap<>());
		this.specifyGermplasmsComponent.setEntriesTable(new Table());
		this.specifyGermplasmsComponent.setGermplasmNameService(this.germplasmNameService);

		final Map<Integer, String> preferredNamesMap = new HashMap<>();
		preferredNamesMap.put(GID, PREFERRED_NAME);
		Mockito.when(this.germplasmNameService.getPreferredNamesByGIDs(ArgumentMatchers.anyList())).thenReturn(preferredNamesMap);
	}

	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsEmptyAndIsTestEntry() {
		final Map<String, String> testMap = new HashMap<String, String>();
		final Map<String, String> standardMap = new HashMap<String, String>();
		final List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		final GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), GID, 1, 1);
		final List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(true, testMap, standardMap, tableEntriesList,
				germplasmListData);
		final TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertEquals("The test entry name should be " + PREFERRED_NAME, PREFERRED_NAME,
				tablesEntries.getTestEntryName());
		Assert.assertEquals("The test entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(),
				tablesEntries.getTestEntryGID());
		Assert.assertEquals("The test entry group id should be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(),
				tablesEntries.getTestEntryGroupID());
		Assert.assertTrue("The standard entry name should be empty", tablesEntries.getStandardEntryName().isEmpty());
		Assert.assertNull("The standard entry gid should be null", tablesEntries.getStandardEntryGID());
		Assert.assertNull("The standard entry group id should be null", tablesEntries.getStandardEntryGroupID());
	}

	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsEmptyAndIsStandardEntry() {
		final Map<String, String> testMap = new HashMap<String, String>();
		final Map<String, String> standardMap = new HashMap<String, String>();
		final List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		final GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), GID, 1, 1);
		final List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(false, testMap, standardMap, tableEntriesList,
				germplasmListData);
		final TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertTrue("The test entry name should be empty", tablesEntries.getTestEntryName().isEmpty());
		Assert.assertNull("The test entry gid should be null", tablesEntries.getTestEntryGID());
		Assert.assertNull("The test entry group id should be null", tablesEntries.getTestEntryGroupID());
		Assert.assertEquals("The standard entry name should be " + PREFERRED_NAME, PREFERRED_NAME,
				tablesEntries.getStandardEntryName());
		Assert.assertEquals("The standard entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(),
				tablesEntries.getStandardEntryGID());
		Assert.assertEquals("The standard entry group idshould be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(),
				tablesEntries.getStandardEntryGroupID());
	}

	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsNotEmptyAndIsTestEntry() {
		final Map<String, String> testMap = new HashMap<String, String>();
		final Map<String, String> standardMap = new HashMap<String, String>();
		standardMap.put(SpecifyGermplasmsComponentTest.GERMPLASM2_GID, SpecifyGermplasmsComponentTest.GERMPLASM2_NAME);

		final List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		final GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), GID, 1, 1);
		final List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(true, testMap, standardMap, tableEntriesList,
				germplasmListData);
		final TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertEquals("The test entry name should be " + PREFERRED_NAME, PREFERRED_NAME,
			tablesEntries.getTestEntryName());
		Assert.assertEquals("The test entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(),
				tablesEntries.getTestEntryGID());
		Assert.assertEquals("The test entry group id should be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(),
				tablesEntries.getTestEntryGroupID());
		Assert.assertEquals("The standard entry name should be " + SpecifyGermplasmsComponentTest.GERMPLASM2_NAME,
				SpecifyGermplasmsComponentTest.GERMPLASM2_NAME, tablesEntries.getStandardEntryName());
		Assert.assertEquals("The standard entry gid should be " + SpecifyGermplasmsComponentTest.GERMPLASM2_GID,
				SpecifyGermplasmsComponentTest.GERMPLASM2_GID, tablesEntries.getStandardEntryGID());
		Assert.assertEquals("The standard entry group id should be " + SpecifyGermplasmsComponentTest.GERMPLASM2_GROUPID,
				SpecifyGermplasmsComponentTest.GERMPLASM2_GROUPID, tablesEntries.getStandardEntryGroupID());
	}

	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsNotEmptyAndIsStandardEntry() {
		final Map<String, String> testMap = new HashMap<String, String>();
		final Map<String, String> standardMap = new HashMap<String, String>();
		testMap.put(SpecifyGermplasmsComponentTest.GERMPLASM2_GID, SpecifyGermplasmsComponentTest.GERMPLASM2_NAME);

		final List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		final GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), GID, 1, 1);
		final List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(false, testMap, standardMap, tableEntriesList,
				germplasmListData);
		final TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertEquals("The test entry name should be " + SpecifyGermplasmsComponentTest.GERMPLASM2_NAME,
				SpecifyGermplasmsComponentTest.GERMPLASM2_NAME, tablesEntries.getTestEntryName());
		Assert.assertEquals("The test entry gid should be " + SpecifyGermplasmsComponentTest.GERMPLASM2_GID,
				SpecifyGermplasmsComponentTest.GERMPLASM2_GID, tablesEntries.getTestEntryGID());
		Assert.assertEquals("The test entry group id should be " + SpecifyGermplasmsComponentTest.GERMPLASM2_GROUPID,
				SpecifyGermplasmsComponentTest.GERMPLASM2_GROUPID, tablesEntries.getTestEntryGroupID());
		Assert.assertEquals("The standard entry name should be " + PREFERRED_NAME, PREFERRED_NAME,
				tablesEntries.getStandardEntryName());
		Assert.assertEquals("The standard entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(),
				tablesEntries.getStandardEntryGID());
		Assert.assertEquals("The standard entry group id should be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(),
				tablesEntries.getStandardEntryGroupID());
	}

	@Test
	public void testNextButtonClickAction() {
		this.specifyGermplasmsComponent.nextButtonClickAction();

		Mockito.verify(this.traitsComponent).populateTraitsAvailableTable(Matchers.anyListOf(GermplasmPair.class),
				Matchers.anyMapOf(String.class, String.class), Matchers.anyMapOf(String.class, String.class));
		Mockito.verify(this.mainScreen).selectSecondTab();
	}
}

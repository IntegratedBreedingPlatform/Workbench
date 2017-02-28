
package org.generationcp.ibpworkbench.cross.study.h2h.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TablesEntries;
import org.generationcp.middleware.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Table;

import junit.framework.Assert;

@RunWith(value = MockitoJUnitRunner.class)
public class SpecifyGermplasmsComponentTest {
	
	private static final String GERMPLASM2_GID = "2";
	private static final String GERMPLASM2_NAME = "Germplasm 2";
	private static final String GERMPLASM2_GROUPID = "2";

	@InjectMocks
	SpecifyGermplasmsComponent specifyGermplasmsComponent;
	
	Map<String, String> germplasmIdMGIDMap = new HashMap<String, String>();
	@Before
	public void setup() {
		this.germplasmIdMGIDMap.put(GERMPLASM2_GID, GERMPLASM2_GID);
		this.specifyGermplasmsComponent.setGermplasmIdMGIDMap(germplasmIdMGIDMap);
		this.specifyGermplasmsComponent.setSingleEntriesSet(new HashSet<String>());
		this.specifyGermplasmsComponent.setGermplasmIdNameMap(new HashMap<String, String>());
		this.specifyGermplasmsComponent.setEntriesTable(new Table());
	}
	
	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsEmptyAndIsTestEntry() {
		Map<String, String> testMap = new HashMap<String, String>();
		Map<String, String> standardMap = new HashMap<String, String>();
		List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), 1, 1, 1);
		List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(true, testMap, standardMap, tableEntriesList, germplasmListData);
		TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertEquals("The test entry name should be " + germplasmData.getDesignation(), germplasmData.getDesignation(), tablesEntries.getTestEntryName());
		Assert.assertEquals("The test entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(), tablesEntries.getTestEntryGID());
		Assert.assertEquals("The test entry group id should be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(), tablesEntries.getTestEntryGroupID());
		Assert.assertTrue("The standard entry name should be empty", tablesEntries.getStandardEntryName().isEmpty());
		Assert.assertNull("The standard entry gid should be null", tablesEntries.getStandardEntryGID());
		Assert.assertNull("The standard entry group id should be null", tablesEntries.getStandardEntryGroupID());
	}
	
	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsEmptyAndIsStandardEntry() {
		Map<String, String> testMap = new HashMap<String, String>();
		Map<String, String> standardMap = new HashMap<String, String>();
		List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), 1, 1, 1);
		List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(false, testMap, standardMap, tableEntriesList, germplasmListData);
		TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertTrue("The test entry name should be empty", tablesEntries.getTestEntryName().isEmpty());
		Assert.assertNull("The test entry gid should be null", tablesEntries.getTestEntryGID());
		Assert.assertNull("The test entry group id should be null", tablesEntries.getTestEntryGroupID());
		Assert.assertEquals("The standard entry name should be " + germplasmData.getDesignation(), germplasmData.getDesignation(), tablesEntries.getStandardEntryName());
		Assert.assertEquals("The standard entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(), tablesEntries.getStandardEntryGID());
		Assert.assertEquals("The standard entry group idshould be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(), tablesEntries.getStandardEntryGroupID());
	}
	
	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsNotEmptyAndIsTestEntry() {
		Map<String, String> testMap = new HashMap<String, String>();
		Map<String, String> standardMap = new HashMap<String, String>();
		standardMap.put(GERMPLASM2_GID, GERMPLASM2_NAME);
		
		List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), 1, 1, 1);
		List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(true, testMap, standardMap, tableEntriesList, germplasmListData);
		TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertEquals("The test entry name should be " + germplasmData.getDesignation(), germplasmData.getDesignation(), tablesEntries.getTestEntryName());
		Assert.assertEquals("The test entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(), tablesEntries.getTestEntryGID());
		Assert.assertEquals("The test entry group id should be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(), tablesEntries.getTestEntryGroupID());
		Assert.assertEquals("The standard entry name should be " + GERMPLASM2_NAME, GERMPLASM2_NAME, tablesEntries.getStandardEntryName());
		Assert.assertEquals("The standard entry gid should be " + GERMPLASM2_GID, GERMPLASM2_GID, tablesEntries.getStandardEntryGID());
		Assert.assertEquals("The standard entry group id should be " + GERMPLASM2_GROUPID, GERMPLASM2_GROUPID, tablesEntries.getStandardEntryGroupID());
	}
	
	@Test
	public void permutateGermplasmListToPartnerEntriesTestWherePartnerMapIsNotEmptyAndIsStandardEntry() {
		Map<String, String> testMap = new HashMap<String, String>();
		Map<String, String> standardMap = new HashMap<String, String>();
		testMap.put(GERMPLASM2_GID, GERMPLASM2_NAME);
		
		List<TablesEntries> tableEntriesList = new ArrayList<TablesEntries>();
		GermplasmListData germplasmData = GermplasmListDataTestDataInitializer.createGermplasmListData(new GermplasmList(), 1, 1, 1);
		List<GermplasmListData> germplasmListData = Arrays.asList(germplasmData);
		this.specifyGermplasmsComponent.permutateGermplasmListToPartnerEntries(false, testMap, standardMap, tableEntriesList, germplasmListData);
		TablesEntries tablesEntries = tableEntriesList.get(0);
		Assert.assertEquals("The table's size should be 1", 1, tableEntriesList.size());
		Assert.assertEquals("The test entry name should be " + GERMPLASM2_NAME, GERMPLASM2_NAME, tablesEntries.getTestEntryName());
		Assert.assertEquals("The test entry gid should be " + GERMPLASM2_GID, GERMPLASM2_GID, tablesEntries.getTestEntryGID());
		Assert.assertEquals("The test entry group id should be " + GERMPLASM2_GROUPID, GERMPLASM2_GROUPID, tablesEntries.getTestEntryGroupID());
		Assert.assertEquals("The standard entry name should be " + germplasmData.getDesignation(), germplasmData.getDesignation(), tablesEntries.getStandardEntryName());
		Assert.assertEquals("The standard entry gid should be " + germplasmData.getGid(), germplasmData.getGid().toString(), tablesEntries.getStandardEntryGID());
		Assert.assertEquals("The standard entry group id should be " + germplasmData.getGroupId(), germplasmData.getGroupId().toString(), tablesEntries.getStandardEntryGroupID());
	}
}


package org.generationcp.breeding.manager.crossingmanager.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.breeding.manager.crossingmanager.pojos.CrossesMade;
import org.generationcp.middleware.ruleengine.settings.AdditionalDetailsSetting;
import org.generationcp.middleware.ruleengine.settings.BreedingMethodSetting;
import org.generationcp.middleware.ruleengine.settings.CrossNameSetting;
import org.generationcp.middleware.ruleengine.settings.CrossSetting;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.api.germplasm.GermplasmNameService;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;

public class SaveCrossesMadeActionTest {

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private GermplasmDataManager germplasmManager;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private PlatformTransactionManager transactionManager;
	
	@Mock
	private CrossExpansionProperties crossExpansionProperties;

	@Mock
	private GermplasmNameService germplasmNameService;

	private SaveCrossesMadeAction action;

	private CrossesMade crossesMade;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.action = new SaveCrossesMadeAction();
		this.action.setContextUtil(this.contextUtil);
		this.action.setTransactionManager(this.transactionManager);
		this.action.setGermplasmListManager(this.germplasmListManager);
		this.action.setCrossExpansionProperties(this.crossExpansionProperties);

		this.crossesMade = new CrossesMade();
		this.crossesMade.setSetting(this.getCrossingSetting());
	}

	private CrossSetting getCrossingSetting() {
		final CrossSetting toreturn = new CrossSetting();

		final CrossNameSetting crossNameSettingPojo = new CrossNameSetting();
		crossNameSettingPojo.setAddSpaceBetweenPrefixAndCode(true);
		crossNameSettingPojo.setAddSpaceBetweenSuffixAndCode(true);
		crossNameSettingPojo.setNumOfDigits(3);
		crossNameSettingPojo.setPrefix("BMS");
		crossNameSettingPojo.setSaveParentageDesignationAsAString(true);
		crossNameSettingPojo.setSeparator("/");
		crossNameSettingPojo.setStartNumber(1);
		crossNameSettingPojo.setSuffix("END");
		toreturn.setCrossNameSetting(crossNameSettingPojo);

		final AdditionalDetailsSetting additionalDetails = new AdditionalDetailsSetting(1, Util.getCurrentDateAsStringValue());
		toreturn.setAdditionalDetailsSetting(additionalDetails);

		final BreedingMethodSetting breedingMethodSetting = new BreedingMethodSetting(1, true, false);
		toreturn.setBreedingMethodSetting(breedingMethodSetting);

		toreturn.setName("BMS Template Name");

		return toreturn;
	}

	@Test
	public void testBuildGermplasmListData() {
		final GermplasmList list = new GermplasmList();
		final Integer gid = 1;
		final Integer entryId = 1;
		String seedSource = "SeedSource";
		final Map<Integer, String> pedigreeMap = new HashMap<>();
		final String pedigree = "PEDIGREE";
		pedigreeMap.put(gid, pedigree);
		GermplasmListData data = this.action.buildGermplasmListData(list, gid, entryId, seedSource, pedigreeMap);
		Assert.assertEquals(list, data.getList());
		Assert.assertEquals(gid, data.getGid());
		Assert.assertEquals(entryId, data.getEntryId());
		Assert.assertEquals(seedSource, data.getSeedSource());
		Assert.assertEquals(pedigree, data.getGroupName());
		Assert.assertEquals(SaveCrossesMadeAction.LIST_DATA_STATUS, data.getStatus());
		Assert.assertEquals(SaveCrossesMadeAction.LIST_DATA_LRECID, data.getLocalRecordId());

		seedSource = RandomStringUtils.random(266);
		Assert.assertEquals(266, seedSource.length());
		data = this.action.buildGermplasmListData(list, gid, entryId, seedSource, pedigreeMap);
		Assert.assertEquals(SaveCrossesMadeAction.SEEDSOURCE_CHARACTER_LIMIT, data.getSeedSource().length());
	}
}

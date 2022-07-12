
package org.generationcp.ibpworkbench.cross.study.adapted.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import com.vaadin.ui.*;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.cross.study.adapted.main.QueryForAdaptedGermplasmMain;
import org.generationcp.ibpworkbench.cross.study.traitdonors.main.TraitDonorsQueryMain;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.data.initializer.UserDefinedFieldTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Validator.InvalidValueException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

public class SaveToListDialogTest {

	@Mock
	private GermplasmListManager germplasmListManager;
	@Mock
	private QueryForAdaptedGermplasmMain queryFoAdaptedGermplasmMain;

	@Mock
	private TraitDonorsQueryMain multiTraitQueryMainScreen;

	@Mock
	private GermplasmListDataService germplasmListDataService;

	private SaveToListDialog saveToListDialog;
	private ComboBox combobox;
	private TextField txtDescription;
	private static final String PROGRAM_UUID = "1234567";
	private static final Integer WORKBENCHUSER = 1;
	private static ArrayList<GermplasmListData> germplasmListData = new ArrayList<>();
	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private PedigreeService pedigreeService;

	@Resource
	private CrossExpansionProperties crossExpansionProperties;

	private final UserDefinedFieldTestDataInitializer userDefinedFieldTestDataInitializer = new UserDefinedFieldTestDataInitializer();



	@Mock
	private ContextUtil contextUtil;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.saveToListDialog =
				Mockito.spy(new SaveToListDialog(this.queryFoAdaptedGermplasmMain, new AbsoluteLayout(), new Window(),
						new HashMap<Integer, String>()));
		Mockito.when(this.germplasmListManager.getGermplasmListTypes())
				.thenReturn(this.userDefinedFieldTestDataInitializer.getValidListType());
		Mockito.when(this.contextUtil.getCurrentWorkbenchUserId()).thenReturn(SaveToListDialogTest.WORKBENCHUSER);
		Mockito.when(this.contextUtil.getCurrentProgramUUID()).thenReturn(SaveToListDialogTest.PROGRAM_UUID);
		this.saveToListDialog.setGermplasmListManager(this.germplasmListManager);
		this.saveToListDialog.setGermplasmListDataService(this.germplasmListDataService);

		this.combobox = new ComboBox();
		this.txtDescription = new TextField();
		this.saveToListDialog.setComboboxListName(this.combobox);
	}

	@Test
	public void testListNameValues() throws MiddlewareQueryException {
		final Integer numberOfLists = 3;

		Mockito.when(this.germplasmListManager.countAllGermplasmLists()).thenReturn(Long.valueOf(numberOfLists));
		Mockito.when(this.germplasmListManager.getAllGermplasmLists(0, numberOfLists)).thenReturn(this.createDummyListsAndFolder());

		this.saveToListDialog.populateComboBoxListName();

		// there's an extra combobox item ("") for empty selection
		Assert.assertTrue("Three items available, consisting of two lists and one empty selection",
				numberOfLists.equals(this.combobox.size()));
		for (Object itemId : this.combobox.getItemIds()) {
			String listName = (String) itemId;
			if (!"".equals(listName)) {
				Assert.assertTrue("Expecting only lists are included", listName.contains("LIST"));
			}
		}

	}

	private List<GermplasmList> createDummyListsAndFolder() {
		List<GermplasmList> lists = new ArrayList<GermplasmList>();
		lists.add(new GermplasmList(-1, "LIST 1", new Long("20141215"), "LST", -1, "test", null, 1, null, null, null, null, null,
				"TEST NOTES", null));
		lists.add(new GermplasmList(-2, "LIST 2", new Long("20141216"), "LST", -1, "test", null, 1, null, null, null, null, null,
				"TEST NOTES", null));
		lists.add(new GermplasmList(-3, "FOLDER", new Long("20141217"), "FOLDER", -1, "test", null, 1, null, null, null, null, null,
				"TEST NOTES", null));

		return lists;
	}

	@Test
	public void testValidateListNameToSaveForExistingListNameInput() throws MiddlewareQueryException {
		String listName = "Existing List Name";

		Mockito.when(this.germplasmListManager.countGermplasmListByName(listName, Operation.EQUAL)).thenReturn(1L);
		try {
			this.saveToListDialog.validateListNameToSave(listName);
		} catch (InvalidValueException e) {
			Assert.assertEquals("Expected to return an exception message.", "There is already an existing germplasm list with that name",
					e.getMessage());
		}
	}

	@Test
	public void testValidateListNameToSaveForEmptyStringInput() throws MiddlewareQueryException {
		String listName = "";

		Mockito.when(this.germplasmListManager.countGermplasmListByName(listName, Operation.EQUAL)).thenReturn(0L);
		try {
			this.saveToListDialog.validateListNameToSave(listName);
		} catch (InvalidValueException e) {
			Assert.assertEquals("Expected to return an exception message.", "Please specify a List Name before saving", e.getMessage());
		}
	}

	@Test
	public void testValidateListNameToSaveForLongListNameInput() throws MiddlewareQueryException {
		String listName = "1234567890123456789012345678901234567890123456789012345";

		Mockito.when(this.germplasmListManager.countGermplasmListByName(listName, Operation.EQUAL)).thenReturn(0L);
		try {
			this.saveToListDialog.validateListNameToSave(listName);
		} catch (InvalidValueException e) {
			Assert.assertEquals("Expected to return an exception message.",
					"Listname input is too large limit the name only up to 50 characters", e.getMessage());
		}
	}

	@Test
	public void testSaveToListGermplasmWithGroupName() {
		SaveToListDialogTest.germplasmListData.clear();
		final Integer numberOfLists = 3;
		Mockito.when(this.germplasmListManager.countAllGermplasmLists()).thenReturn(Long.valueOf(numberOfLists));
		Mockito.when(this.germplasmListManager.getAllGermplasmLists(0, numberOfLists)).thenReturn(this.createDummyListsAndFolder());

		final GermplasmList listNameData = new GermplasmList(null, "LIST 3", DateUtil.getCurrentDateAsLongValue(), "STUDY", 1,
				"", null, 1);

		final GermplasmList savedListNameData = new GermplasmList(3, "LIST 3", DateUtil.getCurrentDateAsLongValue(), "STUDY", 1,
				"", null, 1);
		listNameData.setProgramUUID(PROGRAM_UUID);
		Mockito.when(this.germplasmListManager.addGermplasmList(listNameData)).thenReturn(3);
		Mockito.when(this.germplasmListManager.getGermplasmListById(3)).thenReturn(savedListNameData);
		Mockito.when(this.germplasmListManager.addGermplasmListData(Mockito.isA(GermplasmListData.class))).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {

				for(Object obj: invocationOnMock.getArguments()) {
					if(obj instanceof  GermplasmListData){
						SaveToListDialogTest.germplasmListData.add((GermplasmListData) obj);
					}
				}
				return Integer.valueOf(1);
			}
		});
		this.saveToListDialog =
				Mockito.spy(new SaveToListDialog(this.multiTraitQueryMainScreen, new AbsoluteLayout(), new Window(),
						getGermPlasmMap()));
		this.saveToListDialog.setPedigreeService(this.pedigreeService);
		this.saveToListDialog.setCrossExpansionProperties(this.crossExpansionProperties);
		this.saveToListDialog.setGermplasmListManager(this.germplasmListManager);
		this.saveToListDialog.setGermplasmListDataService(this.germplasmListDataService);
		this.saveToListDialog.setMessageSource(this.messageSource);
		try {
			this.saveToListDialog.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.saveToListDialog.setComboBoxListNameValue("LIST 3");
		this.saveToListDialog.setSelectTypeValue("LST");
		this.saveToListDialog.setContextUtil(this.contextUtil);
		this.saveToListDialog.setParent(new Window());
		Mockito.when(this.saveToListDialog.bulkGeneratePedigreeString(getGermPlasmMap().keySet())).thenReturn(getReturnCross());
		this.saveToListDialog.saveButtonClickAction();
		Assert.assertEquals(2,SaveToListDialogTest.germplasmListData.size());
		for(GermplasmListData listData : germplasmListData){
			Assert.assertEquals(listData.getGroupName(), getReturnCross().get(listData.getGermplasmId()));
		}

	}

	@Test
	public void testSaveToListGermplasmWithoutGroupName() {
		SaveToListDialogTest.germplasmListData.clear();
		final Integer numberOfLists = 3;
		Mockito.when(this.germplasmListManager.countAllGermplasmLists()).thenReturn(Long.valueOf(numberOfLists));
		Mockito.when(this.germplasmListManager.getAllGermplasmLists(0, numberOfLists)).thenReturn(this.createDummyListsAndFolder());

		final GermplasmList listNameData = new GermplasmList(null, "LIST 3", DateUtil.getCurrentDateAsLongValue(), "STUDY", 1,
				"", null, 1);

		final GermplasmList savedListNameData = new GermplasmList(3, "LIST 3", DateUtil.getCurrentDateAsLongValue(), "STUDY", 1,
				"", null, 1);
		listNameData.setProgramUUID(PROGRAM_UUID);
		Mockito.when(this.germplasmListManager.addGermplasmList(listNameData)).thenReturn(3);
		Mockito.when(this.germplasmListManager.getGermplasmListById(3)).thenReturn(savedListNameData);
		Mockito.when(this.germplasmListManager.addGermplasmListData(Mockito.isA(GermplasmListData.class))).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocationOnMock) throws Throwable {

				for(Object obj: invocationOnMock.getArguments()) {
					if(obj instanceof  GermplasmListData){
						SaveToListDialogTest.germplasmListData.add((GermplasmListData) obj);
					}
				}
				return Integer.valueOf(1);
			}
		});
		this.saveToListDialog =
				Mockito.spy(new SaveToListDialog(this.multiTraitQueryMainScreen, new AbsoluteLayout(), new Window(),
						getGermPlasmMap()));
		this.saveToListDialog.setPedigreeService(this.pedigreeService);
		this.saveToListDialog.setCrossExpansionProperties(this.crossExpansionProperties);
		this.saveToListDialog.setGermplasmListManager(this.germplasmListManager);
		this.saveToListDialog.setGermplasmListDataService(this.germplasmListDataService);
		this.saveToListDialog.setMessageSource(this.messageSource);
		try {
			this.saveToListDialog.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.saveToListDialog.setComboBoxListNameValue("LIST 3");
		this.saveToListDialog.setSelectTypeValue("LST");
		this.saveToListDialog.setContextUtil(this.contextUtil);
		this.saveToListDialog.setParent(new Window());
		Mockito.when(this.saveToListDialog.bulkGeneratePedigreeString(getGermPlasmMapNew().keySet())).thenReturn(getReturnCross());
		this.saveToListDialog.saveButtonClickAction();
		Assert.assertEquals(2,SaveToListDialogTest.germplasmListData.size());
		for(GermplasmListData listData : germplasmListData){
			Assert.assertEquals(listData.getGroupName(), "-");
		}

	}

	private HashMap getGermPlasmMap() {
		HashMap germplasmMap = new HashMap<Integer, String>();
		germplasmMap.put(1, "[IB]2661");
		germplasmMap.put(2, "[IB]2662");
		return germplasmMap;
	}

	private HashMap getGermPlasmMapNew() {
		HashMap germplasmMap = new HashMap<Integer, String>();
		germplasmMap.put(3, "[IB]2661");
		germplasmMap.put(4, "[IB]2662");
		return germplasmMap;
	}

	private HashMap getReturnCross(){
		HashMap returnCross = new HashMap<Integer, String>();
		returnCross.put(1, "SampleCross1/SampleCross5");
		returnCross.put(2, "SampleCross2/SampleCross4");
		return returnCross;
	}
}

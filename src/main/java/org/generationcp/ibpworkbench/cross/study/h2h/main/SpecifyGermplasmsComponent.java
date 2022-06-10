
package org.generationcp.ibpworkbench.cross.study.h2h.main;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.generationcp.middleware.api.germplasm.GermplasmNameService;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.cross.study.h2h.main.dialogs.SelectGermplasmEntryDialog;
import org.generationcp.ibpworkbench.cross.study.h2h.main.dialogs.SelectGermplasmListDialog;
import org.generationcp.ibpworkbench.cross.study.h2h.main.listeners.HeadToHeadCrossStudyMainButtonClickListener;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TablesEntries;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class SpecifyGermplasmsComponent extends AbsoluteLayout implements InitializingBean, InternationalizableComponent {

	private static final String SEARCH_BUTTON_WIDTH = "130px";

	private static final long serialVersionUID = -7925696669478799303L;
	private static final Logger LOG = LoggerFactory.getLogger(SpecifyGermplasmsComponent.class);

	public static final String NEXT_BUTTON_ID = "SpecifyGermplasmsComponent Next Button ID";
	public static final String SELECT_TEST_ENTRY_BUTTON_ID = "SpecifyGermplasmsComponent Select Test Entry Button ID";
	public static final String SELECT_STANDARD_ENTRY_BUTTON_ID = "SpecifyGermplasmsComponent Select Standard Entry Button ID";

	public static final String SELECT_TEST_SEARCH_GERMPLASM_BUTTON_ID = "SpecifyGermplasmsComponent Test Search Germplasm Button ID";
	public static final String SELECT_STANDARD_SEARCH_GERMPLASM_BUTTON_ID =
			"SpecifyGermplasmsComponent Standard Search Germplasm Button ID";
	public static final String SELECT_TEST_SEARCH_GERMPLASM_LIST_BUTTON_ID =
			"SpecifyGermplasmsComponent Test Search Germplasm List Button ID";
	public static final String SELECT_STANDARD_SEARCH_GERMPLASM_LIST_BUTTON_ID =
			"SpecifyGermplasmsComponent Standard Search Germplasm List Button ID";

	// maximum # of pairs can be rendered
	private static final BigInteger MAX_NUM_OF_PAIRS = new BigInteger("2000");

	private Button nextButton;

	private Button testSearchGermplasm;
	private Button standardSearchGermplasm;

	private Button testSearchGermplasmList;
	private Button standardSearchGermplasmList;

	private final HeadToHeadCrossStudyMain mainScreen;
	private final TraitsAvailableComponent nextScreen;

	private Table entriesTable;

	private static final String TEST_ENTRY_COLUMN_ID = "SpecifyGermplasmsComponent Test Entry Column Id";
	private static final String STANDARD_ENTRY_COLUMN_ID = "SpecifyGermplasmsComponent Standard Entry Column Id";
	private static final String TEST_ENTRY_GID = "SpecifyGermplasmsComponent Test Entry GID";
	private static final String STANDARD_ENTRY_GID = "SpecifyGermplasmsComponent Standard Entry GID";
	private static final String TEST_ENTRY_GROUPID = "SpecifyGermplasmsComponent Test Entry Group Id";
	private static final String STANDARD_ENTRY_GROUPID = "SpecifyGermplasmsComponent Standard Entry Group Id";

	private static final Action ACTION_SELECT_ALL = new Action("Select All");
	private static final Action ACTION_DELETE = new Action("Delete selected");
	private static final Action[] ACTIONS_TABLE_CONTEXT_MENU =
			new Action[] {SpecifyGermplasmsComponent.ACTION_SELECT_ALL, SpecifyGermplasmsComponent.ACTION_DELETE};

	@Autowired
	private GermplasmListManager germplasmListManager;

	private final Set<String> tableEntriesId = new HashSet<>();
	private Set<String> singleEntriesSet = new HashSet<>();

	private static final String TEST_ENTRY = "TEST";
	private static final String STANDARD_ENTRY = "STANDARD";

	private Map<String, String> germplasmIdNameMap;
	private Map<String, String> germplasmIdMGIDMap;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private GermplasmNameService germplasmNameService;

	public SpecifyGermplasmsComponent(final HeadToHeadCrossStudyMain mainScreen, final TraitsAvailableComponent nextScreen) {
		this.mainScreen = mainScreen;
		this.nextScreen = nextScreen;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setHeight("600px");
		this.setWidth("1000px");

		final Panel testPanel = new Panel("TEST");
		testPanel.setDebugId("testPanel");
		testPanel.setWidth("400px");
		testPanel.setSizeUndefined();

		final Panel standardPanel = new Panel("STANDARD");
		standardPanel.setDebugId("standardPanel");
		standardPanel.setWidth("470px");
		testPanel.setSizeUndefined();

		final AbsoluteLayout absLayout = new AbsoluteLayout();
		absLayout.setDebugId("absLayout");
		absLayout.setWidth("400px");
		absLayout.setHeight("90px");

		final Label testSearchGermplasmLabel = new Label(this.messageSource.getMessage(Message.SPECIFY_SINGLE_TEST_ENTRY));
		testSearchGermplasmLabel.setDebugId("testSearchGermplasmLabel");
		testSearchGermplasmLabel.setImmediate(true);

		final Label testSearchGermplasmListLabel = new Label(this.messageSource.getMessage(Message.SPECIFY_TEST_GERMPLASM_LIST_ENTRY));
		testSearchGermplasmListLabel.setDebugId("testSearchGermplasmListLabel");
		testSearchGermplasmListLabel.setImmediate(true);

		this.testSearchGermplasm = new Button(this.messageSource.getMessage(Message.HEAD_TO_HEAD_SEARCH_GERMPLASM));
		this.testSearchGermplasm.setDebugId("testSearchGermplasm");
		this.testSearchGermplasm.setData(SpecifyGermplasmsComponent.SELECT_TEST_SEARCH_GERMPLASM_BUTTON_ID);
		this.testSearchGermplasm.setWidth(SpecifyGermplasmsComponent.SEARCH_BUTTON_WIDTH);
		this.testSearchGermplasm.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));

		this.testSearchGermplasmList = new Button(this.messageSource.getMessage(Message.HEAD_TO_HEAD_BROWSE_LIST));
		this.testSearchGermplasmList.setDebugId("testSearchGermplasmList");
		this.testSearchGermplasmList.setData(SpecifyGermplasmsComponent.SELECT_TEST_SEARCH_GERMPLASM_LIST_BUTTON_ID);
		this.testSearchGermplasmList.setWidth(SpecifyGermplasmsComponent.SEARCH_BUTTON_WIDTH);
		this.testSearchGermplasmList.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));

		absLayout.addComponent(testSearchGermplasmLabel, "top:13px;left:2px");
		absLayout.addComponent(testSearchGermplasmListLabel, "top:53px;left:2px");
		absLayout.addComponent(this.testSearchGermplasm, "top:10px;left:260px");
		absLayout.addComponent(this.testSearchGermplasmList, "top:50px;left:260px");
		testPanel.addComponent(absLayout);

		final AbsoluteLayout absLayoutStandard = new AbsoluteLayout();
		absLayoutStandard.setDebugId("absLayoutStandard");
		absLayoutStandard.setWidth("450px");
		absLayoutStandard.setHeight("90px");

		final Label standardSearchGermplasmLabel = new Label(this.messageSource.getMessage(Message.SPECIFY_SINGLE_STANDARD_ENTRY));
		standardSearchGermplasmLabel.setDebugId("standardSearchGermplasmLabel");
		standardSearchGermplasmLabel.setImmediate(true);

		final Label standardSearchGermplasmListLabel =
				new Label(this.messageSource.getMessage(Message.SPECIFY_STANDARD_GERMPLASM_LIST_ENTRY));
		standardSearchGermplasmListLabel.setDebugId("standardSearchGermplasmListLabel");
		standardSearchGermplasmListLabel.setImmediate(true);

		this.standardSearchGermplasm = new Button(this.messageSource.getMessage(Message.HEAD_TO_HEAD_SEARCH_GERMPLASM));
		this.standardSearchGermplasm.setDebugId("standardSearchGermplasm");
		this.standardSearchGermplasm.setData(SpecifyGermplasmsComponent.SELECT_STANDARD_SEARCH_GERMPLASM_BUTTON_ID);
		this.standardSearchGermplasm.setWidth(SpecifyGermplasmsComponent.SEARCH_BUTTON_WIDTH);
		this.standardSearchGermplasm.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));

		this.standardSearchGermplasmList = new Button(this.messageSource.getMessage(Message.HEAD_TO_HEAD_BROWSE_LIST));
		this.standardSearchGermplasmList.setDebugId("standardSearchGermplasmList");
		this.standardSearchGermplasmList.setData(SpecifyGermplasmsComponent.SELECT_STANDARD_SEARCH_GERMPLASM_LIST_BUTTON_ID);
		this.standardSearchGermplasmList.setWidth(SpecifyGermplasmsComponent.SEARCH_BUTTON_WIDTH);
		this.standardSearchGermplasmList.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));

		absLayoutStandard.addComponent(standardSearchGermplasmLabel, "top:13px;left:2px");
		absLayoutStandard.addComponent(standardSearchGermplasmListLabel, "top:53px;left:2px");
		absLayoutStandard.addComponent(this.standardSearchGermplasm, "top:10px;left:290px");
		absLayoutStandard.addComponent(this.standardSearchGermplasmList, "top:50px;left:290px");
		standardPanel.addComponent(absLayoutStandard);

		final Label headerLabel = new Label(this.messageSource.getMessage(Message.SELECT_TEST_STANDARD_COMPARE));
		headerLabel.setDebugId("headerLabel");
		headerLabel.setImmediate(true);

		this.addComponent(headerLabel, "top:10px;left:20px");
		this.addComponent(testPanel, "top:30px;left:20px");
		this.addComponent(standardPanel, "top:30px;left:470px");

		this.entriesTable = new Table();
		this.entriesTable.setDebugId("entriesTable");
		this.entriesTable.setWidth("920px");
		this.entriesTable.setHeight("330px");
		this.entriesTable.setImmediate(true);
		this.entriesTable.setPageLength(0);

		this.entriesTable.addContainerProperty(SpecifyGermplasmsComponent.TEST_ENTRY_GROUPID, String.class, "-");
		final String mgid = ColumnLabels.GROUP_ID.getTermNameFromOntology(this.ontologyDataManager);
		this.entriesTable.setColumnHeader(SpecifyGermplasmsComponent.TEST_ENTRY_GROUPID, mgid);

		this.entriesTable.addContainerProperty(SpecifyGermplasmsComponent.TEST_ENTRY_GID, String.class, null);
		final String gid = ColumnLabels.GID.getTermNameFromOntology(this.ontologyDataManager);
		this.entriesTable.setColumnHeader(SpecifyGermplasmsComponent.TEST_ENTRY_GID, gid + " test");

		this.entriesTable.addContainerProperty(SpecifyGermplasmsComponent.TEST_ENTRY_COLUMN_ID, String.class, null);
		final String designation = ColumnLabels.DESIGNATION.getTermNameFromOntology(this.ontologyDataManager);
		this.entriesTable.setColumnHeader(SpecifyGermplasmsComponent.TEST_ENTRY_COLUMN_ID, "Test Entry " + designation);

		this.entriesTable.addContainerProperty(SpecifyGermplasmsComponent.STANDARD_ENTRY_GROUPID, String.class, "-");
		this.entriesTable.setColumnHeader(SpecifyGermplasmsComponent.STANDARD_ENTRY_GROUPID, mgid);

		this.entriesTable.addContainerProperty(SpecifyGermplasmsComponent.STANDARD_ENTRY_GID, String.class, null);
		this.entriesTable.setColumnHeader(SpecifyGermplasmsComponent.STANDARD_ENTRY_GID, gid + " standard");

		this.entriesTable.addContainerProperty(SpecifyGermplasmsComponent.STANDARD_ENTRY_COLUMN_ID, String.class, null);
		this.entriesTable.setColumnHeader(SpecifyGermplasmsComponent.STANDARD_ENTRY_COLUMN_ID, "Standard Entry " + designation);

		this.entriesTable
				.setVisibleColumns(new Object[] {SpecifyGermplasmsComponent.TEST_ENTRY_GROUPID, SpecifyGermplasmsComponent.TEST_ENTRY_GID,
						SpecifyGermplasmsComponent.TEST_ENTRY_COLUMN_ID, SpecifyGermplasmsComponent.STANDARD_ENTRY_GROUPID,
						SpecifyGermplasmsComponent.STANDARD_ENTRY_GID, SpecifyGermplasmsComponent.STANDARD_ENTRY_COLUMN_ID});
		this.entriesTable.setSelectable(true);
		this.entriesTable.setMultiSelect(true);
		this.entriesTable.setNullSelectionAllowed(false);

		this.entriesTable.addActionHandler(new Action.Handler() {

			private static final long serialVersionUID = 3972058734324800774L;

			@Override
			public Action[] getActions(final Object target, final Object sender) {
				return SpecifyGermplasmsComponent.ACTIONS_TABLE_CONTEXT_MENU;
			}

			@Override
			public void handleAction(final Action action, final Object sender, final Object target) {
				if (SpecifyGermplasmsComponent.ACTION_DELETE == action) {
					SpecifyGermplasmsComponent.this.deleteEntriesAction();
					SpecifyGermplasmsComponent.this.entriesTable.setPageLength(0);
				} else if (SpecifyGermplasmsComponent.ACTION_SELECT_ALL == action) {
					SpecifyGermplasmsComponent.this.entriesTable.setValue(SpecifyGermplasmsComponent.this.entriesTable.getItemIds());
					SpecifyGermplasmsComponent.this.entriesTable.setPageLength(0);
				}
			}
		});

		this.nextButton = new Button("Next");
		this.nextButton.setDebugId("nextButton");
		this.nextButton.setData(SpecifyGermplasmsComponent.NEXT_BUTTON_ID);
		this.nextButton.addListener(new HeadToHeadCrossStudyMainButtonClickListener(this));
		this.nextButton.setEnabled(false);
		this.nextButton.setWidth("80px");
		this.nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.addComponent(this.nextButton, "top:550px;left:460px");

		this.addComponent(this.entriesTable, "top:200px;left:20px");
	}

	private void deleteEntriesAction() {
		final Collection<?> selectedIds = (Collection<?>) this.entriesTable.getValue();
		if (!selectedIds.isEmpty()) {
			for (final Object itemId : selectedIds) {
				this.entriesTable.removeItem(itemId);
				this.tableEntriesId.remove(itemId);
			}
		}

		if (this.isTableEntriesEmpty()) {
			// we set the new set since we already cleared it
			this.singleEntriesSet = new HashSet<>();
			this.germplasmIdNameMap = new HashMap<>();
			this.germplasmIdMGIDMap = new HashMap<>();
		}

		if (this.isEitherTableEntriesEmpty()) {
			this.nextButton.setEnabled(false);
		} else {
			this.nextButton.setEnabled(true);
		}

	}

	public void selectTestEntryButtonClickAction() {
		final Window parentWindow = this.getWindow();
		final SelectGermplasmEntryDialog selectTestEntryAGermplasmDialog = new SelectGermplasmEntryDialog(this, parentWindow, true);
		selectTestEntryAGermplasmDialog.setDebugId("selectTestEntryAGermplasmDialog");
		selectTestEntryAGermplasmDialog.addStyleName(Reindeer.WINDOW_LIGHT);
		parentWindow.addWindow(selectTestEntryAGermplasmDialog);
	}

	public void selectStandardEntryButtonClickAction() {
		final Window parentWindow = this.getWindow();
		final SelectGermplasmEntryDialog selectStandardEntryAGermplasmDialog = new SelectGermplasmEntryDialog(this, parentWindow, false);
		selectStandardEntryAGermplasmDialog.setDebugId("selectStandardEntryAGermplasmDialog");
		selectStandardEntryAGermplasmDialog.addStyleName(Reindeer.WINDOW_LIGHT);
		parentWindow.addWindow(selectStandardEntryAGermplasmDialog);
	}

	public void selectTestGermplasmListButtonClickAction() {
		final Window parentWindow = this.getWindow();
		final SelectGermplasmListDialog selectTestGermplasmListAGermplasmDialog = new SelectGermplasmListDialog(this, parentWindow, true);
		selectTestGermplasmListAGermplasmDialog.setDebugId("selectTestGermplasmListAGermplasmDialog");
		selectTestGermplasmListAGermplasmDialog.addStyleName(Reindeer.WINDOW_LIGHT);
		parentWindow.addWindow(selectTestGermplasmListAGermplasmDialog);
	}

	public void selectStandardGermplasmListButtonClickAction() {
		final Window parentWindow = this.getWindow();
		final SelectGermplasmListDialog selectAGermplasmDialog = new SelectGermplasmListDialog(this, parentWindow, false);
		selectAGermplasmDialog.setDebugId("selectAGermplasmDialog");
		selectAGermplasmDialog.addStyleName(Reindeer.WINDOW_LIGHT);
		parentWindow.addWindow(selectAGermplasmDialog);
	}

	public void nextButtonClickAction() {
		if (this.nextScreen != null) {
			this.nextScreen.populateTraitsAvailableTable(this.getGermplasmPairs(), this.germplasmIdNameMap, this.germplasmIdMGIDMap);
			this.mainScreen.selectSecondTab();

		}
	}

	@SuppressWarnings("rawtypes")
	private List<GermplasmPair> getGermplasmPairs() {
		final List<GermplasmPair> pairList = new ArrayList<>();

		final Iterator iter = this.entriesTable.getItemIds().iterator();

		while (iter.hasNext()) {
			// we iterate and permutate against the list
			final String id = (String) iter.next();
			String leftId = "";
			String rightId = "";
			final StringTokenizer tokenizer = new StringTokenizer(id, ":");
			if (tokenizer.countTokens() == 2) {
				leftId = tokenizer.nextToken().trim();
				rightId = tokenizer.nextToken().trim();
			}

			final GermplasmPair germplasmPair = new GermplasmPair(Integer.valueOf(leftId), Integer.valueOf(rightId));
			pairList.add(germplasmPair);

		}
		return pairList;
	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	private boolean permutationsWillExceedMax(final Integer listSize) {
		if (this.entriesTable != null) {
			Integer tableSize = this.entriesTable.size();
			// if table empty, use 1 as divisor
			tableSize = tableSize == 0 ? 1 : tableSize;

			final BigInteger selectedListSize = new BigInteger(listSize.toString());
			final BigInteger currentTableSize = new BigInteger(tableSize.toString());
			final BigInteger maxAllowableSize = SpecifyGermplasmsComponent.MAX_NUM_OF_PAIRS.divide(currentTableSize);

			return maxAllowableSize.compareTo(selectedListSize) < 0;
		}

		return true;
	}

	public boolean addGermplasmList(final Integer germplasmListId, final Integer listSize, final boolean isTestEntry) {
		// do not allow to select list if resulting pairs will exceed max
		if (!this.permutationsWillExceedMax(listSize)) {
			try {

				final List<GermplasmListData> listGermplasmListData =
						this.germplasmListManager.getGermplasmListDataByListId(germplasmListId);

				if (listGermplasmListData != null && !listGermplasmListData.isEmpty()) {
					this.doGermplasmPermutationOnTable(isTestEntry, false, listGermplasmListData, null);
					return true;
				}

			} catch (final Exception e) {
				SpecifyGermplasmsComponent.LOG.error(e.getMessage(), e);
			}

		} else {
			MessageNotifier.showWarning(this.getWindow(), "Warning", "The list selected will create too "
					+ "many germplasm pairs and may cause the tool to crash. Please select another list.");
		}

		return false;
	}

	public void addTestGermplasm(final Germplasm germplasm) {
		if (germplasm != null) {
			this.doGermplasmPermutationOnTable(true, true, null, germplasm);
		}
	}

	public void addStandardGermplasm(final Germplasm germplasm) {
		if (germplasm != null) {
			this.doGermplasmPermutationOnTable(false, true, null, germplasm);
		}
	}

	private void doGermplasmPermutationOnTable(final boolean isTestEntry, final boolean isGermplasm,
			List<GermplasmListData> germplasmListDataList, final Germplasm germplasm) {

		final Map<String, Map<String, String>> map = this.getBothMapEntries();
		final Map<String, String> testMap = map.get(SpecifyGermplasmsComponent.TEST_ENTRY);
		final Map<String, String> standardMap = map.get(SpecifyGermplasmsComponent.STANDARD_ENTRY);
		final List<TablesEntries> tableEntriesList = new ArrayList<>();

		if (this.isTableEntriesEmpty()) {
			this.germplasmIdNameMap = new HashMap<>();
			this.germplasmIdMGIDMap = new HashMap<>();
		}

		// create a germplasm list with the germplasm as the sole list item
		if (isGermplasm) {
			germplasmListDataList = new ArrayList<>();

			final GermplasmListData germplasmData = new GermplasmListData();
			// GID field will be checked/used
			germplasmData.setGid(germplasm.getGid());
			// Group ID will be displayed in the table
			germplasmData.setGroupId(germplasm.getMgid());
			germplasmListDataList.add(germplasmData);
		}

		this.permutateGermplasmListToPartnerEntries(isTestEntry, testMap, standardMap, tableEntriesList, germplasmListDataList);

		this.addToTable(tableEntriesList);

		if (this.isEitherTableEntriesEmpty()) {
			this.nextButton.setEnabled(false);
		} else {
			this.nextButton.setEnabled(true);
		}

	}

	void permutateGermplasmListToPartnerEntries(final boolean isTestEntry, final Map<String, String> testMap,
			final Map<String, String> standardMap, final List<TablesEntries> tableEntriesList,
			final List<GermplasmListData> germplasmListData) {

		Map<String, String> ownMap = testMap;
		Map<String, String> partnerMap = standardMap;

		if (!isTestEntry) {
			ownMap = standardMap;
			partnerMap = testMap;
		}

		if (ownMap.keySet().isEmpty() && !partnerMap.keySet().isEmpty()) {
			// we need to remove all
			this.deleteAllSingleEntriesInTableListData();
		}

		if (partnerMap.keySet().isEmpty()) {
			final List<Integer> gids = germplasmListData.stream().map(GermplasmListData::getGid).collect(Collectors.toList());
			final Map<Integer, String> preferredNamesMap = this.germplasmNameService.getPreferredNamesByGIDs(gids);
			// just add on one side
			for (final GermplasmListData listData : germplasmListData) {

				final String gid = listData.getGid().toString();
				final String germplasmName = preferredNamesMap.get(listData.getGid());

				String testEntryName = germplasmName;
				String standardEntryName = "";
				String newId = gid + ": ";
				String standardEntryGID = null;
				String testEntryGID = gid;
				String standardEntryMGID = null;
				String testEntryMGID = listData.getGroupId() != 0 ? listData.getGroupId().toString() : "-";
				this.germplasmIdMGIDMap.put(gid, testEntryMGID);
				if (!isTestEntry) {
					standardEntryName = germplasmName;
					testEntryName = "";
					newId = " :" + gid;
					standardEntryGID = gid;
					testEntryGID = null;
					standardEntryMGID = testEntryMGID;
					testEntryMGID = null;
				}

				final TablesEntries entry = new TablesEntries(testEntryName, standardEntryName, newId, testEntryGID, standardEntryGID,
						testEntryMGID, standardEntryMGID);
				tableEntriesList.add(entry);
				this.singleEntriesSet.add(newId);
				this.germplasmIdNameMap.put(gid, germplasmName);
			}

		} else {
			final List<Integer> gids = germplasmListData.stream().map(GermplasmListData::getGid).collect(Collectors.toList());
			final Map<Integer, String> preferredNamesMap = this.germplasmNameService.getPreferredNamesByGIDs(gids);
			// we iterate
			for (final GermplasmListData listData : germplasmListData) {
				final Iterator<String> partnerIterator = partnerMap.keySet().iterator();
				final String gid = listData.getGid().toString();
				final String germplasmName = preferredNamesMap.get(listData.getGid());

				while (partnerIterator.hasNext()) {
					final String partnerId = partnerIterator.next();
					final String partnerName = partnerMap.get(partnerId);

					String testEntryName = germplasmName;
					String standardEntryName = partnerName;
					String newId = gid + ":" + partnerId;

					String standardEntryGID = partnerId;
					String testEntryGID = gid;
					String standardEntryMGID = this.germplasmIdMGIDMap.get(partnerId);
					String testEntryMGID = listData.getGroupId() != 0 ? listData.getGroupId().toString() : "-";
					this.germplasmIdMGIDMap.put(gid, testEntryMGID);
					if (!isTestEntry) {
						testEntryName = partnerName;
						standardEntryName = germplasmName;
						newId = partnerId + ":" + gid;
						standardEntryGID = gid;
						testEntryGID = partnerId;
						standardEntryMGID = testEntryMGID;
						testEntryMGID = this.germplasmIdMGIDMap.get(partnerId);
					}

					if (!gid.equalsIgnoreCase(partnerId)) {
						final TablesEntries entry = new TablesEntries(testEntryName, standardEntryName, newId, testEntryGID,
								standardEntryGID, testEntryMGID, standardEntryMGID);
						tableEntriesList.add(entry);
					}
					this.germplasmIdNameMap.put(partnerId, partnerName);
				}

				this.germplasmIdNameMap.put(gid, germplasmName);
			}

		}
	}

	private void deleteAllSingleEntriesInTableListData() {
		// we delete the single entries
		for (final String idToDelete : this.singleEntriesSet) {
			this.entriesTable.removeItem(idToDelete);
			this.tableEntriesId.remove(idToDelete);
		}

		this.entriesTable.setPageLength(0);

		// we set the new set since we already cleared it
		if (this.isTableEntriesEmpty()) {
			this.singleEntriesSet = new HashSet<>();
		}
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Map<String, String>> getBothMapEntries() {
		final Map<String, String> testMap = new HashMap<>();
		final Map<String, String> standardMap = new HashMap<>();
		final Map<String, Map<String, String>> resultMap = new HashMap<>();
		final Iterator iter = this.entriesTable.getItemIds().iterator();
		while (iter.hasNext()) {
			final String id = (String) iter.next();
			final StringTokenizer tokenizer = new StringTokenizer(id, ":");
			String leftId = "";
			String rightId = "";
			final Item item = this.entriesTable.getItem(id);

			if (tokenizer.countTokens() == 2) {
				leftId = tokenizer.nextToken().trim();
				rightId = tokenizer.nextToken().trim();
			}
			final String testEntryName = (String) item.getItemProperty(SpecifyGermplasmsComponent.TEST_ENTRY_COLUMN_ID).getValue();
			final String standardEntryName = (String) item.getItemProperty(SpecifyGermplasmsComponent.STANDARD_ENTRY_COLUMN_ID).getValue();

			if (leftId != null && !"".equalsIgnoreCase(leftId)) {
				testMap.put(leftId, testEntryName);
			}
			if (rightId != null && !"".equalsIgnoreCase(rightId)) {
				standardMap.put(rightId, standardEntryName);
			}
		}
		resultMap.put(SpecifyGermplasmsComponent.TEST_ENTRY, testMap);
		resultMap.put(SpecifyGermplasmsComponent.STANDARD_ENTRY, standardMap);
		return resultMap;
	}

	private void addToTable(final List<TablesEntries> tableEntryList) {
		for (final TablesEntries tableEntry : tableEntryList) {
			final String newId = tableEntry.getTestStandardEntry();
			// if not in map, add it in the table

			if (!this.tableEntriesId.contains(newId)) {
				this.entriesTable.addItem(
						new Object[] {tableEntry.getTestEntryGroupID(), tableEntry.getTestEntryGID(), tableEntry.getTestEntryName(),
								tableEntry.getStandardEntryGroupID(), tableEntry.getStandardEntryGID(), tableEntry.getStandardEntryName()},
						tableEntry.getTestStandardEntry());
				this.tableEntriesId.add(newId);
			}

			this.entriesTable.setPageLength(0);
		}
	}

	private boolean isTableEntriesEmpty() {
		final Map<String, Map<String, String>> map = this.getBothMapEntries();
		final Map<String, String> testMap = map.get(SpecifyGermplasmsComponent.TEST_ENTRY);
		final Map<String, String> standardMap = map.get(SpecifyGermplasmsComponent.STANDARD_ENTRY);

		return testMap.keySet().isEmpty() && standardMap.keySet().isEmpty();
	}

	private boolean isEitherTableEntriesEmpty() {
		final Map<String, Map<String, String>> map = this.getBothMapEntries();
		final Map<String, String> testMap = map.get(SpecifyGermplasmsComponent.TEST_ENTRY);
		final Map<String, String> standardMap = map.get(SpecifyGermplasmsComponent.STANDARD_ENTRY);

		return testMap.keySet().isEmpty() || standardMap.keySet().isEmpty();
	}

	void setGermplasmIdMGIDMap(final Map<String, String> germplasmIdMGIDMap) {
		this.germplasmIdMGIDMap = germplasmIdMGIDMap;
	}

	void setSingleEntriesSet(final Set<String> singleEntriesSet) {
		this.singleEntriesSet = singleEntriesSet;
	}

	void setGermplasmIdNameMap(final Map<String, String> germplasmIdNameMap) {
		this.germplasmIdNameMap = germplasmIdNameMap;
	}

	void setEntriesTable(final Table entriesTable) {
		this.entriesTable = entriesTable;
	}

	void setGermplasmNameService(final GermplasmNameService germplasmNameService) {
		this.germplasmNameService = germplasmNameService;
	}
}

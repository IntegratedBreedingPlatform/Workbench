package org.generationcp.breeding.manager.crossingmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.crossingmanager.listeners.ParentsTableCheckboxListener;
import org.generationcp.breeding.manager.crossingmanager.pojos.GermplasmListEntry;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.breeding.manager.crossingmanager.listeners.GidLinkClickListener;
import org.generationcp.commons.security.AuthorizationService;
import org.generationcp.middleware.api.germplasm.GermplasmNameService;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.workbench.PermissionsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class MakeCrossesParentsComponent extends VerticalLayout implements BreedingManagerLayout, InitializingBean,
	InternationalizableComponent {

	private static final String CLICK_TO_VIEW_GERMPLASM_INFORMATION = "Click to view Germplasm information";
	private static final Logger LOG = LoggerFactory.getLogger(MakeCrossesParentsComponent.class);
	private static final long serialVersionUID = -4789763601080845176L;

	private static final int PARENTS_TABLE_ROW_COUNT = 5;

	private static final String TAG_COLUMN_ID = "Tag";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Autowired
	private GermplasmNameService germplasmNameService;

	@Autowired
	private AuthorizationService authorizationService;

	private boolean hasViewGermplasmDetailsPermission;

	private TabSheet femaleParentTabSheet;
	private TabSheet maleParentTabSheet;

	private Label parentListsLabel;
	private Label instructionForParentLists;

	private ParentTabComponent femaleParentTab;
	private ParentTabComponent maleParentTab;

	private CrossingManagerMakeCrossesComponent makeCrossesMain;

	public MakeCrossesParentsComponent(final CrossingManagerMakeCrossesComponent parentComponent) {
		this.makeCrossesMain = parentComponent;
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.hasViewGermplasmDetailsPermission = this.authorizationService.hasAnyAuthority(PermissionsEnum.VIEW_GERMPLASM_DETAILS_PERMISSIONS);
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	@Override
	public void instantiateComponents() {
		this.parentListsLabel = new Label(this.messageSource.getMessage(Message.PARENTS_LISTS));
		this.parentListsLabel.setDebugId("parentListsLabel");
		this.parentListsLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		this.parentListsLabel.addStyleName(AppConstants.CssStyles.BOLD);
		this.parentListsLabel.setWidth("230px");

		this.instructionForParentLists = new Label(this.messageSource.getMessage(Message.INSTRUCTION_FOR_PARENT_LISTS));
		this.instructionForParentLists.setDebugId("instructionForParentLists");

		this.femaleParentTab =
			new ParentTabComponent(this.makeCrossesMain, this, this.messageSource.getMessage(Message.LABEL_FEMALE_PARENTS),
				MakeCrossesParentsComponent.PARENTS_TABLE_ROW_COUNT);

		this.maleParentTab = new ParentTabComponent(this.makeCrossesMain, this, this.messageSource.getMessage(Message.LABEL_MALE_PARENTS),
			MakeCrossesParentsComponent.PARENTS_TABLE_ROW_COUNT);
	}

	@Override
	public void initializeValues() {
		// do nothing
	}

	@Override
	public void addListeners() {
		// do nothing
	}

	@Override
	public void layoutComponents() {
		this.setMargin(false, false, false, true);
		this.setWidth("100%");

		this.femaleParentTabSheet = new TabSheet();
		this.femaleParentTabSheet.setDebugId("femaleParentTabSheet");
		this.femaleParentTabSheet.addTab(this.femaleParentTab, this.messageSource.getMessage(Message.LABEL_FEMALE_PARENTS));
		this.femaleParentTabSheet.setWidth("420px");
		this.femaleParentTabSheet.setHeight("365px");

		this.maleParentTabSheet = new TabSheet();
		this.maleParentTabSheet.setDebugId("maleParentTabSheet");
		this.maleParentTabSheet.addTab(this.maleParentTab, this.messageSource.getMessage(Message.LABEL_MALE_PARENTS));
		this.maleParentTabSheet.setWidth("420px");
		this.maleParentTabSheet.setHeight("365px");

		final HorizontalLayout parentListHLayout = new HorizontalLayout();
		parentListHLayout.setWidth("100%");
		parentListHLayout.setDebugId("parentListHLayout");
		parentListHLayout.addComponent(this.femaleParentTabSheet);
		parentListHLayout.setSpacing(true);
		parentListHLayout.addComponent(this.maleParentTabSheet);

		final HeaderLabelLayout parentLabelLayout = new HeaderLabelLayout(AppConstants.Icons.ICON_LIST_TYPES, this.parentListsLabel);
		parentLabelLayout.setDebugId("parentLabelLayout");
		this.addComponent(parentLabelLayout);
		this.addComponent(this.instructionForParentLists);
		this.addComponent(parentListHLayout);
	}

	// end of layoutComponent

	@SuppressWarnings("unchecked")
	public void dropToFemaleOrMaleTable(final Table sourceTable, final Table targetTable, final Integer transferrableItemId) {
		final List<Integer> selectedListEntries = new ArrayList<>();
		selectedListEntries.addAll((Collection<Integer>) sourceTable.getValue());

		if (selectedListEntries.isEmpty() && transferrableItemId != null) {
			selectedListEntries.add(transferrableItemId);
		}

		final List<Integer> entryIdsInSourceTable = new ArrayList<>();
		entryIdsInSourceTable.addAll((Collection<Integer>) sourceTable.getItemIds());

		final List<Integer> initialEntryIdsInDestinationTable = new ArrayList<>();
		initialEntryIdsInDestinationTable.addAll((Collection<Integer>) targetTable.getItemIds());

		for (final Integer itemId : entryIdsInSourceTable) {
			if (selectedListEntries.contains(itemId)) {
				final Integer entryId = (Integer) sourceTable.getItem(itemId).getItemProperty(ColumnLabels.ENTRY_ID.getName()).getValue();

				final String parentage = (String) sourceTable.getItem(itemId).getItemProperty(ColumnLabels.PARENTAGE.getName()).getValue();
				final String designation;
				final Integer gid;
				if (this.hasViewGermplasmDetailsPermission) {
					final Button designationBtn =
							(Button) sourceTable.getItem(itemId).getItemProperty(ColumnLabels.DESIGNATION.getName()).getValue();
					designation  =designationBtn.getCaption();

					final Button gidBtn = (Button) sourceTable.getItem(itemId).getItemProperty(ColumnLabels.GID.getName()).getValue();
					gid = Integer.valueOf(Integer.parseInt(gidBtn.getCaption()));
				} else {
					designation = (String) sourceTable.getItem(itemId).getItemProperty(ColumnLabels.DESIGNATION.getName()).getValue();
					gid = Integer.valueOf((String) sourceTable.getItem(itemId).getItemProperty(ColumnLabels.GID.getName()).getValue());
				}

				final String seedSource = this.getSeedSource(sourceTable, entryId);

				final GermplasmListEntry entryObject = new GermplasmListEntry(itemId, gid, entryId, designation, seedSource);
				final Item item = targetTable.addItem(entryObject);

				if (item != null) {
					this.getDesignationComponent(designation, gid, "newGidButton", item);
					if (targetTable.equals(this.femaleParentTab.getListDataTable())) {
						entryObject.setFromFemaleTable(true);
						this.femaleParentTab.updateNoOfEntries(this.femaleParentTab.getListDataTable().size());
					} else {
						entryObject.setFromFemaleTable(false);
						this.maleParentTab.updateNoOfEntries(this.maleParentTab.getListDataTable().size());
					}

					final CheckBox tag = new CheckBox();
					tag.setDebugId("tag");
					if (targetTable.equals(this.femaleParentTab.getListDataTable())) {
						tag.addListener(
							new ParentsTableCheckboxListener(targetTable, entryObject, this.femaleParentTab.getSelectAllCheckBox()));
					} else {
						tag.addListener(
							new ParentsTableCheckboxListener(targetTable, entryObject, this.maleParentTab.getSelectAllCheckBox()));
					}
					tag.setImmediate(true);
					tag.setValue(true);
					item.getItemProperty(MakeCrossesParentsComponent.TAG_COLUMN_ID).setValue(tag);

					item.getItemProperty(ColumnLabels.PARENTAGE.getName()).setValue(parentage);

					final Collection<GermplasmListEntry> selectedEntries = (Collection<GermplasmListEntry>) targetTable.getValue();
					final Set<GermplasmListEntry> entriesToSelect = new HashSet<>();

					if (selectedEntries != null) {
						entriesToSelect.addAll(selectedEntries);
						entriesToSelect.add(entryObject);
					} else {
						entriesToSelect.add(entryObject);
					}
					targetTable.setValue(entriesToSelect);
				}
			}
			targetTable.requestRepaint();
		}

		final List<Integer> entryIdsInDestinationTable = new ArrayList<>();
		entryIdsInDestinationTable.addAll((Collection<Integer>) targetTable.getItemIds());

		// drag all entries of a list to the parent list
		if (initialEntryIdsInDestinationTable.isEmpty() && entryIdsInSourceTable.size() == entryIdsInDestinationTable.size()) {
			if (targetTable.equals(this.femaleParentTab.getListDataTable())) {
				final GermplasmList femaleGermplasmList =
					((SelectParentsListDataComponent) this.makeCrossesMain.getSelectParentsComponent().getListDetailsTabSheet()
						.getSelectedTab()).getGermplasmList();
				this.updateFemaleParentList(femaleGermplasmList);
			} else {
				final GermplasmList maleGermplasmList =
					((SelectParentsListDataComponent) this.makeCrossesMain.getSelectParentsComponent().getListDetailsTabSheet()
						.getSelectedTab()).getGermplasmList();
				this.updateMaleParentList(maleGermplasmList);
			}

			// updates the crosses made save button if both parents are save at least once
			this.makeCrossesMain.getCrossesTableComponent().updateCrossesMadeSaveButton();

		}
	}

	@SuppressWarnings("unchecked")
	public void assignEntryNumber(final Table parentsTable) {

		int entryNumber = 1;
		final List<GermplasmListEntry> itemIds = new ArrayList<>();
		itemIds.addAll((Collection<GermplasmListEntry>) parentsTable.getItemIds());

		for (final GermplasmListEntry entry : itemIds) {
			final Item item = parentsTable.getItem(entry);
			item.getItemProperty(ColumnLabels.ENTRY_ID.getName()).setValue(Integer.valueOf(entryNumber));
			entry.setEntryId(entryNumber);
			entryNumber++;
		}
	}

	public void updateFemaleListNameForCrosses() {
		String femaleListNameForCrosses = "";
		femaleListNameForCrosses = this.getFemaleList() != null ? this.getFemaleList().getName() : "";
		this.femaleParentTab.setListNameForCrosses(femaleListNameForCrosses);
	}

	public void updateMaleListNameForCrosses() {
		String maleListNameForCrosses = "";
		maleListNameForCrosses = this.getMaleList() != null ? this.getMaleList().getName() : "";
		this.maleParentTab.setListNameForCrosses(maleListNameForCrosses);
	}



	public String getSeedSource(final Table table, final Integer entryId) {
		String seedSource = "";
		if (table.getParent() != null && table.getParent().getParent() instanceof SelectParentsListDataComponent) {
			final SelectParentsListDataComponent parentComponent = (SelectParentsListDataComponent) table.getParent().getParent();
			final String listname = parentComponent.getListName();
			seedSource = listname + ":" + entryId;
		}

		return seedSource;
	}

	/**
	 * Implemented something similar to table.getValue(), because that method returns a collection of items, but does not follow the sorting
	 * done by the drag n drop sorting, this one does
	 *
	 * @param table
	 * @return List of selected germplasm list entries
	 */
	@SuppressWarnings("unchecked")
	public List<GermplasmListEntry> getCorrectSortedValue(final Table table) {
		final List<GermplasmListEntry> allItemIds = new ArrayList<>();
		final List<GermplasmListEntry> selectedItemIds = new ArrayList<>();
		final List<GermplasmListEntry> sortedSelectedValues = new ArrayList<>();

		allItemIds.addAll((Collection<GermplasmListEntry>) table.getItemIds());

		for (final GermplasmListEntry entry : allItemIds) {
			final CheckBox tag = (CheckBox) table.getItem(entry).getItemProperty(MakeCrossesParentsComponent.TAG_COLUMN_ID).getValue();
			final Boolean tagValue = (Boolean) tag.getValue();
			if (tagValue.booleanValue()) {
				selectedItemIds.add(entry);
			}
		}

		for (final GermplasmListEntry itemId : allItemIds) {
			for (final GermplasmListEntry selectedItemId : selectedItemIds) {
				if (itemId.equals(selectedItemId)) {
					sortedSelectedValues.add(selectedItemId);
				}
			}
		}
		return sortedSelectedValues;
	}

	public void addListToMaleTable(final Integer germplasmListId) {

		try {
			final GermplasmList listFromTree = this.germplasmListManager.getGermplasmListById(germplasmListId);
			if (listFromTree != null) {
				final List<GermplasmListData> germplasmListDataFromListFromTree =
					this.inventoryDataManager.getLotCountsForList(germplasmListId, 0, Integer.MAX_VALUE);

				Integer addedCount = 0;

				final List<Integer> gids = germplasmListDataFromListFromTree.stream().map(GermplasmListData::getGid).collect(Collectors.toList());
				final Map<Integer, String> preferredNamesMap = this.germplasmNameService.getPreferredNamesByGIDs(gids);

				for (final GermplasmListData listData : germplasmListDataFromListFromTree) {
					if (listData.getStatus() != 9) {
						final String maleParentValue = preferredNamesMap.get(listData.getGid());
						final CheckBox tag = new CheckBox();
						tag.setDebugId("tag");

						final GermplasmListEntry entryObject =
							new GermplasmListEntry(listData.getId(), listData.getGid(), listData.getEntryId(),
								maleParentValue, listFromTree.getName() + ":" + listData.getEntryId());

						tag.addListener(new ParentsTableCheckboxListener(this.maleParentTab.getListDataTable(), entryObject,
							this.maleParentTab.getSelectAllCheckBox()));
						tag.setImmediate(true);

						// if the item is already existing in the target table, remove the existing item then add a new entry
						this.maleParentTab.getListDataTable().removeItem(entryObject);

						final Item item = this.maleParentTab.getListDataTable().addItem(entryObject);
						this.getDesignationComponent(maleParentValue, listData.getGid(), "gidButton", item);

						item.getItemProperty(MakeCrossesParentsComponent.TAG_COLUMN_ID).setValue(tag);

						addedCount++;
					}
				}

				// After adding, check if the # of items added on the table, is equal to the number of list data of the dragged list, this
				// will enable/disable the save option
				final List<Object> itemsLeftAfterAdding = new ArrayList<>();
				itemsLeftAfterAdding.addAll(this.maleParentTab.getListDataTable().getItemIds());

				if (addedCount == itemsLeftAfterAdding.size()) {
					// updates the crosses made save button if both parents are save at least once
					this.makeCrossesMain.getCrossesTableComponent().updateCrossesMadeSaveButton();
				}
			}

			// set up the Germplasm List in Parent Tab
			this.updateMaleParentList(listFromTree);

		} catch (final MiddlewareQueryException e) {
			MakeCrossesParentsComponent.LOG.error("Error in getting list by GID", e);
		}

		this.assignEntryNumber(this.maleParentTab.getListDataTable());
		this.maleParentTabSheet.setSelectedTab(1);

		this.maleParentTab.updateNoOfEntries(this.maleParentTab.getListDataTable().size());

	}

	private void getDesignationComponent(final String maleParentValue, final Integer listData, final String debugId, final Item item) {
		if (this.hasViewGermplasmDetailsPermission) {
			final Button gidButton = new Button(maleParentValue, new GidLinkClickListener(listData.toString(), true));
			gidButton.setDebugId(debugId);
			gidButton.setStyleName(BaseTheme.BUTTON_LINK);
			gidButton.setDescription(MakeCrossesParentsComponent.CLICK_TO_VIEW_GERMPLASM_INFORMATION);
			item.getItemProperty(ColumnLabels.DESIGNATION.getName()).setValue(gidButton);
		} else {
			item.getItemProperty(ColumnLabels.DESIGNATION.getName()).setValue(maleParentValue);
		}
	}

	public void addListToFemaleTable(final Integer germplasmListId) {

		try {
			final GermplasmList listFromTree = this.germplasmListManager.getGermplasmListById(germplasmListId);

			if (listFromTree != null) {

				final List<GermplasmListData> germplasmListDataFromListFromTree =
					this.inventoryDataManager.getLotCountsForList(germplasmListId, 0, Integer.MAX_VALUE);

				Integer addedCount = 0;

				final List<Integer> gids = germplasmListDataFromListFromTree.stream().map(GermplasmListData::getGid).collect(Collectors.toList());
				final Map<Integer, String> preferredNamesMap = this.germplasmNameService.getPreferredNamesByGIDs(gids);

				for (final GermplasmListData listData : germplasmListDataFromListFromTree) {
					if (listData.getStatus() != 9) {
						final String femaleParentValue = preferredNamesMap.get(listData.getGid());

						final CheckBox tag = new CheckBox();
						tag.setDebugId("tag");

						final GermplasmListEntry entryObject =
							new GermplasmListEntry(listData.getId(), listData.getGid(), listData.getEntryId(),
								femaleParentValue, listFromTree.getName() + ":" + listData.getEntryId());

						tag.addListener(new ParentsTableCheckboxListener(this.femaleParentTab.getListDataTable(), entryObject,
							this.femaleParentTab.getSelectAllCheckBox()));
						tag.setImmediate(true);

						// if the item is already existing in the target table, remove the existing item then add a new entry
						this.femaleParentTab.getListDataTable().removeItem(entryObject);

						final Item item = this.femaleParentTab.getListDataTable().addItem(entryObject);
						this.getDesignationComponent(femaleParentValue, listData.getGid(), "gidButton", item);

						item.getItemProperty(MakeCrossesParentsComponent.TAG_COLUMN_ID).setValue(tag);

						addedCount++;
					}
				}

				// After adding, check if the # of items added on the table, is equal to the number of list data of the dragged list, this
				// will enable/disable the save option
				final List<Object> itemsLeftAfterAdding = new ArrayList<>();
				itemsLeftAfterAdding.addAll(this.femaleParentTab.getListDataTable().getItemIds());

				if (addedCount == itemsLeftAfterAdding.size()) {
					// updates the crosses made save button if both parents are save at least once
					this.makeCrossesMain.getCrossesTableComponent().updateCrossesMadeSaveButton();
				}
			}

			// set up the Germplasm List in Parent Tab
			this.updateFemaleParentList(listFromTree);

		} catch (final MiddlewareQueryException e) {
			MakeCrossesParentsComponent.LOG.error("Error in getting list by GID", e);
		}

		this.assignEntryNumber(this.femaleParentTab.getListDataTable());
		this.femaleParentTabSheet.setSelectedTab(0);

		this.femaleParentTab.updateNoOfEntries(this.femaleParentTab.getListDataTable().size());
	}

	protected void updateMaleParentList(final GermplasmList listFromTree) {
		// whenever we add a list to male parent tab, only the first list added will be marked as the working list
		if (this.maleParentTab.getGermplasmList() == null && listFromTree != null) {
			this.maleParentTab.setGermplasmList(listFromTree);
			this.maleParentTab.setListNameForCrosses(listFromTree.getName());
		}

		this.maleParentTab.updateNoOfEntries();
	}

	protected void updateFemaleParentList(final GermplasmList listFromTree) {
		// whenever we add a list to female parent tab, only the first list added will be marked as the working list
		if (this.femaleParentTab.getGermplasmList() == null && listFromTree != null) {
			this.femaleParentTab.setGermplasmList(listFromTree);
			this.femaleParentTab.setListNameForCrosses(listFromTree.getName());
		}
		this.femaleParentTab.updateNoOfEntries();
	}

	// SETTERS AND GETTERS
	public Table getFemaleTable() {
		return this.femaleParentTab.getListDataTable();
	}

	public Table getMaleTable() {
		return this.maleParentTab.getListDataTable();
	}

	public GermplasmList getFemaleList() {
		return this.femaleParentTab.getGermplasmList();
	}

	public GermplasmList getMaleList() {
		return this.maleParentTab.getGermplasmList();
	}

	public String getFemaleListNameForCrosses() {
		return this.femaleParentTab.getListNameForCrosses();
	}

	public String getMaleListNameForCrosses() {
		return this.maleParentTab.getListNameForCrosses();
	}

	public ParentTabComponent getFemaleParentTab() {
		return this.femaleParentTab;
	}

	public void setFemaleParentTab(final ParentTabComponent femaleParentTab) {
		this.femaleParentTab = femaleParentTab;
	}

	public ParentTabComponent getMaleParentTab() {
		return this.maleParentTab;
	}

	public void setMaleParentTab(final ParentTabComponent maleParentTab) {
		this.maleParentTab = maleParentTab;
	}

	public void setMakeCrossesMain(final CrossingManagerMakeCrossesComponent makeCrossesMain) {
		this.makeCrossesMain = makeCrossesMain;
	}

	public CrossingManagerMakeCrossesComponent getMakeCrossesMain() {
		return this.makeCrossesMain;
	}

	public void updateUIForDeletedList(final GermplasmList germplasmList) {
		if (this.femaleParentTab.getGermplasmList() != null && this.femaleParentTab.getGermplasmList().getName()
			.equals(germplasmList.getName())) {

			this.femaleParentTab.updateUIforDeletedList(germplasmList);

		}

		if (this.maleParentTab.getGermplasmList() != null && this.maleParentTab.getGermplasmList().getName()
			.equals(germplasmList.getName())) {
			this.maleParentTab.updateUIforDeletedList(germplasmList);

		}
	}

	public TabSheet getFemaleParentTabSheet() {
		return this.femaleParentTabSheet;
	}

	public TabSheet getMaleParentTabSheet() {
		return this.maleParentTabSheet;
	}

	public void setHasViewGermplasmDetailsPermission(final boolean hasViewGermplasmDetailsPermission) {
		this.hasViewGermplasmDetailsPermission = hasViewGermplasmDetailsPermission;
	}

}

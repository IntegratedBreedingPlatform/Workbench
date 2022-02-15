
package org.generationcp.breeding.manager.crossingmanager;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.Action;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.themes.BaseTheme;
import liquibase.util.StringUtils;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.crossingmanager.util.CrossingManagerUtil;
import org.generationcp.breeding.manager.customcomponent.ActionButton;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.crossingmanager.listeners.GidLinkClickListener;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Configurable
public class SelectParentsListDataComponent extends VerticalLayout
	implements InitializingBean, InternationalizableComponent, BreedingManagerLayout {

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private StudyEntryService studyEntryService;


	private final class ListDataTableActionHandler implements Action.Handler {

		private static final long serialVersionUID = -2173636726748988046L;

		@Override
		public void handleAction(Action action, Object sender, Object target) {
			if (action.equals(SelectParentsListDataComponent.ACTION_ADD_TO_FEMALE_LIST)) {
				SelectParentsListDataComponent.this.makeCrossesParentsComponent.dropToFemaleOrMaleTable(
					SelectParentsListDataComponent.this.listDataTable,
					SelectParentsListDataComponent.this.makeCrossesParentsComponent.getFemaleTable(), null);
				SelectParentsListDataComponent.this.makeCrossesParentsComponent
					.assignEntryNumber(SelectParentsListDataComponent.this.makeCrossesParentsComponent.getFemaleTable());
				SelectParentsListDataComponent.this.makeCrossesParentsComponent.getFemaleParentTabSheet().setSelectedTab(0);
			} else if (action.equals(SelectParentsListDataComponent.ACTION_ADD_TO_MALE_LIST)) {
				SelectParentsListDataComponent.this.makeCrossesParentsComponent.dropToFemaleOrMaleTable(
					SelectParentsListDataComponent.this.listDataTable,
					SelectParentsListDataComponent.this.makeCrossesParentsComponent.getMaleTable(), null);
				SelectParentsListDataComponent.this.makeCrossesParentsComponent
					.assignEntryNumber(SelectParentsListDataComponent.this.makeCrossesParentsComponent.getMaleTable());
				SelectParentsListDataComponent.this.makeCrossesParentsComponent.getMaleParentTabSheet().setSelectedTab(1);
			}
		}

		@Override
		public Action[] getActions(Object target, Object sender) {
			return SelectParentsListDataComponent.LIST_DATA_TABLE_ACTIONS;
		}
	}


	private final class ActionMenuClickListener implements ContextMenu.ClickListener {

		private static final long serialVersionUID = -2343109406180457070L;

		@Override
		public void contextItemClick(final ClickEvent event) {
			final TransactionTemplate transactionTemplate = new TransactionTemplate(SelectParentsListDataComponent.this.transactionManager);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// Get reference to clicked item
					ContextMenuItem clickedItem = event.getClickedItem();
					if (clickedItem.getName().equals(SelectParentsListDataComponent.this.messageSource.getMessage(Message.SELECT_ALL))) {
						SelectParentsListDataComponent.this.listDataTable
							.setValue(SelectParentsListDataComponent.this.listDataTable.getItemIds());
					} else if (clickedItem.getName()
						.equals(SelectParentsListDataComponent.this.messageSource.getMessage(Message.ADD_TO_FEMALE_LIST))) {
						Collection<?> selectedIdsToAdd = (Collection<?>) SelectParentsListDataComponent.this.listDataTable.getValue();
						if (!selectedIdsToAdd.isEmpty()) {
							SelectParentsListDataComponent.this.makeCrossesParentsComponent.dropToFemaleOrMaleTable(
								SelectParentsListDataComponent.this.listDataTable,
								SelectParentsListDataComponent.this.makeCrossesParentsComponent.getFemaleTable(), null);
							SelectParentsListDataComponent.this.makeCrossesParentsComponent
								.assignEntryNumber(SelectParentsListDataComponent.this.makeCrossesParentsComponent.getFemaleTable());
							SelectParentsListDataComponent.this.makeCrossesParentsComponent.getFemaleParentTabSheet().setSelectedTab(0);
						} else {
							MessageNotifier.showWarning(SelectParentsListDataComponent.this.getWindow(),
								SelectParentsListDataComponent.this.messageSource.getMessage(Message.WARNING),
								SelectParentsListDataComponent.this.messageSource
									.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED));
						}
					} else if (clickedItem.getName()
						.equals(SelectParentsListDataComponent.this.messageSource.getMessage(Message.ADD_TO_MALE_LIST))) {
						Collection<?> selectedIdsToAdd = (Collection<?>) SelectParentsListDataComponent.this.listDataTable.getValue();
						if (!selectedIdsToAdd.isEmpty()) {
							SelectParentsListDataComponent.this.makeCrossesParentsComponent.dropToFemaleOrMaleTable(
								SelectParentsListDataComponent.this.listDataTable,
								SelectParentsListDataComponent.this.makeCrossesParentsComponent.getMaleTable(), null);
							SelectParentsListDataComponent.this.makeCrossesParentsComponent
								.assignEntryNumber(SelectParentsListDataComponent.this.makeCrossesParentsComponent.getMaleTable());
							SelectParentsListDataComponent.this.makeCrossesParentsComponent.getMaleParentTabSheet().setSelectedTab(1);
						} else {
							MessageNotifier.showWarning(SelectParentsListDataComponent.this.getWindow(),
								SelectParentsListDataComponent.this.messageSource.getMessage(Message.WARNING),
								SelectParentsListDataComponent.this.messageSource
									.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED));
						}
					} else if (clickedItem.getName()
						.equals(SelectParentsListDataComponent.this.messageSource.getMessage(Message.SELECT_EVEN_ENTRIES))) {
						SelectParentsListDataComponent.this.listDataTable
							.setValue(CrossingManagerUtil.getEvenEntries(SelectParentsListDataComponent.this.listDataTable));
					} else if (clickedItem.getName()
						.equals(SelectParentsListDataComponent.this.messageSource.getMessage(Message.SELECT_ODD_ENTRIES))) {
						SelectParentsListDataComponent.this.listDataTable
							.setValue(CrossingManagerUtil.getOddEntries(SelectParentsListDataComponent.this.listDataTable));
					}
				}
			});
		}
	}


	private static final Logger LOG = LoggerFactory.getLogger(SelectParentsListDataComponent.class);
	private static final long serialVersionUID = 7907737258051595316L;
	private static final String CHECKBOX_COLUMN_ID = "Checkbox Column ID";

	public static final String LIST_DATA_TABLE_ID = "SelectParentsListDataComponent List Data Table ID";

	private static final Action ACTION_ADD_TO_FEMALE_LIST = new Action("Add to Female List");
	private static final Action ACTION_ADD_TO_MALE_LIST = new Action("Add to Male List");
	private static final Action[] LIST_DATA_TABLE_ACTIONS =
		new Action[] {SelectParentsListDataComponent.ACTION_ADD_TO_FEMALE_LIST, SelectParentsListDataComponent.ACTION_ADD_TO_MALE_LIST};

	private Integer studyId;
	private final Integer germplasmListId;
	private GermplasmList germplasmList;
	private Long count;
	private Label listEntriesLabel;
	private Label totalListEntriesLabel;
	private Label totalSelectedListEntriesLabel;

	private Table listDataTable;
	private final String listName;

	private Button actionButton;
	private ContextMenu actionMenu;

	public static final String ACTIONS_BUTTON_ID = "Actions";

	private TableWithSelectAllLayout tableWithSelectAllLayout;

	// Layout variables
	private HorizontalLayout headerLayout;
	private HorizontalLayout subHeaderLayout;

	private MakeCrossesParentsComponent makeCrossesParentsComponent;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	public SelectParentsListDataComponent(Integer studyId, Integer germplasmListId, String listName,
		MakeCrossesParentsComponent makeCrossesParentsComponent) {
		super();
		this.studyId = studyId;
		this.germplasmListId = germplasmListId;
		this.listName = listName;
		this.makeCrossesParentsComponent = makeCrossesParentsComponent;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();

		this.initializeListView();
	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	@Override
	public void instantiateComponents() {
		this.retrieveListDetails();

		this.listEntriesLabel = new Label(this.messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
		this.listEntriesLabel.setDebugId("listEntriesLabel");
		this.listEntriesLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		this.listEntriesLabel.setWidth("160px");

		this.totalListEntriesLabel = new Label("", Label.CONTENT_XHTML);
		this.totalListEntriesLabel.setDebugId("totalListEntriesLabel");
		this.totalListEntriesLabel.setWidth("120px");
		this.updateNoOfEntries(this.count);

		this.totalSelectedListEntriesLabel = new Label("", Label.CONTENT_XHTML);
		this.totalSelectedListEntriesLabel.setDebugId("totalSelectedListEntriesLabel");
		this.totalSelectedListEntriesLabel.setWidth("95px");
		this.updateNoOfSelectedEntries(0);

		this.actionButton = new ActionButton();
		this.actionButton.setDebugId("actionButton");
		this.actionButton.setData(SelectParentsListDataComponent.ACTIONS_BUTTON_ID);

		this.actionMenu = new ContextMenu();
		this.actionMenu.setDebugId("actionMenu");
		this.actionMenu.setWidth("250px");
		this.actionMenu.addItem(this.messageSource.getMessage(Message.ADD_TO_MALE_LIST));
		this.actionMenu.addItem(this.messageSource.getMessage(Message.ADD_TO_FEMALE_LIST));
		this.actionMenu.addItem(this.messageSource.getMessage(Message.SELECT_ALL));
		this.actionMenu.addItem(this.messageSource.getMessage(Message.SELECT_EVEN_ENTRIES));
		this.actionMenu.addItem(this.messageSource.getMessage(Message.SELECT_ODD_ENTRIES));

		this.initializeListDataTable();
	}

	void initializeListDataTable() {
		this.tableWithSelectAllLayout =
			new TableWithSelectAllLayout(this.count.intValue(), 5, SelectParentsListDataComponent.CHECKBOX_COLUMN_ID);
		this.tableWithSelectAllLayout.setWidth("100%");

		this.listDataTable = this.tableWithSelectAllLayout.getTable();
		this.initializeListDataTable(this.listDataTable);
	}

	void initializeListDataTable(Table listDataTable) {
		if (listDataTable != null) {
			listDataTable.setWidth("100%");
			listDataTable.setData(SelectParentsListDataComponent.LIST_DATA_TABLE_ID);
			listDataTable.setSelectable(true);
			listDataTable.setMultiSelect(true);
			listDataTable.setColumnCollapsingAllowed(true);
			listDataTable.setColumnReorderingAllowed(true);
			listDataTable.setImmediate(true);
			listDataTable.setDragMode(TableDragMode.MULTIROW);

			listDataTable.addContainerProperty(SelectParentsListDataComponent.CHECKBOX_COLUMN_ID, CheckBox.class, null);
			listDataTable.addContainerProperty(ColumnLabels.ENTRY_ID.getName(), Integer.class, null);
			listDataTable.addContainerProperty(ColumnLabels.DESIGNATION.getName(), Button.class, null);
			listDataTable.addContainerProperty(ColumnLabels.PARENTAGE.getName(), String.class, null);
			listDataTable.addContainerProperty(ColumnLabels.ENTRY_CODE.getName(), String.class, null);
			listDataTable.addContainerProperty(ColumnLabels.GID.getName(), Button.class, null);
			listDataTable.addContainerProperty(ColumnLabels.GROUP_ID.getName(), String.class, null);
			listDataTable.addContainerProperty(ColumnLabels.SEED_SOURCE.getName(), String.class, null);

			listDataTable
				.setColumnHeader(SelectParentsListDataComponent.CHECKBOX_COLUMN_ID, this.messageSource.getMessage(Message.CHECK_ICON));
			listDataTable.setColumnHeader(ColumnLabels.ENTRY_ID.getName(), this.messageSource.getMessage(Message.HASHTAG));
			listDataTable.setColumnHeader(ColumnLabels.DESIGNATION.getName(), this.getTermNameFromOntology(ColumnLabels.DESIGNATION));
			listDataTable.setColumnHeader(ColumnLabels.PARENTAGE.getName(), this.getTermNameFromOntology(ColumnLabels.PARENTAGE));
			listDataTable.setColumnHeader(ColumnLabels.ENTRY_CODE.getName(), this.getTermNameFromOntology(ColumnLabels.ENTRY_CODE));
			listDataTable.setColumnHeader(ColumnLabels.GID.getName(), this.getTermNameFromOntology(ColumnLabels.GID));
			listDataTable.setColumnHeader(ColumnLabels.GROUP_ID.getName(), this.getTermNameFromOntology(ColumnLabels.GROUP_ID));
			listDataTable.setColumnHeader(ColumnLabels.SEED_SOURCE.getName(), this.getTermNameFromOntology(ColumnLabels.SEED_SOURCE));

			listDataTable.setColumnWidth(SelectParentsListDataComponent.CHECKBOX_COLUMN_ID, 25);
			listDataTable.setColumnWidth(ColumnLabels.ENTRY_ID.getName(), 25);
			listDataTable.setColumnWidth(ColumnLabels.DESIGNATION.getName(), 150);
			listDataTable.setColumnWidth(ColumnLabels.PARENTAGE.getName(), 150);
			listDataTable.setColumnWidth(ColumnLabels.ENTRY_CODE.getName(), 110);
			listDataTable.setColumnWidth(ColumnLabels.GID.getName(), 70);
			listDataTable.setColumnWidth(ColumnLabels.GROUP_ID.getName(), 70);
			listDataTable.setColumnWidth(ColumnLabels.SEED_SOURCE.getName(), 130);

			listDataTable.setVisibleColumns(new String[] {
				SelectParentsListDataComponent.CHECKBOX_COLUMN_ID, ColumnLabels.ENTRY_ID.getName(), ColumnLabels.DESIGNATION.getName(),
				ColumnLabels.PARENTAGE.getName(), ColumnLabels.ENTRY_CODE.getName(), ColumnLabels.GID.getName(),
				ColumnLabels.GROUP_ID.getName(), ColumnLabels.SEED_SOURCE.getName()});
		}
	}

	private void retrieveListDetails() {
		try {

			if (this.studyId != null) {
				this.count = this.studyEntryService.countStudyEntries(this.studyId);
			} else {
				this.germplasmList = this.germplasmListManager.getGermplasmListById(this.germplasmListId);
				this.count = this.germplasmListManager.countGermplasmListDataByListId(this.germplasmListId);
			}

		} catch (MiddlewareQueryException e) {
			SelectParentsListDataComponent.LOG.error("Error getting list details" + e.getMessage(), e);
		}
	}

	@Override
	public void initializeValues() {
		try {

			// If Study is not empty, that means the germplasm list must be retrieved from Stock table.
			if (this.studyId != null) {
				final List<StudyEntryDto> studyEntryDtoList = this.studyEntryService.getStudyEntries(studyId);
				for (final StudyEntryDto entry : studyEntryDtoList) {
					//TODO: should get entry code from properties?
					final String entryCode = null;

					this.addGermplasmItem(entry.getGid(), entry.getDesignation(), entry.getEntryNumber(), entry.getStudyEntryPropertyValue(TermId.CROSS.getId()),
						entryCode, entry.getStudyEntryPropertyValue(TermId.SEED_SOURCE.getId()), entry.getStudyEntryPropertyValue(TermId.GROUPGID.getId()));
				}
			} else {
				final List<GermplasmListData> listEntries =
					this.inventoryDataManager.getLotCountsForList(this.germplasmListId, 0, Integer.MAX_VALUE);
				for (final GermplasmListData entry : listEntries) {
					this.addGermplasmItem(entry.getGid(), entry.getDesignation(), entry.getId(),
							StringUtils.isEmpty(entry.getGroupName())? Optional.empty() : Optional.of(entry.getGroupName()), entry.getEntryCode(),
							StringUtils.isEmpty(entry.getSeedSource())? Optional.empty() : Optional.of(entry.getSeedSource()),
							entry.getGroupId() == null || entry.getGroupId() == 0 ? Optional.empty() : Optional.of(entry.getGroupId().toString()));
				}
			}

		} catch (MiddlewareQueryException ex) {
			SelectParentsListDataComponent.LOG.error("Error with getting list entries for list: " + this.germplasmListId, ex);
			MessageNotifier.showError(this.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
				"Error in getting list entries.");
		}
	}

	private void addGermplasmItem(final int gid, final String designation, final Integer entryNumber, final Optional<String> groupName,
		final String entryCode, final Optional<String> seedSource, final Optional<String> groupId) {

		final String gidString = String.format("%s", gid);
		final Button gidButton = new Button(gidString, new GidLinkClickListener(gidString, true));
		gidButton.setDebugId("gidButton");
		gidButton.setStyleName(BaseTheme.BUTTON_LINK);
		gidButton.setDescription("Click to view Germplasm information");

		final Button desigButton = new Button(designation, new GidLinkClickListener(gidString, true));
		desigButton.setDebugId("desigButton");
		desigButton.setStyleName(BaseTheme.BUTTON_LINK);
		desigButton.setDescription("Click to view Germplasm information");

		final CheckBox itemCheckBox = new CheckBox();
		itemCheckBox.setDebugId("itemCheckBox");
		itemCheckBox.setData(entryNumber);
		itemCheckBox.setImmediate(true);
		itemCheckBox.addListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final com.vaadin.ui.Button.ClickEvent event) {
				final CheckBox itemCheckBox = (CheckBox) event.getButton();
				if (((Boolean) itemCheckBox.getValue()).equals(true)) {
					SelectParentsListDataComponent.this.getListDataTable().select(itemCheckBox.getData());
				} else {
					SelectParentsListDataComponent.this.getListDataTable().unselect(itemCheckBox.getData());
				}
			}
		});

		Item newItem = this.getListDataTable().getContainerDataSource().addItem(entryNumber);
		newItem.getItemProperty(SelectParentsListDataComponent.CHECKBOX_COLUMN_ID).setValue(itemCheckBox);
		newItem.getItemProperty(ColumnLabels.ENTRY_ID.getName()).setValue(entryNumber);
		newItem.getItemProperty(ColumnLabels.DESIGNATION.getName()).setValue(desigButton);
		newItem.getItemProperty(ColumnLabels.PARENTAGE.getName()).setValue(groupName.orElse(""));
		newItem.getItemProperty(ColumnLabels.ENTRY_CODE.getName()).setValue(entryCode);
		newItem.getItemProperty(ColumnLabels.GID.getName()).setValue(gidButton);
		newItem.getItemProperty(ColumnLabels.SEED_SOURCE.getName()).setValue(seedSource.orElse(""));
		final String groupIdDisplayValue = groupId.orElse("-");
		newItem.getItemProperty(ColumnLabels.GROUP_ID.getName()).setValue(groupIdDisplayValue);

	}

	@Override
	public void addListeners() {

		this.actionButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				SelectParentsListDataComponent.this.actionMenu.show(event.getClientX(), event.getClientY());
			}
		});

		this.actionMenu.addListener(new ActionMenuClickListener());

		this.getListDataTable().addActionHandler(new ListDataTableActionHandler());

		this.getListDataTable().addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				SelectParentsListDataComponent.this.updateNoOfSelectedEntries();
			}
		});

	}

	@Override
	public void layoutComponents() {
		this.setMargin(true);
		this.setSpacing(true);

		this.addComponent(this.actionMenu);

		this.headerLayout = new HorizontalLayout();
		this.headerLayout.setDebugId("headerLayout");
		this.headerLayout.setWidth("100%");
		HeaderLabelLayout headingLayout = new HeaderLabelLayout(AppConstants.Icons.ICON_LIST_TYPES, this.listEntriesLabel);
		headingLayout.setDebugId("headingLayout");
		this.headerLayout.addComponent(headingLayout);
		this.headerLayout.setComponentAlignment(headingLayout, Alignment.MIDDLE_LEFT);

		HorizontalLayout leftSubHeaderLayout = new HorizontalLayout();
		leftSubHeaderLayout.setDebugId("leftSubHeaderLayout");
		leftSubHeaderLayout.setSpacing(true);
		leftSubHeaderLayout.addComponent(this.totalListEntriesLabel);
		leftSubHeaderLayout.addComponent(this.totalSelectedListEntriesLabel);
		leftSubHeaderLayout.setComponentAlignment(this.totalListEntriesLabel, Alignment.MIDDLE_LEFT);
		leftSubHeaderLayout.setComponentAlignment(this.totalSelectedListEntriesLabel, Alignment.MIDDLE_LEFT);

		this.subHeaderLayout = new HorizontalLayout();
		this.subHeaderLayout.setDebugId("subHeaderLayout");
		this.subHeaderLayout.setWidth("100%");
		this.subHeaderLayout.addComponent(leftSubHeaderLayout);
		this.subHeaderLayout.addComponent(this.actionButton);
		this.subHeaderLayout.setComponentAlignment(leftSubHeaderLayout, Alignment.MIDDLE_LEFT);
		this.subHeaderLayout.setComponentAlignment(this.actionButton, Alignment.MIDDLE_RIGHT);

		this.addComponent(this.headerLayout);
		this.addComponent(this.subHeaderLayout);
		this.addComponent(this.tableWithSelectAllLayout);

	}

	void updateNoOfEntries(long count) {
		if (count == 0) {
			this.totalListEntriesLabel.setValue(this.messageSource.getMessage(Message.NO_LISTDATA_RETRIEVED_LABEL));
		} else {
			this.totalListEntriesLabel
				.setValue(this.messageSource.getMessage(Message.TOTAL_LIST_ENTRIES) + ": " + "  <b>" + count + "</b>");
		}
	}

	void updateNoOfEntries() {
		this.updateNoOfEntries(this.getListDataTable().getItemIds().size());
	}

	private void updateNoOfSelectedEntries(int count) {
		this.totalSelectedListEntriesLabel
			.setValue("<i>" + this.messageSource.getMessage(Message.SELECTED) + ": " + "  <b>" + count + "</b></i>");
	}

	void updateNoOfSelectedEntries() {
		int entryCount = 0;
		Collection<?> selectedItems = (Collection<?>) this.getListDataTable().getValue();
		entryCount = selectedItems.size();

		this.updateNoOfSelectedEntries(entryCount);
	}

	private void initializeListView() {
		this.tableWithSelectAllLayout.setVisible(true);

		this.subHeaderLayout.addComponent(this.actionButton);
		this.subHeaderLayout.setComponentAlignment(this.actionButton, Alignment.MIDDLE_RIGHT);

		this.listEntriesLabel.setValue(this.messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
		this.updateNoOfEntries();
		this.updateNoOfSelectedEntries();

		this.addComponent(this.tableWithSelectAllLayout);

		this.requestRepaint();
	}

	public Table getListDataTable() {
		return this.tableWithSelectAllLayout.getTable();
	}

	public String getListName() {
		return this.listName;
	}

	public GermplasmList getGermplasmList() {
		return this.germplasmList;
	}

	public Integer getGermplasmListId() {
		return this.germplasmListId;
	}

	protected String getTermNameFromOntology(ColumnLabels columnLabels) {
		return columnLabels.getTermNameFromOntology(this.ontologyDataManager);
	}

	protected void setListDataTableWithSelectAll(TableWithSelectAllLayout tableWithSelectAllLayout) {
		this.tableWithSelectAllLayout = tableWithSelectAllLayout;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Label getTotalListEntriesLabel() {
		return this.totalListEntriesLabel;
	}

	public Label getTotalSelectedListEntriesLabel() {
		return this.totalSelectedListEntriesLabel;
	}

	public Label getListEntriesLabel() {
		return this.listEntriesLabel;
	}

}

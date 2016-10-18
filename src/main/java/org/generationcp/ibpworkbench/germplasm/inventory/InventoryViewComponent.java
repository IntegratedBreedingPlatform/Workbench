package org.generationcp.ibpworkbench.germplasm.inventory;

import com.vaadin.data.Item;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import org.generationcp.ibpworkbench.Message;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.domain.inventory.LotDetails;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

@Configurable
public class InventoryViewComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(InventoryViewComponent.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	private final Integer listId;
	private Integer recordId; // lrecId
	private Integer gid;

	private Table lotEntriesTable;

	private Label noEntriesLabel;

	public static final String LOT_LOCATION = "lotLocation";
	public static final String ACTUAL_BALANCE = "actualBalance";
	public static final String LOT_UNITS = "lotUnits";
	public static final String TOTAL = "actualBalance";
	public static final String AVAILABLE_BALANCE = "availableBalance";
	public static final String WITHDRAWAL = "withdrawal";
	public static final String STATUS = "status";

	public static final String COMMENTS = "comments";
	public static final String LOT_ID = "lotId";
	public static final String SEED_SOURCE = "seedSource";
	public static final String STOCKID = "stockId";

	private boolean isThereNoInventoryInfo;

	public InventoryViewComponent(Integer listId) {
		this.listId = listId;
	}

	public InventoryViewComponent(Integer listId, Integer recordId, Integer gid) {
		this.listId = listId;
		this.recordId = recordId;
		this.gid = gid;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.layoutComponents();
	}

	@Override
	public void updateLabels() {
		// inherited method - do nothing
	}

	public void instantiateComponents() {

		this.lotEntriesTable = new Table();
		this.lotEntriesTable.setWidth("90%");
		this.lotEntriesTable.setHeight("160px");

		this.noEntriesLabel =
				new Label(this.messageSource.getMessage(Message.THERE_IS_NO_INVENTORY_INFORMATION_AVAILABLE_FOR_THIS_GERMPLASM) + ".");

		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.LOT_LOCATION, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.ACTUAL_BALANCE, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.AVAILABLE_BALANCE, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.WITHDRAWAL, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.STATUS, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.COMMENTS, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.STOCKID, Label.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.LOT_ID, Integer.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.SEED_SOURCE, String.class, null);

		// Get the name of the headers from the ontology
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.LOT_LOCATION, this.getTermNameFromOntology(ColumnLabels.LOT_LOCATION));
		this.lotEntriesTable
				.setColumnHeader(InventoryViewComponent.ACTUAL_BALANCE, this.getTermNameFromOntology(ColumnLabels.ACTUAL_BALANCE));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.AVAILABLE_BALANCE, this.getTermNameFromOntology(ColumnLabels.TOTAL));
		this.lotEntriesTable
				.setColumnHeader(InventoryViewComponent.WITHDRAWAL, this.getTermNameFromOntology(ColumnLabels.SEED_RESERVATION));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.STATUS, this.getTermNameFromOntology(ColumnLabels.STATUS));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.COMMENTS, this.getTermNameFromOntology(ColumnLabels.COMMENT));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.STOCKID, this.getTermNameFromOntology(ColumnLabels.STOCKID));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.LOT_ID, this.getTermNameFromOntology(ColumnLabels.LOT_ID));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.SEED_SOURCE, this.getTermNameFromOntology(ColumnLabels.SEED_SOURCE));
	}

	public void initializeValues() {
		List<? extends LotDetails> lotDetailEntries = this.listId != null && this.recordId != null ?
				this.inventoryDataManager.getLotDetailsForListEntry(this.listId, this.recordId, this.gid) :
				this.inventoryDataManager.getLotDetailsForGermplasm(this.gid);

		for (LotDetails lotEntry : lotDetailEntries) {
			Item newItem = this.lotEntriesTable.addItem(lotEntry.getLotId());

			String lotLocation = "";
			if (lotEntry.getLocationOfLot() != null) {
				if (lotEntry.getLocationOfLot().getLname() != null) {
					lotLocation = lotEntry.getLocationOfLot().getLname();
				}
			}
			newItem.getItemProperty(InventoryViewComponent.LOT_LOCATION).setValue(lotLocation);

			String actualBalance = "";
			if (lotEntry.getActualLotBalance() > 0) {
				actualBalance = lotEntry.getActualLotBalance() + lotEntry.getLotScaleNameAbbr();
			}
			newItem.getItemProperty(InventoryViewComponent.ACTUAL_BALANCE).setValue(actualBalance);

			String availableBalance = "";
			if (lotEntry.getAvailableLotBalance() != null && lotEntry.getAvailableLotBalance() > 0) {
				availableBalance = lotEntry.getAvailableLotBalance() + lotEntry.getLotScaleNameAbbr();
			}
			newItem.getItemProperty(InventoryViewComponent.AVAILABLE_BALANCE).setValue(availableBalance);

			String withdrawalBalance = "";
			if (lotEntry.getWithdrawalBalance() != null) {
				withdrawalBalance = lotEntry.getWithdrawalBalance() + lotEntry.getLotScaleNameAbbr();
			}
			newItem.getItemProperty(InventoryViewComponent.WITHDRAWAL).setValue(withdrawalBalance);

			String withdrawalStatus = "";
			if (lotEntry.getWithdrawalStatus() != null) {
				withdrawalStatus = lotEntry.getWithdrawalStatus();
			}
			newItem.getItemProperty(InventoryViewComponent.STATUS).setValue(withdrawalStatus);

			newItem.getItemProperty(InventoryViewComponent.COMMENTS).setValue(lotEntry.getCommentOfLot());
			newItem.getItemProperty(InventoryViewComponent.STOCKID).setValue(lotEntry.getStockIds());
			newItem.getItemProperty(InventoryViewComponent.LOT_ID).setValue(lotEntry.getLotId());

			// TODO - BMS-3487 seed source
			String seedSource = "";
			newItem.getItemProperty(InventoryViewComponent.SEED_SOURCE).setValue(seedSource);
		}
	}

	public void layoutComponents() {

		this.setSpacing(true);
		if (!this.lotEntriesTable.getItemIds().isEmpty()) {
			this.addComponent(this.lotEntriesTable);
		} else {
			this.addComponent(this.noEntriesLabel);
		}

	}

	public boolean isThereNoInventoryInfo() {
		return this.isThereNoInventoryInfo;
	}

	@Override
	public void attach() {
		super.attach();
		this.updateLabels();
	}

	public Table getTable() {
		return this.lotEntriesTable;
	}

	protected String getTermNameFromOntology(ColumnLabels columnLabels) {
		return columnLabels.getTermNameFromOntology(this.ontologyDataManager);
	}

}

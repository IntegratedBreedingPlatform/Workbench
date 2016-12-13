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
	public static final String AVAILABLE_BALANCE = "availableBalance";
	public static final String LOT_STATUS = "lotStatus";

	public static final String COMMENTS = "comments";
	public static final String LOT_ID = "lotId";
	public static final String STOCKID = "stockId";

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
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.LOT_STATUS, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.COMMENTS, String.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.STOCKID, Label.class, null);
		this.lotEntriesTable.addContainerProperty(InventoryViewComponent.LOT_ID, Integer.class, null);

		// Get the name of the headers from the ontology
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.LOT_LOCATION, this.getTermNameFromOntology(ColumnLabels.LOT_LOCATION));
		this.lotEntriesTable
				.setColumnHeader(InventoryViewComponent.ACTUAL_BALANCE, this.getTermNameFromOntology(ColumnLabels.ACTUAL_BALANCE));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.AVAILABLE_BALANCE, this.getTermNameFromOntology(ColumnLabels.TOTAL));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.LOT_STATUS, this.getTermNameFromOntology(ColumnLabels.LOT_STATUS));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.COMMENTS, this.getTermNameFromOntology(ColumnLabels.COMMENT));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.STOCKID, this.getTermNameFromOntology(ColumnLabels.STOCKID));
		this.lotEntriesTable.setColumnHeader(InventoryViewComponent.LOT_ID, this.getTermNameFromOntology(ColumnLabels.LOT_ID));

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
			if (lotEntry.getAvailableLotBalance() != null) {
				availableBalance = lotEntry.getAvailableLotBalance() + lotEntry.getLotScaleNameAbbr();
			}
			newItem.getItemProperty(InventoryViewComponent.AVAILABLE_BALANCE).setValue(availableBalance);

			newItem.getItemProperty(InventoryViewComponent.LOT_STATUS).setValue(lotEntry.getLotStatus());

			newItem.getItemProperty(InventoryViewComponent.COMMENTS).setValue(lotEntry.getCommentOfLot());
			newItem.getItemProperty(InventoryViewComponent.STOCKID).setValue(lotEntry.getStockIds());
			newItem.getItemProperty(InventoryViewComponent.LOT_ID).setValue(lotEntry.getLotId());

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

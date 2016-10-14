package org.generationcp.ibpworkbench.germplasm.inventory;

import java.util.Collection;

import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Table;

@RunWith(MockitoJUnitRunner.class)
public class InventoryViewComponentTest {

	private static final String LOCATION_HEADER_NAME = "LOCATION";

	private static final String ACTUAL_BALANCE_HEADER_NAME = "ACTUAL BALANCE";

	private static final String AVAILABLE_BALANCE_HEADER_NAME = "AVAILABLE BALANCE";

	private static final String WITHDRAWAL_HEADER_NAME = "WITHDRAWAL";

	private static final String STATUS_HEADER_NAME = "STATUS";

	private static final String COMMENT_HEADER_NAME = "COMMENT";

	private static final String STOCKID_HEADER_NAME = "STOCKID";

	private static final String LOTID_HEADER_NAME = "LOTID";

	private static final String SEED_SOURCE_HEADER_NAME = "TOTAL";

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private InventoryDataManager inventoryDataManager;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@InjectMocks
	private InventoryViewComponent inventoryView = new InventoryViewComponent(null, null, 1);

	@Before
	public void initializeMocks() {

		Mockito.doReturn(new Term(TermId.LOT_LOCATION_INVENTORY.getId(), LOCATION_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.LOT_LOCATION.getTermId().getId());
		Mockito.doReturn(new Term(TermId.ACTUAL_BALANCE.getId(), ACTUAL_BALANCE_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.ACTUAL_BALANCE.getTermId().getId());
		Mockito.doReturn(new Term(TermId.TOTAL_INVENTORY.getId(), AVAILABLE_BALANCE_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.TOTAL.getTermId().getId());
		Mockito.doReturn(new Term(TermId.SEED_RESERVATION.getId(), WITHDRAWAL_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.SEED_RESERVATION.getTermId().getId());
		Mockito.doReturn(new Term(TermId.STATUS.getId(), STATUS_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.STATUS.getTermId().getId());
		Mockito.doReturn(new Term(TermId.COMMENT_INVENTORY.getId(), COMMENT_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.COMMENT.getTermId().getId());
		Mockito.doReturn(new Term(TermId.STOCKID.getId(), STOCKID_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.STOCKID.getTermId().getId());
		Mockito.doReturn(new Term(TermId.LOT_ID_INVENTORY.getId(), LOTID_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.LOT_ID.getTermId().getId());
		Mockito.doReturn(new Term(TermId.SEED_SOURCE.getId(), SEED_SOURCE_HEADER_NAME, "")).when(this.ontologyDataManager)
				.getTermById(ColumnLabels.SEED_SOURCE.getTermId().getId());

	}

	@Test
	public void testInventoryViewColumnsAndHeaderNames() {

		this.inventoryView.instantiateComponents();

		// check expected list of table columns
		Table table = this.inventoryView.getTable();
		Assert.assertNotNull(table);
		Collection<?> columnIds = table.getContainerPropertyIds();

		Assert.assertTrue(columnIds.size() == 9);
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.LOT_LOCATION));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.ACTUAL_BALANCE));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.AVAILABLE_BALANCE));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.WITHDRAWAL));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.STATUS));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.COMMENTS));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.STOCKID));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.LOT_ID));
		Assert.assertTrue(columnIds.contains(InventoryViewComponent.SEED_SOURCE));

		Assert.assertEquals(LOTID_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.LOT_ID));
		Assert.assertEquals(ACTUAL_BALANCE_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.ACTUAL_BALANCE));
		Assert.assertEquals(AVAILABLE_BALANCE_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.AVAILABLE_BALANCE));
		Assert.assertEquals(WITHDRAWAL_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.WITHDRAWAL));
		Assert.assertEquals(STATUS_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.STATUS));
		Assert.assertEquals(COMMENT_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.COMMENTS));
		Assert.assertEquals(STOCKID_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.STOCKID));
		Assert.assertEquals(LOTID_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.LOT_ID));
		Assert.assertEquals(SEED_SOURCE_HEADER_NAME, table.getColumnHeader(InventoryViewComponent.SEED_SOURCE));
	}

}

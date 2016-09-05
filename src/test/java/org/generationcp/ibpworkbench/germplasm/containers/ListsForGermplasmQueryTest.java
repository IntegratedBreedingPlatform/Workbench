
package org.generationcp.ibpworkbench.germplasm.containers;

import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.vaadin.data.Item;

import junit.framework.Assert;

public class ListsForGermplasmQueryTest {

	private ListsForGermplasmQuery listForGermplasmQuery;
	private GermplasmListTestDataInitializer germplasmListTestDataInitializer;

	@Before
	public void setUp() {
		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		final GermplasmListManager germplasmListManager = Mockito.mock(GermplasmListManager.class);
		Mockito.when(germplasmListManager.countGermplasmListByGIDandProgramUUID(Matchers.anyInt(), Matchers.anyString()))
				.thenReturn((long) 1);
		Mockito.when(germplasmListManager.getGermplasmListByGIDandProgramUUID(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt(),
				Matchers.anyString())).thenReturn(Arrays.asList(this.germplasmListTestDataInitializer.createGermplasmList(1)));
		this.listForGermplasmQuery = new ListsForGermplasmQuery(germplasmListManager, 1, "1");
	}

	@Test
	public void testSize() {
		Assert.assertEquals("The size should be 1", (long) 1, this.listForGermplasmQuery.size());
	}

	@Test
	public void testLoadItems() {
		final List<Item> items = this.listForGermplasmQuery.loadItems(1, 1);
		final Item item = items.get(0);
		Assert.assertEquals("The id should be 1", "1", item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID).toString());
		Assert.assertEquals("The id should be List 1 Description", "List 1 Description",
				item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DESCRIPTION).toString());
		Assert.assertEquals("The id should be List 1", "List 1",
				item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).toString());
		Assert.assertEquals("The date should be 20150101", "20150101",
				item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DATE).toString());
	}
}

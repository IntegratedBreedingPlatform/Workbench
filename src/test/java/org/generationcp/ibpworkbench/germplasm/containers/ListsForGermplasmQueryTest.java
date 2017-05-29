
package org.generationcp.ibpworkbench.germplasm.containers;

import com.vaadin.data.Item;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ListsForGermplasmQueryTest {

	private ListsForGermplasmQuery listForGermplasmQuery;
	private GermplasmListTestDataInitializer germplasmListTestDataInitializer;
	private GermplasmList germplasmList;

	@Before
	public void setUp() {
		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		final GermplasmListManager germplasmListManager = Mockito.mock(GermplasmListManager.class);
		Mockito.when(germplasmListManager.countGermplasmListByGIDandProgramUUID(Matchers.anyInt(), Matchers.anyString()))
				.thenReturn((long) 1);
		germplasmList = this.germplasmListTestDataInitializer.createGermplasmList(1);
		Mockito.when(germplasmListManager
			.getGermplasmListByGIDandProgramUUID(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt(), Matchers.anyString()))
			.thenReturn(Arrays.asList(germplasmList));
		this.listForGermplasmQuery = new ListsForGermplasmQuery(germplasmListManager, 1, "1");
	}

	@Test
	public void testSize() {
		assertThat(this.listForGermplasmQuery.size(),equalTo(1));
	}

	@Test
	public void testLoadItems() {
		final List<Item> items = this.listForGermplasmQuery.loadItems(1, 1);
		final Item item = items.get(0);
		final String url = "/BreedingManager/main/list-manager?restartApplication&lists=" + germplasmList.getId();

		assertThat(germplasmList.getId().toString(),equalTo(item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID).toString()));
		assertThat(germplasmList.getDescription(),equalTo(item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DESCRIPTION).toString()));
		assertThat(germplasmList.getName(),equalTo(((LinkButton) item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue()).getCaption()));
		assertThat(germplasmList.getDate().toString(),equalTo(item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DATE).toString()));
		assertThat(url, equalTo(((LinkButton) item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue()).getResource().getURL().toString()));
	}
}

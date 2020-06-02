
package org.generationcp.ibpworkbench.germplasm.containers;

import com.vaadin.data.Item;
import org.generationcp.breeding.manager.data.initializer.GermplasmListDataTestDataInitializer;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ListsForGermplasmQueryTest {

	private ListsForGermplasmQuery listForGermplasmQuery;
	private GermplasmListTestDataInitializer germplasmListTestDataInitializer;
	private GermplasmList germplasmList;
	private GermplasmListManager germplasmListManager;
	private final String programUUID1 = "ProgramID1";
	private final String programUUID2 = "ProgramID2";

	@Before
	public void setUp() {
		this.germplasmListTestDataInitializer = new GermplasmListTestDataInitializer();
		this.germplasmListManager = Mockito.mock(GermplasmListManager.class);
		Mockito.when(this.germplasmListManager.countGermplasmListByGID(Matchers.anyInt()))
				.thenReturn((long) 1);
		this.germplasmList = this.germplasmListTestDataInitializer.createGermplasmList(1);
		Mockito.when(this.germplasmListManager
			.getGermplasmListByGID(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
			.thenReturn(Arrays.asList(this.germplasmList));
		this.listForGermplasmQuery = new ListsForGermplasmQuery(this.germplasmListManager, 1, "1");
	}

	@Test
	public void testSize() {
		assertThat(this.listForGermplasmQuery.size(),equalTo(1));
	}

	@Test
	public void testLoadItems() {
		final List<Item> items = this.listForGermplasmQuery.loadItems(1, 1);
		final Item item = items.get(0);
		final String url = "/ibpworkbench/bm/list-manager?restartApplication&lists=" + this.germplasmList.getId();

		assertThat(this.germplasmList.getId().toString(),equalTo(item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID).toString()));
		assertThat(this.germplasmList.getDescription(),equalTo(item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DESCRIPTION).toString()));
		assertThat(this.germplasmList.getName(),equalTo(((LinkButton) item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue()).getCaption()));
		assertThat(this.germplasmList.getDate().toString(),equalTo(item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DATE).toString()));
		assertThat(url, equalTo(((LinkButton) item.getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue()).getResource().getURL().toString()));
	}

	@Test
	public void testLoadItemsDifferentProgram() {
		final GermplasmList germplasmList1 = this.createGermplasmListTestData( "List1", "", new Date().getTime(), "1", 1,1, this.programUUID1, 1, 1);
		final GermplasmList germplasmList2 = this.createGermplasmListTestData( "List1", "", new Date().getTime(), "1", 1,1, this.programUUID2, 2, 2);
		Mockito.when(this.germplasmListManager
			.getGermplasmListByGID(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
			.thenReturn(Arrays.asList(germplasmList1, germplasmList2));

		this.listForGermplasmQuery = new ListsForGermplasmQuery(this.germplasmListManager, 1, this.programUUID1);
		final List<Item> items = this.listForGermplasmQuery.loadItems(1, 2);
		final LinkButton linkButton1 = (LinkButton) items.get(0).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue();
		final LinkButton linkButton2 = (LinkButton) items.get(1).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue();

		assertThat(items.size(),equalTo(2));

		assertThat(items.get(0).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID).getValue(), equalTo("1"));
		assertTrue(linkButton1.isEnabled());

		assertThat(items.get(1).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID).getValue(), equalTo("2"));
		assertFalse(linkButton2.isEnabled());

	}

	@Test
	public void testLoadItemsNullProgram() {
		final GermplasmList germplasmList1 = this.createGermplasmListTestData( "List1", "", new Date().getTime(), "1", 1,1, this.programUUID1, 1, 1);
		final GermplasmList germplasmList2 = this.createGermplasmListTestData( "List1", "", new Date().getTime(), "1", 1,1, null, 2, 2);
		Mockito.when(this.germplasmListManager
			.getGermplasmListByGID(Matchers.anyInt(), Matchers.anyInt(), Matchers.anyInt()))
			.thenReturn(Arrays.asList(germplasmList1, germplasmList2));

		this.listForGermplasmQuery = new ListsForGermplasmQuery(this.germplasmListManager, 1, this.programUUID1);
		final List<Item> items = this.listForGermplasmQuery.loadItems(1, 2);
		final LinkButton linkButton1 = (LinkButton) items.get(0).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue();
		final LinkButton linkButton2 = (LinkButton) items.get(1).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME).getValue();

		assertThat(items.size(),equalTo(2));

		assertThat(items.get(0).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID).getValue(), equalTo("1"));
		assertTrue(linkButton1.isEnabled());

		assertThat(items.get(1).getItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID).getValue(), equalTo("2"));
		assertTrue(linkButton2.isEnabled());

	}

	public GermplasmList createGermplasmListTestData(final String name, final String description, final long date,
		final String type, final int userId, final int status, final String programUUID, final Integer projectId, final Integer id) throws
		MiddlewareQueryException {
		final GermplasmList list = new GermplasmList();
		list.setId(id);
		list.setName(name);
		list.setDescription(description);
		list.setDate(date);
		list.setType(type);
		list.setUserId(userId);
		list.setStatus(status);
		list.setProgramUUID(programUUID);
		list.setProjectId(projectId);
		return list;
	}
}

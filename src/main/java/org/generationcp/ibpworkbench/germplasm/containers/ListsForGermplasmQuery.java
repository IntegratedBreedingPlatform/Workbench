/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.germplasm.containers;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.themes.BaseTheme;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Query which is needed for using the LazyQueryContainer.
 *
 * Reference: https://vaadin.com/wiki/-/wiki/Main/Lazy%20Query%20Container/#section
 * -Lazy+Query+Container-HowToImplementCustomQueryAndQueryFactory
 *
 * @author Kevin Manansala
 *
 */
public class ListsForGermplasmQuery implements Query {

	private static final Logger LOG = LoggerFactory.getLogger(ListsForGermplasmQuery.class);

	public static final Object GERMPLASMLIST_ID = "id";
	public static final Object GERMPLASMLIST_NAME = "name";
	public static final Object GERMPLASMLIST_DATE = "date";
	public static final Object GERMPLASMLIST_DESCRIPTION = "description";

	private static final String MANAGER_GERMPLASM = "/BreedingManager/main/list-manager?restartApplication&lists=";
	private static final String PARENT_WINDOW = "_parent";

	private final GermplasmListManager dataManager;
	private final Integer gid;
	private int size;
	private final String programUUID;

	/**
	 * These parameters are passed by the QueryFactory which instantiates objects of this class.
	 * 
	 * @param dataManager
	 * @param gid
	 * @param programUUID
	 */
	public ListsForGermplasmQuery(final GermplasmListManager dataManager, final Integer gid, final String programUUID) {
		super();
		this.dataManager = dataManager;
		this.gid = gid;
		this.size = -1;
		this.programUUID = programUUID;
	}

	/**
	 * This method seems to be called for creating blank items on the Table
	 */
	@Override
	public Item constructItem() {
		final PropertysetItem item = new PropertysetItem();
		item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID, new ObjectProperty<String>(""));
		item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME, new ObjectProperty<LinkButton>(new LinkButton( new ExternalResource(MANAGER_GERMPLASM),"")));
		item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DATE, new ObjectProperty<String>(""));
		item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DESCRIPTION, new ObjectProperty<String>(""));
		return item;
	}

	@Override
	public boolean deleteAllItems() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Item> loadItems(final int start, final int numOfRows) {
		final List<Item> items = new ArrayList<Item>();

		final List<GermplasmList> germplasmLists = new ArrayList<GermplasmList>();
		try {
			germplasmLists.addAll(this.dataManager.getGermplasmListByGIDandProgramUUID(this.gid, start, numOfRows, this.programUUID));

			for (final GermplasmList list : germplasmLists) {
				final PropertysetItem item = new PropertysetItem();
				item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_ID, new ObjectProperty<String>(list.getId().toString()));

				final ExternalResource urlToOpenGermplasmList = new ExternalResource(MANAGER_GERMPLASM + list.getId());
				LinkButton linkListButton = new LinkButton(urlToOpenGermplasmList,list.getName(),PARENT_WINDOW);
				linkListButton.setDebugId("linkStudyButton");
				linkListButton.addStyleName(BaseTheme.BUTTON_LINK);

				item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_NAME, new ObjectProperty<LinkButton>(linkListButton));
				item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DATE, new ObjectProperty<String>(String.valueOf(list.getDate())));
				item.addItemProperty(ListsForGermplasmQuery.GERMPLASMLIST_DESCRIPTION, new ObjectProperty<String>(list.getDescription()));
				items.add(item);
			}

			return items;
		} catch (final MiddlewareQueryException ex) {
			ListsForGermplasmQuery.LOG.error("Error with getting lists for gid = " + this.gid + ": " + ex.getMessage(), ex);
			throw new MiddlewareQueryException("Error in displaying the table for germplasm with GID " + this.gid);
		}
	}

	@Override
	public void saveItems(final List<Item> arg0, final List<Item> arg1, final List<Item> arg2) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the total number of rows to be displayed on the Table
	 */
	@Override
	public int size() {
		try {
			if (this.size == -1) {
				this.size = ((Long) this.dataManager.countGermplasmListByGIDandProgramUUID(this.gid, this.programUUID)).intValue();
			}
			return this.size;
		} catch (final MiddlewareQueryException e) {
			ListsForGermplasmQuery.LOG.error("Error in countGermplasmListByGIDandProgramUUID in size() " + e.getMessage(), e);
			throw new MiddlewareQueryException("Error in displaying the table for germplasm with GID " + this.gid);
		}
	}
}

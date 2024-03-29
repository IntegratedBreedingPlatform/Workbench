/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 * @author Kevin L. Manansala
 *
 *         This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of
 *         Part F of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/

package org.generationcp.ibpworkbench.germplasm.containers;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.germplasm.GermplasmSearchResultModel;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.vaadin.addons.lazyquerycontainer.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Query which is needed for using the LazyQueryContainer.
 *
 * Reference: https://vaadin.com/wiki/-/wiki/Main/Lazy%20Query%20Container/#section
 * -Lazy+Query+Container-HowToImplementCustomQueryAndQueryFactory
 *
 * @author Joyce Avestro
 *
 */
public class GermplasmSearchQuery implements Query {

	public static final Object GID = ColumnLabels.GID.getName();
	public static final Object NAMES = ColumnLabels.DESIGNATION.getName();
	public static final Object METHOD = ColumnLabels.BREEDING_METHOD_NAME.getName();
	public static final Object LOCATION = ColumnLabels.GERMPLASM_LOCATION.getName();

	private final GermplasmDataManager germplasmDataManager;
	private final String searchChoice;
	private final String searchValue;
	private int size;
	public static final String SEARCH_OPTION_GID = "GID";
	public static final String SEARCH_OPTION_NAME = "Names";
	/**
	 * These parameters are passed by the QueryFactory which instantiates objects of this class.
	 * 
	 */
	public GermplasmSearchQuery(final GermplasmDataManager germplasmDataManager, final String searchChoice, final String searchValue) {
		super();
		this.germplasmDataManager = germplasmDataManager;
		this.searchChoice = searchChoice;
		this.searchValue = searchValue;
		this.size = -1;
	}

	/**
	 * This method seems to be called for creating blank items on the Table
	 */
	@Override
	public Item constructItem() {
		final PropertysetItem item = new PropertysetItem();
		item.addItemProperty(GermplasmSearchQuery.GID, new ObjectProperty<String>(""));
		item.addItemProperty(GermplasmSearchQuery.NAMES, new ObjectProperty<String>(""));
		item.addItemProperty(GermplasmSearchQuery.METHOD, new ObjectProperty<String>(""));
		item.addItemProperty(GermplasmSearchQuery.LOCATION, new ObjectProperty<String>(""));
		return item;
	}

	@Override
	public boolean deleteAllItems() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Retrieves the dataset by batches of rows. Used for lazy loading the dataset.
	 */
	@Override
	public List<Item> loadItems(final int start, final int numOfRows) {
		final List<Item> items = new ArrayList<>();

		final List<GermplasmSearchResultModel> germplasms = new ArrayList<>();

		try {
			final List<Germplasm> germplasmList;

			if (this.searchChoice.equals(SEARCH_OPTION_NAME)) {
				if (this.searchValue.contains("%")) {
					germplasmList = this.germplasmDataManager.getGermplasmByName(this.searchValue, start, numOfRows, Operation.LIKE);
				} else {
					germplasmList = this.germplasmDataManager.getGermplasmByName(this.searchValue, start, numOfRows, Operation.EQUAL);
				}
				for (final Germplasm g : germplasmList) {
					final Germplasm gData = g;
					final GermplasmSearchResultModel gResult = new GermplasmSearchResultModel();
					germplasms.add(this.setGermplasmSearchResult(gResult, gData));

				}
			} else {
				final Germplasm gData = this.germplasmDataManager.getGermplasmByGID(Integer.parseInt(this.searchValue));
				GermplasmSearchResultModel gResult = new GermplasmSearchResultModel();

				if (gData != null) {
					gResult = this.setGermplasmSearchResult(gResult, gData);
					germplasms.add(gResult);
				}

			}

		} catch (final MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE,
					Message.ERROR_IN_GETTING_GERMPLASM_LIST_RESULT_BY_PREFERRED_NAME);
		}

		for (final GermplasmSearchResultModel germplasm : germplasms) {
			final PropertysetItem item = new PropertysetItem();
			item.addItemProperty(GermplasmSearchQuery.GID, new ObjectProperty<String>(germplasm.getGid().toString()));
			if (germplasm.getNames() != null) {
				item.addItemProperty(GermplasmSearchQuery.NAMES, new ObjectProperty<String>(germplasm.getNames()));
			} else {
				item.addItemProperty(GermplasmSearchQuery.NAMES, new ObjectProperty<String>("-"));
			}
			item.addItemProperty(GermplasmSearchQuery.METHOD, new ObjectProperty<String>(germplasm.getMethod()));
			item.addItemProperty(GermplasmSearchQuery.LOCATION, new ObjectProperty<String>(germplasm.getLocation()));
			items.add(item);
		}

		return items;
	}

	@SuppressWarnings("deprecation")
	private GermplasmSearchResultModel setGermplasmSearchResult(final GermplasmSearchResultModel gResult, final Germplasm gData) {
		gResult.setGid(gData.getGid());
		gResult.setNames(this.getGermplasmNames(gData.getGid()));

		try {
			final Method method = this.germplasmDataManager.getMethodByID(gData.getMethod().getMid());
			if (method != null) {
				gResult.setMethod(method.getMname());
			} else {
				gResult.setMethod("");
			}

			final Location loc = this.germplasmDataManager.getLocationByID(gData.getLocationId());
			if (loc != null) {
				gResult.setLocation(loc.getLname());
			} else {
				gResult.setLocation("");
			}

			return gResult;
		} catch (final MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_SEARCH);
		}

	}

	private String getGermplasmNames(final int gid) {

		try {
			final List<Name> names = this.germplasmDataManager.getNamesByGID(gid, null, null);
			final StringBuilder germplasmNames = new StringBuilder("");
			int i = 0;
			for (final Name n : names) {
				if (i < names.size() - 1) {
					germplasmNames.append(n.getNval() + ",");
				} else {
					germplasmNames.append(n.getNval());
				}
				i++;
			}

			return germplasmNames.toString();
		} catch (final MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_GETTING_NAMES_BY_GERMPLASM_ID);
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
				if (this.searchChoice.equals(SEARCH_OPTION_NAME)) {
					if (this.searchValue.contains("%")) {
						this.size = (int) this.germplasmDataManager.countGermplasmByName(this.searchValue, Operation.LIKE);
					} else {
						this.size = (int) this.germplasmDataManager.countGermplasmByName(this.searchValue, Operation.EQUAL);
					}
				} else {
					this.size = this.germplasmDataManager.getGermplasmByGID(Integer.parseInt(this.searchValue)) != null ? 1 : 0;
				}
			}
		} catch (final MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE,
					Message.ERROR_IN_GETTING_GERMPLASM_LIST_RESULT_BY_PREFERRED_NAME);
		}

		return this.size;
	}

}

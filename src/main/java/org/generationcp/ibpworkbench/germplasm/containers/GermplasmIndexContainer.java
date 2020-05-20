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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import org.generationcp.browser.study.containers.StudyButtonRenderer;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.cross.study.h2h.main.containers.GermplasmEnvironmentSearchQuery;
import org.generationcp.ibpworkbench.cross.study.h2h.main.containers.GermplasmEnvironmentSearchQueryFactory;
import org.generationcp.ibpworkbench.germplasm.GermplasmDetailModel;
import org.generationcp.ibpworkbench.germplasm.GermplasmNamesAttributesModel;
import org.generationcp.ibpworkbench.germplasm.GermplasmQueries;
import org.generationcp.ibpworkbench.germplasm.GermplasmSearchResultModel;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import java.util.List;

public final class GermplasmIndexContainer {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmIndexContainer.class);

	// Germplasm SearchResult Model
	private static final Object GERMPLASM_GID = "gid";
	private static final Object GERMPLASM_NAMES = "names";
	private static final Object GERMPLASM_METHOD = "method";
	private static final Object GERMPLASM_LOCATION = "location";

	// GermplasmNamesAttribute Model
	private static final Object GERMPLASM_NAMES_ATTRIBUTE_TYPE = "type";
	private static final Object GERMPLASM_NAMES_ATTRIBUTE_NAME = "name";
	private static final Object GERMPLASM_NAMES_ATTRIBUTE_DATE = "date";
	private static final Object GERMPLASM_NAMES_ATTRIBUTE_LOCATION = "location";
	private static final Object GERMPLASM_NAMES_ATTRIBUTE_TYPE_DESC = "typedesc";

	// Germplasm Inventory Model
	private static final Object GERMPLASM_INVENTORY_ACTUAL_LOT_BALANCE = "lotbalance";
	private static final Object GERMPLASM_INVENTORY_LOCATION_NAME = "location";
	private static final Object GERMPLASM_INVENTORY_SCALE_NAME = "scale";
	private static final Object GERMPLASM_INVENTORY_LOT_COMMENT = "lotcomment";

	// Study Information Model
	public static final String STUDY_ID = "studyid";
	public static final String STUDY_NAME = "studyname";
	public static final String STUDY_DESCRIPTION = "description";

	private static final Object GERMPLASM_PREFNAME = "prefname";


	private final GermplasmQueries qQuery;

	public GermplasmIndexContainer(final GermplasmQueries qQuery) {
		this.qQuery = qQuery;
	}

	public IndexedContainer getGermplasmResultContainer(final String choice, final String searchValue) {
		final IndexedContainer container = new IndexedContainer();

		// Create the container properties - Germplasm Search Result
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_GID, Integer.class, 0);
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_NAMES, String.class, "");
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_METHOD, String.class, "");
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_LOCATION, String.class, "");

		List<GermplasmSearchResultModel> queryByNames = null;
		GermplasmSearchResultModel queryByGid = null;
		if (choice.equals(GermplasmQueries.SEARCH_OPTION_NAME)) {
			queryByNames = this.qQuery.getGermplasmListResultByPrefStandardizedName(searchValue);
			for (final GermplasmSearchResultModel q : queryByNames) {
				GermplasmIndexContainer.addGermplasmResultContainer(container, q.getGid(), q.getNames(), q.getMethod(), q.getLocation());
			}
		} else {
			queryByGid = this.qQuery.getGermplasmResultByGID(searchValue);
			if (queryByGid != null) {
				GermplasmIndexContainer.addGermplasmResultContainer(container, queryByGid.getGid(), queryByGid.getNames(),
						queryByGid.getMethod(), queryByGid.getLocation());
			}
		}

		return container;
	}

	public LazyQueryContainer getGermplasmResultLazyContainer(final GermplasmDataManager germplasmDataManager, final String searchChoice,
			final String searchValue) {

		final GermplasmSearchQueryFactory factory = new GermplasmSearchQueryFactory(germplasmDataManager, searchChoice, searchValue);
		final LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);

		// add the column ids to the LazyQueryContainer tells the container the columns to display for the Table
		container.addContainerProperty(GermplasmSearchQuery.GID, String.class, null);
		container.addContainerProperty(GermplasmSearchQuery.NAMES, String.class, null);
		container.addContainerProperty(GermplasmSearchQuery.METHOD, String.class, null);
		container.addContainerProperty(GermplasmSearchQuery.LOCATION, String.class, null);

		// initialize the first batch of data to be displayed
		container.getQueryView().getItem(0);
		return container;
	}

	public LazyQueryContainer getGermplasmEnvironmentResultLazyContainer(final CrossStudyDataManager crossStudyDataManager, final String searchChoice,
			final String searchValue, final List<Integer> environmentIds) {

		final GermplasmEnvironmentSearchQueryFactory factory =
				new GermplasmEnvironmentSearchQueryFactory(crossStudyDataManager, searchChoice, searchValue, environmentIds);
		final LazyQueryContainer container = new LazyQueryContainer(factory, false, 10);

		// add the column ids to the LazyQueryContainer tells the container the columns to display for the Table
		container.addContainerProperty(GermplasmEnvironmentSearchQuery.GID, String.class, null);
		container.addContainerProperty(GermplasmEnvironmentSearchQuery.NAMES, String.class, null);
		container.addContainerProperty(GermplasmEnvironmentSearchQuery.LOCATION, String.class, null);

		// initialize the first batch of data to be displayed
		container.getQueryView().getItem(0);
		return container;
	}

	private static void addGermplasmResultContainer(
		final Container container, final int gid, final String names, final String method, final String location) {
		final Object itemId = container.addItem();
		final Item item = container.getItem(itemId);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_GID).setValue(gid);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_NAMES).setValue(names);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_METHOD).setValue(method);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_LOCATION).setValue(location);
	}

	public IndexedContainer getGermplasmAttribute(final GermplasmDetailModel g) {
		final IndexedContainer container = new IndexedContainer();

		// Create the container properties
		this.addContainerProperties(container);

		final List<GermplasmNamesAttributesModel> query = this.qQuery.getAttributes(g.getGid());
		LOG.info("Size of the query  {}", query.size());
		for (final GermplasmNamesAttributesModel q : query) {
			GermplasmIndexContainer.addGermplasmNamesAttributeContainer(container, q.getType(), q.getTypeDesc(), q.getName(), q.getDate(),
					q.getLocation());
		}
		return container;
	}

	public IndexedContainer getGermplasmNames(final GermplasmDetailModel g) {
		final IndexedContainer container = new IndexedContainer();

		// Create the container properties
		this.addContainerProperties(container);

		final List<GermplasmNamesAttributesModel> query = this.qQuery.getNames(g.getGid());
		for (final GermplasmNamesAttributesModel q : query) {
			GermplasmIndexContainer.addGermplasmNamesAttributeContainer(container, q.getName(), q.getDate(), q.getLocation(), q.getType(),
					q.getTypeDesc());
		}
		return container;
	}

	private void addContainerProperties(final Container container) {
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_TYPE, String.class, "");
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_NAME, String.class, "");
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_DATE, String.class, "");
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_LOCATION, String.class, "");
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_TYPE_DESC, String.class, "");
	}

	private static void addGermplasmNamesAttributeContainer(final Container container, final String type, final String name, final String date, final String location,
			final String typeDesc) {
		final Object itemId = container.addItem();
		final Item item = container.getItem(itemId);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_TYPE).setValue(type);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_NAME).setValue(name);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_DATE).setValue(date);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_LOCATION).setValue(location);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_NAMES_ATTRIBUTE_TYPE_DESC).setValue(typeDesc);

	}

	public IndexedContainer getGermplasmGenerationHistory(final GermplasmDetailModel gModel) {
		final IndexedContainer container = new IndexedContainer();

		// Create the container properties
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_GID, Integer.class, 0);
		container.addContainerProperty(GermplasmIndexContainer.GERMPLASM_PREFNAME, String.class, "");

		final List<GermplasmDetailModel> query = this.qQuery.getGenerationHistory(Integer.valueOf(gModel.getGid()));
		for (final GermplasmDetailModel g : query) {
			GermplasmIndexContainer.addGermplasmGenerationHistory(container, g.getGid(), g.getGermplasmPreferredName());
		}
		return container;
	}

	private static void addGermplasmGenerationHistory(final Container container, final int gid, final String prefname) {
		final Object itemId = container.addItem();
		final Item item = container.getItem(itemId);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_GID).setValue(gid);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_PREFNAME).setValue(prefname);
	}


	private static void addLotReportRowContainer(final Container container, final String lotBalance, final String locationName, final String scaleName,
			final String lotComment) {
		final Object itemId = container.addItem();
		final Item item = container.getItem(itemId);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_INVENTORY_ACTUAL_LOT_BALANCE).setValue(lotBalance);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_INVENTORY_LOCATION_NAME).setValue(locationName);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_INVENTORY_SCALE_NAME).setValue(scaleName);
		item.getItemProperty(GermplasmIndexContainer.GERMPLASM_INVENTORY_LOT_COMMENT).setValue(lotComment);
	}

	public IndexedContainer getGermplasmStudyInformation(final GermplasmDetailModel gModel, final ContextUtil contextUtil) {
		final IndexedContainer container = new IndexedContainer();

		// Create the container properties
		container.addContainerProperty(GermplasmIndexContainer.STUDY_ID, Integer.class, 0);
		container.addContainerProperty(GermplasmIndexContainer.STUDY_NAME, Button.class, null);
		container.addContainerProperty(GermplasmIndexContainer.STUDY_DESCRIPTION, String.class, "");

		final List<StudyReference> studies = this.qQuery.getGermplasmStudyInfo(gModel.getGid());
		for (final StudyReference study : studies) {
			this.addGermplasmStudyInformation(container, study, new StudyButtonRenderer(study));		
		}
		return container;
	}

	void addGermplasmStudyInformation(final Container container, final StudyReference study, final StudyButtonRenderer studyButtonRenderer) {
		final Object itemId = container.addItem();
		final Item item = container.getItem(itemId);
		item.getItemProperty(GermplasmIndexContainer.STUDY_ID).setValue(study.getId());
		item.getItemProperty(GermplasmIndexContainer.STUDY_DESCRIPTION).setValue(study.getDescription());
		item.getItemProperty(GermplasmIndexContainer.STUDY_NAME).setValue(studyButtonRenderer.renderStudyButton());
	}

}

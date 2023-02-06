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

import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

public final class GermplasmIndexContainer {

	// Study Information Model
	public static final String STUDY_ID = "studyid";
	public static final String STUDY_NAME = "studyname";

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

}

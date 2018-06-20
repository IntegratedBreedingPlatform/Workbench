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

package org.generationcp.browser.study.containers;

import java.util.List;

import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

/**
 * A factory that creates a list of study details by implementing QueryFactory
 *
 * @author Mae Turiana
 *
 */
public class StudyDetailsQueryFactory implements QueryFactory {

	private final StudyDataManager studyDataManager;
	private final StudyTypeDto studyType;
	private final List<String> columnIds;
	private final String programUUID;
	private Query query;

	public StudyDetailsQueryFactory(final StudyDataManager studyDataManager, final StudyTypeDto studyType, final List<String> columnIds, final String programUUID) {
		super();
		this.studyDataManager = studyDataManager;
		this.studyType = studyType;
		this.columnIds = columnIds;
		this.programUUID = programUUID;
	}

	/**
	 * Create the Query object to be used by the LazyQueryContainer. Sorting is not yet supported so the parameters to this method are not
	 * used.
	 */
	@Override
	public Query constructQuery(final Object[] sortPropertyIds, final boolean[] sortStates) {
		this.query = new StudyDetailsQuery(this.studyDataManager, this.studyType, this.columnIds, this.programUUID);
		return this.query;
	}

	@Override
	public void setQueryDefinition(final QueryDefinition arg0) {
		// no yet used
	}

	public int getNumberOfItems() {
		if (this.query != null) {
			return this.query.size();
		}
		return 0;
	}

}

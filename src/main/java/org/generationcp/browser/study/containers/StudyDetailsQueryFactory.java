/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.browser.study.containers;

import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import java.util.List;

/**
 * A factory that creates a list of study details by implementing QueryFactory
 * 
 * @author Mae Turiana
 * 
 */
public class StudyDetailsQueryFactory implements QueryFactory{

	private StudyDataManager studyDataManager;
	private StudyType studyType;
	private List<String> columnIds;
	private Query query;
	
	public StudyDetailsQueryFactory(StudyDataManager studyDataManager,
			StudyType studyType, List<String> columnIds) {
		super();
		this.studyDataManager = studyDataManager;
		this.studyType = studyType;
		this.columnIds = columnIds;
	}

	/**
     * Create the Query object to be used by the LazyQueryContainer. Sorting is
     * not yet supported so the parameters to this method are not used.
     */
	@Override
	public Query constructQuery(Object[] sortPropertyIds, boolean[] sortStates) {
		query = new StudyDetailsQuery(studyDataManager, studyType, columnIds);
		return query;
	}

	@Override
	public void setQueryDefinition(QueryDefinition arg0) {
		//no yet used
	}

    public int getNumberOfItems() {
    	if(query!=null) {
    		return query.size();
    	}
    	return 0;
    }

}

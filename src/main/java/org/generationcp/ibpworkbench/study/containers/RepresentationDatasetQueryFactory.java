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

package org.generationcp.ibpworkbench.study.containers;

import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import java.util.List;

/**
 * An implementation of QueryFactory which is needed for using the LazyQueryContainer.
 *
 * Reference: https://vaadin.com/wiki/-/wiki/Main/Lazy%20Query%20Container/#section
 * -Lazy+Query+Container-HowToImplementCustomQueryAndQueryFactory
 *
 * @author Kevin Manansala
 *
 */
public class RepresentationDatasetQueryFactory implements QueryFactory {

	private final StudyDataManager studyDataManager;
	private final Integer datasetId;
	private final Integer studyId;
	private final List<String> columnIds;
	@SuppressWarnings("unused")
	private QueryDefinition definition;
	private final boolean fromUrl; // this is true if this component is created by accessing the Study Details page directly from the URL

	private DatasetService datasetService;
	/**
	 * The constructor should be given the parameters which are then passed to RepresentationDataSetQuery which uses them to retrieve the
	 * datasets by using the Middleware.
	 *
	 * @param datasetService
	 * @param dataManager
	 * @param datasetId - id of the selected Representation of a Study
	 * @param columnIds - List of column ids used for the Vaadin Table displaying the dataset
	 * @param studyId 
	 */
	public RepresentationDatasetQueryFactory(DatasetService datasetService, StudyDataManager studyDataManager, Integer datasetId, List<String> columnIds, boolean fromUrl, Integer studyId) {
		super();
		this.studyDataManager = studyDataManager;
		this.datasetId = datasetId;
		this.studyId = studyId;
		this.columnIds = columnIds;
		this.fromUrl = fromUrl;
		this.datasetService = datasetService;
	}

	/**
	 * Create the Query object to be used by the LazyQueryContainer. Sorting is not yet supported so the parameters to this method are not
	 * used.
	 */
	@Override
	public Query constructQuery(Object[] sortPropertyIds, boolean[] sortStates) {
		return new RepresentationDataSetQuery(this.datasetService, this.studyDataManager, this.datasetId, this.columnIds, this.fromUrl, this.studyId);
	}

	@Override
	public void setQueryDefinition(QueryDefinition definition) {
		// not sure how a QueryDefinition is used and how to create one
		// for the current implementation this is not used and I just copied
		// this method declaration
		// from the reference
		this.definition = definition;
	}

}

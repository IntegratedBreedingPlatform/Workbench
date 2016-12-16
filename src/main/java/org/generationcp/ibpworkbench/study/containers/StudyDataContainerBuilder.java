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

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySearchMatchingOption;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.search.StudyResultSet;
import org.generationcp.middleware.domain.search.filter.BrowseStudyQueryFilter;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyDataContainerBuilder {

	public static final String STUDY_ID = "ID";
	public static final String STUDY_NAME = "NAME";
	private static final Logger LOG = LoggerFactory.getLogger(StudyDataContainerBuilder.class);
	// Factor Object
	private static final Object FACTOR_NAME = "factorName";
	private static final Object VARIATE_NAME = "variateName";
	private static final Object DESCRIPTION = "description";
	private static final Object PROPERTY_NAME = "propertyName";
	private static final Object SCALE_NAME = "scaleName";
	private static final Object METHOD_NAME = "methodName";
	private static final Object DATATYPE = "dataType";
	private static final Object VALUE = "value";
	private final StudyDataManager studyDataManager;
	private final int studyId;

	public StudyDataContainerBuilder(StudyDataManager studyDataManager, int studyId) {
		this.studyDataManager = studyDataManager;
		this.studyId = studyId;
	}

	private static void addFactorDataToContainer(Container container, String factorName, String description, String propertyName, String scale,
			String method, String datatype, String value) {
		Object itemId = container.addItem();
		Item item = container.getItem(itemId);
		item.getItemProperty(StudyDataContainerBuilder.FACTOR_NAME).setValue(factorName);
		item.getItemProperty(StudyDataContainerBuilder.DESCRIPTION).setValue(description);
		item.getItemProperty(StudyDataContainerBuilder.PROPERTY_NAME).setValue(propertyName);
		item.getItemProperty(StudyDataContainerBuilder.SCALE_NAME).setValue(scale);
		item.getItemProperty(StudyDataContainerBuilder.METHOD_NAME).setValue(method);
		item.getItemProperty(StudyDataContainerBuilder.DATATYPE).setValue(datatype);
		item.getItemProperty(StudyDataContainerBuilder.VALUE).setValue(value);
	}

	private static void addVariateDataToContainer(Container container, String variateName, String description, String propertyName, String scale,
			String method, String datatype, String value) {
		Object itemId = container.addItem();
		Item item = container.getItem(itemId);
		item.getItemProperty(StudyDataContainerBuilder.VARIATE_NAME).setValue(variateName);
		item.getItemProperty(StudyDataContainerBuilder.DESCRIPTION).setValue(description);
		item.getItemProperty(StudyDataContainerBuilder.PROPERTY_NAME).setValue(propertyName);
		item.getItemProperty(StudyDataContainerBuilder.SCALE_NAME).setValue(scale);
		item.getItemProperty(StudyDataContainerBuilder.METHOD_NAME).setValue(method);
		item.getItemProperty(StudyDataContainerBuilder.DATATYPE).setValue(datatype);
		item.getItemProperty(StudyDataContainerBuilder.VALUE).setValue(value);
	}

	public IndexedContainer buildIndexedContainerForStudyFactor() throws InternationalizableException {
		try {
			IndexedContainer container = new IndexedContainer();

			// Create the container properties
			container.addContainerProperty(StudyDataContainerBuilder.FACTOR_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.DESCRIPTION, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.PROPERTY_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.SCALE_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.METHOD_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.DATATYPE, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.VALUE, String.class, "");

			Study study = this.studyDataManager.getStudy(this.studyId);
			VariableList variableList = study.getConditions();
			List<Variable> conditions = variableList.getVariables();
			VariableTypeList factors = this.studyDataManager.getAllStudyFactors(Integer.valueOf(this.studyId));
			List<DMSVariableType> factorDetails = factors.getVariableTypes();
			for (DMSVariableType factorDetail : factorDetails) {
				String name = factorDetail.getLocalName();
				String description = factorDetail.getStandardVariable().getDescription();
				if (factorDetail.getLocalDescription() != null && factorDetail.getLocalDescription().length() != 0) {
					description = factorDetail.getLocalDescription().trim();
				}
				String propertyName = factorDetail.getStandardVariable().getName();
				if (factorDetail.getStandardVariable().getProperty() != null) {
					propertyName = factorDetail.getStandardVariable().getProperty().getName();
				}
				String scaleName = factorDetail.getStandardVariable().getScale().getName();
				String methodName = factorDetail.getStandardVariable().getMethod().getName();
				String dataType = factorDetail.getStandardVariable().getDataType().getName();
				String value = null;

				for (Variable condition : conditions) {
					String conditionName = condition.getVariableType().getLocalName();
					if (name.equals(conditionName)) {
						value = condition.getDisplayValue();
					}
				}

				StudyDataContainerBuilder
						.addFactorDataToContainer(container, name, description, propertyName, scaleName, methodName, dataType, value);
			}

			return container;

		} catch (MiddlewareException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_GETTING_STUDY_FACTOR);
		}
	}

	public IndexedContainer buildIndexedContainerForStudyVariate() throws InternationalizableException {
		try {
			IndexedContainer container = new IndexedContainer();

			// Create the container properties
			container.addContainerProperty(StudyDataContainerBuilder.VARIATE_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.DESCRIPTION, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.PROPERTY_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.SCALE_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.METHOD_NAME, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.DATATYPE, String.class, "");
			container.addContainerProperty(StudyDataContainerBuilder.VALUE, String.class, "");

			Study study = this.studyDataManager.getStudy(this.studyId);
			VariableList variableList = study.getConstants();
			List<Variable> constants = variableList.getVariables();
			VariableTypeList variates = this.studyDataManager.getAllStudyVariates(Integer.valueOf(this.studyId));
			List<DMSVariableType> variateDetails = variates.getVariableTypes();
			for (DMSVariableType variateDetail : variateDetails) {
				String name = variateDetail.getLocalName();
				String description = variateDetail.getStandardVariable().getDescription();
				if (variateDetail.getLocalDescription() != null && variateDetail.getLocalDescription().length() != 0) {
					description = variateDetail.getLocalDescription().trim();
				}
				String propertyName = variateDetail.getStandardVariable().getName();
				if (variateDetail.getStandardVariable().getProperty() != null) {
					propertyName = variateDetail.getStandardVariable().getProperty().getName();
				}
				String scaleName = variateDetail.getStandardVariable().getScale().getName();
				String methodName = variateDetail.getStandardVariable().getMethod().getName();
				String dataType = variateDetail.getStandardVariable().getDataType().getName();
				String value = null;

				for (Variable constant : constants) {
					String constantName = constant.getVariableType().getLocalName();
					if (name.equals(constantName)) {
						value = constant.getDisplayValue();
					}
				}

				StudyDataContainerBuilder
						.addVariateDataToContainer(container, name, description, propertyName, scaleName, methodName, dataType, value);
			}

			return container;
		} catch (MiddlewareException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_GETTING_STUDY_VARIATE);
		}
	}

	public IndexedContainer buildIndexedContainerForStudies(StudySearchMatchingOption studySearchMatchingOption, String name, String country, Season season, Integer date) throws InternationalizableException {
		IndexedContainer container = new IndexedContainer();

		// Create the container properties
		container.addContainerProperty(StudyDataContainerBuilder.STUDY_ID, Integer.class, "");
		container.addContainerProperty(StudyDataContainerBuilder.STUDY_NAME, String.class, "");
		Map<String, StudyReference> mapChecker = new HashMap<String, StudyReference>();
		try {
			BrowseStudyQueryFilter filter = new BrowseStudyQueryFilter();
			filter.setName(name);
			filter.setCountry(country);
			filter.setSeason(season);
			filter.setStartDate(date);
			filter.setStudySearchMatchingOption(studySearchMatchingOption);
			StudyResultSet studyResultSet = this.studyDataManager.searchStudies(filter, 50);

			if (studyResultSet != null) {
				while (studyResultSet.hasMore()) {
					StudyReference studyRef = studyResultSet.next();
					if (mapChecker.get(studyRef.getId().toString()) == null) {
						mapChecker.put(studyRef.getId().toString(), studyRef);
						this.addStudyDataToContainer(container, studyRef.getId(), studyRef.getName());
					}
				}
			}
		} catch (MiddlewareQueryException e) {
			StudyDataContainerBuilder.LOG.error("Error encountered while searching for studies", e);
			throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_PLEASE_CONTACT_ADMINISTRATOR);
		}

		return container;
	}

	private void addStudyDataToContainer(Container container, Integer id, String name) {
		Object itemId = container.addItem();
		Item item = container.getItem(itemId);
		item.getItemProperty(StudyDataContainerBuilder.STUDY_ID).setValue(id);
		item.getItemProperty(StudyDataContainerBuilder.STUDY_NAME).setValue(name);

	}

}

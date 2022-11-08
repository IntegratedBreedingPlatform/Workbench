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

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.generationcp.ibpworkbench.study.listeners.GidLinkButtonClickListener;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.lazyquerycontainer.Query;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of Query which is needed for using the LazyQueryContainer.
 *
 * Reference:
 * https://vaadin.com/wiki/-/wiki/Main/Lazy%20Query%20Container/#section
 * -Lazy+Query+Container-HowToImplementCustomQueryAndQueryFactory
 *
 * @author Kevin Manansala
 *
 */
public class RepresentationDataSetQuery implements Query {

	private final static Logger LOG = LoggerFactory.getLogger(RepresentationDataSetQuery.class);

	private final StudyDataManager studyDataManager;
	private final Integer studyId;
	private final Integer datasetId;
	private final List<String> columnIds;
	private final boolean fromUrl; // this is true if this component is created
									// by accessing the Study Details page
									// directly from the URL
	private int size;

	public static final String IS_ACCEPTED_VALUE_KEY = "isAcceptedValue";

	public static final String MISSING_VALUE = "missing";

	private DatasetService datasetService;

	/**
	 * These parameters are passed by the QueryFactory which instantiates
	 * objects of this class.
	 *
	 * @param dataManager
	 * @param datasetId
	 * @param columnIds
	 */
	public RepresentationDataSetQuery(final DatasetService datasetService, final StudyDataManager studyDataManager, final Integer datasetId,
			final List<String> columnIds, final boolean fromUrl, final Integer studyId) {
		super();
		this.studyDataManager = studyDataManager;
		this.studyId = studyId;
		this.datasetId = datasetId;
		this.columnIds = columnIds;
		this.fromUrl = fromUrl;
		this.size = -1;
		this.datasetService = datasetService;
	}

	/**
	 * This method seems to be called for creating blank items on the Table
	 */
	@Override
	public Item constructItem() {
		final PropertysetItem item = new PropertysetItem();
		for (final String id : this.columnIds) {
			item.addItemProperty(id, new ObjectProperty<String>(""));
		}
		return item;
	}

	@Override
	public boolean deleteAllItems() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Retrieves the dataset by batches of rows. Used for lazy loading the
	 * dataset.
	 */
	@Override
	public List<Item> loadItems(final int start, final int numOfRows) {
		final List<Item> items = new ArrayList<>();
		final Map<Integer, Item> itemMap = new LinkedHashMap<>();
		List<Experiment> experiments = new ArrayList<>();
		try {
			experiments = this.studyDataManager.getExperiments(this.datasetId, start, numOfRows);
		} catch (final MiddlewareException ex) {
			// Log error in log file
			RepresentationDataSetQuery.LOG
					.error("Error with getting ounitids for representation: " + this.datasetId + "\n" + ex.toString());
			experiments = new ArrayList<>();
		}

		final List<GermplasmNameTypeDTO> germplasmNameTypeDTOs = this.datasetService.getDatasetNameTypes(this.datasetId);
		germplasmNameTypeDTOs.sort(Comparator.comparing(GermplasmNameTypeDTO::getCode));

		if (!experiments.isEmpty()) {
			final Map<String, String> locationNameMap = this.studyDataManager.createInstanceLocationIdToNameMapFromStudy(this.studyId);
			for (final Experiment experiment : experiments) {
				final List<Variable> variables = new ArrayList<>();

				final VariableList factors = experiment.getFactors();
				if (factors != null) {
					variables.addAll(factors.getVariables());
				}

				final VariableList variates = experiment.getVariates();
				if (variates != null) {
					variables.addAll(variates.getVariables());
				}
				this.populateItemMap(itemMap, experiment, variables, locationNameMap);

				germplasmNameTypeDTOs.forEach(germplasmNameTypeDTO -> {
					final String columnId = new StringBuffer().append(germplasmNameTypeDTO.getId()).append("-")
						.append(germplasmNameTypeDTO.getCode()).toString();
					Item item = itemMap.get(Integer.valueOf(experiment.getId()));
					if (item == null) {
						// not yet in map so create a new Item and add to map
						item = new PropertysetItem();
						itemMap.put(Integer.valueOf(experiment.getId()), item);
					}
					final String value = experiment.getNameValueMap().get(germplasmNameTypeDTO.getId());
					item.addItemProperty(columnId, new ObjectProperty<String>(StringUtils.isBlank(value) ? "" : value));
				});
			}
		}

		items.addAll(itemMap.values());
		return items;
	}

	protected void populateItemMap(final Map<Integer, Item> itemMap, final Experiment experiment,
			final List<Variable> variables, final Map<String, String> locationNameMap) {
		for (final Variable variable : variables) {
			final String columnId = new StringBuffer().append(variable.getVariableType().getId()).append("-")
					.append(variable.getVariableType().getLocalName()).toString();

			Item item = itemMap.get(Integer.valueOf(experiment.getId()));
			if (item == null) {
				// not yet in map so create a new Item and add to map
				item = new PropertysetItem();
				itemMap.put(Integer.valueOf(experiment.getId()), item);
			}

			if (variable.getValue() == null) {
				item.addItemProperty(columnId, null);
			} else {
				// check factor name, if it's a GID, then make the GID as a
				// link. else, show it as a value only
				// make GID as link only if the page wasn't directly accessed
				// from the URL
				if (!this.fromUrl &&
					( (TermId.GID.getId() == variable.getVariableType().getId() ||
					 TermId.FEMALE_PARENT_GID.getId() == variable.getVariableType().getId() ||
					 TermId.MALE_PARENT_GID.getId() == variable.getVariableType().getId()) ) &&
					variable.getValue() != "UNKNOWN") {

					final String value = variable.getValue();
					final Button gidButton = new Button(value.trim(), new GidLinkButtonClickListener(value.trim()));
					gidButton.setStyleName(BaseTheme.BUTTON_LINK);
					gidButton.setDescription("Click to view Germplasm information");
					item.addItemProperty(columnId, new ObjectProperty<Button>(gidButton));
					// end GID link creation
				} else if (TermId.LOCATION_ID.getId() == variable.getVariableType().getId()) {
					final String value = variable.getDisplayValue();
					item.addItemProperty(columnId, new ObjectProperty<String>(locationNameMap.get(value)));
				} else {
					// check if the variable value is a number to remove decimal
					// portion if there is no value after the decimal point
					final String value = variable.getDisplayValue();
					this.setAcceptedItemProperty(value, variable.getVariableType().getStandardVariable(), item,
							columnId);
					try {
						final double doubleValue = Double.parseDouble(value);
						if (Math.round(doubleValue) != doubleValue) {
							item.addItemProperty(columnId, new ObjectProperty<String>(value));
						} else {
							item.addItemProperty(columnId,
									new ObjectProperty<String>(String.format("%.0f", doubleValue)));
						}
					} catch (final NumberFormatException ex) {
						// add value as String
						item.addItemProperty(columnId, new ObjectProperty<String>(value));
					}
				}
			}
		}
	}

	protected boolean setAcceptedItemProperty(final String value, final StandardVariable standardVariable,
			final Item item, final String columnId) {
		boolean isAccepted = false;
		if (this.isCategoricalAcceptedValue(value, standardVariable)
				|| this.isNumericalAcceptedValue(value, standardVariable)) {
			item.addItemProperty(columnId + RepresentationDataSetQuery.IS_ACCEPTED_VALUE_KEY,
					new ObjectProperty<Boolean>(true));
			isAccepted = true;
		} else {
			item.addItemProperty(columnId + RepresentationDataSetQuery.IS_ACCEPTED_VALUE_KEY,
					new ObjectProperty<Boolean>(false));
		}
		return isAccepted;
	}

	protected boolean isCategoricalAcceptedValue(final String displayValue, final StandardVariable standardVariable) {
		if (standardVariable.getDataType().getId() == TermId.CATEGORICAL_VARIABLE.getId() && displayValue != null
			&& !displayValue.isEmpty() && !RepresentationDataSetQuery.MISSING_VALUE.equals(displayValue)
			&& standardVariable.getVariableTypes().contains(VariableType.TRAIT)) {
			if (standardVariable.getEnumerations() == null) {
				return true;
			}
			for (final Enumeration enumeration : standardVariable.getEnumerations()) {
				if (enumeration.getDescription().equalsIgnoreCase(displayValue)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	protected boolean isNumericalAcceptedValue(final String displayValue, final StandardVariable standardVariable) {
		if (standardVariable.getDataType().getId() == TermId.NUMERIC_VARIABLE.getId() && displayValue != null
				&& !displayValue.equalsIgnoreCase("") && !RepresentationDataSetQuery.MISSING_VALUE.equals(displayValue)
				&& standardVariable.getConstraints() != null) {
			if (standardVariable.getConstraints().getMaxValue() != null
					&& standardVariable.getConstraints().getMinValue() != null && NumberUtils.isNumber(displayValue)) {

				if (Double.parseDouble(displayValue) < standardVariable.getConstraints().getMinValue()
						|| Double.parseDouble(displayValue) > standardVariable.getConstraints().getMaxValue()) {
					return true;
				}
			}
			return false;
		}
		return false;
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
		if (this.size == -1) {
			final Long count = Long.valueOf(this.studyDataManager.countExperiments(this.datasetId));
			this.size = count.intValue();
		}

		return this.size;
	}

}

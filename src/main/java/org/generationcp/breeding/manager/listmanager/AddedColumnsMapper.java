
package org.generationcp.breeding.manager.listmanager;

import org.generationcp.breeding.manager.listmanager.api.FillColumnSource;
import org.generationcp.breeding.manager.listmanager.util.FillWithOption;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configurable
public class AddedColumnsMapper {

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	private GermplasmColumnValuesGenerator valuesGenerator;

	public AddedColumnsMapper(final FillColumnSource fillWithSource) {
		super();
		this.valuesGenerator = new GermplasmColumnValuesGenerator(fillWithSource);
	}

	public void generateValuesForAddedColumns(final Object[] visibleColumns) {
		if (this.isColumnVisible(visibleColumns, ColumnLabels.PREFERRED_ID.getName())) {
			this.valuesGenerator.setPreferredIdColumnValues(ColumnLabels.PREFERRED_ID.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.GERMPLASM_LOCATION.getName())) {
			this.valuesGenerator.setLocationNameColumnValues(ColumnLabels.GERMPLASM_LOCATION.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.PREFERRED_NAME.getName())) {
			this.valuesGenerator.setPreferredNameColumnValues(ColumnLabels.PREFERRED_NAME.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.GERMPLASM_DATE.getName())) {
			this.valuesGenerator.setGermplasmDateColumnValues(ColumnLabels.GERMPLASM_DATE.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_NAME.getName())) {
			this.valuesGenerator.setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_NAME.getName(),
					FillWithOption.FILL_WITH_BREEDING_METHOD_NAME);
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName())) {
			this.valuesGenerator.setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName(),
					FillWithOption.FILL_WITH_BREEDING_METHOD_ABBREV);
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_NUMBER.getName())) {
			this.valuesGenerator.setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_NUMBER.getName(),
					FillWithOption.FILL_WITH_BREEDING_METHOD_NUMBER);
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_GROUP.getName())) {
			this.valuesGenerator.setMethodInfoColumnValues(ColumnLabels.BREEDING_METHOD_GROUP.getName(),
					FillWithOption.FILL_WITH_BREEDING_METHOD_GROUP);
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.FGID.getName())) {
			this.valuesGenerator.setCrossFemaleInfoColumnValues(ColumnLabels.FGID.getName(),
					FillWithOption.FILL_WITH_CROSS_FEMALE_GID);
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName())) {
			this.valuesGenerator.setCrossFemaleInfoColumnValues(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName(),
					FillWithOption.FILL_WITH_CROSS_FEMALE_NAME);
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.MGID.getName())) {
			this.valuesGenerator.setCrossMaleGIDColumnValues(ColumnLabels.MGID.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName())) {
			this.valuesGenerator.setCrossMalePrefNameColumnValues(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName())) {
			this.valuesGenerator.setGroupSourcePreferredNameColumnValues(ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.GROUP_SOURCE_GID.getName())) {
			this.valuesGenerator.setGroupSourceGidColumnValues(ColumnLabels.GROUP_SOURCE_GID.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName())) {
			this.valuesGenerator.setImmediateSourcePreferredNameColumnValues(ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName());
		}
		if (this.isColumnVisible(visibleColumns, ColumnLabels.IMMEDIATE_SOURCE_GID.getName())) {
			this.valuesGenerator.setImmediateSourceGidColumnValues(ColumnLabels.IMMEDIATE_SOURCE_GID.getName());
		}

		// Check if any of the columns are attribute types
		final Map<String, Integer> attributeTypesMap = this.getAllAttributeTypesMap();
		for (final Object column : visibleColumns) {
			final String columnName = column.toString().toUpperCase();
			final Integer attributeTypeId = attributeTypesMap.get(columnName);
			if (attributeTypeId != null) {
				this.valuesGenerator.fillWithAttribute(attributeTypeId, columnName);
			}
		}

		// Check if any of the columns are name types
		final Map<String, Integer> nameTypesMap = this.getAllNameTypesMap();
		for (final Object column : visibleColumns) {
			final String columnName = column.toString().toUpperCase();
			final Integer nameTypeId = nameTypesMap.get(columnName);
			if (nameTypeId != null) {
				this.valuesGenerator.fillWithGermplasmName(nameTypeId, columnName);
			}
		}
	}

	Map<String, Integer> getAllAttributeTypesMap() {
		final VariableFilter variableFilter = new VariableFilter();
		variableFilter.addVariableType(VariableType.GERMPLASM_ATTRIBUTE);
		variableFilter.addVariableType(VariableType.GERMPLASM_PASSPORT);
		return this.ontologyVariableDataManager.getWithFilter(variableFilter).stream()
			.collect(Collectors.toMap(v -> v.getName().toUpperCase(),
				Variable::getId));
	}

	Map<String, Integer> getAllNameTypesMap() {
		final Map<String, Integer> nameTypesMap = new HashMap<>();
		final List<UserDefinedField> nameTypes = this.germplasmListManager.getGermplasmNameTypes();
		for (final UserDefinedField attributeType : nameTypes) {
			nameTypesMap.put(attributeType.getFname().toUpperCase(), attributeType.getFldno());
		}
		return nameTypesMap;
	}

	private boolean isColumnVisible(final Object[] columns, final String columnName) {
		for (final Object col : columns) {
			if (col.equals(columnName)) {
				return true;
			}
		}
		return false;
	}

	public void setOntologyVariableDataManager(
		final OntologyVariableDataManager ontologyVariableDataManager) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
	}

	public void setValuesGenerator(final GermplasmColumnValuesGenerator valuesGenerator) {
		this.valuesGenerator = valuesGenerator;
	}

	public void setGermplasmListManager(final GermplasmListManager germplasmListManager) {
		this.germplasmListManager = germplasmListManager;
	}

}

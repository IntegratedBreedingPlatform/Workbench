package org.generationcp.breeding.manager.listmanager.util;

import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import org.apache.commons.collections.CollectionUtils;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.pojo.ExportColumnHeader;
import org.generationcp.commons.pojo.ExportRow;
import org.generationcp.commons.pojo.GermplasmListExportInputValues;
import org.generationcp.commons.pojo.GermplasmParents;
import org.generationcp.commons.service.GermplasmExportService;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.service.api.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configurable
public class GermplasmListExporter {

	private static final String FEMALE_PARENT = "FEMALE PARENT";

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListExporter.class);

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private OntologyMethodDataManager ontologyMethodDataManager;

	@Autowired
	private OntologyPropertyDataManager ontologyPropertyDataManager;

	@Autowired
	private OntologyScaleDataManager ontologyScaleDataManager;

	@Autowired
	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Resource
	private ContextUtil contextUtil;

	@Resource
	private GermplasmExportService germplasmExportService;

	@Resource
	private UserService userService;

	private List<String> visibleColumnList;

    public GermplasmListExporter() {
    }

	public FileOutputStream exportKBioScienceGenotypingOrderXLS(final int germplasmListID, final String filename, final int plateSize) throws GermplasmListExporterException {

		final List<ExportColumnHeader> exportColumnHeaders = this.getColumnHeadersForGenotypingData(plateSize);
		final List<ExportRow> exportRows = this.getColumnValuesForGenotypingData(germplasmListID, plateSize);

		try {
			return this.germplasmExportService.generateExcelFileForSingleSheet(exportRows, exportColumnHeaders, filename, "List");
		} catch (final IOException e) {
			throw new GermplasmListExporterException("Error with writing to: " + filename, e);
		}
	}

	protected List<ExportColumnHeader> getColumnHeadersForGenotypingData(final int plateSize) {
		// generate columns headers
		final List<ExportColumnHeader> exportColumnHeaders = new ArrayList<>();
		exportColumnHeaders.add(new ExportColumnHeader(0, "Subject ID", true));
		exportColumnHeaders.add(new ExportColumnHeader(1, "Plate ID", true));
		exportColumnHeaders.add(new ExportColumnHeader(2, "Well", true));
		exportColumnHeaders.add(new ExportColumnHeader(3, "Sample type", true));
		exportColumnHeaders.add(new ExportColumnHeader(4, String.valueOf(plateSize), true));
		exportColumnHeaders.add(new ExportColumnHeader(5, "Primer", true));
		exportColumnHeaders.add(new ExportColumnHeader(6, "Subject BC", true));
		exportColumnHeaders.add(new ExportColumnHeader(7, "Plate BC", true));
		return exportColumnHeaders;
	}

	protected List<ExportRow> getColumnValuesForGenotypingData(final int germplasmListID, final int plateSize) throws GermplasmListExporterException {

		final List<ExportRow> exportRows = new ArrayList<>();

		final GermplasmList germplasmList = this.getGermplasmListAndListData(germplasmListID);
		final String listName = germplasmList.getName();

		final List<GermplasmListData> listDatas = germplasmList.getListData();

		String plateName = listName;
		int plateNum = 0;
		if (plateSize == 96 && listDatas.size() > 95) {
			plateNum = 1;
			plateName = plateName + "-" + plateNum;
		}

		final String[] wellLetters = {"A", "B", "C", "D", "E", "F", "G", "H"};
		int wellLetterIndex = 0;
		int wellNumberIndex = 1;
		for (final GermplasmListData listData : listDatas) {
			if (wellLetterIndex == 7 && wellNumberIndex == 12) {
				// skip H12
				wellLetterIndex = 0;
				wellNumberIndex = 1;
				if (plateNum != 0) {
					plateNum++;
					plateName = listName + "-" + plateNum;
				}
			}

			if (wellNumberIndex == 13) {
				wellLetterIndex++;
				wellNumberIndex = 1;
			}

			String well = wellLetters[wellLetterIndex];
			if (wellNumberIndex < 10) {
				well = well + "0" + wellNumberIndex;
			} else {
				well = well + wellNumberIndex;
			}

			final ExportRow exportRow = new ExportRow();
			exportRow.addColumnValue(0, listData.getEntryId().toString());
			exportRow.addColumnValue(1, plateName);
			exportRow.addColumnValue(2, well);
			exportRow.addColumnValue(3, null);
			exportRow.addColumnValue(4, null);
			exportRow.addColumnValue(5, null);
			exportRow.addColumnValue(6, null);
			exportRow.addColumnValue(7, null);

			exportRows.add(exportRow);

			wellNumberIndex++;
		}
		return exportRows;
	}

	public FileOutputStream exportGermplasmListXLS(final int germplasmListID, final String fileName, final Table listDataTable) throws GermplasmListExporterException {
		final GermplasmListNewColumnsInfo currentColumnsInfo = this.germplasmListManager.getAdditionalColumnsForList(germplasmListID);
		final int currentWorkbenchUserId = this.contextUtil.getCurrentWorkbenchUserId();

		final GermplasmListExportInputValues input = new GermplasmListExportInputValues();
		input.setFileName(fileName);

		final GermplasmList germplasmList = this.getGermplasmListAndListData(germplasmListID);

		input.setGermplasmList(germplasmList);

		input.setListData(germplasmList.getListData());

		input.setOwnerName(this.userService.getPersonNameForUserId(germplasmList.getUserId()));

		input.setCurrentLocalIbdbUserId(currentWorkbenchUserId);

		input.setExporterName(this.userService.getPersonNameForUserId(currentWorkbenchUserId));

		input.setVisibleColumnMap(this.getVisibleColumnMap(listDataTable));

		input.setColumnTermMap(this.getOntologyTermMap(listDataTable));

		input.setInventoryVariableMap(this.getInventoryVariables());

		input.setVariateVariableMap(this.getVariateVariables());

		input.setGermplasmParents(this.getGermplasmParentsMap(listDataTable));

		input.setCurrentColumnsInfo(currentColumnsInfo);

		return this.germplasmExportService.generateGermplasmListExcelFile(input);
	}

	@SuppressWarnings("unchecked")
	private Map<Integer, GermplasmParents> getGermplasmParentsMap(final Table listDataTable) {
		final Map<Integer, GermplasmParents> germplasmParentsMap = new HashMap<>();

		final List<Integer> itemIds = new ArrayList<>((Collection<? extends Integer>) listDataTable.getItemIds());

		if (this.hasParentsColumn(listDataTable)) {
			for (final Integer itemId : itemIds) {
				final Button femaleParentButton =
						(Button) listDataTable.getItem(itemId).getItemProperty(ColumnLabels.FEMALE_PARENT.getName()).getValue();
				final String femaleParentName = femaleParentButton.getCaption();

				final Button maleParentButton =
						(Button) listDataTable.getItem(itemId).getItemProperty(ColumnLabels.MALE_PARENT.getName()).getValue();
				final String maleParentName = maleParentButton.getCaption();

				final Button fgidButton = (Button) listDataTable.getItem(itemId).getItemProperty(ColumnLabels.FGID.getName()).getValue();
				final Integer fgid = Integer.valueOf(fgidButton.getCaption());

				final Button mgidButton = (Button) listDataTable.getItem(itemId).getItemProperty(ColumnLabels.MGID.getName()).getValue();
				final Integer mgid = Integer.valueOf(mgidButton.getCaption());

				final Button gidButton = (Button) listDataTable.getItem(itemId).getItemProperty(ColumnLabels.GID.getName()).getValue();
				final Integer gid = Integer.valueOf(gidButton.getCaption());

				germplasmParentsMap.put(gid, new GermplasmParents(gid, femaleParentName, maleParentName, fgid, mgid));
			}
		}

		return germplasmParentsMap;
	}

	protected boolean hasParentsColumn(final Table listDataTable) {
		final String[] columnHeaders = listDataTable.getColumnHeaders();

        for (final String columnHeader : columnHeaders) {
            // only checks if the existence of the female parent to determine if the export came from crossing manager
            if (columnHeader.equals(GermplasmListExporter.FEMALE_PARENT)) {
                return true;
            }
        }

		return false;
	}

	protected GermplasmList getGermplasmListAndListData(final Integer listId) throws GermplasmListExporterException {
		final GermplasmList germplasmList;
		try {
			germplasmList = this.germplasmListManager.getGermplasmListById(listId);
			this.inventoryDataManager.populateLotCountsIntoExistingList(germplasmList);
		} catch (final MiddlewareQueryException e) {
			throw new GermplasmListExporterException("Error with getting Germplasm List with id: " + listId, e);
		}
		return germplasmList;
	}

	protected Map<String, Boolean> getVisibleColumnMap(final Table listDataTable) {

		final Map<String, Boolean> columnHeaderMap = new HashMap<>();

		final Collection<?> columnHeaders = listDataTable.getContainerPropertyIds();
		final Object[] visibleColumns = listDataTable.getVisibleColumns();

		// change the visibleColumns array to list
		this.visibleColumnList = new ArrayList<>();
		for (final Object column : visibleColumns) {
			if (!listDataTable.isColumnCollapsed(column)) {
				this.visibleColumnList.add(column.toString());
			}
		}

		for (final Object column : columnHeaders) {
			String key = column.toString();
			final ColumnLabels columnLabel = ColumnLabels.get(column.toString());
			if (columnLabel != null && columnLabel.getTermId() != null) {
				key = String.valueOf(columnLabel.getTermId().getId());
			}

			// always set to true for required columns
			if (ColumnLabels.ENTRY_ID.getName().equalsIgnoreCase(column.toString())
					|| ColumnLabels.GID.getName().equalsIgnoreCase(column.toString())
					|| ColumnLabels.DESIGNATION.getName().equalsIgnoreCase(column.toString())) {
				columnHeaderMap.put(key, true);
			} else {
				columnHeaderMap.put(key, this.visibleColumnList.contains(column.toString()));
			}

		}

		return columnHeaderMap;
	}

	protected Map<Integer, Term> getOntologyTermMap(final Table listDataTable) {

		final Map<Integer, Term> columnTermMap = new HashMap<>();
		final Collection<?> columnHeaders = listDataTable.getContainerPropertyIds();

		for (final Object column : columnHeaders) {
			final String columnHeader = column.toString();
			final ColumnLabels columnLabel = ColumnLabels.get(columnHeader);
			if (columnLabel != null && columnLabel.getTermId() != null) {
				this.addOntologyTermToMap(columnTermMap, columnLabel.getTermId().getId());
			}
		}

		return columnTermMap;
	}

	protected Map<Integer, Variable> getInventoryVariables() {

		final Map<Integer, Variable> variableMap = new HashMap<>();
		this.addVariableToMap(variableMap, TermId.SEED_AMOUNT_G.getId());
		return variableMap;
	}


	protected Map<Integer, Variable> getVariateVariables() {

		final Map<Integer, Variable> variableMap = new HashMap<>();
		this.addVariableToMap(variableMap, TermId.NOTES.getId());
		return variableMap;

	}

	private void addVariableToMap(final Map<Integer, Variable> variableMap, final int termId) {

		try {
			final Variable variable =
					this.ontologyVariableDataManager.getVariable(this.contextUtil.getCurrentProgramUUID(), termId, false);
			if (variable != null) {
				variableMap.put(variable.getId(), variable);
			}

		} catch (final MiddlewareQueryException e) {
			GermplasmListExporter.LOG.error(e.getMessage(), e);
		}
	}

	private void addOntologyTermToMap(final Map<Integer, Term> termMap, final int termId) {

		try {
			// Term should exist with that id in database.
			final Term term = this.ontologyDataManager.getTermById(termId);

			GermplasmListExporter.LOG.debug("Finding term with id:" + termId + ". Found: " + (term != null));

			if (term == null) {
				throw new MiddlewareException("Term does not exist with id:" + termId);
			}

			final CvId cvId = CvId.valueOf(term.getVocabularyId());

			if (Objects.equals(cvId, CvId.IBDB_TERMS)) {
				termMap.put(term.getId(), term);
			} else if (Objects.equals(cvId, CvId.METHODS)) {
				termMap.put(term.getId(), this.ontologyMethodDataManager.getMethod(term.getId(), false));
			} else if (Objects.equals(cvId, CvId.PROPERTIES)) {
				termMap.put(term.getId(), this.ontologyPropertyDataManager.getProperty(term.getId(), false));
			} else if (Objects.equals(cvId, CvId.SCALES)) {
				termMap.put(term.getId(), this.ontologyScaleDataManager.getScaleById(term.getId(), false));
			} else {
				termMap.put(term.getId(),
						this.ontologyVariableDataManager.getVariable(this.contextUtil.getCurrentProgramUUID(), term.getId(), false));
			}
		} catch (final MiddlewareQueryException e) {
			GermplasmListExporter.LOG.error(e.getMessage(), e);
		}
	}

	public void exportGermplasmListCSV(final String fileName, final Table listDataTable, final Integer germplasmListId) throws GermplasmListExporterException {

		final GermplasmListNewColumnsInfo currentColumnsInfo = this.germplasmListManager.getAdditionalColumnsForList(germplasmListId);
		final List<ExportRow> exportColumnValues = this.getExportColumnValuesFromTable(listDataTable, currentColumnsInfo);
		final List<ExportColumnHeader> exportColumnHeaders = this.getExportColumnHeadersFromTable(listDataTable, currentColumnsInfo);

		try {

			this.germplasmExportService.generateCSVFile(exportColumnValues, exportColumnHeaders, fileName);

		} catch (final IOException e) {
			throw new GermplasmListExporterException("Error with exporting list to CSV File.", e);
		}

	}

	protected List<ExportColumnHeader> getExportColumnHeadersFromTable(final Table listDataTable,
		final GermplasmListNewColumnsInfo currentColumnsInfo) {

		final Map<String, Boolean> visibleColumns = this.getVisibleColumnMap(listDataTable);

		final List<ExportColumnHeader> exportColumnHeaders = new ArrayList<>();
		int colIndex = 0;

		exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.ENTRY_ID), visibleColumns.get(String
				.valueOf(ColumnLabels.ENTRY_ID.getTermId().getId()))));

		exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.GID), visibleColumns.get(String
				.valueOf(ColumnLabels.GID.getTermId().getId()))));

		exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.ENTRY_CODE), visibleColumns.get(String
				.valueOf(ColumnLabels.ENTRY_CODE.getTermId().getId()))));

		exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.DESIGNATION), visibleColumns.get(String
				.valueOf(ColumnLabels.DESIGNATION.getTermId().getId()))));

		exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.PARENTAGE), visibleColumns.get(String
				.valueOf(ColumnLabels.PARENTAGE.getTermId().getId()))));

		exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.SEED_SOURCE), visibleColumns.get(String
				.valueOf(ColumnLabels.SEED_SOURCE.getTermId().getId()))));

		colIndex = this.addAddedColumnsHeaders(currentColumnsInfo, exportColumnHeaders, colIndex);
		colIndex = this.addAttributeAndNameTypeHeaders(currentColumnsInfo, exportColumnHeaders, colIndex);

		return exportColumnHeaders;
	}

	private int addAddedColumnsHeaders(final GermplasmListNewColumnsInfo columnsInfo, final List<ExportColumnHeader> exportColumnHeaders,
		int colIndex) {

		final Map<String, Map<Integer, ListDataColumnValues>> valuesMap = columnsInfo.getColumnValuesByListDataIdMap();

		if (valuesMap == null) {
			return colIndex;
		}

		if (valuesMap.containsKey(ColumnLabels.PREFERRED_ID.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.PREFERRED_ID), true));
		}

		if (valuesMap.containsKey(ColumnLabels.PREFERRED_NAME.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.PREFERRED_NAME), true));
		}

		if (valuesMap.containsKey(ColumnLabels.GERMPLASM_DATE.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.GERMPLASM_DATE), true));
		}

		if (valuesMap.containsKey(ColumnLabels.GERMPLASM_LOCATION.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.GERMPLASM_LOCATION), true));
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_NAME.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.BREEDING_METHOD_NAME), true));
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.BREEDING_METHOD_ABBREVIATION), true));
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_NUMBER.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.BREEDING_METHOD_NUMBER), true));
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_GROUP.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.BREEDING_METHOD_GROUP), true));
		}

		if (valuesMap.containsKey(ColumnLabels.FGID.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.FGID), true));
		}

		if (valuesMap.containsKey(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME), true));
		}

		if (valuesMap.containsKey(ColumnLabels.MGID.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.MGID), true));
		}

		if (valuesMap.containsKey(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.CROSS_MALE_PREFERRED_NAME), true));
		}

		if (valuesMap.containsKey(ColumnLabels.GROUP_SOURCE_GID.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.GROUP_SOURCE_GID), true));
		}

		if (valuesMap.containsKey(ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.GROUP_SOURCE_PREFERRED_NAME), true));
		}

		if (valuesMap.containsKey(ColumnLabels.IMMEDIATE_SOURCE_GID.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.IMMEDIATE_SOURCE_GID), true));
		}

		if (valuesMap.containsKey(ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName())) {
			exportColumnHeaders.add(new ExportColumnHeader(colIndex++, this.getTermNameFromOntology(ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME), true));
		}

		return colIndex;
	}

	protected int addAttributeAndNameTypeHeaders(final GermplasmListNewColumnsInfo currentColumnsInfo,
		final List<ExportColumnHeader> exportColumnHeaders, int colIndex) {

		if (currentColumnsInfo != null && !currentColumnsInfo.getColumns().isEmpty()) {
			final List<UserDefinedField> nameTypes = this.germplasmListManager.getGermplasmNameTypes();
			final Map<String, String> nameTypesNameToCodeMap = new HashMap<>();
			for(final UserDefinedField nameType: nameTypes) {
				nameTypesNameToCodeMap.put(nameType.getFname().toUpperCase(), nameType.getFcode());
			}
			for (final String column : currentColumnsInfo.getColumns()) {
				if(ColumnLabels.get(column) == null) {
					final String columnHeader = nameTypesNameToCodeMap.get(column) != null ? nameTypesNameToCodeMap.get(column.toUpperCase()) : column;
					exportColumnHeaders.add(new ExportColumnHeader(colIndex++, columnHeader, true));
				}
			}
		}
		return colIndex;
	}

	protected List<ExportRow> getExportColumnValuesFromTable(final Table listDataTable,
		final GermplasmListNewColumnsInfo currentColumnsInfo) {

		final Map<String, Boolean> visibleColumnMap = this.getVisibleColumnMap(listDataTable);
		final Map<String, Map<Integer, ListDataColumnValues>> columnValuesByListDataIdByName = currentColumnsInfo.getColumnValuesByListDataIdMap();

		final List<ExportRow> exportRows = new ArrayList<>();
		for (final Object itemId : listDataTable.getItemIds()) {
			final ExportRow row = new ExportRow();

			final String entryIdValue = listDataTable.getItem(itemId).getItemProperty(ColumnLabels.ENTRY_ID.getName()).getValue().toString();
			final String gidValue = ((Button) listDataTable.getItem(itemId).getItemProperty(ColumnLabels.GID.getName()).getValue()).getCaption();
			final String entryCodeValue = listDataTable.getItem(itemId).getItemProperty(ColumnLabels.ENTRY_CODE.getName()).getValue().toString();
			final String designationValue =
					((Button) listDataTable.getItem(itemId).getItemProperty(ColumnLabels.DESIGNATION.getName()).getValue()).getCaption();
			final String parentageValue = listDataTable.getItem(itemId).getItemProperty(ColumnLabels.PARENTAGE.getName()).getValue().toString();
			final String seedSourceValue =
					listDataTable.getItem(itemId).getItemProperty(ColumnLabels.SEED_SOURCE.getName()).getValue().toString();

			int colIndex = 0;
			row.addColumnValue(colIndex++, entryIdValue);
			row.addColumnValue(colIndex++, gidValue);
			row.addColumnValue(colIndex++, entryCodeValue);
			row.addColumnValue(colIndex++, designationValue);
			row.addColumnValue(colIndex++, parentageValue);
			row.addColumnValue(colIndex++, seedSourceValue);

			colIndex = this.addAddedColumnsValues(columnValuesByListDataIdByName, itemId, row, colIndex);
			colIndex = this.addAttributeAndNameTypeValues(currentColumnsInfo, itemId, row, colIndex);
			exportRows.add(row);
		}

		return exportRows;
	}

	private int addAddedColumnsValues(
		final Map<String, Map<Integer, ListDataColumnValues>> valuesMap,
		final Object itemId,
		final ExportRow row,
		int colIndex) {

		if (valuesMap == null) {
			return colIndex;
		}

		if (valuesMap.containsKey(ColumnLabels.PREFERRED_ID.getName())) {
			final String value = valuesMap.get(ColumnLabels.PREFERRED_ID.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.PREFERRED_NAME.getName())) {
			final String value = valuesMap.get(ColumnLabels.PREFERRED_NAME.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.GERMPLASM_DATE.getName())) {
			final String value = valuesMap.get(ColumnLabels.GERMPLASM_DATE.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.GERMPLASM_LOCATION.getName())) {
			final String value = valuesMap.get(ColumnLabels.GERMPLASM_LOCATION.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_NAME.getName())) {
			final String value = valuesMap.get(ColumnLabels.BREEDING_METHOD_NAME.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName())) {
			final String value = valuesMap.get(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_NUMBER.getName())) {
			final String value = valuesMap.get(ColumnLabels.BREEDING_METHOD_NUMBER.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.BREEDING_METHOD_GROUP.getName())) {
			final String value = valuesMap.get(ColumnLabels.BREEDING_METHOD_GROUP.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.FGID.getName())) {
			final String value = valuesMap.get(ColumnLabels.FGID.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName())) {
			final String value = valuesMap.get(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.MGID.getName())) {
			final String value = valuesMap.get(ColumnLabels.MGID.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName())) {
			final String value = valuesMap.get(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.GROUP_SOURCE_GID.getName())) {
			final String value = valuesMap.get(ColumnLabels.GROUP_SOURCE_GID.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName())) {
			final String value = valuesMap.get(ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.IMMEDIATE_SOURCE_GID.getName())) {
			final String value = valuesMap.get(ColumnLabels.IMMEDIATE_SOURCE_GID.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		if (valuesMap.containsKey(ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName())) {
			final String value = valuesMap.get(ColumnLabels.IMMEDIATE_SOURCE_PREFERRED_NAME.getName()).get(itemId).getValue();
			row.addColumnValue(colIndex++, value);
		}

		return colIndex;
	}

	protected int addAttributeAndNameTypeValues(final GermplasmListNewColumnsInfo currentColumnsInfo, final Object itemId,
			final ExportRow row, int colIndex) {
		if (currentColumnsInfo != null && !currentColumnsInfo.getColumns().isEmpty()) {
			for(final String column : currentColumnsInfo.getColumns()){
				if(ColumnLabels.get(column) == null) {
					final List<ListDataColumnValues> columnValues = currentColumnsInfo.getColumnValuesMap().get(column);
					final ListDataColumnValues listDataColumnValues =
						(ListDataColumnValues) CollectionUtils.find(columnValues, new org.apache.commons.collections.Predicate() {

							public boolean evaluate(final Object object) {
								return ((ListDataColumnValues) object).getListDataId().equals(itemId);
							}
						});
					final String value = (listDataColumnValues != null ? listDataColumnValues.getValue() : "");
					row.addColumnValue(colIndex++, value);
				}
			}
		}
		return colIndex;
	}

	protected void setGermplasmExportService(final GermplasmExportService germplasmExportService) {
		this.germplasmExportService = germplasmExportService;
	}

	protected void setGermplasmListManager(final GermplasmListManager germplasmListManager) {
		this.germplasmListManager = germplasmListManager;
	}

	protected void setOntologyDataManager(final OntologyDataManager ontologyDataManager) {
		this.ontologyDataManager = ontologyDataManager;
	}

	protected void setInventoryDataManager(final InventoryDataManager inventoryDataManager) {
		this.inventoryDataManager = inventoryDataManager;
	}

	protected void setOntologyVariableDataManager(final OntologyVariableDataManager ontologyVariableDataManager) {
		this.ontologyVariableDataManager = ontologyVariableDataManager;
	}

	protected String getTermNameFromOntology(final ColumnLabels columnLabel) {
		return columnLabel.getTermNameFromOntology(this.ontologyDataManager);
	}

}

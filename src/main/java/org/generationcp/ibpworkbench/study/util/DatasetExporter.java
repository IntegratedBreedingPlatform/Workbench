
package org.generationcp.ibpworkbench.study.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFName;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.study.constants.StudyTemplateConstants;
import org.generationcp.ibpworkbench.util.Util;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.util.PoiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Configurable
public class DatasetExporter {

	private static final Logger LOG = LoggerFactory.getLogger(DatasetExporter.class);

	private static final int CONDITION_LIST_HEADER_ROW_INDEX = 8;

	private final StudyDataManager studyDataManager;
	private final Integer studyId;
	private final Integer datasetId;
	private final List<String> factors = new ArrayList<>();
	private final List<String> variates = new ArrayList<>();
	private HSSFCellStyle labelStyle;
	private HSSFCellStyle headingStyle;
	private HSSFCellStyle variateHeadingStyle;
	private int observationSheetColumnIndex;

	@Autowired
	private ContextUtil contextUtil;

	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	public DatasetExporter(final StudyDataManager studyDataManager, final Integer studyId, final Integer datasetId) {
		this.studyDataManager = studyDataManager;
		this.studyId = studyId;
		this.datasetId = datasetId;
	}

	public String exportToFieldBookExcelUsingIBDBv2(final String filenameWithoutExtension) throws DatasetExporterException {

		if (this.studyDataManager == null) {
			throw new DatasetExporterException("studyDataManager should not be null.");
		}

		// create workbook
		final HSSFWorkbook workbook = new HSSFWorkbook();
		final HSSFCellStyle cellStyleForObservationSheet = workbook.createCellStyle();

		this.initializeCellStyles(workbook);

		// create two sheets, one for description and another for measurements
		final HSSFSheet descriptionSheet = workbook.createSheet(StudyTemplateConstants.DESCRIPTION_TAB.getHeader());
		final HSSFSheet observationSheet = workbook.createSheet(StudyTemplateConstants.OBSERVATION_TAB.getHeader());

		this.observationSheetColumnIndex = 0;

		// write the details on the first sheet - description
		// get the study first
		Study study = null;

		try {
			study = this.studyDataManager.getStudy(this.studyId);
		} catch (final MiddlewareException ex) {
			throw new DatasetExporterException("Error with getting Study with id: " + this.studyId, ex);
		}

		if (study != null) {
			// get the needed study details
			final String name = study.getName();

			this.createDescriptionSheet(descriptionSheet, study, name);

			// populate the measurements sheet
			this.createObservationSheet(cellStyleForObservationSheet, observationSheet, name);

			this.adjustColumnWidths(descriptionSheet, observationSheet);

			this.traceSheet(study.getName(), observationSheet);
		}

		return this.writeExcelFile(filenameWithoutExtension, workbook);
	}

	/*
	 * Generation the Observation sheet in the file. It contains the factors and variates and their values across the experiments
	 */
	private void createObservationSheet(final HSSFCellStyle cellStyleForObservationSheet, final HSSFSheet observationSheet,
			final String name) throws DatasetExporterException {
		// establish the columns of the dataset first
		this.createObservationSheetHeaderRow(observationSheet);

		// then work with the data - do it by 1500 rows at a time
		// changed from 50 to 1500 (17-JUL-2015 BMS-805) to reduce unnecessary trips to the DB
		final int pageSize = 1500;
		long totalNumberOfRows = 0;
		int sheetRowIndex = 1;

		try {
			totalNumberOfRows = this.studyDataManager.countExperiments(this.datasetId);
		} catch (final Exception ex) {
			throw new DatasetExporterException(
					"Error with getting count of experiments for study - " + name + ", dataset - " + this.datasetId, ex);
		}

		for (int start = 0; start < totalNumberOfRows; start = start + pageSize) {
			List<Experiment> experiments = new ArrayList<>();
			try {
				experiments = this.studyDataManager.getExperiments(this.datasetId, start, pageSize);
			} catch (final Exception ex) {
				throw new DatasetExporterException(
						"Error with getting ounit ids of study - " + name + ", representation - " + this.datasetId, ex);
			}

			// map each experiment into a row in the observation sheet
			sheetRowIndex = this.writeExperiments(cellStyleForObservationSheet, observationSheet, sheetRowIndex, experiments);
		}
	}

	/*
	 * Create the header row in Observation sheet containing factors and variates names
	 */
	private void createObservationSheetHeaderRow(final HSSFSheet observationSheet) {
		final HSSFRow datasetHeaderRow = observationSheet.createRow(0);
		for (int i = 0; i < this.factors.size(); i++) {
			final String columnName = this.factors.get(i);
			final HSSFCell cell = datasetHeaderRow.createCell(i);
			cell.setCellValue(columnName);
			cell.setCellStyle(this.headingStyle);
		}
		final int startColumn = this.factors.size();
		for (int i = 0; i < this.variates.size(); i++) {
			final String columnName = this.variates.get(i);
			final HSSFCell cell = datasetHeaderRow.createCell(i + startColumn);
			cell.setCellValue(columnName);
			cell.setCellStyle(this.variateHeadingStyle);
		}
	}

	/*
	 * Adjust column widths of description sheet to fit contents
	 */
	private void adjustColumnWidths(final HSSFSheet descriptionSheet, final HSSFSheet observationSheet) {
		for (int ctr = 0; ctr < 8; ctr++) {
			if (ctr != 1) {
				descriptionSheet.autoSizeColumn(ctr);
			}
		}

		for (int ctr = 0; ctr < this.observationSheetColumnIndex; ctr++) {
			observationSheet.autoSizeColumn(ctr);
		}
	}

	String writeExcelFile(final String filenameWithoutExtension, final HSSFWorkbook workbook) throws DatasetExporterException {
		try {
			final String fileNameUnderWorkspaceDirectory = this.installationDirectoryUtil.getTempFileInOutputDirectoryForProjectAndTool(
					filenameWithoutExtension, ".xls", this.contextUtil.getProjectInContext(), ToolName.STUDY_BROWSER);
			final FileOutputStream fileOutputStream = new FileOutputStream(fileNameUnderWorkspaceDirectory);
			workbook.write(fileOutputStream);
			fileOutputStream.close();

			return fileNameUnderWorkspaceDirectory;
		} catch (final Exception ex) {
			throw new DatasetExporterException("Error with writing to: " + filenameWithoutExtension, ex);
		}
	}

	/*
	 * For all experiments and dataset, write the factors and variates values in the Observation sheet
	 */
	@SuppressWarnings("deprecation")
	private int writeExperiments(final HSSFCellStyle cellStyleForObservationSheet, final HSSFSheet observationSheet,
			final int sheetRowIndex, final List<Experiment> experiments) {
		int newSheetRowIndex = sheetRowIndex;
		for (final Experiment experiment : experiments) {
			final HSSFRow row = observationSheet.createRow(newSheetRowIndex);
			newSheetRowIndex++;

			final List<Variable> factorsOfExperiments = experiment.getFactors().getVariables();
			for (final Variable factorVariable : factorsOfExperiments) {
				String factorName = factorVariable.getVariableType().getLocalName();
				if (factorName != null) {
					factorName = factorName.trim();
				}
				final Integer columnIndexInteger = this.factors.indexOf(factorName);
				if (columnIndexInteger != null) {
					final short columnIndex = columnIndexInteger.shortValue();
					if (columnIndex >= 0) {
						final HSSFCell cell = row.createCell(columnIndex);
						final boolean isNumeric =
								Util.isNumericVariable(factorVariable.getVariableType().getStandardVariable().getDataType().getId());
						if (isNumeric) {
							double elemValue = 0;
							if (factorVariable.getValue() != null) {
								try {
									elemValue = Double.valueOf(factorVariable.getValue());
									cell.setCellValue(elemValue);
								} catch (final NumberFormatException ex) {
									String value = factorVariable.getValue();
									if (value != null) {
										value = value.trim();
									}
									cell.setCellValue(value);
								}
							} else {
								final String nullValue = null;
								cell.setCellValue(nullValue);
							}
						} else {
							String value = factorVariable.getDisplayValue();
							if (value != null) {
								value = value.trim();
							}
							cell.setCellValue(value);
						}
					}
				}
			}

			final List<Variable> variateVariables = experiment.getVariates().getVariables();
			for (final Variable variateVariable : variateVariables) {
				String variateName = variateVariable.getVariableType().getLocalName();
				if (variateName != null) {
					variateName = variateName.trim();
				}
				Integer columnIndexInteger = this.variates.indexOf(variateName);
				if (columnIndexInteger != null) {
					columnIndexInteger += this.factors.size();
					final short columnIndex = columnIndexInteger.shortValue();
					if (columnIndex >= 0) {
						final Cell cell = PoiUtil.createCell(cellStyleForObservationSheet, row, columnIndex, CellStyle.ALIGN_CENTER,
								CellStyle.ALIGN_CENTER);
						final boolean isNumeric =
								Util.isNumericVariable(variateVariable.getVariableType().getStandardVariable().getDataType().getId());
						if (isNumeric) {
							double elemValue = 0;
							if (variateVariable.getValue() != null) {
								try {
									elemValue = Double.valueOf(variateVariable.getDisplayValue());
									cell.setCellValue(elemValue);
								} catch (final NumberFormatException ex) {
									String value = variateVariable.getValue();
									if (value != null) {
										value = value.trim();
									}
									cell.setCellValue(value);
								}
							} else {
								final String nullValue = null;
								cell.setCellValue(nullValue);
							}
						} else {
							String value = variateVariable.getDisplayValue();
							if (value != null) {
								value = value.trim();
							}
							cell.setCellValue(value);
						}
					}
				}
			}
		}
		return newSheetRowIndex;
	}

	/*
	 * Create description sheet containing the Study Details, Conditions, Factors, Constants and Variates sections
	 */
	private void createDescriptionSheet(final HSSFSheet descriptionSheet, final Study study, final String name)
			throws DatasetExporterException {
		this.createStudyDetailsSection(this.labelStyle, descriptionSheet, study, name);

		final int conditionRowIndex = this.createConditionsSection(this.headingStyle, descriptionSheet, study);

		DataSet dataset = null;
		try {
			dataset = this.studyDataManager.getDataSet(this.datasetId);
		} catch (final MiddlewareException ex) {
			throw new DatasetExporterException("Error with getting Dataset with id: " + this.datasetId, ex);
		}

		// get the factors and their details
		final VariableTypeList datasetVariableTypes = dataset.getVariableTypes();
		final int factorRowIndex = this.createFactorsSection(descriptionSheet, conditionRowIndex, datasetVariableTypes);

		// row with headings for variate list
		final int constantHeaderRowIndex = factorRowIndex + 1;
		this.createSectionHeaderRow(this.variateHeadingStyle, descriptionSheet, StudyTemplateConstants.getConstantsHeaders(),
				constantHeaderRowIndex);
		final int constantRowIndex = constantHeaderRowIndex + 1;
		descriptionSheet.createRow(constantRowIndex);

		final int variateHeaderRowIndex = constantRowIndex + 1;
		this.createVariatesSection(descriptionSheet, datasetVariableTypes, variateHeaderRowIndex);
	}

	private void createVariatesSection(final HSSFSheet descriptionSheet, final VariableTypeList datasetVariableTypes,
			final int variateHeaderRowIndex) {
		this.createSectionHeaderRow(this.variateHeadingStyle, descriptionSheet, StudyTemplateConstants.getVariateHeaders(),
				variateHeaderRowIndex);

		// get the variates and their details
		final VariableTypeList variateVariableTypeList = datasetVariableTypes.getVariates();
		final List<DMSVariableType> variateVariableTypes = variateVariableTypeList.getVariableTypes();

		this.populateVariates(descriptionSheet, variateHeaderRowIndex, variateVariableTypes);
	}

	private void populateVariates(final HSSFSheet descriptionSheet, final int variateHeaderRowIndex,
			final List<DMSVariableType> variateVariableTypes) {
		int variateRowIndex = variateHeaderRowIndex + 1;
		for (final DMSVariableType variate : variateVariableTypes) {
			final String dataType = variate.getStandardVariable().getDataType().getName();
			String variateName = variate.getLocalName();
			if (variateName != null) {
				variateName = variateName.trim();
			}

			final HSSFRow variateRow = descriptionSheet.createRow(variateRowIndex);
			variateRow.createCell(0).setCellValue(variateName);
			if (variate.getLocalDescription() != null && variate.getLocalDescription().length() != 0) {
				variateRow.createCell(1).setCellValue(variate.getLocalDescription().trim());
			} else {
				variateRow.createCell(1).setCellValue(variate.getStandardVariable().getDescription());
			}
			if (variate.getStandardVariable().getProperty() != null) {
				variateRow.createCell(2).setCellValue(variate.getStandardVariable().getProperty().getName());
			} else {
				variateRow.createCell(2).setCellValue(variate.getStandardVariable().getName());
			}
			variateRow.createCell(3).setCellValue(variate.getStandardVariable().getScale().getName());
			variateRow.createCell(4).setCellValue(variate.getStandardVariable().getMethod().getName());
			variateRow.createCell(5).setCellValue(dataType);
			// empty for "Value" column, that's why no index 6
			variateRow.createCell(7).setCellValue(PhenotypicType.TRIAL_DESIGN.getLabelList().get(0));

			this.variates.add(variateName);
			this.observationSheetColumnIndex++;

			variateRowIndex++;
		}
	}

	private int createFactorsSection(final HSSFSheet descriptionSheet, final int conditionRowIndex,
			final VariableTypeList datasetVariableTypes) {
		final VariableTypeList factorVariableTypeList = datasetVariableTypes.getFactors();
		final List<DMSVariableType> factorVariableTypes = factorVariableTypeList.getVariableTypes();

		final int factorRowHeaderIndex = conditionRowIndex + 1;
		this.createSectionHeaderRow(this.headingStyle, descriptionSheet, StudyTemplateConstants.getFactorHeaders(), factorRowHeaderIndex);

		return this.populateFactors(descriptionSheet, factorRowHeaderIndex, factorVariableTypes);
	}

	private int populateFactors(final HSSFSheet descriptionSheet, final int factorRowHeaderIndex,
			final List<DMSVariableType> factorVariableTypes) {
		int factorRowIndex = factorRowHeaderIndex + 1;
		for (final DMSVariableType factor : factorVariableTypes) {
			final StandardVariable standardVariable = factor.getStandardVariable();
			final String dataType = standardVariable.getDataType().getName();
			String factorName = factor.getLocalName();
			if (factorName != null) {
				factorName = factorName.trim();
			}

			// check if factor is already written as a condition
			final Integer temp = this.factors.indexOf(factorName);
			if (temp == -1 && !"STUDY".equals(factorName)) {
				final HSSFRow factorRow = descriptionSheet.createRow(factorRowIndex);
				factorRow.createCell(0).setCellValue(factorName);
				if (factor.getLocalDescription() != null && factor.getLocalDescription().length() != 0) {
					factorRow.createCell(1).setCellValue(factor.getLocalDescription());
				} else {
					factorRow.createCell(1).setCellValue(standardVariable.getDescription());
				}
				if (standardVariable.getProperty() != null) {
					factorRow.createCell(2).setCellValue(standardVariable.getProperty().getName());
				} else {
					factorRow.createCell(2).setCellValue(standardVariable.getName());
				}
				factorRow.createCell(3).setCellValue(standardVariable.getScale().getName());
				factorRow.createCell(4).setCellValue(standardVariable.getMethod().getName());
				factorRow.createCell(5).setCellValue(dataType);
				// empty string for "Value" column - why no index 6

				// for PLOT, ENTRY and TRIAL to set as label value
				final PhenotypicType phenotypicType = standardVariable.getPhenotypicType();
				if (phenotypicType != null && !CollectionUtils.isEmpty(phenotypicType.getLabelList())) {
					factorRow.createCell(7).setCellValue(phenotypicType.getLabelList().get(0));
				}

				this.factors.add(factorName);
				this.observationSheetColumnIndex++;

				factorRowIndex++;
			}
		}
		// empty row
		descriptionSheet.createRow(factorRowIndex);

		return factorRowIndex;
	}

	private void initializeCellStyles(final HSSFWorkbook workbook) {
		final HSSFFont whiteFont = workbook.createFont();
		whiteFont.setColor(new HSSFColor.WHITE().getIndex());
		whiteFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// set cell style for labels in the description sheet
		this.labelStyle = workbook.createCellStyle();
		this.labelStyle.setFillForegroundColor(new HSSFColor.BROWN().getIndex());
		this.labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		this.labelStyle.setFont(whiteFont);

		// set cell style for headings in the description sheet
		this.headingStyle = workbook.createCellStyle();
		this.headingStyle.setFillForegroundColor(new HSSFColor.SEA_GREEN().getIndex());
		this.headingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		this.headingStyle.setFont(whiteFont);

		// set cell style for variate headings in the description sheet
		this.variateHeadingStyle = workbook.createCellStyle();
		this.variateHeadingStyle.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
		this.variateHeadingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		this.variateHeadingStyle.setFont(whiteFont);
	}

	private int createConditionsSection(final HSSFCellStyle headingStyle, final HSSFSheet descriptionSheet, final Study study) {
		// row with headings for condition list
		this.createSectionHeaderRow(headingStyle, descriptionSheet, StudyTemplateConstants.getConditionHeaders(),
				DatasetExporter.CONDITION_LIST_HEADER_ROW_INDEX);

		// get the conditions and their details
		final int conditionRowIndex = this.populateStudyConditions(descriptionSheet, study);

		// empty row
		descriptionSheet.createRow(conditionRowIndex);

		return conditionRowIndex;
	}

	/*
	 * Populates the condition section with the actual study conditions
	 */
	private int populateStudyConditions(final HSSFSheet descriptionSheet, final Study study) {

		final VariableList conditions = study.getConditions();

		int conditionRowIndex = DatasetExporter.CONDITION_LIST_HEADER_ROW_INDEX + 1;
		final List<Variable> conditionVariables = conditions.getVariables();
		for (final Variable conditionVariable : conditionVariables) {
			String conditionName = conditionVariable.getVariableType().getLocalName();
			if (conditionName != null) {
				conditionName = conditionName.trim();
			}
			final Term dataType = conditionVariable.getVariableType().getStandardVariable().getDataType();

			final HSSFRow conditionRow = descriptionSheet.createRow(conditionRowIndex);
			conditionRow.createCell(0).setCellValue(conditionName);
			if (conditionVariable.getVariableType().getLocalDescription() != null
					&& conditionVariable.getVariableType().getLocalDescription().length() != 0) {
				conditionRow.createCell(1).setCellValue(conditionVariable.getVariableType().getLocalDescription());
			} else {
				conditionRow.createCell(1).setCellValue(conditionVariable.getVariableType().getStandardVariable().getDescription());
			}
			if (conditionVariable.getVariableType().getStandardVariable().getProperty() != null) {
				conditionRow.createCell(2).setCellValue(conditionVariable.getVariableType().getStandardVariable().getProperty().getName());
			} else {
				conditionRow.createCell(2).setCellValue(conditionVariable.getVariableType().getStandardVariable().getName());
			}
			conditionRow.createCell(3).setCellValue(conditionVariable.getVariableType().getStandardVariable().getScale().getName());
			conditionRow.createCell(4).setCellValue(conditionVariable.getVariableType().getStandardVariable().getMethod().getName());
			conditionRow.createCell(5).setCellValue(dataType.getName());
			final boolean isNumeric = Util.isNumericVariable(dataType.getId());
			final String conditionValue = conditionVariable.getValue();
			if (isNumeric && conditionValue != null && !conditionValue.isEmpty()) {
				final Double thevalue = Double.valueOf(conditionValue);
				conditionRow.createCell(6).setCellValue(thevalue);
			} else {
				conditionRow.createCell(6).setCellValue(conditionVariable.getDisplayValue());
			}
			// use "STUDY" as label
			conditionRow.createCell(7).setCellValue(PhenotypicType.STUDY.getLabelList().get(0));

			conditionRowIndex++;
		}
		return conditionRowIndex;
	}

	/*
	 * Creates a row at given index, with given column headers and style
	 */
	private void createSectionHeaderRow(final HSSFCellStyle cellStyle, final HSSFSheet descriptionSheet,
			final List<StudyTemplateConstants> columns, final int index) {

		final HSSFRow conditionHeaderRow = descriptionSheet.createRow(index);
		for (int i = 0; i < columns.size(); i++) {
			final HSSFCell conditionHeaderCell = conditionHeaderRow.createCell(i);
			conditionHeaderCell.setCellValue(columns.get(i).getHeader());
			conditionHeaderCell.setCellStyle(cellStyle);
		}

		final HSSFName namedCell = descriptionSheet.getWorkbook().createName();
		// "CONDITION", "FACTOR", "CONSTANT", "VARIATES"
		namedCell.setNameName(columns.get(0).getHeader());
		final int fixedRow = index + 1;
		// area reference
		final String reference = descriptionSheet.getSheetName() + "!$A$" + fixedRow + ":$H$" + fixedRow;
		namedCell.setRefersToFormula(reference);
	}

	/*
	 * Generates study details section containing study name, title, objective, etc
	 */
	private void createStudyDetailsSection(final HSSFCellStyle labelStyle, final HSSFSheet descriptionSheet, final Study study,
			final String name) {
		final String title = study.getDescription();
		final String objective = study.getObjective();
		final String pmKey = study.getDisplayValue(TermId.PM_KEY);
		final Integer startDate = study.getStartDate();
		final Integer endDate = study.getEndDate();
		final String type = study.getType().getName();

		// add to the sheet
		this.createStudyDetailsRow(labelStyle, descriptionSheet, StudyTemplateConstants.STUDY, name, 0);
		this.createStudyDetailsRow(labelStyle, descriptionSheet, StudyTemplateConstants.TITLE, title, 1);
		this.createStudyDetailsRow(labelStyle, descriptionSheet, StudyTemplateConstants.PM_KEY, pmKey, 2);
		this.createStudyDetailsRow(labelStyle, descriptionSheet, StudyTemplateConstants.OBJECTIVE, objective, 3);

		final String startDateString = startDate != null ? startDate.toString() : null;
		this.createStudyDetailsRow(labelStyle, descriptionSheet, StudyTemplateConstants.START_DATE, startDateString, 4);

		final String endDateString = endDate != null ? endDate.toString() : null;
		this.createStudyDetailsRow(labelStyle, descriptionSheet, StudyTemplateConstants.END_DATE, endDateString, 5);

		this.createStudyDetailsRow(labelStyle, descriptionSheet, StudyTemplateConstants.STUDY_TYPE, type, 6);

		// formula reference needed file will be template for CIMMYT Fieldbook
		final HSSFName namedCell = descriptionSheet.getWorkbook().createName();
		namedCell.setNameName(StudyTemplateConstants.STUDY.getHeader());
		// area reference
		final String reference = descriptionSheet.getSheetName() + "!A" + 1 + ":H" + 1;
		namedCell.setRefersToFormula(reference);

		// merge cells for the study details
		for (int ctr = 0; ctr < DatasetExporter.CONDITION_LIST_HEADER_ROW_INDEX - 1; ctr++) {
			descriptionSheet.addMergedRegion(new CellRangeAddress(ctr, ctr, 1, 7));
		}

		// empty row
		descriptionSheet.createRow(DatasetExporter.CONDITION_LIST_HEADER_ROW_INDEX - 1);
	}

	/*
	 * Creates a row in the Study Details section
	 */
	private void createStudyDetailsRow(final HSSFCellStyle labelStyle, final HSSFSheet descriptionSheet,
			final StudyTemplateConstants constant, final String value, final int index) {
		final HSSFRow row = descriptionSheet.createRow(index);
		final HSSFCell typeCell = row.createCell(0);
		typeCell.setCellValue(constant.getHeader());
		typeCell.setCellStyle(labelStyle);
		row.createCell(1).setCellValue(value);
	}

	/**
	 * Specifically for troubleshooting, as visual introspection of Workbooks in an IDe for developers is very difficult - basic logging
	 * will print at DEBUG level - data will only print on TRACE level logging
	 *
	 * @param studyName
	 *
	 * @param sheet
	 */
	private void traceSheet(final String studyName, final HSSFSheet sheet) {

		DatasetExporter.LOG.debug("Exporting Worksheet for Study " + studyName);
		DatasetExporter.LOG.debug("Number of Rows : " + sheet.getPhysicalNumberOfRows());
		if (DatasetExporter.LOG.isTraceEnabled()) {
			for (final Row row : sheet) {
				DatasetExporter.LOG.trace("Row :" + row.getRowNum());
				for (final Iterator iterator = row.cellIterator(); iterator.hasNext();) {
					final Cell cell = (Cell) iterator.next();
					if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						DatasetExporter.LOG.trace(cell.getColumnIndex() + ":" + cell.getNumericCellValue());
					} else {
						DatasetExporter.LOG.trace(cell.getColumnIndex() + ":" + cell.getStringCellValue());
					}
				}
			}
		}

	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

}


package org.generationcp.ibpworkbench.cross.study.h2h.main.util;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.cross.study.h2h.main.ResultsComponent;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.ResultsData;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TraitForComparison;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class HeadToHeadDataListExport {

	private static final String LABEL_STYLE = "labelStyle";
	private static final String HEADING_STYLE = "headingStyle";
	private static final String HEADING_MERGED_STYLE = "headingMergedStyle";
	private static final String NUMERIC_STYLE = "numericStyle";
	private static final String NUMERIC_DOUBLE_STYLE = "numericDoubleStyle";

	@Autowired
	private ContextUtil contextUtil;
	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	public HeadToHeadDataListExport() {
		// empty constructor
	}

	private HashMap<String, CellStyle> createStyles(final HSSFWorkbook wb) {
		final HashMap<String, CellStyle> styles = new HashMap<>();

		// set cell style for labels in the description sheet
		final CellStyle labelStyle = wb.createCellStyle();
		labelStyle.setFillForegroundColor(IndexedColors.BROWN.getIndex());
		labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		final Font labelFont = wb.createFont();
		labelFont.setColor(IndexedColors.WHITE.getIndex());
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		labelStyle.setFont(labelFont);
		styles.put(HeadToHeadDataListExport.LABEL_STYLE, labelStyle);

		// set cell style for headings in the description sheet
		final CellStyle headingStyle = wb.createCellStyle();
		headingStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
		headingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		final Font headingFont = wb.createFont();
		headingFont.setColor(IndexedColors.WHITE.getIndex());
		headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingStyle.setFont(headingFont);
		styles.put(HeadToHeadDataListExport.HEADING_STYLE, headingStyle);

		final CellStyle headingMergedStyle = wb.createCellStyle();
		headingMergedStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
		headingMergedStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headingMergedStyle.setAlignment(CellStyle.ALIGN_CENTER);
		final Font headingMergedFont = wb.createFont();
		headingMergedFont.setColor(IndexedColors.WHITE.getIndex());
		headingMergedFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headingMergedStyle.setFont(headingMergedFont);
		styles.put(HeadToHeadDataListExport.HEADING_MERGED_STYLE, headingMergedStyle);

		// set cell style for numeric values (left alignment)
		final CellStyle numericStyle = wb.createCellStyle();
		numericStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		numericStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
		styles.put(HeadToHeadDataListExport.NUMERIC_STYLE, numericStyle);

		final CellStyle numericDoubleStyle = wb.createCellStyle();
		numericDoubleStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		numericDoubleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
		styles.put(HeadToHeadDataListExport.NUMERIC_DOUBLE_STYLE, numericDoubleStyle);

		return styles;
	}

	public String exportHeadToHeadDataListExcel(final String filenameWithoutExtension, final List<ResultsData> resultDataList,
			final Set<TraitForComparison> traitsIterator, final String[] columnIdData, final Map<String, String> columnIdDataMsgMap)
			throws HeadToHeadDataListExportException {

		final HSSFWorkbook wb = this.createExcelWorkbookContents(resultDataList, traitsIterator, columnIdData, columnIdDataMsgMap);

		try {
			final String directory = this.installationDirectoryUtil.getOutputDirectoryForProjectAndTool(this.contextUtil.getProjectInContext(), ToolName.MAIN_HEAD_TO_HEAD_BROWSER);
			final String fileName = FileNameGenerator.generateFileName(directory, "" , filenameWithoutExtension);
			final String fileNameUnderWorkspaceDirectory = this.installationDirectoryUtil.getTempFileInOutputDirectoryForProjectAndTool(
					fileName, ".xls", this.contextUtil.getProjectInContext(), ToolName.MAIN_HEAD_TO_HEAD_BROWSER);

			// write the excel file
			final FileOutputStream fileOutputStream = new FileOutputStream(fileNameUnderWorkspaceDirectory);
			wb.write(fileOutputStream);
			fileOutputStream.close();

			return fileNameUnderWorkspaceDirectory;
		} catch (final Exception ex) {
			throw new HeadToHeadDataListExportException("Error with writing to: " + filenameWithoutExtension, ex);
		}
	}

	HSSFWorkbook createExcelWorkbookContents(final List<ResultsData> resultDataList, final Set<TraitForComparison> traitsIterator,
			final String[] columnIdData, final Map<String, String> columnIdDataMsgMap) {
		final HSSFWorkbook wb = new HSSFWorkbook();
		final HashMap<String, CellStyle> sheetStyles = this.createStyles(wb);
		final HSSFSheet sheet = wb.createSheet("Data List");

		int cellIndex = 0;
		int startDataRowIndex = 0;

		final HSSFRow headerColSpan = sheet.createRow(startDataRowIndex++);
		final HSSFRow header = sheet.createRow(startDataRowIndex++);

		Cell cell = header.createCell(cellIndex++);
		cell.setCellValue("Test GroupID");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
		cell = header.createCell(cellIndex++);
		cell.setCellValue("Test GID");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
		cell = header.createCell(cellIndex++);
		cell.setCellValue("Test Entry");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
		cell = header.createCell(cellIndex++);
		cell.setCellValue("Standard GroupID");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
		cell = header.createCell(cellIndex++);
		cell.setCellValue("Standard GID");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
		cell = header.createCell(cellIndex++);
		cell.setCellValue("Standard Entry");
		cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));

		for (final TraitForComparison traitForCompare : traitsIterator) {
			if (traitForCompare.isDisplay()) {
				final int startCol = cellIndex;
				int endCol;
				final Cell cellHeader = headerColSpan.createCell(cellIndex);
				cellHeader.setCellValue(traitForCompare.getTraitInfo().getName());
				cellHeader.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_MERGED_STYLE));

				for (int k = 0; k < columnIdData.length; k++) {
					final String colId = columnIdData[k];
					final String msg = columnIdDataMsgMap.get(colId);
					cell = header.createCell(cellIndex++);
					cell.setCellValue(msg);
					cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.HEADING_STYLE));
				}

				endCol = cellIndex;
				sheet.addMergedRegion(new CellRangeAddress(
						// first row (0-based)
						0,
						// last row (0-based)
						0,
						// first column (0-based)
						startCol,
						// last column (0-based)
						endCol - 1));
			}
		}

		for (final ResultsData resData : resultDataList) {
			// we iterate and permutate against the list
			cellIndex = 0;
			final HSSFRow rowData = sheet.createRow(startDataRowIndex++);
			final String testEntryGroupId = resData.getGroupId1();
			final Integer testEntryGid = resData.getGid1();
			final String testEntryName = resData.getGid1Name();
			final String standardEntryGroupId = resData.getGroupId2();
			final Integer standardEntryGid = resData.getGid2();
			final String standardEntryName = resData.getGid2Name();

			rowData.createCell(cellIndex++).setCellValue(testEntryGroupId);
			rowData.createCell(cellIndex++).setCellValue(testEntryGid);
			rowData.createCell(cellIndex++).setCellValue(testEntryName);
			rowData.createCell(cellIndex++).setCellValue(standardEntryGroupId);
			rowData.createCell(cellIndex++).setCellValue(standardEntryGid);
			rowData.createCell(cellIndex++).setCellValue(standardEntryName);

			for (final TraitForComparison traitForCompare : traitsIterator) {
				if (traitForCompare.isDisplay()) {
					for (final String colId : columnIdData) {
						final String traitColId = traitForCompare.getTraitInfo().getName() + colId;
						String numVal = resData.getTraitDataMap().get(traitColId);
						if (numVal == null) {
							numVal = "0";
						}
						numVal = numVal.replaceAll(",", "");

						cell = rowData.createCell(cellIndex++);

						if ("-".equalsIgnoreCase(numVal)) {
							cell.setCellValue(numVal);
						} else {

							cell.setCellValue(Double.parseDouble(numVal));
							cell.setCellType(Cell.CELL_TYPE_NUMERIC);

							if (colId.equalsIgnoreCase(ResultsComponent.NUM_OF_ENV_COLUMN_ID)
									|| colId.equalsIgnoreCase(ResultsComponent.NUM_SUP_COLUMN_ID)) {
								cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.NUMERIC_STYLE));
							} else {
								cell.setCellStyle(sheetStyles.get(HeadToHeadDataListExport.NUMERIC_DOUBLE_STYLE));
							}
						}
					}
				}
			}
		}

		for (int ctr = 0; ctr < cellIndex; ctr++) {
			sheet.autoSizeColumn(ctr);
		}
		return wb;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}
}

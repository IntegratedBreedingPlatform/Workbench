
package org.generationcp.ibpworkbench.study.util;

import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.study.TableViewerDatasetTable;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;

@Configurable
public class TableViewerExporter {

	private final TableViewerDatasetTable table;
	private final TableViewerCellSelectorUtil tableViewerCellSelectorUtil;

	@Autowired
	private ContextUtil contextUtil;
	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	public TableViewerExporter(final TableViewerDatasetTable table, final TableViewerCellSelectorUtil tableViewerCellSelectorUtil) {
		this.table = table;
		this.tableViewerCellSelectorUtil = tableViewerCellSelectorUtil;
	}

	public String exportToExcel(final String filename) throws DatasetExporterException {

		// create workbook
		final Workbook workbook = new XSSFWorkbook();

		// Create first sheet
		final Sheet sheet1 = workbook.createSheet("Sheet 1");

		// Prepare data
		final ArrayList<String> columnId = new ArrayList<String>();
		final ArrayList<String> columnHeaders = new ArrayList<String>();
		final Object[] columnHeadersObjectArray = this.table.getVisibleColumns();
		final Object[] columnHeadersStringArray = this.table.getColumnHeaders();
		for (int x = 0; x < columnHeadersObjectArray.length; x++) {
			columnId.add(columnHeadersObjectArray[x].toString());
			columnHeaders.add(columnHeadersStringArray[x].toString());
		}
		final Object[] tableItemIds = this.table.getItemIds().toArray();

		// Create headers row, and populate with data
		final XSSFCellStyle headerStyle = (XSSFCellStyle) workbook.createCellStyle();
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(171, 171, 171)));
		final Font labelFont = workbook.createFont();
		labelFont.setColor(IndexedColors.BLACK.getIndex());
		labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headerStyle.setFont(labelFont);

		final Row headerRow = sheet1.createRow(0);
		for (int x = 0; x < columnHeaders.size(); x++) {
			headerRow.createCell(x).setCellValue(columnHeaders.get(x).toString());
			headerRow.getCell(x).setCellStyle(headerStyle);
		}

		// Traverse through table, and create rows/columns and populate with data
		final ArrayList<XSSFColor> cellColor = new ArrayList<XSSFColor>();
		final ArrayList<XSSFCellStyle> cellStyle = new ArrayList<XSSFCellStyle>();

		XSSFColor currentColor;

		final Row[] rows = new Row[tableItemIds.length];

		for (int y = 0; y < tableItemIds.length; y++) {
			rows[y] = sheet1.createRow(y + 1);
			for (int x = 0; x < columnHeaders.size(); x++) {
				String stringValue = "";
				if (this.table.getItem(tableItemIds[y]).getItemProperty(columnId.get(x)).getValue() instanceof Button) {
					stringValue = ((Button) this.table.getItem(tableItemIds[y]).getItemProperty(columnId.get(x)).getValue()).getCaption()
							.toString();
				} else {
					stringValue = this.table.getItem(tableItemIds[y]).getItemProperty(columnId.get(x)).toString();
				}

				// set as number if value is numeric
				if (NumberUtils.isNumber(stringValue)) {
					// integer
					if (NumberUtils.isDigits(stringValue)) {
						rows[y].createCell(x).setCellValue(Long.valueOf(stringValue));
					} else {
						rows[y].createCell(x).setCellValue(Double.valueOf(stringValue));
					}

				} else {
					rows[y].createCell(x).setCellValue(stringValue);
				}

				currentColor = this.tableViewerCellSelectorUtil.getColor(tableItemIds[y].toString(), columnId.get(x).toString());
				if (currentColor != null) {

					cellColor.add(currentColor);

					cellStyle.add((XSSFCellStyle) workbook.createCellStyle());
					cellStyle.get(cellStyle.size() - 1).setFillPattern(CellStyle.SOLID_FOREGROUND);
					cellStyle.get(cellStyle.size() - 1).setFillForegroundColor(cellColor.get(cellColor.size() - 1));
					rows[y].getCell(x).setCellStyle(cellStyle.get(cellStyle.size() - 1));

				}
			}

		}

		return this.writeExcelFile(filename, workbook);
	}

	String writeExcelFile(final String filename, final Workbook workbook) throws DatasetExporterException {
		try {
			// write the excel file
			final String fileNameUnderWorkspaceDirectory = this.installationDirectoryUtil.getTempFileInOutputDirectoryForProjectAndTool(
					filename, ".xlsx", this.contextUtil.getProjectInContext(), ToolName.STUDY_BROWSER);
			final FileOutputStream fileOutputStream = new FileOutputStream(fileNameUnderWorkspaceDirectory);
			workbook.write(fileOutputStream);
			fileOutputStream.close();

			return fileNameUnderWorkspaceDirectory;
		} catch (final Exception ex) {
			throw new DatasetExporterException("Error with writing to: " + filename, ex);
		}
	}

	public static boolean isInteger(final String s) {
		try {
			Integer.parseInt(s);
		} catch (final NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}
}

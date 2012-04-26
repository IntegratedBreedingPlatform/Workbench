package org.generationcp.ibpworkbench.util;

import java.awt.Point;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;

/**
 * A convenience class for POI library.
 *  
 * @author Glenn Marintes
 */
public class PoiUtil {
    
    // WorkBook convenience methods
    public static void setRepeatingRows(Workbook workBook, int sheetIndex, int fromRow, int toRow) {
        workBook.setRepeatingRowsAndColumns(sheetIndex, -1, -1, fromRow, toRow);
    }
    
    public static void setRepeatingColumns(Workbook workBook, int sheetIndex, int fromCol, int toCol) {
        workBook.setRepeatingRowsAndColumns(sheetIndex, fromCol, toCol, -1, -1);
    }
    
    public static void setRepeatingRowsAndColumns(Workbook workBook, int sheetIndex, int fromCol, int toCol, int fromRow, int toRow) {
        workBook.setRepeatingRowsAndColumns(sheetIndex, fromCol, toCol, fromRow, toRow);
    }
    
    public static Double getCellNumericValue(Cell cell) {
        return cell == null ? null : cell.getNumericCellValue();
    }
    
    public static String getCellStringValue(Cell cell) {
        return cell == null ? null : cell.getStringCellValue();
    }
    
    // setCellValue with cell name as parameter
    
    public static void setCellValue(Sheet sheet, String cellName, String value) {
        Point cellIndex = getCellIndex(cellName);
        setCellValue(sheet, cellIndex.y, cellIndex.x, value);
    }
    
    public static void setCellValue(Sheet sheet, String cellName, Integer value) {
        Point cellIndex = getCellIndex(cellName);
        setCellValue(sheet, cellIndex.y, cellIndex.x, value);
    }
    
    public static void setCellValue(Sheet sheet, String cellName, Long value) {
        Point cellIndex = getCellIndex(cellName);
        setCellValue(sheet, cellIndex.y, cellIndex.x, value);
    }
    
    public static void setCellValue(Sheet sheet, String cellName, Double value) {
        Point cellIndex = getCellIndex(cellName);
        setCellValue(sheet, cellIndex.y, cellIndex.x, value);
    }
    
    public static void setCellValue(Sheet sheet, String cellName, Date value) {
        Point cellIndex = getCellIndex(cellName);
        setCellValue(sheet, cellIndex.y, cellIndex.x, value);
    }
    
    public static void setCellValue(Sheet sheet, String cellName, Object value) {
        Point cellIndex = getCellIndex(cellName);
        setCellValue(sheet, cellIndex.y, cellIndex.x, value);
    }
    
    // setCellValue with String column name and integer row index as parameter
    public static void setCellValue(Sheet sheet, String colName, int rowIndex, String value) {
        int columnIndex = getColumnIndex(colName);
        setCellValue(sheet, columnIndex, rowIndex, value);
    }
    
    public static void setCellValue(Sheet sheet, String colName, int rowIndex, Integer value) {
        int columnIndex = getColumnIndex(colName);
        setCellValue(sheet, columnIndex, rowIndex, value);
    }
    
    public static void setCellValue(Sheet sheet, String colName, int rowIndex, Long value) {
        int columnIndex = getColumnIndex(colName);
        setCellValue(sheet, columnIndex, rowIndex, value);
    }
    
    public static void setCellValue(Sheet sheet, String colName, int rowIndex, Double value) {
        int columnIndex = getColumnIndex(colName);
        setCellValue(sheet, columnIndex, rowIndex, value);
    }
    
    public static void setCellValue(Sheet sheet, String colName, int rowIndex, Date value) {
        int columnIndex = getColumnIndex(colName);
        setCellValue(sheet, columnIndex, rowIndex, value);
    }
    
    public static void setCellValue(Sheet sheet, String colName, int rowIndex, Object value) {
        int columnIndex = getColumnIndex(colName);
        setCellValue(sheet, columnIndex, rowIndex, value);
    }
    
    // setCellValue with integer indices as parameter
    
    public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, String value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row == null ? null : row.getCell(columnIndex);
        if (cell == null) {
            cell = row == null ? null : row.createCell(columnIndex);
        }
        
        if (row == null || cell == null) {
            throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
        }
        
        if (value == null) {
            cell.setCellValue("");
        }
        else {
            cell.setCellValue(value);
        }
    }
    
    public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Integer value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row == null ? null : row.getCell(columnIndex);
        if (cell == null) {
            cell = row == null ? null : row.createCell(columnIndex);
        }
        
        if (row == null || cell == null) {
            throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
        }
        
        if (value == null) {
            cell.setCellValue("");
        }
        else {
            cell.setCellValue(value);
        }
    }
    
    public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Long value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row == null ? null : row.getCell(columnIndex);
        if (cell == null) {
            cell = row == null ? null : row.createCell(columnIndex);
        }
        
        if (row == null || cell == null) {
            throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
        }
        
        if (value == null) {
            cell.setCellValue("");
        }
        else {
            cell.setCellValue(value);
        }
    }
    
    public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Double value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row == null ? null : row.getCell(columnIndex);
        if (cell == null) {
            cell = row == null ? null : row.createCell(columnIndex);
        }
        
        if (row == null || cell == null) {
            throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
        }
        
        if (value == null) {
            cell.setCellValue("");
        }
        else {
            cell.setCellValue(value);
        }
    }
    
    public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Date value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row == null ? null : row.getCell(columnIndex);
        if (cell == null) {
            cell = row == null ? null : row.createCell(columnIndex);
        }
        
        if (row == null || cell == null) {
            throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
        }
        
        if (value == null) {
            cell.setCellValue("");
        }
        else {
            cell.setCellValue(value);
        }
    }
    
    public static void setCellValue(Sheet sheet, int columnIndex, int rowIndex, Object value) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row == null ? null : row.getCell(columnIndex);
        if (cell == null) {
            cell = row == null ? null : row.createCell(columnIndex);
        }
        
        if (row == null || cell == null) {
            throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
        }
        
        if (value == null) {
            cell.setCellValue("");
        }
        else {
            cell.setCellValue(value.toString());
        }
    }
    
    public static void setCellAlignment(Sheet sheet, int rowIndex, String columnName, short alignment) {
        setCellAlignment(sheet, rowIndex, getColumnIndex(columnName), alignment);
    }
    
    public static void setCellAlignment(Sheet sheet, int rowIndex, int columnIndex, short alignment) {
        Cell cell = getCell(sheet, columnIndex, rowIndex);
        
        if (cell == null) {
            throw new IllegalArgumentException("Cell with col=" + columnIndex + " and row=" + rowIndex + " is null.");
        }
        
        CellUtil.setAlignment(cell, sheet.getWorkbook(), alignment);
    }
    
    // other convenience methods
    public static Cell getCell(Sheet sheet, String cellName) {
        Point cellIndex = getCellIndex(cellName);
        return getCell(sheet, cellIndex.x, cellIndex.y);
    }
    
    public static Cell getCell(Sheet sheet, String columnName, int rowIndex) {
        return getCell(sheet, getColumnIndex(columnName), rowIndex);
    }
    
    public static Cell getCell(Sheet sheet, int columnIndex, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        
        Cell cell = row == null ? null : row.getCell(columnIndex);
        if (cell == null) {
            cell = row == null ? null : row.createCell(columnIndex);
        }
        
        return cell;
    }
    
    public static int getColumnIndex(String columnName) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        int columnIndex = 0;
        int charPosition = 0;
        for (int index = columnName.length() - 1; index >= 0; index--) {
            char ch = columnName.charAt(index);
            int charIndex = chars.indexOf(ch);
            if (charIndex == -1) throw new IllegalArgumentException("Invalid character in column name: " + ch);
            
            columnIndex += ((charIndex + 1) * Math.pow(26, charPosition));
            charPosition++;
        }
        
        return columnIndex - 1;
    }
    
    public static Point getCellIndex(String cellName) {
        int indexOfRowNum = -1;
        for (int index = 0; index < cellName.length(); index++) {
            char ch = cellName.charAt(index);
            if (Character.isDigit(ch)) {
                indexOfRowNum = index;
                break;
            }
        }
        
        String columnName = cellName.substring(0, indexOfRowNum);
        String rowStr = cellName.substring(indexOfRowNum);
        
        int row = StringUtil.parseInt(rowStr, 1) - 1;
        int col = getColumnIndex(columnName);
        
        return new Point(row, col);
    }
}

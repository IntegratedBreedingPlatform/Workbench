/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.comp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.ibpworkbench.util.PoiUtil;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class FieldBookObservationPanel extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    
    private String filename;
    
    private List<String> columnNames;
    private List<List<String>> observationList;

    private Table observationTable;
    
    public FieldBookObservationPanel(String filename) {
        this.filename = filename;
        
        assemble();
    }
    
    protected void initialize() {
        Workbook wb = null;
        
        try {
            wb = new HSSFWorkbook(new FileInputStream(filename));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot open file", e);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot process file", e);
        }
        catch (POIXMLException e) {
            try {
                wb = new XSSFWorkbook(new FileInputStream(filename));
            }
            catch (FileNotFoundException e1) {
                throw new RuntimeException("Cannot open file", e1);
            }
            catch (IOException e1) {
                throw new RuntimeException("Cannot process file", e1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (wb == null) {
                return;
            }
        }
        
        Sheet sheet = wb.getSheet("Observation");
        if (sheet == null) {
            sheet = wb.getSheet("Measurements");
        }
        if (sheet == null) return;

        observationList = new ArrayList<List<String>>();

        columnNames = new ArrayList<String>();
        for (int colIndex = 0; colIndex < 1024; colIndex++) {
            Row row = sheet.getRow(0);
            if (row == null) break;

            Cell cell = row.getCell(colIndex);
            String columnName = PoiUtil.getCellStringValue(cell);
            if (StringUtils.isBlank(columnName)) break;

            columnNames.add(columnName);
        }

        for (int rowIndex = 1; rowIndex < 1048576; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) break;

            List<String> observations = new ArrayList<String>();
            for (int colIndex = 0; colIndex < columnNames.size(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                Object cellValue = PoiUtil.getCellValue(cell);

                observations.add(cellValue == null ? "" : cellValue.toString());
            }

            observationList.add(observations);
        }
    }
    
    protected void initializeComponents() {
        observationTable = new Table();
        
        observationTable.setSelectable(true); // allow row selection
        
        observationTable.setImmediate(true); // react at once when something is selected
        
//        BeanContainer<String, FieldBookObservation> container = new BeanContainer<String, FieldBookObservation>(FieldBookObservation.class);
//        container.setBeanIdProperty("plotNo");
//        
//        for (FieldBookObservation observation : observationList) {
//            container.addBean(observation);
//        }
//        
//        observationTable.setContainerDataSource(container);
//        
//        observationTable.setColumnHeader("plotNo", "Plot No");
//        observationTable.setColumnHeader("rep", "Rep");
//        observationTable.setColumnHeader("mainPlot", "Main Plot");
//        observationTable.setColumnHeader("subPlot", "Sub Plot");
//        observationTable.setColumnHeader("variety", "Variety");
//        observationTable.setColumnHeader("gid", "GID");
//        observationTable.setColumnHeader("fertilizer", "Fert");
//        observationTable.setColumnHeader("yield", "Yield");
//        observationTable.setColumnHeader("pht", "PHT");
//        
//        String[] columns = new String[]{"plotNo", "rep", "mainPlot", "subPlot", "variety", "gid", "fertilizer", "yield", "pht"};
//        observationTable.setVisibleColumns(columns);
        
        for (String columnName : columnNames) {
            observationTable.addContainerProperty(columnName, String.class, null);
        }
        
        for (int rowIndex = 0; rowIndex < observationList.size(); rowIndex++) {
            List<String> observation = observationList.get(rowIndex);
            observationTable.addItem(observation.toArray(), rowIndex);
        }
    }
    
    protected void initializeLayout() {
        setMargin(true);
        
        observationTable.setWidth("100%");
        addComponent(observationTable);
    }
    
    protected void initializeActions() {
    }
    
    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}

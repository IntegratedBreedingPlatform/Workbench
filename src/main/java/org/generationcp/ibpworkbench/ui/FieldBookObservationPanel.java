/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui;

import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.POIXMLException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.PoiUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configurable
public class FieldBookObservationPanel extends VerticalLayout implements InitializingBean{
    
    private static final Logger LOG = LoggerFactory.getLogger(FieldBookObservationPanel.class);
    private static final long serialVersionUID = 1L;

    private String filename;

    private List<String> columnNames;
    private List<List<String>> observationList;

    private Table observationTable;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public FieldBookObservationPanel(String filename) {
        super();
        this.filename = filename;

    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    protected void initialize() throws Exception {
        Workbook wb = null;

        try {
            wb = new HSSFWorkbook(new FileInputStream(filename));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(messageSource.getMessage(Message.FILE_CANNOT_OPEN_DESC), e);
        } catch (IOException e) {
            throw new RuntimeException(messageSource.getMessage(Message.FILE_CANNOT_PROCESS_DESC), e);
        } catch (POIXMLException e) {
            try {
                wb = new XSSFWorkbook(new FileInputStream(filename));
            } catch (FileNotFoundException e1) {
                throw new RuntimeException(messageSource.getMessage(Message.FILE_CANNOT_OPEN_DESC), e1);
            } catch (IOException e1) {
                throw new RuntimeException(messageSource.getMessage(Message.FILE_CANNOT_PROCESS_DESC), e1);
            }
        } catch (Exception e) {
            LOG.error("Exception", e);
            InternationalizableException i18e = new InternationalizableException(e);
            i18e.setCaption(Message.FILE_ERROR);
            i18e.setDescription(e.getCause().getMessage());
            throw i18e;
        } finally {
            if (wb == null) {
                return;
            }
        }

        Sheet sheet = wb.getSheet("Observation");
        if (sheet == null) {
            sheet = wb.getSheet("Measurements");
        }
        if (sheet == null) {
            return;
        }

        observationList = new ArrayList<List<String>>();

        columnNames = new ArrayList<String>();
        for (int colIndex = 0; colIndex < 1024; colIndex++) {
            Row row = sheet.getRow(0);
            if (row == null) {
                break;
            }

            Cell cell = row.getCell(colIndex);
            String columnName = PoiUtil.getCellStringValue(cell);
            if (StringUtils.isBlank(columnName)) {
                break;
            }

            columnNames.add(columnName);
        }

        for (int rowIndex = 1; rowIndex < 1048576; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }

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

        observationTable.setImmediate(true); // react at once when something is
                                             // selected

        // BeanContainer<String, FieldBookObservation> container = new
        // BeanContainer<String,
        // FieldBookObservation>(FieldBookObservation.class);
        // container.setBeanIdProperty("plotNo");
        //
        // for (FieldBookObservation observation : observationList) {
        // container.addBean(observation);
        // }
        //
        // observationTable.setContainerDataSource(container);
        //
        // observationTable.setColumnHeader("plotNo", "Plot No");
        // observationTable.setColumnHeader("rep", "Rep");
        // observationTable.setColumnHeader("mainPlot", "Main Plot");
        // observationTable.setColumnHeader("subPlot", "Sub Plot");
        // observationTable.setColumnHeader("variety", "Variety");
        // observationTable.setColumnHeader("gid", "GID");
        // observationTable.setColumnHeader("fertilizer", "Fert");
        // observationTable.setColumnHeader("yield", "Yield");
        // observationTable.setColumnHeader("pht", "PHT");
        //
        // String[] columns = new String[]{"plotNo", "rep", "mainPlot",
        // "subPlot", "variety", "gid", "fertilizer", "yield", "pht"};
        // observationTable.setVisibleColumns(columns);

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

    protected void assemble() throws Exception {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}

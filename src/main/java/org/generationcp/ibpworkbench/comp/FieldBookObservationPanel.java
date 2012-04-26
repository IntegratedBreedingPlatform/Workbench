package org.generationcp.ibpworkbench.comp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.generationcp.ibpworkbench.model.FieldBookObservation;
import org.generationcp.ibpworkbench.util.PoiUtil;
import org.generationcp.ibpworkbench.util.Util;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class FieldBookObservationPanel extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    
    private String filename;
    
    private List<FieldBookObservation> observationList;

    private Table observationTable;
    
    public FieldBookObservationPanel(String filename) {
        this.filename = filename;
        
        assemble();
    }
    
    protected void initialize() {
        try {
            Workbook wb = new XSSFWorkbook(new FileInputStream(filename));
            
            Sheet sheet = wb.getSheet("Observation");
            if (sheet == null) return;
            
            observationList = new ArrayList<FieldBookObservation>();
            
            for (int index = 1; index < 1048576; index++) {
                Row row = sheet.getRow(index);
                if (row == null) break;
                
                Cell cellA = row.getCell(0);
                Cell cellB = row.getCell(1);
                Cell cellC = row.getCell(2);
                Cell cellD = row.getCell(3);
                Cell cellE = row.getCell(4);
                Cell cellF = row.getCell(5);
                Cell cellG = row.getCell(6);
                Cell cellH = row.getCell(7);
                Cell cellI = row.getCell(8);
                
                if (Util.isAllNull(cellA, cellB, cellC, cellD, cellE, cellF, cellG, cellH, cellI)) {
                    break;
                }
                
                Double plotNo = PoiUtil.getCellNumericValue(cellA);
                Double rep = PoiUtil.getCellNumericValue(cellB);
                Double mainPlot = PoiUtil.getCellNumericValue(cellC);
                Double subPlot = PoiUtil.getCellNumericValue(cellD);
                String variety = PoiUtil.getCellStringValue(cellE);
                Double gid = PoiUtil.getCellNumericValue(cellF);
                Double fertilizer = PoiUtil.getCellNumericValue(cellG);
                Double yield = PoiUtil.getCellNumericValue(cellH);
                Double pht = PoiUtil.getCellNumericValue(cellI);
                
                if (Util.isAllNull(plotNo, rep, mainPlot, subPlot, variety, gid, fertilizer, yield, pht)) {
                    break;
                }
                
                FieldBookObservation observation = new FieldBookObservation();
                observation.setPlotNo(plotNo.intValue());
                observation.setRep(rep.intValue());
                observation.setMainPlot(mainPlot.intValue());
                observation.setSubPlot(subPlot.intValue());
                observation.setVariety(variety);
                observation.setGid(gid.intValue());
                observation.setFertilizer(fertilizer.intValue());
                observation.setYield(yield);
                observation.setPht(pht.intValue());
                
                observationList.add(observation);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void initializeComponents() {
        observationTable = new Table();
        
        observationTable.setImmediate(true); // react at once when something is selected
        
        BeanContainer<String, FieldBookObservation> container = new BeanContainer<String, FieldBookObservation>(FieldBookObservation.class);
        container.setBeanIdProperty("plotNo");
        
        for (FieldBookObservation observation : observationList) {
            container.addBean(observation);
        }
        
        observationTable.setContainerDataSource(container);
        
        observationTable.setColumnHeader("plotNo", "Plot No");
        observationTable.setColumnHeader("rep", "Rep");
        observationTable.setColumnHeader("mainPlot", "Main Plot");
        observationTable.setColumnHeader("subPlot", "Sub Plot");
        observationTable.setColumnHeader("variety", "Variety");
        observationTable.setColumnHeader("gid", "GID");
        observationTable.setColumnHeader("fertilizer", "Fert");
        observationTable.setColumnHeader("yield", "Yield");
        observationTable.setColumnHeader("pht", "PHT");
        
        String[] columns = new String[]{"plotNo", "rep", "mainPlot", "subPlot", "variety", "gid", "fertilizer", "yield", "pht"};
        observationTable.setVisibleColumns(columns);
    }
    
    protected void initializeLayout() {
        setMargin(true);
        
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

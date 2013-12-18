package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.generationcp.commons.util.PoiUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;

import au.com.bytecode.opencsv.CSVWriter;


public class DatasetExporter {

    private static final int CONDITION_LIST_HEADER_ROW_INDEX = 7;
    private static final String NUMERIC_VARIABLE = "Numeric variable";
    
    private StudyDataManager studyDataManager;
    private OntologyDataManager ontologyDataManager;
    private Integer studyId;
    private Integer datasetId;
    
    public DatasetExporter(StudyDataManager studyDataManager, OntologyDataManager ontologyDataManager, Integer studyId, Integer representationId) {
        this.studyDataManager = studyDataManager;
        this.ontologyDataManager = ontologyDataManager;
        this.studyId = studyId;
        this.datasetId = representationId;
    }
    
    public DatasetExporter(StudyDataManager studyDataManager, Integer studyId, Integer datasetId){
        this.studyDataManager = studyDataManager;
        this.studyId = studyId;
        this.datasetId = datasetId;
    }
    
    public FileOutputStream exportToFieldBookExcelUsingIBDBv2(String filename) throws DatasetExporterException {
        if(studyDataManager == null){
            throw new DatasetExporterException("studyDataManagerV2 should not be null.");
        }
        
        //create workbook
        Workbook workbook = new HSSFWorkbook();
        CellStyle cellStyleForObservationSheet = workbook.createCellStyle();
        
        // set cell style for labels in the description sheet
        CellStyle labelStyle = workbook.createCellStyle();
        labelStyle.setFillForegroundColor(IndexedColors.BROWN.getIndex());
        labelStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font labelFont = workbook.createFont();
        labelFont.setColor(IndexedColors.WHITE.getIndex());
        labelFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        labelStyle.setFont(labelFont);
        
        // set cell style for headings in the description sheet
        CellStyle headingStyle = workbook.createCellStyle();
        headingStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        headingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Font headingFont = workbook.createFont();
        headingFont.setColor(IndexedColors.WHITE.getIndex());
        headingFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headingStyle.setFont(headingFont);
        
        // set cell style for variate headings in the description sheet
        CellStyle variateHeadingStyle = workbook.createCellStyle();
        variateHeadingStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        variateHeadingStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        variateHeadingStyle.setFont(headingFont);
        
        //create two sheets, one for description and another for measurements
        Sheet descriptionSheet = workbook.createSheet("Description");
        Sheet observationSheet = workbook.createSheet("Observation");
        
        //this map is for mapping the columns names of the dataset to their column index in the excel sheet
        Map<String, Integer> columnsMap = new HashMap<String, Integer>(); 
        int observationSheetColumnIndex = 0;
        
        //write the details on the first sheet - description
        //get the study first
        Study study = null;
        try {
            study = studyDataManager.getStudy(this.studyId);
        } catch (MiddlewareQueryException ex) {
            throw new DatasetExporterException("Error with getting Study with id: " + this.studyId, ex);
        }
        
        if(study != null) {
            //get the needed study details
            String name = study.getName();
            String title = study.getTitle();
            String objective = study.getObjective();
            Integer startDate = study.getStartDate();
            Integer endDate = study.getEndDate();
            String type = study.getType();
            
            //add to the sheet
            Row row0 = descriptionSheet.createRow(0);
            Cell studyNameCell = row0.createCell(0);
            studyNameCell.setCellValue("STUDY");
            studyNameCell.setCellStyle(labelStyle);
            row0.createCell(1).setCellValue(name);
            
            Row row1 = descriptionSheet.createRow(1);
            Cell titleCell = row1.createCell(0);
            titleCell.setCellValue("TITLE");
            titleCell.setCellStyle(labelStyle);
            row1.createCell(1).setCellValue(title);
            
            Row row2 = descriptionSheet.createRow(2);
            Cell objectiveCell = row2.createCell(0);
            objectiveCell.setCellValue("OBJECTIVE");
            objectiveCell.setCellStyle(labelStyle);
            row2.createCell(1).setCellValue(objective);
            
            Row row3 = descriptionSheet.createRow(3);
            Cell startDateCell = row3.createCell(0);
            startDateCell.setCellValue("START DATE");
            startDateCell.setCellStyle(labelStyle);
            if(startDate != null){
                row3.createCell(1).setCellValue(startDate.toString());
            } else {
                String nullString = null;
                row3.createCell(1).setCellValue(nullString);
            }
            
            Row row4 = descriptionSheet.createRow(4);
            Cell endDateCell = row4.createCell(0);
            endDateCell.setCellValue("END DATE");
            endDateCell.setCellStyle(labelStyle);
            if(endDate != null){
                row4.createCell(1).setCellValue(endDate.toString());
            } else {
                String nullString = null;
                row4.createCell(1).setCellValue(nullString);
            }
            
            Row row5 = descriptionSheet.createRow(5);
            Cell typeCell = row5.createCell(0);
            typeCell.setCellValue("STUDY TYPE");
            typeCell.setCellStyle(labelStyle);
            row5.createCell(1).setCellValue(type);
            
            //merge cells for the study details
            for(int ctr = 0; ctr < 7; ctr++) {
                descriptionSheet.addMergedRegion(new CellRangeAddress(ctr, ctr, 1, 7));
            }
            
            //empty row
            Row row6 = descriptionSheet.createRow(6);
            
            //row with headings for condition list
            Row conditionHeaderRow = descriptionSheet.createRow(CONDITION_LIST_HEADER_ROW_INDEX);
            Cell conditionHeaderCell = conditionHeaderRow.createCell(0);
            conditionHeaderCell.setCellValue("CONDITION");
            conditionHeaderCell.setCellStyle(headingStyle);
            Cell descriptionHeaderCell = conditionHeaderRow.createCell(1);
            descriptionHeaderCell.setCellValue("DESCRIPTION");
            descriptionHeaderCell.setCellStyle(headingStyle);
            Cell propertyHeaderCell = conditionHeaderRow.createCell(2);
            propertyHeaderCell.setCellValue("PROPERTY");
            propertyHeaderCell.setCellStyle(headingStyle);
            Cell scaleHeaderCell = conditionHeaderRow.createCell(3);
            scaleHeaderCell.setCellValue("SCALE");
            scaleHeaderCell.setCellStyle(headingStyle);
            Cell methodHeaderCell = conditionHeaderRow.createCell(4);
            methodHeaderCell.setCellValue("METHOD");
            methodHeaderCell.setCellStyle(headingStyle);
            Cell dataTypeHeaderCell = conditionHeaderRow.createCell(5);
            dataTypeHeaderCell.setCellValue("DATA TYPE");
            dataTypeHeaderCell.setCellStyle(headingStyle);
            Cell valueHeaderCell = conditionHeaderRow.createCell(6);
            valueHeaderCell.setCellValue("VALUE");
            valueHeaderCell.setCellStyle(headingStyle);
            
            //get the conditions and their details
            VariableList conditions = study.getConditions();
            
            int conditionRowIndex = CONDITION_LIST_HEADER_ROW_INDEX + 1;
            List<Variable> conditionVariables = conditions.getVariables();
            for(Variable conditionVariable : conditionVariables) {
                String conditionName = conditionVariable.getVariableType().getLocalName();
                if(conditionName != null) {
                    conditionName = conditionName.trim();
                }
                String conditionType = conditionVariable.getVariableType().getStandardVariable().getDataType().getName();
                
                Row conditionRow = descriptionSheet.createRow(conditionRowIndex);
                conditionRow.createCell(0).setCellValue(conditionName);
                conditionRow.createCell(1).setCellValue(conditionVariable.getVariableType().getStandardVariable().getDescription());
                conditionRow.createCell(2).setCellValue(conditionVariable.getVariableType().getStandardVariable().getName());
                conditionRow.createCell(3).setCellValue(conditionVariable.getVariableType().getStandardVariable().getScale().getName());
                conditionRow.createCell(4).setCellValue(conditionVariable.getVariableType().getStandardVariable().getMethod().getName());
                conditionRow.createCell(5).setCellValue(conditionType);
                if(conditionType.equals(NUMERIC_VARIABLE)) {
                    Double thevalue = Double.valueOf(conditionVariable.getValue());
                    conditionRow.createCell(6).setCellValue(thevalue);
                } else {
                    conditionRow.createCell(6).setCellValue((conditionVariable.getValue()!=null) ? conditionVariable.getDisplayValue() : null);
                }
                
                //add entry to columns mapping
                //we set the value to -1 to signify that this should not be a column in the observation sheet
                if(!conditionName.equals("STUDY")) {
                    columnsMap.put(conditionName, Integer.valueOf(-1));
                }
                
                conditionRowIndex++;
            }
            
            //empty row
            Row emptyRowBeforeFactors = descriptionSheet.createRow(conditionRowIndex);
            
            DataSet dataset = null;
            try {
                dataset = this.studyDataManager.getDataSet(this.datasetId);
            } catch (MiddlewareQueryException ex) {
                throw new DatasetExporterException("Error with getting Dataset with id: " + this.studyId, ex);
            }
            
            //row with headings for factor list
            int factorRowHeaderIndex = conditionRowIndex + 1;
            Row factorHeaderRow = descriptionSheet.createRow(factorRowHeaderIndex);
            Cell factorHeaderCell = factorHeaderRow.createCell(0);
            factorHeaderCell.setCellValue("FACTOR");
            factorHeaderCell.setCellStyle(headingStyle);
            Cell factorDescriptionHeaderCell = factorHeaderRow.createCell(1);
            factorDescriptionHeaderCell.setCellValue("DESCRIPTION");
            factorDescriptionHeaderCell.setCellStyle(headingStyle);
            Cell factorPropertyHeaderCell = factorHeaderRow.createCell(2);
            factorPropertyHeaderCell.setCellValue("PROPERTY");
            factorPropertyHeaderCell.setCellStyle(headingStyle);
            Cell factorScaleHeaderCell = factorHeaderRow.createCell(3);
            factorScaleHeaderCell.setCellValue("SCALE");
            factorScaleHeaderCell.setCellStyle(headingStyle);
            Cell factorMethodHeaderCell = factorHeaderRow.createCell(4);
            factorMethodHeaderCell.setCellValue("METHOD");
            factorMethodHeaderCell.setCellStyle(headingStyle);
            Cell factorDataTypeHeaderCell = factorHeaderRow.createCell(5);
            factorDataTypeHeaderCell.setCellValue("DATA TYPE");
            factorDataTypeHeaderCell.setCellStyle(headingStyle);
            
            //get the factors and their details
            VariableTypeList datasetVariableTypes = dataset.getVariableTypes();
            VariableTypeList factorVariableTypeList = datasetVariableTypes.getFactors();
            List<VariableType> factorVariableTypes = factorVariableTypeList.getVariableTypes();
            
            int factorRowIndex = factorRowHeaderIndex + 1;
            for(VariableType factor : factorVariableTypes) {
            	
            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET) continue;
            	
                String dataType = factor.getStandardVariable().getDataType().getName();
                String factorName = factor.getLocalName();
                if(factorName != null) {
                    factorName = factorName.trim();
                }
                
                //check if factor is already written as a condition
                Integer temp = columnsMap.get(factorName);
                if(temp == null && !factorName.equals("STUDY")) {
                    Row factorRow = descriptionSheet.createRow(factorRowIndex);
                    factorRow.createCell(0).setCellValue(factorName);
                    factorRow.createCell(1).setCellValue(factor.getStandardVariable().getDescription());
                    factorRow.createCell(2).setCellValue(factor.getStandardVariable().getName());
                    factorRow.createCell(3).setCellValue(factor.getStandardVariable().getScale().getName());
                    factorRow.createCell(4).setCellValue(factor.getStandardVariable().getMethod().getName());
                    factorRow.createCell(5).setCellValue(dataType);
                    
                    //add entry to columns mapping
                    columnsMap.put(factorName, Integer.valueOf(observationSheetColumnIndex));
                    observationSheetColumnIndex++;
                        
                    factorRowIndex++;
                }
            }
            
            //empty row
            Row emptyRowBeforeVariate = descriptionSheet.createRow(factorRowIndex);
            
            //row with headings for variate list
            int variateHeaderRowIndex = factorRowIndex + 1;
            Row variateHeaderRow = descriptionSheet.createRow(variateHeaderRowIndex);
            Cell variateHeaderCell = variateHeaderRow.createCell(0);
            variateHeaderCell.setCellValue("VARIATE");
            variateHeaderCell.setCellStyle(variateHeadingStyle);
            Cell variateDescriptionHeaderCell = variateHeaderRow.createCell(1);
            variateDescriptionHeaderCell.setCellValue("DESCRIPTION");
            variateDescriptionHeaderCell.setCellStyle(variateHeadingStyle);
            Cell variatePropertyHeaderCell = variateHeaderRow.createCell(2);
            variatePropertyHeaderCell.setCellValue("PROPERTY");
            variatePropertyHeaderCell.setCellStyle(variateHeadingStyle);
            Cell variateScaleHeaderCell = variateHeaderRow.createCell(3);
            variateScaleHeaderCell.setCellValue("SCALE");
            variateScaleHeaderCell.setCellStyle(variateHeadingStyle);
            Cell variateMethodHeaderCell = variateHeaderRow.createCell(4);
            variateMethodHeaderCell.setCellValue("METHOD");
            variateMethodHeaderCell.setCellStyle(variateHeadingStyle);
            Cell variateDataTypeHeaderCell = variateHeaderRow.createCell(5);
            variateDataTypeHeaderCell.setCellValue("DATA TYPE");
            variateDataTypeHeaderCell.setCellStyle(variateHeadingStyle);
            
            //get the variates and their details
            VariableTypeList variateVariableTypeList = datasetVariableTypes.getVariates();
            List<VariableType> variateVariableTypes = variateVariableTypeList.getVariableTypes();
            
            
            int variateRowIndex = variateHeaderRowIndex + 1;
            for(VariableType variate : variateVariableTypes) {
            	
                String dataType = variate.getStandardVariable().getDataType().getName();
                String variateName = variate.getLocalName();
                if(variateName != null) {
                    variateName = variateName.trim();
                }
                
                Row variateRow = descriptionSheet.createRow(variateRowIndex);
                variateRow.createCell(0).setCellValue(variateName);
                variateRow.createCell(1).setCellValue(variate.getStandardVariable().getDescription());
                variateRow.createCell(2).setCellValue(variate.getStandardVariable().getName());
                variateRow.createCell(3).setCellValue(variate.getStandardVariable().getScale().getName());
                variateRow.createCell(4).setCellValue(variate.getStandardVariable().getMethod().getName());
                variateRow.createCell(5).setCellValue(dataType);
                
                //add entry to columns mapping
                columnsMap.put(variateName, Integer.valueOf(observationSheetColumnIndex));
                observationSheetColumnIndex++;
                
                variateRowIndex++;
            }
            
            //populate the measurements sheet
            //establish the columns of the dataset first
            Row datasetHeaderRow = observationSheet.createRow(0);
            for(String columnName : columnsMap.keySet()) {
                short columnIndex = columnsMap.get(columnName).shortValue();
                if(columnIndex >= 0) {
                    Cell cell = PoiUtil.createCell(cellStyleForObservationSheet, datasetHeaderRow, columnIndex, CellStyle.ALIGN_CENTER, CellStyle.ALIGN_CENTER);
                    cell.setCellValue(columnName);
                }
            }
            
            //then work with the data
            //do it by 50 rows at a time
            int pageSize = 50;
            long totalNumberOfRows = 0;
            int sheetRowIndex = 1;
            
            /* try {
                totalNumberOfRows = this.studyDataManagerV2.countExperiments(this.datasetId);
            } catch(Exception ex) {
                throw new DatasetExporterException("Error with getting count of experiments for study - " + name 
                        + ", dataset - " + this.datasetId, ex); 
            } */
            
            List<Experiment> experiments = new ArrayList<Experiment>();
            
            try {
                experiments = this.studyDataManager.getExperiments(this.datasetId,0,Integer.MAX_VALUE);
            } catch(Exception ex) {
                throw new DatasetExporterException("Error with getting ounit ids of study - " + name 
                        + ", representation - " + this.datasetId, ex); 
            }
            
            // for(int start = 0; start < totalNumberOfRows; start = start + pageSize) {
                
                
                //map each experiment into a row in the observation sheet
                for(Experiment experiment : experiments) {
                    Row row = observationSheet.createRow(sheetRowIndex);
                    sheetRowIndex++;
                        
                    List<Variable> factorsOfExperiments = experiment.getFactors().getVariables();
                    for(Variable factorVariable : factorsOfExperiments){
                        String factorName = factorVariable.getVariableType().getLocalName();
                        if(factorName != null){
                            factorName = factorName.trim();
                        }
                        Integer columnIndexInteger = columnsMap.get(factorName); 
                        if(columnIndexInteger != null){
                            short columnIndex = columnIndexInteger.shortValue();
                            if(columnIndex >= 0) {
                                Cell cell = PoiUtil.createCell(cellStyleForObservationSheet, row, columnIndex, CellStyle.ALIGN_CENTER, CellStyle.ALIGN_CENTER);
                                if(factorVariable.getVariableType().getStandardVariable().getDataType().getName().equals(NUMERIC_VARIABLE)){
                                    double elemValue = 0;
                                    if(factorVariable.getValue() != null){
                                        try{
                                            elemValue = Double.valueOf(factorVariable.getValue());
                                            cell.setCellValue(elemValue);
                                            if (elemValue == Double.valueOf("-1E+36")) cell.setCellValue("");
                                        }catch(NumberFormatException ex){
                                            String value = factorVariable.getValue();
                                            if(value != null) {
                                                value = value.trim();
                                            }
                                            cell.setCellValue(value);
                                        }
                                    } else {
                                        String nullValue = null;
                                        cell.setCellValue(nullValue);
                                    }
                                } else{
                                    String value = factorVariable.getValue();
                                    if(value != null) {
                                        value = value.trim();
                                    }
                                    cell.setCellValue(value);
                                }
                            }
                        }
                    }
                        
                    List<Variable> variateVariables = experiment.getVariates().getVariables();
                    for(Variable variateVariable : variateVariables){
                        String variateName = variateVariable.getVariableType().getLocalName();
                        if(variateName != null){
                            variateName = variateName.trim();
                        }
                        Integer columnIndexInteger = columnsMap.get(variateName); 
                        if(columnIndexInteger != null){
                            short columnIndex = columnIndexInteger.shortValue();
                            if(columnIndex >= 0) {
                                Cell cell = PoiUtil.createCell(cellStyleForObservationSheet, row, columnIndex, CellStyle.ALIGN_CENTER, CellStyle.ALIGN_CENTER);
                                if(variateVariable.getVariableType().getStandardVariable().getDataType().getName().equals(NUMERIC_VARIABLE)){
                                    double elemValue = 0;
                                    if(variateVariable.getValue() != null){
                                        try{
                                            elemValue = Double.valueOf(variateVariable.getValue());
                                            cell.setCellValue(elemValue);
                                            if (elemValue == Double.valueOf("-1E+36")) cell.setCellValue("");
                                        }catch(NumberFormatException ex){
                                            String value = variateVariable.getValue();
                                            if(value != null) {
                                                value = value.trim();
                                            }
                                            cell.setCellValue(value);
                                        }
                                    } else {
                                        String nullValue = null;
                                        cell.setCellValue(nullValue);
                                    }
                                } else{
                                    String value = variateVariable.getValue();
                                    if(value != null) {
                                        value = value.trim();
                                    }
                                    cell.setCellValue(value);
                                }
                            }
                        }
                    }
                }
            // }
        }
        
        //adjust column widths of description sheet to fit contents
        for(int ctr = 0; ctr < 8; ctr++) {
            if(ctr != 1) {
                descriptionSheet.autoSizeColumn(ctr);
            }
        }
        
        //adjust column widths of observation sheet to fit contents
        for(int ctr = 0; ctr < observationSheetColumnIndex; ctr++) {
            observationSheet.autoSizeColumn(ctr);
        }
        
        //exportToFieldBookCSVUsingIBDBv2(filename, "URRC");
        
        try {
            //write the excel file
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            return fileOutputStream;
        } catch(Exception ex) {
            throw new DatasetExporterException("Error with writing to: " + filename, ex);
        }
        
        
    }
    
    @SuppressWarnings("unchecked")
	public void exportToFieldBookCSVUsingIBDBv2(String filename, String selectedFactor , List<String> selectedEnvironment, BreedingViewInput breedingViewInput) throws DatasetExporterException {

        DataSet dataset = null;
        try {
            dataset = this.studyDataManager.getDataSet(this.datasetId);
        } catch (MiddlewareQueryException ex) {
            throw new DatasetExporterException("Error with getting Dataset with id: " + this.studyId, ex);
        }
    	
        //this map is for mapping the columns names of the dataset to their column index in the excel sheet
        Map<String, Integer> columnsMap = new HashMap<String, Integer>(); 
        int observationSheetColumnIndex = 0;
    	
      //get the factors and their details
        VariableTypeList datasetVariableTypes = dataset.getVariableTypes();
        VariableTypeList factorVariableTypeList = datasetVariableTypes.getFactors();
        List<VariableType> factorVariableTypes = factorVariableTypeList.getVariableTypes();
        
        for(VariableType factor : factorVariableTypes) {
        	
        	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET) continue;
        	
            String factorName = factor.getLocalName();
            if(factorName != null) {
                factorName = factorName.trim();
            }
            
            //check if factor is already written as a condition
            Integer temp = columnsMap.get(factorName);
            if(temp == null && !factorName.equals("STUDY")) {
                //add entry to columns mapping
                columnsMap.put(factorName, Integer.valueOf(observationSheetColumnIndex));
                observationSheetColumnIndex++;
            }
        }
    	
    	
      //get the variates and their details
        VariableTypeList variateVariableTypeList = datasetVariableTypes.getVariates();
        List<VariableType> variateVariableTypes = variateVariableTypeList.getVariableTypes();
        HashMap<Integer, String> variateColumnsMap = new HashMap<Integer, String>();
        
        Collections.sort(variateVariableTypes, new Comparator<VariableType>(){

			@Override
			public int compare(VariableType o1, VariableType o2) {
				// TODO Auto-generated method stub
				return o1.getLocalName().compareTo(o2.getLocalName());
			}
        	
        });
        
        for(VariableType variate : variateVariableTypes) {
  
            String variateName = variate.getLocalName();
            
            if(variateName != null) {
                variateName = variateName.trim();
            }
            
            //get only the selected traits
            if (breedingViewInput.getVariatesActiveState().get(variateName).booleanValue()){
            	//add entry to columns mapping
                columnsMap.put(variateName, Integer.valueOf(observationSheetColumnIndex));
                variateColumnsMap.put(Integer.valueOf(observationSheetColumnIndex), variateName);
                observationSheetColumnIndex++;
            }

            
        }
    	
    	
    	
    	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    	  ArrayList<String[]> tableItems = new ArrayList<String[]>();
    	  List<Experiment> experiments = new ArrayList<Experiment>();
    	
    	  //int sheetRowIndex = 0;
   
          try {
              experiments = this.studyDataManager.getExperiments(this.datasetId,0,Integer.MAX_VALUE);
          } catch(Exception ex) {
             ex.printStackTrace();
          }
          
          	List keys = new ArrayList(columnsMap.keySet());
          
	  		//Sort keys by values.
	  		final Map<String, Integer> langForComp = columnsMap;
	  		Collections.sort(keys, 
	  			new Comparator<Object>(){
	  				public int compare(Object left, Object right){
	  					String leftKey = (String)left;
	  					String rightKey = (String)right;
	   
	  					Integer leftValue = (Integer)langForComp.get(leftKey);
	  					Integer rightValue = (Integer)langForComp.get(rightKey);
	  					return leftValue.compareTo(rightValue);
	  				}
	  			});
	   
	  		ArrayList<String> rowHeader = new ArrayList<String>();
	  		for(Iterator i=keys.iterator(); i.hasNext();){
	  			Object k = i.next();
	  			Integer columnIndex = columnsMap.get(k).intValue();
	              if(columnIndex >= 0) {
	                  rowHeader.add((String) k);
	              }
	  		}
	  		
	  		tableItems.add(rowHeader.toArray(new String[0]));
      
          
          for(Experiment experiment : experiments) {
        	  
        	  Variable temp = experiment.getFactors().findByLocalName("TRIALNO");
        	  
        	  boolean outerBreak = true;
        	  for (Variable factorVariables1 : experiment.getFactors().getVariables()){
        		  if (factorVariables1.getVariableType().getLocalName().trim().equalsIgnoreCase(selectedFactor)
        				  && selectedEnvironment.contains(factorVariables1.getValue())) { outerBreak=false; continue; }  else { continue;}
        	  }
        	  if (outerBreak) continue;
        	  
              ArrayList<String> row = new ArrayList<String>();
              //sheetRowIndex++;
                  
              List<Variable> factorsOfExperiments = experiment.getFactors().getVariables();
              for(Variable factorVariable : factorsOfExperiments){
                  String factorName = factorVariable.getVariableType().getLocalName();
                  if(factorName != null){
                      factorName = factorName.trim();
                  }
                  Integer columnIndexInteger = columnsMap.get(factorName); 
                  if(columnIndexInteger != null){
                      short columnIndex = columnIndexInteger.shortValue();
                      if(columnIndex >= 0) {
                          //Cell cell = PoiUtil.createCell(cellStyleForObservationSheet, row, columnIndex, CellStyle.ALIGN_CENTER, CellStyle.ALIGN_CENTER);
                          if(factorVariable.getVariableType().getStandardVariable().getDataType().getName().equals(NUMERIC_VARIABLE)){
                              double elemValue = 0;
                              if(factorVariable.getValue() != null){
                                  try{
                                      elemValue = Double.valueOf(factorVariable.getValue());
                                      
                                      if (elemValue == Double.valueOf("-1E+36")){
                                    	  row.add("");
                                      } else row.add(String.valueOf(factorVariable.getValue()));
                                      
                                  }catch(NumberFormatException ex){
                                      String value = factorVariable.getValue();
                                      if(value != null) {
                                          value = value.trim();
                                      }
                                      row.add(value);
                                  }
                              } else {
                                  String nullValue = null;
                                  row.add(nullValue);
                              }
                          } else{
                              String value = factorVariable.getValue();
                              if(value != null) {
                                  value = value.trim();
                              }
                              row.add(value);
                          }
                      }
                  }
              }
                  
              //List<Variable> variateVariables = experiment.getVariates().getVariables();
              for( Entry<Integer, String> entry : variateColumnsMap.entrySet()){
            	  String variateName = "";
            	  Variable variateVariable = experiment.getVariates().findByLocalName(entry.getValue());
            	  if (variateVariable != null){
            		  variateName = variateVariable.getVariableType().getLocalName();
                      if(variateName != null){
                          variateName = variateName.trim();
                      }
            	  }else{
            		  row.add("");
            		  continue;
            	  }
                   
                  Integer columnIndexInteger = columnsMap.get(variateName); 
                  if(columnIndexInteger != null){
                      short columnIndex = columnIndexInteger.shortValue();
                      if(columnIndex >= 0) {
                          //Cell cell = PoiUtil.createCell(cellStyleForObservationSheet, row, columnIndex, CellStyle.ALIGN_CENTER, CellStyle.ALIGN_CENTER);
                          if(variateVariable.getVariableType().getStandardVariable().getDataType().getName().equals(NUMERIC_VARIABLE)){
                              double elemValue = 0;
                              if(variateVariable.getValue() != null){
                                  try{
                                      elemValue = Double.valueOf(variateVariable.getValue());
                                      
                                      if (elemValue == Double.valueOf("-1E+36")) {
                                    	  row.add("");
                                      }else row.add(String.valueOf(elemValue));
                                      
                                  }catch(NumberFormatException ex){
                                      String value = variateVariable.getValue();
                                      if(value != null) {
                                          value = value.trim();
                                      }
                                      row.add(value);
                                  }
                              } else {
                                  
                                  row.add("");
                              }
                          } else{
                              String value = variateVariable.getValue();
                              if(value != null) {
                                  value = value.trim();
                              }else{
                            	  value = "";
                              }
                              row.add(value);
                          }
                      }
                  }
              }
              
              tableItems.add(row.toArray(new String[0]));
          }
    	
          try {

  			File csvFile = new File(filename);

  			CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR , CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
  			csvWriter.writeAll(tableItems);
  			csvWriter.flush();
  			csvWriter.close();

  		} catch (Exception e) {
  			e.printStackTrace();
 
  		}
    	
    }
    
    
}
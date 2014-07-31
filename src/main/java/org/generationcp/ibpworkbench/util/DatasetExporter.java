package org.generationcp.ibpworkbench.util;

import au.com.bytecode.opencsv.CSVWriter;

import org.generationcp.commons.util.ObjectUtil;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

@Configurable
public class DatasetExporter {

	public static final String NUMERIC_VARIABLE = "Numeric variable";
	public static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	private StudyDataManager studyDataManager;
	private Integer studyId;
	private Integer datasetId;

	public DatasetExporter(StudyDataManager studyDataManager, Integer studyId, Integer datasetId){
		this.studyDataManager = studyDataManager;
		this.studyId = studyId;
		this.datasetId = datasetId;
	}


	public void exportToCSVForBreedingView(String filename, String selectedFactor , List<String> selectedEnvironment, BreedingViewInput breedingViewInput) throws DatasetExporterException {

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

		ArrayList<String[]> tableItems = new ArrayList<String[]>();
		List<Experiment> experiments = new ArrayList<Experiment>();

		try {
			experiments = this.studyDataManager.getExperiments(this.datasetId,0,Integer.MAX_VALUE);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		List<String> keys = new ArrayList<String>(columnsMap.keySet());

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
		HashMap<String, String> headerNameAliasMap = new HashMap<String, String>();
		for(Iterator<String> i=keys.iterator(); i.hasNext();){
			String k = i.next();
			Integer columnIndex = columnsMap.get(k).intValue();
			if(columnIndex >= 0) {
				String nameSanitized = k.replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
				rowHeader.add(nameSanitized);
				headerNameAliasMap.put(nameSanitized, k); 
			}
		}

		tableItems.add(rowHeader.toArray(new String[0]));


		for(Experiment experiment : experiments) {

			boolean outerBreak = true;
			for (Variable factorVariables1 : experiment.getFactors().getVariables()){
				if (factorVariables1.getVariableType().getLocalName().trim().equalsIgnoreCase(selectedFactor)
						&& selectedEnvironment.contains(factorVariables1.getValue())) { outerBreak=false; continue; }  else { continue;}
			}
			if (outerBreak) continue;

			ArrayList<String> row = new ArrayList<String>();

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
								if (factorVariable.getVariableType().getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
										|| factorVariable.getVariableType().getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM
										){
									value = value.trim().replace(",", ";");
								}else{
									value = value.trim();
								}


							}
							row.add(value);
						}
					}
				}
			}

			
			List<Integer> variatekeys = new ArrayList<Integer>(variateColumnsMap.keySet());
			Collections.sort(variatekeys, 
					new Comparator<Object>(){
				public int compare(Object left, Object right){
					Integer leftKey = (Integer)left;
					Integer rightKey = (Integer)right;

					return leftKey.compareTo(rightKey);
				}
			});
			
			for(Object key : variatekeys){
				String variateName = "";
				Variable variateVariable = experiment.getVariates().findByLocalName(variateColumnsMap.get(key));
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

		try{
			String tempFolder = String.format("%s\\temp", 
					workbenchDataManager.getWorkbenchSetting().getInstallationDirectory());
			new File(tempFolder).mkdir();
			String fileName = tempFolder + "\\mapping.ser";
			new ObjectUtil<HashMap<String,String>>().serializeObject(headerNameAliasMap, fileName);
		}catch (Exception e) {
			e.printStackTrace();
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
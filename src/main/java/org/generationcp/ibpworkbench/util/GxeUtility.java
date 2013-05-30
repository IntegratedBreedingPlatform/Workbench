package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class GxeUtility {
    private static final Logger LOG = LoggerFactory.getLogger(GxeUtility.class);

	public static Table generateTestData(Table table, Integer numRows) throws Exception{
		
		ArrayList<Class<?>> arr =  new ArrayList<Class<?>>();
		Object item = table.addItem();
		for (Iterator i = table.getContainerPropertyIds().iterator(); i.hasNext();) {
			Property p = table.getContainerProperty(item, i.next());
			arr.add(p.getType());
		}
		table.removeItem(item);
		for (Integer c = 1;c <= numRows; c++ ){
			
			Object[] obj = new Object[arr.size()];
			for(Integer c2 = 0; c2 < arr.size(); c2++){
				obj[c2] = createObjectCaption(arr.get(c2), "Data" + c, c2);
			}
		
			System.out.println(table.addItem(obj, new Integer(c)));
					
		}
		
		return table;
	
	}
	
	public static Object createObjectCaption(Class<?> propertyType, String value, Integer colIndex) throws Exception{
	
		if (propertyType.equals(CheckBox.class) || propertyType.isInstance(CheckBox.class)){
			CheckBox o = new CheckBox();
			if (colIndex > 1){
				o.setCaption(randomInRange(1,100).toString());
			}else{
				o.setCaption(value);
			}
			
			return o;
		}else if (propertyType.equals(Label.class)){
			Label o = new Label();
			o.setCaption(value);
			o.setValue(value);
			return o;
		}else if (propertyType.equals(Link.class)){
			Link o = new Link();
			o.setCaption(value);
			return o;
		}else if (propertyType.equals(Link.class)){
			Link o = new Link();
			o.setCaption(value);
			return o;
		}else if (propertyType.equals(TextField.class)){
			TextField o = new TextField();
			o.setCaption(value);
			o.setValue(value);
			return o;
		}else if (propertyType.equals(String.class)){
			return value;
		}else if (propertyType.equals(Integer.class)){
			return new Random().nextInt(100);
		}else if (propertyType.equals(Double.class)){
			return randomInRange(1,100);
		}else{
			throw new Exception(String.format("Property Type: {%s} is not yet supported.", propertyType.toString()));
		}
	
	
	}
	
	protected static Random random = new Random();
	
	public static Double randomInRange(double min, double max) {
	  double range = max - min;
	  double scaled = random.nextDouble() * range;
	  double shifted = scaled + min;
	  return shifted; 
	
	}
	/**
	 * Generates GxE Multi-site analysis XML data, stored in IBWorkflowSystem\workspace\{PROJECT}\breeding_view\input
	 * @param tableContainer
	 * @param currentProject
	 * @param breedingViewInput
	 * @return void
	 */
	
	public static void generateXmlFieldBook(GxeInput gxeInput){
		try {
			new GxeXMLWriter(gxeInput).writeProjectXML();
		} catch (GxeXMLWriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Generates GxE Multi-site analysis output to excel, stored in IBWorkflowSystem\workspace\{PROJECT}\breeding_view\input
	 * @param gxeDataSet
	 * @param experiments
	 * @param currentProject
	 * @param xlsfilename
	 * @return File
	 */
	public static File exportGxEDatasetToBreadingViewXls(DataSet gxeDataset,List<Experiment> experiments,String locationProperty,String trialInstanceProperty,GxeEnvironment gxeEnv,List<Trait> selectedTraits, Project currentProject) {
		Workbook workbook = new HSSFWorkbook();
		Sheet defaultSheet = workbook.createSheet(gxeDataset.getName());
		
		// get the headers first
		VariableTypeList vtList = gxeDataset.getVariableTypes();
		
		//Hashtable<Integer,Integer> variableTypeIdToColNoMap = new Hashtable<Integer, Integer>();
		Hashtable<String,Integer> traitToColNoMap = new Hashtable<String, Integer>();
		
		int i = 0, j = 0;
		//Integer datasetTypeFactorId = null;
		// create header row
		Row headerRow = defaultSheet.createRow(i);
		
		// site no && site code insert to columnMap
		if (locationProperty != null && !locationProperty.isEmpty()) {
			traitToColNoMap.put(locationProperty,j);
			headerRow.createCell(j).setCellValue(locationProperty);
			j++;
		}
		
		if (trialInstanceProperty != null && !trialInstanceProperty.isEmpty()) {
			traitToColNoMap.put(trialInstanceProperty,j);
			headerRow.createCell(j).setCellValue(trialInstanceProperty);
			j++;
		}
		
		for (Trait trait : selectedTraits) {
			LOG.debug(trait.getName());
			
			//if (trait.getName().trim().)
			traitToColNoMap.put(trait.getName(),j);
			headerRow.createCell(j).setCellValue(trait.getName());
			
			j++;
		}
		
		i++;
		
		List<String> gxeEnvLabels = new ArrayList<String>();
		for (GxeEnvironmentLabel env : gxeEnv.getLabels()) {
			gxeEnvLabels.add(env.getName());
		}
		
		// create table content
		for (Experiment experiment : experiments) {
			Row row = defaultSheet.createRow(i);
			
			// site no && site code insert to columnMap
			if (locationProperty != null && !locationProperty.isEmpty()) {
				Variable var = experiment.getFactors().findByLocalName(locationProperty);
				
				if (var == null) {
					var = experiment.getVariates().findByLocalName(locationProperty);
				}
				
				if (var != null && var.getValue() != null) {
					if (!gxeEnvLabels.contains(var.getValue())) {
						continue;						
					}					
					row.createCell(traitToColNoMap.get(locationProperty)).setCellValue(var.getValue());
				}
			}
			
			if (trialInstanceProperty != null && !trialInstanceProperty.isEmpty()) {
				Variable var = experiment.getFactors().findByLocalName(trialInstanceProperty);
				//Variable traitVar = experiment.getVariates().findByLocalName(traitMapEntry`)
				
				if (var == null) {
					var = experiment.getVariates().findByLocalName(trialInstanceProperty);
				}
				
				if (var != null && var.getValue() != null)
					row.createCell(traitToColNoMap.get(trialInstanceProperty)).setCellValue(var.getValue());
			}
			
			for (Entry<String, Integer> traitMapEntry : traitToColNoMap.entrySet()) {
				Variable var = experiment.getFactors().findByLocalName(traitMapEntry.getKey());
				//Variable traitVar = experiment.getVariates().findByLocalName(traitMapEntry`)
				
				if (var == null) {
					var = experiment.getVariates().findByLocalName(traitMapEntry.getKey());
				}
				
				if (var != null && var.getValue() != null)
					row.createCell(traitMapEntry.getValue()).setCellValue(var.getValue());
				
			}
			i++;
		}
		
		// done creating the worksheet! time to create the excel file
		try {
			if (currentProject == null)
				throw new Exception("currentProject is null");
			
			// TODO NOTE: Directory location is hardcoded to workspace/{projectId-projectName/breeding_view/input}
			String dir = "workspace" + File.separator + currentProject.getProjectId().toString() + "-" + currentProject.getProjectName() + File.separator + "breeding_view" + File.separator + "input";
			
			LOG.debug("save to " + dir);
			
			new File(dir).mkdirs();
			
			File xlsFile = new File(dir + File.separator + gxeDataset.getName().trim() + ".xls");
			
			/*
			for (i = 1; !xlsFile.createNewFile(); i++) {
				String newFile = gxeDataset.getName().trim().split("\\.(?=[^\\.]+$)")[0] + "_" + i + "." + gxeDataset.getName().trim().split("\\.(?=[^\\.]+$)")[1];
				
				xlsFile = new File(dir + File.separator + newFile);
			}*/
			
			FileOutputStream fos = new FileOutputStream(xlsFile);
			workbook.write(fos);
			
			fos.close();
			
			
			return xlsFile.getAbsoluteFile();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	public static File exportGxEDatasetToBreadingViewCsv(DataSet gxeDataset,List<Experiment> experiments,String locationProperty,String trialInstanceProperty,GxeEnvironment gxeEnv,List<Trait> selectedTraits, Project currentProject) {
		ArrayList<String[]> tableItems = new ArrayList<String[]>();

		// get the headers first
		VariableTypeList vtList = gxeDataset.getVariableTypes();
		
		//Hashtable<Integer,Integer> variableTypeIdToColNoMap = new Hashtable<Integer, Integer>();
		Hashtable<String,Integer> traitToColNoMap = new Hashtable<String, Integer>();
		
		int i = 0, j = 0;
		//Integer datasetTypeFactorId = null;
		// create header row
		List<String> headerRow = new ArrayList<String>();
		// site no && site code insert to columnMap
		if (locationProperty != null && !locationProperty.isEmpty()) {
			traitToColNoMap.put(locationProperty,j);
		
			headerRow.add(j,locationProperty);
			j++;
		}
		
		if (trialInstanceProperty != null && !trialInstanceProperty.isEmpty()) {
			traitToColNoMap.put(trialInstanceProperty,j);
			
			headerRow.add(j,trialInstanceProperty);
			j++;
		}
		
		for (Trait trait : selectedTraits) {
			LOG.debug(trait.getName());
			
			//if (trait.getName().trim().)
			traitToColNoMap.put(trait.getName(),j);
			
			headerRow.add(j,trait.getName());
			
			j++;
		}
		
		String[] headerRowArr = new String[headerRow.size()];
		tableItems.add(i,headerRow.toArray(headerRowArr));
		
		i++;
		
		List<String> gxeEnvLabels = new ArrayList<String>();
		for (GxeEnvironmentLabel env : gxeEnv.getLabels()) {
			gxeEnvLabels.add(env.getName());
		}
		
		// create table content
		for (Experiment experiment : experiments) {
			List<String> row = new ArrayList<String>();
			
			// site no && site code insert to columnMap
			if (locationProperty != null && !locationProperty.isEmpty()) {
				Variable var = experiment.getFactors().findByLocalName(locationProperty);
				
				if (var == null) {
					var = experiment.getVariates().findByLocalName(locationProperty);
				}
				
				if (var != null && var.getValue() != null) {
					if (!gxeEnvLabels.contains(var.getValue())) {
						continue;						
					}					
					row.add(traitToColNoMap.get(locationProperty),var.getValue());
				}
			}
			
			if (trialInstanceProperty != null && !trialInstanceProperty.isEmpty()) {
				Variable var = experiment.getFactors().findByLocalName(trialInstanceProperty);
				//Variable traitVar = experiment.getVariates().findByLocalName(traitMapEntry`)
				
				if (var == null) {
					var = experiment.getVariates().findByLocalName(trialInstanceProperty);
				}
				
				if (var != null && var.getValue() != null)
					row.add(traitToColNoMap.get(trialInstanceProperty),var.getValue());
				
			}
			
			for (Entry<String, Integer> traitMapEntry : traitToColNoMap.entrySet()) {
				Variable var = experiment.getFactors().findByLocalName(traitMapEntry.getKey());
				//Variable traitVar = experiment.getVariates().findByLocalName(traitMapEntry`)
				
				if (var == null) {
					var = experiment.getVariates().findByLocalName(traitMapEntry.getKey());
				}
				
				if (var != null && var.getValue() != null)
					row.add(traitMapEntry.getValue(),var.getValue());
				
			}
			
			String[] rowArr = new String[row.size()];
			tableItems.add(i,row.toArray(rowArr));
			
			i++;
		}
		
		try {

			if (currentProject == null)
				throw new Exception("currentProject is null");

			String dir = "workspace" + File.separator + currentProject.getProjectId().toString() + "-" + currentProject.getProjectName() + File.separator + "breeding_view" + File.separator + "input";

			LOG.debug("save to" + dir);

			new File(dir).mkdirs();

			File csvFile = new File(dir + File.separator + gxeDataset.getName()+ ".csv");

			CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));
			csvWriter.writeAll(tableItems);
			csvWriter.flush();
			csvWriter.close();

			return csvFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

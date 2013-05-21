package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

public class GxEUtility {
    private static final Logger LOG = LoggerFactory.getLogger(GxEUtility.class);

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
	
	public static void generateXmlFieldBook(Container tableContainer, Project currentProject, BreedingViewInput breedingViewInput){
		
		List<Trait> selectedTraits = new ArrayList<Trait>();
		
		tableContainer.getContainerPropertyIds();
		
		try {
			new GxEXMLWriter(breedingViewInput).writeProjectXML(selectedTraits);
		} catch (GxEXMLWriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Generates GxE Multi-site analysis output to excel, stored in IBWorkflowSystem\workspace\{PROJECT}\breeding_view\input
	 * TODO: modify logic based on tableContainer data structure
	 * @param tableContainer
	 * @param currentProject
	 * @param xlsfilename
	 * @return generated file
	 */
	public static File exportGeneratedXlsFieldbook(Container tableContainer,Project currentProject,String xlsfilename) {
		Workbook workbook = new HSSFWorkbook();
		Sheet defaultSheet = workbook.createSheet();
		
		Object[] itemIds = tableContainer.getItemIds().toArray();
		Object[] propertyIds = tableContainer.getContainerPropertyIds().toArray(); 
		
		
		// grab only the indexes of the header column where there is a check
		List<Integer> colIndexes = new ArrayList<Integer>();
		for (int i = 1; i < propertyIds.length; i++) {
			Property headerP = tableContainer.getContainerProperty(itemIds[0],propertyIds[i]);
			Object headerCol = headerP.getValue();
			
			if (headerCol instanceof CheckBox && (Boolean)((CheckBox)headerCol).getValue()
					|| headerCol instanceof Label)
			{
				colIndexes.add(i);
			}
		}
		
		
		// First row is headers checkbox, first column is selection checkbox
		
		int k=0,l=0; // write pointer cell index to the excel workbook
		for (int i = 1;i < itemIds.length;i++) {

			Row row = defaultSheet.createRow(k);
			boolean nextRow = false;
			
			for (Integer j : colIndexes) {
				Property currentCellP = tableContainer.getContainerProperty(itemIds[i],propertyIds[j]);
				Object currentCell = currentCellP.getValue();
				
				Property firstColCellP = tableContainer.getContainerProperty(itemIds[i], propertyIds[0]);
				if (!(Boolean)((CheckBox)firstColCellP.getValue()).getValue())
					break;
				else
					nextRow = true;
				
				if (currentCell instanceof Label) {
					String currentCellString = ((Label)currentCell).toString();
					
					row.createCell(l).setCellValue(currentCellString);
					
				} else if (currentCell instanceof CheckBox) {
					String currentCellString = ((CheckBox)currentCell).getCaption();
					row.createCell(l).setCellValue(currentCellString);
				}
				defaultSheet.autoSizeColumn(l);
				l++;
			}
			l=0;
			
			if (nextRow) k++;
		}
		
		try {
			if (currentProject == null)
				throw new Exception("currentProject is null");
			
			// TODO NOTE: Directory location is hardcoded to workspace/{projectId-projectName/breeding_view/input}
			String dir = "c:\\workspace" + File.separator + currentProject.getProjectId().toString() + "-" + currentProject.getProjectName() + File.separator + "breeding_view" + File.separator + "input";
			
			//LOG.debug("save to" + dir);
			
			new File(dir).mkdirs();
			
			File xlsFile = new File(dir + File.separator + xlsfilename);
			
			for (int i = 1; !xlsFile.createNewFile(); i++) {
				String newFile = xlsfilename.split("\\.(?=[^\\.]+$)")[0] + "_" + i + "." + xlsfilename.split("\\.(?=[^\\.]+$)")[1];
				
				xlsFile = new File(dir + File.separator + newFile);
			}
			
			FileOutputStream fos = new FileOutputStream(xlsFile);
			workbook.write(fos);
			
			fos.close();
			
			
			return xlsFile;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
		
	}
}

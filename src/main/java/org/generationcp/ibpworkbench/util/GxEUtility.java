package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

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
	 * Generates the GxE Input CSV<br/><br/>
	 * Sets default csv file to <b>"GxE_input.csv"</b> and does not include the first row and column of the table
	 * @param mainWindow
	 * @param tableContainer
	 * @param gxeHeaders
	 */
	public static File generateGxEInputCSV(Window mainWindow, Container tableContainer,Project currentProject, String[] gxeHeaders) {
		return generateGxEInputCSV(mainWindow, tableContainer,currentProject, gxeHeaders,"GxE_input.csv",true,true);
	}
	
	/**
	 * Generates the GxE Input CSV
	 * @param mainWindow
	 * @param tableContainer
	 * @param gxeHeaders
	 * @param csvfilename
	 * @param skipFirstRow
	 * @param skipFirstColumn
	 */
	public static File generateGxEInputCSV(Window mainWindow, Container tableContainer,Project currentProject,  String[] gxeHeaders,String csvfilename, boolean skipFirstRow,boolean skipFirstColumn) {
		ArrayList<String[]> tableItems = new ArrayList<String[]>();
			
		Iterator<?> itemIdsIterator = tableContainer.getItemIds().iterator();
		itemIdsIterator.next();	// skip first row
		
		// add the headers
		if (skipFirstRow) tableItems.add(gxeHeaders);
		
		while(itemIdsIterator.hasNext()) {
			Object itemId = itemIdsIterator.next();
			ArrayList<String> cellList = new ArrayList<String>();
			
			Iterator<?> propertyIdsIterator = tableContainer.getContainerPropertyIds().iterator();
			
			// skip first column
			if (skipFirstColumn) {
				Object propertyId = propertyIdsIterator.next();
				Property cellItemProperty = tableContainer.getContainerProperty(itemId, propertyId);
				Object cellItemValue = cellItemProperty.getValue();
				
				if (!(Boolean) ((CheckBox) cellItemValue).getValue()) {
					continue;	// skip entire row
				}
			}
			
			while (propertyIdsIterator.hasNext()) {
				Object propertyId = propertyIdsIterator.next();

				Property cellItemProperty = tableContainer.getContainerProperty(itemId, propertyId);
				
				Object cellItemValue = cellItemProperty.getValue();
				String cellItemString = "";
				
				if (cellItemValue instanceof CheckBox ) {
					if ((Boolean) ((CheckBox) cellItemValue).getValue())
						cellItemString = ((CheckBox) cellItemValue).getCaption();
					else
						cellItemString = "";
				} else if (cellItemValue instanceof Label)
					cellItemString = cellItemValue.toString();
					
				cellList.add(cellItemString);
			}
			
			String[] row = new String[cellList.size()];
			cellList.toArray(row);
			
			tableItems.add(row);
		}
		
		try {
			if (currentProject == null)
				throw new Exception("currentProject is null");
			
			/*
			final ByteArrayOutputStream content = new ByteArrayOutputStream();
			
			CSVWriter csvWriter = new CSVWriter(new PrintWriter(content),',');
			
			csvWriter.writeAll(tableItems);
			csvWriter.flush();
		
			final ByteArrayInputStream bis = new ByteArrayInputStream(content.toByteArray());
			csvWriter.close();
		
			FileResource fr = new FileResource(new File(csvfilename), mainWindow.getApplication()) {
				private static final long serialVersionUID = 765143030552676513L;
				@Override
				public DownloadStream getStream() {
					final DownloadStream ds = new DownloadStream(bis, getMIMEType(), getFilename());
					ds.setParameter("Content-Disposition", "attachment; filename="+getFilename());
					ds.setCacheTime(getCacheTime());
					return ds;
				}
			};
			
			mainWindow.open(fr);
			*/
			
			// TODO NOTE: Directory location is hardcoded to workspace/{projectId-projectName/breeding_view/input}
			String dir = "workspace" + File.separator + currentProject.getProjectId().toString() + "-" + currentProject.getProjectName() + File.separator + "breeding_view" + File.separator + "input";
			
			//LOG.debug("save to" + dir);
			
			new File(dir).mkdirs();
			
			File csvFile = new File(dir + File.separator + csvfilename);
			
			for (int i = 1; !csvFile.createNewFile(); i++) {
				String newFile = csvfilename.split("\\.(?=[^\\.]+$)")[0] + "_" + i + "." + csvfilename.split("\\.(?=[^\\.]+$)")[1];
				
				csvFile = new File(dir + File.separator + newFile);
			}
			
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
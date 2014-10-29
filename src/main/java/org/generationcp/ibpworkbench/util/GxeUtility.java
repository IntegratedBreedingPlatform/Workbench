package org.generationcp.ibpworkbench.util;

import au.com.bytecode.opencsv.CSVWriter;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.Map.Entry;

@Configurable
public class GxeUtility {
	
	@Autowired
	private StudyDataManager studyDataManager;
	
    private static final Logger LOG = LoggerFactory.getLogger(GxeUtility.class);
	
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
			GxeXMLWriter writer = new GxeXMLWriter(gxeInput);
			writer.writeProjectXML();
			
		} catch (GxeXMLWriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static File exportGxEDatasetToBreadingViewCsv(DataSet gxeDataset,List<Experiment> experiments,String environmentName, String environmentGroup,String genotypeName ,GxeEnvironment gxeEnv,List<Trait> selectedTraits, Project currentProject) {
		ArrayList<String[]> tableItems = new ArrayList<String[]>();
		
		Hashtable<String,Integer> traitToColNoMap = new Hashtable<String, Integer>();
		
		int i = 0, j = 0;
		// create header row
		List<String> headerRow = new ArrayList<String>();
		// site no && site code insert to columnMap
		if (environmentName != null && !environmentName.isEmpty()) {
			traitToColNoMap.put(environmentName,j);
			headerRow.add(environmentName.replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
			j++;
		}
		
		if (!environmentGroup.equalsIgnoreCase(environmentName) && environmentGroup != null && !environmentGroup.isEmpty() && !environmentGroup.equalsIgnoreCase("none")) {
			traitToColNoMap.put(environmentGroup,j);
			headerRow.add(environmentGroup.replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
			j++;
		}
		
		traitToColNoMap.put(genotypeName,j);
		headerRow.add(genotypeName.replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
		j++;
				
		for (Trait trait : selectedTraits) {

			traitToColNoMap.put(trait.getName(),j);			
			headerRow.add(j,trait.getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
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
			String[] row = new String[headerRow.size()];
			
			
			// site no && site code insert to columnMap
			if (environmentName != null && !environmentName.isEmpty()) {
				Variable var = experiment.getFactors().findByLocalName(environmentName);
				
				if (var == null) {
					var = experiment.getVariates().findByLocalName(environmentName);
				}
				
				if (var != null && var.getValue() != null) {
					if (!gxeEnvLabels.contains(var.getValue())) {
						continue;						
					}					
					row[traitToColNoMap.get(environmentName)] = var.getValue().replace(",", ";");
				}
			}
			
		
			for (Entry<String, Integer> traitMapEntry : traitToColNoMap.entrySet()) {
				Variable var = experiment.getFactors().findByLocalName(traitMapEntry.getKey());

				if (var == null) {
					var = experiment.getVariates().findByLocalName(traitMapEntry.getKey());
				}
				
				if (var != null && var.getValue() != null && !var.getValue().trim().matches("\\-1(\\.0+)?(E|e)(\\+36)")) {
                    row[traitMapEntry.getValue()] = var.getValue().replace(",", ";");
                }
				
			}
			
			tableItems.add(i,row);
			
			i++;
		}
		
		try {

			if (currentProject == null) {
                throw new Exception("currentProject is null");
            }

			String dir = "workspace" + File.separator + currentProject.getProjectName() + File.separator + "breeding_view" + File.separator + "input";

			LOG.debug("save to" + dir);

			new File(dir).mkdirs();

			File csvFile = new File(dir + File.separator + gxeDataset.getName()+ ".csv");

			CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR , CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
			csvWriter.writeAll(tableItems);
			csvWriter.flush();
			csvWriter.close();

			return csvFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public static File exportTrialDatasetToSummaryStatsCsv(DataSet trialDataSet, List<Experiment> experiments, String environmentName, List<Trait> selectedTraits, Project currentProject) throws MiddlewareQueryException {
		
		ArrayList<String[]> tableItems = new ArrayList<String[]>();
		
		String[] header = new String[] {
				environmentName
				,"Trait"
				,"NumValues"
				,"NumMissing"
				,"Mean"
				,"Variance"
				,"SD"
				,"Min"
				,"Max"
				,"Range"
				,"Median"
				,"LowerQuartile"
				,"UpperQuartile"
				,"MeanRep"
				,"MinRep"
				,"MaxRep"
				,"MeanSED"
				,"MinSED"
				,"MaxSED"
				,"MeanLSD"
				,"MinLSD"
				,"MaxLSD"
				,"CV"
				,"Heritability"
				,"WaldStatistic"
				,"WaldDF"
				,"Pvalue"

		};
		
		tableItems.add(header);
		
		for (Experiment exp : experiments){
			
			Map<String, Variable> map = exp.getVariatesMap();
			
			for (Trait trait : selectedTraits){
				
				List<String> row = new ArrayList<String>();
				row.add(exp.getFactors().findByLocalName(environmentName).getValue());
				row.add(trait.getName().replaceAll(DatasetExporter.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_"));
				
				for (int i = 2; i < header.length; i++){
					boolean existsFlag = false;
					for (Variable variable : map.values()){
						if (variable.getVariableType().getLocalName().equals(trait.getName().replace("_Means", "") + "_" + header[i])){
							row.add(variable.getValue());
							existsFlag = true;
							break;
						}
					}
					if (!existsFlag){
						row.add("");
					}	
				}
				
				tableItems.add(row.toArray(new String[0]));
			}
			
		}

	
		try {

			if (currentProject == null) {
                throw new Exception("currentProject is null");
            }

			String dir = "workspace" + File.separator + currentProject.getProjectName() + File.separator + "breeding_view" + File.separator + "input";

			LOG.debug("save to " + dir);

			new File(dir).mkdirs();

			File csvFile = new File(dir + File.separator + trialDataSet.getName() + "_SummaryStats.csv");

			CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile), CSVWriter.DEFAULT_SEPARATOR , CSVWriter.NO_QUOTE_CHARACTER, "\r\n");
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

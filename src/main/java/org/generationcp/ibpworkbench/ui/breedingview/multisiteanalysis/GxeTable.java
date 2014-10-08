package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class GxeTable extends Table {

    private final static Logger LOG = LoggerFactory.getLogger(GxeTable.class);
	
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                return e1.getValue().compareTo(e2.getValue());
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
	private static final long serialVersionUID = 1274131837702381485L;
	
	public static final int CELL_CHECKBOX = 1;
	public static final int CELL_CHECKBOX_HEADER = 2;
	public static final int CELL_CHECKBOX_COLUMN = 3;
	public static final int CELL_CHECKBOX_ROW = 4;
	public static final int CELL_CHECKBOX_ALL = 5;
	public static final int CELL_LABEL = 6;
	
	private StudyDataManager studyDataManager;
	private List<String> columnNames = new ArrayList<String>(); 
	private Map<Integer, String> factorLocalNames = new TreeMap<Integer, String>();
	private Map<Integer, String> variateLocalNames = new TreeMap<Integer, String>();
	private Map<String, Boolean> variatesCheckBoxState = new HashMap<String, Boolean>();

	private Map<String, Map<String, String>> heritabilityValues = new HashMap<String, Map<String, String>>();
	
	private String trialInstanceFactorName = "";
	
	private String selectedEnvFactorName = "";
	private String selectedEnvGroupFactorName = "";

	private int meansDataSetId;

	private DataSet meansDataSet;

	private List<Experiment> exps;

	private VariableTypeList germplasmFactors = new VariableTypeList();
	
	private Property.ValueChangeListener gxeCheckBoxColumnListener;

	public GxeTable(StudyDataManager studyDataManager, Integer studyId, String selectedEnvFactorName, String selectedEnvGroupFactorName, Map<String, Boolean> variatesCheckBoxState, Property.ValueChangeListener gxeCheckBoxColumnListener) {
		this.selectedEnvFactorName = selectedEnvFactorName;
		this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
		this.studyDataManager = studyDataManager;
		this.variatesCheckBoxState = variatesCheckBoxState;
		this.gxeCheckBoxColumnListener = gxeCheckBoxColumnListener;
		initializeTable();
		fillTableWithDataset(studyId);
	}


	private void initializeTable() {

		this.setImmediate(true);
		this.setWidth("100%");
		this.setHeight("453px");
		this.setContainerDataSource(new IndexedContainer());
		this.setEditable(true);
		this.setColumnReorderingAllowed(true);
		this.setSortDisabled(true);
		this.setColumnCollapsingAllowed(true);


	}


	private void initializeHeader(Map<Integer, String> factors, Map<Integer, String> variates, Container container) {


		
		List<String> factorsList = new ArrayList<String>(factors.values());
		List<String> variatesList = new ArrayList<String>(variates.values());
		
		Collections.sort(variatesList);

		columnNames.add(" ");
		columnNames.addAll(factorsList);
		columnNames.addAll(variatesList);
		
		List<String> columnHeaders = new ArrayList<String>();
		for (String s : columnNames){
			columnHeaders.add(s.replace("_Means", ""));
		}
		
		this.setVisibleColumns(columnNames.toArray(new Object[0]));
		this.setColumnHeaders(columnHeaders.toArray(new String[0]));
	
	}

	private void createRow(int rowIndex, TableItems[] tableItems, Container container) {

		Object[] obj = new Object[tableItems.length];

		for (int i = 0; i < tableItems.length; i++) {

			if (tableItems[i].getType() == (GxeTable.CELL_CHECKBOX)) {

				CheckBox cb = new CheckBox();
				cb.setCaption(tableItems[i].getLabel());
				cb.setValue(tableItems[i].getValue());
				cb.setImmediate(true);
				cb.addListener(gxeCheckBoxColumnListener);

				obj[i] = cb;
			} else if (tableItems[i].getType() == (GxeTable.CELL_LABEL)) {
				obj[i] = tableItems[i].getLabel();
			}

			
		}

		this.addItem(obj, rowIndex);

	}
	
	private void fillTableWithDataset(Integer studyId) {

		Container container = this.getContainerDataSource();
		container.removeAllItems();
		
		container.addContainerProperty(" ", CheckBox.class, null);
		
		
		List<DataSet> plotDatasets = new ArrayList<DataSet>();
		
		try {
			
			List<DatasetReference> datasetRefs = studyDataManager.getDatasetReferences(studyId);
			for (DatasetReference dsRef : datasetRefs){
				DataSet ds = studyDataManager.getDataSet(dsRef.getId());
				
				if (ds.getDataSetType() != DataSetType.MEANS_DATA){
					
					Iterator<VariableType> itrFactor = ds.getVariableTypes().getFactors().getVariableTypes().iterator();
					while(itrFactor.hasNext()){
						VariableType f = itrFactor.next();
						if (f.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()){
							trialInstanceFactorName = f.getLocalName(); break;
						}
					}
					
					Iterator<VariableType> itrVariates = ds.getVariableTypes().getVariates().getVariableTypes().iterator();
					while(itrVariates.hasNext()){
						if (itrVariates.next().getLocalName().contains("_Heritability")){
							plotDatasets.add(ds); break;
						}
					}
				
					
				}
					
			}
			
			
			if (plotDatasets.size() > 0){
				List<Experiment> exps = studyDataManager.getExperiments(plotDatasets.get(0).getId(), 0, Integer.MAX_VALUE);
				for (Experiment exp : exps){
					
					String envName = exp.getFactors().findByLocalName(trialInstanceFactorName).getValue();
					
					Map<String, String> vals = new HashMap<String, String>();
					
					for (Entry<String, Boolean> entry : getVariatesCheckBoxState().entrySet()){
						String name = entry.getKey().replace("_Means", "_Heritability");
						Variable var = exp.getVariates().findByLocalName(name);
						if (var != null){
							//heritability value
							vals.put(entry.getKey(), var.getValue().toString());
						}	
		
					}
					
					getHeritabilityValues().put(envName, vals);
					
				}
			}
			
		} catch (MiddlewareQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
		
		
		
		
		HashSet<String> envNames = new HashSet<String>();
		
		try {
			List<DataSet> meansDataSets = studyDataManager.getDataSetsByType(
					studyId, DataSetType.MEANS_DATA);
			if (meansDataSets != null) {
				if (meansDataSets.size() > 0) {
					
					meansDataSet = meansDataSets.get(0);
					meansDataSetId = meansDataSet.getId();
					
					TrialEnvironments envs = studyDataManager.getTrialEnvironmentsInDataset(meansDataSetId);
					//get the SITE NAME and SITE NO
					VariableTypeList trialEnvFactors = meansDataSet.getVariableTypes().getFactors();
					
					for(VariableType f : trialEnvFactors.getVariableTypes()){
						
						//Always Show the TRIAL INSTANCE Factor
						if (f.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getRank(), f.getLocalName());
						}
						
						//Selected Environment Name
						if (f.getLocalName().equalsIgnoreCase(selectedEnvFactorName)){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getRank(), f.getLocalName());
						}
						
						//Selected Environment Group Name
						if (f.getLocalName().equalsIgnoreCase(selectedEnvGroupFactorName)){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getRank(), f.getLocalName());
						}
						
					}
					
					germplasmFactors.addAll(meansDataSet.getFactorsByPhenotypicType(PhenotypicType.GERMPLASM));
					//get the Variates
					VariableTypeList variates = meansDataSet.getVariableTypes().getVariates();
					for(VariableType v : variates.getVariableTypes()){
						container.addContainerProperty(v.getLocalName(), Label.class, null);
						if (!v.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate") 
								&& !v.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate (" + v.getLocalName().replace("_UnitErrors", "") + ")") 
								&& !v.getStandardVariable().getMethod().getName().equalsIgnoreCase("ls blups")){
							if (variatesCheckBoxState.get(v.getLocalName()))
								variateLocalNames.put(v.getRank(), v.getLocalName());
						}
					}
					
					
					initializeHeader(factorLocalNames, variateLocalNames, container);
					
					//generate the rows
					exps = studyDataManager.getExperiments(meansDataSetId, 0, Integer.MAX_VALUE);
					
					int rowCounter = 3;
					
					for (Experiment exp : exps){
						
						String locationValTrial = exp.getFactors().findByLocalName(trialInstanceFactorName).getValue();
						String locationVal = exp.getFactors().findByLocalName(selectedEnvFactorName).getValue();
						if (envNames.contains(locationVal)) continue;
						
						
						TableItems[] row = new TableItems[factorLocalNames.size()+variateLocalNames.size()+1];
						
						
						row[0] = new TableItems();
						row[0].setType(GxeTable.CELL_CHECKBOX);
						row[0].setLabel(" ");
						row[0].setValue(true);
						
						int cellCounter = 1;
						int colIndexEnvFactorName = 0;
						
						
						for (Map.Entry<Integer, String> f : factorLocalNames.entrySet()){
							String fValue = exp.getFactors().findByLocalName(f.getValue()).getValue();
							if (f.getValue().equalsIgnoreCase(selectedEnvFactorName)) {envNames.add(fValue); colIndexEnvFactorName = cellCounter;}
							row[cellCounter] = new TableItems();
							row[cellCounter].setLabel(fValue);
							row[cellCounter].setType(GxeTable.CELL_LABEL);
							cellCounter++;
						}
						
						
						for (Iterator<Entry<Integer, String>> v  = entriesSortedByValues(variateLocalNames).iterator() ; v.hasNext();){
							
							Entry<Integer, String> x = v.next();
							
							row[cellCounter] = new TableItems();
							Variable var = exp.getVariates().findByLocalName(x.getValue());
							int varKey = 0;
							if (var != null){
								varKey = var.getVariableType().getId();
							}
							String meansData = "";
							try{
								
								meansData = String.valueOf(studyDataManager.countStocks(
										meansDataSetId
										,envs.findOnlyOneByLocalName(selectedEnvFactorName, row[colIndexEnvFactorName].getLabel()).getId()
										,varKey
											)
										);
								
								String heritabilityVal = getHeritabilityValues().get(locationValTrial).get(x.getValue());
								if (heritabilityVal != null){
									meansData = String.format("%s (%s)", meansData, heritabilityVal);
								}
								
							}catch(Exception e){
								LOG.debug("Error in getting the means data.");
								e.printStackTrace();
							}
							
							row[cellCounter].setLabel(meansData);
							row[cellCounter].setValue(true);
							row[cellCounter].setType(GxeTable.CELL_LABEL);
							cellCounter++;
						}
						
						rowCounter++;
						createRow(rowCounter, row, container);
					}					
					
				}
			}

		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}


	public GxeEnvironment getGxeEnvironment() {
		GxeEnvironment gxeEnvironment = new GxeEnvironment();
		List<GxeEnvironmentLabel> environmentLabels = new ArrayList<GxeEnvironmentLabel>();
		
		Object[] obj = this.getContainerDataSource().getItemIds().toArray();
		
		for (Integer i = 0; i < obj.length; i++){
			Property cb_column = this.getContainerProperty(obj[i], " ");
			Property location_column = this.getContainerProperty(obj[i], selectedEnvFactorName);
			if((Boolean)((CheckBox) cb_column.getValue()).getValue()){
				GxeEnvironmentLabel environmentLabel = new GxeEnvironmentLabel();
				environmentLabel.setName(((Label)location_column.getValue()).getValue().toString());
				environmentLabel.setActive(true);
				environmentLabels.add(environmentLabel);
			}
		}
		
		gxeEnvironment.setLabel(environmentLabels);
		
		return gxeEnvironment;
		
	}
	
	public List<Environment> getSelectedEnvironments() {
		List<Environment> selectedEnvironments = new ArrayList<Environment>();
		
		Object[] obj = this.getContainerDataSource().getItemIds().toArray();
		
		for (Integer i = 0; i < obj.length; i++){
			Property cb_column = this.getContainerProperty(obj[i], " ");
			Property location_column = this.getContainerProperty(obj[i], selectedEnvFactorName);
			Property trialno_column = this.getContainerProperty(obj[i], trialInstanceFactorName);
			if((Boolean)((CheckBox) cb_column.getValue()).getValue()){
				Environment environment = new Environment();
				environment.setName(((Label)location_column.getValue()).getValue().toString());
				environment.setActive(true);
				environment.setTrialno(((Label) trialno_column.getValue()).getValue().toString());
				selectedEnvironments.add(environment);
			}
		}
	
		return selectedEnvironments;
		
	}


	public int getMeansDataSetId() {
		return meansDataSetId;
	}
	
	public DataSet getMeansDataSet() {
		return meansDataSet;
	}
	
	
	public List<Experiment> getExperiments() {
		return exps;
	}
	
	public String getEnvironmentName(){
		return selectedEnvFactorName;
	}
	
	public List<VariableType> getGermplasmFactors() {
		return germplasmFactors.getVariableTypes();
	}
	
	public VariableTypeList getEntryCodeFactor() {
		return meansDataSet.getFactorsByProperty(TermId.ENTRY_NUMBER_STORAGE.getId());
	}


	public Map<String, Boolean> getVariatesCheckBoxState() {
		return variatesCheckBoxState;
	}


	public void setVariatesCheckBoxState(Map<String, Boolean> variatesCheckBoxState) {
		this.variatesCheckBoxState = variatesCheckBoxState;
	}


	public Map<String, Map<String, String>> getHeritabilityValues() {
		return heritabilityValues;
	}


	public void setHeritabilityValues(Map<String, Map<String, String>> heritabilityValues) {
		this.heritabilityValues = heritabilityValues;
	}
}



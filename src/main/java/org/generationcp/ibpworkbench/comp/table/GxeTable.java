package org.generationcp.ibpworkbench.comp.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class GxeTable extends Table {
	
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

	private String selectedEnvFactorName = "";

	private int meansDataSetId;

	private DataSet meansDataSet;

	private List<Experiment> exps;

	private VariableTypeList germplasmFactors = new VariableTypeList();

	public GxeTable(StudyDataManager studyDataManager, Integer studyId, String selectedEnvFactorName) {
		this.selectedEnvFactorName = selectedEnvFactorName;
		this.studyDataManager = studyDataManager;
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


	}


	private void initializeHeader(Map<Integer, String> factors, Map<Integer, String> variates, Container container) {


		
		List<String> factorsList = new ArrayList<String>(factors.values());
		List<String> variatesList = new ArrayList<String>(variates.values());
		
		Collections.sort(factorsList);
		Collections.sort(variatesList);
		
		
		int cnt = 0;
		TableItems[] rowHeaders = new TableItems[factors.size()+variates.size()+1];
		rowHeaders[cnt] = new TableItems();
		rowHeaders[cnt].setType(GxeTable.CELL_CHECKBOX_ALL);
		rowHeaders[cnt].setLabel("SELECT ALL");
		rowHeaders[cnt].setRowId(1);
		rowHeaders[cnt].setValue(true);
		
		cnt++;
		
		for( String f : factorsList ){
			rowHeaders[cnt] = new TableItems();
			rowHeaders[cnt].setType(GxeTable.CELL_LABEL);
			rowHeaders[cnt].setLabel(f);
			rowHeaders[cnt].setRowId(1);
			rowHeaders[cnt].setValue(true);
			cnt++;
		}
		
		for( String v : variatesList ){
			rowHeaders[cnt] = new TableItems();
			rowHeaders[cnt].setType(GxeTable.CELL_CHECKBOX_COLUMN);
			rowHeaders[cnt].setLabel(v);
			rowHeaders[cnt].setRowId(1);
			rowHeaders[cnt].setValue(true);
			cnt++;
		}
		
		columnNames.add(" ");
		columnNames.addAll(factorsList);
		columnNames.addAll(variatesList);

		this.setVisibleColumns(columnNames.toArray(new Object[0]));
		this.setColumnHeaders(columnNames.toArray(new String[0]));

		createRow(1, rowHeaders, this.getContainerDataSource());
	
	}

	private void createRow(int rowIndex, TableItems[] tableItems, Container container) {

		Object[] obj = new Object[tableItems.length];

		for (int i = 0; i < tableItems.length; i++) {

			if (tableItems[i].getType() == (GxeTable.CELL_CHECKBOX)) {

				CheckBox cb = new CheckBox();
				if (rowIndex != 1) {
					cb.addStyleName("hidecheckbox");
				}
				cb.setCaption(tableItems[i].getLabel());
				cb.setValue(tableItems[i].getValue());

				obj[i] = cb;
			} else if (tableItems[i].getType() == (GxeTable.CELL_LABEL)) {
				obj[i] = tableItems[i].getLabel();
			} else if (tableItems[i].getType() == (GxeTable.CELL_CHECKBOX_ALL)) {
				GxeCheckBoxGroup og = new GxeCheckBoxGroup(GxeTable.CELL_CHECKBOX_ALL, rowIndex, this);
				
				og.setImmediate(true);

				//og.addListener(new GxeAllCheckboxListener(columnNames.toArray(new String[0]), this
				//		.getContainerDataSource()));

				obj[i] = og;
			}

			// column checkbox
			else if (tableItems[i].getType() == (GxeTable.CELL_CHECKBOX_COLUMN)) {
				CheckBox cb = new CheckBox();
				cb.setCaption(tableItems[i].getLabel());
				cb.setValue(tableItems[i].getValue());

				cb.setImmediate(true);

				cb.addListener(new GxeColumnCheckboxListener(columnNames.toArray(new String[0]), this
						.getContainerDataSource()));

				obj[i] = cb;
			}

			// row checkbox
			else if (tableItems[i].getType() == (GxeTable.CELL_CHECKBOX_ROW)) {
				CheckBox cb = new CheckBox();
				cb.setCaption(tableItems[i].getLabel());
				cb.setValue(tableItems[i].getValue());

				cb.setImmediate(true);

				cb.addListener(new GxeRowCheckboxListener(columnNames.toArray(new String[0]), this.getContainerDataSource()));

				obj[i] = cb;
			}
		}

		this.addItem(obj, rowIndex);

	}
	
	private void fillTableWithDataset(Integer studyId) {

		Container container = this.getContainerDataSource();
		container.removeAllItems();
		
		container.addContainerProperty(" ", GxeCheckBoxGroup.class, new GxeCheckBoxGroup(GxeTable.CELL_CHECKBOX_ALL, 0, null));
		
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
					VariableTypeList trialEnvFactors = meansDataSet.getFactorsByPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
					
					for(VariableType f : trialEnvFactors.getVariableTypes()){
						//SITE_NAME
						if (f.getLocalName().equalsIgnoreCase(selectedEnvFactorName)){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getRank(), f.getLocalName());
						}
						/**SITE_NO
						if (f.getStandardVariable().getProperty().getName().equalsIgnoreCase("trial instance")){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getId(), f.getLocalName());
							
						}**/
					}
					
					germplasmFactors.addAll(meansDataSet.getFactorsByPhenotypicType(PhenotypicType.GERMPLASM));
					//get the Variates
					VariableTypeList variates = meansDataSet.getVariableTypes().getVariates();
					for(VariableType v : variates.getVariableTypes()){
						container.addContainerProperty(v.getLocalName(), CheckBox.class, new CheckBox());
						if (!v.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate"))
							variateLocalNames.put(v.getRank(), v.getLocalName());
					}
					
					
					initializeHeader(factorLocalNames, variateLocalNames, container);
					
					//generate the rows
					exps = studyDataManager.getExperiments(meansDataSetId, 0, Integer.MAX_VALUE);
					
					int rowCounter = 2;
					
					for (Experiment exp : exps){
						
						String locationVal = exp.getFactors().findByLocalName(selectedEnvFactorName).getValue();
						if (envNames.contains(locationVal)) continue;
						
						
						TableItems[] row = new TableItems[factorLocalNames.size()+variateLocalNames.size()+1];
						
						
						row[0] = new TableItems();
						row[0].setType(GxeTable.CELL_CHECKBOX_ALL);
						row[0].setLabel(" ");
						row[0].setValue(true);
						
						int cellCounter = 1;
						
						
						for (Map.Entry<Integer, String> f : factorLocalNames.entrySet()){
							String fValue = exp.getFactors().findByLocalName(f.getValue()).getValue();
							if (f.getValue().equalsIgnoreCase(selectedEnvFactorName)) envNames.add(fValue);
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
								//meansData = String.valueOf(studyDataManager.countExperimentsByTrialEnvironmentAndVariate(
								//		envs.findOnlyOneByLocalName(selectedEnvFactorName, row[1].getLabel()).getId(), 
								//		v.getKey()));
								meansData = String.valueOf(studyDataManager.countStocks(
										meansDataSetId
										,envs.findOnlyOneByLocalName(selectedEnvFactorName, row[1].getLabel()).getId()
										,varKey
											)
										);
							}catch(Exception e){
								System.out.println("Error in getting the means data.");
								e.printStackTrace();
							}
							
							row[cellCounter].setLabel(meansData);
							row[cellCounter].setValue(true);
							row[cellCounter].setType(GxeTable.CELL_CHECKBOX);
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
		
		for (Integer i = 1; i < obj.length; i++){
			Property cb_column = this.getContainerProperty(obj[i], " ");
			Property location_column = this.getContainerProperty(obj[i], selectedEnvFactorName);
			if(((GxeCheckBoxGroup) cb_column.getValue()).getValue()){
				GxeEnvironmentLabel environmentLabel = new GxeEnvironmentLabel();
				environmentLabel.setName(((Label)location_column.getValue()).getValue().toString());
				environmentLabel.setActive(true);
				environmentLabels.add(environmentLabel);
			}
		}
		
		gxeEnvironment.setLabel(environmentLabels);
		
		return gxeEnvironment;
		
	}
	
	public List<Trait> getSelectedTraits(){
		List<Trait> traits = new ArrayList<Trait>();
		Object obj = this.getContainerDataSource().getItemIds().toArray()[0];
		for (Map.Entry<Integer, String> v : variateLocalNames.entrySet()){
			Property p = this.getContainerProperty(obj, v.getValue());
			if((Boolean) ((CheckBox) p.getValue()).getValue()){
				Trait t = new Trait();
				t.setName(v.getValue());
				t.setActive(true);
				traits.add(t);
			}
		}
		 
		return traits;
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
}



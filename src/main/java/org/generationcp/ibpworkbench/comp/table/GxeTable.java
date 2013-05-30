package org.generationcp.ibpworkbench.comp.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.gxe.xml.GxeEnvironmentLabel;
import org.generationcp.ibpworkbench.util.TableItems;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DataSetType;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.FactorType;
import org.generationcp.middleware.v2.domain.TrialEnvironments;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class GxeTable extends Table {
	
	
	
	private static final long serialVersionUID = 1274131837702381485L;
	
	public static final int CELL_CHECKBOX = 1;
	public static final int CELL_CHECKBOX_HEADER = 2;
	public static final int CELL_CHECKBOX_COLUMN = 3;
	public static final int CELL_CHECKBOX_ROW = 4;
	public static final int CELL_CHECKBOX_ALL = 5;
	public static final int CELL_LABEL = 6;
	
	private StudyDataManager studyDataManager;
	private List<String> columnNames = new ArrayList<String>(); 
	private Map<Integer, String> factorLocalNames = new HashMap<Integer, String>();
	private Map<Integer, String> variateLocalNames = new HashMap<Integer, String>();
	private String location_property = "";
	
	public String getLocation_property() {
		return location_property;
	}

	private String trial_instance_property = "";

	public String getTrial_instance_property() {
		return trial_instance_property;
	}

	private int meansDataSetId;

	private DataSet meansDataSet;

	private List<Experiment> exps;

	public GxeTable(StudyDataManager studyDataManager, Integer studyId) {
		this.studyDataManager = studyDataManager;
		initializeTable();
		fillTableWithDataset(studyId);
	}


	private void initializeTable() {

		this.setImmediate(true);
		this.setSizeFull();
		this.setContainerDataSource(new IndexedContainer());
		this.setEditable(true);

	}


	private void initializeHeader(Map<Integer, String> factors, Map<Integer, String> variates, Container container) {

		int cnt = 0;
		TableItems[] rowHeaders = new TableItems[factors.size()+variates.size()+1];
		rowHeaders[cnt] = new TableItems();
		rowHeaders[cnt].setType(GxeTable.CELL_CHECKBOX_ALL);
		rowHeaders[cnt].setLabel("SELECT ALL");
		rowHeaders[cnt].setRowId(1);
		rowHeaders[cnt].setValue(true);
		
		cnt++;
		
		for( Entry<Integer, String> f : factors.entrySet() ){
			rowHeaders[cnt] = new TableItems();
			rowHeaders[cnt].setType(GxeTable.CELL_LABEL);
			rowHeaders[cnt].setLabel(f.getValue());
			rowHeaders[cnt].setRowId(1);
			rowHeaders[cnt].setValue(true);
			cnt++;
		}
		
		for( Entry<Integer, String> v : variates.entrySet() ){
			rowHeaders[cnt] = new TableItems();
			rowHeaders[cnt].setType(GxeTable.CELL_CHECKBOX_COLUMN);
			rowHeaders[cnt].setLabel(v.getValue());
			rowHeaders[cnt].setRowId(1);
			rowHeaders[cnt].setValue(true);
			cnt++;
		}
		
		
		columnNames.add(" ");
		columnNames.addAll(factors.values());
		columnNames.addAll(variates.values());

		this.setVisibleColumns(columnNames.toArray(new Object[0]));
		this.setColumnHeaders(columnNames.toArray(new String[0]));

		createRow(1, rowHeaders, this.getContainerDataSource());

	}

	private void createRow(Integer rowIndex, TableItems[] tableItems, Container container) {

		Object[] obj = new Object[tableItems.length];

		for (Integer i = 0; i < tableItems.length; i++) {

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
					VariableTypeList factors = meansDataSet.getFactorsByFactorType(FactorType.TRIAL_ENVIRONMENT);
					for(VariableType f : factors.getVariableTypes()){
						//SITE_NAME
						if (f.getStandardVariable().getProperty().getName().equalsIgnoreCase("location")){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getId(), f.getLocalName());
							location_property = f.getLocalName();
						}
						//SITE_NO
						if (f.getStandardVariable().getProperty().getName().equalsIgnoreCase("trial instance")){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getId(), f.getLocalName());
							trial_instance_property = f.getLocalName();
						}
					}
					//get the Variates
					VariableTypeList variates = meansDataSet.getVariableTypes().getVariates();
					for(VariableType v : variates.getVariableTypes()){
						container.addContainerProperty(v.getLocalName(), CheckBox.class, new CheckBox());
						variateLocalNames.put(v.getId(), v.getLocalName());
					}
					
					
					initializeHeader(factorLocalNames, variateLocalNames, container);
					
					//generate the rows
					exps = studyDataManager.getExperiments(meansDataSetId, 0, Integer.MAX_VALUE);
					
					Integer rowCounter = 2;
					
					for (Experiment exp : exps){
						
						String locationVal = exp.getFactors().findByLocalName(location_property).getValue();
						if (envNames.contains(locationVal)) continue;
						
						
						TableItems[] row = new TableItems[factorLocalNames.size()+variateLocalNames.size()+1];
						
						
						row[0] = new TableItems();
						row[0].setType(GxeTable.CELL_CHECKBOX_ALL);
						row[0].setLabel(" ");
						row[0].setValue(true);
						
						Integer cellCounter = 1;
						
						
						for (Map.Entry<Integer, String> f : factorLocalNames.entrySet()){
							String fValue = exp.getFactors().findById(f.getKey()).getValue();
							if (f.getValue().equalsIgnoreCase(location_property)) envNames.add(fValue);
							row[cellCounter] = new TableItems();
							row[cellCounter].setLabel(fValue);
							row[cellCounter].setType(GxeTable.CELL_LABEL);
							cellCounter++;
						}
						
						
						for (Map.Entry<Integer, String> v : variateLocalNames.entrySet()){
							row[cellCounter] = new TableItems();
							
							String meansData = "";
							try{
								meansData = String.valueOf(studyDataManager.countExperimentsByTrialEnvironmentAndVariate(
										envs.findOnlyOneByLocalName(trial_instance_property, row[2].getLabel()).getId(), 
										v.getKey()));
							}catch(Exception e){
								System.out.println("Error in getting the means data.");
								e.printStackTrace();
							}
							
							row[cellCounter].setLabel(meansData);
							row[cellCounter].setValue(true);
							row[cellCounter].setType(GxeTable.CELL_CHECKBOX);
							cellCounter++;
						}
						
						rowCounter=rowCounter+1;
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
			Property location_column = this.getContainerProperty(obj[i], location_property);
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
		return location_property;
	}
}

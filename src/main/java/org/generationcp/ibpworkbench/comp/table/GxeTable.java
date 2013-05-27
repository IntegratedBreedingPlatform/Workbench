package org.generationcp.ibpworkbench.comp.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	List<String> columnNames = new ArrayList<String>();

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
				CheckBox cb = new CheckBox();
				cb.setCaption(tableItems[i].getLabel());
				cb.setValue(tableItems[i].getValue());

				cb.setImmediate(true);

				cb.addListener(new GxeAllCheckboxListener(columnNames.toArray(new String[0]), this
						.getContainerDataSource()));

				obj[i] = cb;
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
		
		container.addContainerProperty(" ", CheckBox.class, new CheckBox());
		
		Map<Integer, String> factorLocalNames = new HashMap<Integer, String>();
		Map<Integer, String> variateLocalNames = new HashMap<Integer, String>();
		
		
		try {
			List<DataSet> meansDataSets = studyDataManager.getDataSetsByType(
					studyId, DataSetType.MEANS_DATA);
			if (meansDataSets != null) {
				if (meansDataSets.size() > 0) {
					
					TrialEnvironments envs = studyDataManager.getTrialEnvironmentsInDataset(meansDataSets.get(0).getId());
					//get the SITE NAME and SITE NO
					VariableTypeList factors = meansDataSets.get(0).getFactorsByFactorType(FactorType.TRIAL_ENVIRONMENT);
					for(VariableType f : factors.getVariableTypes()){
						//SITE_NAME
						if (f.getStandardVariable().getProperty().getName().equalsIgnoreCase("location")){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getId(), f.getLocalName());
						}
						//SITE_NO
						if (f.getStandardVariable().getProperty().getName().equalsIgnoreCase("trial instance")){
							container.addContainerProperty(f.getLocalName(), Label.class, "");
							factorLocalNames.put(f.getId(), f.getLocalName());
						}
					}
					//get the Variates
					VariableTypeList variates = meansDataSets.get(0).getVariableTypes().getVariates();
					for(VariableType v : variates.getVariableTypes()){
						container.addContainerProperty(v.getLocalName(), CheckBox.class, new CheckBox());
						variateLocalNames.put(v.getId(), v.getLocalName());
					}
					
					
					initializeHeader(factorLocalNames, variateLocalNames, container);
					
					//generate the rows
					List<Experiment> exps = studyDataManager.getExperiments(meansDataSets.get(0).getId(), 0, Integer.MAX_VALUE);
					
					Integer rowCounter = 2;
					
					for (Experiment exp : exps){
						TableItems[] row = new TableItems[factorLocalNames.size()+variateLocalNames.size()+1];
						
						row[0] = new TableItems();
						row[0].setType(GxeTable.CELL_CHECKBOX_ROW);
						row[0].setLabel(" ");
						row[0].setValue(true);
						
						Integer cellCounter = 1;
						
						
						for (Map.Entry<Integer, String> f : factorLocalNames.entrySet()){
							row[cellCounter] = new TableItems();
							row[cellCounter].setLabel(exp.getFactors().findById(f.getKey()).getValue());
							row[cellCounter].setType(GxeTable.CELL_LABEL);
							cellCounter++;
						}
						
						for (Map.Entry<Integer, String> v : variateLocalNames.entrySet()){
							row[cellCounter] = new TableItems();
							
							String meansData = "";
							try{
								meansData = String.valueOf(studyDataManager.countExperimentsByTrialEnvironmentAndVariate(
										envs.findOnlyOneByLocalName("SITENO", row[2].getLabel()).getId(), 
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

}

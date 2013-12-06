package org.generationcp.ibpworkbench.ui.metaanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.model.MetaEnvironmentModel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class SelectTraitsForMetaAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private List<MetaEnvironmentModel> metaEnvironments;
	private Map<Integer, DataSet> dataSets;
	private Map<Integer, TrialEnvironments> trialEnvironmentsList;
	private Table environmentsTable;
	private Table variatesSelectionTable;
	private Table factorsAnalysisTable;
	private Table factorsSelectionTable;
	
	private Project currentProject;
	
	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;
	    
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	    
	private StudyDataManager studyDataManager;
	    
	private ManagerFactory managerFactory;
	

	public SelectTraitsForMetaAnalysisPanel(Project project,List<MetaEnvironmentModel> metaEnvironments) {
		this.metaEnvironments = metaEnvironments;
		this.currentProject = project;
	}
	

	private void initializeComponents(){
		 environmentsTable = new Table();
		 environmentsTable.setWidth("100%");
		 factorsAnalysisTable = new Table();
		 factorsAnalysisTable.setWidth("100%");
		
		 dataSets =  new HashMap<Integer, DataSet>();
		 trialEnvironmentsList =  new HashMap<Integer, TrialEnvironments>();
		
		 ColumnGenerator generatedVariateColumn = new ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				String countData = "0";
				MetaEnvironmentModel item = (MetaEnvironmentModel) itemId;
				VariableType varType = dataSets.get(item.getDataSetId()).findVariableTypeByLocalName(columnId.toString());
				
				if (varType == null) return "0";
				
				try {
					countData = String.valueOf(getStudyDataManager().countStocks(
								item.getDataSetId()
								,trialEnvironmentsList.get(item.getDataSetId()).findOnlyOneByLocalName(item.getTrialFactorName(), item.getTrial()).getId()
								,varType.getId()
									)
								);
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}
				
				return countData;
			}
			 
		 };
		 
		 ColumnGenerator generatedFactorColumn = new ColumnGenerator(){

				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(Table source, Object itemId,
						Object columnId) {
					
					MetaEnvironmentModel item = (MetaEnvironmentModel) itemId;
					VariableType varType = dataSets.get(item.getDataSetId()).findVariableTypeByLocalName(columnId.toString());
					if (varType ==  null){
						return "";
					}else{
						return "X";
					}
					
				}
				 
			 };
		
	     environmentsTable.addGeneratedColumn("", new ColumnGenerator(){

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				CheckBox chk = new CheckBox();
				chk.setValue(true);
				chk.setImmediate(true);
				return chk;
			}});
		 
		 HashSet<String> variatesColumnList = new HashSet<String>();
		 HashSet<String> factorsColumnList = new HashSet<String>();
		 for (MetaEnvironmentModel metaEnvironment : metaEnvironments){
			 if (dataSets.get(metaEnvironment.getDataSetId()) == null){
				try {
					DataSet ds;
					TrialEnvironments envs;
					ds = this.getStudyDataManager().getDataSet(metaEnvironment.getDataSetId());
					envs = this.getStudyDataManager().getTrialEnvironmentsInDataset(ds.getId());
					dataSets.put(metaEnvironment.getDataSetId(), ds);
					trialEnvironmentsList.put(metaEnvironment.getDataSetId(), envs);
					
			
					for (VariableType v : ds.getVariableTypes().getVariates().getVariableTypes()){
						try{
						environmentsTable.addGeneratedColumn(v.getLocalName(), generatedVariateColumn);
						variatesColumnList.add(v.getLocalName());
						}catch(Exception e){}
					}
					
					for (VariableType f : ds.getVariableTypes().getFactors().getVariableTypes()){
						if (f.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET
		            			) continue;
						try{
							factorsAnalysisTable.addGeneratedColumn(f.getLocalName(), generatedFactorColumn);
							factorsColumnList.add(f.getLocalName());
						}catch(Exception e){}
					}
					
					
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			 }else{
				 continue;
			 }
		 }
		 
		 BeanItemContainer<MetaEnvironmentModel> environmentsTableContainer = new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);
		 for (MetaEnvironmentModel metaEnvironment : metaEnvironments){
			 environmentsTableContainer.addBean(metaEnvironment);
			 
		 }
		 environmentsTable.setContainerDataSource(environmentsTableContainer);
		 List<String> visibleCols = new ArrayList<String>();
		 visibleCols.add("");
		 visibleCols.add("dataSetName");
		 visibleCols.add("trial");
		 visibleCols.add("environment");
		 visibleCols.addAll(variatesColumnList);
		 environmentsTable.setVisibleColumns(visibleCols.toArray());
		 visibleCols.clear();
		 visibleCols.add("SELECT");
		 visibleCols.add("Dataset Name");
		 visibleCols.add("Trial");
		 visibleCols.add("Environment");
		 visibleCols.addAll(variatesColumnList);
		 environmentsTable.setColumnHeaders(visibleCols.toArray(new String[0]));
		 
		 BeanItemContainer<MetaEnvironmentModel> factorsAnalysisTableContainer = new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);
		 for (MetaEnvironmentModel metaEnvironment : metaEnvironments){
			 factorsAnalysisTableContainer.addBean(metaEnvironment);
			 
		 }
		 factorsAnalysisTable.setContainerDataSource(factorsAnalysisTableContainer);
		 visibleCols.clear();
		 visibleCols.add("dataSetName");
		 visibleCols.add("trial");
		 visibleCols.add("environment");
		 visibleCols.addAll(factorsColumnList);
		 factorsAnalysisTable.setVisibleColumns(visibleCols.toArray());
		 visibleCols.clear();
		 visibleCols.add("Dataset Name");
		 visibleCols.add("Trial");
		 visibleCols.add("Environment");
		 visibleCols.addAll(factorsColumnList);
		 factorsAnalysisTable.setColumnHeaders(visibleCols.toArray(new String[0]));
		 
		 
		 variatesSelectionTable = new Table();
		 variatesSelectionTable.setWidth("100%");
		 variatesSelectionTable.setHeight("80px");
		 IndexedContainer variatesSelectionTableContainer = new IndexedContainer();
		 for (Object s : variatesColumnList.toArray()){
			 variatesSelectionTableContainer.addContainerProperty(s.toString(), CheckBox.class, new CheckBox());
		 }
		 variatesSelectionTableContainer.addItem();
		 variatesSelectionTable.setContainerDataSource(variatesSelectionTableContainer);
		
		 
		 
		 factorsSelectionTable = new Table();
		 factorsSelectionTable.setWidth("100%");
		 factorsSelectionTable.setHeight("80px");
		 IndexedContainer factorsSelectionTableContainer = new IndexedContainer();
		 for (Object s : factorsColumnList.toArray()){
			 factorsSelectionTableContainer.addContainerProperty(s.toString(), CheckBox.class, new CheckBox());
		 }
		 factorsSelectionTableContainer.addItem();
		 factorsSelectionTable.setContainerDataSource(factorsSelectionTableContainer);
		
	}
	
	
	private void initializeLayout(){
		
		setSizeUndefined();
		setSpacing(true);
		setMargin(true);
		setWidth("95%");
		
		addComponent(environmentsTable);
		addComponent(variatesSelectionTable);
		addComponent(factorsAnalysisTable);
		addComponent(factorsSelectionTable);
		
	}

	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub
		
	}
	
	private void assemble(){
		initializeComponents();
		initializeLayout();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProject);
		assemble();
		
	}
	
	public StudyDataManager getStudyDataManager() {
	    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
			return this.studyDataManager;
	}

}

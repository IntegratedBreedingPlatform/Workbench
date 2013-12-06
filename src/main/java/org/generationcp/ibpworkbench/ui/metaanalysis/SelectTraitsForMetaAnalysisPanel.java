package org.generationcp.ibpworkbench.ui.metaanalysis;

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
		
		 ColumnGenerator generatedVariateColumn = new ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				
				return 0;
			}
			 
		 };
		 
		 ColumnGenerator generatedFactorColumn = new ColumnGenerator(){

				private static final long serialVersionUID = 1L;

				@Override
				public Object generateCell(Table source, Object itemId,
						Object columnId) {
					
					return "X";
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
					ds = this.getStudyDataManager().getDataSet(metaEnvironment.getDataSetId());
					dataSets.put(metaEnvironment.getDataSetId(), ds);
					
			
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
		 
		 BeanItemContainer<MetaEnvironmentModel> factorsAnalysisTableContainer = new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);
		 for (MetaEnvironmentModel metaEnvironment : metaEnvironments){
			 factorsAnalysisTableContainer.addBean(metaEnvironment);
			 
		 }
		 factorsAnalysisTable.setContainerDataSource(factorsAnalysisTableContainer);
		 
		 
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
		
		setSpacing(true);
		setMargin(true);
		setWidth("90%");
		setHeight("1200px");
		
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

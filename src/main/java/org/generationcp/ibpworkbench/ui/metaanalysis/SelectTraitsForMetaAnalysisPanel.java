package org.generationcp.ibpworkbench.ui.metaanalysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.MetaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Experiment;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class SelectTraitsForMetaAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = 1L;
	private List<MetaEnvironmentModel> metaEnvironments;
	private Map<Integer, DataSet> dataSets;
	private Map<Integer, TrialEnvironments> trialEnvironmentsList;
	private Map<String, Boolean> factorsCheckBoxState;
	private Map<String, Boolean> variatesCheckBoxState;
	private Table environmentsTable;
	private Table variatesSelectionTable;
	private Table factorsAnalysisTable;
	private Table factorsSelectionTable;
	
	private CheckBox chkSelectAllVariates;
	private CheckBox chkSelectAllFactors;
	private CheckBox chkSelectAllEnvironments;
	private Property.ValueChangeListener selectAllEnvironmentsListener;
	private Property.ValueChangeListener selectAllFactorsListener;
	private Property.ValueChangeListener selectAllTraitsListener;
	
    private Button btnCancel;
    private Button btnNext;
    private Component buttonArea;
	
	private Label lblSelectEnvVarForAnalysis;
	private Label lblSelectEnvVarForAnalysisDesc;
	private Label lblSelectVariates;
	private Label lblSelectFactorsForAnalysis;
	private Label lblSelectFactorsForAnalysisDesc;
	private Label lblSelectFactors;
	
	private Project currentProject;
	
	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;
	    
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	    
	private StudyDataManager studyDataManager;
	    
	private ManagerFactory managerFactory;
	
	private SelectDatasetsForMetaAnalysisPanel selectDatasetsForMetaAnalysisPanel;
	

	public SelectTraitsForMetaAnalysisPanel(Project project,List<MetaEnvironmentModel> metaEnvironments, SelectDatasetsForMetaAnalysisPanel selectDatasetsForMetaAnalysisPanel) {
		this.metaEnvironments = metaEnvironments;
		this.currentProject = project;
		this.selectDatasetsForMetaAnalysisPanel = selectDatasetsForMetaAnalysisPanel;
	}
	

	private void initializeComponents(){
		
		factorsCheckBoxState = new HashMap<String, Boolean>();
		variatesCheckBoxState = new HashMap<String, Boolean>();
		
		
		environmentsTable = new Table();
		
		selectAllEnvironmentsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = environmentsTable.getContainerDataSource().getItemIds().iterator(); itr.hasNext();){
					MetaEnvironmentModel m = (MetaEnvironmentModel) itr.next();
					m.setActive((Boolean) event.getProperty().getValue());
				}
				
				environmentsTable.refreshRowCache();
				
			}
		};
		
		selectAllFactorsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = factorsSelectionTable.getContainerPropertyIds().iterator(); itr.hasNext();){
					Object propertyId = itr.next();
					CheckBox chk = (CheckBox) factorsSelectionTable.getItem(1).getItemProperty(propertyId).getValue();
					chk.setValue(event.getProperty().getValue());
					factorsCheckBoxState.put(propertyId.toString(), (Boolean) event.getProperty().getValue());
				}
				
				
			}
		};
    	
    	selectAllTraitsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = variatesSelectionTable.getContainerPropertyIds().iterator(); itr.hasNext();){
					Object propertyId = itr.next();
					CheckBox chk = (CheckBox) variatesSelectionTable.getItem(1).getItemProperty(propertyId).getValue();
					chk.setValue(event.getProperty().getValue());
					variatesCheckBoxState.put(propertyId.toString(), (Boolean) event.getProperty().getValue());
				}
				
				
			}
		};
		
		
		
		chkSelectAllVariates = new CheckBox();
		chkSelectAllVariates.setImmediate(true);
    	chkSelectAllVariates.setCaption("Select All Traits");
    	chkSelectAllVariates.addListener(selectAllTraitsListener);
    	chkSelectAllFactors = new CheckBox();
    	chkSelectAllFactors.setImmediate(true);
    	chkSelectAllFactors.setCaption("Select All Factors");
    	chkSelectAllFactors.addListener(selectAllFactorsListener);
    	chkSelectAllEnvironments = new CheckBox();
    	chkSelectAllEnvironments.setImmediate(true);
    	chkSelectAllEnvironments.setCaption("Select All Environments");
    	chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
		
		lblSelectEnvVarForAnalysis = new Label();
		lblSelectEnvVarForAnalysis.setStyleName("gcp-content-header");
		lblSelectEnvVarForAnalysisDesc  = new Label();
		lblSelectVariates  = new Label();
		lblSelectFactorsForAnalysis  = new Label();
		lblSelectFactorsForAnalysis.setStyleName("gcp-content-header");
		lblSelectFactorsForAnalysisDesc  = new Label();
		lblSelectFactors  = new Label();
		
	
		
		 
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
			 
			 final Property.ValueChangeListener envCheckBoxListener = new Property.ValueChangeListener(){

					@Override
					public void valueChange(ValueChangeEvent event) {
						Boolean val = (Boolean) event.getProperty().getValue();
						CheckBox chk = (CheckBox) event.getProperty();
						((MetaEnvironmentModel) chk.getData()).setActive(val);
						if (val == false){
							chkSelectAllEnvironments.removeListener(selectAllEnvironmentsListener);
							chkSelectAllEnvironments.setValue(false);
							chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
						}
						
					}
					
				};	 
		
	     environmentsTable.addGeneratedColumn("", new ColumnGenerator(){

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				MetaEnvironmentModel item = (MetaEnvironmentModel) itemId;
				
				CheckBox chk = new CheckBox();
				chk.setValue(item.getActive());
				chk.setData(itemId);
				chk.setImmediate(true);
				chk.addListener(envCheckBoxListener);
				
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
			 metaEnvironment.setActive(true);
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
		 
		 Property.ValueChangeListener traitCheckBoxListener = new Property.ValueChangeListener(){
				@Override
				public void valueChange(ValueChangeEvent event) {
					Boolean val = (Boolean) event.getProperty().getValue();
					CheckBox chk = (CheckBox) event.getProperty();
					variatesCheckBoxState.put(chk.getData().toString(), val);
					if (val == false){
						chkSelectAllVariates.removeListener(selectAllTraitsListener);
						chkSelectAllVariates.setValue(false);
						chkSelectAllVariates.addListener(selectAllTraitsListener);
					}
					
				}
				
			};
		 
		
		 
		 variatesSelectionTable = new Table();
		 variatesSelectionTable.setWidth("100%");
		 variatesSelectionTable.setHeight("80px");
		 List<CheckBox> vCheckBoxes = new ArrayList<CheckBox>();
		 IndexedContainer variatesSelectionTableContainer = new IndexedContainer();
		 for (Object s : variatesColumnList.toArray()){
			 variatesSelectionTableContainer.addContainerProperty(s.toString(), CheckBox.class, null);
				 CheckBox variateCheckBox = new CheckBox();
				 variateCheckBox.setImmediate(true);
				 variateCheckBox.addListener(traitCheckBoxListener);
				 variateCheckBox.setData(s);
				 vCheckBoxes.add(variateCheckBox);
				 variatesCheckBoxState.put(s.toString(), false);
		 }

		 variatesSelectionTable.setContainerDataSource(variatesSelectionTableContainer);
		 variatesSelectionTable.addItem(vCheckBoxes.toArray(), 1);
		
		 Property.ValueChangeListener factorCheckBoxListener = new Property.ValueChangeListener(){
				@Override
				public void valueChange(ValueChangeEvent event) {
					Boolean val = (Boolean) event.getProperty().getValue();
					CheckBox chk = (CheckBox) event.getProperty();
					factorsCheckBoxState.put(chk.getData().toString(), val);
					if (val == false){
						chkSelectAllFactors.removeListener(selectAllFactorsListener);
						chkSelectAllFactors.setValue(false);
						chkSelectAllFactors.addListener(selectAllFactorsListener);
					}
					
				}
				
			};
		 
		 
		 
		 factorsSelectionTable = new Table();
		 factorsSelectionTable.setWidth("100%");
		 factorsSelectionTable.setHeight("80px");
		 List<CheckBox> fCheckBoxes = new ArrayList<CheckBox>();
		 IndexedContainer factorsSelectionTableContainer = new IndexedContainer();
		 for (Object s : factorsColumnList.toArray()){
			 factorsSelectionTableContainer.addContainerProperty(s.toString(), CheckBox.class, null);
				 CheckBox factorCheckBox = new CheckBox();
				 factorCheckBox.setImmediate(true);
				 factorCheckBox.addListener(factorCheckBoxListener);
				 factorCheckBox.setData(s);
				 fCheckBoxes.add(factorCheckBox);
				 factorsCheckBoxState.put(s.toString(), false);
				 
		 }
		 factorsSelectionTable.setContainerDataSource(factorsSelectionTableContainer);
		 factorsSelectionTable.addItem(fCheckBoxes.toArray(), 1);
		
	}
	
	
	private void initializeLayout(){
		
		setSizeUndefined();
		setSpacing(true);
		setWidth("95%");
		
		VerticalLayout layout1 = new VerticalLayout();
		layout1.setMargin(true);
		layout1.setSpacing(true);
		layout1.addComponent(lblSelectEnvVarForAnalysis);
		layout1.addComponent(lblSelectEnvVarForAnalysisDesc);
		layout1.addComponent(environmentsTable);
		layout1.addComponent(chkSelectAllEnvironments);
		addComponent(layout1);
		
		VerticalLayout layout2 = new VerticalLayout();
		layout2.setMargin(true);
		layout2.setSpacing(true);
		layout2.addComponent(lblSelectVariates);
		layout2.addComponent(variatesSelectionTable);
		layout2.addComponent(chkSelectAllVariates);
		addComponent(layout2);
		
		
		VerticalLayout layout3 = new VerticalLayout();
		layout3.setMargin(true);
		layout3.setSpacing(true);
		layout3.addComponent(lblSelectFactorsForAnalysis);
		layout3.addComponent(lblSelectFactorsForAnalysisDesc);
		layout3.addComponent(factorsAnalysisTable);
		addComponent(layout3);
		
		VerticalLayout layout4 = new VerticalLayout();
		layout4.setMargin(true);
		layout4.setSpacing(true);
		layout4.addComponent(lblSelectFactors);
		layout4.addComponent(factorsSelectionTable);
		layout4.addComponent(chkSelectAllFactors);
		addComponent(layout4);
		
		buttonArea = layoutButtonArea();
		addComponent(buttonArea);
		setComponentAlignment(buttonArea, Alignment.TOP_CENTER);
		
	}
	
	 protected Component layoutButtonArea() {
	    	
	        HorizontalLayout buttonLayout = new HorizontalLayout();
	        
	        buttonLayout.setSpacing(true);
	        buttonLayout.setMargin(true);
	        
	        btnCancel = new Button();
	        btnNext = new Button();
	        btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
	   
	        buttonLayout.addComponent(btnCancel);
	        buttonLayout.addComponent(btnNext);
	        buttonLayout.setComponentAlignment(btnCancel, Alignment.TOP_CENTER);
	        buttonLayout.setComponentAlignment(btnNext, Alignment.TOP_CENTER);
	        return buttonLayout;
	    }
	 
	 protected void initializeActions() {
	    	btnCancel.addListener(new Button.ClickListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					IContentWindow window = (IContentWindow) event.getComponent().getWindow();
					window.showContent(selectDatasetsForMetaAnalysisPanel);
				}
			});
	    	
	        btnNext.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
						if (variatesCheckBoxState.size() == 0 
								|| factorsCheckBoxState.size() == 0 
								
								) { return;}
						
						final File file = exportData();
						
						if (file == null) return;
						
						FileResource fr = new FileResource(file, event.getComponent().getWindow().getApplication()) {
							private static final long serialVersionUID = 765143030552676513L;
							@Override
							public DownloadStream getStream() {
								DownloadStream ds;
								try {
									ds = new DownloadStream(new FileInputStream(
											getSourceFile()), getMIMEType(), getFilename());
									
									ds.setParameter("Content-Disposition", "attachment; filename="+ file.getName());
									ds.setCacheTime(getCacheTime());
									return ds;
									
								} catch (FileNotFoundException e) {
									// No logging for non-existing files at this level.
									return null;
								}
							}
						};
						
						event.getComponent().getWindow().getApplication().getMainWindow().open(fr);
				}
			});

	    }  
	
	private File exportData(){
		
		Workbook workbook = new HSSFWorkbook();
		Sheet defaultSheet = workbook.createSheet("Merged DataSets");
		
		
		//Create Header Row
		int cellCounter = 0;
		int rowCounter = 0;
		Row headerRow = defaultSheet.createRow(rowCounter++);
		
		for (Entry<String, Boolean> entry : factorsCheckBoxState.entrySet()){
			if (entry.getValue()) headerRow.createCell(cellCounter++).setCellValue(entry.getKey());	
		}
		for (Entry<String, Boolean> entry : variatesCheckBoxState.entrySet()){
			if (entry.getValue()) headerRow.createCell(cellCounter++).setCellValue(entry.getKey());	
		}
	
		
		Iterator<?> envIterator = environmentsTable.getItemIds().iterator();
		while(envIterator.hasNext()){
			MetaEnvironmentModel envModel = (MetaEnvironmentModel) envIterator.next();
			
			if (envModel.getActive()){
				
				 try {
					List<Experiment> exps = getStudyDataManager().getExperiments(envModel.getDataSetId(), 0, Integer.MAX_VALUE);
					for (Experiment exp : exps){
						
						Variable trialVariable = exp.getFactors().findByLocalName(envModel.getTrialFactorName());
						if (trialVariable == null) continue;
						if (!trialVariable.getValue().equalsIgnoreCase(envModel.getTrial())) continue;
						
						cellCounter = 0;
						Row row = defaultSheet.createRow(rowCounter++);
						
						for (Entry<String, Boolean> entry : factorsCheckBoxState.entrySet()){
							if (entry.getValue()) {
								Variable var = exp.getFactors().findByLocalName(entry.getKey());
								String cellValue = "";
								if (var != null){
									cellValue = var.getValue();
								}
								row.createCell(cellCounter++).setCellValue(cellValue);	
							}
								
						}
						for (Entry<String, Boolean> entry : variatesCheckBoxState.entrySet()){
							if (entry.getValue()) {
								Variable var = exp.getVariates().findByLocalName(entry.getKey());
								String cellValue = "";
								if (var != null){
									cellValue = var.getValue();
								}
								row.createCell(cellCounter++).setCellValue(cellValue);	
							}
						}
						
					}
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
				
			}
		}//while
		
		try {
			//NOTE: Directory location is hardcoded to workspace/{projectId/breeding_view/input}
			String dir = "workspace" + File.separator + currentProject.getProjectId().toString() + File.separator + "breeding_view" + File.separator + "input";
			new File(dir).mkdirs();
			File xlsFile = new File(dir + File.separator + "mergedDataSets.xls");
			FileOutputStream fos = new FileOutputStream(xlsFile);
			workbook.write(fos);
			fos.close();
			return xlsFile.getAbsoluteFile();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
		
	}

	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub
		messageSource.setCaption(btnCancel, Message.CANCEL);
        messageSource.setCaption(btnNext, Message.EXPORT_DATA);
		messageSource.setValue(lblSelectEnvVarForAnalysis, Message.META_SELECT_ENV_VAR_FOR_ANALYSIS);
		messageSource.setValue(lblSelectEnvVarForAnalysisDesc, Message.META_SELECT_ENV_VAR_FOR_ANALYSIS_DESC);
		messageSource.setValue(lblSelectVariates, Message.META_SELECT_VARIATES);
		messageSource.setValue(lblSelectFactorsForAnalysis, Message.META_SELECT_FACTORS_FOR_ANALYSIS);
		messageSource.setValue(lblSelectFactorsForAnalysisDesc, Message.META_SELECT_FACTORS_FOR_ANALYSIS_DESC);
		messageSource.setValue(lblSelectFactors, Message.META_SELECT_FACTORS);
		
	}
	
	private void assemble(){
		initializeComponents();
		initializeLayout();
		initializeActions();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProject);
		assemble();
		
	}
	
	 @Override
	    public void attach() {
	        super.attach();
	        
	        updateLabels();
	    }
	
	public StudyDataManager getStudyDataManager() {
	    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
			return this.studyDataManager;
	}

}

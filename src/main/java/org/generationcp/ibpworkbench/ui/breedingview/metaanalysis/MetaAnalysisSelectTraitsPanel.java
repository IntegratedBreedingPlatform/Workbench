package org.generationcp.ibpworkbench.ui.breedingview.metaanalysis;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table.ColumnGenerator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.MetaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;

@Configurable
public class MetaAnalysisSelectTraitsPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

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
	
    private Button btnBack;
    private Button btnReset;
    private Button btnNext;
    private Component buttonArea;
	
    private Label lblPageTitle;
	private Label lblSelectEnvVarForAnalysis;
	private Label lblSelectEnvVarForAnalysisDesc;
	private Label lblSelectVariates;
	private Label lblSelectFactorsForAnalysis;
	private Label lblSelectFactorsForAnalysisDesc;
	private Label lblSelectFactors;
	
	private Project currentProject;
	    
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	    
	private StudyDataManager studyDataManager;
	    
	private ManagerFactory managerFactory;
	
	private MetaAnalysisPanel selectDatasetsForMetaAnalysisPanel;
	

	public MetaAnalysisSelectTraitsPanel(Project project,List<MetaEnvironmentModel> metaEnvironments, MetaAnalysisPanel selectDatasetsForMetaAnalysisPanel, ManagerFactory managerFactory) {
		this.metaEnvironments = metaEnvironments;
		this.currentProject = project;
		this.selectDatasetsForMetaAnalysisPanel = selectDatasetsForMetaAnalysisPanel;
		this.managerFactory = managerFactory;
	}
	

	private void initializeComponents(){
		
		lblPageTitle = new Label();
    	lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());
		
		factorsCheckBoxState = new HashMap<String, Boolean>();
		variatesCheckBoxState = new HashMap<String, Boolean>();
		
		
		environmentsTable = new Table();
		environmentsTable.setColumnCollapsingAllowed(true);
		
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
					if (chk.isEnabled()) {
						chk.setValue(event.getProperty().getValue());
						factorsCheckBoxState.put(propertyId.toString(), (Boolean) event.getProperty().getValue());
					}
					
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
    	chkSelectAllEnvironments.setValue(true);
		
		lblSelectEnvVarForAnalysis = new Label();
		lblSelectEnvVarForAnalysis.setStyleName(Bootstrap.Typography.H4.styleName());
		lblSelectEnvVarForAnalysisDesc  = new Label();
		lblSelectVariates  = new Label();
		lblSelectFactorsForAnalysis  = new Label();
		lblSelectFactorsForAnalysis.setStyleName(Bootstrap.Typography.H4.styleName());
		lblSelectFactorsForAnalysisDesc  = new Label();
		lblSelectFactors  = new Label();
	 
		 environmentsTable.setWidth("100%");
		 factorsAnalysisTable = new Table();
		 factorsAnalysisTable.setWidth("100%");
		 factorsAnalysisTable.setColumnCollapsingAllowed(true);
		
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
				
				if (varType == null) {
                    return "0";
                }
				
				try {
					
					if (item.getDataSetTypeId() == DataSetType.MEANS_DATA.getId()){
						countData = String.valueOf(getStudyDataManager().countStocks(
								item.getDataSetId()
								,trialEnvironmentsList.get(item.getDataSetId()).findOnlyOneByLocalName(item.getTrialFactorName(), item.getTrial()).getId()
								,varType.getId()
									)
								);
					}else{
						countData = String.valueOf(getStudyDataManager().countStocks(
								item.getDataSetId()
								,trialEnvironmentsList.get(item.getDataSetId()).findOnlyOneByLocalName(item.getTrialFactorName(), item.getTrial()).getId()
								,varType.getId()
									)
								);
					}
					
				} catch (MiddlewareQueryException e) {
					
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

				private static final long serialVersionUID = 6946721935764963485L;

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

			private static final long serialVersionUID = -850728728803335183L;

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
		 HashMap<String, Boolean> factorsColumnList = new HashMap<String, Boolean>();
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
		            			) {
                            continue;
                        }
						
						Boolean isGidOrDesig = false;
						
						if (f.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_DESIGNATION_STORAGE.getId() ||
								f.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_GID_STORAGE.getId()
		            			) {
                            isGidOrDesig = true;
                        }
						
						try{
							factorsAnalysisTable.addGeneratedColumn(f.getLocalName(), generatedFactorColumn);
							factorsColumnList.put(f.getLocalName(), isGidOrDesig);
						}catch(Exception e){}
					}
					
					
				} catch (MiddlewareQueryException e) {
					
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
		 for (Entry<String, Boolean>  s : factorsColumnList.entrySet()) {
             visibleCols.add(s.getKey());
         }
		 factorsAnalysisTable.setVisibleColumns(visibleCols.toArray());
		 visibleCols.clear();
		 visibleCols.add("Dataset Name");
		 visibleCols.add("Trial");
		 visibleCols.add("Environment");
		 for (Entry<String, Boolean>  s : factorsColumnList.entrySet()) {
             visibleCols.add(s.getKey());
         }
		 factorsAnalysisTable.setColumnHeaders(visibleCols.toArray(new String[0]));
		 
		 Property.ValueChangeListener traitCheckBoxListener = new Property.ValueChangeListener(){
			
			private static final long serialVersionUID = 1572419094504976594L;

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
		 variatesSelectionTable.setColumnCollapsingAllowed(true);
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
				
			private static final long serialVersionUID = 456441415676960629L;

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
		 factorsSelectionTable.setColumnCollapsingAllowed(true);
		 List<CheckBox> fCheckBoxes = new ArrayList<CheckBox>();
		 IndexedContainer factorsSelectionTableContainer = new IndexedContainer();
		 for (Entry<String, Boolean> s : factorsColumnList.entrySet()){
			 factorsSelectionTableContainer.addContainerProperty(s.getKey(), CheckBox.class, null);
				 CheckBox factorCheckBox = new CheckBox();
				 factorCheckBox.setImmediate(true);
				 factorCheckBox.addListener(factorCheckBoxListener);
				 factorCheckBox.setData(s.getKey());
				 fCheckBoxes.add(factorCheckBox);
				 factorsCheckBoxState.put(s.getKey(), false);
				 
				 if (s.getValue()){//GID and DESIG factors are required
					 factorCheckBox.setValue(true);
					 factorCheckBox.setCaption("Required");
					 factorCheckBox.setStyleName("gcp-required-caption");
					 factorCheckBox.setEnabled(false);
					 factorsCheckBoxState.put(s.getKey(), true);
				 }
				 
				 
		 }
		 factorsSelectionTable.setContainerDataSource(factorsSelectionTableContainer);
		 factorsSelectionTable.addItem(fCheckBoxes.toArray(), 1);
		
	}
	
	private void initializeLayout(){
		
		setSizeUndefined();
		setSpacing(true);
		setWidth("95%");
		
		VerticalLayout layout1 = new VerticalLayout();
		layout1.setMargin(new MarginInfo(false,true,false,true));
		layout1.setSpacing(true);
		layout1.addComponent(lblPageTitle);
		layout1.addComponent(lblSelectEnvVarForAnalysis);
		layout1.addComponent(lblSelectEnvVarForAnalysisDesc);
		layout1.addComponent(environmentsTable);
		layout1.addComponent(chkSelectAllEnvironments);
		addComponent(layout1);
		
		VerticalLayout layout2 = new VerticalLayout();
		layout2.setMargin(new MarginInfo(false,true,false,true));
		layout2.setSpacing(true);
		layout2.addComponent(lblSelectVariates);
		layout2.addComponent(variatesSelectionTable);
		layout2.addComponent(chkSelectAllVariates);
		addComponent(layout2);
		
		VerticalLayout layout3 = new VerticalLayout();
		layout3.setMargin(new MarginInfo(true,true,false,true));
		layout3.setSpacing(true);
		layout3.addComponent(lblSelectFactorsForAnalysis);
		layout3.addComponent(lblSelectFactorsForAnalysisDesc);
		layout3.addComponent(factorsAnalysisTable);
		addComponent(layout3);
		
		VerticalLayout layout4 = new VerticalLayout();
		layout4.setMargin(new MarginInfo(false,true,false,true));
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
	        
	        btnBack = new Button();
	        btnReset = new Button();
	        btnNext = new Button();
	        btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
	   
	        buttonLayout.addComponent(btnBack);
	        buttonLayout.addComponent(btnReset);
	        buttonLayout.addComponent(btnNext);
	        buttonLayout.setComponentAlignment(btnBack, Alignment.TOP_CENTER);
	        buttonLayout.setComponentAlignment(btnNext, Alignment.TOP_CENTER);
	        return buttonLayout;
	    }
	 
	 protected void initializeActions() {
	    	btnBack.addListener(new Button.ClickListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
					IContentWindow window = (IContentWindow) event.getComponent().getWindow();
					selectDatasetsForMetaAnalysisPanel.setParent(null);
					window.showContent(selectDatasetsForMetaAnalysisPanel);
				}
			});
	    	
	    	btnReset.addListener(new Button.ClickListener() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick(ClickEvent event) {
		
					chkSelectAllVariates.setValue(true);
					chkSelectAllVariates.setValue(false);
					
					chkSelectAllFactors.setValue(true);
					chkSelectAllFactors.setValue(false);
					
					
					chkSelectAllEnvironments.setValue(true);
				}
			});
	    	
	        btnNext.addListener(new Button.ClickListener() {
				
				private static final long serialVersionUID = -4809085840378185820L;

				@Override
				public void buttonClick(ClickEvent event) {
						if (variatesCheckBoxState.size() == 0 
								|| factorsCheckBoxState.size() == 0 
								
								) { return;}
						
						final File file = exportData();
						
						managerFactory.close();
						
						if (file == null) {
                            return;
                        }
						
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
		Boolean headerRowCreated=false;
		List<String> supressColumnList = new ArrayList<String>();
		
		Row headerRow = defaultSheet.createRow(rowCounter++);
		
		Iterator<?> envIterator = environmentsTable.getItemIds().iterator();
		while(envIterator.hasNext()){
			MetaEnvironmentModel envModel = (MetaEnvironmentModel) envIterator.next();
			
			if (envModel.getActive()){
				
				 try {
					 
					String desigFactorName = "";
					String gidFactorName = "";
					String entrynoFactorName = "";
					
					List<Experiment> exps = getStudyDataManager().getExperiments(envModel.getDataSetId(), 0, Integer.MAX_VALUE);
					Experiment e = exps.get(0);
					if (e != null){
						for (VariableType var : e.getFactors().getVariableTypes().getVariableTypes()){
							if (var.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_DESIGNATION_STORAGE.getId()){
								desigFactorName = var.getLocalName();
							}else if (var.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_GID_STORAGE.getId()){
								gidFactorName = var.getLocalName();
							}else if (var.getStandardVariable().getStoredIn().getId() == TermId.ENTRY_NUMBER_STORAGE.getId()){
								entrynoFactorName = var.getLocalName();
							}
						}
					}
					
					for (Experiment exp : exps){
						
						if (!headerRowCreated){
							
							headerRow.createCell(cellCounter++).setCellValue("STUDYNAME");	
							headerRow.createCell(cellCounter++).setCellValue("TRIALID");	
							headerRow.createCell(cellCounter++).setCellValue("ENTRYID");	
							if (desigFactorName!="") {
                                headerRow.createCell(cellCounter++).setCellValue(desigFactorName);
                            } else {
                                headerRow.createCell(cellCounter++).setCellValue("DESIG");
                            }
							if (gidFactorName!="") {
                                headerRow.createCell(cellCounter++).setCellValue(gidFactorName);
                            } else {
                                headerRow.createCell(cellCounter++).setCellValue("GID");
                            }
							supressColumnList.add(desigFactorName);
							supressColumnList.add(gidFactorName);
							for (Entry<String, Boolean> entry : factorsCheckBoxState.entrySet()){
								//suppress the desig and gid columns
								if (supressColumnList.contains(entry.getKey())) {
                                    continue;
                                }
								
								if (entry.getValue()) {
                                    headerRow.createCell(cellCounter++).setCellValue(entry.getKey());
                                }
							}
							for (Entry<String, Boolean> entry : variatesCheckBoxState.entrySet()){
								if (entry.getValue()) {
                                    headerRow.createCell(cellCounter++).setCellValue(entry.getKey());
                                }
							}
							
							headerRowCreated = true;
						}//if header Row Created
						
						
						Variable trialVariable = exp.getFactors().findByLocalName(envModel.getTrialFactorName());
						if (trialVariable == null) {
                            continue;
                        }
						if (!trialVariable.getValue().equalsIgnoreCase(envModel.getTrial())) {
                            continue;
                        }
						
						cellCounter = 0;
						Row row = defaultSheet.createRow(rowCounter++);
						
						row.createCell(cellCounter++).setCellValue(envModel.getStudyName());	//STUDYNAME
						row.createCell(cellCounter++).setCellValue(String.format("%s-%s", envModel.getStudyId(), envModel.getTrial()));	//TRIALID	
						Variable varEntryNo = exp.getFactors().findByLocalName(entrynoFactorName); ////ENTRYID
						if (varEntryNo != null) {
                            row.createCell(cellCounter++).setCellValue(String.format("%s-%s", envModel.getStudyId(), varEntryNo.getValue()));
                        } else {
                            row.createCell(cellCounter++).setCellValue("");
                        }
						Variable varDesig = exp.getFactors().findByLocalName(desigFactorName); //DESIG
						if (varDesig != null) {
                            row.createCell(cellCounter++).setCellValue(varDesig.getValue());
                        } else {
                            row.createCell(cellCounter++).setCellValue("");
                        }
						Variable varGid = exp.getFactors().findByLocalName(gidFactorName); //GID
						if (varGid != null) {
                            row.createCell(cellCounter++).setCellValue(varGid.getValue());
                        } else {
                            row.createCell(cellCounter++).setCellValue("");
                        }
						
						for (Entry<String, Boolean> entry : factorsCheckBoxState.entrySet()){
							
							//suppress the desig and gid columns
							if (supressColumnList.contains(entry.getKey())) {
                                continue;
                            }
							
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
					
					e.printStackTrace();
				}
				
			
				
			}
		}//while
		
		try {
			//NOTE: Directory location is hardcoded to workspace/<projectId/breeding_view/input>
			String dir = "workspace" + File.separator + currentProject.getProjectName().toString() + File.separator + "breeding_view" + File.separator + "input";
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
		
		messageSource.setCaption(btnBack, Message.BACK);
		messageSource.setCaption(btnReset, Message.RESET);
        messageSource.setCaption(btnNext, Message.EXPORT_DATA);
        messageSource.setValue(lblPageTitle, Message.TITLE_METAANALYSIS);
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
		assemble();	
	}
	
	 @Override
	    public void attach() {
	        super.attach();
	        
	        updateLabels();
	    }
	
	public StudyDataManager getStudyDataManager() {
	    	if (this.studyDataManager == null) {
                this.studyDataManager = getManagerFactory().getNewStudyDataManager();
            }
			return this.studyDataManager;
	}


	public ManagerFactory getManagerFactory() {
		return managerFactory;
	}


}

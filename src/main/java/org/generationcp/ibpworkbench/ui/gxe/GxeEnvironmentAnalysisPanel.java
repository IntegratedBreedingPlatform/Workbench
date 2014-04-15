/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.gxe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.GxeUtility;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Aldrin Batac
 *
 */
@Configurable
public class GxeEnvironmentAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private GxeTable gxeTable;
    private Table selectTraitsTable;
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;

    private String selectedEnvFactorName;
    private String selectedEnvGroupFactorName;
    private String selectedGenotypeFactorName;
    
    private Button btnBack;
    private Button btnReset;
    private Button btnRunBreedingView;
    private Map<String, Boolean> variatesCheckboxState;
    private GxeSelectEnvironmentPanel gxeSelectEnvironmentPanel;
    
    @Autowired
	private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
	private ToolUtil toolUtil;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;

	private Label lblDataSelectedForAnalysisHeader;
	private Label lblDatasetName;
	private Label txtDatasetName;
	private Label lblDatasourceName;
	private Label txtDatasourceName;
	private Label lblSelectedEnvironmentFactor;
	private Label txtSelectedEnvironmentFactor;
	private Label lblSelectedEnvironmentGroupFactor;
	private Label txtSelectedEnvironmentGroupFactor;
	private Label lblAdjustedMeansHeader;
	private Label lblAdjustedMeansDescription;
	private Label lblSelectTraitsForAnalysis;
	private CheckBox chkSelectAllEnvironments;
	private CheckBox chkSelectAllTraits;
	private Property.ValueChangeListener selectAllEnvironmentsListener;
	private Property.ValueChangeListener selectAllTraitsListener;
	
	
    public GxeEnvironmentAnalysisPanel(StudyDataManager studyDataManager,
    		Project currentProject, 
    		Study study,  
    		GxeSelectEnvironmentPanel gxeSelectEnvironmentPanel, 
    		String selectedEnvFactorName,
    		String selectedGenotypeFactorName,
    		String selectedEnvGroupFactorName,
    		Map<String, Boolean> variatesCheckboxState) {
    	this.studyDataManager = studyDataManager;
        this.currentProject = currentProject;
        this.currentStudy = study;
        this.gxeSelectEnvironmentPanel = gxeSelectEnvironmentPanel;
        this.selectedEnvFactorName = selectedEnvFactorName;
        this.selectedGenotypeFactorName = selectedGenotypeFactorName;
        this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
        this.variatesCheckboxState = variatesCheckboxState;
        
        setWidth("100%");
        setSpacing(true);
		setMargin(true);
		setCaption(study.getName());
        
    }
    
	public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public Study getCurrentStudy() {
        return currentStudy;
    }

    public void setCurrentStudy(Study currentStudy) {
        this.currentStudy = currentStudy;
    }

    public Integer getCurrentRepresentationId() {
        return currentRepresentationId;
    }

    public void setCurrentRepresentationId(Integer currentRepresentationId) {
        this.currentRepresentationId = currentRepresentationId;
    }
    
    public Integer getCurrentDataSetId() {
        return currentDataSetId;
    }

    public void setCurrentDataSetId(Integer currentDataSetId) {
        this.currentDataSetId = currentDataSetId;
    }

    public String getCurrentDatasetName() {
        return currentDatasetName;
    }

    public void setCurrentDatasetName(String currentDatasetName) {
        this.currentDatasetName = currentDatasetName;
    }
    
    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
		return this.studyDataManager;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}
	
	public void setVariatesCheckboxState(HashMap<String, Boolean> hashMap) {
			this.variatesCheckboxState = hashMap;
	}

	public GxeTable getGxeTable() {
		return gxeTable;
	}

	public void setGxeTable(GxeTable gxeTable) {
		this.gxeTable = gxeTable;
	}

	public String getSelectedEnvFactorName() {
		return selectedEnvFactorName;
	}

	public void setSelectedEnvFactorName(String selectedEnvFactorName) {
		this.selectedEnvFactorName = selectedEnvFactorName;
	}
	
	public String getSelectedEnvGroupFactorName() {
		return selectedEnvGroupFactorName;
	}

	public void setSelectedEnvGroupFactorName(String selectedEnvGroupFactorName) {
		this.selectedEnvGroupFactorName = selectedEnvGroupFactorName;
	}

    protected void initializeComponents() {
    	
    	lblDataSelectedForAnalysisHeader = new Label();
    	lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	lblDatasetName = new Label();
    	lblDatasetName.setStyleName("label-bold");
    	txtDatasetName = new Label();
    	lblDatasourceName = new Label();
    	lblDatasourceName.setStyleName("label-bold");
    	txtDatasourceName = new Label();
    	lblSelectedEnvironmentFactor = new Label();
    	lblSelectedEnvironmentFactor.setStyleName("label-bold");
    	txtSelectedEnvironmentFactor = new Label();
    	lblSelectedEnvironmentGroupFactor = new Label();
    	lblSelectedEnvironmentGroupFactor.setStyleName("label-bold");
    	txtSelectedEnvironmentGroupFactor = new Label();
    	
    	chkSelectAllEnvironments = new CheckBox("Select all environments", true);
    	chkSelectAllEnvironments.setImmediate(true);
    	chkSelectAllTraits = new CheckBox("Select all traits", true);
    	chkSelectAllTraits.setImmediate(true);
    	
    	lblAdjustedMeansHeader  = new Label();
    	lblAdjustedMeansHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	lblAdjustedMeansDescription  = new Label();
    	lblSelectTraitsForAnalysis = new Label();
    	
    	selectAllEnvironmentsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = gxeTable.getItemIds().iterator(); itr.hasNext();){
					CheckBox chk = (CheckBox) gxeTable.getItem(itr.next()).getItemProperty((Object) " ").getValue();
					chk.setValue((Boolean) event.getProperty().getValue());
				}
				
			}
		};
    	
    	selectAllTraitsListener = new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				for (Iterator<?> itr = selectTraitsTable.getContainerPropertyIds().iterator(); itr.hasNext();){
					CheckBox chk = (CheckBox) selectTraitsTable.getItem(1).getItemProperty(itr.next()).getValue();
					chk.setValue(event.getProperty().getValue());
				}
				
				
			}
		};
    	
    	
    	btnRunBreedingView = new Button();
		btnBack = new Button();  
		btnReset = new Button(); 
        
    }

	protected void initializeLayout() {
		
		GridLayout selectedInfoLayout = new GridLayout(4, 3);
        selectedInfoLayout.setSizeUndefined();
        selectedInfoLayout.setWidth("100%");
        selectedInfoLayout.setSpacing(true);
        selectedInfoLayout.setMargin(true, false, true, false);
        selectedInfoLayout.setColumnExpandRatio(0, 1);
        selectedInfoLayout.setColumnExpandRatio(1, 3);
        selectedInfoLayout.setColumnExpandRatio(2, 2);
        selectedInfoLayout.setColumnExpandRatio(3, 1);
        selectedInfoLayout.addComponent(lblDataSelectedForAnalysisHeader , 0, 0, 3, 0);
        selectedInfoLayout.addComponent(lblDatasetName, 0, 1);
        selectedInfoLayout.addComponent(txtDatasetName, 1, 1);
        selectedInfoLayout.addComponent(lblDatasourceName, 0, 2);
        selectedInfoLayout.addComponent(txtDatasourceName, 1, 2);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentFactor, 2, 1);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentFactor, 3, 1);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentGroupFactor , 2, 2);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentGroupFactor, 3, 2);
		
		addComponent(selectedInfoLayout);
		addComponent(lblAdjustedMeansHeader);
		addComponent(lblAdjustedMeansDescription);
		
		List<DataSet> ds = null;
		try {
			ds = studyDataManager.getDataSetsByType(currentStudy.getId(), DataSetType.MEANS_DATA);
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
		
		if (ds != null && ds.size() > 0){
			setCaption(ds.get(0).getName());
			txtDatasetName.setValue(ds.get(0).getName());
			txtDatasourceName.setValue(currentStudy.getName());
			txtSelectedEnvironmentFactor.setValue(getSelectedEnvFactorName());
			txtSelectedEnvironmentGroupFactor.setValue(getSelectedEnvGroupFactorName());
			
			Property.ValueChangeListener envCheckBoxListener = new Property.ValueChangeListener(){
				
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					Boolean val = (Boolean) event.getProperty().getValue();
					if (val == false){
						chkSelectAllEnvironments.removeListener(selectAllEnvironmentsListener);
						chkSelectAllEnvironments.setValue(false);
						chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
					}
					
				}
				
			};
			
			
			setGxeTable(new GxeTable(studyDataManager, currentStudy.getId(), getSelectedEnvFactorName(), selectedEnvGroupFactorName, variatesCheckboxState, envCheckBoxListener));
			getGxeTable().setHeight("300px");
			addComponent(getGxeTable());
			addComponent(chkSelectAllEnvironments);
			setExpandRatio(getGxeTable(), 1.0F);
			
		}else{
			Label temp = new Label("&nbsp;&nbsp;No means dataset available for this study (" + currentStudy.getName().toString() + ")" );
			temp.setContentMode(Label.CONTENT_XHTML);
			addComponent(temp);
			setExpandRatio(temp, 1.0F);
		}
		
		addComponent(lblSelectTraitsForAnalysis);
		
		selectTraitsTable = new Table();
		IndexedContainer container = new IndexedContainer();
		
		Property.ValueChangeListener traitCheckBoxListener = new Property.ValueChangeListener(){
		
			private static final long serialVersionUID = -1109780465477901066L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Boolean val = (Boolean) event.getProperty().getValue();
				
				if (val == false){
					chkSelectAllTraits.removeListener(selectAllTraitsListener);
					chkSelectAllTraits.setValue(false);
					chkSelectAllTraits.addListener(selectAllTraitsListener);
				}
				
			}
			
		};
		
		
		List<CheckBox> cells = new ArrayList<CheckBox>();
		List<String> columnNames = new ArrayList<String>();
		

		SortedSet<String> keys = new TreeSet<String>(getVariatesCheckboxState().keySet());
		for (String key : keys) { 
		   if(getVariatesCheckboxState().get(key)){
			   container.addContainerProperty(key, CheckBox.class, null);
				columnNames.add(key.replace("_Means", ""));
				CheckBox chk = new CheckBox("", true);
				chk.setImmediate(true);
				chk.addListener(traitCheckBoxListener);
				cells.add(chk);
		   }
		 
		}
		
		selectTraitsTable.setContainerDataSource(container);
		selectTraitsTable.addItem(cells.toArray(new Object[0]), 1);
		selectTraitsTable.setHeight("80px");
		selectTraitsTable.setWidth("100%");
		selectTraitsTable.setColumnHeaders(columnNames.toArray(new String[0]));
		selectTraitsTable.setColumnCollapsingAllowed(true);
		for (Entry<String, Boolean> trait : getVariatesCheckboxState().entrySet()){
			if (trait.getValue()){
				selectTraitsTable.setColumnWidth(trait.getKey(), 100);
			}
		}
		
		addComponent(selectTraitsTable);
		setExpandRatio(selectTraitsTable, 1.0F);
		addComponent(chkSelectAllTraits);
		
        addComponent(layoutButtonArea());
        
    }
    
    protected void initialize() {
    
    }

    protected void initializeActions() {
		//Generate Buttons
		
		btnRunBreedingView.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7090745965019240566L;

			@Override
			public void buttonClick(ClickEvent event) {
				final ClickEvent buttonClickEvent = event;
				launchBV(false,buttonClickEvent.getComponent().getWindow());
						
			}
			
			private void launchBV(boolean isXLS,final Window windowSource) {
				String inputDir = "";
				Tool breedingViewTool = null;
				try{
					breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
					inputDir = toolUtil.getInputDirectoryForTool(currentProject, breedingViewTool);
				}catch(MiddlewareQueryException ex){
					
				}

				String inputFileName = "";
				
				if (currentStudy == null){
					MessageNotifier
					.showError(windowSource,
							"Cannot export dataset",
							"No dataset is selected. Please open a study that has a dataset.");
					
					return;
				}
				
				
				if (gxeTable != null) {
					
					
					GxeInput gxeInput =  new GxeInput(currentProject, "", 0, 0, "", "", "", "");
		
					
					inputFileName = String.format("%s_%s_%s", currentProject.getProjectName().trim(), gxeTable.getMeansDataSetId(), gxeTable.getMeansDataSet().getName());
					GxeEnvironment gxeEnv = gxeTable.getGxeEnvironment();
					
					
					List<Trait> selectedTraits = new ArrayList<Trait>();
					Iterator<?> itr = selectTraitsTable.getItem(1).getItemPropertyIds().iterator();
					
					while (itr.hasNext()){
						Object propertyId = itr.next();
						CheckBox cb = (CheckBox)selectTraitsTable.getItem(1).getItemProperty(propertyId).getValue();
						if ((Boolean)cb.getValue()){
							Trait t = new Trait();
							t.setName(propertyId.toString());
							t.setActive(true);
							//t.setId(counter++);
							selectedTraits.add(t);
						}
					}
					
					
					List<Environment> selectedEnnvironments = gxeTable.getSelectedEnvironments();
					

					
					File datasetExportFile = null;
					
					datasetExportFile = GxeUtility.exportGxEDatasetToBreadingViewCsv(gxeTable.getMeansDataSet(), gxeTable.getExperiments(),gxeTable.getEnvironmentName(), selectedEnvGroupFactorName , selectedGenotypeFactorName ,gxeEnv,selectedTraits, currentProject);
					
					if (isXLS)
						gxeInput.setSourceXLSFilePath(datasetExportFile.getAbsolutePath());
					else
						gxeInput.setSourceCSVFilePath(datasetExportFile.getAbsolutePath());
				
					gxeInput.setDestXMLFilePath(String.format("%s\\%s.xml", inputDir, inputFileName));
					gxeInput.setTraits(selectedTraits);
					gxeInput.setEnvironment(gxeEnv);
					gxeInput.setSelectedEnvironments(selectedEnnvironments);
					gxeInput.setEnvironmentGroup(selectedEnvGroupFactorName);
					
				
					Genotypes genotypes = new Genotypes();
					if (selectedGenotypeFactorName != null && selectedGenotypeFactorName != "") {
							genotypes.setName(selectedGenotypeFactorName);
						}else{
							genotypes.setName("G!");
						}
						
				
	
					gxeInput.setGenotypes(genotypes);
					gxeInput.setEnvironmentName(gxeTable.getEnvironmentName());
					gxeInput.setBreedingViewProjectName(currentProject.getProjectName());
					
					
					
					
					GxeUtility.generateXmlFieldBook(gxeInput);
					
					File absoluteToolFile = new File(breedingViewTool.getPath()).getAbsoluteFile();
		            try {
						ProcessBuilder pb = new ProcessBuilder(absoluteToolFile.getAbsolutePath(), "-project=", gxeInput.getDestXMLFilePath());
						Process process = pb.start();

						/* Added while loop to get input stream because process.waitFor() has a problem
						 * Reference: 
						 * http://stackoverflow.com/questions/5483830/process-waitfor-never-returns
						 */
						BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
						while ((reader.readLine()) != null) {}

						process.waitFor();

                        MessageNotifier.showMessage(windowSource, "GxE files saved", "Successfully generated the means dataset and xml input files for breeding view.");
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                        MessageNotifier.showMessage(windowSource, "Cannot launch " + absoluteToolFile.getName(), "But it successfully created GxE Excel and XML input file for the breeding_view!");
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        MessageNotifier.showMessage(windowSource, "Cannot launch " + absoluteToolFile.getName(), "But it successfully created GxE Excel and XML input file for the breeding_view!");
                    }
				}
				
				managerFactory.close();
			}
		});
		
		
		btnBack.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				TabSheet tabSheet = gxeSelectEnvironmentPanel.getGxeAnalysisComponentPanel()
					.getStudiesTabsheet();
				tabSheet.replaceComponent(tabSheet.getSelectedTab(), gxeSelectEnvironmentPanel);
					
			}
		});
		
		btnReset.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				chkSelectAllTraits.setValue(true);
				chkSelectAllEnvironments.setValue(true);
			}
		});
		
		chkSelectAllEnvironments.addListener(selectAllEnvironmentsListener);
		
		chkSelectAllTraits.addListener(selectAllTraitsListener);

    }
    

    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        buttonLayout.setSizeFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

        btnRunBreedingView.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
        spacer.setSizeFull();
        
        buttonLayout.addComponent(spacer);
        buttonLayout.setExpandRatio(spacer,1.0F);
        buttonLayout.addComponent(btnBack);
        buttonLayout.addComponent(btnReset);
        buttonLayout.addComponent(btnRunBreedingView);

        return buttonLayout;
    }
    
  
    
  

    protected void assemble() {
        initialize();
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
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(btnBack, Message.BACK);
        messageSource.setCaption(btnReset, Message.RESET);
        messageSource.setCaption(btnRunBreedingView, Message.LAUNCH_BREEDING_VIEW);
        messageSource.setValue(lblDataSelectedForAnalysisHeader, Message.GXE_SELECTED_INFO);
        messageSource.setValue(lblDatasetName , Message.BV_DATASET_NAME);
        messageSource.setValue(lblDatasourceName, Message.BV_DATASOURCE_NAME);
        messageSource.setValue(lblSelectedEnvironmentFactor, Message.GXE_SELECTED_ENVIRONMENT_FACTOR);
        messageSource.setValue(lblSelectedEnvironmentGroupFactor, Message.GXE_SELECTED_ENVIRONMENT_GROUP_FACTOR);
        messageSource.setValue(lblAdjustedMeansHeader , Message.GXE_ADJUSTED_MEANS_HEADER);
        messageSource.setValue(lblAdjustedMeansDescription  , Message.GXE_ADJUSTED_MEANS_DESCRIPTION);
        messageSource.setValue(lblSelectTraitsForAnalysis, Message.GXE_SELECT_TRAITS_FOR_ANALYSIS);
    }

    @Override
	public Object getData(){
		return this.getCurrentStudy();
		
	}

    


}

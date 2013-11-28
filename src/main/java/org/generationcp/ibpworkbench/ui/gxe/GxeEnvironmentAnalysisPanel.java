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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.StudiesTabCloseListener;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.GxeUtility;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
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
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;
    
    private Role role;

    private String selectedEnvFactorName;
    
    private Button btnCancel;
    private Button btnRunBreedingView;
    private Map<String, Boolean> variatesCheckboxState;
    
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
	private TextField txtDatasetName;
	private Label lblDatasourceName;
	private TextField txtDatasourceName;
	private Label lblSelectedEnvironmentFactor;
	private TextField txtSelectedEnvironmentFactor;
	private Label lblSelectedEnvironmentGroupFactor;
	private TextField txtSelectedEnvironmentGroupFactor;
	private Label lblAdjustedMeansHeader;
	private Label lblAdjustedMeansDescription;
	private Label lblSelectTraitsForAnalysis;
	
	
    public GxeEnvironmentAnalysisPanel(StudyDataManager studyDataManager,Project currentProject, Study study, GxeComponentPanel gxeAnalysisComponentPanel, String selectedEnvFactorName, Map<String, Boolean> variatesCheckboxState) {
    	this.studyDataManager = studyDataManager;
        this.currentProject = currentProject;
        this.currentStudy = study;
        this.selectedEnvFactorName = selectedEnvFactorName;
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

    protected void initializeComponents() {
    	
    	lblDataSelectedForAnalysisHeader = new Label();
    	lblDatasetName = new Label();
    	txtDatasetName = new TextField();
    	lblDatasourceName = new Label();
    	txtDatasourceName = new TextField();
    	lblSelectedEnvironmentFactor = new Label();
    	txtSelectedEnvironmentFactor = new TextField();
    	lblSelectedEnvironmentGroupFactor = new Label();
    	txtSelectedEnvironmentGroupFactor = new TextField();
    	
    	lblAdjustedMeansHeader  = new Label();
    	lblAdjustedMeansDescription  = new Label();
    	lblSelectTraitsForAnalysis = new Label();
    	
    	
    	btnRunBreedingView = new Button();
		btnCancel = new Button();    	
        
    }

	protected void initializeLayout() {
		
		List<DataSet> ds = null;
		try {
			ds = studyDataManager.getDataSetsByType(currentStudy.getId(), DataSetType.MEANS_DATA);
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
		
		if (ds != null && ds.size() > 0){
			setGxeTable(new GxeTable(studyDataManager, currentStudy.getId(), getSelectedEnvFactorName(), variatesCheckboxState));
			addComponent(getGxeTable());
			setExpandRatio(getGxeTable(), 1.0F);
			
			ds.get(0);
		}else{
			Label temp = new Label("&nbsp;&nbsp;No means dataset available for this study (" + currentStudy.getName().toString() + ")" );
			temp.setContentMode(Label.CONTENT_XHTML);
			addComponent(temp);
			setExpandRatio(temp, 1.0F);
		}
		
		GridLayout selectedInfoLayout = new GridLayout(3, 4);
        selectedInfoLayout.setSizeUndefined();
        selectedInfoLayout.setWidth("95%");
        selectedInfoLayout.setSpacing(true);
        selectedInfoLayout.setMargin(true, false, true, false);
        selectedInfoLayout.setColumnExpandRatio(0, 1.2f);
        selectedInfoLayout.setColumnExpandRatio(1, 5);
        selectedInfoLayout.setColumnExpandRatio(2, 1);
        selectedInfoLayout.setColumnExpandRatio(3, 2);
        selectedInfoLayout.addComponent(lblDataSelectedForAnalysisHeader , 0, 0, 3, 0);
        selectedInfoLayout.addComponent(lblDatasetName, 0, 1);
        selectedInfoLayout.addComponent(txtDatasetName, 1, 1);
        selectedInfoLayout.addComponent(lblDatasourceName, 0, 2);
        selectedInfoLayout.addComponent(txtDatasourceName, 1, 2);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentFactor, 2, 1);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentFactor, 3, 1);
        selectedInfoLayout.addComponent(lblSelectedEnvironmentGroupFactor , 2, 2);
        selectedInfoLayout.addComponent(txtSelectedEnvironmentGroupFactor, 3, 2);
		
        
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
		
					
					inputFileName = String.format("%s_%s_%s", currentProject.getProjectName().trim(), gxeTable.getMeansDataSetId(), gxeTable.getMeansDataSet().getName());
					GxeEnvironment gxeEnv = gxeTable.getGxeEnvironment();
					
					List<Trait> selectedTraits = gxeTable.getSelectedTraits();
					
					File datasetExportFile = null;
					
					if (isXLS)
						datasetExportFile = GxeUtility.exportGxEDatasetToBreadingViewXls(gxeTable.getMeansDataSet(), gxeTable.getExperiments(),gxeTable.getEnvironmentName(),gxeEnv,selectedTraits, currentProject);
					else
						datasetExportFile = GxeUtility.exportGxEDatasetToBreadingViewCsv(gxeTable.getMeansDataSet(), gxeTable.getExperiments(),gxeTable.getEnvironmentName(),gxeEnv,selectedTraits, currentProject);
					
					
					GxeInput gxeInput =  new GxeInput(currentProject, "", 0, 0, "", "", "", "");
					
					if (isXLS)
						gxeInput.setSourceXLSFilePath(datasetExportFile.getAbsolutePath());
					else
						gxeInput.setSourceCSVFilePath(datasetExportFile.getAbsolutePath());
				
					gxeInput.setDestXMLFilePath(String.format("%s\\%s.xml", inputDir, inputFileName));
					gxeInput.setTraits(selectedTraits);
					gxeInput.setEnvironment(gxeEnv);
					Genotypes genotypes = new Genotypes();
					
					try {
						String strGenoType;
						strGenoType = studyDataManager.getLocalNameByStandardVariableId(gxeTable.getMeansDataSetId(), 8230);
						if (strGenoType != null && strGenoType != "") genotypes.setName(strGenoType);
					} catch (MiddlewareQueryException e1) {
						genotypes.setName("G!");
					}
	
					gxeInput.setGenotypes(genotypes);
					gxeInput.setEnvironmentName(gxeTable.getEnvironmentName());
					gxeInput.setBreedingViewProjectName(currentProject.getProjectName());
					
					GxeUtility.generateXmlFieldBook(gxeInput);
					
					File absoluteToolFile = new File(breedingViewTool.getPath()).getAbsoluteFile();
		            Runtime runtime = Runtime.getRuntime();
		            
		            try {
						runtime.exec(absoluteToolFile.getAbsolutePath() + " -project=\"" +  gxeInput.getDestXMLFilePath() + "\"");
					
						MessageNotifier
						.showMessage(windowSource,
								"GxE files saved",
								"Successfully generated the means dataset and xml input files for breeding view.");
		            } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						MessageNotifier
						.showMessage(windowSource,
								"Cannot launch " + absoluteToolFile.getName(),
								"But it successfully created GxE Excel and XML input file for the breeding_view!");
					}
				}
			}
		});
		
		
		btnCancel.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
			        
				
	            String url = String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", currentProject.getProjectId(), role.getRoleId());
	            (new OpenWorkflowForRoleAction(currentProject)).doAction(event.getComponent().getWindow(), url, true);
				} catch (Exception e) {
					
		            if(e.getCause() instanceof InternationalizableException) {
		                InternationalizableException i = (InternationalizableException) e.getCause();
		                MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
		            }
		            return;
				}
			}
		});

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
        buttonLayout.addComponent(btnCancel);
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
        messageSource.setCaption(btnCancel, Message.CANCEL);
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

    


}

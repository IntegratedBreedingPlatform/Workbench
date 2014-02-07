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

package org.generationcp.ibpworkbench.ui.ibtools.breedingview.select;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.BreedingViewDesignTypeValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewEnvFactorValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewReplicatesValueChangeListener;
import org.generationcp.ibpworkbench.actions.RunBreedingViewAction;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.mysql.jdbc.StringUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class SelectDetailsForBreedingViewPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private static final String REPLICATION_FACTOR = "replication factor";
    private static final String BLOCKING_FACTOR = "blocking factor";
    private static final String ROW_FACTOR = "row in layout";
    private static final String COLUMN_FACTOR = "column in layout";
    
    private Label lblPageTitle;
    private Label lblTitle;
    private Label lblDatasetName;
    private Label lblDatasourceName;
    
    private Label lblVersion;
    private Label lblProjectType;
    private Label lblAnalysisName;
    private Label lblSiteEnvironment;
    private Label lblSpecifyEnvFactor;
    private Label lblSelectEnvironmentForAnalysis;
    private Label lblSpecifyNameForAnalysisEnv;
    private Label lblDesign;
    private Label lblDesignType;
    private Label lblReplicates;
    private Label lblBlocks;
    private Label lblSpecifyRowFactor;
    private Label lblSpecifyColumnFactor;
    private Label lblGenotypes;
    private Label lblDataSelectedForAnalysisHeader;
    private Label lblAnalysisNameHeader;
    private Label lblChooseEnvironmentHeader;
    private Label lblChooseEnvironmentDescription;
    private Label lblChooseEnvironmentForAnalysisDescription;
    private Label lblSpecifyDesignDetailsHeader;
    private Label lblSpecifyGenotypesHeader;
    private Button btnRun;
    private Button btnCancel;
    private TextField txtVersion;
    private TextField txtProjectType;
    private TextField txtAnalysisName;
    private TextField txtNameForAnalysisEnv;
    private TextField txtDatasetName;
    private TextField txtDatasourceName;
    private Select selDesignType;
    private Select selEnvFactor;
    private Select selReplicates;
    private Select selBlocks;
    private Select selRowFactor;
    private Select selColumnFactor;
    private Select selGenotypes;
    
    private CheckBox footerCheckBox;
    
    private HashMap<String, Boolean> environmentsCheckboxState;
    
    private VerticalLayout tblEnvironmentLayout;
    private Table tblEnvironmentSelection;
    
    private BreedingViewInput breedingViewInput;
    private Tool tool;
    private List<VariableType> factorsInDataset;
    
    private Project project;

    private VerticalLayout mainLayout;
    
    private ManagerFactory managerFactory;
    
    private StudyDataManager studyDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private Property.ValueChangeListener envCheckBoxListener;
    private Property.ValueChangeListener footerCheckBoxListener;

    public SelectDetailsForBreedingViewPanel(Tool tool, BreedingViewInput breedingViewInput, List<VariableType> factorsInDataset
            ,Project project, StudyDataManager studyDataManager, ManagerFactory managerFactory) {

        this.tool = tool;
        this.setBreedingViewInput(breedingViewInput);
        this.factorsInDataset = factorsInDataset;
        this.project = project;
        this.studyDataManager = studyDataManager;
        this.managerFactory = managerFactory;

        setWidth("100%");
        
    }

    public Tool getTool() {
        return tool;
    }

    public TextField getTxtVersion() {
        return txtVersion;
    }

    public Select getSelDesignType() {
        return selDesignType;
    }
    
    public BreedingViewInput getBreedingViewInput() {
        return breedingViewInput;
    }
    
    public TextField getTxtProjectType() {
        return txtProjectType;
    }

    public TextField getTxtAnalysisName() {
        return txtAnalysisName;
    }
    
    public TextField getTxtNameForAnalysisEnv() {
        return txtNameForAnalysisEnv;
    }
    
    public Select getSelEnvFactor() {
        return selEnvFactor;
    }

    public Select getSelReplicates() {
        return selReplicates;
    }
    
    public Select getSelBlocks() {
        return selBlocks;
    }
    
    public Select getSelRowFactor() {
        return selRowFactor;
    }
    
    public Select getSelColumnFactor() {
        return selColumnFactor;
    }
    
    public Select getSelGenotypes() {
        return selGenotypes;
    }

    protected void initialize() {
    }

    protected void initializeComponents() {
    	
    	lblPageTitle = new Label();
    	lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());
    	
    	environmentsCheckboxState = new HashMap<String, Boolean>();
    	
    	tblEnvironmentLayout = new VerticalLayout();
    	tblEnvironmentLayout.setSizeUndefined();
    	tblEnvironmentLayout.setWidth("100%");
    	
    	tblEnvironmentSelection = new Table();
    	tblEnvironmentSelection.setHeight("200px");
    	tblEnvironmentSelection.setWidth("100%");
    	
    	envCheckBoxListener = new Property.ValueChangeListener(){

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				CheckBox chk = (CheckBox) event.getProperty();
				Boolean val = (Boolean) event.getProperty()
						.getValue();
				SeaEnvironmentModel model = (SeaEnvironmentModel) chk.getData();
			
				 TrialEnvironments trialEnvironments;
					try {
						trialEnvironments = studyDataManager.getTrialEnvironmentsInDataset(getBreedingViewInput().getDatasetId());
						TrialEnvironment trialEnv = trialEnvironments.findOnlyOneByLocalName(getSelEnvFactor().getValue().toString(), model.getEnvironmentName());
						
						if (trialEnv == null){
							
							 getWindow().showNotification("\""+ model.getEnvironmentName()  + "\" value is not a valid selection for breeding view.", Notification.TYPE_ERROR_MESSAGE);
							 chk.setValue(false);
							 model.setActive(false);
							
						}else{
							
							model.setActive(val);
							
						}
					} catch (ConfigException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MiddlewareQueryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (val == false) { 
						footerCheckBox.removeListener(footerCheckBoxListener);
						footerCheckBox.setValue(false); 
						footerCheckBox.addListener(footerCheckBoxListener);
						return; }
				
			}
			
		};
		
		
		tblEnvironmentSelection.addGeneratedColumn("select", new ColumnGenerator(){

			private static final long serialVersionUID = 8164025367842219781L;

			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				SeaEnvironmentModel item = (SeaEnvironmentModel) itemId;
				
				CheckBox chk = new CheckBox();
				chk.setData(item);
				chk.setValue(item.getActive());
				chk.setImmediate(true);
				chk.addListener(envCheckBoxListener);
				return chk;
			}
			
		});
		
		footerCheckBoxListener = new Property.ValueChangeListener(){

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

					for (Iterator<?> itr = tblEnvironmentSelection.getContainerDataSource().getItemIds().iterator(); itr.hasNext();){
						SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
						m.setActive((Boolean) event.getProperty().getValue());
					}
					
					tblEnvironmentSelection.refreshRowCache();
				}
				
			
			
		};
    	
    	
		footerCheckBox = new CheckBox("SELECT ALL",false);
		footerCheckBox.addListener(footerCheckBoxListener);
		footerCheckBox.setImmediate(true);
		
		tblEnvironmentLayout.addComponent(tblEnvironmentSelection);
		tblEnvironmentLayout.addComponent(footerCheckBox);
          
        mainLayout = new VerticalLayout();
        
        lblTitle = new Label();
        lblTitle.setContentMode(Label.CONTENT_XHTML);
        lblTitle.setStyleName(Bootstrap.Typography.H2.styleName());
        lblDatasetName = new Label();
        lblDatasetName.setContentMode(Label.CONTENT_XHTML);
        lblDatasetName.setStyleName("label-bold");
        lblDatasourceName = new Label();
        lblDatasourceName.setContentMode(Label.CONTENT_XHTML);
        lblDatasourceName.setStyleName("label-bold");
        
        lblVersion = new Label();
        lblVersion.setStyleName("label-bold");
        lblProjectType = new Label();
        lblProjectType.setStyleName("label-bold");
        lblAnalysisName = new Label();
        lblAnalysisName.setContentMode(Label.CONTENT_XHTML);
        lblSiteEnvironment = new Label();
        lblSpecifyEnvFactor = new Label();
        lblSpecifyEnvFactor.setContentMode(Label.CONTENT_XHTML);
        lblSelectEnvironmentForAnalysis = new Label();
        lblSelectEnvironmentForAnalysis.setContentMode(Label.CONTENT_XHTML);
        lblSpecifyNameForAnalysisEnv = new Label();
        lblSpecifyNameForAnalysisEnv.setContentMode(Label.CONTENT_XHTML);
        lblDesign = new Label();
        lblDesignType = new Label();
        lblDesignType.setContentMode(Label.CONTENT_XHTML);
        setLblReplicates(new Label());
        getLblReplicates().setContentMode(Label.CONTENT_XHTML);
        setLblBlocks(new Label());
        getLblBlocks().setContentMode(Label.CONTENT_XHTML);
        getLblBlocks().setVisible(false);
        setLblSpecifyRowFactor(new Label());
        getLblSpecifyRowFactor().setContentMode(Label.CONTENT_XHTML);
        getLblSpecifyRowFactor().setVisible(false);
        setLblSpecifyColumnFactor(new Label());
        getLblSpecifyColumnFactor().setContentMode(Label.CONTENT_XHTML);
        getLblSpecifyColumnFactor().setVisible(false);
        lblGenotypes = new Label();
        
        
        lblDataSelectedForAnalysisHeader = new Label();
        lblDataSelectedForAnalysisHeader.setStyleName(Bootstrap.Typography.H3.styleName());
        lblAnalysisNameHeader = new Label();
        lblAnalysisNameHeader.setStyleName(Bootstrap.Typography.H3.styleName());
        lblChooseEnvironmentHeader = new Label();
        lblChooseEnvironmentHeader.setStyleName(Bootstrap.Typography.H3.styleName());
        lblChooseEnvironmentDescription = new Label();
        lblChooseEnvironmentForAnalysisDescription = new Label();
        lblChooseEnvironmentForAnalysisDescription.setContentMode(Label.CONTENT_XHTML);
        lblSpecifyDesignDetailsHeader = new Label();
        lblSpecifyDesignDetailsHeader.setStyleName(Bootstrap.Typography.H3.styleName());
        lblSpecifyGenotypesHeader = new Label();
        lblSpecifyGenotypesHeader.setStyleName(Bootstrap.Typography.H3.styleName());
        
        txtVersion = new TextField();
        txtVersion.setNullRepresentation("");
        
        if (!StringUtils.isNullOrEmpty(getBreedingViewInput().getVersion())) {
            
            txtVersion.setValue(getBreedingViewInput().getVersion());
            txtVersion.setReadOnly(true);
            txtVersion.setRequired(false);
            
        } else {
            
            txtVersion.setNullSettingAllowed(false);
            txtVersion.setRequired(false);
            
        }
        
        txtProjectType = new TextField();
        txtProjectType.setNullRepresentation("");
        txtProjectType.setValue("Field Trial");
        txtProjectType.setReadOnly(true);
        txtProjectType.setRequired(false);
        
        txtDatasetName = new TextField();
        txtDatasetName.setWidth("100%");
        txtDatasetName.setNullRepresentation("");
        txtDatasetName.setValue(breedingViewInput.getDatasetName());
        txtDatasetName.setReadOnly(true);
        txtDatasetName.setRequired(false);
        
        txtDatasourceName = new TextField();
        txtDatasourceName.setWidth("100%");
        txtDatasourceName.setNullRepresentation("");
        txtDatasourceName.setValue(breedingViewInput.getDatasetSource());
        txtDatasourceName.setReadOnly(true);
        txtDatasourceName.setRequired(false);
        
        txtAnalysisName = new TextField();
        txtAnalysisName.setNullRepresentation("");
        if (!StringUtils.isNullOrEmpty(getBreedingViewInput().getBreedingViewAnalysisName())) {
            txtAnalysisName.setValue(getBreedingViewInput().getBreedingViewAnalysisName());
        }
        txtAnalysisName.setRequired(false);
        txtAnalysisName.setWidth("80%");
               
        
        selEnvFactor = new Select();
        selEnvFactor.setImmediate(true); 
        populateChoicesForEnvironmentFactor();
        selEnvFactor.setNullSelectionAllowed(true);
        selEnvFactor.setNewItemsAllowed(false);
    
        populateChoicesForEnvForAnalysis();
        
        txtNameForAnalysisEnv = new TextField();
        txtNameForAnalysisEnv.setNullRepresentation("");
        txtNameForAnalysisEnv.setRequired(false);
        
        selDesignType = new Select();
        selDesignType.setImmediate(true); 
        selDesignType.setNullSelectionAllowed(true);
        selDesignType.setNewItemsAllowed(false);
        selDesignType.addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.ROW_COLUMN_DESIGN.getName());
        
        checkDesignFactor();
        
        
        selReplicates = new Select();
        selReplicates.setImmediate(true); 
        populateChoicesForReplicates();
        selReplicates.setNullSelectionAllowed(true);
        selReplicates.setNewItemsAllowed(false);
        
        selBlocks = new Select();
        selBlocks.setImmediate(true); 
        populateChoicesForBlocks();
        selBlocks.setNullSelectionAllowed(false);
        selBlocks.setNewItemsAllowed(false);
        selBlocks.setVisible(false);
        
        selRowFactor = new Select();
        selRowFactor.setImmediate(true); 
        populateChoicesForRowFactor();
        selRowFactor.setNullSelectionAllowed(false);
        selRowFactor.setNewItemsAllowed(false);
        selRowFactor.setVisible(false);
        
        selColumnFactor = new Select();
        selColumnFactor.setImmediate(true); 
        populateChoicesForColumnFactor();
        selColumnFactor.setNullSelectionAllowed(false);
        selColumnFactor.setNewItemsAllowed(false);
        selColumnFactor.setVisible(false);
        
        refineChoicesForBlocksReplicationRowAndColumnFactos();
        
        selGenotypes = new Select();
        selGenotypes.setImmediate(true); 
        populateChoicesForGenotypes();
        selGenotypes.setNullSelectionAllowed(true);
        selGenotypes.setNewItemsAllowed(false);
        
        btnRun = new Button();
        btnRun.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        btnCancel = new Button();
    }

    
    private void populateChoicesForEnvironmentFactor(){
    	
    	if (this.factorsInDataset == null) return;
    	
    	for (VariableType factor : factorsInDataset){
    		if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT){
    			 this.selEnvFactor.addItem(factor.getLocalName());
    			 if (PhenotypicType.TRIAL_ENVIRONMENT.getLabelList().contains(factor.getLocalName())){
    				 this.selEnvFactor.setValue(factor.getLocalName());
    			 }
    		}
    	}
    	
    	selEnvFactor.select(selEnvFactor.getItemIds().iterator().next());
    	
        
        if (this.selEnvFactor.getItemIds().size() < 1) {
        	this.selEnvFactor.setEnabled(false);
        }else{
        	this.selEnvFactor.setEnabled(true);
        }
        
        
    }
    
    public VariableType getFactorByLocalName(String name){
    	for (VariableType factor : factorsInDataset){
    		if (factor.getLocalName().equals(name)) {
    			return factor;
    		}
    	}
    	return null;
    }
    
    public void populateChoicesForEnvForAnalysis(){
    	
    	footerCheckBox.setValue(false);
    	String trialInstanceFactor = "";

    	
    	try{
        	environmentsCheckboxState.clear();
        	tblEnvironmentSelection.removeAllItems();
        }catch(Exception e){}	
    	
        String envFactorName = (String) this.selEnvFactor.getValue();   
		
        VariableType factor = getFactorByLocalName(envFactorName);
        
        if (factor != null){
        	
			try {

				
				BeanItemContainer<SeaEnvironmentModel> container = new BeanItemContainer<SeaEnvironmentModel>(SeaEnvironmentModel.class);
				tblEnvironmentSelection.setContainerDataSource(container);
				
				VariableTypeList trialEnvFactors = studyDataManager.getDataSet(getBreedingViewInput().getDatasetId()).getVariableTypes().getFactors();
				
				for(VariableType f : trialEnvFactors.getVariableTypes()){
					
					//Always Show the TRIAL INSTANCE Factor
					if (f.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()){
						trialInstanceFactor = f.getLocalName();
					}
					
				}
				
				
				TrialEnvironments trialEnvironments;	
				trialEnvironments = studyDataManager.getTrialEnvironmentsInDataset(getBreedingViewInput().getDatasetId());
				
				
				for (TrialEnvironment env : trialEnvironments.getTrialEnvironments()){
					
						Variable trialVar = env.getVariables().findByLocalName(trialInstanceFactor);
						Variable selectedEnvVar = env.getVariables().findByLocalName(envFactorName);
						
						
						if (trialVar != null && selectedEnvVar != null){
							
							TrialEnvironment temp = trialEnvironments.findOnlyOneByLocalName(envFactorName, selectedEnvVar.getValue());
							if (temp == null) continue;
							
							SeaEnvironmentModel bean = new SeaEnvironmentModel();
							bean.setActive(false);	
							bean.setEnvironmentName(selectedEnvVar.getValue());
							bean.setTrialno(trialVar.getValue());
							container.addBean(bean);

						}else{
							continue;
						}
						
				}
				
				if (trialInstanceFactor.equalsIgnoreCase(envFactorName)){
					tblEnvironmentSelection.setVisibleColumns(new Object[] { "select", "trialno" });
					tblEnvironmentSelection.setColumnHeaders(new String[] { "SELECT",trialInstanceFactor});
					tblEnvironmentSelection.setColumnWidth("select", 45);
					tblEnvironmentSelection.setColumnWidth("trialno", -1);
					getBreedingViewInput().setTrialInstanceName(trialInstanceFactor);
				}else{
					tblEnvironmentSelection.setVisibleColumns(new Object[] { "select", "trialno", "environmentName"});
					tblEnvironmentSelection.setColumnHeaders(new String[] { "SELECT",trialInstanceFactor, envFactorName});
					tblEnvironmentSelection.setColumnWidth("select", 45);
					tblEnvironmentSelection.setColumnWidth("trialno", 60);
					tblEnvironmentSelection.setColumnWidth("environmentName", 500);
					
					
					getBreedingViewInput().setTrialInstanceName(trialInstanceFactor);
				}
				
			} catch (ConfigException e) {
				
				e.printStackTrace();
			} catch (MiddlewareQueryException e) {
				
				e.printStackTrace();
			}

        } 
    }
    
    private void populateChoicesForGenotypes(){
        
    	for (VariableType factor : factorsInDataset){
    		if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM){
    			 this.selGenotypes.addItem(factor.getLocalName());
    			 this.selGenotypes.setValue(factor.getLocalName());
    		}
    	}
    	
    	selGenotypes.select(selGenotypes.getItemIds().iterator().next());
    	
    }
    
    private void populateChoicesForReplicates(){
        for (VariableType factor : this.factorsInDataset){
        	if (factor.getStandardVariable().getProperty().getName().toString().trim().equalsIgnoreCase(REPLICATION_FACTOR)){
        		this.selReplicates.addItem(factor.getLocalName());
        		this.selReplicates.setValue(factor.getLocalName());
        	}
        }
        
        if (this.selReplicates.getItemIds().size() < 1) {
        	this.selReplicates.setEnabled(false);
        }else{
        	this.selReplicates.setEnabled(true);
        }
    }
    
    private void populateChoicesForBlocks(){
        
    	 for (VariableType factor : this.factorsInDataset){
         	if (factor.getStandardVariable().getProperty().getName().toString().trim().equalsIgnoreCase(BLOCKING_FACTOR)){
         		this.selBlocks.addItem(factor.getLocalName());
         		this.selBlocks.setValue(factor.getLocalName());
         	}
        }
        
    }
    
    private void populateChoicesForRowFactor(){
        
    	 for (VariableType factor : this.factorsInDataset){
          	if (factor.getStandardVariable().getProperty().getName().toString().trim().equalsIgnoreCase(ROW_FACTOR)){
          		this.selRowFactor.addItem(factor.getLocalName());
          		this.selRowFactor.setValue(factor.getLocalName());
          	}
         }
        
    }
    
    private void populateChoicesForColumnFactor(){
      
    	 for (VariableType factor : this.factorsInDataset){
           	if (factor.getStandardVariable().getProperty().getName().toString().trim().equalsIgnoreCase(COLUMN_FACTOR)){
           		this.selColumnFactor.addItem(factor.getLocalName());
           		this.selColumnFactor.setValue(factor.getLocalName());
           	}
          }
        
    }
    
    private void refineChoicesForBlocksReplicationRowAndColumnFactos(){
        if(this.selReplicates.getValue() != null){
            this.selBlocks.removeItem(this.selReplicates.getValue());
            this.selRowFactor.removeItem(this.selReplicates.getValue());
            this.selColumnFactor.removeItem(this.selReplicates.getValue());
        }
        
        if(this.selBlocks.getValue() != null){
            this.selReplicates.removeItem(this.selBlocks.getValue());
            this.selRowFactor.removeItem(this.selBlocks.getValue());
            this.selColumnFactor.removeItem(this.selBlocks.getValue());
        }
        
        if(this.selRowFactor.getValue() != null){
            this.selReplicates.removeItem(this.selRowFactor.getValue());
            this.selBlocks.removeItem(this.selRowFactor.getValue());
            this.selColumnFactor.removeItem(this.selRowFactor.getValue());
        }
        
        if(this.selColumnFactor.getValue() != null){
            this.selReplicates.removeItem(this.selColumnFactor.getValue());
            this.selBlocks.removeItem(this.selColumnFactor.getValue());
            this.selRowFactor.removeItem(this.selColumnFactor.getValue());
        }
    }
    
    private void checkDesignFactor(){
    	 
    	
    }
    
    protected void initializeLayout() {
    	
        mainLayout.setSizeUndefined();
        mainLayout.setWidth("100%");
        mainLayout.setSpacing(true);
        
        GridLayout selectedInfoLayout = new GridLayout(4, 5);
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
        selectedInfoLayout.addComponent(lblProjectType, 2, 1);
        selectedInfoLayout.addComponent(txtProjectType, 3, 1);
        selectedInfoLayout.addComponent(lblAnalysisNameHeader , 0, 3, 3, 3);
        selectedInfoLayout.addComponent(lblAnalysisName, 0, 4);
        selectedInfoLayout.addComponent(txtAnalysisName, 1, 4, 3, 4);
        
        
        GridLayout chooseEnvironmentLayout = new GridLayout(2, 9);
        chooseEnvironmentLayout.setColumnExpandRatio(0, 4);
        chooseEnvironmentLayout.setColumnExpandRatio(1, 2);
        chooseEnvironmentLayout.setWidth("450");
        chooseEnvironmentLayout.setSpacing(true);
        chooseEnvironmentLayout.setMargin(true, true, true, false);
        chooseEnvironmentLayout.addComponent(lblChooseEnvironmentHeader ,0, 0, 1, 0);
        chooseEnvironmentLayout.addComponent(lblChooseEnvironmentDescription , 0, 1, 1, 1);
        chooseEnvironmentLayout.addComponent(lblSpecifyEnvFactor, 0, 2);
        chooseEnvironmentLayout.addComponent(selEnvFactor, 1, 2);
        chooseEnvironmentLayout.addComponent(lblChooseEnvironmentForAnalysisDescription , 0, 3, 1, 3);
        chooseEnvironmentLayout.addComponent(tblEnvironmentLayout, 0, 4, 1, 4);
        chooseEnvironmentLayout.addComponent(lblVersion, 0, 5);
        chooseEnvironmentLayout.addComponent(txtVersion, 1, 5);
        
        GridLayout designDetailsLayout = new GridLayout(2, 8);
        designDetailsLayout.setColumnExpandRatio(0, 4);
        designDetailsLayout.setColumnExpandRatio(1, 4);
        designDetailsLayout.setWidth("550");
        designDetailsLayout.setSpacing(true);
        designDetailsLayout.setMargin(true, false, true, false);
        designDetailsLayout.addComponent(lblSpecifyDesignDetailsHeader, 0, 0, 1, 0);
        designDetailsLayout.addComponent(lblDesignType, 0, 1);
        designDetailsLayout.addComponent(selDesignType, 1, 1);
        designDetailsLayout.addComponent(getLblReplicates(), 0, 2);
        designDetailsLayout.addComponent(selReplicates, 1, 2);
        designDetailsLayout.addComponent(getLblBlocks(), 0, 3);
        designDetailsLayout.addComponent(selBlocks, 1, 3);
        designDetailsLayout.addComponent(getLblSpecifyRowFactor(), 0, 4);
        designDetailsLayout.addComponent(selRowFactor, 1, 4);
        designDetailsLayout.addComponent(getLblSpecifyColumnFactor(), 0, 5);
        designDetailsLayout.addComponent(selColumnFactor, 1, 5);
        designDetailsLayout.addComponent(lblSpecifyGenotypesHeader, 0, 6, 1, 6);
        designDetailsLayout.addComponent(lblGenotypes, 0, 7);
        designDetailsLayout.addComponent(selGenotypes, 1, 7);
        
        mainLayout.addComponent(lblPageTitle);
        mainLayout.addComponent(new Label(""));
        mainLayout.addComponent(lblTitle);
        mainLayout.addComponent(selectedInfoLayout);
        
        HorizontalLayout combineLayout = new HorizontalLayout();
        combineLayout.addComponent(chooseEnvironmentLayout);
        combineLayout.addComponent(designDetailsLayout);
        mainLayout.addComponent(combineLayout);
       
        HorizontalLayout combineLayout2 = new HorizontalLayout();
        combineLayout2.setSpacing(true);
        combineLayout2.addComponent(btnCancel);
        combineLayout2.addComponent(btnRun);
        combineLayout2.setComponentAlignment(btnCancel, Alignment.TOP_CENTER);
        combineLayout2.setComponentAlignment(btnRun, Alignment.TOP_CENTER);
        mainLayout.addComponent(combineLayout2);
        mainLayout.setComponentAlignment(combineLayout2, Alignment.TOP_CENTER);
        
        mainLayout.setMargin(new MarginInfo(false,true,true,true));
       
        addComponent(mainLayout);
    }
    
    private void reset(){
    
    	selEnvFactor.select(selEnvFactor.getItemIds().iterator().next());
    	selDesignType.select((Object) null);
    	selReplicates.select(selReplicates.getItemIds().iterator().next());
    	selGenotypes.select(selGenotypes.getItemIds().iterator().next());
    	footerCheckBox.setValue(false);
    	txtAnalysisName.setValue(getBreedingViewInput().getBreedingViewAnalysisName());
    
    }

    protected void initializeActions() {
       btnCancel.addListener(new Button.ClickListener() {
			
		private static final long serialVersionUID = 3878612968330447329L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				reset();
				
			}
		});
       
       Button.ClickListener runBreedingView = new Button.ClickListener() {
		
		private static final long serialVersionUID = -6682011023617457906L;

		@Override
		public void buttonClick(final ClickEvent event) {
			
			List<DataSet> dataSets;
			try {
				
				dataSets = studyDataManager.getDataSetsByType(breedingViewInput.getStudyId(), DataSetType.MEANS_DATA);
				if (dataSets.size() > 0){
					
					DataSet meansDataSet = dataSets.get(0);
					TrialEnvironments envs = studyDataManager.getTrialEnvironmentsInDataset(meansDataSet.getId());
					
					Boolean environmentExists = false;
					for (SeaEnvironmentModel model : getSelectedEnvironments()){
						
						TrialEnvironment env = envs.findOnlyOneByLocalName(breedingViewInput.getTrialInstanceName(), model.getTrialno());
						if (env != null){
							environmentExists = true;
							break;
						}
						
					}
					
					if (environmentExists){
						ConfirmDialog.show(event.getComponent().getWindow(), 
								"", 
								"One or more of the selected traits has existing means data. If you save the results of this analysis, the existing values will be overwritten.", 
								"OK", 
								"Cancel", new Runnable(){

									@Override
									public void run() {
										
										new RunBreedingViewAction(SelectDetailsForBreedingViewPanel.this, project).buttonClick(event);
									}
							
									});
					}else{
						new RunBreedingViewAction(SelectDetailsForBreedingViewPanel.this, project).buttonClick(event);
					}
					
				}else{
					new RunBreedingViewAction(SelectDetailsForBreedingViewPanel.this, project).buttonClick(event);
				}
				
			} catch (MiddlewareQueryException e) {
				new RunBreedingViewAction(SelectDetailsForBreedingViewPanel.this, project).buttonClick(event);
				e.printStackTrace();
			} catch (Exception e){
				new RunBreedingViewAction(SelectDetailsForBreedingViewPanel.this, project).buttonClick(event);
				e.printStackTrace();
			}
			
			
		}
       };
       
       btnRun.addListener(runBreedingView);
       
       btnRun.setClickShortcut(KeyCode.ENTER);
       btnRun.addStyleName("primary");
       
       selDesignType.addListener(new BreedingViewDesignTypeValueChangeListener(this));
       selReplicates.addListener(new BreedingViewReplicatesValueChangeListener(this));
       selEnvFactor.addListener(new BreedingViewEnvFactorValueChangeListener(this));
    }

    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    @Override
    public void afterPropertiesSet() {        
        assemble();
        managerFactory.close();
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setValue(lblVersion, Message.BV_VERSION);
        messageSource.setValue(lblProjectType, Message.BV_PROJECT_TYPE);
        messageSource.setValue(lblAnalysisName, Message.BV_ANALYSIS_NAME);
        messageSource.setValue(lblSiteEnvironment, Message.BV_SITE_ENVIRONMENT);
        messageSource.setValue(lblSpecifyEnvFactor, Message.BV_SPECIFY_ENV_FACTOR);
        messageSource.setValue(lblSelectEnvironmentForAnalysis, Message.BV_SELECT_ENV_FOR_ANALYSIS);
        messageSource.setValue(lblSpecifyNameForAnalysisEnv, Message.BV_SPECIFY_NAME_FOR_ANALYSIS_ENV);
        messageSource.setValue(lblDesign, Message.BV_DESIGN);
        messageSource.setValue(lblDesignType, Message.DESIGN_TYPE);
        messageSource.setValue(getLblReplicates(), Message.BV_SPECIFY_REPLICATES);
        messageSource.setValue(getLblBlocks(), Message.BV_SPECIFY_BLOCKS);
        messageSource.setValue(getLblSpecifyRowFactor(), Message.BV_SPECIFY_ROW_FACTOR);
        messageSource.setValue(getLblSpecifyColumnFactor(), Message.BV_SPECIFY_COLUMN_FACTOR);
        messageSource.setValue(lblGenotypes, Message.BV_GENOTYPES);
        messageSource.setCaption(btnRun, Message.RUN_BREEDING_VIEW);
        messageSource.setCaption(btnCancel, Message.RESET);
        
        messageSource.setValue(lblTitle, Message.BV_TITLE);
        messageSource.setValue(lblPageTitle, Message.TITLE_SSA);
        messageSource.setValue(lblDatasetName, Message.BV_DATASET_NAME);
        messageSource.setValue(lblDatasourceName, Message.BV_DATASOURCE_NAME);
        messageSource.setValue(lblDataSelectedForAnalysisHeader, Message.BV_DATA_SELECTED_FOR_ANALYSIS_HEADER);
        messageSource.setValue(lblAnalysisNameHeader, Message.BV_ANALYSIS_NAME_HEADER);
        messageSource.setValue(lblChooseEnvironmentHeader, Message.BV_CHOOSE_ENVIRONMENT_HEADER);
        messageSource.setValue(lblChooseEnvironmentDescription, Message.BV_CHOOSE_ENVIRONMENT_DESCRIPTION);
        messageSource.setValue(lblChooseEnvironmentForAnalysisDescription, Message.BV_CHOOSE_ENVIRONMENT_FOR_ANALYSIS_DESC);
        messageSource.setValue(lblSpecifyDesignDetailsHeader, Message.BV_SPECIFY_DESIGN_DETAILS_HEADER);
        messageSource.setValue(lblSpecifyGenotypesHeader, Message.BV_SPECIFY_GENOTYPES_HEADER);
    }

	public void setBreedingViewInput(BreedingViewInput breedingViewInput) {
		this.breedingViewInput = breedingViewInput;
	}

	public ManagerFactory getManagerFactory() {
		return managerFactory;
	}

	public void setManagerFactory(ManagerFactory managerFactory) {
		this.managerFactory = managerFactory;
	}

	public Label getLblBlocks() {
		return lblBlocks;
	}

	public void setLblBlocks(Label lblBlocks) {
		this.lblBlocks = lblBlocks;
	}

	public Label getLblSpecifyRowFactor() {
		return lblSpecifyRowFactor;
	}

	public void setLblSpecifyRowFactor(Label lblSpecifyRowFactor) {
		this.lblSpecifyRowFactor = lblSpecifyRowFactor;
	}

	public Label getLblSpecifyColumnFactor() {
		return lblSpecifyColumnFactor;
	}

	public void setLblSpecifyColumnFactor(Label lblSpecifyColumnFactor) {
		this.lblSpecifyColumnFactor = lblSpecifyColumnFactor;
	}

	public Label getLblReplicates() {
		return lblReplicates;
	}

	public void setLblReplicates(Label lblReplicates) {
		this.lblReplicates = lblReplicates;
	}

	public HashMap<String, Boolean> getEnvironmentsCheckboxState() {
		return environmentsCheckboxState;
	}

	public void setEnvironmentsCheckboxState(
			HashMap<String, Boolean> environmentsCheckboxState) {
		this.environmentsCheckboxState = environmentsCheckboxState;
	}
	
	
	public List<SeaEnvironmentModel> getSelectedEnvironments(){
		
		List<SeaEnvironmentModel> envs = new ArrayList<SeaEnvironmentModel>();
		for (Iterator<?> itr = tblEnvironmentSelection.getContainerDataSource().getItemIds().iterator(); itr.hasNext();){
			SeaEnvironmentModel m = (SeaEnvironmentModel) itr.next();
			if (m.getActive()) envs.add(m);
		}
		return envs;
	}
    

}


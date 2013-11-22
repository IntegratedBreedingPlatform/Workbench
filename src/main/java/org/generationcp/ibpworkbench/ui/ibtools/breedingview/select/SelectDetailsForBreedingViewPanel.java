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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.BreedingViewDesignTypeValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewEnvFactorValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewEnvNameForAnalysisValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewReplicatesValueChangeListener;
import org.generationcp.ibpworkbench.actions.OpenWorkflowForRoleAction;
import org.generationcp.ibpworkbench.actions.RunBreedingViewAction;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.mysql.jdbc.StringUtils;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

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
    private Select selEnvForAnalysis;
    private Select selReplicates;
    private Select selBlocks;
    private Select selRowFactor;
    private Select selColumnFactor;
    private Select selGenotypes;
    
    private HashMap<String, Boolean> environmentCheckboxStates;
    
    private VerticalLayout tblEnvironmentLayout;
    private Table tblEnvironmentSelection;
    
    private BreedingViewInput breedingViewInput;
    private Tool tool;
    private List<VariableType> factorsInDataset;
    
    private Project project;
    private Role role;
    
    private VerticalLayout mainLayout;
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    private ManagerFactory managerFactory;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public SelectDetailsForBreedingViewPanel(Tool tool, BreedingViewInput breedingViewInput, List<VariableType> factorsInDataset
            ,Project project, Role role) {

        this.tool = tool;
        this.setBreedingViewInput(breedingViewInput);
        this.factorsInDataset = factorsInDataset;
        this.project = project;
        this.role = role;
     
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
    
    public Select getSelEnvForAnalysis() {
        return selEnvForAnalysis;
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
    	
    	environmentCheckboxStates = new HashMap<String, Boolean>();
    	
    	tblEnvironmentLayout = new VerticalLayout();
    	tblEnvironmentLayout.setHeight("200px");
    	tblEnvironmentLayout.setWidth("60%");
    	
    	tblEnvironmentSelection = new Table();
    	tblEnvironmentSelection.setSizeFull();
    	tblEnvironmentSelection.addContainerProperty("SELECT", CheckBox.class, new CheckBox("",true));
    	tblEnvironmentSelection.addContainerProperty("ENVIRONMENT", String.class, "");
    	
		final CheckBox footerCheckBox = new CheckBox("SELECT ALL",true);
		footerCheckBox.addListener(new Property.ValueChangeListener(){

				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					Boolean val = (Boolean) event.getProperty()
							.getValue();
					for (Iterator<?> itr = tblEnvironmentSelection.getItemIds().iterator(); itr.hasNext();){
						CheckBox chkObj = (CheckBox) tblEnvironmentSelection.getContainerProperty(itr.next(), "SELECT").getValue();
						if (chkObj.getData() != null){
							chkObj.setValue(val);
						}
						
					}
					
					
				}
				
			});
		footerCheckBox.setImmediate(true);
		
		HorizontalLayout footerCheckBoxLayout = new HorizontalLayout();
		footerCheckBoxLayout.setSizeUndefined();
		footerCheckBoxLayout.setStyleName("v-table-header-wrap");
		footerCheckBoxLayout.addComponent(footerCheckBox);
		
		tblEnvironmentLayout.addComponent(tblEnvironmentSelection);
		tblEnvironmentLayout.addComponent(footerCheckBoxLayout);
          
        mainLayout = new VerticalLayout();
        
        lblTitle = new Label();
        lblTitle.setContentMode(Label.CONTENT_XHTML);
        lblTitle.setStyleName("gcp-content-header");
        lblTitle.setHeight("20px");
        lblDatasetName = new Label();
        lblDatasetName.setContentMode(Label.CONTENT_XHTML);
        lblDatasourceName = new Label();
        lblDatasourceName.setContentMode(Label.CONTENT_XHTML);
        
        lblVersion = new Label();
        lblProjectType = new Label();
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
        getLblReplicates().setVisible(false);
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
        lblDataSelectedForAnalysisHeader.setStyleName("gcp-table-header-bold");
        lblAnalysisNameHeader = new Label();
        lblAnalysisNameHeader.setStyleName("gcp-table-header-bold");
        lblChooseEnvironmentHeader = new Label();
        lblChooseEnvironmentHeader.setStyleName("gcp-table-header-bold");
        lblChooseEnvironmentDescription = new Label();
        lblChooseEnvironmentForAnalysisDescription = new Label();
        lblChooseEnvironmentForAnalysisDescription.setContentMode(Label.CONTENT_XHTML);
        lblSpecifyDesignDetailsHeader = new Label();
        lblSpecifyDesignDetailsHeader.setStyleName("gcp-table-header-bold");
        lblSpecifyGenotypesHeader = new Label();
        lblSpecifyGenotypesHeader.setStyleName("gcp-table-header-bold");
        
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
        if (!StringUtils.isNullOrEmpty(getBreedingViewInput().getBreedingViewProjectName())) {
            txtAnalysisName.setValue(getBreedingViewInput().getBreedingViewProjectName());
        }
        txtAnalysisName.setRequired(false);
        txtAnalysisName.setWidth("80%");
               
        
        selEnvFactor = new Select();
        selEnvFactor.setImmediate(true); 
        populateChoicesForEnvironmentFactor();
        selEnvFactor.setNullSelectionAllowed(true);
        selEnvFactor.setNewItemsAllowed(false);
        
        selEnvForAnalysis = new Select();
        selEnvForAnalysis.setImmediate(true); 
        populateChoicesForEnvForAnalysis();
        selEnvForAnalysis.setNullSelectionAllowed(true);
        selEnvForAnalysis.setNewItemsAllowed(false);
        
        txtNameForAnalysisEnv = new TextField();
        txtNameForAnalysisEnv.setNullRepresentation("");
        txtNameForAnalysisEnv.setRequired(false);
        
        selDesignType = new Select();
        selDesignType.setImmediate(true); 
        selDesignType.addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.ROW_COLUMN_DESIGN.getName());
        
        checkDesignFactor();
        selDesignType.setNullSelectionAllowed(false);
        selDesignType.setNewItemsAllowed(false);
        
        selReplicates = new Select();
        selReplicates.setImmediate(true); 
        populateChoicesForReplicates();
        selReplicates.setNullSelectionAllowed(true);
        selReplicates.setNewItemsAllowed(false);
        selReplicates.setVisible(false);
        
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
        selGenotypes.setNullSelectionAllowed(false);
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
    	
        
        if (this.selEnvFactor.getItemIds().size() < 1) {
        	this.selEnvFactor.setEnabled(false);
        }else{
        	this.selEnvFactor.setEnabled(true);
        }
        
        if (((String)this.selEnvFactor.getValue()) == "" && this.selEnvForAnalysis != null) {
        	this.selEnvForAnalysis.removeAllItems();
        	this.selEnvForAnalysis.setEnabled(false);
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
    	
    	try{
        	selEnvForAnalysis.removeAllItems();
        	environmentCheckboxStates.clear();
        	tblEnvironmentSelection.removeAllItems();
        }catch(Exception e){}
    	
        String envFactorName = (String) this.selEnvFactor.getValue();   
		
        VariableType factor = getFactorByLocalName(envFactorName);
        
        if (factor != null){
        	
			try {
				
				TrialEnvironments trialEnvironments;	
				trialEnvironments = getManagerFactory().getNewStudyDataManager().getTrialEnvironmentsInDataset(getBreedingViewInput().getDatasetId());
				for (Variable var : trialEnvironments.getVariablesByLocalName(envFactorName)){
					if (var.getValue() != null) {
						selEnvForAnalysis.addItem(var.getValue());
						
						final CheckBox chk = new CheckBox("",true);
						chk.setData(var.getValue());
						chk.setImmediate(true);
						chk.addListener(new Property.ValueChangeListener(){

							private static final long serialVersionUID = 1L;

							@Override
							public void valueChange(ValueChangeEvent event) {
								Boolean val = (Boolean) event.getProperty()
										.getValue();
								getEnvironmentCheckboxStates().put(chk.getData().toString(), val);
								System.out.println(chk.getData().toString() + ":" + val);
							}
							
						}
						);
						
						Object[] cells = new Object[]{ chk, var.getValue() };
						environmentCheckboxStates.put(var.getValue(), true);
						tblEnvironmentSelection.addItem(cells, var.getValue()+"");
					}
					
				}
				
			} catch (ConfigException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
            if (this.selEnvForAnalysis.getItemIds().size() < 1) {
            	this.selEnvForAnalysis.setEnabled(false);
            }else{
            	this.selEnvForAnalysis.setEnabled(true);
            }
            
        } else {
            this.selEnvForAnalysis.removeAllItems();
            this.selEnvForAnalysis.setEnabled(false);
        }
    }
    
    private void populateChoicesForGenotypes(){
        
    	for (VariableType factor : factorsInDataset){
    		if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM){
    			 this.selGenotypes.addItem(factor.getLocalName());
    			 this.selGenotypes.setValue(factor.getLocalName());
    		}
    	}
    	
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
        
    	/**
        if (this.selRowFactor.getItemIds().size() < 1) {
        	this.selRowFactor.setEnabled(false);
        }else{
        	this.selRowFactor.setEnabled(true);
        }**/
    }
    
    private void populateChoicesForColumnFactor(){
      
    	 for (VariableType factor : this.factorsInDataset){
           	if (factor.getStandardVariable().getProperty().getName().toString().trim().equalsIgnoreCase(COLUMN_FACTOR)){
           		this.selColumnFactor.addItem(factor.getLocalName());
           		this.selColumnFactor.setValue(factor.getLocalName());
           	}
          }
        
    	 /**
        if (this.selColumnFactor.getItemIds().size() < 1) {
        	this.selColumnFactor.setEnabled(false);
        }else{
        	this.selColumnFactor.setEnabled(true);
        }**/
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
        mainLayout.setSpacing(false);
        
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
        chooseEnvironmentLayout.addComponent(selEnvForAnalysis, 0, 4, 1, 4);
        chooseEnvironmentLayout.addComponent(tblEnvironmentLayout, 0, 5, 1, 5);
        chooseEnvironmentLayout.addComponent(lblVersion, 0, 6);
        chooseEnvironmentLayout.addComponent(txtVersion, 1, 6);
        
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
        
        mainLayout.setMargin(true);
       
        addComponent(mainLayout);
    }

    protected void initializeActions() {
       btnCancel.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				try {
			       
				
	            String url = String.format("/OpenProjectWorkflowForRole?projectId=%d&roleId=%d", project.getProjectId(), role.getRoleId());
	            (new OpenWorkflowForRoleAction(project)).doAction(event.getComponent().getWindow(), url, true);
				} catch (Exception e) {
					//LOG.error("Exception", e);
		            if(e.getCause() instanceof InternationalizableException) {
		                InternationalizableException i = (InternationalizableException) e.getCause();
		                MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
		            }
		            return;
				}
			}
		});
       
       btnRun.addListener(new RunBreedingViewAction(this));
       
       btnRun.setClickShortcut(KeyCode.ENTER);
       btnRun.addStyleName("primary");
       
       selDesignType.addListener(new BreedingViewDesignTypeValueChangeListener(this));
       selReplicates.addListener(new BreedingViewReplicatesValueChangeListener(this));
       selEnvFactor.addListener(new BreedingViewEnvFactorValueChangeListener(this));
       selEnvForAnalysis.addListener(new BreedingViewEnvNameForAnalysisValueChangeListener(this));
    }

    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    @Override
    public void afterPropertiesSet() {
        setManagerFactory(managerFactoryProvider.getManagerFactoryForProject(this.project));
        
        assemble();
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
        messageSource.setCaption(btnCancel, Message.CANCEL);
        
        messageSource.setValue(lblTitle, Message.BV_TITLE);
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

	public HashMap<String, Boolean> getEnvironmentCheckboxStates() {
		return environmentCheckboxStates;
	}

	public void setEnvironmentCheckboxStates(
			HashMap<String, Boolean> environmentCheckboxStates) {
		this.environmentCheckboxStates = environmentCheckboxStates;
	}
    

}


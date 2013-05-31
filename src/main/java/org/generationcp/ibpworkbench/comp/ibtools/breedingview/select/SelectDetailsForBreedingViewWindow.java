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

package org.generationcp.ibpworkbench.comp.ibtools.breedingview.select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.BreedingViewDesignTypeValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewEnvFactorValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewEnvNameForAnalysisValueChangeListener;
import org.generationcp.ibpworkbench.actions.BreedingViewReplicatesValueChangeListener;
import org.generationcp.ibpworkbench.actions.CancelDetailsAsInputForBreedingViewAction;
import org.generationcp.ibpworkbench.actions.RunBreedingViewAction;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.exceptions.ConfigException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.pojos.CharacterLevel;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.NumericLevel;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.v2.domain.Enumeration;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.FactorType;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.util.Debug;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.mysql.jdbc.StringUtils;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class SelectDetailsForBreedingViewWindow extends Window implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private static final String GERMPLASM_ID = "germplasm id";
    private static final String GERMPLASM_IDENTIFICATION = "germplasm identification";
    private static final String GERMPLASM_ENTRY = "germplasm entry";
    private static final String TRIAL_INSTANCE = "trial instance";
    private static final String FIELD_PLOT = "field plot";
    private static final String REPLICATION = "replication";
    private static final String BLOCK = "block";
    private static final String ROW_IN_LAYOUT = "row in layout";
    private static final String COLUMN_IN_LAYOUT = "column in layout";
    private static final String RCBD_DESIGN = "RCBD";
    private static final String ALPHA_DESIGN = "ALPHA";
    private static final String ROWCOL_DESIGN = "ROWCOL";
    private static final String EXPERIMENTAL_DESIGN = "experimental design";
    private static final String STUDY_TRAIT_NAME = "study";
    
    
    private static final String REPLICATION_FACTOR = "replication factor";
    private static final String BLOCKING_FACTOR = "blocking factor";
    private static final String ROW_FACTOR = "row in layout";
    private static final String COLUMN_FACTOR = "column in layout";
    
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
    private Button btnRun;
    private Button btnCancel;
    private TextField txtVersion;
    private TextField txtProjectType;
    private TextField txtAnalysisName;
    private TextField txtNameForAnalysisEnv;
    private Select selDesignType;
    private Select selEnvFactor;
    private Select selEnvForAnalysis;
    private Select selReplicates;
    private Select selBlocks;
    private Select selRowFactor;
    private Select selColumnFactor;
    private Select selGenotypes;
    
    private BreedingViewInput breedingViewInput;
    private Tool tool;
    private List<VariableType> factorsInDataset;
    //for mapping factor ids with the key being the trait name of the factors identified by the ids
    private Map<String, Integer> factorIdsMap;
    //for mapping label ids with the key being the trait name of the factors identified by the ids
    private Map<String, Integer> labelIdsMap;
    
    private Project project;
    
    private AbsoluteLayout mainLayout;
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    private ManagerFactory managerFactory;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public SelectDetailsForBreedingViewWindow(Tool tool, BreedingViewInput breedingViewInput, List<VariableType> factorsInDataset
            ,Project project) {

        this.tool = tool;
        this.breedingViewInput = breedingViewInput;
        this.factorsInDataset = factorsInDataset;
        this.project = project;
        
        setModal(true);
        
       /* Make the sub window 50% the size of the browser window */
        setWidth("60%");
        setHeight("70%");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        
        setScrollable(true);

        setCaption("Breeding View Analysis Specifications: ");
        
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
        
        mainLayout = new AbsoluteLayout();
        
        lblVersion = new Label();
        lblProjectType = new Label();
        lblAnalysisName = new Label();
        lblAnalysisName.setContentMode(Label.CONTENT_XHTML);
        lblSiteEnvironment = new Label();
        lblSpecifyEnvFactor = new Label();
        lblSelectEnvironmentForAnalysis = new Label();
        lblSelectEnvironmentForAnalysis.setContentMode(Label.CONTENT_XHTML);
        lblSpecifyNameForAnalysisEnv = new Label();
        lblSpecifyNameForAnalysisEnv.setContentMode(Label.CONTENT_XHTML);
        lblDesign = new Label();
        lblDesignType = new Label();
        lblDesignType.setContentMode(Label.CONTENT_XHTML);
        lblReplicates = new Label();
        lblBlocks = new Label();
        lblBlocks.setContentMode(Label.CONTENT_XHTML);
        lblSpecifyRowFactor = new Label();
        lblSpecifyRowFactor.setContentMode(Label.CONTENT_XHTML);
        lblSpecifyColumnFactor = new Label();
        lblSpecifyColumnFactor.setContentMode(Label.CONTENT_XHTML);
        lblGenotypes = new Label();
        
        txtVersion = new TextField();
        txtVersion.setNullRepresentation("");
        
        if (!StringUtils.isNullOrEmpty(breedingViewInput.getVersion())) {
            
            txtVersion.setValue(breedingViewInput.getVersion());
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
        
        txtAnalysisName = new TextField();
        txtAnalysisName.setNullRepresentation("");
        if (!StringUtils.isNullOrEmpty(breedingViewInput.getBreedingViewProjectName())) {
            txtAnalysisName.setValue(breedingViewInput.getBreedingViewProjectName());
        }
        txtAnalysisName.setRequired(false);
        txtAnalysisName.setWidth("95%");
        
        factorIdsMap = new HashMap<String, Integer>();
        labelIdsMap = new HashMap<String, Integer>();
        
        
        selEnvFactor = new Select();
        selEnvFactor.setImmediate(true); 
        populateChoicesForEnvironmentFactor();
        selEnvFactor.setNullSelectionAllowed(true);
        selEnvFactor.setNewItemsAllowed(false);
        
        selEnvForAnalysis = new Select();
        selEnvForAnalysis.setImmediate(true); 
        populateChoicesForEnvForAnalysis();
        selEnvForAnalysis.setNullSelectionAllowed(false);
        selEnvForAnalysis.setNewItemsAllowed(false);
        
        txtNameForAnalysisEnv = new TextField();
        txtNameForAnalysisEnv.setNullRepresentation("");
        txtNameForAnalysisEnv.setRequired(false);
        
        selDesignType = new Select();
        selDesignType.setImmediate(true); 
        selDesignType.addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
        selDesignType.addItem(DesignType.ROW_COLUMN_DESIGN.getName());
        selDesignType.addItem(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
        checkDesignFactor();
        selDesignType.setNullSelectionAllowed(false);
        selDesignType.setNewItemsAllowed(false);
        
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
        
        selRowFactor = new Select();
        selRowFactor.setImmediate(true); 
        populateChoicesForRowFactor();
        selRowFactor.setNullSelectionAllowed(false);
        selRowFactor.setNewItemsAllowed(false);
        
        selColumnFactor = new Select();
        selColumnFactor.setImmediate(true); 
        populateChoicesForColumnFactor();
        selColumnFactor.setNullSelectionAllowed(false);
        selColumnFactor.setNewItemsAllowed(false);
        
        refineChoicesForBlocksReplicationRowAndColumnFactos();
        
        selGenotypes = new Select();
        selGenotypes.setImmediate(true); 
        populateChoicesForGenotypes();
        selGenotypes.setNullSelectionAllowed(false);
        selGenotypes.setNewItemsAllowed(false);
        
        btnRun = new Button();
        btnCancel = new Button();
    }

    
    private void populateChoicesForEnvironmentFactor(){
    	
    	if (this.factorsInDataset == null) return;
    	
    	for (VariableType factor : factorsInDataset){
    		if (factor.getStandardVariable().getFactorType() == FactorType.TRIAL_ENVIRONMENT){
    			 this.selEnvFactor.addItem(factor.getLocalName());
    			 this.selEnvFactor.setValue(factor.getLocalName());
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
        	this.selEnvForAnalysis.removeAllItems();
        }catch(Exception e){}
    	
        String envFactorName = (String) this.selEnvFactor.getValue();
        
		
        
        VariableType factor = getFactorByLocalName(envFactorName);
        
        if (factor != null){
        	
        	List<Experiment> exps;
			try {
				exps = managerFactory.getNewStudyDataManager().getExperiments(breedingViewInput.getDatasetId(), 0, Integer.MAX_VALUE);
				for (Experiment exp : exps){
	    			String locationVal = exp.getFactors().findByLocalName(envFactorName).getValue();
	    			if (selEnvForAnalysis.containsId(locationVal)) continue;
	    			selEnvForAnalysis.addItem(locationVal);
	    		}
			} catch (ConfigException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
        	
        	/**if (factor.getStandardVariable().hasEnumerations()){
        		for (Enumeration e: factor.getStandardVariable().getEnumerations()){
        			selEnvForAnalysis.addItem(e.getName());
        			selEnvForAnalysis.setValue(e.getName());
        		}
        		
        	}
        	
        	//for testing
        	selEnvForAnalysis.addItem("AAA");
        	selEnvForAnalysis.setValue("AAA");
            **/
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
    		if (factor.getStandardVariable().getFactorType() == FactorType.GERMPLASM){
    			 this.selGenotypes.addItem(factor.getLocalName());
    			 this.selGenotypes.setValue(factor.getLocalName());
    		}
    	}
    	
    	
    	/**
    	Integer germplasmEntryFactorId = null;
        //try getting factor with trait germplasm entry
        if(this.labelIdsMap.get(GERMPLASM_ENTRY) != null){
            Factor germplasmEntryFactor = getFactorByLabelId(this.labelIdsMap.get(GERMPLASM_ENTRY));
            this.selGenotypes.addItem(germplasmEntryFactor.getName());
            this.selGenotypes.setValue(germplasmEntryFactor.getName());
            germplasmEntryFactorId = germplasmEntryFactor.getFactorId();
        } else if(this.labelIdsMap.get(GERMPLASM_ID) != null){
            //next try getting factor with trait germplasm id
            Factor germplasmEntryFactor = getFactorByLabelId(this.labelIdsMap.get(GERMPLASM_ID));
            this.selGenotypes.addItem(germplasmEntryFactor.getName());
            this.selGenotypes.setValue(germplasmEntryFactor.getName());
            germplasmEntryFactorId = germplasmEntryFactor.getFactorId();
        } else if(this.labelIdsMap.get(GERMPLASM_IDENTIFICATION) != null){
            //next try getting factor with the trait germplasm identification
            Factor germplasmEntryFactor = getFactorByLabelId(this.labelIdsMap.get(GERMPLASM_IDENTIFICATION));
            this.selGenotypes.addItem(germplasmEntryFactor.getName());
            this.selGenotypes.setValue(germplasmEntryFactor.getName());
            germplasmEntryFactorId = germplasmEntryFactor.getFactorId();
        }
        
        //and then add all factors which are labels of germplasm entry factor
        if(germplasmEntryFactorId != null){
            for(Factor factor : this.factorsInDataset){
                if(germplasmEntryFactorId.equals(factor.getFactorId())){
                    this.selGenotypes.addItem(factor.getName());
                } 
            }
        } else {
            //if there is no germplasm entry factor add any factor which is a possible choice
            for(Factor factor : this.factorsInDataset){
                if(!factor.getFactorId().equals(this.factorIdsMap.get(BLOCK))
                    && !factor.getFactorId().equals(this.factorIdsMap.get(REPLICATION))
                    && !factor.getFactorId().equals(this.factorIdsMap.get(ROW_IN_LAYOUT))
                    && !factor.getFactorId().equals(this.factorIdsMap.get(COLUMN_IN_LAYOUT))
                    && !factor.getFactorId().equals(this.factorIdsMap.get(EXPERIMENTAL_DESIGN))
                    && !factor.getFactorId().equals(this.factorIdsMap.get(TRIAL_INSTANCE))
                    && !factor.getFactorId().equals(this.factorIdsMap.get(STUDY_TRAIT_NAME))
                    && !factor.getFactorId().equals(this.factorIdsMap.get(FIELD_PLOT))){
                    this.selGenotypes.addItem(factor.getName());
                }
            }
        }**/
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
        
        if (this.selRowFactor.getItemIds().size() < 1) {
        	this.selRowFactor.setEnabled(false);
        }else{
        	this.selRowFactor.setEnabled(true);
        }
    }
    
    private void populateChoicesForColumnFactor(){
      
    	 for (VariableType factor : this.factorsInDataset){
           	if (factor.getStandardVariable().getProperty().getName().toString().trim().equalsIgnoreCase(COLUMN_FACTOR)){
           		this.selColumnFactor.addItem(factor.getLocalName());
           		this.selColumnFactor.setValue(factor.getLocalName());
           	}
          }
        
        if (this.selColumnFactor.getItemIds().size() < 1) {
        	this.selColumnFactor.setEnabled(false);
        }else{
        	this.selColumnFactor.setEnabled(true);
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
        /**
    	//try getting a factor with trait = experimental design
        try{
            if(this.labelIdsMap.get(EXPERIMENTAL_DESIGN) != null){
                Factor designFactor = getFactorByLabelId(this.labelIdsMap.get(EXPERIMENTAL_DESIGN));
                
                List<CharacterLevel> levelsForDesign = managerFactory.getStudyDataManager().getCharacterLevelsByFactorAndDatasetId(designFactor
                        , this.breedingViewInput.getDatasetId());
                
                if(!levelsForDesign.isEmpty()){
                    CharacterLevel level = levelsForDesign.get(0);
                    
                    if(level.getValue().equals(RCBD_DESIGN)){
                        this.selDesignType.setValue(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
                    } else if(level.getValue().equals(ALPHA_DESIGN)){
                        this.selDesignType.setValue(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
                    } else if(level.getValue().equals(ROWCOL_DESIGN)){
                        this.selDesignType.setValue(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
                    }
                }
            }
        } catch(MiddlewareQueryException ex){
            //do nothing for now
        }**/
    }
    
    protected void initializeLayout() {
        mainLayout.setWidth("575");
        mainLayout.setHeight("500");
        
        mainLayout.addComponent(lblVersion, "left: 35px; top: 30px;");
        mainLayout.addComponent(txtVersion, "left: 135px; top: 30px;");
        mainLayout.addComponent(lblProjectType, "left: 35px; top: 60px;");
        mainLayout.addComponent(txtProjectType, "left: 135px; top: 60px;");
        mainLayout.addComponent(lblAnalysisName, "left: 35px; top: 90px;");
        mainLayout.addComponent(txtAnalysisName, "left: 135px; top: 90px;");
        mainLayout.addComponent(lblSiteEnvironment, "left: 35px; top: 120px;");
        mainLayout.addComponent(lblSpecifyEnvFactor, "left: 85px; top: 150px;");
        mainLayout.addComponent(selEnvFactor, "left: 305px; top: 150px;");
        mainLayout.addComponent(lblSelectEnvironmentForAnalysis, "left: 85px; top: 180px;");
        mainLayout.addComponent(selEnvForAnalysis, "left: 305px; top: 180px;");
        //mainLayout.addComponent(lblSpecifyNameForAnalysisEnv, "left: 85px; top: 210px;");
        //mainLayout.addComponent(txtNameForAnalysisEnv, "left: 305px; top: 210px;");
        mainLayout.addComponent(lblDesign, "left: 35px; top: 210px;");
        mainLayout.addComponent(lblDesignType, "left: 85px; top: 240px;");
        mainLayout.addComponent(selDesignType, "left: 305px; top: 240px;");
        mainLayout.addComponent(lblReplicates, "left: 85px; top: 270px;");
        mainLayout.addComponent(selReplicates, "left: 305px; top: 270px;");
        mainLayout.addComponent(lblBlocks, "left: 85px; top: 300px;");
        mainLayout.addComponent(selBlocks, "left: 305px; top: 300px;");
        mainLayout.addComponent(lblSpecifyRowFactor, "left: 85px; top: 330px;");
        mainLayout.addComponent(selRowFactor, "left: 305px; top: 330px;");
        mainLayout.addComponent(lblSpecifyColumnFactor, "left: 85px; top: 360px;");
        mainLayout.addComponent(selColumnFactor, "left: 305px; top: 360px;");
        mainLayout.addComponent(lblGenotypes, "left: 35px; top: 390px;");
        mainLayout.addComponent(selGenotypes, "left: 135px; top: 390px;");
        mainLayout.addComponent(btnCancel, "left: 35px; top: 430px;");
        mainLayout.addComponent(btnRun, "left: 105px; top: 430px;");
        
        mainLayout.setMargin(true);
        
       
        
        setContent(mainLayout);
    }

    protected void initializeActions() {
       btnCancel.addListener(new CancelDetailsAsInputForBreedingViewAction(this));
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
        managerFactory = managerFactoryProvider.getManagerFactoryForProject(this.project);
        
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
        messageSource.setValue(lblReplicates, Message.BV_SPECIFY_REPLICATES);
        messageSource.setValue(lblBlocks, Message.BV_SPECIFY_BLOCKS);
        messageSource.setValue(lblSpecifyRowFactor, Message.BV_SPECIFY_ROW_FACTOR);
        messageSource.setValue(lblSpecifyColumnFactor, Message.BV_SPECIFY_COLUMN_FACTOR);
        messageSource.setValue(lblGenotypes, Message.BV_GENOTYPES);
        messageSource.setCaption(btnRun, Message.RUN_BREEDING_VIEW);
        messageSource.setCaption(btnCancel, Message.CANCEL);
    }
    
  

}

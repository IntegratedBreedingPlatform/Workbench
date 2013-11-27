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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
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
import org.generationcp.middleware.pojos.workbench.Project;
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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Aldrin Batac
 *
 */
@Configurable
public class SelectEnvironmentForGxePanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private Label lblSelectDataForAnalysisHeader;
    private Label lblSelectDataForAnalysisDescription;
    private Label lblEnvironmentFactorHeader;
    private Label lblEnvironmentFactorDescription;
    private Label lblEnvironmentGroupsHeader;
    private Label lblEnvironmentGroupsDescription;
    private Label lblEnvironmentGroupsSpecify;
    private Label lblReviewSelectedDataset;
    private Label lblFactorTableHeader;
    private Label lblFactorTableDescription;
    private Label lblVariateTableHeader;
    private Label lblVariateTableDescription;
    
    private Label lblStudyTreeDetailTitle;
    
    private VerticalLayout generalLayout;
    
    private HorizontalLayout specifyEnvironmentFactorLayout;
    private HorizontalLayout specifyEnvironmentGroupsLayout;
    
    private VerticalLayout datasetVariablesDetailLayout;
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;

    private Button btnCancel;
    private Button btnNext;
    private Component buttonArea;
    private Select selectSpecifyEnvironment;
    private Select selectSpecifyEnvironmentGroups;
    
    private Map<String, Boolean> variatesCheckboxState;

    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private GxeAnalysisComponentPanel gxeAnalysisComponentPanel;
    
	private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;
    
    private List<String> environmentNames = new ArrayList<String>();
    private TrialEnvironments trialEnvironments = null;

    public SelectEnvironmentForGxePanel(StudyDataManager studyDataManager,Project currentProject, Study study, GxeAnalysisComponentPanel gxeAnalysisComponentPanel) {
    	this.studyDataManager = studyDataManager;
        this.currentProject = currentProject;
        this.currentStudy = study;
        this.gxeAnalysisComponentPanel = gxeAnalysisComponentPanel;
        
        setWidth("100%");
        
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

    protected void initializeComponents() {
    	
    	setVariatesCheckboxState(new HashMap<String, Boolean>());
    	
    	lblSelectDataForAnalysisHeader = new Label();
    	lblSelectDataForAnalysisHeader.setStyleName("gcp-content-header");
    	lblSelectDataForAnalysisDescription = new Label();
    	lblEnvironmentFactorHeader = new Label();
    	lblEnvironmentFactorHeader.setStyleName("gcp-content-header");
    	lblEnvironmentFactorDescription = new Label();
    	lblEnvironmentGroupsHeader = new Label();
    	lblEnvironmentGroupsHeader.setStyleName("gcp-content-header");
    	lblEnvironmentGroupsDescription = new Label();
    	lblEnvironmentGroupsSpecify = new Label();
    	lblReviewSelectedDataset = new Label();
    	lblReviewSelectedDataset.setStyleName("gcp-content-header");
    	lblFactorTableHeader = new Label();
    	lblFactorTableHeader.setStyleName("gcp-table-header-bold");
    	lblFactorTableDescription = new Label();
    	lblVariateTableHeader = new Label();
    	lblVariateTableHeader.setStyleName("gcp-table-header-bold");
    	lblVariateTableDescription = new Label();
        
        generalLayout = new VerticalLayout();
        generalLayout.setSpacing(true);
        generalLayout.setMargin(true);
        
        specifyEnvironmentFactorLayout = new HorizontalLayout();
        specifyEnvironmentFactorLayout.setSpacing(true);
        specifyEnvironmentGroupsLayout = new HorizontalLayout();
        specifyEnvironmentGroupsLayout.setSpacing(true);
        datasetVariablesDetailLayout = new VerticalLayout();
        

        lblStudyTreeDetailTitle = new Label();
        lblStudyTreeDetailTitle.setStyleName("gcp-content-title");

        final Table factors = initializeFactorsTable();
        factors.setImmediate(true);
        final Table variates = initializeVariatesTable();
        variates.setImmediate(true);
        
        selectSpecifyEnvironment = new Select();
        selectSpecifyEnvironment.setSizeFull();
        selectSpecifyEnvironment.setImmediate(true);
        selectSpecifyEnvironment.addListener(new Property.ValueChangeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				
				try{
					factors.removeAllItems();
					variates.removeAllItems();
					environmentNames.clear();
				}catch(Exception e){}
					
				 try {
						trialEnvironments = getStudyDataManager().getTrialEnvironmentsInDataset(getCurrentDataSetId());
						for (Variable var : trialEnvironments.getVariablesByLocalName(selectSpecifyEnvironment.getValue().toString())){
							if (var.getValue() != null && var.getValue() != "") environmentNames.add(var.getValue());			
						}
			        } catch (MiddlewareQueryException e) {
						
						e.printStackTrace();
					}
			
				populateFactorsVariatesByDataSetId(currentStudy, factors, variates);
				
			}
		});
        
        selectSpecifyEnvironmentGroups = new Select();
        selectSpecifyEnvironmentGroups.setSizeFull();
        
        populateFactorsVariatesByDataSetId(currentStudy, factors, variates);
        
        for (Iterator<?> i = selectSpecifyEnvironment.getItemIds().iterator(); i.hasNext();){
        	selectSpecifyEnvironment.select(i.next());
        	//break;
        }
        
        buttonArea = layoutButtonArea();
        
        generalLayout.addComponent(lblSelectDataForAnalysisHeader);
        generalLayout.addComponent(lblSelectDataForAnalysisDescription);
        
        generalLayout.addComponent(lblEnvironmentFactorHeader);
        	specifyEnvironmentFactorLayout.addComponent(lblEnvironmentFactorDescription);
        	specifyEnvironmentFactorLayout.addComponent(selectSpecifyEnvironment);
        generalLayout.addComponent(specifyEnvironmentFactorLayout);
        
        generalLayout.addComponent(lblEnvironmentGroupsHeader);
        generalLayout.addComponent(lblEnvironmentGroupsDescription);
        	specifyEnvironmentGroupsLayout.addComponent(lblEnvironmentGroupsSpecify);
        	specifyEnvironmentGroupsLayout.addComponent(selectSpecifyEnvironmentGroups);
        generalLayout.addComponent(specifyEnvironmentGroupsLayout);	
        	
        generalLayout.addComponent(lblReviewSelectedDataset);
        generalLayout.addComponent(lblFactorTableHeader);
        generalLayout.addComponent(lblFactorTableDescription);
        generalLayout.addComponent(factors);
        generalLayout.addComponent(lblVariateTableHeader);
        generalLayout.addComponent(lblVariateTableDescription);
        generalLayout.addComponent(variates);
        
        generalLayout.addComponent(datasetVariablesDetailLayout);
        generalLayout.addComponent(buttonArea);
        
        environmentNames.clear();
        try {
			trialEnvironments = getStudyDataManager().getTrialEnvironmentsInDataset(getCurrentDataSetId());
			for (Variable var : trialEnvironments.getVariablesByLocalName(selectSpecifyEnvironment.getValue().toString())){
				if (var.getValue() != null && var.getValue() != "") environmentNames.add(var.getValue());			
			}
        } catch (MiddlewareQueryException e) {
			
			e.printStackTrace();
		}
        
        addComponent(generalLayout);
        
    }
    
    protected void initializeLayout() {
        
        generalLayout.setComponentAlignment(buttonArea, Alignment.TOP_LEFT);
        
    }
    
    protected void initialize() {
    }

    protected void initializeActions() {
        btnCancel.addListener(new Button.ClickListener() {
	
			private static final long serialVersionUID = 4719456133687409089L;

			@Override
			public void buttonClick(ClickEvent event) {
				//SelectEnvironmentForGxePanel.this.getParent().removeWindow(SelectEnvironmentForGxePanel.this);
			}
		});
        btnNext.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8377610125826448065L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (selectSpecifyEnvironment.getValue().toString() != "" && selectSpecifyEnvironment.getValue() != null ){
					gxeAnalysisComponentPanel.generateTabContent(currentStudy, selectSpecifyEnvironment.getValue().toString());
				}
				
			}
		});

    }
    
    protected void initializeSelectEnvironment() {
        
      
    }

    protected Table initializeFactorsTable() {
        
        Table tblFactors = new Table();
        tblFactors.setImmediate(true);
        tblFactors.setWidth("100%");
        tblFactors.setHeight("100%");
        
        BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
        container.setBeanIdProperty("id");
        tblFactors.setContainerDataSource(container);
        
        String[] columns = new String[] {"name", "description"};
        String[] columnHeaders = new String[] {"Name", "Description"};
        tblFactors.setVisibleColumns(columns);
        tblFactors.setColumnHeaders(columnHeaders);
        return tblFactors;
    }
    
    protected Table initializeVariatesTable() {
        
        final Table tblVariates = new Table();
        tblVariates.setImmediate(true);
        tblVariates.setWidth("100%");
        tblVariates.setHeight("100%");
        tblVariates.setColumnExpandRatio("", 0.5f);
        tblVariates.setColumnExpandRatio("name", 1);
        tblVariates.setColumnExpandRatio("description", 4);
        tblVariates.setColumnExpandRatio("testedin", 1);
        
        tblVariates.addGeneratedColumn("", new Table.ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) tblVariates.getContainerDataSource();
				final VariateModel vm = container.getItem(itemId).getBean();
				
				final CheckBox checkBox = new CheckBox();
				checkBox.setImmediate(true);
				checkBox.setVisible(true);
				checkBox.addListener(new Property.ValueChangeListener() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(final ValueChangeEvent event) {
						Boolean val = (Boolean) event.getProperty()
								.getValue();
						getVariatesCheckboxState().put(vm.getName(), val);
						vm.setActive(val);
					
					}
				});

				if (vm.getActive()) {
					checkBox.setValue(true);
				} else {
					checkBox.setValue(false);
				}
				
				return checkBox;
				
			}
        	
        });
		
        
        tblVariates.addGeneratedColumn("testedin", new Table.ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				
					BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) source.getContainerDataSource();
					VariateModel vm = container.getItem(itemId).getBean();
				
					return getTestedIn(selectSpecifyEnvironment.getValue().toString(), environmentNames, vm.getVariableId(), getCurrentDataSetId(), trialEnvironments);
					
			}});
        
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        tblVariates.setContainerDataSource(container);
        
        String[] columns = new String[] {"","displayName", "description","testedin"};
        String[] columnHeaders = new String[] {"","Name", "Description","Tested In"};
        tblVariates.setVisibleColumns(columns);
        tblVariates.setColumnHeaders(columnHeaders);
        return tblVariates;
    }
   
    
    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        
        buttonLayout.setSizeFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

        btnCancel = new Button();
        btnNext = new Button();
        btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
        spacer.setSizeFull();
        
        buttonLayout.addComponent(spacer);
        buttonLayout.setExpandRatio(spacer,1.0F);
        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnNext);

        return buttonLayout;
    }
    
    public void populateFactorsVariatesByDataSetId(Study study, Table factors, Table variates) {

        try {
            
        	
            DataSet ds = studyDataManager.getDataSetsByType(study.getId(), DataSetType.MEANS_DATA).get(0);
            if (ds==null) return;
            
            List<FactorModel> factorList = new ArrayList<FactorModel>();
            List<VariateModel> variateList = new ArrayList<VariateModel>();
            
            for (VariableType factor : ds.getVariableTypes().getFactors().getVariableTypes()){
            	
            	FactorModel fm = new FactorModel();
            	fm.setId(factor.getRank());
            	fm.setName(factor.getLocalName());
            	fm.setScname(factor.getStandardVariable().getScale().getName());
            	fm.setScaleid(factor.getStandardVariable().getScale().getId());
            	fm.setTmname(factor.getStandardVariable().getMethod().getName());
            	fm.setTmethid(factor.getStandardVariable().getMethod().getId());
            	fm.setTrname(factor.getStandardVariable().getName());
            	//fm.setTrname(factor.getStandardVariable().getProperty().getName());
            	fm.setDescription(factor.getLocalDescription());
            	fm.setTraitid(factor.getStandardVariable().getProperty().getId());
            	
            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM){
            		factorList.add(fm);
            	}
            	
            	
            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT){
            		// only TRIAL_ENVIRONMENT_INFO_STORAGE(1020) TRIAL_INSTANCE_STORAGE(1021) factors in selectEnv dropdown
            		if (factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()
            			|| factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()	)
	            		selectSpecifyEnvironment.addItem(factor.getLocalName());
            			selectSpecifyEnvironmentGroups.addItem(factor.getLocalName());
            	}
            }
            
            for (VariableType variate : ds.getVariableTypes().getVariates().getVariableTypes()){
            	
            	VariateModel vm = new VariateModel();
            	vm.setId(variate.getRank());
            	vm.setVariableId(variate.getId());
            	vm.setName(variate.getLocalName());
            	vm.setDisplayName(variate.getLocalName().replace("_Means", ""));
            	vm.setScname(variate.getStandardVariable().getScale().getName());
            	vm.setScaleid(variate.getStandardVariable().getScale().getId());
            	vm.setTmname(variate.getStandardVariable().getMethod().getName());
            	vm.setTmethid(variate.getStandardVariable().getMethod().getId());
            	vm.setTrname(variate.getStandardVariable().getName());
            	vm.setTraitid(variate.getStandardVariable().getProperty().getId());
            	vm.setDescription(variate.getLocalDescription());
            	if (!variate.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate")){
            		vm.setActive(true);
            		variateList.add(vm);
            	}
            	
            }
            
           
            this.setCurrentDatasetName(ds.getName());
            this.setCurrentDataSetId(ds.getId());
            
            updateFactorsTable(factorList, factors);
            updateVariatesTable(variateList, factors, variates);

        }
        catch (MiddlewareQueryException e) {
            
        }
    }
    
    private void updateFactorsTable(List<FactorModel> factorList, Table factors){
    	   Object[] oldColumns = factors.getVisibleColumns();
           String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
           
           BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
           container.setBeanIdProperty("id");
           factors.setContainerDataSource(container);
           
           for (FactorModel f : factorList ){
        	   container.addBean(f);
           }
           
           factors.setContainerDataSource(container);
           
           factors.setVisibleColumns(columns);
    }
    
    
    private void updateVariatesTable(List<VariateModel> variateList,Table factors, Table variates){
 	   
        
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        variates.setContainerDataSource(container);
        
        for (VariateModel v : variateList ){
     	   container.addBean(v);
        }
        
        variates.setContainerDataSource(container);
        
        variates.setVisibleColumns(new String[]{ "", "displayName", "description","testedin"});
        variates.setColumnHeaders(new String[]{ "", "Name", "Description", "Tested In"});
        
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
        messageSource.setCaption(btnNext, Message.NEXT);
        messageSource.setValue(lblSelectDataForAnalysisHeader, Message.GXE_SELECT_DATA_FOR_ANALYSIS_HEADER);
        messageSource.setValue(lblSelectDataForAnalysisDescription, Message.GXE_SELECT_DATA_FOR_ANALYSIS_DESCRIPTION);
        messageSource.setValue(lblEnvironmentFactorHeader, Message.GXE_ENVIRONMENT_FACTOR_HEADER);
        messageSource.setValue(lblEnvironmentFactorDescription, Message.GXE_ENVIRONMENT_FACTOR_DESCRIPTION);
        messageSource.setValue(lblEnvironmentGroupsHeader, Message.GXE_ENVIRONMENT_GROUPS_HEADER);
        messageSource.setValue(lblEnvironmentGroupsDescription, Message.GXE_ENVIRONMENT_GROUPS_DESCRIPTION);
        messageSource.setValue(lblEnvironmentGroupsSpecify, Message.GXE_ENVIRONMENT_GROUPS_SPECIFY);
        messageSource.setValue(lblReviewSelectedDataset, Message.GXE_REVIEW_SELECTED_DATASET);
        messageSource.setValue(lblFactorTableHeader, Message.GXE_FACTOR_TABLE_HEADER);
        messageSource.setValue(lblFactorTableDescription, Message.GXE_FACTOR_TABLE_DESCRIPTION);
        messageSource.setValue(lblVariateTableHeader, Message.GXE_VARIATE_TABLE_HEADER);
        messageSource.setValue(lblVariateTableDescription, Message.GXE_VARIATE_TABLE_DESCRIPTION);
    }

    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
		return this.studyDataManager;
	}

	public Map<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}

	public void setVariatesCheckboxState(Map<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}
	
	private String getTestedIn(String envFactorName, List<String> environmentNames , Integer variableId , Integer meansDataSetId ,TrialEnvironments trialEnvironments){
		
		
		
		
		int counter = 0;
		
		try {
			for (String environmentName : environmentNames){
				long count = studyDataManager.countStocks(
						meansDataSetId
					,trialEnvironments.findOnlyOneByLocalName(envFactorName, environmentName).getId()
					,variableId
						);
				if (count > 0) counter++;
			
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			return String.format("%s of %s", counter, environmentNames.size());
		}catch (Exception e){
			return "";
		}
		
		
		 
		
	}


}

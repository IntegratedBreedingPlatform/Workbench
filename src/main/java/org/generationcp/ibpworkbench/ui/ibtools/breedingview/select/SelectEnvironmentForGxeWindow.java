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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.ui.gxe.GxeAnalysisComponentPanel;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
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

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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
public class SelectEnvironmentForGxeWindow extends Window implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private Label lblStudyTreeDetailTitle;
    
    private VerticalLayout generalLayout;
    
    private GridLayout studyDetailsLayout;
    
    private VerticalLayout studyDatasetDetailLayout;
    
    private HorizontalLayout datasetVariablesDetailLayout;
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;

    private Button btnCancel;
    private Button btnNext;
    private Component buttonArea;
    private Select selectEnv;

    private Database database;

    private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    private GxeAnalysisComponentPanel gxeAnalysisComponentPanel;
    
	private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;


    public SelectEnvironmentForGxeWindow(StudyDataManager studyDataManager,Project currentProject, Study study, GxeAnalysisComponentPanel gxeAnalysisComponentPanel) {
    	this.studyDataManager = studyDataManager;
        this.currentProject = currentProject;
        this.currentStudy = study;
        this.gxeAnalysisComponentPanel = gxeAnalysisComponentPanel;

        setModal(true);

       /* Make the sub window 50% the size of the browser window */
        setWidth("80%");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        
        setCaption("Select Environment to generate Multi-site Analysis: ");
        
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
        
        generalLayout = new VerticalLayout();
        
        studyDetailsLayout = new GridLayout(10, 1);
        
        studyDatasetDetailLayout = new VerticalLayout();
        
        datasetVariablesDetailLayout = new HorizontalLayout();
        
        //selectDatasetTitle = new Label("Select Dataset For Breeding View");
        //selectDatasetTitle.setStyleName("gcp-content-title");
        
        //studyTreeLayout.addComponent(selectDatasetTitle);
        lblStudyTreeDetailTitle = new Label();
        lblStudyTreeDetailTitle.setStyleName("gcp-content-title");

        Table factors = initializeFactorsTable();
        
        Table variates = initializeVariatesTable();
        
        selectEnv = new Select("Please select the Environment Factor:");
        selectEnv.setSizeFull();
        
        populateFactorsVariatesByDataSetId(currentStudy, factors, variates);
        
        for (Iterator<?> i = selectEnv.getItemIds().iterator(); i.hasNext();){
        	selectEnv.select(i.next());
        	break;
        }
        
        buttonArea = layoutButtonArea();
        
        
        studyDatasetDetailLayout.addComponent(selectEnv);
        
        datasetVariablesDetailLayout.addComponent(factors);
        datasetVariablesDetailLayout.addComponent(variates);
        
        studyDetailsLayout.addComponent(studyDatasetDetailLayout, 0, 0, 2, 0);
        studyDetailsLayout.addComponent(datasetVariablesDetailLayout, 3, 0, 9, 0);
        
        generalLayout.addComponent(studyDetailsLayout);
        generalLayout.addComponent(buttonArea);
        
        setContent(generalLayout);
        
    }
    
    protected void initializeLayout() {
        
        //generalLayout.setSpacing(true);
        //generalLayout.setMargin(true);
        generalLayout.setComponentAlignment(buttonArea, Alignment.TOP_LEFT);
        
        studyDetailsLayout.setWidth("100%");
        
        studyDatasetDetailLayout.setMargin(true);
        studyDatasetDetailLayout.setWidth("100%");

        datasetVariablesDetailLayout.setMargin(true);
        datasetVariablesDetailLayout.setSpacing(true);
        datasetVariablesDetailLayout.setWidth("100%");

        
    }
    
    protected void initialize() {
    }

    protected void initializeActions() {
        btnCancel.addListener(new Button.ClickListener() {
	
			private static final long serialVersionUID = 4719456133687409089L;

			@Override
			public void buttonClick(ClickEvent event) {
				SelectEnvironmentForGxeWindow.this.getParent().removeWindow(SelectEnvironmentForGxeWindow.this);
			}
		});
        btnNext.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 8377610125826448065L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (selectEnv.getValue().toString() != "" && selectEnv.getValue() != null ){
					SelectEnvironmentForGxeWindow.this.getParent().removeWindow(SelectEnvironmentForGxeWindow.this);
					gxeAnalysisComponentPanel.generateTabContent(currentStudy, selectEnv.getValue().toString());
				}
				
			}
		});

    }
    
    protected void initializeSelectEnvironment() {
        
      
    }

    protected Table initializeFactorsTable() {
        
        Table tblFactors = new Table("Factors of the Dataset: ");
        tblFactors.setImmediate(true);
        tblFactors.setWidth("100%");
        tblFactors.setHeight("100%");
        
        BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
        container.setBeanIdProperty("id");
        tblFactors.setContainerDataSource(container);
        
        String[] columns = new String[] {"name", "trname", "scname", "tmname"};
        String[] columnHeaders = new String[] {"Name", "Property", "Scale", "Method"};
        tblFactors.setVisibleColumns(columns);
        tblFactors.setColumnHeaders(columnHeaders);
        return tblFactors;
    }
    
    protected Table initializeVariatesTable() {
        
        Table tblVariates = new Table("Variates of the Dataset: ");
        tblVariates.setImmediate(true);
        tblVariates.setWidth("100%");
        tblVariates.setHeight("100%");
        
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        tblVariates.setContainerDataSource(container);
        
        String[] columns = new String[] {"name", "trname", "scname", "tmname"};
        String[] columnHeaders = new String[] {"Name", "Property", "Scale", "Method"};
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
            	fm.setTraitid(factor.getStandardVariable().getProperty().getId());
            	
            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM){
            		factorList.add(fm);
            	}
            	
            	
            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT){
            		// only TRIAL_ENVIRONMENT_INFO_STORAGE(1020) TRIAL_INSTANCE_STORAGE(1021) factors in selectEnv dropdown
            		if (factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()
            			|| factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()	)
	            		selectEnv.addItem(factor.getLocalName());
            	}
            }
            
            for (VariableType variate : ds.getVariableTypes().getVariates().getVariableTypes()){
            	
            	VariateModel vm = new VariateModel();
            	vm.setId(variate.getRank());
            	vm.setName(variate.getLocalName());
            	vm.setScname(variate.getStandardVariable().getScale().getName());
            	vm.setScaleid(variate.getStandardVariable().getScale().getId());
            	vm.setTmname(variate.getStandardVariable().getMethod().getName());
            	vm.setTmethid(variate.getStandardVariable().getMethod().getId());
            	vm.setTrname(variate.getStandardVariable().getName());
            	vm.setTraitid(variate.getStandardVariable().getProperty().getId());
            	if (!variate.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate")){
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
 	    Object[] oldColumns = factors.getVisibleColumns();
        String[] columns = Arrays.copyOf(oldColumns, oldColumns.length, String[].class);
        
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        variates.setContainerDataSource(container);
        
        for (VariateModel v : variateList ){
     	   container.addBean(v);
        }
        
        variates.setContainerDataSource(container);
        
        variates.setVisibleColumns(columns);
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
    }

    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
		return this.studyDataManager;
	}


}

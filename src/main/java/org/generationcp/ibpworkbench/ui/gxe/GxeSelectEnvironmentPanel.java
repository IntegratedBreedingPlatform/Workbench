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
import java.util.Map.Entry;

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
import org.generationcp.middleware.domain.dms.TrialEnvironment;
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

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
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
public class GxeSelectEnvironmentPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private Table variates;
    private Property.ValueChangeListener selectAllListener;
    private CheckBox chkVariatesSelectAll;
    
    private Boolean refreshing = false;
    
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
    
    private GxeComponentPanel gxeAnalysisComponentPanel;
    
	private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;
    
    private List<String> environmentNames = new ArrayList<String>();
    private TrialEnvironments trialEnvironments = null;

    public GxeSelectEnvironmentPanel(StudyDataManager studyDataManager,Project currentProject, Study study, GxeComponentPanel gxeAnalysisComponentPanel) {
    	this.studyDataManager = studyDataManager;
        this.currentProject = currentProject;
        this.currentStudy = study;
        this.setGxeAnalysisComponentPanel(gxeAnalysisComponentPanel);
        
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
    	
    	
    	lblEnvironmentFactorHeader = new Label();
    	lblEnvironmentFactorHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	lblEnvironmentFactorDescription = new Label();
    	lblEnvironmentGroupsHeader = new Label();
    	lblEnvironmentGroupsHeader.setStyleName(Bootstrap.Typography.H2.styleName());
    	lblEnvironmentGroupsDescription = new Label();
    	lblEnvironmentGroupsSpecify = new Label();
    	lblReviewSelectedDataset = new Label();
    	lblReviewSelectedDataset.setStyleName(Bootstrap.Typography.H2.styleName());
    	lblFactorTableHeader = new Label();
    	lblFactorTableHeader.setStyleName(Bootstrap.Typography.H3.styleName());
    	lblFactorTableDescription = new Label();
    	lblVariateTableHeader = new Label();
    	lblVariateTableHeader.setStyleName(Bootstrap.Typography.H3.styleName());
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
        lblStudyTreeDetailTitle.setStyleName(Bootstrap.Typography.H1.styleName());

        final Table factors = initializeFactorsTable();
        factors.setImmediate(true);
        initializeVariatesTable();
        variates.setImmediate(true);
        
        
        selectAllListener = new Property.ValueChangeListener(){

			@Override
			public void valueChange(ValueChangeEvent event) {
				// TODO Auto-generated method stub
				Boolean val = (Boolean) event.getProperty().getValue();
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) variates.getContainerDataSource();
				for (Object itemId : container.getItemIds()){
					container.getItem(itemId).getBean().setActive(val);
				}
				for (Entry<String, Boolean> entry : variatesCheckboxState.entrySet()){
					variatesCheckboxState.put(entry.getKey(), val);
				}
				
				refreshing = true;
				variates.refreshRowCache();
				refreshing = false;
				//variatesCheckboxState.put(vm.getName(), val);
				//vm.setActive(val);
			}
        	
        };
        
        chkVariatesSelectAll = new CheckBox();
        chkVariatesSelectAll.setImmediate(true);
        chkVariatesSelectAll.setCaption("Select All");
        chkVariatesSelectAll.addListener(selectAllListener);
        
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
        	break;
        }
        
        buttonArea = layoutButtonArea();
       
        
        generalLayout.addComponent(lblEnvironmentFactorHeader);
        	specifyEnvironmentFactorLayout.addComponent(lblEnvironmentFactorDescription);
        	specifyEnvironmentFactorLayout.addComponent(selectSpecifyEnvironment);
        generalLayout.addComponent(specifyEnvironmentFactorLayout);
        
        //generalLayout.addComponent(lblEnvironmentGroupsHeader);
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
        generalLayout.addComponent(chkVariatesSelectAll);
        
        generalLayout.addComponent(datasetVariablesDetailLayout);
        generalLayout.addComponent(buttonArea);
        
        Object item = "Analyze All";
        selectSpecifyEnvironmentGroups.addItem(item);
        selectSpecifyEnvironmentGroups.select(item);
        
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
					getGxeAnalysisComponentPanel().generateTabContent(currentStudy, 
							selectSpecifyEnvironment.getValue().toString(), 
							selectSpecifyEnvironmentGroups.getValue().toString(), 
							variatesCheckboxState, GxeSelectEnvironmentPanel.this);
				}
				
			}
		});

    }
    
    protected void initializeSelectEnvironment() {
        
      
    }

    protected Table initializeFactorsTable() {
        
        final Table tblFactors = new Table();
        tblFactors.setImmediate(true);
        tblFactors.setWidth("100%");
        tblFactors.setHeight("170px");
        
        BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
        container.setBeanIdProperty("id");
        tblFactors.setContainerDataSource(container);
        
        String[] columns = new String[] {"name", "description"};
        String[] columnHeaders = new String[] {"Name", "Description"};
        tblFactors.setVisibleColumns(columns);
        tblFactors.setColumnHeaders(columnHeaders);
        
        
        tblFactors.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             

			private static final long serialVersionUID = 1L;

				public String generateDescription(Component source, Object itemId, Object propertyId) {
        	    	 BeanContainer<Integer, FactorModel> container = (BeanContainer<Integer, FactorModel>) tblFactors.getContainerDataSource();
        	    	 FactorModel fm = container.getItem(itemId).getBean();
        	    	 
        	    	 StringBuilder sb = new StringBuilder();
        	    	 sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", fm.getName()));
        	    	 sb.append(String.format("<span>Property:</span> %s<br>", fm.getTrname()));
        	    	 sb.append(String.format("<span>Scale:</span> %s<br>", fm.getScname()));
        	    	 sb.append(String.format("<span>Method:</span> %s<br>", fm.getTmname()));
        	    	 sb.append(String.format("<span>Data Type:</span> %s", fm.getDataType()));
        	                                                                        
        	         return sb.toString();
        	     }
        	});
        
        return tblFactors;
    }
    
    protected void initializeVariatesTable() {
        
        variates = new Table();
        variates.setImmediate(true);
        variates.setWidth("100%");
        variates.setHeight("100%");
        variates.setColumnExpandRatio("", 0.5f);
        variates.setColumnExpandRatio("name", 1);
        variates.setColumnExpandRatio("description", 4);
        variates.setColumnExpandRatio("testedin", 1);
        
        variates.addGeneratedColumn("testedin", new Table.ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				
					BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) source.getContainerDataSource();
					VariateModel vm = container.getItem(itemId).getBean();
					
					int testedIn = getTestedIn(selectSpecifyEnvironment.getValue().toString(), environmentNames, vm.getVariableId(), getCurrentDataSetId(), trialEnvironments);
					if (testedIn > 3){
						vm.setActive(true);
					}
					
					return String.format("%s of %s", testedIn, environmentNames.size());
					
			}});
        
        variates.addGeneratedColumn("", new Table.ColumnGenerator(){

			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public Object generateCell(Table source, Object itemId,
					Object columnId) {
				
				BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) variates.getContainerDataSource();
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
						
						if (!val){
							chkVariatesSelectAll.removeListener(selectAllListener);
							chkVariatesSelectAll.setValue(val);
							chkVariatesSelectAll.addListener(selectAllListener);
						}
					
					}
				});
				
				if (!refreshing){
					int testedIn = getTestedIn(selectSpecifyEnvironment.getValue().toString(), environmentNames, vm.getVariableId(), getCurrentDataSetId(), trialEnvironments);
					if (testedIn > 3){
						vm.setActive(true);
					}else{
						vm.setActive(false);
					}
				}

				if (vm.getActive()) {
					checkBox.setValue(true);
					getVariatesCheckboxState().put(vm.getName(), true);
				} else {
					checkBox.setValue(false);
					getVariatesCheckboxState().put(vm.getName(), false);
				}
				
				return checkBox;
				
			}
        	
        });
        
        variates.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             

			private static final long serialVersionUID = 1L;

				public String generateDescription(Component source, Object itemId, Object propertyId) {
        	    	 BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) variates.getContainerDataSource();
        	    	 VariateModel vm = container.getItem(itemId).getBean();
        	    	 
        	    	 StringBuilder sb = new StringBuilder();
        	    	 sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", vm.getDisplayName()));
        	    	 sb.append(String.format("<span>Property:</span> %s<br>", vm.getTrname()));
        	    	 sb.append(String.format("<span>Scale:</span> %s<br>", vm.getScname()));
        	    	 sb.append(String.format("<span>Method:</span> %s<br>", vm.getTmname()));
        	    	 sb.append(String.format("<span>Data Type:</span> %s", vm.getDatatype()));
        	                                                                        
        	         return sb.toString();
        	     }
        	});
		
        

        
        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
        container.setBeanIdProperty("id");
        variates.setContainerDataSource(container);
        
        String[] columns = new String[] {"", "displayName", "description","testedin"};
        String[] columnHeaders = new String[] {"","Name", "Description","Tested In"};
        variates.setVisibleColumns(columns);
        variates.setColumnHeaders(columnHeaders);
        variates.setColumnWidth("", 18);
        //variates.refreshRowCache();

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
            	fm.setDataType(factor.getStandardVariable().getDataType().getName());
            	
            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
            			&& factor.getStandardVariable().getStoredIn().getId() != TermId.TRIAL_INSTANCE_STORAGE.getId()
            			){
            		selectSpecifyEnvironmentGroups.addItem(fm.getName());
            		
            	}
            	
            	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM){
            		factorList.add(fm);
            		
            	}
            	
            	
           		// only TRIAL_ENVIRONMENT_INFO_STORAGE(1020) TRIAL_INSTANCE_STORAGE(1021) factors in selectEnv dropdown
            	if (factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()
            			|| factor.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()	)
	            		selectSpecifyEnvironment.addItem(factor.getLocalName());
            	
            	
            	
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
            	vm.setDatatype(variate.getStandardVariable().getDataType().getName());
            	if (!variate.getStandardVariable().getMethod().getName().equalsIgnoreCase("error estimate") && !variate.getStandardVariable().getMethod().getName().equalsIgnoreCase("ls blups")){
            		vm.setActive(false);
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
        this.variates.setContainerDataSource(container);
        
        for (VariateModel v : variateList ){
     	   container.addBean(v);
        }
        
        this.variates.setContainerDataSource(container);
        
        this.variates.setVisibleColumns(new String[]{ "","displayName", "description","testedin"});
        this.variates.setColumnHeaders(new String[]{ "", "Name", "Description", "Tested In"});
        //this.variates.refreshRowCache();
        
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
	
	private int getTestedIn(String envFactorName, List<String> environmentNames , Integer variableId , Integer meansDataSetId ,TrialEnvironments trialEnvironments){
		
		int counter = 0;
		
		
			for (String environmentName : environmentNames){
				try{
					TrialEnvironment te = trialEnvironments.findOnlyOneByLocalName(envFactorName, environmentName);
					if (te!=null){
						long count = studyDataManager.countStocks(
								meansDataSetId
							,te.getId()
							,variableId
								);
						if (count > 0) counter++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			
			}
			 
		
		
		return counter;
		
	}

	public GxeComponentPanel getGxeAnalysisComponentPanel() {
		return gxeAnalysisComponentPanel;
	}

	public void setGxeAnalysisComponentPanel(GxeComponentPanel gxeAnalysisComponentPanel) {
		this.gxeAnalysisComponentPanel = gxeAnalysisComponentPanel;
	}
	
	@Override
	public Object getData(){
		return this.getCurrentStudy();
		
	}


}

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

package org.generationcp.ibpworkbench.ui.breedingview.metaanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.generationcp.browser.study.listeners.ViewStudyDetailsButtonClickListener;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.HeaderLabelLayout;
import org.generationcp.ibpworkbench.IBPWorkbenchLayout;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.MetaEnvironmentModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
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
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Aldrin Batac
 *
 */
@Configurable
public class MetaAnalysisPanel extends VerticalLayout implements InitializingBean, 
									InternationalizableComponent, IBPWorkbenchLayout {

    private static final long serialVersionUID = 1L;
   
    private TabSheet tabSheet; 
	private Label lblPageTitle;
	private HeaderLabelLayout heading;	
    private Label lblBuildNewAnalysisDescription;
    private Label lblReviewEnvironments;
    private Label lblSelectDatasetsForAnalysis;
    private Label lblSelectDatasetsForAnalysisDescription;
    private Button linkCloseAllTab;
    private Table selectedEnvironmenTable;
    
    private Button browseLink;
   
    private VerticalLayout selectedDataSetEnvironmentLayout;
    private GridLayout studyDetailsLayout;
    
    private Project currentProject;
    private Study currentStudy;
    private Integer currentRepresentationId;
    private Integer currentDataSetId;
  
    private String currentDatasetName;

    private Button btnCancel;
    private Button btnNext;
    private Component buttonArea;

    private HashMap<String, Boolean> variatesCheckboxState;
    
    private final static Logger LOG = LoggerFactory.getLogger(MetaAnalysisPanel.class);
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
	private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;
    
    public MetaAnalysisPanel(Project currentProject, Database database) {
        this.currentProject = currentProject;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
    }
    
    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(btnCancel, Message.RESET);
        messageSource.setCaption(btnNext, Message.NEXT);
        messageSource.setValue(lblPageTitle, Message.TITLE_METAANALYSIS);
        messageSource.setValue(lblBuildNewAnalysisDescription,  Message.META_BUILD_NEW_ANALYSIS_DESCRIPTION);
        messageSource.setValue(lblSelectDatasetsForAnalysisDescription, Message.META_SELECT_DATASETS_FOR_ANALYSIS_DESCRIPTION);
    }
    
    
	@Override
	public void instantiateComponents() {
		managerFactory = managerFactoryProvider.getManagerFactoryForProject(currentProject);
		
		lblPageTitle = new Label();
    	lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());
    	lblPageTitle.setHeight("26px");
		
		ThemeResource resource = new ThemeResource("../vaadin-retro/images/search-nurseries.png");
		Label headingLabel =  new Label("Select Data for Analysis");
		headingLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		headingLabel.addStyleName("label-bold");
		heading = new HeaderLabelLayout(resource,headingLabel);
    	
    	browseLink = new Button();
		browseLink.setImmediate(true);
		browseLink.setStyleName("link");
		browseLink.setCaption("Browse");
		browseLink.setWidth("48px");
				
    	tabSheet = new TabSheet();
    	tabSheet.setWidth("100%");
    	
        setSelectedEnvironmenTable(new Table());
        BeanItemContainer<MetaEnvironmentModel> container = new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);
        getSelectedEnvironmenTable().setWidth("100%");
        getSelectedEnvironmenTable().setHeight("450px");
        getSelectedEnvironmenTable().setContainerDataSource(container);
        getSelectedEnvironmenTable().setVisibleColumns(new Object[]{"studyName","dataSetName","trial", "environment"});
        getSelectedEnvironmenTable().setColumnHeaders(new String[]{"Study Name","Dataset Name","Trial", "Environment"});
        
        lblReviewEnvironments = new Label("<span class='bms-environments' style='position:relative; top: -2px; color: #0076A9; "
        		+ "font-size: 25px; font-weight: bold;'></span><b>&nbsp;"
        		+ "<span style='position:relative; top: -3px;'>"
        		+ messageSource.getMessage(Message.META_REVIEW_ENVIRONMENTS)+"</span></b>",Label.CONTENT_XHTML);
        lblReviewEnvironments.setStyleName(Bootstrap.Typography.H3.styleName());
        
        lblBuildNewAnalysisDescription = new Label();
        
        linkCloseAllTab = new Button();
        linkCloseAllTab.setStyleName("link");
        linkCloseAllTab.setImmediate(true);
        linkCloseAllTab.setCaption("Close All Tabs");
        linkCloseAllTab.setVisible(false);
        
        lblSelectDatasetsForAnalysis = new Label("<span class='bms-dataset' style='position:relative; top: -1px; color: #FF4612; "
        		+ "font-size: 20px; font-weight: bold;'></span><b>&nbsp;"+
        		messageSource.getMessage(Message.META_SELECT_DATASETS_FOR_ANALYSIS)+"</b>",Label.CONTENT_XHTML);
        lblSelectDatasetsForAnalysis.setStyleName(Bootstrap.Typography.H3.styleName());
        
        lblSelectDatasetsForAnalysisDescription = new Label();
        
        //initialize buttons
        btnCancel = new Button();
        btnNext = new Button();
        btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        
	}

	@Override
	public void initializeValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListeners() {
		
        tabSheet.addListener(new TabSheet.SelectedTabChangeListener() {

            private static final long serialVersionUID = -7822326039221887888L;

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if(tabSheet.getComponentCount() <= 1){
                    linkCloseAllTab.setVisible(false);
                }
                else{
                	linkCloseAllTab.setVisible(true);
                }
            }
        });
        
		browseLink.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1425892265723948423L;

			@Override
			public void buttonClick(ClickEvent event) {
					
				SelectDatasetDialog dialog = new SelectDatasetDialog(event.getComponent().getWindow(), MetaAnalysisPanel.this ,(StudyDataManagerImpl) getStudyDataManager());
				event.getComponent().getWindow().addWindow(dialog);
			}
			
		});
		
        linkCloseAllTab.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				tabSheet.removeAllComponents();
				linkCloseAllTab.setVisible(false);
			}
		});

    	btnCancel.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				tabSheet.removeAllComponents();
				selectedEnvironmenTable.removeAllItems();
			}
		});
    	
        btnNext.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 3367191648910396919L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
					List<MetaEnvironmentModel> metaEnvironments = new ArrayList<MetaEnvironmentModel>();
					Iterator<MetaEnvironmentModel> itr = (Iterator<MetaEnvironmentModel>) MetaAnalysisPanel.this.getSelectedEnvironmenTable().getContainerDataSource().getItemIds().iterator();
					while(itr.hasNext()){
						metaEnvironments.add(itr.next());
					}
					
					if (metaEnvironments.size() > 0){
						IContentWindow w = (IContentWindow) event.getComponent().getWindow();
						w.showContent(new MetaAnalysisSelectTraitsPanel(MetaAnalysisPanel.this.getCurrentProject(), metaEnvironments, MetaAnalysisPanel.this, managerFactory));
					}
					
			}
		});
	}

	@Override
	public void layoutComponents() {
    	setMargin(false,true,false,true);
    	setSpacing(true);
    	setWidth("100%");
    	
		HorizontalLayout browseLabelLayout = new HorizontalLayout();
		browseLabelLayout.addComponent(browseLink);
		browseLabelLayout.addComponent(new Label("for a dataset to work with."));
		browseLabelLayout.setSizeUndefined();
		browseLabelLayout.setMargin(false);
		
		VerticalLayout selectDataForAnalysisLayout = new VerticalLayout();
    	selectDataForAnalysisLayout.addComponent(heading);
    	selectDataForAnalysisLayout.addComponent(browseLabelLayout);
		
		studyDetailsLayout = new GridLayout(10, 3);
        studyDetailsLayout.setMargin(false);
        studyDetailsLayout.setSpacing(true);
        studyDetailsLayout.setWidth("100%");
        studyDetailsLayout.addComponent(lblReviewEnvironments, 0, 0, 4, 0);
        studyDetailsLayout.addComponent(linkCloseAllTab, 8, 0, 9, 0);
        studyDetailsLayout.setComponentAlignment(linkCloseAllTab, Alignment.TOP_RIGHT);
        studyDetailsLayout.addComponent(lblBuildNewAnalysisDescription, 0, 1, 9, 1);

        selectedDataSetEnvironmentLayout = new VerticalLayout();
        selectedDataSetEnvironmentLayout.setMargin(false);
        selectedDataSetEnvironmentLayout.setSpacing(true);
        selectedDataSetEnvironmentLayout.addComponent(lblSelectDatasetsForAnalysis);
        selectedDataSetEnvironmentLayout.addComponent(lblSelectDatasetsForAnalysisDescription);
        selectedDataSetEnvironmentLayout.addComponent(getSelectedEnvironmenTable());
		
        buttonArea = layoutButtonArea();
        
    	addComponent(lblPageTitle);
        addComponent(selectDataForAnalysisLayout);
        addComponent(studyDetailsLayout);
        addComponent(selectedDataSetEnvironmentLayout);
        addComponent(buttonArea);
        setComponentAlignment(buttonArea, Alignment.TOP_CENTER);
	}
	
    protected Component layoutButtonArea() {
    	
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
   
        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnNext);
        buttonLayout.setComponentAlignment(btnCancel, Alignment.TOP_CENTER);
        buttonLayout.setComponentAlignment(btnNext, Alignment.TOP_CENTER);
        return buttonLayout;
    }
    
	public void generateTab(int dataSetId) {
		try {
			if (studyDetailsLayout.getComponent(0, 2) == null){
				studyDetailsLayout.addComponent(tabSheet, 0, 2, 9, 2);
			}

			TabSheet tabSheet = MetaAnalysisPanel.this.getTabsheet();
			DataSet ds = MetaAnalysisPanel.this.getStudyDataManager().getDataSet(dataSetId);

			Iterator<Component> itr = tabSheet.getComponentIterator();
			while(itr.hasNext()){
				EnvironmentTabComponent tab = (EnvironmentTabComponent) itr.next();
				if (tab.getDataSetId() == ds.getId()){
					tabSheet.setSelectedTab(tab);
					return;
				}
			}

			EnvironmentTabComponent component = new EnvironmentTabComponent(ds);
			tabSheet.addTab(component);
			tabSheet.getTab(component).setClosable(true);
			tabSheet.getTab(component).setCaption(ds.getName());
			tabSheet.setSelectedTab(component);
			
			if(tabSheet.getComponentCount() > 1){
				linkCloseAllTab.setVisible(true);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}// end of generateTab

	// SETTERS AND GETTERS
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
    	if (this.studyDataManager == null) {
            this.studyDataManager = managerFactory.getNewStudyDataManager();
        }
    	return this.studyDataManager;
		
	}

	public HashMap<String, Boolean> getVariatesCheckboxState() {
		return variatesCheckboxState;
	}

	public void setVariatesCheckboxState(HashMap<String, Boolean> variatesCheckboxState) {
		this.variatesCheckboxState = variatesCheckboxState;
	}

	public TabSheet getTabsheet() {
		return tabSheet;
	}

	public void setTabsheet(TabSheet tabsheet) {
		this.tabSheet = tabsheet;
	}
	
	public Table getSelectedEnvironmenTable() {
		return selectedEnvironmenTable;
	}

	public void setSelectedEnvironmenTable(Table selectedEnvironmenTable) {
		this.selectedEnvironmenTable = selectedEnvironmenTable;
	}
		

	class EnvironmentTabComponent extends VerticalLayout {
		
		private static final long serialVersionUID = 1L;

		DataSet dataSet;
		String studyName;
		
		Label lblFactors;
		Label lblFactorDescription;
		Label lblTraits;
		Label lblTraitDescription;

		public EnvironmentTabComponent(DataSet dataSet){
			
			this.dataSet = dataSet;
			
			setSpacing(true);
			setMargin(true);
			setWidth("100%");
			setHeight("100%");
			
			initializeComponents();
			
			managerFactory.close();
			
		}
		
		public int getDataSetId(){
			return dataSet.getId();
		}
		
		private void initializeComponents(){
			
			try {
				studyName = MetaAnalysisPanel.this.getStudyDataManager().getStudy(dataSet.getStudyId()).getName();
			} catch (MiddlewareQueryException e) {
				
				e.printStackTrace();
			}

			lblFactors = new Label("<span class='bms-factors' style='color: #39B54A; font-size: 20px; font-weight: bold;'></span><b>&nbsp;FACTORS</b>",Label.CONTENT_XHTML);
			lblFactors.setStyleName(Bootstrap.Typography.H3.styleName());
			lblFactorDescription = new Label("The factors of the dataset you have selected are shown below for your review.");
			lblTraits = new Label("<span class='bms-variates' style='color: #B8D433; font-size: 20px; font-weight: bold;'></span><b>&nbsp;TRAITS</b>",Label.CONTENT_XHTML);
			lblTraits.setStyleName(Bootstrap.Typography.H3.styleName());
			lblTraitDescription = new Label("The traits of the dataset you have selected are shown below for your review.");
			
			Label lblStudyName = new Label("<b>Study Name:</b> " + studyName);
			lblStudyName.setContentMode(Label.CONTENT_XHTML);
			Button linkFullStudyDetails = new Button("<span class='glyphicon glyphicon-open' style='right: 6px'></span>Full Study Details");
			Button linkSaveToList = new Button("<span class='glyphicon glyphicon-plus' style='right: 6px'></span>Save To List");
			linkFullStudyDetails.setHtmlContentAllowed(true);
			linkSaveToList.setHtmlContentAllowed(true);
			
			linkSaveToList.addListener(new Button.ClickListener() {
			
				private static final long serialVersionUID = -91508239632267095L;

				@Override
				public void buttonClick(ClickEvent event) {
						addDataSetToTable();
					
				}
			});
			
//			linkFullStudyDetails.addListener(new Button.ClickListener() {
//			
//				private static final long serialVersionUID = 1425892265723948423L;
//
//				@Override
//				public void buttonClick(ClickEvent event) {	
////					StudyInfoDialog dialog = new StudyInfoDialog(event.getComponent().getWindow(), dataSet.getStudyId() ,false, (StudyDataManagerImpl) getStudyDataManager());
////					event.getComponent().getWindow().addWindow(dialog);
//				}
//			});
			
			linkFullStudyDetails.addListener(new ViewStudyDetailsButtonClickListener(dataSet.getStudyId(), studyName));

            final HorizontalLayout buttonContainer = new HorizontalLayout();
            buttonContainer.setSpacing(true);
            buttonContainer.addComponent(linkFullStudyDetails);
            buttonContainer.addComponent(linkSaveToList);

            final HorizontalLayout tableContainer = new HorizontalLayout();
            tableContainer.setSpacing(true);
            tableContainer.setSizeFull();

            final VerticalLayout factorsContainer = new VerticalLayout();
            factorsContainer.setSpacing(true);

            final VerticalLayout descContainer1 = new VerticalLayout();
            descContainer1.setSpacing(false);
            descContainer1.setHeight("90px");
            descContainer1.setWidth("100%");
            descContainer1.addComponent(lblFactors);
            descContainer1.addComponent(lblFactorDescription);
            descContainer1.setExpandRatio(lblFactorDescription,1.0f);

            factorsContainer.addComponent(descContainer1);
            factorsContainer.addComponent(initializeFactorsTable());

            final VerticalLayout variatesContainer = new VerticalLayout();
            variatesContainer.setSpacing(true);

            final VerticalLayout descContainer2 = new VerticalLayout();
            descContainer2.setSpacing(false);
            descContainer2.setHeight("90px");
            descContainer2.setWidth("100%");
            descContainer2.addComponent(lblTraits);
            descContainer2.addComponent(lblTraitDescription);
            descContainer2.setExpandRatio(lblTraitDescription,1.0f);

            variatesContainer.addComponent(descContainer2);
            variatesContainer.addComponent(initializeVariatesTable());

            tableContainer.addComponent(factorsContainer);
            tableContainer.addComponent(variatesContainer);
            tableContainer.setExpandRatio(factorsContainer, 1.0F);
            tableContainer.setExpandRatio(variatesContainer,1.0F);

            this.addComponent(lblStudyName);
            this.addComponent(buttonContainer);
            this.addComponent(tableContainer);

            this.setComponentAlignment(buttonContainer,Alignment.MIDDLE_CENTER);
            this.setExpandRatio(tableContainer,1.0f);
        }
		
		protected Table initializeVariatesTable() {
	        
	        final Table tblVariates = new Table();
	        tblVariates.setImmediate(true);
	        tblVariates.setWidth("100%");
            tblVariates.setHeight("270px");
	        tblVariates.setColumnExpandRatio("name", 1);
	        tblVariates.setColumnExpandRatio("description", 4);
	        tblVariates.setColumnExpandRatio("scname", 1);
	        
	        tblVariates.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             

	        	
				private static final long serialVersionUID = 1L;

					@SuppressWarnings("unchecked")
					public String generateDescription(Component source, Object itemId, Object propertyId) {
	        	    	 BeanContainer<Integer, VariateModel> container = (BeanContainer<Integer, VariateModel>) tblVariates.getContainerDataSource();
	        	    	 VariateModel vm = container.getItem(itemId).getBean();
	        	    	 
	        	    	 StringBuilder sb = new StringBuilder();
	        	    	 sb.append(String.format("<span class=\"gcp-table-header-bold\">%s</span><br>", vm.getName()));
	        	    	 sb.append(String.format("<span>Property:</span> %s<br>", vm.getTrname()));
	        	    	 sb.append(String.format("<span>Scale:</span> %s<br>", vm.getScname()));
	        	    	 sb.append(String.format("<span>Method:</span> %s<br>", vm.getTmname()));
	        	    	 sb.append(String.format("<span>Data Type:</span> %s", vm.getDatatype()));
	        	                                                                        
	        	         return sb.toString();
	        	     }
	        	});
	        
	        BeanContainer<Integer, VariateModel> container = new BeanContainer<Integer, VariateModel>(VariateModel.class);
	        container.setBeanIdProperty("id");
	        
	        for (VariableType variate : dataSet.getVariableTypes().getVariates().getVariableTypes()){
	        	VariateModel vm = new VariateModel();
            	vm.setId(variate.getRank());
            	vm.setName(variate.getLocalName());
            	vm.setDescription(variate.getLocalDescription());
            	vm.setScname(variate.getStandardVariable().getScale().getName());
            	vm.setScaleid(variate.getStandardVariable().getScale().getId());
            	vm.setTmname(variate.getStandardVariable().getMethod().getName());
            	vm.setTmethid(variate.getStandardVariable().getMethod().getId());
            	vm.setTrname(variate.getStandardVariable().getProperty().getName());
            	vm.setTraitid(variate.getStandardVariable().getProperty().getId());
            	vm.setDatatype(variate.getStandardVariable().getDataType().getName());
            	container.addBean(vm);
	        }
	        
	        
	        
	        tblVariates.setContainerDataSource(container);
	        
	        String[] columns = new String[] {"name", "description", "scname"};
	        String[] columnHeaders = new String[] {"Name", "Description", "Scale"};
	        tblVariates.setVisibleColumns(columns);
	        tblVariates.setColumnHeaders(columnHeaders);
	        
	        return tblVariates;
	    }
		
		 protected Table initializeFactorsTable() {
		        
		        final Table tblFactors = new Table();
		        tblFactors.setImmediate(true);
                tblFactors.setWidth("100%");
                tblFactors.setHeight("270px");
                BeanContainer<Integer, FactorModel> container = new BeanContainer<Integer, FactorModel>(FactorModel.class);
		        container.setBeanIdProperty("id");
		        
		        for (VariableType factor : dataSet.getVariableTypes().getFactors().getVariableTypes()){
		        	
		        	if (factor.getStandardVariable().getPhenotypicType() == PhenotypicType.DATASET
	            			) {
                        continue;
                    }
		        	
			        FactorModel fm = new FactorModel();
	            	fm.setId(factor.getRank());
	            	fm.setName(factor.getLocalName());
	            	fm.setDescription(factor.getLocalDescription());
	            	fm.setScname(factor.getStandardVariable().getScale().getName());
	            	fm.setScaleid(factor.getStandardVariable().getScale().getId());
	            	fm.setTmname(factor.getStandardVariable().getMethod().getName());
	            	fm.setTmethid(factor.getStandardVariable().getMethod().getId());
	            	fm.setTrname(factor.getStandardVariable().getProperty().getName());
	            	fm.setTraitid(factor.getStandardVariable().getProperty().getId());
	            	container.addBean(fm);
		        }
		        
		        
		        
		        tblFactors.setContainerDataSource(container);
		       
		        String[] columns = new String[] {"name", "description"};
		        String[] columnHeaders = new String[] {"Name", "Description"};
		        tblFactors.setVisibleColumns(columns);
		        tblFactors.setColumnHeaders(columnHeaders);
		        
		        tblFactors.setItemDescriptionGenerator(new ItemDescriptionGenerator() {                             

					private static final long serialVersionUID = 1L;

						@SuppressWarnings("unchecked")
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
		
		
		@SuppressWarnings("unchecked")
		private void addDataSetToTable(){
			
			BeanItemContainer<MetaEnvironmentModel> container  = (BeanItemContainer<MetaEnvironmentModel>) MetaAnalysisPanel.this.getSelectedEnvironmenTable().getContainerDataSource();
				
			String trialInstanceFactorName=null;
			String environmentFactorName=null;
			
			for (VariableType f : dataSet.getVariableTypes().getFactors().getVariableTypes()){
				if (f.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_INSTANCE_STORAGE.getId()){
					trialInstanceFactorName = f.getLocalName();
				}
				
				if (f.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
						&& f.getStandardVariable().getScale().getName().equalsIgnoreCase("abbreviation")){
					environmentFactorName = f.getLocalName();
				}
				
				if (f.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT
						&& f.getStandardVariable().getStoredIn().getId() == TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId()){
					if (environmentFactorName == null) {
                        environmentFactorName = f.getLocalName();
                    }
				}
				
			}
			
			if (environmentFactorName == null) {
                environmentFactorName = trialInstanceFactorName;
            }
			
			try {
				TrialEnvironments envs = MetaAnalysisPanel.this.getStudyDataManager().getTrialEnvironmentsInDataset(dataSet.getId());
			
				List<Variable> variables;
				variables = envs.getVariablesByLocalName(environmentFactorName);
				
				for (Variable var : variables){
					TrialEnvironment env = envs.findOnlyOneByLocalName(environmentFactorName, var.getValue());
					if (env == null && environmentFactorName != trialInstanceFactorName){
						environmentFactorName = trialInstanceFactorName;
					}
					break;
				}
				
				if (environmentFactorName == trialInstanceFactorName){
					variables = envs.getVariablesByLocalName(environmentFactorName);
				}
					
				for (Variable var : variables){
					if (var != null){
						if (var.getValue() != ""){
								//
								TrialEnvironment env = envs.findOnlyOneByLocalName(environmentFactorName, var.getValue());
								
								if (env!=null){
										
										String trialNo = env.getVariables().findByLocalName(trialInstanceFactorName).getValue();
										String envName = env.getVariables().findByLocalName(environmentFactorName).getValue();
										
										MetaEnvironmentModel bean = new MetaEnvironmentModel();
										bean.setTrial(trialNo);
										bean.setEnvironment(envName);
										bean.setDataSetId(dataSet.getId());
										bean.setDataSetName(dataSet.getName());
										bean.setStudyId(dataSet.getStudyId());
										bean.setStudyName(studyName);
										bean.setTrialFactorName(trialInstanceFactorName);
										if (dataSet.getDataSetType() == null){
											bean.setDataSetTypeId(DataSetType.PLOT_DATA.getId());
										}else{
											bean.setDataSetTypeId(dataSet.getDataSetType().getId());
										}
										
										
										container.addBean(bean);
								}
						}
					}
				}
				
			} catch (MiddlewareQueryException e) {
				
				e.printStackTrace();
			} catch (Exception e){
				
				e.printStackTrace();
			}
			
			managerFactory.close();
		}
		
	}// end of EnvironmentTabComponent inner class

}//  end of MetaAnalysisPanel 



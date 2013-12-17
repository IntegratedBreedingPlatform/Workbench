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

package org.generationcp.ibpworkbench.ui.metaanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.generationcp.browser.study.StudyInfoDialog;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.StudyTreeExpandAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.MetaEnvironmentModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
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
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
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
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class SelectDatasetsForMetaAnalysisPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
   
	private TabSheet tabSheet; 
	private Label lblPageTitle;
    private Label lblStudyTreeDetailTitle;
    private Label lblStudyTreeDetailDescription;
    private Label lblBuildNewAnalysisHeader;
    private Label lblBuildNewAnalysisDescription;
    private Label lblReviewEnvironments;
    private Label lblSelectDatasetsForAnalysis;
    private Label lblSelectDatasetsForAnalysisDescription;
    private Button linkCloseAllTab;
    private Table selectedEnvironmenTable;
    
    private VerticalLayout generalLayout;
    private VerticalLayout studyTreeLayout;
    private VerticalLayout studyTreeLayoutTableContainer;
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

    private Database database;
    private Role role;
    private HashMap<String, Boolean> variatesCheckboxState;
    
    private ThemeResource folderResource;
    private ThemeResource leafResource;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
	private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;
    
    public SelectDatasetsForMetaAnalysisPanel(Project currentProject, Database database) {
  
        this.currentProject = currentProject;
        this.database = database;

        setWidth("100%");
        
    }
    
    public SelectDatasetsForMetaAnalysisPanel(Project currentProject, Database database, Role role) {
    	  
        this.currentProject = currentProject;
        this.database = database;
        this.role = role;

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
    	
    	lblPageTitle = new Label();
    	lblPageTitle.setStyleName(Bootstrap.Typography.H1.styleName());
    	
    	tabSheet = new TabSheet();
    	tabSheet.setWidth("100%");
    	//tabSheet.setStyleName(Reindeer.);
    	
    	folderResource =  new ThemeResource("images/folder.png");
        leafResource =  new ThemeResource("images/leaf_16.png");
        
        generalLayout = new VerticalLayout();
        generalLayout.setWidth("95%");
        
        studyTreeLayout = new VerticalLayout();
        studyTreeLayoutTableContainer = new VerticalLayout();
        
        studyDetailsLayout = new GridLayout(10, 4);
        studyDetailsLayout.setMargin(true);
        studyDetailsLayout.setSpacing(true);
        
        selectedDataSetEnvironmentLayout = new VerticalLayout();
        selectedDataSetEnvironmentLayout.setMargin(true);
        selectedDataSetEnvironmentLayout.setSpacing(true);
        
        
        studyTreeLayout.addComponent(lblPageTitle);
        studyTreeLayout.addComponent(new Label(""));
      
        lblStudyTreeDetailTitle = new Label();
        lblStudyTreeDetailTitle.setStyleName(Bootstrap.Typography.H2.styleName());
        studyTreeLayout.addComponent(lblStudyTreeDetailTitle);
        
        lblStudyTreeDetailDescription = new Label();
        studyTreeLayout.addComponent(lblStudyTreeDetailDescription);
        
        setSelectedEnvironmenTable(new Table());
        BeanItemContainer<MetaEnvironmentModel> container = new BeanItemContainer<MetaEnvironmentModel>(MetaEnvironmentModel.class);
        getSelectedEnvironmenTable().setWidth("100%");
        getSelectedEnvironmenTable().setHeight("450px");
        getSelectedEnvironmenTable().setContainerDataSource(container);
        getSelectedEnvironmenTable().setVisibleColumns(new Object[]{"studyName","dataSetName","trial", "environment"});
        getSelectedEnvironmenTable().setColumnHeaders(new String[]{"Study Name","Dataset Name","Trial", "Environment"});
        
        
        TreeTable tr = createStudyTreeTable(this.database);
        studyTreeLayoutTableContainer.addComponent(tr);
        studyTreeLayout.addComponent(studyTreeLayoutTableContainer);
        
        lblBuildNewAnalysisHeader = new Label();
        lblBuildNewAnalysisHeader.setStyleName(Bootstrap.Typography.H2.styleName());
        lblBuildNewAnalysisDescription = new Label();
        lblReviewEnvironments = new Label();
        lblReviewEnvironments.setStyleName(Bootstrap.Typography.H3.styleName());
        linkCloseAllTab = new Button();
        linkCloseAllTab.setStyleName("link");
        linkCloseAllTab.setImmediate(true);
        linkCloseAllTab.setCaption("Close All Tabs");
        lblSelectDatasetsForAnalysis = new Label();
        lblSelectDatasetsForAnalysis.setStyleName(Bootstrap.Typography.H2.styleName());
        lblSelectDatasetsForAnalysisDescription = new Label();
        
        linkCloseAllTab.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				tabSheet.removeAllComponents();
			}
		});
     
        studyDetailsLayout.addComponent(lblBuildNewAnalysisHeader, 0, 0, 9, 0);
        studyDetailsLayout.addComponent(lblReviewEnvironments, 0, 1, 2, 1);
        studyDetailsLayout.addComponent(linkCloseAllTab, 8, 1, 9, 1);
        studyDetailsLayout.setComponentAlignment(linkCloseAllTab, Alignment.TOP_RIGHT);
        studyDetailsLayout.addComponent(lblBuildNewAnalysisDescription, 0, 2, 9, 2);
        studyDetailsLayout.addComponent(tabSheet, 0, 3, 9, 3);
        //studyDetailsLayout.addComponent(selectedEnvironmenTable, 5, 2, 9, 3);
        
        
        selectedDataSetEnvironmentLayout.addComponent(lblSelectDatasetsForAnalysis);
        selectedDataSetEnvironmentLayout.addComponent(lblSelectDatasetsForAnalysisDescription);
        selectedDataSetEnvironmentLayout.addComponent(getSelectedEnvironmenTable());
        
        buttonArea = layoutButtonArea();
          
        generalLayout.addComponent(studyTreeLayout);  
        generalLayout.addComponent(studyDetailsLayout);
        generalLayout.addComponent(selectedDataSetEnvironmentLayout);
        generalLayout.addComponent(buttonArea);
        generalLayout.setComponentAlignment(buttonArea, Alignment.TOP_CENTER);
        
        addComponent(generalLayout);
        
    }
    
    protected void initializeLayout() {
              
        studyTreeLayout.setSpacing(true);
        studyTreeLayout.setMargin(new MarginInfo(false,true,true,true));
        studyDetailsLayout.setWidth("100%");
        
    }
    
    protected void initialize() {
    }

    protected void initializeActions() {
    	btnCancel.addListener(new Button.ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				tabSheet.removeAllComponents();
				selectedEnvironmenTable.removeAllItems();
			}
		});
    	
        btnNext.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
					List<MetaEnvironmentModel> metaEnvironments = new ArrayList<MetaEnvironmentModel>();
					Iterator<MetaEnvironmentModel> itr = (Iterator<MetaEnvironmentModel>) SelectDatasetsForMetaAnalysisPanel.this.getSelectedEnvironmenTable().getContainerDataSource().getItemIds().iterator();
					while(itr.hasNext()){
						metaEnvironments.add(itr.next());
					}
					
					if (metaEnvironments.size() > 0){
						IContentWindow w = (IContentWindow) event.getComponent().getWindow();
						w.showContent(new SelectTraitsForMetaAnalysisPanel(SelectDatasetsForMetaAnalysisPanel.this.getCurrentProject(), metaEnvironments, SelectDatasetsForMetaAnalysisPanel.this));
					}
					
			}
		});

    }  

    protected Component layoutButtonArea() {
    	
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        
        btnCancel = new Button();
        btnNext = new Button();
        btnNext.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
   
        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnNext);
        buttonLayout.setComponentAlignment(btnCancel, Alignment.TOP_CENTER);
        buttonLayout.setComponentAlignment(btnNext, Alignment.TOP_CENTER);
        return buttonLayout;
    }

    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    private TreeTable createStudyTreeTable(Database database) {
        
        TreeTable tr = new TreeTable();
            
       
        tr.addContainerProperty("Study Name", String.class, "sname");
        tr.addContainerProperty("Title", String.class, "title");
        tr.addContainerProperty("Description", String.class, "description");
 
        List<FolderReference> folderRef = null;
        
        try {
			folderRef = getStudyDataManager().getRootFolders(database);
        } catch (MiddlewareQueryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	            if (getWindow() != null){
	                MessageNotifier.showWarning(getWindow(), 
	                        messageSource.getMessage(Message.ERROR_DATABASE),
	                    messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_STUDIES));
	            }
		}
        

        for (FolderReference fr : folderRef) {
            
            Object[] cells = new Object[3];
            cells[0] = " " + fr.getName();
            cells[1] = "";
            cells[2] = fr.getDescription();
            
            Object itemId = tr.addItem(cells, fr);
            tr.setItemIcon(itemId, folderResource);
            
        }
        
        // reserve excess space for the "treecolumn"
        tr.setSizeFull();
        tr.setColumnExpandRatio("Study Name", 1);
        tr.setColumnExpandRatio("Title", 1);
        tr.setColumnExpandRatio("Description", 1);
        tr.setSelectable(true);
        
        tr.addListener(new StudyTreeExpandAction(this, tr));
        tr.addListener(new ShowEnvironmentTab());
        return tr;
    }
    
    public void refreshStudyTreeTable(Database database) {
        
        this.studyTreeLayout.removeAllComponents();
        TreeTable tr = createStudyTreeTable(database);
        this.studyTreeLayout.addComponent(tr);
    }
    
    
    public void queryChildrenStudies(Reference parentFolderReference, TreeTable tr) throws InternationalizableException{
    	 
    	List<Reference> childrenReference = new ArrayList<Reference>();
    	  
         try {
         
         	childrenReference = getStudyDataManager().getChildrenOfFolder(parentFolderReference.getId());
         	
         } catch (MiddlewareQueryException e) {
             //LOG.error(e.toString() + "\n" + e.getStackTrace());
             e.printStackTrace();
             MessageNotifier.showWarning(getWindow(), 
                     messageSource.getMessage(Message.ERROR_DATABASE), 
                     messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
         }
         
         for (java.util.Iterator<Reference> i = childrenReference.iterator(); i.hasNext(); ){

        	 Reference r = i.next();
        	 
        	 Object[] cells = new Object[3];
        	 
        	Study s = null;
			try {
				s = this.getStudyDataManager().getStudy(r.getId());
			} catch (MiddlewareQueryException e) {}
        	
             cells[0] = " " + r.getName();
             cells[1] = (s != null) ? s.getTitle() : "" ;
             cells[2] = r.getDescription();
             
             if (r instanceof FolderReference) System.out.println("r is FolderReference");
             if (r instanceof StudyReference) System.out.println("r is StudyReference");

				
        	 tr.addItem(cells, r);
        	 tr.setParent(r, parentFolderReference);
        	 if (hasChildStudy(r.getId()) || hasChildDataset(r.getId())) {
                 tr.setChildrenAllowed(r, true);
                 tr.setItemIcon(r, folderResource);
             } else {
                 tr.setChildrenAllowed(r, false);
                 tr.setItemIcon(r, leafResource);
             }
         }
    	
    }
    
    public void queryChildrenDatasets(Reference parentFolderReference, TreeTable tr) throws InternationalizableException{
   	 
    	List<DatasetReference> childrenReference = new ArrayList<DatasetReference>();
    	  
         try {
         
         	childrenReference = getStudyDataManager().getDatasetReferences(parentFolderReference.getId());
         	
         } catch (MiddlewareQueryException e) {
             //LOG.error(e.toString() + "\n" + e.getStackTrace());
             e.printStackTrace();
             MessageNotifier.showWarning(getWindow(), 
                     messageSource.getMessage(Message.ERROR_DATABASE), 
                     messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
         }
         
         for (java.util.Iterator<DatasetReference> i = childrenReference.iterator(); i.hasNext(); ){

        	 Reference r = i.next();
        	 
        	 Object[] cells = new Object[3];
        	
             cells[0] = " " + r.getName();
             cells[1] = "";
             cells[2] = r.getDescription();
             
             if (r instanceof DatasetReference) System.out.println("r is DatasetReference");
				
        	 tr.addItem(cells, r);
        	 tr.setParent(r, parentFolderReference);
             tr.setChildrenAllowed(r, false);
             tr.setItemIcon(r, leafResource);
             
         }
    	
    }
    
    private boolean hasChildStudy(int folderId) {

        List<Reference> children = new ArrayList<Reference>();

        try {
            children = getStudyDataManager().getChildrenOfFolder(folderId);
        } catch (MiddlewareQueryException e) {
            //LOG.error(e.toString() + "\n" + e.getStackTrace());
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
            children = new ArrayList<Reference>();
        }
        if (!children.isEmpty()) {
            return true;
        }
        return false;
    }
    
    private boolean hasChildDataset(int folderId) {

        List<DatasetReference> children = new ArrayList<DatasetReference>();

        try {
            children = getStudyDataManager().getDatasetReferences(folderId);
        } catch (MiddlewareQueryException e) {
            //LOG.error(e.toString() + "\n" + e.getStackTrace());
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
            children = new ArrayList<DatasetReference>();
        }
        if (!children.isEmpty()) {
            return true;
        }
        return false;
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
        messageSource.setCaption(btnCancel, Message.RESET);
        messageSource.setCaption(btnNext, Message.NEXT);
        messageSource.setValue(lblPageTitle, Message.TITLE_METAANALYSIS);
        messageSource.setValue(lblStudyTreeDetailTitle, Message.BV_STUDY_TREE_TITLE);
        messageSource.setValue(lblStudyTreeDetailDescription, Message.META_SELECT_DATA_FOR_ANALYSIS_DESCRIPTION);
        messageSource.setValue(lblBuildNewAnalysisHeader,  Message.META_BUILD_NEW_ANALYSIS_HEADER);
        messageSource.setValue(lblBuildNewAnalysisDescription,  Message.META_BUILD_NEW_ANALYSIS_DESCRIPTION);
        messageSource.setValue(lblReviewEnvironments, Message.META_REVIEW_ENVIRONMENTS);
        messageSource.setValue(lblSelectDatasetsForAnalysis, Message.META_SELECT_DATASETS_FOR_ANALYSIS);
        messageSource.setValue(lblSelectDatasetsForAnalysisDescription, Message.META_SELECT_DATASETS_FOR_ANALYSIS_DESCRIPTION);
        
        
    }

    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
		return this.studyDataManager;
	}

	public Role getCurrentRole() {
		return role;
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

	class ShowEnvironmentTab implements ItemClickListener {


		private static final long serialVersionUID = 1L;

		@Override
		public void itemClick(ItemClickEvent event) {
			
			if (!(event.getItemId() instanceof DatasetReference)) return;

	    	DatasetReference datasetRef = (DatasetReference) event.getItemId();
	        Integer dataSetId = datasetRef.getId();

	        if (dataSetId == null) return;

	        try {
	        	
	        	
	            TabSheet tabSheet = SelectDatasetsForMetaAnalysisPanel.this.getTabsheet();
	            DataSet ds = SelectDatasetsForMetaAnalysisPanel.this.getStudyDataManager().getDataSet(dataSetId);
	            
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
	            
	            
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
			
			
			
		}
		
	}
	
	class EnvironmentTabComponent extends VerticalLayout {
		
		private static final long serialVersionUID = 1L;

		DataSet dataSet;
		String studyName;
		
		Label lblFactor;
		Label lblFactorDescription;
		Label lblVariate;
		Label lblVariateDescription;

		public EnvironmentTabComponent(DataSet dataSet){
			
			this.dataSet = dataSet;
			
			setSpacing(true);
			setMargin(true);
			setWidth("100%");
			setHeight("100%");
			
			initializeComponents();
			
		}
		
		public int getDataSetId(){
			return dataSet.getId();
		}
		
		private void initializeComponents(){
			
			try {
				studyName = SelectDatasetsForMetaAnalysisPanel.this.getStudyDataManager().getStudy(dataSet.getStudyId()).getName();
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			lblFactor = new Label("Factor");
			lblFactor.setStyleName(Bootstrap.Typography.H3.styleName());
			lblFactorDescription = new Label("The factors of the dataset you have selected are shown below for your review.");
			lblVariate = new Label("Variate");
			lblVariate.setStyleName(Bootstrap.Typography.H3.styleName());
			lblVariateDescription = new Label("The variates of the dataset you have selected are shown below for your review.");
			
			Label lblStudyName = new Label("<b>Study Name:</b> " + studyName);
			lblStudyName.setContentMode(Label.CONTENT_XHTML);
			Button linkFullStudyDetails = new Button("<span class='glyphicon glyphicon-open' style='right: 6px'></span>Full Study Details");
			Button linkSaveToList = new Button("<span class='glyphicon glyphicon-plus' style='right: 6px'></span>Save To List");
			linkFullStudyDetails.setHtmlContentAllowed(true);
			linkSaveToList.setHtmlContentAllowed(true);
			
			linkSaveToList.addListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
						addDataSetToTable();
					
				}
			});
			
			linkFullStudyDetails.addListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
						
					StudyInfoDialog dialog = new StudyInfoDialog(event.getComponent().getWindow(), (Integer) dataSet.getStudyId(), false, (StudyDataManagerImpl) getStudyDataManager());
					event.getComponent().getWindow().addWindow(dialog);
				}
			});

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
            descContainer1.addComponent(lblFactor);
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
            descContainer2.addComponent(lblVariate);
            descContainer2.addComponent(lblVariateDescription);
            descContainer2.setExpandRatio(lblVariateDescription,1.0f);

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
            	vm.setTrname(variate.getStandardVariable().getName());
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
	            			) continue;
		        	
			        FactorModel fm = new FactorModel();
	            	fm.setId(factor.getRank());
	            	fm.setName(factor.getLocalName());
	            	fm.setDescription(factor.getLocalDescription());
	            	fm.setScname(factor.getStandardVariable().getScale().getName());
	            	fm.setScaleid(factor.getStandardVariable().getScale().getId());
	            	fm.setTmname(factor.getStandardVariable().getMethod().getName());
	            	fm.setTmethid(factor.getStandardVariable().getMethod().getId());
	            	fm.setTrname(factor.getStandardVariable().getName());
	            	//fm.setTrname(factor.getStandardVariable().getProperty().getName());
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
		
		
		private void addDataSetToTable(){
			
			BeanItemContainer<MetaEnvironmentModel> container  = (BeanItemContainer<MetaEnvironmentModel>) SelectDatasetsForMetaAnalysisPanel.this.getSelectedEnvironmenTable().getContainerDataSource();
				
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
			}
			
			if (environmentFactorName == null) environmentFactorName = trialInstanceFactorName;
			
			try {
				TrialEnvironments envs = SelectDatasetsForMetaAnalysisPanel.this.getStudyDataManager().getTrialEnvironmentsInDataset(dataSet.getId());
			
				List<Variable> variables = envs.getVariablesByLocalName(environmentFactorName);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e){
				
				e.printStackTrace();
			}
		}
		
	}


}



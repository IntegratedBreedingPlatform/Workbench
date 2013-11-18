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

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.ContentModel;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelDatasetAsInputForBreedingViewAction;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.actions.ShowDatasetVariablesDetailAction;
import org.generationcp.ibpworkbench.actions.ShowStudyDatasetDetailAction;
import org.generationcp.ibpworkbench.actions.StudyTreeExpandAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.StudyReference;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout.MarginInfo;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class SelectDatasetForBreedingViewPanel extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private Label lblStudyTreeDetailTitle;
    
    private VerticalLayout generalLayout;
    
    private VerticalLayout studyTreeLayout;
    
    private GridLayout studyDetailsLayout;
    
    private HorizontalLayout datasetVariablesDetailLayout;
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
    private Integer currentDataSetId;
    
    private String currentDatasetName;

    private Button btnCancel;
    private Button btnNext;
    private Component buttonArea;

    private Database database;

    private OpenSelectDatasetForExportAction openSelectDatasetForExportAction;
    
    @Autowired
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    
	private StudyDataManager studyDataManager;
    
    private ManagerFactory managerFactory;


    public SelectDatasetForBreedingViewPanel(Project currentProject, Database database) {
  
        this.currentProject = currentProject;
        this.database = database;

        setWidth("90%");
        
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
        
        studyTreeLayout = new VerticalLayout();
        
        studyDetailsLayout = new GridLayout(10, 1);
        
        datasetVariablesDetailLayout = new HorizontalLayout();
        
        lblStudyTreeDetailTitle = new Label("Select Data for Analysis");
        lblStudyTreeDetailTitle.setStyleName("gcp-content-header");
        studyTreeLayout.addComponent(lblStudyTreeDetailTitle);
        
        Label lblStudyTreeDetailDescription = new Label("You can run Single-Site Analysis on datasets that belong to studies in your own project. Select a study and then dataset from the tree below.");
        studyTreeLayout.addComponent(lblStudyTreeDetailDescription);

        Table factors = initializeFactorsTable();
        
        Table variates = initializeVariatesTable();
        
        TreeTable tr = createStudyTreeTable(this.database, factors, variates);
        studyTreeLayout.addComponent(tr);
        
        buttonArea = layoutButtonArea();
        
        datasetVariablesDetailLayout.addComponent(factors);
        datasetVariablesDetailLayout.addComponent(variates);     
    
        studyDetailsLayout.addComponent(datasetVariablesDetailLayout, 0, 0, 9, 0);
         
        generalLayout.addComponent(studyTreeLayout);  
        
        VerticalLayout studyDetailsDescriptionLayout = new VerticalLayout();
        studyDetailsDescriptionLayout.setSpacing(true);
        studyDetailsDescriptionLayout.setMargin(new MarginInfo(false, true, false, true));
        Label lblDatasetDetailTitle = new Label("Review the Factors and Variates in the Selected Dataset");
        lblDatasetDetailTitle.setStyleName("gcp-content-header");
      
        studyDetailsDescriptionLayout.addComponent(lblDatasetDetailTitle);
        
        Label lblDatasetDetailDescription = new Label("You can review the factors and variates in the dataset you selected before submitting them for analysis.");
        studyDetailsDescriptionLayout.addComponent(lblDatasetDetailDescription);
        
        generalLayout.addComponent(studyDetailsDescriptionLayout);
        generalLayout.addComponent(studyDetailsLayout);
        generalLayout.addComponent(buttonArea);
        
        addComponent(generalLayout);
        
    }
    
    protected void initializeLayout() {
        
        //generalLayout.setSpacing(true);
        //generalLayout.setMargin(true);
        generalLayout.setComponentAlignment(buttonArea, Alignment.TOP_LEFT);
        
        studyTreeLayout.setSpacing(true);
        studyTreeLayout.setMargin(true);
        
        studyDetailsLayout.setWidth("100%");

        datasetVariablesDetailLayout.setMargin(true);
        datasetVariablesDetailLayout.setSpacing(true);
        datasetVariablesDetailLayout.setWidth("100%");
        
    }
    
    protected void initialize() {
    }

    protected void initializeActions() {
        btnCancel.addListener(new CancelDatasetAsInputForBreedingViewAction(this));
        openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this);
        btnNext.addListener(openSelectDatasetForExportAction);

    }

    protected Table initializeFactorsTable() {
        
        Table tblFactors = new Table("FACTORS");
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
        
        Table tblVariates = new Table("VARIATES");
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
    
    private Table[] refreshFactorsAndVariatesTable() {
        Table toreturn[] = new Table[2];
        datasetVariablesDetailLayout.removeAllComponents();
        Table factors = initializeFactorsTable();
        Table variates = initializeVariatesTable();
        datasetVariablesDetailLayout.addComponent(factors);
        datasetVariablesDetailLayout.addComponent(variates);
        toreturn[0] = factors;
        toreturn[1] = variates;
        return toreturn;
    }
    

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        
        
        buttonLayout.setSizeFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);

        btnCancel = new Button();
        btnNext = new Button();

        Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
        spacer.setSizeFull();
        
        buttonLayout.addComponent(spacer);
        buttonLayout.setExpandRatio(spacer,1.0F);
        buttonLayout.addComponent(btnCancel);
        buttonLayout.addComponent(btnNext);

        return buttonLayout;
    }

    protected void assemble() {
        initialize();
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    private TreeTable createStudyTreeTable(Database database, Table factors, Table variates) {
        
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
            cells[0] = fr.getName();
            cells[1] = "";
            cells[2] = fr.getDescription();
            
            tr.addItem(cells, fr);
            
        }
        
        // reserve excess space for the "treecolumn"
        tr.setWidth("100%");
        tr.setColumnExpandRatio("Study Name", 1);
        tr.setSelectable(true);
        
        tr.addListener(new StudyTreeExpandAction(this, tr));
        tr.addListener(new ShowDatasetVariablesDetailAction(factors, variates, this));
        return tr;
    }
    
    public void refreshStudyTreeTable(Database database) {
        
        Table variables[] =  refreshFactorsAndVariatesTable();
        
        this.studyTreeLayout.removeAllComponents();
        TreeTable tr = createStudyTreeTable(database, variables[0], variables[1]);
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
        	
             cells[0] = r.getName();
             cells[1] = (s != null) ? s.getTitle() : "" ;
             cells[2] = r.getDescription();
             
             if (r instanceof FolderReference) System.out.println("r is FolderReference");
             if (r instanceof StudyReference) System.out.println("r is StudyReference");

				
        	 tr.addItem(cells, r);
        	 tr.setParent(r, parentFolderReference);
        	 if (hasChildStudy(r.getId()) || hasChildDataset(r.getId())) {
                 tr.setChildrenAllowed(r, true);
             } else {
                 tr.setChildrenAllowed(r, false);
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
        	
             cells[0] = r.getName();
             cells[1] = "";
             cells[2] = r.getDescription();
             
             if (r instanceof DatasetReference) System.out.println("r is DatasetReference");
				
        	 tr.addItem(cells, r);
        	 tr.setParent(r, parentFolderReference);
             tr.setChildrenAllowed(r, false);
             
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
        messageSource.setCaption(btnCancel, Message.CANCEL);
        messageSource.setCaption(btnNext, Message.NEXT);
    }

    public StudyDataManager getStudyDataManager() {
    	if (this.studyDataManager == null) this.studyDataManager = managerFactory.getNewStudyDataManager();
		return this.studyDataManager;
	}


}

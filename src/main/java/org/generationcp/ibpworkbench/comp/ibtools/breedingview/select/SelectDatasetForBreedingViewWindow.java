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


import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelDatasetAsInputForBreedingViewAction;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.actions.ShowStudyDatasetDetailAction;
import org.generationcp.ibpworkbench.actions.StudyTreeExpandAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.RepresentationModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.Reference;
import org.generationcp.middleware.v2.domain.Study;
import org.generationcp.middleware.v2.domain.StudyReference;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.v2.domain.FolderReference;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
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
public class SelectDatasetForBreedingViewWindow extends Window implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private Label lblStudyTreeDetailTitle;
    
    private VerticalLayout generalLayout;
    
    private VerticalLayout studyTreeLayout;
    
    private GridLayout studyDetailsLayout;
    
    private VerticalLayout studyDatasetDetailLayout;
    
    private HorizontalLayout datasetVariablesDetailLayout;
    
    private Project currentProject;

    private Study currentStudy;
    
    private Integer currentRepresentationId;
    
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
    
    @Autowired
    private StudyDataManager studyDataManagerV2;
    
    private ManagerFactory managerFactory;


    public SelectDatasetForBreedingViewWindow(Project currentProject, Database database) {
  
        this.currentProject = currentProject;
        this.database = database;

        setModal(true);

       /* Make the sub window 50% the size of the browser window */
        setWidth("80%");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        
        setCaption("Select a Study and a Dataset to run the Breeding View: ");
        
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
        
        studyDatasetDetailLayout = new VerticalLayout();
        
        datasetVariablesDetailLayout = new HorizontalLayout();
        
        //selectDatasetTitle = new Label("Select Dataset For Breeding View");
        //selectDatasetTitle.setStyleName("gcp-content-title");
        
        //studyTreeLayout.addComponent(selectDatasetTitle);
        lblStudyTreeDetailTitle = new Label();
        lblStudyTreeDetailTitle.setStyleName("gcp-content-title");

        Table factors = initializeFactorsTable();
        
        Table variates = initializeVariatesTable();
        
        Table datasets = initializeDatasetTable();
        
        TreeTable tr = createStudyTreeTable(this.database, datasets, factors, variates);
        studyTreeLayout.addComponent(tr);
        
        buttonArea = layoutButtonArea();
        
        studyDatasetDetailLayout.addComponent(datasets);
        
        datasetVariablesDetailLayout.addComponent(factors);
        datasetVariablesDetailLayout.addComponent(variates);
        
        studyDetailsLayout.addComponent(studyDatasetDetailLayout, 0, 0, 2, 0);
        studyDetailsLayout.addComponent(datasetVariablesDetailLayout, 3, 0, 9, 0);
        
        generalLayout.addComponent(studyTreeLayout);
        generalLayout.addComponent(studyDetailsLayout);
        generalLayout.addComponent(buttonArea);
        
        setContent(generalLayout);
        
    }
    
    protected void initializeLayout() {
        
        //generalLayout.setSpacing(true);
        //generalLayout.setMargin(true);
        generalLayout.setComponentAlignment(buttonArea, Alignment.TOP_LEFT);
        
        studyTreeLayout.setSpacing(true);
        studyTreeLayout.setMargin(true);
        
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
        btnCancel.addListener(new CancelDatasetAsInputForBreedingViewAction(this));
        openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this);
        btnNext.addListener(openSelectDatasetForExportAction);

    }
    
    protected Table initializeDatasetTable() {
        
        Table tblDataset = new Table("Datasets Of Selected Study: ");
        tblDataset.setImmediate(true);
        tblDataset.setWidth("100%");
        tblDataset.setHeight("100%");
        tblDataset.setSelectable(true);
        
        BeanContainer<Integer, DatasetReference> container = new BeanContainer<Integer, DatasetReference>(DatasetReference.class);
        container.setBeanIdProperty("id");
        tblDataset.setContainerDataSource(container);
        
        String[] columns = new String[] {"name"};
        String[] columnHeaders = new String[] {"Name"};
        tblDataset.setVisibleColumns(columns);
        tblDataset.setColumnHeaders(columnHeaders);
        return tblDataset;
    }

    protected Table initializeFactorsTable() {
        
        Table tblFactors = new Table("Factors of the Selected Dataset: ");
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
        
        Table tblVariates = new Table("Variates of the Selected Dataset: ");
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
    
    private Table refreshDatasetTable() {
        studyDatasetDetailLayout.removeAllComponents();
        Table datasets = initializeDatasetTable();
        studyDatasetDetailLayout.addComponent(datasets);
        return datasets;
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
    
/*    private Component layoutStudyTreeDetailArea() {
        // layout the tables
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setMargin(false);
        horizontalLayout.setSpacing(true);
        
        horizontalLayout.addComponent(tblDataset);
        horizontalLayout.setExpandRatio(tblDataset, 1.0f);
        
        //tblVariables.setWidth("300px");
        //horizontalLayout.addComponent(tblVariables);
        
        // layout the project detail area
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setMargin(false);
        verticalLayout.setSpacing(true);
        
        verticalLayout.addComponent(lblStudyTreeDetailTitle);
        verticalLayout.addComponent(horizontalLayout);
        
        return verticalLayout;
    }*/

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        btnCancel = new Button();
        btnNext = new Button();

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
    
    private TreeTable createStudyTreeTable(Database database, Table datasets, Table factors, Table variates) {
        
        TreeTable tr = new TreeTable("Study Tree Selection: ");
            
        
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
            cells[1] = fr.getName();
            cells[2] = fr.getId();
            
            tr.addItem(cells, fr);
            
        }
        
        // reserve excess space for the "treecolumn"
        tr.setWidth("100%");
        tr.setColumnExpandRatio("Study Name", 1);
        tr.setSelectable(true);
        
        tr.addListener(new StudyTreeExpandAction(this, tr));
        tr.addListener(new ShowStudyDatasetDetailAction(datasets, factors, variates, this));
        
        return tr;
    }
    
    public void refreshStudyTreeTable(Database database) {
        
        Table variables[] =  refreshFactorsAndVariatesTable();
        Table datasets = refreshDatasetTable();
        
        this.studyTreeLayout.removeAllComponents();
        TreeTable tr = createStudyTreeTable(database, datasets, variables[0], variables[1]);
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
             cells[0] = r.getName();
             cells[1] = r.getId();
             cells[2] = r.getDescription();
             
             if (r instanceof FolderReference) System.out.println("r is FolderReference");
             if (r instanceof StudyReference) System.out.println("r is StudyReference");

				
        	 tr.addItem(cells, r);
        	 tr.setParent(r, parentFolderReference);
        	 if (hasChildStudy(r.getId())) {
                 tr.setChildrenAllowed(r, true);
             } else {
                 tr.setChildrenAllowed(r, false);
             }
         }

         /**
         for (Study sc : studyChildren) {
             
             Object[] cells = new Object[3];
             cells[0] = sc.getName();
             cells[1] = sc.getTitle();
             cells[2] = sc.getObjective();
             
             tr.addItem(cells, sc);

             tr.setParent(sc, parentStudy);
  
             if (hasChildStudy(sc.getId())) {
                 tr.setChildrenAllowed(sc, true);
             } else {
                 tr.setChildrenAllowed(sc, false);
             }
         }
         **/
    	
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
		return studyDataManagerV2;
	}


}

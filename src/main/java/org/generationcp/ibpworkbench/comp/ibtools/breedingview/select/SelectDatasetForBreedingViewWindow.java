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
import org.generationcp.ibpworkbench.actions.DatabaseOptionAction;
import org.generationcp.ibpworkbench.actions.OpenSelectDatasetForExportAction;
import org.generationcp.ibpworkbench.actions.ShowStudyDatasetDetailAction;
import org.generationcp.ibpworkbench.actions.StudyTreeExpandAction;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.RepresentationModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Study;
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
import com.vaadin.ui.OptionGroup;
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
    
    private OptionGroup databaseOption;
    
    private TreeTable tr;
    
    private Table tblDataset;
    
    private Table tblFactors;
    
    private Table tblVariates;
    
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
    
    private StudyDataManager studyDataManager;


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
        
        databaseOption = new OptionGroup("Select Database Location to Query: ");
        databaseOption.addItem(Database.CENTRAL);
        databaseOption.addItem(Database.LOCAL);

        databaseOption.setMultiSelect(false);
        databaseOption.setImmediate(true); 
        databaseOption.select(Database.CENTRAL);

        studyTreeLayout.addComponent(databaseOption);
        
        lblStudyTreeDetailTitle = new Label();
        lblStudyTreeDetailTitle.setStyleName("gcp-content-title");

        initializeFactorsTable();
        
        initializeVariatesTable();
        
        initializeDatasetTable();
        
        createStudyTreeTable(this.database);
        
        buttonArea = layoutButtonArea();
        
        studyDatasetDetailLayout.addComponent(tblDataset);
        
        datasetVariablesDetailLayout.addComponent(tblFactors);
        datasetVariablesDetailLayout.addComponent(tblVariates);
        
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
        
        studyDataManager = managerFactoryProvider.getManagerFactoryForProject(currentProject).getStudyDataManager();
        
    }

    protected void initializeActions() {
        btnCancel.addListener(new CancelDatasetAsInputForBreedingViewAction(this));
        databaseOption.addListener(new DatabaseOptionAction(this));
        openSelectDatasetForExportAction = new OpenSelectDatasetForExportAction(this);
        btnNext.addListener(openSelectDatasetForExportAction);

    }
    
    protected void initializeDatasetTable() {
        
        tblDataset = new Table("Datasets Of Selected Study: ");
        tblDataset.setImmediate(true);
        tblDataset.setWidth("100%");
        tblDataset.setHeight("100%");
        
        BeanContainer<Integer, RepresentationModel> container = new BeanContainer<Integer, RepresentationModel>(RepresentationModel.class);
        container.setBeanIdProperty("id");
        tblDataset.setContainerDataSource(container);
        
        String[] columns = new String[] {"userFriendlyName"};
        String[] columnHeaders = new String[] {"Name"};
        tblDataset.setVisibleColumns(columns);
        tblDataset.setColumnHeaders(columnHeaders);
        
    }

    protected void initializeFactorsTable() {
        
        tblFactors = new Table("Factors of the Selected Dataset: ");
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
        
    }
    
    protected void initializeVariatesTable() {
        
        tblVariates = new Table("Variates of the Selected Dataset: ");
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
        
    }
    
    private void refreshDatasetTable() {
        
        if (tblDataset != null) {
            studyDatasetDetailLayout.removeComponent(tblDataset);
            tblDataset.removeAllItems();
        }
        
        initializeDatasetTable();
        
        studyDatasetDetailLayout.addComponent(tblDataset);
        
    }
    
    private void refreshFactorsTable() {
        
        if (tblFactors != null) {
            datasetVariablesDetailLayout.removeComponent(tblFactors);
            tblFactors.removeAllItems();
        }
        
        initializeFactorsTable();
        
        datasetVariablesDetailLayout.addComponent(tblFactors);
        
    }
    
    private void refreshVariatesTable() {
        
        if (tblVariates != null) {
            datasetVariablesDetailLayout.removeComponent(tblVariates);
            tblVariates.removeAllItems();
        }
        
        initializeVariatesTable();
        
        datasetVariablesDetailLayout.addComponent(tblVariates);
        
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
    
    private void createStudyTreeTable(Database database) {
        
        tr = new TreeTable("Study Tree Selection: ");
            
        
        tr.addContainerProperty("Study Name", String.class, "sname");
        tr.addContainerProperty("Title", String.class, "title");
        tr.addContainerProperty("Description", String.class, "description");

        List<Study> studyParent = new ArrayList<Study>();

        try {
            studyParent = studyDataManager.getAllTopLevelStudies(0, 
                    (int) studyDataManager.countAllTopLevelStudies(this.database), 
                    this.database);
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            if (getWindow() != null){
                MessageNotifier.showWarning(getWindow(), 
                        messageSource.getMessage(Message.ERROR_DATABASE),
                    messageSource.getMessage(Message.ERROR_IN_GETTING_TOP_LEVEL_STUDIES));
            }
            studyParent = new ArrayList<Study>();
        }

        for (Study ps : studyParent) {
            
            Object[] cells = new Object[3];
            cells[0] = ps.getName();
            cells[1] = ps.getTitle();
            cells[2] = ps.getObjective();
            
            tr.addItem(cells, ps);
            
        }
        
        // reserve excess space for the "treecolumn"
        tr.setWidth("100%");
        tr.setColumnExpandRatio("Study Name", 1);
        
        tr.addListener(new StudyTreeExpandAction(this));
        tr.addListener(new ShowStudyDatasetDetailAction(tblDataset, tblFactors, tblVariates, this));
        
        studyTreeLayout.addComponent(tr);
       
    }
    
    public void refreshStudyTreeTable(Database database) {
        
        refreshFactorsTable();
        refreshVariatesTable();
        refreshDatasetTable();
        
        if (tr != null) {
            studyTreeLayout.removeComponent(tr);
            tr.removeAllItems();
        }
        
        createStudyTreeTable(database);

    }
    
    public void queryChildrenStudies(Study parentStudy) throws InternationalizableException{
        List<Study> studyChildren = new ArrayList<Study>();
        try {
            studyChildren = this.studyDataManager.getStudiesByParentFolderID(parentStudy.getId(), 0, 
                    (int) studyDataManager.countAllStudyByParentFolderID(parentStudy.getId(), this.database));
        } catch (MiddlewareQueryException e) {
            //LOG.error(e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
            studyChildren = new ArrayList<Study>();
        }

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
    }
    
    private boolean hasChildStudy(int studyId) {

        List<Study> studyChildren = new ArrayList<Study>();

        try {
            studyChildren = this.studyDataManager.getStudiesByParentFolderID(studyId, 0, 1);
        } catch (MiddlewareQueryException e) {
            //LOG.error(e.toString() + "\n" + e.getStackTrace());
            MessageNotifier.showWarning(getWindow(), 
                    messageSource.getMessage(Message.ERROR_DATABASE), 
                    messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
            studyChildren = new ArrayList<Study>();
        }
        if (!studyChildren.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        
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

}

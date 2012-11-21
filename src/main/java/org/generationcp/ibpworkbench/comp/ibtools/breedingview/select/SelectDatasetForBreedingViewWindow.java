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
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.CancelDatasetAsInputForBreedingViewAction;
import org.generationcp.ibpworkbench.actions.DatabaseOptionAction;
import org.generationcp.ibpworkbench.actions.ShowStudyDatasetDetailAction;
import org.generationcp.ibpworkbench.actions.StudyTreeExpandAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
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
public class SelectDatasetForBreedingViewWindow extends Window implements InitializingBean {

    private static final long serialVersionUID = 1L;
    
    private OptionGroup databaseOption;
    
    private TreeTable tr;
    
    private Table tblDataset;
    
    private Table tblVariables;
    
    private Label lblStudyTreeDetailTitle;
    
    private VerticalLayout verticalLayout;
    
    private VerticalLayout studyTreeLayout;
    
    private VerticalLayout studyDatasetDetailLayout;

    private Label selectDatasetTitle;
    private Button cancelButton;
    private Button selectDatasetButton;
    private Component buttonArea;
    private Component studyTreeDetailArea;
    private Project currentProject;
    private Database database;
    
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
        setWidth("60%");
        /*
         * Center the window both horizontally and vertically in the browser
         * window
         */
        center();
        
        
        
        setCaption("Select Dataset As Input for Breeding View");
        
    }

    protected void initializeComponents() {
        
        verticalLayout = new VerticalLayout();
        
        studyTreeLayout = new VerticalLayout();
        
        studyDatasetDetailLayout = new VerticalLayout();
        
        setContent(verticalLayout);
        
        selectDatasetTitle = new Label("Select Dataset For Breeding View");
        selectDatasetTitle.setStyleName("gcp-content-title");
        
        verticalLayout.addComponent(selectDatasetTitle);
        
        databaseOption = new OptionGroup("Select Database Location to Query: ");
        databaseOption.addItem(Database.CENTRAL);
        databaseOption.addItem(Database.LOCAL);

        databaseOption.setMultiSelect(false);
        databaseOption.setImmediate(true); 

        verticalLayout.addComponent(databaseOption);
        
        lblStudyTreeDetailTitle = new Label();
        lblStudyTreeDetailTitle.setStyleName("gcp-content-title");
        
        verticalLayout.addComponent(studyTreeLayout);
        
        verticalLayout.addComponent(studyDatasetDetailLayout);

        initializeDatasetTable();
        
        createStudyTreeTable(this.database);
        
        buttonArea = layoutButtonArea();
        
        studyTreeDetailArea = layoutStudyTreeDetailArea();
        
        verticalLayout.addComponent(studyTreeDetailArea);
        
        verticalLayout.addComponent(buttonArea);

        
    }
    
    protected void initializeLayout() {
        
        verticalLayout.setSpacing(true);
        
        verticalLayout.setMargin(true);
        
        verticalLayout.setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);

    }
    
    protected void initialize() {
        
        studyDataManager = managerFactoryProvider.getManagerFactoryForProject(currentProject).getStudyDataManager();
        
    }

    protected void initializeActions() {
        cancelButton.addListener(new CancelDatasetAsInputForBreedingViewAction(this));
        databaseOption.addListener(new DatabaseOptionAction(this));
    }
    
    protected void initializeDatasetTable() {
        
        tblDataset = new Table("Datasets Of Selected Study: ");
        tblDataset.setImmediate(true);
        
        BeanContainer<Integer, Representation> container = new BeanContainer<Integer, Representation>(Representation.class);
        container.setBeanIdProperty("id");
        tblDataset.setContainerDataSource(container);
        
        String[] columns = new String[] {"id", "name"};
        tblDataset.setVisibleColumns(columns);
        
    }
    
    private void refreshDatasetTable() {
        
        if (tblDataset != null) {
            studyDatasetDetailLayout.removeComponent(tblDataset);
            tblDataset.removeAllItems();
        }
        
        initializeDatasetTable();
        
        studyDatasetDetailLayout.addComponent(tblDataset);
        
    }
    
    protected void initializeVariablesTable() {
        
        tblVariables = new Table();
        tblVariables.setImmediate(true);
        
        BeanContainer<Integer, Representation> container = new BeanContainer<Integer, Representation>(Representation.class);
        container.setBeanIdProperty("id");
        tblVariables.setContainerDataSource(container);
        
        String[] columns = new String[] {"id", "name"};
        tblVariables.setVisibleColumns(columns);
        
    }
    
    private Component layoutStudyTreeDetailArea() {
        // layout the tables
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setMargin(false);
        horizontalLayout.setSpacing(true);
        
        tblDataset.setWidth("100%");
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
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        selectDatasetButton = new Button("Select Dataset");

        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(selectDatasetButton);

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
            
        
        tr.addContainerProperty("SName", String.class, "sname");
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
        tr.setColumnExpandRatio("SName", 1);
        
        tr.addListener(new StudyTreeExpandAction(this));
        tr.addListener(new ShowStudyDatasetDetailAction(selectDatasetTitle, tblDataset, tblVariables, currentProject));
        
        studyTreeLayout.addComponent(tr);
       
    }
    
    public void refreshStudyTreeTable(Database database) {
        
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
        // TODO Auto-generated method stub
        assemble();
        
    }

}

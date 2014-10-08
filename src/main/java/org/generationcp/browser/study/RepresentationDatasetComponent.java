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

package org.generationcp.browser.study;

import com.vaadin.ui.*;
import org.generationcp.browser.study.containers.RepresentationDatasetQueryFactory;
import org.generationcp.browser.study.listeners.StudyButtonClickListener;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import java.util.ArrayList;
import java.util.List;
/**
 * This class creates the Vaadin Table where a dataset can be displayed.
 * 
 * @author Kevin Manansala
 * 
 */
@Configurable
public class RepresentationDatasetComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private final static Logger LOG = LoggerFactory.getLogger(RepresentationDatasetComponent.class);
    private static final long serialVersionUID = -8476739652987572690L;
    
    public static final String EXPORT_CSV_BUTTON_ID = "RepresentationDatasetComponent Export CSV Button";
    public static final String EXPORT_EXCEL_BUTTON_ID = "RepresentationDatasetComponent Export to FieldBook Excel File Button";
    public static final String OPEN_TABLE_VIEWER_BUTTON_ID = "RepresentationDatasetComponent Open Table Viewer Button";

    private Table datasetTable;
    private String reportName;
    private Integer studyIdHolder;
    private Integer datasetId;

    private Button openTableViewerButton;
    private StringBuffer reportTitle;
    
    private StudyDataManagerImpl studyDataManager;
    
    private boolean fromUrl;                //this is true if this component is created by accessing the Study Details page directly from the URL

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	private boolean h2hCall;
    
    public RepresentationDatasetComponent(StudyDataManagerImpl studyDataManager,
            Integer datasetId, String datasetTitle, Integer studyId, boolean fromUrl,boolean h2hCall) {
        this.reportName = datasetTitle;
        this.studyIdHolder = studyId;
        this.datasetId = datasetId;
        this.studyDataManager = studyDataManager;
        this.fromUrl = fromUrl;
        this.h2hCall=h2hCall;
    }

   
    
    // Called by StudyButtonClickListener
    public void openTableViewerAction() {
        try {
            long expCount = studyDataManager.countExperiments(datasetId);
            if (expCount > 1000) {
                //ask confirmation from user for generating large datasets           
                String confirmDialogCaption=messageSource.getMessage(Message.TABLE_VIEWER_CAPTION);
                String confirmDialogMessage=messageSource.getMessage(Message.CONFIRM_DIALOG_MESSAGE_OPEN_TABLE_VIEWER); 


            } else {
                openTableViewer();
            }
        }  catch (MiddlewareQueryException ex) {
            LOG.error("Error with getting experiments for dataset: " + datasetId + "\n" + ex.toString());
        }
    }
    
    private void openTableViewer() {
    	Window mainWindow = this.getWindow();
    	TableViewerDatasetTable tableViewerDataset = new TableViewerDatasetTable(studyDataManager, studyIdHolder, datasetId);
    	String studyName;
		try {
			studyName = studyDataManager.getStudy(studyIdHolder).getName();
			Window tableViewer = new TableViewerComponent(tableViewerDataset,studyName);
			mainWindow.addWindow(tableViewer);
		} catch (MiddlewareQueryException e) {
			Window tableViewer = new TableViewerComponent(tableViewerDataset);
			mainWindow.addWindow(tableViewer);
		}
    }
    
    
    @Override
    public void afterPropertiesSet() throws Exception{
        
        datasetTable = generateLazyDatasetTable(false);
        
        setMargin(true);
        setSpacing(true);
        addComponent(datasetTable);
        setData(this.reportName);
        setWidth("97%");
        setHeight("97%");
        
        if(!h2hCall){
	       
	        openTableViewerButton = new Button();
	        openTableViewerButton.setData(OPEN_TABLE_VIEWER_BUTTON_ID);
	        openTableViewerButton.addListener(new StudyButtonClickListener(this));
	        
	        HorizontalLayout buttonLayout = new HorizontalLayout();
	        buttonLayout.setSpacing(true);
	        //only show Fieldbook Export to Excel button if study page not accessed directly from URL
	        if (!fromUrl) {
	            buttonLayout.addComponent(openTableViewerButton);
	        }
	
        }
    }
    
    private Table generateLazyDatasetTable(boolean fromUrl) {
    	// set the column header ids
        List<VariableType> variables = new ArrayList<VariableType>();
        List<String> columnIds = new ArrayList<String>();

        try {
            DataSet dataset = studyDataManager.getDataSet(datasetId);
            variables = dataset.getVariableTypes().getVariableTypes();
        } catch (MiddlewareQueryException e) {
            LOG.error("Error in getting variables of dataset: "
                            + datasetId + "\n" + e.toString() + "\n" + e.getStackTrace());
            e.printStackTrace();
            variables = new ArrayList<VariableType>();
            if (getWindow() != null) {
                MessageNotifier.showWarning(getWindow(), 
                        messageSource.getMessage(Message.ERROR_DATABASE), 
                        messageSource.getMessage(Message.ERROR_IN_GETTING_VARIABLES_OF_DATASET)  + " " + datasetId); 
            }
        }
        
        for(VariableType variable : variables)
        {
            if(variable.getStandardVariable().getStoredIn().getId() != TermId.STUDY_INFORMATION.getId()){
                String columnId = new StringBuffer().append(variable.getId()).append("-").append(variable.getLocalName()).toString();
                columnIds.add(columnId);
            }
        }

        // create item container for dataset table
        RepresentationDatasetQueryFactory factory = new RepresentationDatasetQueryFactory(studyDataManager, datasetId, columnIds, fromUrl);
        LazyQueryContainer datasetContainer = new LazyQueryContainer(factory, false, 50);

        // add the column ids to the LazyQueryContainer tells the container the columns to display for the Table
        for (String columnId : columnIds) {
            if (columnId.contains("GID") && !fromUrl) {
                datasetContainer.addContainerProperty(columnId, Link.class, null);
            } else {
                datasetContainer.addContainerProperty(columnId, String.class, null);
            }
        }

        datasetContainer.getQueryView().getItem(0); // initialize the first batch of data to be displayed

        // create the Vaadin Table to display the dataset, pass the container object created
        Table datasetTable = new Table("", datasetContainer);
        datasetTable.setColumnCollapsingAllowed(true);
        datasetTable.setColumnReorderingAllowed(true);
        datasetTable.setPageLength(15); // number of rows to display in the Table
        datasetTable.setSizeFull(); // to make scrollbars appear on the Table component

        // set column headers for the Table
        for (VariableType variable : variables) {
            String columnId = new StringBuffer().append(variable.getId()).append("-").append(variable.getLocalName()).toString();
            String columnHeader = variable.getLocalName();
            datasetTable.setColumnHeader(columnId, columnHeader);
        }
        
        return datasetTable;
    }
    
    @Override
    public void attach() {        
        super.attach();
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(openTableViewerButton, Message.OPEN_TABLE_VIEWER_LABEL);
    }
    
}

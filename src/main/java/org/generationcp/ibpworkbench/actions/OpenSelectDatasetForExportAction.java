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
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.ibtools.breedingview.select.SelectDatasetForBreedingViewPanel;
import org.generationcp.ibpworkbench.ui.ibtools.breedingview.select.SelectDetailsForBreedingViewPanel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.datetime.joda.DateTimeParser;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class OpenSelectDatasetForExportAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenSelectDatasetForExportAction.class);
    
    private SelectDatasetForBreedingViewPanel selectDatasetForBreedingViewPanel; 
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;
    
    public OpenSelectDatasetForExportAction(SelectDatasetForBreedingViewPanel selectDatasetForBreedingViewWindow) {
        
        this.selectDatasetForBreedingViewPanel = selectDatasetForBreedingViewWindow;
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Project project = selectDatasetForBreedingViewPanel.getCurrentProject();

        Integer studyId = selectDatasetForBreedingViewPanel.getCurrentStudy().getId();
        String studyName = selectDatasetForBreedingViewPanel.getCurrentStudy().getName();
        
        Integer dataSetId = selectDatasetForBreedingViewPanel.getCurrentDataSetId();
        String datasetName = selectDatasetForBreedingViewPanel.getCurrentDatasetName();
        
        // study is required
        if (selectDatasetForBreedingViewPanel.getCurrentStudy() == null) {
            event.getComponent().getWindow().showNotification("Please select a Study first.", Notification.TYPE_ERROR_MESSAGE);
            return;
        }
        
        // data set is required
        if (studyId == null 
            //|| studyName == null
            || datasetName == null
            || dataSetId == null) {
            event.getComponent().getWindow().showNotification("Please select a Dataset first.", Notification.TYPE_ERROR_MESSAGE);
            return;
        }
        
        try {
           

            //List of factors from the new schema
            List<VariableType> factorsInDataset = selectDatasetForBreedingViewPanel.getStudyDataManager().getDataSet(dataSetId).getVariableTypes().getFactors().getVariableTypes();
            
            String breedingViewProjectName;
            String defaultFilePath = "";
            String inputDir = "";

            
            //DatasetExporter datasetExporter = new DatasetExporter(selectDatasetForBreedingViewWindow.getStudyDataManager(), studyId, dataSetId);

            Tool breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
            LOG.info(breedingViewTool + "");

            inputDir = toolUtil.getInputDirectoryForTool(project, breedingViewTool);

            LOG.info("Input Directory: " + inputDir);

            breedingViewProjectName = project.getProjectName().trim() + "_" + dataSetId + "_" + datasetName.trim();
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH:mm").format(Calendar.getInstance().getTime());
            String breedingViewAnalysisName = String.format("SSA analysis of %s  (run at %s)", datasetName.trim(),  timeStamp);

            defaultFilePath = File.separator + breedingViewProjectName;

            LOG.info("Default File Path: " + defaultFilePath);

            String sourceCSVFile = inputDir + defaultFilePath + ".csv";

            LOG.info("Source CSV File Path: " + sourceCSVFile);

            //datasetExporter.exportToFieldBookCSVUsingIBDBv2(sourceXLSFilePath, "URRC");

            String destXMLFilePath = inputDir + defaultFilePath + ".xml"; 

            LOG.info("Destination XML File Path: " + destXMLFilePath);

            BreedingViewInput breedingViewInput = new BreedingViewInput(project
                                                                        , breedingViewProjectName
                                                                        , studyId
                                                                        , dataSetId
                                                                        , breedingViewTool.getVersion()
                                                                        , sourceCSVFile
                                                                        , destXMLFilePath
                                                                        , ProjectType.FIELD_TRIAL.getName());
            
            breedingViewInput.setBreedingViewAnalysisName(breedingViewAnalysisName);
            breedingViewInput.setDatasetName(selectDatasetForBreedingViewPanel.getCurrentDatasetName());
            breedingViewInput.setDatasetSource(selectDatasetForBreedingViewPanel.getCurrentStudy().getName());
            breedingViewInput.setVariatesActiveState(selectDatasetForBreedingViewPanel.getVariatesCheckboxState());
            List<DataSet> meansDs = selectDatasetForBreedingViewPanel.getStudyDataManager().getDataSetsByType(studyId, DataSetType.MEANS_DATA);
            if (meansDs != null){
            	if (meansDs.size() > 0){
            		if (meansDs.get(0) != null) breedingViewInput.setOutputDatasetId(meansDs.get(0).getId());
            	}else{
            	 breedingViewInput.setOutputDatasetId(0);
            	}
            }
           
            IContentWindow w = (IContentWindow) event.getComponent().getWindow();
            
            w.showContent( new SelectDetailsForBreedingViewPanel(breedingViewTool, breedingViewInput, factorsInDataset
                    , project, selectDatasetForBreedingViewPanel.getStudyDataManager()));
            
            

        }
        catch (MiddlewareQueryException e) {
            MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
        }
    }
}

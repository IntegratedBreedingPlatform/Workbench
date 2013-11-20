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
    
    private SelectDatasetForBreedingViewPanel selectDatasetForBreedingViewWindow; 
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;
    
    public OpenSelectDatasetForExportAction(SelectDatasetForBreedingViewPanel selectDatasetForBreedingViewWindow) {
        
        this.selectDatasetForBreedingViewWindow = selectDatasetForBreedingViewWindow;
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Project project = selectDatasetForBreedingViewWindow.getCurrentProject();

        Integer studyId = selectDatasetForBreedingViewWindow.getCurrentStudy().getId();
        String studyName = selectDatasetForBreedingViewWindow.getCurrentStudy().getName();
        
        Integer dataSetId = selectDatasetForBreedingViewWindow.getCurrentDataSetId();
        String datasetName = selectDatasetForBreedingViewWindow.getCurrentDatasetName();
        
        Role role = selectDatasetForBreedingViewWindow.getCurrentRole();
        
        // study is required
        if (selectDatasetForBreedingViewWindow.getCurrentStudy() == null) {
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
            List<VariableType> factorsInDataset = selectDatasetForBreedingViewWindow.getStudyDataManager().getDataSet(dataSetId).getVariableTypes().getFactors().getVariableTypes();
            
            String breedingViewProjectName;
            String defaultFilePath = "";
            String inputDir = "";

            
            //DatasetExporter datasetExporter = new DatasetExporter(selectDatasetForBreedingViewWindow.getStudyDataManager(), studyId, dataSetId);

            Tool breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
            LOG.info(breedingViewTool + "");

            inputDir = toolUtil.getInputDirectoryForTool(project, breedingViewTool);

            LOG.info("Input Directory: " + inputDir);

            breedingViewProjectName = project.getProjectName().trim() + "_" + dataSetId + "_" + datasetName.trim();

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
            
            breedingViewInput.setDatasetName(selectDatasetForBreedingViewWindow.getCurrentDatasetName());
            breedingViewInput.setDatasetSource(selectDatasetForBreedingViewWindow.getCurrentStudy().getName());
            
            List<DataSet> meansDs = selectDatasetForBreedingViewWindow.getStudyDataManager().getDataSetsByType(studyId, DataSetType.MEANS_DATA);
            if (meansDs != null){
            	if (meansDs.size() > 0){
            		if (meansDs.get(0) != null) breedingViewInput.setOutputDatasetId(meansDs.get(0).getId());
            	}else{
            	 breedingViewInput.setOutputDatasetId(0);
            	}
            }
           
            IContentWindow w = (IContentWindow) event.getComponent().getWindow();
            
            w.showContent( new SelectDetailsForBreedingViewPanel(breedingViewTool, breedingViewInput, factorsInDataset
                    , project, role));
            
            

        }
        catch (MiddlewareQueryException e) {
            MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
        }
    }
}

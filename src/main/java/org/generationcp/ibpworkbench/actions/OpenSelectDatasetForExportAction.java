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
import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDetailsForBreedingViewWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.ibpworkbench.util.DatasetExporterException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DataSetType;
import org.generationcp.middleware.v2.domain.VariableType;
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
    
    private SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow; 
    
    @Autowired 
    private ManagerFactoryProvider managerFactoryProvider;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;
    
    public OpenSelectDatasetForExportAction(SelectDatasetForBreedingViewWindow selectDatasetForBreedingViewWindow) {
        
        this.selectDatasetForBreedingViewWindow = selectDatasetForBreedingViewWindow;
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Project project = selectDatasetForBreedingViewWindow.getCurrentProject();

        Integer studyId = selectDatasetForBreedingViewWindow.getCurrentStudy().getId();
        String studyName = selectDatasetForBreedingViewWindow.getCurrentStudy().getName();
        
        Integer dataSetId = selectDatasetForBreedingViewWindow.getCurrentDataSetId();
        String datasetName = selectDatasetForBreedingViewWindow.getCurrentDatasetName();
        
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

            
            DatasetExporter datasetExporter = new DatasetExporter(selectDatasetForBreedingViewWindow.getStudyDataManager(), studyId, dataSetId);

            Tool breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
            LOG.info(breedingViewTool + "");

            inputDir = toolUtil.getInputDirectoryForTool(project, breedingViewTool);

            LOG.info("Input Directory: " + inputDir);

            breedingViewProjectName = project.getProjectName().trim() + "_" + dataSetId + "_" + datasetName.trim();

            defaultFilePath = File.separator + breedingViewProjectName;

            LOG.info("Default File Path: " + defaultFilePath);

            String sourceXLSFilePath = inputDir + defaultFilePath + ".xls";

            LOG.info("Source XLS File Path: " + sourceXLSFilePath);

            datasetExporter.exportToFieldBookExcelUsingIBDBv2(sourceXLSFilePath);

            String destXMLFilePath = inputDir + defaultFilePath + ".xml"; 

            LOG.info("Destination XML File Path: " + destXMLFilePath);

            BreedingViewInput breedingViewInput = new BreedingViewInput(project
                                                                        , breedingViewProjectName
                                                                        , studyId
                                                                        , dataSetId
                                                                        , breedingViewTool.getVersion()
                                                                        , sourceXLSFilePath
                                                                        , destXMLFilePath
                                                                        , ProjectType.FIELD_TRIAL.getName());
            
            List<DataSet> meansDs = selectDatasetForBreedingViewWindow.getStudyDataManager().getDataSetsByType(studyId, DataSetType.MEANS_DATA);
            if (meansDs.get(0) != null){
            	 breedingViewInput.setOutputDatasetId(meansDs.get(0).getId());
            }else{
            	 breedingViewInput.setOutputDatasetId(0);
            }
           
            event.getComponent().getWindow().getParent().addWindow( new SelectDetailsForBreedingViewWindow(breedingViewTool, breedingViewInput, factorsInDataset
                    , project) );
            event.getComponent().getWindow().getParent().removeWindow(selectDatasetForBreedingViewWindow);

        }
        catch (DatasetExporterException e) {
            MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
        }
        catch (MiddlewareQueryException e) {
            MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
        }
    }
}

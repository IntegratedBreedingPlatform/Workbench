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

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.breedingview.xml.Replicates;
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
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.workbench.Project;
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
        
        ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
        
        StudyDataManager studyDataManager = managerFactory.getStudyDataManager();
        TraitDataManager traitDataManager = managerFactory.getTraitDataManager();

        Integer studyId = selectDatasetForBreedingViewWindow.getCurrentStudy().getId();
        String studyName = selectDatasetForBreedingViewWindow.getCurrentStudy().getName();
        
        Integer representationId = selectDatasetForBreedingViewWindow.getCurrentRepresentationId();
        String datasetName = selectDatasetForBreedingViewWindow.getCurrentDatasetName();
        
        Blocks blocks = null;
        Replicates replicates = null;
        String environment = null;
        String design = null;
        
        if (selectDatasetForBreedingViewWindow.getCurrentStudy() != null) {
            
            if (studyId != null 
                    && studyName != null
                    && representationId != null 
                    && datasetName != null) {

                try {
                    Trait blockTrait = studyDataManager.getBlockTrait();
                    if(blockTrait != null){
                        Factor blockFactor = studyDataManager.getFactorOfDatasetByTraitid(representationId, blockTrait.getTraitId());
                        if(blockFactor != null){
                            blocks = new Blocks();
                            blocks.setName(blockFactor.getName());
                        }
                    }
        
                    Trait repTrait = studyDataManager.getReplicationTrait();
                    if(repTrait != null){
                        Factor repFactor = studyDataManager.getFactorOfDatasetByTraitid(representationId, repTrait.getTraitId());
                        if(repFactor != null){
                            replicates = new Replicates();
                            replicates.setName(repFactor.getName());
                        }
                    }
                    
                    Trait envTrait = studyDataManager.getEnvironmentTrait();
                    if(envTrait != null){
                        Factor envFactor = studyDataManager.getFactorOfDatasetByTraitid(representationId, envTrait.getTraitId());
                        if(envFactor != null){
                            environment = envFactor.getName();
                        }
                    }
                    
                    Trait desTrait = studyDataManager.getDesignTrait();
                    if(desTrait != null){
                        Factor desFactor = studyDataManager.getFactorOfDatasetByTraitid(representationId, desTrait.getTraitId());
                        if(desFactor != null){
                            design = desFactor.getName();
                        }
                    }
                    
                } catch(MiddlewareQueryException e){
                    MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
                }
                
                String breedingViewProjectName;
                String defaultFilePath = "";
                String inputDir = "";


                DatasetExporter datasetExporter = new DatasetExporter(studyDataManager, traitDataManager, 
                        studyId, 
                        representationId);
                
                try {
                    
                    Tool breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
                    LOG.info(breedingViewTool + "");
                    
                    inputDir = toolUtil.getInputDirectoryForTool(project, breedingViewTool);
                    
                    LOG.info("Input Directory: " + inputDir);
                    
                    breedingViewProjectName = project.getProjectName().trim() + "_" + representationId + "_" + datasetName.trim();
                    
                    defaultFilePath = File.separator + breedingViewProjectName;
                    
                    LOG.info("Default File Path: " + defaultFilePath);
                    
                    String sourceXLSFilePath = inputDir + defaultFilePath + ".xls";
                    
                    LOG.info("Source XLS File Path: " + sourceXLSFilePath);
                    
                    datasetExporter.exportToFieldBookExcel(sourceXLSFilePath);
                    
                    String destXMLFilePath = inputDir + defaultFilePath + ".xml"; 
                    
                    LOG.info("Destination XML File Path: " + destXMLFilePath);
                    
                    BreedingViewInput breedingViewInput = new BreedingViewInput(project
                            , breedingViewProjectName
                            , representationId
                            , environment
                            , breedingViewTool.getVersion()
                            , sourceXLSFilePath
                            , destXMLFilePath
                            , ProjectType.FIELD_TRIAL.getName()
                            , design
                            , blocks
                            , replicates);
                    
                    event.getComponent().getWindow().getParent().addWindow(new SelectDetailsForBreedingViewWindow(breedingViewTool, breedingViewInput));
                    event.getComponent().getWindow().getParent().removeWindow(selectDatasetForBreedingViewWindow);
    
                } catch (DatasetExporterException e) {
                    MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
                } catch (MiddlewareQueryException e) {
                    MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
                }
            
            } else {
                
                event.getComponent().getWindow().showNotification("Please select a Dataset first.", Notification.TYPE_ERROR_MESSAGE);
                
            }


        } else {
          
            event.getComponent().getWindow().showNotification("Please select a Study first.", Notification.TYPE_ERROR_MESSAGE);
            
        }

    }
}

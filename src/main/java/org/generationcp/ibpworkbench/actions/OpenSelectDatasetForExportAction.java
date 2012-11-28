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

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.ibtools.breedingview.select.SelectDatasetForBreedingViewWindow;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.ibpworkbench.util.DatasetExporterException;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.ManagerFactoryProvider;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
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
        Integer studyId = null;
        String studyName = null;
        
        if (selectDatasetForBreedingViewWindow.getCurrentStudy() != null) {
        
            studyId = selectDatasetForBreedingViewWindow.getCurrentStudy().getId();
            studyName = selectDatasetForBreedingViewWindow.getCurrentStudy().getName();
            
        }
        
        Integer representationId = selectDatasetForBreedingViewWindow.getCurrentRepresentationId();
        String datasetName = selectDatasetForBreedingViewWindow.getCurrentDatasetName();
        
                
        if (studyId != null 
                && studyName != null
                && representationId != null 
                && datasetName != null) {
            
            ManagerFactory managerFactory = managerFactoryProvider.getManagerFactoryForProject(project);
            
            StudyDataManager studyDataManager = managerFactory.getStudyDataManager();
            TraitDataManager traitDataManager = managerFactory.getTraitDataManager();
            
            DatasetExporter datasetExporter = new DatasetExporter(studyDataManager, traitDataManager, 
                    selectDatasetForBreedingViewWindow.getCurrentStudy().getId(), 
                    selectDatasetForBreedingViewWindow.getCurrentRepresentationId());
            
            try {
                Tool breedingViewTool = workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
                String inputDir = toolUtil.getInputDirectoryForTool(project, breedingViewTool);
                
                String fileName = inputDir + File.separator + studyId + "_" + studyName.trim() + "_" + representationId + "_" + datasetName.trim() + ".xls";
                
                datasetExporter.exportToFieldBookExcel(fileName);
                
            } catch (DatasetExporterException e) {
                MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
            }
            catch (MiddlewareQueryException e) {
                MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
            }
            
        } else {
          
            event.getComponent().getWindow().showNotification("Please select a Dataset first.", Notification.TYPE_ERROR_MESSAGE);
            
        }

    }
}

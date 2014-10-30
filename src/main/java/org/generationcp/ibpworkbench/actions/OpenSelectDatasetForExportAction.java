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

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.DatasetUtil;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Jeffrey Morales
 *
 */
@Configurable
public class OpenSelectDatasetForExportAction implements ClickListener {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LoggerFactory.getLogger(OpenSelectDatasetForExportAction.class);
    
    private SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel; 
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private ToolUtil toolUtil;
    
    @Autowired
	private SimpleResourceBundleMessageSource messageSource;
    
    public OpenSelectDatasetForExportAction(SingleSiteAnalysisPanel selectDatasetForBreedingViewWindow) {
        
        this.selectDatasetForBreedingViewPanel = selectDatasetForBreedingViewWindow;
        
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Project project = selectDatasetForBreedingViewPanel.getCurrentProject();

        Integer studyId = selectDatasetForBreedingViewPanel.getCurrentStudy().getId();
        Integer dataSetId = selectDatasetForBreedingViewPanel.getCurrentDataSetId();
        String datasetName = selectDatasetForBreedingViewPanel.getCurrentDatasetName();
        
        if(!validateInput(event, studyId, dataSetId, datasetName)) {
        	return;
        }
        	
        try {
           

            //List of factors from the new schema
            List<VariableType> factorsInDataset = selectDatasetForBreedingViewPanel.getStudyDataManager().getDataSet(dataSetId).getVariableTypes().getFactors().getVariableTypes();
            
            String breedingViewProjectName;
            String defaultFilePath = "";
            String inputDir = "";

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

            String destXMLFilePath = inputDir + defaultFilePath + ".xml"; 

            LOG.info("Destination XML File Path: " + destXMLFilePath);

            BreedingViewInput breedingViewInput = new BreedingViewInput();
            breedingViewInput.setProject(project);
            breedingViewInput.setBreedingViewProjectName(breedingViewProjectName);
            breedingViewInput.setStudyId(studyId);
            breedingViewInput.setDatasetId(dataSetId);
            breedingViewInput.setVersion(breedingViewTool.getVersion());
            breedingViewInput.setSourceXLSFilePath(sourceCSVFile);
            breedingViewInput.setDestXMLFilePath(destXMLFilePath);
            breedingViewInput.setProjectType(ProjectType.FIELD_TRIAL.getName());
            breedingViewInput.setBreedingViewAnalysisName(breedingViewAnalysisName);
            breedingViewInput.setDatasetName(selectDatasetForBreedingViewPanel.getCurrentDatasetName());
            breedingViewInput.setDatasetSource(selectDatasetForBreedingViewPanel.getCurrentStudy().getName());
            breedingViewInput.setVariatesActiveState(selectDatasetForBreedingViewPanel.getVariatesCheckboxState());
            List<DataSet> meansDs = selectDatasetForBreedingViewPanel.getStudyDataManager().getDataSetsByType(studyId, DataSetType.MEANS_DATA);
            if (meansDs != null){
            	if (!meansDs.isEmpty()) {
            		breedingViewInput.setOutputDatasetId(meansDs.get(0).getId());
            	} else{
            		breedingViewInput.setOutputDatasetId(0);
            	}
            }
           
            IContentWindow w = (IContentWindow) event.getComponent().getWindow();
            

            List<VariableType> trialVariablesInDataset = null;
            DataSet trialDataset = DatasetUtil.getTrialDataSet(selectDatasetForBreedingViewPanel.getStudyDataManager(), studyId);
            if(trialDataset!=null && trialDataset.getVariableTypes()!=null) {
            	trialVariablesInDataset = trialDataset.getVariableTypes().getVariableTypes();
            }
            w.showContent(new SingleSiteAnalysisDetailsPanel(breedingViewTool, breedingViewInput, factorsInDataset,
                    trialVariablesInDataset, project, selectDatasetForBreedingViewPanel.getStudyDataManager(), selectDatasetForBreedingViewPanel.getManagerFactory(), selectDatasetForBreedingViewPanel));
            
            

        } catch (MiddlewareQueryException e) {
        	LOG.error(e.getMessage(), e);
            MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
        }
    }

	private boolean validateInput(ClickEvent event, Integer studyId, 
			Integer dataSetId, String datasetName ) {
		// study is required
        if (selectDatasetForBreedingViewPanel.getCurrentStudy() == null) {
            event.getComponent().getWindow().showNotification("Please select a Study first.", 
            		Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        
        // data set is required
        if (studyId == null 
            || datasetName == null
            || dataSetId == null) {
            event.getComponent().getWindow().showNotification("Please select a Dataset first.", 
            		Notification.TYPE_ERROR_MESSAGE);
            return false;
        }
        List<VariateModel> variates = selectDatasetForBreedingViewPanel.getVariateList();
        Map<String, Boolean> variatesCheckboxState = selectDatasetForBreedingViewPanel.getVariatesCheckboxState();
        boolean includesNonNumeric = 
        		checkIfNonNumericVarAreIncluded(variates,variatesCheckboxState);
        if(includesNonNumeric) {
        	MessageNotifier.showError(event.getComponent().getWindow(),
        			messageSource.getMessage(Message.INVALID_INPUT),
        			messageSource.getMessage(Message.SSA_NON_NUMERIC_CATEGORICAL_VAR_ERROR));
        	return false;
        }
        boolean includesNumericCategorical = 
        		checkIfNumericCategoricalVarAreIncluded(
        				variates,variatesCheckboxState);
        if(includesNumericCategorical) {
        	MessageNotifier.showWarning(event.getComponent().getWindow(),
        			messageSource.getMessage(Message.WARNING),
        			messageSource.getMessage(Message.SSA_NUMERIC_CATEGORICAL_VAR_WARNING));
        }
		return true;
	}

	protected boolean checkIfNumericCategoricalVarAreIncluded(List<VariateModel> variates,
			Map<String, Boolean> variatesCheckboxState) {
		for (VariateModel vm : variates) {
        	boolean isSelected = variatesCheckboxState.get(vm.getName());
        	if(isSelected && vm.isNumericCategoricalVariate()) {
        		return true;
        	}
        }
        return false;
	}

	protected boolean checkIfNonNumericVarAreIncluded(List<VariateModel> variates,
			Map<String, Boolean> variatesCheckboxState) {
		for (VariateModel vm : variates) {
        	boolean isSelected = variatesCheckboxState.get(vm.getName());
        	if(isSelected && vm.isNonNumeric()) {
        		return true;
        	}
        }
        return false;
	}
}

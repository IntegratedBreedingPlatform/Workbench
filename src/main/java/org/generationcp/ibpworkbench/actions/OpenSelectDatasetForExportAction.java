/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.util.DateUtil;
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
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

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

	private final SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	public OpenSelectDatasetForExportAction(SingleSiteAnalysisPanel selectDatasetForBreedingViewWindow) {

		this.selectDatasetForBreedingViewPanel = selectDatasetForBreedingViewWindow;

	}

	@Override
	public void buttonClick(ClickEvent event) {
		Project project = this.selectDatasetForBreedingViewPanel.getCurrentProject();

		Integer studyId = this.selectDatasetForBreedingViewPanel.getCurrentStudy().getId();
		Integer dataSetId = this.selectDatasetForBreedingViewPanel.getCurrentDataSetId();
		String datasetName = this.selectDatasetForBreedingViewPanel.getCurrentDatasetName();

		if (!this.validateInput(event, studyId, dataSetId, datasetName)) {
			return;
		}

		try {

			// List of factors from the new schema
			List<VariableType> factorsInDataset =
					this.selectDatasetForBreedingViewPanel.getStudyDataManager().getDataSet(dataSetId).getVariableTypes().getFactors()
							.getVariableTypes();

			String breedingViewProjectName;
			String defaultFilePath = "";
			String inputDir = "";

			Tool breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
			OpenSelectDatasetForExportAction.LOG.info(breedingViewTool + "");

			inputDir = this.toolUtil.getInputDirectoryForTool(project, breedingViewTool);

			OpenSelectDatasetForExportAction.LOG.info("Input Directory: " + inputDir);

			breedingViewProjectName = project.getProjectName().trim() + "_" + dataSetId + "_" + datasetName.trim();
			String timeStamp = DateUtil.getCurrentDateAsStringValue("yyyy-MM-dd_HH:mm");

			String breedingViewAnalysisName = String.format("SSA analysis of %s  (run at %s)", datasetName.trim(), timeStamp);

			defaultFilePath = File.separator + breedingViewProjectName;

			OpenSelectDatasetForExportAction.LOG.info("Default File Path: " + defaultFilePath);

			String sourceCSVFile = "";
			if (Boolean.parseBoolean(this.isServerApp)) {
				sourceCSVFile = breedingViewProjectName + ".csv";
			} else {
				sourceCSVFile = inputDir + defaultFilePath + ".csv";
			}

			OpenSelectDatasetForExportAction.LOG.info("Source CSV File Path: " + sourceCSVFile);

			String destXMLFilePath = inputDir + defaultFilePath + ".xml";

			OpenSelectDatasetForExportAction.LOG.info("Destination XML File Path: " + destXMLFilePath);

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
			breedingViewInput.setDatasetName(this.selectDatasetForBreedingViewPanel.getCurrentDatasetName());
			breedingViewInput.setDatasetSource(this.selectDatasetForBreedingViewPanel.getCurrentStudy().getName());
			breedingViewInput.setVariatesActiveState(this.selectDatasetForBreedingViewPanel.getVariatesCheckboxState());
			List<DataSet> meansDs =
					this.selectDatasetForBreedingViewPanel.getStudyDataManager().getDataSetsByType(studyId, DataSetType.MEANS_DATA);
			if (meansDs != null) {
				if (!meansDs.isEmpty()) {
					breedingViewInput.setOutputDatasetId(meansDs.get(0).getId());
				} else {
					breedingViewInput.setOutputDatasetId(0);
				}
			}

			IContentWindow w = (IContentWindow) event.getComponent().getWindow();

			List<VariableType> trialVariablesInDataset = null;
			DataSet trialDataset = DatasetUtil.getTrialDataSet(this.selectDatasetForBreedingViewPanel.getStudyDataManager(), studyId);
			if (trialDataset != null && trialDataset.getVariableTypes() != null) {
				trialVariablesInDataset = trialDataset.getVariableTypes().getVariableTypes();
			}
			w.showContent(new SingleSiteAnalysisDetailsPanel(breedingViewTool, breedingViewInput, factorsInDataset,
					trialVariablesInDataset, project, this.selectDatasetForBreedingViewPanel.getStudyDataManager(),
					this.selectDatasetForBreedingViewPanel.getManagerFactory(), this.selectDatasetForBreedingViewPanel));

		} catch (MiddlewareException e) {
			OpenSelectDatasetForExportAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
		}
	}

	private boolean validateInput(ClickEvent event, Integer studyId, Integer dataSetId, String datasetName) {
		// study is required
		if (this.selectDatasetForBreedingViewPanel.getCurrentStudy() == null) {
			event.getComponent().getWindow().showNotification("Please select a Study first.", Notification.TYPE_ERROR_MESSAGE);
			return false;
		}

		// data set is required
		if (studyId == null || datasetName == null || dataSetId == null) {
			event.getComponent().getWindow().showNotification("Please select a Dataset first.", Notification.TYPE_ERROR_MESSAGE);
			return false;
		}
		List<VariateModel> variates = this.selectDatasetForBreedingViewPanel.getVariateList();
		Map<String, Boolean> variatesCheckboxState = this.selectDatasetForBreedingViewPanel.getVariatesCheckboxState();
		boolean includesNonNumeric = this.checkIfNonNumericVarAreIncluded(variates, variatesCheckboxState);
		if (includesNonNumeric) {
			MessageNotifier.showError(event.getComponent().getWindow(), this.messageSource.getMessage(Message.INVALID_INPUT),
					this.messageSource.getMessage(Message.SSA_NON_NUMERIC_CATEGORICAL_VAR_ERROR));
			return false;
		}
		boolean includesNumericCategorical = this.checkIfNumericCategoricalVarAreIncluded(variates, variatesCheckboxState);
		if (includesNumericCategorical) {
			MessageNotifier.showWarning(event.getComponent().getWindow(), this.messageSource.getMessage(Message.WARNING),
					this.messageSource.getMessage(Message.SSA_NUMERIC_CATEGORICAL_VAR_WARNING));
		}
		return true;
	}

	protected boolean checkIfNumericCategoricalVarAreIncluded(List<VariateModel> variates, Map<String, Boolean> variatesCheckboxState) {
		for (VariateModel vm : variates) {
			boolean isSelected = variatesCheckboxState.get(vm.getName());
			if (isSelected && vm.isNumericCategoricalVariate()) {
				return true;
			}
		}
		return false;
	}

	protected boolean checkIfNonNumericVarAreIncluded(List<VariateModel> variates, Map<String, Boolean> variatesCheckboxState) {
		for (VariateModel vm : variates) {
			boolean isSelected = variatesCheckboxState.get(vm.getName());
			if (isSelected && vm.isNonNumeric()) {
				return true;
			}
		}
		return false;
	}
}

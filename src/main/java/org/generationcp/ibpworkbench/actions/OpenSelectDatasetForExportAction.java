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

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.util.DatasetUtil;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.unbescape.html.HtmlEscape;

import java.io.File;
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

	private final SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private ToolUtil toolUtil;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	@Value("${workbench.is.server.app}")
	private String isServerApp;

	private Project project;

	private Study study;

	private Integer dataSetId;

	private String datasetName;


	public OpenSelectDatasetForExportAction(SingleSiteAnalysisPanel selectDatasetForBreedingViewWindow) {

		this.selectDatasetForBreedingViewPanel = selectDatasetForBreedingViewWindow;

	}

	@Override
	public void buttonClick(ClickEvent event) {

		project = this.selectDatasetForBreedingViewPanel.getCurrentProject();
		study = this.selectDatasetForBreedingViewPanel.getCurrentStudy();
		dataSetId = this.selectDatasetForBreedingViewPanel.getCurrentDataSetId();
		datasetName = this.selectDatasetForBreedingViewPanel.getCurrentDatasetName();

		if (!this.validateInput(event, study.getId(), dataSetId, datasetName)) {
			return;
		}

		try {

			Tool breedingViewTool = this.workbenchDataManager.getToolWithName(ToolName.breeding_view.toString());
			String inputDir = this.toolUtil.getInputDirectoryForTool(project, breedingViewTool);

			// List of factors from the new schema
			List<DMSVariableType> factorsInDataset =
					studyDataManager.getDataSet(dataSetId).getVariableTypes().getFactors()
							.getVariableTypes();

			BreedingViewInput breedingViewInput = new BreedingViewInput();
			breedingViewInput.setProject(project);
			breedingViewInput.setStudyId(study.getId());
			breedingViewInput.setDatasetId(dataSetId);
			breedingViewInput.setVersion(breedingViewTool.getVersion());
			breedingViewInput.setProjectType(ProjectType.FIELD_TRIAL.getName());
			breedingViewInput.setDatasetName(this.datasetName);

			// THe study name is stored in the database as escaped html. We need to decode it to use it properly in generating filenames.
			String studyName = HtmlEscape.unescapeHtml(this.study.getName());
			breedingViewInput.setDatasetSource(studyName);

			// OutputDatasetId is not used anymore in Breeding View web service, but OutputDatasetId attribute should still be included in XML to ensure compatibility.
			breedingViewInput.setOutputDatasetId(0);

			populateProjectNameAndFilePaths(breedingViewInput, project, inputDir);
			populateAnalysisName(breedingViewInput, datasetName);

			breedingViewInput.setVariatesActiveState(this.selectDatasetForBreedingViewPanel.getVariatesCheckboxState());

			IContentWindow w = (IContentWindow) event.getComponent().getWindow();

			List<DMSVariableType> trialVariablesInDataset = null;
			DataSet trialDataset = DatasetUtil.getTrialDataSet(studyDataManager, study.getId());
			if (trialDataset != null && trialDataset.getVariableTypes() != null) {
				trialVariablesInDataset = trialDataset.getVariableTypes().getVariableTypes();
			}
			
			w.showContent(new SingleSiteAnalysisDetailsPanel(breedingViewTool, breedingViewInput, factorsInDataset,
					trialVariablesInDataset, project, 
					this.selectDatasetForBreedingViewPanel));

		} catch (MiddlewareException e) {
			OpenSelectDatasetForExportAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
		}
	}

	void populateProjectNameAndFilePaths(BreedingViewInput breedingViewInput, Project project, String inputDirectory){

		String breedingViewProjectName = "";
		String defaultFilePath = "";

		breedingViewProjectName = project.getProjectName().trim() + "_" + dataSetId + "_" + datasetName.trim();

		breedingViewProjectName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(breedingViewProjectName);

		defaultFilePath = File.separator + breedingViewProjectName;

		breedingViewInput.setBreedingViewProjectName(breedingViewProjectName);

		String sourceCSVFile = "";
		if (Boolean.parseBoolean(this.isServerApp)) {
			sourceCSVFile = breedingViewProjectName + ".csv";
		} else {
			sourceCSVFile = inputDirectory + defaultFilePath + ".csv";
		}

		breedingViewInput.setSourceXLSFilePath(sourceCSVFile);

		String destXMLFilePath = inputDirectory + defaultFilePath + ".xml";

		breedingViewInput.setDestXMLFilePath(destXMLFilePath);

	}


	void populateAnalysisName(final BreedingViewInput breedingViewInput, final String datasetName) {

		String timeStamp = DateUtil.getCurrentDateAsStringValue("yyyy-MM-dd_HH:mm");
		breedingViewInput.setBreedingViewAnalysisName(String.format("SSA analysis of %s  (run at %s)", BreedingViewUtil.sanitizeNameAlphaNumericOnly(datasetName), timeStamp));

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

	public void setDatasetName(final String datasetName) {
		this.datasetName = datasetName;
	}

	public void setDataSetId(final Integer dataSetId) {
		this.dataSetId = dataSetId;
	}

	public void setStudy(final Study study) {
		this.study = study;
	}

	public void setProject(final Project project) {
		this.project = project;
	}
}

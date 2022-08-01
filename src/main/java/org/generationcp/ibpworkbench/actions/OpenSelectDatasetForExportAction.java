/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;
import org.generationcp.commons.breedingview.xml.ProjectType;
import org.generationcp.commons.util.BreedingViewUtil;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.VariableTableItem;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.api.tool.ToolService;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.unbescape.html.HtmlEscape;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author Jeffrey Morales
 */
@Configurable
public class OpenSelectDatasetForExportAction implements ClickListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(OpenSelectDatasetForExportAction.class);

	private final SingleSiteAnalysisPanel selectDatasetForBreedingViewPanel;

	@Autowired
	private ToolService toolService;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();

	private Project project;

	private Study study;

	private Integer dataSetId;

	private String datasetName;

	public OpenSelectDatasetForExportAction(final SingleSiteAnalysisPanel selectDatasetForBreedingViewWindow) {

		this.selectDatasetForBreedingViewPanel = selectDatasetForBreedingViewWindow;

	}

	@Override
	public void buttonClick(final ClickEvent event) {

		this.project = this.selectDatasetForBreedingViewPanel.getCurrentProject();
		this.study = this.selectDatasetForBreedingViewPanel.getCurrentStudy();
		this.dataSetId = this.selectDatasetForBreedingViewPanel.getCurrentDataSetId();
		this.datasetName = this.selectDatasetForBreedingViewPanel.getCurrentDatasetName();

		if (!this.validateInput(event, this.study.getId(), this.dataSetId, this.datasetName)) {
			return;
		}

		try {

			final Tool breedingViewTool = this.toolService.getToolWithName(ToolName.BREEDING_VIEW.getName());
			final String inputDir = this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(this.project, ToolName.BREEDING_VIEW);

			// List of factors from the new schema
			final List<DMSVariableType> factorsInDataset =
				this.studyDataManager.getDataSet(this.dataSetId).getVariableTypes().getFactors().getVariableTypes();

			final BreedingViewInput breedingViewInput = new BreedingViewInput();
			breedingViewInput.setProject(this.project);
			breedingViewInput.setStudyId(this.study.getId());
			breedingViewInput.setDatasetId(this.dataSetId);
			breedingViewInput.setVersion(breedingViewTool.getVersion());
			breedingViewInput.setProjectType(ProjectType.FIELD_TRIAL.getName());
			breedingViewInput.setDatasetName(this.datasetName);
			breedingViewInput.setDescription(this.study.getDescription());
			breedingViewInput.setObjective(this.study.getObjective());

			// THe study name is stored in the database as escaped html. We need to decode it to use it properly in generating filenames.
			final String studyName = HtmlEscape.unescapeHtml(this.study.getName());
			breedingViewInput.setDatasetSource(studyName);

			// OutputDatasetId is not used anymore in Breeding View web service, but OutputDatasetId attribute should still be included in
			// XML to ensure compatibility.
			breedingViewInput.setOutputDatasetId(0);

			this.populateProjectNameAndFilePaths(breedingViewInput, this.project, inputDir);
			this.populateAnalysisName(breedingViewInput, this.datasetName);

			breedingViewInput.setVariatesSelectionMap(this.selectDatasetForBreedingViewPanel.getVariatesSelectionMap());
			breedingViewInput.setCovariatesSelectionMap(this.selectDatasetForBreedingViewPanel.getCovariatesSelectionMap());

			final IContentWindow w = (IContentWindow) event.getComponent().getWindow();

			List<DMSVariableType> trialVariablesInDataset = null;
			final DataSet trialDataset = this.studyDataManager.findOneDataSetByType(this.study.getId(), DatasetTypeEnum.SUMMARY_DATA.getId());
			if (trialDataset != null && trialDataset.getVariableTypes() != null) {
				trialVariablesInDataset = trialDataset.getVariableTypes().getVariableTypes();
			}

			w.showContent(new SingleSiteAnalysisDetailsPanel(breedingViewTool, breedingViewInput, factorsInDataset, trialVariablesInDataset,
				this.project, this.selectDatasetForBreedingViewPanel));

		} catch (final MiddlewareException e) {
			OpenSelectDatasetForExportAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(event.getComponent().getWindow(), e.getMessage(), "");
		}
	}

	void populateProjectNameAndFilePaths(final BreedingViewInput breedingViewInput, final Project project, final String inputDirectory) {

		String breedingViewProjectName = project.getProjectName().trim() + "_" + this.dataSetId + "_" + this.datasetName.trim();

		breedingViewProjectName = BreedingViewUtil.sanitizeNameAlphaNumericOnly(breedingViewProjectName);

		breedingViewInput.setBreedingViewProjectName(breedingViewProjectName);

		final String sourceCSVFile = FileNameGenerator.generateFileName(breedingViewProjectName,"csv");;
		breedingViewInput.setSourceXLSFilePath(sourceCSVFile);

		final String destXMLFilePath = inputDirectory + File.separator + FileNameGenerator.generateFileName(breedingViewProjectName, "xml");
		breedingViewInput.setDestXMLFilePath(destXMLFilePath);

	}

	void populateAnalysisName(final BreedingViewInput breedingViewInput, final String datasetName) {

		final String timeStamp = DateUtil.getCurrentDateAsStringValue("yyyy-MM-dd_HH:mm");
		breedingViewInput.setBreedingViewAnalysisName(
			String.format("SSA analysis of %s  (run at %s)", BreedingViewUtil.sanitizeNameAlphaNumericOnly(datasetName), timeStamp));

	}

	protected boolean validateInput(final ClickEvent event, final Integer studyId, final Integer dataSetId, final String datasetName) {
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

		final List<VariableTableItem> variates = this.selectDatasetForBreedingViewPanel.getVariateList();
		final Map<String, Boolean> variatesCheckboxState = this.selectDatasetForBreedingViewPanel.getVariatesSelectionMap();
		final Map<String, Boolean> covariatesCheckboxState = this.selectDatasetForBreedingViewPanel.getCovariatesSelectionMap();

		final boolean variatesTableIncludesNonNumeric = this.checkIfNonNumericVarAreIncluded(variates, variatesCheckboxState);
		final boolean covariatesTableIncludesNonNumeric = this.checkIfNonNumericVarAreIncluded(variates, covariatesCheckboxState);
		if (variatesTableIncludesNonNumeric || covariatesTableIncludesNonNumeric) {
			MessageNotifier.showError(event.getComponent().getWindow(), this.messageSource.getMessage(Message.INVALID_INPUT),
				this.messageSource.getMessage(Message.SSA_NON_NUMERIC_CATEGORICAL_VAR_ERROR));
			return false;
		}

		final boolean includesNumericCategorical = this.checkIfNumericCategoricalVarAreIncluded(variates, variatesCheckboxState);
		if (includesNumericCategorical) {
			MessageNotifier.showWarning(event.getComponent().getWindow(), this.messageSource.getMessage(Message.WARNING),
				this.messageSource.getMessage(Message.SSA_NUMERIC_CATEGORICAL_VAR_WARNING));
		}
		return true;
	}

	protected boolean checkIfNumericCategoricalVarAreIncluded(
		final List<VariableTableItem> variableTableItems,
		final Map<String, Boolean> variatesCheckboxState) {
		for (final VariableTableItem vm : variableTableItems) {
			final boolean isSelected = variatesCheckboxState.get(vm.getName());
			if (isSelected && vm.isNumericCategoricalVariate()) {
				return true;
			}
		}
		return false;
	}

	protected boolean checkIfNonNumericVarAreIncluded(
		final List<VariableTableItem> variableTableItems, final Map<String, Boolean> variatesCheckboxState) {
		for (final VariableTableItem vm : variableTableItems) {
			if (variatesCheckboxState.containsKey(vm.getName())) {
				final boolean isSelected = variatesCheckboxState.get(vm.getName());
				if (isSelected && vm.isNonNumeric()) {
					return true;
				}
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

	public void setToolService(final ToolService toolService) {
		this.toolService = toolService;
	}

	public void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	public void setInstallationDirectoryUtil(final InstallationDirectoryUtil installationDirectoryUtil) {
		this.installationDirectoryUtil = installationDirectoryUtil;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}

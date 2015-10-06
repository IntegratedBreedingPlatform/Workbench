
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.commons.exceptions.BreedingViewImportException;
import org.generationcp.commons.service.BreedingViewImportService;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputInformation;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputParser;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputParser.ZipFileInvalidContentException;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomFileFactory;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class UploadBreedingViewOutputAction implements ClickListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(UploadBreedingViewOutputAction.class);

	private static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Autowired
	private BreedingViewImportService breedingViewImportService;

	private BMSOutputParser bmsOutputParser;

	private FileUploadBreedingViewOutputWindow window;

	public UploadBreedingViewOutputAction() {

	}

	public UploadBreedingViewOutputAction(FileUploadBreedingViewOutputWindow fileUploadBreedingViewOutputWindow) {

		this.window = fileUploadBreedingViewOutputWindow;
		this.bmsOutputParser = new BMSOutputParser();

	}

	@Override
	public void buttonClick(final ClickEvent event) {

		final int studyId = this.window.getStudyId();
		final Project project = this.window.getProject();

		if (this.isUploadedZipFileValid(studyId, project)) {

			List<Integer> locationIds = new ArrayList<>();
			try {
				locationIds.addAll(this.getLocationIdsBasedOnInformationFromMeansDataFile(studyId, this.bmsOutputParser.getMeansFile()));
			} catch (IOException e) {
				// Do nothing here.
				LOG.error(e.getMessage(), e);
			}

			boolean environmentExists = false;

			if (!locationIds.isEmpty()) {
				environmentExists =
						this.studyDataManager.checkIfAnyLocationIDsExistInExperiments(studyId, DataSetType.MEANS_DATA, locationIds);
			}

			if (environmentExists) {
				ConfirmDialog.show(event.getComponent().getApplication().getMainWindow(), "",
						this.messageSource.getMessage(Message.BV_UPLOAD_OVERWRITE_WARNING), this.messageSource.getMessage(Message.OK),
						this.messageSource.getMessage(Message.CANCEL), new Runnable() {

							@Override
							public void run() {
								UploadBreedingViewOutputAction.this.processTheUploadedFile(event, studyId, project);

							}

						});
			} else {
				this.processTheUploadedFile(event, studyId, project);
			}

		}

	}

	protected boolean isUploadedZipFileValid(final int studyId, final Project project) {

		BMSOutputInformation bmsOutputInformation;

		try {

			this.window.getUploadZip().validate();

		} catch (InvalidValueException e) {
			UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);

			MessageNotifier.showError(this.window.getParent(), this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_INVALID_FORMAT));

			return false;
		}

		try {
			CustomFileFactory uploadZipFileFactory = (CustomFileFactory) this.window.getUploadZip().getFileFactory();
			bmsOutputInformation = this.bmsOutputParser.parseZipFile(uploadZipFileFactory.getFile());
		} catch (ZipFileInvalidContentException e1) {

			MessageNotifier.showError(this.window.getParent(), this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_INVALID_CONTENT));

			return false;
		}

		if (!this.isUploadedZipFileCompatibleWithCurrentStudy(bmsOutputInformation, studyId, project)) {

			MessageNotifier.showError(this.window.getParent(), this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_NOT_COMPATIBLE));

			return false;
		}

		return true;
	}

	protected List<Integer> getLocationIdsBasedOnInformationFromMeansDataFile(int studyId, File meansDataFile) throws IOException {

		List<Integer> locationIds = new ArrayList<>();

		BMSOutputInformation bmsOutputInformation = this.bmsOutputParser.getBmsOutputInformation();

		List<DataSet> datasets = this.studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA);

		if (!datasets.isEmpty()) {
			TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(datasets.get(0).getId());
			Set<TrialEnvironment> trialEnvironmentList = trialEnvironments.getTrialEnvironments();
			for (TrialEnvironment trialEnvironment : trialEnvironmentList) {
				for (String environmentName : bmsOutputInformation.getEnvironmentNames()) {
					if (this.containsValueByLocalName(bmsOutputInformation.getEnvironmentFactorName(), environmentName, trialEnvironment)) {
						locationIds.add(trialEnvironment.getId());
					}
				}
			}
		}

		return locationIds;

	}

	protected boolean containsValueByLocalName(String environmentFactor, String environmentName, TrialEnvironment trialEnvironment) {

		Variable environmentFactorVariable = trialEnvironment.getVariables().findByLocalName(environmentFactor);
		if (environmentFactorVariable != null) {

			// Unfortunately, Breeding View cannot handle double quotes in CSV! It's very likely that the data that we
			// pass to Breeding View has comma in them (e.g. Location Name), so we had to replace the comma with semicolon so that Breeding
			// View will not treat data as multiple fields. Now we have to compare the location name from CSV with the location
			// name from the database, so we have no choice but to replace the comma in location name to properly match it from the data
			// from the CSV file.

			if (environmentName.equals(environmentFactorVariable.getValue().replace(",", ";"))) {
				return true;
			}
		}
		return false;
	}

	protected boolean isUploadedZipFileCompatibleWithCurrentStudy(BMSOutputInformation bmsInformation, int studyId, Project project) {

		if (bmsInformation.getWorkbenchProjectId() != project.getProjectId() || bmsInformation.getStudyId() != studyId) {
			return false;
		} else {
			return true;
		}

	}

	public void processTheUploadedFile(ClickEvent event, int studyId, Project project) {

		Map<String, String> localNameToAliasMap =
				this.generateNameAliasMap(this.bmsOutputParser.getBmsOutputInformation().getInputDataSetId());

		try {

			this.breedingViewImportService.importMeansData(this.bmsOutputParser.getMeansFile(), studyId, localNameToAliasMap);
			this.breedingViewImportService.importSummaryStatsData(this.bmsOutputParser.getSummaryStatsFile(), studyId, localNameToAliasMap);

			if (this.bmsOutputParser.getOutlierFile() != null) {
				this.breedingViewImportService.importOutlierData(this.bmsOutputParser.getOutlierFile(), studyId, localNameToAliasMap);
			}

			MessageNotifier.showMessage(this.window.getParent(), this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_DESCRIPTION));

			this.bmsOutputParser.deleteUploadedZipFile();

			event.getComponent().getWindow().getParent().removeWindow(this.window);

		} catch (BreedingViewImportException e) {

			UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);

			MessageNotifier.showError(this.window.getParent(), this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_MEANS));

		}

		this.bmsOutputParser.deleteTemporaryFiles();

	}

	/**
	 * Breeding View only supports alphanumeric, dash, underscore and percentage characters in trait header names. When we generate the
	 * input file for Breeding View, we replace the invalid characters in trait header names with underscore. We create this map so that BMS
	 * knows the original name of the traits.
	 * 
	 * @param studyId
	 * @return
	 */
	protected Map<String, String> generateNameAliasMap(int dataSetId) {
		Map<String, String> map = new HashMap<>();

		DataSet dataSet = this.studyDataManager.getDataSet(dataSetId);
		VariableTypeList variableTypeList = dataSet.getVariableTypes().getVariates();

		if (variableTypeList.getVariableTypes() != null) {
			for (DMSVariableType variableType : variableTypeList.getVariableTypes()) {

				String nameSanitized =
						variableType.getLocalName().replaceAll(UploadBreedingViewOutputAction.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
				map.put(nameSanitized, variableType.getLocalName());
			}
		}

		return map;

	}

}

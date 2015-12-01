
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
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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
	private BreedingViewImportService breedingViewImportService;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private final BMSOutputParser bmsOutputParser;

	private final FileUploadBreedingViewOutputWindow window;

	public UploadBreedingViewOutputAction() {
		this.window = null;
		this.bmsOutputParser = new BMSOutputParser();
	}

	public UploadBreedingViewOutputAction(final FileUploadBreedingViewOutputWindow fileUploadBreedingViewOutputWindow) {

		this.window = fileUploadBreedingViewOutputWindow;
		this.bmsOutputParser = new BMSOutputParser();

	}

	@Override
	public void buttonClick(final ClickEvent event) {

		final int studyId = this.window.getStudyId();
		final Project project = this.window.getProject();

		if (this.isUploadedZipFileValid(studyId, project)) {

			final List<Integer> locationIds = new ArrayList<>();
			try {
				locationIds.addAll(this.getLocationIdsBasedOnInformationFromMeansDataFile(studyId, this.bmsOutputParser.getMeansFile()));
			} catch (final IOException e) {
				// Do nothing here.
				UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
			}

			boolean environmentExists = false;

			if (!locationIds.isEmpty()) {
				environmentExists =
						this.studyDataManager.checkIfAnyLocationIDsExistInExperiments(studyId, DataSetType.MEANS_DATA, locationIds);
			}

			try {

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

			} catch (final RuntimeException e) {

				UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);

				MessageNotifier.showError(UploadBreedingViewOutputAction.this.window.getParent(),
						UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
						UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_MEANS));
			}

		}

	}

	protected boolean isUploadedZipFileValid(final int studyId, final Project project) {

		BMSOutputInformation bmsOutputInformation;

		try {

			this.window.getUploadZip().validate();

		} catch (final InvalidValueException e) {
			UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);

			MessageNotifier.showError(this.window.getParent(), this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_INVALID_FORMAT));

			return false;
		}

		try {
			final CustomFileFactory uploadZipFileFactory = (CustomFileFactory) this.window.getUploadZip().getFileFactory();
			bmsOutputInformation = this.bmsOutputParser.parseZipFile(uploadZipFileFactory.getFile());
		} catch (final ZipFileInvalidContentException e1) {

			UploadBreedingViewOutputAction.LOG.error(e1.getMessage(), e1);

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

	protected List<Integer> getLocationIdsBasedOnInformationFromMeansDataFile(final int studyId, final File meansDataFile)
			throws IOException {

		final List<Integer> locationIds = new ArrayList<>();

		final BMSOutputInformation bmsOutputInformation = this.bmsOutputParser.getBmsOutputInformation();

		final List<DataSet> datasets = this.studyDataManager.getDataSetsByType(studyId, DataSetType.MEANS_DATA);

		if (!datasets.isEmpty()) {
			final TrialEnvironments trialEnvironments = this.studyDataManager.getTrialEnvironmentsInDataset(datasets.get(0).getId());
			final Set<TrialEnvironment> trialEnvironmentList = trialEnvironments.getTrialEnvironments();
			for (final TrialEnvironment trialEnvironment : trialEnvironmentList) {
				for (final String environmentName : bmsOutputInformation.getEnvironmentNames()) {
					if (this.containsValueByLocalName(bmsOutputInformation.getEnvironmentFactorName(), environmentName, trialEnvironment)) {
						locationIds.add(trialEnvironment.getId());
					}
				}
			}
		}

		return locationIds;

	}

	protected boolean containsValueByLocalName(final String environmentFactor, final String environmentName,
			final TrialEnvironment trialEnvironment) {

		final Variable environmentFactorVariable = trialEnvironment.getVariables().findByLocalName(environmentFactor);
		if (environmentFactorVariable != null) {

			// Unfortunately, Breeding View cannot handle double quotes in CSV! It's very likely that the data that we
			// pass to Breeding View has comma in them (e.g. Location Name), so we had to replace the comma with semicolon so that Breeding
			// View will not treat data as multiple fields. Now we have to compare the location name from CSV with the location
			// name from the database, so we have no choice but to replace the comma in location name to properly match it from the data
			// from the CSV file.

			return environmentName.equals(environmentFactorVariable.getValue().replace(",", ";"));

		}
		return false;
	}

	protected boolean isUploadedZipFileCompatibleWithCurrentStudy(final BMSOutputInformation bmsInformation, final int studyId,
			final Project project) {

		return bmsInformation.getWorkbenchProjectId() == project.getProjectId() && bmsInformation.getStudyId() == studyId;

	}

	public void processTheUploadedFile(final ClickEvent event, final int studyId, final Project project) {

		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus arg0) {
				final Map<String, String> localNameToAliasMap =
						UploadBreedingViewOutputAction.this.generateNameAliasMap(UploadBreedingViewOutputAction.this.bmsOutputParser
								.getBmsOutputInformation().getInputDataSetId());

				try {
					UploadBreedingViewOutputAction.this.breedingViewImportService.importMeansData(
							UploadBreedingViewOutputAction.this.bmsOutputParser.getMeansFile(), studyId, localNameToAliasMap);
					UploadBreedingViewOutputAction.this.breedingViewImportService.importSummaryStatsData(
							UploadBreedingViewOutputAction.this.bmsOutputParser.getSummaryStatsFile(), studyId, localNameToAliasMap);

					if (UploadBreedingViewOutputAction.this.bmsOutputParser.getOutlierFile() != null) {
						UploadBreedingViewOutputAction.this.breedingViewImportService.importOutlierData(
								UploadBreedingViewOutputAction.this.bmsOutputParser.getOutlierFile(), studyId, localNameToAliasMap);
					}

					MessageNotifier.showMessage(UploadBreedingViewOutputAction.this.window.getParent(),
							UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_HEADER),
							UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_DESCRIPTION));

					UploadBreedingViewOutputAction.this.bmsOutputParser.deleteUploadedZipFile();

					event.getComponent().getWindow().getParent().removeWindow(UploadBreedingViewOutputAction.this.window);

				} catch (final BreedingViewImportException e) {

					UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);

					throw new RuntimeException(e);

				} finally {
					UploadBreedingViewOutputAction.this.bmsOutputParser.deleteTemporaryFiles();
				}
			}

		});

	}

	/**
	 * Breeding View only supports alphanumeric, dash, underscore and percentage characters in trait header names. When we generate the
	 * input file for Breeding View, we replace the invalid characters in trait header names with underscore. We create this map so that BMS
	 * knows the original name of the traits.
	 *
	 * @param studyId
	 * @return
	 */
	protected Map<String, String> generateNameAliasMap(final int dataSetId) {
		final Map<String, String> map = new HashMap<>();

		final DataSet dataSet = this.studyDataManager.getDataSet(dataSetId);
		final VariableTypeList variableTypeList = dataSet.getVariableTypes().getVariates();

		if (variableTypeList.getVariableTypes() != null) {
			for (final DMSVariableType variableType : variableTypeList.getVariableTypes()) {

				final String nameSanitized =
						variableType.getLocalName().replaceAll(UploadBreedingViewOutputAction.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
				map.put(nameSanitized, variableType.getLocalName());
			}
		}

		return map;

	}

}

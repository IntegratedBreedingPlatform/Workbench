
package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.apache.commons.lang3.StringUtils;
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
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configurable
public class UploadBreedingViewOutputAction implements ClickListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(UploadBreedingViewOutputAction.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private BreedingViewImportService breedingViewImportService;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private LocationService locationService;

	private BMSOutputParser bmsOutputParser;

	private FileUploadBreedingViewOutputWindow window;

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
			locationIds.addAll(this.getLocationIdsBasedOnInformationFromMeansDataFile(studyId, this.bmsOutputParser.getMeansFile()));

			boolean environmentExists = false;

			if (!locationIds.isEmpty()) {
				environmentExists =
					this.studyDataManager.checkIfAnyLocationIDsExistInExperiments(studyId, DatasetTypeEnum.MEANS_DATA.getId(), locationIds);
			}

			try {
				if (environmentExists) {
					ConfirmDialog.show(event.getComponent().getWindow(), "",
						this.messageSource.getMessage(Message.BV_UPLOAD_OVERWRITE_WARNING), this.messageSource.getMessage(Message.OK),
						this.messageSource.getMessage(Message.CANCEL), new Runnable() {

							@Override
							public void run() {
								UploadBreedingViewOutputAction.this.processTheUploadedFile(studyId, project);
							}

						});
				} else {
					this.processTheUploadedFile(studyId, project);
				}

			} catch (final RuntimeException e) {
				UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
			}

		}

	}

	protected boolean isUploadedZipFileValid(final int studyId, final Project project) {

		final BMSOutputInformation bmsOutputInformation;

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

	protected List<Integer> getLocationIdsBasedOnInformationFromMeansDataFile(final int studyId, final File meansDataFile) {

		final List<Integer> locationIds = new ArrayList<>();

		final BMSOutputInformation bmsOutputInformation = this.bmsOutputParser.getBmsOutputInformation();

		final List<DataSet> datasets = this.studyDataManager.getDataSetsByType(studyId, DatasetTypeEnum.MEANS_DATA.getId());

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

	protected boolean containsValueByLocalName(
		final String environmentFactor, final String environmentName,
		final TrialEnvironment trialEnvironment) {

		final Variable environmentFactorVariable = trialEnvironment.getVariables().findByLocalName(environmentFactor);
		if (environmentFactorVariable != null) {
			//Check if the selected environment factor is the LOCATION_ID and retrieve the Location using the LOCATION_ID value
			if (environmentFactorVariable.getVariableType().getStandardVariable().getId() == TermId.LOCATION_ID.getId()) {
				final Location location = this.locationService.getLocationByID(Integer.valueOf(environmentFactorVariable.getIdValue()));
				return environmentName.equals(location.getLname());
			}

			// Unfortunately, Breeding View cannot handle double quotes in CSV! It's very likely that the data that we
			// pass to Breeding View has comma in them (e.g. Location Name), so we had to replace the comma with semicolon so that Breeding
			// View will not treat data as multiple fields. Now we have to compare the location name from CSV with the location
			// name from the database, so we have no choice but to replace the comma in location name to properly match it from the data
			// from the CSV file.
			return environmentName.equals(environmentFactorVariable.getValue().replace(",", ";"));
		}
		return false;
	}

	protected boolean isUploadedZipFileCompatibleWithCurrentStudy(
		final BMSOutputInformation bmsInformation, final int studyId,
		final Project project) {

		return bmsInformation.getWorkbenchProjectId() == project.getProjectId() && bmsInformation.getStudyId() == studyId;

	}

	public void processTheUploadedFile(final int studyId, final Project project) throws RuntimeException {

		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(final TransactionStatus arg0) {
				try {
					UploadBreedingViewOutputAction.this.breedingViewImportService.importMeansData(
						UploadBreedingViewOutputAction.this.bmsOutputParser.getMeansFile(), studyId);
					UploadBreedingViewOutputAction.this.breedingViewImportService.importSummaryStatisticsData(
						UploadBreedingViewOutputAction.this.bmsOutputParser.getSummaryStatsFile(), studyId);

					if (UploadBreedingViewOutputAction.this.bmsOutputParser.getOutlierFile() != null) {
						UploadBreedingViewOutputAction.this.breedingViewImportService.importOutlierData(
							UploadBreedingViewOutputAction.this.bmsOutputParser.getOutlierFile(), studyId);
					}

					MessageNotifier.showMessage(
						UploadBreedingViewOutputAction.this.window.getParent(),
						UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_HEADER),
						UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_DESCRIPTION));

					UploadBreedingViewOutputAction.this.bmsOutputParser.deleteUploadedZipFile();

					UploadBreedingViewOutputAction.this.window.getParent().removeWindow(UploadBreedingViewOutputAction.this.window);

				} catch (final BreedingViewImportException e) {

					UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
					final StringBuilder detailedError = new StringBuilder();
					detailedError.append(
						UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_MEANS));

					if (StringUtils.isNotEmpty(e.getMessageKey())) {
						detailedError.append("\n");
						detailedError.append(UploadBreedingViewOutputAction.this.messageSource.getMessage(
							e.getMessageKey(), e.getMessageParameters()));
					}

					MessageNotifier.showError(
						UploadBreedingViewOutputAction.this.window.getParent(),
						UploadBreedingViewOutputAction.this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
						detailedError.toString());

					// Wrapping in RuntimeException because TransactionCallbackWithoutResult will only send rollback signal if it is a runtime exception.
					// See http://docs.spring.io/autorepo/docs/spring/3.2.11.RELEASE/javadoc-api/org/springframework/transaction/support/TransactionCallbackWithoutResult.html
					throw new RuntimeException(e);

				} finally {
					UploadBreedingViewOutputAction.this.bmsOutputParser.deleteTemporaryFiles();
				}
			}

		});

	}

	public FileUploadBreedingViewOutputWindow getWindow() {
		return this.window;
	}

	public void setWindow(final FileUploadBreedingViewOutputWindow window) {
		this.window = window;
	}

	public void setBmsOutputParser(final BMSOutputParser bmsOutputParser) {
		this.bmsOutputParser = bmsOutputParser;
	}

}

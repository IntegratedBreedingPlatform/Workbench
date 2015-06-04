
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import org.generationcp.commons.exceptions.BreedingViewImportException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.service.BreedingViewImportService;
import org.generationcp.commons.service.impl.BreedingViewImportServiceImpl;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomFileFactory;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomUploadField;
import org.generationcp.ibpworkbench.util.ZipUtil;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@Configurable
public class UploadBreedingViewOutputAction implements ClickListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(UploadBreedingViewOutputAction.class);

	private static final String REGEX_VALID_BREEDING_VIEW_CHARACTERS = "[^a-zA-Z0-9-_%']+";

	private File meansFile = null;
	private File summaryStatsFile = null;
	private File outlierFile = null;
	private File bmsInformationFile = null;
	private File zipFile = null;

	private int studyId;
	private Project project;

	private CustomUploadField uploadZip;

	private String uploadDirectory = "temp";

	private FileUploadBreedingViewOutputWindow window;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	public UploadBreedingViewOutputAction(FileUploadBreedingViewOutputWindow fileUploadBreedingViewOutputWindow) {
		this.window = fileUploadBreedingViewOutputWindow;
		this.project = this.window.getProject();
		this.studyId = this.window.getStudyId();
		this.uploadZip = this.window.getUploadZip();
	}

	public UploadBreedingViewOutputAction() {

	}

	@Override
	public void buttonClick(ClickEvent event) {

		BreedingViewImportService breedingViewImportService = this.getBreedingViewImportService();

		CustomFileFactory uploadZipFileFactory = (CustomFileFactory) this.uploadZip.getFileFactory();

		Map<String, String> bmsInformation = new HashMap<>();

		StringBuilder importErrorMessage = new StringBuilder();

		Map<String, String> localNameToAliasMap = this.generateNameAliasMap();

		try {
			this.uploadZip.validate();
		} catch (Exception e) {
			UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
			this.showError(this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_INVALID_FORMAT));
			return;
		}

		if (this.window.getUploadZip().hasFileSelected() && this.window.getUploadZip().isValid()) {

			this.zipFile = uploadZipFileFactory.getFile();
			String zipFilePath = this.zipFile.getAbsolutePath();

			this.bmsInformationFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSInformation", this.uploadDirectory);
			this.meansFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSOutput", this.uploadDirectory);
			this.summaryStatsFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSSummary", this.uploadDirectory);
			this.outlierFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSOutlier", this.uploadDirectory);

			bmsInformation = this.parseTxt(this.bmsInformationFile);

		}

		if (this.meansFile == null && this.summaryStatsFile == null && this.outlierFile == null) {
			this.showError(this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
					"The selected output zip file does not contain data.");
			return;
		}

		if (!bmsInformation.isEmpty()) {
			if (!bmsInformation.get("WorkbenchProjectId").equals(this.project.getProjectId().toString())
					|| !bmsInformation.get("StudyId").equals(String.valueOf(this.studyId))) {
				this.showError(this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER),
						"The selected output zip file is not compatible for this study");
				return;
			}
		}

		if (this.meansFile != null) {
			try {
				if (!localNameToAliasMap.isEmpty()) {
					breedingViewImportService.importMeansData(this.meansFile, this.studyId, localNameToAliasMap);
				} else {
					breedingViewImportService.importMeansData(this.meansFile, this.studyId);
				}

			} catch (BreedingViewImportException e) {
				importErrorMessage.append(this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_MEANS));
				UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
			}
		}

		if (this.summaryStatsFile != null) {
			try {

				if (!localNameToAliasMap.isEmpty()) {
					breedingViewImportService.importSummaryStatsData(this.summaryStatsFile, this.studyId, localNameToAliasMap);
				} else {
					breedingViewImportService.importSummaryStatsData(this.summaryStatsFile, this.studyId);
				}

			} catch (BreedingViewImportException e) {
				importErrorMessage.append(this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_SUMMARY));
				UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
			}
		}

		if (this.outlierFile != null) {
			try {
				if (!localNameToAliasMap.isEmpty()) {
					breedingViewImportService.importOutlierData(this.outlierFile, this.studyId, localNameToAliasMap);
				} else {
					breedingViewImportService.importOutlierData(this.outlierFile, this.studyId);
				}

			} catch (BreedingViewImportException e) {
				importErrorMessage.append(this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_OUTLIER));
				UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
			}
		}

		if (!importErrorMessage.toString().isEmpty()) {
			this.showError(this.messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER), importErrorMessage.toString());
		} else {
			this.showMessage(this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_HEADER),
					this.messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_DESCRIPTION));
			this.closeWindow(event);
		}

		this.cleanUp();
	}

	protected void deleteZipFile() {

		if (this.zipFile != null && this.zipFile.exists()) {
			this.zipFile.delete();
		}
	}

	protected void cleanUp() {

		this.deleteZipFile();

		if (this.meansFile != null && this.meansFile.exists()) {
			this.meansFile.delete();
		}
		if (this.summaryStatsFile != null && this.summaryStatsFile.exists()) {
			this.summaryStatsFile.delete();
		}
		if (this.outlierFile != null && this.outlierFile.exists()) {
			this.outlierFile.delete();
		}
		if (this.bmsInformationFile != null && this.bmsInformationFile.exists()) {
			this.bmsInformationFile.delete();
		}

	}

	protected Map<String, String> generateNameAliasMap() {
		Map<String, String> map = new HashMap<>();

		Map<String, Boolean> variates = this.window.getVariatesStateMap();

		if (variates != null) {
			for (Entry<String, Boolean> entry : variates.entrySet()) {
				if (entry.getValue()) {
					String nameSanitized =
							entry.getKey().replaceAll(UploadBreedingViewOutputAction.REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
					map.put(nameSanitized, entry.getKey());
				}
			}
		}

		return map;

	}

	public Map<String, String> parseTxt(File file) {
		Map<String, String> result = new HashMap<String, String>();

		if (file == null) {
			return result;
		}

		Scanner scanner = null;
		try {
			scanner = new Scanner(new FileReader(file));
		} catch (FileNotFoundException e) {
			UploadBreedingViewOutputAction.LOG.error(e.getMessage(), e);
			return result;
		}

		try {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (!line.startsWith("#")) {
					String[] mapping = line.split("=");
					result.put(mapping[0], mapping[1]);
				}
			}
		} finally {
			scanner.close();
		}
		return result;
	}

	protected void closeWindow(ClickEvent event) {
		event.getComponent().getWindow().getParent().removeWindow(this.window);
	}

	protected BreedingViewImportServiceImpl getBreedingViewImportService() {
		return new BreedingViewImportServiceImpl(this.project, this.managerFactoryProvider);
	}

	protected void showError(String caption, String description) {
		MessageNotifier.showError(this.window.getParent(), caption, description);
	}

	protected void showMessage(String caption, String description) {
		MessageNotifier.showMessage(this.window.getParent(), caption, description);
	}

	protected String getUploadDirectory() {
		return this.uploadDirectory;
	}

	protected void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	protected void setStudyId(int studyId) {
		this.studyId = studyId;
	}

	protected void setProject(Project project) {
		this.project = project;
	}

}

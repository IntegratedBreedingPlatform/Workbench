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
    
    public UploadBreedingViewOutputAction(FileUploadBreedingViewOutputWindow fileUploadBreedingViewOutputWindow){
    	this.window = fileUploadBreedingViewOutputWindow;
    	this.project = window.getProject();
    	this.studyId = window.getStudyId();
    	this.uploadZip = window.getUploadZip();
    }

	public UploadBreedingViewOutputAction() {
		
	}

	@Override
	public void buttonClick(ClickEvent event) {
		
			BreedingViewImportService breedingViewImportService = getBreedingViewImportService();
			
			CustomFileFactory uploadZipFileFactory = (CustomFileFactory) uploadZip.getFileFactory();
		
			Map<String, String> bmsInformation = new HashMap<>();
			
			StringBuilder importErrorMessage = new StringBuilder();
			
			Map<String, String> localNameToAliasMap = generateNameAliasMap();
			
			try{
				uploadZip.validate();
			}catch(Exception e){
				LOG.error(e.getMessage(), e);
				showError(messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER), messageSource.getMessage(Message.BV_UPLOAD_ERROR_INVALID_FORMAT));
				return;
			}
			
			if (window.getUploadZip().hasFileSelected() && window.getUploadZip().isValid()){
				
				zipFile = uploadZipFileFactory.getFile();
				String zipFilePath = zipFile.getAbsolutePath();		
				
				bmsInformationFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSInformation", uploadDirectory);
				meansFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSOutput", uploadDirectory);
				summaryStatsFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSSummary", uploadDirectory);
				outlierFile = ZipUtil.extractZipSpecificFile(zipFilePath, "BMSOutlier", uploadDirectory);
				
				bmsInformation = parseTxt(bmsInformationFile);
				
			}
			
			if (meansFile == null && summaryStatsFile == null && outlierFile == null){
				showError(messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER), "The selected output zip file does not contain data.");
				return;
			}
			
			if (!bmsInformation.isEmpty()){
				if (!bmsInformation.get("WorkbenchProjectId").equals(project.getProjectId().toString())
					|| !bmsInformation.get("StudyId").equals(String.valueOf(studyId))){
					showError(messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER), "The selected output zip file is not compatible for this study");
					return;
				}
			}
			
			if (meansFile!=null){
				try {
					if (!localNameToAliasMap.isEmpty()){
						breedingViewImportService.importMeansData(meansFile, studyId, localNameToAliasMap);
					}else{
						breedingViewImportService.importMeansData(meansFile, studyId);
					}
					
				} catch (BreedingViewImportException e) {
					importErrorMessage.append(messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_MEANS));
					LOG.error(e.getMessage(), e);
				}
			}
				
			if (summaryStatsFile!=null){
				try {
					
					if (!localNameToAliasMap.isEmpty()){
						breedingViewImportService.importSummaryStatsData(summaryStatsFile, studyId, localNameToAliasMap);
					}else{
						breedingViewImportService.importSummaryStatsData(summaryStatsFile, studyId);
					}
					
				} catch (BreedingViewImportException e) {
					importErrorMessage.append(messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_SUMMARY));
					LOG.error(e.getMessage(), e);
				}
			}
			
			if(outlierFile!=null){
				try {
					if (!localNameToAliasMap.isEmpty()){
						breedingViewImportService.importOutlierData(outlierFile, studyId, localNameToAliasMap);
					}else{
						breedingViewImportService.importOutlierData(outlierFile, studyId);
					}
					
				} catch (BreedingViewImportException e) {
					importErrorMessage.append(messageSource.getMessage(Message.BV_UPLOAD_ERROR_CANNOT_UPLOAD_OUTLIER));
					LOG.error(e.getMessage(), e);
				}
			}
			
			
				
			if (!importErrorMessage.toString().isEmpty()){
				showError(messageSource.getMessage(Message.BV_UPLOAD_ERROR_HEADER), importErrorMessage.toString());
			}else{
				showMessage(messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_HEADER), messageSource.getMessage(Message.BV_UPLOAD_SUCCESSFUL_DESCRIPTION));
				closeWindow(event);
			}
				
			cleanUp();
		}
	
		protected void deleteZipFile(){
			
			if (zipFile!=null && zipFile.exists()){
				zipFile.delete();
			}
		}

		protected void cleanUp(){
			
			deleteZipFile();
			
			if (meansFile!=null && meansFile.exists()){
				meansFile.delete();
			}
			if (summaryStatsFile!=null && summaryStatsFile.exists()){
				summaryStatsFile.delete();
			}
			if (outlierFile!=null && outlierFile.exists()){
				outlierFile.delete();
			}
			if (bmsInformationFile!=null && bmsInformationFile.exists()){
				bmsInformationFile.delete();
			}
		
		}
		
		protected Map<String, String> generateNameAliasMap(){
			Map<String, String> map = new HashMap<>();
			
			Map<String, Boolean> variates = window.getVariatesStateMap();
			
			if (variates!=null){
				for (Entry<String, Boolean> entry : variates.entrySet()){
					if (entry.getValue()){
						String nameSanitized = entry.getKey().replaceAll(REGEX_VALID_BREEDING_VIEW_CHARACTERS, "_");
						map.put(nameSanitized, entry.getKey());
					}
				}
			}
			
			return map;
			
		}
		
		public Map<String, String> parseTxt(File file) {
			Map<String, String> result = new HashMap<String, String>();
			
			if (file == null){
				return result;
			}
			
			Scanner scanner = null;
			try {
				scanner = new Scanner(new FileReader(file));
			} catch (FileNotFoundException e) {
				LOG.error(e.getMessage(), e);
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
			event.getComponent().getWindow().getParent().removeWindow(window);
		}
		
		protected BreedingViewImportServiceImpl getBreedingViewImportService(){
			return new BreedingViewImportServiceImpl(project, managerFactoryProvider);
		}
		
		protected void showError(String caption, String description){
			MessageNotifier.showError(window.getParent(), caption, description);
		}
		
		protected void showMessage(String caption, String description){
			MessageNotifier.showMessage(window.getParent(), caption, description);
		}

		protected String getUploadDirectory() {
			return uploadDirectory;
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
package org.generationcp.ibpworkbench.actions;

import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.generationcp.commons.exceptions.BreedingViewImportException;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.service.impl.BreedingViewImportServiceImpl;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomFileFactory;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomUploadField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button.ClickEvent;

public class UploadBreedingViewOutputActionTest {

	@Mock
	private FileUploadBreedingViewOutputWindow window;
	
	@Mock
    private SimpleResourceBundleMessageSource messageSource;
    
	@Mock
    private ManagerFactoryProvider managerFactoryProvider;
	
	@Mock
	private CustomUploadField uploadZip;
	
	@Mock
	private ClickEvent event;
	
	@Mock
	BreedingViewImportServiceImpl breedingViewImportService;
	
	@InjectMocks
	UploadBreedingViewOutputAction uploadBreedingViewOutputAction = Mockito.spy(new UploadBreedingViewOutputAction());
	
	@Before
	public void setUp(){
		
		MockitoAnnotations.initMocks(this);
		
		doReturn(breedingViewImportService).when(uploadBreedingViewOutputAction).getBreedingViewImportService();
		doReturn(uploadZip).when(window).getUploadZip();
		
		doNothing().when(uploadBreedingViewOutputAction).showError(anyString(), anyString());
		doNothing().when(uploadBreedingViewOutputAction).showMessage(anyString(), anyString());
		doNothing().when(uploadBreedingViewOutputAction).closeWindow(event);
		doNothing().when(uploadBreedingViewOutputAction).deleteZipFile();
		
	}
	
	@Test
	public void testUploadInvalidFileOrNoFileSelected(){
		
		doThrow(new Validator.InvalidValueException("NOT_VALID")).when(uploadZip).validate();
		
		uploadBreedingViewOutputAction.buttonClick(event);
		
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).showError(anyString(), anyString());
	}
	
	@Test
	public void testUploadZipHasNoContent() throws URISyntaxException{
		
		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("zipToExtractNoContent.zip").toURI());
		CustomFileFactory factory = mock(CustomFileFactory.class);
		
		doReturn(true).when(uploadZip).hasFileSelected();
		doReturn(true).when(uploadZip).isValid();
		doReturn(factory).when(uploadZip).getFileFactory();
		doReturn(zipFile).when(factory).getFile();
		
		uploadBreedingViewOutputAction.buttonClick(event);
		
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).showError(anyString(), anyString());
	}
	
	@Test
	public void testUploadZipOutputZipFileDoesNotMatchTheTargetProjectAndStudy() throws URISyntaxException, BreedingViewImportException{
		
		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		CustomFileFactory factory = mock(CustomFileFactory.class);
		
		doReturn(true).when(uploadZip).hasFileSelected();
		doReturn(true).when(uploadZip).isValid();
		
		doReturn(factory).when(uploadZip).getFileFactory();
		doReturn(zipFile).when(factory).getFile();
		doReturn(generateInvalidBMSInformation()).when(uploadBreedingViewOutputAction).parseTxt(any(File.class));
		
		uploadBreedingViewOutputAction.setProject(createProject());
		uploadBreedingViewOutputAction.setStudyId(-23);
		
		String uploadDirectory = ClassLoader.getSystemResource("").getPath();
		uploadBreedingViewOutputAction.setUploadDirectory(uploadDirectory);
		uploadBreedingViewOutputAction.buttonClick(event);
		
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).showError(anyString(), anyString());
		
		
	}
	
	@Test
	public void testUploadZipHasValidContentWithDefaultNameToAliasMap() throws URISyntaxException, BreedingViewImportException{
		
		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		CustomFileFactory factory = mock(CustomFileFactory.class);
		
		doReturn(true).when(uploadZip).hasFileSelected();
		doReturn(true).when(uploadZip).isValid();
		
		doReturn(factory).when(uploadZip).getFileFactory();
		doReturn(zipFile).when(factory).getFile();
		doReturn(generateBMSInformation()).when(uploadBreedingViewOutputAction).parseTxt(any(File.class));
		
		uploadBreedingViewOutputAction.setProject(createProject());
		uploadBreedingViewOutputAction.setStudyId(-23);
		
		String uploadDirectory = ClassLoader.getSystemResource("").getPath();
		uploadBreedingViewOutputAction.setUploadDirectory(uploadDirectory);
		uploadBreedingViewOutputAction.buttonClick(event);
		
		Mockito.verify(breedingViewImportService, times(1)).importMeansData(any(File.class), anyInt());
		Mockito.verify(breedingViewImportService, times(1)).importSummaryStatsData(any(File.class), anyInt());
		Mockito.verify(breedingViewImportService, times(1)).importOutlierData(any(File.class), anyInt());
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).showMessage(anyString(), anyString());
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).closeWindow(event);
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).cleanUp();
	}
	
	@Test
	public void testUploadZipHasValidContentWithSpecifiedNameToAliasMap() throws URISyntaxException, BreedingViewImportException{
		
		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		CustomFileFactory factory = mock(CustomFileFactory.class);
		
		doReturn(true).when(uploadZip).hasFileSelected();
		doReturn(true).when(uploadZip).isValid();
		
		doReturn(factory).when(uploadZip).getFileFactory();
		doReturn(zipFile).when(factory).getFile();
		doReturn(generateBMSInformation()).when(uploadBreedingViewOutputAction).parseTxt(any(File.class));
		doReturn(generateLocalNameToAliasMap()).when(uploadBreedingViewOutputAction).generateNameAliasMap();
		
		uploadBreedingViewOutputAction.setProject(createProject());
		uploadBreedingViewOutputAction.setStudyId(-23);
		
		String uploadDirectory = ClassLoader.getSystemResource("").getPath();
		uploadBreedingViewOutputAction.setUploadDirectory(uploadDirectory);
		uploadBreedingViewOutputAction.buttonClick(event);
		
		Mockito.verify(breedingViewImportService, times(1)).importMeansData(any(File.class), anyInt(), anyMap());
		Mockito.verify(breedingViewImportService, times(1)).importSummaryStatsData(any(File.class), anyInt(), anyMap());
		Mockito.verify(breedingViewImportService, times(1)).importOutlierData(any(File.class), anyInt(), anyMap());
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).showMessage(anyString(), anyString());
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).closeWindow(event);
		Mockito.verify(uploadBreedingViewOutputAction, times(1)).cleanUp();
	}
	
	private Map<String,String> generateBMSInformation(){
		Map<String, String> map = new HashMap<>();
		map.put("InputDataSetId", "-25");
		map.put("OutputDataSetId", "-26");
		map.put("StudyId", "-23");
		map.put("WorkbenchProjectId", "12");
		return map;
	}
	
	private Map<String,String> generateInvalidBMSInformation(){
		Map<String, String> map = new HashMap<>();
		map.put("InputDataSetId", "-25");
		map.put("OutputDataSetId", "-26");
		map.put("StudyId", "999");
		map.put("WorkbenchProjectId", "999");
		return map;
	}
	
	private Project createProject(){
		Project project = new Project();
		project.setProjectId(12L);
		return project;
	}
	
	private Map<String, String> generateLocalNameToAliasMap(){
		Map<String, String> localNameToAliasMapping = new HashMap<>();
		localNameToAliasMapping.put("TRIAL_INSTANCE", "TRIAL_INSTANCE");
		localNameToAliasMapping.put("ENTRY_NO", "ENTRY_NO");
		localNameToAliasMapping.put("GID", "GID");
		localNameToAliasMapping.put("ASI", "ASI");
		localNameToAliasMapping.put("Aphid1_5", "Aphid1_5");
		localNameToAliasMapping.put("EPH", "EPH");
		localNameToAliasMapping.put("FMSROT", "FMSROT");
		return localNameToAliasMapping;
	}
	
	
}

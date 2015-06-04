
package org.generationcp.ibpworkbench.actions;

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
import org.mockito.Matchers;
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
	public void setUp() {

		MockitoAnnotations.initMocks(this);

		Mockito.doReturn(this.breedingViewImportService).when(this.uploadBreedingViewOutputAction).getBreedingViewImportService();
		Mockito.doReturn(this.uploadZip).when(this.window).getUploadZip();

		Mockito.doNothing().when(this.uploadBreedingViewOutputAction).showError(Matchers.anyString(), Matchers.anyString());
		Mockito.doNothing().when(this.uploadBreedingViewOutputAction).showMessage(Matchers.anyString(), Matchers.anyString());
		Mockito.doNothing().when(this.uploadBreedingViewOutputAction).closeWindow(this.event);
		Mockito.doNothing().when(this.uploadBreedingViewOutputAction).deleteZipFile();

	}

	@Test
	public void testUploadInvalidFileOrNoFileSelected() {

		Mockito.doThrow(new Validator.InvalidValueException("NOT_VALID")).when(this.uploadZip).validate();

		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).showError(Matchers.anyString(), Matchers.anyString());
	}

	@Test
	public void testUploadZipHasNoContent() throws URISyntaxException {

		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("zipToExtractNoContent.zip").toURI());
		CustomFileFactory factory = Mockito.mock(CustomFileFactory.class);

		Mockito.doReturn(true).when(this.uploadZip).hasFileSelected();
		Mockito.doReturn(true).when(this.uploadZip).isValid();
		Mockito.doReturn(factory).when(this.uploadZip).getFileFactory();
		Mockito.doReturn(zipFile).when(factory).getFile();

		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).showError(Matchers.anyString(), Matchers.anyString());
	}

	@Test
	public void testUploadZipOutputZipFileDoesNotMatchTheTargetProjectAndStudy() throws URISyntaxException, BreedingViewImportException {

		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		CustomFileFactory factory = Mockito.mock(CustomFileFactory.class);

		Mockito.doReturn(true).when(this.uploadZip).hasFileSelected();
		Mockito.doReturn(true).when(this.uploadZip).isValid();

		Mockito.doReturn(factory).when(this.uploadZip).getFileFactory();
		Mockito.doReturn(zipFile).when(factory).getFile();
		Mockito.doReturn(this.generateInvalidBMSInformation()).when(this.uploadBreedingViewOutputAction).parseTxt(Matchers.any(File.class));

		this.uploadBreedingViewOutputAction.setProject(this.createProject());
		this.uploadBreedingViewOutputAction.setStudyId(-23);

		String uploadDirectory = ClassLoader.getSystemResource("").getPath();
		this.uploadBreedingViewOutputAction.setUploadDirectory(uploadDirectory);
		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).showError(Matchers.anyString(), Matchers.anyString());

	}

	@Test
	public void testUploadZipHasValidContentWithDefaultNameToAliasMap() throws URISyntaxException, BreedingViewImportException {

		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		CustomFileFactory factory = Mockito.mock(CustomFileFactory.class);

		Mockito.doReturn(true).when(this.uploadZip).hasFileSelected();
		Mockito.doReturn(true).when(this.uploadZip).isValid();

		Mockito.doReturn(factory).when(this.uploadZip).getFileFactory();
		Mockito.doReturn(zipFile).when(factory).getFile();
		Mockito.doReturn(this.generateBMSInformation()).when(this.uploadBreedingViewOutputAction).parseTxt(Matchers.any(File.class));

		this.uploadBreedingViewOutputAction.setProject(this.createProject());
		this.uploadBreedingViewOutputAction.setStudyId(-23);

		String uploadDirectory = ClassLoader.getSystemResource("").getPath();
		this.uploadBreedingViewOutputAction.setUploadDirectory(uploadDirectory);
		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.breedingViewImportService, Mockito.times(1)).importMeansData(Matchers.any(File.class), Matchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(1))
				.importSummaryStatsData(Matchers.any(File.class), Matchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(1)).importOutlierData(Matchers.any(File.class), Matchers.anyInt());
		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).showMessage(Matchers.anyString(), Matchers.anyString());
		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).closeWindow(this.event);
		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).cleanUp();
	}

	@Test
	public void testUploadZipHasValidContentWithSpecifiedNameToAliasMap() throws URISyntaxException, BreedingViewImportException {

		File zipFile = new File(ClassLoader.getSystemClassLoader().getResource("BMSOutput.zip").toURI());
		CustomFileFactory factory = Mockito.mock(CustomFileFactory.class);

		Mockito.doReturn(true).when(this.uploadZip).hasFileSelected();
		Mockito.doReturn(true).when(this.uploadZip).isValid();

		Mockito.doReturn(factory).when(this.uploadZip).getFileFactory();
		Mockito.doReturn(zipFile).when(factory).getFile();
		Mockito.doReturn(this.generateBMSInformation()).when(this.uploadBreedingViewOutputAction).parseTxt(Matchers.any(File.class));
		Mockito.doReturn(this.generateLocalNameToAliasMap()).when(this.uploadBreedingViewOutputAction).generateNameAliasMap();

		this.uploadBreedingViewOutputAction.setProject(this.createProject());
		this.uploadBreedingViewOutputAction.setStudyId(-23);

		String uploadDirectory = ClassLoader.getSystemResource("").getPath();
		this.uploadBreedingViewOutputAction.setUploadDirectory(uploadDirectory);
		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.breedingViewImportService, Mockito.times(1)).importMeansData(Matchers.any(File.class), Matchers.anyInt(),
				Matchers.anyMap());
		Mockito.verify(this.breedingViewImportService, Mockito.times(1)).importSummaryStatsData(Matchers.any(File.class),
				Matchers.anyInt(), Matchers.anyMap());
		Mockito.verify(this.breedingViewImportService, Mockito.times(1)).importOutlierData(Matchers.any(File.class), Matchers.anyInt(),
				Matchers.anyMap());
		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).showMessage(Matchers.anyString(), Matchers.anyString());
		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).closeWindow(this.event);
		Mockito.verify(this.uploadBreedingViewOutputAction, Mockito.times(1)).cleanUp();
	}

	private Map<String, String> generateBMSInformation() {
		Map<String, String> map = new HashMap<>();
		map.put("InputDataSetId", "-25");
		map.put("OutputDataSetId", "-26");
		map.put("StudyId", "-23");
		map.put("WorkbenchProjectId", "12");
		return map;
	}

	private Map<String, String> generateInvalidBMSInformation() {
		Map<String, String> map = new HashMap<>();
		map.put("InputDataSetId", "-25");
		map.put("OutputDataSetId", "-26");
		map.put("StudyId", "999");
		map.put("WorkbenchProjectId", "999");
		return map;
	}

	private Project createProject() {
		Project project = new Project();
		project.setProjectId(12L);
		return project;
	}

	private Map<String, String> generateLocalNameToAliasMap() {
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

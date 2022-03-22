
package org.generationcp.ibpworkbench.actions;

import com.vaadin.Application;
import com.vaadin.data.Validator;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.commons.exceptions.BreedingViewImportException;
import org.generationcp.commons.service.BreedingViewImportService;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputInformation;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputParser;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.BMSOutputParser.ZipFileInvalidContentException;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomFileFactory;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow.CustomUploadField;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.LocationTestDataInitializer;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class UploadBreedingViewOutputActionTest {

	private static final int LOCATION_ID1 = 1;
	private static final int LOCATION_ID2 = 2;

	private static final int ASI_TERMID = 1234;
	private static final int PLT_HEIGHT_TERMID = 7653;
	private static final int TWG_TERMID = 4567;

	private static final int TEST_STUDY_ID = 2;
	private static final long TEST_PROJECT_ID = 1L;
	private static final int TEST_MEANS_DATASET_ID = 3;

	@Mock
	private FileUploadBreedingViewOutputWindow fileUploadBreedingViewOutputWindow;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private CustomUploadField uploadZip;

	@Mock
	private File zipFile;

	@Mock
	private CustomFileFactory customFileFactory;

	@Mock
	private BMSOutputParser bmsOutputParser;

	@Mock
	private ClickEvent event;

	@Mock
	private Window window;

	@Mock
	private BreedingViewImportService breedingViewImportService;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private Component component;

	@Mock
	private Window parentWindow;

	@Mock
	private Application application;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private LocationDataManager locationDataManager;

	@InjectMocks
	private final UploadBreedingViewOutputAction uploadBreedingViewOutputAction = new UploadBreedingViewOutputAction();

	@Before
	public void setUp() {
		this.uploadBreedingViewOutputAction.setWindow(this.fileUploadBreedingViewOutputWindow);

		final Project project = this.createProject();

		Mockito.when(this.fileUploadBreedingViewOutputWindow.getProject()).thenReturn(project);
		Mockito.when(this.fileUploadBreedingViewOutputWindow.getStudyId()).thenReturn(UploadBreedingViewOutputActionTest.TEST_STUDY_ID);
		Mockito.when(this.fileUploadBreedingViewOutputWindow.getUploadZip()).thenReturn(this.uploadZip);
		Mockito.when(this.fileUploadBreedingViewOutputWindow.getParent()).thenReturn(this.window);
		Mockito.when(this.event.getComponent()).thenReturn(this.component);
	}

	@Test
	public void testButtonClickUploadedZipIsInvalidFileOrNoFileSelected() {

		Mockito.doThrow(new Validator.InvalidValueException(FileUploadBreedingViewOutputWindow.NOT_VALID)).when(this.uploadZip).validate();

		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.messageSource).getMessage(Message.BV_UPLOAD_ERROR_INVALID_FORMAT);

	}

	@Test
	public void testButtonClickUploadZipHasNoContent() throws URISyntaxException, ZipFileInvalidContentException {

		Mockito.when(this.uploadZip.getFileFactory()).thenReturn(this.customFileFactory);
		Mockito.when(this.customFileFactory.getFile()).thenReturn(this.zipFile);
		Mockito.when(this.bmsOutputParser.parseZipFile(this.zipFile)).thenThrow(new ZipFileInvalidContentException());

		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.messageSource).getMessage(Message.BV_UPLOAD_ERROR_INVALID_CONTENT);

	}

	@Test
	public void testButtonClickUploadZipOutputZipFileDoesNotMatchTheTargetProjectAndStudy() throws URISyntaxException,
		BreedingViewImportException, ZipFileInvalidContentException {

		Mockito.when(this.fileUploadBreedingViewOutputWindow.getStudyId()).thenReturn(55);
		Mockito.when(this.uploadZip.getFileFactory()).thenReturn(this.customFileFactory);
		Mockito.when(this.customFileFactory.getFile()).thenReturn(this.zipFile);

		final BMSOutputInformation bmsInformation = this.createBmsOutputInformation();
		bmsInformation.setWorkbenchProjectId(99);
		Mockito.when(this.bmsOutputParser.parseZipFile(this.zipFile)).thenReturn(bmsInformation);

		ClassLoader.getSystemResource("").getPath();

		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.messageSource).getMessage(Message.BV_UPLOAD_ERROR_NOT_COMPATIBLE);

	}

	@Test
	public void testButtonClickUploadSuccessful() throws URISyntaxException, BreedingViewImportException, ZipFileInvalidContentException,
		IOException {

		Mockito.when(this.uploadZip.getFileFactory()).thenReturn(this.customFileFactory);
		Mockito.when(this.customFileFactory.getFile()).thenReturn(this.zipFile);

		final BMSOutputInformation bmsOutputInformation = this.createBmsOutputInformation();
		Mockito.when(this.bmsOutputParser.parseZipFile(this.zipFile)).thenReturn(bmsOutputInformation);
		Mockito.when(this.bmsOutputParser.getBmsOutputInformation()).thenReturn(bmsOutputInformation);
		Mockito.when(this.bmsOutputParser.getMeansFile()).thenReturn(Mockito.mock(File.class));
		Mockito.when(this.bmsOutputParser.getSummaryStatsFile()).thenReturn(Mockito.mock(File.class));
		Mockito.when(this.bmsOutputParser.getOutlierFile()).thenReturn(Mockito.mock(File.class));

		Mockito.when(this.studyDataManager.getDataSetsByType(UploadBreedingViewOutputActionTest.TEST_STUDY_ID, DatasetTypeEnum.MEANS_DATA.getId()))
			.thenReturn(this.createDataSetList());
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(UploadBreedingViewOutputActionTest.TEST_MEANS_DATASET_ID))
			.thenReturn(this.createEnvironments());

		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.breedingViewImportService, Mockito.times(1))
			.importMeansData(ArgumentMatchers.any(File.class), ArgumentMatchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(1)).importSummaryStatisticsData(
			ArgumentMatchers.any(File.class),
			ArgumentMatchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(1))
			.importOutlierData(ArgumentMatchers.any(File.class), ArgumentMatchers.anyInt());

		Mockito.verify(this.bmsOutputParser).deleteUploadedZipFile();
		Mockito.verify(this.bmsOutputParser).deleteTemporaryFiles();

		Mockito.verify(this.messageSource).getMessage(Message.BV_UPLOAD_SUCCESSFUL_HEADER);
		Mockito.verify(this.window).removeWindow(this.fileUploadBreedingViewOutputWindow);
	}

	@Test
	public void testButtonClickMeansDataAlreadyExists() throws ZipFileInvalidContentException, IOException {

		Mockito.when(this.uploadZip.getFileFactory()).thenReturn(this.customFileFactory);
		Mockito.when(this.customFileFactory.getFile()).thenReturn(this.zipFile);

		final BMSOutputInformation bmsOutputInformation = this.createBmsOutputInformation();
		Mockito.when(this.bmsOutputParser.parseZipFile(this.zipFile)).thenReturn(bmsOutputInformation);
		Mockito.when(this.bmsOutputParser.getBmsOutputInformation()).thenReturn(bmsOutputInformation);
		Mockito.when(this.bmsOutputParser.getMeansFile()).thenReturn(Mockito.mock(File.class));

		Mockito.when(this.studyDataManager.getDataSetsByType(UploadBreedingViewOutputActionTest.TEST_STUDY_ID, DatasetTypeEnum.MEANS_DATA.getId()))
			.thenReturn(this.createDataSetList());
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(UploadBreedingViewOutputActionTest.TEST_MEANS_DATASET_ID))
			.thenReturn(this.createEnvironments());
		Mockito.when(
			this.studyDataManager.checkIfAnyLocationIDsExistInExperiments(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(),
				ArgumentMatchers.<List<Integer>>any())).thenReturn(true);

		Mockito.when(this.messageSource.getMessage(Message.BV_UPLOAD_OVERWRITE_WARNING)).thenReturn("");
		Mockito.when(this.messageSource.getMessage(Message.OK)).thenReturn("");
		Mockito.when(this.messageSource.getMessage(Message.CANCEL)).thenReturn("");

		this.uploadBreedingViewOutputAction.buttonClick(this.event);

		Mockito.verify(this.messageSource).getMessage(Message.BV_UPLOAD_OVERWRITE_WARNING);

	}

	@Test
	public void testProcessTheUploadedFileOutlierFileNotAvailable() throws BreedingViewImportException {

		Mockito.when(this.bmsOutputParser.getMeansFile()).thenReturn(Mockito.mock(File.class));
		Mockito.when(this.bmsOutputParser.getSummaryStatsFile()).thenReturn(Mockito.mock(File.class));
		Mockito.when(this.bmsOutputParser.getOutlierFile()).thenReturn(null);

		Assert.assertEquals(this.fileUploadBreedingViewOutputWindow, this.uploadBreedingViewOutputAction.getWindow());
		this.uploadBreedingViewOutputAction.processTheUploadedFile(
			UploadBreedingViewOutputActionTest.TEST_STUDY_ID,
			this.fileUploadBreedingViewOutputWindow.getProject());

		Mockito.verify(this.breedingViewImportService, Mockito.times(1))
			.importMeansData(ArgumentMatchers.any(File.class), ArgumentMatchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(1)).importSummaryStatisticsData(
			ArgumentMatchers.any(File.class),
			ArgumentMatchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(0))
			.importOutlierData(ArgumentMatchers.any(File.class), ArgumentMatchers.anyInt());

		Mockito.verify(this.bmsOutputParser).deleteUploadedZipFile();
		Mockito.verify(this.bmsOutputParser).deleteTemporaryFiles();

		Mockito.verify(this.messageSource).getMessage(Message.BV_UPLOAD_SUCCESSFUL_HEADER);
		Mockito.verify(this.window).removeWindow(this.fileUploadBreedingViewOutputWindow);
	}

	@Test(expected = RuntimeException.class)
	public void testProcessTheUploadedFileFailed() throws BreedingViewImportException {

		Mockito.when(this.bmsOutputParser.getMeansFile()).thenReturn(Mockito.mock(File.class));

		Mockito.doThrow(new BreedingViewImportException()).when(this.breedingViewImportService)
			.importMeansData(ArgumentMatchers.any(File.class), ArgumentMatchers.anyInt());

		this.uploadBreedingViewOutputAction.processTheUploadedFile(
			UploadBreedingViewOutputActionTest.TEST_STUDY_ID,
			this.fileUploadBreedingViewOutputWindow.getProject());

		Mockito.verify(this.breedingViewImportService, Mockito.times(1))
			.importMeansData(ArgumentMatchers.any(File.class), ArgumentMatchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(0)).importSummaryStatisticsData(
			ArgumentMatchers.any(File.class),
			ArgumentMatchers.anyInt());
		Mockito.verify(this.breedingViewImportService, Mockito.times(0))
			.importOutlierData(ArgumentMatchers.any(File.class), ArgumentMatchers.anyInt());

	}

	@Test
	public void testContainsValueByLocalName() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.TRIAL_INSTANCE_FACTOR, "1");
		final TrialEnvironment environment = Mockito.mock(TrialEnvironment.class);
		final VariableList variableList = Mockito.mock(VariableList.class);
		Mockito.when(environment.getVariables()).thenReturn(variableList);
		Mockito.when(variableList.findByLocalName("TRIAL_INSTANCE")).thenReturn(variable);

		boolean containsValue = this.uploadBreedingViewOutputAction.containsValueByLocalName("TRIAL_INSTANCE", "1", environment);
		Assert.assertTrue(containsValue);

		containsValue = this.uploadBreedingViewOutputAction.containsValueByLocalName("TRIAL_INSTANCE", "2", environment);
		Assert.assertFalse(containsValue);
	}

	@Test
	public void testContainsValueByLocalNameForLocationName() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LOCATION_ID, "1");
		final TrialEnvironment environment = Mockito.mock(TrialEnvironment.class);
		final VariableList variableList = Mockito.mock(VariableList.class);
		Mockito.when(environment.getVariables()).thenReturn(variableList);
		Mockito.when(variableList.findByLocalName("LOCATION_NAME")).thenReturn(variable);
		Mockito.when(this.locationDataManager.getLocationByID(1)).thenReturn(LocationTestDataInitializer.createLocation(1, "WARDA"));

		boolean containsValue = this.uploadBreedingViewOutputAction.containsValueByLocalName("LOCATION_NAME", "WARDA", environment);
		Assert.assertTrue(containsValue);

		containsValue = this.uploadBreedingViewOutputAction.containsValueByLocalName("LOCATION_NAME", "CIMMYT", environment);
		Assert.assertFalse(containsValue);
	}

	@Test
	public void testGetLocationIdsBasedOnInformationFromMeansDataFile() throws IOException {

		Mockito.when(this.bmsOutputParser.getBmsOutputInformation()).thenReturn(this.createBmsOutputInformation());
		Mockito.when(this.studyDataManager.getDataSetsByType(UploadBreedingViewOutputActionTest.TEST_STUDY_ID, DatasetTypeEnum.MEANS_DATA.getId()))
			.thenReturn(this.createDataSetList());
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(UploadBreedingViewOutputActionTest.TEST_MEANS_DATASET_ID))
			.thenReturn(this.createEnvironments());

		final List<Integer> locationIds =
			this.uploadBreedingViewOutputAction.getLocationIdsBasedOnInformationFromMeansDataFile(
				UploadBreedingViewOutputActionTest.TEST_STUDY_ID, Mockito.mock(File.class));

		Assert.assertEquals("The test means data has only one trial instance", 1, locationIds.size());
		Assert.assertEquals(UploadBreedingViewOutputActionTest.LOCATION_ID1, locationIds.get(0).intValue());

	}

	private Project createProject() {
		final Project project = new Project();
		project.setProjectId(UploadBreedingViewOutputActionTest.TEST_PROJECT_ID);
		return project;
	}

	private TrialEnvironments createEnvironments() {

		final TrialEnvironments environments = new TrialEnvironments();

		final VariableList variableList = new VariableList();
		variableList.add(this.createVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), "TRIAL_INSTANCE", "1"));

		final TrialEnvironment environment = new TrialEnvironment(UploadBreedingViewOutputActionTest.LOCATION_ID1, variableList);
		environments.add(environment);

		final VariableList variableList2 = new VariableList();
		variableList2.add(this.createVariable(TermId.TRIAL_INSTANCE_FACTOR.getId(), "TRIAL_INSTANCE", "2"));

		final TrialEnvironment environment1 = new TrialEnvironment(UploadBreedingViewOutputActionTest.LOCATION_ID2, variableList2);
		environments.add(environment1);

		return environments;
	}

	private List<DataSet> createDataSetList() {
		final List<DataSet> dataSets = new ArrayList<>();
		final DataSet dataSet = new DataSet();
		dataSet.setId(UploadBreedingViewOutputActionTest.TEST_MEANS_DATASET_ID);
		dataSets.add(dataSet);
		return dataSets;
	}

	private Variable createVariable(final int termId, final String localName, final String value) {
		final Variable variable = new Variable();
		variable.setVariableType(this.createDMSVariableType(termId, localName));
		variable.setValue(value);
		return variable;
	}

	private DMSVariableType createDMSVariableType(final int termId, final String localName) {

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setStandardVariable(this.createStandardVariable(termId, localName));
		dmsVariableType.setLocalName(localName);

		return dmsVariableType;
	}

	private StandardVariable createStandardVariable(final int termId, final String name) {
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(termId);
		standardVariable.setName(name);
		return standardVariable;
	}

	private VariableTypeList createVariateVariableList() {
		final VariableTypeList variableTypeList = new VariableTypeList();
		variableTypeList.add(this.createDMSVariableType(UploadBreedingViewOutputActionTest.ASI_TERMID, "ASI"));
		variableTypeList.add(this.createDMSVariableType(UploadBreedingViewOutputActionTest.PLT_HEIGHT_TERMID, "PLANT HEIGHT"));
		variableTypeList.add(this.createDMSVariableType(UploadBreedingViewOutputActionTest.TWG_TERMID, "TWG?"));
		return variableTypeList;
	}

	private BMSOutputInformation createBmsOutputInformation() {

		final BMSOutputInformation bmsOutputInformation = new BMSOutputInformation();
		bmsOutputInformation.setInputDataSetId(3);
		bmsOutputInformation.setOutputDataSetId(4);
		bmsOutputInformation.setStudyId(2);
		bmsOutputInformation.setWorkbenchProjectId(1);

		final Set<String> environmentNames = new HashSet<>();
		environmentNames.add("1");

		bmsOutputInformation.setEnvironmentFactorName("TRIAL_INSTANCE");
		bmsOutputInformation.setEnvironmentNames(environmentNames);
		return bmsOutputInformation;
	}

}

package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.generationcp.commons.breedingview.xml.Trait;
import org.generationcp.commons.gxe.xml.GxeEnvironment;
import org.generationcp.commons.sea.xml.Environment;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.util.ZipUtil;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.GxeTable;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.MultiSiteDataExporter;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

import junit.framework.Assert;

@RunWith(MockitoJUnitRunner.class)
public class RunMultiSiteActionTest {

	private static final String[] TEST_TRAITS = {"TWG_100", "AleuCol_15", "Plant_Height"};
	private static final String ENVIRONMENT_GROUP_NAME = "MegaEnvironment";
	private static final String ENVIRONMENT_NAME = "SITE";
	private static final String GENOTYPE_NAME = "GID";
	private static final String PROJECT_NAME = "TEST MAIZE PROGRAM";
	private static final String MEANS_DATASET_NAME = "TEST STUDY-MEANS";
	private static final String SUMMARY_DATASET_NAME = "TEST STUDY-SUMMARY";
	private static final String TEST_SPECIAL_CHARACTERS = "\\ &amp;/:*?'\"<>|[]{},.?~`!@#$%^&()-=_+111";
	private static final int STUDY_ID = 98;
	private static final int MEANS_DATASET_ID = 99;
	private static final String BMS_INPUT_FILES_DIR = "/someDirectory/input";
	private static final String SUMMARY_DATA_FILEPATH = BMS_INPUT_FILES_DIR + "summary.csv";
	private static final String MEANS_DATA_FILEPATH = BMS_INPUT_FILES_DIR + "means.csv";
	private static final String STUDY_NAME = "TEST STUDY";
	private static final String ZIP_FILE_PATH = "/someDirectory/output/" + STUDY_NAME + ".zip";
	

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private GxeTable gxeTable;

	@Mock
	private MultiSiteDataExporter multiSiteDataExporter;

	@Mock
	private Tool breedingViewTool;

	@Mock
	private ZipUtil zipUtil;

	@Mock
	private IBPWorkbenchApplication workbenchApplication;

	@Mock
	Window window;
	
	@Mock
	private InstallationDirectoryUtil installationDirectoryUtil;
	
	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private final RunMultiSiteAction runMultiSiteAction = new RunMultiSiteAction();
	
	@Captor
	private ArgumentCaptor<List<String>> filesInZipCaptor;

	private List<Trait> selectedTraits;
	private GxeEnvironment gxeEnvironment;
	private MultiSiteParameters multiSiteParameters;

	@Before
	public void init() throws IOException {

		final Table selectTraitsTable = this.createTestSelectedTraitsTable();
		runMultiSiteAction.setSelectTraitsTable(selectTraitsTable);
		runMultiSiteAction.setBreedingViewTool(breedingViewTool);
		
		selectedTraits = runMultiSiteAction.getSelectedTraits();
		gxeEnvironment = this.createGxeEnvironment();
		multiSiteParameters = this.createMultiSiteParameters();
		runMultiSiteAction.setMultiSiteParameters(multiSiteParameters);

		this.initMocks();

	}

	void initMocks() throws IOException {

		Mockito.when(this.workbenchApplication.getMainWindow()).thenReturn(window);

		Mockito.when(this.gxeTable.getSelectedEnvironments()).thenReturn(this.createEnvironmentList());
		Mockito.when(this.gxeTable.getEnvironmentName()).thenReturn(ENVIRONMENT_NAME);
		Mockito.when(this.gxeTable.getMeansDataSet()).thenReturn(this.createMeansDataSet(MEANS_DATASET_NAME));
		Mockito.when(this.gxeTable.getGxeEnvironment()).thenReturn(this.gxeEnvironment);

		Mockito.when(this.installationDirectoryUtil.getInputDirectoryForProjectAndTool(this.createProject(PROJECT_NAME), ToolName.BREEDING_VIEW))
				.thenReturn(BMS_INPUT_FILES_DIR);

		Mockito.when(this.studyDataManager.getDataSetsByType(STUDY_ID, DataSetType.SUMMARY_DATA)).thenReturn(this.createDataSets());

		Mockito.when(multiSiteDataExporter.exportMeansDatasetToCsv(Mockito.anyString(), Mockito.any(MultiSiteParameters.class),
				Mockito.anyList(), Mockito.eq(ENVIRONMENT_NAME), Mockito.any(GxeEnvironment.class), Mockito.anyList()))
				.thenReturn(MEANS_DATA_FILEPATH);

		Mockito.when(multiSiteDataExporter.exportTrialDatasetToSummaryStatsCsv(Mockito.anyInt(), Mockito.anyString(), Mockito.anyList(),
				Mockito.eq(ENVIRONMENT_NAME), Mockito.anyList(), Mockito.any(Project.class))).thenReturn(SUMMARY_DATA_FILEPATH);
		
		Mockito.when(this.zipUtil.zipIt(Mockito.anyString(), Mockito.anyListOf(String.class), Mockito.any(Project.class),
				Mockito.any(ToolName.class))).thenReturn(ZIP_FILE_PATH);

	}

	@Test
	public void testButtonClickServerAppIsTrue() throws IOException {
		final Project project = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();
		runMultiSiteAction.setIsServerApp(true);

		runMultiSiteAction.buttonClick(Mockito.mock(Button.ClickEvent.class));

		// Make sure that the expected files are compressed in zip
		final ArgumentCaptor<String> filenameCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
		final ArgumentCaptor<ToolName> toolCaptor = ArgumentCaptor.forClass(ToolName.class);
		Mockito.verify(this.zipUtil).zipIt(filenameCaptor.capture(), filesInZipCaptor.capture(), projectCaptor.capture(),
				toolCaptor.capture());
		Assert.assertEquals(STUDY_NAME, filenameCaptor.getValue());
		Assert.assertEquals(project, projectCaptor.getValue());
		final List<String> filesInZip = filesInZipCaptor.getValue();
		Assert.assertEquals(3, filesInZip.size());
		Assert.assertTrue(filesInZip.contains(SUMMARY_DATA_FILEPATH));
		Assert.assertTrue(filesInZip.contains(MEANS_DATA_FILEPATH));
		Assert.assertTrue(filesInZip.contains(BMS_INPUT_FILES_DIR + File.separator + getExpectedBVInputXmlFilename() + ".xml"));
		Assert.assertEquals(ToolName.BV_GXE, toolCaptor.getValue());
		
		// Verify zip file is downloaded to the browser with proper filename
		final ArgumentCaptor<VaadinFileDownloadResource> fileDownloadResourceCaptor = ArgumentCaptor.forClass(VaadinFileDownloadResource.class);
		Mockito.verify(this.window).open(fileDownloadResourceCaptor.capture());
		final VaadinFileDownloadResource downloadResource = fileDownloadResourceCaptor.getValue();
		Assert.assertEquals(new File(ZIP_FILE_PATH).getAbsolutePath(), downloadResource.getSourceFile().getAbsolutePath());
		Assert.assertEquals(STUDY_NAME + ".zip", downloadResource.getFilename());
	}

	@Test
	public void testExportMultiSiteProjectFile() {

		final GxeInput gxeInput = runMultiSiteAction.createGxeInput(multiSiteParameters, gxeEnvironment, selectedTraits);

		runMultiSiteAction.exportMultiSiteProjectFile(multiSiteParameters, gxeInput);

		Mockito.verify(multiSiteDataExporter).generateXmlFieldBook(gxeInput);
		Mockito.verify(this.installationDirectoryUtil).getInputDirectoryForProjectAndTool(this.multiSiteParameters.getProject(),
				ToolName.BREEDING_VIEW);

		Assert.assertEquals(BMS_INPUT_FILES_DIR + File.separator + PROJECT_NAME + "_0_TEST STUDY-MEANS.xml", gxeInput.getDestXMLFilePath());

	}

	@Test
	public void testExportDataFiles() {

		final GxeInput gxeInput = runMultiSiteAction.createGxeInput(multiSiteParameters, gxeEnvironment, selectedTraits);

		this.runMultiSiteAction.exportDataFiles(multiSiteParameters, gxeInput, gxeEnvironment, selectedTraits);

		// Make sure the Means DataSet is exported to CSV
		Mockito.verify(multiSiteDataExporter).exportMeansDatasetToCsv(Mockito.anyString(), Mockito.eq(multiSiteParameters),
				Mockito.anyList(), Mockito.eq(ENVIRONMENT_NAME), Mockito.any(GxeEnvironment.class), Mockito.anyList());

		// Make sure the Summary Data is exported to CSV
		Mockito.verify(multiSiteDataExporter).exportTrialDatasetToSummaryStatsCsv(Mockito.anyInt(), Mockito.anyString(), Mockito.anyList(),
				Mockito.eq(ENVIRONMENT_NAME), Mockito.anyList(), Mockito.any(Project.class));

		Assert.assertEquals(MEANS_DATA_FILEPATH, gxeInput.getSourceCSVFilePath());
		Assert.assertEquals(SUMMARY_DATA_FILEPATH, gxeInput.getSourceCSVSummaryStatsFilePath());

	}

	@Test
	public void testGenerateInputFileName() {

		final String result = runMultiSiteAction.generateInputFileName(this.createProject(PROJECT_NAME));

		Assert.assertEquals(getExpectedBVInputXmlFilename(), result);

	}

	private String getExpectedBVInputXmlFilename() {
		return PROJECT_NAME + "_0_" + MEANS_DATASET_NAME;
	}

	@Test
	public void testGenerateInputFileNameWithSpecialCharacters() {

		Mockito.when(this.gxeTable.getMeansDataSet()).thenReturn(this.createMeansDataSet(MEANS_DATASET_NAME + TEST_SPECIAL_CHARACTERS));
		final String result = runMultiSiteAction.generateInputFileName(this.createProject(PROJECT_NAME));

		Assert.assertEquals(getExpectedBVInputXmlFilename() + "_ _-_111", result);

	}

	@Test
	public void testCreateGxeInput() {

		final GxeInput result = runMultiSiteAction.createGxeInput(multiSiteParameters, gxeEnvironment, selectedTraits);
		Assert.assertNotNull(result.getTraits());
		Assert.assertNotNull(result.getEnvironment());
		Assert.assertNotNull(result.getSelectedEnvironments());
		Assert.assertEquals(ENVIRONMENT_GROUP_NAME, result.getEnvironmentGroup());
		Assert.assertEquals(GENOTYPE_NAME, result.getGenotypes().getName());
		Assert.assertEquals(ENVIRONMENT_NAME, result.getEnvironmentName());
		Assert.assertEquals(PROJECT_NAME, result.getBreedingViewProjectName());

	}

	@Test
	public void testGetSelectedTraits() {

		final List<Trait> result = runMultiSiteAction.getSelectedTraits();

		Assert.assertEquals(TEST_TRAITS.length, result.size());

	}

	@Test
	public void testGetSelectedTraitsOnlyTwoTraitsAreSelected() {

		// deselects the first trait in the table
		final CheckBox cb = (CheckBox) runMultiSiteAction.getSelectTraitsTable().getItem(1).getItemProperty(TEST_TRAITS[0]).getValue();
		cb.setValue(false);

		final List<Trait> result = runMultiSiteAction.getSelectedTraits();

		Assert.assertEquals("Only 2 traits are selected.", 2, result.size());

	}

	@Test
	public void testDownloadInputFile() {

		runMultiSiteAction.downloadInputFile(Mockito.mock(File.class), Mockito.anyString());

		// Make sure the file is downloaded to the browser.
		Mockito.verify(this.window).open(Mockito.any(FileResource.class));

	}

	private Table createTestSelectedTraitsTable() {

		final Table table = new Table();
		for (final String trait : TEST_TRAITS) {
			table.addContainerProperty(trait, CheckBox.class, new CheckBox());
		}

		table.addItem(1);

		final Iterator<?> itr = table.getItem(1).getItemPropertyIds().iterator();

		while (itr.hasNext()) {
			final Object propertyId = itr.next();
			final CheckBox cb = (CheckBox) table.getItem(1).getItemProperty(propertyId).getValue();
			cb.setValue(true);
		}

		return table;
	}

	private MultiSiteParameters createMultiSiteParameters() {

		final MultiSiteParameters parameters = new MultiSiteParameters();
		parameters.setProject(this.createProject(PROJECT_NAME));
		parameters.setStudy(this.createStudy(STUDY_NAME));
		parameters.setSelectedEnvGroupFactorName(ENVIRONMENT_GROUP_NAME);
		parameters.setSelectedEnvironmentFactorName(ENVIRONMENT_NAME);
		parameters.setSelectedGenotypeFactorName(GENOTYPE_NAME);

		return parameters;

	}

	private GxeEnvironment createGxeEnvironment() {
		final GxeEnvironment gxeEnvironment = new GxeEnvironment();
		gxeEnvironment.setName(ENVIRONMENT_NAME);
		return gxeEnvironment;
	}

	private Study createStudy(final String name) {

		final Study study = new Study();
		study.setId(STUDY_ID);
		study.setName(name);
		
		final VariableList conditions = new VariableList();
		final Variable studyNameVariable = new Variable();
		studyNameVariable.setValue(name);

		study.setConditions(conditions);

		return study;

	}

	private Project createProject(final String name) {

		final Project project = new Project();
		project.setProjectName(name);
		return project;
	}

	private List<Environment> createEnvironmentList() {

		final List<Environment> list = new ArrayList<Environment>();
		return list;

	}

	private DataSet createMeansDataSet(final String dataSetName) {

		final DataSet ds = new DataSet();
		ds.setDataSetType(DataSetType.MEANS_DATA);
		ds.setId(MEANS_DATASET_ID);
		ds.setName(dataSetName);

		return ds;

	}

	private DataSet createSummaryDataSet() {

		final DataSet ds = new DataSet();
		ds.setDataSetType(DataSetType.SUMMARY_DATA);
		ds.setName(SUMMARY_DATASET_NAME);

		return ds;

	}

	private List<DataSet> createDataSets() {

		final List<DataSet> datasets = new ArrayList<>();
		datasets.add(this.createSummaryDataSet());
		return datasets;

	}

}

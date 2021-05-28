package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.Application;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;
import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.util.ZipUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriter;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RunSingleSiteActionTest {

	private static final String BASE_FILENAME = "MaizeProgram_0_TestStudy-PLOTDATA";
	private static final String ENVIRONMENT_FACTOR = "Environment Factor";
	private static final String ANALYSIS_NAME = "Analysis Name";
	private static final String ROW_FACTOR = "Row Factor";
	private static final String COLUMN_FACTOR = "Column Factor";
	private static final String BLOCK_FACTOR = "Block Factor";
	private static final String REPLICATES_FACTOR = "Replicates Factor";
	private static final String GENOTYPES_FACTOR = "Genotypes Factor";
	private static final String PROJECT_NAME = "Project Name";
	private static final String DATA_SOURCE_NAME = "Data source Name";
	private static final String DATASET_NAME = "Dataset Name";
	private static final int DATASET_ID = 2;
	private static final Long PROJECT_ID = 1L;
	private static final String ENTRY_NO = "ENTRY_NO";
	private static final String PLOT_NO = "PLOT_NO";
	private static final String PLOT_NNO = "PLOT_NNO";
	private static final String BMS_INPUT_FILES_DIR = "/someDirectory/input";
	private static final String XML_FILEPATH = BMS_INPUT_FILES_DIR + BASE_FILENAME + ".xml";
	private static final String XLS_FILEPATH = BMS_INPUT_FILES_DIR + BASE_FILENAME + ".xls";
	private static final String ZIP_FILE_PATH = "/someDirectory/output/" + DATA_SOURCE_NAME + ".zip";

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private ManagerFactory managerFactory;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ClickEvent event;

	@Mock
	private Window window;

	@Mock
	private Component component;

	@Mock
	private SingleSiteAnalysisDetailsPanel source;

	private BreedingViewInput breedingViewInput;

	@Mock
	private ZipUtil zipUtil;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private Select envFactorSelectComponent;

	@Mock
	private DatasetExporter datasetExporter;

	@Mock
	private BreedingViewXMLWriter breedingViewXMLWriter;

	@Mock
	private Application application;

	@InjectMocks
	private final RunSingleSiteAction runSingleSiteAction = new RunSingleSiteAction(null);

	@Captor
	private ArgumentCaptor<List<String>> filesInZipCaptor;

	@Before
	public void setup() throws IOException {
		this.breedingViewInput = new BreedingViewInput();
		this.breedingViewInput.setSelectedEnvironments(this.createSelectedEnvironments());
		this.breedingViewInput.setDatasetName(RunSingleSiteActionTest.DATASET_NAME);
		this.breedingViewInput.setDatasetSource(RunSingleSiteActionTest.DATA_SOURCE_NAME);
		this.breedingViewInput.setDatasetId(DATASET_ID);
		this.breedingViewInput.setSourceXLSFilePath(XLS_FILEPATH);
		this.breedingViewInput.setDestXMLFilePath(XML_FILEPATH);

		final Project project = new Project();
		project.setProjectId(PROJECT_ID);
		project.setProjectName(RunSingleSiteActionTest.PROJECT_NAME);
		project.setUserId(-1);

		this.initializeTestUserInputFromSource();
		this.initializeStudyDataManagerMocks();
		Mockito.doReturn(this.component).when(this.event).getComponent();
		Mockito.doReturn(this.window).when(this.component).getWindow();
		Mockito.doReturn(this.breedingViewInput.getSelectedEnvironments()).when(this.source).getSelectedEnvironments();
		Mockito.doReturn(this.breedingViewInput).when(this.source).getBreedingViewInput();
		Mockito.doReturn(this.application).when(this.source).getApplication();
		Mockito.when(this.zipUtil.zipIt(Mockito.anyString(), Mockito.anyListOf(String.class), Mockito.any(Project.class),
				Mockito.any(ToolName.class))).thenReturn(ZIP_FILE_PATH);
		Mockito.when(this.source.getWindow()).thenReturn(this.window);
	}

	private void initializeStudyDataManagerMocks() {
		Mockito.when(this.studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.ENTRY_NO.getId())).thenReturn(ENTRY_NO);
		Mockito.when(this.studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(PLOT_NO);
		Mockito.when(this.studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NNO.getId())).thenReturn(PLOT_NNO);
	}

	private List<SeaEnvironmentModel> createSelectedEnvironments() {

		final List<SeaEnvironmentModel> selectedEnvironments = new ArrayList<>();
		selectedEnvironments.add(new SeaEnvironmentModel());

		return selectedEnvironments;

	}

	private void initializeTestUserInputFromSource() {

		Mockito.when(this.source.getTxtAnalysisNameValue()).thenReturn(ANALYSIS_NAME);
		Mockito.when(this.source.getSelEnvFactorValue()).thenReturn(ENVIRONMENT_FACTOR);
		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(REPLICATES_FACTOR);
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(BLOCK_FACTOR);
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn(COLUMN_FACTOR);
		Mockito.when(this.source.getSelRowFactorValue()).thenReturn(ROW_FACTOR);
		Mockito.when(this.source.getSelGenotypesValue()).thenReturn(GENOTYPES_FACTOR);
	}

	@Test
	public void testValidateDesignInputAllInputsAreCorrect() {

		Assert.assertTrue("The default test data is all correct, the return value should be true",
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputAnalysisNameIsBlank() {

		Mockito.when(this.source.getTxtAnalysisNameValue()).thenReturn(null);
		Assert.assertFalse("Analysis Name is required so it should return false",
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputEnvironmentFactorIsBlank() {

		Mockito.when(this.source.getSelEnvFactorValue()).thenReturn(null);
		Assert.assertFalse("Environment factor is required so it should return false",
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputDesignTypeIsBlank() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(null);
		Assert.assertFalse("Design type is required so it should return false",
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputReplicatesFactorIsBlankAndDesignTypeIsRCBD() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(null);
		Mockito.doReturn(true).when(this.source).replicateFactorEnabled();

		Assert.assertFalse("Replicates factor is required if the design type is " + ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputReplicatesFactorIsDisabledAndDesignTypeIsRCBD() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(null);
		Mockito.doReturn(false).when(this.source).replicateFactorEnabled();

		Assert.assertTrue("Replicates factor is required if the design type is " + ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputReplicatesFactorIsBlankAndDesignTypeIsNotRCBD() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(null);

		Assert.assertTrue("Replicates factor is not required if the design type is not " + ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndDesignTypeIsIncompleteBlockDesign() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertFalse("Block factor is required if the design type is " + ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndDesignTypeIsPRepDesign() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.P_REP.getBvName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertFalse("Block factor is required if the design type is " + ExperimentDesignType.P_REP.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndNotRequired() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.ROW_COL.getBvName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertTrue("Validation should pass because Block factor is not required if the design type is not "
						+ ExperimentDesignType.RESOLVABLE_INCOMPLETE_BLOCK.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputRowFactorIsBlankAndRequired() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.ROW_COL.getBvName());
		Mockito.when(this.source.getSelRowFactorValue()).thenReturn(null);

		Assert.assertFalse("Row factor is required if the design type is " + ExperimentDesignType.ROW_COL.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputColumnFactorIsBlankAndRequired() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(ExperimentDesignType.ROW_COL.getBvName());
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn(null);

		Assert.assertFalse("Column factor is required if the design type is " + ExperimentDesignType.ROW_COL.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputColumnFactorIsBlankButRowFactorHasValue() {

		Mockito.when(this.source.getSelRowFactorValue()).thenReturn("ROW");
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn(null);

		Assert.assertFalse("Column and row factors shoul be specified together",
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputRowFactorIsBlankButColumnFactorHasValue() {

		Mockito.when(this.source.getSelRowFactorValue()).thenReturn(null);
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn("COL");

		Assert.assertFalse("Column and row factors shoul be specified together",
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testValidateDesignInputRowAndColumnsFactorsAreBlankAndNotRequired() {

		Mockito.when(this.source.getSelRowFactorValue()).thenReturn(null);
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn(null);

		Assert.assertTrue("Column and row factors can be blank if the design type is not " + ExperimentDesignType.ROW_COL.getBvName(),
			this.runSingleSiteAction.validateDesignInput(this.window, this.breedingViewInput));

	}

	@Test
	public void testCreateBlocks() {

		Assert.assertNull(this.runSingleSiteAction.createBlocks(""));

		final Blocks blocks = this.runSingleSiteAction.createBlocks(BLOCK_FACTOR);

		Assert.assertNotNull(blocks);
		Assert.assertEquals("Block_Factor", blocks.getName());

	}

	@Test
	public void testCreateColumns() {

		Assert.assertNull(this.runSingleSiteAction.createColumns(""));

		final Columns columns = this.runSingleSiteAction.createColumns(COLUMN_FACTOR);

		Assert.assertNotNull(columns);
		Assert.assertEquals("Column_Factor", columns.getName());

	}

	@Test
	public void testCreateEnvironment() {

		final Environment environment = this.runSingleSiteAction.createEnvironment(ENVIRONMENT_FACTOR);
		Assert.assertNotNull(environment);
		Assert.assertEquals("Environment_Factor", environment.getName());

	}

	@Test
	public void testCreateGenotypes() {
		final Genotypes genotypes = this.runSingleSiteAction.createGenotypes(DATASET_ID, GENOTYPES_FACTOR);

		Assert.assertNotNull(genotypes);
		Assert.assertEquals("Genotypes_Factor", genotypes.getName());
		Assert.assertEquals(ENTRY_NO, genotypes.getEntry());

	}

	@Test
	public void testCreatePlot() {
		final Plot plot = this.runSingleSiteAction.createPlot(DATASET_ID);

		Assert.assertNotNull(plot);
		Assert.assertEquals(PLOT_NO, plot.getName());
	}

	@Test
	public void testCreatePlotPlotNoIsNull() {
		Mockito.when(this.studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(null);

		final Plot plot = this.runSingleSiteAction.createPlot(DATASET_ID);

		Assert.assertNotNull(plot);
		Assert.assertEquals(PLOT_NNO, plot.getName());
	}

	@Test
	public void testCreateReplicates() {

		final Replicates replicates = this.runSingleSiteAction
			.createReplicates(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK, REPLICATES_FACTOR);
		Assert.assertNotNull(replicates);
		Assert.assertEquals("Replicates_Factor", replicates.getName());

	}

	@Test
	public void testCreateReplicatesBlankReplicates() {

		final Replicates replicates = this.runSingleSiteAction.createReplicates(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK, null);
		Assert.assertNotNull(replicates);
		Assert.assertEquals(DatasetExporter.DUMMY_REPLICATES, replicates.getName());

	}

	@Test
	public void testCreateReplicatesForPrepDesign() {

		final Replicates replicates = this.runSingleSiteAction.createReplicates(ExperimentDesignType.P_REP, REPLICATES_FACTOR);
		Assert.assertNull("No replicates should be created if the design type is " + ExperimentDesignType.P_REP, replicates);

	}

	@Test
	public void testCreateRows() {

		Assert.assertNull(this.runSingleSiteAction.createRows(""));

		final Rows rows = this.runSingleSiteAction.createRows(ROW_FACTOR);

		Assert.assertNotNull(rows);
		Assert.assertEquals("Row_Factor", rows.getName());

	}

	@Test
	public void testPopulateRowPosAndColPosDesignIsNotRowColumn() {

		this.runSingleSiteAction.populateRowPosAndColPos(ExperimentDesignType.P_REP, this.breedingViewInput);

		// ColPos and RowPos should not be null if the design type is NOT Row-Column Design
		Assert.assertNotNull(this.breedingViewInput.getColPos());
		Assert.assertNotNull(this.breedingViewInput.getRowPos());

	}

	@Test
	public void testPopulateRowPosAndColPosDesignIsRowColumn() {

		this.runSingleSiteAction.populateRowPosAndColPos(ExperimentDesignType.ROW_COL, this.breedingViewInput);

		// ColPos and RowPos should be null if the design type is Row-Column Design
		Assert.assertNull(this.breedingViewInput.getColPos());
		Assert.assertNull(this.breedingViewInput.getRowPos());

	}

	@Test
	public void testPopulateRowAndColumnForRowAndColumnDesign() {

		this.runSingleSiteAction.populateRowAndColumn(ExperimentDesignType.ROW_COL, this.breedingViewInput);

		// Columns and Rows should not be null if the design type is Row and Column Design
		Assert.assertNotNull(this.breedingViewInput.getRows());
		Assert.assertNotNull(this.breedingViewInput.getColumns());

	}

	@Test
	public void testPopulateRowAndColumnDesignIsNotRowAndColumn() {

		this.runSingleSiteAction.populateRowAndColumn(ExperimentDesignType.RANDOMIZED_COMPLETE_BLOCK, this.breedingViewInput);

		// Columns and Rows should be null if the design type is NOT Row and Column Design
		Assert.assertNull(this.breedingViewInput.getRows());
		Assert.assertNull(this.breedingViewInput.getColumns());

	}

	@Test
	public void testButtonClick() throws IOException {
		final Project project = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();

		this.runSingleSiteAction.setSource(this.source);
		this.runSingleSiteAction.setZipUtil(this.zipUtil);
		this.runSingleSiteAction.buttonClick(this.event);

		// Make sure that the expected files are compressed in zip
		final ArgumentCaptor<String> filenameCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
		final ArgumentCaptor<ToolName> toolCaptor = ArgumentCaptor.forClass(ToolName.class);
		Mockito.verify(this.zipUtil).zipIt(filenameCaptor.capture(), this.filesInZipCaptor.capture(), projectCaptor.capture(),
				toolCaptor.capture());
		Assert.assertTrue(filenameCaptor.getValue().contains(DATA_SOURCE_NAME));
		Assert.assertEquals(project, projectCaptor.getValue());
		final List<String> filesInZip = this.filesInZipCaptor.getValue();
		Assert.assertEquals(2, filesInZip.size());
		Assert.assertTrue(filesInZip.contains(XML_FILEPATH));
		Assert.assertTrue(filesInZip.contains(XLS_FILEPATH));
		Assert.assertEquals(ToolName.BV_SSA, toolCaptor.getValue());

		// Verify zip file is downloaded to the browser with proper filename
		final ArgumentCaptor<VaadinFileDownloadResource> fileDownloadResourceCaptor = ArgumentCaptor.forClass(VaadinFileDownloadResource.class);
		Mockito.verify(this.window).open(fileDownloadResourceCaptor.capture());
		final VaadinFileDownloadResource downloadResource = fileDownloadResourceCaptor.getValue();
		final String[] uSCount = downloadResource.getFilename().split("_");
		Assert.assertEquals(new File(ZIP_FILE_PATH).getAbsolutePath(), downloadResource.getSourceFile().getAbsolutePath());
		Assert.assertTrue(FileNameGenerator.isValidFileNameFormat(downloadResource.getFilename(), FileNameGenerator.ZIP_DATE_TIME_PATTERN));
	}
}

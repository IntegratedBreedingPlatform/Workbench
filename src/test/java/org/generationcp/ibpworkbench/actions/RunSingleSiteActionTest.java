package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.util.ZipUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.BreedingViewXMLWriter;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
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
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.Application;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

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
		Mockito.doReturn(this.window).when(this.application).getMainWindow();
		Mockito.when(this.zipUtil.zipIt(Mockito.anyString(), Mockito.anyListOf(String.class), Mockito.any(Project.class),
				Mockito.any(ToolName.class))).thenReturn(ZIP_FILE_PATH);
	}
	
	private void initializeStudyDataManagerMocks() {
		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.ENTRY_NO.getId())).thenReturn(ENTRY_NO);
		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(PLOT_NO);
		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NNO.getId())).thenReturn(PLOT_NNO);
	}

	private List<SeaEnvironmentModel> createSelectedEnvironments() {

		final List<SeaEnvironmentModel> selectedEnvironments = new ArrayList<>();
		selectedEnvironments.add(new SeaEnvironmentModel());

		return selectedEnvironments;

	}

	private void initializeTestUserInputFromSource() {

		Mockito.when(this.source.getTxtAnalysisNameValue()).thenReturn(ANALYSIS_NAME);
		Mockito.when(this.source.getSelEnvFactorValue()).thenReturn(ENVIRONMENT_FACTOR);
		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(REPLICATES_FACTOR);
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(BLOCK_FACTOR);
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn(COLUMN_FACTOR);
		Mockito.when(this.source.getSelRowFactorValue()).thenReturn(ROW_FACTOR);
		Mockito.when(this.source.getSelGenotypesValue()).thenReturn(GENOTYPES_FACTOR);
		Mockito.when(this.source.getSelEnvFactor()).thenReturn(envFactorSelectComponent);
		Mockito.when(this.envFactorSelectComponent.getValue()).thenReturn(ENVIRONMENT_FACTOR);
	}

	@Test
	public void testValidateDesignInputAllInputsAreCorrect() {

		Assert.assertTrue("The default test data is all correct, the return value should be true",
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputAnalysisNameIsBlank() {

		Mockito.when(this.source.getTxtAnalysisNameValue()).thenReturn(null);
		Assert.assertFalse("Analysis Name is required so it should return false",
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputEnvironmentFactorIsBlank() {

		Mockito.when(this.source.getSelEnvFactorValue()).thenReturn(null);
		Assert.assertFalse("Environment factor is required so it should return false",
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputDesignTypeIsBlank() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(null);
		Assert.assertFalse("Design type is required so it should return false",
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputReplicatesFactorIsBlankAndDesignTypeIsRCBD() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(null);
		Mockito.when(this.source.getSelReplicates()).thenReturn(new Select());

		Assert.assertFalse("Replicates factor is required if the design type is " + DesignType.RANDOMIZED_BLOCK_DESIGN.getName(),
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputReplicatesFactorIsBlankAndDesignTypeIsNotRCBD() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(null);
		Mockito.when(this.source.getSelReplicates()).thenReturn(new Select());

		Assert.assertTrue("Replicates factor is not required if the design type is not " + DesignType.RANDOMIZED_BLOCK_DESIGN.getName(),
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndDesignTypeIsIncompleteBlockDesign() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertFalse("Block factor is required if the design type is " + DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName(),
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndDesignTypeIsPRepDesign() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.P_REP_DESIGN.getName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertFalse("Block factor is required if the design type is " + DesignType.P_REP_DESIGN.getName(),
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndNotRequired() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertTrue("Validation should pass because Block factor is not required if the design type is not "
						+ DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName(),
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputRowFactorIsBlankAndRequired() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
		Mockito.when(this.source.getSelRowFactorValue()).thenReturn(null);

		Assert.assertFalse("Row factor is required if the design type is " + DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName(),
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputColumnFactorIsBlankAndRequired() {

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn(null);

		Assert.assertFalse("Column factor is required if the design type is " + DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName(),
				runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testCreateBlocks() {

		Assert.assertNull(runSingleSiteAction.createBlocks(""));

		final Blocks blocks = runSingleSiteAction.createBlocks(BLOCK_FACTOR);

		Assert.assertNotNull(blocks);
		Assert.assertEquals("Block_Factor", blocks.getName());

	}

	@Test
	public void testCreateColumns() {

		Assert.assertNull(runSingleSiteAction.createColumns(""));

		final Columns columns = runSingleSiteAction.createColumns(COLUMN_FACTOR);

		Assert.assertNotNull(columns);
		Assert.assertEquals("Column_Factor", columns.getName());

	}

	@Test
	public void testCreateEnvironment() {

		final Environment environment = runSingleSiteAction.createEnvironment(ENVIRONMENT_FACTOR);
		Assert.assertNotNull(environment);
		Assert.assertEquals("Environment_Factor", environment.getName());

	}
	
	@Test
	public void testCreateGenotypes() {
		final Genotypes genotypes = runSingleSiteAction.createGenotypes(DATASET_ID, GENOTYPES_FACTOR);

		Assert.assertNotNull(genotypes);
		Assert.assertEquals("Genotypes_Factor", genotypes.getName());
		Assert.assertEquals(ENTRY_NO, genotypes.getEntry());

	}

	@Test
	public void testCreatePlot() {
		final Plot plot = runSingleSiteAction.createPlot(DATASET_ID);

		Assert.assertNotNull(plot);
		Assert.assertEquals(PLOT_NO, plot.getName());
	}

	@Test
	public void testCreatePlotPlotNoIsNull() {
		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(null);
		
		final Plot plot = runSingleSiteAction.createPlot(DATASET_ID);

		Assert.assertNotNull(plot);
		Assert.assertEquals(PLOT_NNO, plot.getName());
	}

	@Test
	public void testCreateReplicates() {

		final Replicates replicates = runSingleSiteAction.createReplicates(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), REPLICATES_FACTOR);
		Assert.assertNotNull(replicates);
		Assert.assertEquals("Replicates_Factor", replicates.getName());

	}

	@Test
	public void testCreateReplicatesBlankReplicates() {

		final Replicates replicates = runSingleSiteAction.createReplicates(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), null);
		Assert.assertNotNull(replicates);
		Assert.assertEquals(DatasetExporter.DUMMY_REPLICATES, replicates.getName());

	}

	@Test
	public void testCreateReplicatesForPrepDesign() {

		final Replicates replicates = runSingleSiteAction.createReplicates(DesignType.P_REP_DESIGN.getName(), REPLICATES_FACTOR);
		Assert.assertNull("No replicates should be created if the design type is " + DesignType.P_REP_DESIGN.getName(), replicates);

	}

	@Test
	public void testCreateRows() {

		Assert.assertNull(runSingleSiteAction.createRows(""));

		final Rows rows = runSingleSiteAction.createRows(ROW_FACTOR);

		Assert.assertNotNull(rows);
		Assert.assertEquals("Row_Factor", rows.getName());

	}

	@Test
	public void testPopulateRowPosAndColPosDesignIsPRep() {

		runSingleSiteAction.populateRowPosAndColPos(DesignType.P_REP_DESIGN, this.breedingViewInput);

		// ColPos and RowPos should not be null if the design type is P-Rep Design
		Assert.assertNotNull(this.breedingViewInput.getColPos());
		Assert.assertNotNull(this.breedingViewInput.getRowPos());

	}

	@Test
	public void testPopulateRowPosAndColPosDesignIsNotPRep() {

		runSingleSiteAction.populateRowPosAndColPos(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN, this.breedingViewInput);

		// ColPos and RowPos should be null if the design type is NOT P-Rep Design
		Assert.assertNull(this.breedingViewInput.getColPos());
		Assert.assertNull(this.breedingViewInput.getRowPos());

	}

	@Test
	public void testPopulateRowAndColumnForRowAndColumnDesign() {

		runSingleSiteAction.populateRowAndColumn(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN, this.breedingViewInput);

		// Columns and Rows should not be null if the design type is Row and Column Design
		Assert.assertNotNull(this.breedingViewInput.getRows());
		Assert.assertNotNull(this.breedingViewInput.getColumns());

	}

	@Test
	public void testPopulateRowAndColumnDesignIsNotRowAndColumn() {

		runSingleSiteAction.populateRowAndColumn(DesignType.RANDOMIZED_BLOCK_DESIGN, this.breedingViewInput);

		// Columns and Rows should be null if the design type is NOT Row and Column Design
		Assert.assertNull(this.breedingViewInput.getRows());
		Assert.assertNull(this.breedingViewInput.getColumns());

	}
	
	@Test
	public void testButtonClickServerAppIsTrue() throws IOException {
		final Project project = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(project).when(this.contextUtil).getProjectInContext();

		this.runSingleSiteAction.setIsServerApp("true");
		this.runSingleSiteAction.setSource(this.source);
		this.runSingleSiteAction.setZipUtil(this.zipUtil);
		this.runSingleSiteAction.buttonClick(event);
		
		// Make sure that the expected files are compressed in zip
		final ArgumentCaptor<String> filenameCaptor = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
		final ArgumentCaptor<ToolName> toolCaptor = ArgumentCaptor.forClass(ToolName.class);
		Mockito.verify(this.zipUtil).zipIt(filenameCaptor.capture(), filesInZipCaptor.capture(), projectCaptor.capture(),
				toolCaptor.capture());
		Assert.assertEquals(DATA_SOURCE_NAME, filenameCaptor.getValue());
		Assert.assertEquals(project, projectCaptor.getValue());
		final List<String> filesInZip = filesInZipCaptor.getValue();
		Assert.assertEquals(2, filesInZip.size());
		Assert.assertTrue(filesInZip.contains(XML_FILEPATH));
		Assert.assertTrue(filesInZip.contains(XLS_FILEPATH));
		Assert.assertEquals(ToolName.BV_SSA, toolCaptor.getValue());
		
		// Verify zip file is downloaded to the browser with proper filename
		final ArgumentCaptor<VaadinFileDownloadResource> fileDownloadResourceCaptor = ArgumentCaptor.forClass(VaadinFileDownloadResource.class);
		Mockito.verify(this.window).open(fileDownloadResourceCaptor.capture());
		final VaadinFileDownloadResource downloadResource = fileDownloadResourceCaptor.getValue();
		Assert.assertEquals(new File(ZIP_FILE_PATH).getAbsolutePath(), downloadResource.getSourceFile().getAbsolutePath());
		Assert.assertEquals(DATA_SOURCE_NAME + ".zip", downloadResource.getFilename());
	}

}

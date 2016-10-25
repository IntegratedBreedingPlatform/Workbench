package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;
import org.generationcp.commons.breedingview.xml.Blocks;
import org.generationcp.commons.breedingview.xml.Columns;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.breedingview.xml.Environment;
import org.generationcp.commons.breedingview.xml.Genotypes;
import org.generationcp.commons.breedingview.xml.Plot;
import org.generationcp.commons.breedingview.xml.Replicates;
import org.generationcp.commons.breedingview.xml.Rows;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.ibpworkbench.util.DatasetExporter;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RunSingleSiteActionTest {

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
	private SingleSiteAnalysisDetailsPanel source;

	private BreedingViewInput breedingViewInput;

	@InjectMocks
	private final RunSingleSiteAction runSingleSiteAction = new RunSingleSiteAction(null, null);

	@Before
	public void setup() {

		this.breedingViewInput = new BreedingViewInput();
		this.breedingViewInput.setSelectedEnvironments(this.createSelectedEnvironments());
		this.breedingViewInput.setDatasetName(RunSingleSiteActionTest.DATASET_NAME);
		this.breedingViewInput.setDatasetSource(RunSingleSiteActionTest.DATA_SOURCE_NAME);
		this.breedingViewInput.setDatasetId(DATASET_ID);

		final Project project = new Project();
		project.setProjectId(PROJECT_ID);
		project.setProjectName(RunSingleSiteActionTest.PROJECT_NAME);
		project.setUserId(-1);

		this.initializeTestUserInputFromSource();

		this.runSingleSiteAction.setProject(project);

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

		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.ENTRY_NO.getId())).thenReturn(ENTRY_NO);

		final Genotypes genotypes = runSingleSiteAction.createGenotypes(DATASET_ID, GENOTYPES_FACTOR);

		Assert.assertNotNull(genotypes);
		Assert.assertEquals("Genotypes_Factor", genotypes.getName());
		Assert.assertEquals(ENTRY_NO, genotypes.getEntry());

	}

	@Test
	public void testCreatePlot() {

		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(PLOT_NO);

		final Plot plot = runSingleSiteAction.createPlot(DATASET_ID);

		Assert.assertNotNull(plot);
		Assert.assertEquals(PLOT_NO, plot.getName());
	}

	@Test
	public void testCreatePlotPlotNoIsNull() {

		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(null);
		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NNO.getId())).thenReturn(PLOT_NNO);

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

}

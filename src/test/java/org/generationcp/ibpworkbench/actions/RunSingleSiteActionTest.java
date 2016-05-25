
package org.generationcp.ibpworkbench.actions;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Select;
import org.generationcp.commons.breedingview.xml.*;
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

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RunSingleSiteActionTest {

	private static final String ENVIRONMENT_FACTOR = "Environment Name";
	private static final String ANALYSIS_NAME = "Analysis Name";
	private static final String ROW_FACTOR = "ROW FACTOR";
	private static final String COLUMN_FACTOR = "COLUMN FACTOR";
	private static final String BLOCK_FACTOR = "BLOCK FACTOR";
	private static final String REPLICATES_FACTOR = "REPLICATES FACTOR";
	private static final String GENOTYPES_FACTOR = "GENOTYPES FACTOR";
	private static final String PROJECT_NAME = "PROJECT NAME";
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
	private RunSingleSiteAction runSingleSiteAction = new RunSingleSiteAction(null, null);

	@Before
	public void setup() {

		this.breedingViewInput = new BreedingViewInput();
		this.breedingViewInput.setSelectedEnvironments(this.createSelectedEnvironments());
		this.breedingViewInput.setDatasetName(RunSingleSiteActionTest.DATASET_NAME);
		this.breedingViewInput.setDatasetSource(RunSingleSiteActionTest.DATA_SOURCE_NAME);
		this.breedingViewInput.setDatasetId(DATASET_ID);

		Project project = new Project();
		project.setProjectId(PROJECT_ID);
		project.setProjectName(RunSingleSiteActionTest.PROJECT_NAME);
		project.setUserId(-1);

		this.initializeTestUserInputFromSource();

		this.runSingleSiteAction.setProject(project);


	}

	private List<SeaEnvironmentModel> createSelectedEnvironments() {

		List<SeaEnvironmentModel> selectedEnvironments = new ArrayList<>();
		selectedEnvironments.add(new SeaEnvironmentModel());

		return selectedEnvironments;

	}

	private void initializeTestUserInputFromSource(){

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
	public void testValidateDesignInputAllInputsAreCorrect(){

		Assert.assertTrue("The default test data is all correct, the return value should be true",runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputAnalysisNameIsBlank(){

		Mockito.when(this.source.getTxtAnalysisNameValue()).thenReturn(null);
		Assert.assertFalse("Analysis Name is required so it should return false", runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputEnvironmentFactorIsBlank(){

		Mockito.when(this.source.getSelEnvFactorValue()).thenReturn(null);
		Assert.assertFalse("Environment factor is required so it should return false",runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputDesignTypeIsBlank(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(null);
		Assert.assertFalse("Design type is required so it should return false",runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputReplicatesFactorIsBlankAndDesignTypeIsRCBD(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(null);
		Mockito.when(this.source.getSelReplicates()).thenReturn(new Select());

		Assert.assertFalse("Replicates factor is required if the design type is " + DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputReplicatesFactorIsBlankAndDesignTypeIsNotRCBD(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		Mockito.when(this.source.getSelReplicatesValue()).thenReturn(null);
		Mockito.when(this.source.getSelReplicates()).thenReturn(new Select());

		Assert.assertTrue("Replicates factor is not required if the design type is not " + DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndDesignTypeIsIncompleteBlockDesign(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertFalse("Block factor is required if the design type is " + DesignType.INCOMPLETE_BLOCK_DESIGN.getName(), runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndDesignTypeIsPRepDesign(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.P_REP_DESIGN.getName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertFalse("Block factor is required if the design type is " + DesignType.P_REP_DESIGN.getName(), runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputBlocksFactorIsBlankAndNotRequired(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.ROW_COLUMN_DESIGN.getName());
		Mockito.when(this.source.getSelBlocksValue()).thenReturn(null);

		Assert.assertTrue("Validation should pass because Block factor is not required if the design type is not " + DesignType.INCOMPLETE_BLOCK_DESIGN.getName(),runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputRowFactorIsBlankAndRequired(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.ROW_COLUMN_DESIGN.getName());
		Mockito.when(this.source.getSelRowFactorValue()).thenReturn(null);

		Assert.assertFalse("Row factor is required if the design type is " + DesignType.ROW_COLUMN_DESIGN.getName(),runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testValidateDesignInputColumnFactorIsBlankAndRequired(){

		Mockito.when(this.source.getSelDesignTypeValue()).thenReturn(DesignType.ROW_COLUMN_DESIGN.getName());
		Mockito.when(this.source.getSelColumnFactorValue()).thenReturn(null);

		Assert.assertFalse("Column factor is required if the design type is " + DesignType.ROW_COLUMN_DESIGN.getName(),runSingleSiteAction.validateDesignInput(this.window, breedingViewInput));

	}

	@Test
	public void testCreateBlocks(){

		Assert.assertNull(runSingleSiteAction.createBlocks(""));

		Blocks blocks = runSingleSiteAction.createBlocks(BLOCK_FACTOR);

		Assert.assertNotNull(blocks);
		Assert.assertEquals(BLOCK_FACTOR, blocks.getName());

	}

	@Test
	public void testCreateColumns(){

		Assert.assertNull(runSingleSiteAction.createColumns(""));

		Columns columns = runSingleSiteAction.createColumns(COLUMN_FACTOR);

		Assert.assertNotNull(columns);
		Assert.assertEquals(COLUMN_FACTOR, columns.getName());

	}

	@Test
	public void testCreateEnvironment(){

		Environment environment = runSingleSiteAction.createEnvironment(ENVIRONMENT_FACTOR);
		Assert.assertNotNull(environment);
		Assert.assertEquals(ENVIRONMENT_FACTOR, environment.getName());

	}


	@Test
	public void testCreateGenotypes(){

		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.ENTRY_NO.getId())).thenReturn(ENTRY_NO);

		Genotypes genotypes = runSingleSiteAction.createGenotypes(DATASET_ID, GENOTYPES_FACTOR);

		Assert.assertNotNull(genotypes);
		Assert.assertEquals(GENOTYPES_FACTOR, genotypes.getName());
		Assert.assertEquals(ENTRY_NO, genotypes.getEntry());

	}

	@Test
	public void testCreatePlot(){

		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(PLOT_NO);

		Plot plot = runSingleSiteAction.createPlot(DATASET_ID);

		Assert.assertNotNull(plot);
		Assert.assertEquals(PLOT_NO, plot.getName());
	}

	@Test
	public void testCreatePlotPlotNoIsNull(){

		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NO.getId())).thenReturn(null);
		Mockito.when(studyDataManager.getLocalNameByStandardVariableId(DATASET_ID, TermId.PLOT_NNO.getId())).thenReturn(PLOT_NNO);

		Plot plot = runSingleSiteAction.createPlot(DATASET_ID);

		Assert.assertNotNull(plot);
		Assert.assertEquals(PLOT_NNO, plot.getName());
	}

	@Test
	public void testCreateReplicates(){

		Replicates replicates = runSingleSiteAction.createReplicates(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), REPLICATES_FACTOR);
		Assert.assertNotNull(replicates);
		Assert.assertEquals(REPLICATES_FACTOR, replicates.getName());

	}


	@Test
	public void testCreateReplicatesBlankReplicates(){

		Replicates replicates = runSingleSiteAction.createReplicates(DesignType.RANDOMIZED_BLOCK_DESIGN.getName(), null);
		Assert.assertNotNull(replicates);
		Assert.assertEquals(DatasetExporter.DUMMY_REPLICATES, replicates.getName());

	}

	@Test
	public void testCreateReplicatesForPrepDesign(){

		Replicates replicates = runSingleSiteAction.createReplicates(DesignType.P_REP_DESIGN.getName(), REPLICATES_FACTOR);
		Assert.assertNull("No replicates should be created if the design type is " + DesignType.P_REP_DESIGN.getName() ,replicates);

	}

	@Test
	public void testCreateRows(){

		Assert.assertNull(runSingleSiteAction.createRows(""));

		Rows rows = runSingleSiteAction.createRows(ROW_FACTOR);

		Assert.assertNotNull(rows);
		Assert.assertEquals(ROW_FACTOR, rows.getName());

	}



}


package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.actions.RunSingleSiteAction;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class RunSingleSiteActionTest {

	private static final String TRIAL_NO = "1";
	private static final String ENVIRONMENT_NAME = "Environment Name";
	private static final String NEW_ITEM = "New Item";
	private static final String NEW_ITEM_ID = "New Item ID";
	private static final String ANALYSIS_NAME = "Analysis Name";
	private static final String _REPLICATES = "_REPLICATES_";
	private static final String ROW_FACTOR = "ROW FACTOR";
	private static final String COLUMN_FACTOR = "COLUMN FACTOR";
	private static final String BLOCK_FACTOR = "BLOCK FACTOR";
	private static final String REP_FACTOR = "REP FACTOR";
	private static final String PROJECT_NAME = "PROJECT NAME";
	private static final String DATA_SOURCE_NAME = "Data source Name";
	private static final String DATASET_NAME = "Dataset Name";

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

	private BreedingViewInput breedingViewInput;
	private SingleSiteAnalysisDetailsPanel source;
	private RunSingleSiteAction runBreedingViewAction;

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);

		List<SeaEnvironmentModel> selectedEnvironments = new ArrayList<SeaEnvironmentModel>();

		this.breedingViewInput = new BreedingViewInput();
		this.breedingViewInput.setSelectedEnvironments(selectedEnvironments);
		this.breedingViewInput.setDatasetName(RunSingleSiteActionTest.DATASET_NAME);
		this.breedingViewInput.setDatasetSource(RunSingleSiteActionTest.DATA_SOURCE_NAME);

		Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName(RunSingleSiteActionTest.PROJECT_NAME);
		project.setUserId(-1);

		this.source =
				Mockito.spy(new SingleSiteAnalysisDetailsPanel(null, this.breedingViewInput, null, null, project, this.studyDataManager,
						this.managerFactory, null));
		Mockito.doReturn(this.breedingViewInput.getSelectedEnvironments()).when(this.source).getSelectedEnvironments();
		Mockito.doNothing().when(this.source).populateChoicesForEnvForAnalysis();
		Mockito.doNothing().when(this.source).populateChoicesForReplicates();
		Mockito.doNothing().when(this.source).populateChoicesForBlocks();
		Mockito.doNothing().when(this.source).populateChoicesForRowFactor();
		Mockito.doNothing().when(this.source).populateChoicesForColumnFactor();
		Mockito.doNothing().when(this.source).checkDesignFactor();
		Mockito.doNothing().when(this.source).populateChoicesForGenotypes();

		Mockito.when(this.event.getComponent()).thenReturn(Mockito.mock(Component.class));
		Mockito.when(this.event.getComponent().getWindow()).thenReturn(this.window);

		this.source.setMessageSource(this.messageSource);
		this.source.assemble();

		this.runBreedingViewAction = Mockito.spy(new RunSingleSiteAction(this.source, project));
		Mockito.doNothing().when(this.runBreedingViewAction)
				.showErrorMessage(Matchers.any(Window.class), Matchers.anyString(), Matchers.anyString());

	}

	@Test
	public void test_ButtonClick_ValidateAnalsisName() {

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window, "Please enter an Analysis Name.", "");

	}

	@Test
	public void test_ButtonClick_ValidateEnvironmentFactor() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window, "Please select an environment factor.",
				"");

	}

	@Test
	public void test_ButtonClick_ValidateEnvironmentForAnalysis() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window,
				"Please select environment for analysis.", "");

	}

	@Test
	public void test_ButtonClick_ValidateDesignType() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(RunSingleSiteActionTest.ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(RunSingleSiteActionTest.TRIAL_NO);
		this.breedingViewInput.getSelectedEnvironments().add(environment);

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window, "Please specify design type.", "");

	}

	@Test
	public void test_ButtonClick_ValidateReplicatesFactor() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(RunSingleSiteActionTest.ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(RunSingleSiteActionTest.TRIAL_NO);
		this.breedingViewInput.getSelectedEnvironments().add(environment);

		this.source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window, "Please specify replicates factor.", "");

	}

	@Test
	public void test_ButtonClick_NoReplicates() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(RunSingleSiteActionTest.ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(RunSingleSiteActionTest.TRIAL_NO);
		this.breedingViewInput.getSelectedEnvironments().add(environment);

		this.source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		this.source.getSelReplicates().setEnabled(false);
		this.runBreedingViewAction.buttonClick(this.event);
		Assert.assertEquals(RunSingleSiteActionTest._REPLICATES, this.breedingViewInput.getReplicates().getName());

	}

	@Test
	public void test_ButtonClick_ValidateIncompleteBlockFactor() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(RunSingleSiteActionTest.ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(RunSingleSiteActionTest.TRIAL_NO);
		this.breedingViewInput.getSelectedEnvironments().add(environment);

		this.source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		this.source.getSelReplicates().addItem(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().select(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().setEnabled(true);
		this.source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window,
				"Please specify incomplete block factor.", "");

	}

	@Test
	public void test_ButtonClick_ValidateColumnFactor() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(RunSingleSiteActionTest.ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(RunSingleSiteActionTest.TRIAL_NO);
		this.breedingViewInput.getSelectedEnvironments().add(environment);

		this.source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		this.source.getSelReplicates().addItem(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().select(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().setEnabled(true);
		this.source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());

		this.source.getSelBlocks().addItem(RunSingleSiteActionTest.BLOCK_FACTOR);
		this.source.getSelBlocks().select(RunSingleSiteActionTest.BLOCK_FACTOR);
		this.source.getSelDesignType().addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.ROW_COLUMN_DESIGN.getName());

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window, "Please specify column factor.", "");

	}

	@Test
	public void test_ButtonClick_ValidateRowFactor() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(RunSingleSiteActionTest.ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(RunSingleSiteActionTest.TRIAL_NO);
		this.breedingViewInput.getSelectedEnvironments().add(environment);

		this.source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		this.source.getSelReplicates().addItem(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().select(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().setEnabled(true);
		this.source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());

		this.source.getSelBlocks().addItem(RunSingleSiteActionTest.BLOCK_FACTOR);
		this.source.getSelBlocks().select(RunSingleSiteActionTest.BLOCK_FACTOR);
		this.source.getSelDesignType().addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.ROW_COLUMN_DESIGN.getName());

		this.source.getSelColumnFactor().addItem(RunSingleSiteActionTest.COLUMN_FACTOR);
		this.source.getSelColumnFactor().select(RunSingleSiteActionTest.COLUMN_FACTOR);

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window, "Please specify row factor.", "");

	}

	@Test
	public void test_ButtonClick_ValidateGenotypeFactor() {

		this.source.getTxtAnalysisName().setValue(RunSingleSiteActionTest.ANALYSIS_NAME);
		this.source.getSelEnvFactor().addItem(RunSingleSiteActionTest.NEW_ITEM_ID);
		this.source.getSelEnvFactor().setItemCaption(RunSingleSiteActionTest.NEW_ITEM_ID, RunSingleSiteActionTest.NEW_ITEM);
		this.source.getSelEnvFactor().select(RunSingleSiteActionTest.NEW_ITEM_ID);

		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(RunSingleSiteActionTest.ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(RunSingleSiteActionTest.TRIAL_NO);
		this.breedingViewInput.getSelectedEnvironments().add(environment);

		this.source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		this.source.getSelReplicates().addItem(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().select(RunSingleSiteActionTest.REP_FACTOR);
		this.source.getSelReplicates().setEnabled(true);
		this.source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());

		this.source.getSelBlocks().addItem(RunSingleSiteActionTest.BLOCK_FACTOR);
		this.source.getSelBlocks().select(RunSingleSiteActionTest.BLOCK_FACTOR);
		this.source.getSelDesignType().addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		this.source.getSelDesignType().select(DesignType.ROW_COLUMN_DESIGN.getName());

		this.source.getSelColumnFactor().addItem(RunSingleSiteActionTest.COLUMN_FACTOR);
		this.source.getSelColumnFactor().select(RunSingleSiteActionTest.COLUMN_FACTOR);

		this.source.getSelRowFactor().addItem(RunSingleSiteActionTest.ROW_FACTOR);
		this.source.getSelRowFactor().select(RunSingleSiteActionTest.ROW_FACTOR);

		this.runBreedingViewAction.buttonClick(this.event);
		Mockito.verify(this.runBreedingViewAction, Mockito.times(1)).showErrorMessage(this.window, "Please specify Genotypes factor.", "");

	}

}

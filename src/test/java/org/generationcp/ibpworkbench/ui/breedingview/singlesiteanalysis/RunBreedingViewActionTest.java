package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.actions.RunBreedingViewAction;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RunBreedingViewActionTest {
	
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
	private RunBreedingViewAction runBreedingViewAction;
	
	@Before
	public void setup(){
		
		MockitoAnnotations.initMocks(this);
		
		List<SeaEnvironmentModel> selectedEnvironments = new ArrayList<SeaEnvironmentModel>();
		
		breedingViewInput = new BreedingViewInput();
		breedingViewInput.setSelectedEnvironments(selectedEnvironments);
		breedingViewInput.setDatasetName(DATASET_NAME);
		breedingViewInput.setDatasetSource(DATA_SOURCE_NAME);
		
		Project project = new Project();
		project.setProjectId(1L);
		project.setProjectName(PROJECT_NAME);
		project.setUserId(-1);
		
		source = spy(new SingleSiteAnalysisDetailsPanel(null, breedingViewInput, null, null, project, studyDataManager, managerFactory, null));
		doReturn(breedingViewInput.getSelectedEnvironments()).when(source).getSelectedEnvironments();
		doNothing().when(source).populateChoicesForEnvForAnalysis();
		doNothing().when(source).populateChoicesForReplicates();
		doNothing().when(source).populateChoicesForBlocks();
		doNothing().when(source).populateChoicesForRowFactor();
		doNothing().when(source).populateChoicesForColumnFactor();
		doNothing().when(source).checkDesignFactor();
		doNothing().when(source).populateChoicesForGenotypes();
		
		when(event.getComponent()).thenReturn(mock(Component.class));
		when(event.getComponent().getWindow()).thenReturn(window);
		
		source.setMessageSource(messageSource);
		source.assemble();
		
		
		runBreedingViewAction = spy(new RunBreedingViewAction(source, project));
		doNothing().when(runBreedingViewAction).showErrorMessage(any(Window.class), anyString(), anyString());
	
	}
	
	@Test
	public void test_ButtonClick_ValidateAnalsisName() {
		
			runBreedingViewAction.buttonClick(event);
			verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please enter an Analysis Name.", "");
			
	}
	
	@Test
	public void test_ButtonClick_ValidateEnvironmentFactor() {
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please select an environment factor.", "");
		
	}
	
	@Test
	public void test_ButtonClick_ValidateEnvironmentForAnalysis(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
	
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please select environment for analysis.", "");
		
	}
	
	@Test 
	public void test_ButtonClick_ValidateDesignType(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
		
		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(TRIAL_NO);
		breedingViewInput.getSelectedEnvironments().add(environment);
		
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please specify design type.", "");
		
	}
	
	@Test 
	public void test_ButtonClick_ValidateReplicatesFactor(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
		
		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(TRIAL_NO);
		breedingViewInput.getSelectedEnvironments().add(environment);
		
		source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please specify replicates factor.", "");
		
	}
	@Test 
	public void test_ButtonClick_NoReplicates(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
		
		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(TRIAL_NO);
		breedingViewInput.getSelectedEnvironments().add(environment);
		
		source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		
		source.getSelReplicates().setEnabled(false);
		runBreedingViewAction.buttonClick(event);
		assertEquals(_REPLICATES, breedingViewInput.getReplicates().getName());
		
	}
	@Test 
	public void test_ButtonClick_ValidateIncompleteBlockFactor(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
		
		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(TRIAL_NO);
		breedingViewInput.getSelectedEnvironments().add(environment);
		
		source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		
		source.getSelReplicates().addItem(REP_FACTOR);
		source.getSelReplicates().select(REP_FACTOR);
		source.getSelReplicates().setEnabled(true);
		source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please specify incomplete block factor.", "");
	
	}
	@Test 
	public void test_ButtonClick_ValidateColumnFactor(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
		
		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(TRIAL_NO);
		breedingViewInput.getSelectedEnvironments().add(environment);
		
		source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		
		source.getSelReplicates().addItem(REP_FACTOR);
		source.getSelReplicates().select(REP_FACTOR);
		source.getSelReplicates().setEnabled(true);
		source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		
		source.getSelBlocks().addItem(BLOCK_FACTOR);
		source.getSelBlocks().select(BLOCK_FACTOR);
		source.getSelDesignType().addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		source.getSelDesignType().select(DesignType.ROW_COLUMN_DESIGN.getName());
		
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please specify column factor.", "");
		
	}
	@Test 
	public void test_ButtonClick_ValidateRowFactor(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
		
		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(TRIAL_NO);
		breedingViewInput.getSelectedEnvironments().add(environment);
		
		source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		
		source.getSelReplicates().addItem(REP_FACTOR);
		source.getSelReplicates().select(REP_FACTOR);
		source.getSelReplicates().setEnabled(true);
		source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		
		source.getSelBlocks().addItem(BLOCK_FACTOR);
		source.getSelBlocks().select(BLOCK_FACTOR);
		source.getSelDesignType().addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		source.getSelDesignType().select(DesignType.ROW_COLUMN_DESIGN.getName());
		
		source.getSelColumnFactor().addItem(COLUMN_FACTOR);
		source.getSelColumnFactor().select(COLUMN_FACTOR);
		
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please specify row factor.", "");
		
	}
	@Test 
	public void test_ButtonClick_ValidateGenotypeFactor(){
		
		source.getTxtAnalysisName().setValue(ANALYSIS_NAME);
		source.getSelEnvFactor().addItem(NEW_ITEM_ID);
		source.getSelEnvFactor().setItemCaption(NEW_ITEM_ID, NEW_ITEM);
		source.getSelEnvFactor().select(NEW_ITEM_ID);
		
		SeaEnvironmentModel environment = new SeaEnvironmentModel();
		environment.setActive(true);
		environment.setEnvironmentName(ENVIRONMENT_NAME);
		environment.setLocationId(1);
		environment.setTrialno(TRIAL_NO);
		breedingViewInput.getSelectedEnvironments().add(environment);
		
		source.getSelDesignType().addItem(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.RANDOMIZED_BLOCK_DESIGN.getName());
		
		source.getSelReplicates().addItem(REP_FACTOR);
		source.getSelReplicates().select(REP_FACTOR);
		source.getSelReplicates().setEnabled(true);
		source.getSelDesignType().addItem(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		source.getSelDesignType().select(DesignType.INCOMPLETE_BLOCK_DESIGN.getName());
		
		source.getSelBlocks().addItem(BLOCK_FACTOR);
		source.getSelBlocks().select(BLOCK_FACTOR);
		source.getSelDesignType().addItem(DesignType.ROW_COLUMN_DESIGN.getName());
		source.getSelDesignType().select(DesignType.ROW_COLUMN_DESIGN.getName());
		
		source.getSelColumnFactor().addItem(COLUMN_FACTOR);
		source.getSelColumnFactor().select(COLUMN_FACTOR);
		
		source.getSelRowFactor().addItem(ROW_FACTOR);
		source.getSelRowFactor().select(ROW_FACTOR);
		
		runBreedingViewAction.buttonClick(event);
		verify(runBreedingViewAction, times(1)).showErrorMessage(window, "Please specify Genotypes factor.", "");
		
	}
	
}

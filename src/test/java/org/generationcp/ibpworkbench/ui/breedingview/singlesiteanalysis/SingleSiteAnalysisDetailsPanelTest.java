
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.RunBreedingViewButtonClickListener;
import org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis.UploadBVFilesButtonClickListener;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import junit.framework.Assert;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 12/17/2014 Time: 1:39 PM
 */

public class SingleSiteAnalysisDetailsPanelTest {

	private static final String DATASET_TYPE = "DATASET_TYPE";
	private static final String DATASET_TITLE = "DATASET_TITLE";
	private static final String DATASET_NAME = "DATASET_NAME";
	private static final String EXPT_DESIGN = "EXPT_DESIGN";
	private static final int DATASET_ID = 3;
	private static final int STUDY_ID = 1;
	private static final int LOCATION_ID = 101;
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";

	@Mock
	private SingleSiteAnalysisStudyDetailsComponent studyDetailsComponent;

	@Mock
	private SingleSiteAnalysisEnvironmentsComponent environmentsComponent;

	@Mock
	private SingleSiteAnalysisDesignDetails designDetailsComponent;

	@Mock
	private SingleSiteAnalysisGenotypesComponent genotypesComponent;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private Component parentComponent;

	@Mock
	private Window window;

	@InjectMocks
	private SingleSiteAnalysisDetailsPanel ssaPanel;

	private List<DMSVariableType> factors;
	private List<DMSVariableType> studyFactors;

	private BreedingViewInput input;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		this.initializeBreedingViewInput();
		this.factors = this.createTestFactors();
		this.studyFactors = this.createStudyVariables();

		final Project project = new Project();
		this.ssaPanel = new SingleSiteAnalysisDetailsPanel(new Tool(), this.input, this.factors, this.studyFactors, project,
				new SingleSiteAnalysisPanel(project));
		this.ssaPanel.setMessageSource(this.messageSource);
		this.ssaPanel.setStudyDataManager(this.studyDataManager);
		this.ssaPanel.setParent(this.parentComponent);
		this.ssaPanel.setEnvironmentsComponent(this.environmentsComponent);
		this.ssaPanel.setGenotypesComponent(this.genotypesComponent);
		this.ssaPanel.setStudyDetailsComponent(this.studyDetailsComponent);
		this.ssaPanel.setDesignDetailsComponent(this.designDetailsComponent);

		when(this.parentComponent.getWindow()).thenReturn(this.window);

	}

	private void initializeBreedingViewInput() {
		this.input = new BreedingViewInput();
		this.input.setStudyId(STUDY_ID);
		this.input.setDatasetId(DATASET_ID);
	}

	@Test
	public void testButtonListeners() {
		this.ssaPanel.instantiateActionButtons();
		this.ssaPanel.initializeActions();

		Assert.assertNotNull(this.ssaPanel.getBtnBack().getListeners(ClickEvent.class));
		Assert.assertNotNull(this.ssaPanel.getBtnReset().getListeners(ClickEvent.class));
		final Collection<?> runBtnListeners = this.ssaPanel.getBtnRun().getListeners(ClickEvent.class);
		Assert.assertNotNull(runBtnListeners);
		Assert.assertTrue(runBtnListeners.iterator().next() instanceof RunBreedingViewButtonClickListener);
		final Collection<?> uploadBtnListeners = this.ssaPanel.getBtnUpload().getListeners(ClickEvent.class);
		Assert.assertNotNull(uploadBtnListeners);
		Assert.assertTrue(uploadBtnListeners.iterator().next() instanceof UploadBVFilesButtonClickListener);
	}

	@Test
	public void testReset() {
		this.ssaPanel.reset();
		Mockito.verify(this.environmentsComponent).reset();
		Mockito.verify(this.designDetailsComponent).reset();
		Mockito.verify(this.genotypesComponent).selectFirstItem();
		Mockito.verify(this.studyDetailsComponent).setAnalysisName();

	}

	@Test
	public void testButtonsLayoutWhenServerAppConfigured() {
		this.ssaPanel.instantiateActionButtons();
		this.ssaPanel.setIsServerApp("true");
		this.ssaPanel.updateLabels();
		Mockito.verify(this.messageSource).setCaption(this.ssaPanel.getBtnRun(), Message.DOWNLOAD_INPUT_FILES);
		Assert.assertTrue(this.ssaPanel.getBtnUpload().isVisible());
		Assert.assertEquals("Upload Output Files to BMS", this.ssaPanel.getBtnUpload().getCaption());
	}

	@Test
	public void testButtonsLayoutWhenStandaloneConfigured() {
		this.ssaPanel.instantiateActionButtons();
		this.ssaPanel.setIsServerApp("false");
		this.ssaPanel.updateLabels();
		Mockito.verify(this.messageSource).setCaption(this.ssaPanel.getBtnRun(), Message.RUN_BREEDING_VIEW);
		Assert.assertFalse(this.ssaPanel.getBtnUpload().isVisible());
	}

	@Test
	public void testGetSelectedEnvironments() {
		this.ssaPanel.getSelectedEnvironments();
		Mockito.verify(this.environmentsComponent).getSelectedEnvironments();
	}

	@Test
	public void testReplicateFactorEnabled() {
		this.ssaPanel.replicateFactorEnabled();
		Mockito.verify(this.designDetailsComponent).replicateFactorEnabled();
	}

	@Test
	public void testGetSelDesignTypeValue() {
		this.ssaPanel.getSelDesignTypeValue();
		Mockito.verify(this.designDetailsComponent).getSelDesignTypeValue();
	}

	@Test
	public void testGetSelEnvFactorValue() {
		this.ssaPanel.getSelEnvFactorValue();
		Mockito.verify(this.environmentsComponent).getSelEnvFactorValue();
	}

	@Test
	public void testGetTxtAnalysisNameValue() {
		this.ssaPanel.getTxtAnalysisNameValue();
		Mockito.verify(this.studyDetailsComponent).getTxtAnalysisName();
	}

	@Test
	public void testGetSelReplicatesValue() {
		this.ssaPanel.getSelReplicatesValue();
		Mockito.verify(this.designDetailsComponent).getSelReplicatesValue();
	}

	@Test
	public void testGetSelBlocksValue() {
		this.ssaPanel.getSelBlocksValue();
		Mockito.verify(this.designDetailsComponent).getSelBlocksValue();
	}

	@Test
	public void testGetSelRowFactorValue() {
		this.ssaPanel.getSelRowFactorValue();
		Mockito.verify(this.designDetailsComponent).getSelRowFactorValue();
	}

	@Test
	public void testGetSelColumnFactorValue() {
		this.ssaPanel.getSelColumnFactorValue();
		Mockito.verify(this.designDetailsComponent).getSelColumnFactorValue();
	}

	@Test
	public void testGetSelGenotypesValue() {
		this.ssaPanel.getSelGenotypesValue();
		Mockito.verify(this.genotypesComponent).getSelGenotypesValue();
	}

	@Test
	public void testGetTblEnvironmentSelection() {
		this.ssaPanel.getTblEnvironmentSelection();
		Mockito.verify(this.environmentsComponent).getTblEnvironmentSelection();
	}

	@Test
	public void testEnvironmentContainsValidDataForAnalysis_ReturnTrue() {
		Mockito.doReturn(TermId.ENTRY_NO.name()).when(this.genotypesComponent).getSelGenotypesValue();
		final SeaEnvironmentModel model = new SeaEnvironmentModel();
		model.setLocationId(LOCATION_ID);
		Mockito.doReturn(true).when(this.studyDataManager).containsAtLeast2CommonEntriesWithValues(Matchers.anyInt(), Matchers.anyInt(),
				Matchers.anyInt());

		Assert.assertTrue(this.ssaPanel.environmentContainsValidDataForAnalysis(model));
		Mockito.verify(this.studyDataManager).containsAtLeast2CommonEntriesWithValues(DATASET_ID, LOCATION_ID, TermId.ENTRY_NO.getId());
	}

	@Test
	public void testEnvironmentContainsValidDataForAnalysis_ReturnFalse() {
		Mockito.doReturn(TermId.ENTRY_NO.name()).when(this.genotypesComponent).getSelGenotypesValue();
		final SeaEnvironmentModel model = new SeaEnvironmentModel();
		model.setLocationId(LOCATION_ID);
		Mockito.doReturn(false).when(this.studyDataManager).containsAtLeast2CommonEntriesWithValues(Matchers.anyInt(), Matchers.anyInt(),
				Matchers.anyInt());

		Assert.assertFalse(this.ssaPanel.environmentContainsValidDataForAnalysis(model));
		Mockito.verify(this.studyDataManager).containsAtLeast2CommonEntriesWithValues(DATASET_ID, LOCATION_ID, TermId.ENTRY_NO.getId());
	}
	
	private List<DMSVariableType> createTestFactors() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		int rank = 1;
		final StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		factors.add(new DMSVariableType(TermId.ENTRY_NO.name(), TermId.ENTRY_NO.name(), entryNoVariable, rank++));

		final StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType(TermId.GID.name(), TermId.ENTRY_NO.name(), gidVariable, rank++));

		final StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, rank++));

		final StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name()));
		factors.add(new DMSVariableType(TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name(), entryTypeVariable, rank++));

		final StandardVariable obsUnitIdVariable = new StandardVariable();
		obsUnitIdVariable.setId(TermId.OBS_UNIT_ID.getId());
		obsUnitIdVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		obsUnitIdVariable.setProperty(new Term(1, TermId.OBS_UNIT_ID.name(), TermId.OBS_UNIT_ID.name()));
		factors.add(new DMSVariableType(TermId.OBS_UNIT_ID.name(), TermId.OBS_UNIT_ID.name(), obsUnitIdVariable, rank++));

		final StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new DMSVariableType(TermId.REP_NO.name(), TermId.REP_NO.name(), repVariable, rank++));

		final StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new DMSVariableType(TermId.BLOCK_NO.name(), TermId.BLOCK_NO.name(), blockVariable, rank++));

		final StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.ROW_FACTOR, "ROW_NO"));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, rank++));

		final StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.COLUMN_FACTOR, "COL_NO"));
		factors.add(new DMSVariableType(TermId.COLUMN_NO.name(), TermId.COLUMN_NO.name(), columnVariable, rank++));

		return factors;
	}

	private List<DMSVariableType> createStudyVariables() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		final StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(
				new Term(1, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, trialInstanceVar, 1));

		final StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN, SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN,
				exptDesignVar, 2));

		final StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_NAME, SingleSiteAnalysisDetailsPanelTest.DATASET_NAME,
				datasetNameVar, 3));

		final StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE, SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE,
				datasetTitleVar, 4));

		final StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE, SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE,
				datasetTypeVar, 5));

		return factors;
	}

}

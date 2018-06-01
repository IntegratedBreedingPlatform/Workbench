package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DMSVariableTypeTestDataInitializer;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 12/17/2014 Time:
 * 1:39 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class SingleSiteAnalysisDetailsPanelTest {

	private static final String LOCATION_NAME = "LOCATION_NAME";
	private static final String DATASET_TYPE = "DATASET_TYPE";
	private static final String DATASET_TITLE = "DATASET_TITLE";
	private static final String DATASET_NAME = "DATASET_NAME";
	private static final String LOC_ID = "LOC_ID";
	private static final String LOC_NAME = "LOC_NAME";
	private static final String EXPT_DESIGN = "EXPT_DESIGN";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	private static final String DEFAULT_REPLICATES = "REPLICATES";

	private static final String ROW_FACTOR_LABEL = "Specify Row Factor";
	private static final String COLUMN_FACTOR_LABEL = "Specify Column Factor";

	private static final String BLOCK_NO = "BLOCK_NO";

	private static final String[] TRIAL_ENV_FACTORS =
			{SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, SingleSiteAnalysisDetailsPanelTest.LOC_ID,
					SingleSiteAnalysisDetailsPanelTest.LOC_NAME, SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN};
	private static final String[] DATASET_FACTORS =
			{SingleSiteAnalysisDetailsPanelTest.DATASET_NAME, SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE,
					SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE};
	public static final int DATASET_ID = 3;
	public static final int STUDY_ID = 1;

	@InjectMocks
	private SingleSiteAnalysisDetailsPanel ssaPanel;

	private List<DMSVariableType> factors;
	private List<DMSVariableType> studyFactors;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private BreedingViewInput input;

	@Mock
	private StudyDataManager studyDataManager;

	@Mock
	private Component parentComponent;

	@Mock
	private Window window;

	@Before
	public void setup() {
		this.initializeBreedingViewInput();
		this.factors = this.createTestFactors();
		this.studyFactors = this.createStudyVariables();

		final Project project = new Project();
		this.ssaPanel = new SingleSiteAnalysisDetailsPanel(new Tool(), this.input, this.factors, this.studyFactors, project,
				new SingleSiteAnalysisPanel(project));
		this.ssaPanel.setMessageSource(this.messageSource);
		this.ssaPanel.setStudyDataManager(this.studyDataManager);
		this.ssaPanel.setParent(this.parentComponent);

		final Select selEnvFactor = new Select();
		selEnvFactor.addItem(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);
		selEnvFactor.setValue(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);
		this.ssaPanel.setSelEnvFactor(selEnvFactor);

		this.mockStudyDataManagerCalls();
		this.mockMessageResource();

		when(parentComponent.getWindow()).thenReturn(this.window);

	}

	private void mockMessageResource() {
		when(this.messageSource.getMessage(Message.PLEASE_CHOOSE)).thenReturn("Please choose");
		when(this.messageSource.getMessage(Message.BV_SPECIFY_ROW_FACTOR)).thenReturn(SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL);
		when(this.messageSource.getMessage(Message.BV_SPECIFY_COLUMN_FACTOR))
				.thenReturn(SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL);
	}

	private void mockStudyDataManagerCalls() {
		final DataSet dataset = new DataSet();
		final VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.setVariableTypes(this.factors);
		dataset.setVariableTypes(variableTypes);
		when(this.studyDataManager.getDataSet(this.input.getDatasetId())).thenReturn(dataset);

		final TrialEnvironments environments = new TrialEnvironments();
		final TrialEnvironment trialEnvironment = new TrialEnvironment(2);
		environments.add(trialEnvironment);
		when(this.studyDataManager.getTrialEnvironmentsInDataset(this.input.getDatasetId())).thenReturn(environments);
	}

	private void initializeBreedingViewInput() {
		this.input = new BreedingViewInput();
		this.input.setStudyId(STUDY_ID);
		this.input.setDatasetId(DATASET_ID);
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableNonLatin() {

		when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));

		Assert.assertEquals(this.ssaPanel.getSelDesignType().getValue(), DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty()) && !this.ssaPanel
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES,
					this.ssaPanel.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableLatin() {
		when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));

		Assert.assertEquals(this.ssaPanel.getSelDesignType().getValue(), DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN.getName());

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty()) && !this.ssaPanel
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES,
					this.ssaPanel.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignLatin() {
		when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelBlocks()));

		Assert.assertEquals(this.ssaPanel.getSelDesignType().getValue(), DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty()) && !this.ssaPanel
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES,
					this.ssaPanel.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignNonLatin() {
		when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelBlocks()));

		Assert.assertEquals(this.ssaPanel.getSelDesignType().getValue(), DesignType.RESOLVABLE_ROW_COLUMN_DESIGN.getName());

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty()) && !this.ssaPanel
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES,
					this.ssaPanel.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeRandomizedBlockDesign() {
		when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));

		this.ssaPanel.initializeComponents();

		final List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelBlocks()));

		Assert.assertEquals(this.ssaPanel.getSelDesignType().getValue(), DesignType.RANDOMIZED_BLOCK_DESIGN.getName());

		if ((!this.ssaPanel.getSelReplicates().isEnabled() || this.ssaPanel.getSelReplicates().getItemIds().isEmpty()) && !this.ssaPanel
				.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.ssaPanel.getSelReplicates().isEnabled());
			for (final Object itemId : this.ssaPanel.getSelBlocks().getItemIds()) {
				Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES,
					this.ssaPanel.getSelReplicates().getItemCaption(itemId));
			}
		}
	}

	@Test
	public void testDesignTypeInvalid() {
		when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(null);

		this.ssaPanel.initializeComponents();

		Assert.assertNull(this.ssaPanel.getSelDesignType().getValue());
	}

	@Test
	public void testPopulateChoicesForGenotypes() {
		this.ssaPanel.setSelGenotypes(new Select());
		this.ssaPanel.populateChoicesForGenotypes();
		Assert.assertEquals("Genotypes dropdown should have 3 factors", 3, this.ssaPanel.getSelGenotypes().getItemIds().size());
		for (final Object id : this.ssaPanel.getSelGenotypes().getItemIds()) {
			final String localName = (String) id;
			Assert.assertFalse("Entry Type factor should not be included in Genotypes dropdown",
					TermId.ENTRY_TYPE.name().equals(localName));
			Assert.assertFalse("Plot ID factor should not be included in Genotypes dropdown", TermId.PLOT_ID.name().equals(localName));
		}
	}

	@Test
	public void testPopulateChoicesForReplicates() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.studyFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select repSelect = new Select();
		Mockito.doReturn(repSelect).when(mockSSAPanel).getSelReplicates();

		mockSSAPanel.populateChoicesForReplicates();
		Assert.assertEquals("Dropdown should have 1 factor", 1, repSelect.getItemIds().size());
		Assert.assertNotNull(repSelect.getItem("REP_NO"));
	}

	@Test
	public void testPopulateChoicesForBlocks() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.studyFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select blockSelect = new Select();
		Mockito.doReturn(blockSelect).when(mockSSAPanel).getSelBlocks();

		mockSSAPanel.populateChoicesForBlocks();
		Assert.assertEquals("Dropdown should have 1 factor", 1, blockSelect.getItemIds().size());
		Assert.assertNotNull(blockSelect.getItem("BLOCK_NO"));
	}

	@Test
	public void testPopulateChoicesForRowFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.studyFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select rowSelect = new Select();
		Mockito.doReturn(rowSelect).when(mockSSAPanel).getSelRowFactor();

		mockSSAPanel.populateChoicesForRowFactor();
		Assert.assertEquals("Dropdown should have 1 factor", 1, rowSelect.getItemIds().size());
		Assert.assertNotNull(rowSelect.getItem("ROW_NO"));
	}

	@Test
	public void testPopulateChoicesForEnvForAnalysis() {
		this.ssaPanel.setTrialVariablesInDataset(DMSVariableTypeTestDataInitializer.createDMSVariableTypeList());
		this.ssaPanel.setFooterCheckBox(new CheckBox("Select All", false));
		this.ssaPanel.setEnvironmentsCheckboxState(new HashMap<String, Boolean>());

		this.ssaPanel.createEnvironmentSelectionTable();
		final TrialEnvironments environments = new TrialEnvironments();
		final TrialEnvironment environment = Mockito.mock(TrialEnvironment.class);
		when(environment.getId()).thenReturn(1);
		environments.add(environment);

		final VariableList variableList = Mockito.mock(VariableList.class);
		when(environment.getVariables()).thenReturn(variableList);
		final Variable trialInstance = new Variable();
		trialInstance.setValue("1");
		when(variableList.findByLocalName(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE)).thenReturn(trialInstance);
		when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(environments);
		when(this.studyDataManager.getLocalNameByStandardVariableId(Matchers.anyInt(), Matchers.anyInt()))
				.thenReturn(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);

		this.ssaPanel.populateChoicesForEnvForAnalysis();
		Assert.assertFalse("The footer checkbox value should be false", this.ssaPanel.getFooterCheckBox().booleanValue());
		Assert.assertEquals("The environment check box state's size should be 0", 0, this.ssaPanel.getEnvironmentsCheckboxState().size());
		Assert.assertEquals("The trial instance name should be TRIAL_INSTANCE", SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				this.ssaPanel.getBreedingViewInput().getTrialInstanceName());
	}

	@Test
	public void testPopulateEnvironmentSelectionTableWithTrialEnvironmets() {
		final Table table = new Table();
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");

		final TrialEnvironments environments = new TrialEnvironments();
		final TrialEnvironment environment = Mockito.mock(TrialEnvironment.class);
		when(environment.getId()).thenReturn(1);
		environments.add(environment);

		final VariableList variableList = Mockito.mock(VariableList.class);
		when(environment.getVariables()).thenReturn(variableList);
		final Variable trialInstance = new Variable();
		trialInstance.setValue("1");
		when(variableList.findByLocalName(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE)).thenReturn(trialInstance);
		final Variable locationVariable = new Variable();
		locationVariable.setValue("Africa Rice Center");
		when(variableList.findByLocalName(SingleSiteAnalysisDetailsPanelTest.LOCATION_NAME)).thenReturn(locationVariable);
		when(this.studyDataManager.getTrialEnvironmentsInDataset(Matchers.anyInt())).thenReturn(environments);

		this.ssaPanel.populateEnvironmentSelectionTableWithTrialEnvironmets(table, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.LOCATION_NAME);
		final BeanItemContainer<SeaEnvironmentModel> container = (BeanItemContainer<SeaEnvironmentModel>) table.getContainerDataSource();
		final SeaEnvironmentModel bean = container.getIdByIndex(0);
		Assert.assertFalse("The active value should be false", bean.getActive());
		Assert.assertEquals("The environment name should be Africa Rice Center", "Africa Rice Center", bean.getEnvironmentName());
		Assert.assertEquals("The study no should be 1", "1", bean.getTrialno());
		Assert.assertEquals("The location id should be 1", "1", bean.getLocationId().toString());
	}

	@Test
	public void testAdjustEnvironmentSelectionTableWhereTrialInstanceFactorNotSelectedEnvFactor() {
		final Table table = new Table();
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME, String.class, "");

		this.ssaPanel.adjustEnvironmentSelectionTable(table, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.LOCATION_NAME);
		Assert.assertEquals("There should be 3 visible columns", 3, table.getVisibleColumns().length);
		Assert.assertEquals("There should be 3 column headers", 3, table.getColumnHeaders().length);
		Assert.assertEquals("Select column's width should be 45.", 45, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN));
		Assert.assertEquals("Study No's width should be 60.", 60, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN));
		Assert.assertEquals("Environment Names's width should be 500.", 500,
				table.getColumnWidth(SingleSiteAnalysisDetailsPanel.ENVIRONMENT_NAME));
		Assert.assertEquals("Table's width should be 90.0.", "90.0", String.valueOf(table.getWidth()));

	}

	@Test
	public void testAdjustEnvironmentSelectionTableWhereTrialInstanceFactorIsTheSelectedEnvFactor() {
		final Table table = new Table();
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN, Select.class, "");
		table.addContainerProperty(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN, Integer.class, "");

		this.ssaPanel.adjustEnvironmentSelectionTable(table, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE);
		Assert.assertEquals("There should be 2 visible columns", 2, table.getVisibleColumns().length);
		Assert.assertEquals("There should be 2 column headers", 2, table.getColumnHeaders().length);
		Assert.assertEquals("Select column's width should be 45.", 45, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.SELECT_COLUMN));
		Assert.assertEquals("Study No's width should be -1.", -1, table.getColumnWidth(SingleSiteAnalysisDetailsPanel.TRIAL_NO_COLUMN));
		Assert.assertEquals("Table's width should be 45.0.", "45.0", String.valueOf(table.getWidth()));

	}

	@Test
	public void testPopulateChoicesForColumnFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.studyFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select columnSelect = new Select();
		Mockito.doReturn(columnSelect).when(mockSSAPanel).getSelColumnFactor();

		mockSSAPanel.populateChoicesForColumnFactor();
		Assert.assertEquals("Dropdown should have 1 factor", 1, columnSelect.getItemIds().size());
		Assert.assertNotNull(columnSelect.getItem("COLUMN_NO"));
	}

	@Test
	public void testPopulateChoicesForEnvironmentFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.studyFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		mockSSAPanel.setMessageSource(this.messageSource);
		final String pleaseChooseOption = "Please Choose";
		Mockito.doReturn(pleaseChooseOption).when(this.messageSource).getMessage(Message.PLEASE_CHOOSE);

		final Select envSelect = new Select();
		Mockito.doReturn(envSelect).when(mockSSAPanel).getSelEnvFactor();

		mockSSAPanel.populateChoicesForEnvironmentFactor();
		// "Please Choose" was added as dropdown item
		Assert.assertEquals("Dropdown should return fixed # of env factors", envSelect.getItemIds().size(),
			SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS.length + 1);
		for (final Object id : envSelect.getItemIds()) {
			final String localName = (String) id;
			Assert.assertTrue(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS, localName) || pleaseChooseOption
					.equals(localName));
			Assert.assertFalse(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.DATASET_FACTORS, localName));
		}
	}

	@Test
	public void testDisplayPRepDesignElements() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.displayPRepDesignElements();

		final List<Component> components = this.getComponentsListFromGridLayout();

		// The following components should be visible in Design Details' Grid
		// Layout
		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		// The following components should not be added in Design Details'
		// GridLayout
		Assert.assertFalse(components.contains(this.ssaPanel.getLblReplicates()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelReplicates()));

		Assert.assertNull("Replicates factor is not needed in P-rep design, so replicates should be unselected (null)",
				this.ssaPanel.getSelReplicates().getValue());

	}

	@Test
	public void testDisplayAugmentedDesignElements() {

		this.ssaPanel.initializeComponents();
		this.ssaPanel.displayAugmentedDesignElements();

		final List<Component> components = this.getComponentsListFromGridLayout();

		// The following components should be visible in Design Details' Grid
		// Layout
		Assert.assertTrue(components.contains(this.ssaPanel.getLblBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelBlocks()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.ssaPanel.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.ssaPanel.getSelGenotypes()));

		// The following components should not be added in Design Details'
		// GridLayout
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelRowFactor()));
		Assert.assertFalse(components.contains(this.ssaPanel.getLblReplicates()));
		Assert.assertFalse(components.contains(this.ssaPanel.getSelReplicates()));

		Assert.assertNull("Replicates factor is not needed in Augmented design, so replicates should be unselected (null)",
				this.ssaPanel.getSelReplicates().getValue());

	}

	@Test
	public void testSubstituteMissingReplicatesWithBlocksNoReplicatesFactor() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.getSelBlocks().addItem(SingleSiteAnalysisDetailsPanelTest.BLOCK_NO);
		this.ssaPanel.getSelReplicates().removeAllItems();

		this.ssaPanel.substituteMissingReplicatesWithBlocks();

		Assert.assertEquals("The value of Replicates Factor Select Field should be the same as the Block factor",
				SingleSiteAnalysisDetailsPanelTest.BLOCK_NO, this.ssaPanel.getSelReplicates().getValue());
		Assert.assertEquals("If block factor is used as a substitute for replicates, then the item caption should be \""
						+ SingleSiteAnalysisDetailsPanel.REPLICATES + "\"", SingleSiteAnalysisDetailsPanel.REPLICATES,
				this.ssaPanel.getSelReplicates().getItemCaption(this.ssaPanel.getSelReplicates().getValue()));

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypePRepDesign() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.changeRowAndColumnLabelsBasedOnDesignType(DesignType.P_REP_DESIGN);

		// Row and Column factors are optional in P-rep Design, the labels
		// should not have required field indicator (red asterisk '*')
		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL, this.ssaPanel.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL, this.ssaPanel.getLblSpecifyRowFactor().getValue());

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypeRowAndColumnDesign() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.changeRowAndColumnLabelsBasedOnDesignType(DesignType.RESOLVABLE_ROW_COLUMN_DESIGN);

		// Row and Column factors are required in Row-and-Column Design, the
		// labels should have a required field indicator (red asterisk
		// '*')
		Assert.assertEquals(
				SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR,
				this.ssaPanel.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR,
				this.ssaPanel.getLblSpecifyRowFactor().getValue());

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypeRowAndOtherDesign() {

		this.ssaPanel.initializeComponents();

		this.ssaPanel.changeRowAndColumnLabelsBasedOnDesignType(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN);

		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.COLUMN_FACTOR_LABEL, this.ssaPanel.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(SingleSiteAnalysisDetailsPanelTest.ROW_FACTOR_LABEL, this.ssaPanel.getLblSpecifyRowFactor().getValue());

	}

	@Test
	public void testEnvironmentCheckBoxListenerUnchecked() {

		final SeaEnvironmentModel model = new SeaEnvironmentModel();

		final CheckBox checkBox = new CheckBox();
		checkBox.setData(model);
		// Uncheck the checkbox
		checkBox.setValue(false);
		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		when(event.getProperty()).thenReturn(checkBox);

		final CheckBox footerCheckBox = Mockito.mock(CheckBox.class);
		this.ssaPanel.setFooterCheckBox(footerCheckBox);

		final SingleSiteAnalysisDetailsPanel.EnvironmentCheckBoxListener listener = this.ssaPanel.new EnvironmentCheckBoxListener();

		listener.valueChange(event);

		Assert.assertFalse(model.getActive());
		verify(footerCheckBox).removeListener(Mockito.any(Property.ValueChangeListener.class));
		verify(footerCheckBox).setValue(false);
		verify(footerCheckBox).addListener(Mockito.any(Property.ValueChangeListener.class));

	}

	@Test
	public void testEnvironmentCheckBoxListenerChecked() {

		final Integer locationId = 1;
		final SeaEnvironmentModel model = new SeaEnvironmentModel();
		model.setLocationId(1);

		final CheckBox checkBox = new CheckBox();
		checkBox.setData(model);
		// Check the checkbox
		checkBox.setValue(true);
		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		when(event.getProperty()).thenReturn(checkBox);

		final Select selectGenotype = Mockito.mock(Select.class);
		when(selectGenotype.getValue()).thenReturn("GID");
		this.ssaPanel.setSelGenotypes(selectGenotype);

		when(this.studyDataManager.containsAtLeast2CommonEntriesWithValues(DATASET_ID, locationId, TermId.GID.getId())).thenReturn(true);

		final SingleSiteAnalysisDetailsPanel.EnvironmentCheckBoxListener listener = this.ssaPanel.new EnvironmentCheckBoxListener();
		listener.valueChange(event);

		Assert.assertTrue(model.getActive());
		Assert.assertTrue((Boolean) checkBox.getValue());

	}

	@Test
	public void testEnvironmentCheckBoxListenerCheckedStudyHasNoData() {

		final Integer locationId = 1234;
		final String trialInstanceNumber = "1";
		final SeaEnvironmentModel model = new SeaEnvironmentModel();
		model.setLocationId(locationId);
		model.setTrialno(trialInstanceNumber);
		model.setEnvironmentName(trialInstanceNumber);

		final CheckBox checkBox = new CheckBox();
		checkBox.setData(model);
		// Check the checkbox
		checkBox.setValue(true);
		final Property.ValueChangeEvent event = Mockito.mock(Property.ValueChangeEvent.class);
		when(event.getProperty()).thenReturn(checkBox);

		final Select selectGenotype = Mockito.mock(Select.class);
		when(selectGenotype.getValue()).thenReturn("GID");
		this.ssaPanel.setSelGenotypes(selectGenotype);

		when(this.studyDataManager.containsAtLeast2CommonEntriesWithValues(DATASET_ID, locationId, TermId.GID.getId())).thenReturn(false);

		final SingleSiteAnalysisDetailsPanel.EnvironmentCheckBoxListener listener = this.ssaPanel.new EnvironmentCheckBoxListener();

		listener.valueChange(event);

		Assert.assertFalse(model.getActive());
		Assert.assertFalse((Boolean) checkBox.getValue());

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);

		verify(this.window).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();

		Assert.assertEquals("Invalid Selection", notification.getCaption());
		Assert.assertEquals(
				"</br>TRIAL_INSTANCE \"1\" cannot be used for analysis because the plot data is not complete. The data must contain at least 2 common entries with values.",
				notification.getDescription());

	}

	private List<Component> getComponentsListFromGridLayout() {

		final GridLayout gLayout = (GridLayout) this.ssaPanel.getDesignDetailsContainer().getComponentIterator().next();
		final Iterator<Component> componentsIterator = gLayout.getComponentIterator();
		final List<Component> components = new ArrayList<>();
		while (componentsIterator.hasNext()) {
			final Component component = componentsIterator.next();
			components.add(component);
		}

		return components;
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

		final StandardVariable plotIdVariable = new StandardVariable();
		plotIdVariable.setId(TermId.PLOT_ID.getId());
		plotIdVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		plotIdVariable.setProperty(new Term(1, TermId.PLOT_ID.name(), TermId.PLOT_ID.name()));
		factors.add(new DMSVariableType(TermId.PLOT_ID.name(), TermId.PLOT_ID.name(), plotIdVariable, rank++));

		final StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new DMSVariableType(TermId.REP_NO.name(), TermId.REP_NO.name(), repVariable, rank++));

		final StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new DMSVariableType(TermId.BLOCK_NO.name(), TermId.BLOCK_NO.name(), blockVariable, rank++));

		final StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.ROW_FACTOR, "ROW_NO"));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, rank++));

		final StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.COLUMN_FACTOR, "COL_NO"));
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
		factors.add(
				new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
						trialInstanceVar, 1));

		final StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN, SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN,
				exptDesignVar, 2));

		final StandardVariable locNameVar = new StandardVariable();
		locNameVar.setId(TermId.SITE_NAME.getId());
		locNameVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locNameVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(
				new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.LOC_NAME, SingleSiteAnalysisDetailsPanelTest.LOC_NAME, locNameVar,
						3));

		final StandardVariable locIDVar = new StandardVariable();
		locIDVar.setId(TermId.LOCATION_ID.getId());
		locIDVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locIDVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.LOC_ID, SingleSiteAnalysisDetailsPanelTest.LOC_ID, locIDVar, 4));

		final StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_NAME, SingleSiteAnalysisDetailsPanelTest.DATASET_NAME,
				datasetNameVar, 5));

		final StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE, SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE,
				datasetTitleVar, 6));

		final StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE, SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE,
				datasetTypeVar, 7));

		return factors;
	}

}

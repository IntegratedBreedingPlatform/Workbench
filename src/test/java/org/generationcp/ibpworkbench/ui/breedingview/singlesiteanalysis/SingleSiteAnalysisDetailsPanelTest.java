
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Select;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 12/17/2014 Time: 1:39 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class SingleSiteAnalysisDetailsPanelTest {

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


	private static final String[] TRIAL_ENV_FACTORS = {SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
			SingleSiteAnalysisDetailsPanelTest.LOC_ID, SingleSiteAnalysisDetailsPanelTest.LOC_NAME,
			SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN};
	private static final String[] DATASET_FACTORS = {SingleSiteAnalysisDetailsPanelTest.DATASET_NAME,
			SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE, SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE};

	@InjectMocks
	private SingleSiteAnalysisDetailsPanel dut;

	private List<DMSVariableType> factors;
	private List<DMSVariableType> trialFactors;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	private BreedingViewInput input;

	@Mock
	private StudyDataManager studyDataManager;

	@Before
	public void setup() {
		this.initializeBreedingViewInput();
		this.factors = this.createTestFactors();
		this.trialFactors = this.createTrialVariables();

		final Select selEnvFactor = new Select();
		selEnvFactor.setValue("TRIAL INSTANCE");
		this.dut.setSelEnvFactor(selEnvFactor);

		final Project project = new Project();
		this.dut =
				new SingleSiteAnalysisDetailsPanel(new Tool(), this.input, this.factors, this.trialFactors, project,
						new SingleSiteAnalysisPanel(project, null));
		this.dut.setMessageSource(this.messageSource);
		this.dut.setStudyDataManager(this.studyDataManager);

		this.mockStudyDataManagerCalls();
		this.mockMessageResource();

	}

	private void mockMessageResource() {
		Mockito.when(this.messageSource.getMessage(Message.PLEASE_CHOOSE)).thenReturn("Please choose");
		Mockito.when(this.messageSource.getMessage(Message.BV_SPECIFY_ROW_FACTOR)).thenReturn(ROW_FACTOR_LABEL);
		Mockito.when(this.messageSource.getMessage(Message.BV_SPECIFY_COLUMN_FACTOR)).thenReturn(COLUMN_FACTOR_LABEL);
	}

	private void mockStudyDataManagerCalls() {
		final DataSet dataset = new DataSet();
		final VariableTypeList variableTypes = new VariableTypeList();
		variableTypes.setVariableTypes(this.factors);
		dataset.setVariableTypes(variableTypes);
		Mockito.when(this.studyDataManager.getDataSet(this.input.getDatasetId())).thenReturn(dataset);

		final TrialEnvironments trialEnvironments = new TrialEnvironments();
		final TrialEnvironment trialEnvironment = new TrialEnvironment(2);
		trialEnvironments.add(trialEnvironment);
		Mockito.when(this.studyDataManager.getTrialEnvironmentsInDataset(this.input.getDatasetId())).thenReturn(trialEnvironments);
	}

	private void initializeBreedingViewInput() {
		this.input = new BreedingViewInput();
		this.input.setStudyId(1);
		this.input.setDatasetId(3);
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableNonLatin() {

		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()));

		this.dut.initializeComponents();

		List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.dut.getLblBlocks()));
		Assert.assertTrue(components.contains(this.dut.getSelBlocks()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.dut.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.dut.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.dut.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.dut.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.dut.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.dut.getSelRowFactor()));

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (final Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()));

		this.dut.initializeComponents();

		List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.dut.getLblBlocks()));
		Assert.assertTrue(components.contains(this.dut.getSelBlocks()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.dut.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.dut.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.dut.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.dut.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.dut.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.dut.getSelRowFactor()));

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (final Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()));

		this.dut.initializeComponents();

		List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.dut.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.dut.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.dut.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.dut.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.dut.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.dut.getLblBlocks()));
		Assert.assertFalse(components.contains(this.dut.getSelBlocks()));

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.ROW_COLUMN_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (final Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignNonLatin() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()));

		this.dut.initializeComponents();

		List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.dut.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.dut.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.dut.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.dut.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.dut.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.dut.getLblBlocks()));
		Assert.assertFalse(components.contains(this.dut.getSelBlocks()));

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.ROW_COLUMN_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (final Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRandomizedBlockDesign() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(Integer.toString(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()));

		this.dut.initializeComponents();

		List<Component> components = this.getComponentsListFromGridLayout();

		Assert.assertTrue(components.contains(this.dut.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.dut.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.dut.getSelGenotypes()));

		Assert.assertFalse(components.contains(this.dut.getLblSpecifyColumnFactor()));
		Assert.assertFalse(components.contains(this.dut.getSelColumnFactor()));
		Assert.assertFalse(components.contains(this.dut.getLblSpecifyRowFactor()));
		Assert.assertFalse(components.contains(this.dut.getSelRowFactor()));
		Assert.assertFalse(components.contains(this.dut.getLblBlocks()));
		Assert.assertFalse(components.contains(this.dut.getSelBlocks()));

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (final Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeInvalid() {
		Mockito.when(this.studyDataManager.getGeolocationPropValue(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), this.input.getStudyId()))
				.thenReturn(null);

		this.dut.initializeComponents();

		Assert.assertNull(this.dut.getSelDesignType().getValue());
	}

	@Test
	public void testPopulateChoicesForGenotypes() {
		this.dut.setSelGenotypes(new Select());
		this.dut.populateChoicesForGenotypes();
		Assert.assertTrue("Dropdown should have 3 factors", this.dut.getSelGenotypes().getItemIds().size() == 3);
		for (final Object id : this.dut.getSelGenotypes().getItemIds()) {
			final String localName = (String) id;
			Assert.assertFalse("Entry Type factor not included in dropdown", "ENTRY_TYPE".equals(localName));
		}
	}

	@Test
	public void testPopulateChoicesForReplicates() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select repSelect = new Select();
		Mockito.doReturn(repSelect).when(mockSSAPanel).getSelReplicates();

		mockSSAPanel.populateChoicesForReplicates();
		Assert.assertTrue("Dropdown should have 1 factor", repSelect.getItemIds().size() == 1);
		Assert.assertNotNull(repSelect.getItem("REP_NO"));
	}

	@Test
	public void testPopulateChoicesForBlocks() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select blockSelect = new Select();
		Mockito.doReturn(blockSelect).when(mockSSAPanel).getSelBlocks();

		mockSSAPanel.populateChoicesForBlocks();
		Assert.assertTrue("Dropdown should have 1 factor", blockSelect.getItemIds().size() == 1);
		Assert.assertNotNull(blockSelect.getItem("BLOCK_NO"));
	}

	@Test
	public void testPopulateChoicesForRowFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select rowSelect = new Select();
		Mockito.doReturn(rowSelect).when(mockSSAPanel).getSelRowFactor();

		mockSSAPanel.populateChoicesForRowFactor();
		Assert.assertTrue("Dropdown should have 1 factor", rowSelect.getItemIds().size() == 1);
		Assert.assertNotNull(rowSelect.getItem("ROW_NO"));
	}

	@Test
	public void testPopulateChoicesForColumnFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		final Select columnSelect = new Select();
		Mockito.doReturn(columnSelect).when(mockSSAPanel).getSelColumnFactor();

		mockSSAPanel.populateChoicesForColumnFactor();
		Assert.assertTrue("Dropdown should have 1 factor", columnSelect.getItemIds().size() == 1);
		Assert.assertNotNull(columnSelect.getItem("COLUMN_NO"));
	}

	@Test
	public void testPopulateChoicesForEnvironmentFactor() {
		final SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null);
		final SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		mockSSAPanel.setMessageSource(this.messageSource);
		final String pleaseChooseOption = "Please Choose";
		Mockito.doReturn(pleaseChooseOption).when(this.messageSource).getMessage(Message.PLEASE_CHOOSE);

		final Select envSelect = new Select();
		Mockito.doReturn(envSelect).when(mockSSAPanel).getSelEnvFactor();

		mockSSAPanel.populateChoicesForEnvironmentFactor();
		// "Please Choose" was added as dropdown item
		Assert.assertTrue("Dropdown should return fixed # of env factors",
				envSelect.getItemIds().size() == SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS.length + 1);
		for (final Object id : envSelect.getItemIds()) {
			final String localName = (String) id;
			Assert.assertTrue(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS, localName)
					|| pleaseChooseOption.equals(localName));
			Assert.assertFalse(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.DATASET_FACTORS, localName));
		}
	}


	@Test
	public void testDisplayPRepDesignElements() {

		this.dut.initializeComponents();

		this.dut.displayPRepDesignElements();

		List<Component> components = this.getComponentsListFromGridLayout();

		// The following components should be visible in Design Details' Grid Layout
		Assert.assertTrue(components.contains(this.dut.getLblBlocks()));
		Assert.assertTrue(components.contains(this.dut.getSelBlocks()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyColumnFactor()));
		Assert.assertTrue(components.contains(this.dut.getSelColumnFactor()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyRowFactor()));
		Assert.assertTrue(components.contains(this.dut.getSelRowFactor()));
		Assert.assertTrue(components.contains(this.dut.getLblSpecifyGenotypesHeader()));
		Assert.assertTrue(components.contains(this.dut.getLblGenotypes()));
		Assert.assertTrue(components.contains(this.dut.getSelGenotypes()));

		// The following components should not be added in Design Details' GridLayout
		Assert.assertFalse(components.contains(this.dut.getLblReplicates()));
		Assert.assertFalse(components.contains(this.dut.getSelReplicates()));

		Assert.assertNull("Replicates factor is not needed in P-rep design, so replicates should be unselected (null)",this.dut.getSelReplicates().getValue());

	}



	@Test
	public void testSubstituteMissingReplicatesWithBlocksNoReplicatesFactor(){

		this.dut.initializeComponents();

		dut.getSelBlocks().addItem(BLOCK_NO);
		dut.getSelReplicates().removeAllItems();

		dut.substituteMissingReplicatesWithBlocks();

		Assert.assertEquals("The value of Replicates Factor Select Field should be the same as the Block factor", BLOCK_NO, dut.getSelReplicates().getValue());
		Assert.assertEquals("If block factor is used as a substitute for replicates, then the item caption should be \"" + SingleSiteAnalysisDetailsPanel.REPLICATES + "\"" ,SingleSiteAnalysisDetailsPanel.REPLICATES ,dut.getSelReplicates().getItemCaption((String) dut.getSelReplicates().getValue()));

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypePRepDesign(){

		this.dut.initializeComponents();

		dut.changeRowAndColumnLabelsBasedOnDesignType(DesignType.P_REP_DESIGN);

		// Row and Column factors are optional in P-rep Design, the labels should not have required field indicator (red asterisk '*')
		Assert.assertEquals(COLUMN_FACTOR_LABEL, dut.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(ROW_FACTOR_LABEL, dut.getLblSpecifyRowFactor().getValue());

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypeRowAndColumnDesign(){

		this.dut.initializeComponents();

		dut.changeRowAndColumnLabelsBasedOnDesignType(DesignType.ROW_COLUMN_DESIGN);

		// Row and Column factors are required in Row-and-Column Design, the labels should have a required field indicator (red asterisk '*')
		Assert.assertEquals(COLUMN_FACTOR_LABEL + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR, dut.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(ROW_FACTOR_LABEL + SingleSiteAnalysisDetailsPanel.REQUIRED_FIELD_INDICATOR, dut.getLblSpecifyRowFactor().getValue());

	}

	@Test
	public void testChangeRowAndColumnLabelsBasedOnDesignTypeRowAndOtherDesign(){

		this.dut.initializeComponents();

		dut.changeRowAndColumnLabelsBasedOnDesignType(DesignType.RESOLVABLE_INCOMPLETE_BLOCK_DESIGN);

		Assert.assertEquals(COLUMN_FACTOR_LABEL, dut.getLblSpecifyColumnFactor().getValue());
		Assert.assertEquals(ROW_FACTOR_LABEL, dut.getLblSpecifyRowFactor().getValue());

	}

	private List<Component> getComponentsListFromGridLayout() {

		final GridLayout gLayout = (GridLayout) this.dut.getDesignDetailsContainer().getComponentIterator().next();
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

		final StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		factors.add(new DMSVariableType("ENTRY_NO", "ENTRY_NO", entryNoVariable, 1));

		final StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("GID", "GID", gidVariable, 2));

		final StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, 3));

		final StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, "ENTRY TYPE", "ENTRY_TYPE"));
		factors.add(new DMSVariableType("ENTRY_TYPE", "ENTRY_TYPE", entryTypeVariable, 4));

		final StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new DMSVariableType("REP_NO", "REP_NO", repVariable, 5));

		final StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new DMSVariableType("BLOCK_NO", "BLOCK_NO", blockVariable, 6));

		final StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.ROW_FACTOR, "ROW_NO"));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, 7));

		final StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.COLUMN_FACTOR, "COL_NO"));
		factors.add(new DMSVariableType("COLUMN_NO", "COLUMN_NO", columnVariable, 8));

		return factors;
	}

	private List<DMSVariableType> createTrialVariables() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		final StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(new Term(1, "TRIAL INSTANCE", "TRIAL INSTANCE"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, trialInstanceVar, 1));

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
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.LOC_NAME, SingleSiteAnalysisDetailsPanelTest.LOC_NAME,
				locNameVar, 3));

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

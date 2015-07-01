
package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import com.vaadin.ui.Select;
import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

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

	private static final String[] TRIAL_ENV_FACTORS = {SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
			SingleSiteAnalysisDetailsPanelTest.LOC_ID, SingleSiteAnalysisDetailsPanelTest.LOC_NAME,
			SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN};
	private static final String[] DATASET_FACTORS = {SingleSiteAnalysisDetailsPanelTest.DATASET_NAME,
			SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE, SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE};

	private SingleSiteAnalysisDetailsPanel dut;

	private List<DMSVariableType> factors;
	private List<DMSVariableType> trialFactors;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private BreedingViewInput input;

	@Before
	public void setup() {
		SingleSiteAnalysisDetailsPanel panel = new SingleSiteAnalysisDetailsPanel();
		this.dut = Mockito.spy(panel);
		this.dut.setMessageSource(this.messageSource);
		Mockito.doReturn(this.input).when(this.dut).getBreedingViewInput();
		Mockito.doNothing().when(this.dut).populateChoicesForEnvForAnalysis();
		Mockito.doNothing().when(this.dut).populateChoicesForReplicates();
		Mockito.doNothing().when(this.dut).populateChoicesForBlocks();
		Mockito.doNothing().when(this.dut).populateChoicesForRowFactor();
		Mockito.doNothing().when(this.dut).populateChoicesForColumnFactor();
		Mockito.doNothing().when(this.dut).refineChoicesForBlocksReplicationRowAndColumnFactos();
		Mockito.doNothing().when(this.dut).populateChoicesForGenotypes();
		Mockito.when(this.input.getVersion()).thenReturn(null);

		this.factors = this.createTestFactors();
		this.trialFactors = this.createTrialVariables();
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableNonLatin() {

		Mockito.doReturn(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()).when(this.dut).retrieveExperimentalDesignTypeID();

		this.dut.initializeComponents();

		Mockito.verify(this.dut).displayIncompleteBlockDesignElements();

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableLatin() {

		Mockito.doReturn(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()).when(this.dut).retrieveExperimentalDesignTypeID();

		this.dut.initializeComponents();

		Mockito.verify(this.dut).displayIncompleteBlockDesignElements();

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignLatin() {
		Mockito.doReturn(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()).when(this.dut).retrieveExperimentalDesignTypeID();

		this.dut.initializeComponents();

		Mockito.verify(this.dut).displayRowColumnDesignElements();

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.ROW_COLUMN_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignNonLatin() {
		Mockito.doReturn(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()).when(this.dut).retrieveExperimentalDesignTypeID();

		this.dut.initializeComponents();

		Mockito.verify(this.dut).displayRowColumnDesignElements();

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.ROW_COLUMN_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRandomizedBlockDesign() {
		Mockito.doReturn(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()).when(this.dut).retrieveExperimentalDesignTypeID();

		this.dut.initializeComponents();

		Mockito.verify(this.dut).displayRandomizedBlockDesignElements();

		Assert.assertTrue(this.dut.getSelDesignType().getValue().equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()));

		if ((!this.dut.getSelReplicates().isEnabled() || this.dut.getSelReplicates().getItemIds().isEmpty())
				&& !this.dut.getSelBlocks().getItemIds().isEmpty()) {
			Assert.assertTrue(this.dut.getSelReplicates().isEnabled());
			for (Object itemId : this.dut.getSelBlocks().getItemIds()) {
				Assert.assertTrue(SingleSiteAnalysisDetailsPanelTest.DEFAULT_REPLICATES.equals(this.dut.getSelReplicates().getItemCaption(
						itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeInvalid() {
		Mockito.doReturn(0).when(this.dut).retrieveExperimentalDesignTypeID();

		this.dut.initializeComponents();

		Assert.assertNull(this.dut.getSelDesignType().getValue());
	}

	@Test
	public void testPopulateChoicesForGenotypes() {
		SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		Select genotypeSelect = new Select();
		Mockito.doReturn(genotypeSelect).when(mockSSAPanel).getSelGenotypes();

		mockSSAPanel.populateChoicesForGenotypes();
		Assert.assertTrue("Dropdown should have 3 factors", genotypeSelect.getItemIds().size() == 3);
		for (Object id : genotypeSelect.getItemIds()) {
			String localName = (String) id;
			Assert.assertFalse("Entry Type factor not included in dropdown", "ENTRY_TYPE".equals(localName));
		}
	}

	@Test
	public void testPopulateChoicesForReplicates() {
		SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		Select repSelect = new Select();
		Mockito.doReturn(repSelect).when(mockSSAPanel).getSelReplicates();

		mockSSAPanel.populateChoicesForReplicates();
		Assert.assertTrue("Dropdown should have 1 factor", repSelect.getItemIds().size() == 1);
		Assert.assertNotNull(repSelect.getItem("REP_NO"));
	}

	@Test
	public void testPopulateChoicesForBlocks() {
		SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		Select blockSelect = new Select();
		Mockito.doReturn(blockSelect).when(mockSSAPanel).getSelBlocks();

		mockSSAPanel.populateChoicesForBlocks();
		Assert.assertTrue("Dropdown should have 1 factor", blockSelect.getItemIds().size() == 1);
		Assert.assertNotNull(blockSelect.getItem("BLOCK_NO"));
	}

	@Test
	public void testPopulateChoicesForRowFactor() {
		SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		Select rowSelect = new Select();
		Mockito.doReturn(rowSelect).when(mockSSAPanel).getSelRowFactor();

		mockSSAPanel.populateChoicesForRowFactor();
		Assert.assertTrue("Dropdown should have 1 factor", rowSelect.getItemIds().size() == 1);
		Assert.assertNotNull(rowSelect.getItem("ROW_NO"));
	}

	@Test
	public void testPopulateChoicesForColumnFactor() {
		SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		Select columnSelect = new Select();
		Mockito.doReturn(columnSelect).when(mockSSAPanel).getSelColumnFactor();

		mockSSAPanel.populateChoicesForColumnFactor();
		Assert.assertTrue("Dropdown should have 1 factor", columnSelect.getItemIds().size() == 1);
		Assert.assertNotNull(columnSelect.getItem("COLUMN_NO"));
	}

	@Test
	public void testPopulateChoicesForEnvironmentFactor() {
		SingleSiteAnalysisDetailsPanel ssaPanel =
				new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), this.factors, this.trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = Mockito.spy(ssaPanel);
		mockSSAPanel.setMessageSource(this.messageSource);
		String pleaseChooseOption = "Please Choose";
		Mockito.doReturn(pleaseChooseOption).when(this.messageSource).getMessage(Message.PLEASE_CHOOSE);

		Select envSelect = new Select();
		Mockito.doReturn(envSelect).when(mockSSAPanel).getSelEnvFactor();

		mockSSAPanel.populateChoicesForEnvironmentFactor();
		// "Please Choose" was added as dropdown item
		Assert.assertTrue("Dropdown should return fixed # of env factors",
				envSelect.getItemIds().size() == SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS.length + 1);
		for (Object id : envSelect.getItemIds()) {
			String localName = (String) id;
			Assert.assertTrue(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.TRIAL_ENV_FACTORS, localName)
					|| pleaseChooseOption.equals(localName));
			Assert.assertFalse(ArrayUtils.contains(SingleSiteAnalysisDetailsPanelTest.DATASET_FACTORS, localName));
		}
	}

	private List<DMSVariableType> createTestFactors() {
		List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		factors.add(new DMSVariableType("ENTRY_NO", "ENTRY_NO", entryNoVariable, 1));

		StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("GID", "GID", gidVariable, 2));

		StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, 3));

		StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, "ENTRY TYPE", "ENTRY_TYPE"));
		factors.add(new DMSVariableType("ENTRY_TYPE", "ENTRY_TYPE", entryTypeVariable, 4));

		StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new DMSVariableType("REP_NO", "REP_NO", repVariable, 5));

		StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new DMSVariableType("BLOCK_NO", "BLOCK_NO", blockVariable, 6));

		StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.ROW_FACTOR, "ROW_NO"));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, 7));

		StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.COLUMN_FACTOR, "COL_NO"));
		factors.add(new DMSVariableType("COLUMN_NO", "COLUMN_NO", columnVariable, 8));

		return factors;
	}

	private List<DMSVariableType> createTrialVariables() {
		List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(new Term(1, "TRIAL INSTANCE", "TRIAL INSTANCE"));
		factors.add(
				new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE, SingleSiteAnalysisDetailsPanelTest.TRIAL_INSTANCE,
				trialInstanceVar, 1));

		StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN, SingleSiteAnalysisDetailsPanelTest.EXPT_DESIGN,
				exptDesignVar, 2));

		StandardVariable locNameVar = new StandardVariable();
		locNameVar.setId(TermId.SITE_NAME.getId());
		locNameVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locNameVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(
				new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.LOC_NAME, SingleSiteAnalysisDetailsPanelTest.LOC_NAME, locNameVar,
				3));

		StandardVariable locIDVar = new StandardVariable();
		locIDVar.setId(TermId.LOCATION_ID.getId());
		locIDVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locIDVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.LOC_ID, SingleSiteAnalysisDetailsPanelTest.LOC_ID, locIDVar, 4));

		StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_NAME, SingleSiteAnalysisDetailsPanelTest.DATASET_NAME,
				datasetNameVar, 5));

		StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE, SingleSiteAnalysisDetailsPanelTest.DATASET_TITLE,
				datasetTitleVar, 6));

		StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new DMSVariableType(SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE, SingleSiteAnalysisDetailsPanelTest.DATASET_TYPE,
				datasetTypeVar, 7));

		return factors;
	}

}

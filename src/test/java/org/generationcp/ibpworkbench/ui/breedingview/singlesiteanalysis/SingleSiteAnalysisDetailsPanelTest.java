package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Select;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 12/17/2014
 * Time: 1:39 PM
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
	
	private static final String[] TRIAL_ENV_FACTORS = {TRIAL_INSTANCE, LOC_ID, LOC_NAME, EXPT_DESIGN};
	private static final String[] DATASET_FACTORS = {DATASET_NAME, DATASET_TITLE, DATASET_TYPE};

	
	private SingleSiteAnalysisDetailsPanel dut;
	
	private List<VariableType> factors;
	private List<VariableType> trialFactors;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private BreedingViewInput input;

	@Before
	public void setup() {
		SingleSiteAnalysisDetailsPanel panel = new SingleSiteAnalysisDetailsPanel();
		dut = spy(panel);
		dut.setMessageSource(messageSource);
		doReturn(input).when(dut).getBreedingViewInput();
		doNothing().when(dut).populateChoicesForEnvForAnalysis();
		doNothing().when(dut).populateChoicesForReplicates();
		doNothing().when(dut).populateChoicesForBlocks();
		doNothing().when(dut).populateChoicesForRowFactor();
		doNothing().when(dut).populateChoicesForColumnFactor();
		doNothing().when(dut).refineChoicesForBlocksReplicationRowAndColumnFactos();
		doNothing().when(dut).populateChoicesForGenotypes();
		when(input.getVersion()).thenReturn(null);
		
		factors = createTestFactors();
		trialFactors = createTrialVariables();
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableNonLatin() {
		
		doReturn(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()).when(dut).retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayIncompleteBlockDesignElements();

		assertTrue(dut.getSelDesignType().getValue().equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));
		
		if ((!dut.getSelReplicates().isEnabled() || 
			dut.getSelReplicates().getItemIds().isEmpty()) && 
			!dut.getSelBlocks().getItemIds().isEmpty()) {
			assertTrue(dut.getSelReplicates().isEnabled());
			for (Object itemId : dut.getSelBlocks().getItemIds()) {
				assertTrue(DEFAULT_REPLICATES.equals(
						dut.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableLatin() {

		doReturn(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayIncompleteBlockDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));
		
		if ((!dut.getSelReplicates().isEnabled() || 
			dut.getSelReplicates().getItemIds().isEmpty()) && 
			!dut.getSelBlocks().getItemIds().isEmpty()) {
			assertTrue(dut.getSelReplicates().isEnabled());
			for (Object itemId : dut.getSelBlocks().getItemIds()) {
				assertTrue(DEFAULT_REPLICATES.equals(
						dut.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignLatin() {
		doReturn(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayRowColumnDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.ROW_COLUMN_DESIGN.getName()));
		
		if ((!dut.getSelReplicates().isEnabled() || 
			dut.getSelReplicates().getItemIds().isEmpty()) && 
			!dut.getSelBlocks().getItemIds().isEmpty()) {
			assertTrue(dut.getSelReplicates().isEnabled());
			for (Object itemId : dut.getSelBlocks().getItemIds()) {
				assertTrue(DEFAULT_REPLICATES.equals(
						dut.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRowColumnDesignNonLatin() {
		doReturn(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayRowColumnDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.ROW_COLUMN_DESIGN.getName()));
		
		if ((!dut.getSelReplicates().isEnabled() || 
			dut.getSelReplicates().getItemIds().isEmpty()) && 
			!dut.getSelBlocks().getItemIds().isEmpty()) {
			assertTrue(dut.getSelReplicates().isEnabled());
			for (Object itemId : dut.getSelBlocks().getItemIds()) {
				assertTrue(DEFAULT_REPLICATES.equals(
						dut.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeRandomizedBlockDesign() {
		doReturn(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayRandomizedBlockDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()));
		
		if ((!dut.getSelReplicates().isEnabled() || 
			dut.getSelReplicates().getItemIds().isEmpty()) && 
			!dut.getSelBlocks().getItemIds().isEmpty()) {
			assertTrue(dut.getSelReplicates().isEnabled());
			for (Object itemId : dut.getSelBlocks().getItemIds()) {
				assertTrue(DEFAULT_REPLICATES.equals(
						dut.getSelReplicates().getItemCaption(itemId)));
			}
		}
	}

	@Test
	public void testDesignTypeInvalid() {
		doReturn(0).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		assertNull(dut.getSelDesignType().getValue());
	}
	
	@Test
	public void testPopulateChoicesForGenotypes(){
		SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), factors, 
				trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = spy(ssaPanel);
		Select genotypeSelect = new Select();
		doReturn(genotypeSelect).when(mockSSAPanel).getSelGenotypes();
		
		mockSSAPanel.populateChoicesForGenotypes();
		assertTrue("Dropdown should have 3 factors", genotypeSelect.getItemIds().size() == 3);
		for (Object id : genotypeSelect.getItemIds()){
			String localName = (String) id;
			assertFalse("Entry Type factor not included in dropdown", "ENTRY_TYPE".equals(localName));
		}
	}
	
	@Test
	public void testPopulateChoicesForReplicates(){
		SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), factors, 
				trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = spy(ssaPanel);
		Select repSelect = new Select();
		doReturn(repSelect).when(mockSSAPanel).getSelReplicates();
		
		mockSSAPanel.populateChoicesForReplicates();
		assertTrue("Dropdown should have 1 factor", repSelect.getItemIds().size() == 1);
		assertNotNull(repSelect.getItem("REP_NO"));
	}
	
	@Test
	public void testPopulateChoicesForBlocks(){
		SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), factors, 
				trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = spy(ssaPanel);
		Select blockSelect = new Select();
		doReturn(blockSelect).when(mockSSAPanel).getSelBlocks();
		
		mockSSAPanel.populateChoicesForBlocks();
		assertTrue("Dropdown should have 1 factor", blockSelect.getItemIds().size() == 1);
		assertNotNull(blockSelect.getItem("BLOCK_NO"));
	}
	
	@Test
	public void testPopulateChoicesForRowFactor(){
		SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), factors, 
				trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = spy(ssaPanel);
		Select rowSelect = new Select();
		doReturn(rowSelect).when(mockSSAPanel).getSelRowFactor();
		
		mockSSAPanel.populateChoicesForRowFactor();
		assertTrue("Dropdown should have 1 factor", rowSelect.getItemIds().size() == 1);
		assertNotNull(rowSelect.getItem("ROW_NO"));
	}
	
	@Test
	public void testPopulateChoicesForColumnFactor(){
		SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), factors, 
				trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = spy(ssaPanel);
		Select columnSelect = new Select();
		doReturn(columnSelect).when(mockSSAPanel).getSelColumnFactor();
		
		mockSSAPanel.populateChoicesForColumnFactor();
		assertTrue("Dropdown should have 1 factor", columnSelect.getItemIds().size() == 1);
		assertNotNull(columnSelect.getItem("COLUMN_NO"));
	}

	@Test
	public void testPopulateChoicesForEnvironmentFactor(){
		SingleSiteAnalysisDetailsPanel ssaPanel = new SingleSiteAnalysisDetailsPanel(null, new BreedingViewInput(), factors, 
				trialFactors, null, null, null, null);
		SingleSiteAnalysisDetailsPanel mockSSAPanel = spy(ssaPanel);
		mockSSAPanel.setMessageSource(messageSource);
		String pleaseChooseOption = "Please Choose";
		doReturn(pleaseChooseOption).when(messageSource).getMessage(Message.PLEASE_CHOOSE);
		
		Select envSelect = new Select();
		doReturn(envSelect).when(mockSSAPanel).getSelEnvFactor();
		
		mockSSAPanel.populateChoicesForEnvironmentFactor();
		//"Please Choose" was added as dropdown item
		assertTrue("Dropdown should return fixed # of env factors", envSelect.getItemIds().size() == TRIAL_ENV_FACTORS.length + 1);
		for (Object id : envSelect.getItemIds()){
			String localName = (String) id;
			assertTrue(ArrayUtils.contains(TRIAL_ENV_FACTORS, localName) || pleaseChooseOption.equals(localName));
			assertFalse(ArrayUtils.contains(DATASET_FACTORS, localName));
		}
	}
	
	
	private List<VariableType> createTestFactors() {
		List<VariableType> factors = new ArrayList<VariableType>();
		
		StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		factors.add(new VariableType("ENTRY_NO", "ENTRY_NO", entryNoVariable, 1));
		
		StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new VariableType("GID", "GID", gidVariable, 2));
		
		StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		factors.add(new VariableType("DESIGNATION", "DESIGNATION", desigVariable, 3));
		
		StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, "ENTRY TYPE", "ENTRY_TYPE"));
		factors.add(new VariableType("ENTRY_TYPE", "ENTRY_TYPE", entryTypeVariable, 4));
		
		StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.REPLICATION_FACTOR, "REP_NO"));
		factors.add(new VariableType("REP_NO", "REP_NO", repVariable, 5));
		
		StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.BLOCKING_FACTOR, "BLOCK_NO"));
		factors.add(new VariableType("BLOCK_NO", "BLOCK_NO", blockVariable, 6));
		
		StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.ROW_FACTOR, "ROW_NO"));
		factors.add(new VariableType("ROW_NO", "ROW_NO", rowVariable, 7));
		
		StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.COLUMN_FACTOR, "COL_NO"));
		factors.add(new VariableType("COLUMN_NO", "COLUMN_NO", columnVariable, 8));
		
		return factors;
	}
	
	
	private List<VariableType> createTrialVariables() {
		List<VariableType> factors = new ArrayList<VariableType>();
		
		StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(new Term(1, "TRIAL INSTANCE", "TRIAL INSTANCE"));
		factors.add(new VariableType(TRIAL_INSTANCE, TRIAL_INSTANCE, trialInstanceVar, 1));
		
		StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		factors.add(new VariableType(EXPT_DESIGN, EXPT_DESIGN, exptDesignVar, 2));
		
		StandardVariable locNameVar = new StandardVariable();
		locNameVar.setId(TermId.SITE_NAME.getId());
		locNameVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locNameVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new VariableType(LOC_NAME, LOC_NAME, locNameVar, 3));
		
		StandardVariable locIDVar = new StandardVariable();
		locIDVar.setId(TermId.LOCATION_ID.getId());
		locIDVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locIDVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		factors.add(new VariableType(LOC_ID, LOC_ID, locIDVar, 4));
		
		StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new VariableType(DATASET_NAME, DATASET_NAME, datasetNameVar, 5));
		
		StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", DATASET_TITLE));
		factors.add(new VariableType(DATASET_TITLE, DATASET_TITLE, datasetTitleVar, 6));
		
		StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		factors.add(new VariableType(DATASET_TYPE, DATASET_TYPE, datasetTypeVar, 7));
		
		return factors;
	}
	
	
}

package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.Select;

public class MultiSiteAnalysisSelectPanelTest {
	
	private static final String DATASET_TYPE = "DATASET_TYPE";
	private static final String DATASET_TITLE = "DATASET_TITLE";
	private static final String DATASET_NAME = "DATASET_NAME";
	private static final String LOC_ID = "LOC_ID";
	private static final String LOC_NAME = "LOC_NAME";
	private static final String EXPT_DESIGN = "EXPT_DESIGN";
	private static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	
	private static final String DRR_VARIATE = "DRR";
	private static final String SDWT_VARIATE = "SDWT";
	
	private static final String[] TRIAL_ENV_FACTORS = {TRIAL_INSTANCE, LOC_ID, LOC_NAME, EXPT_DESIGN};
	private static final String[] VARIATES = {DRR_VARIATE, SDWT_VARIATE};
	
	private MultiSiteAnalysisSelectPanel selectPanel;
	private DataSet dataset;
	
	@Before
	public void setup(){
		MultiSiteAnalysisSelectPanel panel = new MultiSiteAnalysisSelectPanel(null, null, null, null);
		selectPanel = Mockito.spy(panel);
		
		dataset = new DataSet();
		VariableTypeList varTypeList = new VariableTypeList();
		List<VariableType> factors = createTestGenotypeAndPlotFactors();
		factors.addAll(createTrialVariables());
		factors.addAll(createTraitVariables());
		varTypeList.setVariableTypes(factors);
		dataset.setVariableTypes(varTypeList);
		
	}
	
	@Test
	public void testPopulateGenotypeDropdown(){
		Select select = new Select();
		Mockito.doReturn(select).when(selectPanel).getSelectSpecifyGenotypes();

		selectPanel.populateGenotypeDropdown(dataset, new ArrayList<FactorModel>());
		
		assertTrue("Dropdown should have 3 factors", select.getItemIds().size() == 3);
		for (Object id : select.getItemIds()){
			String localName = (String) id;
			assertFalse("Entry Type factor not included in dropdown", "ENTRY_TYPE".equals(localName));
		}
	}
	
	@Test
	public void testPopulateEnvironmentDropdown(){
		Select select = new Select();
		Mockito.doReturn(select).when(selectPanel).getSelectSpecifyEnvironment();

		selectPanel.populateEnvironmentDropdown(dataset);
		
		assertTrue("Dropdown should return fixed # of env factors", select.getItemIds().size() == TRIAL_ENV_FACTORS.length);
		for (Object id : select.getItemIds()){
			String localName = (String) id;
			assertTrue(ArrayUtils.contains(TRIAL_ENV_FACTORS, localName));
		}
	}
	
	@Test
	public void testPopulateTraitGroups(){
		ArrayList<VariateModel> variateList = new ArrayList<VariateModel>();
		selectPanel.populateTraitGroup(dataset, variateList);
		
		assertTrue("Dropdown should return fixed # traits", variateList.size() == VARIATES.length);
		for (VariateModel variate : variateList){
			String displayName = variate.getDisplayName();
			assertTrue(ArrayUtils.contains(VARIATES, displayName));
		}
	}
	
	
	private List<VariableType> createTestGenotypeAndPlotFactors() {
		List<VariableType> factors = new ArrayList<VariableType>();
		
		StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		entryNoVariable.setScale(new Term(2, "NUMBER", ""));
		entryNoVariable.setMethod(new Term(3, "ENUMERATED", ""));
		entryNoVariable.setStoredIn(new Term(TermId.ENTRY_NUMBER_STORAGE.getId(), "", ""));
		entryNoVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "" ));
		factors.add(new VariableType("ENTRY_NO", "ENTRY_NO", entryNoVariable, 1));
		
		StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		gidVariable.setScale(new Term(2, "NUMBER", ""));
		gidVariable.setMethod(new Term(3, "ENUMERATED", ""));
		gidVariable.setStoredIn(new Term(TermId.ENTRY_GID_STORAGE.getId(), "", ""));
		gidVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "" ));
		factors.add(new VariableType("GID", "GID", gidVariable, 2));
		
		StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		desigVariable.setScale(new Term(2, "NUMBER", ""));
		desigVariable.setMethod(new Term(3, "ENUMERATED", ""));
		desigVariable.setStoredIn(new Term(TermId.ENTRY_DESIGNATION_STORAGE.getId(), "", ""));
		desigVariable.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", "" ));
		factors.add(new VariableType("DESIGNATION", "DESIGNATION", desigVariable, 3));
		
		
		StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, "ENTRY TYPE", "ENTRY_TYPE"));
		entryTypeVariable.setScale(new Term(2, "NUMBER", ""));
		entryTypeVariable.setMethod(new Term(3, "ENUMERATED", ""));
		entryTypeVariable.setStoredIn(new Term(TermId.GERMPLASM_ENTRY_STORAGE.getId(), "", ""));
		entryTypeVariable.setDataType(new Term(TermId.CATEGORICAL_VARIABLE.getId(), "Categorical variable", "" ));
		factors.add(new VariableType("ENTRY_TYPE", "ENTRY_TYPE", entryTypeVariable, 4));
		
		StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.REPLICATION_FACTOR, "REP_NO"));
		repVariable.setScale(new Term(2, "NUMBER", ""));
		repVariable.setMethod(new Term(3, "ENUMERATED", ""));
		repVariable.setStoredIn(new Term(TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), "", ""));
		repVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "" ));
		factors.add(new VariableType("REP_NO", "REP_NO", repVariable, 5));
		
		StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.BLOCKING_FACTOR, "BLOCK_NO"));
		blockVariable.setScale(new Term(2, "NUMBER", ""));
		blockVariable.setMethod(new Term(3, "ENUMERATED", ""));
		blockVariable.setStoredIn(new Term(TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), "", ""));
		blockVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "" ));
		factors.add(new VariableType("BLOCK_NO", "BLOCK_NO", blockVariable, 6));
		
		StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.ROW_FACTOR, "ROW_NO"));
		rowVariable.setScale(new Term(2, "NUMBER", ""));
		rowVariable.setMethod(new Term(3, "ENUMERATED", ""));
		rowVariable.setStoredIn(new Term(TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), "", ""));
		rowVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "" ));
		factors.add(new VariableType("ROW_NO", "ROW_NO", rowVariable, 7));
		
		StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDetailsPanel.COLUMN_FACTOR, "COL_NO"));
		columnVariable.setScale(new Term(2, "NUMBER", ""));
		columnVariable.setMethod(new Term(3, "ENUMERATED", ""));
		columnVariable.setStoredIn(new Term(TermId.TRIAL_DESIGN_INFO_STORAGE.getId(), "", ""));
		columnVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "" ));
		factors.add(new VariableType("COLUMN_NO", "COLUMN_NO", columnVariable, 8));
		
		return factors;
	}
	
	
	private List<VariableType> createTrialVariables() {
		List<VariableType> factors = new ArrayList<VariableType>();
		
		StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(new Term(1, "TRIAL INSTANCE", "TRIAL INSTANCE"));
		trialInstanceVar.setScale(new Term(2, "NUMBER", ""));
		trialInstanceVar.setMethod(new Term(3, "ENUMERATED", ""));
		trialInstanceVar.setStoredIn(new Term(TermId.TRIAL_INSTANCE_STORAGE.getId(), "", ""));
		trialInstanceVar.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", "" ));
		factors.add(new VariableType(TRIAL_INSTANCE, TRIAL_INSTANCE, trialInstanceVar, 1));
		
		StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		exptDesignVar.setScale(new Term(2, "NUMBER", ""));
		exptDesignVar.setMethod(new Term(3, "ENUMERATED", ""));
		exptDesignVar.setStoredIn(new Term(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", ""));
		exptDesignVar.setDataType(new Term(TermId.CATEGORICAL_VARIABLE.getId(), "Categorical variable", "" ));
		factors.add(new VariableType(EXPT_DESIGN, EXPT_DESIGN, exptDesignVar, 2));
		
		StandardVariable locNameVar = new StandardVariable();
		locNameVar.setId(TermId.SITE_NAME.getId());
		locNameVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locNameVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		locNameVar.setScale(new Term(2, "NUMBER", ""));
		locNameVar.setMethod(new Term(3, "ENUMERATED", ""));
		locNameVar.setStoredIn(new Term(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", ""));
		locNameVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", "" ));
		factors.add(new VariableType(LOC_NAME, LOC_NAME, locNameVar, 3));
		
		StandardVariable locIDVar = new StandardVariable();
		locIDVar.setId(TermId.LOCATION_ID.getId());
		locIDVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locIDVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		locIDVar.setScale(new Term(2, "NUMBER", ""));
		locIDVar.setMethod(new Term(3, "ENUMERATED", ""));
		locIDVar.setStoredIn(new Term(TermId.TRIAL_ENVIRONMENT_INFO_STORAGE.getId(), "", ""));
		locIDVar.setDataType(new Term(TermId.CHARACTER_DBID_VARIABLE.getId(), "Character DBID variable", "" ));
		factors.add(new VariableType(LOC_ID, LOC_ID, locIDVar, 4));
		
		StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		datasetNameVar.setScale(new Term(2, "NUMBER", ""));
		datasetNameVar.setMethod(new Term(3, "ENUMERATED", ""));
		datasetNameVar.setStoredIn(new Term(TermId.DATASET_NAME_STORAGE.getId(), "", ""));
		datasetNameVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", "" ));
		factors.add(new VariableType(DATASET_NAME, DATASET_NAME, datasetNameVar, 5));
		
		StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", DATASET_TITLE));
		datasetTitleVar.setScale(new Term(2, "NUMBER", ""));
		datasetTitleVar.setMethod(new Term(3, "ENUMERATED", ""));
		datasetTitleVar.setStoredIn(new Term(TermId.DATASET_TITLE_STORAGE.getId(), "", ""));
		datasetTitleVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", "" ));
		factors.add(new VariableType(DATASET_TITLE, DATASET_TITLE, datasetTitleVar, 6));
		
		StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		datasetTypeVar.setScale(new Term(2, "NUMBER", ""));
		datasetTypeVar.setMethod(new Term(3, "ENUMERATED", ""));
		datasetTypeVar.setStoredIn(new Term(TermId.DATASET_INFO_STORAGE.getId(), "", ""));
		datasetTypeVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", "" ));
		factors.add(new VariableType(DATASET_TYPE, DATASET_TYPE, datasetTypeVar, 7));
		
		return factors;
	}
	
	private List<VariableType> createTraitVariables() {
		List<VariableType> traits = new ArrayList<VariableType>();
		
		StandardVariable trait = new StandardVariable();
		trait.setId(1);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "DRY ROOT RESISTANCE", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "Error Estimate", ""));
		trait.setStoredIn(new Term(TermId.OBSERVATION_VARIATE.getId(), "", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", "" ));
		traits.add(new VariableType(DRR_VARIATE + "_UnitErrors", DRR_VARIATE + "_UnitErrors", trait, 1));
		
		
		trait = new StandardVariable();
		trait.setId(2);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "DRY ROOT RESISTANCE", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "LS MEAN", ""));
		trait.setStoredIn(new Term(TermId.OBSERVATION_VARIATE.getId(), "", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", "" ));
		traits.add(new VariableType(DRR_VARIATE + "_Means", DRR_VARIATE + "_Means", trait, 2));
		
		trait = new StandardVariable();
		trait.setId(3);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "SEED WEIGHT", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "Error Estimate", ""));
		trait.setStoredIn(new Term(TermId.OBSERVATION_VARIATE.getId(), "", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", "" ));
		traits.add(new VariableType(SDWT_VARIATE + "_UnitErrors", SDWT_VARIATE + "_UnitErrors", trait, 3));
		
		
		trait = new StandardVariable();
		trait.setId(4);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "SEED WEIGHT", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "LS MEAN", ""));
		trait.setStoredIn(new Term(TermId.OBSERVATION_VARIATE.getId(), "", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", "" ));
		traits.add(new VariableType(SDWT_VARIATE + "_Means", SDWT_VARIATE + "_Means", trait, 4));




//		
		return traits;
	}


}


package org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.ibpworkbench.model.FactorModel;
import org.generationcp.ibpworkbench.model.VariateModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDesignDetails;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DataSet;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
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

	private static final String[] TRIAL_ENV_FACTORS =
			{MultiSiteAnalysisSelectPanelTest.TRIAL_INSTANCE, MultiSiteAnalysisSelectPanelTest.LOC_ID,
					MultiSiteAnalysisSelectPanelTest.LOC_NAME, MultiSiteAnalysisSelectPanelTest.EXPT_DESIGN};
	private static final String[] VARIATES = {MultiSiteAnalysisSelectPanelTest.DRR_VARIATE, MultiSiteAnalysisSelectPanelTest.SDWT_VARIATE};

	private MultiSiteAnalysisSelectPanel selectPanel;
	private DataSet dataset;

	@Before
	public void setup() {
		final MultiSiteAnalysisSelectPanel panel = new MultiSiteAnalysisSelectPanel(null, null, null, null);
		this.selectPanel = Mockito.spy(panel);

		this.dataset = new DataSet();
		final VariableTypeList varTypeList = new VariableTypeList();
		final List<DMSVariableType> factors = this.createTestGenotypeAndPlotFactors();
		factors.addAll(this.createStudyVariables());
		factors.addAll(this.createTraitVariables());
		varTypeList.setVariableTypes(factors);
		this.dataset.setVariableTypes(varTypeList);

	}

	@Test
	public void testPopulateGenotypeDropdown() {
		final Select select = new Select();
		Mockito.doReturn(select).when(this.selectPanel).getSelectSpecifyGenotypes();

		final List<FactorModel> factorList = new ArrayList<>();
		this.selectPanel.populateGenotypeDropdown(this.dataset, factorList);

		Assert.assertEquals("Genotypes dropdown should have 3 factors", 3, select.getItemIds().size());
		Assert.assertEquals("Factor list should have 3 factors", 3, factorList.size());
		final ListIterator<FactorModel> factorsIterator = factorList.listIterator();
		for (final Object id : select.getItemIds()) {
			final String localName = (String) id;
			Assert.assertFalse("Entry Type factor should not be included in Genotypes dropdown",
					TermId.ENTRY_TYPE.name().equals(localName));
			Assert.assertFalse("Plot ID factor should not be included in Genotypes dropdown", TermId.OBS_UNIT_ID.name().equals(localName));

			final FactorModel factor = factorsIterator.next();
			Assert.assertFalse("Entry Type factor should not be included in FactorModel list.",
					TermId.ENTRY_TYPE.name().equals(factor.getName()));
			Assert.assertFalse("Plot ID factor should not be included in FactorModel list.",
					TermId.OBS_UNIT_ID.name().equals(factor.getName()));
		}
	}

	@Test
	public void testPopulateEnvironmentDropdown() {
		final Select envSelect = new Select();
		final Select envGroupSelect = new Select();
		Mockito.doReturn(envSelect).when(this.selectPanel).getSelectSpecifyEnvironment();
		Mockito.doReturn(envGroupSelect).when(this.selectPanel).getSelectSpecifyEnvironmentGroups();

		this.selectPanel.populateEnvironmentDropdown(this.dataset);

		Assert.assertEquals("Dropdown should return fixed # of env factors", envSelect.getItemIds().size(),
			MultiSiteAnalysisSelectPanelTest.TRIAL_ENV_FACTORS.length);
		for (final Object id : envSelect.getItemIds()) {
			final String localName = (String) id;
			Assert.assertTrue(ArrayUtils.contains(MultiSiteAnalysisSelectPanelTest.TRIAL_ENV_FACTORS, localName));
		}

		Assert.assertEquals("Dropdown should return fixed # of env group factors", envGroupSelect.getItemIds().size(),
			MultiSiteAnalysisSelectPanelTest.TRIAL_ENV_FACTORS.length - 1);
		for (final Object id : envGroupSelect.getItemIds()) {
			final String localName = (String) id;
			Assert.assertTrue(ArrayUtils.contains(MultiSiteAnalysisSelectPanelTest.TRIAL_ENV_FACTORS, localName));
			Assert.assertFalse(MultiSiteAnalysisSelectPanelTest.TRIAL_INSTANCE.equals(localName));
		}
	}

	@Test
	public void testPopulateTraitGroups() {
		final ArrayList<VariateModel> variateList = new ArrayList<VariateModel>();
		this.selectPanel.populateTraitGroup(this.dataset, variateList);

		Assert.assertEquals("Dropdown should return fixed # traits", variateList.size(), MultiSiteAnalysisSelectPanelTest.VARIATES.length);
		for (final VariateModel variate : variateList) {
			final String displayName = variate.getDisplayName();
			Assert.assertTrue(ArrayUtils.contains(MultiSiteAnalysisSelectPanelTest.VARIATES, displayName));
		}
	}

	private List<DMSVariableType> createTestGenotypeAndPlotFactors() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		int rank = 1;
		final StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		entryNoVariable.setScale(new Term(2, "NUMBER", ""));
		entryNoVariable.setMethod(new Term(3, "ENUMERATED", ""));
		entryNoVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));
		factors.add(new DMSVariableType(TermId.ENTRY_NO.name(), TermId.ENTRY_NO.name(), entryNoVariable, rank++));

		final StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		gidVariable.setScale(new Term(2, "NUMBER", ""));
		gidVariable.setMethod(new Term(3, "ENUMERATED", ""));
		gidVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));
		factors.add(new DMSVariableType(TermId.GID.name(), TermId.GID.name(), gidVariable, rank++));

		final StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		desigVariable.setScale(new Term(2, "NUMBER", ""));
		desigVariable.setMethod(new Term(3, "ENUMERATED", ""));
		desigVariable.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", ""));
		factors.add(new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, rank++));

		final StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, "ENTRY TYPE", "ENTRY_TYPE"));
		entryTypeVariable.setScale(new Term(2, "NUMBER", ""));
		entryTypeVariable.setMethod(new Term(3, "ENUMERATED", ""));
		entryTypeVariable.setDataType(new Term(TermId.CATEGORICAL_VARIABLE.getId(), "Categorical variable", ""));
		factors.add(new DMSVariableType(TermId.ENTRY_TYPE.name(), TermId.ENTRY_TYPE.name(), entryTypeVariable, rank++));

		final StandardVariable obsUnitIdVariable = new StandardVariable();
		obsUnitIdVariable.setId(TermId.OBS_UNIT_ID.getId());
		obsUnitIdVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		obsUnitIdVariable.setProperty(new Term(1, "ENTRY TYPE", "ENTRY_TYPE"));
		obsUnitIdVariable.setScale(new Term(2, "NUMBER", ""));
		obsUnitIdVariable.setMethod(new Term(3, "ENUMERATED", ""));
		obsUnitIdVariable.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", ""));
		factors.add(new DMSVariableType(TermId.OBS_UNIT_ID.name(), TermId.OBS_UNIT_ID.name(), obsUnitIdVariable, rank++));

		final StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.REPLICATION_FACTOR, "REP_NO"));
		repVariable.setScale(new Term(2, "NUMBER", ""));
		repVariable.setMethod(new Term(3, "ENUMERATED", ""));
		repVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));
		factors.add(new DMSVariableType(TermId.REP_NO.name(), TermId.REP_NO.name(), repVariable, rank++));

		final StandardVariable blockVariable = new StandardVariable();
		blockVariable.setId(TermId.BLOCK_NO.getId());
		blockVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		blockVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.BLOCKING_FACTOR, "BLOCK_NO"));
		blockVariable.setScale(new Term(2, "NUMBER", ""));
		blockVariable.setMethod(new Term(3, "ENUMERATED", ""));
		blockVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));
		factors.add(new DMSVariableType(TermId.BLOCK_NO.name(), TermId.BLOCK_NO.name(), blockVariable, rank++));

		final StandardVariable rowVariable = new StandardVariable();
		rowVariable.setId(TermId.ROW.getId());
		rowVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		rowVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.ROW_FACTOR, "ROW_NO"));
		rowVariable.setScale(new Term(2, "NUMBER", ""));
		rowVariable.setMethod(new Term(3, "ENUMERATED", ""));
		rowVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));
		factors.add(new DMSVariableType("ROW_NO", "ROW_NO", rowVariable, rank++));

		final StandardVariable columnVariable = new StandardVariable();
		columnVariable.setId(TermId.COLUMN_NO.getId());
		columnVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		columnVariable.setProperty(new Term(1, SingleSiteAnalysisDesignDetails.COLUMN_FACTOR, "COL_NO"));
		columnVariable.setScale(new Term(2, "NUMBER", ""));
		columnVariable.setMethod(new Term(3, "ENUMERATED", ""));
		columnVariable.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));
		factors.add(new DMSVariableType(TermId.COLUMN_NO.name(), TermId.COLUMN_NO.name(), columnVariable, rank++));

		return factors;
	}

	private List<DMSVariableType> createStudyVariables() {
		final List<DMSVariableType> factors = new ArrayList<DMSVariableType>();

		final StandardVariable trialInstanceVar = new StandardVariable();
		trialInstanceVar.setId(TermId.TRIAL_INSTANCE_FACTOR.getId());
		trialInstanceVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		trialInstanceVar.setProperty(new Term(1, "TRIAL INSTANCE", "TRIAL INSTANCE"));
		trialInstanceVar.setScale(new Term(2, "NUMBER", ""));
		trialInstanceVar.setMethod(new Term(3, "ENUMERATED", ""));
		trialInstanceVar.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numerical variable", ""));
		factors.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.TRIAL_INSTANCE, MultiSiteAnalysisSelectPanelTest.TRIAL_INSTANCE,
				trialInstanceVar, 1));

		final StandardVariable exptDesignVar = new StandardVariable();
		exptDesignVar.setId(TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		exptDesignVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		exptDesignVar.setProperty(new Term(1, "EXPERIMENTAL DESIGN", "EXPERIMENTAL DESIGN"));
		exptDesignVar.setScale(new Term(2, "NUMBER", ""));
		exptDesignVar.setMethod(new Term(3, "ENUMERATED", ""));
		exptDesignVar.setDataType(new Term(TermId.CATEGORICAL_VARIABLE.getId(), "Categorical variable", ""));
		factors.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.EXPT_DESIGN, MultiSiteAnalysisSelectPanelTest.EXPT_DESIGN,
				exptDesignVar, 2));

		final StandardVariable locNameVar = new StandardVariable();
		locNameVar.setId(TermId.SITE_NAME.getId());
		locNameVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locNameVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		locNameVar.setScale(new Term(2, "NUMBER", ""));
		locNameVar.setMethod(new Term(3, "ENUMERATED", ""));
		locNameVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", ""));
		factors.add(
				new DMSVariableType(MultiSiteAnalysisSelectPanelTest.LOC_NAME, MultiSiteAnalysisSelectPanelTest.LOC_NAME, locNameVar, 3));

		final StandardVariable locIDVar = new StandardVariable();
		locIDVar.setId(TermId.LOCATION_ID.getId());
		locIDVar.setPhenotypicType(PhenotypicType.TRIAL_ENVIRONMENT);
		locIDVar.setProperty(new Term(1, "LOCATION", "LOCATION"));
		locIDVar.setScale(new Term(2, "NUMBER", ""));
		locIDVar.setMethod(new Term(3, "ENUMERATED", ""));
		locIDVar.setDataType(new Term(TermId.CHARACTER_DBID_VARIABLE.getId(), "Character DBID variable", ""));
		factors.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.LOC_ID, MultiSiteAnalysisSelectPanelTest.LOC_ID, locIDVar, 4));

		final StandardVariable datasetNameVar = new StandardVariable();
		datasetNameVar.setId(TermId.DATASET_NAME.getId());
		datasetNameVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetNameVar.setProperty(new Term(1, "DATASET", "DATASET"));
		datasetNameVar.setScale(new Term(2, "NUMBER", ""));
		datasetNameVar.setMethod(new Term(3, "ENUMERATED", ""));
		datasetNameVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", ""));
		factors.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.DATASET_NAME, MultiSiteAnalysisSelectPanelTest.DATASET_NAME,
				datasetNameVar, 5));

		final StandardVariable datasetTitleVar = new StandardVariable();
		datasetTitleVar.setId(TermId.DATASET_NAME.getId());
		datasetTitleVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTitleVar.setProperty(new Term(1, "DATASET TITLE", MultiSiteAnalysisSelectPanelTest.DATASET_TITLE));
		datasetTitleVar.setScale(new Term(2, "NUMBER", ""));
		datasetTitleVar.setMethod(new Term(3, "ENUMERATED", ""));
		datasetTitleVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", ""));
		factors.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.DATASET_TITLE, MultiSiteAnalysisSelectPanelTest.DATASET_TITLE,
				datasetTitleVar, 6));

		final StandardVariable datasetTypeVar = new StandardVariable();
		datasetTypeVar.setId(TermId.DATASET_NAME.getId());
		datasetTypeVar.setPhenotypicType(PhenotypicType.DATASET);
		datasetTypeVar.setProperty(new Term(1, "DATASET", "DATASET"));
		datasetTypeVar.setScale(new Term(2, "NUMBER", ""));
		datasetTypeVar.setMethod(new Term(3, "ENUMERATED", ""));
		datasetTypeVar.setDataType(new Term(TermId.CHARACTER_VARIABLE.getId(), "Character variable", ""));
		factors.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.DATASET_TYPE, MultiSiteAnalysisSelectPanelTest.DATASET_TYPE,
				datasetTypeVar, 7));

		return factors;
	}

	private List<DMSVariableType> createTraitVariables() {
		final List<DMSVariableType> traits = new ArrayList<DMSVariableType>();

		StandardVariable trait = new StandardVariable();
		trait.setId(1);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "DRY ROOT RESISTANCE", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "Error Estimate", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", ""));
		traits.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.DRR_VARIATE + "_UnitErrors",
				MultiSiteAnalysisSelectPanelTest.DRR_VARIATE + "_UnitErrors", trait, 1));

		trait = new StandardVariable();
		trait.setId(2);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "DRY ROOT RESISTANCE", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "LS MEAN", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", ""));
		traits.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.DRR_VARIATE + "_Means",
				MultiSiteAnalysisSelectPanelTest.DRR_VARIATE + "_Means", trait, 2));

		trait = new StandardVariable();
		trait.setId(3);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "SEED WEIGHT", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "Error Estimate", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", ""));
		traits.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.SDWT_VARIATE + "_UnitErrors",
				MultiSiteAnalysisSelectPanelTest.SDWT_VARIATE + "_UnitErrors", trait, 3));

		trait = new StandardVariable();
		trait.setId(4);
		trait.setPhenotypicType(PhenotypicType.VARIATE);
		trait.setProperty(new Term(1, "SEED WEIGHT", ""));
		trait.setScale(new Term(2, "NUMBER", ""));
		trait.setMethod(new Term(3, "LS MEAN", ""));
		trait.setDataType(new Term(TermId.NUMERIC_VARIABLE.getId(), "Numeric variable", ""));
		traits.add(new DMSVariableType(MultiSiteAnalysisSelectPanelTest.SDWT_VARIATE + "_Means",
				MultiSiteAnalysisSelectPanelTest.SDWT_VARIATE + "_Means", trait, 4));

		//
		return traits;
	}

}

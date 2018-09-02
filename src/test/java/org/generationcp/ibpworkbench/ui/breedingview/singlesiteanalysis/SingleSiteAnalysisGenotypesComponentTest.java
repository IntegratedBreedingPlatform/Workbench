package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property;

public class SingleSiteAnalysisGenotypesComponentTest {
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;
	
	private SingleSiteAnalysisGenotypesComponent genotypesComponent;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		Mockito.doReturn(this.createTestFactors()).when(this.ssaDetailsPanel).getFactorsInDataset();
		
		this.genotypesComponent = new SingleSiteAnalysisGenotypesComponent(this.ssaDetailsPanel);
		this.genotypesComponent.setMessageSource(this.messageSource);
		this.genotypesComponent.instantiateComponents();
	}
	
	@Test
	public void testListenersAdded() {
		this.genotypesComponent.addListeners();
		Assert.assertNotNull(this.genotypesComponent.getSelGenotypes().getListeners(Property.ValueChangeEvent.class));
	}
	
	@Test
	public void testPopulateChoicesForGenotypes() {
		this.genotypesComponent.populateChoicesForGenotypes();
		Assert.assertEquals("Genotypes dropdown should have 3 factors", 3, this.genotypesComponent.getSelGenotypes().getItemIds().size());
		for (final Object id : this.genotypesComponent.getSelGenotypes().getItemIds()) {
			final String localName = (String) id;
			Assert.assertFalse("Entry Type factor should not be included in Genotypes dropdown",
					TermId.ENTRY_TYPE.name().equals(localName));
			Assert.assertFalse("Plot ID factor should not be included in Genotypes dropdown", TermId.PLOT_ID.name().equals(localName));
		}
		Assert.assertEquals("ENTRY_NO", this.genotypesComponent.getSelGenotypesValue());
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

	
}

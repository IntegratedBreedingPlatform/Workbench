package org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis;

import org.generationcp.commons.breedingview.xml.DesignType;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.util.BreedingViewInput;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 12/17/2014
 * Time: 1:39 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class SingleSiteAnalysisDetailsPanelTest {

	private SingleSiteAnalysisDetailsPanel dut;

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
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableNonLatin() {
		
		doReturn(TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId()).when(dut).retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayIncompleteBlockDesignElements();

		assertTrue(dut.getSelDesignType().getValue().equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));
	}

	@Test
	public void testDesignTypeIncompleteBlockDesignResolvableLatin() {

		doReturn(TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayIncompleteBlockDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.INCOMPLETE_BLOCK_DESIGN.getName()));
	}

	@Test
	public void testDesignTypeRowColumnDesignLatin() {
		doReturn(TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayRowColumnDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.ROW_COLUMN_DESIGN.getName()));
	}

	@Test
	public void testDesignTypeRowColumnDesignNonLatin() {
		doReturn(TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayRowColumnDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.ROW_COLUMN_DESIGN.getName()));
	}

	@Test
	public void testDesignTypeRandomizedBlockDesign() {
		doReturn(TermId.RANDOMIZED_COMPLETE_BLOCK.getId()).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		verify(dut).displayRandomizedBlockDesignElements();

		assertTrue(dut.getSelDesignType().getValue()
				.equals(DesignType.RANDOMIZED_BLOCK_DESIGN.getName()));
	}

	@Test
	public void testDesignTypeInvalid() {
		doReturn(0).when(dut)
				.retrieveExperimentalDesignTypeID();

		dut.initializeComponents();

		assertNull(dut.getSelDesignType().getValue());
	}
}

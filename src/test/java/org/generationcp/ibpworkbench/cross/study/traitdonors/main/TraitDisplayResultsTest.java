package org.generationcp.ibpworkbench.cross.study.traitdonors.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CategoricalTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.CharacterTraitFilter;
import org.generationcp.ibpworkbench.cross.study.adapted.main.pojos.NumericTraitFilter;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.EnvironmentForComparison;
import org.generationcp.ibpworkbench.data.initializer.TableResultRowTestDataInitializer;
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import junit.framework.Assert;

@RunWith(value = MockitoJUnitRunner.class)
public class TraitDisplayResultsTest {
	private static final Logger LOG = LoggerFactory.getLogger(TraitDisplayResultsTest.class);
	private static final String GERMPLASM_COL_TABLE_HEIGHT = "445";
	@Mock
	private CrossStudyDataManager crossStudyDataManager;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private TraitDonorsQueryMain main;

	@InjectMocks
	private TraitDisplayResults traitDisplayResults;

	@Before
	public void setUp() {
		this.traitDisplayResults = new TraitDisplayResults(this.main);
		this.traitDisplayResults.setEnvironmentIds(new ArrayList<Integer>());
		this.traitDisplayResults.setTraitIds(new ArrayList<Integer>());
		this.traitDisplayResults.setCrossStudyDataManager(this.crossStudyDataManager);
		Mockito.when(this.crossStudyDataManager.getObservationsForTraits(ArgumentMatchers.<List<Integer>>any(), ArgumentMatchers.<List<Integer>>any()))
				.thenReturn(new ArrayList<Observation>());
		this.traitDisplayResults.setGermplasmDataManager(this.germplasmDataManager);
		Mockito.when(this.germplasmDataManager.getPreferredNamesByGids(ArgumentMatchers.<List<Integer>>any()))
				.thenReturn(new HashMap<Integer, String>());
		this.traitDisplayResults.setResultsTable(new AbsoluteLayout());
		this.traitDisplayResults.setGermplasmIdNameMap(new HashMap<Integer, String>());
		this.traitDisplayResults.setSelectedGermplasmMap(new HashMap<Integer, String>());
		this.traitDisplayResults.setCurrentLineIndex(0);
	}

	@Test
	public void testPopulateResultsTable() {
		this.traitDisplayResults.populateResultsTable(new ArrayList<EnvironmentForComparison>(),
				new ArrayList<NumericTraitFilter>(), new ArrayList<CharacterTraitFilter>(),
				new ArrayList<CategoricalTraitFilter>());
		Mockito.verify(this.germplasmDataManager, Mockito.times(1)).getPreferredNamesByGids(ArgumentMatchers.<List<Integer>>any());
		Mockito.verify(this.crossStudyDataManager, Mockito.times(4)).getObservationsForTraits(ArgumentMatchers.<List<Integer>>any(),
				ArgumentMatchers.<List<Integer>>any());
		Assert.assertEquals(this.traitDisplayResults.getResultsTable().getComponentCount(), 3);
		final Iterator<Component> componentIterator = this.traitDisplayResults.getResultsTable().getComponentIterator();
		final List<String> debugIds = new ArrayList<>(
				Arrays.asList("germplasmColTable", "traitsColTable", "combinedScoreTagColTable"));
		while (componentIterator.hasNext()) {
			Assert.assertTrue(debugIds.contains(componentIterator.next().getDebugId()));
		}

	}

	@Test
	public void testCreateCombinedScoreTagColTable() {
		this.traitDisplayResults.createCombinedScoreTagColTable();
		final PagedTable combinedScoreTagColTable = this.traitDisplayResults.getCreateCombinedScoreTagColTable();
		Assert.assertEquals("combinedScoreTagColTable", combinedScoreTagColTable.getDebugId());
		Assert.assertEquals(Float.valueOf("160"), combinedScoreTagColTable.getWidth());
		Assert.assertEquals(Float.valueOf(TraitDisplayResultsTest.GERMPLASM_COL_TABLE_HEIGHT),
				combinedScoreTagColTable.getHeight());
		Assert.assertTrue(combinedScoreTagColTable.isImmediate());
		Assert.assertEquals(15, combinedScoreTagColTable.getPageLength());
		Assert.assertTrue(combinedScoreTagColTable.isColumnCollapsingAllowed());
		Assert.assertFalse(combinedScoreTagColTable.isColumnReorderingAllowed());
	}

	@Test
	public void testCreateTraitsColTable() {
		this.traitDisplayResults.createTraitsColTable();
		final PagedTable traitsColTable = this.traitDisplayResults.getTraitsColTable();
		Assert.assertEquals("traitsColTable", traitsColTable.getDebugId());
		Assert.assertEquals(Float.valueOf("490"), traitsColTable.getWidth());
		Assert.assertEquals(Float.valueOf(TraitDisplayResultsTest.GERMPLASM_COL_TABLE_HEIGHT),
				traitsColTable.getHeight());
		Assert.assertTrue(traitsColTable.isImmediate());
		Assert.assertEquals(15, traitsColTable.getPageLength());
		Assert.assertTrue(traitsColTable.isColumnCollapsingAllowed());
		Assert.assertFalse(traitsColTable.isColumnReorderingAllowed());
	}

	@Test
	public void testCreateGermplasmColTable() {
		this.traitDisplayResults.createGermplasmColTable();
		final PagedTable germplasmColTable = this.traitDisplayResults.getGermplasmColTable();
		Assert.assertEquals("germplasmColTable", germplasmColTable.getDebugId());
		Assert.assertEquals(Float.valueOf("340"), germplasmColTable.getWidth());
		Assert.assertEquals(Float.valueOf(TraitDisplayResultsTest.GERMPLASM_COL_TABLE_HEIGHT),
				germplasmColTable.getHeight());
		Assert.assertTrue(germplasmColTable.isImmediate());
		Assert.assertEquals(15, germplasmColTable.getPageLength());
		Assert.assertTrue(germplasmColTable.isColumnCollapsingAllowed());
		Assert.assertFalse(germplasmColTable.isColumnReorderingAllowed());
	}
	
	@Test
	public void testNextAndPrevEntryButtonClickAction() {
		this.traitDisplayResults.createGermplasmColTable();
		this.traitDisplayResults.createTraitsColTable();
		this.traitDisplayResults.createCombinedScoreTagColTable();
		this.traitDisplayResults.setTableRows(TableResultRowTestDataInitializer.createTableResultRows(20));
		
		final PagedTable germplasmColTable = this.traitDisplayResults.getGermplasmColTable();
		final PagedTable traitsColTable = this.traitDisplayResults.getTraitsColTable();
		final PagedTable combinedScoreTagColTable = this.traitDisplayResults.getCreateCombinedScoreTagColTable();
		
		this.traitDisplayResults.populateRowsResultsTable(germplasmColTable, 10);
		this.traitDisplayResults.populateRowsResultsTable(traitsColTable, 10);
		this.traitDisplayResults.populateRowsResultsTable(combinedScoreTagColTable, 10);
		
		Assert.assertEquals(this.traitDisplayResults.getCurrentLineIndex().intValue(), 0 );
		this.traitDisplayResults.nextEntryButtonClickAction();
		Assert.assertEquals(this.traitDisplayResults.getCurrentLineIndex().intValue(), 15 );
		this.traitDisplayResults.prevEntryButtonClickAction();
		Assert.assertEquals(this.traitDisplayResults.getCurrentLineIndex().intValue(), 0);
	}
}

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
import org.generationcp.middleware.domain.h2h.Observation;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;

import junit.framework.Assert;

@RunWith(value = MockitoJUnitRunner.class)
public class TraitDisplayResultsTest {

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
		Mockito.when(this.crossStudyDataManager.getObservationsForTraits(Matchers.anyList(), Matchers.anyList()))
				.thenReturn(new ArrayList<Observation>());
		this.traitDisplayResults.setGermplasmDataManager(this.germplasmDataManager);
		Mockito.when(this.germplasmDataManager.getPreferredNamesByGids(Matchers.anyList()))
				.thenReturn(new HashMap<Integer, String>());
		this.traitDisplayResults.setResultsTable(new AbsoluteLayout());
	}

	@Test
	public void testPopulateResultsTable() {
		this.traitDisplayResults.populateResultsTable(new ArrayList<EnvironmentForComparison>(),
				new ArrayList<NumericTraitFilter>(), new ArrayList<CharacterTraitFilter>(),
				new ArrayList<CategoricalTraitFilter>());
		Mockito.verify(this.germplasmDataManager, Mockito.times(1)).getPreferredNamesByGids(Matchers.anyList());
		Mockito.verify(this.crossStudyDataManager, Mockito.times(4)).getObservationsForTraits(Matchers.anyList(),
				Matchers.anyList());
		Assert.assertEquals(this.traitDisplayResults.getResultsTable().getComponentCount(), 3);
		final Iterator<Component> componentIterator = this.traitDisplayResults.getResultsTable().getComponentIterator();
		final List<String> debugIds = new ArrayList<>(
				Arrays.asList("germplasmColTable", "traitsColTable", "combinedScoreTagColTable"));
		while (componentIterator.hasNext()) {
			Assert.assertTrue(debugIds.contains(componentIterator.next().getDebugId()));
		}

	}
}

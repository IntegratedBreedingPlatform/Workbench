
package org.generationcp.ibpworkbench.cross.study.h2h.main;

import java.util.Arrays;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.cross.study.commons.EnvironmentFilter;
import org.generationcp.middleware.domain.dms.TrialEnvironment;
import org.generationcp.middleware.domain.dms.TrialEnvironments;
import org.generationcp.middleware.domain.h2h.GermplasmPair;
import org.generationcp.middleware.domain.h2h.TraitInfo;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.CrossStudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

public class TraitsAvailableComponentTest {

	private static final String ANALYSIS = "Analysis";

	private static final String TRAITS = "Traits";

	@Mock
	private HeadToHeadCrossStudyMain mainScreen;

	@Mock
	private EnvironmentFilter environmentFilter;

	@Mock
	private CrossStudyDataManager crossStudyDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@InjectMocks
	private TraitsAvailableComponent traitsAvailableComponent;

	final List<GermplasmPair> pairs = Lists.newArrayList(new GermplasmPair(2434138, 1356114));

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.traitsAvailableComponent = new TraitsAvailableComponent(this.mainScreen, this.environmentFilter);
		this.traitsAvailableComponent.setMessageSource(this.messageSource);
		this.traitsAvailableComponent.setCrossStudyDataManager(this.crossStudyDataManager);

		// Mock Trait Filter Option Group captions
		Mockito.doReturn("All").when(this.messageSource).getMessage(Message.HEAD_TO_HEAD_CHECK_ALL);
		Mockito.doReturn(TraitsAvailableComponentTest.TRAITS).when(this.messageSource).getMessage(Message.HEAD_TO_HEAD_CHECK_TRAITS);
		Mockito.doReturn(TraitsAvailableComponentTest.ANALYSIS).when(this.messageSource).getMessage(Message.HEAD_TO_HEAD_CHECK_ANALYSIS);

		// Mock environments/traits returned by Middleware query to prevent NPE
		final TraitInfo trait = new TraitInfo(1, "TRAIT1", "TRAIT1-DESC", "Percentage", TermId.NUMERIC_VARIABLE.getId());
		final TrialEnvironment env = new TrialEnvironment(1);
		env.setTraits(Arrays.asList(trait));
		final TrialEnvironments trialEnvs = new TrialEnvironments();
		trialEnvs.add(env);
		final GermplasmPair germplasmPair = this.pairs.get(0);
		germplasmPair.setTrialEnvironments(trialEnvs);
		Mockito.doReturn(this.pairs).when(this.crossStudyDataManager)
				.getEnvironmentsForGermplasmPairs(Matchers.anyListOf(GermplasmPair.class), Matchers.anyListOf(Integer.class));
	}

	@Test
	public void testRefreshEnviromentPairListWhenAllVariablesSelected() {
		this.traitsAvailableComponent.initializeVariableFilterOptionGroup();

		// Method to test
		this.traitsAvailableComponent.refreshEnviromentPairList(this.pairs);

		final List<Integer> experimentTypes = Arrays.asList(TermId.PLOT_EXPERIMENT.getId(), TermId.AVERAGE_EXPERIMENT.getId());
		Mockito.verify(this.crossStudyDataManager).getEnvironmentsForGermplasmPairs(Matchers.eq(this.pairs), Matchers.eq(experimentTypes));
	}

	@Test
	public void testRefreshEnviromentPairListWhenTraitsSelected() throws Exception {
		this.traitsAvailableComponent.afterPropertiesSet();
		this.traitsAvailableComponent.getVariableFilterOptionGroup().setValue(TraitsAvailableComponentTest.TRAITS);

		// Method to test
		this.traitsAvailableComponent.refreshEnviromentPairList(this.pairs);

		// Verify that only plot experiments are considered in query so that only trait variables are retrieved
		final List<Integer> experimentTypes = Arrays.asList(TermId.PLOT_EXPERIMENT.getId());
		Mockito.verify(this.crossStudyDataManager).getEnvironmentsForGermplasmPairs(Matchers.eq(this.pairs), Matchers.eq(experimentTypes));
	}

	@Test
	public void testRefreshEnviromentPairListWhenAnalysisVariablesSelected() throws Exception {
		this.traitsAvailableComponent.afterPropertiesSet();
		this.traitsAvailableComponent.getVariableFilterOptionGroup().setValue(TraitsAvailableComponentTest.ANALYSIS);

		// Method to test
		this.traitsAvailableComponent.refreshEnviromentPairList(this.pairs);

		// Verify that only Means dataset experiments are considered in query so that only Analysis variables are retrieved
		final List<Integer> experimentTypes = Arrays.asList(TermId.AVERAGE_EXPERIMENT.getId());
		Mockito.verify(this.crossStudyDataManager).getEnvironmentsForGermplasmPairs(Matchers.eq(this.pairs), Matchers.eq(experimentTypes));
	}

}

package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisDetailsPanel;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;

public class RunBreedingViewButtonClickListenerTest {
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private SingleSiteAnalysisDetailsPanel ssaDetailsPanel;

	@InjectMocks
	private RunBreedingViewButtonClickListener listener;
	
	@Mock
	private RunSingleSiteAction runSsaAction;
	
	@Mock
	private ClickEvent event;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.listener.setRunSingleSiteAction(this.runSsaAction);
	}
	
	@Test
	public void testButtonClickWhenServerAppConfigured() {
		Mockito.doReturn("true").when(this.ssaDetailsPanel).getIsServerApp();
		this.listener.buttonClick(this.event);
		Mockito.verifyZeroInteractions(this.studyDataManager);
		Mockito.verify(this.runSsaAction).buttonClick(event);
	}
	
	
}

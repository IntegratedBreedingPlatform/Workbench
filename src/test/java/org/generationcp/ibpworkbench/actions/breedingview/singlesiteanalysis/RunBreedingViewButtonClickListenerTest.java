package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.ui.Button.ClickEvent;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class RunBreedingViewButtonClickListenerTest {
	
	@Mock
	private StudyDataManager studyDataManager;
	
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
		this.listener.buttonClick(this.event);
		Mockito.verifyZeroInteractions(this.studyDataManager);
		Mockito.verify(this.runSsaAction).buttonClick(event);
	}
	
	
}

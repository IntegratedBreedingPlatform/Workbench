package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Field.ValueChangeEvent;

public class BreedingViewEnvFactorValueChangeListenerTest {
	
	@Mock
	private SingleSiteAnalysisEnvironmentsComponent environmentsComponent;
	
	@InjectMocks
	private BreedingViewEnvFactorValueChangeListener listener;
	
	@Mock
	private ValueChangeEvent event;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testValueChange() {
		this.listener.valueChange(this.event);
		Mockito.verify(this.environmentsComponent).populateChoicesForEnvForAnalysis();
	}
	

}

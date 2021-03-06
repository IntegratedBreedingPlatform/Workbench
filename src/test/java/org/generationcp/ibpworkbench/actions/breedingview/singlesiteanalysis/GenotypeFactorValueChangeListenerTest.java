package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

public class GenotypeFactorValueChangeListenerTest {
	
	@Mock
	private ValueChangeEvent event;
	
	@Mock
	private Window window;
	
	@Mock
	private SingleSiteAnalysisEnvironmentsComponent environmentsComponent;
	
	@InjectMocks
	private GenotypeFactorValueChangeListener listener;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.window).when(this.environmentsComponent).getWindow();
	}
	
	@Test
	public void testFooterCheckboxSelectedAllValidEnvironments(){
		Mockito.doReturn(new ArrayList<String>()).when(this.environmentsComponent).getInvalidEnvironments(Mockito.anyBoolean());
		this.listener.valueChange(this.event);
		
		Mockito.verify(this.environmentsComponent).getInvalidEnvironments(false);
		Mockito.verifyZeroInteractions(this.window);
	}
	@Test
	public void testFooterCheckboxSelectedWithInvalidEnvironment(){
		Mockito.doReturn(Arrays.asList("ENV 1")).when(this.environmentsComponent).getInvalidEnvironments(Mockito.anyBoolean());
		this.listener.valueChange(this.event);
		
		Mockito.verify(this.environmentsComponent).getInvalidEnvironments(false);
		Mockito.verify(this.window).showNotification(Matchers.any(Notification.class));
	}
}

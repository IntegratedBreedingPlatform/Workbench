package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import org.junit.Assert;

public class SSAEnvironmentsCheckboxValueChangeListenerTest {
	
	@Mock
	private SingleSiteAnalysisEnvironmentsComponent ssaEnvironmentsComponent;
	
	@InjectMocks
	private SSAEnvironmentsCheckboxValueChangeListener listeners;
	
	@Mock
	private ValueChangeEvent mockEvent;
	
	@Mock
	private CheckBox environmentCheckbox;
	
	@Mock
	private CheckBox footerCheckbox;
	
	@Mock
	private ValueChangeListener footerCheckboxListener;
	
	@Mock
	private Window window;
	
	private SeaEnvironmentModel model;
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.environmentCheckbox).when(this.mockEvent).getProperty();
		Mockito.doReturn(this.footerCheckbox).when(this.ssaEnvironmentsComponent).getFooterCheckBox();
		Mockito.doReturn(this.footerCheckboxListener).when(this.ssaEnvironmentsComponent).getFooterCheckBoxListener();
		Mockito.doReturn(this.window).when(this.ssaEnvironmentsComponent).getWindow();
		
		model = new SeaEnvironmentModel();
		model.setEnvironmentName(RandomStringUtils.randomAlphanumeric(20));
		Mockito.doReturn(model).when(this.environmentCheckbox).getData();
	}
	
	@Test
	public void testValueChangeWithEnvironmentCheckboxNotSelected() {
		Mockito.doReturn(false).when(this.environmentCheckbox).getValue();
		this.listeners.valueChange(mockEvent);
		
		Assert.assertFalse(model.getActive());
		Mockito.verify(this.footerCheckbox).removeListener(this.footerCheckboxListener);
		Mockito.verify(this.footerCheckbox).setValue(false);
		Mockito.verify(this.footerCheckbox).addListener(this.footerCheckboxListener);
	}
	
	@Test
	public void testValueChangeWithEnvironmentSelectedAndWithMeasurementData() {
		Mockito.doReturn(true).when(this.environmentCheckbox).getValue();
		Mockito.doReturn(true).when(this.ssaEnvironmentsComponent).environmentContainsValidDataForAnalysis(this.model);
		this.listeners.valueChange(mockEvent);
		
		Assert.assertTrue(model.getActive());
		Mockito.verify(this.ssaEnvironmentsComponent, Mockito.never()).getSelEnvFactorValue();
		Mockito.verifyZeroInteractions(this.window);
	}

	@Test
	public void testValueChangeWithEnvironmentSelectedButNoMeasurementData() {
		Mockito.doReturn(true).when(this.environmentCheckbox).getValue();
		Mockito.doReturn(false).when(this.ssaEnvironmentsComponent).environmentContainsValidDataForAnalysis(this.model);
		this.listeners.valueChange(mockEvent);
		
		Assert.assertFalse(model.getActive());
		Mockito.verify(this.environmentCheckbox).setValue(false);
		Mockito.verify(this.ssaEnvironmentsComponent).getSelEnvFactorValue();
		Mockito.verify(this.window).showNotification(Matchers.any(Notification.class));;
	}
}

package org.generationcp.ibpworkbench.actions.breedingview.singlesiteanalysis;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import org.generationcp.ibpworkbench.model.SeaEnvironmentModel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisEnvironmentsComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

public class SSAEnvironmentsFooterCheckboxListenerTest {
	
	@Mock
	private Table environmentsTable;
	
	@Mock
	private ValueChangeEvent event;
	
	@Mock
	private Window window;
	
	@Mock
	private Property property;
	
	@Mock
	private SingleSiteAnalysisEnvironmentsComponent environmentsComponent;
	
	@Mock
	private Container container;
	
	private SeaEnvironmentModel model1;
	private SeaEnvironmentModel model2;
	
	@InjectMocks
	private SSAEnvironmentsFooterCheckboxListener listener;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(this.window).when(this.environmentsComponent).getWindow();
		Mockito.doReturn(this.property).when(this.event).getProperty();
		Mockito.doReturn(this.environmentsTable).when(this.environmentsComponent).getTblEnvironmentSelection();
		Mockito.doReturn(this.container).when(this.environmentsTable).getContainerDataSource();
		
		this.model1 = new SeaEnvironmentModel();
		this.model1.setLocationId(1);
		this.model1.setActive(true);
		this.model2 = new SeaEnvironmentModel();
		this.model2.setLocationId(2);
		this.model2.setActive(true);
		Mockito.doReturn(Arrays.asList(this.model1, this.model2)).when(this.container).getItemIds();
	}
	
	@Test
	public void testFooterCheckboxNotSelected(){
		Mockito.doReturn(false).when(this.property).getValue();
		this.listener.valueChange(this.event);
		
		Mockito.verify(this.environmentsComponent, Mockito.never()).getInvalidEnvironments(true);
		Mockito.verifyZeroInteractions(this.window);
		Mockito.verify(this.environmentsTable).refreshRowCache();
		Assert.assertFalse(this.model1.getActive());
		Assert.assertFalse(this.model2.getActive());
	}
	
	@Test
	public void testFooterCheckboxSelectedAllValidEnvironments(){
		Mockito.doReturn(true).when(this.property).getValue();
		Mockito.doReturn(new ArrayList<String>()).when(this.environmentsComponent).getInvalidEnvironments(true);
		this.listener.valueChange(this.event);
		
		Mockito.verify(this.environmentsComponent).getInvalidEnvironments(true);
		Mockito.verifyZeroInteractions(this.window);
		Mockito.verify(this.environmentsTable, Mockito.never()).refreshRowCache();
		Assert.assertTrue(this.model1.getActive());
		Assert.assertTrue(this.model2.getActive());
	}
	@Test
	public void testFooterCheckboxSelectedWithInvalidEnvironment(){
		Mockito.doReturn(true).when(this.property).getValue();
		Mockito.doReturn(Arrays.asList("ENV 1")).when(this.environmentsComponent).getInvalidEnvironments(true);
		this.listener.valueChange(this.event);
		
		Mockito.verify(this.environmentsComponent).getInvalidEnvironments(true);
		Mockito.verify(this.window).showNotification(Matchers.any(Notification.class));
		Mockito.verify(this.environmentsTable, Mockito.never()).refreshRowCache();
		Assert.assertTrue(this.model1.getActive());
		Assert.assertTrue(this.model2.getActive());
	}

}

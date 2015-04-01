package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.GxeTable;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

@RunWith(MockitoJUnitRunner.class)
public class RunMultiSiteActionTest {
	
	@Mock
	private WorkbenchDataManager workbenchDataManager;
	
	@Mock
	GxeTable gxeTable;
	
	@Mock
	MultiSiteParameters parameters;
	
	@Mock
	GxeInput gxeInput;
	
	@InjectMocks
	RunMultiSiteAction multiSiteAction = Mockito.spy(new RunMultiSiteAction(Mockito.mock(ManagerFactory.class), new StudyDataManagerImpl(), 
				gxeTable, null, parameters));
	
	private void setUpButtonClickActionTest() throws MiddlewareQueryException{
		gxeInput = Mockito.mock(GxeInput.class);
		Mockito.doReturn("DummyXML.xml").when(gxeInput).getDestXMLFilePath();
		Mockito.doReturn("DummySummaryStats.csv").when(gxeInput).getSourceCSVSummaryStatsFilePath();
		Mockito.doReturn("DummyMeans.csv").when(gxeInput).getSourceCSVFilePath();
		
		Mockito.doReturn(gxeInput).when(multiSiteAction).generateInputFiles();
	}
	
	
	@Test
	public void testRunBreedingViewOnLocalSetup() throws IllegalAccessException, MiddlewareQueryException{
		setUpButtonClickActionTest();	
		
		ClickEvent clickEvent = Mockito.mock(ClickEvent.class);
		Component layout = Mockito.mock(HorizontalLayout.class);
		Mockito.doReturn(layout).when(clickEvent).getComponent();
		Mockito.doReturn(new Window()).when(layout).getWindow();
		Mockito.doNothing().when(multiSiteAction).launchBV(Mockito.anyString(), Mockito.any(Window.class));
		
		multiSiteAction.setIsServerApp("false");
		multiSiteAction.buttonClick(clickEvent);
		
		Mockito.verify(multiSiteAction, Mockito.times(1)).launchBV(Mockito.anyString(), Mockito.any(Window.class));
		Mockito.verify(multiSiteAction, Mockito.times(0)).zipInputFilesAndDownload(gxeInput);
	}
	
	@Test
	public void testDownloadBVInputFilesOnWebSetup() throws MiddlewareQueryException{
		setUpButtonClickActionTest();
		Mockito.doNothing().when(multiSiteAction).zipInputFilesAndDownload(gxeInput);
		
		multiSiteAction.setIsServerApp("true");
		multiSiteAction.buttonClick(Mockito.mock(ClickEvent.class));
		
		Mockito.verify(multiSiteAction, Mockito.times(1)).zipInputFilesAndDownload(gxeInput);
		Mockito.verify(multiSiteAction, Mockito.times(0)).launchBV(Mockito.anyString(), Mockito.any(Window.class));
		
		
	}

}


package org.generationcp.ibpworkbench.actions;

import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.GxeTable;
import org.generationcp.ibpworkbench.util.GxeInput;
import org.generationcp.ibpworkbench.util.bean.MultiSiteParameters;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.StudyDataManagerImpl;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
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
			this.gxeTable, null, this.parameters));

	private void setUpButtonClickActionTest() throws MiddlewareQueryException {
		this.gxeInput = Mockito.mock(GxeInput.class);
		Mockito.doReturn("DummyXML.xml").when(this.gxeInput).getDestXMLFilePath();
		Mockito.doReturn("DummySummaryStats.csv").when(this.gxeInput).getSourceCSVSummaryStatsFilePath();
		Mockito.doReturn("DummyMeans.csv").when(this.gxeInput).getSourceCSVFilePath();

		Mockito.doReturn(this.gxeInput).when(this.multiSiteAction).generateInputFiles();
	}

	@Test
	public void testRunBreedingViewOnLocalSetup() throws IllegalAccessException, MiddlewareQueryException {
		this.setUpButtonClickActionTest();

		ClickEvent clickEvent = Mockito.mock(ClickEvent.class);
		Component layout = Mockito.mock(HorizontalLayout.class);
		Mockito.doReturn(layout).when(clickEvent).getComponent();
		Mockito.doReturn(new Window()).when(layout).getWindow();
		Mockito.doNothing().when(this.multiSiteAction).launchBV(Matchers.anyString(), Matchers.any(Window.class));

		this.multiSiteAction.setIsServerApp("false");
		this.multiSiteAction.buttonClick(clickEvent);

		Mockito.verify(this.multiSiteAction, Mockito.times(1)).launchBV(Matchers.anyString(), Matchers.any(Window.class));
		Mockito.verify(this.multiSiteAction, Mockito.times(0)).zipInputFilesAndDownload(this.gxeInput);
	}

	@Test
	public void testDownloadBVInputFilesOnWebSetup() throws MiddlewareQueryException {
		this.setUpButtonClickActionTest();
		Mockito.doNothing().when(this.multiSiteAction).zipInputFilesAndDownload(this.gxeInput);

		this.multiSiteAction.setIsServerApp("true");
		this.multiSiteAction.buttonClick(Mockito.mock(ClickEvent.class));

		Mockito.verify(this.multiSiteAction, Mockito.times(1)).zipInputFilesAndDownload(this.gxeInput);
		Mockito.verify(this.multiSiteAction, Mockito.times(0)).launchBV(Matchers.anyString(), Matchers.any(Window.class));

	}

}

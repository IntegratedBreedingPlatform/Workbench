package org.generationcp.ibpworkbench.study;

import java.io.File;

import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserApplication;
import org.generationcp.ibpworkbench.study.util.DatasetExporterException;
import org.generationcp.ibpworkbench.study.util.TableViewerExporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.Application;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import junit.framework.Assert;

public class TableViewerComponentTest {
	
	private static final String STUDY_NAME = "TEST_TRIAL  ";
	
	private static final String XLS_FILEPATH = "/someDirectory/output/" + TableViewerComponent.FILENAME_PREFIX + ".xlsx";
	
	@Mock
	private TableViewerExporter tableViewerExporter;
	
	@Mock
	private Window window;
	
	@Mock
	private Application application;
	
	@Mock
	private Window parentComponent;
	
	@Mock 
	private TableViewerDatasetTable tableViewerTable;
	
	private TableViewerComponent tableViewer;
	
	@Before
	public void setup() throws DatasetExporterException {
		MockitoAnnotations.initMocks(this);
		this.tableViewer = new TableViewerComponent(this.tableViewerTable);
		Mockito.when(this.tableViewerTable.getVisibleColumns()).thenReturn(new Object[]{"STUDY_ID"});
		Mockito.when(this.tableViewerExporter.exportToExcel(Matchers.anyString())).thenReturn(XLS_FILEPATH);
	}
	
	@Test
	public void testExportToExcelActionStudyNameIsNull() throws DatasetExporterException {
		// Need to spy to be able to mock window to open file to
		final TableViewerComponent spyComponent = this.setupSpyTableViewer();
		spyComponent.exportToExcelAction();
		
		// Verify file is downloaded to the browser with proper filename
		Mockito.verify(this.tableViewerExporter).exportToExcel(TableViewerComponent.FILENAME_PREFIX);
		final ArgumentCaptor<VaadinFileDownloadResource> fileDownloadResourceCaptor = ArgumentCaptor.forClass(VaadinFileDownloadResource.class);
		Mockito.verify(this.window).open(fileDownloadResourceCaptor.capture(), Matchers.anyString(), Matchers.eq(false));
		final VaadinFileDownloadResource downloadResource = fileDownloadResourceCaptor.getValue();
		Assert.assertEquals(new File(XLS_FILEPATH).getAbsolutePath(), downloadResource.getSourceFile().getAbsolutePath());
		Assert.assertEquals(TableViewerComponent.FILENAME_PREFIX + ".xlsx", downloadResource.getFilename());
		
	}
	
	@Test
	public void testExportToExcelActionStudyNameIsNotNull() throws DatasetExporterException {
		// Need to spy to be able to mock window to open file to
		final TableViewerComponent spyComponent = this.setupSpyTableViewer();
		spyComponent.setStudyName(STUDY_NAME);
		spyComponent.exportToExcelAction();
		
		// Verify file is downloaded to the browser with proper filename
		Mockito.verify(this.tableViewerExporter).exportToExcel(TableViewerComponent.FILENAME_PREFIX);
		final ArgumentCaptor<VaadinFileDownloadResource> fileDownloadResourceCaptor = ArgumentCaptor.forClass(VaadinFileDownloadResource.class);
		Mockito.verify(this.window).open(fileDownloadResourceCaptor.capture(), Matchers.anyString(), Matchers.eq(false));
		final VaadinFileDownloadResource downloadResource = fileDownloadResourceCaptor.getValue();
		Assert.assertEquals(new File(XLS_FILEPATH).getAbsolutePath(), downloadResource.getSourceFile().getAbsolutePath());
		Assert.assertEquals(TableViewerComponent.FILENAME_PREFIX + "_" + STUDY_NAME.replace(" ", "_").trim() + ".xlsx",
				downloadResource.getFilename());
	}
	
	@Test
	public void testExportToExcelActionDatasetExporterExceptionThrown() throws DatasetExporterException {
		// Need to spy to be able to mock window to open file to
		final TableViewerComponent spyComponent = this.setupSpyTableViewer();
		Mockito.doReturn(this.window).when(this.application).getWindow(GermplasmStudyBrowserApplication.STUDY_WINDOW_NAME);
		final String errorMessage = "Some TableViewer error.";
		Mockito.doThrow(new DatasetExporterException(errorMessage)).when(this.tableViewerExporter).exportToExcel(Matchers.anyString());
		spyComponent.exportToExcelAction();
		
		Mockito.verify(this.tableViewerExporter).exportToExcel(TableViewerComponent.FILENAME_PREFIX);
		Mockito.verify(this.window, Mockito.never()).open(Matchers.any(VaadinFileDownloadResource.class), Matchers.anyString(), Matchers.anyBoolean());
		final ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
		Mockito.verify(this.window).showNotification(notifCaptor.capture());
		final Notification error = notifCaptor.getValue();
		Assert.assertEquals(errorMessage, error.getCaption());
		Assert.assertEquals("</br>", error.getDescription());
		
	}
	

	private TableViewerComponent setupSpyTableViewer() {
		final TableViewerComponent spyComponent = Mockito.spy(this.tableViewer);
		spyComponent.setTableViewerExporter(this.tableViewerExporter);
		Mockito.doReturn(this.parentComponent).when(spyComponent).getParent();
		Mockito.doReturn(this.window).when(parentComponent).getWindow();
		Mockito.doReturn(this.application).when(spyComponent).getApplication();
		return spyComponent;
	}
}

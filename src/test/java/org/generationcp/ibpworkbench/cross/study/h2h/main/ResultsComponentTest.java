package org.generationcp.ibpworkbench.cross.study.h2h.main;

import com.vaadin.Application;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import org.generationcp.commons.util.FileNameGenerator;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.EnvironmentForComparison;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.ResultsData;
import org.generationcp.ibpworkbench.cross.study.h2h.main.pojos.TraitForComparison;
import org.generationcp.ibpworkbench.cross.study.h2h.main.util.HeadToHeadDataListExport;
import org.generationcp.ibpworkbench.cross.study.h2h.main.util.HeadToHeadDataListExportException;
import org.generationcp.ibpworkbench.cross.study.util.test.MockCrossStudyDataUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.List;

public class ResultsComponentTest {

	private static final String XLS_FILE_PATH = "/someDirectory/output/HeadtoHeadDataList.xls";

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private HeadToHeadDataListExport listExporter;

	@Mock
	private HeadToHeadCrossStudyMain mainScreen;

	@Mock
	private Application application;

	@Mock
	private Window window;

	private ResultsComponent resultsComponent;

	private final List<EnvironmentForComparison> environments = MockCrossStudyDataUtil.getEqualEnvironmentForComparisons();

	@Before
	public void setup() throws HeadToHeadDataListExportException {
		MockitoAnnotations.initMocks(this);
		// Need to spy to be able to set the window and application of class
		this.resultsComponent = Mockito.spy(new ResultsComponent(this.mainScreen));
		this.resultsComponent.setFinalEnvironmentForComparisonList(this.environments);
		this.resultsComponent.setListExporter(this.listExporter);

		Mockito.when(this.listExporter.exportHeadToHeadDataListExcel(Mockito.anyString(), Mockito.anyListOf(ResultsData.class), Mockito.anySetOf(TraitForComparison.class), Mockito.any(String[].class),
				Mockito.anyMapOf(String.class, String.class))).thenReturn(XLS_FILE_PATH);
		Mockito.doReturn(this.application).when(this.resultsComponent).getApplication();
		Mockito.doReturn(this.window).when(this.resultsComponent).getWindow();
		Mockito.doReturn(this.window).when(this.application).getWindow(Mockito.anyString());
	}

	@Test
	public void testExportButtonClickAction() throws HeadToHeadDataListExportException {
		this.resultsComponent.exportButtonClickAction();

		Mockito.verify(this.listExporter).exportHeadToHeadDataListExcel(Mockito.eq(ResultsComponent.HEAD_TO_HEAD_DATA_LIST), Mockito.anyListOf(ResultsData.class), Mockito.anySetOf(TraitForComparison.class), Mockito.any(String[].class),
				Mockito.anyMapOf(String.class, String.class));

		// Verify file is downloaded to the browser with proper filename
		final ArgumentCaptor<VaadinFileDownloadResource> fileDownloadResourceCaptor = ArgumentCaptor.forClass(VaadinFileDownloadResource.class);
		Mockito.verify(this.window).open(fileDownloadResourceCaptor.capture());
		final VaadinFileDownloadResource downloadResource = fileDownloadResourceCaptor.getValue();
		final String[] uSCount = downloadResource.getFilename().split("_");
		Assert.assertEquals(new File(XLS_FILE_PATH).getAbsolutePath(), downloadResource.getSourceFile().getAbsolutePath());
		Assert.assertTrue(FileNameGenerator.isValidFileNameFormat(downloadResource.getFilename(), FileNameGenerator.XLS_DATE_TIME_PATTERN));
		Mockito.verify(this.window).open(downloadResource);
		Mockito.verify(this.mainScreen).selectFirstTab();
	}

	@Test
	public void testExportButtonClickActionWhenExportExceptionIsThrown() throws HeadToHeadDataListExportException{
		final String message = "Some H2H error.";
		Mockito.doThrow(new HeadToHeadDataListExportException(message)).when(this.listExporter).exportHeadToHeadDataListExcel(
				Mockito.eq(ResultsComponent.HEAD_TO_HEAD_DATA_LIST), Mockito.anyListOf(ResultsData.class),
				Mockito.anySetOf(TraitForComparison.class), Mockito.any(String[].class), Mockito.anyMapOf(String.class, String.class));
		this.resultsComponent.exportButtonClickAction();

		Mockito.verify(this.listExporter).exportHeadToHeadDataListExcel(Mockito.eq(ResultsComponent.HEAD_TO_HEAD_DATA_LIST), Mockito.anyListOf(ResultsData.class), Mockito.anySetOf(TraitForComparison.class), Mockito.any(String[].class),
				Mockito.anyMapOf(String.class, String.class));
		Mockito.verify(this.window, Mockito.never()).open(Mockito.any(VaadinFileDownloadResource.class));
		Mockito.verify(this.mainScreen, Mockito.never()).selectFirstTab();
		final ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
		Mockito.verify(this.window).showNotification(notifCaptor.capture());
		final Notification error = notifCaptor.getValue();
		Assert.assertEquals("Error with exporting list.", error.getCaption());
		Assert.assertEquals("</br>" + message, error.getDescription());
	}

	@Test
	public void testBackButtonClickAction(){
		this.resultsComponent.backButtonClickAction();
		Mockito.verify(this.mainScreen).selectThirdTab();
	}
}

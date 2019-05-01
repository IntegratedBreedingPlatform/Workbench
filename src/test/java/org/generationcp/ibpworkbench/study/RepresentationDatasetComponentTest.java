
package org.generationcp.ibpworkbench.study;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.Link;
import org.generationcp.commons.util.VaadinFileDownloadResource;
import org.generationcp.ibpworkbench.GermplasmStudyBrowserApplication;
import org.generationcp.ibpworkbench.study.containers.RepresentationDatasetQueryFactory;
import org.generationcp.ibpworkbench.study.util.DatasetExporter;
import org.generationcp.ibpworkbench.study.util.DatasetExporterException;
import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.Application;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import junit.framework.Assert;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

public class RepresentationDatasetComponentTest {
	
	private static final String XLS_FILEPATH = "/someDirectory/output/" + RepresentationDatasetComponent.TEMP_FILENAME + ".xls";

	private static final int DATASET_ID = 2;

	@Mock
	private DatasetExporter datasetExporter;

	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private Window window;
	
	@Mock
	private Application application;
	
	private DataSet mockDataset;
	private RepresentationDatasetComponent datasetComponent;

	@Before
	public void setup() throws DatasetExporterException {
		MockitoAnnotations.initMocks(this);
		this.datasetComponent = new RepresentationDatasetComponent(this.studyDataManager, RepresentationDatasetComponentTest.DATASET_ID, "",
				1, false, false);
		this.mockDataset = this.createMockDataset();
		Mockito.doReturn(this.mockDataset).when(this.studyDataManager).getDataSet(RepresentationDatasetComponentTest.DATASET_ID);
		Mockito.doReturn(XLS_FILEPATH).when(this.datasetExporter)
				.exportToFieldBookExcelUsingIBDBv2(RepresentationDatasetComponent.TEMP_FILENAME);
	}

	private DataSet createMockDataset() {
		DataSet dataSet = new DataSet();
		VariableTypeList variables = new VariableTypeList();
		for (DMSVariableType variable : RepresentationDatasetComponentTest.createTestFactors()) {
			variables.add(variable);
		}
		dataSet.setVariableTypes(variables);
		return dataSet;
	}

	private static List<DMSVariableType> createTestFactors() {
		List<DMSVariableType> factors = new ArrayList<>();

		StandardVariable entryNoVariable = new StandardVariable();
		entryNoVariable.setId(TermId.ENTRY_NO.getId());
		entryNoVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryNoVariable.setProperty(new Term(1, "GERMPLASM ENTRY", "GERMPLASM ENTRY"));
		DMSVariableType varType = new DMSVariableType("ENTRY_NO", "ENTRY_NO", entryNoVariable, 1);
		varType.setLocalName("ENTRY_NO");
		factors.add(varType);

		StandardVariable gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		varType = new DMSVariableType("GID", "GID", gidVariable, 2);
		varType.setLocalName("GID");
		factors.add(varType);

		StandardVariable desigVariable = new StandardVariable();
		desigVariable.setId(TermId.DESIG.getId());
		desigVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		desigVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		varType = new DMSVariableType("DESIGNATION", "DESIGNATION", desigVariable, 3);
		varType.setLocalName("DESIG");
		factors.add(varType);

		StandardVariable entryTypeVariable = new StandardVariable();
		entryTypeVariable.setId(TermId.ENTRY_TYPE.getId());
		entryTypeVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		entryTypeVariable.setProperty(new Term(1, "ENTRY TYPE", "ENTRY_TYPE"));
		varType = new DMSVariableType("ENTRY_TYPE", "ENTRY_TYPE", entryTypeVariable, 4);
		varType.setLocalName("ENTRY_TYPE");
		factors.add(varType);

		StandardVariable repVariable = new StandardVariable();
		repVariable.setId(TermId.REP_NO.getId());
		repVariable.setPhenotypicType(PhenotypicType.TRIAL_DESIGN);
		repVariable.setProperty(new Term(1, "REP_NO", "REP_NO"));
		varType = new DMSVariableType("REP_NO", "REP_NO", repVariable, 5);
		varType.setLocalName("REP_NO");
		factors.add(varType);

		return factors;
	}

	@Test
	public void testValidateNoDuplicateColumns() throws MiddlewareException {
		// duplicate GID variable
		StandardVariable gidVariable = new StandardVariable();
		gidVariable = new StandardVariable();
		gidVariable.setId(TermId.GID.getId());
		gidVariable.setPhenotypicType(PhenotypicType.GERMPLASM);
		gidVariable.setProperty(new Term(1, "GERMPLASM ID", "GERMPLASM ID"));
		DMSVariableType varType = new DMSVariableType("GID", "GID", gidVariable, 6);
		varType.setLocalName("GID");
		mockDataset.getVariableTypes().add(varType);

		Table table = this.datasetComponent.generateLazyDatasetTable(false);
		Assert.assertTrue("Table should only have 5 columns, excluding duplicate variables", table.getColumnHeaders().length == 5);
	}

	@Test
	public void testPopulateDatasetContainerProperties() {
		final List<String> columnIds = new ArrayList<>();
		columnIds.add(new StringBuffer().append(TermId.GID.getId()).append("-").append(TermId.GID.name()).toString());
		columnIds.add(new StringBuffer().append(TermId.ENTRY_NO.getId()).append("-").append(TermId.ENTRY_NO.name()).toString());

		final RepresentationDatasetQueryFactory factory = new RepresentationDatasetQueryFactory(this.studyDataManager, 1, columnIds, false, 1);
		final LazyQueryContainer datasetContainer = new LazyQueryContainer(factory, false, 50);

		this.datasetComponent.populateDatasetContainerProperties(false, columnIds, datasetContainer);

		Assert.assertEquals(Link.class, datasetContainer.getQueryView().getQueryDefinition().getPropertyType(columnIds.get(0)));
		Assert.assertEquals(String.class, datasetContainer.getQueryView().getQueryDefinition().getPropertyType(columnIds.get(1)));
	}

	@Test
	public void testValidateDatasetVariablesAreExcludedFromTable() throws MiddlewareException {
		// add DatasetVariables
		StandardVariable datasetVariable = new StandardVariable();
		datasetVariable = new StandardVariable();
		datasetVariable.setId(TermId.DATASET_NAME.getId());
		datasetVariable.setPhenotypicType(PhenotypicType.DATASET);
		DMSVariableType varType = new DMSVariableType("DATASET_NAME", "DATASET_NAME", datasetVariable, 6);
		varType.setLocalName("DATASET_NAME");
		mockDataset.getVariableTypes().add(varType);

		datasetVariable = new StandardVariable();
		datasetVariable.setId(TermId.DATASET_TITLE.getId());
		datasetVariable.setPhenotypicType(PhenotypicType.DATASET);
		varType = new DMSVariableType("DATASET_TITLE", "DATASET_TITLE", datasetVariable, 7);
		varType.setLocalName("DATASET_TITLE");
		mockDataset.getVariableTypes().add(varType);

		datasetVariable = new StandardVariable();
		datasetVariable.setId(TermId.DATASET_NAME.getId());
		datasetVariable.setPhenotypicType(PhenotypicType.DATASET);
		varType = new DMSVariableType("DATASET_TYPE", "DATASET_TYPE", datasetVariable, 6);
		varType.setLocalName("DATASET_TYPE");
		mockDataset.getVariableTypes().add(varType);

		Table table = this.datasetComponent.generateLazyDatasetTable(false);
		Assert.assertTrue("Table should only have 5 columns, excluding dataset variables", table.getColumnHeaders().length == 5);
	}
	
	@Test
	public void testExportToExcelAction() throws DatasetExporterException {
		// Need to spy to be able to mock window to open file to
		final RepresentationDatasetComponent spyComponent = Mockito.spy(this.datasetComponent);
		spyComponent.setDatasetExporter(this.datasetExporter);
		Mockito.doReturn(this.window).when(spyComponent).getWindow();
		Mockito.doReturn(this.application).when(spyComponent).getApplication();
		spyComponent.exportToExcelAction();
		
		// Verify file is downloaded to the browser with proper filename
		Mockito.verify(this.datasetExporter).exportToFieldBookExcelUsingIBDBv2(RepresentationDatasetComponent.TEMP_FILENAME);
		final ArgumentCaptor<VaadinFileDownloadResource> fileDownloadResourceCaptor = ArgumentCaptor.forClass(VaadinFileDownloadResource.class);
		Mockito.verify(this.window).open(fileDownloadResourceCaptor.capture(), ArgumentMatchers.<String>isNull(), ArgumentMatchers.eq(false));
		final VaadinFileDownloadResource downloadResource = fileDownloadResourceCaptor.getValue();
		Assert.assertEquals(new File(XLS_FILEPATH).getAbsolutePath(), downloadResource.getSourceFile().getAbsolutePath());
		Assert.assertEquals(RepresentationDatasetComponent.XLS_DOWNLOAD_FILENAME, downloadResource.getFilename());
	}
	
	@Test
	public void testExportToExcelActionDatasetExporterExceptionThrown() throws DatasetExporterException {
		// Need to spy to be able to mock window to open file to
		final RepresentationDatasetComponent spyComponent = Mockito.spy(this.datasetComponent);
		spyComponent.setDatasetExporter(this.datasetExporter);
		final String message = "Some DatasetExporterException message.";
		Mockito.doThrow(new DatasetExporterException(message)).when(this.datasetExporter)
				.exportToFieldBookExcelUsingIBDBv2(ArgumentMatchers.anyString());
		Mockito.doReturn(this.application).when(spyComponent).getApplication();
		Mockito.doReturn(this.window).when(this.application).getWindow(GermplasmStudyBrowserApplication.STUDY_WINDOW_NAME);
		spyComponent.exportToExcelAction();
		
		Mockito.verify(this.datasetExporter).exportToFieldBookExcelUsingIBDBv2(RepresentationDatasetComponent.TEMP_FILENAME);
		Mockito.verify(this.window, Mockito.never()).open(ArgumentMatchers.any(VaadinFileDownloadResource.class), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyBoolean());
		final ArgumentCaptor<Notification> notifCaptor = ArgumentCaptor.forClass(Notification.class);
		Mockito.verify(this.window).showNotification(notifCaptor.capture());
		final Notification error = notifCaptor.getValue();
		Assert.assertEquals(message, error.getCaption());
		Assert.assertEquals("</br>", error.getDescription());
	}

}

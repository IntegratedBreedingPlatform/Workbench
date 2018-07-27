package org.generationcp.ibpworkbench.study.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.ibpworkbench.study.constants.StudyTemplateConstants;
import org.generationcp.middleware.data.initializer.DMSVariableTestDataInitializer;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.workbench.ToolName;
import org.generationcp.middleware.util.PoiUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DatasetExporterTest {
	
	private static final String STUDY_NAME = "Test Study";

	@Mock
	private ContextUtil contextUtil;
	
	@InjectMocks
	private DatasetExporter datasetExporter;
	
	private final InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	private HSSFCellStyle cellStyleForObservationSheet;
	private HSSFRow row;

	@Before
	public void setup() {
		final HSSFWorkbook workbook = new HSSFWorkbook();
		this.cellStyleForObservationSheet = workbook.createCellStyle();
		final HSSFSheet observationSheet = workbook.createSheet(StudyTemplateConstants.OBSERVATION_TAB.getHeader());
		this.row = observationSheet.createRow(0);
		MockitoAnnotations.initMocks(this);
		this.datasetExporter.setContextUtil(this.contextUtil);
		Mockito.doReturn(ProjectTestDataInitializer.createProject()).when(this.contextUtil).getProjectInContext();
	}
	
	@Test
	public void testWriteExcelFile() throws DatasetExporterException {
		final String filePath = this.datasetExporter.writeExcelFile(STUDY_NAME, new HSSFWorkbook());
		
		final File excelFile = new File(filePath);
		Assert.assertTrue(excelFile.exists());
		Assert.assertTrue(excelFile.getName().startsWith(STUDY_NAME));
		Assert.assertTrue(excelFile.getName().endsWith(".xls"));
		final String outputDirectory = this.installationDirectoryUtil
				.getOutputDirectoryForProjectAndTool(this.contextUtil.getProjectInContext(), ToolName.STUDY_BROWSER);
		final File outputDirectoryFile = new File(outputDirectory);
		Assert.assertTrue(outputDirectoryFile.exists());
		Assert.assertEquals(outputDirectoryFile, excelFile.getParentFile());
		
		this.deleteTestInstallationDirectory();
	}
	
	@Test
	public void testPopulateCellValueForNumeric() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LATITUDE,
				"10");
		Cell cell = PoiUtil.createCell(this.cellStyleForObservationSheet, this.row, (short) 0, CellStyle.ALIGN_CENTER,
				CellStyle.ALIGN_CENTER);
		this.datasetExporter.populateCellValue(new HashMap<String, String>(), variable, cell, true);
		Assert.assertEquals("10.0", String.valueOf(cell.getNumericCellValue()));
	}
	
	@Test
	public void testPopulateCellValueForDate() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.DATE_VARIABLE,
				"10/10/2018");
		final Cell cell = PoiUtil.createCell(this.cellStyleForObservationSheet, this.row, (short) 0, CellStyle.ALIGN_CENTER,
				CellStyle.ALIGN_CENTER);
		this.datasetExporter.populateCellValue(new HashMap<String, String>(), variable, cell, true);
		Assert.assertEquals("10/10/2018", cell.getStringCellValue());
	}
	
	@Test
	public void testPopulateCellValueForString() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.DESIG,
				"Desig");
		final Cell cell = PoiUtil.createCell(this.cellStyleForObservationSheet, this.row, (short) 0, CellStyle.ALIGN_CENTER,
				CellStyle.ALIGN_CENTER);
		this.datasetExporter.populateCellValue(new HashMap<String, String>(), variable, cell, false);
		Assert.assertEquals("Desig", cell.getStringCellValue());
	}
	
	@Test
	public void testPopulateCellValueForNullValue() {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.DESIG,
				null);
		final Cell cell = PoiUtil.createCell(this.cellStyleForObservationSheet, this.row, (short) 0, CellStyle.ALIGN_CENTER,
				CellStyle.ALIGN_CENTER);
		this.datasetExporter.populateCellValue(new HashMap<String, String>(), variable, cell, false);
		Assert.assertEquals("", cell.getStringCellValue());
	}
	
	@Test
	public void testPopulateCellValueForLocation() {
		Map<String, String> locationNameMap = new HashMap<>();
		locationNameMap.put("9015", "INT WATER MANAGEMENT INSTITUTE");
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(TermId.LOCATION_ID,
				"9015");
		final Cell cell = PoiUtil.createCell(this.cellStyleForObservationSheet, this.row, (short) 0, CellStyle.ALIGN_CENTER,
				CellStyle.ALIGN_CENTER);
		this.datasetExporter.populateCellValue(locationNameMap, variable, cell, false);
		Assert.assertEquals("INT WATER MANAGEMENT INSTITUTE", cell.getStringCellValue());
	}
	
	private void deleteTestInstallationDirectory() {
		// Delete test installation directory and its contents as part of cleanup
		final File testInstallationDirectory = new File(InstallationDirectoryUtil.WORKSPACE_DIR);
		this.installationDirectoryUtil.recursiveFileDelete(testInstallationDirectory);
	}

}

package org.generationcp.ibpworkbench.util.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.ibpworkbench.util.SheetUtil;
import org.junit.Test;

public class TestSheetUtil {

	@Test
	public void testSheetToCSV() {
		String xlsFilename = "Project Gohan nii_-6_RLWOFE99L-PD.xls";
		String csvFilename = "Project Gohan nii_-6_RLWOFE99L-PD.csv";
		String dir = "C:"+ File.separator+ "IBWorkflowSystem"+ File.separator + "workspace" + File.separator + "4-Project Gohan nii" + File.separator + "breeding_view" + File.separator + "input";

		Workbook wb;
		try {
			wb = WorkbookFactory.create(new FileInputStream(dir + File.separator + xlsFilename));
			
			SheetUtil.sheetToCSV(wb.getSheetAt(1),new File(dir + File.separator + csvFilename));
			
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}

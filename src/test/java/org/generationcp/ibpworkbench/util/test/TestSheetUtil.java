package org.generationcp.ibpworkbench.util.test;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.generationcp.ibpworkbench.util.SheetUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.fail;

public class TestSheetUtil {

    public static final String SHEET_FILE_NAME = "Test.xls";
    public static final String CSV_FILE_NAME = "Test.csv";
    public static final String OUTPUT_PATH_PROPERTY = "test.output.directory";
    private Workbook workbook;
    private File outputFile;

    @Before
    public void setUp() {
        try {
            InputStream sheetStream = ClassLoader.getSystemResourceAsStream(SHEET_FILE_NAME);
            workbook = WorkbookFactory.create(sheetStream);
            sheetStream.close();

            InputStream propertiesStream = ClassLoader.getSystemResourceAsStream("test.properties");
            Properties props = new Properties();
            props.load(propertiesStream);
            propertiesStream.close();

            String outputDirectory = props.getProperty(OUTPUT_PATH_PROPERTY);
            File file = new File(outputDirectory);
            if (!file.exists()) {
                file.mkdir();
            }

            outputFile = new File(outputDirectory + File.separator + CSV_FILE_NAME);
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (InvalidFormatException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSheetToCSV() {
        /*String xlsFilename = "Project Gohan nii_-6_RLWOFE99L-PD.xls";
		String csvFilename = "Project Gohan nii_-6_RLWOFE99L-PD.csv";
		String dir = "C:"+ File.separator+ "IBWorkflowSystem"+ File.separator + "workspace" + File.separator + "4-Project Gohan nii" + File.separator + "breeding_view" + File.separator + "input";*/


        try {
            SheetUtil.sheetToCSV(workbook.getSheetAt(1), outputFile);
        } catch (IOException e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test Remove second col (SITECODE)
     */
    /*@Test
    public void testRemoveColThenWriteToCSV() {
		
		
		*//*String xlsFilename = "Project Gohan nii_-6_RLWOFE99L-PD.xls";
		String csvFilename = "Project Gohan nii_-6_RLWOFE99L-PD.csv";
		String dir = "C:"+ File.separator+ "IBWorkflowSystem"+ File.separator + "workspace" + File.separator + "4-Project Gohan nii" + File.separator + "breeding_view" + File.separator + "input";*//*

        int colToRemove = 1;

        Workbook wb;
        try {
            wb = WorkbookFactory.create(new FileInputStream(dir + File.separator + xlsFilename));

            *//**SheetUtil.deleteColumn(wb.getSheetAt(1),colToRemove);

             // write to CSV
             SheetUtil.sheetToCSV(wb.getSheetAt(1),new File(dir + File.separator + csvFilename));
             **//*

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

    }*/

}

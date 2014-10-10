package org.generationcp.ibpworkbench.util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.generationcp.ibpworkbench.util.SchemaVersionUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SchemaVersionUtilTest {
	
	private File workbenchFile;
	private File emptyFile;
	private final String workbenchFilePath = System.getProperty("user.dir")+"/workbench.properties";
	private final String emptyFilePath = System.getProperty("user.dir")+"/empty.properties";
	private String minimumCropVersion;
	
	@Before
    public void setup() throws SecurityException, NoSuchFieldException, Exception{
		workbenchFile = new File(workbenchFilePath);
		FileWriter workbenchFileWriter = new FileWriter(workbenchFile);
		minimumCropVersion = "3.0.3";
		String fileContent = SchemaVersionUtil.MINIMUM_CROP_VERSION+"="+minimumCropVersion;
		PrintWriter printWriter = new PrintWriter(workbenchFileWriter);
		printWriter.print(fileContent);    	
		printWriter.close();
		workbenchFileWriter.close();
		
		emptyFile = new File(emptyFilePath);
		FileWriter emptyFileWriter = new FileWriter(emptyFile);
		emptyFileWriter.close();
		
    }
	@Test
	public void testNullOrEmptyMinimumCropVersion() {
		Assert.assertTrue("Any version is supported if minimum crop version is null",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.0",null));
		Assert.assertTrue("Any version is supported if minimum crop version is empty",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.0",""));
		Assert.assertTrue("Version can be null if minimum crop version is null",
				SchemaVersionUtil.checkIfVersionIsSupported(null,null));
		Assert.assertTrue("Version can be null if minimum crop version is empty",
				SchemaVersionUtil.checkIfVersionIsSupported(null,""));
	}
	@Test
	public void testNullOrEmptyVersion() {
		Assert.assertFalse("Version must not be null if minimum crop version is defined",
				SchemaVersionUtil.checkIfVersionIsSupported(null,"3.0.0"));
		Assert.assertFalse("Version must not be empty if minimum crop version is defined",
				SchemaVersionUtil.checkIfVersionIsSupported("","3.0.0"));
	}
	
	@Test
	public void testSameVersion() {
		Assert.assertTrue("Version is supported if it is equal to the minimum crop version",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.1.RELEASE","3.0.1.RELEASE"));
	}
	
	@Test
	public void testSameDiffCaseVersion() {
		Assert.assertTrue("Version is supported if it is equal (case-insensitive) to the minimum crop version",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.1.release","3.0.1.RELEASE"));
	}
	
	@Test
	public void testHigherVersion() {
		Assert.assertTrue("Version (with non-numeric characters) is supported if it is higher than the minimum crop version",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.1.RELEASE","3.0.0"));
		Assert.assertTrue("Version (all numeric characters) is supported if it is higher than the minimum crop version",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.0","2.1.3.5"));
		Assert.assertTrue("Version (with special characters) is supported if it is higher than the minimum crop version",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.1.%*^&^","3.0.0"));
		Assert.assertTrue("Version (with more digits) is supported if it is higher than the minimum crop version",
				SchemaVersionUtil.checkIfVersionIsSupported("3.1.2","3.1"));
	}
	
	@Test
	public void testLowerVersion() {
		Assert.assertFalse("Version is not supported if it is lower than the minimum crop version (with non-numeric characters)",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.0","3.0.1.RELEASE"));
		Assert.assertFalse("Version is not supported if it is lower than the minimum crop version (all numeric characters)",
				SchemaVersionUtil.checkIfVersionIsSupported("2.1.0.1","3.0.0"));
		Assert.assertFalse("Version is not supported if it is lower than the minimum crop version (with special characters)",
				SchemaVersionUtil.checkIfVersionIsSupported("3.0.0.RELEASE","3.0.1.%*^&^"));
		Assert.assertFalse("Version (with less digits) is supported if it is higher than the minimum crop version",
				SchemaVersionUtil.checkIfVersionIsSupported("3.1","3.1.2"));
	}
	
	@Test
	public void testLoadPropertiesNonExistingFile() {
		Assert.assertNull("A non-existing properties file should return null",
				SchemaVersionUtil.loadPropertiesFile("dummy.properties"));
	}
	
	@Test
	public void testLoadPropertiesExistingFile() {
		Assert.assertNotNull("An existing properties file should not return null",
				SchemaVersionUtil.loadPropertiesFile(workbenchFilePath));
	}
	
	@Test
	public void testGetMinimumGetVersionNotFound() {
		Assert.assertEquals("Minimum crop version is empty if it is not defined in the config file",
				"",SchemaVersionUtil.getMinimumCropVersionFromFile(emptyFilePath));
	}
	
	@Test
	public void testGetMinimumGetVersionFound() {
		String expectedCurrentCropVersion = minimumCropVersion;
		String actualCurrentCropVersion = SchemaVersionUtil.getMinimumCropVersionFromFile(workbenchFilePath);
		Assert.assertNotNull("Minimum crop version is not null if it is defined in the config file",
				actualCurrentCropVersion);
		Assert.assertEquals("Minimum crop version must be equal to "+expectedCurrentCropVersion, 
				expectedCurrentCropVersion, actualCurrentCropVersion);
	}
	
	@After
	public void cleanUp() {
		if(workbenchFile!=null) {
			workbenchFile.delete();
		}
		if(emptyFile!=null) {
			emptyFile.delete();
		}
	}
}


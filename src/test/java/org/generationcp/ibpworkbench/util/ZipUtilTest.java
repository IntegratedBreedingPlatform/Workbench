/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.ibpworkbench.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.generationcp.middleware.util.Debug;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtilTest {
    
    private static final Logger LOG = LoggerFactory.getLogger(ZipUtilTest.class);
    List<String> filenameList;
    String zipFilename = "test.zip";

    @Before
    public void setUp() {
    	filenameList = new ArrayList<String>();
    	filenameList.add("test1.txt");
    	filenameList.add("test2.txt");
    	try {
	    	for(String fName : filenameList){
	    		File f = new File(fName);
	    		
					f.createNewFile();
				
	    	}
    	} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
    }
    
    private void deleteFiles(){
    	for(String fName : filenameList){
    		File f = new File(fName);
    		f.delete();			
    	}
    	File zipFile = new File(zipFilename);
    	zipFile.deleteOnExit();
    }
	
	/**
	 * Test file zipping.
	 */
	@Test
	public void testFileZipping() {
		ZipUtil.zipIt(zipFilename, filenameList);
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(zipFilename);
		    Enumeration<? extends ZipEntry> entries = zipFile.entries();
		    int size = 0;
		    while(entries.hasMoreElements()){
		        ZipEntry entry = entries.nextElement();
		        Debug.println(0, entry.getName());
		        assertFalse(!filenameList.contains(entry.getName()));
		        size++;
		    }
		    assertEquals(filenameList.size(), size);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		deleteFiles();
	}
	
	/**
	 * Test file extraction.
	 * @throws URISyntaxException 
	 */
	@Test
	public void testFileExtraction() throws URISyntaxException {
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource("zipToExtract.zip").toURI());
		String destination = ClassLoader.getSystemResource("").getPath();
		ZipUtil.extractZip(file.getAbsolutePath(), destination);
		
		File testFile = new File(destination + File.separator + "test.txt");
	    assertTrue(testFile.exists());
	    
	    testFile.delete();
	}
	
	/**
	 * Test file extraction - specific file.
	 * @throws URISyntaxException 
	 */
	@Test
	public void testFileExtractionSpecificFile() throws URISyntaxException {
		
		File file = new File(ClassLoader.getSystemClassLoader().getResource("zipToExtract.zip").toURI());
		String destination = ClassLoader.getSystemResource("").getPath();
		File extractedFile = ZipUtil.extractZipSpecificFile(file.getAbsolutePath(), "test" , destination);
	
	    assertTrue(extractedFile.exists());
	    
	    extractedFile.delete();
	}
}

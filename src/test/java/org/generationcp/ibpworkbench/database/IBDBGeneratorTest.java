package org.generationcp.ibpworkbench.database;

import java.io.File;

import org.generationcp.commons.exceptions.SQLFileException;
import org.generationcp.commons.util.MySQLUtil;
import org.junit.Test;
import org.mockito.Mockito;

public class IBDBGeneratorTest {
	
	
	@Test
	public void testRunScriptsInDirectory() throws SQLFileException{
		IBDBGenerator generator = new IBDBGenerator();
		MySQLUtil mySQLUtil = new MySQLUtil();
		MySQLUtil mockSqlUtil = Mockito.spy (mySQLUtil);
		generator.setMySQLUtil(mockSqlUtil);
		
		Mockito.doReturn(true).when(mockSqlUtil).runScriptsInDirectory(Mockito.anyString(), Mockito.any(File.class));
		
		File dummyFile = new File("dummy");
		generator.runScriptsInDirectory("dummy", dummyFile);
		
		//verify that mySQLUtil was invoked to run directory of scripts
		Mockito.verify(mockSqlUtil).runScriptsInDirectory("dummy", dummyFile);
	}
}

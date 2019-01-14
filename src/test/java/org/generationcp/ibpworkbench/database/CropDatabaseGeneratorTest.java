
package org.generationcp.ibpworkbench.database;

import java.io.File;
import java.sql.Connection;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CropDatabaseGeneratorTest {

	private final CropType cropType = new CropType();

	@Mock
	private Connection connection;

	@InjectMocks
	private final CropDatabaseGenerator cropDbGenerator = Mockito.spy(new CropDatabaseGenerator(this.cropType));

	@Mock
	private MySQLUtil mysqlUtil;

	@Before
	public void setup() throws Exception {
		this.cropType.setCropName(CropType.CropEnum.MAIZE.toString());
		this.cropDbGenerator.setMySQLUtil(this.mysqlUtil);
		Mockito.doNothing().when(this.connection).setCatalog(Matchers.anyString());
	}

	@Test
	public void testGenerateDatabase() throws Exception {
		Mockito.doNothing().when(this.cropDbGenerator).createConnection();
		Mockito.doNothing().when(this.cropDbGenerator).createCropDatabase();

		this.cropDbGenerator.generateDatabase();

		// verify that main steps of function are called
		Mockito.verify(this.cropDbGenerator, Mockito.times(1)).createConnection();
		Mockito.verify(this.cropDbGenerator, Mockito.times(1)).createCropDatabase();
		Mockito.verify(this.cropDbGenerator, Mockito.times(1)).runSchemaCreationScripts();

		final File localDatabaseDirectory = new File(CropDatabaseGenerator.DB_SCRIPT_FOLDER);
		Mockito.verify(this.cropDbGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, "common"));
		Mockito.verify(this.cropDbGenerator, Mockito.times(1)).runScriptsInDirectory(null,
				new File(localDatabaseDirectory, this.cropType.getCropName()));
		Mockito.verify(this.cropDbGenerator, Mockito.times(1)).runScriptsInDirectory(null,
				new File(localDatabaseDirectory, "common-update"));
	}

}

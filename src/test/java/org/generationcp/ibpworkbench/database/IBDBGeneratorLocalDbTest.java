
package org.generationcp.ibpworkbench.database;

import java.io.File;
import java.sql.Connection;

import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IBDBGeneratorLocalDbTest {

	private final CropType cropType = new CropType();

	@Mock
	private WorkbenchDataManager workbenchManager;

	@Mock
	private Connection connection;

	@InjectMocks
	private final IBDBGeneratorLocalDb localDbGenerator = Mockito.spy(new IBDBGeneratorLocalDb(this.cropType, 1L));

	@Before
	public void setup() throws Exception {
		this.cropType.setCropName(CropType.CropEnum.MAIZE.toString());
		Mockito.doNothing().when(this.connection).setCatalog(Matchers.anyString());
	}

	@Test
	public void testGenerateDatabase() throws Exception {

		WorkbenchSetting setting = new WorkbenchSetting();
		setting.setInstallationDirectory("C:/BMS");
		Mockito.doReturn(setting).when(this.workbenchManager).getWorkbenchSetting();

		Mockito.doNothing().when(this.localDbGenerator).createConnection();
		Mockito.doNothing().when(this.localDbGenerator).createLocalDatabase();
		Mockito.doNothing().when(this.localDbGenerator).runScriptsInDirectory(Matchers.anyString(), Matchers.any(File.class));

		this.localDbGenerator.generateDatabase();

		// verify that main steps of function are called
		Mockito.verify(this.localDbGenerator, Mockito.times(1)).createConnection();
		Mockito.verify(this.localDbGenerator, Mockito.times(1)).createLocalDatabase();
		Mockito.verify(this.localDbGenerator, Mockito.times(1)).createManagementSystems();

		File localDatabaseDirectory = new File(setting.getInstallationDirectory(), IBDBGeneratorLocalDb.DATABASE_LOCAL);
		Mockito.verify(this.localDbGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, "common"));
		Mockito.verify(this.localDbGenerator, Mockito.times(1)).runScriptsInDirectory(null,
				new File(localDatabaseDirectory, this.cropType.getCropName()));
		Mockito.verify(this.localDbGenerator, Mockito.times(1)).runScriptsInDirectory(null,
				new File(localDatabaseDirectory, "common-update"));
	}

}

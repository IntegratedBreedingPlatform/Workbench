package org.generationcp.ibpworkbench.database;

import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.sql.Connection;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class IBDBGeneratorLocalDbTest {

	private CropType cropType = new CropType();

	@Mock
	private WorkbenchDataManager workbenchManager;

	@Mock
	private Connection connection;

	@InjectMocks
	private IBDBGeneratorLocalDb localDbGenerator = spy(new IBDBGeneratorLocalDb(cropType, 1L));

	@Before
	public void setup() throws Exception {
		cropType.setCropName(CropType.CropEnum.MAIZE.toString());
		doNothing().when(connection).setCatalog(anyString());
	}

	@Test
	public void testGenerateDatabase() throws Exception {

		WorkbenchSetting setting = new WorkbenchSetting();
		setting.setInstallationDirectory("C:/BMS");
		Mockito.doReturn(setting).when(workbenchManager).getWorkbenchSetting();

		Mockito.doNothing().when(localDbGenerator).createConnection();
		Mockito.doNothing().when(localDbGenerator).createLocalDatabase();
		Mockito.doNothing().when(localDbGenerator)
				.runScriptsInDirectory(Mockito.anyString(), Mockito.any(File.class));

		localDbGenerator.generateDatabase();

		//verify that main steps of function are called
		Mockito.verify(localDbGenerator, Mockito.times(1)).createConnection();
		Mockito.verify(localDbGenerator, Mockito.times(1)).createLocalDatabase();
		Mockito.verify(localDbGenerator, Mockito.times(1)).createManagementSystems();

		File localDatabaseDirectory = new File(setting.getInstallationDirectory(),
				IBDBGeneratorLocalDb.DATABASE_LOCAL);
		Mockito.verify(localDbGenerator, Mockito.times(1))
				.runScriptsInDirectory(null, new File(localDatabaseDirectory, "common"));
		Mockito.verify(localDbGenerator, Mockito.times(1)).runScriptsInDirectory(null,
				new File(localDatabaseDirectory, cropType.getCropName()));
		Mockito.verify(localDbGenerator, Mockito.times(1))
				.runScriptsInDirectory(null, new File(localDatabaseDirectory, "common-update"));
	}

}

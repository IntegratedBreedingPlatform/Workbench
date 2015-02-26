package org.generationcp.ibpworkbench.database;

import java.io.File;

import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.Test;
import org.mockito.Mockito;

public class IBDBGeneratorLocalDbTest {
	
	@Test
	public void testGenerateDatabase() throws Exception {
		CropType cropType = new CropType();
		cropType.setCropName(CropType.CropEnum.MAIZE.toString());
		IBDBGeneratorLocalDb localDbGenerator = new IBDBGeneratorLocalDb(cropType, 1L);
		IBDBGeneratorLocalDb moleGenerator = Mockito.spy(localDbGenerator);

		WorkbenchDataManager workbenchManager = Mockito.mock(WorkbenchDataManager.class);
		WorkbenchSetting setting = new WorkbenchSetting();
		setting.setInstallationDirectory("C:/BMS");
		Mockito.doReturn(setting).when(workbenchManager).getWorkbenchSetting();
		moleGenerator.setWorkbenchDataManager(workbenchManager);
		
		Mockito.doNothing().when(moleGenerator).createConnection();
		Mockito.doNothing().when(moleGenerator).createLocalDatabase();
		Mockito.doNothing().when(moleGenerator).runScriptsInDirectory(Mockito.anyString(), Mockito.any(File.class));
		
		moleGenerator.generateDatabase();
		
		//verify that main steps of function are called
		Mockito.verify(moleGenerator, Mockito.times(1)).createConnection();
		Mockito.verify(moleGenerator, Mockito.times(1)).createLocalDatabase();
		Mockito.verify(moleGenerator, Mockito.times(1)).createManagementSystems();
		
		File localDatabaseDirectory = new File(setting.getInstallationDirectory(), IBDBGeneratorLocalDb.DATABASE_LOCAL);
		Mockito.verify(moleGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, "common"));
		Mockito.verify(moleGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, cropType.getCropName()));
		Mockito.verify(moleGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, "common-update"));
	}

}

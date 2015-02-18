package org.generationcp.ibpworkbench.database;

import java.io.File;

import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.junit.Test;
import org.mockito.Mockito;

public class IBDBGeneratorCentralDbTest {
	
	@Test
	public void testGenerateDatabaseNewCustomCrop() throws Exception {
		CropType cropType = new CropType();
		cropType.setCropName("banana");
		IBDBGeneratorCentralDb centralDbGenerator = new IBDBGeneratorCentralDb(cropType);
		IBDBGeneratorCentralDb moleGenerator = Mockito.spy(centralDbGenerator);

		WorkbenchDataManager workbenchManager = Mockito.mock(WorkbenchDataManager.class);
		WorkbenchSetting setting = new WorkbenchSetting();
		setting.setInstallationDirectory("C:/BMS");
		Mockito.doReturn(setting).when(workbenchManager).getWorkbenchSetting();
		moleGenerator.setWorkbenchDataManager(workbenchManager);
		
		Mockito.doReturn(false).when(moleGenerator).databaseExists();
		Mockito.doNothing().when(moleGenerator).createConnection();
		Mockito.doNothing().when(moleGenerator).createDatabase();
		Mockito.doNothing().when(moleGenerator).runScriptsInDirectory(Mockito.anyString(), Mockito.any(File.class));
		
		moleGenerator.generateDatabase();
		
		//verify that main steps of function are called
		Mockito.verify(moleGenerator, Mockito.times(1)).createConnection();
		Mockito.verify(moleGenerator, Mockito.times(1)).createDatabase();
		Mockito.verify(moleGenerator, Mockito.times(1)).createManagementSystems();
		
		File localDatabaseDirectory = new File(setting.getInstallationDirectory(), IBDBGeneratorCentralDb.DATABASE_CENTRAL);
		Mockito.verify(moleGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, "common"));
		Mockito.verify(moleGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, "custom"));
		Mockito.verify(moleGenerator, Mockito.times(1)).runScriptsInDirectory(null, new File(localDatabaseDirectory, "common-update"));
	}
	
	
	@Test
	public void testGenerateDatabaseCustomCropExistsAlready() throws Exception {
		CropType cropType = new CropType();
		cropType.setCropName("banana");
		IBDBGeneratorCentralDb centralDbGenerator = new IBDBGeneratorCentralDb(cropType);
		IBDBGeneratorCentralDb moleGenerator = Mockito.spy(centralDbGenerator);

		Mockito.doReturn(true).when(moleGenerator).databaseExists();
		Mockito.doNothing().when(moleGenerator).createConnection();
		
		moleGenerator.generateDatabase();
		
		//verify that new DB was not created since crop exists already
		Mockito.verify(moleGenerator, Mockito.times(0)).createDatabase();
		Mockito.verify(moleGenerator, Mockito.times(0)).createManagementSystems();
		Mockito.verify(moleGenerator, Mockito.times(0)).runScriptsInDirectory(Mockito.anyString(), Mockito.any(File.class));
	}

}

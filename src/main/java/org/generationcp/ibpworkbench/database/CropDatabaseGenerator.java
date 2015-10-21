/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 **************************************************************/

package org.generationcp.ibpworkbench.database;

import java.io.File;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.exceptions.SQLFileException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class CropDatabaseGenerator extends IBDBGenerator {

	public static final String DB_SCRIPT_FOLDER = "database/merged";

	private static final Logger LOG = LoggerFactory.getLogger(CropDatabaseGenerator.class);

	private CropType cropType;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private Properties workbenchProperties;

	public CropDatabaseGenerator() {
	}

	public CropDatabaseGenerator(CropType cropType) {
		this.cropType = cropType;
	}

	public boolean generateDatabase() {

		boolean isGenerationSuccess = false;

		try {
			this.createConnection();
			this.createCropDatabase();
			this.runSchemaCreationScripts();
			this.generatedDatabaseName = this.cropType.getDbName();
			this.connection.setCatalog(this.generatedDatabaseName);
			isGenerationSuccess = true;
		} catch (InternationalizableException e) {
			isGenerationSuccess = false;
			throw e;
		} catch (SQLException e) {
			isGenerationSuccess = false;
			CropDatabaseGenerator.handleDatabaseError(e);
		} finally {
			this.closeConnection();
		}

		return isGenerationSuccess;
	}

	protected void createCropDatabase() {

		String databaseName = this.cropType.getDbName();
		StringBuilder createDatabaseSyntax = new StringBuilder();

		Statement statement = null;

		try {

			statement = this.connection.createStatement();

			createDatabaseSyntax.append(IBDBGenerator.SQL_CREATE_DATABASE).append(databaseName).append(IBDBGenerator.SQL_CHAR_SET)
					.append(IBDBGenerator.DEFAULT_CHAR_SET).append(IBDBGenerator.SQL_COLLATE).append(IBDBGenerator.DEFAULT_COLLATE);

			if (this.isLanInstallerMode(this.workbenchProperties)) {
				statement.addBatch(createDatabaseSyntax.toString());

				String grantFormat = "GRANT ALL ON %s.* TO %s@'%s' IDENTIFIED BY '%s'";

				// grant the user
				String allGrant =
						String.format(grantFormat, databaseName, IBDBGenerator.DEFAULT_LOCAL_USER, "%",
								IBDBGenerator.DEFAULT_LOCAL_PASSWORD);
				String localGrant =
						String.format(grantFormat, databaseName, IBDBGenerator.DEFAULT_LOCAL_USER, IBDBGenerator.DEFAULT_LOCAL_HOST,
								IBDBGenerator.DEFAULT_LOCAL_PASSWORD);

				statement.execute(allGrant);
				statement.execute(localGrant);
				statement.execute("FLUSH PRIVILEGES");

				statement.executeBatch();
			} else {
				StringBuilder createGrantSyntax = new StringBuilder();
				StringBuilder createFlushSyntax = new StringBuilder();
				statement.executeUpdate(createDatabaseSyntax.toString());

				createGrantSyntax.append(IBDBGenerator.SQL_GRANT_ALL).append(databaseName).append(IBDBGenerator.SQL_PERIOD)
						.append(IBDBGenerator.DEFAULT_ALL).append(IBDBGenerator.SQL_TO).append(IBDBGenerator.SQL_SINGLE_QUOTE)
						.append(IBDBGenerator.DEFAULT_LOCAL_USER).append(IBDBGenerator.SQL_SINGLE_QUOTE).append(IBDBGenerator.SQL_AT_SIGN)
						.append(IBDBGenerator.SQL_SINGLE_QUOTE).append(IBDBGenerator.DEFAULT_LOCAL_HOST)
						.append(IBDBGenerator.SQL_SINGLE_QUOTE).append(IBDBGenerator.SQL_IDENTIFIED_BY)
						.append(IBDBGenerator.SQL_SINGLE_QUOTE).append(IBDBGenerator.DEFAULT_LOCAL_PASSWORD)
						.append(IBDBGenerator.SQL_SINGLE_QUOTE);

				statement.executeUpdate(createGrantSyntax.toString());

				createFlushSyntax.append(IBDBGenerator.SQL_FLUSH_PRIVILEGES);

				statement.executeUpdate(createFlushSyntax.toString());
			}

			this.generatedDatabaseName = databaseName;

			this.connection.setCatalog(databaseName);
		} catch (SQLException e) {
			CropDatabaseGenerator.handleDatabaseError(e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					CropDatabaseGenerator.handleDatabaseError(e);
				}
			}
		}
	}

	protected void runSchemaCreationScripts() {
		try {
			WorkbenchSetting setting = this.workbenchDataManager.getWorkbenchSetting();
			if (setting == null) {
				throw new IllegalStateException("Workbench setting record not found");
			}

			File localDatabaseDirectory = new File(setting.getInstallationDirectory(), CropDatabaseGenerator.DB_SCRIPT_FOLDER);
			// run the common scripts
			this.runScriptsInDirectory(this.generatedDatabaseName, new File(localDatabaseDirectory, "common"));

			// run crop specific script
			this.runScriptsInDirectory(this.generatedDatabaseName, new File(localDatabaseDirectory, this.cropType.getCropName()));

			// run the common-update scripts
			this.runScriptsInDirectory(this.generatedDatabaseName, new File(localDatabaseDirectory, "common-update"));

		} catch (SQLFileException | MiddlewareQueryException e) {
			CropDatabaseGenerator.handleDatabaseError(e);
		}
	}

	public static void handleDatabaseError(Exception e) {
		CropDatabaseGenerator.LOG.error(e.toString(), e);
		throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
	}

	public static void handleConfigurationError(Exception e) {
		CropDatabaseGenerator.LOG.error(e.toString(), e);
		throw new InternationalizableException(e, Message.CONFIG_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
	}

	public void setCropType(CropType cropType) {
		this.cropType = cropType;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}

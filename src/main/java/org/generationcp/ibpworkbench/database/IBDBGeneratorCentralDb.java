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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @deprecated No "on the fly" DB generation in merged db world. Keeping this class mainly as some backup/restore code (which also needs
 *             re-engineering BMS-209) refers to it.
 */
@Configurable
@Deprecated
public class IBDBGeneratorCentralDb extends IBDBGenerator {

	public static final String DATABASE_CENTRAL = "database/central";
	private static final Logger LOG = LoggerFactory.getLogger(IBDBGeneratorCentralDb.class);

	private CropType cropType;
	private boolean alreadyExistsFlag = false;

	@Resource
	private Properties workbenchProperties;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	public IBDBGeneratorCentralDb() {

	}

	public IBDBGeneratorCentralDb(CropType cropType) {
		this.cropType = cropType;
	}

	public boolean isAlreadyExists() {
		return this.alreadyExistsFlag;
	}

	public boolean generateDatabase() {

		boolean isGenerationSuccess = false;

		try {
			this.createConnection();
			this.alreadyExistsFlag = this.databaseExists();
			if (this.alreadyExistsFlag) {
				return true;
			}
			this.createDatabase();
			this.createManagementSystems();
			isGenerationSuccess = true;
		} catch (InternationalizableException e) {
			isGenerationSuccess = false;
			throw e;
		} finally {
			this.closeConnection();
		}
		return isGenerationSuccess;
	}

	protected boolean databaseExists() {
		Statement statement = null;
		try {
			statement = this.connection.createStatement();
			statement.execute("USE " + this.cropType.getDbName());
			return true;
		} catch (SQLException e) {
			IBDBGeneratorCentralDb.LOG.error(e.getMessage(), e);
			return false;
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					IBDBGeneratorCentralDb.LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	protected void createDatabase() {
		StringBuilder createDatabaseSyntax = new StringBuilder();

		String databaseName = this.cropType.getDbName();

		Statement statement = null;
		try {
			statement = this.connection.createStatement();
			createDatabaseSyntax.append(IBDBGenerator.SQL_CREATE_DATABASE_IF_NOT_EXISTS).append(databaseName)
					.append(IBDBGenerator.SQL_CHAR_SET).append(IBDBGenerator.DEFAULT_CHAR_SET).append(IBDBGenerator.SQL_COLLATE)
					.append(IBDBGenerator.DEFAULT_COLLATE);
			statement.addBatch(createDatabaseSyntax.toString());

			if (this.isLanInstallerMode(this.workbenchProperties)) {
				String grantFormat = "GRANT ALL ON %s.* TO %s@'%s' IDENTIFIED BY '%s'";

				// grant the user
				String allGrant =
						String.format(grantFormat, databaseName, IBDBGenerator.DEFAULT_CENTRAL_USER, "%",
								IBDBGenerator.DEFAULT_CENTRAL_PASSWORD);
				String localGrant =
						String.format(grantFormat, databaseName, IBDBGenerator.DEFAULT_CENTRAL_USER, IBDBGenerator.DEFAULT_LOCAL_HOST,
								IBDBGenerator.DEFAULT_CENTRAL_PASSWORD);

				statement.execute(allGrant);
				statement.execute(localGrant);
				statement.execute("FLUSH PRIVILEGES");
			} else {
				StringBuilder createGrantSyntax = new StringBuilder();
				StringBuilder createFlushSyntax = new StringBuilder();
				createGrantSyntax.append(IBDBGenerator.SQL_GRANT_ALL).append(databaseName).append(IBDBGenerator.SQL_PERIOD)
						.append(IBDBGenerator.DEFAULT_ALL).append(IBDBGenerator.SQL_TO).append(IBDBGenerator.SQL_SINGLE_QUOTE)
						.append(IBDBGenerator.DEFAULT_CENTRAL_USER).append(IBDBGenerator.SQL_SINGLE_QUOTE)
						.append(IBDBGenerator.SQL_AT_SIGN).append(IBDBGenerator.SQL_SINGLE_QUOTE).append(IBDBGenerator.DEFAULT_LOCAL_HOST)
						.append(IBDBGenerator.SQL_SINGLE_QUOTE).append(IBDBGenerator.SQL_IDENTIFIED_BY)
						.append(IBDBGenerator.SQL_SINGLE_QUOTE).append(IBDBGenerator.DEFAULT_CENTRAL_PASSWORD)
						.append(IBDBGenerator.SQL_SINGLE_QUOTE);

				statement.addBatch(createGrantSyntax.toString());
				createFlushSyntax.append(IBDBGenerator.SQL_FLUSH_PRIVILEGES);
				statement.addBatch(createFlushSyntax.toString());
				statement.executeBatch();
			}

			statement.executeBatch();

			this.generatedDatabaseName = databaseName.toString();
			this.connection.setCatalog(databaseName.toString());
		} catch (SQLException e) {
			IBDBGenerator.handleDatabaseError(e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					IBDBGenerator.handleDatabaseError(e);
				}
			}
		}
	}

	protected void createManagementSystems() {
		try {
			WorkbenchSetting setting = this.workbenchDataManager.getWorkbenchSetting();
			if (setting == null) {
				throw new IllegalStateException("Workbench setting record not found");
			}

			File localDatabaseDirectory = new File(setting.getInstallationDirectory(), IBDBGeneratorCentralDb.DATABASE_CENTRAL);

			// run the common scripts
			this.runScriptsInDirectory(this.generatedDatabaseName, new File(localDatabaseDirectory, "common"));

			// run the scripts for custom crops
			this.runScriptsInDirectory(this.generatedDatabaseName, new File(localDatabaseDirectory, "custom"));

			// run the common-post scripts
			this.runScriptsInDirectory(this.generatedDatabaseName, new File(localDatabaseDirectory, "common-update"));

			// NOTE: IBDBGeneratorCentralDb is intended to be run for custom crops only,
			// hence, we should not be running scripts for specific crops here

		} catch (SQLFileException | MiddlewareQueryException e) {
			IBDBGenerator.handleDatabaseError(e);
		}
	}

	public void setCropType(CropType cropType) {
		this.cropType = cropType;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}
}

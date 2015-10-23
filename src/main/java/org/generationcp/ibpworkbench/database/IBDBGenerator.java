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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.Resource;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.exceptions.SQLFileException;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.ibpworkbench.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class IBDBGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(IBDBGenerator.class);
	// common constants
	public static final String WORKBENCH_PROP = "workbench.properties";

	public static final String DB_PROP_HOST = "db.host";
	public static final String DB_PROP_PORT = "db.port";
	public static final String DB_PROP_USER = "db.username";
	public static final String DB_PROP_PASSWORD = "db.password";

	public static final String WORKBENCH_PROP_INSTALLER_MODE = "workbench.installer.mode";

	public static final String INSTALLER_MODE_LAN = "lan";
	public static final String INSTALLER_MODE_LOCAL = "local";

	protected static final String SQL_CREATE_DATABASE = "CREATE DATABASE ";
	protected static final String SQL_CREATE_DATABASE_IF_NOT_EXISTS = "CREATE DATABASE IF NOT EXISTS ";
	protected static final String SQL_CHAR_SET = " CHARACTER SET ";
	protected static final String SQL_COLLATE = " COLLATE ";
	protected static final String SQL_GRANT_ALL = "GRANT ALL ON ";
	protected static final String SQL_TO = " TO ";
	protected static final String SQL_IDENTIFIED_BY = " IDENTIFIED BY ";
	protected static final String SQL_FLUSH_PRIVILEGES = "FLUSH PRIVILEGES ";
	protected static final String SQL_SINGLE_QUOTE = "'";
	protected static final String SQL_AT_SIGN = "@";
	protected static final String SQL_PERIOD = ".";
	protected static final String DEFAULT_LOCAL_USER = "local";
	protected static final String DEFAULT_CENTRAL_USER = "central";
	protected static final String DEFAULT_LOCAL_HOST = "localhost";
	protected static final String DEFAULT_LOCAL_PASSWORD = "local";
	protected static final String DEFAULT_CENTRAL_PASSWORD = "central";
	protected static final String DEFAULT_ALL = "*";
	protected static final String DEFAULT_CHAR_SET = "utf8";
	protected static final String DEFAULT_COLLATE = "utf8_general_ci";

	protected Connection connection;
	protected String generatedDatabaseName;

	protected String dbHost;
	protected String dbPort;
	protected String dbUsername;
	protected String dbPassword;
	protected String jdbcURL;

	@Resource
	protected Properties databaseProperties;

	@Autowired
	private MySQLUtil mysqlUtil;

	protected void createConnection() {
		if (this.connection == null) {

			this.dbHost = this.databaseProperties.getProperty(IBDBGenerator.DB_PROP_HOST);
			this.dbPort = this.databaseProperties.getProperty(IBDBGenerator.DB_PROP_PORT);
			this.dbUsername = this.databaseProperties.getProperty(IBDBGenerator.DB_PROP_USER);
			this.dbPassword = this.databaseProperties.getProperty(IBDBGenerator.DB_PROP_PASSWORD);
			this.jdbcURL = "jdbc:mysql://" + this.dbHost + ":" + this.dbPort;

			try {
				this.connection = DriverManager.getConnection(this.jdbcURL, this.dbUsername, this.dbPassword);
			} catch (SQLException e) {
				IBDBGenerator.handleDatabaseError(e);
			}
		}
	}

	protected void runScriptsInDirectory(String databaseName, File directory) throws SQLFileException {
		this.mysqlUtil.runScriptsInDirectory(databaseName, directory);
	}

	protected void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
				this.connection = null;
			} catch (SQLException e) {
				IBDBGenerator.handleDatabaseError(e);
			}
		}
	}

	protected static void handleDatabaseError(Exception e) {
		IBDBGenerator.LOG.error(e.toString(), e);
		throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
	}

	protected static void handleConfigurationError(Exception e) {
		IBDBGenerator.LOG.error(e.toString(), e);
		throw new InternationalizableException(e, Message.CONFIG_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
	}

	protected boolean isLanInstallerMode(Properties properties) {
		String installerMode = properties.getProperty(IBDBGenerator.WORKBENCH_PROP_INSTALLER_MODE, IBDBGenerator.INSTALLER_MODE_LOCAL);
		return IBDBGenerator.INSTALLER_MODE_LAN.equals(installerMode);
	}

	public void setMySQLUtil(MySQLUtil sqlUtil) {
		this.mysqlUtil = sqlUtil;
	}
}

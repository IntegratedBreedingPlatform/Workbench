
package org.generationcp.ibpworkbench.service;

import java.io.File;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * GCP
 */

@Service
@Transactional
public class BackupIBDBService {

	private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBService.class);
	
	@Autowired
	private MySQLUtil mysqlUtil;
	
	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	//TODO Iryna
	public File backupIBDB(final String projectId, final String dbName) throws Exception {

		final ProjectBackup projectBackup = new ProjectBackup();
		projectBackup.setProjectId(Long.valueOf(projectId));
		projectBackup.setBackupTime(Util.getCurrentDate());
		final File backupFile = this.mysqlUtil.backupDatabase(dbName);
		projectBackup.setBackupPath(backupFile.getAbsolutePath());
		// save result to DB
		this.workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
		return backupFile;
	}
}

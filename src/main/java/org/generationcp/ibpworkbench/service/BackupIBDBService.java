
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
	private static final String BACKUP_DIR = "backup";
	
	@Autowired
	private MySQLUtil mysqlUtil;
	
	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	public File backupIBDB(String projectId, String dbName) throws Exception {
		BackupIBDBService.LOG.debug("onClick > do save backup");
		BackupIBDBService.LOG.debug("Current ProjectID: " + projectId);

		this.checkBackupDir();
		ProjectBackup projectBackup = new ProjectBackup();
		projectBackup.setProjectId(Long.valueOf(projectId));
		projectBackup.setBackupTime(Util.getCurrentDate());
		File backupFile = this.mysqlUtil.backupDatabase(dbName);
		projectBackup.setBackupPath(backupFile.getAbsolutePath());
		// save result to DB
		this.workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
		return backupFile;
	}

	public void checkBackupDir() {
		File saveDir = new File(BackupIBDBService.BACKUP_DIR);
		if (!saveDir.exists() || !saveDir.isDirectory()) {
			saveDir.mkdirs();
		}

		this.mysqlUtil.setBackupDir(BackupIBDBService.BACKUP_DIR);

		BackupIBDBService.LOG.debug("dumppath: " + new File(this.mysqlUtil.getMysqlDumpPath()).getAbsolutePath());
	}
}

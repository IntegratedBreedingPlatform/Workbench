package org.generationcp.ibpworkbench.service;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Calendar;

/**
 * GCP
 */

@Service
public class BackupIBDBService {
    private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBService.class);
    private static final String BACKUP_DIR = "backup";
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    @Autowired
    private MySQLUtil mysqlUtil;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    public File backupIBDB(String projectId, String localDbName) throws Exception {
        LOG.debug("onClick > do save backup");
        LOG.debug("Current ProjectID: " + projectId);

        checkBackupDir();
        ProjectBackup projectBackup = new ProjectBackup();
        projectBackup.setProjectId(Long.valueOf(projectId));
        projectBackup.setBackupTime(Calendar.getInstance().getTime());
        File backupFile = mysqlUtil.backupDatabase(localDbName);
        projectBackup.setBackupPath(backupFile.getAbsolutePath());
        // save result to DB
        workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
        return backupFile;
    }

    public void checkBackupDir() throws Exception {
        File saveDir = new File(BACKUP_DIR);
        if (!saveDir.exists() || !saveDir.isDirectory())
            saveDir.mkdirs();

        mysqlUtil.setBackupDir(BACKUP_DIR);

        LOG.debug("dumppath: " + new File(mysqlUtil.getMysqlDumpPath()).getAbsolutePath() );
    }
}

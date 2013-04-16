package org.generationcp.ibpworkbench.actions;

import java.io.File;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.common.ConfirmDialog;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener, InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);
	
	private Window sourceWindow;
	//private Select select;
	private Table table;
	
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
    @Autowired
    private MySQLUtil mysqlUtil;
	
    private Project project;
	private static final String BACKUP_DIR = "backup";
	
	public RestoreIBDBSaveAction(Project project, Table table,Window sourceWindow) {
		//this.select = select;
		this.table = table;
		this.sourceWindow = sourceWindow;
		this.project = project;
	}
		
	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			LOG.debug("onClick > do Restore IBDB");
			
			//sourceWindow = event.getButton().getWindow();

			try {
				ProjectBackup pb = ((BeanItem<ProjectBackup>) table.getItem(table.getValue())).getBean();
			
				mysqlUtil.restoreDatabase(project.getLocalDbName(),new File(pb.getBackupPath()));
				
				MessageNotifier.showMessage(sourceWindow.getParent(),messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE),"");
				
				sourceWindow.getParent().removeWindow(sourceWindow);
				
				LOG.debug("selected backup: " + pb.getProjectBackupId());
			
			} catch(Exception e) {
				sourceWindow.showNotification("No project backup is selected");
			}		
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mysqlUtil.setBackupDir(BACKUP_DIR);
	}

}

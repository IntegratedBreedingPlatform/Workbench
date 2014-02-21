package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener, Receiver, SucceededListener, InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);

	protected Window sourceWindow;
	//private Select select;
    private ProjectBackup pb;
	
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private MySQLUtil mysqlUtil;

    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SessionData sessionData;
	
    private Project project;

	private File file;
    private File destFile;
    private static final String BACKUP_DIR = "backup";


    public RestoreIBDBSaveAction(Project project, Table table,Window sourceWindow) {
        pb = ((BeanItem<ProjectBackup>) table.getItem(table.getValue())).getBean();

		this.sourceWindow = sourceWindow;
		this.project = project;
	}

    public void setProjectBackup(ProjectBackup pb) {
        this.pb = pb;
    }

    public RestoreIBDBSaveAction(Project project, ProjectBackup pb,Window sourceWindow) {
        //this.select = select;
        this.pb = pb;
        this.sourceWindow = sourceWindow;
        this.project = project;
    }

    public void setSourceWindow(Window sourceWindow) {
        this.sourceWindow = sourceWindow;
    }

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
            LOG.debug("onClick > do Restore IBDB");

			try {
                // attempt to close all native apps first
                toolUtil.closeAllNativeTools();

                mysqlUtil.restoreDatabase(project.getLocalDbName(),new File(pb.getBackupPath()));

				MessageNotifier.showMessage(sourceWindow,messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE),"");

				LOG.debug("selected backup: " + pb.getProjectBackupId());
			
			} catch(IOException ex) {
                MessageNotifier.showError(sourceWindow,messageSource.getMessage(Message.ERROR_UPLOAD), ex.getMessage());
            }

            catch(Exception e) {
                MessageNotifier.showError(sourceWindow, messageSource.getMessage(Message.ERROR_UPLOAD), "No project backup is selected");
			}		
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mysqlUtil.setBackupDir(BACKUP_DIR);
	}

    protected void doFileUploadAndValidate() throws IOException {
        FileResource fileResource = new FileResource(file,sourceWindow.getApplication());
        File tmpFile = fileResource.getSourceFile();

        // validate file

        if (!getExtension(tmpFile).toLowerCase().contains("sql")) {
            MessageNotifier.showError(sourceWindow, messageSource.getMessage(Message.ERROR_UPLOAD),messageSource.getMessage(Message.ERROR_INVALID_FILE));

            tmpFile.delete();

            return;
        }

        destFile = new File(BACKUP_DIR + File.separator + tmpFile.getName());
        // write to backup databases if not exists else update
        FileUtils.copyFile(tmpFile,destFile);

    }

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		try {
		     doFileUploadAndValidate();
             doSQLFileRestore();

            // notify user of success
            MessageNotifier.showMessage(sourceWindow,messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE),"");

        } catch (Exception e) {
			LOG.error(e.getMessage());
			MessageNotifier.showError(sourceWindow,messageSource.getMessage(Message.ERROR_UPLOAD),e.getMessage());
		}
    }

    protected void doSQLFileRestore() throws MiddlewareQueryException,IOException,SQLException {

        toolUtil.closeAllNativeTools();

        ProjectBackup pb = workbenchDataManager.saveOrUpdateProjectBackup(new ProjectBackup(null, project.getProjectId(),new Date(),destFile.getAbsolutePath()));

        mysqlUtil.restoreDatabase(project.getLocalDbName(),new File(pb.getBackupPath()));

        //TODO: internationalize this
        ProjectActivity projAct = new ProjectActivity(new Integer(project.getProjectId().intValue()), project, "Program Local Database Restore", "Restore performed on " + project.getProjectName(),sessionData.getUserData(), new Date());
        workbenchDataManager.addProjectActivity(projAct);

    }


    /**
	 * Will return null if error
	 */
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
	    
		return doRecieveUpload(filename,mimeType);
	}

    protected OutputStream doRecieveUpload(String filename,String mimeType) {
        FileOutputStream fos = null;

        try {

            new File("tmp/uploads").mkdirs();	// create the directories if not exist

            file = new File("tmp/uploads/" + filename);
            fos = new FileOutputStream(file);

        } catch (FileNotFoundException e) {
            LOG.error(e.getLocalizedMessage());

            return null;
        }

        return fos;
    }
	
	/**
	 * Get filename Extension
	 * @param f
	 * @return
	 */
	private static String getExtension(File f)
	{
	String ext = null;
	String s = f.getName();
	int i = s.lastIndexOf('.');

	if (i > 0 && i < s.length() - 1)
	ext = s.substring(i+1).toLowerCase();

	if(ext == null)
	return "";
	return ext;
	}

}

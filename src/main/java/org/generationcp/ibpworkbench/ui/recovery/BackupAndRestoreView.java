package org.generationcp.ibpworkbench.ui.recovery;

import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.BackupIBDBSaveAction;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.easyuploads.FileFactory;
import org.vaadin.easyuploads.UploadField;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 * Created by cyrus on 2/20/14.
 */
@Configurable
public class BackupAndRestoreView extends CustomComponent implements InitializingBean {
    private ListSelect restoreList;
    private Button backupBtn;
    private UploadField uploadFrm;
    private Button restoreBtn;

    @Autowired
    private SessionData sessionData;
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    @Autowired
    private ToolUtil toolUtil;

    private final Panel root = new Panel();

    public BackupAndRestoreView() {
        this.setCompositionRoot(root);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initializeComponents();
        this.initializeLayout();
    }

    @Override
    public void attach() {
        super.attach();
        this.initializeActions();
    }

    public void initializeComponents() {
        backupBtn = new Button(messageSource.getMessage("BACKUP_BMS_BUTTON"));
        uploadFrm = new UploadField() {

            @Override
            public void validate() throws Validator.InvalidValueException {
                if (this.getLastFileName() == null)
                    throw new Validator.InvalidValueException("NO_FILE");
                else if (!this.isValid())
                    throw new Validator.InvalidValueException("NOT_VALID");
            }

            @Override
            public boolean isValid() {
                return (this.getLastFileName() != null && getExtension(this.getLastFileName()).toLowerCase().contains("sql"));
            }

            private String getExtension(String f)
            {
                String ext = null;
                int i = f.lastIndexOf('.');

                if (i > 0 && i < f.length() - 1)
                    ext = f.substring(i+1).toLowerCase();

                if(ext == null)
                    return "";
                return ext;
            }
        };

        uploadFrm.setDeleteCaption(messageSource.getMessage("CLEAR"));
        uploadFrm.setFieldType(UploadField.FieldType.FILE);


        //uploadFrm.setButtonCaption("Click to Restore from SQL file");
        //uploadFrm.setImmediate(true);
        restoreBtn = new Button(messageSource.getMessage("RESTORE_BMS_BUTTON"));
        restoreList = new ListSelect();

        populateRestoreList();

        restoreList.setMultiSelect(false);
        restoreList.setRows(1);
        restoreList.setNullSelectionAllowed(true);


    }

    public void populateRestoreList() {
        restoreList.removeAllItems();

        try {
            for(ProjectBackup pb : workbenchDataManager.getProjectBackups(sessionData.getLastOpenedProject())) {
                restoreList.addItem(pb);

                String dateStr = (new SimpleDateFormat("MMMM dd, yyyy")).format(pb.getBackupTime());



                restoreList.setItemCaption(pb,dateStr + " - " + pb.getBackupPath());
                restoreList.setValue(pb);
            }
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }

        if (restoreList.getItemIds().isEmpty())
            restoreBtn.setEnabled(false);

    }

    /**
     * If possible, move to a controller class in the future
     */
    public void initializeActions() {
        final BackupIBDBSaveAction backupAction = new BackupIBDBSaveAction(sessionData.getLastOpenedProject(),this.getWindow()) {
            @Override
            public void doAction() {
                super.doAction();
                BackupAndRestoreView.this.populateRestoreList();
            }
        };

        backupBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                ConfirmDialog.show(clickEvent.getComponent().getWindow(),
                        messageSource.getMessage(Message.BACKUP_IBDB_WINDOW_CAPTION),
                        messageSource.getMessage(Message.BACKUP_IBDB_WINDOW_DESC),
                        messageSource.getMessage(Message.BACKUP_IBDB_LINK),
                        messageSource.getMessage(Message.CANCEL),
                        backupAction);
            }
        });

        final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(sessionData.getLastOpenedProject(),(ProjectBackup)null,this.getWindow()) {
            @Override
            public void onClose(ConfirmDialog dialog) {
                super.onClose(dialog);

                if (dialog.isConfirmed())
                    BackupAndRestoreView.this.populateRestoreList();
            }
        };

        restoreBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                // validate file upload

                try {

                    uploadFrm.validate();
                    restoreAction.setIsUpload(true);

                } catch (Validator.InvalidValueException e) {
                    if (!e.getMessage().equals("NO_FILE")) {
                        MessageNotifier.showError(clickEvent.getComponent().getWindow(), messageSource.getMessage(Message.ERROR_UPLOAD),messageSource.getMessage(Message.ERROR_INVALID_FILE));
                        return;
                    }

                    if (restoreList.getValue() == null) {
                        MessageNotifier.showError(clickEvent.getComponent().getWindow(), messageSource.getMessage(Message.ERROR_UPLOAD), "No project backup is selected");
                        return;
                    }

                    restoreAction.setProjectBackup((ProjectBackup) restoreList.getValue());
                    restoreAction.setIsUpload(false);
                }


                String restoreDescMessageFormat = "%s<br/><br/><b style='color:red'>%s</b>";

                ConfirmDialog dialog = ConfirmDialog.show(clickEvent.getComponent().getWindow(),
                        messageSource.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION),
                        String.format(restoreDescMessageFormat,
                                messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM),
                                messageSource.getMessage("RESTORE_BMS_WARN")),
                        messageSource.getMessage(Message.RESTORE),
                        messageSource.getMessage(Message.CANCEL),
                        restoreAction);
                dialog.setContentMode(ConfirmDialog.CONTENT_HTML);
            }
        });

        uploadFrm.setFileFactory(restoreAction);

    }

    public void initializeLayout() {
        backupBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        restoreBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        uploadFrm.getRootLayout().setStyleName("bms-upload-container");
        uploadFrm.getRootLayout().setWidth("100%");
        uploadFrm.setButtonCaption("Browse");


        restoreList.setWidth("400px");

        final Label pageTitle = new Label(messageSource.getMessage("BACKUP_RESTORE_TITLE"));
        pageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

        final Label backupTitle = new Label(messageSource.getMessage("BACKUP_BMS_TITLE"));
        backupTitle.setStyleName(Bootstrap.Typography.H2.styleName());

        final Label restoreTitle = new Label(messageSource.getMessage("RESTORE_BMS_TITLE"));
        restoreTitle.setStyleName(Bootstrap.Typography.H2.styleName());

        final Label restoreDropdownTitle = new Label(messageSource.getMessage("RESTORE_CHOOSE_BACKUP"));
        restoreDropdownTitle.setStyleName(Bootstrap.Typography.H6.styleName());

        final Label restoreUploadTitle = new Label(messageSource.getMessage("RESTORE_BMS_UPLOAD"));
        restoreUploadTitle.setStyleName(Bootstrap.Typography.H6.styleName());

        root.setSizeFull();
        root.setScrollable(true);
        root.setStyleName(Reindeer.PANEL_LIGHT);

        final VerticalLayout rootContent = new VerticalLayout();
        root.setContent(rootContent);

        rootContent.setSizeUndefined();
        rootContent.setWidth("100%");
        rootContent.setMargin(true);
        rootContent.setSpacing(true);

        rootContent.addComponent(pageTitle);
        rootContent.addComponent(backupTitle);
        rootContent.addComponent(new Label(messageSource.getMessage("BACKUP_BMS_DESCRIPTION",sessionData.getLastOpenedProject().getProjectName()),Label.CONTENT_XHTML));
        rootContent.addComponent(backupBtn);
        rootContent.addComponent(new Label("<div style='height: 40px'></div>",Label.CONTENT_XHTML));
        rootContent.addComponent(restoreTitle);
        rootContent.addComponent(new Label(messageSource.getMessage("RESTORE_BMS_DESCRIPTION")));
        rootContent.addComponent(restoreDropdownTitle);
        rootContent.addComponent(restoreList);
        rootContent.addComponent(new Label("",Label.CONTENT_XHTML));
        rootContent.addComponent(new Label("<div style='margin: 5px 0;'>Or</div>",Label.CONTENT_XHTML));
        rootContent.addComponent(restoreUploadTitle);
        rootContent.addComponent(uploadFrm);
        rootContent.addComponent(restoreBtn);

    }
}

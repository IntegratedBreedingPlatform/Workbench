package org.generationcp.ibpworkbench.ui.recovery;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.BackupIBDBSaveAction;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by cyrus on 2/20/14.
 */
@Configurable
public class BackupAndRestoreView extends CustomComponent implements InitializingBean {
    private ListSelect restoreList;
    private Button backupBtn;
    private Upload uploadFrm;
    private Button restoreBtn;

    @Autowired
    private SessionData sessionData;
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    private final Panel root = new Panel();

    public BackupAndRestoreView() {
        this.setCompositionRoot(root);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initializeComponents();
        this.initializeActions();
        this.initializeLayout();
    }

    public void initializeComponents() {
        backupBtn = new Button(messageSource.getMessage("BACKUP_BMS_BUTTON"));
        uploadFrm = new Upload(messageSource.getMessage("RESTORE_BMS_UPLOAD"),null);
        uploadFrm.setButtonCaption("Click to Restore from SQL file");
        restoreBtn = new Button(messageSource.getMessage("RESTORE_BMS_BUTTON"));
        restoreList = new ListSelect();

        populateRestoreList();

        restoreList.setMultiSelect(false);
        restoreList.setRows(1);
        restoreList.setNullSelectionAllowed(false);


    }

    public void populateRestoreList() {
        restoreList.removeAllItems();

        try {
            for(ProjectBackup pb : workbenchDataManager.getProjectBackups(sessionData.getLastOpenedProject())) {
                restoreList.addItem(pb);

                String dateStr = (new SimpleDateFormat("MMMM DD yyyy")).format(pb.getBackupTime());

                restoreList.setItemCaption(pb,dateStr + " - " + pb.getBackupPath());
            }
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }

        if (restoreList.getItemIds().isEmpty())
            restoreBtn.setEnabled(false);
    }


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

        final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(sessionData.getLastOpenedProject(),(ProjectBackup)restoreList.getValue(),this.getWindow()) {
            @Override
            public void onClose(ConfirmDialog dialog) {
                super.onClose(dialog);
                BackupAndRestoreView.this.populateRestoreList();
            }
        };

        restoreBtn.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                ConfirmDialog.show(clickEvent.getComponent().getWindow(),
                        messageSource.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION),
                        messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM),
                        messageSource.getMessage(Message.RESTORE),
                        messageSource.getMessage(Message.CANCEL),
                        restoreAction);
            }
        });

        uploadFrm.setReceiver(restoreAction);
        uploadFrm.addListener(restoreAction);
    }

    public void initializeLayout() {
        backupBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        restoreBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        restoreList.setWidth("350px");

        final Label backupTitle = new Label(messageSource.getMessage("BACKUP_BMS_TITLE"));
        backupTitle.setStyleName(Bootstrap.Typography.H1.styleName());

        final Label restoreTitle = new Label(messageSource.getMessage("RESTORE_BMS_TITLE"));
        restoreTitle.setStyleName(Bootstrap.Typography.H1.styleName());

        final HorizontalLayout restoreForm = new HorizontalLayout();
        restoreForm.setSizeUndefined();
        restoreForm.setSpacing(true);
        restoreForm.addComponent(restoreList);
        restoreForm.addComponent(restoreBtn);

        root.setSizeFull();
        root.setScrollable(true);
        root.setStyleName(Reindeer.PANEL_LIGHT);

        final VerticalLayout rootContent = new VerticalLayout();
        root.setContent(rootContent);

        rootContent.setSizeUndefined();
        rootContent.setWidth("100%");
        rootContent.setMargin(true);
        rootContent.setSpacing(true);
        rootContent.addComponent(backupTitle);
        rootContent.addComponent(new Label(messageSource.getMessage("BACKUP_BMS_DESCRIPTION",sessionData.getLastOpenedProject().getProjectName())));
        rootContent.addComponent(backupBtn);
        rootContent.addComponent(new Label("<hr/>",Label.CONTENT_XHTML));
        rootContent.addComponent(restoreTitle);
        rootContent.addComponent(new Label(messageSource.getMessage("RESTORE_BMS_DESCRIPTION",sessionData.getLastOpenedProject().getProjectName())));
        rootContent.addComponent(restoreForm);
        rootContent.addComponent(uploadFrm);

    }
}

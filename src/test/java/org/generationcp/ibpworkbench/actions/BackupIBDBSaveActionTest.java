package org.generationcp.ibpworkbench.actions;

import com.vaadin.Application;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BackupIBDBSaveActionTest {

	public static final String SUCCESS = "success";
	public static final String BACKUP_COMPLETE = "Backup Complete";
	public static final String MAIZE = "maize";
	public static final String CROP_DATABASE_BACKUP = "crop database backup";
	public static final String BACKUP_PERFORMED_ON = "backup performed on";
	public static final String CANNOT_PERFORM_OPERATION = "Cannot perform operation";
	private BackupIBDBSaveAction backupIBDBSaveAction;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private MySQLUtil mysqlUtil;

	@Mock
	private Window window;

	@Mock
	private Window mainWindow;

	@Mock
	private File backupFile;

	@Mock
	private Application application;

	private Project project;

	@Before
	public void init() throws IOException, InterruptedException {

		project = new Project();
		project.setCropType(new CropType());
		project.getCropType().setCropName(MAIZE);

		this.backupIBDBSaveAction = new BackupIBDBSaveAction(project, window);
		this.backupIBDBSaveAction.setMessageSource(messageSource);
		this.backupIBDBSaveAction.setContextUtil(contextUtil);
		this.backupIBDBSaveAction.setMysqlUtil(mysqlUtil);

		final String backupFileName = "backupFileName.sql";
		final String dbName = project.getDatabaseName();
		when(mysqlUtil.getBackupFilename(dbName, ".sql", "temp")).thenReturn(backupFileName);
		when(mysqlUtil.backupDatabase(dbName, backupFileName, true)).thenReturn(this.backupFile);
		when(messageSource.getMessage(Message.SUCCESS)).thenReturn(SUCCESS);
		when(messageSource.getMessage(Message.BACKUP_IBDB_COMPLETE)).thenReturn(BACKUP_COMPLETE);
		when(messageSource.getMessage(Message.CROP_DATABASE_BACKUP)).thenReturn(CROP_DATABASE_BACKUP);
		when(messageSource.getMessage(Message.BACKUP_PERFORMED_ON)).thenReturn(BACKUP_PERFORMED_ON);
		when(messageSource.getMessage(Message.BACKUP_IBDB_CANNOT_PERFORM_OPERATION)).thenReturn(CANNOT_PERFORM_OPERATION);
		when(window.getApplication()).thenReturn(application);
		when(application.getMainWindow()).thenReturn(mainWindow);

	}

	@Test
	public void testDoActionSuccess() {

		this.backupIBDBSaveAction.doAction();

		verify(mainWindow).open(any(FileResource.class));

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);
		verify(window).showNotification(captor.capture());
		verify(contextUtil).logProgramActivity(CROP_DATABASE_BACKUP, BACKUP_PERFORMED_ON + " " + project.getDatabaseName());

		final Window.Notification notification = captor.getValue();
		assertEquals(SUCCESS, notification.getCaption());
		assertEquals("</br>" + BACKUP_COMPLETE, notification.getDescription());

	}

	@Test
	public void testDoActionFail() throws IOException, InterruptedException {

		final String errorMessage = "error message";
		final InterruptedException exception = new InterruptedException(errorMessage);

		when(mysqlUtil.backupDatabase(any(String.class), any(String.class), Mockito.anyBoolean())).thenThrow(exception);

		this.backupIBDBSaveAction.doAction();

		verify(contextUtil, Mockito.never())
				.logProgramActivity(CROP_DATABASE_BACKUP, BACKUP_PERFORMED_ON + " " + project.getDatabaseName());

		final ArgumentCaptor<Window.Notification> captor = ArgumentCaptor.forClass(Window.Notification.class);
		verify(window).showNotification(captor.capture());

		final Window.Notification notification = captor.getValue();
		assertEquals(CANNOT_PERFORM_OPERATION, notification.getCaption());
		assertEquals("</br>" + errorMessage, notification.getDescription());
	}
}

package org.generationcp.ibpworkbench.ui.recovery;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.ui.common.UploadField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

public class RestoreButtonClickListenerTest {
	
	private static final String DATABASE_NAME = "ibdbv2_maize_merged";
	private static final String RESTORE_CONFIRM =
			"Restoring will overwrite entire content of the current crop database: {0} with the data in the selected file. Are you sure you want to proceed?";
	private static final String RESTORE_WARN = "Note: all external applications launched by BMS will be closed in this process.";
	
	@Mock
	private Project project;
	
	@Mock
	private RestoreIBDBSaveAction restoreAction;
	
	@Mock
	private UploadField uploadField;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@Mock
	private Component component;
	
	@Mock
	private Window window;
	
	@Mock
	private ClickEvent clickEvent;
	
	@InjectMocks
	private RestoreButtonClickListener restoreButtonListener;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.restoreButtonListener.setMessageSource(this.messageSource);
		
		Mockito.doReturn(this.component).when(this.clickEvent).getComponent();
		Mockito.doReturn(this.window).when(this.component).getWindow();
		Mockito.doReturn(DATABASE_NAME).when(this.project).getDatabaseName();
		Mockito.doReturn(RESTORE_CONFIRM).when(this.messageSource).getMessage(Message.RESTORE_IBDB_CONFIRM);
		Mockito.doReturn(RESTORE_WARN).when(this.messageSource).getMessage(Message.RESTORE_BMS_WARN);
	}
	
	@Test
	public void testButtonClick() {
		this.restoreButtonListener.buttonClick(this.clickEvent);
		
		Mockito.verify(this.uploadField).validate();
		verifyMockInteractionsOnRestore(1);
	}
	
	@Test
	public void testButtonClickWhenNoFileExceptionOccurs() {
		Mockito.doThrow(new Validator.InvalidValueException(BackupAndRestoreView.NO_FILE)).when(this.uploadField).validate();
		this.restoreButtonListener.buttonClick(this.clickEvent);
		
		Mockito.verify(this.uploadField).validate();
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(Message.ERROR_UPLOAD);
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(BackupAndRestoreView.NO_FILE_SELECTED);
		this.verifyMockInteractionsOnRestore(0);
	}
	
	@Test
	public void testButtonClickWhenOtherExceptionOccurs() {
		Mockito.doThrow(new Validator.InvalidValueException("DB_ERROR")).when(this.uploadField).validate();
		this.restoreButtonListener.buttonClick(this.clickEvent);
		
		Mockito.verify(this.uploadField).validate();
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(Message.ERROR_UPLOAD);
		Mockito.verify(this.messageSource, Mockito.times(1)).getMessage(Message.ERROR_INVALID_FILE);
		this.verifyMockInteractionsOnRestore(0);
	}

	private void verifyMockInteractionsOnRestore(final int numOfInvocations) {
		Mockito.verify(this.messageSource, Mockito.times(numOfInvocations)).getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION);
		Mockito.verify(this.messageSource, Mockito.times(numOfInvocations)).getMessage(Message.RESTORE_IBDB_CONFIRM, DATABASE_NAME);
		Mockito.verify(this.messageSource, Mockito.times(numOfInvocations)).getMessage(Message.RESTORE_BMS_WARN);
		Mockito.verify(this.messageSource, Mockito.times(numOfInvocations)).getMessage(Message.RESTORE);
		Mockito.verify(this.messageSource, Mockito.times(numOfInvocations)).getMessage(Message.CANCEL);
		Mockito.verify(this.component).getWindow();
	}
	
	

}

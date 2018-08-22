package org.generationcp.ibpworkbench.ui.recovery;

import java.util.Collection;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.actions.BackupIBDBSaveAction;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.ui.common.UploadField;
import org.generationcp.ibpworkbench.ui.recovery.BackupAndRestoreView;
import org.generationcp.ibpworkbench.ui.recovery.RestoreButtonClickListener;
import org.generationcp.middleware.data.initializer.ProjectTestDataInitializer;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import junit.framework.Assert;

public class BackupAndRestoreViewTest {
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private UploadField uploadField;
	
	@InjectMocks
	private BackupAndRestoreView backupAndRestoreView;
	
	private Project project;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.project = ProjectTestDataInitializer.createProject();
		Mockito.doReturn(this.project).when(this.contextUtil).getProjectInContext();
		
		this.backupAndRestoreView.initializeComponents();
	}
	
	@Test
	public void testInitializeActions() {
		this.backupAndRestoreView.setUploadField(this.uploadField);
		this.backupAndRestoreView.initializeActions();
		
		final Button backupButton = this.backupAndRestoreView.getBackupButton();
		final Collection<?> backupButtonListeners = backupButton.getListeners(ClickEvent.class);
		Assert.assertNotNull(backupButtonListeners);
		Assert.assertEquals(1, backupButtonListeners.size());
		final BackupIBDBSaveAction backupAction = (BackupIBDBSaveAction) backupButtonListeners.iterator().next();
		Assert.assertEquals(this.project, backupAction.getSelectedProject());
		
		final Button restoreButton = this.backupAndRestoreView.getRestoreButton();
		final Collection<?> restoreButtonListeners = restoreButton.getListeners(ClickEvent.class);
		Assert.assertNotNull(restoreButtonListeners);
		Assert.assertEquals(1, restoreButtonListeners.size());
		final RestoreButtonClickListener restoreListener = (RestoreButtonClickListener) restoreButtonListeners.iterator().next();
		Assert.assertEquals(this.project, restoreListener.getProject());
		Assert.assertEquals(this.project, restoreListener.getProject());
		
		final ArgumentCaptor<RestoreIBDBSaveAction> restoreAction =  ArgumentCaptor.forClass(RestoreIBDBSaveAction.class);
		Mockito.verify(this.uploadField).setFileFactory(restoreAction.capture());
		Assert.assertEquals(this.project, restoreAction.getValue().getProject());
	}
	
}

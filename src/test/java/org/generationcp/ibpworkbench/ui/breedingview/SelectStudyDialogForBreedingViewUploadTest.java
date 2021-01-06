package org.generationcp.ibpworkbench.ui.breedingview;

import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.window.FileUploadBreedingViewOutputWindow;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Window;

import org.junit.Assert;

public class SelectStudyDialogForBreedingViewUploadTest {
	
	@Mock
	private StudyPermissionValidator studyPermissionValidator;
	
	@Mock
	private SingleSiteAnalysisPanel component;
	
	@Mock
	private Window window;
	
	
	@Mock
	private Project project;
	
	@Mock
	private StudyDataManager studyDataManager;
	
	@Mock
	private Window parentWindow;
	
	@Mock
	private SimpleResourceBundleMessageSource messageSource;
	
	@InjectMocks
	private SelectStudyDialogForBreedingViewUpload selectStudyForBVDialog;
	
	private StudyReference study;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.selectStudyForBVDialog = new SelectStudyDialogForBreedingViewUpload(this.window, this.component, this.project);
		this.selectStudyForBVDialog.setStudyDataManager(this.studyDataManager);
		this.selectStudyForBVDialog.setStudyPermissionValidator(this.studyPermissionValidator);
		this.selectStudyForBVDialog.setMessageSource(this.messageSource);
		
		this.study = new StudyReference(1, "Trial 1");
		Mockito.doReturn(study).when(this.studyDataManager).getStudyReference(Matchers.anyInt());
		Mockito.doReturn(this.parentWindow).when(this.window).getWindow();
	}
	
	@Test
	public void testOpenStudy() {
		Mockito.doReturn(false).when(this.studyPermissionValidator).userLacksPermissionForStudy(Matchers.any(StudyReference.class));

		this.selectStudyForBVDialog.openStudy(this.study);
		final ArgumentCaptor<Window> windowCaptor = ArgumentCaptor.forClass(Window.class);
		Mockito.verify(this.parentWindow).addWindow(windowCaptor.capture());
		Assert.assertTrue(windowCaptor.getValue() instanceof FileUploadBreedingViewOutputWindow);
		Mockito.verify(this.parentWindow).removeWindow(this.selectStudyForBVDialog);
	}
	
	@Test
	public void testOpenStudyWhenUserLacksPermission() {
		Mockito.doReturn(true).when(this.studyPermissionValidator).userLacksPermissionForStudy(Matchers.any(StudyReference.class));

		this.selectStudyForBVDialog.openStudy(this.study);
		Mockito.verify(this.parentWindow, Mockito.never()).addWindow(Matchers.any(Window.class));
		Mockito.verify(this.parentWindow, Mockito.never()).removeWindow(this.selectStudyForBVDialog);
	}
	
	

}

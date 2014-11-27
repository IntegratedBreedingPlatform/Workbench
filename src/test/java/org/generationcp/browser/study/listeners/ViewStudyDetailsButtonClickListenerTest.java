package org.generationcp.browser.study.listeners;

import static org.generationcp.browser.study.listeners.ViewStudyDetailsButtonClickListener.STUDY_BROWSER_LINK;
import static org.mockito.Mockito.doReturn;

import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

public class ViewStudyDetailsButtonClickListenerTest {
	
	private static final String STUDY_NAME = "Dummy Study";

	private static final int STUDY_ID = 1;

	private WorkbenchDataManager workbenchManager;
	
	private ViewStudyDetailsButtonClickListener viewStudyListener;
	
	@Before
	public void setUp(){
		workbenchManager = Mockito.mock(WorkbenchDataManager.class);
		viewStudyListener = new ViewStudyDetailsButtonClickListener(STUDY_ID, STUDY_NAME);
		viewStudyListener.setWorkbenchDataManager(workbenchManager);
	}
	
	@Test
	public void testLaunchStudyDetailsWindow() throws MiddlewareQueryException{
		ViewStudyDetailsButtonClickListener moleListener = Mockito.spy(viewStudyListener);
		
		doReturn(null).when(workbenchManager).getToolWithName(Matchers.anyString());
		doReturn("").when(moleListener).getAdditionalParams();
		
		ClickEvent event = Mockito.mock(ClickEvent.class);
		Button mockButton = Mockito.mock(Button.class);
		Window parentWindow = new Window();
		doReturn(parentWindow).when(mockButton).getWindow();
		doReturn(mockButton).when(event).getComponent();
		
		moleListener.buttonClick(event);

		// Assert state
		Set<Window> childWindows = parentWindow.getChildWindows();
		Assert.assertNotNull(childWindows);
		Assert.assertTrue("Should have one sub-window", !childWindows.isEmpty());
		
		Window studyWindow = childWindows.iterator().next();
		Assert.assertNotNull(studyWindow.getContent());
		Assert.assertEquals("Study Details window caption should be", "Study Information: " + STUDY_NAME, studyWindow.getCaption());
		
		
		Embedded embeddedResource = (Embedded) 
				studyWindow.getContent().getComponentIterator().next();
		ExternalResource externalResource = (ExternalResource) embeddedResource.getSource();
		String expectedStudyURL = STUDY_BROWSER_LINK + STUDY_ID + "?restartApplication";
		Assert.assertEquals("URL to StudyBrowser resource should be", expectedStudyURL, externalResource.getURL());
		
		
	}

}

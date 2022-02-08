
package org.generationcp.browser.study.containers;

import java.util.Collection;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.commons.context.ContextInfo;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.StudyPermissionValidator;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ui.common.LinkButton;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import org.junit.Assert;

public class StudyButtonRendererTest {

	private static final String STUDY_NAME = "TEST STUDY";
	private static final Integer USER_ID = 2;
	private static final Long PROJECT_ID = Long.valueOf("15");
	private static final Integer STUDY_ID = 123;

	@Mock
	private StudyPermissionValidator studyPermissionValidator;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ContextInfo contextInfo;

	@Mock
	private StudyReference study;

	@InjectMocks
	private StudyButtonRenderer buttonRenderer;

	private static final String PARAMS =
			"?restartApplication&loggedInUserId=" + USER_ID + "&selectedProjectId=" + PROJECT_ID;

	private static final String URL = StudyButtonRenderer.URL_STUDY_TRIAL[0] + STUDY_ID + PARAMS + StudyButtonRenderer.URL_STUDY_TRIAL[1];

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.buttonRenderer.setContextUtil(this.contextUtil);
		this.buttonRenderer.setStudyPermissionValidator(this.studyPermissionValidator);
		
		Mockito.doReturn(this.contextInfo).when(this.contextUtil).getContextInfoFromSession();
		Mockito.doReturn(PROJECT_ID).when(this.contextInfo).getSelectedProjectId();
		Mockito.doReturn(USER_ID).when(this.contextInfo).getLoggedInUserId();
		Mockito.doReturn(false).when(this.studyPermissionValidator).userLacksPermissionForStudy(this.study);
		Mockito.doReturn(STUDY_NAME).when(this.study).getName();
		Mockito.doReturn(STUDY_ID).when(this.study).getId();
		Mockito.doReturn(RandomStringUtils.randomAlphabetic(20)).when(this.contextUtil).getCurrentProgramUUID();
		Mockito.doReturn(RandomStringUtils.randomAlphabetic(20)).when(this.study).getProgramUUID();
	}

	@Test
	public void testRenderButtonForStudyTemplate() {
		Mockito.doReturn(null).when(this.study).getProgramUUID();
		final Button button = this.buttonRenderer.renderStudyButton();
		Assert.assertFalse(button.isEnabled());
		Assert.assertEquals(STUDY_NAME, button.getCaption());
		final LinkButton linkButton = (LinkButton) button;
		Assert.assertEquals(URL, linkButton.getResource().getURL());
	}
	
	@Test
	public void testRenderButtonForCropStudy() {
		final Button button = this.buttonRenderer.renderStudyButton();
		Assert.assertEquals(STUDY_NAME, button.getCaption());
		final LinkButton linkButton = (LinkButton) button;
		Assert.assertEquals(URL, linkButton.getResource().getURL());
	}
	
	@Test
	public void testRenderButtonForRestrictedStudy() {
		Mockito.doReturn(true).when(this.studyPermissionValidator).userLacksPermissionForStudy(this.study);
		final Button button = this.buttonRenderer.renderStudyButton();
		Assert.assertEquals(STUDY_NAME, button.getCaption());
		final Collection<?> listeners = button.getListeners(ClickEvent.class);
		Assert.assertNotNull(listeners);
		Assert.assertTrue(listeners.iterator().next() instanceof StudyButtonRenderer.LockedStudyButtonClickListener);
	}
		

}

package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ContentWindow;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenProgramLocationsActionTest {

	public static final String PROJECT_LOCATIONS_LINK = "Project Locations Link";
	public static final String LAUNCHED_APP = "Launched App";

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private OpenProgramLocationsAction openProgramLocationsAction;

	@Before
	public void init() {

		when(this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK)).thenReturn(PROJECT_LOCATIONS_LINK);
		when(this.messageSource.getMessage(Message.LAUNCHED_APP, PROJECT_LOCATIONS_LINK)).thenReturn(LAUNCHED_APP);

	}

	@Test
	public void testDoAction() {

		final ContentWindow window = mock(ContentWindow.class);

		openProgramLocationsAction.doAction(window, null, false);

		verify(contextUtil).logProgramActivity(PROJECT_LOCATIONS_LINK, LAUNCHED_APP);
		window.showContent(any(ProgramLocationsView.class));

	}

}

package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.ContentWindow;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OpenProgramMethodsActionTest {

	public static final String PROJECT_METHODS_LINK = "Project Methods Link";
	public static final String LAUNCHED_APP = "Launched App";

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private OpenProgramMethodsAction openProgramMethodsAction;

	@Before
	public void init() {

		when(this.messageSource.getMessage(Message.PROJECT_METHODS_LINK)).thenReturn(PROJECT_METHODS_LINK);
		when(this.messageSource.getMessage(Message.LAUNCHED_APP, PROJECT_METHODS_LINK)).thenReturn(LAUNCHED_APP);

	}

	@Test
	public void testDoAction() {

		final ContentWindow window = mock(ContentWindow.class);

		openProgramMethodsAction.doAction(window, null, false);

		verify(contextUtil).logProgramActivity(PROJECT_METHODS_LINK, LAUNCHED_APP);
		window.showContent(any(ProgramMethodsView.class));

	}

}

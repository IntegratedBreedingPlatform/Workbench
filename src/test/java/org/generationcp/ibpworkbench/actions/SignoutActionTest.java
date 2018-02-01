package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignoutActionTest {

	@Mock
	private Button button;

	@Mock
	private WorkbenchMainView workbenchMainView;

	private MockHttpServletRequest request;

	private final SignoutAction signoutAction = new SignoutAction();

	@Before
	public void setup() {
		request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

	}

	@Test
	public void testButtonClick() {

		final Button.ClickEvent clickEvent = mock(Button.ClickEvent.class);
		when(clickEvent.getButton()).thenReturn(button);
		when(button.getWindow()).thenReturn(workbenchMainView);

		signoutAction.buttonClick(clickEvent);

		verify(workbenchMainView).setUriFragment("", true);

		final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(workbenchMainView).showContent(captor.capture());

		assertEquals("http://localhost:80/ibpworkbench/controller/logout", captor.getValue());

	}

}

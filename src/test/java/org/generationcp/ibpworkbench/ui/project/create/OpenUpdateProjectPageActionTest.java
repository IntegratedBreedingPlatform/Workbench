package org.generationcp.ibpworkbench.ui.project.create;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.ContentWindow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;

public class OpenUpdateProjectPageActionTest {
	
	@Mock
	private ContextUtil contextUtil;
	
	@Mock
	private ContentWindow window;
	
	@InjectMocks
	private OpenUpdateProjectPageAction action;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testDoAction() {
		this.action.doAction(this.window, "", true);
		this.verifyUpdateProjectMockInteractions();
	}
	
	@Test
	public void testButtonClick() {
		final ClickEvent event = Mockito.mock(ClickEvent.class);
		final Component layout = Mockito.mock(Component.class);
		Mockito.when(event.getComponent()).thenReturn(layout);
		Mockito.when(layout.getWindow()).thenReturn(this.window);
		this.action.buttonClick(event);
		
		this.verifyUpdateProjectMockInteractions();
	}
	
	@Test
	public void testDoActionForComponentEvent() {
		this.action.doAction(Mockito.mock(Event.class));
		Mockito.verifyZeroInteractions(this.window);
		Mockito.verifyZeroInteractions(this.contextUtil);
	}

	private void verifyUpdateProjectMockInteractions() {
		final ArgumentCaptor<Component> contentCaptor = ArgumentCaptor.forClass(Component.class);
		Mockito.verify(this.window).showContent(contentCaptor.capture());
		Assert.assertTrue(contentCaptor.getValue() instanceof UpdateProjectPanel);
		Mockito.verify(this.contextUtil).logProgramActivity("Update Program", "Launched Update Program");
	}

}

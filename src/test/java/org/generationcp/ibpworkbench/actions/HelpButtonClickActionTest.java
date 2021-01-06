package org.generationcp.ibpworkbench.actions;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import org.junit.Assert;

public class HelpButtonClickActionTest {
	
	@Mock
	private Window window;
	
	@Mock
	private ClickEvent event;
	
	private String url = RandomStringUtils.random(20);
	
	private HelpButtonClickAction action;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.action = new HelpButtonClickAction(window, url);
	}
	
	@Test
	public void testButtonClick() {
		this.action.buttonClick(event);
		final ArgumentCaptor<ExternalResource> captor = ArgumentCaptor.forClass(ExternalResource.class);
		Mockito.verify(this.window).open(captor.capture(), Matchers.eq("_blank")) ;
		Assert.assertEquals(this.url, captor.getValue().getURL());
	}

}

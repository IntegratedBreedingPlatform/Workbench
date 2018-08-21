package org.generationcp.ibpworkbench.actions;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;


public class AskForSupportAction implements ClickListener {
	
	private static final long serialVersionUID = 1L;
	private Window sourceWindow;
	private String url;

	public AskForSupportAction(Window sourceWindow, String url) {
		super();
		this.sourceWindow = sourceWindow;
		this.url = url;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		this.sourceWindow.open(new ExternalResource(url), "_blank");
	}
	
	public Window getSourceWindow() {
		return sourceWindow;
	}
	
	public String getUrl() {
		return url;
	}

}

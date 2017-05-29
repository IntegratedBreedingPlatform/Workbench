package org.generationcp.ibpworkbench.ui.common;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;

public class LinkButton extends Button {

	private final ExternalResource url;

	public LinkButton(final ExternalResource url, final String caption) {
		super(caption);
		this.url = url;

		setImmediate(true);
		setListener(url, "_self");
	}

	public LinkButton(final ExternalResource url, final String caption, final String windowName) {
		super(caption);
		this.url = url;

		setImmediate(true);
		setListener(url, windowName);

	}

	private void setListener(final ExternalResource url, final String windowName) {
		addListener(new Button.ClickListener() {

			public void buttonClick(final ClickEvent event) {
				if (LinkButton.this.url != null) {
					LinkButton.this.getWindow().open(new ExternalResource(LinkButton.this.url.getURL()), windowName);
				}
			}
		});
	}

	public ExternalResource getResource() {
		return this.url;
	}
}

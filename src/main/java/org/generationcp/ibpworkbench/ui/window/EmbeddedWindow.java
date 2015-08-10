package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created by cyrus on 7/23/15.
 */
@Configurable
public class EmbeddedWindow extends BaseSubWindow {
	private VerticalLayout mainContent;

	public void showContent(String toolUrl) {
		if (!toolUrl.isEmpty()) {
			Embedded browser = new Embedded(null, new ExternalResource(toolUrl));

			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();

			this.mainContent.removeAllComponents();
			this.mainContent.addComponent(browser);
			this.mainContent.setExpandRatio(browser, 1.0F);

		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.mainContent = new VerticalLayout();
		this.mainContent.setSizeFull();
		this.mainContent.setMargin(false);
		this.mainContent.setSpacing(false);

		this.setContent(mainContent);
	}

}

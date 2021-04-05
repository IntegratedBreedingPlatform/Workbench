package org.generationcp.ibpworkbench;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class WorkbenchContentAppWindow extends Window implements IContentWindow, InitializingBean {

	private VerticalLayout mainContent;

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchContentAppWindow.class);

	public WorkbenchContentAppWindow() {
		super("Breeding Management System | Workbench");
	}

	@Override
	public void showContent(final Component content) {

		this.mainContent.removeAllComponents();

		if (content instanceof Embedded) {
			this.mainContent.addComponent(content);
			this.mainContent.setExpandRatio(content, 1.0F);

		} else {

			if (content instanceof Panel) {
				content.setStyleName(Reindeer.PANEL_LIGHT);
				this.mainContent.addComponent(content);
				this.mainContent.setExpandRatio(content, 1.0F);
			} else {
				final Panel wrap = new Panel();
				wrap.setDebugId("wrap");
				wrap.setStyleName(Reindeer.PANEL_LIGHT);
				wrap.setSizeFull();
				wrap.setScrollable(true);

				if (content instanceof ComponentContainer) {
					wrap.setContent((ComponentContainer) content);
				} else {
					final VerticalLayout vl = new VerticalLayout();
					vl.setDebugId("vl");
					vl.addComponent(content);
					vl.setSizeUndefined();
					wrap.setContent(vl);
				}

				this.mainContent.addComponent(wrap);
				this.mainContent.setExpandRatio(wrap, 1.0F);
			}
		}

	}

	@Override
	public void showContent(final String toolUrl) {
		if (!toolUrl.isEmpty()) {
			final Embedded browser = new Embedded(null, new ExternalResource(toolUrl));
			browser.setDebugId("browser");

			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();

			this.showContent(browser);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void assemble() {
		this.initializeLayout();
	}

	protected void initializeLayout() {
		this.mainContent = new VerticalLayout();
		this.mainContent.setDebugId("mainContent");
		this.mainContent.setStyleName("gcp-maincontentarea");
		this.mainContent.setSizeFull();
		this.mainContent.setMargin(false);
		this.mainContent.setSpacing(false);
		this.setContent(this.mainContent);
	}
}

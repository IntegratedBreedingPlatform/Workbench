package org.generationcp.ibpworkbench;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;
import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.generationcp.ibpworkbench.ui.common.IContainerFittable;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import java.net.URL;
import java.util.Map;

/**
 * Created by cyrus on 1/24/14.
 */
@Configurable
public class ContentWindow extends Window implements IContentWindow, InitializingBean, URIHandler, ParameterHandler {

	/**
	 *
	 */
	private static final long serialVersionUID = -4166591931885992086L;
	private static final Logger LOG = LoggerFactory.getLogger(ContentWindow.class);
	public static final String PROGRAM_LOCATIONS = "ProgramLocations";
	public static final String PROGRAM_METHODS = "ProgramMethods";
	private String path;

	private OpenProgramLocationsAction openProgramLocationsAction = new OpenProgramLocationsAction();
	private OpenProgramMethodsAction openProgramMethodsAction = new OpenProgramMethodsAction();

	public ContentWindow() {
		super("Breeding Management System | Workbench");
	}

	@Override
	public void showContent(final Component content) {

		try {
			this.removeAllComponents();
		} catch (final Exception e) {
			// swallow the exception
			LOG.warn(e.getMessage(), e);
		}

		if (content instanceof ComponentContainer) {

			if (content instanceof IContainerFittable) {
				((IContainerFittable) content).fitToContainer(this);
			}

			this.setContent((ComponentContainer) content);
		} else {
			this.addComponent(content);
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
	public void handleParameters(Map<String, String[]> stringMap) {
		// does nothing
	}

	@Override
	public DownloadStream handleURI(final URL url, final String s) {
		this.path = s;

		ContentWindow.LOG.debug("path: " + this.path);

		if (this.path != null) {
			if (PROGRAM_LOCATIONS.equals(this.path)) {

				this.openProgramLocationsAction.doAction(this, "/" + this.path, false);

				return null;
			} else if (PROGRAM_METHODS.equals(this.path)) {

				this.openProgramMethodsAction.doAction(this, "/" + this.path, false);

				return null;
			}

		}

		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.addURIHandler(this);
		this.addParameterHandler(this);
	}

	public void setOpenProgramLocationsAction(final OpenProgramLocationsAction openProgramLocationsAction) {
		this.openProgramLocationsAction = openProgramLocationsAction;
	}

	public void setOpenProgramMethodsAction(final OpenProgramMethodsAction openProgramMethodsAction) {
		this.openProgramMethodsAction = openProgramMethodsAction;
	}

}

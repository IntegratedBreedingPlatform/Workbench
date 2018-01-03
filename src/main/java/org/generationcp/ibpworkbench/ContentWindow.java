
package org.generationcp.ibpworkbench;

import java.net.URL;
import java.util.Map;

import org.generationcp.ibpworkbench.actions.OpenProgramLocationsAction;
import org.generationcp.ibpworkbench.actions.OpenProgramMethodsAction;
import org.generationcp.ibpworkbench.ui.common.IContainerFittable;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

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
	private Map<String, String[]> queryMap;
	private String path;

	public ContentWindow() {
		super("Breeding Management System | Workbench");
	}

	@Override
	public void showContent(Component content) {

		try {
			this.removeAllComponents();
		} catch (Exception e) {
			// swallow the exception
			LOG.warn(e.getMessage(),e);
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
	public void showContent(String toolUrl) {
		if (!toolUrl.isEmpty()) {
			Embedded browser = new Embedded(null, new ExternalResource(toolUrl));
			browser.setDebugId("browser");

			browser.setType(Embedded.TYPE_BROWSER);
			browser.setSizeFull();

			this.showContent(browser);
		}
	}

	@Override
	public void attach() {
		super.attach();
	}

	@Override
	public void handleParameters(Map<String, String[]> stringMap) {
		for (String key : stringMap.keySet()) {

			String values = "";
			for (String val : stringMap.get(key)) {
				values += val + " ";
			}

			ContentWindow.LOG.debug("query: " + key + " value(s): " + values);
		}

		this.queryMap = stringMap;
	}

	@Override
	public DownloadStream handleURI(URL url, String s) {
		this.path = s;

		String errorMessage = "";
		ContentWindow.LOG.debug("path: " + this.path);

		// perform navigation here
		try {

			if (this.path != null) {
				if (this.path.equals("ProgramLocations")) {

					if (this.queryMap.get("programId") == null) {
						throw new Exception("Wrong query string, should be <strong>programId=[ID]<strong/>.");
					}

					Project project = this.workbenchDataManager.getProjectById(Long.parseLong(this.queryMap.get("programId")[0]));

					if (project == null) {
						throw new Exception("No Program Exists with <strong>programId=" + this.queryMap.get("programId")[0] + "</strong>");
					}

					// execute
					new OpenProgramLocationsAction().doAction(this, "/" + this.path, false);

					return null;
				}

				else if ("ProgramMethods".equals(this.path)) {

					if (this.queryMap.get("programId") == null) {
						throw new Exception("Wrong query string, should be <strong>programId=[ID]<strong/>.");
					}

					Project project = this.workbenchDataManager.getProjectById(Long.parseLong(this.queryMap.get("programId")[0]));

					if (project == null) {
						throw new Exception("No Program Exists with <strong>programId=" + this.queryMap.get("programId")[0] + "</strong>");
					}

					new OpenProgramMethodsAction().doAction(this, "/" + this.path, false);

					return null;
				}

			}

			errorMessage =
					"Incorrect URL. Correct format should be<br/> <strong>/ibpworkbench/content/ProgramLocations?programId=[ID]</strong> or <strong>/ibpworkbench/content/ProgramMethods?programId=[ID]</strong>";

		} catch (NumberFormatException e) {
			errorMessage = "The value you entered for programId is not a number.";
		} catch (Exception e) {

			// error happened
			errorMessage = e.getMessage();

			LOG.error(e.getMessage(),e);

		}

		CustomLayout errorPage = new CustomLayout("error");
		errorPage.setDebugId("errorPage");
		errorPage.setSizeUndefined();
		errorPage.setWidth("100%");
		errorPage.setStyleName("error-page");
		errorPage.addComponent(new Label(errorMessage, Label.CONTENT_XHTML), "error_message");

		this.showContent(errorPage);

		return null;
	}

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.addURIHandler(this);
		this.addParameterHandler(this);
	}
}

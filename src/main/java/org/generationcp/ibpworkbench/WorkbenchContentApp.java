
package org.generationcp.ibpworkbench;

import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.vaadin.actions.UpdateComponentLabelsAction;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * Created by cyrus on 1/24/14.
 */
public class WorkbenchContentApp extends SpringContextApplication implements IWorkbenchSession {

	/**
	 *
	 */
	private static final long serialVersionUID = -3098125752010885259L;

	private final static Logger LOG = LoggerFactory.getLogger(WorkbenchContentApp.class);

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	private UpdateComponentLabelsAction messageSourceListener;

	@Override
	public void close() {
		super.close();

		// implement this when we need to do something on session timeout

		this.messageSource.removeListener(this.messageSourceListener);

		WorkbenchContentApp.LOG.debug("Application closed");
	}

	@Override
	protected void initSpringApplication(ConfigurableWebApplicationContext configurableWebApplicationContext) {
		this.setTheme("gcp-default");

		this.messageSourceListener = new UpdateComponentLabelsAction(this);
		this.messageSource.addListener(this.messageSourceListener);

		this.setMainWindow(new ContentWindow());
	}

	@Override
	public SessionData getSessionData() {
		return this.sessionData;
	}

	public static WorkbenchContentApp get() {
		return ContextApplication.get(WorkbenchContentApp.class);
	}
}

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench;

import com.vaadin.terminal.Terminal;
import com.vaadin.ui.Window;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.commons.hibernate.util.HttpRequestAwareUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.cross.study.adapted.main.QueryForAdaptedGermplasmMain;
import org.generationcp.ibpworkbench.cross.study.h2h.main.HeadToHeadCrossStudyMain;
import org.generationcp.ibpworkbench.cross.study.traitdonors.main.TraitDonorsQueryMain;
import org.generationcp.ibpworkbench.util.awhere.AWhereFormComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The main Vaadin application class for the project.
 */
@Configurable
public class GermplasmStudyBrowserApplication extends SpringContextApplication implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmStudyBrowserApplication.class);

	private static final long serialVersionUID = 1L;

	public static final String STUDY_WINDOW_NAME = "study";
	public static final String HEAD_TO_HEAD_COMPARISON_WINDOW_NAME = "Head_to_head_comparison";
	public static final String QUERY_FOR_ADAPTED_GERMPLASM_WINDOW_NAME = "Query_For_Adapted_Germplasm";
	public static final String TRAIT_DONORS_QUERY_NAME = "Trait_Donors_Query";
	public static final String AWHERE_WINDOW_NAME = "awheretool";

	private static final String HTML_BREAK = "</br>";

	private Window window;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public void initSpringApplication(final ConfigurableWebApplicationContext arg0) {

		// create blank root layouts for the other tabs, the content will be
		// added as the tabs are selected or as the buttons on the WelcomeTab are clicked
		this.window = this.instantiateWindow();
		this.setMainWindow(this.window);
		this.setTheme("gcp-default");
		this.window.setSizeUndefined();

		// Override the existing error handler that shows the stack trace
		this.setErrorHandler(this);
	}

	@Override
	public Window getWindow(final String name) {
		// dynamically create other application-level windows which is associated with specific URLs
		// these windows are the jumping on points to parts of the application
		if (super.getWindow(name) == null) {
			if (GermplasmStudyBrowserApplication.HEAD_TO_HEAD_COMPARISON_WINDOW_NAME.equals(name)) {
				final Window headToHeadQueryToolWindow = new Window("Cross Study: Head-to-Head Comparison");
				// Browser
				headToHeadQueryToolWindow.setName(GermplasmStudyBrowserApplication.HEAD_TO_HEAD_COMPARISON_WINDOW_NAME);
				headToHeadQueryToolWindow.setSizeUndefined();
				headToHeadQueryToolWindow.setContent(new HeadToHeadCrossStudyMain());
				this.addWindow(headToHeadQueryToolWindow);
				return headToHeadQueryToolWindow;
			} else if (GermplasmStudyBrowserApplication.QUERY_FOR_ADAPTED_GERMPLASM_WINDOW_NAME.equals(name)) {
				final Window queryForAdaptedGermplasmToolWindow = new Window("Cross Study: Query-for-Adapted Germplasm");
				// Browser
				queryForAdaptedGermplasmToolWindow.setName(GermplasmStudyBrowserApplication.QUERY_FOR_ADAPTED_GERMPLASM_WINDOW_NAME);
				queryForAdaptedGermplasmToolWindow.setSizeUndefined();
				queryForAdaptedGermplasmToolWindow.setContent(new QueryForAdaptedGermplasmMain());
				this.addWindow(queryForAdaptedGermplasmToolWindow);
				return queryForAdaptedGermplasmToolWindow;
			} else if (GermplasmStudyBrowserApplication.TRAIT_DONORS_QUERY_NAME.equals(name)) {
				final Window traitDonorsQueryToolWindow = new Window("Cross Study: Trait-Donors-Query");
				// Browser
				traitDonorsQueryToolWindow.setName(GermplasmStudyBrowserApplication.TRAIT_DONORS_QUERY_NAME);
				traitDonorsQueryToolWindow.setSizeUndefined();
				traitDonorsQueryToolWindow.setContent(new TraitDonorsQueryMain());
				this.addWindow(traitDonorsQueryToolWindow);
				return traitDonorsQueryToolWindow;
			} else if (GermplasmStudyBrowserApplication.AWHERE_WINDOW_NAME.equals(name)) {
				final Window awhereWindow = new Window("AWhere Test Tool");
				awhereWindow.setName(GermplasmStudyBrowserApplication.AWHERE_WINDOW_NAME);
				awhereWindow.addComponent(new AWhereFormComponent());
				awhereWindow.setWidth("100%");
				awhereWindow.setHeight("100%");
				this.addWindow(awhereWindow);
				return awhereWindow;
			}
		}
		return super.getWindow(name);
	}

	private Window instantiateWindow() {
		final Window window = new Window(this.messageSource.getMessage(Message.STUDY_BROWSER_LINK)); // Study
		// Browser
		window.setName(GermplasmStudyBrowserApplication.STUDY_WINDOW_NAME);
		window.setSizeUndefined();

		return window;
	}

	/**
	 * Override terminalError() to handle terminal errors, to avoid showing the stack trace in the application
	 */
	@Override
	public void terminalError(final Terminal.ErrorEvent event) {
		GermplasmStudyBrowserApplication.LOG.error("An unchecked exception occurred: ", event.getThrowable());
		// Some custom behaviour.
		if (this.getMainWindow() != null) {
			MessageNotifier.showError(this.getMainWindow(), this.messageSource.getMessage(Message.ERROR_INTERNAL), // TESTED
				this.messageSource.getMessage(Message.ERROR_PLEASE_CONTACT_ADMINISTRATOR)
					+ (event.getThrowable().getLocalizedMessage() == null ? "" : GermplasmStudyBrowserApplication.HTML_BREAK
					+ event.getThrowable().getLocalizedMessage()));
		}
	}

	@Override
	public void close() {
		super.close();
		GermplasmStudyBrowserApplication.LOG.debug("Application closed");
	}

	public static GermplasmStudyBrowserApplication get() {
		return ContextApplication.get(GermplasmStudyBrowserApplication.class);
	}

	@Override
	protected void doOnRequestStart(final HttpServletRequest request, final HttpServletResponse response) {
		GermplasmStudyBrowserApplication.LOG.trace("Request started " + request.getRequestURI() + "?" + request.getQueryString());
		synchronized (this) {
			HttpRequestAwareUtil.onRequestStart(this.applicationContext, request, response);
		}
		super.doOnRequestStart(request, response);
	}

	@Override
	protected void doOnRequestEnd(final HttpServletRequest request, final HttpServletResponse response) {
		super.doOnRequestEnd(request, response);

		GermplasmStudyBrowserApplication.LOG.trace("Request ended " + request.getRequestURI() + "?" + request.getQueryString());

		synchronized (this) {
			HttpRequestAwareUtil.onRequestEnd(this.applicationContext, request, response);
		}
	}
}

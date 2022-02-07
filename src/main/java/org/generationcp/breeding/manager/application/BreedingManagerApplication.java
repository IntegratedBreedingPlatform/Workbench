
package org.generationcp.breeding.manager.application;

import com.vaadin.terminal.Terminal;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.apache.commons.lang.math.NumberUtils;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.generationcp.breeding.manager.crossingmanager.settings.ManageCrossingSettingsMain;
import org.generationcp.breeding.manager.util.BreedingManagerUtil;
import org.generationcp.commons.hibernate.util.HttpRequestAwareUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.pojos.GermplasmList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BreedingManagerApplication extends SpringContextApplication implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(BreedingManagerApplication.class);

	private static final long serialVersionUID = 1L;

	public static final String BREEDING_MANAGER_WINDOW_NAME = "breeding-manager";
	public static final String NAVIGATION_FROM_STUDY_PREFIX = "createcrosses";
	public static final String REQ_PARAM_STUDY_ID = "studyid";
	public static final String REQ_PARAM_LIST_ID = "germplasmlistid";
	public static final String REQ_PARAM_STUDY_TYPE = "studyType";
	public static final String REQ_PARAM_CROSSES_LIST_ID = "crosseslistid";
	public static final String[] URL_STUDY = {"/Fieldbook/TrialManager/openTrial/","#/trialSettings"};

	private Window window;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private ApplicationContext applicationContext;

	private ManageCrossingSettingsMain manageCrossingSettingsMain;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public void initSpringApplication(final ConfigurableWebApplicationContext arg0) {

		this.window = this.instantiateWindow(BreedingManagerApplication.BREEDING_MANAGER_WINDOW_NAME);
		this.window.setDebugId("window");
		this.setMainWindow(this.window);
		this.setTheme("breeding-manager");
		this.window.setSizeUndefined();

		// Override the existing error handler that shows the stack trace
		this.setErrorHandler(this);
	}

	@Override
	public Window getWindow(final String name) {
		// dynamically create other application-level windows which is associated with specific URLs
		// these windows are the jumping on points to parts of the application
		if (super.getWindow(name) == null) {

			if (name.startsWith(NAVIGATION_FROM_STUDY_PREFIX)) {

				final Window manageCrossingSettings = new Window(this.messageSource.getMessage(Message.MANAGE_CROSSES));
				manageCrossingSettings.setDebugId("manageCrossingSettings");
				try {
					final String[] studyIdParameterValues =
							BreedingManagerUtil.getApplicationRequest().getParameterValues(BreedingManagerApplication.REQ_PARAM_STUDY_ID);
					final String studyId = studyIdParameterValues != null && studyIdParameterValues.length > 0 ?
							studyIdParameterValues[0] : "";

					final boolean errorWithStudyIdReqParam = studyId.isEmpty() || !NumberUtils.isDigits(studyId);

					manageCrossingSettings.setSizeUndefined();
					return this.validateAndConstructWindow(manageCrossingSettings, Integer.valueOf(studyId), errorWithStudyIdReqParam);
				} catch (final NumberFormatException nfe) {
					return this.getWindowWithErrorMessage(manageCrossingSettings,
							this.messageSource.getMessage(Message.ERROR_WRONG_GERMPLASM_LIST_ID));
				}
			}
		}

		return super.getWindow(name);
	}

	private Window validateAndConstructWindow(final Window manageCrossingSettings, final Integer studyId,
			final boolean errorWithStudyIdReqParam) {

		this.constructCreateCrossesWindow(manageCrossingSettings, studyId);

		if (errorWithStudyIdReqParam) {
			MessageNotifier.showWarning(manageCrossingSettings, this.messageSource.getMessage(Message.ERROR_WITH_REQUEST_PARAMETERS),
					this.messageSource.getMessage(Message.ERROR_WRONG_STUDY_ID));
		}
		return manageCrossingSettings;
	}

	private void constructCreateCrossesWindow(final Window manageCrossingSettings, final Integer studyId) {
		this.manageCrossingSettingsMain = new ManageCrossingSettingsMain(manageCrossingSettings, studyId);
		this.manageCrossingSettingsMain.setDebugId("manageCrossingSettingsMain");
		manageCrossingSettings.setContent(this.manageCrossingSettingsMain);
		this.addWindow(manageCrossingSettings);
	}

	private Window getWindowWithErrorMessage(final Window manageCrossingSettings, final String description) {
		this.manageCrossingSettingsMain = new ManageCrossingSettingsMain(manageCrossingSettings);
		this.manageCrossingSettingsMain.setDebugId("manageCrossingSettingsMain");
		manageCrossingSettings.setContent(this.manageCrossingSettingsMain);
		this.addWindow(manageCrossingSettings);
		MessageNotifier.showWarning(this.getWindow(manageCrossingSettings.getName()),
				this.messageSource.getMessage(Message.ERROR_WITH_REQUEST_PARAMETERS),
				description);
		return manageCrossingSettings;
	}

	protected Window getEmptyWindowWithErrorMessage() {
		final Window emptyGermplasmListDetailsWindow = new Window(this.messageSource.getMessage(Message.LIST_MANAGER_TAB_LABEL));
		emptyGermplasmListDetailsWindow.setDebugId("emptyGermplasmListDetailsWindow");
		emptyGermplasmListDetailsWindow.setSizeUndefined();
		emptyGermplasmListDetailsWindow.addComponent(new Label(this.messageSource.getMessage(Message.INVALID_LIST_ID)));
		this.addWindow(emptyGermplasmListDetailsWindow);
		return emptyGermplasmListDetailsWindow;
	}

	private Window instantiateWindow(final String name) {
		final Window listManagerWindow = new Window(this.messageSource.getMessage(Message.LIST_MANAGER_TAB_LABEL));
		listManagerWindow.setDebugId("breedingManagerWindow");
		listManagerWindow.setName(name);
		listManagerWindow.setSizeFull();
		listManagerWindow.setResizable(true);

		return listManagerWindow;
	}

	/**
	 * Override terminalError() to handle terminal errors, to avoid showing the stack trace in the application
	 */
	@Override
	public void terminalError(final Terminal.ErrorEvent event) {
		BreedingManagerApplication.LOG.error("An unchecked exception occurred: ", event.getThrowable());
		// Some custom behaviour.
		if (this.getMainWindow() != null) {
			MessageNotifier.showError(this.getMainWindow(), this.messageSource.getMessage(Message.ERROR_INTERNAL), // TESTED
					this.messageSource.getMessage(Message.ERROR_PLEASE_CONTACT_ADMINISTRATOR)
							+ (event.getThrowable().getLocalizedMessage() == null ? "" : "</br>"
									+ event.getThrowable().getLocalizedMessage()));
		}
	}

	@Override
	public void close() {
		super.close();

		BreedingManagerApplication.LOG.debug("Application closed");
	}

	public static BreedingManagerApplication get() {
		return ContextApplication.get(BreedingManagerApplication.class);
	}

	@Override
	protected void doOnRequestStart(final HttpServletRequest request, final HttpServletResponse response) {
		super.doOnRequestStart(request, response);
		BreedingManagerApplication.LOG.trace("Request started " + request.getRequestURI() + "?" + request.getQueryString());
		synchronized (this) {
			HttpRequestAwareUtil.onRequestStart(this.applicationContext, request, response);
		}
	}

	@Override
	protected void doOnRequestEnd(final HttpServletRequest request, final HttpServletResponse response) {
		super.doOnRequestEnd(request, response);
		BreedingManagerApplication.LOG.trace("Request ended " + request.getRequestURI() + "?" + request.getQueryString());

		synchronized (this) {
			HttpRequestAwareUtil.onRequestEnd(this.applicationContext, request, response);
		}

	}

	public ManageCrossingSettingsMain getManageCrossingSettingsMain() {
		return this.manageCrossingSettingsMain;
	}

	public void refreshCrossingManagerTree() {
		final ManageCrossingSettingsMain manageCrossSettingsMain = this.getManageCrossingSettingsMain();
		if (manageCrossSettingsMain != null) {
			manageCrossSettingsMain.getMakeCrossesComponent().getSelectParentsComponent().getListTreeComponent().refreshComponent();
		}
	}

	public void updateUIForDeletedList(final GermplasmList germplasmList) {
		if (this.getManageCrossingSettingsMain() != null) {
			this.getManageCrossingSettingsMain().getMakeCrossesComponent().getParentsComponent().updateUIForDeletedList(germplasmList);
			this.getManageCrossingSettingsMain().getMakeCrossesComponent().showNodeOnTree(germplasmList.getId());
		}
	}

}

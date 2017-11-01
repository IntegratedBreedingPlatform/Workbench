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

package org.generationcp.ibpworkbench.actions;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.window.ChangeCredentialsWindow;
import org.generationcp.ibpworkbench.ui.window.ChangePasswordWindow;
import org.generationcp.ibpworkbench.ui.window.UserToolsManagerWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

@Configurable
public class OpenWindowAction implements WorkflowConstants, ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(OpenWindowAction.class);

	private Project project;
	private WindowEnum windowEnum;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Resource
	private SessionData sessionData;

	public OpenWindowAction() {
	}

	public OpenWindowAction(final WindowEnum windowEnum) {
		this.windowEnum = windowEnum;
	}

	public OpenWindowAction(final WindowEnum windowEnum, final Project project) {
		this.windowEnum = windowEnum;
		this.project = project;

	}

	@Override
	public void buttonClick(final ClickEvent event) {
		this.doAction(event);
	}

	@Override
	public void doAction(final Event event) {
		final Window window = event.getComponent().getWindow();
		this.launchWindow(window, this.windowEnum);
	}

	@Override
	public void doAction(final Window window, final String uriFragment, final boolean isLinkAccessed) {

		final String windowName = uriFragment.split("/")[1];

		if (WindowEnum.isCorrectTool(windowName)) {
			this.launchWindow(window, WindowEnum.equivalentWindowEnum(windowName));
		} else {
			OpenWindowAction.LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
			MessageNotifier.showError(window, this.messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
					this.messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
		}
	}

	public void launchWindow(final Window window, final WindowEnum windowName) {
		final Window mywindow;
		Boolean logActivity = false;
		String windowCaption = "";

		if (WindowEnum.CHANGE_PASSWORD.equals(windowName)) {
			mywindow = new ChangePasswordWindow();
			window.addWindow(mywindow);
			windowCaption = mywindow.getCaption();
			logActivity = false;
		} else if (WindowEnum.CHANGE_CREDENTIALS.equals(windowName)) {
			mywindow = new ChangeCredentialsWindow();
			window.addWindow(mywindow);
			windowCaption = mywindow.getCaption();
			logActivity = false;
		} else if (WindowEnum.USER_TOOLS.equals(windowName)) {
			mywindow = new UserToolsManagerWindow();
			window.addWindow(mywindow);
			windowCaption = mywindow.getCaption();
			logActivity = true;
		} else if (WindowEnum.SOFTWARE_LICENSING_AGREEMENT.equals(windowName)) {
			final ConfirmDialog dialog =
					ConfirmDialog.show(window, this.messageSource.getMessage(Message.SOFTWARE_LICENSE_AGREEMENT),
							this.messageSource.getMessage(Message.SOFTWARE_LICENSE_AGREEMENT_DETAILS, this.getCutOffDate()),
									this.messageSource.getMessage(Message.DONE), null, new ConfirmDialog.Listener() {

								private static final long serialVersionUID = 1L;

						@Override
						public void onClose(final ConfirmDialog dialog) {

							if (dialog.isConfirmed()) {
								window.removeWindow(dialog);
							}

						}
					});

			dialog.setContentMode(ConfirmDialog.CONTENT_HTML);
			windowCaption = this.messageSource.getMessage(Message.SOFTWARE_LICENSE_AGREEMENT);
			logActivity = true;

		}

		// Add to Project Activity logs the launched windows
		if (logActivity && this.project != null) {
			try {
				this.sessionData.logProgramActivity(windowName.getwindowName(),
						this.messageSource.getMessage(Message.LAUNCHED_APP, windowCaption));
			} catch (final MiddlewareQueryException e) {
				OpenWindowAction.LOG.error(e.getMessage(), e);
				MessageNotifier.showError(window, this.messageSource.getMessage(Message.DATABASE_ERROR),
						"<br />" + this.messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));

			}

		}
	}

	protected String getCutOffDate() {
		// Dec 31, 2015
		final Calendar cal = DateUtil.getCalendarInstance();
		cal.set(Calendar.YEAR, 2015);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DATE, 31);
		final Date cutOffDate = cal.getTime();
		return DateUtil.formatDateAsStringValue(cutOffDate, "MMMMM dd, yyyy");

	}

	public enum WindowEnum {
		CHANGE_PASSWORD("change_password"), USER_TOOLS("user_tools"), SOFTWARE_LICENSING_AGREEMENT("software_license"), CHANGE_CREDENTIALS("change_credentials");

		private final String windowName;

		WindowEnum(final String windowName) {
			this.windowName = windowName;
		}

		public static WindowEnum equivalentWindowEnum(final String windowName) {
			for (final WindowEnum window : WindowEnum.values()) {
				if (window.getwindowName().equals(windowName)) {
					return window;
				}
			}
			return null;
		}

		public static boolean isCorrectTool(final String windowName) {

			for (final WindowEnum winEnum : WindowEnum.values()) {
				if (winEnum.getwindowName().equals(windowName)) {
					return true;
				}
			}

			return false;

		}

		public String getwindowName() {
			return this.windowName;
		}
	}

}

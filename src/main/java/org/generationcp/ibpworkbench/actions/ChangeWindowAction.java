/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import org.apache.commons.lang.StringUtils;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.ibpworkbench.ui.breedingview.metaanalysis.MetaAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.MultiSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.recovery.BackupAndRestoreView;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;

@Configurable
public class ChangeWindowAction implements WorkflowConstants, ClickListener, ActionListener {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ChangeWindowAction.class);

	@Autowired
	private SessionData sessionData;
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Project project;
	private WindowEnums windowEnums;

	public ChangeWindowAction(WindowEnums windowEnum, Project project) {
		this.windowEnums = windowEnum;
		this.project = project;
	}

	/**
	 * @Depricated, toolConfiguration is no longer necessary
	 */
	@Deprecated
	public ChangeWindowAction(WindowEnums windowEnum, Project project, String toolConfiguration) {
		this.windowEnums = windowEnum;
		this.project = project;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		doAction(event);
	}

	@Override
	public void doAction(Event event) {
		doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
		String windowName = StringUtils.isNotBlank(uriFragment) ? StringUtils.removeStart(uriFragment,"/") : windowEnums.getwindowName();

		if (WindowEnums.isCorrectTool(windowName)) {
			launchWindow(window, windowName, isLinkAccessed);
		} else {
			LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
			MessageNotifier
					.showError(window, messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
							messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC,
									Arrays.asList(windowName).toArray()));
		}

	}

	public void launchWindow(Window window, String windowName, boolean isLinkAccessed) {
		IContentWindow w = (IContentWindow) window;

		// TASK: get messagesource equivalent
		String appLaunched = windowName;
		if (WindowEnums.MEMBER.getwindowName().equals(windowName)) {
			appLaunched = messageSource.getMessage(Message.MEMBERS_LINK);
			ProgramMembersPanel projectLocationPanel = new ProgramMembersPanel(this.project);
			w.showContent(projectLocationPanel);
		} else if (WindowEnums.RECOVERY.getwindowName().equals(windowName)) {
			appLaunched = messageSource.getMessage("BACKUP_RESTORE_TITLE");
			BackupAndRestoreView backupAndRestoreView = new BackupAndRestoreView();
			w.showContent(backupAndRestoreView);
		} else if (WindowEnums.BREEDING_GXE.getwindowName().equals(windowName)) {
			appLaunched = messageSource.getMessage(Message.TITLE_GXE);
			MultiSiteAnalysisPanel gxeAnalysisPanel = new MultiSiteAnalysisPanel(this.project);
			w.showContent(gxeAnalysisPanel);
		} else if (WindowEnums.BREEDING_VIEW.getwindowName().equals(windowName)) {
			appLaunched = messageSource.getMessage(Message.TITLE_SSA);
			SingleSiteAnalysisPanel breedingViewPanel = new SingleSiteAnalysisPanel(this.project,
					Database.LOCAL);
			w.showContent(breedingViewPanel);
		} else if (WindowEnums.BV_META_ANALYSIS.getwindowName().equals(windowName)) {
			appLaunched = messageSource.getMessage(Message.TITLE_METAANALYSIS);
			MetaAnalysisPanel metaAnalyis = new MetaAnalysisPanel(this.project, Database.LOCAL);
			w.showContent(metaAnalyis);
		}

		try {

			sessionData.logProgramActivity(windowName,messageSource.getMessage(Message.LAUNCHED_APP,appLaunched));

		} catch (MiddlewareQueryException e1) {
			MessageNotifier.showError(window, messageSource.getMessage(Message.DATABASE_ERROR),
					"<br />" + messageSource.getMessage(Message.CONTACT_ADMIN_ERROR_DESC));
		}

	}

	public static enum WindowEnums {
		BREEDING_VIEW("breeding_view_wb")
		, BREEDING_GXE("breeding_gxe")
		, BV_META_ANALYSIS("bv_meta_analysis")
		, MEMBER("program_member")
		, RECOVERY("recovery")
		;

		String windowName;

		WindowEnums(String windowName) {
			this.windowName = windowName;
		}

		public static WindowEnums equivalentWindowEnum(String windowName) {
			for (WindowEnums window : WindowEnums.values()) {
				if (window.getwindowName().equals(windowName)) {
					return window;
				}
			}
			return null;
		}

		public static boolean isCorrectTool(String windowName) {

			for (WindowEnums winEnum : WindowEnums.values()) {
				if (winEnum.getwindowName().equals(windowName)) {
					return true;
				}
			}

			return false;
		}

		public String getwindowName() {
			return windowName;
		}
	}

}

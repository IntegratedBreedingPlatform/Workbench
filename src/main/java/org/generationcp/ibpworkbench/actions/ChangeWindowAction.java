/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import org.apache.commons.lang.StringUtils;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.breedingview.metaanalysis.MetaAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.multisiteanalysis.MultiSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.programmembers.ProgramMembersPanel;
import org.generationcp.ibpworkbench.ui.recovery.BackupAndRestoreView;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Arrays;

@Configurable
public class ChangeWindowAction implements ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(ChangeWindowAction.class);

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final Project project;
	private final WindowEnums windowEnums;

	public ChangeWindowAction(final WindowEnums windowEnum, final Project project) {
		this.windowEnums = windowEnum;
		this.project = project;
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		this.doAction(event);
	}

	@Override
	public void doAction(final Event event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(final Window window, final String uriFragment, final boolean isLinkAccessed) {
		final String windowName =
				StringUtils.isNotBlank(uriFragment) ? StringUtils.removeStart(uriFragment, "/") : this.windowEnums.getwindowName();

		if (WindowEnums.isCorrectTool(windowName)) {
			this.launchWindow(window, windowName, isLinkAccessed);
		} else {
			ChangeWindowAction.LOG.debug("Cannot launch window due to invalid window name: {}", windowName);
			MessageNotifier.showError(window, this.messageSource.getMessage(Message.LAUNCH_TOOL_ERROR),
					this.messageSource.getMessage(Message.INVALID_TOOL_ERROR_DESC, Arrays.asList(windowName).toArray()));
		}

	}

	public void launchWindow(final Window window, final String windowName, final boolean isLinkAccessed) {
		final IContentWindow w = (IContentWindow) window;

		// TASK: get messagesource equivalent
		String appLaunched = windowName;
		if (WindowEnums.MEMBER.getwindowName().equals(windowName)) {
			appLaunched = this.messageSource.getMessage(Message.MEMBERS_LINK);
			final ProgramMembersPanel projectLocationPanel = new ProgramMembersPanel(this.project);
			projectLocationPanel.setDebugId("projectLocationPanel");
			w.showContent(projectLocationPanel);
		} else if (WindowEnums.RECOVERY.getwindowName().equals(windowName)) {
			appLaunched = this.messageSource.getMessage("BACKUP_RESTORE_TITLE");
			final BackupAndRestoreView backupAndRestoreView = new BackupAndRestoreView();
			backupAndRestoreView.setDebugId("backupAndRestoreView");
			w.showContent(backupAndRestoreView);
		} else if (WindowEnums.BREEDING_GXE.getwindowName().equals(windowName)) {
			appLaunched = this.messageSource.getMessage(Message.TITLE_GXE);
			final MultiSiteAnalysisPanel gxeAnalysisPanel = new MultiSiteAnalysisPanel(this.project);
			gxeAnalysisPanel.setDebugId("gxeAnalysisPanel");
			w.showContent(gxeAnalysisPanel);
		} else if (WindowEnums.BREEDING_VIEW.getwindowName().equals(windowName)) {
			appLaunched = this.messageSource.getMessage(Message.TITLE_SSA);
			final SingleSiteAnalysisPanel breedingViewPanel = new SingleSiteAnalysisPanel(this.project, Database.LOCAL);
			breedingViewPanel.setDebugId("breedingViewPanel");
			w.showContent(breedingViewPanel);
		} else if (WindowEnums.BV_META_ANALYSIS.getwindowName().equals(windowName)) {
			appLaunched = this.messageSource.getMessage(Message.TITLE_METAANALYSIS);
			final MetaAnalysisPanel metaAnalyis = new MetaAnalysisPanel(this.project, Database.LOCAL);
			metaAnalyis.setDebugId("metaAnalyis");
			w.showContent(metaAnalyis);
		}

		this.contextUtil.logProgramActivity(windowName, this.messageSource.getMessage(Message.LAUNCHED_APP, appLaunched));

	}

	public enum WindowEnums {
		BREEDING_VIEW("breeding_view_wb"), BREEDING_GXE("breeding_gxe"), BV_META_ANALYSIS("bv_meta_analysis"), MEMBER(
				"program_member"), RECOVERY("recovery");

		String windowName;

		WindowEnums(final String windowName) {
			this.windowName = windowName;
		}

		public static WindowEnums equivalentWindowEnum(final String windowName) {
			for (final WindowEnums window : WindowEnums.values()) {
				if (window.getwindowName().equals(windowName)) {
					return window;
				}
			}
			return null;
		}

		public static boolean isCorrectTool(final String windowName) {

			for (final WindowEnums winEnum : WindowEnums.values()) {
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

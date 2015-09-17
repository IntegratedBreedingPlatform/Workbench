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

import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.breedingview.singlesiteanalysis.SingleSiteAnalysisPanel;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 *
 * @author Jeffrey Morales
 *
 */
public class OpenSelectProjectForStudyAndDatasetViewAction implements ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(OpenSelectProjectForStudyAndDatasetViewAction.class);

	private final Project currentProject;
	private Project lastOpenedProject;
	private Role role;

	public OpenSelectProjectForStudyAndDatasetViewAction(Project currentProject) {

		this.currentProject = currentProject;

	}

	public OpenSelectProjectForStudyAndDatasetViewAction(Project project, Role role) {
		this.currentProject = project;
		this.role = role;
	}

	@Override
	public void buttonClick(ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {

		IContentWindow w = (IContentWindow) window;

		IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

		this.lastOpenedProject = app.getSessionData().getLastOpenedProject();

		if (this.currentProject != null) {

			w.showContent(new SingleSiteAnalysisPanel(this.currentProject, Database.LOCAL));
		} else if (this.lastOpenedProject != null) {

			w.showContent(new SingleSiteAnalysisPanel(this.lastOpenedProject, Database.LOCAL));
		} else {
			MessageNotifier.showWarning(window, "Error", "Please select a Project first.");

		}

	}

	@Override
	public void doAction(Event event) {
		// does nothing
	}

}

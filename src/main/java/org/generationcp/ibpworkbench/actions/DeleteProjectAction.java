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

import java.util.List;

import org.generationcp.commons.hibernate.DefaultManagerFactoryProvider;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
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
public class DeleteProjectAction implements ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(DeleteProjectAction.class);
	private Project currentProject;
	private ClickEvent evt;

	@Autowired
	private WorkbenchDataManager manager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private DefaultManagerFactoryProvider managerFactoryProvider;
	
	@Autowired
	private StudyDataManager studyDataManager;

	public DeleteProjectAction() {
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		this.evt = event;

		this.doAction(event.getComponent().getWindow(), "delete_program", true);
	}

	@Override
	public void doAction(Event event) {
		// do nothing
	}

	@Override
	public void doAction(final Window window, String uriFragment, boolean isLinkAccessed) {
		final IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
		if (app.getMainWindow() != null) {
			this.currentProject = app.getSessionData().getSelectedProject();
			if (this.currentProject == null) {
				MessageNotifier.showError(window, this.messageSource.getMessage(Message.INVALID_OPERATION),
						this.messageSource.getMessage(Message.INVALID_NO_PROGRAM_SELECTED));

			}
			ConfirmDialog.show(app.getMainWindow(), this.messageSource.getMessage(Message.DELETE_PROJECT_LINK),
					this.messageSource.getMessage(Message.DELETE_PROGRAM_CONFIRM, this.currentProject.getProjectName()),
					this.messageSource.getMessage(Message.YES), this.messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {

						/**
				 *
				 */
				 private static final long serialVersionUID = 6975196694103407530L;

						@Override
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								try {
									DeleteProjectAction.this.deleteProgram(app.getSessionData());
								} catch (MiddlewareQueryException e) {
									DeleteProjectAction.LOG.error(e.getMessage(), e);
								}
								// go back to dashboard
								new HomeAction().doAction(window, "/Home", true);
							}
						}
					});
		}
	}

	protected void deleteProgram(SessionData sessionData) throws MiddlewareQueryException {
		this.deleteAllProgramStudies();
		this.deleteAllProgramFavorites();
		this.deleteAllProgramLocationsAndMethods();
		this.managerFactoryProvider.removeProjectFromSessionCache(this.currentProject.getProjectId());
		this.manager.deleteProjectDependencies(this.currentProject);
		Project newProj = new Project();
		newProj.setProjectId(this.currentProject.getProjectId());
		newProj.setProjectName(this.currentProject.getProjectName());
		this.manager.deleteProject(newProj);
		sessionData.setSelectedProject(this.manager.getLastOpenedProject(sessionData.getUserData().getUserid()));
	}

	protected void deleteAllProgramLocationsAndMethods() throws MiddlewareQueryException {
		ManagerFactory managerFactory = this.managerFactoryProvider.getManagerFactoryForProject(this.currentProject);
		GermplasmDataManager germplasmDataManager = managerFactory.getGermplasmDataManager();
		LocationDataManager locationDataManager = managerFactory.getLocationDataManager();
		locationDataManager.deleteProgramLocationsByUniqueId(this.currentProject.getUniqueID());
		germplasmDataManager.deleteProgramMethodsByUniqueId(this.currentProject.getUniqueID());
	}

	protected void deleteAllProgramFavorites() throws MiddlewareQueryException {
		ManagerFactory managerFactory = this.managerFactoryProvider.getManagerFactoryForProject(this.currentProject);
		GermplasmDataManager germplasmDataManager = managerFactory.getGermplasmDataManager();
		List<ProgramFavorite> favoriteLocations =
				germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, this.currentProject.getUniqueID());
		List<ProgramFavorite> favoriteMethods =
				germplasmDataManager.getProgramFavorites(FavoriteType.METHOD, this.currentProject.getUniqueID());
		germplasmDataManager.deleteProgramFavorites(favoriteLocations);
		germplasmDataManager.deleteProgramFavorites(favoriteMethods);

	}

	protected void deleteAllProgramStudies() throws MiddlewareQueryException {
		this.studyDataManager.deleteProgramStudies(this.currentProject.getUniqueID());
	}
}

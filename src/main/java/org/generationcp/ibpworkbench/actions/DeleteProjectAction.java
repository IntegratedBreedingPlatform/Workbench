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
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Configurable
public class DeleteProjectAction implements ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(DeleteProjectAction.class);

	@Autowired
	private WorkbenchDataManager manager;

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Resource
	private HttpServletRequest request;

	public DeleteProjectAction() {
		// does nothing here
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		try {
			this.doAction(event.getComponent().getWindow(), "delete_program", true);
		} catch (final AccessDeniedException ex) {
			LOG.error(ex.getMessage(), ex);
			//the only reason we are catching this is in case someone used this wrongly.
			MessageNotifier.showError(event.getComponent().getWindow(), "Error!", "Operation not allowed for user.");
		}

	}

	/**
	 * Class implements workbench's ActionListener class which has two doAction implementations.
	 * This class only uses doAction(final Window window, String uriFragment, boolean isLinkAccessed) thats why this method remains unimplemented
	 *
	 * @param event the event
	 */
	@Override
	public void doAction(final Event event) {
		// No state or initial values are required to be initialized for this layout
	}

	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CROP_MANAGEMENT')")
	public void doAction(final Window window, final String uriFragment, final boolean isLinkAccessed) {
		final Project currentProject = this.contextUtil.getProjectInContext();

		if (!Objects.isNull(window)) {
			if (currentProject == null) {
				MessageNotifier.showError(window, this.messageSource.getMessage(Message.INVALID_OPERATION),
						this.messageSource.getMessage(Message.INVALID_NO_PROGRAM_SELECTED));

			}
			ConfirmDialog.show(window, this.messageSource.getMessage(Message.DELETE_PROJECT_LINK),
					this.messageSource.getMessage(Message.DELETE_PROGRAM_CONFIRM, currentProject.getProjectName()),
					this.messageSource.getMessage(Message.YES), this.messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {

						/**
						 *
						 */
						private static final long serialVersionUID = 6975196694103407530L;

						@Override
						public void onClose(final ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								try {
									DeleteProjectAction.this.deleteProgram(currentProject);

									org.generationcp.commons.util.ContextUtil
										.setContextInfo(DeleteProjectAction.this.request, DeleteProjectAction.this.contextUtil.getCurrentWorkbenchUserId(), null, null);

								} catch (final MiddlewareQueryException e) {
									DeleteProjectAction.LOG.error(e.getMessage(), e);
								}
								// go back to dashboard
								window.executeJavaScript("window.top.postMessage({ programDeleted: 'true'}, '*');");
							}
						}
					});
		}
	}

	protected void deleteProgram(final Project project) {

		// soft delete
		this.deleteAllProgramStudies(project);

		//hard delete
		this.deleteAllProgramFavorites(project);

		// soft delete
		this.deleteAllProgramLists(project);

		this.manager.deleteProjectDependencies(project);

		this.manager.deleteProject(project);

	}

	protected void deleteAllProgramFavorites(final Project project) {
		final List<ProgramFavorite> favoriteLocations =
				germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, project.getUniqueID());
		final List<ProgramFavorite> favoriteMethods = germplasmDataManager.getProgramFavorites(FavoriteType.METHODS, project.getUniqueID());
		germplasmDataManager.deleteProgramFavorites(favoriteLocations);
		germplasmDataManager.deleteProgramFavorites(favoriteMethods);

	}

	protected void deleteAllProgramStudies(final Project project) {
		this.studyDataManager.deleteProgramStudies(project.getUniqueID());
	}

	protected void deleteAllProgramLists(final Project project) {
		this.germplasmListManager.deleteGermplasmListsByProgram(project.getUniqueID());
	}

}


package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.exception.GermplasmStudyBrowserException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Window;

@Configurable
public class StudyTreeDeleteItemHandler {

	protected final class DeleteItemConfirmHandler implements ConfirmDialog.Listener {

		private final Integer studyId;
		private static final long serialVersionUID = 1L;

		protected DeleteItemConfirmHandler(final Integer studyId) {
			this.studyId = studyId;
		}

		@Override
		public void onClose(final ConfirmDialog dialog) {
			if (dialog.isConfirmed()) {
				StudyTreeDeleteItemHandler.this.performDeleteAction(this.studyId);
			}
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(StudyTreeDeleteItemHandler.class);

	private static final String NO_SELECTION = "Please select a folder item";
	private static final String NOT_FOLDER = "Selected item is not a folder.";
	private static final String HAS_CHILDREN = "Folder has child items.";

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	private StudyTree targetTree;
	private StudyTreeButtonsPanel buttonsPanel;
	private final Window parentWindow;

	public StudyTreeDeleteItemHandler(final StudyTree targetTree, final StudyTreeButtonsPanel buttonsPanel, final Window parentWindow) {
		super();
		this.targetTree = targetTree;
		this.buttonsPanel = buttonsPanel;
		this.parentWindow = parentWindow;
	}

	/**
	 * Checks if given id is: 1. existing in the database 2. is a folder and 3. does not has have children items
	 *
	 * If any of the checking failed, throws exception
	 *
	 * @param id
	 * @throws GermplasmStudyBrowserException
	 */
	void validateItemForDeletion(final Integer id) throws GermplasmStudyBrowserException {
		if (id == null) {
			throw new GermplasmStudyBrowserException(StudyTreeDeleteItemHandler.NO_SELECTION);
		}
		DmsProject project = null;

		try {
			project = this.studyDataManager.getProject(id);

		} catch (final MiddlewareQueryException e) {
			StudyTreeDeleteItemHandler.LOG.error(e.getMessage(), e);
			throw new GermplasmStudyBrowserException(this.messageSource.getMessage(Message.ERROR_DATABASE));
		}

		if (project == null) {
			throw new GermplasmStudyBrowserException(this.messageSource.getMessage(Message.ERROR_DATABASE));
		}

		if (!this.targetTree.isFolder(id)) {
			throw new GermplasmStudyBrowserException(StudyTreeDeleteItemHandler.NOT_FOLDER);
		}

		if (this.targetTree.hasChildStudy(id)) {
			throw new GermplasmStudyBrowserException(StudyTreeDeleteItemHandler.HAS_CHILDREN);
		}

	}

	/**
	 * Performs validations on folder to be deleted. If folder can be deleted, deletes it from database and adjusts tree view
	 *
	 * @param studyId
	 */
	public void showConfirmDeletionDialog(final Integer studyId) {
		try {
			this.validateItemForDeletion(studyId);
		} catch (final GermplasmStudyBrowserException e) {
			StudyTreeDeleteItemHandler.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(this.targetTree.getWindow(), this.messageSource.getMessage(Message.ERROR), e.getMessage());
			return;
		}

		ConfirmDialog.show(this.parentWindow, this.messageSource.getMessage(Message.DELETE_ITEM),
				this.messageSource.getMessage(Message.DELETE_ITEM_CONFIRM), this.messageSource.getMessage(Message.YES),
				this.messageSource.getMessage(Message.NO), new DeleteItemConfirmHandler(studyId));
	}

	protected void performDeleteAction(final Integer studyId) {
		try {

			final DmsProject parent = this.studyDataManager.getParentFolder(studyId);
			final String programUUID = this.contextUtil.getProjectInContext().getUniqueID();
			this.studyDataManager.deleteEmptyFolder(studyId, programUUID);

			this.targetTree.removeItem(this.targetTree.getValue());
			if (parent != null) {
				final Integer parentId = parent.getProjectId();
				if (DmsProject.SYSTEM_FOLDER_ID.equals(parentId)) {
					this.targetTree.selectItem(StudyTree.STUDY_ROOT_NODE);
				} else {
					this.targetTree.selectItem(parentId);
					this.targetTree.expandItem(parentId);
				}
			}
			this.targetTree.setImmediate(true);
			this.buttonsPanel.updateButtons(this.targetTree.getValue());

		} catch (final MiddlewareQueryException e) {
			StudyTreeDeleteItemHandler.LOG.error(e.getMessage(), e);
			MessageNotifier.showError(this.targetTree.getWindow(), this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
		}
	}

	protected void setTargetTree(final StudyTree targetTree) {
		this.targetTree = targetTree;
	}

	protected void setButtonsPanel(final StudyTreeButtonsPanel buttonsPanel) {
		this.buttonsPanel = buttonsPanel;
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

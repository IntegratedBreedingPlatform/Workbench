
package org.generationcp.ibpworkbench.study.tree;

import java.io.Serializable;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Tree.TreeTargetDetails;

@Configurable
public class StudyTreeDragAndDropHandler implements Serializable {

	private static final long serialVersionUID = -4427723835290060592L;
	private static final Logger LOG = LoggerFactory.getLogger(StudyTreeDragAndDropHandler.class);

	private static final String HAS_CHILDREN = "Folder has child items.";

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private StudyDataManager studyDataManager;

	private final StudyTree targetTree;

	public StudyTreeDragAndDropHandler(final StudyTree targetTree) {
		this.targetTree = targetTree;
	}

	protected boolean setParent(final Object sourceItemId, final Object targetItemId, final boolean isStudy) {

		if (sourceItemId.equals(StudyTree.STUDY_ROOT_NODE)) {
			MessageNotifier.showWarning(this.targetTree.getWindow(), this.messageSource.getMessage(Message.ERROR_WITH_MODIFYING_STUDY_TREE),
					this.messageSource.getMessage(Message.MOVE_ROOT_FOLDERS_NOT_ALLOWED));
			return false;
		}

		Integer sourceId = null;
		Integer targetId = null;

		if (sourceItemId != null) {
			sourceId = Integer.valueOf(sourceItemId.toString());
		}

		if (this.targetTree.hasChildStudy(sourceId)) {
			MessageNotifier.showWarning(this.targetTree.getWindow(), this.messageSource.getMessage(Message.ERROR_WITH_MODIFYING_STUDY_TREE),
					StudyTreeDragAndDropHandler.HAS_CHILDREN);
			return false;
		}

		if (StudyTree.STUDY_ROOT_NODE.equals(targetItemId)) {
			targetId = DmsProject.SYSTEM_FOLDER_ID;
		} else if (targetItemId != null) {
			targetId = Integer.valueOf(targetItemId.toString());
		}

		try {
			if (targetId != null && sourceId != null) {
				this.studyDataManager.moveDmsProject(sourceId.intValue(), targetId.intValue(), isStudy);
				// apply to UI
				if (targetItemId == null || this.targetTree.getItem(targetItemId) == null) {
					this.targetTree.setChildrenAllowed(sourceItemId, true);
					this.targetTree.setParent(sourceItemId, StudyTree.STUDY_ROOT_NODE);
					this.targetTree.expandItem(StudyTree.STUDY_ROOT_NODE);
				} else {
					this.targetTree.setChildrenAllowed(targetItemId, true);
					this.targetTree.setParent(sourceItemId, targetItemId);
					this.targetTree.expandItem(targetItemId);
				}
				this.targetTree.select(sourceItemId);
				return true;
			}
		} catch (final MiddlewareQueryException e) {
			StudyTreeDragAndDropHandler.LOG.error("Error with moving node to target folder.", e);
			MessageNotifier.showError(this.targetTree.getWindow(), this.messageSource.getMessage(Message.ERROR),
					e.getMessage());
		}
		return false;
	}

	public void setupTreeDragAndDropHandler() {
		this.targetTree.setDropHandler(new DropHandler() {

			private static final long serialVersionUID = -6676297159926786216L;

			@Override
			public void drop(final DragAndDropEvent dropEvent) {
				final Transferable t = dropEvent.getTransferable();
				if (t.getSourceComponent() != StudyTreeDragAndDropHandler.this.targetTree) {
					return;
				}

				final TreeTargetDetails target = (TreeTargetDetails) dropEvent.getTargetDetails();

				final Object sourceItemId = t.getData("itemId");
				final Object targetItemId = target.getItemIdOver();

				final VerticalDropLocation location = target.getDropLocation();

				if (location != VerticalDropLocation.MIDDLE || sourceItemId.equals(targetItemId)) {
					return;
				}

				final boolean sourceIsStudy = !StudyTreeDragAndDropHandler.this.targetTree.isFolder((Integer) sourceItemId);
				if (targetItemId instanceof Integer) {
					final Boolean targetIsFolder = StudyTreeDragAndDropHandler.this.targetTree.isFolder((Integer) targetItemId);
					if (targetIsFolder) {
						StudyTreeDragAndDropHandler.this.setParent(sourceItemId, targetItemId, sourceIsStudy);
					} else {
						try {
							final DmsProject parentFolder =
									StudyTreeDragAndDropHandler.this.studyDataManager.getParentFolder(((Integer) targetItemId).intValue());
							if (parentFolder != null) {
								if (parentFolder.getProjectId().equals(Integer.valueOf(1))) {
									StudyTreeDragAndDropHandler.this.setParent(sourceItemId, StudyTree.STUDY_ROOT_NODE, sourceIsStudy);
								} else {
									StudyTreeDragAndDropHandler.this.setParent(sourceItemId, parentFolder.getProjectId(), sourceIsStudy);
								}
							} else {
								StudyTreeDragAndDropHandler.this.setParent(sourceItemId, StudyTree.STUDY_ROOT_NODE, sourceIsStudy);
							}
						} catch (final MiddlewareQueryException e) {
							StudyTreeDragAndDropHandler.LOG.error("Error with getting parent folder of a project record.", e);
							MessageNotifier.showError(StudyTreeDragAndDropHandler.this.targetTree.getWindow(),
									StudyTreeDragAndDropHandler.this.messageSource.getMessage(Message.ERROR_INTERNAL),
									StudyTreeDragAndDropHandler.this.messageSource.getMessage(Message.ERROR_REPORT_TO));
						}
					}
				} else {
					StudyTreeDragAndDropHandler.this.setParent(sourceItemId, targetItemId, sourceIsStudy);
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion() {
				return AcceptAll.get();
			}
		});
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

}


package org.generationcp.ibpworkbench.study.tree.listeners;

import org.generationcp.ibpworkbench.study.tree.BrowseStudyTreeComponent;
import org.generationcp.ibpworkbench.study.tree.StudyTree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents.ClickEvent;

public class StudyTreeItemClickListener implements ItemClickListener {

	private static final long serialVersionUID = 1L;
	private final StudyTree studyTree;
	private final BrowseStudyTreeComponent browseStudyTreeComponent;

	public StudyTreeItemClickListener(final StudyTree studyTree, final BrowseStudyTreeComponent browseStudyTreeComponent) {
		super();
		this.studyTree = studyTree;
		this.browseStudyTreeComponent = browseStudyTreeComponent;
	}

	@Override
	public void itemClick(final ItemClickEvent event) {
		final Object itemId = event.getItemId();

		// Open selected study when double clicked
		if (event.getButton() == ClickEvent.BUTTON_LEFT && event.isDoubleClick()) {
			this.openSelectedStudy(itemId);
		} else if (event.getButton() == ClickEvent.BUTTON_LEFT) {
			// If just single click, just select the item and expand or collapse if applicable
			this.studyTree.expandOrCollapseStudyTreeNode(itemId);
			this.studyTree.selectItem(itemId);
		}
	}

	public void openSelectedStudy(final Object itemId) {
		if (!StudyTree.STUDY_ROOT_NODE.equals(itemId)) {
			final int studyId = Integer.parseInt(itemId.toString());
			if (!this.studyTree.hasChildStudy(studyId) && !this.studyTree.isFolder(studyId)) {
				this.browseStudyTreeComponent.createStudyInfoTab(studyId);
			}
		}
		this.studyTree.expandOrCollapseStudyTreeNode(itemId);
		this.studyTree.selectItem(itemId);
	}

}

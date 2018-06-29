package org.generationcp.ibpworkbench.study.tree.listeners;

import org.generationcp.ibpworkbench.study.tree.BrowseStudyTreeComponent;
import org.generationcp.ibpworkbench.study.tree.StudyTree;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents.ClickEvent;


public class StudyTreeItemClickListener implements ItemClickListener {
	
	private static final long serialVersionUID = 1L;
	private StudyTree studyTree;
	private BrowseStudyTreeComponent browseStudyTreeComponent;

	public StudyTreeItemClickListener(StudyTree studyTree, BrowseStudyTreeComponent browseStudyTreeComponent) {
		super();
		this.studyTree = studyTree;
		this.browseStudyTreeComponent = browseStudyTreeComponent;
	}

	@Override
	public void itemClick(ItemClickEvent event) {
		if (event.getButton() == ClickEvent.BUTTON_LEFT) {
			this.studyTreeItemClickAction(event.getItemId());
		}
	}
	
	public void studyTreeItemClickAction(final Object itemId) {
		this.studyTree.expandOrCollapseStudyTreeNode(itemId);
		if (!StudyTree.STUDY_ROOT_NODE.equals(itemId)) {
			final int studyId = Integer.valueOf(itemId.toString());
			if (!this.studyTree.hasChildStudy(studyId) && !this.studyTree.isFolder(studyId)) {
				this.browseStudyTreeComponent.createStudyInfoTab(studyId);
			}
		}
		this.studyTree.selectItem(itemId);
	}

}

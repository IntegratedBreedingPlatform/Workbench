
package org.generationcp.ibpworkbench.ui.breedingview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Window;

/**
 * Created by Daniel Villafuerte on 6/22/2015.
 */
@Configurable
public class SaveBreedingViewStudyTreeState implements Window.CloseListener {

	private BreedingViewTreeTable treeTable;
    
    @Autowired
	private UserProgramStateDataManager userProgramStateManager;

	@Autowired
	private ContextUtil contextUtil;

	public SaveBreedingViewStudyTreeState(BreedingViewTreeTable treeTable) {
		this.treeTable = treeTable;
	}

	@Override
	public void windowClose(Window.CloseEvent e) {
		List<String> itemIds = getExpandedIds();

		userProgramStateManager.saveOrUpdateUserProgramTreeState(contextUtil.getCurrentWorkbenchUserId(),
				contextUtil.getProjectInContext().getUniqueID(), ListTreeState.STUDY_LIST.name(), itemIds);
	}

	protected List<String> getExpandedIds() {
		List<String> expandedIds = new ArrayList<>();
		List<Reference> firstLevelFolders = getFirstLevelFolders();

		// study tree used in analysis always has an expanded "root node"
		expandedIds.add("STUDY");

		for (Reference firstLevelFolder : firstLevelFolders) {
			recurseSaveOpenNodes(firstLevelFolder, expandedIds);
		}

		return expandedIds;
	}

	public void recurseSaveOpenNodes(Reference item, List<String> openNodes) {
		if (treeTable.isCollapsed(item)) {
			return;
		}

		openNodes.add(item.getId().toString());

		if (item instanceof StudyReference) {
			return;
		}

		Collection children = treeTable.getChildren(item);
		if (children != null && !children.isEmpty()) {
			for (Object child : children) {
				recurseSaveOpenNodes((Reference) child, openNodes);
			}
		}
	}

	protected List<Reference> getFirstLevelFolders() {
		List<Reference> firstlevelFolders = new ArrayList<>();
		for (Reference reference : treeTable.getNodeMap().values()) {
			if(reference.isFolder()) {
				Integer parentFolderId = ((FolderReference) reference).getParentFolderId();
				if (parentFolderId != null && parentFolderId.equals(DmsProject.SYSTEM_FOLDER_ID)) {
					firstlevelFolders.add(reference);
				}
			}
		}

		return firstlevelFolders;
	}
}

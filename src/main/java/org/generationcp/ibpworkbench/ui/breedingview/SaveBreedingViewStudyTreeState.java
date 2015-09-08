
package org.generationcp.ibpworkbench.ui.breedingview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Window;

/**
 * Created by Daniel Villafuerte on 6/22/2015.
 */
@Configurable
public class SaveBreedingViewStudyTreeState implements Window.CloseListener {

	private static final Logger LOG = LoggerFactory.getLogger(SaveBreedingViewStudyTreeState.class);
	private BreedingViewTreeTable treeTable;

	@Autowired
	private ManagerFactoryProvider provider;
    
    @Autowired
	private UserProgramStateDataManager userProgramStateManager;

	@Autowired
	private SessionData sessionData;

	public SaveBreedingViewStudyTreeState(BreedingViewTreeTable treeTable) {
		this.treeTable = treeTable;
	}

	@Override
	public void windowClose(Window.CloseEvent e) {
		List<String> itemIds = getExpandedIds();

		ManagerFactory managerFactory = provider.getManagerFactoryForProject(sessionData.getSelectedProject());

		UserProgramStateDataManager programStateDataManager = managerFactory.getUserProgramStateDataManager();
		try {
			programStateDataManager.saveOrUpdateUserProgramTreeState(sessionData.getUserData().getUserid(), sessionData
					.getSelectedProject().getUniqueID(), ListTreeState.STUDY_LIST.name(), itemIds);
		} catch (MiddlewareQueryException e1) {
			LOG.error(e1.getMessage(), e1);
		}
	}

	protected List<String> getExpandedIds() {
		List<String> expandedIds = new ArrayList<>();
		List<FolderReference> firstLevelFolders = getFirstLevelFolders();

		// study tree used in analysis always has an expanded "root node"
		expandedIds.add("STUDY");

		for (FolderReference firstLevelFolder : firstLevelFolders) {
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

	protected List<FolderReference> getFirstLevelFolders() {
		List<FolderReference> firstlevelFolders = new ArrayList<>();
		for (FolderReference reference : treeTable.getNodeMap().values()) {
			Integer parentFolderId = reference.getParentFolderId();
			if (parentFolderId != null && parentFolderId.equals(DmsProject.SYSTEM_FOLDER_ID)) {
				firstlevelFolders.add(reference);
			}
		}

		return firstlevelFolders;
	}
}


package org.generationcp.ibpworkbench.ui.breedingview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.commons.constant.ListTreeState;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.TreeTable;

/**
 * Created by Daniel Villafuerte on 6/22/2015.
 */
@Configurable
public class BreedingViewTreeTable extends TreeTable {

	private static final Logger LOG = LoggerFactory.getLogger(BreedingViewTreeTable.class);
	private Map<Integer, Reference> nodeMap;

	@Autowired
	private ContextUtil contextUtil;
    
    @Autowired
    private UserProgramStateDataManager programStateDataManager;

	public BreedingViewTreeTable() {
		nodeMap = new HashMap<>();
	}

	public Object addFolderReferenceNode(Object[] cells, Reference folderReference) {
		nodeMap.put(folderReference.getId(), folderReference);
		return super.addItem(cells, folderReference);
	}

	public Reference getRootNode() {
		return nodeMap.get(DmsProject.SYSTEM_FOLDER_ID);
	}

	public Reference getFolderNode(Integer itemId) {
		return nodeMap.get(itemId);
	}

	public void setCollapsedFolder(Integer itemId, boolean collapsed) {
		Reference referenceObject = nodeMap.get(itemId);

		if (referenceObject == null) {
			return;
		}

		this.setCollapsed(referenceObject, collapsed);
	}

	public void reinitializeTree() {

		try {
			List<String> parsedState =
					programStateDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(contextUtil.getCurrentWorkbenchUserId(),
							contextUtil.getProjectInContext().getUniqueID(), ListTreeState.STUDY_LIST.name());

			if (parsedState.isEmpty() || (parsedState.size() == 1 && !StringUtils.isEmpty(parsedState.get(0)))) {
				return;
			}

			for (String s : parsedState) {
				String trimmed = s.trim();
				if (!StringUtils.isNumeric(trimmed)) {
					continue;
				}

				int itemId = Integer.parseInt(trimmed);

				setCollapsedFolder(itemId, false);
			}
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public Map<Integer, Reference> getNodeMap() {
		return nodeMap;
	}
}

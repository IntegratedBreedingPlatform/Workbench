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

package org.generationcp.ibpworkbench.study.tree.listeners;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.tree.StudyTree;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Window;

@Configurable
public class StudyTreeExpandListener implements Tree.ExpandListener {

	private static final Logger LOG = LoggerFactory.getLogger(StudyTreeExpandListener.class);
	private static final long serialVersionUID = -5091664285613837786L;
	
	@Autowired
	private StudyDataManager studyDataManager;
	
	@Autowired
	private ContextUtil contextUtil;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final StudyTree studyTree;

	public StudyTreeExpandListener(final StudyTree source) {
		this.studyTree = source;
	}

	@Override
	public void nodeExpand(final ExpandEvent event) {
			final Object itemId = event.getItemId();
			if (!StudyTree.STUDY_ROOT_NODE.equals(itemId)) {
				this.addChildren(Integer.valueOf(itemId.toString()), event.getComponent().getWindow());
			}
			this.studyTree.selectItem(itemId);
	}
	
	public void addChildren(final int parentStudyId, final Window window) {

		List<Reference> studyChildren = new ArrayList<Reference>();
		try {
			final String programUUID = this.contextUtil.getProjectInContext().getUniqueID();
			studyChildren =
					this.studyDataManager.getChildrenOfFolder(Integer.valueOf(parentStudyId), programUUID);
		} catch (final MiddlewareQueryException e) {
			StudyTreeExpandListener.LOG.error(e.getMessage(), e);
			MessageNotifier.showWarning(window, this.messageSource.getMessage(Message.ERROR_DATABASE),
					this.messageSource.getMessage(Message.ERROR_IN_GETTING_STUDIES_BY_PARENT_FOLDER_ID));
			studyChildren = new ArrayList<Reference>();
		}

		for (final Reference item : studyChildren) {
			if (this.studyTree.itemMatchesStudyTypeFilter(item)){
				this.studyTree.addItem(item.getId());
				this.studyTree.setItemCaption(item.getId(), item.getName());
				this.studyTree.setParent(item.getId(), parentStudyId);
				
				// check if the study has sub study
				if (this.studyTree.hasChildStudy(item.getId())) {
					this.studyTree.setChildrenAllowed(item.getId(), true);
					this.studyTree.setItemIcon(item.getId(), this.studyTree.getThemeResourceByReference(item));
				} else {
					this.studyTree.setChildrenAllowed(item.getId(), false);
					this.studyTree.setItemIcon(item.getId(), this.studyTree.getThemeResourceByReference(item));
				}
			}

		}
	}

	
	protected void setStudyDataManager(StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	
	protected void setContextUtil(ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	
	protected void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

}

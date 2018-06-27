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

package org.generationcp.ibpworkbench.study.listeners;

import org.generationcp.ibpworkbench.study.tree.StudyTree;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;

public class StudyTreeCollapseListener implements Tree.CollapseListener {

	private static final long serialVersionUID = -5091664285613837786L;

	private final StudyTree source;

	public StudyTreeCollapseListener(final StudyTree tree) {
		this.source = tree;
	}

	@Override
	public void nodeCollapse(CollapseEvent event) {
		this.source.selectItem(event.getItemId());
	}

}

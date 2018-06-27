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

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.study.tree.StudyTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

public class StudyTreeExpandListener implements Tree.ExpandListener {

	private static final Logger LOG = LoggerFactory.getLogger(StudyTreeExpandListener.class);
	private static final long serialVersionUID = -5091664285613837786L;

	private final StudyTree source;

	public StudyTreeExpandListener(StudyTree source) {
		this.source = source;
	}

	@Override
	public void nodeExpand(ExpandEvent event) {
			final Object itemId = event.getItemId();
			try {
				this.source.addStudyNode(Integer.valueOf(itemId.toString()));
			} catch (InternationalizableException e) {
				StudyTreeExpandListener.LOG.error(e.toString() + "\n" + e.getStackTrace());
				e.printStackTrace();
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription()); // TESTED
			} catch (NumberFormatException e) {
			}
			this.source.selectItem(itemId);
			this.source.select(itemId);
	}

}

/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.study.listeners;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.MouseEvents.ClickEvent;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.germplasm.GermplasmStudyInfoComponent;
import org.generationcp.ibpworkbench.germplasm.containers.GermplasmIndexContainer;
import org.generationcp.ibpworkbench.study.StudySearchMainComponent;
import org.generationcp.ibpworkbench.study.StudyTreeComponent;
import org.generationcp.ibpworkbench.study.containers.StudyDataContainerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joyce Avestro
 *
 */

public class StudyItemClickListener implements ItemClickEvent.ItemClickListener {

	private static final Logger LOG = LoggerFactory.getLogger(StudyItemClickListener.class);
	private static final long serialVersionUID = -5286616518840026212L;

	private final Object source;

	public StudyItemClickListener(final Object source) {
		this.source = source;
	}

	@Override
	public void itemClick(final ItemClickEvent event) {

		if (this.source instanceof StudyTreeComponent) {
			if (event.getButton() == ClickEvent.BUTTON_LEFT) {
				try {
					((StudyTreeComponent) this.source).studyTreeItemClickAction(event.getItemId());
				} catch (final InternationalizableException e) {
					StudyItemClickListener.LOG.error(e.getMessage(), e);
					MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription()); // TESTED
				}
				((StudyTreeComponent) this.source).getStudyTree().setNullSelectionAllowed(false);
				((StudyTreeComponent) this.source).getStudyTree().select(event.getItemId());
				((StudyTreeComponent) this.source).getStudyTree().setValue(event.getItemId());
			}
		}

		if (this.source instanceof StudySearchMainComponent && event.getButton() == ClickEvent.BUTTON_LEFT) {
			final int studyId = Integer.valueOf(event.getItem().getItemProperty(StudyDataContainerBuilder.STUDY_ID).getValue().toString());
			try {
				((StudySearchMainComponent) this.source).getSearchResultComponent().studyItemClickAction(studyId);
			} catch (final InternationalizableException e) {
				StudyItemClickListener.LOG.error(e.getMessage(), e);
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			}
		}

		if (this.source instanceof GermplasmStudyInfoComponent) {
			final int studyId = Integer.valueOf(event.getItem().getItemProperty(GermplasmIndexContainer.STUDY_ID).getValue().toString());
			if (event.getButton() == ClickEvent.BUTTON_LEFT) {
				try {
					((GermplasmStudyInfoComponent) this.source).studyItemClickAction(event, studyId);
				} catch (final InternationalizableException e) {
					StudyItemClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace());
					e.printStackTrace();
					MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
				}
			}
		}
	}

}

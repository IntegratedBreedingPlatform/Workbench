/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Component;

/**
 * @author Efficio.Daniel
 *
 */
public class DashboardMainTreeListener implements ItemClickEvent.ItemClickListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -7404027023456975933L;
	private final Project project;
	private final Component source;

	public DashboardMainTreeListener(Component source, Project project) {
		this.project = project;
		this.source = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.event.ItemClickEvent.ItemClickListener#itemClick(com.vaadin.event.ItemClickEvent)
	 */
	@Override
	public void itemClick(ItemClickEvent event) {

		if (this.source instanceof GermplasmListPreview) {
			GermplasmListPreview preview = (GermplasmListPreview) this.source;

			Object propertyValue = event.getItemId();

			// expand the node
			preview.expandTree(event.getItemId());

			preview.processToolbarButtons(propertyValue);
		} else if (this.source instanceof NurseryListPreview) {
			NurseryListPreview preview = (NurseryListPreview) this.source;

			Object propertyValue = event.getItemId();

			// expand the node
			preview.expandTree(event.getItemId());

			preview.processToolbarButtons(propertyValue);
		}
	}
}

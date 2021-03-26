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

package org.generationcp.ibpworkbench.germplasm.listeners;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import org.generationcp.ibpworkbench.germplasm.GermplasmDerivativeNeighborhoodComponent;
import org.generationcp.ibpworkbench.germplasm.GermplasmDetail;
import org.generationcp.ibpworkbench.germplasm.GermplasmMaintenanceNeighborhoodComponent;
import org.generationcp.ibpworkbench.germplasm.SaveGermplasmListDialog;
import org.generationcp.ibpworkbench.germplasm.pedigree.GermplasmPedigreeGraphComponent;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GermplasmButtonClickListener implements Button.ClickListener {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmButtonClickListener.class);
	private static final long serialVersionUID = 1721485345429990412L;

	private final Object source;

	public GermplasmButtonClickListener(final Object source) {
		this.source = source;
	}

	@Override
	public void buttonClick(final ClickEvent event) {

		if (this.source instanceof SaveGermplasmListDialog && event.getButton().getData().equals(SaveGermplasmListDialog.SAVE_BUTTON_ID)) {
			try {
				((SaveGermplasmListDialog) this.source).saveGermplasmListButtonClickAction();
			} catch (final InternationalizableException e) {
				GermplasmButtonClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace(), e);
				// TESTED
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			}

		} else if (this.source instanceof SaveGermplasmListDialog
				&& event.getButton().getData().equals(SaveGermplasmListDialog.CANCEL_BUTTON_ID)) {
			((SaveGermplasmListDialog) this.source).cancelGermplasmListButtonClickAction();

		} else if (this.source instanceof GermplasmDerivativeNeighborhoodComponent
				&& event.getButton().getData().equals(GermplasmDerivativeNeighborhoodComponent.DISPLAY_BUTTON_ID)) {
			try {
				((GermplasmDerivativeNeighborhoodComponent) this.source).displayButtonClickAction();
			} catch (final InternationalizableException e) {
				GermplasmButtonClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace(), e);
				// TESTED
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			}

		} else if (this.source instanceof GermplasmMaintenanceNeighborhoodComponent
				&& event.getButton().getData().equals(GermplasmMaintenanceNeighborhoodComponent.DISPLAY_BUTTON_ID)) {
			try {
				((GermplasmMaintenanceNeighborhoodComponent) this.source).displayButtonClickAction();
			} catch (final InternationalizableException e) {
				GermplasmButtonClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace(), e);
				// TESTED
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			}
		} else if (this.source instanceof GermplasmDetail && event.getButton().getData().equals(GermplasmDetail.VIEW_PEDIGREE_GRAPH_ID)) {
			try {
				((GermplasmDetail) this.source).viewPedigreeGraphClickAction();
			} catch (final InternationalizableException e) {
				GermplasmButtonClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace(), e);
				// TESTED
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			}
		} else if (this.source instanceof GermplasmPedigreeGraphComponent
				&& event.getButton().getData().equals(GermplasmPedigreeGraphComponent.UPDATE_PEDIGREE_GRAPH_BUTTON_ID)) {
			try {
				((GermplasmPedigreeGraphComponent) this.source).updatePedigreeGraphButtonClickAction();
			} catch (final InternationalizableException e) {
				GermplasmButtonClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace(), e);
				// TESTED
				MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
			}
		} else if (this.source instanceof GermplasmDetail && event.getButton().getData().equals(GermplasmDetail.REFRESH_BUTTON_ID)) {
			((GermplasmDetail) this.source).refreshPedigreeTree();
		} else {
			GermplasmButtonClickListener.LOG.error("GermplasmButtonClickListener: Error with buttonClick action. Source not identified.");
		}
	}

}

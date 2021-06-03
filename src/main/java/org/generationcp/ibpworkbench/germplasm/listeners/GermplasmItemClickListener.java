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

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.ui.Table;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.cross.study.h2h.main.dialogs.SelectGermplasmEntryDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GermplasmItemClickListener implements ItemClickEvent.ItemClickListener {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmItemClickListener.class);
	private static final long serialVersionUID = -1095503156046245812L;

	private final Object sourceClass;

	public GermplasmItemClickListener(final Object sourceClass) {
		this.sourceClass = sourceClass;
	}

	@Override
	public void itemClick(final ItemClickEvent event) {
		if (this.sourceClass instanceof SelectGermplasmEntryDialog) {
			if (event.getButton() == ClickEvent.BUTTON_LEFT) {
				try {
					((SelectGermplasmEntryDialog) this.sourceClass).resultTableItemClickAction((Table) event.getSource(),
						event.getItemId(), event.getItem());
				} catch (final InternationalizableException e) {
					GermplasmItemClickListener.LOG.error(e.toString() + "\n" + e.getStackTrace(), e);
					MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
				}
			}
		}
	}

}

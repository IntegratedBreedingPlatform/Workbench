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

package org.generationcp.ibpworkbench.germplasmlist.listeners;

import com.vaadin.ui.Layout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;
import org.generationcp.ibpworkbench.germplasmlist.ListManagerTreeComponent;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GermplasmListTreeExpandListener implements Tree.ExpandListener {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListTreeExpandListener.class);
	private static final long serialVersionUID = -5145904396164706110L;

	private final Layout source;

	public GermplasmListTreeExpandListener(Layout source) {
		this.source = source;
	}

	@Override
	public void nodeExpand(ExpandEvent event) {
		if (this.source instanceof ListManagerTreeComponent) {
			if (!event.getItemId().toString().equals("CENTRAL") && !event.getItemId().toString().equals("LOCAL")) {
				try {
					((ListManagerTreeComponent) this.source).addGermplasmListNode(Integer.valueOf(event.getItemId().toString()));
				} catch (InternationalizableException e) {
					GermplasmListTreeExpandListener.LOG.error(e.toString() + "\n" + e.getStackTrace());
					e.printStackTrace();
					MessageNotifier.showError(event.getComponent().getWindow(), e.getCaption(), e.getDescription());
				}
			}
			((ListManagerTreeComponent) this.source).getGermplasmListTree().select(event.getItemId());
			((ListManagerTreeComponent) this.source).getGermplasmListTree().setValue(event.getItemId());
			((ListManagerTreeComponent) this.source).setSelectedListId(event.getItemId());
			((ListManagerTreeComponent) this.source).updateButtons(event.getItemId());
		}
	}

}

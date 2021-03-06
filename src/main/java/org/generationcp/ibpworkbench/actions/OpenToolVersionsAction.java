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

package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.ui.ToolsAndCropVersionsView;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

public class OpenToolVersionsAction implements ClickListener, ActionListener {

	private static final Logger LOG = LoggerFactory.getLogger(OpenToolVersionsAction.class);

	private static final long serialVersionUID = 1L;

	@Override
	public void buttonClick(ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(Event event) {
		// does nothing
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
		IContentWindow w = (IContentWindow) window;

		try {
			ToolsAndCropVersionsView toolsAndCropVersionsView = new ToolsAndCropVersionsView();
			toolsAndCropVersionsView.setDebugId("toolsAndCropVersionsView");

			w.showContent(toolsAndCropVersionsView);

		} catch (Exception e) {
			OpenToolVersionsAction.LOG.error("Exception", e);
			if (e.getCause() instanceof InternationalizableException) {
				InternationalizableException i = (InternationalizableException) e.getCause();
				MessageNotifier.showError(window, i.getCaption(), i.getDescription());
			}
			return;
		}
	}
}

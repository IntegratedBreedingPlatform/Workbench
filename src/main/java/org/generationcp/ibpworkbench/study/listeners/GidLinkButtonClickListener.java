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

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.apache.commons.lang3.ArrayUtils;
import org.generationcp.breeding.manager.service.GermplasmDetailsUrlService;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.ibpworkbench.cross.study.adapted.dialogs.ViewTraitObservationsDialog;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class GidLinkButtonClickListener implements Button.ClickListener {

	private static final long serialVersionUID = -6751894969990825730L;
	private final static Logger LOG = LoggerFactory.getLogger(GidLinkButtonClickListener.class);
	private final String[] CHILD_WINDOWS = {
		ViewTraitObservationsDialog.LINE_BY_TRAIT_WINDOW_NAME};

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private GermplasmDetailsUrlService germplasmDetailsUrlService;

	private final String gid;

	public GidLinkButtonClickListener(final String gid) {
		this.gid = gid;
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		final Window mainWindow;
		final Window eventWindow = event.getComponent().getWindow();
		if (ArrayUtils.contains(this.CHILD_WINDOWS, eventWindow.getName())) {
			mainWindow = eventWindow.getParent();
		} else {
			mainWindow = eventWindow;
		}

		final ExternalResource germplasmDetailsLink = this.germplasmDetailsUrlService.getExternalResource(Integer.parseInt(this.gid), true);

		String preferredName = null;
		try {
			preferredName = this.germplasmDataManager.getPreferredNameValueByGID(Integer.valueOf(this.gid));
		} catch (final MiddlewareQueryException ex) {
			GidLinkButtonClickListener.LOG.error("Error with getting preferred name of " + this.gid, ex);
		}

		String windowTitle = "Germplasm Details: " + "(GID: " + this.gid + ")";
		if (preferredName != null) {
			windowTitle = "Germplasm Details: " + preferredName + " (GID: " + this.gid + ")";
		}
		final Window germplasmWindow = new BaseSubWindow(windowTitle);

		final VerticalLayout layoutForGermplasm = new VerticalLayout();
		layoutForGermplasm.setMargin(false);
		layoutForGermplasm.setWidth("98%");
		layoutForGermplasm.setHeight("98%");

		final Embedded germplasmInfo = new Embedded("", germplasmDetailsLink);
		germplasmInfo.setType(Embedded.TYPE_BROWSER);
		germplasmInfo.setSizeFull();
		layoutForGermplasm.addComponent(germplasmInfo);

		germplasmWindow.setContent(layoutForGermplasm);

		// Instead of setting by percentage, compute it
		germplasmWindow.setWidth(Integer.valueOf((int) Math.round(mainWindow.getWidth() * .90)) + "px");
		germplasmWindow.setHeight(Integer.valueOf((int) Math.round(mainWindow.getHeight() * .90)) + "px");

		germplasmWindow.center();
		germplasmWindow.setResizable(false);
		germplasmWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		germplasmWindow.setModal(true);

		mainWindow.addWindow(germplasmWindow);
	}

}

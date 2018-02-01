/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programlocations.ProgramLocationsView;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author Joyce Avestro
 */
@Configurable
public class OpenProgramLocationsAction implements ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	public OpenProgramLocationsAction() {
		// does nothing here
	}

	@Override
	public void buttonClick(final ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(final Event event) {
		// does nothing
	}

	@Override
	public void doAction(final Window window, final String uriFragment, final boolean isLinkAccessed) {

		contextUtil.logProgramActivity(this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK),
				this.messageSource.getMessage(Message.LAUNCHED_APP, this.messageSource.getMessage(Message.PROJECT_LOCATIONS_LINK)));

		final IContentWindow w = (IContentWindow) window;
		w.showContent(new ProgramLocationsView(this.contextUtil.getProjectInContext()));

	}
}

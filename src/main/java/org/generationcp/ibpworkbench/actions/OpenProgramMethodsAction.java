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

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.Window;

/**
 * @author Joyce Avestro
 */
@Configurable
public class OpenProgramMethodsAction implements ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;


	@Autowired
	private ContextUtil contextUtil;

	public OpenProgramMethodsAction() {
		// does nothing here
	}

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
		ProgramMethodsView methodsView = new ProgramMethodsView(this.contextUtil.getProjectInContext());
		methodsView.setDebugId("methodsView");
		w.showContent(methodsView);

		this.contextUtil.logProgramActivity(this.messageSource.getMessage(Message.PROJECT_METHODS_LINK), this.messageSource.getMessage(
				Message.LAUNCHED_APP, this.messageSource.getMessage(Message.PROJECT_METHODS_LINK)));


	}
}

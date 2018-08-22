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
import org.generationcp.commons.util.WorkbenchAppPathResolver;
import org.generationcp.ibpworkbench.ui.WorkbenchMainView;

/**
 * <b>Description</b>: Listener class for closing the application.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Michael Blancaflor <br>
 * <b>File Created</b>: Jun 11, 2012.
 */
public class SignoutAction implements ClickListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1898892299981012511L;

	/**
	 * Button click.
	 *
	 * @param event the event
	 */
	@Override
	public void buttonClick(final ClickEvent event) {
		final WorkbenchMainView window = (WorkbenchMainView) event.getButton().getWindow();
		window.setUriFragment("", true);

		final String fullLogoutUrl = WorkbenchAppPathResolver.getFullWebAddress("ibpworkbench/controller/logout", "");

		window.showContent(fullLogoutUrl);
	}

}

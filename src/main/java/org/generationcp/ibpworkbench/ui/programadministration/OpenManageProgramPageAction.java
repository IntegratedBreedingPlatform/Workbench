
package org.generationcp.ibpworkbench.ui.programadministration;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.navigation.NavManager;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

/**
 * Created with IntelliJ IDEA. User: aldrin Date: 10/28/13 Time: 12:40 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class OpenManageProgramPageAction implements Button.ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(OpenManageProgramPageAction.class);

	@Override
	public void buttonClick(Button.ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(Component.Event event) {
		NavManager.breadCrumbClick(this, event);
	}

	@Override
	public void doAction(Window window, String uriFragment, boolean isLinkAccessed) {
		final IContentWindow w = (IContentWindow) window;

		try {
			ProgramAdministrationPanel projectPanel = new ProgramAdministrationPanel();
			w.showContent(projectPanel);

		} catch (Exception e) {
			OpenManageProgramPageAction.LOG.error("Exception", e);
			if (e.getCause() instanceof InternationalizableException) {
				InternationalizableException i = (InternationalizableException) e.getCause();
				MessageNotifier.showError(window, i.getCaption(), i.getDescription());
			}
		}

	}

}

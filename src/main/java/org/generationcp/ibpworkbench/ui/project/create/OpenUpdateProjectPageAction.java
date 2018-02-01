package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Window;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.actions.ActionListener;
import org.generationcp.ibpworkbench.ui.window.IContentWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 10/28/13 Time: 12:40 PM To change this template use File | Settings | File Templates.
 */
@Configurable
public class OpenUpdateProjectPageAction implements Button.ClickListener, ActionListener {

	private static final long serialVersionUID = 1L;

	@Autowired
	private ContextUtil contextUtil;

	@Override
	public void buttonClick(final Button.ClickEvent event) {
		this.doAction(event.getComponent().getWindow(), null, true);
	}

	@Override
	public void doAction(final Component.Event event) {
		// currently does nothing
	}

	@Override
	public void doAction(final Window window, final String uriFragment, final boolean isLinkAccessed) {

		final IContentWindow w = (IContentWindow) window;
		final UpdateProjectPanel projectPanel = new UpdateProjectPanel();
		projectPanel.setDebugId("projectPanel");
		w.showContent(projectPanel);

		contextUtil.logProgramActivity("Update Program", "Launched Update Program");

	}

}
